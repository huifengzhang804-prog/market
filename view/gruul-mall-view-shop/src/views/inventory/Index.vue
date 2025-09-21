<script lang="ts" setup>
import Search, { SearchType } from './components/Search.vue'
import { ElMessage, TabPaneName } from 'element-plus'
import CommodityInfo from './components/CommodityInfo.vue'
import PageManage from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PurchaseDialog from './components/purchase-dialog.vue'
import priceSkuItem from './components/priceSkuItem.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import InventoryDialog from './components/inventory-dialog.vue'
import { doGetInventoryCommodity, doGetProductPurchasing, doGetCommoditySku, doPriceUpdate } from '@/apis/good'
import type { ApiCommodityType, CommoditySpecTable } from '@/views/goods/types'
import useClipboard from 'vue-clipboard3'

const { mulTenThousand, divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const tableList = reactive({
    page: { size: 10, current: 1 },
    goods: [],
    total: 0,
})
const searchParams = ref({
    productName: '',
    productId: '',
    shopCategoryId: '',
    productType: '',
    sellType: '',
    status: '',
    isDeleted: false,
})
const multiSelect = ref<ApiCommodityType[]>([])
const currentRow = ref<Partial<ApiCommodityType>>({})
//限购弹窗
const purchaseVisible = ref(false)
//库存弹窗
const inventoryVisible = ref(false)
//规格弹窗
const specsVisible = ref(false)
// 当前选中行sku信息
const currentSku = ref<CommoditySpecTable[]>()

const violationTypeMap = {
    PROHIBITED: '违禁品',
    COUNTERFEIT: '假冒伪劣',
    EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
    TITLE_IRREGULARITY: '标题有问题',
    OTHER: '其他',
}
// 违规信息
const explainData = reactive({ rummager: '', violationType: '', violationExplain: '', violationEvidence: [] as string[], examineDateTime: '' })
// 全部划线价
const allPrice = ref(0)
// 全部销售价
const allSalePrice = ref(0)
const priceSkuItemRef = ref<InstanceType<typeof priceSkuItem>>()

/**
 * 库存与限购返回
 */
async function initProductPurchasing(productId: Long, type: 'purchase' | 'inventory' | 'specs') {
    const { code, data } = await doGetProductPurchasing(productId)
    if (code !== 200) return ElMessage.error('商品信息获取失败')
    currentSku.value = data.map((item: CommoditySpecTable) => {
        item.num = 0
        return item
    })
    if (type === 'purchase') {
        purchaseVisible.value = true
    } else if (type === 'inventory') {
        inventoryVisible.value = true
    } else {
        specsVisible.value = true
    }
}
const initList = async () => {
    const { data } = await doGetInventoryCommodity({
        ...searchParams.value,
        ...tableList.page,
    })
    tableList.total = +data.total
    tableList.goods = data.records
}
initList()

const getSearch = (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e } as any
    initList()
}

/**
 * 操作下拉
 * @param {*} e
 */
const singleOptionHandle = (e: string, row: ApiCommodityType) => {
    currentRow.value = row
    if (currentRow.value?.id) {
        if (e === 'purchase') {
            initProductPurchasing(currentRow.value.id, 'purchase')
            return
        } else if (e === 'inventory') {
            initProductPurchasing(currentRow.value.id, 'inventory')
            return
        } else {
            initProductPurchasing(currentRow.value.id, 'specs')
        }
    }
}
const handleClose = () => {
    allPrice.value = 0
    allSalePrice.value = 0
}
const handleClosePurchase = (e: 'refresh') => {
    if (e) {
        // 设置限购成功后刷新列表
        initList()
    }
    purchaseVisible.value = false
}
const handleCloseInventory = (e: 'refresh') => {
    if (e) {
        // 设置库存成功后刷新列表
        initList()
    }
    inventoryVisible.value = false
}

/**
 * 列表库存计算
 */
const handleComputeStocks = (storageSkus: CommoditySpecTable[]) => {
    let show = ''
    if (
        storageSkus.every((item) => {
            return item.stockType === 'UNLIMITED'
        })
    ) {
        show = '无限库存'
    } else {
        show =
            storageSkus.reduce((pre, cur) => {
                return pre + Number(cur.stock)
            }, 0) + ''
    }
    return show
}

