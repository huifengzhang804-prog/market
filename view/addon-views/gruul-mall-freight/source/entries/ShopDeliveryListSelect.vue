<script lang="ts" setup>
import { ref, watch } from 'vue'
import type { ApiLogisticCompany } from '../index'
import { doGetLogisticsList, doGetLogisticsExpressUsableList } from '../apis'
import { ElMessage } from 'element-plus'

const prop = defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
const emit = defineEmits(['update:properties'])
const deliverDialogFormData = ref(
  JSON.parse(JSON.stringify(prop.properties.deliverDialogFormData)) || {
    deliverType: 'WITHOUT' as 'PRINT_EXPRESS' | 'EXPRESS' | 'WITHOUT',
    printId: '',
    receiver: { name: '', mobile: '', address: '' },
    // expressCompany: {
    //     expressCompanyCode: '',
    //     expressCompanyName: '',
    // },
    addressaddress: '',
  },
)
watch(
  () => prop.properties.deliverDialogFormData,
  (val) => {
    if (val) {
      deliverDialogFormData.value = JSON.parse(JSON.stringify(val))
    }
  },
  {
    immediate: true,
    deep: true,
  },
)

//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
// 发货地址数据
const deliveryAddressData = ref<any[]>([])

initLogisticsCompany()
initLogisticsList()

/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
  const { data, code } = await doGetLogisticsList({})
  if (code !== 200) {
    ElMessage({ message: '请刷新重试...', type: 'warning' })
  } else {
    deliveryAddressData.value = data.records
  }
  const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
  if (!sender) {
    // ElMessage.info('请添加设置物流')
    return
  }
  deliverDialogFormData.value.addressaddress = sender.id
  prop.properties.loadDeliverDialogFormData(deliverDialogFormData.value)
  prop.properties.loadDeliveryAddressData(deliveryAddressData.value)
}
/**
 * 获取物流服务列表  如果此列表无数据
 */
async function initLogisticsCompany() {
  const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
  if (code !== 200) {
    ElMessage.error('获取物流公司失败')
    return
  }
  if (data.records.length) {
    prop.properties.loadDeliverDialogFormData(deliverDialogFormData.value)
  }
  companySelectList.value = data.records
  prop.properties.loadCompanySelectListData(data.records)
}

type LogisticsCompany = {
  freightId: string
  id: string
  logisticsCompanyCode: string
  logisticsCompanyName: string
  printName: string
}
/**
 * 选择物流公司
 */
const handleExpressCompanyChange = (val: LogisticsCompany) => {
  prop.properties.loadexpressCompanyMapData(val)
}

const handleAddressAddressChange = () => {
  prop.properties.loadDeliverDialogFormData(deliverDialogFormData.value)
}
</script>

<template>
  <el-form-item
    v-if="!['VIRTUAL', 'INTRA_CITY_DISTRIBUTION'].includes(deliverDialogFormData.deliveryMethod)"
    label="物流服务"
    prop="expressCompany.expressCompanyCode"
    label-width="100px"
  >
    <el-select
      v-model="deliverDialogFormData.expressCompany"
      placeholder="请选择物流服务"
      style="width: 444px"
      clearable
      value-key="id"
      @change="handleExpressCompanyChange"
    >
      <el-option v-for="item in companySelectList" :key="item.id" :label="item.logisticsCompanyName" :value="item" />
    </el-select>
  </el-form-item>
  <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label="发货地址" prop="addressaddress" label-width="100px">
    <el-select v-model="deliverDialogFormData.addressaddress" placeholder="请选择发货地址" style="width: 444px" @change="handleAddressAddressChange">
      <el-option
        v-for="item in deliveryAddressData"
        :key="item.id"
        :value="item.id"
        :label="`${item.contactName} ${item.contactPhone} ${item.area?.join(' ')} ${item.address}`"
      />
    </el-select>
  </el-form-item>
</template>

<style scoped lang="scss"></style>
