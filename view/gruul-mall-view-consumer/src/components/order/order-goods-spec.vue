<script lang="ts" setup>
import { computed, ref, type PropType } from 'vue'
import { isComment, isCompleted } from '@/hooks'
import storage from '@/utils/storage'
import { Decimal } from 'decimal.js-light'
import { ORDER_LIST_IMAGE_STYLE } from '@/config/order-view-config'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { aRefundType, getAsfsStatusBtnCn } from '@/pluginPackage/order/hooks/useAfssStatus'
import type { IcStatus, ORDERSTATUS, ShopOrderItem } from '@pluginPackage/order/orderList/types'
import QPrice from '@/components/q-price/index.vue'

//按钮控制器
export interface ButtonController {
  //是否展示按钮
  show: boolean
  //售后按钮文案
  text: string
  //售后按钮点击事件
  click: () => void
}

type PayStatus = keyof typeof ORDERSTATUS | undefined
const $props = defineProps({
  info: {
    type: Object as PropType<ShopOrderItem>,
    default() {
      return {}
    },
  },
  // 控制点击 商品跳转 商品详情 我的订单页面 点击不跳转
  jump: { type: Boolean, default: false },
  deliveryNum: { type: Number, default: 0 },
  // 控制按钮组
  isFooter: {
    type: Boolean,
    default: false,
  },
  payStatus: {
    type: String as PropType<PayStatus>,
    default: '',
  },
  orderNo: { type: String, default: '' },
  distributionMode: { type: String, default: '' },
  // 是否为售后页面  如果是 点击售后按钮 将会唤起售后选择弹窗 false 则是触发 apply-refund 自定义事件
  isAfsPages: { type: Boolean, default: false },
  // 是否展示评价按钮
  isShowEvaluationBtn: { type: Boolean, default: true },
  isPackage: { type: Boolean, default: false },
  icStatus: {
    type: String as PropType<keyof typeof IcStatus>,
    default: '',
  },
})
const $emit = defineEmits(['apply-refund', 'evaluateButtonController'])
// 是否展示售后类型选择器
const showAfsTypeSelector = ref(false)
//当前选择的售后类型
const afsType = ref<'RETURN_REFUND' | 'REFUND'>('REFUND')

/**
 * 导航去申请售后(设置缓存拿的最新数据)
 */
const handleGoApplyAfterSales = () => {
  //未合并 跳到 售后页 选择商品进行售后
  if (unMerged.value && $props.info.afsNo) {
    // 如果是售后页面 跳转对应售后 || 唤起售后选择
    storage.set('applyAfterSalesOrder', $props.info)
    console.log($props.info)
    return uni.navigateTo({
      url: `/pluginPackage/order/detailsRefund/DetailsRefund?no=${$props.info.afsNo}&packageId=${$props.info.packageId}`,
    })
  }
  // 不是售后页面 触发自定义事件
  $emit('apply-refund')
}
// 用于计算ShopOrderItem 是否未被合并
// 售后页 或 非合并的 item
const unMerged = computed(() => $props.isAfsPages || !$props.info.merged)
/**
 * 售后按钮控制器
 */
