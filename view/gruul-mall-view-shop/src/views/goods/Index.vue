<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
import Search, { SearchType } from './components/Search.vue'
import { ElMessage, ElMessageBox, TabPaneName } from 'element-plus'
import CommodityInfo from './components/CommodityInfo.vue'
import PageManage from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PurchaseDialog from './components/purchase-dialog.vue'
import InventoryDialog from './components/inventory-dialog.vue'
import CommodityDetails from './details/index.vue'
import { doGetCommodity, doUpdateSellStatus, doDeleteCommodity, doGetProductPurchasing } from '@/apis/good'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import type { ApiCommodityType, CommoditySpecTable, TableTypes, GoodsType } from './types'
import { useShopInfoStore } from '@/store/modules/shopInfo'

enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}

const sideList = reactive([
    // {
    //     name: 'copy',
    //     label: '复制',
    // },
    {
        name: 'delete',
        label: '删除',
    },
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
const $router = useRouter()
const $route = useRoute()
const tableList = reactive<TableTypes>({
    page: { size: 10, current: Number($route.query.current) || 1 },
    goods: [],
    total: 0,
})
const searchParams = ref({
    createBeginTime: '',
    createEndTime: '',
    name: '',
    categoryId: '',
    secondPlatformCategoryId: '',
    status: '',
})
const multiSelect = ref<GoodsType[]>([])
const currentRow = ref<Partial<ApiCommodityType>>({})
//限购弹窗
const purchaseVisible = ref(false)
//库存弹窗
const inventoryVisible = ref(false)
// 详情弹窗
const previewVisible = ref(false)
// 当前选中行sku信息
const currentSku = ref<CommoditySpecTable[]>()
const labelIcon = ref(false)

const violationTypeMap = {
    PROHIBITED: '违禁品',
    COUNTERFEIT: '假冒伪劣',
    EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
    TITLE_IRREGULARITY: '标题有问题',
    OTHER: '其他',
}

// 违规信息
const explainData = reactive({ rummager: '', violationType: '', violationExplain: '', violationEvidence: [] as string[], examineDateTime: '' })

/**
 * 库存与限购返回
 * @param {*} productId
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
const loading = ref(false)
const initList = async (sort?: string) => {
    loading.value = true
    const { data } = await doGetCommodity({
        ...searchParams.value,
        ...tableList.page,
        sort: sort,
    })
    tableList.total = data.total
    tableList.goods = data.records.map((v: GoodsType) => {
        if (v.status === 'SUPPLIER_SELL_OFF') {
            return {
                ...v,
                disabled: true,
            }
        }
        return {
            ...v,
            disabled: false,
        }
    })
    loading.value = false
}

const getshangSearch = (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}
/**
 * 商品上下架
 * @param {boolean} isMulti
 */
const changeCommodityStatus = (isMulti: boolean, status: string) => {
    if (multiSelect.value.length === 0 && isMulti) {
        ElMessage.error('请选择商品')
        return
    }
    removeFromShelves(isMulti, status)
}
const removeFromShelves = async (isMulti: boolean, status: string, row?: ApiCommodityType) => {
    if (row) {
        currentRow.value = row
    }
    let ids = isMulti
        ? multiSelect.value.map((item) => ({ shopId: useShopInfoStore().shopInfo.shopId, productId: item.id }))
        : [{ shopId: useShopInfoStore().shopInfo.shopId, productId: currentRow.value.id as string }]
    const { code, success } = await doUpdateSellStatus(ids, status)
    if (code === 200 && success) {
        multiSelect.value = []
        ElMessage.success(`${status.includes('OFF') ? '下' : '上'}架成功`)
        initList()
    }
}

const delCommodity = (isMulti: boolean) => {
    if (multiSelect.value.some((item: any) => item.sellType === 'CONSIGNMENT')) {
        return ElMessage.warning('代销商品不能删除')
    }
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
            multiSelect.value = []
            ElMessage.success('删除成功')
            initList()
        } else {
            ElMessage.error(msg || '删除失败')
        }
    })
}
const handleDel = (isMulti: boolean, row: ApiCommodityType) => {
    currentRow.value = row
    delCommodity(isMulti)
}

