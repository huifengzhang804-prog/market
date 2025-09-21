<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetAddressList, doSetAddressDefault, doDelAddress } from '@/apis/address'
import type { AddressItemType } from '@/apis/address/model'
// 地区hooks
import { useChosseAddress } from '@/hooks/useChooseAddress'
import useAddressDispatcher from '@/store/dispatcher/useAddressDispatcher'
import { useStatusBar } from '@/hooks'
import { computed, onUnmounted, ref } from 'vue'

const $useAddressDispatcher = useAddressDispatcher()
const { confirmOredrAddress, orderBtnAddress, areasAddress, interalAreasAddress, interalListAddress } = useChosseAddress()
const { setConfirmAddress, setEdlAddress } = confirmOredrAddress()
const { setOrderBtnAddress } = orderBtnAddress()
const { setAreasAddress } = areasAddress()
const { setInteralAreasAddress } = interalAreasAddress()
const { setInteralListAddress } = interalListAddress()

// 发送地区优惠
const addressList = ref<AddressItemType[]>([])

onShow(() => {
  $useAddressDispatcher.addCartSubscriber(initList)
  initList()
})
const callSetAddressObj = {
  callConfirm: setConfirmAddress,
  calltOrderBtn: setOrderBtnAddress,
  callAreas: setAreasAddress,
  integral: setInteralAreasAddress,
  integralList: setInteralListAddress,
}
let callSetAddressObjKey: keyof typeof callSetAddressObj | 'null'
onLoad((res) => {
  let key = null
  if (res) {
    key = res.callKey
  }
  callSetAddressObjKey = key ? key : 'null'
})

const navToHandle = () => {
  uni.navigateTo({
    url: '/basePackage/pages/addressManage/NewAddress',
  })
}

// 地址
const defaultAddressId = ref<Long>('')
async function initList() {
  const { data, code, success } = await doGetAddressList()
  if (code === 200 && success && data) {
    addressList.value = data.records
    const obj = addressList.value.find((item) => item.isDefault)
    if (obj) {
      defaultAddressId.value = obj.id!
    }
  } else {
    return
  }
}
/**
 * 选中check回调
 * @param {string} e 地址id
 */
const handleChangeCheck = async (e: any) => {
  const { code, success } = await doSetAddressDefault(e.detail.value, true)
  if (code === 200 && success) {
    uni.showToast({
      icon: 'none',
      title: '设置成功',
    })
    $useAddressDispatcher.updateCartData()
    const obj = addressList.value.find((item) => item.id === e.detail.value)
    if (obj) {
      defaultAddressId.value = obj.id!
      handleChooseAddress(obj, 'handleChangeCheck')
    }
  }
}
/**
 * 编辑跳转
 */
