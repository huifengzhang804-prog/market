<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, provide } from 'vue'
import { useRoute } from 'vue-router'
import ListSearch from '../search.vue'
import ListPart from '../list-part.vue'
import { storeStatusList } from '@/apis/shops'

defineProps({
  tabRadio: {
    type: String,
    default: () => 'supplierList',
  },
})
const statuss = ref()
const listPartRef = ref()
const $route = useRoute()
type SearchParamType = Record<'no' | 'name' | 'status', string>
const rv = reactive({
  searchParams: {
    no: '',
    name: '',
    status: $route.query.name || '',
  },
})
const storeStatusNun = ref('')
const tabChoose = ref<string>($route.query.name?.toString() || 'UNDER_REVIEW')
const tabChangeHandle = async () => {
  statuss.value = tabChoose.value
  await nextTick()
  listPartRef.value.initList({ status: tabChoose.value }, 'tabChangeHandle')
}
onMounted(() => {
  getStoreStatus()
  tabChangeHandle()
})

const getStoreStatus = async () => {
  const data = await storeStatusList({
    ...listPartRef.value.searchParams,
    shopModes: ['SUPPLIER'],
    status: 'UNDER_REVIEW',
  })
  storeStatusNun.value = data.data
}

const searchHandle = async (params: SearchParamType) => {
  if (tabChoose.value !== '') {
    listPartRef.value.initList({ ...params, status: tabChoose.value }, 'searchHandle')
    tabChoose.value === 'UNDER_REVIEW' && getStoreStatus()
    return
  }
  listPartRef.value.initList(params, 'searchHandle')
}
/**
 * 改变tabs切换并重新请求对应数据
 * @param {*} status
 */
const changeTabsEvent = (status: string) => {
  if (status.trim()) {
    tabChoose.value = status
  }
  listPartRef.value.initList({ status })
}
provide('parentTabChangeHandle', changeTabsEvent)
provide('parentTabChoose', tabChoose)
</script>

<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <el-config-provider :empty-values="[undefined, null]">
      <ListSearch :is-add-shop="false" @search-params="searchHandle" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container">
      <el-tabs v-model="tabChoose" class="tabs" @tab-change="tabChangeHandle">
        <el-tab-pane :label="`待审核(${storeStatusNun})`" name="UNDER_REVIEW"></el-tab-pane>
        <el-tab-pane label="已通过" name=""></el-tab-pane>
        <el-tab-pane label="已拒绝" name="REJECT"></el-tab-pane>
      </el-tabs>
    </div>
    <ListPart
      ref="listPartRef"
      :key="tabChoose"
      :tab-radio="tabRadio"
      :current-tab-choose="tabChoose"
      :search-params="rv.searchParams"
      :is-selection="false"
      :is-examine-shop="true"
      @get-store-status="getStoreStatus"
    />
  </div>
</template>
