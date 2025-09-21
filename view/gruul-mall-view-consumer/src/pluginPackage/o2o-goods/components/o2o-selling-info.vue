<script lang="ts" setup>
import { computed, inject, type PropType, ref, type Ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { doGetAssessOrder } from '@/apis/good'
import { doAddAssess } from '@/apis/consumer/footprint'
import type { StorageSku, ProductResponse } from '@/pluginPackage/goods/commodityInfo/types'
import { onLoad } from '@dcloudio/uni-app'
import selectSpec from './select-spec.vue'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import { Decimal } from 'decimal.js-light'
import Auth from '@/components/auth/auth.vue'

const $userInfo = useUserStore()
const isCollection = ref<boolean | null | undefined>(false)

const $props = defineProps({
  name: {
    type: String,
    default: '',
  },
  sku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  goodInfo: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
})
const { divTenThousand, salesVolumeToStr } = useConvert()
const $comProvide = inject('comProvide') as {
  isPaidMember: () => boolean
  forecastPrice: Ref<string | number | DecimalType>
  memberPrice: Ref<string | DecimalType>
}

const isEquation = computed(() => Number($comProvide.forecastPrice).toFixed(2) !== Number($props.sku.salePrice).toFixed(2))
const o2oShopId = ref('')
const o2oGoodId = ref('')
onLoad((res) => {
  if (!res) return
  const { goodId, shopId } = res
  if (goodId && shopId) {
    o2oShopId.value = shopId
    o2oGoodId.value = goodId
    if ($userInfo.getterToken) {
      initAssessStatus(shopId, goodId)
    }
  }
})
/**
 * 收藏
 */
const handleAssess = async () => {
  if ($userInfo.userInfo.token) {
    const message = isCollection.value ? '取消' : '加入'
    const { shopId, productId, albumPics, name: productName, price, supplierId } = $props.goodInfo
    const productPic = albumPics[0]
    const { code, msg } = !isCollection.value
      ? await doAddAssess({
          userCollectDTOList: [{ shopId, productId, productPic, productName, productPrice: price.estimate, supplierId }],
          whetherCollect: true,
        })
      : await doAddAssess({ userCollectDTOList: [{ shopId, productId }], whetherCollect: false })
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : `${message}收藏失败`}`, icon: 'none' })
    uni.showToast({ title: `${message}收藏成功`, icon: 'none' })
    initAssessStatus(shopId, productId)
    return
  }
  // uni.showToast({ title: '请先登录', icon: 'error' })
  uni.$emit(TOKEN_DEL_KEY)
}

/**
 * 查询收藏状态
 */

async function initAssessStatus(shopId: Long, goodsId: Long) {
  const { code, msg, data } = await doGetAssessOrder(shopId, goodsId)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取收藏状态失败'}`, icon: 'none' })
  isCollection.value = data as any
}

const selectSpecRef = ref()
const openSelectSpec = (id: string) => {
  // 判断是否登录
  if (!$userInfo.getterToken) {
    uni.$emit(TOKEN_DEL_KEY)
    return
  }
  // 提示：不能直接通过provide传递子组件中的方法
  selectSpecRef.value?.selectSpec(id)
}

//sku销量
const skuSale = computed(() => {
  const salesVolume = $props.sku.salesVolume
  const initSalesVolume = $props.sku.initSalesVolume
  const totalSale = new Decimal(salesVolume || 0).add(initSalesVolume || 0)
  return totalSale.gte(10000) ? totalSale.div(10000).toFixed(2) + 'w' : totalSale.toString()
})
</script>

<template>
  <view class="selling">
    <view class="selling__info">
      <view>
        <text class="selling__info--price">
          {{ divTenThousand($comProvide.forecastPrice.value).toFixed(2) }}
        </text>

        <text v-if="isEquation" style="color: #999; margin-left: 30rpx">
          ￥
          <text class="selling__info--original-price">
            {{ $props.sku.price && Number(divTenThousand($props.sku.price)).toFixed(2) }}
          </text>
        </text>
      </view>
      <view class="selling__info--joinCar" @click="openSelectSpec(o2oGoodId)">
        <q-icon name="icon-gouwuche5" size="40rpx"></q-icon>
        <view style="width: 20rpx"></view>
        加入购物车
      </view>
    </view>
    <view class="selling__line-shop--count">
      已售 {{ skuSale }}
      <view v-if="+sku.stock < 5 && sku.stockType === 'LIMITED'" style="margin-left: 20rpx; margin-bottom: 5rpx">
        仅剩
        <text style="font-size: 36rpx; font-weight: 700; color: #e50c00">
          {{ sku.stock }}
        </text>
      </view>
    </view>
    <view class="selling__line">
      <view class="selling__line-shop">
        <view class="selling__line-shop--name">
          {{ $props.name }}
        </view>
      </view>
      <view class="selling__line--collect" @click="handleAssess">
        <q-icon v-if="isCollection" color="red" name="icon-pingfen" size="50rpx" />
        <q-icon v-else name="icon-shoucang2" size="50rpx" />
        <view style="margin-top: -15rpx"> {{ isCollection ? '已收藏' : '收藏' }}</view>
      </view>
    </view>

    <selectSpec ref="selectSpecRef" :shop-id="o2oShopId" />
    <Auth></Auth>
  </view>
</template>

<style lang="scss" scoped>
@include b(selling) {
  box-sizing: border-box;

  @include e(line) {
    font-size: 32rpx;
    line-height: 52rpx;
    display: flex;
    align-items: center;
    @include m(collect) {
      text-align: center;
      font-size: 24rpx;
      color: #999999;
      width: 100rpx;
      height: 80rpx;
      padding-left: 10rpx;
      border-left: 1px solid #ccc;
    }
  }
  @include e(line-shop) {
    flex: 1;
    @include m(count) {
      font-size: 26rpx;
      color: #999999;
      display: flex;
      align-items: center;
    }
    @include m(name) {
      color: #1e1c1c;
      font-weight: 700;
      width: 560rpx;
      @include utils-ellipsis(2);
    }
  }
  @include e(info) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0 10rpx;
    @include m(integral_price) {
      margin-left: 10rpx;
      font-weight: 700;
      font-size: 44rpx;
      color: #e31436;
    }
    @include m(original-price) {
      text-decoration: line-through;
      display: inline-block;
      font-size: 28rpx;
    }
    @include m(price) {
      font-size: 50rpx;
      color: #e31436;
      font-weight: 700;
      &::before {
        font-weight: 400;
        font-size: 40rpx;
        content: '￥';
      }
    }
    @include m(num) {
      color: #999999;
      font-size: 20rpx;
      @include flex(space-between);
      & > text {
        padding-left: 22rpx;
      }
    }
    @include m(joinCar) {
      display: flex;
      align-items: center;
      padding: 15rpx 20rpx;
      background: #fa3534;
      border-radius: 4rpx;
      color: #fff;
      font-style: 24rpx;
    }
  }
  @include e(preferential) {
    padding: 10rpx 0;
    color: #333;
    font-size: 24rpx;
    & > text:nth-child(2) {
      padding-left: 10rpx;
    }
  }
}
</style>
