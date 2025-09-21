<script lang="ts" setup>
import { computed, inject, ref, type PropType } from 'vue'
import { debounce } from 'lodash'

// 组件
import GoodSpec from '@/components/good-spec/good-spec.vue'
import CountNumber from '@/components/count-number/count-number.vue'
import BuyCartBtns from '@/pluginPackage/goods/commodityInfo/components/buy-cart-btns.vue'
import ShopStoreCheck from '@pluginPackage/shopStore/components/shopStoreCheck.vue'
import QPrice from '@/components/q-price/index.vue'
// #ifdef H5
import ModelInput from '@/components/ModelInput/ModelInput.vue'
// #endif

// 接口
import { toConfirmOrderValid } from '@/apis/order'
import { doAddTocar } from '@/apis/good'
import { doPostConsumerCollectCoupon } from '@/apis/plugin/coupon'

// 类型
import type { Shop } from '@/apis/shops/type'
import type { comDispatcherType, ComOperationType, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { DistributionKeyType, ProductFeaturesValueDTO } from '@/apis/good/model'

// 方法
import useCartDispatcher from '@/store/dispatcher/useCartDispatcher'
import { useUserStore } from '@/store/modules/user'
import { handleProductAttributes, handleProductAttributesPrice } from '@/pluginPackage/goods/commodityInfo'
import { handleParams } from '@/utils/goodsUtils'
import STORAGE from '@/utils/storage'
import { isSoldOut } from '@/utils/util'

const $useCartDispatcher = useCartDispatcher()
const comProvideInject = inject('comProvide') as comDispatcherType
const {
  setOperation,
  instruction,
  currentChoosedSku,
  TEAM: { groupInfo },
  goodInfo,
  skuGroup,
  updateSku,
  activityDisSkuOptions,
  getOrderPrice,
  currentActivity,
  getParam,
  updateInstruction,
  updateIsCar,
} = comProvideInject

const isJoinSecKill = computed(() => {
  return goodInfo.value.activity?.type === 'SPIKE' && goodInfo.value.activityOpen && goodInfo.value.skuActivity
})
const GoodSpecRef = ref<InstanceType<typeof GoodSpec>>()
const $props = defineProps({
  currentSpecs: {
    type: Array<ProductFeaturesValueDTO>,
    default: () => [],
  },
  productAttributes: {
    type: Array<ProductFeaturesValueDTO>,
    default: () => [],
  },
  isGroup: {
    type: Boolean,
    default: false,
  },
  isFreshSpec: {
    type: Boolean,
    default: false,
  },
  currentParams: {
    type: Array<string>,
    default: [],
  },
  shopInfo: {
    type: Object as PropType<Shop>,
    default: () => ({}),
  },
})

/**
 * 当前配送方式
 */
const curDistributionMode = ref<DistributionKeyType>(goodInfo.value.distributionMode[0] || 'EXPRESS')

const $emit = defineEmits([
  'update:isGroup',
  'update:isFreshSpec',
  'update:currentSpecs',
  'update:currentParams',
  'changeshowStockModal',
  'copyBargainLink',
])

const computedTeamStock = computed(() => groupInfo.value.skus.find((item) => item.skuId === currentChoosedSku.value.id)?.stock || '0')

const count = ref(1)
const handleChooseSpec = (e: StorageSku[], isUpdate = true) => {
  const currentSpecs = GoodSpecRef.value?.productAttributes || []
  // 处理选择校验
  if (isUpdate) {
    if (handleProductAttributes(currentSpecs)) {
      $emit('update:currentSpecs', currentSpecs)
    }
  }
  count.value = 1
  updateSku(e[0], isUpdate, currentSpecs)
}

// 校验规格
function validate() {
  if (!currentChoosedSku.value.id || !currentChoosedSku.value.productId) {
    uni.showToast({
      title: '请选择规格',
      icon: 'none',
    })
    return false
  }
  if (count.value < 1) {
    uni.showToast({
      title: '请选择数量',
      icon: 'none',
    })
    return false
  }
  return true
}

const handleInstructionConfirm = (type: ComOperationType) => {
  if (validate()) {
    if (type === 'JOINCART') {
      handleAddToCar()
      return
    }
    if (currentActivity.value.type === 'SPIKE') {
      setOperation.source = 'ACTIVITY'
    }
    handleBuyNow()
  }
}
const handleAddToCar = async () => {
  const { code, success, msg } = await doAddTocar({
    skuId: currentChoosedSku.value?.id,
    productId: currentChoosedSku.value.productId,
    num: count.value,
    shopId: goodInfo.value.shopId,
    productAttributes: GoodSpecRef.value?.productAttributes?.length ? GoodSpecRef.value?.productAttributes : null,
  })
  if (code === 200 && success) {
    setOperation.control = false
    uni.showToast({
      title: '添加成功',
      icon: 'none',
    })
    $useCartDispatcher.updateCartData()
  } else {
    uni.showToast({
      title: `${msg}`,
      icon: 'none',
    })
  }
}
/**
 * 立即购买方法
 */
const $comProvide = inject('comProvide') as comDispatcherType
const handleBuyNow = async () => {
  if (!goodInfo.value) return
  const couponList = $comProvide.discountMap.value.COUPON?.data?.coupons
  if (couponList?.length) {
    const { shopId, couponUserId, id } = couponList[0]
    if (!couponUserId && useUserStore().userInfo.token) {
      // 未领取已登录
      doPostConsumerCollectCoupon(shopId, id).then(() => {
        uni.showToast({
          title: '已帮您领取最佳优惠~',
          icon: 'none',
        })
      })
    }
  }
  const { name: productName, productId: id, freightTemplateId, distributionMode: productDistributionMode, sellType, supplierId } = goodInfo.value
  const { id: skuId, image, price, productId, weight, specs, salePrice } = currentChoosedSku.value

  const shopInfo = $props.shopInfo
  let paramsSalePrice =
    setOperation.immediately && (!$props.isGroup || !isJoinSecKill.value) ? salePrice : currentChoosedSku.value.activePrice || getOrderPrice()
  // 拼团或者秒杀 价格需要另作处理
  if ($props.isGroup || isJoinSecKill.value) {
    // $emit('update:isGroup', false)
    const productAttributes = GoodSpecRef.value?.productAttributes || []
    // 处理价格
    // paramsSalePrice = handleProductAttributesPrice(productAttributes) + +paramsSalePrice
    paramsSalePrice = +paramsSalePrice
  }
  const params = {
    skuId,
    image: image || goodInfo.value.pic || '',
    price,
    salePrice: paramsSalePrice,
    productId,
    productName,
    id,
    freightTemplateId,
    weight,
    sellType,
    supplierId,
    distributionMode: productDistributionMode,
    productFeaturesValue: GoodSpecRef.value?.productAttributes?.length ? GoodSpecRef.value?.productAttributes : null,
    specs,
  }
  const param = [
    Object.assign(
      {},
      shopInfo,
      { activityParam: setOperation.source === 'ACTIVITY' ? getParam(productId) : undefined },
      { products: [{ ...params, num: count.value }] },
      {
        distributionMode: distributionModeValidateUpdate(), // 配送方式
      },
    ),
  ]
  STORAGE.set('commodityInfo', param)
  const res = await toConfirmOrderValid(param)
  if (!res.success) {
    $emit('changeshowStockModal', res)
  } else {
    uni.navigateTo({
      url: `/pluginPackage/order/confirmOrder/confirmOrder?source=PRODUCT`,
    })
  }
}

function distributionModeValidateUpdate() {
  // 可能为默认值 取 goodsinfo 中的第一项
  if (goodInfo.value.distributionMode.length === 1) {
    curDistributionMode.value = goodInfo.value.distributionMode[0]
  }
  return curDistributionMode.value
}

const handleConfirm = debounce(() => {
  if (validate()) {
    $emit('update:isFreshSpec', false)
    if (goodInfo.value.extra) {
      const currentSpecs = GoodSpecRef.value?.productAttributes || []
      $emit('update:currentSpecs', currentSpecs)
      // 处理选择校验
      if (!handleProductAttributes(currentSpecs)) return
      // 处理属性参数
      const currentParams = handleParams(currentSpecs)
      console.log('处理属性参数', currentParams)
      $emit('update:currentParams', currentParams)
    }
    if (setOperation.isCar) {
      handleAddToCar()
      return
    }
    if (setOperation.type === 'BARGAIN') {
      // 直接复制砍价链接
      $emit('copyBargainLink')
    }
    if (setOperation.type === 'BUY') {
      if (isJoinSecKill.value) {
        setOperation.source = 'ACTIVITY'
      }
      handleBuyNow()
      return
    }
    setOperation.control = false
  }
}, 300)
const handleClosePopup = () => {
  updateInstruction()
  updateIsCar(false)
  if ($props.isFreshSpec) {
    GoodSpecRef.value?.refresh()
  }
}
</script>

<template>
  <u-popup v-model="setOperation.control" closeable border-radius="14" mode="bottom" @close="handleClosePopup">
    <view class="spec" @touchmove.stop.prevent>
      <view class="spec__info">
        <u-image :fade="true" mode="aspectFit" :height="180" :src="currentChoosedSku.image || goodInfo.albumPics?.[0]" :width="180" />
        <view class="spec__product">
          <view style="display: flex; align-items: flex-end; justify-content: space-between; height: 60rpx">
            <view>
              <!-- 秒杀和拼团价格另算 -->
              <QPrice
                :price="
                  goodInfo.activityOpen && goodInfo.skuActivity && (isGroup || goodInfo.activity?.type === 'SPIKE')
                    ? currentChoosedSku.activePrice || 0
                    : currentChoosedSku.salePrice
                "
                style="font-size: 1.1875rem; font-weight: bold; color: #fa5555"
                unit="￥"
              ></QPrice>
            </view>
            <view class="spec__product-line">
              <view v-if="goodInfo && skuGroup.skus.length >= 1 && currentChoosedSku.limitType !== 'UNLIMITED'" class="spec__product-line--limit">
                <span> 限购{{ currentChoosedSku.limitNum }}件 </span>
              </view>
            </view>
          </view>
          <view class="spec__product--space" style="margin-top: 15rpx">
            {{ currentChoosedSku.specs && currentChoosedSku.specs.join(' ； ') }}
          </view>
        </view>
      </view>
      <view
        v-if="(goodInfo && skuGroup.specGroups && skuGroup.specGroups.length > 0) || goodInfo.extra?.productAttributes"
        class="spe__select"
        @touchmove.stop.prevent
      >
        <scroll-view scroll-y style="max-height: 700rpx">
          <GoodSpec
            v-if="setOperation.control"
            ref="GoodSpecRef"
            :currentChoose="currentChoosedSku.specs"
            :currentSpecs="currentSpecs"
            :disableSpec="setOperation.source === 'ACTIVITY' ? activityDisSkuOptions : []"
            :params="goodInfo.extra"
            :skuGroup="skuGroup"
            :goodInfo="goodInfo"
            :isGroup="isGroup"
            @chooseSpec="handleChooseSpec"
          />
          <!-- 选择配送方式 同城配送 / 到店自提 -->
          <template v-if="!setOperation.isCar && goodInfo.distributionMode.length > 1">
            <ShopStoreCheck v-model:curDistributionMode="curDistributionMode" :list="goodInfo?.distributionMode" />
          </template>
        </scroll-view>
      </view>
      <view class="spec__number">
        <text style="font-size: 32rpx; font-weight: 700">购买数量</text>
        <!-- #ifdef H5 -->
        <!-- H5 下 使用弹窗输入组件(防淘宝) -->
        <ModelInput
          v-model="count"
          :rule="{
            limitNum: currentChoosedSku.limitNum,
            limitType: currentChoosedSku.limitType,
            stockType: currentChoosedSku.stockType,
            stock: currentChoosedSku.stock,
            teamStock: computedTeamStock,
          }"
        ></ModelInput>
        <!-- #endif -->
        <!-- #ifndef H5 -->
        <CountNumber
          v-model="count"
          :rule="{
            limitNum: currentChoosedSku.limitNum,
            limitType: currentChoosedSku.limitType,
            stockType: currentChoosedSku.stockType,
            stock: currentChoosedSku.stock,
            teamStock: computedTeamStock,
          }"
        />
        <!-- #endif -->
      </view>
      <!-- 当前商品已售罄 s -->
      <view v-if="isSoldOut([currentChoosedSku.stockType], [currentChoosedSku.stock])" class="spec__btn spec__sellout"> 商品缺货 </view>
      <!-- 当前商品已售罄 e -->
      <!-- 通过首页商品组件进入时按钮样式 -->
      <BuyCartBtns v-else-if="instruction" @btn-click="handleInstructionConfirm"></BuyCartBtns>
      <!-- 通过首页商品组件进入时按钮样式 -->
      <button v-else :disabled="setOperation.isCar ? !!isJoinSecKill : false" class="spec__btn" @click="handleConfirm">
        {{ setOperation.isCar ? (!!isJoinSecKill ? '秒杀中' : '确认') : setOperation.type === 'BARGAIN' ? '找人帮砍' : '确认' }}
      </button>
    </view>
  </u-popup>
</template>

<style lang="scss" scoped>
@include b(spec) {
  box-sizing: border-box;
  padding-top: 30rpx;
  padding-left: 36rpx;
  padding-right: 36rpx;
  // 底部安全区域
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    margin-bottom: 20rpx;
  }
  @include e(product) {
    flex: 1;
    margin-left: 10rpx;
    @include m(price) {
      font-size: 38rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 30rpx;
      }
    }
    @include m(space) {
      font-size: 24rpx;
      color: #000000;
      @include utils-ellipsis(3);
    }
  }
  @include e(product-line) {
    font-size: 24rpx;
    font-weight: 400;
    @include m(stock) {
      height: 30rpx;
      line-height: 30rpx;
      margin: 20rpx 20rpx 0 0;
    }
    @include m(limit) {
      color: #fa5555;
    }
  }
  @include e(btn) {
    margin-top: 20rpx;
    background: linear-gradient(95.47deg, #fa3534 0%, #ff794d 78.13%);
    color: #fff;
    font-size: 28rpx;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
  }
  @include e(sellout) {
    color: #ff9177;
    background: #f543190a;
  }
  @include e(number) {
    margin-top: 20rpx;
    @include flex(space-between);
  }
}
</style>
