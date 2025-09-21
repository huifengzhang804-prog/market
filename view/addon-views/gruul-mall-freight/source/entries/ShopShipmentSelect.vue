<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { doGetLogisticsList, doGetLogisticsExpressUsableList } from '../apis'
import { ElMessage } from 'element-plus'
import type { ApiLogisticCompany, ApiAddressItem } from '../index'

const prop = defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
const deliverDialogFormData = reactive(
  prop.properties.deliverDialogFormData || {
    deliverType: 'WITHOUT',
    receiver: { name: '', mobile: '', address: '' },
    expressCompany: '',
    addressaddress: '',
    expressNo: '',
  },
)
// 发货地址数据
const deliveryAddressData = ref<ApiAddressItem[]>([])
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])

initLogisticsList()
initLogisticsCompany()

/**
 * 获取可用物流服务
 */
async function initLogisticsCompany() {
  const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
  if (code !== 200) {
    ElMessage.error('获取物流公司失败')
    return
  }
  companySelectList.value = data.records
  prop.properties.loadCompanySelectListData(data.records)
}
/**
 * 获取发货物流地址列表
 */
async function initLogisticsList() {
  const { data, code } = await doGetLogisticsList({})
  code !== 200 ? ElMessage({ message: '请刷新重试...', type: 'warning' }) : (deliveryAddressData.value = data.records)
  console.log('deliveryAddressData.value', deliveryAddressData.value)
  const sender = deliveryAddressData.value.find((item) => item.defSend === 'YES')
  if (!sender) return
  deliverDialogFormData.addressaddress = sender.id
  prop.properties.loadDeliverDialogFormData(deliverDialogFormData)
  prop.properties.loadDeliveryAddressData(deliveryAddressData.value)
}

const handleChange = () => {
  prop.properties.loadDeliverDialogFormData(deliverDialogFormData)
}

defineExpose({
  initLogisticsList,
})
</script>

<template>
  <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="90px" prop="expressCompany">
    <template #label>
      <div class="send">物流服务</div>
    </template>
    <el-row style="width: 100%">
      <el-col :span="20">
        <el-select
          v-model="deliverDialogFormData.expressCompany"
          class="m-2"
          placeholder="请选择物流服务"
          style="width: 100%; height: 30px"
          @change="handleChange"
        >
          <el-option
            v-for="item in companySelectList"
            :key="item.logisticsCompanyName"
            :label="item.logisticsCompanyName"
            :value="item.logisticsCompanyCode"
          />
        </el-select>
      </el-col>
    </el-row>
  </el-form-item>
  <el-form-item v-if="deliverDialogFormData.deliverType === 'EXPRESS'" label-width="90px" prop="expressNo">
    <template #label>
      <div>物流单号</div>
    </template>
    <el-row style="width: 100%">
      <el-col :span="20">
        <el-input v-model="deliverDialogFormData.expressNo" placeholder="" style="width: 100%; height: 30px" maxlength="40" @change="handleChange" />
      </el-col>
    </el-row>
  </el-form-item>
  <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label-width="90px" prop="">
    <template #label>
      <div>发货地址</div>
    </template>
    <el-row style="width: 100%">
      <el-col :span="20">
        <el-select v-model="deliverDialogFormData.addressaddress" placeholder="选择发货地址" style="width: 100%; height: 30px" @change="handleChange">
          <el-option
            v-for="item in deliveryAddressData"
            :key="item.id"
            :value="item.id"
            :label="`${item.contactName} ${item.contactPhone} ${item.area?.join('')} ${item.address}`"
          />
        </el-select>
      </el-col>
    </el-row>
  </el-form-item>
</template>

<style scoped lang="scss"></style>
