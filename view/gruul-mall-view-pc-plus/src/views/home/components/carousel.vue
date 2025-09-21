<template>
  <div v-if="props?.propertiesLists?.show" ref="mainBox">
    <el-carousel height="520px" style="z-index: 6; background-color: #fff">
      <el-carousel-item v-for="(item, index) in props?.propertiesLists?.formData" :key="index" @click="carouselItemFn(item.link)">
        <img :src="item?.img" :title="item?.title" />
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
const $router = useRouter()

const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
  shopVal: {
    type: Boolean,
    default: false,
  },
})

const navigationTitleLists = {
  所有分类: '',
  首页: '/home',
  所有商品: '/classification',
  id: '/activityClassIfication',
  限时秒杀: '/seckill',
  领券中心: '/discountCoupon',
  积分商城: '/integralShop',
} as any

interface VAL {
  id?: string
  name?: string
  type?: string
  item?: { productId?: string; shopId?: string; id?: string }
}
const carouselItemFn = (val?: VAL) => {
  switch (val?.type) {
    case '商品':
      $router.push({
        path: '/detail',
        query: { productId: val?.item?.productId, shopId: val?.item?.shopId },
      })
      break
    case '营销活动':
      $router.push({
        path: navigationTitleLists[val?.item?.id || '/'],
      })
      break
    case '活动页面':
      $router.push({
        path: '/activityClassIfication',
        query: { link: val?.item?.id, type: 'activity', shopVal: props?.shopVal as any, shopId: val?.item?.shopId },
      })
      break
    case '图文页面':
      $router.push({
        path: '/activityClassIfication',
        query: { link: val?.item?.id, type: 'custom', shopVal: props?.shopVal as any, shopId: val?.item?.shopId },
      })
      break
    case '店铺':
      $router.push({
        path: '/shop',
        query: { shopId: val?.item?.id },
      })
      break
    default:
      if (val) window.open(val as unknown as URL)
      break
  }
}
</script>

<style scoped lang="scss">
img {
  width: 1920px;
  height: 100%;
}
</style>
