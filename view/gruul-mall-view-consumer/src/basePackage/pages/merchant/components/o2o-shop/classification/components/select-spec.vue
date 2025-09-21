<template>
  <!-- 规格弹窗 -->
  <u-popup v-model="control" z-index="998" mode="bottom" border-radius="14" :max-height="spacHeight" @close="handleClosePopup">
    <view class="spec" @touchmove.stop.prevent>
      <view class="spec__info">
        <u-image :width="180" :height="180" :fade="true" :src="currentChoosedSku.image" />
        <view class="spec__product">
          <view style="display: flex; align-items: flex-end; justify-content: space-between; height: 60rpx">
            <view>
              <span class="spec__product--price">{{ currentChoosedSku.salePrice ? divTenThousand(currentChoosedSku.salePrice) : '0' }}</span>
            </view>
            <view class="spec__product-line">
              <view v-if="currentSkus.skus.length >= 1 && currentChoosedSku.limitType !== 'UNLIMITED'" class="spec__product-line--limit">
                <span> 限购{{ currentChoosedSku.limitNum }}件 </span>
              </view>
            </view>
          </view>
          <view class="spec__product--space" style="margin-top: 15rpx">
            {{ currentChoosedSku.specs && currentChoosedSku.specs.join(' ； ') }}
          </view>
        </view>
      </view>
      <view v-if="(currentSkus.specGroups && currentSkus.specGroups.length > 0) || currentSpecs?.productAttributes" class="spe__select">
        <scroll-view scroll-y style="max-height: 530rpx; overflow: hidden">
          <GoodSpec
            ref="GoodSpecRef"
            :params="currentSpecs"
            :sku-group="currentSkus"
            :current-choose="currentChoosedSku.specs"
            @choose-spec="handleChooseSku"
          />
        </scroll-view>
      </view>

      <view class="spec__number">
        <text style="font-size: 32rpx; font-weight: 700">购买数量</text>
        <count-number
          v-model="count"
          :rule="{
            limitNum: currentChoosedSku.limitNum,
            limitType: currentChoosedSku.limitType,
            stockType: currentChoosedSku.stockType,
            stock: currentChoosedSku.stock,
          }"
        />
      </view>
      <button class="spec__btn" @click="handleConfirm">确认</button>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref, inject, nextTick, type PropType } from 'vue'
import { doGetGoodSku, doGetGoodDetail, doAddTocar } from '@/apis/good'
import type { StorageSku, ProductSpecsSkusVO } from '@/pluginPackage/goods/commodityInfo/types'
import GoodSpec from '@/components/good-spec/good-spec.vue'
import useCartDispatcher from '@/store/dispatcher/useCartDispatcher'

const $useCartDispatcher = useCartDispatcher()
const { divTenThousand } = useConvert()

const props = defineProps({
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})

// 弹框
const control = ref(false)

// 商品数量
const count = ref(1)

// 弹框高度
const spacHeight = ref('0')

// 计算弹框高度
const computedSpacHeight = () => {
  const specGroupsHeight = currentSkus.value.specGroups?.length || currentSpecs.value?.productAttributes ? 0 : 580
  return 1050 - specGroupsHeight + 'rpx'
}

// 弹框组件
const GoodSpecRef = ref()

// 关闭弹框组件
const handleClosePopup = () => {
  GoodSpecRef.value?.refresh()
  count.value = 1
}

// 当前弹框商品的sku
const currentSkus = ref<ProductSpecsSkusVO>({
  specGroups: [],
  skus: [],
})
// 当前弹框商品选中的sku
const currentChoosedSku = ref<StorageSku>({
  id: '',
  image: '',
  initSalesVolume: 0,
  limitNum: 0,
  limitType: 'UNLIMITED',
  price: '0',
  productId: '',
  salePrice: '0',
  shopId: '0',
  specs: [''],
  stock: '0',
  stockType: 'UNLIMITED',
  weight: 0,
  salesVolume: '0',
})

// 当前弹框商品属性
const currentSpecs = ref<any>({})

// 打开弹框初始化数据
const selectSpec = async (goodId: string) => {
  const { code, success, data } = await doGetGoodSku(goodId, props.shopId)
  const { code: rescode, data: res } = await doGetGoodDetail(goodId, props.shopId)
  if (code === 200 && rescode === 200 && success && data && res) {
    // 商品规格
    currentSkus.value = data
    // 默认商品规格
    currentChoosedSku.value = data.skus[0]
    // 商品属性
    currentSpecs.value = res.extra
    spacHeight.value = computedSpacHeight()
    control.value = true
  } else {
    uni.showToast({
      title: '获取商品信息失败',
      icon: 'none',
    })
  }
}

// 选中商品
const handleChooseSku = (e: StorageSku[]) => {
  currentChoosedSku.value = e[0]
  nextTick(() => {
    count.value = 1
  })
}
// 父文件o2o-shop.vue传递--刷新购物车数据
const refreshCar = inject('refreshCar') as () => void

// 添加购物车
const handleConfirm = async () => {
  const { code, success, msg } = await doAddTocar({
    skuId: currentChoosedSku.value?.id,
    productId: currentChoosedSku.value.productId,
    num: count.value,
    shopId: props.shopId,
    productAttributes: GoodSpecRef.value?.productAttributes?.length ? GoodSpecRef.value.productAttributes : null,
  })
  if (code === 200 && success) {
    uni.showToast({
      title: '添加成功',
      icon: 'none',
    })
    refreshCar()
    $useCartDispatcher.updateCartData()
    control.value = false
  } else {
    uni.showToast({
      title: msg,
      icon: 'none',
    })
  }
}

defineExpose({
  selectSpec,
})
</script>

<style lang="scss" scoped>
@include b(spec) {
  box-sizing: border-box;
  padding: 50rpx 36rpx;
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    margin-bottom: 40rpx;
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
  @include e(number) {
    margin-top: 40rpx;
    @include flex(space-between);
  }
}
</style>
