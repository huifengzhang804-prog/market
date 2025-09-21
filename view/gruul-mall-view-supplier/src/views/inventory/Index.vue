<script lang="ts" setup>
import Search, { SearchType } from './components/Search.vue'
import type { TabPaneName } from 'element-plus'
import { ElMessage } from 'element-plus'
import CommodityInfo from './components/CommodityInfo.vue'
import PageManage from '@/components/PageManage/index.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PurchaseDialog from './components/purchase-dialog.vue'
import priceSkuItem from './components/priceSkuItem.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import InventoryDialog from './components/inventory-dialog.vue'
import { doGetCommoditySku, doGetInventoryCommodity, doGetProductPurchasing, doPriceUpdate } from '@/apis/good'
import type { ApiCommodityType, CommoditySpecTable } from '@/views/goods/types'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()
const { mulTenThousand, divTenThousand } = useConvert()
const tableList = reactive({
    page: { size: 10, current: 1 },
    goods: [],
    total: 0,
})
const searchParams = ref<any>({
    supplierGoodsName: '',
    platformCategoryId: '',
    supplierProductStatus: '',
    isDeleted: false,
})
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
const explainData = reactive({
    rummager: '',
    violationType: '',
    violationExplain: '',
    violationEvidence: [] as string[],
    examineDateTime: '',
})
// 全部划线价
const allPrice = ref(0)
// 全部销售价
const allSalePrice = ref(0)
const priceSkuItemRef = ref<InstanceType<typeof priceSkuItem>>()

/**
 * @description: 库存与限购返回
 * @param {*} productId
 * @returns {*}
 */
async function initProductPurchasing(productId: string, type: 'purchase' | 'inventory' | 'specs') {
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
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}

/**
 * @description: 操作下拉
 * @param {*} e
 * @returns {*}
 */
