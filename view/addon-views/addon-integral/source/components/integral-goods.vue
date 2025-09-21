<script setup lang="ts">
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import { doPutIntegralProductList, doPutIntegralProductInfoUpdateStatus, doDelIntegralProductDelete } from '../apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { IntegralGoods } from './types/goods'
import useConvert from '@/composables/useConvert'
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import SchemaForm from '@/components/SchemaForm.vue'

const { divTenThousand } = useConvert()

const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
const searchParams = ref({
  status: '',
  keywords: '',
})
const $router = useRouter()
const tableList = ref<IntegralGoods[]>([])
const tableSelectedArr = ref<IntegralGoods[]>([])
// 表单配置项
const columns = [
  {
    label: '商品名称',
    prop: 'keywords',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
    },
  },
]
initIntegralProductList()

async function initIntegralProductList() {
  const { status, keywords: keyword } = searchParams.value
  const { code, data, msg } = await doPutIntegralProductList({ keyword, status, ...pageConfig })
  if (code !== 200) {
    ElMessage.error(msg || '获取积分商品列表失败')
    return
  }
  tableList.value = data.records
  pageConfig.total = data.total
}

const handleReset = () => {
  Object.keys(searchParams.value).forEach((key) => ((searchParams.value as any)[key] = ''))
  initIntegralProductList()
}
/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (!tableSelectedArr.value.length) {
    ElMessage.error('请选择商品')
    return
  }
  try {
    const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const ids = tableSelectedArr.value.map((item) => item.id)
    await delIntegralProduct(ids)
    tableSelectedArr.value = []
  } catch (error) {
    return
  }
}
const delIntegralProduct = async (ids: string[]) => {
  const { code, data, msg } = await doDelIntegralProductDelete(ids)
  if (code !== 200) {
    ElMessage.error(msg || '删除失败')
    return
  }
  ElMessage.success(msg || '删除成功')
  initIntegralProductList()
}
/**
 * 批量上下架
 */
