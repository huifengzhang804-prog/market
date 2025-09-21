<script setup lang="ts">
import { type PropType, ref, reactive, watch, watchEffect, computed } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doAddAssess } from '@/apis/consumer/footprint'
import { swipeActionWidth } from './swipe-actiom-node-width'
// 满减相关
import FullDiscountScroll from '@plugin/fullDiscount/components/fullDiscountScroll.vue'
import CountNumber from '@/components/count-number/count-number.vue'
import type { GoodListType, GoodItemType } from '../types'
import type { DoAddAssessQuery } from '@/apis/consumer/footprint/model'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { cropImg } from '@/utils'
import { doGetAssessOrder } from '@/apis/good'
import { handleParams } from '@/utils/goodsUtils'
import { debounce } from 'lodash'
import type { CartFullReductionResponseParameters } from '@/apis/plugin/fullDiscount/model'

const $prop = defineProps({
  // 常规商品列表
  list: {
    type: Array as PropType<GoodListType[]>,
    default() {
      return []
    },
  },
  isOverdue: {
    type: Boolean,
    default: false,
  },
  fullReductionList: {
    type: Object as PropType<CartFullReductionResponseParameters>,
    default: () => ({}),
  },
})
const $emit = defineEmits([
  'goodChecked',
  'goodAllChecked',
  'changeSku',
  'changeCount',
  'swipe-action-change',
  'del-sku',
  'share',
  'changeCouponShow',
  'openClick',
])
const { divTenThousand } = useConvert()
const goodFailReasonName = (item: GoodItemType) => {
  const id = item.reason || (!item.distributionMode?.length ? 109 : 110)
  let reason = ''
  switch (id) {
    case 109:
      reason = `无物流服务`
      break
    case 110:
      reason = `仅剩${item.needUpdateNum ? Number(item.skuStock.stock) : 0}件`
      break
    case 11001:
      reason = '店铺不可用'
      break
    case 11003:
      reason = 'sku不可用'
      break
    case 11004:
      reason = '超库存'
      break
    case 11005:
      reason = '超限购'
      break
    case 11006:
      reason = '售罄'
      break
    default:
      reason = '商品不可用'
      break
  }
  return reason
}

const collectBol = ref<any>(false)
const swipeActionOptions = reactive([
  {
    text: '分享',
    style: {
      backgroundColor: '#909399',
      borderRadius: '15rpx 0 0 15rpx',
    },
    icon: 'icon-fenxiang1',
  },
  {
    text: '收藏',
    style: {
      backgroundColor: '#ff9900',
    },
    icon: 'icon-shoucang2',
  },
  {
    text: '删除',
    style: {
      backgroundColor: '#fa3534',
      borderTopRightRadius: '15rpx',
      borderRadius: '0 15rpx 15rpx 0',
    },
    icon: 'icon-shanchu',
  },
])

/**
 * 商品选中方法
 * @param {Long} shopId
 */
const checked = ref('未触发')
const goodCheckHandle = (shopId: Long) => {
  checked.value = '触发了'
  $emit('goodChecked', shopId)
}
const goodCheckAllHandle = (shopId: Long) => {
  $emit('goodAllChecked', shopId)
}
const changeSkuHandle = (shopId: Long, goodId: Long, oldSkuId: Long, num: number, ite: any) => {
  $emit('changeSku', shopId, goodId, oldSkuId, num, ite)
}
const countChange = debounce((shopId: Long, goodId: Long, oldSkuId: Long, num: number, ite: any) => {
  $emit('changeCount', shopId, goodId, oldSkuId, num, ite)
}, 500)
const handleNavToDetail = (goodId: Long, shopId: Long) => {
  jumpGoods(shopId, goodId)
}
const initIsCountNumberComponentShow = () => {
  $prop.list.forEach((item) => {
    item.products.forEach((ite) => {
      ite.isCountNumberComponentShow = true
    })
  })
}
const handleNavToShop = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
const handleCouponRedemption = (item: GoodListType) => {
  $emit('openClick', item)
}
/**
 * 滑动操作
 * @param {*} index 第一个参数为通过Props传入的index参数，第二个参数为滑动按钮的索引，即options数组的索引， 用于标识第几个按钮被点击
 */
const handleSwipeAction = (uniqueId: string, index: number) => {
  // 0 分享 1 收藏 2 删除
  switch (index) {
    case 0:
      share(uniqueId)
      break
    case 1:
      collection(uniqueId)
      break

    default:
      del(uniqueId)
      break
  }
}