/**
 * 判断商品规格是否不足10个
 */
const handleStockLess = (storageSkus: CommoditySpecTable[]) => {
    const filterLimitStockArr = storageSkus.filter((item) => {
        return item.stockType === 'LIMITED'
    })
    const checkLimitStockArr = filterLimitStockArr.filter((item) => {
        return Number(item.stock) <= 10
    })
    if (!filterLimitStockArr.length || !checkLimitStockArr.length) return ''
    return filterLimitStockArr.length === checkLimitStockArr.length ? '库存不足' : '部分库存不足'
}

// 违规原因弹框
const sellOffVisible = ref(false)
// 违规商品点击更多
const handleCheck = (row: ApiCommodityType) => {
    const productViolation = row?.extra?.productViolation || {}
    Object.keys(explainData).forEach((key) => {
        const explainDataKey = key as keyof typeof explainData
        if (explainDataKey === 'violationEvidence') {
            explainData.violationEvidence = productViolation?.violationEvidence ? productViolation.violationEvidence.split(',') : []
        } else if (explainDataKey === 'violationType') {
            const list: string[] = []
            productViolation?.violationType?.forEach((item: keyof typeof violationTypeMap) => {
                list.push(violationTypeMap[item])
            })
            explainData.violationType = list.join(',')
        } else {
            explainData[key as keyof typeof explainData] = productViolation?.[key] || ''
        }
    })
    sellOffVisible.value = true
}

const goodsStatus = {
    全部: '',
    已上架: 'SELL_ON',
    已下架: 'SELL_OFF',
    违规下架: 'PLATFORM_SELL_OFF',
    已删除: 'delted',
}

const currentTab = ref<'' | 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF' | 'delted'>('')

