import { Ref, ref, reactive, computed } from 'vue'
import Decimal from 'decimal.js'
import { ElMessage, type TabPaneName } from 'element-plus'
import useClipboard from 'vue-clipboard3'
import { regionData } from '@/store/modules/GDRegionData'
import { AddressFn } from '@/components/q-address'
import { getPurchaseOrderList } from './apis'
import DateUtil from '@/utils/date'
import useConvert from '@/composables/useConvert'
import { useRouter } from 'vue-router'

const quickSearchTabNames = ['近一个月订单', '近三个月订单', '全部订单']
const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const dateTool = new DateUtil()

export interface ApiPurchaseOrder {
  createTime: string
  no: string
  payAmount: string
  shopId: string
  shopUserId: string
  status: keyof typeof PurchaseOrderStatus
  supplierId: string
  type: 'PURCHASE'
  extra: {
    receiver: {
      address: string
      area: string[]
      mobile: string
      name: string
    }
    pay: {
      orderNo: string
      payType: 'OFFLINE' | 'OFFLINE'
      proof?: string
    }
    remark?: string
  }
  extraInfo: {
    supplierName: string
    supplierPhone: string
  }
  orderItems: OrderItems[]
  timeNodes: {
    payTime: string
    deliveryTime: string
    receiveTime: string
  }

  [key: string]: any
}

export interface OrderItems {
  id: string
  dealPrice: string
  freightPrice: string
  image: string
  num: number
  orderNo: string
  packageStatus: 'WAITING_FOR_DELIVER' | 'WAITING_FOR_RECEIVE' | 'COMPLETED'
  productName: string
  salePrice: string
  supplierId: string
}

export enum PurchaseOrderStatus {
  UNPAID, // 未支付
  PAYMENT_AUDIT, // 支付审核中
  AUDIT_FAIL_CLOSED, // 审核未通过 已关闭
  PAID, // 已支付
  SYSTEM_CLOSED, // 超时未支付 系统关闭
  BUYER_CLOSED, // 买家关闭订单
  SELLER_CLOSED, // 卖家关闭订单
}

enum QUERYORDERSTATUS {
  UNPAID,
  PAYMENT_AUDIT,
  WAITING_FOR_DELIVER,
  WAITING_FOR_PUTIN,
  FINISHED,
  CLOSED,
}

export const queryOrderStatus: Record<keyof typeof QUERYORDERSTATUS, string> = {
  UNPAID: '待支付',
  PAYMENT_AUDIT: '待审核',
  WAITING_FOR_DELIVER: '待发货',
  WAITING_FOR_PUTIN: '待入库',
  FINISHED: '已完成',
  CLOSED: '已关闭',
}

/**
 * 展开快速搜索的监听
 * @param { boolean } isShowQuickSearch 是否展开快速搜索
 * @param tableHeight 表格高度
 */
export const changeShowQuickSearch = (isShowQuickSearch: boolean, tableHeight: Ref<string>) => {
  if (isShowQuickSearch) {
    tableHeight.value = 'calc(100vh - 450px)'
  } else {
    tableHeight.value = 'calc(100vh - 350px)'
  }
}

const stepMap = {
  待支付: 1,
  待审核: 2,
  待发货: 2,
  部分发货: 2,
  待入库: 3,
  已完成: 3,
  已关闭: 1,
}

const payTypeMap = {
  OFFLINE: '线下支付',
  BALANCE: '余额支付',
}
export const useOrderBasicDetails = (orderDetail: ApiPurchaseOrder, initialOrder?: any) => {
  const orderDetails = ref<ApiPurchaseOrder>()
  const payOrderRef = ref<any | null>(null)
  orderDetails.value = orderDetail
  const stepInfo = reactive({
    statusText: '',
    activeStep: 0,
  })
  const orderStatusText = (stepInfo.statusText = filterMainOrderStatus(orderDetail))
  stepInfo.activeStep = stepMap[orderStatusText as keyof typeof stepMap]
  const copyOrderNo = (orderNo: string) => {
    toClipboard(orderNo)
      .then(() => {
        ElMessage.success('复制成功')
      })
      .catch(() => ElMessage.error('复制失败'))
  }
  const handleCopyReceiver = () => {
    const str = `
            收货人姓名：${orderDetails?.value?.extra?.receiver?.name}\n
            联系人电话：${orderDetails?.value?.extra?.receiver?.mobile}\n
            收货地址：${orderDetails?.value?.extra?.receiver.area?.join('')} ${orderDetails?.value?.extra?.receiver?.address}\n
            采购备注：${orderDetails?.value?.extra?.remark}
        `
    toClipboard(str)
      .then(() => ElMessage.success('复制成功'))
      .catch(() => ElMessage.error('复制失败'))
  }
  const computedCalculateFreight = computed(() => (orderItems?: ApiPurchaseOrder['orderItems']) => calculateFreight(orderItems))
  const computedCalculateCommodityPrice = computed(() => (orderItems?: ApiPurchaseOrder['orderItems']) => calculateCommodityPrice(orderItems))
  return {
    orderDetails,
    stepInfo,
    payTypeMap,
    divTenThousand,
    computedCalculateFreight,
    computedCalculateCommodityPrice,
    payOrderRef,
    copyOrderNo,
    handleCopyReceiver,
  }
}

/**
 * 计算订单运费
 * @param orderItems 订单项
 */
const calculateFreight = (orderItems: ApiPurchaseOrder['orderItems'] = []) => {
  return orderItems.reduce((pre, item) => {
    return pre.plus(new Decimal(divTenThousand(item.freightPrice)))
  }, new Decimal(0))
}

