import {
    auditPurchasePayResult,
    getPurchaseOrderList,
    cancelPurchaseOrder,
    putRemarkPurchaseOrder,
    postAddContact,
} from '@/apis/order/purchase/index'
import { CheckboxValueType, ElMessage, ElMessageBox, type TabPaneName } from 'element-plus'
import type { ApiPurchaseOrder } from '../components/split-table/order'
import DateUtil from '@/utils/date'
import useClipboard from 'vue-clipboard3'
import Decimal from 'decimal.js'
import useAudit from './useAudit'
import useCombineDelivery from './delivery/useCombineDelivery'
import { cloneDeep } from 'lodash-es'
import useDeliveryOrderList from '@/store/modules/order/purchase'
import useRemark from './useRemark'
import { filterMainOrderStatus } from './useOrderBasicDetails'
interface BtnType {
    action: string
    text: string
    type: 'default' | 'success' | 'warning' | 'info' | 'primary' | 'danger'
}
const quickSearchTabNames = ['近一个月订单', '近三个月订单', '全部订单']
const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const dateTool = new DateUtil()
const usePurchaseOrderList = () => {
    const { setOrderInfo, auditOrderInfo, showAuditDialog } = useAudit()
    const { showDeliveryDialog, deliveryProps, setDeliveryProps, deliveryRef } = useCombineDelivery()
    // 导入备注模块
    const { remarkData, setRemarkData, showRemarkDialog } = useRemark()
    const payTypeMap = {
        OFFLINE: '线下支付',
        BALANCE: '余额支付',
    }

    enum INVOICE_STATUS {
        // 待开票
        INVOICE_NOT_START = '待开票',
        // 申请开票处理中
        REQUEST_IN_PROCESS = '开票中',
        //发票申请失败
        FAILED_INVOICE_REQUEST = '开票失败',
        //  已经成功开票
        SUCCESSFULLY_INVOICED = '开票成功',
        // 客户取消开票
        CLIENT_CANCEL_REQUEST = '撤销开票',
    }
    const deliveryConfirmLoading = ref(false)
    const multiSelect = ref<ApiPurchaseOrder[]>([])
    const $router = useRouter()
    const showPayDialog = ref(false)
    const currentRow = ref<ApiPurchaseOrder>()
    const orderListCondition = reactive({ status: '', startTime: '', endTime: '', purchaser: '', no: '', purchasePhone: '', shopId: '' })
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
            orderDataList.value = data?.map((item: any) => ({ ...item, checked: false }))
            pagination.total = Number(total)
        }
    }
    initOrderList()
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
        orderListCondition.startTime = searchCondition.startDate
        orderListCondition.endTime = searchCondition.endDate
        orderListCondition.purchaser = searchCondition.purchaser
        orderListCondition.no = searchCondition.no
        orderListCondition.purchasePhone = searchCondition.purchasePhone
        orderListCondition.shopId = searchCondition.shopId
        pagination.page.current = 1
        initOrderList()
    }
    const loadHandleTabClick = async (startTime: string) => {
        const endTime = dateTool.getYMDs(new Date())
        orderListCondition.startTime = startTime
        orderListCondition.endTime = endTime
        initOrderList()
    }
    const handleDispatchEvent = (action: string, orderInfo: ApiPurchaseOrder) => {
        switch (action) {
            case 'audit':
                setOrderInfo({
                    orderNo: orderInfo.no,
                    payof: orderInfo?.extra?.pay?.proof || '',
                    totalPrice: divTenThousand(orderInfo.payAmount).toString(),
                })
                showAuditDialog.value = true
                break
            case 'details':
                $router.push({ path: '/order/purchase/detail', query: { orderNo: orderInfo.no } })
                break
            case 'contact':
                postAddContact(orderInfo.supplierId, orderInfo.shopId).then(({ code }) => {
                    if (code === 200) {
                        $router.push({ path: '/mall/customer/service', query: { id: orderInfo.shopId } })
                    }
                })
                break
            case 'delivery':
                setDeliveryProps({
                    currentNo: orderInfo.no,
                    listOrderItems: orderInfo.orderItems,
                    receiver: orderInfo?.extra?.receiver,
                    createTime: orderInfo?.createTime,
                })
                break
            case 'cancel':
                ElMessageBox.confirm('请确认是否将订单取消？？').then(async () => {
                    const { code, msg } = await cancelPurchaseOrder(orderInfo.no)
                    if (code === 200) {
                        ElMessage.success({ message: msg || '取消成功' })
                    } else {
                        ElMessage.error({ message: msg || '取消失败' })
                    }
                })
                break
            case 'remark':
                openRemark(orderInfo.no, orderInfo.remark)
                break
            default:
                break
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
    const handleConfirmAudit = async (orderData: { orderNo: string; success: boolean }) => {
        const { code, msg } = await auditPurchasePayResult(orderData)
        if (code === 200) {
            ElMessage.success({ message: msg || '审核成功' })
            showAuditDialog.value = false
            initOrderList()
        } else {
            ElMessage.success({ message: msg || '审核失败' })
        }
    }
    const handleConfirmDelivery = async () => {
        deliveryConfirmLoading.value = true
        try {
            const result = await deliveryRef.value?.handleeSubmit()
            if (result) {
                showDeliveryDialog.value = false
                initOrderList()
            }
        } finally {
            deliveryConfirmLoading.value = false
        }
    }
    const handleChangeRow = (val: CheckboxValueType, orderNo: string) => {
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
    const handleDatchDeliverys = () => {
        if (!multiSelect.value.length) return ElMessage.error('请选择商品')
        const payOrder = cloneDeep(multiSelect.value.filter((item) => item.status === 'PAID'))
        let deliverOrder = payOrder.filter((item) => {
            let currentShopOrder = item.orderItems
            currentShopOrder = currentShopOrder.filter((ite: any) => ite.packageStatus === 'WAITING_FOR_DELIVER')
            item.orderItems = currentShopOrder
            return currentShopOrder.length
        })
        if (!deliverOrder.length) {
            return ElMessage.error('暂无需要发货的商品')
        }
        useDeliveryOrderList().SET_ORDER_LIST(deliverOrder)
        $router.push({ name: 'orderPurchaseBatch' })
    }
    /**
     * @description 打开备注弹窗
     * @param orderNo 订单号数组可选，不传即批量备注
     * @param remark 默认备注信息
     * @returns
     */
    const openRemark = (orderNo?: string, remark = '') => {
        let orderNos: string[] = []
        if (!orderNo) {
            if (!multiSelect.value.length) {
                return ElMessage.error({ message: '请选择需要备注的订单' })
            }
            orderNos = multiSelect.value.map((item) => item.no)
        } else {
            orderNos = [orderNo]
        }
        setRemarkData(orderNos, remark)
    }
    /**
     * @description 确认提交备注
     */
    const handleConfirmRemark = async () => {
        const { code, msg } = await putRemarkPurchaseOrder(remarkData)
        if (code === 200) {
            ElMessage.success({ message: msg || '备注成功' })
            initOrderList()
            showRemarkDialog.value = false
        } else {
            ElMessage.error({ message: msg || '备注失败' })
        }
    }

    // 分页器
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
        INVOICE_STATUS,
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
        copyOrderNo,
        computedCalculateFreight,
        showPayDialog,
        currentRow,
        payTypeMap,
        handleConfirmAudit,
        setOrderInfo,
        auditOrderInfo,
        showAuditDialog,
        showDeliveryDialog,
        deliveryProps,
        deliveryRef,
        handleConfirmDelivery,
        multiSelect,
        handleChangeRow,
        handleDatchDeliverys,
        remarkData,
        showRemarkDialog,
        openRemark,
        handleConfirmRemark,
        deliveryConfirmLoading,
        handleSizeChange,
        handleCurrentChange,
    }
}

export default usePurchaseOrderList

/**
 * @description 根据订单状态筛选按钮
 * @param orderInfo 订单信息
 * @returns { BtnType[] }
 */
const filterBtnList = (orderInfo: ApiPurchaseOrder) => {
    const btnList: BtnType[] = []
    const statusText = filterMainOrderStatus(orderInfo)
    btnList.push({ action: 'details', text: '查看详情', type: 'primary' })
    if (statusText === '待支付') {
        btnList.push({ action: 'cancel', text: '关闭订单', type: 'danger' })
    } else if (statusText === '待审核') {
        btnList.push({ action: 'audit', text: '审核', type: 'primary' })
    } else if (['待发货', '部分发货'].includes(statusText)) {
        btnList.push({ action: 'delivery', text: '发货', type: 'primary' })
    }
    btnList.push({ action: 'remark', text: '备注', type: 'primary' })
    return btnList
}
/**
 * @description 计算订单运费
 * @param orderItems 订单项
 * @returns
 */
const calculateFreight = (orderItems: ApiPurchaseOrder['orderItems']) => {
    return orderItems.reduce((pre, item) => {
        return pre.plus(new Decimal(divTenThousand(item.freightPrice)))
    }, new Decimal(0))
}
