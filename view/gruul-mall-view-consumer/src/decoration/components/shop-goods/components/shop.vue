<template>
  <view class="shop" :class="{ only }">
    <view class="shop__title">
      <view class="shop__title-image">
        <!-- #ifndef H5 -->
        <image :src="cropImg(shopItemInfo.logo, 112, 112)" style="width: 100%; height: 100%" :lazy-load="true"></image>
        <!-- #endif -->
        <!-- #ifdef H5 -->
        <lazy-load>
          <image :src="cropImg(shopItemInfo.logo, 112, 112)" alt style="width: 100%; height: 100%"></image>
        </lazy-load>
        <!-- #endif -->
      </view>
      <view style="margin-left: 15rpx; flex: 1; height: 90rpx; display: flex; flex-direction: column; justify-content: space-between">
        <view style="display: flex; align-items: center">
          <text
            v-if="shopItemInfo.shopType && shopItemInfo.shopType !== 'ORDINARY'"
            class="shop__tag"
            :style="{ background: shopItemInfo.shopType === 'SELF_OWNED' ? '#FB232F' : '#8239F6' }"
          >
            {{ shopType[shopItemInfo.shopType] }}
          </text>
          <text class="shop__title--name">
            {{ shopItemInfo.name }}
          </text>
        </view>
        <view class="shop__title--coupon">
          <text
            v-for="item in shopItem.shop.couponList && shopItem.shop.couponList.length > 2
              ? shopItem.shop.couponList.slice(0, 2)
              : shopItem.shop.couponList"
            :key="item.id"
          >
            {{ item.consumerDesc }}
          </text>
        </view>
      </view>
      <view style="margin-left: 26rpx" class="shop__title--button" @click="jumpShop">
        <text>进店</text>
      </view>
    </view>

    <view class="shop__main">
      <Goods
        v-for="item in goodsList"
        :key="item.id"
        :goods-item="item"
        class="shop__main-goods"
        :shopId="shopItem.shop.id"
        :mode="shopItemInfo.shopMode"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, computed } from 'vue'
import Goods from './goods.vue'
import type { PropType } from 'vue'
import { doGetOrderConcernStatusByShopId, doCancelAttentionAndAttention } from '@/apis/concern'
import { useUserStore } from '@/store/modules/user'
import { shopInfo, goods as IGods } from '../shop-goods-default'
import { doGetRetrieveCommodity, doGetShopInfo } from '@/apis/good'
import { cropImg } from '@/utils'
import type { ShopType } from './index'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

const $collectionDispatcherStore = useCollectionDispatcher()
const token = computed(() => useUserStore().getterToken)
const { divTenThousand } = useConvert()

const props = defineProps({
  only: {
    type: Boolean,
    default: false,
  },
  shopItem: {
    type: Object as PropType<(typeof shopInfo)[0]>,
    default: shopInfo[0],
  },
})

onBeforeMount(() => {
  if (!props.shopItem.shop.id) return
  checkCollect(props.shopItem.shop.id)
  initShopInfo(props.shopItem.shop.id)
  if (!props.shopItem.goods[0].id) return
  getGoodList()
})
// 收藏状态
const isCollect = ref(false)
/**
 * 查询收藏状态
 * @param {*} shopId
 */
const checkCollect = async (shopId: Long) => {
  isCollect.value = false
  if (token.value) {
    const { code, data, success, msg } = await doGetOrderConcernStatusByShopId(shopId)
    if (!success) return uni.showToast({ title: `${msg ? msg : `查询关注状态失败`}`, icon: 'none' })
    isCollect.value = data
  }
}
/**
 * 点击收藏按钮
 */
const handleCollect = () => {
  if (!token.value) return uni.$emit(TOKEN_DEL_KEY)
  if (!shopItemInfo.value.name) return
  handleConcern(shopItemInfo.value, isCollect.value)
}
/**
 * 收藏/取消收藏店铺
 */
