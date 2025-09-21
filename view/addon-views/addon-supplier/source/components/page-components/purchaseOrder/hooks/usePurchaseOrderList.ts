import { ref, reactive, computed } from 'vue'
import {
  cancelPurchaseOrder,
  getPurchaseOrderList,
  payPurchaseOrder,
  postAddContact,
  putFinishPurchaseOrderInStorage,
  putPurchaseOrderInStorage,
  doGetInvoiceTryRequest,
  doGetinvoiceDetail,
  doPostInvoiceRequest,
  doGetinvoiceSettings,
  doGetDefault,
  doPutwithdraw,
  doPostReSend,
} from '../../../../apis'
import { ElMessage, ElMessageBox, type TabPaneName } from 'element-plus'
import type { ApiPurchaseOrder } from '../../../../index'
import payOrder from '../components/pay-order.vue'
import DateUtil from '@/utils/date'
import useClipboard from 'vue-clipboard3'
import Decimal from 'decimal.js'
import useOpenInStorageDialog from './useOpenInStorageDialog'
import useOpenStorageDetails from './useOpenStorageDetails'
import { InStorageInterface } from '../components/types/instorage'
import useConvert from '@/composables/useConvert'
import { useRouter } from 'vue-router'
import { INVOICE_STATUS, InvoiceRequest } from '../type/index'
import { useShopInfoStore } from '@/store/modules/shopInfo'
interface BtnType {
  action: string
  text: string
  type: 'default' | 'success' | 'warning' | 'info' | 'primary' | 'danger'
}
type InvoiceStatusAndStart = keyof typeof INVOICE_STATUS
type InvoiceStatusAndStartCn = '申请开票' | '开票成功' | '开票中' | '开票失败'
type InvoiceStatusHanderBtnConfig = {
  text: '重新发送发票到邮箱' | '重新申请开票' | '撤销开票申请' | ''
  type: 'primary' | 'danger' | ''
}
const quickSearchTabNames = ['近一个月订单', '近三个月订单', '全部订单']
const InvoiceStatusHander: Record<
  InvoiceStatusAndStart,
  { title: InvoiceStatusAndStartCn; btnConfig: InvoiceStatusHanderBtnConfig; describe: string }
