<script setup lang="ts">
import { computed, reactive, ref, type PropType } from 'vue'
import CouponCard from '@plugin/coupon/components/couponCard/coupon-card.vue'
import { doGetCouponList, doPostConsumerCollectCoupon } from '@pluginPackage/coupon/apis'
import { isDiscountFn, showToast, formattingTime, formattingPrice } from '@plugin/coupon/utils'
import { useSettingStore } from '@/store/modules/setting'
import PlaceLine from '@pluginPackage/coupon/components/line.vue'
import { ProductType } from '@/apis/plugin/coupon/model'
import type { CouponQueryDTO, ApiCouponVO, ProductTypeJointType, CouponQueryStatusJointType } from '@/apis/plugin/coupon/model'
import { useUserStore } from '@/store/modules/user'

const $props = defineProps({
  isPlatform: {
    type: Boolean,
    default: false,
  },
  status: {
    type: String as PropType<CouponQueryStatusJointType>,
    default: 'UNCLAIMED',
  },
  // 筛选库存
  showStock: {
    type: Boolean,
    default: false,
  },
  // 是否领券中心
  couponCenter: { type: Boolean, default: false },
})
const emit = defineEmits(['couponClick'])
const couponList = ref<ApiCouponVO[]>([])
const pageConfig = reactive({ pages: 1 })
const $settingStore = useSettingStore()
const $useUserStore = useUserStore()
const couponListTotal = ref(0)
const couponStat = ref<CouponQueryStatusJointType>('UNCLAIMED')
const query = ref<CouponQueryDTO>({
  isPlatform: false,
  shopId: '',
  status: 'UNCLAIMED',
  lastCreateTime: '',
  current: 1,
  size: 10,
})
initGetCouponListFn()

async function initGetCouponListFn(isLoad = false) {
  query.value.isPlatform = $props.isPlatform
  if ($props.status) {
    query.value.status = $props.status
  }
  if (!isLoad) {
    // 刷新
    query.value.lastCreateTime = ''
    query.value.current = 1
    couponList.value = await getCouponListFn()
    return couponListTotal.value
  } else if (isLoad && query.value.current < pageConfig.pages) {
    query.value.lastCreateTime = couponList.value[couponList.value.length - 1].createTime
    // 更新
    query.value.current++
    couponList.value = couponList.value.concat(await getCouponListFn())
    console.log('领券中心couponList.value', couponList.value)
    return couponListTotal.value
  }
}
async function getCouponListFn() {
  // 用户未登录 不许调接口
  if (!$useUserStore.getterToken) return []
  const { code, data, msg } = await doGetCouponList(query.value)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取优惠券列表失败'}`, icon: 'none' })
    return []
  }
  if ($props.showStock) {
    data.records = data.records.filter((item: ApiCouponVO) => item.stock !== '0')
  }
  couponListTotal.value = data.total
  pageConfig.pages = data.pages
  couponStat.value = data.status
  return data.records
}
function formattingName(item: ApiCouponVO) {
  console.log(item)
  if (item.productType === ProductType.ALL) {
    return '全场商品通用'
  }
  const name = item.shopName ? item.shopName : ''
  return '限' + name + '可用'
}
function formattingPermissions(type: ProductTypeJointType) {
  return type === ProductType.SHOP_ALL || type === ProductType.ALL ? '' : '部分商品可用'
}
/**
 * 用户领取优惠券
 * @param {*} item
 */
const handelCollect = async (val: '去使用' | '立即领券' | '已使用' | '已过期', item: ApiCouponVO) => {
  switch (val) {
    case '立即领券':
      getACoupon(item)
      break
    case '去使用':
      if (item.productType === ProductType.ALL) {
        $settingStore.NAV_TO_INDEX('首页')
        return
      }
      uni.navigateTo({ url: `/basePackage/pages/merchant/Index?shopId=${item.shopId}` })
      break
    default:
      break
  }
}
const getACoupon = async (item: ApiCouponVO) => {
  const { code, data, msg } = await doPostConsumerCollectCoupon(item.shopId, item.id)
  if (code !== 200) {
    showToast(msg || '领取失败')
    return
  }
  showToast('领取成功')
  if (!data) {
    // 不能再领取
    couponList.value = couponList.value.filter((coupon) => coupon.id !== item.id)
  }
}
const handleMaskClick = (e: string) => {
  console.log('e', e)
}
defineExpose({
  couponList,
  initGetCouponListFn,
})
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - 3
})
</script>

<template>
  <PlaceLine />
  <template v-if="couponList.length">
    <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initGetCouponListFn(true)">
      <view v-for="(item, index) in couponList" :key="index" class="coupon-item" @click="emit('couponClick', item)">
        <CouponCard
          :name="formattingName(item)"
          :permissions="formattingPermissions(item.productType)"
          :time="formattingTime(item)"
          :type="item.type"
          :text="isDiscountFn(item.type) ? '折扣券' : '现金券'"
          :unit="isDiscountFn(item.type) ? '折' : '元'"
          :price="formattingPrice(item)"
          :required-amount="item.requiredAmount || '0'"
          :used-status="$props.status"
          :not-used-status="item.claimedStatus"
          :coupon-center="$props.couponCenter"
          :status="couponStat"
          @receive-click="(val) => handelCollect(val, item)"
          @mask-click="handleMaskClick"
        />
      </view>
    </scroll-view>
  </template>
  <view v-else style="font-size: 250rpx">
    <u-empty text="暂无优惠券" mode="coupon" class="coupon-empty" />
  </view>
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
@include b(coupon-item) {
  margin: 10rpx 30rpx;
  &:nth-child(2) {
    margin-top: 30rpx;
  }
  &:last-child {
    padding-bottom: 30rpx;
  }
}
@include b(coupon-empty) {
  height: 80vh;
  margin: 0 auto;
}
</style>
