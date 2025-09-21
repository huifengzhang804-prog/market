<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { Decimal } from 'decimal.js-light'
import { doGetDistributeCenter, doGetCheckWithdrawOrder, doPostWithdraw } from '../apis'
import type { ApiDistributorInfoType, SHARETYPE } from '@/apis/plugin/distribute/model'

type WithdrawType = 'WECHAT' | 'ALIPAY' | 'BANK_CARD'

const { divHundred } = useConvert()
const { divTenThousand, mulTenThousand } = useConvert()
const memberInfo = ref<ApiDistributorInfoType>({
  avatar: '',
  nickname: '',
  name: '',
  config: {
    id: '',
    level: 'ONE',
    protocol: '',
    condition: {
      types: ['APPLY'],
      requiredAmount: 0,
    },
    purchase: false,
    poster: '',
    shareType: 'RATE',
    one: '0',
  },
  createTime: '',
  id: '',
  identity: 'UserType',
  mobile: '',
  statistics: { customer: '0', order: '0', bonus: '0' },
  total: '0',
  undrawn: '0',
  unsettled: '0',
  userId: '',
  referrer: '',
  code: '',
})
const codePop = ref(false)
const qrcodeRef = ref()
const withDrawPop = ref(false)
const isAvailableWechat = ref(false)
const withdrawType = ref<WithdrawType>('ALIPAY')
const withdrawCount = ref('0')
// const validateWithdraw: Record<WithdrawType, () => boolean> = {
//     WECHAT: () => true,
//     ALIPAY: validateAli,
//     BANK_CARD: validateBank,
// }

checkAvailableWechat()

const handleNavToGoods = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/distributorGoods/distributorGoods',
  })
}
const handleNavToRanking = () => {
  uni.navigateTo({
    url: `/pluginPackage/distribute/ranking/ranking`,
  })
}
const handleNavToTeam = () => {
  uni.navigateTo({
    url: `/pluginPackage/distribute/myTeam/myTeam?referrer=${memberInfo.value.referrer ? memberInfo.value.referrer : '平台'}`,
  })
}
const handleNavToPromotionCode = () => {
  uni.navigateTo({
    url: `/pluginPackage/distribute/promotionCode/promotionCode?distributorCode=${memberInfo.value.code}`,
  })
}
const handleNavToOrder = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/order/Index',
  })
}
const handleNavToIntroduction = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/Introduction/Index',
  })
}
const handleNavToCustomer = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/customer/Index',
  })
}
/**
 * 转换输入值
 */
const handleInputBlur = (e: number | string) => {
  if (e === '') return
  const priceArr = String(new Decimal(e).absoluteValue()).split('.')
  if (priceArr.length > 1) {
    withdrawCount.value = `${priceArr[0]}.${priceArr[1].slice(0, 2)}`
    return
  } else {
    withdrawCount.value = priceArr[0]
  }
}
const handleWithDraw = () => {
  withDrawPop.value = false
  uni.navigateTo({
    url: '/pluginPackage/distribute/withdrawDeposit/withdrawDeposit',
  })
}
const handleNavToDetail = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/commissionDetail/Index',
  })
}
const handleChangeWithdrawType = (e: { detail: { value: WithdrawType } }) => {
  withdrawType.value = e.detail.value
}
/**
 * 关闭提现弹窗回调
 */
const handleCloseWithdrawPopup = () => {
  withdrawCount.value = '0'
}
/**
 * 全部提现
 */
const handleWithdrawAll = () => {
  // undrawn
  withdrawCount.value = divTenThousand(memberInfo.value.undrawn).toString()
}
const handleWithdrawSubmit = async () => {
  const tempObj = {
    type: withdrawType.value,
    amount: mulTenThousand(withdrawCount.value),
  }
  if (!withdrawType.value) {
    return uni.showToast({
      icon: 'none',
      title: '请选择提现方式',
    })
  }
  if (!Number(withdrawCount.value)) {
    return uni.showToast({
      icon: 'none',
      title: '请输入提现金额',
    })
  }
  if (Number(divTenThousand(memberInfo.value.undrawn)) < Number(withdrawCount.value)) {
    return uni.showToast({
      icon: 'none',
      title: '提现金额大于可提现金额',
    })
  }
  const { code, msg } = await doPostWithdraw(tempObj)
  if (code === 200) {
    uni.showToast({
      icon: 'none',
      title: '提现申请成功',
    })
    withDrawPop.value = false

    memberInfo.value.undrawn = (Number(memberInfo.value.undrawn) - mulTenThousand(withdrawCount.value).toNumber()).toString()
  } else {
    uni.showToast({
      icon: 'none',
      title: `${msg ? msg : '提现申请失败'}`,
    })
  }
}

async function initMemerInfo() {
  const { code, data } = await doGetDistributeCenter()
  if (code && code === 200) {
    memberInfo.value = data
    uni.$on('distributeUndrawn', (value) => {
      memberInfo.value.undrawn = value
    })
  }
}

