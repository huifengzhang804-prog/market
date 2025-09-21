<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import Decimal from 'decimal.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetDistributeCom, doGetDistributeConfig, doPutCancelDistribute } from '../apis'
import type { ApiDistributeComItem, SHARETYPE } from '../index'
import useCalculationFormula from './hooks/useCalculationFormula'
import { ref, reactive, computed, watch } from 'vue'
import useConvert from '@/composables/useConvert'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

type DistributeInfoType = {
  current: number
  size: number
  total: number
  list: ApiDistributeComItem[]
  shopName: string
  productName: string
  distributionStatus: string
}

const cardFlag = ref(false)
const platformLevel = ref('ONE')
const platforminfo = ref<any>({})
const { divTenThousand, divHundred } = useConvert()
const distributeInfo = reactive<DistributeInfoType>({
  current: 1,
  size: 10,
  total: 0,
  list: [],
  distributionStatus: 'ALL',
  productName: '',
  shopName: '',
})
const sortState = ref('')

const selectedList = ref<ApiDistributeComItem[]>([])
// 表单配置项
const columns = [
  {
    label: '店铺名称',
    labelWidth: 75,
    prop: 'shopName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入店铺名称',
      maxlength: 20,
    },
  },
  {
    label: '商品名称',
    prop: 'productName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
      maxlength: 30,
    },
  },
]

initPlatformDisConfig()
initDisComList()

const handleChangeCurrent = (e: number) => {
  distributeInfo.current = e
  initDisComList()
}
const handleChangeSize = (e: number) => {
  distributeInfo.current = 1
  distributeInfo.size = e
  initDisComList()
}

const handleSearch = () => {
  distributeInfo.current = 1
  initDisComList()
}
/**
 * 重置搜索条件
 */
const handleReset = () => {
  distributeInfo.current = 1
  distributeInfo.productName = ''
  distributeInfo.shopName = ''
  initDisComList()
}
const handleCancelDistribution = async (info: ApiDistributeComItem) => {
  ElMessageBox.confirm('确定取消分销当前商品？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, success } = await doPutCancelDistribute([info.id])
    if (code === 200 && success) {
      ElMessage.success('取消分销成功')
      initDisComList()
    } else {
      ElMessage.error('取消分销失败')
    }
  })
}
const handleTableSelect = (e: ApiDistributeComItem[]) => {
  selectedList.value = e
}

const handleBatchCancelDistrube = () => {
  if (!selectedList.value.length) {
    ElMessage.warning('请选择商品')
    return
  }
  ElMessageBox.confirm('确定需要取消分销商品？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const paramArr = selectedList.value.map((item) => item.id)
    const { code, success } = await doPutCancelDistribute(paramArr)
    if (code === 200 && success) {
      ElMessage.success('取消分销成功')
      initDisComList()
    } else {
      ElMessage.error('取消分销失败')
    }
  })
}

async function initDisComList() {
  const { code, data } = await doGetDistributeCom({
    current: distributeInfo.current,
    size: distributeInfo.size,
    productName: distributeInfo.productName,
    shopName: distributeInfo.shopName,
    distributionStatus: distributeInfo.distributionStatus === 'ALL' ? null : distributeInfo.distributionStatus,
  })
  if (code === 200 && data) {
    distributeInfo.list = data.records.map((item: any) => ({ ...item, originSalePrices: [...new Set(item.salePrices)].sort() }))
    distributeInfo.total = data.total
    console.log('distributeInfo.list', distributeInfo.list)
  } else {
    ElMessage.error('获取分销商品失败')
  }
}
function conversionStatus(status: string) {
  return status === 'SELL_ON' ? '上架' : status === 'SELL_OFF' ? '下架' : '违规禁用'
}
const handleChangeTab = () => {
  distributeInfo.current = 1
  initDisComList()
}
function conversionCount(status: keyof typeof SHARETYPE, count: string) {
  if (status === 'UNIFIED') {
    if (platforminfo.value.shareType === 'RATE') {
      return divTenThousand(count)
    } else {
      return divTenThousand(count)
    }
  } else if (status === 'RATE') {
    return divTenThousand(count)
  } else {
    return divTenThousand(count)
  }
}
// 排序
// const sortTableList = (label: string) => {
//     switch (label) {
//         case '分销佣金(预计)':
//             break
//         case '加入分销时间':
//             break
//         default:
//             break
//     }
// }
/**
 * 获取平台分销配置
 */