const afsButtonController = computed<ButtonController>(() => {
  const result = {
    show: false,
    text: '',
    click: () => {},
  } as ButtonController
  //如果未支付 则不需要显示
  if ($props.payStatus !== 'PAID') {
    return result
  }
  const shopOrderItem = $props.info
  const { status, afsStatus, packageStatus } = shopOrderItem
  //是否未发货
  const notDelivery = packageStatus === 'WAITING_FOR_DELIVER'
  //检查售后状态 确定是否已申请过售后
  //是否未申请过售后
  const nonAfs = afsStatus === 'NONE'
  //订单是否已关闭
  const closed = status !== 'OK'
  //未申请过售后
  if (nonAfs) {
    // 订单项已关闭 或订单已完成 无需申请售后
    if (closed || isCompleted(packageStatus)) {
      return result
    }

    //如果未合并 则需要计算 可申请售后价格 价格不大于 0 无法申请售后
    if (unMerged.value) {
      //计算 可售后退款价格
      const { dealPrice, num, fixPrice, freightPrice } = shopOrderItem
      const price = new Decimal(dealPrice).div(num).add(fixPrice)
      //未发货需要加上运费
      if (packageStatus && notDelivery) {
        price.add(freightPrice)
      }

      //如果总费用小于 0 则点击售后按钮提示 实付款金额为0 不支持售后
      if (price.lte(0)) {
        result.show = true
        result.text = '申请售后'
        result.click = () => {
          uni.showToast({
            title: '实付款金额为0，不支持售后',
            icon: 'none',
          })
        }
        return result
      }
    }
    //未申请过  订单项状态正常 可以申请
    result.show = true
    result.text = '申请售后'

    if ($props.distributionMode === 'INTRA_CITY_DISTRIBUTION' && !['', 'DELIVERED'].includes($props.icStatus)) {
      // 同城配送订单 当配送状态为【已送达】再展示该按钮，即其它配送状态不展示【申请售后】按钮；
      result.show = false
      result.text = ''
    }
    //已发货弹出 售后类型选择器 否则 直接使用默认类型
    result.click = unMerged.value
      ? notDelivery
        ? handleGoApplyRefund
        : () => {
            showAfsTypeSelector.value = true
            afsType.value = 'RETURN_REFUND'
          }
      : handleGoApplyAfterSales
    return result
  }
  //有售后申请
  //查看售后历史
  result.show = true
  result.text = getAsfsStatusBtnCn(afsStatus)
  result.click = handleGoApplyAfterSales
  return result
})

//评价按钮控制器
const evaluateButtonController = computed<ButtonController>(() => {
  const result = {
    show: false,
    text: '',
    click: () => {},
  } as ButtonController
  if (!$props.isShowEvaluationBtn) {
    return result
  }
  const packageStatus = $props.info.packageStatus
  const canComment = isComment(packageStatus)
  //不能评价 且 未评价 则不显示
  if (!canComment && !isCompleted(packageStatus)) {
    return result
  }
  if (afsButtonController.value.show && ['待商家审核', '退款成功'].includes(afsButtonController.value.text)) {
    result.show = false
    return result
  }
  result.show = true
  result.text = canComment ? '评价' : '已评价'
  result.click = canComment ? () => navGoAssess($props.info) : () => navGoEvaluation($props.info)
  $emit('evaluateButtonController', result)
  return result
})

/// functions

const handleNavToGoods = () => {
  if ($props.jump) {
    const { shopId, productId } = $props.info
    jumpGoods(shopId, productId)
  }
}

/**
 * 去申请退款
 */
const handleGoApplyRefund = (directJump = false) => {
  if (directJump || $props.isAfsPages) {
    // 如果是售后页面 跳转对应售后
    uni.navigateTo({
      url: `/pluginPackage/order/applyAfterSales/ApplyAfterSales?type=${afsType.value}&no=${$props.orderNo}&itemId=${$props.info?.id}`,
    })
    return
  }
  // 不是售后页面 触发自定义事件
  $emit('apply-refund')
}

/**
 * 导航去评价
 */
const navGoAssess = (info: ShopOrderItem) => {
  const key = 'RELEASE_ASSESS'
  storage.set(key, { ...info, orderNo: $props.orderNo })
  uni.navigateTo({ url: `/basePackage/pages/releaseAssess/ReleaseAssess?storageKey=${key}` })
}
const navGoEvaluation = (info: ShopOrderItem) => {
  const { productId, skuId, shopId, specs } = info
  const storageKey = 'ORDER_DETAIL_EVALUATION'
  storage.set(storageKey, { orderNo: $props.orderNo, shopId, productId, skuId, specs })
  uni.navigateTo({
    url: `/pluginPackage/order/orderDetail/Evaluation?storageKey=${storageKey}`,
  })
}
</script>

