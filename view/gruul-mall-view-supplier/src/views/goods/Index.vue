<script lang="ts" setup>
import { useRouter } from 'vue-router'
import Search, { SearchType } from './components/Search.vue'
import { ElMessage, ElMessageBox, TabPaneName } from 'element-plus'
import CommodityInfo from './components/CommodityInfo.vue'
import PageManage from '@/components/PageManage/index.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PurchaseDialog from './components/purchase-dialog.vue'
import InventoryDialog from './components/inventory-dialog.vue'
import { doGetCommodity, doUpdateSellStatus, doDeleteCommodity, doGetProductPurchasing, doGetCommoditySku } from '@/apis/good'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import type { ApiCommodityType, CommoditySpecTable } from './types'
import { SellTypeEnum } from './types'
import CommodityDetails from './details/index.vue'
import priceSkuItem from '@/views/goods/components/priceSkuItem.vue'
import { doPriceUpdate } from '@/apis/good'
import { useShopInfoStore } from '@/store/modules/shopInfo'

const sideList = reactive([
    { name: 'preview', label: '查看' },
    {
        name: 'delete',
        label: '删除',
    },
    {
        name: 'purchase',
        label: '限购设置',
    },
])
const SELLONsideList = reactive([
    { name: 'preview', label: '查看' },
    // {
    //     name: 'copy',
    //     label: '复制',
    // },
    {
        name: 'purchase',
        label: '限购设置',
    },
])
const platformsideList = reactive([
    {
        name: 'delete',
        label: '删除',
    },
    {
        name: 'sellOff',
        label: '违规原因',
    },
])
const tableList = reactive({
    page: { size: 10, current: 1 },
    goods: [],
    total: 0,
})
const searchParams = ref({
    supplierGoodsName: '',
    platformCategoryId: '',
    supplierProductStatus: '',
})
const multiSelect = ref<ApiCommodityType[]>([])
const currentRow = ref<Partial<ApiCommodityType>>({})
const $router = useRouter()
//限购弹窗
const purchaseVisible = ref(false)
//库存弹窗
const inventoryVisible = ref(false)
// 详情弹窗
const previewVisible = ref(false)
//编辑价格弹窗
const editPriceVisible = ref(false)
// 全部划线价
const allPrice = ref(0)
// 全部销售价
const allSalePrice = ref(0)
const priceSkuItemRef = ref<InstanceType<typeof priceSkuItem>>()
const skus = ref<any[]>([])
// 当前选中行sku信息
const currentSku = ref<CommoditySpecTable[]>()
// const tableHeight = ref('calc(100vh - 300px)')
const tableHeight = ref('calc(100vh - 300px)')

const violationTypeMap = {
    PROHIBITED: '违禁品',
    COUNTERFEIT: '假冒伪劣',
    EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
    TITLE_IRREGULARITY: '标题有问题',
    OTHER: '其他',
}
// 违规信息
const explainData = reactive({ rummager: '', violationType: '', violationExplain: '', violationEvidence: [] as string[], examineDateTime: '' })
const { mulTenThousand, divTenThousand } = useConvert()

/**
 * @description: 库存与限购返回
 * @param {*} productId
 * @returns {*}
 */
async function initProductPurchasing(productId: string, type: 'purchase' | 'inventory') {
    const { code, data } = await doGetProductPurchasing(productId)
    if (code !== 200) return ElMessage.error('商品信息获取失败')
    currentSku.value = data.map((item: CommoditySpecTable) => {
        item.num = 0
        return item
    })
    if (type === 'purchase') {
        purchaseVisible.value = true
    } else {
        inventoryVisible.value = true
    }
}
const initList = async () => {
    const { data } = await doGetCommodity({
        ...searchParams.value,
        ...tableList.page,
    })
    if (data) {
        tableList.total = data.total
        tableList.goods = data.records
    }
}
initList()

const getSearch = (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}
/**
 * @description: 商品上下架
 * @param {boolean} isMulti
 */