> = {
  START: {
    title: '申请开票',
    btnConfig: {
      text: '',
      type: '',
    },
    describe: '',
  },
  SUCCESSFULLY_INVOICED: {
    title: '开票成功',
    btnConfig: {
      text: '重新发送发票到邮箱',
      type: 'primary',
    },
    describe: '开票成功后，发票将发送至您的邮箱地址，请注意查看邮件~',
  },
  REQUEST_IN_PROCESS: {
    title: '开票中',
    btnConfig: {
      text: '撤销开票申请',
      type: 'danger',
    },
    describe: '开票成功后，发票将发送至您的邮箱地址，请注意查看邮件~',
  },
  FAILED_INVOICE_REQUEST: {
    title: '开票失败',
    btnConfig: {
      text: '重新申请开票',
      type: 'primary',
    },
    describe: '开票失败后，您可以重新申请开票~',
  },
}
const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const dateTool = new DateUtil()
const usePurchaseOrderList = () => {
  const { showInStorageDialog, inStorageOrderNo, openInStorageDialog, inStorageRefs } = useOpenInStorageDialog()
  const { storageDetailsOrderNo, showStorageDetailsDialog, openStorageDetailsDialog } = useOpenStorageDetails()
  const payTypeMap = {
    OFFLINE: '线下支付',
    BALANCE: '余额支付',
  }
  const $router = useRouter()
  const showPayDialog = ref(false)
  const showInvoiceDialog = ref(false)
  const invoiceDetail = ref<InvoiceRequest>({
    applicantId: '',
    applicantShopId: '',
    invoiceOwnerType: 'SHOP',
    shopSupplierName: '',
    applicationTime: '',
    orderNo: '',
    invoiceAmount: '',
    invoiceType: 'VAT_GENERAL',
    invoiceStatus: 'START',
    billingRemarks: '',
    denialReason: '',
    invoiceHeaderId: '',
  })
  const invoiceSetType = ref('VAT_COMBINED')
  const currentRow = ref<ApiPurchaseOrder>()
  const payOrderRef = ref<InstanceType<typeof payOrder> | null>(null)
  const orderListCondition = reactive({ status: '', startTime: '', endTime: '', purchaser: '', no: '', supplierId: '', purchasePhone: '' })
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  const activeTabName = ref(' ')
  const quickSearchTabName = ref('全部订单')
  const multipleTableRef = ref()
  const orderDataList = ref<ApiPurchaseOrder[]>([])
  const chooseList = ref<ApiPurchaseOrder[]>([])
  const handleTabChange = (name: TabPaneName) => {
    orderListCondition.status = name as string
    initOrderList()
  }

  const computedBtnList = computed(() => (row: ApiPurchaseOrder) => filterBtnList(row))
  const getMainOrderStatusText = computed(() => (row: ApiPurchaseOrder) => filterMainOrderStatus(row))
  const computedCalculateFreight = computed(() => (orderItems: ApiPurchaseOrder['orderItems']) => calculateFreight(orderItems))
  const initOrderList = async () => {
    let data = [],
      total = 0
    try {
      const result = await getPurchaseOrderList({ ...pagination.page, ...orderListCondition })
      data = result.data?.records
      total = result.data?.total || 0
    } finally {
      orderDataList.value = data
      pagination.total = Number(total)
    }
  }
  const handleQuickSearchCommand = (command: string) => {
    activeTabName.value = ' '
    quickSearchTabName.value = command
    if (quickSearchTabName.value === '近一个月订单') {
      const startTime = dateTool.getLastMonth(new Date())
      loadHandleTabClick(startTime)
    } else if (quickSearchTabName.value === '近三个月订单') {
      const startTime = dateTool.getLastThreeMonth(new Date())
      loadHandleTabClick(startTime)
    } else {
      // 请求全部订单清空时间
      orderListCondition.startTime = ''
      orderListCondition.endTime = ''
      initOrderList()
    }
  }
  const handleSearch = (searchCondition: any) => {
    orderListCondition.startTime = searchCondition.startTime
    orderListCondition.endTime = searchCondition.endTime
    orderListCondition.purchaser = searchCondition.purchaser
    orderListCondition.no = searchCondition.no
    orderListCondition.purchasePhone = searchCondition.purchasePhone
    orderListCondition.supplierId = searchCondition.supplierId
    pagination.page.current = 1
    initOrderList()
  }
  const loadHandleTabClick = async (startTime: string) => {
    const endTime = dateTool.getYMDs(new Date())
    orderListCondition.startTime = startTime
    orderListCondition.endTime = endTime
    initOrderList()
  }
  const handleInvoiceConfig = () => {
    // eslint-disable-next-line default-case
    switch (invoiceDetail.value.invoiceStatus) {
      case 'START':
        handleInvoiceConfigStart()
        break
      case 'REQUEST_IN_PROCESS':
        handleInvoiceConfigINPROGRESS()
        break
      case 'SUCCESSFULLY_INVOICED':
        handleInvoiceConfigSUCCESS()
        break
      case 'FAILED_INVOICE_REQUEST':
        handleInvoiceConfigFAILURE()
        break
    }
  }
  const handleCloseInvoiceDialog = () => {
    invoiceDetail.value = {
      applicantId: '',
      applicantShopId: '',
      invoiceOwnerType: 'SHOP',
      shopSupplierName: '',
      applicationTime: '',
      orderNo: '',
      invoiceAmount: '',
      invoiceType: 'VAT_GENERAL',
      invoiceStatus: 'START',
      billingRemarks: '',
      denialReason: '',
      invoiceHeaderId: '',
    }
  }
  /**
   * 申请开票
   */
  const handleInvoiceConfigStart = async () => {
    if (!invoiceDetail.value.invoiceHeaderId) return ElMessage.error('请选择抬头')
    if (!invoiceDetail.value.invoiceType) return ElMessage.error('请选择发票类型')
    // START 为前端增加的枚举 用于方便切换不同的场景
    // const invoiceStatus = invoiceDetail.value.invoiceStatus === 'START' ? undefined : invoiceDetail.value.invoiceStatus
    const { code, data, msg } = await doPostInvoiceRequest(invoiceDetail.value)
    if (code !== 200) return ElMessage.error(msg || '申请开票失败')
    ElMessage.success('申请开票成功')
    showInvoiceDialog.value = false
  }
  /**
   * 撤销开票
   */
  const handleInvoiceConfigINPROGRESS = async () => {
    const { data, code, msg } = await doPutwithdraw(invoiceDetail.value.id as string)
    if (code !== 200) return ElMessage.error(msg || '撤销开票失败')
    ElMessage.success('撤销开票成功')
    showInvoiceDialog.value = false
  }
  /**
   * 重发发票
   */
  const handleInvoiceConfigSUCCESS = async () => {
    const { data, code, msg } = await doPostReSend({ invoiceRequestId: invoiceDetail.value.id, shopId: invoiceDetail.value.applicantShopId })
    if (code !== 200) return ElMessage.error(msg || '重发发票失败')
    ElMessage.success('重发发票成功')
    showInvoiceDialog.value = false
  }
  /**
   * 重新开票
   */
  const handleInvoiceConfigFAILURE = async () => {
    invoiceDetail.value = {
      ...invoiceDetail.value,
      applicantId: '',
      applicationTime: '',
      invoiceType: 'VAT_GENERAL',
      invoiceStatus: 'START',
      billingRemarks: '',
      denialReason: '',
      invoiceHeaderId: '',
    }
    doGetDefault()
      .then((res) => {
        invoiceDetail.value.invoiceHeaderId = res.data.id
      })
      .catch(() => {
        ElMessage.error('获取默认抬头失败')
      })
    doGetinvoiceSettings({ invoiceSettingsClientType: 'SUPPLIER', shopId: invoiceDetail.value.applicantShopId })
      .then((res) => {
        invoiceSetType.value = res.data.invoiceSetupValue[0].invoiceType
      })
      .catch(() => {
        ElMessage.error('获取发票设置失败')
      })
  }
  const handleDispatchEvent = async (action: string, orderInfo: ApiPurchaseOrder) => {
    switch (action) {
      case 'cancel':
        ElMessageBox.confirm('请确认是否将订单取消？？').then(async () => {
          const { code, msg } = await cancelPurchaseOrder(orderInfo.no)
          if (code === 200) {
            ElMessage.success({ message: msg || '取消成功' })
            initOrderList()
          } else {
            ElMessage.error({ message: msg || '取消失败' })
          }
        })
        break
      case 'details':
        $router.push({ path: '/goods/purchase/detail', query: { orderNo: orderInfo.no } })
        break
      case 'pay':
        currentRow.value = orderInfo
        showPayDialog.value = true
        break
      case 'finish':
        ElMessageBox.confirm('完成后将无法入库，请确认订单是否已完成？？').then(async () => {
          const { code, msg } = await putFinishPurchaseOrderInStorage(orderInfo.no)
          if (code === 200) {
            ElMessage.success({ message: msg || '订单入库已完成' })
            initOrderList()
          } else {
            ElMessage.error({ message: msg || '订单完成失败' })
          }
        })
        break
      case 'inStorage':
        openInStorageDialog(orderInfo.no)
        break
      case 'storageDetails':
        openStorageDetailsDialog(orderInfo.no)
        break
      case 'contact':
        postAddContact(orderInfo.shopId, orderInfo.supplierId).then(({ code }) => {
          if (code === 200) {
            $router.push({ path: '/message/customer/supplierService', query: { id: orderInfo.supplierId } })
          }
        })
        break
      case 'invoice':
        invoice(orderInfo)
        break
      default:
        break
    }
  }
  const invoice = async (orderInfo: ApiPurchaseOrder) => {
    const { data, code, msg } = await doGetInvoiceTryRequest({
      invoiceOwnerType: 'SHOP',
      applicantId: useShopInfoStore().shopInfo.id,
      applicantShopId: orderInfo.supplierId,
      orderNo: orderInfo.no,
    })

    if (code !== 200 || !data) return ElMessage.error({ message: msg || '获取开票信息失败' })
    if (data.invoiceStatus === 'REQUEST_HAS_EXPIRED') return ElMessage.error({ message: msg || '已超出可开票时间' })
    if (data.invoiceStatus === 'SERVER_NOT_SUPPORTED') return ElMessage.error({ message: msg || '供应商不支持开票' })
    showInvoiceDialog.value = true
    if (data.invoiceStatus !== 'ALLOWED_INVOICING') {
      doGetinvoiceDetail(data.id)
        .then((res) => {
          if (res.code === 200 && res.data) {
            invoiceDetail.value = {
              ...invoiceDetail.value,
              ...res.data,
            }
          } else {
            ElMessage.error('获取发票详情失败')
          }
        })
        .catch(() => {
          ElMessage.error('获取发票详情失败')
        })
    } else {
      invoiceDetail.value.shopSupplierName = orderInfo.extraInfo.supplierName
      invoiceDetail.value.applicantShopId = orderInfo.supplierId
      invoiceDetail.value.orderNo = orderInfo.no
      invoiceDetail.value.invoiceAmount = data.billMoney
      doGetDefault()
        .then((res) => {
          invoiceDetail.value.invoiceHeaderId = res.data.id
        })
        .catch(() => {
          ElMessage.error('获取默认抬头失败')
        })
      doGetinvoiceSettings({ invoiceSettingsClientType: 'SUPPLIER', shopId: orderInfo.supplierId })
        .then((res) => {
          invoiceSetType.value = res.data.invoiceSetupValue[0].invoiceType
        })
        .catch(() => {
          ElMessage.error('获取发票设置失败')
        })
    }
  }
  const copyOrderNo = async (orderNo: string) => {
    try {
      await toClipboard(orderNo)
      ElMessage.success('复制成功')
    } catch (e) {
      ElMessage.error('复制失败')
    }
  }
  const handlePayOrder = async () => {
    const data: any = await payOrderRef.value?.getPayOrderFormModel()
    const { code, msg } = await payPurchaseOrder({ ...data, orderNo: currentRow.value?.no })
    if (code === 200) {
      ElMessage.success('支付成功')
      initOrderList()
      showPayDialog.value = false
    } else {
      ElMessage.error(msg || '支付失败')
    }
  }
  const handleConfirmInStorage = async () => {
    const inStorageComp = inStorageRefs.value
    if (inStorageComp) {
      if (inStorageRefs.value?.tableData.filter((value: any) => value.skus.find((sku: any) => +sku.inStorageNum > 0)).length > 0) {
        const data = {
          skuStorages: buildInStorageData(inStorageComp.tableData).filter((value: any) => value.value > 0),
          remark: inStorageComp.remark,
          orderNo: inStorageOrderNo.value,
        }
        const result = await putPurchaseOrderInStorage(data)
        if (result.code === 200) {
          ElMessage.success({ message: result.msg || '入库成功' })
          showInStorageDialog.value = false
          initOrderList()
        } else {
          ElMessage.error({ message: result.msg || '入库失败' })
        }
      } else {
        ElMessage.error('本次实际入库数需大于等于1')
      }
    }
  }
  const multiSelect = ref<ApiPurchaseOrder[]>([])
  const handleChangeRow = (val: any, orderNo: string) => {
    const currentChooseRow = orderDataList.value.find((orderData) => orderData.no === orderNo)
    if (currentChooseRow) {
      if (val === false) {
        const multiSelectIndex = multiSelect.value.findIndex((orderData) => orderData.no === orderNo)
        if (multiSelectIndex > -1) {
          multiSelect.value.splice(multiSelectIndex, 1)
        }
      } else {
        multiSelect.value.push(currentChooseRow)
      }
    }
  }

  // 处理选择变化
  function handleSelectionChange(selection: ApiPurchaseOrder[]) {
    chooseList.value = selection
  }
  const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initOrderList()
  }
  const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initOrderList()
  }
  return {
    handleTabChange,
    pagination,
    multipleTableRef,
    initOrderList,
    orderDataList,
    chooseList,
    handleQuickSearchCommand,
    handleSearch,
    quickSearchTabName,
    quickSearchTabNames,
    activeTabName,
    getMainOrderStatusText,
    computedBtnList,
    handleDispatchEvent,
    copyOrderNo,
    computedCalculateFreight,
    showPayDialog,
    currentRow,
    handlePayOrder,
    payOrderRef,
    payTypeMap,
    showInStorageDialog,
    inStorageOrderNo,
    inStorageRefs,
    storageDetailsOrderNo,
    showStorageDetailsDialog,
    handleConfirmInStorage,
    showInvoiceDialog,
    InvoiceStatusHander,
    invoiceDetail,
    handleInvoiceConfig,
    handleCloseInvoiceDialog,
    invoiceSetType,
    handleChangeRow,
    multiSelect,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange,
  }
}