//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    if (status === 'delted') {
        searchParams.value.isDeleted = true
    } else {
        searchParams.value.isDeleted = false
        searchParams.value.status = status as string
    }
    initList()
}
const handleUpdatename = (e: any, r: any) => {
    r.productName = e
}
//修改价格 s
const skus = ref<any[]>([])
const editPriceVisible = ref(false)
const getprice = (storageSkus: CommoditySpecTable[]) => {
    const tempArr = storageSkus.map((item) => {
        return +item.salePrice
    })
    const min = Math.min(...tempArr) / 10000
    const max = Math.max(...tempArr) / 10000
    if (max === min) {
        return max
    } else {
        return `${min.toFixed(2)}~￥${max.toFixed(2)}`
    }
}
const openEditPrice = async (row: ApiCommodityType) => {
    currentRow.value = row
    try {
        const data = await getCommoditySku(currentRow.value.id as string)
        skus.value = data.skus
        editPriceVisible.value = true
    } catch (error) {
        ElMessage.error('获取商品信息失败')
    }
}
// 获取当前商品sku
const getCommoditySku = async (productId: string): Promise<any> => {
    const { code, data } = await doGetCommoditySku(useShopInfoStore().shopInfo.id, productId)
    if (code !== 200) {
        ElMessage.error('获取商品信息失败')
        return
    }
    data.skus.forEach((item: any) => {
        item.initStock = 0
        item.price = divTenThousand(item.price)
        item.salePrice = divTenThousand(item.salePrice)
    })
    return data
}
const handleEditPrice = async () => {
    try {
        const skus = priceSkuItemRef.value?.itemSku
        if (!skus?.length) return
        for (const item of skus) {
            if (item.price < item.salePrice) {
                return ElMessage.error('划线价应大于等于销售价')
            }
        }
        const data = skus?.map((item) => ({
            skuId: item.id,
            price: mulTenThousand(item.price).toNumber(),
            salePrice: mulTenThousand(item.salePrice).toNumber(),
        }))
        const { code, msg } = await doPriceUpdate(data, skus[0]?.productId)
        if (code !== 200) return ElMessage.error(msg || '更新商品失败')
        ElMessage.success('更新成功')
        editPriceVisible.value = false
        allSalePrice.value = 0
        allPrice.value = 0
        initList()
    } catch (error) {
        ElMessage.error('更新失败')
    }
}
const copyOrderNo = async (data: string) => {
    try {
        await toClipboard(data)
        ElMessage.success('复制成功')
    } catch (e) {
        ElMessage.error('复制失败')
    }
}
// 分页方法
const handleSizeChange = (val: number) => {
    tableList.page.current = 1
    tableList.page.size = val
    initList()
}
const handleCurrentChange = (val: number) => {
    tableList.page.current = val
    initList()
}
</script>
<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search @on-search-params="getSearch" />
    </el-config-provider>

    <div class="tab_container">
        <el-tabs v-model="currentTab" style="margin-top: 15px" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>

    <QTable v-model:checkedItem="multiSelect" :data="tableList.goods" no-border class="table">
        <QTableColumn label="商品" width="350">
            <template #default="scope">
                <CommodityInfo :info="scope.row" :currentTab="currentTab" @update-name="(e:any) => handleUpdatename(e,scope.row)" />
            </template>
        </QTableColumn>
        <QTableColumn label="销售价" style="flex-direction: column" width="170">
            <!-- <QTableColumn label="价格" style="flex-direction: column" :sort="true" width="220"> -->
            <template #default="{ row }">
                <div class="price">
                    ￥{{ getprice(row.storageSkus) }}
                    <q-icon v-if="currentTab !== 'delted'" name="icon-bianji_o edit" color="#333" class="edit" @click="openEditPrice(row)"></q-icon>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="库存" width="130">
            <template #default="{ row }">
                <div @mouseenter="row.stockIcon = true" @mouseleave="row.stockIcon = false">
                    <div v-if="row.storageSkus.length" class="fcenter" style="position: relative; width: 100%">
                        {{ handleComputeStocks(row.storageSkus) }}
                        <div
                            v-if="handleComputeStocks(row.storageSkus) !== '无限库存'"
                            style="cursor: pointer"
                            @click="singleOptionHandle('inventory', row)"
                        >
                            <q-icon
                                v-if="currentTab !== 'delted'"
                                v-show="row.stockIcon"
                                key="inventory/center"
                                class="ml10"
                                name="icon-bianji_o"
                                color="#333"
                            ></q-icon>
                        </div>
                    </div>
                    <div class="warning">
                        {{ handleStockLess(row.storageSkus) }}
                    </div>
                </div>
            </template>
        </QTableColumn>

        <QTableColumn label="状态" width="80">
            <template #default="scope">
                <div v-if="scope.row.isDeleted" class="sell sell__off">已删除</div>
                <div v-else-if="scope.row.status === 'SELL_ON'" class="sell sell__on">已上架</div>
                <div v-else-if="scope.row.status === 'PLATFORM_SELL_OFF'" class="sell sell__platform">违规下架</div>
                <div v-else class="sell sell__off">已下架</div>
            </template>
        </QTableColumn>
        <QTableColumn label="操作" align="right" fixed="right" width="200">
            <template #default="scope">
                <el-link v-if="scope.row.status === 'PLATFORM_SELL_OFF'" type="primary" @click="handleCheck(scope.row)">违规原因</el-link>
                <el-link
                    v-if="scope.row.status !== 'PLATFORM_SELL_OFF' && currentTab !== 'delted'"
                    type="primary"
                    @click="singleOptionHandle('purchase', scope.row)"
                    >限购设置</el-link
                >
                <el-link
                    v-if="scope.row.storageSkus.length > 1 && scope.row.status !== 'PLATFORM_SELL_OFF'"
                    type="primary"
                    style="margin-left: 10px"
                    @click="singleOptionHandle('specs', scope.row)"
                    >规格明细</el-link
                >
            </template>
        </QTableColumn>
    </QTable>
    <PageManage
        v-if="tableList.total"
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <purchase-dialog :show="purchaseVisible" :sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleClosePurchase" />
    <inventory-dialog :show="inventoryVisible" :sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleCloseInventory" />
    <el-dialog v-model="sellOffVisible" title="违规原因" width="690px" destroy-on-close @close="sellOffVisible = false">
        <div style="line-height: 30px" class="explainData">
            <div>
                <p class="explainData_label">检查员：</p>
                {{ explainData.rummager }}
            </div>
            <div>
                <p class="explainData_label">检查时间：</p>
                {{ explainData.examineDateTime }}
            </div>
            <div>
                <p class="explainData_label">类型：</p>
                {{ explainData.violationType }}
            </div>
            <div>
                <p class="explainData_label">违规说明：</p>
                {{ explainData.violationExplain }}
            </div>
            <div style="display: flex">
                <p class="explainData_label">相关证据：</p>
                <div style="flex: 1">
                    <img v-for="(pic, picIndex) in explainData.violationEvidence" :key="picIndex" :src="pic" class="violation-evidence" />
                </div>
            </div>
        </div>
    </el-dialog>
    <el-dialog v-model="editPriceVisible" title="价格设置" center width="900px" destroy-on-close @close="handleClose">
        <template #default>
            <el-row :gutter="20" align="middle">
                <el-col :span="6" style="text-align: center"> 批量设置 </el-col>
                <el-col :span="8"
                    >划线价： <el-input-number v-model="allPrice" :controls="false" @blur="priceSkuItemRef?.setALLskyPrice('price', allPrice)"
                /></el-col>
                <el-col :span="7"
                    >销售价：
                    <el-input-number v-model="allSalePrice" :controls="false" @blur="priceSkuItemRef?.setALLskyPrice('salePrice', allSalePrice)"
                /></el-col>
            </el-row>
            <price-sku-item ref="priceSkuItemRef" :skus="skus" />
        </template>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="editPriceVisible = false">取消</el-button>
                <el-button type="primary" @click="handleEditPrice"> 确定 </el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="specsVisible" title="规格明细" center width="900px" destroy-on-close>
        <el-table :data="currentSku" height="60vh">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="商品规格" align="center" width="300">
                <template #default="{ row }">
                    <div style="display: flex; align-items: center">
                        <el-image v-if="row.image" style="width: 80px; height: 80px; margin-right: 13px; flex-shrink: 0" :src="row.image" />
                        <div>
                            <div style="margin-bottom: 11px; text-align: left">{{ row.specs[0] }}</div>
                            <span style="color: #0f40f5; cursor: pointer" @click="copyOrderNo(row.id)"> SKUID:{{ row.id }}</span>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="price" label="划线价" align="center">
                <template #default="{ row }"> ￥{{ divTenThousand(row.price) }} </template>
            </el-table-column>
            <el-table-column prop="salePrice" label="销售价" align="center">
                <template #default="{ row }"
                    ><span style="color: #f50707">￥{{ divTenThousand(row.salePrice) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="库存" align="center">
                <template #default="{ row }">
                    <div v-if="row.stockType !== 'UNLIMITED'">{{ row.stock }}</div>
                    <div v-else>无限库存</div>
                </template>
            </el-table-column>
            <el-table-column label="限购" align="center">
                <template #default="{ row }">
                    <div v-if="row.limitType === 'UNLIMITED'">不限购</div>
                    <div v-else-if="row.limitType === 'PRODUCT_LIMITED'">商品限购：{{ row.limitNum }}</div>
                    <div v-else>规格限购：{{ row.limitNum }}</div>
                </template>
            </el-table-column>
        </el-table>
    </el-dialog>
</template>
<style scoped lang="scss">
* {
    font-size: 14px;
}
@include b(tool) {
    height: 70px;
    @include flex(flex-start);
}
.ml10 {
    margin-left: 10px;
}

@include b(warning) {
    color: #e6a23c;
    margin-top: 5px;
    width: 100%;
}

@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}

@include b(sell) {
    width: 76px;
    height: 26px;
    border-radius: 4px;
    font-size: 14px;
    line-height: 26px;

    @include e(on) {
        color: #00bb2c;
    }

    @include e(off) {
        color: #666;
    }

    @include e(platform) {
        color: #f54319;
    }
}
.violation-evidence {
    width: 100px;
    height: 100px;
    vertical-align: top;
}
.violation-evidence + .violation-evidence {
    margin-left: 5px;
}
.explainData {
    > div {
        display: flex;
        margin-top: 8px;
        img {
            margin-top: 6px;
        }
    }
    .explainData_label {
        width: 80px;
        text-align: right;
        margin-right: 8px;
    }
}
.price {
    display: flex;
    position: relative;
    width: 100%;
    &:hover .edit {
        display: block;
    }
    .edit {
        display: none;
        margin-left: 6px;
        cursor: pointer;
    }
}
</style>
