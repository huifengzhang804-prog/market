import { doGetLogisticsExpressUsableList, doGetLogisticsList } from '@/apis'
import { ApiLogisticCompany } from '@/views/freight/components/types'
import { ElMessage, FormRules } from 'element-plus'
import { AddressFn } from '@/components/q-address'
import { ApiPurchaseOrder } from '../components/split-table/order'
import useDeliveryOrderList from '@/store/modules/order/purchase'
import { batchDeliveryPurchasePackages, getPurchaseOrderList } from '@/apis/order/purchase'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
const { divTenThousand } = useConvert()
const $deliveryOrderList = useDeliveryOrderList()
const deliverTypeMap = [
    { key: 'EXPRESS', value: '手动发货' },
    { key: 'PRINT_EXPRESS', value: '打印快递单并发货' },
    { key: 'WITHOUT', value: '无需物流发货' },
]

const useBatchDelivery = () => {
    const $router = useRouter()
    const deliverDialogFormData = reactive({
        deliverType: 'EXPRESS' as 'PRINT_EXPRESS' | 'EXPRESS' | 'WITHOUT',
        printId: '',
        receiver: { name: '', mobile: '', address: '' },
        expressCompany: {
            logisticsCompanyCode: '',
            logisticsCompanyName: '',
            expressNo: '',
        },
        addressaddress: '',
    })
    const companySelectList = ref<ApiLogisticCompany[]>([])
    // 发货地址数据
    const deliveryAddressData = ref<any[]>([])
    const orderDataList = ref<ApiPurchaseOrder[]>([])
    const formRules: FormRules = {
        deliverType: [{ required: true, message: '请选择发货方式', trigger: 'change' }],
    }
    initLogisticsCompany().then((resultData) => {
        deliverDialogFormData.expressCompany.logisticsCompanyCode = resultData?.[0]?.logisticsCompanyCode
        companySelectList.value = resultData
        nextTick(() => {
            orderDataList.value = orderDataList.value?.map((item) => {
                return {
                    ...item,
                    express: { logisticsCompanyCode: resultData?.[0]?.logisticsCompanyCode },
                }
            })
        })
    })
    const changeGlobalExpress = (val: string) => {
        orderDataList.value = orderDataList.value?.map((item) => {
            return {
                ...item,
                express: { logisticsCompanyCode: val },
            }
        })
    }
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
        deliverDialogFormData.addressaddress = sender.id
    }
    initLogisticsList()
    orderDataList.value = $deliveryOrderList.orderList?.map((item: any) => ({ ...item, express: { logisticsCompanyCode: '', expressNo: '' } }))
    const exportAllToDeliveryOrder = async () => {
        let data = []
        try {
            const result = await getPurchaseOrderList({ current: 1, size: 1000, status: 'WAITING_FOR_DELIVER' })
            data = result.data?.records
        } finally {
            orderDataList.value = data?.map((item: any) => ({
                ...item,
                express: { logisticsCompanyCode: deliverDialogFormData.expressCompany.logisticsCompanyCode, expressNo: '' },
            }))
        }
    }
    function buildBatchSubmitData() {
        const buildExpressCompany = (item: any) => {
            if (deliverDialogFormData.deliverType === 'WITHOUT') return null
            if (deliverDialogFormData.deliverType === 'PRINT_EXPRESS') {
                return {
                    expressCompanyCode: deliverDialogFormData.expressCompany.logisticsCompanyCode,
                    expressCompanyName: deliverDialogFormData.expressCompany.logisticsCompanyName,
                }
            }
            const nameOptions = companySelectList.value?.find((info) => info.logisticsCompanyCode === item?.express?.logisticsCompanyCode)
            return {
                expressCompanyCode: item?.express?.logisticsCompanyCode,
                expressCompanyName: nameOptions?.logisticsCompanyName || '',
                expressNo: item?.express?.expressNo,
            }
        }
        const getCurrentCheckedAddress = deliveryAddressData.value.find((item) => item.id === deliverDialogFormData.addressaddress)
        const result = orderDataList.value.map((item) => {
            return {
                sender: {
                    address:
                        deliverDialogFormData.deliverType === 'PRINT_EXPRESS'
                            ? `${AddressFn(regionData, [
                                  getCurrentCheckedAddress.provinceCode,
                                  getCurrentCheckedAddress.cityCode,
                                  getCurrentCheckedAddress.regionCode,
                              ])}${getCurrentCheckedAddress.address}`
                            : '',
                    mobile: deliverDialogFormData.deliverType === 'PRINT_EXPRESS' ? getCurrentCheckedAddress?.contactPhone : '',
                    name: deliverDialogFormData.deliverType === 'PRINT_EXPRESS' ? getCurrentCheckedAddress?.contactName : '',
                },
                remark: '',
                orderNo: orderDataList.value?.[0]?.no,
                dropDeliver: true,
                deliverType: deliverDialogFormData.deliverType,
                expressCompany: buildExpressCompany(item),
                items: item.orderItems.map((orderItem) => ({ itemId: orderItem.id, num: orderItem.num })),
                receiver: {
                    name: item?.extra?.receiver?.name,
                    mobile: item?.extra?.receiver?.mobile,
                    area: item?.extra?.receiver?.area,
                    address: item?.extra?.receiver?.address,
                },
            }
        })
        return result
    }
    const handleSubmit = async () => {
        const result = await batchDeliveryPurchasePackages(buildBatchSubmitData())
        if (result.code === 200) {
            ElMessage.success(result.msg || '发货成功')
            $router.replace({ name: 'orderPurchaseIndex' })
        } else {
            ElMessage.error(result.msg || '发货失败')
        }
    }
    const handleRemoveRow = (row: ApiPurchaseOrder) => {
        const index = orderDataList.value.findIndex((orderData) => orderData.no === row.no)
        if (index > -1) {
            orderDataList.value.splice(index, 1)
        }
    }
    const changeRowExpress = (val: any, no: string, key: string) => {
        const index = orderDataList.value.findIndex((orderData) => orderData.no === no)
        if (index > -1) {
            orderDataList.value[index].express[key] = val
        }
    }
    const cancelBatchDeliver = () => {
        $deliveryOrderList.SET_ORDER_LIST([])
        $router.replace({ name: 'orderPurchaseIndex' })
    }
    return {
        deliverTypeMap,
        formRules,
        companySelectList,
        deliverDialogFormData,
        deliveryAddressData,
        AddressFn,
        regionData,
        orderDataList,
        exportAllToDeliveryOrder,
        divTenThousand,
        changeGlobalExpress,
        handleSubmit,
        cancelBatchDeliver,
        handleRemoveRow,
        changeRowExpress,
    }
}

export default useBatchDelivery

async function initLogisticsCompany() {
    const { code, data } = await doGetLogisticsExpressUsableList({ size: 1000, current: 1 })
    if (code !== 200) {
        ElMessage.error('获取物流公司失败')
        return
    }
    return data.records || []
}