// 价钱排序
const sort = ref('')
const sortTableList = (label: string) => {
    const values1 = ['SALE_PRICE_ASC', 'SALE_PRICE_DESC', '']
    const values2 = ['STOCK_ASC', 'STOCK_DESC', '']
    const values3 = ['SALE_COUNT_ASC', 'SALE_COUNT_DESC', '']
    switch (label) {
        case '价格':
            sort.value = values1[(values1.indexOf(sort.value) + 1) % values1.length]
            initList(sort.value)
            break
        case '库存':
            sort.value = values2[(values2.indexOf(sort.value) + 1) % values2.length]
            initList(sort.value)
            break
        case '已售':
            sort.value = values3[(values3.indexOf(sort.value) + 1) % values3.length]
            initList(sort.value)
            break
        default:
            break
    }
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
        } else if (e === 'copy') {
            $router.push({
                path: '/goods/list/copy',
                query: {
                    id: row?.id,
                    isCopy: 'true',
                },
            })
            return
        } else if (e === 'preview') {
            previewHandle(row)
            return
        }
        if (e === 'delete') {
            delCommodity(false)
            return
        } else {
            changeCommodityStatus(false, e)
        }
    }
}

const copy = (row: ApiCommodityType) => {
    $router.push({
        path: '/goods/list/copy',
        query: {
            id: row?.id,
            isCopy: true as any,
        },
    })
}

const editHandle = (row: any) => {
    let path = ''
    if (row?.sellType === 'CONSIGNMENT') {
        path = '/goods/list/edit-consignment'
    } else {
        path = '/goods/list/edit'
    }
    $router.push({
        path,
        query: {
            id: row.id,
            current: row?.sellType !== 'CONSIGNMENT' && (tableList.page.current as any),
        },
    })
}
const previewHandle = (row: any) => {
    currentRow.value = row
    previewVisible.value = true
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
    let isAllUnlimited = true
    storageSkus.forEach((storageSku) => {
        if (storageSku.stockType === 'LIMITED') isAllUnlimited = false
    })
    if (isAllUnlimited) return '无限库存'
    return storageSkus.reduce((pre, cur) => {
        return pre + (cur.stockType === 'LIMITED' ? Number(cur.stock) : 0)
    }, 0)
}
/**
 * 判断商品规格是否不足10个
 */
const handleStockLess = (storageSkus: CommoditySpecTable[] = [], stocks = 0) => {
    if (stocks === 0) {
        return '已售罄'
    }
    const filterLimitStockArr = storageSkus.filter((item) => {
        return item.stockType === 'LIMITED'
    })
    const checkLimitStockArr = filterLimitStockArr.filter((item) => {
        return Number(item.stock) <= 10
    })
    if (!filterLimitStockArr.length || !checkLimitStockArr.length) return ''
    return filterLimitStockArr.length === checkLimitStockArr.length ? '库存不足' : '部分库存不足'
}
// 计算已售
const computedSalesVolume = (storageSkus: CommoditySpecTable[]) => {
    return (
        storageSkus?.reduce((pre, cur) => {
            return pre + Number(cur.salesVolume)
        }, 0) || 0
    )
}

