<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GetStoreList, DelStore, PutStatus } from '../apis'
import { storeListType } from '../index'
import { useRouter } from 'vue-router'
import SchemaForms from '@/components/SchemaForm.vue'

const $router = useRouter()
const storeList = ref<storeListType[]>([])
const storeStatusList = reactive([
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
])
const searchParams = ref({
  state: '',
})
const columns = [
  {
    label: '门店状态',
    prop: 'state',
    valueType: 'select',
    options: storeStatusList,
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
  const { code, data } = await GetStoreList({ size: 5, status: searchParams.value.state })
  if (code === 200 && data) {
    storeList.value = data.records
  } else {
    ElMessage.error('获取列表失败')
  }
}

const GoToStoreDetail = (shopId: string, id: string, lookType: string) => {
  $router.push({
    path: '/shop/store/AddStore',
    query: {
      shopId,
      id,
      lookType,
    },
  })
}
/**
 * 删除门店
 */
const DelStoreInfo = (shopId: string, id: string) => {
  ElMessageBox.confirm('确定删除该门店吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, msg } = await DelStore(shopId, id)
    if (code === 200) {
      ElMessage.success('删除成功')
      getStoreList()
    } else {
      ElMessage.error(msg ? msg : '删除失败')
    }
  })
}
/**
 * 修改门店状态
 */
const PutStoreStatus = async (shopId: string, id: string, status: string) => {
  const { code, msg } = await PutStatus(status, [{ shopId, ids: [id] }])
  if (code === 200) {
    ElMessage.success('修改状态成功')
  } else {
    ElMessage.error(msg ? msg : '修改状态失败')
  }
  getStoreList()
}
type KnownType = { [key: string]: any }

const handleReset = () => {
  Object.keys(searchParams.value).forEach((key) => {
    ;(searchParams.value as KnownType)[key] = ''
  })
  getStoreList()
}
</script>

<template>
  <div>
    <el-config-provider :empty-values="[undefined, null]">
      <SchemaForms v-model="searchParams" :columns="columns" @searchHandle="getStoreList" @handleReset="handleReset" />
    </el-config-provider>
    <div class="handle_container">
      <el-button round type="primary" @click="$router.push('/shop/store/AddStore')">添加门店</el-button>
    </div>
    <div class="table_container">
      <el-table
        :data="storeList"
        :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
        :header-row-style="{ color: '#333' }"
        empty-text="暂无数据~"
        style="width: 100%"
      >
        <el-table-column label="门店名称" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="ellipsis">{{ row.storeName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="负责人手机号" width="130">
          <template #default="{ row }">
            <div>{{ row.functionaryPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="负责人姓名" width="100">
          <template #default="{ row }">
            <div>{{ row.functionaryName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="地址" width="350" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="ellipsis">{{ row.detailedAddress }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="NORMAL"
              inactive-value="SHOP_FORBIDDEN"
              inline-prompt
              active-text="开启"
              inactive-text="关闭"
              @click="PutStoreStatus(row.shopId, row.id, row.status)"
            />
          </template>
        </el-table-column>
        <el-table-column align="right" label="操作" width="200px">
          <template #default="{ row }">
            <span style="color: #555cfd; cursor: pointer; margin: 0 10px" @click="GoToStoreDetail(row.shopId, row.id, 'OnlyLook')">查看</span>
            <span
              v-if="row.status === 'NORMAL'"
              style="color: #555cfd; cursor: pointer; margin-left: 10px"
              @click="GoToStoreDetail(row.shopId, row.id, 'Edite')"
              >编辑</span
            >
            <!-- <span
                            v-if="row.status !== 'PLATFORM_FORBIDDEN'"
                            style="color: #2e99f3; cursor: pointer; margin: 0 10px"
                            @click="PutStoreStatus(row.shopId, row.id, row.status)"
                            >{{ row.status === 'SHOP_FORBIDDEN' ? '开启' : '关闭' }}</span
                        > -->
            <span v-if="row.status !== 'NORMAL'" style="color: #f54319; cursor: pointer; margin-left: 10px" @click="DelStoreInfo(row.shopId, row.id)"
              >删除</span
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
</style>