async function initPlatformDisConfig() {
  const { code, data } = await doGetDistributeConfig()
  if (code === 200) {
    platformLevel.value = data.level
    if (data.one || data.two || data.three) {
      data.one = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.one)) : String(divHundred(data.one))
      data.two = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.two)) : String(divHundred(data.two))
      data.three = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.three)) : String(divHundred(data.three))
      platforminfo.value = data
    }
  } else {
    ElMessage.error('获取分销配置失败')
  }
}
const TableexpectPrice = (salePrices: Decimal, row: ApiDistributeComItem) => {
  if (row.shareType !== 'UNIFIED') {
    if (row.shareType === 'RATE') {
      if (platformLevel.value === 'ONE') {
        return salePrices.mul(divTenThousand(divHundred(row.one))).toFixed(2)
      } else if (platformLevel.value === 'TWO') {
        return salePrices
          .mul(divTenThousand(divHundred(row.one)))
          .add(salePrices.mul(divHundred(divHundred(row.two))))
          .toFixed(2)
      } else {
        return salePrices
          .mul(divTenThousand(divHundred(row.one)))
          .add(salePrices.mul(divTenThousand(divHundred(row.two))))
          .add(salePrices.mul(divTenThousand(divHundred(row.three))))
          .toFixed(2)
      }
    } else if (platformLevel.value === 'ONE') {
      return divTenThousand(row.one).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return divTenThousand(row.one).add(divTenThousand(row.two)).toFixed(2)
    } else {
      return divTenThousand(row.one).add(divTenThousand(row.two)).add(divTenThousand(row.three)).toFixed(2)
    }
  } else if (platforminfo.value.shareType === 'RATE') {
    if (platformLevel.value === 'ONE') {
      return salePrices.mul(divHundred(platforminfo.value.one)).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .toFixed(2)
    } else {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .add(salePrices.mul(divHundred(platforminfo.value.three)))
        .toFixed(2)
    }
  } else if (platformLevel.value === 'ONE') {
    return new Decimal(platforminfo.value.one).toFixed(2)
  } else if (platformLevel.value === 'TWO') {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).toFixed(2)
  } else {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).add(platforminfo.value.three).toFixed(2)
  }
}
</script>