const findCurrentProduct = (uniqueId: string, isSkuId = false) => {
  const productsArr: Array<DoAddAssessQuery> = []
  for (let index = 0; index < $prop.list.length; index++) {
    const products = $prop.list[index].products
    const shopId = $prop.list[index].shopId
    for (let j = 0; j < products.length; j++) {
      const product = products[j]
      if (product.uniqueId === uniqueId) {
        const { id, productId, productName, image: productPic, price: productPrice, productImage, supplierId } = product
        let productObj: DoAddAssessQuery = {
          productId,
          productName,
          productPic: productPic || productImage,
          productPrice,
          shopId,
          supplierId,
        }
        if (isSkuId) {
          productObj.skuId = id
        }
        productsArr.push(productObj)
      }
    }
  }
  return productsArr
}
const share = (uniqueId: string) => {
  const currentProduct = findCurrentProduct(uniqueId)
  if (currentProduct.length) {
    const { productId: goodId, productName, productPrice: salePrice, shopId, productPic: image } = currentProduct[0]
    $emit('share', { shopId, goodId, productName, image, salePrice })
    $emit('swipe-action-change')
  }
}
const collection = async (uniqueId: string) => {
  const currentProduct = findCurrentProduct(uniqueId)
  if (currentProduct.length) {
    const { code, msg } = await doAddAssess({
      whetherCollect: !collectBol.value,
      userCollectDTOList: { shopId: currentProduct?.[0]?.shopId, productId: currentProduct?.[0]?.productId },
    })

    if (code !== 200) return uni.showToast({ title: `${msg || (collectBol.value ? '取消收藏失败' : '收藏失败')}`, icon: 'none' })
    uni.showToast({
      title: collectBol.value ? '已取消收藏' : '收藏成功',
      icon: 'none',
    })
    $emit('swipe-action-change')
  }
}
const del = (uniqueId: string) => {
  const currentProduct = findCurrentProduct(uniqueId)
  if (currentProduct.length) {
    const delData = findCurrentProduct(uniqueId, true).map((item) => ({ shopId: item.shopId, skuIds: [item.skuId], uniqueIds: [uniqueId] }))
    $emit('del-sku', delData)
  }
}

const handleOpen = async (uniqueId: string) => {
  const currentProduct = findCurrentProduct(uniqueId)
  if (currentProduct.length) {
    const { productId: goodId } = currentProduct[0]
    const datas = findCurrentProduct(uniqueId, true).map((item) => ({ shopId: item.shopId, skuIds: [item.skuId], uniqueIds: [uniqueId] }))
    const { data, code, msg } = await doGetAssessOrder(datas?.[0]?.shopId, goodId)
    if (code === 200) {
      const collectionStatus = data || false
      collectBol.value = collectionStatus
      swipeActionOptions[1].text = collectionStatus ? '已收藏' : '收藏'
      swipeActionOptions[1].style = collectionStatus
        ? {
            backgroundColor: '#ff9900',
          }
        : {
            backgroundColor: '#ff9900',
          }
      swipeActionOptions[1].icon = collectionStatus ? 'icon-pingfen' : 'icon-shoucang2'
    } else {
      uni.showToast({
        title: `${msg || '查询收藏失败'}`,
        icon: 'none',
      })
    }
  }
  $emit('swipe-action-change', uniqueId)
}
const needUpdateNum = (ite: GoodItemType) => {
  // 验证是否无物流服务
  if (!ite.distributionMode?.length) return true

  if (ite.needUpdateNum && ite.num > Number(ite.skuStock.stock)) {
    return ite.needUpdateNum
  }
  return false
}

const getIsOverdue = (ite: GoodItemType) => {
  return ($prop.isOverdue && ite.reason) || ite.needUpdateNum || !ite.distributionMode?.length
}

defineExpose({ initIsCountNumberComponentShow })
watchEffect(() => {
  console.log('======', $prop.list)
})
</script>

