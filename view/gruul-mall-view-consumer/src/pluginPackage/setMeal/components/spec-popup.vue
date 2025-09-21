<script setup lang="ts">
import { ref, type PropType, computed } from 'vue'
import GoodSpec from '@/components/good-spec/good-spec.vue'

import { doGetGoodSku } from '@/apis/good/index'
import type { ProductSpecsSkusVO, StorageSku } from '@/apis/good/model/index'
import type { extra } from '@/pluginPackage/goods/commodityInfo/types'

const { divTenThousand } = useConvert()

const props = defineProps({
  skus: {
    type: Array as PropType<StorageSku[]>,
    default: () => [],
  },
  setMenu: {
    type: Boolean,
    default: false,
  },
  allChooseAttr: {
    type: Object,
    default: () => ({}),
  },
})
const emit = defineEmits(['save-sku', 'save-attr'])

// 控制弹出框
const control = ref(false)

const productId = ref<Long>('')
/**
 * 点击确认把当前选中的sky处理后传递出去
 */
const handleConfirm = () => {
  const attributes = getProductAttributes.value
  // 判断属性长度
  let errObj: any
  if (attributes.length) {
    const required = attributes.every((item: any) => {
      if (item.isRequired && item.featureValues.length === 0) {
        errObj = item
        return false
      } else if (item.isRequired && item.featureValues.length > 0) {
        return true
      } else if (!item.isRequired) {
        return true
      }
      return false
    })
    if (!required) {
      return uni.showToast({ title: `${errObj.featureName}为必选项`, icon: 'none' })
    }
  }
  const sku = {
    [productId.value]: { ...currentChoosedSku.value, pirceFlag: true },
  }
  control.value = false
  emit('save-sku', sku)
  emit('save-attr', { key: productId.value, value: attributes })
}

const skuGroup = ref<ProductSpecsSkusVO>()
const currentChoosedSku = ref<StorageSku>()
// const count = ref(1)

/**
 * 打开弹出框请求sku数据
 * @param {*} product
 * @param {*} shopId
 */
// 当前商品全部属性
const productExtra = ref<extra>()
const open = async (product: Long, shopId: Long, skuId: Long, extra?: extra) => {
  const { code, data, msg } = await doGetGoodSku(product, shopId)
  if (code !== 200 || !data) return uni.showToast({ title: `${msg || '获取套餐规格失败'}`, icon: 'none' })
  productId.value = product
  const { specGroups } = data
  skuGroup.value = { specGroups, skus: props.skus }
  const findFirstHasStock = props.skus.find((item) => item.stock)
  if (findFirstHasStock) {
    currentChoosedSku.value = props.skus.find((item) => item.id === skuId) || findFirstHasStock
  }
  productExtra.value = extra
  control.value = true
}

// 选中改变sku
const changeCurrentChoosedSku = (e: StorageSku[]) => {
  if (e.length) {
    currentChoosedSku.value = e[0]
    console.log('选中改变', currentChoosedSku.value)
  }
}
const GoodSpecRef = ref()
const handleClosePopup = () => {
  control.value = false
  // nextTick(() => {
  //     GoodSpecRef.value?.refresh()
  // })
}
const getProductAttributes = computed(() => {
  return GoodSpecRef.value.productAttributes
})
defineExpose({
  open,
})
</script>

<template>
  <!-- 规格弹窗 s-->
  <u-popup v-model="control" mode="bottom" border-radius="14" @close="handleClosePopup">
    <view class="spec">
      <view class="spec__info">
        <u-image :width="220" :height="220" :fade="true" :src="currentChoosedSku?.image" border-radius="30rpx" class="spec__info--image">
          <template #error>
            <view style="font-size: 24rpx; font-weight: bolder; color: #615a54">无图</view>
          </template>
        </u-image>
        <view class="spec__product">
          <view style="display: flex; align-items: flex-end">
            <view class="spec__product--price">
              <span>{{ currentChoosedSku?.salePrice ? divTenThousand(currentChoosedSku.salePrice) : '0' }}</span>
            </view>
          </view>
        </view>
      </view>
      <view v-if="(skuGroup && skuGroup.specGroups!.length > 0 && control) || productExtra" class="spe__select">
        <scroll-view scroll-y style="max-height: 500rpx; overflow: hidden">
          <GoodSpec
            v-if="control"
            ref="GoodSpecRef"
            :currentSpecs="allChooseAttr[productId]"
            :params="productExtra"
            :skuGroup="skuGroup"
            :currentChoose="currentChoosedSku?.specs"
            :setMenu="props.setMenu"
            @chooseSpec="changeCurrentChoosedSku"
          />
        </scroll-view>
      </view>
      <button class="spec__btn" @click="handleConfirm">确认</button>
    </view>
  </u-popup>
  <!-- 规格弹窗 e-->
</template>

<style scoped lang="scss">
@include b(spec) {
  box-sizing: border-box;
  padding: 20rpx 30rpx 0 30rpx;
  overflow: hidden;
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    align-items: center;
    margin-bottom: 40rpx;
    @include m(image) {
      margin-right: 20rpx;
    }
  }
  @include e(product) {
    @include m(price) {
      font-size: 34rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        font-weight: normal;
        display: inline-block;
        font-size: 26rpx;
      }
    }
  }
  @include e(product-line) {
    display: flex;
    align-items: flex-end;
    font-size: 26rpx;
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
    width: 100%;
    background: #f54319;
    color: #fff;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
    margin: 20rpx 0;
  }
  @include e(number) {
    @include flex(space-between);
  }
}
</style>
