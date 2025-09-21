<template>
  <view class="shop">
    <scroll-view class="shop__swiper" scroll-x enhanced :show-scrollbar="false" :scroll-left="scrollLeft" @scroll="onScroll">
      <view class="shop__swiper--container">
        <view v-for="(shop, index) in computedShopAbstract" :key="index" class="shop__swiper--item" @click="handleChangeShopIndex(index)">
          <CircleComp
            v-if="showCircle"
            :animation="currentShopIndex === index"
            :animation-speed="5000"
            :percent="currentShopIndex === index ? 100 : 0"
            circle-color="#F54319"
            default-color="#fff"
            :circle-width="3"
            bg-color="#fff"
            :size="47"
          >
            <!-- 放置一个空的插槽，不展示进度信息 -->
            <template #content>
              <image :src="shop.shop.logo" class="swiper-img" />
            </template>
          </CircleComp>
        </view>
      </view>
    </scroll-view>
    <view class="shop__bg">
      <view class="shop__info" @click="jumpShop(currentShopInfo.shop.id)">
        <view class="shop__info--advertisement">
          <image :src="currentShopInfo.shop.advertisement" style="width: calc(100vw - 40rpx); border-radius: 25rpx 25rpx 0 0; height: 365rpx" />
        </view>
      </view>
      <scroll-view scroll-x enhanced :show-scrollbar="false" style="position: absolute; bottom: 0rpx; left: 0; height: 325rpx">
        <view class="shop__goods" :style="{ justifyContent: currentShopInfo.goods?.length >= 3 ? 'space-between' : 'flex-start' }">
          <view
            v-for="(good, goodIndex) in currentShopInfo.goods"
            :key="goodIndex"
            class="shop__goods--item"
            @click="jumpGoods(currentShopInfo.shop.id, good.id)"
          >
            <image :src="good.logo" class="img" />
            <view class="name">{{ good.name.length > 5 ? good.name.slice(0, 5) : good.name }}</view>
            <view class="price">
              <GoodPrice :price="range((+good.price * 10000).toString()).toString()" :member-info="good?.memberInfo" />
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { PropType } from 'vue'
import { doGetRetrieveCommodity } from '@/apis/good'
import CircleComp from './circle.vue'
import { doPostShopInfo } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import GoodPrice from '@/decoration/components/good/good-template/good-price.vue'
import type { GoodItemType } from '../../good/good'
import { onHide, onShow } from '@dcloudio/uni-app'

const { range } = usePriceRange()

const showCircle = ref(true)
const scrollLeft = ref(0)
enum ShopTypeEnum {
  SELF_OWNED = '自营',
  PREFERRED = '优选',
  ORDINARY = '普通',
}
interface IGoodInterface {
  name: string
  id: Long
  onlyId?: string
  logo: string
  price: Long
  memberInfo?: GoodItemType['memberInfo']
}
interface ShopInfo {
  shop: {
    name: string
    id: Long
    logo: string
    advertisement: string
  }
  goods: IGoodInterface[]
}
const props = defineProps({
  shopAbstract: {
    type: Array as PropType<ShopInfo[]>,
    default: () => [],
  },
})
const computedShopAbstract = ref(props.shopAbstract?.map((item) => ({ ...item, shop: { ...item.shop, shopType: '' } })))

const jumpShop = (shopId: Long = '') => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
const currentShopInfo = computed(() => computedShopAbstract.value[currentShopIndex.value])
const currentShopIndex = ref(0)
const globalInterval = ref<any>(null)
const computedCurrentShopCheckedData = () => {
  const scrollViewWidth = 355
  if (computedShopAbstract.value.length) {
    globalInterval.value = setInterval(() => {
      if (currentShopIndex.value === computedShopAbstract.value.length - 1) {
        currentShopIndex.value = 0
        scrollLeft.value = 0
      } else {
        currentShopIndex.value++
        if (currentShopIndex.value * 66 + 50 > scrollViewWidth + scrollLeft.value) {
          scrollLeft.value = currentShopIndex.value * 66 + 50 - scrollViewWidth
        }
      }
    }, 5000)
  }
}
const onScroll = (e: any) => {
  scrollLeft.value = e.detail.scrollLeft
}
/**
 * 获取店铺基本信息
 * @param {*} shopId
 */
