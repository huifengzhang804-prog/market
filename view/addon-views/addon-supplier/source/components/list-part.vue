<script lang="ts" setup>
import { type PropType, reactive, ref, watch, nextTick } from 'vue'
import SupplierTable from './supplier-table.vue'
import { doGetShopList } from '@/apis/shops'
import { ElMessage } from 'element-plus'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'

type searchParamStatus = 'NORMAL' | 'REJECT'

interface searchParamType {
  no: string
  name: string
  status: searchParamStatus
  data: any
  applyOfData: any
}

const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
const tableData = ref([])
const shopTableRef = ref()
const commandList = ref([
  {
    label: '删除',
    name: 'Delete',
  },
  {
    label: '启用',
    name: 'NORMAL',
  },
  {
    label: '禁用',
    name: 'FORBIDDEN',
  },
])
const $prop = defineProps({
  searchParams: {
    type: Object as PropType<any>,
    default() {
      return {}
    },
  },
  currentTabChoose: {
    type: String,
    required: true,
  },
  isSelection: {
    type: Boolean,
    default() {
      return true
    },
  },
  isExamineShop: {
    type: Boolean,
    default: false,
  },
  tabRadio: {
    type: String,
    default: () => 'supplierList',
  },
})
const $emit = defineEmits(['getStoreStatus'])

watch(
  () => $prop.currentTabChoose,
  (val) => {
    switch (val) {
      case 'REJECT':
        commandList.value = [{ label: 'Delete', name: '删除' }]
        break
      case 'UNDER_REVIEW':
        commandList.value = [{ label: 'refusedTo', name: '拒绝' }]
        break
      default:
        commandList.value = [
          {
            name: 'Delete',
            label: '删除',
          },
          {
            name: 'NORMAL',
            label: '启用',
          },
          {
            name: 'FORBIDDEN',
            label: '禁用',
          },
        ]
        break
    }
  },
  { immediate: true, deep: true },
)

async function initList(param: searchParamType, from = 'none') {
  const params = Object.assign(param, pageConfig, { shopModes: 'SUPPLIER' })
  delete params.data
  delete params.applyOfData
  const { data } = await doGetShopList({ ...params, queryAuditInfo: true, settledWay: $prop.tabRadio === 'supplierAudit' ? 'APPLY' : undefined })
  tableData.value = []
  await nextTick()
  tableData.value = data.records
  pageConfig.current = +data.current
  pageConfig.total = +data.total
  pageConfig.size = +data.size
}

const commandChange = (e: string) => {
  switch (e) {
    case 'Delete':
      batchDeleteShops()
      break
    case 'NORMAL':
      batchChangeShops('NORMAL')
      break
    case 'FORBIDDEN':
      batchChangeShops('FORBIDDEN')
      break
    default:
      batchChangeShops('refusedTo')
      break
  }
}
const batchDeleteShops = () => {
  shopTableRef.value.batchDeleteShop()
}
/**
 * 批量启用禁用
 */
const batchChangeShops = async (status: string) => {
  if ($prop.currentTabChoose === 'REJECT') {
    return ElMessage.error('该商户已被拒绝')
  }
  const res = await shopTableRef.value.batchChangeStatus(status)
  if (!res) return
  initList(Object.assign($prop.searchParams, { status: $prop.currentTabChoose }))
}
const refreshHandle = (status: string) => {
  initList(Object.assign($prop.searchParams, { status: status }))
}
const handleSizeChange = (val: number) => {
  pageConfig.size = val
  initList(Object.assign($prop.searchParams, { status: $prop.currentTabChoose }))
}
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initList(Object.assign($prop.searchParams, { status: $prop.currentTabChoose }))
}
defineExpose({
  initList,
  searchParams: $prop.searchParams,
  pageConfig,
})
</script>

<template>
  <!-- 操作 -->
  <div class="handle_container">
    <q-dropdown-btn v-if="$prop.currentTabChoose === ' '" :option="commandList" title="批量操作" @right-click="commandChange" />
  </div>
  <!-- table -->
  <SupplierTable
    ref="shopTableRef"
    :key="$prop.currentTabChoose"
    :tab-radio="$prop.tabRadio"
    :tableList="tableData"
    :is-select="$prop.isSelection"
    :is-examine-shop="$prop.isExamineShop"
    :current-tab-choose="$prop.currentTabChoose"
    @get-store-status="$emit('getStoreStatus')"
    @refresh="refreshHandle"
  />
  <PageManage
    :page-num="pageConfig.current"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    class="pagination"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
</template>

<style lang="scss" scoped>
@include b(btns) {
  @include flex;
  justify-content: flex-start;
}

@include b(down) {
  height: 18px;
}

.mr-20 {
  margin-right: 20px;
}

.mb-15 {
  margin-bottom: 16px;
}

@include b(group) {
  position: relative;
  @include e(placeholder) {
    position: absolute;
    right: -2px;
    z-index: 999;
    &:hover {
      color: #337ecc !important;
    }
  }
}

@include b(text-center) {
  margin-bottom: 5px;
}
</style>