export default usePurchaseOrderList

const buildInStorageData = (tableData: InStorageInterface['products']) => {
  const skuStorages: any[] = []
  tableData.forEach((productItem) => {
    const productId = productItem.productId
    const skus = productItem.skus
    skus.forEach((sku) => {
      const skuId = sku.skuId
      const value = sku.inStorageNum || 0
      skuStorages.push({
        key: { productId, skuId },
        value,
      })
    })
  })
  return skuStorages
}
const filterMainOrderStatus = (orderInfo: ApiPurchaseOrder) => {
  if (orderInfo.status === 'UNPAID') return '待支付'
  if (orderInfo.status === 'PAYMENT_AUDIT') return '待审核'
  const paidOrderStatusStack = ['WAITING_FOR_DELIVER', 'WAITING_FOR_RECEIVE', 'COMPLETED']
  const paidOrderStatusMap = {
    WAITING_FOR_DELIVER: '待发货',
    WAITING_FOR_RECEIVE: '待入库',
    COMPLETED: '已完成',
  }
  if (orderInfo.status === 'PAID') {
    let statusText = 'COMPLETED'
    for (const item of orderInfo.orderItems) {
      const currentIndex = paidOrderStatusStack.findIndex((stat) => stat === statusText)
      if (item.packageStatus === 'WAITING_FOR_DELIVER' && currentIndex > 0) {
        statusText = 'WAITING_FOR_DELIVER'
        continue
      }
      if (item.packageStatus === 'WAITING_FOR_RECEIVE' && currentIndex > 1) {
        statusText = 'WAITING_FOR_RECEIVE'
        continue
      }
    }
    let returnStatus = paidOrderStatusMap[statusText as keyof typeof paidOrderStatusMap]
    if (returnStatus === '待发货') {
      const deliveryedItem = orderInfo.orderItems.find((item) => item.packageStatus !== 'WAITING_FOR_DELIVER')
      if (deliveryedItem) {
        returnStatus = '部分发货'
      }
    }
    return returnStatus
  }
  return '已关闭'
}