const calculateCommodityPrice = (orderItems: ApiPurchaseOrder['orderItems'] = []) => {
  return orderItems.reduce((pre, item) => {
    return pre.plus(new Decimal(divTenThousand(item.salePrice).mul(new Decimal(item.num))))
  }, new Decimal(0))
}

interface BtnType {
  action: string
  text: string
  type: 'default' | 'success' | 'warning' | 'info' | 'primary' | 'danger'
}

export const usePurchaseOrderList = () => {
  const payTypeMap = {
    OFFLINE: '线下支付',
    BALANCE: '余额支付',
  }
  const $router = useRouter()
  const showPayDialog = ref(false)
  const currentRow = ref<ApiPurchaseOrder>()
  const orderListCondition = reactive({
    status: '',
    startTime: '',
    endTime: '',
    purchaser: '',
    no: '',
    supplierId: '',
    shopId: '',
    purchasePhone: '',
  })
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  const activeTabName = ref(' ')
  const quickSearchTabName = ref('全部订单')
  const orderDataList = ref<ApiPurchaseOrder[]>([])
  const handleTabChange = (name: TabPaneName) => {
    orderListCondition.status = name as string
    initOrderList()
  }

  const copyOrderNo = (orderNo: string) => {
    toClipboard(orderNo)
      .then(() => {
        ElMessage.success('复制成功')
      })
      .catch(() => ElMessage.error('复制失败'))
  }
  const computedBtnList = computed(() => (row: ApiPurchaseOrder) => filterBtnList(row))
  const getMainOrderStatusText = computed(() => (row: ApiPurchaseOrder) => filterMainOrderStatus(row))
  const computedCalculateFreight = computed(() => (orderItems: ApiPurchaseOrder['orderItems']) => calculateFreight(orderItems))
  const initOrderList = () => {
    let data: ApiPurchaseOrder[] = [],
      total = 0
    getPurchaseOrderList({ ...pagination.page, ...orderListCondition, needSupplier: true })
      .then((result) => {
        data = result.data?.records
        total = result.data?.total || 0
      })
      .finally(() => {
        orderDataList.value = data
        pagination.total = Number(total)
      })
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
    orderListCondition.supplierId = searchCondition.supplierId
    orderListCondition.shopId = searchCondition.shopId
    orderListCondition.purchasePhone = searchCondition.purchasePhone
    pagination.page.current = 1
    initOrderList()
  }
  const loadHandleTabClick = (startTime: string) => {
    const endTime = dateTool.getYMDs(new Date())
    orderListCondition.startTime = startTime
    orderListCondition.endTime = endTime
    initOrderList()
  }
  const handleDispatchEvent = (action: string, orderInfo: ApiPurchaseOrder) => {
    switch (action) {
      case 'details':
        $router.push({ path: '/order/purchase/details', query: { orderNo: orderInfo.no } })
        break
      default:
        break
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
  return {
    handleTabChange,
    pagination,
    initOrderList,
    orderDataList,
    handleQuickSearchCommand,
    handleSearch,
    quickSearchTabName,
    quickSearchTabNames,
    activeTabName,
    getMainOrderStatusText,
    computedBtnList,
    handleDispatchEvent,
    computedCalculateFreight,
    showPayDialog,
    currentRow,
    payTypeMap,
    copyOrderNo,
    multiSelect,
    handleChangeRow,
  }
}

export const filterMainOrderStatus = (orderInfo?: ApiPurchaseOrder) => {
  if (!orderInfo) return ''
  if (orderInfo.status === 'UNPAID') return '待支付'
  if (orderInfo.status === 'PAYMENT_AUDIT') return '待审核'
  const paidOrderStatusStack = ['WAITING_FOR_DELIVER', 'WAITING_FOR_RECEIVE', 'COMPLETED']
  const paidOrderStatusMap = {
    WAITING_FOR_DELIVER: '待发货',
    WAITING_FOR_RECEIVE: '待入库',
    COMPLETED: '已完成',
  }
  if (orderInfo.status === 'PAID') {
    let statusText: keyof typeof paidOrderStatusMap = 'COMPLETED'
    for (const item of orderInfo.orderItems) {
      const currentIndex = paidOrderStatusStack.findIndex((stat) => stat === statusText)
      if (item.packageStatus === 'WAITING_FOR_DELIVER' && currentIndex > 0) {
        statusText = 'WAITING_FOR_DELIVER'
        continue
      }
      if (item.packageStatus === 'WAITING_FOR_RECEIVE' && currentIndex > 1) {
        statusText = 'WAITING_FOR_RECEIVE'
      }
    }
    let returnStatus = paidOrderStatusMap[statusText]
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
  btnList.push({ action: 'details', text: '查看详情', type: 'primary' })
  return btnList
}

export const useShowProof = () => {
  const showProof = ref(false)
  const currentProof = ref('')
  const goToShowProof = (currentRow: { extra: { pay: { proof: string } } }) => {
    currentProof.value = currentRow?.extra?.pay?.proof || ''
    showProof.value = true
  }
  return {
    showProof,
    goToShowProof,
    currentProof,
  }
}

/**
 * 供应商列表类型
 */
export interface ApiSupplierType {
  address: string
  area: string
  city: string
  country: string
  id: string
  mobile: string
  name: string
  productInfo: string
  province: string
  status: string
}
/**
 * 商品标签类型
 */
export interface ProductLabel {
  backgroundColor: string
  createTime: string
  deleted: boolean
  fontColor: string
  id: string
  name: string
  shopId: number
  shopType: string[]
  updateTime: string
  version: number
}