async function checkAvailableWechat() {
  try {
    const { code, data } = await doGetCheckWithdrawOrder()
    if (code === 200 && code) {
      isAvailableWechat.value = data.wechat
    } else {
      uni.showToast({
        icon: 'none',
        title: '微信提现暂不可用',
      })
    }
  } catch (error) {
    uni.showToast({
      icon: 'none',
      title: '微信提现暂不可用',
    })
  }
}

onMounted(() => {
  initMemerInfo()
})
</script>

<template>
  <view class="header">
    <view class="header__Withdrawable">
      <view class="header__Withdrawable--num">{{ divTenThousand(memberInfo.undrawn) || 0 }}</view>
      <view class="header__word">佣金余额(元)</view>
    </view>
    <view class="header__Withdrawable">
      <view class="header__Withdrawable--num">{{ divTenThousand(memberInfo.total) || 0 }}</view>
      <view class="header__word">累计佣金</view>
    </view>
    <view class="header__Withdrawable">
      <view class="header__Withdrawable--num" style="max-width: 188px; overflow: hidden">
        {{ divTenThousand(memberInfo.unsettled) || 0 }}
      </view>
      <view class="header__word">待结算佣金</view>
    </view>
  </view>
  <view class="list">
    <view class="list__item" style="background-color: #f4a261" @click="handleNavToGoods">
      <view class="list__item--left">
        <view class="list__item--title">分销商品</view>
        <view class="list__item--desc">佣金赚不够</view>
      </view>
      <view class="list__item-right">
        <q-icon color="#FFFFFF4F" name="icon-Fdingdan" size="110rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #81b29a" @click="handleNavToRanking">
      <view class="list__item--left">
        <view class="list__item--title">佣金排行榜</view>
        <view class="list__item--desc">谁与争锋</view>
      </view>
      <view class="list__item-right">
        <q-icon color="#FFFFFF4F" name="icon-a-Frame1" size="110rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #3d5a80" @click="handleNavToTeam">
      <view class="list__item--left">
        <view class="list__item--title">我的团队</view>
        <view class="list__item--desc">一起赚佣金</view>
      </view>
      <view class="list__item-right">
        <q-icon color="#FFFFFF4F" name="icon-Frame1" size="110rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #e9c46a" @click="handleNavToPromotionCode">
      <view class="list__item--left">
        <view class="list__item--title"> 推广码</view>
        <view class="list__item--desc" style="transform: translateX(8rpx)">邀请好友扩团队</view>
      </view>
      <view class="list__item-right" style="padding-left: 20rpx; padding-top: 10rpx">
        <q-icon color="#FFFFFF4F" name="icon-a-Vector2" size="90rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #159a9c" @click="handleNavToOrder">
      <view class="list__item--left">
        <view class="list__item--title"> 分销订单</view>
        <view class="list__item--desc" style="transform: translateX(10rpx)">佣金来自哪里？</view>
      </view>
      <view class="list__item-right" style="padding: 10rpx 0 0 16rpx">
        <q-icon color="#FFFFFF4F" name="icon-a-Vector1" size="90rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #e07a5f" @click="handleNavToDetail">
      <view class="list__item--left">
        <view class="list__item--title">提现明细</view>
        <view class="list__item--desc">清晰账目</view>
      </view>
      <view class="list__item-right">
        <q-icon color="#FFFFFF4F" name="icon-Vector" size="110rpx" />
      </view>
    </view>
    <view class="list__item" style="background-color: #ee6c4d" @click="handleNavToIntroduction">
      <view class="list__item--left">
        <view class="list__item--title"> 赚钱攻略</view>
        <view class="list__item--desc">规则说明</view>
      </view>
      <view class="list__item-right">
        <q-icon color="#FFFFFF4F" name="icon-path" size="100rpx" />
      </view>
    </view>
  </view>
  <view class="but" @click="handleWithDraw">提现</view>

  <!-- 提现弹窗 -->
  <u-popup v-model="withDrawPop" border-radius="10" mode="center" width="91%" @close="handleCloseWithdrawPopup">
    <view class="withDraw">
      <view class="withDraw__title">提现(申请)</view>
      <view class="withDraw__price">
        <view class="withDraw__word">提现金额</view>
        <view class="withDraw__price-border">
          <view class="withDraw__price--unit">￥</view>
          <u-input v-model="withdrawCount" height="60" placeholder="输入金额" type="number" @blur="handleInputBlur" />
        </view>
      </view>
      <view class="withDraw__balance">
        <view class="withDraw__word">可提现佣金</view>
        <view style="display: flex; align-items: center; justify-content: space-between; width: 364rpx">
          <text>￥{{ divTenThousand(memberInfo.undrawn) || 0 }}</text>
          <text class="withDraw__balance--all" @click="handleWithdrawAll">全部提现</text>
        </view>
      </view>
      <view style="display: flex; align-items: center">
        <view class="withDraw__word">提现到</view>
        <radio-group class="withDraw__pay" @change="handleChangeWithdrawType">
          <view v-if="isAvailableWechat" class="withDraw__pay--item" style="margin-right: 20rpx">
            <radio :checked="withdrawType === 'WECHAT'" color="#DDB170" style="transform: scale(0.8)" value="WECHAT" />
            <text>微信</text>
          </view>
          <view class="withDraw__pay--item" style="margin-right: 20rpx">
            <radio :checked="withdrawType === 'ALIPAY'" color="#DDB170" style="transform: scale(0.8)" value="ALIPAY" />
            <text>支付宝</text>
          </view>
          <view class="withDraw__pay--item">
            <radio :checked="withdrawType === 'BANK_CARD'" color="#DDB170" style="transform: scale(0.8)" value="BANK_CARD" />
            <text>银行卡</text>
          </view>
        </radio-group>
      </view>
    </view>
    <view style="display: flex">
      <view class="withDraw__btn cancel" @click="withDrawPop = false">取消</view>
      <view class="withDraw__btn" @click="handleWithdrawSubmit">确定</view>
    </view>
  </u-popup>
