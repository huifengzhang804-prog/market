<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import DateSelect from '@/pluginPackage/distribute/commissionDetail/components/date-select.vue'
import DataPicker from '@/pluginPackage/distribute/commissionDetail/components/data-picker.vue'
import { doGetWithdrawList } from '../apis'
import { useStatusBar } from '@/hooks/useStatusBar'
import DateClass from '@/utils/date'
import type { ApiCommissionType } from '@/apis/plugin/distribute/model'
import Auth from '@/components/auth/auth.vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
const { divTenThousand } = useConvert()
const popupSelectDate = ref(false)
const currentSelectDate = ref({ year: '', month: '' })
const searchParam = reactive<{ queryTime: string }>({
  queryTime: `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}`,
})
const startTime = ref(
  `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}-01 00:00:00`,
)
const endTime = ref(
  new Date().getMonth() + 1 === 12
    ? `${new Date().getFullYear() + 1}-01-01 00:00:00`
    : `${new Date().getFullYear()}-${new Date().getMonth() + 2 < 10 ? '0' + (new Date().getMonth() + 2) : new Date().getMonth() + 2}-01 00:00:00`,
)
const dateUtils = new DateClass()
const pageConfig = reactive({
  size: 10,
  current: 1,
  pages: 1,
  status: 'nomore',
})
// const withdrawMap = ref(new Map())
const withdrawList = ref<any>([])
// const currentTabIndex = ref(0)
const scrollHeight = computed(() => {
  const sysInfo = uni.getSystemInfoSync()
  const navHeight = uni.upx2px(88)
  const statusHeight = useStatusBar()
  const tabNodeHeight = uni.upx2px(314)
  return `${sysInfo.screenHeight - navHeight - statusHeight.value - tabNodeHeight}px`
})
const statistic = ref({
  applying: '',
  success: '',
  total: '',
})
const showtip = ref(false)

initWithdrawList()

const handleReachBottom = () => {
  initWithdrawList(true)
}
async function initWithdrawList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    withdrawList.value = await getWithdrawList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    withdrawList.value = withdrawList.value.concat(await getWithdrawList())
  }
}
async function getWithdrawList() {
  const tempObj = {
    current: pageConfig.current,
    size: pageConfig.size,
    startTime: startTime.value,
    endTime: endTime.value,
    withdrawSourceType: 'DISTRIBUTE',
    ownerType: 'DISTRIBUTOR',
  }
  const { code, data, msg } = await doGetWithdrawList(JSON.parse(JSON.stringify(tempObj)))
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取佣金明细失败'}`, icon: 'none' })
  if (data.current >= data.pages) {
    pageConfig.status = 'nomore'
  } else {
    pageConfig.status = 'loadmore'
  }
  pageConfig.pages = data.pages
  statistic.value = data.statistic
  return data.records
}
function convertSource(value: string) {
  return value === 'BANK_CARD' ? '银行卡' : value === 'WECHAT' ? '微信钱包' : '支付宝钱包'
}
function convertStatus(value: ApiCommissionType['status']) {
  const statusType = {
    APPLYING: '待审核',
    PROCESSING: '提现中',
    SUCCESS: '已到账',
    CLOSED: '提现失败',
    FORBIDDEN: '已拒绝',
  }
  return statusType[value]
}
const handleDetermine = () => {
  const { month, year } = currentSelectDate.value
  if (month) {
    searchParam.queryTime = Number(month) < 10 ? `${year}-0${month}` : `${year}-${month}`
    startTime.value = Number(month) < 10 ? `${year}-0${month}-01 00:00:00` : `${year}-${month}-01 00:00:00`
    if (Number(month) === 12) {
      endTime.value = `${year + 1}-01-01 00:00:00`
    } else {
      endTime.value = Number(month + 1) < 10 ? `${year}-0${month + 1}-01 00:00:00` : `${year}-${month + 1}-01 00:00:00`
    }
  }
  initWithdrawList()
  popupSelectDate.value = false
}
</script>

