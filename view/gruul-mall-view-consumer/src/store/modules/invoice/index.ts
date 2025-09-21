import { defineStore } from 'pinia'
import $storage from '@/utils/storage'
interface InvoiceType {
  orderNo: string
  shopId: Long
  shopName: string
  invoiceAmount: string
  isDetail: boolean
  id: string
}

export const useInvoiceStore = defineStore('InvoiceStore', {
  state: () => ({
    InvoiceInfo: {
      orderNo: '',
      shopId: '',
      shopName: '',
      invoiceAmount: '',
      isDetail: false,
      id: '',
    } as InvoiceType,
    invoiceHeaderId: '',
    invoiceType: '',
  }),
  actions: {
    SET_INVOICE_INFO(val: InvoiceType) {
      this.InvoiceInfo = val
      $storage.set('InvoiceInfo', val, 1)
    },
    SET_INVOICE_HEADER_ID(val: string) {
      this.invoiceHeaderId = val
      $storage.set('InvoiceInfo-HeaderId', val)
    },
    DEL_HEADER_ID() {
      this.invoiceHeaderId = ''
      $storage.remove('InvoiceInfo-HeaderId')
    },
    SET_INVOICE_TYPE(val: string) {
      this.invoiceType = val
      $storage.set('InvoiceInfo-invoiceType', val)
    },
    DEL_INVOICE_TYPE() {
      this.invoiceType = ''
      $storage.remove('InvoiceInfo-invoiceType')
    },
  },
  getters: {
    getInvoiceInfo: (state) => {
      if (!state.InvoiceInfo.orderNo) {
        return $storage.get(`InvoiceInfo`)
      } else {
        return state.InvoiceInfo
      }
    },
    getInvoiceHeaderId: (state) => {
      if (!state.invoiceHeaderId) {
        return $storage.get(`InvoiceInfo-HeaderId`)
      } else {
        return state.invoiceHeaderId
      }
    },
    getInvoiceType: (state) => {
      if (!state.invoiceType) {
        return $storage.get(`InvoiceInfo-invoiceType`)
      } else {
        return state.invoiceType
      }
    },
  },
})