</template>
<style lang="scss" scoped>
@include b(header) {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: #fd5e37;
  color: #fff;
  font-size: 24rpx;
  font-weight: bold;
  height: 176rpx;
  @include e(word) {
    font-size: 26rpx;
    font-weight: 600;
    margin-top: 10rpx;
    text-align: center;
  }
  @include e(Withdrawable) {
    @include m(num) {
      text-align: center;
      font-size: 53rpx;
      font-weight: 700;
    }
  }
}

@include b(list) {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 10rpx 20rpx;
  @include e(item) {
    width: 345rpx;
    height: 180rpx;
    border-radius: 20rpx;
    margin-top: 20rpx;
    color: #ffffff;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    @include m(left) {
      text-align: center;
      width: 185rpx;
    }
    @include m(title) {
      font-size: 30rpx;
      font-weight: 500;
    }
    @include m(desc) {
      margin-top: 20rpx;
      font-size: 26rpx;
      font-weight: 400;
    }
  }
  @include e(item-right) {
    width: 110rpx;
    height: 110rpx;
    margin-right: 28rpx;
    // margin: 0 auto;
  }
}

@include b(but) {
  width: 100%;
  height: 112rpx;
  background-color: #fd5e37;
  position: fixed;
  bottom: 0;
  color: #fff;
  font-size: 38rpx;
  text-align: center;
  line-height: 112rpx;
  font-weight: 500;
}

@include b(qrcode) {
  @include flex;
  margin: 40rpx 0;
}

@include b(withDraw) {
  padding: 60rpx 56rpx;
  font-size: 30rpx;
  @include e(title) {
    margin-bottom: 64rpx;
    text-align: center;
    font-size: 36rpx;
  }
  @include e(word) {
    font-size: 28rpx;
    width: 160rpx;
  }
  @include e(price-border) {
    border: 1px solid rgba(187, 187, 187, 1);
    @include flex(space-between);
    border-radius: 16rpx;
  }
  @include e(price) {
    display: flex;
    align-items: center;
    color: #000;
    @include m(unit) {
      margin-right: 10rpx;
      margin-left: 10rpx;
      color: #888888;
    }
  }
  @include e(balance) {
    margin: 34rpx 0 32rpx 0;
    display: flex;
    align-items: center;
    @include m(all) {
      color: #ff8c15;
    }
  }
  @include e(pay) {
    // box-sizing: border-box;
    font-size: 24rpx;
    @include flex(flex-start);
    @include m(item) {
      @include flex;
    }
  }
  @include e(form) {
    width: 100%;
    padding: 0 60rpx;
    box-sizing: border-box;
  }
  @include e(form-item) {
    height: 110rpx;
    @include flex;
    font-size: 30rpx;
    @include m(text) {
      margin-right: 20rpx;
      &::after {
        content: '*';
        display: inline-block;
        font-size: 26rpx;
        color: red;
      }
    }
  }
  @include e(btn) {
    width: 160rpx;
    height: 60rpx;
    line-height: 60rpx;
    background: #0f40f5;
    border-radius: 16rpx;
    text-align: center;
    color: #fff;
    font-size: 28rpx;
    margin: 20rpx auto;
  }
}

.cancel {
  border: 1px solid rgba(187, 187, 187, 1);
  color: rgba(16, 16, 16, 1);
  background: #fff;
}

.txtCenter {
  text-align: center;
}

.color6a {
  color: #6a6a6a;
}

.colora8 {
  color: #a8a8a8;
}

.flex {
  @include flex(space-between, center);
}
</style>