<template>
  <schema-form v-model="distributeInfo" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset"> </schema-form>
  <el-tabs v-model="distributeInfo.distributionStatus" class="demo-tabs tab_container" @tab-change="handleChangeTab">
    <el-tab-pane label="全部" name="ALL" />
    <el-tab-pane label="分销中" name="IN_DISTRIBUTION" />
    <el-tab-pane label="取消分销" name="CANCEL_DISTRIBUTION" />
    <!-- <el-tab-pane label="违规下架" name="FORBIDDEN" /> -->
  </el-tabs>
  <div v-if="distributeInfo.distributionStatus !== 'CANCEL_DISTRIBUTION'" class="handle_container">
    <el-button round :disabled="selectedList.length === 0" @click="handleBatchCancelDistrube">取消分销</el-button>
  </div>

  <div class="table_container">
    <el-table
      :data="distributeInfo.list"
      :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
      :cell-style="{ height: '100px' }"
      @selection-change="handleTableSelect"
    >
      <template #empty>
        <ElTableEmpty />
      </template>
      <el-table-column v-if="distributeInfo.distributionStatus !== 'CANCEL_DISTRIBUTION'" type="selection" width="50" fixed="left" />
      <el-table-column width="140" label="店铺信息" align="left">
        <template #default="{ row }">
          <el-tooltip v-if="row.shopName.length >= 14" class="box-item" effect="dark" :content="row.shopName" placement="top">
            <div class="ellipsis f12">
              {{ row.shopName }}
            </div>
          </el-tooltip>
          <div v-else class="ellipsis f12">
            {{ row.shopName }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" width="290" align="left">
        <template #default="{ row }">
          <div class="com">
            <el-image class="com__img" :src="row.pic" />
            <div class="com__right">
              <el-tooltip v-if="row.name.length >= 30" class="box-item" effect="dark" :content="row.name" placement="top">
                <div class="com__right--name">
                  <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                  {{ row.name }}
                </div>
              </el-tooltip>
              <div v-else class="com__right--name">
                <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ row.name }}
              </div>
              <div v-if="row.originSalePrices.length > 1">
                ￥{{ divTenThousand(row.originSalePrices[0]) }} ~ ￥{{ divTenThousand(row.originSalePrices[row.originSalePrices.length - 1]) }}
              </div>
              <div v-else>￥{{ divTenThousand(row.originSalePrices[0]) }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="100" align="left">
        <template #default="{ row }">
          <div class="f12">{{ row.stock }}</div>
        </template>
      </el-table-column>
      <el-table-column label="商品状态" width="100" align="left">
        <template #default="{ row }: { row: ApiDistributeComItem }">
          <div
            class="f12"
            :style="{
              color: ['SELL_ON'].includes(row.status) ? '#333' : ['SELL_OFF'].includes(row.status) ? '#999' : '#F54319',
            }"
          >
            {{ conversionStatus(row.status) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分销状态" width="110" align="left">
        <template #default="{ row }">
          <div class="f12" :style="{ color: ['IN_DISTRIBUTION'].includes(row.distributionStatus) ? '#333' : '#999' }">
            {{ row.distributionStatus === 'IN_DISTRIBUTION' ? '分销中' : '取消分销' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分佣参数" width="110" align="left">
        <template #default="{ row }">
          <div class="f12 color51">
            一级:<span
              :class="[
                row.shareType === 'UNIFIED'
                  ? platforminfo.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage'
                  : row.shareType === 'FIXED_AMOUNT'
                  ? 'amount'
                  : 'percentage',
              ]"
              >{{ conversionCount(row.shareType, row.one) }}</span
            >
          </div>
          <div v-if="platformLevel !== 'ONE' && row.two" class="f12 color51">
            二级:<span
              :class="[
                row.shareType === 'UNIFIED'
                  ? platforminfo.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage'
                  : row.shareType === 'FIXED_AMOUNT'
                  ? 'amount'
                  : 'percentage',
              ]"
              >{{ conversionCount(row.shareType, row.two) }}</span
            >
          </div>
          <div v-if="platformLevel === 'THREE' && row.three" class="f12 color51">
            三级:<span
              :class="[
                row.shareType === 'UNIFIED'
                  ? platforminfo.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage'
                  : row.shareType === 'FIXED_AMOUNT'
                  ? 'amount'
                  : 'percentage',
              ]"
              >{{ conversionCount(row.shareType, row.three) }}</span
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column label="" width="200" align="left">
        <template #header>
          <label
            >分销佣金(预计)
            <!-- <i class="iconfont icon-path" style="font-size: 12px; margin-left: 5px" @click="sortTableList('分销佣金(预计)')"></i> -->
          </label>
        </template>
        <template #default="scope">
          <div style="display: flex">
            <div
              v-if="scope.row.salePrices[0] !== scope.row.salePrices[scope.row.salePrices.length - 1] && scope.row.salePrices.length > 1"
              placement="bottom"
              effect="light"
              class="colorRed"
            >
              <span>￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[0]), scope.row) }}</span>
              ~
              <span>￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[scope.row.salePrices.length - 1]), scope.row) }}</span>
            </div>
            <div v-else class="f12 colorRed">￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[0]), scope.row) }}</div>
            <el-tooltip
              :raw-content="true"
              :content="
                useCalculationFormula(
                  scope.row.shareType,
                  scope.row.salePrices[0],
                  scope.row.salePrices[scope.row.salePrices.length - 1],
                  scope.row.one,
                  scope.row?.two,
                  scope.row?.three,
                )
              "
              placement="bottom"
              effect="light"
            >
              <i class="iconfont icon-wenhao" style="color: #4d4d4d52; margin-left: 5px"></i>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="" :width="distributeInfo.distributionStatus !== 'CANCEL_DISTRIBUTION' ? 160 : ''" align="left">
        <template #header>
          <label
            >加入分销时间
            <!-- <i class="iconfont icon-path" style="font-size: 12px; margin-left: 5px" @click="sortTableList('加入分销时间')"></i> -->
          </label>
        </template>
        <template #default="{ row }">
          <div class="f12">{{ row.createTime }}</div>
        </template>
      </el-table-column>
      <el-table-column v-if="distributeInfo.distributionStatus !== 'CANCEL_DISTRIBUTION'" fixed="right" label="操作" width="110" align="right">
        <template #default="{ row }">
          <el-button v-if="row.distributionStatus === 'IN_DISTRIBUTION'" type="danger" link @click="handleCancelDistribution(row)"
            >取消分销</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
  <page-manage
    style="border: 0"
    :page-num="distributeInfo.current"
    :page-size="distributeInfo.size"
    :total="distributeInfo.total"
    @handle-current-change="handleChangeCurrent"
    @handle-size-change="handleChangeSize"
  />
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}

.input-with-select .el-input-group__prepend {
  background-color: var(--el-fill-color-blank);
}
.ellipsis {
  @include utils-ellipsis;
}
@include b(dis) {
  @include e(header) {
    margin-bottom: 16px;
    padding-left: 16px;
  }
}
@include b(com) {
  width: 270px;
  font-size: 12px;
  color: #515151;
  display: flex;
  @include e(img) {
    display: inline-block;
    width: 68px;
    height: 68px;
    margin-right: 12px;
  }
  @include e(right) {
    height: 68px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    text-align: left;
    @include m(name) {
      width: 174px;
      @include utils-ellipsis(2);
      line-height: 1.2em;
    }
  }
}
@include b(colorRed) {
  color: #f54319;
}
@include b(dialogCom) {
  @include flex();
  @include e(img) {
    width: 60px;
    height: 60px;
    margin-right: 12px;
    border-radius: 10px;
  }
  @include e(right) {
    @include m(name) {
      width: 210px;
      font-size: 14px;
      color: #333;
      line-height: 20px;
    }
  }
}
.amount {
  &::before {
    content: '￥';
    display: inline-block;
  }
}
.percentage {
  &::after {
    content: '%';
    display: inline-block;
  }
}
</style>