/**
 * 根据订单状态筛选按钮
 * @param orderInfo 订单信息
 */
const filterBtnList = (orderInfo: ApiPurchaseOrder) => {
  const btnList: BtnType[] = []
  const statusText = filterMainOrderStatus(orderInfo)
  btnList.push({ action: 'details', text: '查看', type: 'primary' })
  // btnList.push({ action: 'contact', text: '联系卖家', type: 'primary' })
  if (statusText === '待支付') {
    btnList.push({ action: 'pay', text: '去支付', type: 'danger' })
    btnList.push({ action: 'cancel', text: '取消订单', type: 'danger' })
  } else if (['待入库', '部分发货'].includes(statusText)) {
    btnList.push({ action: 'inStorage', text: '入库', type: 'primary' })
    btnList.push({ action: 'finish', text: '完成', type: 'primary' })
    btnList.push({ action: 'storageDetails', text: '入库详情', type: 'primary' })
  } else if (statusText === '已完成') {
    btnList.push({ action: 'storageDetails', text: '入库详情', type: 'primary' })
    btnList.push({ action: 'invoice', text: '申请开票', type: 'primary' })
  }
  if (orderInfo.stockInCount === 0) {
    btnList.forEach((item, index) => {
      if (item.text === '入库详情') {
        btnList.splice(index, 1)
      }
    })
  }

  return btnList
}
/**
 * 计算订单运费
 * @param orderItems 订单项
 */
const calculateFreight = (orderItems: ApiPurchaseOrder['orderItems']) => {
  return orderItems.reduce((pre, item) => {
    return pre.plus(new Decimal(divTenThousand(item.freightPrice)))
  }, new Decimal(0))
}