const handleNavToEdit = (id: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/addressManage/NewAddress?id=${id}`,
  })
}
onUnmounted(() => $useAddressDispatcher.removeCartSubscriber(initList))
/**
 */
const handleDelAddress = (id: Long) => {
  uni.showModal({
    title: '提示',
    content: '确定要删除该地址?',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doDelAddress(id)
        if (code === 200 && success) {
          uni.showToast({
            title: '删除成功',
            icon: 'none',
            duration: 1000,
          })
          $useAddressDispatcher.updateCartData()
          setEdlAddress()
        } else {
          uni.showToast({
            title: '删除失败',
            icon: 'none',
            duration: 1000,
          })
        }
      }
    },
  })
}
/**
 * 选中当前地址
 */
const handleChooseAddress = (data: AddressItemType, from = '') => {
  const pages = getCurrentPages()
  if (pages.length === 1) {
    return
  }
  const prevPage = pages[pages.length - 2]
  // 获取上一个页面
  if (prevPage.route === 'pages/platform/Index') {
    // 如果是首页则直接返回
    return
  }
  if (from === 'handleChangeCheck') {
    return
  }
  if (callSetAddressObjKey === null) {
    return handleNavToEdit(data.id!)
  }
  uni.navigateBack()
  // 设置确认订单地址
  if (callSetAddressObjKey !== 'null') {
    callSetAddressObj[callSetAddressObjKey](data)
  }
}
const safeHeight = useBottomSafe()
const btnHeight = computed(() => {
  return safeHeight.value > 0 ? uni.upx2px(68) : uni.upx2px(98)
})
const contentHeight = computed(() => {
  return useScreenHeight().value - btnHeight.value - safeHeight.value
})
</script>

<template>
  <radio-group style="width: 100%" @change="handleChangeCheck">
    <view class="address" :style="`height: ${contentHeight}px;`">
      <view v-for="item in addressList" :key="item.name" class="address__item">
        <view class="address__item-top" @click="handleChooseAddress(item)">
          <view style="width: 616rpx">
            <view class="address__item-top--info">
              <view v-if="item.isDefault" class="is-default">默认</view>
              <view class="is-name"> {{ item.address.split('~')[1] || '' }}</view>
            </view>
            <view class="address__item-top--txt">
              <view>
                <text>{{ item.area?.join?.('') }}</text>
                <text>{{ item.address.split('~')[0] || '' }} {{ item.address.split('~')[2] || '' }}</text>
              </view>
            </view>
            <view class="address__item-top--txt">
              <view>
                <text>{{ item.name }}</text>
                <text style="margin-left: 10rpx">{{ item.mobile }}</text>
              </view>
            </view>
          </view>
          <view class="address__item-top--edit" @click.stop="handleNavToEdit(item.id!)">
            <view style="margin-top: 19rpx; text-align: right">
              <q-icon size="44rpx" style="margin-right: 8rpx" name="icon-wenbenshuru" />
            </view>
          </view>
        </view>
        <view class="address__line"></view>
        <view class="address__item-bottom">
          <!-- 站位 -->
          <view />
          <radio class="address__item-bottom--radio" :value="item.id" :checked="item.id === defaultAddressId" color="#F54319">
            <text style="color: rgba(0, 0, 0, 0.5)">设为默认地址</text>
          </radio>
          <view class="address__item-bottom--opration">
            <view class="flex" @click.stop="handleDelAddress(item.id!)">
              <text>删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </radio-group>
  <view class="fix" :style="`height: ${safeHeight + btnHeight}px;`" @click="navToHandle"> 添加 </view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(address) {
  width: 100%;
  overflow-y: auto;
  @include e(item) {
    padding: 0 26rpx;
    background: #fff;
    margin-bottom: 14rpx;
  }
  @include e(item-top) {
    padding: 18rpx 0;
    display: flex;
    @include m(info) {
      font-size: 34rpx;
      color: #333333;
      margin-bottom: 19rpx;
      display: flex;
      align-items: center;
      @include when(default) {
        margin-right: 9rpx;
        border-radius: 4rpx;
        background: rgba(245, 67, 25, 0.1);
        color: rgb(245, 67, 25);
        font-size: 24rpx;
        padding-left: 10rpx;
        padding-right: 10rpx;
        padding-top: 5rpx;
        padding-bottom: 5rpx;
      }
      @include when(name) {
        font-weight: bold;
      }
    }
    @include m(txt) {
      font-size: 22rpx;
      margin-bottom: 6rpx;
      color: #999;
      @include utils-ellipsis(2);
    }
    @include m(edit) {
      flex: 1;
    }
  }
  @include e(line) {
    border-bottom: 1px solid #ccc;
    margin: 0 -20rpx;
  }
  @include e(item-bottom) {
    height: 79rpx;
    @include flex(space-between);
    position: relative;
    @include m(radio) {
      position: absolute;
      left: -40rpx;
      transform: scale(0.7);
      font-size: 36rpx;
    }
    @include m(opration) {
      @include flex();
    }
  }
}
@include b(fix) {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  background: #f54319;
  font-size: 36rpx;
  line-height: 98rpx;
  text-align: center;
  color: #fff;
}
@include b(flex) {
  @include flex();
  margin: 10rpx;
  color: #707070;
}
.uni-radio {
  &:deep(.uni-radio-input) {
    transform: scale(0.9);
    margin-right: 20rpx;
  }
}
</style>
