<script setup lang="ts">
import { ref, reactive, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { regionData } from '@/store/modules/GDRegionData'
import { AddressFn } from '@/components/q-address'
import { doPutIntegralOrderDeliver, doGetIntegralOrderDeliverSingle, doGetLogisticsCompany } from '../../apis'
import type { IntegralOrderInfo } from '../types/order'
import type { FormInstance, FormRules } from 'element-plus'

const $props = defineProps({
  currentNo: {
    type: String,
    required: true,
  },
  isShow: { type: Boolean, required: true },
})
const companyList = ref<any[]>([])

const $emit = defineEmits(['update:isShow', 'upload-list'])
const _isShow = useVModel($props, 'isShow', $emit)
const orderInfo = ref<IntegralOrderInfo>({
  sellerRemark: '',
  buyerRemark: '',
  payTime: '',
  deliveryTime: '',
  accomplishTime: '',
  buyerId: '',
  buyerNickname: '',
  createTime: '',
  freightPrice: 0,
  image: '',
  salePrice: '',
  integralOrderReceiver: {
    address: '',
    area: [],
    createTime: '',
    id: '',
    mobile: '',
    name: '',
    orderNo: '',
    updateTime: '',
  },
  no: '',
  num: 1,
  price: '',
  productName: '',
  status: 'PAID',
  productType: 'REAL_PRODUCT',
})
const tableRef = ref()
// 校验实例
const ruleFormRef = ref<FormInstance>()
// 校验
const rules = reactive<FormRules>({
  address: [],
  company: [
    {
      required: true,
      message: '请选择物流公司',
      trigger: 'change',
    },
  ],
  expressNo: [
    { required: true, message: '请填写物流单号', trigger: 'blur' },
    { min: 8, max: 20, message: '请填写正确的物流单号', trigger: 'blur' },
  ],
  expressCode: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
})
// 发货类型枚举
const deliverType = [
  { key: 'EXPRESS', value: '手动发货' },
  { key: 'WITHOUT', value: '无需物流发货' },
]
const deliverDialogFormData = reactive({
  deliverType: 'EXPRESS',
  receiver: { name: '', mobile: '', address: '' },
  expressCompany: '',
  expressCode: '',
  addressaddress: '',
  expressNo: '',
  expressName: '',
})
initOrderInfo()
doGetCompanyList()
/**
 * 积分订单详情 by 订单号
 */
async function initOrderInfo() {
  if (!$props.currentNo) {
    ElMessage.error('获取订单号失败')
    _isShow.value = false
    return
  }
  const { code, data, msg } = await doGetIntegralOrderDeliverSingle($props.currentNo)
  if (code !== 200) {
    ElMessage.error(msg || '获取订单详情失败')
    _isShow.value = false
    return
  }
  orderInfo.value = data
  tableRef.value.toggleRowSelection(orderInfo.value, true)
}

async function doGetCompanyList() {
  const { data } = await doGetLogisticsCompany()
  companyList.value = data?.records
}
/**
 * 发货请求
 */
const handleeSubmit = async () => {
  try {
    const isVlidate = await ruleFormRef.value?.validate()
    if (isVlidate) {
      const params = formatParams(deliverDialogFormData.deliverType)
      console.log(params)
      const { code, msg, data } = await doPutIntegralOrderDeliver([params])
      if (code !== 200) {
        ElMessage.error(msg || '发货失败')
        return
      }
      ElMessage.success('发货成功')
      _isShow.value = false
      $emit('upload-list')
    }
  } catch (error) {
    error
  }
}

const changeExpressCode = (val: string) => {
  deliverDialogFormData.expressName = companyList.value?.filter((item) => item.logisticsCompanyCode === val)?.pop()?.logisticsCompanyName || ''
}
const formatParams = (type: string) => {
  const { deliverType, expressCode, expressNo, expressName } = deliverDialogFormData
  if (type === 'EXPRESS') {
    return {
      integralOrderNo: $props.currentNo,
      // integralOrderDeliverType: deliverType,
      integralOrderDeliverType: orderInfo.value?.productType === 'REAL_PRODUCT' ? 'EXPRESS' : 'WITHOUT',
      expressCompanyName: expressCode,
      expressNo: expressNo,
      expressName: expressName,
    }
  }
  return {
    integralOrderNo: $props.currentNo,
    // integralOrderDeliverType: deliverType,
    integralOrderDeliverType: orderInfo.value?.productType === 'REAL_PRODUCT' ? 'EXPRESS' : 'WITHOUT',
  }
}
const handleClose = () => {
  _isShow.value = false
}
</script>

<template>
  <el-form ref="ruleFormRef" :model="deliverDialogFormData" class="notShipment" :rules="rules">
    <div style="display: flex; justify-content: space-between; border-bottom: 1px dashed #ccc; height: 90px">
      <div style="display: flex; justify-content: space-around; flex-direction: column">
        <div style="font-weight: bold">{{ orderInfo?.integralOrderReceiver?.name }}</div>
        <div>{{ orderInfo?.integralOrderReceiver?.mobile }}</div>
        <div v-if="orderInfo.integralOrderReceiver.address">
          {{ orderInfo.integralOrderReceiver.area?.join('') }} {{ orderInfo.integralOrderReceiver.address }}
        </div>
        <div v-else>暂无地址信息</div>
      </div>
      <div style="display: flex; justify-content: space-around; flex-direction: column">
        <div v-if="orderInfo?.productType === 'REAL_PRODUCT'" style="width: 300px">
          <el-form-item v-if="deliverDialogFormData.deliverType !== 'WITHOUT'" label-width="120px" prop="expressCode">
            <template #label>
              <div class="send">物流公司</div>
            </template>
            <el-row style="width: 100%">
              <el-col :span="20">
                <el-select
                  v-model="deliverDialogFormData.expressCode"
                  placeholder="请选择物流公司"
                  class="m-2"
                  style="width: 100%; height: 30px"
                  @change="changeExpressCode"
                >
                  <el-option
                    v-for="item in companyList"
                    :key="item.logisticsCompanyName"
                    :label="item.logisticsCompanyName"
                    :value="item.logisticsCompanyCode"
                  />
                </el-select>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item v-if="deliverDialogFormData.deliverType === 'EXPRESS'" label-width="120px" prop="expressNo">
            <template #label>
              <div>物流单号</div>
            </template>
            <el-row style="width: 100%">
              <el-col :span="20">
                <el-input v-model="deliverDialogFormData.expressNo" placeholder="" style="width: 100%; height: 30px" maxlength="40" />
              </el-col>
            </el-row>
          </el-form-item>
        </div>
        <el-form-item v-else-if="orderInfo?.productType === 'VIRTUAL_PRODUCT'" label-width="90px">
          <div style="color: #f20404">您可通过网络形式(如:聊天工具将商品发给用户)发货</div>
        </el-form-item>
      </div>
    </div>
    <div style="margin: 5px 0; display: flex; justify-content: space-between; height: 30px">
      <el-form-item label-width="70px" left>
        <template #label>
          <div class="send">订单号：</div>
        </template>
        <div class="send">{{ $props.currentNo }}</div>
      </el-form-item>
      <el-form-item label-width="90px">
        <template #label>
          <div class="send">配送方式：</div>
        </template>
        <div v-if="orderInfo?.productType === 'VIRTUAL_PRODUCT'" class="send" style="font-weight: bold">无需物流</div>
        <div v-else class="send" style="font-weight: bold">快递配送</div>
      </el-form-item>
      <el-form-item label-width="90px">
        <template #label>
          <div class="send">下单时间：</div>
        </template>
        <div class="send">{{ orderInfo.createTime }}</div>
      </el-form-item>
    </div>
    <el-table
      ref="tableRef"
      empty-text="暂无数据~"
      :data="[orderInfo]"
      max-height="250"
      style="width: 100%; margin-bottom: 20px"
      :header-row-style="() => ({ fontSize: '14px', color: '#333333', fontWeight: 'bold' })"
    >
      <el-table-column label="商品" align="center">
        <template #default="{ row }: { row: IntegralOrderInfo }">
          <div style="display: flex">
            <el-avatar style="width: 68px; height: 68px; flex-shrink: 0" shape="square" size="large" :src="row.image" />
            <div class="notShipment__info" style="width: 200px; padding-left: 10px">
              <div class="notShipment__show">{{ row.productName }}</div>
              <!--修改前 <div>积分：{{ row.price && divTenThousand(row.price) }}</div> -->
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="200" align="center">
        <template #default="{ row }: { row: IntegralOrderInfo }">
          <div style="text-align: center">
            <!-- <div>
                            <span style="color: #ecb751"> {{ row.price }} <span style="font-size: 10px">分</span></span>
                            <span v-if="+row.salePrice === 0" style="color: #f20404">
                                + <span style="font-size: 10px">￥</span>{{ row.salePrice }}
                            </span>
                        </div> -->
            <div style="font-size: 12px">{{ row.num }} 件</div>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-form-item v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'" label-width="90px" prop="">
      <template #label>
        <div>发货地址</div>
      </template>
      <el-row style="width: 100%">
        <el-col :span="20">
          <el-select v-model="deliverDialogFormData.addressaddress" placeholder="选择发货地址" style="width: 100%; height: 30px">
            <el-option
              v-for="item in deliveryAddressData"
              :key="item.id"
              :value="item.id"
              :label="`${AddressFn(regionData, [item.provinceCode, item.cityCode, item.regionCode])}${item.address}`"
            />
          </el-select>
        </el-col>
      </el-row>
    </el-form-item>
  </el-form>
  <footer class="footer"><el-button @click="handleClose">取消</el-button> <el-button type="primary" @click="handleeSubmit">确认</el-button></footer>
</template>

<style lang="scss" scoped>
@include b(notShipment) {
  @include e(info) {
    @include flex;
    flex-direction: column;
    color: #838383;
    align-items: start;
  }
  @include e(commodityInfo) {
  }
  @include e(show) {
    font-size: 14px;
    color: #333333;
    @include utils-ellipsis();
  }
}

//去除table的每一条数据的下边框
.el-table td {
  border-bottom: none;
}
//去除表格的最下面一行的边框
.tableStyle::before {
  width: 0;
}
//设置表的外边框
.el-table {
  border-radius: 9px;
  border: 1px solid #d5d5d5;
}
.el-table th.is-leaf {
  border-bottom: none;
}

@include b(footer) {
  @include flex(flex-end);
}
</style>