<template>
  <view class="car">
    <view v-for="item in $prop.list" :key="item.shopId" class="car__item" :class="{ car__item_border: $prop.isOverdue }">
      <u-checkbox-group style="width: 100%" size="35" width="45rpx">
        <!-- 全选/店铺/领券 -->
        <view v-if="!$prop.isOverdue" class="car__item-title">
          <view class="flex">
            <u-checkbox
              v-model="item.isAllChecked"
              :disabled="!item.enable"
              shape="circle"
              active-color="#F54319"
              @change="goodCheckAllHandle(item.shopId)"
            />
            <q-icon name="icon-dianpu" size="18px" />
            <text class="emphasis f14 fontBold" @click="handleNavToShop(item.shopId)">{{ item.shopName }}</text>
            <text class="f14" style="color: #ccc; margin-left: 6rpx">></text>
          </view>
          <view class="car__item-coupon f12" @click="handleCouponRedemption(item)">领券</view>
        </view>

        <!-- 满减规则 s -->
        <view v-if="fullReductionList[item.shopId]" style="margin-bottom: 10rpx; overflow: hidden">
          <FullDiscountScroll :fullDiscountList="fullReductionList[item.shopId]" />
        </view>
        <!-- 满减规则 e -->

        <!-- 同店铺商品列表 -->
        <view v-for="ite in item.products" :key="ite.productId" class="car__item-list" :class="{ overdue: getIsOverdue(ite) }">
          <!-- 商品单选 -->
          <view class="car__item-list-checkbox">
            <u-checkbox
              v-if="!$prop.isOverdue"
              v-model="ite.isChecked"
              :disabled="needUpdateNum(ite) || !item.enable"
              shape="circle"
              size="20px"
              style="width: 30px; height: 30px; pointer-events: auto"
              active-color="#F54319"
              @change="goodCheckHandle(item.shopId)"
            />
          </view>

          <!-- 左滑操作容器 -->
          <u-swipe-action
            :key="ite.id"
            :disabled="$prop.isOverdue"
            :index="ite.uniqueId"
            :options="swipeActionOptions"
            :btn-width="150"
            :show="ite.swipeAction"
            :swipe-action-width="swipeActionWidth"
            @click="handleSwipeAction"
            @open="handleOpen"
          >
            <view class="car__item-list-row">
              <!-- 图片及其左侧区域 -->
              <view class="car__item-list-row-left">
                <!-- 满减标识 s -->
                <view v-if="fullReductionList[item.shopId]" class="car__item-list-row-left-full-duction-tag"> </view>
                <!-- 满减标识 e -->

                <!-- sku info start -->
                <!-- 排列占位容器 -->
                <view class="car__item-list-row-left-placeholder"></view>

                <!-- #ifndef H5 -->
                <u-image
                  mode="aspectFit"
                  :src="cropImg(ite.image, 200, 200)"
                  :width="200"
                  :height="200"
                  border-radius="30"
                  @click="handleNavToDetail(ite.productId, item.shopId)"
                />
                <!-- #endif -->

                <!-- #ifdef H5 -->
                <!-- <lazy-load> -->
                <img
                  :src="cropImg(ite.image, 200, 200)"
                  style="width: 200rpx; height: 200rpx; border-radius: 30rpx; object-fit: cover"
                  @click="handleNavToDetail(ite.productId, item.shopId)"
                />
                <!-- </lazy-load> -->
                <!-- #endif -->

                <!-- 失效商品 -->
                <view v-if="getIsOverdue(ite)" class="car__item-list-mask">
                  <view class="car__item-list-mask--circle">
                    {{ goodFailReasonName(ite) }}
                  </view>
                </view>
                <view v-else-if="ite.skuStock?.limitType === 'SKU_LIMITED' && ite.num > ite.skuStock.limitNum" class="car__item-list-mask">
                  <view class="car__item-list-mask--circle"> 限购{{ ite.skuStock.limitNum }}件 </view>
                </view>
              </view>

              <!-- 商品详情(图片右侧区域) -->
              <view class="car__item-list-row-right">
                <!-- 商品名称 -->
                <view class="f14 goodName" @click="handleNavToDetail(ite.productId, item.shopId)">{{ ite.productName }}</view>

                <!-- 已选择的规格(点击唤出规格弹窗) -->
                <view
                  v-if="ite.specs?.length && item.enable"
                  class="car__item-list-row-right--spec"
                  @click="changeSkuHandle(item.shopId, ite.productId, ite.id, ite.num, ite)"
                >
                  <text :style="`margin-right: ${ite.productAttributes?.length ? '0' : '10'}rpx`">{{
                    [...ite.specs, ...handleParams(ite.productAttributes)].join('；')
                  }}</text>
                  <u-icon name="arrow-down"></u-icon>
                </view>

                <!-- 未过期商品显示价格及选中数量 -->
                <view v-if="!$prop.isOverdue" class="car__item-list-row-right--bottom">
                  <!-- 商品价格 -->
                  <view class="car__item-list-row-right--price">{{ divTenThousand(ite.finalPrice) }}</view>

                  <!-- 选中数量 -->
                  <view v-if="!$prop.isOverdue">
                    <CountNumber
                      v-model="ite.num"
                      class="car__item-list-row-right--count-component"
                      :rule="{
                        limitNum: ite.skuStock.limitNum,
                        limitType: ite.skuStock.limitType,
                        stockType: ite.skuStock.stockType,
                        stock: ite.skuStock.stock,
                        teamStock: '0',
                      }"
                      @change="countChange(item.shopId, ite.productId, ite.id, ite.num, ite)"
                    />
                  </view>
                </view>
              </view>
            </view>
            <!-- sku info end -->

            <!-- 左滑操作按钮组(分享/收藏/删除) -->
            <template #btn="{ option }: { option: (typeof swipeActionOptions)[0] }">
              <view class="flex-space-between swipe-action-btn f12">
                <view style="margin-bottom: 10rpx">
                  <q-icon size="45rpx" :name="option.icon"></q-icon>
                </view>
                <text>{{ option.text }}</text>
              </view>
            </template>
          </u-swipe-action>
        </view>
      </u-checkbox-group>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(car) {
  @include e(item) {
    box-sizing: border-box;
    padding: 24rpx 15rpx 40rpx 15rpx;
    background: #fff;
    border-radius: 30rpx;
    margin-bottom: 20rpx;
  }
  @include e(item_border) {
    border-top-right-radius: 0;
    border-top-left-radius: 0;
    margin-bottom: 0;
    padding: 0 15rpx;
  }
  @include e(item-title) {
    width: 100%;
    @include flex(space-between);
    height: 90rpx;
  }
  @include e(item-coupon) {
    color: $qszr-red;
  }
  @include e(item-list) {
    width: 100%;
    position: relative;
  }
  @include e(item-list-checkbox) {
    position: absolute;
    top: 0;
    bottom: 0;
    left: -1px;
    width: 30px;
    z-index: 3;
    display: flex;
    align-items: center;
    background-color: #fff;
  }
  @include e(item-list-row) {
    @include flex(flex-start, space-between);
    margin-bottom: 20rpx;
    position: relative;
  }
  @include e(item-list-row-left) {
    position: relative;
    @include flex(flex-start);
  }

  @include e(item-list-row-left-placeholder) {
    width: 60rpx;
  }

  @include e(item-list-row-left-full-duction-tag) {
    position: absolute;
    left: -15rpx;
    top: 10rpx;
  }
  @include e(item-list-row-right) {
    margin: 0 10rpx 0 15rpx;
    display: flex;
    flex-direction: column;
    flex: 1;
    justify-content: space-between;
    @include m(bottom) {
      display: flex;
      align-items: center;
      justify-content: space-between;
      white-space: nowrap;
    }
    @include m(spec) {
      width: calc(100% - 20rpx);
      font-size: 24rpx;
      color: #777777;
      border-radius: 6rpx;
      display: flex;
      align-items: center;
      & > text {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 1;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    @include m(price) {
      font-size: 34rpx;
      color: #f83f30;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 24rpx;
      }
    }
    @include m(count) {
      padding: 5rpx 10rpx;
      background: #ffffff;
      border: 2rpx solid #8f8f8f;
      border-radius: 8rpx;
      font-size: 28rpx;
      font-weight: normal;
      text-align: center;
      color: #121212;
    }
    @include m(count-component) {
    }
    @include m(num) {
      border: 1px solid;
      font-size: 28rpx;
      padding: 10rpx;
      border-radius: 5rpx;
      &::before {
        content: '×';
        display: inline-block;
      }
    }
  }
  @include e(item-list-mask) {
    position: absolute;
    left: 60rpx;
    right: 0;
    bottom: 0;
    top: 0;
    border-radius: 30rpx;
    background: rgba($color: #000, $alpha: 0.5);
    @include flex();
    @include m(circle) {
      width: 170rpx;
      height: 170rpx;
      border-radius: 50%;
      line-height: 170rpx;
      text-align: center;
      border: 1px solid #fff;
      color: #fff;
    }
  }
}

@include b(overdue) {
  @include b(car__item-list-row-right) {
    color: #aaa;
  }

  @include b(car__item-list-row-right--spec) {
    color: #aaa;
  }
}

@include b(emphasis) {
  margin: 0 10rpx;
  max-width: 450rpx;
  @include utils-ellipsis;
}
@include b(full-discount-scroll-box) {
  padding: 20rpx 0;
}

@include b(swipe-action-btn) {
  text-align: center;
  /* #ifndef MP-WEIXIN */
  height: 100%;
  align-items: center;
  justify-content: center;
  /* #endif */

  flex-direction: column;
  /* #ifdef MP-WEIXIN */
  /* 微信小程序兼容 slot 误删 */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  /* 微信小程序兼容 slot 误删 */
  /* #endif */
}

@include b(goodName) {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
