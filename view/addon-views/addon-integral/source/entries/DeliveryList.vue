<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import ExpressTable from '../components/deliveryList/express-table.vue'
import UnExpressTable from '../components/deliveryList/un-express-table.vue'
import Storage from '@/utils/Storage'
import { useRouter } from 'vue-router'
import receiverCompanyList from '@/assets/json/data.json'
import { doPutIntegralOrderDeliver, doGetIntegralOrderUndeliver } from '../apis'
import type { IntegralOrderItem } from '../components/types/order'
import type { FormRules } from 'element-plus'

const TabData = ref<IntegralOrderItem[]>([])
// 批量发货携带参数
const $router = useRouter()
const deliverDialogFormData = reactive({
  deliverType: 'EXPRESS' as 'EXPRESS' | 'WITHOUT',
  expressCompany: {
    logisticsCompanyCode: '',
    logisticsCompanyName: '',
    expressNo: '',
  },
})
// 发货类型枚举
const deliverType = [
  { key: 'EXPRESS', value: '手动发货' },
  { key: 'WITHOUT', value: '无需物流发货' },
]
const DatchDeliveryFormRules = reactive<FormRules>({
  deliverType: [{ required: true, trigger: 'blur', message: '请选择' }],
  addressaddress: [{ required: true, trigger: 'change', message: '请选择服务' }],
  printId: [{ required: true, trigger: 'change', message: '请选择' }],
  // logisticsCompanyCode: [
  //     {
  //         validator(rule, value, callback, source, options) {
  //             if (!deliverDialogFormData.expressCompany.logisticsCompanyCode) {
  //                 callback(new Error('请选择物流服务'))
  //                 return
  //             }
  //             callback()
  //         },
  //     },
  // ],
})
export type CompanySelectList = {
  value: string
  label: string
  companyType: string
}
//物流公司列表
const companySelectList: {
  value: CompanySelectList
  label: string
  companyType: string
}[] = receiverCompanyList.map((item) => ({
  label: item.companyName,
  value: { value: item.companyCode, label: item.companyName, companyType: item.companyType },
  companyType: item.companyType,
}))

initStoreDeliveryOrderList()

/**
 * 移除商品
 */
function removeOrderItem(integralOrderItem: IntegralOrderItem): void {
  const updatedTabData = TabData.value.filter((order) => order.no !== integralOrderItem.no)
  TabData.value = updatedTabData
  const integralOrderStorage = new Storage()
  integralOrderStorage.setItem('integralOrderSendGoods', updatedTabData, 24 * 60 * 60 * 1000)
}
/**
 * 内存中获取待发货商品
 */
function initStoreDeliveryOrderList(): void {
  TabData.value = new Storage().getItem('integralOrderSendGoods') ?? []
  TabData.value.forEach((item) => {
    item.expressCompanyName = ''
    item.expressName = ''
    item.expressNo = ''
  })
}
/**
 * 获取全部待发货的订单
 */
async function initOederList() {
  const { data, code } = await doGetIntegralOrderUndeliver()
  console.log('data', data)
  if (code === 200) {
    ElMessage.success('导入成功')
    data.forEach((item: IntegralOrderItem) => {
      item.integralOrderReceiverVO = item.integralOrderReceiver!
      item.expressCompanyName = ''
      item.expressName = ''
      item.expressNo = ''
    })
    TabData.value = data
    return
  }
  ElMessage.error('导入失败')
}
/**
 * 确定发货
 */
const handleSubmit = async () => {
  let data:
    | {
        integralOrderNo: string
        integralOrderDeliverType: 'WITHOUT' | 'EXPRESS' | string
        expressCompanyName?: string
        expressName?: string
        expressNo?: string
      }[]
    | boolean
  switch (deliverDialogFormData.deliverType) {
    case 'EXPRESS':
      data = manualDelivery()
      break
    default:
      data = unDelivery()
      break
  }
  if (!data) return
  batchDeliver(data)
}
/**
 * 手动发货
 */
function manualDelivery() {
  console.log('abData.value', TabData.value)
  const isVlidate = TabData.value.every((item) => item.expressCompanyName && item.expressName && item.expressNo)
  if (!isVlidate) {
    ElMessage.error({ message: '请选择物流服务或填写运单号' })
    return false
  }
  return TabData.value.map((item) => {
    const { no: integralOrderNo, expressCompanyName, expressName, expressNo } = item
    return {
      integralOrderNo,
      integralOrderDeliverType: deliverDialogFormData.deliverType,
      expressCompanyName,
      expressName,
      expressNo,
    }
  })
}
/**
 * 无需物流
 */