<template>
  <q-nav title="提现记录" color="#222222" />
  <view class="head">
    <view class="head__num">{{ divTenThousand(statistic?.total) }}</view>
    <view class="head__word">
      <view class="head__word--title">累计提现(元)</view>
      <q-icon name="icon-wenhao" size="34rpx" color="#fff" @click="showtip = true" />
      <!-- <u-icon name="question-circle-fill" size="46" color="#fff" @click="showtip = true" /> -->
    </view>
  </view>
  <view class="filter">
    <date-select class="date-select" :query-time="searchParam.queryTime" @click="popupSelectDate = true" />
    <view class="filter__right">
      <view class="filter__right--received"
        >已到账：<span style="color: #fa3534">￥{{ divTenThousand(statistic?.success) }}</span></view
      >
    </view>
    <view class="filter__right--examine"
      >待审核：<span style="color: #fa3534">￥{{ divTenThousand(statistic?.applying) }}</span></view
    >
  </view>
  <u-popup v-model="popupSelectDate" mode="bottom" border-radius="20" safe-area-inset-bottom>
    <view class="popupSelectDate-title">
      <view @click="popupSelectDate = false">取消</view>
      <view style="color: #f54319" @click="handleDetermine">确认</view>
    </view>
    <u-line color="#ccc" />
    <data-picker @change="currentSelectDate = $event" />
  </u-popup>
  <!-- <scroll-view scroll-y :style="{ height: scrollHeight }" class="commissionList" @scrolltolower="handleReachBottom"> -->
  <view v-for="(item, value) in withdrawList" :key="value" class="item">
    <view class="item__num">{{ divTenThousand(item.drawType?.amount).toFixed(2) }}</view>
    <!-- <view class="item__num">999.99</view> -->
    <view class="item__middle">
      <view>
        <q-icon v-if="item.status === 'APPLYING'" name="icon-item_icon" size="38rpx" color="#005cf4" />
        <!-- <u-icon v-if="item.status === 'APPLYING'" name="clock-fill" color="#3EAEFF" size="40"></u-icon> -->
        <q-icon v-else-if="item.status === 'SUCCESS'" name="icon-a-item_icon2" size="38rpx" color="#00CF78" />
        <q-icon v-else-if="item.status === 'PROCESSING'" name="icon-jinhangzhong2" size="38rpx" color="#FD9224" />
        <q-icon v-else-if="item.status === 'CLOSED'" name="icon-shibai" size="38rpx" color="#999999" />
        <!-- <u-icon v-else-if="item.status === 'SUCCESS'" name="checkmark-circle-fill" color="#3ABB07" size="40"></u-icon> -->
        <u-icon v-else name="error-circle-fill" color="#E60C00" size="38"></u-icon>
        {{ convertStatus(item.status) }}
        <span v-if="item.status === 'APPLYING'"></span>
        <span v-else-if="item.status === 'SUCCESS'"></span>
        <span v-else class="span"></span>
      </view>
      <view class="item__middle--time">{{ item.createTime }}</view>
    </view>
    <view class="item__right">
      <view>{{ convertSource(item.drawType?.type) }}</view>
      <view style="font-size: 24rpx; margin-top: 8rpx">{{ item.updateTime }}</view>
    </view>
  </view>
  <!-- <u-loadmore :status="pageConfig.status" /> -->
  <!-- </scroll-view> -->
  <u-popup v-model="showtip" mode="bottom" border-radius="14" width="600rpx">
    <view style="padding: 20rpx">
      <view style="text-align: center; font-size: 32rpx; margin: 30rpx 0; font-weight: bold">说明</view>
      <view> 1、累计提现是指所有提现已到账的金额之和</view>
      <view>2、佣金余额 = 累计佣金 - 累计提现佣金</view>

      <view style="margin-top: 40rpx">提现状态</view>
      <view> 1、待审核是指提现申请正在审核过程中 </view>
      <view>2、提现成功是指提现申请通过并打款至您的账号上</view>
      <view>3、提现失败是指提现申请被拒绝或打款至提现账号失败</view>
      <view>4、分账成功是指系统自动将分销佣金打款至您的账号上</view>
      <view style="margin-bottom: 40rpx">5、分账失败是指系统自动分账失败请耐心等待平台重新分账</view>
    </view>
  </u-popup>
  <Auth />
</template>
<style lang="scss" scoped>
@include b(head) {
  color: #fff;
  background-color: #fd5e37;
  height: 167rpx;
  text-align: center;
  padding: 30rpx 0;
  @include e(word) {
    font-size: 26rpx;
    line-height: 36rpx;
    display: flex;
    justify-content: center;
    @include m(title) {
      transform: translateY(-2rpx);
      margin-right: 10rpx;
    }
  }
  @include e(num) {
    font-size: 53rpx;
    font-weight: bold;
  }
}
@include b(filter) {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 2rpx;
  background-color: #fff;
  @include e(right) {
    // margin: 2rpx 0;
    display: flex;
    padding-right: 20rpx;
    @include m(received) {
      margin-right: 20rpx;
      font-size: 28rpx;
    }
    @include m(examine) {
      font-size: 28rpx;
    }
  }
}
@include b(item) {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background-color: #fff;
  height: 112rpx;
  margin-bottom: 2rpx;
  @include e(num) {
    width: 160rpx;
    font-size: 40rpx;
    font-weight: bold;
    text-align: center;
  }
  @include e(middle) {
    width: 260rpx;
    @include m(time) {
      color: #333;
      // margin-top: 20rpx;
      font-size: 22rpx;
    }
  }
  @include e(right) {
    text-align: center;
    font-size: 26rpx;
    @include m(time) {
      color: #333;
      // margin-top: 20rpx;
    }
  }
}
.span {
  font-size: 26rpx;
}
@include b(popupSelectDate-title) {
  @include flex;
  justify-content: space-between;
  padding: 20rpx 40rpx 30rpx 40rpx;
  color: #000;
  font-size: 30rpx;
}

@include b(commissionList) {
  height: 112rpx;
  background-color: #fff;
  font-size: 30rpx;
  color: #000;
  @include e(time) {
    height: 106rpx;
    line-height: 106rpx;
    padding: 0 36rpx;
  }
  @include e(list) {
    background: #fff;
    padding: 0 18rpx 0 22rpx;
  }
  @include e(item) {
    height: 120rpx;
    border-bottom: 1px solid #9c9d9c;
    @include flex(space-between);
    @include m(right) {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      font-size: 24rpx;
    }
  }
}
@include b(orderTabs) {
  width: 100%;
  height: 100rpx;
  background: #fff;
  // padding: 0 30rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  @include flex(space-between);
  @include e(item) {
    width: 50%;
    line-height: 100rpx;
    text-align: center;
    position: relative;
  }
}
@include b(choosed) {
  width: 50%;
  color: #ff0000;
  &::before {
    content: '';
    display: inline-block;
    width: 50%;
    box-sizing: border-box;
    height: 4rpx;
    background: #ff0000;
    position: absolute;
    bottom: 0;
    left: 25%;
    border-radius: 10rpx;
  }
}
.color09 {
  color: #09bb07;
}
.colorFe {
  color: #fe4e63;
}
.color44 {
  color: #444444;
}
</style>