<template>
  <view class="container flex-space-between" @click="handleNavToGoods">
    <!-- 商品大图 s -->
    <view :style="{ width: ORDER_LIST_IMAGE_STYLE.width, height: ORDER_LIST_IMAGE_STYLE.height }" class="container__left">
      <u-image :src="info.image" border-radius="15" height="100%" width="100%"></u-image>
    </view>
    <!-- 商品大图 e -->
    <!-- 商品信息 s -->
    <view class="container__right flex-space-between">
      <text class="container__right--name">{{ info.productName }}</text>
      <text class="container__right--specs f14">{{ info.specs && info.specs.join(',') }}</text>
      <view class="container__right-footer flex-space-between f12">
        <QPrice :price="$props.info?.salePrice" class="container__right-footer--price" font-size="24rpx" unit="¥" />
        <text class="f12" style="color: #666666; font-weight: normal">x{{ info.num }}</text>
      </view>
    </view>

    <!-- 商品信息 e -->
  </view>
  <view v-if="isFooter" :style="{ justifyContent: isAfsPages ? 'flex-end' : 'space-between' }" class="order-goods-spec-footer">
    <!-- 实付0元暂时隐藏售后 -->
    <view></view>
    <view>
      <u-button
        v-show="afsButtonController.show"
        :custom-style="{ margin: '0 20rpx 0 10rpx', zIndex: 0, color: '#000', border: '1px solid #bbbb' }"
        :hair-line="false"
        plain
        ripple
        size="mini"
        type="info"
        @click="afsButtonController.click"
      >
        {{ afsButtonController.text }}
      </u-button>
      <u-button
        v-show="evaluateButtonController.show"
        :custom-style="{ margin: '0 20rpx 0 10rpx', width: '130rpx', zIndex: 0, color: '#000', border: '1px solid #bbbb' }"
        :hair-line="false"
        plain
        ripple
        size="mini"
        type="info"
        @click="evaluateButtonController.click"
      >
        {{ evaluateButtonController.text }}
      </u-button>
    </view>
  </view>
  <u-popup v-model="showAfsTypeSelector" :closeable="true" border-radius="14" mask-close-able mode="center" zoom @close="afsType = 'REFUND'">
    <h3 class="popupTil">申请售后</h3>
    <u-radio-group v-model="afsType" class="radioGroup">
      <u-radio v-for="(value, key) in aRefundType" :key="key" :name="key" active-color="#fa3534">{{ value }}</u-radio>
    </u-radio-group>
    <u-button
      :custom-style="{
        marginBottom: '30rpx',
        width: '80%',
        background: '#fa3534',
        color: '#fff',
      }"
      type="error"
      @click="() => handleGoApplyRefund(true)"
    >
      确定
    </u-button>
  </u-popup>
</template>
<style lang="scss" scoped>
@include b(container) {
  padding: 20rpx;
  @include e(left) {
    width: 200rpx;
    height: 150rpx;
  }
  @include e(right) {
    padding: 0 0 0 20rpx;
    flex: 1;
    flex-direction: column;
    align-items: start;
    @include m(name) {
      width: 500rpx;
      font-size: 26rpx;
      font-weight: 700;
      @include utils-ellipsis();
    }
    @include m(specs) {
      width: 440rpx;
      @include utils-ellipsis(2);
    }
  }
  @include e(right-footer) {
    width: 100%;
    font-weight: 700;
  }
}

@include b(order-goods-spec-footer) {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

@include b(radioGroup) {
  width: 600rpx;
  display: flex;
  justify-content: space-around;
  padding: 60rpx 0;
}

@include b(popupTil) {
  text-align: center;
  padding: 30rpx;
  padding-bottom: 0;
}

::v-deep .u-model__content {
  text-align: center;
}
</style>