const changeCommodityStatus = async (isMulti: boolean, status: string) => {
    if (multiSelect.value.length === 0 && isMulti) {
        ElMessage.error('请选择商品')
        return
    }
    let ids = isMulti ? multiSelect.value.map((item) => item.id) : [currentRow.value.id as string]
    const { code, success } = await doUpdateSellStatus(ids, status)
    if (code === 200 && success) {
        ElMessage.success('更新成功')
        initList()
    }
}
const delCommodity = (isMulti: boolean) => {
    if (isMulti && multiSelect.value.length === 0) {
        ElMessage.error('请选择商品')
        return
    }
    ElMessageBox.confirm('确定需要删除商品？', '提示', {
        confirmButtonText: '是',
        cancelButtonText: '否',
        type: 'warning',
    }).then(async () => {
        let ids = isMulti ? multiSelect.value.map((item) => item.id) : [currentRow.value.id as string]
        const { code, success, msg } = await doDeleteCommodity(ids.join(','))
        if (code === 200 && success) {
            ElMessage.success('删除成功')
            initList()
        } else {
            ElMessage.error(msg)
        }
    })
}
const handleDel = (isMulti: boolean, row: ApiCommodityType) => {
    currentRow.value = row
    delCommodity(isMulti)
}

/**
 * @description: 操作下拉
 * @param {*} e
 * @returns {*}
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
        } else if (e === 'copy') {
            copyHandle(currentRow.value.id)
            return
        } else if (e === 'preview') {
            previewHandle(row)
            return
        } else if (e === 'delete') {
            delCommodity(false)
        } else {
            changeCommodityStatus(false, e)
        }
    }
}

const copy = (row: ApiCommodityType) => {
    singleOptionHandle('copy', row)
}

const editHandle = (id: string) => {
    $router.push({
        path: '/goods/list/edit',
        query: {
            id,
        },
    })
}
const copyHandle = (id: string) => {
    $router.push({
        path: '/goods/list/edit',
        query: {
            id,
            isCopy: 'true',
        },
    })
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
 * @description: 列表库存计算
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
 * @description: 判断商品规格是否不足10个
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
const handleSearchShow = (e: boolean) => {
    if (e) {
        tableHeight.value = 'calc(100vh - 440px)'
    } else {
        tableHeight.value = 'calc(100vh - 300px)'
    }
}
// 计算已售
const computedSalesVolume = (storageSkus: CommoditySpecTable[]) => {
    return storageSkus.reduce((pre, cur) => {
        return pre + Number(cur.salesVolume)
    }, 0)
}

// 违规原因弹框
const sellOffVisible = ref(false)
// 违规商品点击更多
const handleCheck = (status: string, row: ApiCommodityType) => {
    if (status === 'delete') {
        handleDel(false, row)
    } else if (status === 'copy') {
        copyHandle(row.id)
    } else {
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
}

const goodsStatus = {
    全部: '',
    已上架: 'SELL_ON',
    已下架: 'SELL_OFF',
    已售罄: 'SELL_OUT',
    库存预警: 'STOCK_ALERT',
    违规下架: 'PLATFORM_SELL_OFF',
}

const currentTab = ref<'' | 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF' | 'SELL_OUT'>('')

//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    searchParams.value.supplierProductStatus = status as string
    initList()
}
const handleUpdatePrice = (e: any[], v: any, r: any) => {
    r.storageSkus[0].salePrice = e[0].salePrice
}

// 获取当前商品sku
const getCommoditySku = async (info: any) => {
    const { code, data } = (await doGetCommoditySku(useShopInfoStore().shopInfo.id, info.id)) as any
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
const openEditPrice = async (row: any) => {
    try {
        const data = (await getCommoditySku(row)) as any
        getSku(data.skus)
        handleOpen(true)
    } catch (error) {
        ElMessage.error('获取商品信息失败')
    }
}
const handleUpdatename = (e: any, r: any) => {
    r.productName = e
}
const previewHandle = (row: any) => {
    currentRow.value = row
    previewVisible.value = true
}
const handleOpen = (flag: boolean) => {
    editPriceVisible.value = flag
}
const handleClose = () => {
    allPrice.value = 0
    allSalePrice.value = 0
}
const getSku = (data: any) => {
    skus.value = data
}
const handleEditPrice = async () => {
    const skus = priceSkuItemRef.value?.itemSku
    if (!skus?.length) return
    for (const item of skus) {
        if (item.price < item.salePrice) {
            return ElMessage.error('划线价应大于等于供货价')
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
    handleUpdatePrice(data, '', skus[0]?.productId)
}
const price = (info: any) => {
    const tempArr = info.storageSkus.map((item: any) => {
        return +item.salePrice
    })
    const min = Math.min(...tempArr) / 10000
    const max = Math.max(...tempArr) / 10000
    if (max === min) {
        return max.toFixed(2)
    } else {
        return `${min.toFixed(2)}-${max.toFixed(2)}`
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
    <div class="search_container">
        <Search @on-search-params="getSearch" @change-show="handleSearchShow" />
    </div>
    <div class="grey_bar"></div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" style="margin-top: 15px" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>

    <div class="handle_container">
        <router-link to="/goods/list/new">
            <el-button style="margin-right: 10px" type="primary" round>发布商品</el-button>
        </router-link>
        <el-button
            v-if="currentTab === 'SELL_OFF' || currentTab === ''"
            :disabled="multiSelect.length === 0"
            round
            @click="changeCommodityStatus(true, 'SELL_ON')"
            >批量上架</el-button
        >
        <el-button
            v-if="currentTab === 'SELL_ON' || currentTab === ''"
            :disabled="multiSelect.length === 0"
            round
            @click="changeCommodityStatus(true, 'SELL_OFF')"
            >批量下架</el-button
        >
    </div>
    <q-table v-model:checkedItem="multiSelect" :data="tableList.goods" selection no-border class="table">
        <q-table-column label="商品" align="left" width="300">
            <template #default="scope">
                <CommodityInfo
                    :info="scope.row"
                    :priceFlag="false"
                    @update-price="(e:any[], v:any) => handleUpdatePrice(e, v, scope.row)"
                    @update-name="(name) => (scope.row.productName = name)"
                />
            </template>
        </q-table-column>

        <q-table-column label="供货价" width="160px">
            <template #default="{ row }">
                <span class="price">
                    <span>￥{{ price(row) }}</span>
                    <span class="priceEdit" @click="openEditPrice(row)"> <q-icon name="icon-bianji_o " color="#333"></q-icon> </span
                ></span>
            </template>
        </q-table-column>
        <q-table-column label="库存" style="flex-direction: column; align-items: start" width="100px">
            <template #default="{ row }">
                <div v-if="row.storageSkus.length" class="stock" style="position: relative; width: 100%">
                    <span>{{ handleComputeStocks(row.storageSkus) }}</span>
                    <q-icon
                        v-if="handleComputeStocks(row.storageSkus) !== '无限库存'"
                        class="stockEdit"
                        name="icon-bianji_o"
                        color="#333"
                        @click="singleOptionHandle('inventory', row)"
                    ></q-icon>
                </div>
                <div class="warning">
                    {{ handleComputeStocks(row.storageSkus) === '0' || currentTab === 'SELL_OUT' ? '已售罄' : handleStockLess(row.storageSkus) }}
                </div>
            </template>
        </q-table-column>
        <q-table-column label="已售" width="80px">
            <template #default="{ row }">
                <div>{{ computedSalesVolume(row.storageSkus) }}</div>
            </template>
        </q-table-column>
        <q-table-column label="销售方式" width="100px">
            <template #default="{ row }">
                <div>{{ SellTypeEnum[row.sellType as keyof typeof SellTypeEnum] }}</div>
            </template>
        </q-table-column>
        <q-table-column label="商品状态" width="100px">
            <template #default="{ row }">
                <!-- https://github.com/element-plus/element-plus/issues/12657 -->
                <!-- 存在违规下架的参数值，因违规下架的参数值为PLATFORM_SELL_OFF，既不属于SELL_ON，也不属于SELL_OFF
                    因此会导致switch组件触发一次change事件，导致当前v-model绑定的row.status强制置为否定项也就是SELL_OFF，从而出现组件下架状态渲染错误的情况
                -->
                <div v-if="['SELL_ON', 'SELL_OFF'].includes(row.status)">
                    <el-switch
                        v-model="row.status"
                        inline-prompt
                        active-text="上架"
                        inactive-text="下架"
                        active-value="SELL_ON"
                        inactive-value="SELL_OFF"
                        @click="singleOptionHandle(row.status, row)"
                    />
                </div>
                <div v-else>
                    <div v-if="row.status === 'SELL_ON'" class="sell sell__on">已上架</div>
                    <div v-else-if="row.status === 'PLATFORM_SELL_OFF'" class="sell sell__platform">违规下架</div>
                    <div v-else-if="row.status === 'SUPPLIER_SELL_OFF'" class="sell sell__platform">供应商下架</div>
                    <div v-else class="sell sell__off">已下架</div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="操作" align="right" width="150px" fixed="right">
            <template #default="scope">
                <div>
                    <QDropdownBtn
                        v-if="scope.row.status === 'PLATFORM_SELL_OFF'"
                        title="查看"
                        :option="platformsideList"
                        type="primary"
                        @right-click="(e: any) => handleCheck(e, scope.row)"
                        @left-click="previewHandle(scope.row)"
                    >
                        <template v-if="scope.row.status !== 'PLATFORM_SELL_OFF'" #link>
                            <el-link type="primary" style="margin-left: 12px" @click="copy(scope.row)">复制</el-link>
                        </template>
                    </QDropdownBtn>
                    <QDropdownBtn
                        v-else-if="scope.row.status === 'SELL_ON'"
                        title="编辑"
                        :option="SELLONsideList"
                        type="primary"
                        @right-click="(e: any) => singleOptionHandle(e, scope.row)"
                        @left-click="editHandle(scope.row.id)"
                    >
                        <template #link>
                            <el-link type="primary" style="margin-left: 12px" @click="copy(scope.row)">复制</el-link>
                        </template>
                    </QDropdownBtn>
                    <QDropdownBtn
                        v-else
                        title="编辑"
                        :option="sideList.filter((item) => item.name !== scope.row.status)"
                        type="primary"
                        @right-click="(e: any) => singleOptionHandle(e, scope.row)"
                        @left-click="editHandle(scope.row.id)"
                    >
                        <template #link>
                            <el-link type="primary" style="margin-left: 12px" @click="copy(scope.row)">复制</el-link>
                        </template>
                    </QDropdownBtn>
                </div>
            </template>
        </q-table-column>
    </q-table>

    <PageManage
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <purchase-dialog :show="purchaseVisible" :sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleClosePurchase" />
    <inventory-dialog :show="inventoryVisible" :sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleCloseInventory" />
    <el-dialog v-model="sellOffVisible" title="违规原因" width="690px" center>
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
    <el-dialog v-model="previewVisible" title="商品详情" center width="900px" top="5vh" destroy-on-close>
        <CommodityDetails :commodity-id="currentRow.id" />
    </el-dialog>
    <el-dialog v-model="editPriceVisible" title="价格设置" center width="900px" style="order: 100" destroy-on-close @close="handleClose">
        <template #default>
            <el-row :gutter="20" align="middle">
                <el-col :span="6" style="text-align: center;bagc"> 批量设置 </el-col>
                <el-col :span="8"
                    >划线价：
                    <el-input-number
                        v-model="allPrice"
                        :max="999999"
                        :min="0.01"
                        :precision="2"
                        :controls="false"
                        @blur="priceSkuItemRef?.setALLskyPrice('price', allPrice)"
                /></el-col>
                <el-col :span="7"
                    >供货价：
                    <el-input-number
                        v-model="allSalePrice"
                        :max="999999"
                        :min="0.01"
                        :precision="2"
                        :controls="false"
                        @blur="priceSkuItemRef?.setALLskyPrice('salePrice', allSalePrice)"
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
</template>
<style lang="scss" scoped>
@include b(tool) {
    height: 70px;
    @include flex(flex-start);
}

@include b(warning) {
    color: #e6a23c;
    margin-top: 3px;
    font-size: 12px;
}

@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}

@include b(sell) {
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
.stock {
    display: flex;
    align-items: center;
    &:hover {
        .stockEdit {
            display: block;
        }
    }
    .stockEdit {
        display: none;
        margin-left: 5px;
        cursor: pointer;
    }
}
.price {
    display: flex;
    align-items: center;
    color: red;
    &:hover {
        .priceEdit {
            display: block;
        }
    }
    .priceEdit {
        display: none;
        margin-left: 5px;
        cursor: pointer;
    }
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
        min-width: 80px;
        text-align: right;
        margin-right: 8px;
    }
}
</style>
