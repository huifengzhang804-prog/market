<script setup lang="ts">
import { ref, computed, watchEffect } from 'vue'
import useConvert from '@/composables/useConvert'
import { doGetInvoicePageInvoiceHeader } from '../../../../apis'
import { InvoiceRequest, invoiceTypeCn, PageInvoiceHeader, PageInvoiceHeaderTypeCn } from '../type'
import { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import QIcon from '@/components/q-icon/q-icon.vue'

const props = defineProps({
  invoiceDetail: {
    type: Object as PropType<InvoiceRequest>,
    default: () => ({}),
  },
  invoiceSetType: {
    type: String,
    default: '',
  },
})
const emit = defineEmits(['update:invoiceDetail'])
const _invoiceDetail = useVModel(props, 'invoiceDetail', emit)
const { divTenThousand } = useConvert()
const pageInvoiceHeaderList = ref<Array<PageInvoiceHeader>>([])
const pageInvoiceHeader = computed(() => {
  return props.invoiceDetail.invoiceStatus !== 'START'
    ? props.invoiceDetail
    : pageInvoiceHeaderList.value.find((item) => item.id === _invoiceDetail.value.invoiceHeaderId)
}) as any

watchEffect(() => {
  _invoiceDetail.value.invoiceStatus === 'START' && initInvoicePageInvoiceHeader()
})

async function initInvoicePageInvoiceHeader() {
  const { data, msg, code } = await doGetInvoicePageInvoiceHeader()
  if (code === 200) {
    pageInvoiceHeaderList.value = data.records
  }
}
</script>

<template>
  <div style="padding: 0 30px" class="invoice-dialog">
    <div class="invoice-dialog-main">
      <div class="invoice-dialog-main--name">
        供应商名称：<span>{{ invoiceDetail.shopSupplierName }}</span>
      </div>
      <div>
        开票金额
        <span class="invoice-dialog-main--price">{{ divTenThousand(invoiceDetail.invoiceAmount).toFixed(2) }}</span>
        <el-tooltip
          class="box-item"
          effect="dark"
          content="1、开票金额为消费者实付款金额，红包、优惠、购物券、消费返利等不在开票范围 2、如订单发生退货退款、退款，开票金额也将对应退款金额扣除。"
          placement="bottom"
        >
          <QIcon name="icon-wenhao1" color="#666" style="margin-left: 5px" />
        </el-tooltip>
      </div>
      <div class="invoice-dialog-main--number">
        订单号：
        <span>{{ invoiceDetail.orderNo }}</span>
      </div>
    </div>
    <div class="invoice-dialog-main__note">
      <span style="flex-shrink: 0">开票备注：</span>
      <el-input
        v-if="invoiceDetail.invoiceStatus !== 'START'"
        :model-value="invoiceDetail.billingRemarks"
        :rows="2"
        style="margin-left: 10px"
        disabled
        type="textarea"
        maxlength="100"
      />
      <el-input
        v-else
        v-model="_invoiceDetail.billingRemarks"
        :rows="2"
        style="margin-left: 10px"
        type="textarea"
        maxlength="100"
        placeholder="选填"
      />
    </div>
    <div class="invoice-dialog-main__type">
      <div>
        <span style="margin-right: 10px">发票类型：</span>
        <el-radio-group v-if="invoiceDetail.invoiceStatus === 'START'" v-model="_invoiceDetail.invoiceType">
          <el-radio v-if="invoiceSetType === 'VAT_GENERAL' || invoiceSetType === 'VAT_COMBINED'" value="VAT_GENERAL">增值税电子普通发票</el-radio>
          <el-radio v-if="invoiceSetType === 'VAT_SPECIAL' || invoiceSetType === 'VAT_COMBINED'" value="VAT_SPECIAL">增值税电子专用发票</el-radio>
        </el-radio-group>
        <span v-else>{{ invoiceTypeCn[invoiceDetail.invoiceType as keyof typeof invoiceTypeCn] }}</span>
      </div>
      <div v-if="invoiceDetail.invoiceStatus === 'START'" style="display: flex; align-items: center; margin-top: 10px">
        <span style="margin-right: 12px">抬头选择：</span>
        <div style="width: 200px">
          <el-select v-model="_invoiceDetail.invoiceHeaderId" placeholder="请选择抬头" size="small">
            <el-option v-for="item in pageInvoiceHeaderList" :key="item.id" :label="item.header" :value="item.id" />
          </el-select>
        </div>
      </div>
    </div>
    <div class="invoice-dialog-main__content">
      <div style="flex: 3">
        抬头类型：
        <span>{{
          pageInvoiceHeader?.invoiceHeaderType && PageInvoiceHeaderTypeCn[pageInvoiceHeader.invoiceHeaderType as keyof typeof PageInvoiceHeaderTypeCn]
        }}</span>
      </div>
      <div style="flex: 3">发票抬头： {{ pageInvoiceHeader?.header }}</div>
      <div style="flex: 3">税号：{{ pageInvoiceHeader?.taxIdentNo }}</div>
    </div>
    <div class="invoice-dialog-main__content">
      <div style="flex: 3">开户行：{{ pageInvoiceHeader?.openingBank }}</div>
      <div style="flex: 3">银行账号：{{ pageInvoiceHeader?.bankAccountNo }}</div>
      <div style="flex: 3">企业电话：{{ pageInvoiceHeader?.enterprisePhone }}</div>
    </div>
    <div class="invoice-dialog-main__content">
      <div style="flex: 3">邮箱地址：{{ pageInvoiceHeader?.email }}</div>
      <div style="flex: 6">企业地址：{{ pageInvoiceHeader?.enterpriseAddress }}</div>
    </div>
    <div v-if="invoiceDetail?.denialReason" class="invoice-dialog-main__content">
      <div>拒绝原因：{{ invoiceDetail?.denialReason }}</div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.invoice-dialog {
  color: #000;
}
@include b(invoice-dialog-main) {
  @include flex;
  justify-content: space-between;
  @include e(content) {
    margin: 10px 0;
    @include flex;
    justify-content: space-between;
  }
  @include e(note) {
    @include flex;
    align-items: flex-start;
    justify-content: flex-start;
    margin-top: 10px;
  }
  @include e(type) {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  @include m(price) {
    color: rgba(253, 5, 5, 1);
    font-size: 14px;
    font-weight: 700;
    &::before {
      content: '￥';
      font-size: 10px;
      font-weight: normal;
    }
  }
}
</style>
