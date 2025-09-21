<template>
  <view class="recommand">
    <RecommandItem v-for="(shop, shopIndex) in recommandShopList" :key="shopIndex" :shop-info="shop" />
  </view>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import RecommandItem from './recommand-item.vue'
import { doGetConcernRecommandShops } from '@/apis/concern'
import type { RecommandShop } from './recommand'

const pagination = reactive({
  page: 1,
  size: 10,
  pages: 0,
})
const recommandShopList = ref<RecommandShop[]>([])
const getRecommandShopList = async () => {
  const { data } = await doGetConcernRecommandShops({ current: pagination.page, size: pagination.size })
  recommandShopList.value = data?.records || []
  pagination.pages = data?.total
}
getRecommandShopList()
</script>
