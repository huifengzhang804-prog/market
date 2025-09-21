<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/modules/user'

const $router = useRouter()
const $userStore = useUserStore()
const route = useRoute()
const navList = reactive([
  {
    name: '订单中心',
    list: [
      { name: '我的订单', link: '/personalcenter/order/myorder' },
      { name: '售后订单', link: '/personalcenter/order/aftersales' },
      { name: '评价晒单', link: '/personalcenter/order/comment' },
    ],
  },
  {
    name: '关注中心',
    list: [
      { name: '商品收藏', link: '/personalcenter/follow/goodscollection' },
      { name: '关注店铺', link: '/personalcenter/follow/shopcollection' },
      { name: '我的足迹', link: '/personalcenter/follow/footprint' },
    ],
  },
  {
    name: '资产中心',
    list: [
      { name: '我的储值', link: '/personalcenter/assets/balance' },
      { name: '我的积分', link: '/personalcenter/assets/integral' },
      // { name: '会员中心', link: '/personalcenter/assets/member' },
      // { name: '领券中心', link: '/personalcenter/assets/collectcoupons' },
      { name: '我的优惠券', link: '/personalcenter/assets/mycoupon' },
    ],
  },
  {
    name: '设置',
    list: [
      { name: '个人信息', link: '/personalcenter/set/information' },
      { name: '收货地址', link: '/personalcenter/set/address' },
      // { name: '联系客服', link: '/personalcenter/set/customerservice' },
    ],
  },
])
const navChoose = ref('')

onMounted(() => {
  navList.forEach((item) => {
    item.list.forEach((nav) => {
      if (nav.link === route.path) {
        navChoose.value = nav.name
      }
    })
  })
})

const handleClickNav = (nav: any) => {
  if (nav.link === '/personalcenter/assets/integral' && !$userStore.getterToken) return $userStore.loginTypeTrue()
  else if (nav.link === '/personalcenter/assets/mycoupon' && !$userStore.getterToken) {
    $userStore.loginTypeTrue()
    return
  }
  navChoose.value = nav.name

  $router.push({
    path: nav.link,
    query: {},
  })
}
</script>

<template>
  <div c-w-120>
    <template v-for="item in navList" :key="item.name">
      <div b-b c-bc-ccc c-fs-18 e-c-3 text-left c-h-66 c-lh-66 c-mb-21 fw-700>
        {{ item.name }}
      </div>
      <div
        v-for="nav in item.list"
        :key="nav.name"
        c-h-14
        c-fs-14
        cursor-pointer
        c-lh-14
        c-mb-15
        hover-c-c-e31436
        hover-underline
        :class="nav.name === navChoose ? 'c-c-e31436 b-l-2 c-bc-e31436' : 'e-c-3 b-l-2 c-bc-fff'"
        @click="handleClickNav(nav)"
      >
        {{ nav.name }}
      </div>
    </template>
  </div>
</template>