// 违规原因弹框
const sellOffVisible = ref(false)
// 违规商品点击更多
const handleCheck = (status: string, row: ApiCommodityType) => {
    if (status === 'delete') {
        handleDel(false, row)
    } else if (status === 'copy') {
        $router.push({
            path: '/goods/list/copy',
            query: {
                id: row?.id,
                isCopy: true as any,
            },
        })
    } else {
        const productViolation = row?.extra?.productViolation || {}
        ;(Object.keys(explainData) as (keyof typeof explainData)[]).forEach((key) => {
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
                explainData[explainDataKey] = productViolation?.[explainDataKey] || ''
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

onMounted(() => {
    initList()
})

const currentTab = ref<'' | 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF'>('')

//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    multiSelect.value = []
    searchParams.value.status = status as string
    initList()
}
const handleUpdatePrice = (e: any[], v: any, r: any) => {
    r.storageSkus[0].salePrice = e[0].salePrice
}
/**
 * 计算显示的按钮列表
 */
const computedSideList = computed(() => (row: any) => {
    const basicSideList = [{ name: 'preview', label: '查看' }]
    if (row?.sellType === 'CONSIGNMENT') return [...basicSideList]
    if (['PLATFORM_SELL_OFF', 'SELL_OFF'].includes(row.status) && row?.sellType !== 'PURCHASE') {
        return [...basicSideList, ...sideList]
    }
    return [...basicSideList, ...sideList.filter((item) => item.name !== 'delete')]
})
/**
 * 违规下架计算显示的按钮列表
 */
const computedViolationSideList = computed(() => (row: any) => {
    const basicSideList = [{ name: 'preview', label: '查看' }]
    if (row?.sellType === 'CONSIGNMENT' || row?.sellType === 'PURCHASE') {
        return [...basicSideList]
    }
    return [...platformsideList]
})
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
const curRowKey = ref<Long>('')
const openTagsPopover = (row: GoodsType) => {
    console.log(row)
    curRowKey.value = row.id
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search @onSearchParams="getshangSearch" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" style="margin-top: 15px" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>
    <div class="handle_container">
        <router-link to="/goods/list/new">
            <el-button style="margin-right: 10px" type="primary" round>新增商品</el-button>
        </router-link>
        <el-button
            v-show="[' ', 'SELL_OFF'].includes(currentTab)"
            :disabled="multiSelect.length === 0"
            style="margin-right: 10px"
            round
            @click="changeCommodityStatus(true, 'SELL_ON')"
            >批量上架</el-button
        >
        <el-button
            v-show="[' ', 'SELL_ON'].includes(currentTab)"
            :disabled="multiSelect.length === 0"
            style="margin-right: 10px"
            round
            @click="changeCommodityStatus(true, 'SELL_OFF')"
            >批量下架</el-button
        >
    </div>
    <QTable v-model:checkedItem="multiSelect" :data="tableList.goods" :selection="true" no-border class="table" @change-sort="sortTableList">
        <template #noData>
            <div class="no_data">
                <img src="@/assets/images/no_shop.png" alt="" />
                <p class="cont">您还没有相关商品</p>
            </div>
        </template>
        <QTableColumn label="商品" width="400">
            <template #default="{ row }">
                <div @mouseenter="row.labelIcon = true" @mouseleave="row.labelIcon = false">
                    <CommodityInfo
                        :info="row"
                        :isPrice="false"
                        :curRowKey="curRowKey"
                        style="height: 83px"
                        @initList="initList"
                        @openTagsPopover="openTagsPopover(row)"
                    />
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="销售价" width="190">
            <template #default="{ row }">
                <CommodityInfo :info="row" :is-price="true" :show-pic="false" @updatePrice="(e:any[], v:any) => handleUpdatePrice(e, v, row)" />
            </template>
        </QTableColumn>
        <QTableColumn label="库存" width="130">
            <template #default="{ row }">
                <div @mouseenter="row.stockIcon = true" @mouseleave="row.stockIcon = false">
                    <div v-if="row.storageSkus?.length" class="stock_container fcenter">
                        <span style="cursor: pointer">{{ handleComputeStocks(row.storageSkus) }}</span>
                        <div
                            v-if="row?.sellType !== 'CONSIGNMENT' && handleComputeStocks(row.storageSkus) !== '无限库存'"
                            class="edit"
                            style="cursor: pointer"
                            @click="singleOptionHandle('inventory', row)"
                        >
                            <q-icon v-show="row.stockIcon" key="goods/list" name="icon-bianji_o" color="#333" style="margin-left: 10px"></q-icon>
                        </div>
                    </div>
                    <div class="warning">
                        {{ handleStockLess(row.storageSkus, +handleComputeStocks(row.storageSkus)) }}
                    </div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="已售" width="100px">
            <template #default="{ row }">
                <div>{{ computedSalesVolume(row.storageSkus) }}</div>
            </template>
        </QTableColumn>
        <QTableColumn label="商品来源" width="120px">
            <template #default="{ row }">
                <div>{{ SellTypeEnum[row.sellType as keyof typeof SellTypeEnum] }}</div>
            </template>
        </QTableColumn>
        <QTableColumn label="商品状态" width="110px">
            <template #default="{ row }">
                <div v-if="['SELL_ON', 'SELL_OFF'].includes(row.status)">
                    <el-switch
                        v-model="row.status"
                        inline-prompt
                        active-text="上架"
                        :inactive-text="row.status === 'SUPPLIER_SELL_OFF' ? '供应商下架' : '下架'"
                        active-value="SELL_ON"
                        inactive-value="SELL_OFF"
                        @click="removeFromShelves(false, row.status, row)"
                    />
                </div>
                <div v-else>
                    <div v-if="row.status === 'SELL_ON'" class="sell sell__on">已上架</div>
                    <div v-else-if="row.status === 'PLATFORM_SELL_OFF'" class="sell sell__platform">违规下架</div>
                    <div v-else-if="row.status === 'SUPPLIER_SELL_OFF'" class="sell sell__platform">供应商下架</div>
                    <div v-else class="sell sell__off">已下架</div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="操作" width="150px" align="right" fixed="right">
            <template #default="scope">
                <q-dropdown-btn
                    v-if="scope.row.status === 'PLATFORM_SELL_OFF'"
                    title="查看"
                    :option="computedViolationSideList(scope.row)"
                    @rightClick="(e: any) => handleCheck(e, scope.row)"
                    @leftClick="previewHandle(scope.row)"
                >
                    <template #link>
                        <el-link v-if="scope.row.status !== 'PLATFORM_SELL_OFF'" type="primary" style="margin-left: 12px" @click="copy(scope.row)"
                            >复制</el-link
                        >
                    </template>
                </q-dropdown-btn>
                <q-dropdown-btn
                    v-else
                    title="编辑"
                    :option="computedSideList(scope.row)"
                    @rightClick="(e: any) => singleOptionHandle(e, scope.row)"
                    @leftClick="editHandle(scope.row)"
                >
                    <template #link>
                        <el-link type="primary" style="margin-left: 12px" @click="copy(scope.row)">复制</el-link>
                    </template>
                </q-dropdown-btn>
            </template>
        </QTableColumn>
    </QTable>
    <div v-if="tableList.total" class="pagination">
        <PageManage
            :page-size="tableList.page.size"
            :page-num="tableList.page.current"
            :total="tableList.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <PurchaseDialog v-model:show="purchaseVisible" v-model:sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleClosePurchase" />
    <InventoryDialog v-model:show="inventoryVisible" :sku="currentSku" :product-id="currentRow && currentRow.id" @close="handleCloseInventory" />
    <el-dialog v-model="sellOffVisible" title="违规原因" width="500px" center>
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
    <el-dialog v-model="previewVisible" title="商品详情" center width="900px" destroy-on-close top="3vh">
        <CommodityDetails :commodity-id="currentRow.id" />
    </el-dialog>
</template>
<style lang="scss">
* {
    font-size: 14px;
}
.stock_container {
    width: fit-content;
    .edit {
        display: block !important;
        position: relative !important;
        margin-left: 10px !important;
    }
}

@include b(warning) {
    color: #e6a23c;
    margin-top: 5px;
}

@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}

@include b(sell) {
    font-size: 14px;
    @include e(on) {
        color: #82c26b;
    }

    @include e(off) {
        color: #f4a584;
    }

    @include e(platform) {
        color: #f12f22;
    }
}
.no_data {
    margin-top: 100px;
    img {
        width: 170px;
        height: 170px;
    }
    .cont {
        color: #737b80;
        text-align: center;
        margin-top: 20px;
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
.violation-evidence {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    object-fit: contain;
}
.violation-evidence + .violation-evidence {
    margin-left: 5px;
}
</style>
