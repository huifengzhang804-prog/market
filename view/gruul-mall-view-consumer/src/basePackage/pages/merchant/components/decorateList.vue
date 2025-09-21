<script setup lang="ts">
import { ref, watch, type PropType } from 'vue'
import BlankHolder from '@decoration/components/blank-holder/blank-holder.vue'
import CubeBox from '@decoration/components/cube-box/cube-box.vue'
// import Good from '@decoration/components/good/good.vue'
import ResizeImage from '@decoration/components/resize-image/resize-image.vue'
import RichTextArea from '@decoration/components/rich-text/rich-text.vue'
import Search from '@decoration/components/search/search.vue'
import Separator from '@decoration/components/separator/separator.vue'
import ShopNav from '@decoration/components/shop-nav/shop-nav.vue'
import CusSwiper from '@decoration/components/swiper/swiper.vue'
import TitleBar from '@decoration/components/title-bar/title-bar.vue'
import CusVideo from '@decoration/components/cus-video/cus-video.vue'
// 插件
import SecKill from '@plugin/secKill/components/decorationComponents/sec-kill.vue'
import Coupon from '@plugin/coupon/components/decorationComponents/coupon.vue'
import { doGetEnablePageByType, doGetShopPagesDetail } from '@/apis/decoration/shop/index'
import ShopGood from '@/decoration/components/good/shop-good.vue'

interface ProperType {
  formData: any
  icon: string
  id: string
  label: string
  value: string
}

const decorateList = ref<ProperType[]>([])
const $props = defineProps({
  scrollHeight: {
    type: Number,
    default: 0,
  },
  pageId: {
    type: String,
    default: '',
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})

const safeHeight = useBottomSafe()

watch(
  $props,
  (newVal) => {
    if (newVal.pageId) {
      initDecorationFromCus(newVal.pageId)
    } else {
      initDecoration()
    }
  },
  {
    immediate: true,
  },
)

/**
 * 商铺首页获取装修数据
 */
async function initDecoration() {
  // #ifdef MP-WEIXIN
  // @ts-ignore
  const endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  // @ts-ignore
  // eslint-disable-next-line no-redeclare
  const endpointType = 'H5_APP'
  // #endif
  const { code, data } = await doGetEnablePageByType($props.shopId, endpointType, 'SHOP_HOME_PAGE')
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取装修失败',
    })
    return
  }
  if (data) {
    decorateList.value = JSON.parse(data.properties)
  }
}
/**
 * 自定义页面获取装修数据
 */
async function initDecorationFromCus(pageId: string) {
  const { code, data } = await doGetShopPagesDetail(pageId)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取装修失败',
    })
    return
  }
  decorateList.value = JSON.parse(data.properties)
}
</script>

<template>
  <!-- <scroll-view scroll-y class="scroll" :style="{ height: `${$props.scrollHeight}px` }"> -->
  <view v-for="(item, index) in decorateList" :key="index">
    <BlankHolder v-if="item.value === 'blankPaceholder'" :decoration-properties="item.formData" />
    <CubeBox v-else-if="item.value === 'cubeBox'" :decoration-properties="item.formData" />
    <ShopGood v-else-if="item.value === 'goods'" :decoration-properties="item.formData" />
    <ResizeImage v-else-if="item.value === 'resizeImage'" :decorationProperties="item.formData" />
    <RichTextArea v-else-if="item.value === 'richText'" :decoration-properties="item.formData" />
    <Search v-else-if="item.value === 'search'" :decoration-properties="item.formData" :shopId="$props.shopId" />
    <Separator v-else-if="item.value === 'separator'" :decoration-properties="item.formData" />
    <ShopNav v-else-if="item.value === 'navigation'" :decoration-properties="item.formData" />
    <CusSwiper v-else-if="item.value === 'swiper'" :decoration-properties="item.formData" :shop-id="$props.shopId" />
    <TitleBar v-else-if="item.value === 'titleBar'" :decoration-properties="item.formData" />
    <CusVideo v-else-if="item.value === 'video'" :decoration-properties="item.formData" />
    <SecKill v-else-if="item.value === 'secKill'" :decoration-properties="item.formData" :shopId="$props.shopId" />
    <Coupon v-else-if="item.value === 'coupon'" :decoration-properties="item.formData" :shopId="$props.shopId" />
  </view>
  <view :style="{ height: 50 + safeHeight + 'px' }"></view>
  <!-- </scroll-view> -->
</template>

<style lang="scss" scoped>
@include b(scroll) {
  // padding: 20rpx 20rpx 0 20rpx;
  box-sizing: border-box;
}
</style>