const unDelivery = () => {
  const data = TabData.value.map((order) => {
    const { no: integralOrderNo } = order
    return {
      integralOrderNo,
      integralOrderDeliverType: deliverDialogFormData.deliverType,
    }
  })
  return data
}
/**
 * 发货清除缓存跳路由
 */
async function batchDeliver(
  params: {
    integralOrderNo: string
    integralOrderDeliverType: 'WITHOUT' | 'EXPRESS' | string
    expressCompanyName?: string
    expressName?: string
    expressNo?: string
  }[],
) {
  const { code, data: res } = await doPutIntegralOrderDeliver(params)
  if (code !== 200) return ElMessage.error({ message: '发货失败' })
  new Storage().setItem('integralOrderSendGoods', [], 60 * 60 * 24)
  ElMessage.success({ message: '发货成功' })
  $router.replace({ name: 'integralMall', query: { type: 'order' } })
}

/**
 * 选择物流公司
 */
const handleExpressCompanyChange = (val: CompanySelectList) => {
  TabData.value = TabData.value.map((item) => {
    return {
      ...item,
      expressCompanyName: val.value,
      expressName: val.label,
    }
  })
}
const cancelBatchDeliver = () => {
  new Storage().setItem('integralOrderSendGoods', [], 60 * 60 * 24)
  $router.replace({ name: 'integralMall', query: { type: 'order' } })
}
const handleExpressNo = (val: string, row: IntegralOrderItem) => {
  const data = TabData.value.find((item) => item.no === row.no)
  if (data) {
    data.expressNo = val
  }
}
const handleExpressCompanyName = (val: CompanySelectList, row: IntegralOrderItem) => {
  const data = TabData.value.find((item) => item.no === row.no)
  if (data) {
    data.expressCompanyName = val.value
    data.expressName = val.label
  }
}
</script>

<template>
  <div class="q_plugin_container fdc1 overh">
    <el-form ref="DatchDeliveryFormRef" :model="deliverDialogFormData" :rules="DatchDeliveryFormRules" class="DatchDelivery_container fdc1 overh">
      <el-form-item label="发货方式" prop="deliverType" label-width="100px">
        <el-radio-group v-model="deliverDialogFormData.deliverType" class="ml-4" size="default">
          <el-radio v-for="item in deliverType" :key="item.key" :value="item.key" size="small">{{ item.value }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-show="deliverDialogFormData.deliverType !== 'WITHOUT'" label="物流公司" prop="logisticsCompanyCode" label-width="100px">
        <el-select-v2
          v-model="deliverDialogFormData.expressCompany"
          :options="companySelectList"
          value-key="value"
          placeholder="请选择物流公司"
          style="width: 444px"
          @change="handleExpressCompanyChange"
        >
        </el-select-v2>
      </el-form-item>
      <el-form-item>
        <el-row justify="end" style="width: 100%"><el-button type="primary" link @click="initOederList">导入全部待发货订单</el-button> </el-row>
      </el-form-item>
      <el-form-item class="fdc1 overh table_item">
        <ExpressTable
          v-show="deliverDialogFormData.deliverType === 'EXPRESS'"
          :table-data="TabData"
          :express-company="deliverDialogFormData.expressCompany"
          @filter-order-list="removeOrderItem"
          @express-no="handleExpressNo"
          @express-company-name="handleExpressCompanyName"
        />
        <UnExpressTable v-show="!['EXPRESS'].includes(deliverDialogFormData.deliverType)" :tab-data="TabData" @filter-order-list="removeOrderItem" />
      </el-form-item>
    </el-form>
    <div class="DatchDelivery_container__tool">
      <el-row justify="center" style="width: 100%">
        <el-button round type="primary" @click="handleSubmit">确认</el-button>
        <!-- <el-button round @click="cancelBatchDeliver">取消</el-button> -->
        <el-popconfirm title="确定退出批量发货?" @confirm="cancelBatchDeliver">
          <template #reference>
            <el-button round>取消</el-button>
          </template>
        </el-popconfirm>
      </el-row>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.table_item {
  :deep(.el-form-item__content) {
    flex-direction: column;
    display: flex;
    flex: 1;
    overflow: hidden;
  }
}
@include b(avatar_text_box) {
  @include flex(space-between, flex-start);
}
@include b(send) {
  font-size: 14px;
  color: #333333;
}
@include b(DatchDelivery_container) {
  padding: 10px 25px;
  @include e(tabText) {
    color: #000;
  }
  @include e(tool) {
    @include flex();
    position: sticky;
    bottom: 0;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    z-index: 100;
    margin-top: auto;
  }
}
</style>