const handleBatchSell = async (e: 'SELL_ON' | 'SELL_OFF') => {
  if (!tableSelectedArr.value.length) {
    ElMessage.error('请选择商品')
    return
  }
  try {
    const isValidate = await ElMessageBox.confirm(`确定进行${e === 'SELL_OFF' ? '下' : '上'}架?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    // 批量上下架接口
    const ids = tableSelectedArr.value.map((item) => item.id)
    await updateStatus(e, ids)
    tableSelectedArr.value = []
  } catch (error) {
    return false
  }
}
async function updateStatus(e: 'SELL_ON' | 'SELL_OFF', ids: string[]) {
  const { code, data, msg } = await doPutIntegralProductInfoUpdateStatus(e, ids)
  if (code !== 200) {
    ElMessage.error(msg || '操作失败')
    return false
  }
  ElMessage.success('操作成功')
  initIntegralProductList()
}
/**
 * 添加商品跳转页面
 */
const handleNavToAddGoodsPage = () => {
  $router.push({
    name: 'addIntegralMallGoods',
  })
}
const handleEdit = (row: IntegralGoods) => {
  $router.push({
    name: 'addIntegralMallGoods',
    query: {
      id: row.id,
    },
  })
}
const handleBtnChange = async (e: 'SELL_OFF' | 'SELL_ON' | 'DELETE', row: IntegralGoods) => {
  switch (e) {
    // case 'SELL_OFF':
    //     {
    //         updateStatus(e, [row.id])
    //     }
    //     break
    // case 'SELL_ON':
    //     {
    //         updateStatus(e, [row.id])
    //     }
    //     break
    case 'DELETE':
      {
        try {
          const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
          if (!isValidate) return
          delIntegralProduct([row.id])
        } catch (error) {
          console.log(error)
        }
      }
      break
    default:
      break
  }
}
const handleSizeChange = (val: number) => {
  pageConfig.size = val
  initIntegralProductList()
}
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initIntegralProductList()
}
// 上下架
const changeStatus = async (val: any) => {
  const { code, data, msg } = await doPutIntegralProductInfoUpdateStatus(val.status === 'SELL_OFF' ? 'SELL_ON' : 'SELL_OFF', [val.id])
  if (code !== 200) {
    ElMessage.error(msg || '操作失败')
    return false
  }
  initIntegralProductList()
}
const handleChangeTab = () => {
  tableSelectedArr.value = []
  initIntegralProductList()
}
const productType = {
  REAL_PRODUCT: '实物商品',
  VIRTUAL_PRODUCT: '虚拟商品',
}
</script>
<template>
  <SchemaForm v-model="searchParams" :columns="columns" @searchHandle="initIntegralProductList" @handleReset="handleReset" />
  <el-tabs v-model="searchParams.status" class="demo-tabs tab_container" @tab-change="handleChangeTab">
    <el-tab-pane label="全部" name="" />
    <el-tab-pane label="已上架" name="SELL_ON" />
    <el-tab-pane label="已下架" name="SELL_OFF" />
  </el-tabs>
  <div class="handle_container">
    <el-button type="primary" @click="handleNavToAddGoodsPage">新增积分商品</el-button>
    <el-button v-if="searchParams.status !== 'SELL_ON'" :disabled="tableSelectedArr.length === 0" @click="handleBatchSell('SELL_ON')"
      >批量上架</el-button
    >
    <el-button v-if="searchParams.status !== 'SELL_OFF'" :disabled="tableSelectedArr.length === 0" @click="handleBatchSell('SELL_OFF')"
      >批量下架</el-button
    >
    <el-button :disabled="tableSelectedArr.length === 0" @click="handleBatchDelete">批量删除</el-button>
  </div>
  <q-table ref="multipleTableRef" v-model:checked-item="tableSelectedArr" :data="tableList" :selection="true" style="overflow: auto" no-border>
    <!-- <template #header></template> -->
    <q-table-column label="商品" prop="goodInfo" align="left" width="280">
      <template #default="{ row }: { row: IntegralGoods }">
        <div class="commodity">
          <div style="width: 68px; height: 68px">
            <el-image style="width: 68px; height: 68px" :src="row.pic" fit="fill"></el-image>
          </div>
          <div class="commodity__right">
            <div class="commodity__right--name">
              <el-tooltip v-if="row.name.length >= 30" effect="dark" :content="row.name" placement="top-start">
                <span>
                  <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                  {{ row?.name }}</span
                ></el-tooltip
              >
              <span v-else>
                <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ row?.name }}</span
              >
            </div>
          </div>
        </div>
      </template>
    </q-table-column>
    <!-- <q-table-column label="积分">
            <template #default="{ row }: { row: IntegralGoods }">
                <span class="shop-name">{{ row.integralPrice }}</span>
            </template>
        </q-table-column> -->
    <q-table-column label="积分" prop="productType" align="left" width="160">
      <template #default="{ row }">
        <div style="color: #ecb789">
          <span>{{ row.integralPrice }}</span>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="销售价(元)" prop="salePrice" align="left" width="160">
      <template #default="{ row }">
        <div style="color: red">
          <template v-if="divTenThousand(row.salePrice)">
            <span style="font-size: 16px">{{ divTenThousand(row.salePrice).toFixed(2).split('.')[0] }}</span>
            <span v-if="+divTenThousand(row.salePrice).toFixed(2) > 0">.{{ divTenThousand(row.salePrice).toFixed(2).split('.')[1] }} </span>
          </template>
          <span v-else>0</span>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="库存" prop="stock" align="left" width="120" />
    <q-table-column label="商品状态" width="120" align="left">
      <template #default="{ row }: { row: IntegralGoods }">
        <span class="shop-name"
          ><el-switch
            :model-value="row.status === 'SELL_ON'"
            class="ml-2"
            inline-prompt
            active-text="上架"
            inactive-text="下架"
            @change="changeStatus(row)"
        /></span>
      </template>
    </q-table-column>
    <q-table-column label="操作" align="right" width="120" fixed="right">
      <template #default="{ row }: { row: IntegralGoods }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button v-if="row.status === 'SELL_OFF'" type="danger" link @click="handleBtnChange('DELETE', row)">删除</el-button>
      </template>
    </q-table-column>
  </q-table>
  <page-manage
    class="pagination"
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
</template>
<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(commodity) {
  @include flex();
  justify-content: flex-start;
  align-items: flex-start;
  @include e(right) {
    height: 68px;
    margin-left: 10px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    // width: 100px;
    @include m(name) {
      @include utils-ellipsis(2);
    }
    @include m(price) {
      width: 150px;
      margin-top: 15px;
      font-size: 12px;
    }
  }
}
@include b(del) {
  cursor: pointer;
  margin: 0 10px;
  color: #f00;
}
@include b(bj) {
  cursor: pointer;
  margin: 0 10px;
  color: #409eff;
}
@include b(show) {
  display: none;
}
@include b(input-with-select) {
  width: 350px;
  float: right;
  position: relative;
  z-index: 9;
}
:deep .body--header {
  width: 14px;
  height: 14px;
  float: left;
  border: 0;
  transform: translate(5px, 50px);
}
// :deep .m__table--shrink {
//     margin-left: 50px;
// }
:deep .m__table--head.padding:after {
  content: '';
}
:deep .m__table--body:after {
  content: '';
}
</style>
