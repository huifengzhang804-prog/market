<script setup lang="ts">
import useConvert from '@/composables/useConvert'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { doGetBargainOrder, bargainStatusCn } from '../../apis'
import type { BargainOrderList } from '../../index'
import { reactive, toRefs } from 'vue'
import SchemaForms from '@/components/SchemaForm.vue'

const pageData = reactive({
  form: { keyword: '' },
  bargainOrderList: [] as BargainOrderList[],
  chooseList: [],
})
const $routes = useRouter()
const { form, bargainOrderList, chooseList } = toRefs(pageData)
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const { divTenThousand } = useConvert()
// 表单配置项
const columns = [
  {
    label: '发起人',
    prop: 'keyword',
    valueType: 'copy',
    labelWidth: 55,
    fieldProps: {
      placeholder: '请输入活动发起人',
    },
  },
]
getBargainOrder()
async function getBargainOrder() {
  const { code, data, msg } = await doGetBargainOrder({ ...pageConfig, keyword: form.value.keyword })
  if (code !== 200) {
    ElMessage.error(msg || '获取砍价活动详情')
    return
  }
  bargainOrderList.value = data.records
  pageConfig.size = data.size
  pageConfig.total = data.total
  pageConfig.current = data.current
}

type KnownType = { [key: string]: any }

const handleReset = () => {
  Object.keys(form.value as KnownType).forEach((key) => ((form.value as KnownType)[key] = ''))
  getBargainOrder()
}

const handleSizeChange = (value: number) => {
  pageConfig.size = value
  getBargainOrder()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  getBargainOrder()
}
</script>

<template>
  <SchemaForms v-model="form" :columns="columns" @searchHandle="getBargainOrder" @handleReset="handleReset" />
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      :data="bargainOrderList"
      stripe
      :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
      :header-row-style="{ color: '#333' }"
      @selection-change="chooseList = $event"
    >
      <el-table-column label="发起人" width="240px">
        <template #default="{ row }: { row: BargainOrderList }">
          <div class="bargain_origin">
            <el-image style="width: 30px; height: 30px; border-radius: 3px" fit="" :src="row.userHeadPortrait" />
            <div class="bargain_origin--name" style="width: 170px; margin-left: 5px">
              {{ row.userNickname }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" width="280px">
        <template #default="{ row }: { row: BargainOrderList }">
          <div class="bargain_origin">
            <el-image style="width: 36px; height: 36px" fit="" :src="row.productPic" />
            <div class="bargain_origin--shop_name" style="width: 205px; margin-left: 5px">
              {{ row.productName }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="销售价(元)" width="120">
        <template #default="{ row }: { row: BargainOrderList }">
          <span>{{ row.salePrice && divTenThousand(row.salePrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="底价(元)" width="120">
        <template #default="{ row }: { row: BargainOrderList }">
          <span>{{ row.floorPrice && divTenThousand(row.floorPrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="砍价人数" width="120">
        <template #default="{ row }: { row: BargainOrderList }">
          <span>{{ row.bargainingPeople }}</span>
        </template>
      </el-table-column>
      <el-table-column label="砍价开始时间" width="200px">
        <template #default="{ row }: { row: BargainOrderList }">
          <span>{{ row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="砍价结束时间" width="200px">
        <template #default="{ row }: { row: BargainOrderList }">
          <span>{{ row.endTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }: { row: BargainOrderList }">
          <span :style="{ color: row.bargainStatus === 'SUCCESSFUL_BARGAIN' ? '#00BB2C' : '' }">{{ bargainStatusCn[row.bargainStatus] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="130" align="right">
        <template #default="{ row }: { row: BargainOrderList }">
          <el-button link type="primary" @click="() => $routes.push({ name: 'BargainHelpInfo', query: { id: row.id } })">帮砍记录</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <!-- 好用的分页器 -->
  <pageManage
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-current-change="handleCurrentChange"
    @handle-size-change="handleSizeChange"
  />
</template>

<style scoped lang="scss">
@include b(bargain_origin) {
  @include flex;
  @include m(name) {
    width: 120px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  @include m(shop_name) {
    @include utils-ellipsis(2);
  }
}
</style>