const handleConcern = async ({ name, logo }: typeof shopItemInfo.value, collect: boolean) => {
  const { data, code, msg } = await doCancelAttentionAndAttention(name, props.shopItem.shop.id, !collect, logo)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : `${collect ? '取消' : '关注'}失败`}`, icon: 'none' })
  uni.showToast({
    title: `${collect ? '取消' : '关注'}成功`,
    icon: 'none',
  })
  $collectionDispatcherStore.updateShopData()
  checkCollect(props.shopItem.shop.id)
}

const shopType: ShopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}
// 店铺基本信息
const shopItemInfo = ref({
  name: '',
  logo: '',
  shopType: 'ORDINARY',
  shopMode: 'B2B2C',
})
/**
 * 获取店铺基本信息
 * @param {*} shopId
 */
async function initShopInfo(shopId: Long) {
  const { data, code, msg } = await doGetShopInfo({ shopId, type: 'PRODUCT_DETAIL' })
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取商铺信息失败'}`, icon: 'none' })
  if (data) {
    const { name, logo, shopType, shopMode } = data
    shopItemInfo.value = { name, logo, shopType, shopMode }
  }
}
// 商品基本信息
const goodsList = ref<typeof IGods>([])
/**
 * 获取商品列表
 */
async function getGoodList() {
  const productId = props.shopItem.goods?.map((item) => item.id).filter((item) => item)
  const { code, data, msg } = await doGetRetrieveCommodity({
    productId,
    shopId: props.shopItem.shop.id,
    searchTotalStockGtZero: true,
    showCoupon: true,
  })
  if (code !== 200 || !data) {
    return uni.showToast({
      icon: 'none',
      title: `${msg || '获取商品列表失败'}`,
    })
  }
  goodsList.value = productId.map((item) => {
    const obj = data.list.find((ele: any) => ele.productId === item)
    if (!obj)
      return {
        name: '',
        id: '' as Long,
        logo: '',
        price: '',
        onlyId: '',
      }
    const { pic, salePrices, productName, productId } = obj
    return {
      name: productName,
      id: productId,
      logo: pic,
      price: divTenThousand(salePrices[0]) as unknown as string,
    }
  })
}

/**
 * 跳转入店铺
 */
const jumpShop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${props.shopItem.shop.id}`,
  })
}
</script>

<style scoped lang="scss">
@include b(shop) {
  width: calc(100vw);
  font-size: 28rpx;
  padding-bottom: 26rpx;
  background-color: #fff;
  @include e(tag) {
    width: 47rpx;
    text-align: center;
    height: 28rpx;
    background-color: #fd0505;
    color: #fff;
    border-radius: 4rpx;
    font-size: 18rpx;
    line-height: 28rpx;
    margin-right: 8rpx;
    flex-shrink: 0;
  }
  @include e(title) {
    padding: 26rpx 20rpx 0;
    display: flex;
    align-items: center;
    @include m(name) {
      color: #000;
      font-size: 32rpx;
      font-weight: 700;
      max-width: 490rpx;
      @include utils-ellipsis(1);
    }
    @include m(coupon) {
      display: flex;
      align-items: center;
      > text {
        line-height: 28rpx;
        color: #fb232f;
        padding: 0 5rpx;
        border: 1px solid #fb232f;
        border-radius: 4rpx;
        font-size: 18rpx;
        margin-right: 10rpx;
      }
    }
    @include m(button) {
      width: 110rpx;
      height: 50rpx;
      background-color: #000;
      color: #fff;
      border-radius: 25rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  @include e(title-image) {
    width: 90rpx;
    height: 90rpx;
    border: 1px solid#eee;
    border-radius: 10rpx;
    overflow: hidden;
  }
  @include e(main) {
    padding: 20rpx;
    display: flex;
    justify-content: start;
    flex-wrap: wrap;
    .shop__main-goods {
      width: 32%;
      margin-right: 2%;
      &:nth-child(3n) {
        margin-right: 0;
      }
    }
  }
}
.shop.only {
  border-bottom: transparent;
  border-radius: 16rpx 16rpx 0 0;
}
</style>
