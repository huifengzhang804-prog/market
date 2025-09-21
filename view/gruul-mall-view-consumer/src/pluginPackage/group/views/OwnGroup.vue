<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { ref, computed, reactive, nextTick } from 'vue'
import { doGetGroupUser, doGetGroupActivity, doGetGroupActivityStatus } from '@pluginPackage/group/apis'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import DateUtils from '@/utils/date'
import { doGetGuessYouLike } from '@/apis/consumer/footprint'
import QSharePage from '@/components/q-sharePage/q-sharePage.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { useSettingStore } from '@/store/modules/setting'
import type { ApiGroupUser, ApiGroupActivity } from '@/apis/plugin/group/model'
import Auth from '@/components/auth/auth.vue'
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'

type GuessYouLike = {
  cIndex: number
  column: number
  index: number
  lowestPrice: string
  o: number
  productAlbumPics: string
  productId: Long
  productName: string
  salesVolume: string
  shopId: Long
}

const userList = ref<ApiGroupUser[]>([])
const groupInfo = ref<ApiGroupActivity>({
  name: '',
  commanderId: '',
  commanderAvatar: '',
  commanderNickname: '',
  openTime: '',
  effectTimeout: 0,
  currentNum: 0,
  totalNum: 0,
  productId: '',
  productName: '',
  buyNum: 0,
  orderNo: '',
  shopId: '',
  status: 'ING',
})
const getSurplus = computed(() => {
  if (groupInfo.value?.buyNum && groupInfo.value?.currentNum) {
    const surplus = groupInfo.value.totalNum - groupInfo.value.currentNum
    return surplus || 0
  }
  return 0
})
const dateUtils = new DateUtils()
const $settingStore = useSettingStore()
const endTime = ref('')
// 刷新页面标志
const refreshPageFlag = ref(true)
const { resume, timeSet, pause } = useCountdown(endTime, { immediate: true }, () => {
  refreshPage()
})
// 刷新页面
const refreshPage = async () => {
  refreshPageFlag.value = true
  getGroupUser(teamNo.value)
  getGroupInfo(teamNo.value)
  await nextTick()
  refreshPageFlag.value = false
}
// 轮询标识符
const loopTag = ref(false)
const likeData = reactive<{ list: any[] }>({
  list: [],
})
const waterFallRef = ref()
const teamNo = ref('')
const pageConfig = reactive({ current: 1, size: 10, pages: 1, status: 'loadmore' })
const shareUrl = computed(() => {
  const { shopId, productId, commanderId } = groupInfo.value
  const extra = {
    type: 'Group',
    teamNo: teamNo.value,
  }
  const baseUrl = import.meta.env.VITE_BASE_URL.replace(/api\//, '')
  const url = `${baseUrl}h5/#/pluginPackage/goods/commodityInfo/InfoEntrance?shopId=${shopId}&goodId=${productId}&extra=${encodeURIComponent(
    JSON.stringify(extra),
  )}`
  return productId ? url : ''
})

onLoad((param = {}) => {
  teamNo.value = param.teamNo
  if (param.payType) {
    checkStatus(param.teamNo)
  } else {
    getGroupUser(param.teamNo)
    getGroupInfo(param.teamNo)
  }
  initGuessYouLike()
})

const handleNavToGoodsDetail = (goods: GuessYouLike) => {
  const { productId, shopId } = goods
  jumpGoods(shopId, productId)
}
async function getGroupUser(param: string) {
  const { data } = await doGetGroupUser(param)
  userList.value = data.records
}
async function getGroupInfo(param: string) {
  const { data, msg } = await doGetGroupActivity(param)
  if (!data) return uni.showToast({ title: `${msg || '查询拼团信息失败'}`, icon: 'none' })
  if (data.status === 'ING') {
    endTime.value = new Date(dateUtils.getTime(data.openTime) + Number(data.effectTimeout) * 60 * 1000).toString()
    resume()
  } else {
    pause()
  }
  groupInfo.value = data
}
async function initGuessYouLike(isLoad = false) {
  if (!isLoad) {
    waterFallRef?.value?.clear?.()
    // 刷新
    pageConfig.current = 1
    likeData.list = await getGuessYouLike()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    likeData.list = likeData.list.concat(await getGuessYouLike())
  }
}
async function getGuessYouLike() {
  const { code, data, msg } = await doGetGuessYouLike(pageConfig.current, pageConfig.size)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取猜你喜欢失败'}`, icon: 'none' })
  if (pageConfig.current === data.pages) {
    pageConfig.status = 'nomore'
  }
  pageConfig.pages = data.pages
  return data.records
}
const handleShareError = () => {
  uni.showToast({
    title: '拼团已过期，或已关闭',
    icon: 'none',
  })
}
const generateCode = () => {
  if (shareUrl.value.length) {
    uni.setClipboardData({
      data: shareUrl.value,
      showToast: false,
      success: function () {
        uni.showToast({ title: '已复制快去粘贴吧~', icon: 'none' })
      },
    })
  } else {
    handleShareError()
  }
}
const handleNavToHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
/**
 */
let count = 0
async function checkStatus(teamNo: string) {
  const timer = setTimeout(async () => {
    const { data } = await doGetGroupActivityStatus(teamNo)
    if (!data && count < 20) {
      checkStatus(teamNo)
      count++
    } else if (!data && count >= 20) {
      clearTimeout(timer)
    } else {
      getGroupUser(teamNo)
      getGroupInfo(teamNo)
      clearTimeout(timer)
    }
  }, 200)
}
const error = ref(-1)
const loadError = (index: number) => {
  error.value = index
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initGuessYouLike(true)">
    <view class="group">
      <view v-if="groupInfo.status === 'ING'">
        <view class="group__title">邀请好友参团</view>
        <view class="group__time f12"
          >仅剩<text class="group__time--red">{{ getSurplus }}</text
          >个名额，<text class="group__time--red">{{ timeSet.hours }}:{{ timeSet.minutes }}:{{ timeSet.seconds }}</text></view
        >
      </view>
      <view v-if="groupInfo.status === 'FAIL'" class="group__time--red f14 group__status">真可惜拼团未能成功！！！</view>
      <view v-if="groupInfo.status === 'SUCCESS'" class="group__time--red f14 group__status">拼团成功！！！</view>
      <!-- <view class="group__time--red f14 group__status">拼团成功</view> -->
      <view class="group__user">
        <view
          v-for="(item, index) in userList"
          :key="item.userId"
          :class="['group__user-item', item.commander ? 'group__user-item--solid' : 'group__user-item--dash']"
        >
          <img
            v-if="error === index"
            class="group__user-image"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564e7de4b0dd23f6b86f2f.png"
          />
          <image v-if="error !== index" class="group__user-image" :src="item.avatar" mode="aspectFill" @error="loadError(index)"></image>
          <view v-if="item.commander" class="group__user-tag">团长</view>
        </view>
        <!-- 未参团站位 -->
        <view v-for="ite of getSurplus" :key="ite" class="group__user-item group__user-item--dash group__user-item--plus" @click="generateCode">
          <img
            v-if="groupInfo.status === 'SUCCESS'"
            class="group__user-image"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564e7de4b0dd23f6b86f2f.png"
          />
        </view>
      </view>
      <view class="group__product">
        <view class="group__product-item" style="margin-bottom: 16rpx">
          <view class="group__product-item--title">商品名称</view>
          <view class="group__product-item--content">{{ groupInfo.productName }}</view>
        </view>
        <view class="group__product-item">
          <view class="group__product-item--title">拼团规则</view>
          <view class="group__product-item--content">参团人数不足，系统自动退款</view>
        </view>
      </view>
      <view v-if="groupInfo.status === 'ING'" class="group__btn">
        <view class="group__btn--home" @click="handleNavToHome">去首页逛逛</view>
        <q-share-page :url="shareUrl" :text="`再邀${getSurplus}人拼团`" @error="handleShareError"> </q-share-page>
      </view>
      <view class="guessList">
        <u-waterfall v-if="likeData.list.length" ref="waterFallRef" v-model="likeData.list" idKey="productId">
          <template #left="{ leftList }">
            <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
              <u-lazy-load threshold="-450" border-radius="16" :image="item.productAlbumPics" :index="index"></u-lazy-load>
              <RetrieveComUnit
                :good-name="item.productName"
                :price="[item.lowestPrice]"
                :sale-volume="Number(item.salesVolume)"
                :commodity-info="item"
              />
            </view>
          </template>
          <template #right="{ rightList }">
            <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
              <u-lazy-load threshold="-450" border-radius="16" :image="item.productAlbumPics" :index="index"></u-lazy-load>
              <RetrieveComUnit
                :good-name="item.productName"
                :price="[item.lowestPrice]"
                :sale-volume="Number(item.salesVolume)"
                :commodity-info="item"
              />
            </view>
          </template>
        </u-waterfall>
      </view>
    </view>
    <Auth />
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(group) {
  background: #fff;
  overflow: hidden;
  @include e(title) {
    font-size: 34rpx;
    font-weight: bold;
    margin: 0 48rpx;
    @include flex;
    margin: 36rpx 0 16rpx 0;
    &::before {
      content: '';
      width: 70rpx;
      display: inline-block;
      border: 1px dashed #333;
      margin-right: 48rpx;
    }
    &::after {
      content: '';
      width: 70rpx;
      display: inline-block;
      border: 1px dashed #333;
      margin-left: 48rpx;
    }
  }
  @include e(time) {
    text-align: center;
    letter-spacing: 2rpx;
    margin-bottom: 64rpx;
    @include m(red) {
      color: #d0323d;
    }
  }
  @include e(user) {
    box-sizing: border-box;
    padding: 0 30rpx 30rpx 30rpx;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(110rpx, 1fr));
    grid-row-gap: 10rpx;
    grid-column-gap: 10rpx;
    justify-items: center;
    align-items: center;
    border-bottom: 1px solid #eee;
    justify-content: center;
  }
  @include e(user-item) {
    width: 110rpx;
    height: 110rpx;
    @include flex;
    border-radius: 50%;
    position: relative;
    @include m(dash) {
      border: 1px dashed #eee;
    }
    @include m(plus) {
      &::before {
        content: '+';
        position: absolute;
        width: 40upx;
        height: 40upx;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 40upx;
        color: #eee;
      }
    }
    @include m(solid) {
      border: 4rpx solid rgba(255, 201, 0, 1);
    }
  }
  @include e(user-image) {
    width: 102rpx;
    height: 102rpx;
    border-radius: 50%;
  }
  @include e(user-tag) {
    width: 70rpx;
    height: 46rpx;
    border-radius: 70rpx;
    color: #fff;
    background-color: rgba(255, 201, 0, 1);
    text-align: center;
    line-height: 46rpx;
    font-size: 24rpx;
    position: absolute;
    top: -23rpx;
    left: -26rpx;
  }
  @include e(product) {
    margin: 40rpx 30rpx 90rpx 52rpx;
  }
  @include e(product-item) {
    @include flex(space-between);
    font-size: 24rpx;
    @include m(title) {
      color: #999;
    }
    @include m(content) {
      width: 486rpx;
      @include utils-ellipsis;
      text-align: right;
    }
  }
  @include e(btn) {
    margin: 0 22rpx 40rpx 32rpx;
    @include flex(space-between);
    @include m(home) {
      width: 280rpx;
      height: 76rpx;
      border: 1px solid rgba(227, 20, 54, 1);
      border-radius: 6rpx;
      background: #fff;
      color: #d0323d;
      line-height: 76rpx;
      text-align: center;
    }
    @include m(invite) {
      width: 350rpx;
      height: 76rpx;
      border-radius: 6rpx;
      background-color: rgba(222, 50, 36, 1);
      color: #fff;
      line-height: 76rpx;
      text-align: center;
    }
  }
  @include e(status) {
    text-align: center;
    margin: 68rpx 0 94rpx 0;
  }
}
@include b(guessList) {
  padding: 0 28rpx;
  box-sizing: border-box;
}
</style>
