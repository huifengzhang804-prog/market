import type { Ref } from 'vue'
import type { ApiCommodityType } from '@/views/goods/types'
import { ElMessageBox } from 'element-plus'
type SelectComInfo = {
    current: number
    list: ApiCommodityType[]
    total: number
    size: number
    supplierGoodsName: string
    supplierProductId: string
    sellType: string
    loading: boolean
    productIds: string[]
}

const useBatchStorage = (commodityList: Ref<ApiCommodityType[]>, SelectComInfo: SelectComInfo) => {
    const tableData = commodityList
    const handleChangeStorageNum = (rowId: string) => {
        nextTick(() => {
            const currentRowIndex = tableData.value.findIndex((line) => line.id === rowId)
            const totalNum = tableData.value[currentRowIndex]?.storageSkus?.reduce((pre: number, currentRow: any) => {
                return pre + Number(currentRow.StorageNum || 0)
            }, 0)
            tableData.value[currentRowIndex].totalStorage = totalNum
        })
    }
    const maxRowBatchNum = computed(() => (row: any) => {
        let minLotStartingNum =
            row.storageSkus.find((item: any) => {
                return item.stockType !== 'UNLIMITED'
            })?.stock || 0
        row?.storageSkus?.forEach((item: any) => {
            if (item.stockType !== 'UNLIMITED' && item.stock < minLotStartingNum) {
                minLotStartingNum = item.stock
            }
        })
        return minLotStartingNum
    })
    const computedSuplier = computed(() => (storageSkus?: any[]) => {
        let show = ''
        if (
            storageSkus?.every((item) => {
                return item.stockType === 'UNLIMITED'
            })
        ) {
            show = '无限库存'
        } else {
            show =
                storageSkus?.reduce((pre, cur) => {
                    return pre + Number(cur.stock)
                }, 0) + ''
        }
        return show
    })
    const changeRowBatchNum = (rowId: string, batchNum?: number) => {
        if (typeof batchNum === 'number') {
            nextTick(() => {
                const currentRowIndex = tableData.value.findIndex((line) => line.id === rowId)
                if (currentRowIndex > -1) {
                    tableData.value[currentRowIndex]?.storageSkus?.forEach((item: any) => {
                        if (item.stockType !== 'UNLIMITED') {
                            item.StorageNum = batchNum
                        }
                    })

                    handleChangeStorageNum(rowId)
                }
            })
        }
    }
    const handleRemoveBatch = (index: number) => {
        ElMessageBox.confirm('确认移除当前商品信息').then(() => {
            commodityList.value.splice(index, 1)
            SelectComInfo.productIds = commodityList.value.map((item) => {
                return item.id
            })
        })
    }
    const handleRemoveSku = (skuIndex: number, index: number) => {
        ElMessageBox.confirm('确认移除当前规格信息').then(() => {
            commodityList.value?.[index]?.storageSkus?.splice(skuIndex, 1)
            commodityList.value = commodityList.value.filter((item) => item.storageSkus && item.storageSkus.length > 0)
            SelectComInfo.productIds = commodityList.value.map((item) => {
                return item.id
            })
            handleChangeStorageNum(commodityList.value[index].id)
        })
    }
    return {
        tableData,
        handleChangeStorageNum,
        maxRowBatchNum,
        computedSuplier,
        changeRowBatchNum,
        handleRemoveBatch,
        handleRemoveSku,
    }
}

export default useBatchStorage