const singleOptionHandle = (e: string, row: ApiCommodityType) => {
    if (handleComputeStocks(row.storageSkus) === '无限库存') {
        return
    }
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
        searchParams.value.supplierProductStatus = status
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
        return `${min}-${max}`
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
const getCommoditySku = async (productId: string) => {
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
//修改价格 e
const handleClose = () => {
    allPrice.value = 0
    allSalePrice.value = 0
}

// 分页器
const handleSizeChange = (value: number) => {
    tableList.page.size = value
    initList()
}
const handleCurrentChange = (value: number) => {
    tableList.page.current = value
    initList()
}
</script>
<template>
    <div class="search_container">
        <Search @on-search-params="getSearch" />
    </div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" style="margin-top: 15px" @tab-change="handleTabClick">
            <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
        </el-tabs>
    </div>

    <q-table :data="tableList.goods" class="table" no-border>
        <q-table-column label="商品" width="450">
            <template #default="scope">
                <CommodityInfo
                    :info="scope.row"
                    :currentTab="currentTab"
                    @updateName="(name:string) => (scope.row.productName = name)"
                    @update-name="(e:any) => handleUpdatename(e,scope.row)"
                />
            </template>
        </q-table-column>
        <q-table-column label="供货价" width="180">
            <template #default="{ row }">
                <div class="price">
                    <span>￥{{ getprice(row.storageSkus) }}</span>
                    <QIcon v-if="currentTab !== 'delted'" name="icon-bianji_o" class="edit" size="18px" @click="openEditPrice(row)" />
                </div>
            </template>
        </q-table-column>
        <q-table-column label="库存" width="130">
            <template #default="{ row }">
                <div>
                    <div v-if="row.storageSkus.length" class="fcenter" style="position: relative; width: 100%">
                        <text>{{ handleComputeStocks(row.storageSkus) }}</text>
                        <QIcon
                            v-if="currentTab !== 'delted'"
                            key="inventory/center"
                            class="edit"
                            size="18px"
                            name="icon-bianji_o"
                            color="#333"
                            @click="singleOptionHandle('inventory', row)"
                        />
                    </div>
                    <div class="warning">
                        {{ handleStockLess(row.storageSkus) }}
                    </div>
                </div>
            </template>
        </q-table-column>

        <q-table-column label="状态" width="110">
            <template #default="scope">
                <div v-if="scope.row.deleted" class="sell sell__platform">已删除</div>
                <div v-else-if="scope.row.status === 'SELL_ON'" class="sell sell__on">已上架</div>
                <div v-else-if="scope.row.status === 'PLATFORM_SELL_OFF'" class="sell sell__platform">违规下架</div>
                <div v-else class="sell sell__off">已下架</div>
            </template>
        </q-table-column>
        <q-table-column align="right" label="操作" width="160" fixed="right">
            <template #default="scope">
                <el-link v-if="scope.row.status === 'PLATFORM_SELL_OFF'" type="primary" @click="handleCheck(scope.row)"> 违规原因 </el-link>
                <el-link
                    v-if="scope.row.status !== 'PLATFORM_SELL_OFF' && currentTab !== 'delted'"
                    type="primary"
                    @click="singleOptionHandle('purchase', scope.row)"
                    >限购设置
                </el-link>
                <el-link
                    v-if="scope.row.storageSkus.length > 1 && scope.row.status !== 'PLATFORM_SELL_OFF'"
                    style="margin-left: 10px"
                    type="primary"
                    @click="singleOptionHandle('specs', scope.row)"
                    >规格明细
                </el-link>
            </template>
        </q-table-column>
    </q-table>
    <PageManage
        :page-num="tableList.page.current"
        :page-size="tableList.page.size"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <purchase-dialog :product-id="currentRow && currentRow.id" :show="purchaseVisible" :sku="currentSku" @close="handleClosePurchase" />
    <inventory-dialog :product-id="currentRow && currentRow.id" :show="inventoryVisible" :sku="currentSku" @close="handleCloseInventory" />
    <el-dialog v-model="sellOffVisible" center title="违规原因" width="690px">
        <div class="explainData" style="line-height: 30px">
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
    <el-dialog v-model="editPriceVisible" center destroy-on-close title="价格设置" width="900px" @close="handleClose">
        <template #default>
            <el-row :gutter="20" align="middle">
                <el-col :span="6" style="text-align: center"> 批量设置</el-col>
                <el-col :span="8"
                    >划线价：
                    <el-input-number v-model="allPrice" :controls="false" @blur="priceSkuItemRef?.setALLskyPrice('price', allPrice)" />
                </el-col>
                <el-col :span="7"
                    >供货价：
                    <el-input-number v-model="allSalePrice" :controls="false" @blur="priceSkuItemRef?.setALLskyPrice('salePrice', allSalePrice)" />
                </el-col>
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
    <el-dialog v-model="specsVisible" center destroy-on-close title="规格明细" width="900px">
        <el-table :data="currentSku" height="60vh">
            <el-table-column label="商品规格" width="340">
                <template #default="{ row }">
                    <div class="sku-item">
                        <el-image v-if="row.image" :src="row.image" />
                        <div>
                            <div style="margin-bottom: 11px; text-align: left">{{ row.specs[0] }}</div>
                            <div style="color: #0f40f5; cursor: pointer" @click="copyOrderNo(row.id)">SKUID: {{ row.id }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="划线价" prop="price">
                <template #default="{ row }"> ￥{{ divTenThousand(row.price) }}</template>
            </el-table-column>
            <el-table-column label="供货价" prop="salePrice">
                <template #default="{ row }"
                    ><span style="color: #f50707">￥{{ divTenThousand(row.salePrice) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="库存">
                <template #default="{ row }">
                    <div v-if="row.stockType !== 'UNLIMITED'">{{ row.stock }}</div>
                    <div v-else>无限库存</div>
                </template>
            </el-table-column>
            <el-table-column label="限购">
                <template #default="{ row }">
                    <div v-if="row.limitType === 'UNLIMITED'">不限购</div>
                    <div v-else-if="row.limitType === 'PRODUCT_LIMITED'">商品限购：{{ row.limitNum }}</div>
                    <div v-else>规格限购：{{ row.limitNum }}</div>
                </template>
            </el-table-column>
        </el-table>
    </el-dialog>
</template>
<style lang="scss">
@include b(tool) {
    height: 70px;
    @include flex(flex-start);
}

@include b(warning) {
    color: #e6a23c;
    margin-top: 5px;
    width: 100%;
}

@include b(sku-item) {
    display: flex;
    align-items: center;
    @include b(el-image) {
        width: 80px;
        height: 80px;
        margin-right: 10px;
    }
}

@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}

@include b(sell) {
    font-size: 14px;

    @include e(on) {
        color: #00bb2c;
    }

    @include e(off) {
        color: #666666;
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
.price,
.fcenter {
    display: flex;
    align-items: center;
    &:hover .edit {
        display: block;
    }
    .edit {
        margin-left: 5px;
        cursor: pointer;
        display: none;
    }
}
</style>