async function initShopInfo() {
  if (!computedShopAbstract.value?.[0]?.shop?.id) return
  const shopGoods: { [key: string]: string[] } = {}
  const goodsShop: { [key: string]: Long } = {}

  const shopIds = computedShopAbstract.value.map((item) => {
    // 记录shop下面的商品id
    shopGoods[item.shop.id] = item.goods?.map((goods) => goods.onlyId!).filter((onlyId) => onlyId)
    // 记录商品id上面的shop
    shopGoods[item.shop.id]?.forEach((onlyId) => {
      goodsShop[onlyId] = item.shop.id
    })

    return item.shop.id
  })

  const goods = await getGoods(goodsShop)
  const { data, code, msg } = await doPostShopInfo(shopIds)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取商铺信息失败'}`, icon: 'none' })
  computedShopAbstract.value.forEach((item) => {
    data.forEach((element: any) => {
      const { name, logo, id, shopType, advertisement } = element
      if (element.id === item.shop.id) {
        item.shop = { name, logo, id, shopType, advertisement }
      }
    })
    if (goods) item.goods = goods[item.shop.id]
  })
  computedShopAbstract.value = computedShopAbstract.value.filter((item) => item.shop.shopType)
}

/**
 * @: 获取商品 并组合
 */
const getGoods = async (goodsShop: { [key: string]: Long }) => {
  const ids = Object.keys(goodsShop)
  const { code, data } = await doGetRetrieveCommodity({ ids, searchTotalStockGtZero: true, size: ids.length })

  const goods: { [key: string]: any[] } = {}
  if (code !== 200 || !data) return uni.showToast({ icon: 'none', title: '获取商品列表失败' })

  data.list.forEach((item: any) => {
    const { id, productName, productId, pic, salePrices, memberInfo } = item

    const price = Array.isArray(salePrices) ? salePrices[0] : salePrices
    const obj = {
      onlyId: id,
      id: productId,
      name: productName,
      logo: pic,
      price,
      memberInfo,
    }
    if (goods[goodsShop[item.id]]) {
      goods[goodsShop[item.id]].push(obj)
    } else {
      goods[goodsShop[item.id]] = [obj]
    }
  })

  return goods
}

watch(
  () => props.shopAbstract,
  (val) => {
    initShopInfo()
    computedCurrentShopCheckedData()
  },
  { deep: true },
)

const handleChangeShopIndex = (index: number) => {
  if (globalInterval.value) {
    clearInterval(globalInterval.value)
    globalInterval.value = null
  }
  currentShopIndex.value = index
  computedCurrentShopCheckedData()
}

const clearTimer = () => {
  if (globalInterval.value) {
    clearInterval(globalInterval.value)
    globalInterval.value = null
  }
}
onHide(() => {
  clearTimer()
  showCircle.value = false
})
onShow(() => {
  handleChangeShopIndex(0)
  showCircle.value = true
})
</script>
<script lang="ts">
export default {
  options: { styleIsolation: 'shared' },
}
</script>

<style scoped lang="scss">
@include b(shop) {
  @include e(swiper) {
    @include m(container) {
      @include flex(flex-start);
    }
    @include m(item) {
      margin-left: 32rpx;
      position: relative;
      &:first-child {
        margin-left: 2rpx;
      }
    }
  }
  @include e(bg) {
    margin-top: 20rpx;
    box-sizing: border-box;
    border-radius: 8rpx;
    background-size: 100% 130%;
    position: relative;
    height: 600rpx;
  }
  @include e(info) {
    @include flex(space-between, center);
    @include m(advertisement) {
      @include flex(flex-start, center);
      @include b(name) {
        font-size: 32rpx;
        line-height: 44rpx;
      }
    }
  }
  &__goods {
    @include flex(flex-start);
    @include m(item) {
      height: 325rpx;
      border-radius: 25rpx;
      width: 31%;
      background-color: #fafafa;
      margin-left: 15rpx;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: start;
      &:first-child {
        margin-left: 0;
      }
      .img {
        width: 199rpx;
        height: 199rpx;
        padding-top: 10rpx;
      }
      .name {
        margin-top: 10rpx;
        padding: 0 10rpx;
        color: #000;
        overflow: hidden;
        white-space: nowrap;
        width: 85%;
        font-family: Arial, sans-serif;
        font-weight: 400;
        text-align: center;
        font-size: 26rpx;
      }
      .price {
        padding: 0 10rpx 10rpx 10rpx;
        color: #000;
        font-family: Arial, sans-serif;
        font-weight: 500;
        margin-top: 10rpx;
        width: 100%;
        text-align: center;
        .currency {
          font-size: 20rpx;
        }
        &-main {
          font-size: 28rpx;
          font-weight: bold;
        }
      }
    }
  }
}

@include b(swiper-img) {
  width: 101rpx;
  height: 101rpx;
  border-radius: 50%;
  object-fit: cover;
}
</style>
