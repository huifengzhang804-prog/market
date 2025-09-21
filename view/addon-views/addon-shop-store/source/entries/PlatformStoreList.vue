<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetStoreList } from '../apis'
import { storeListType } from '../index'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const $router = useRouter()
const cardShow = ref(false)
const storeList = ref<storeListType[]>([])
const searchForm = ref({
  shopName: '',
  storeName: '',
  status: '',
})
const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
// 表单配置项
const columns = [
  {
    label: '店铺名称',
    labelWidth: 75,
    prop: 'shopName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写店铺名称',
      maxlength: 20,
    },
  },
  {
    label: '门店名称',
    labelWidth: 90,
    prop: 'storeName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写门店名称',
      maxlength: 20,
    },
  },
  {
    label: '状态',
    labelWidth: 45,
    prop: 'status',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部',
      },
      {
        value: 'NORMAL',
        label: '已启用',
      },
      {
        value: 'SHOP_FORBIDDEN',
        label: '已禁用',
      },
      // {
      //     value: 'PLATFORM_FORBIDDEN',
      //     label: '平台禁用',
      // },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]

getStoreList()

/**
 * 获取门店列表
 */
async function getStoreList() {
  const { code, data } = await GetStoreList({ ...pageConfig, ...searchForm.value })
  if (code === 200 && data) {
    storeList.value = data.records
    pageConfig.total = data.total
  } else {
    ElMessage.error('获取列表失败')
  }
}

const GoToStoreDetail = (shopId: string, id: string, lookType: string) => {
  $router.push({
    path: '/store/AddStore',
    query: {
      shopId,
      id,
      lookType,
    },
  })
}
const handleSizeChange = (val: number) => {
  pageConfig.size = val
  getStoreList()
}
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  getStoreList()
}

const searchHandle = () => {
  pageConfig.current = 1
  getStoreList()
}
const resetHandle = () => {
  Object.keys(searchForm.value).forEach((key) => ((searchForm.value as any)[key] = ''))
  searchHandle()
}
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <SchemaForm v-if="!cardShow" v-model="searchForm" :columns="columns" @searchHandle="searchHandle" @handleReset="resetHandle"> </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="table_container">
      <el-table
        :data="storeList"
        :header-cell-style="{
          'background-color': '#F7F8FA',
          'font-weight': 'bold',
          color: '#333',
          height: '48px',
        }"
        :row-style="{
          height: '80px',
        }"
        style="width: 100%"
      >
        <template #empty>
          <ElTableEmpty />
        </template>
        <el-table-column label="门店名称" width="300">
          <template #default="{ row }">
            <div class="ellipsis" style="width: 220px">{{ row.storeName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="负责人姓名" width="180">
          <template #default="{ row }">
            <div>{{ row.functionaryName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="180">
          <template #default="{ row }">
            <div>{{ row.functionaryPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="店铺名称">
          <template #default="{ row }">
            <div class="ellipsis">{{ row.shopName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="门店状态" width="150">
          <template #default="{ row }">
            <div :style="{ color: row.status === 'SHOP_FORBIDDEN' ? '#999' : '#333' }">
              {{ row.status === 'NORMAL' ? '已启用' : row.status === 'SHOP_FORBIDDEN' ? '已禁用' : '平台禁用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column align="right" label="操作" width="70" fixed="right">
          <template #default="{ row }">
            <span style="color: #555cfd; cursor: pointer" @click="GoToStoreDetail(row.shopId, row.id, 'OnlyLook')">查看</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <PageManage
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      class="pagination"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </div>
</template>

<style lang="scss" scoped>
* {
  color: #333;
}
.button {
  margin: 15px 0 20px;
}

.ellipsis {
  @include utils-ellipsis(2);
  white-space: normal;
}
</style>
