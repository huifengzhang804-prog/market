<script setup lang="ts">
import PageManage from '@components/PageManage.vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { ElMessage, UploadProps } from 'element-plus'
import { doGetInventoryCommodity, doGetCategory } from '@/apis/good'
import { doPostStorageOrderCreate, doGetStorageOrderDetail, doPostStorageOrderedit } from '@/apis/inventory'
import useBatchStorage from './hooks/useBatchStorage'
import { useRouter } from 'vue-router'
import type { ApiCommodityType, CommoditySpecTable } from '@/views/goods/types'

type SelectComInfo = {
    current: number
    list: ApiCommodityType[]
    total: number
    size: number
    productName: string
    productId: string
    shopCategoryId: string
    sellType: string
    loading: boolean
    productIds: string[]
}
interface ManagementOrderItemsType {
    productId: string
    productName: string
    pic: string
    skuStockItems: {
        productId: string
        skuId: string
        num: number
        stockType: 'UNLIMITED' | 'LIMITED'
        stock?: number
        specs?: number
    }[]
}

const $router = useRouter()
const $route = useRoute()
const selectComDialog = ref(false)
const storageForm = ref({
    stockChangeType: 'ALLOCATION_INBOUND',
    evidence: [] as string[],
    remark: '',
    storageManagementOrderItems: [] as ManagementOrderItemsType[],
    storageManagementOrderType: 'IN_OUT',
})
// 选择商品数据
const commodityInfo = reactive<SelectComInfo>({
    current: 1,
    list: [],
    total: 0,
    size: 10,
    loading: false,
    sellType: '',
    productName: '',
    productId: '',
    shopCategoryId: '',
    productIds: [], //不需要展示的商品 id列表
})
//选择商品类型列表
const typeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'PURCHASE',
        label: '采购商品',
    },
    {
        value: 'OWN',
        label: '自有商品',
    },
])
const categoryList = ref([])
const shopCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'name',
    value: 'id',
}
// 选择商品选中列表
const selectGoodsList = ref<ApiCommodityType[]>([])
//表格数据
const storageTableData = ref<ApiCommodityType[]>([])
const { maxRowBatchNum, computedSuplier, handleChangeStorageNum, changeRowBatchNum, handleRemoveBatch, handleRemoveSku } = useBatchStorage(
    storageTableData,
    commodityInfo,
)

initstorageForm()
initCategory()

async function initCategory() {
    const { data, code } = await doGetCategory({ size: 1000 })
    categoryList.value = data.records
}
const handleChangeCascader = (e: any) => {
    commodityInfo.shopCategoryId = e[e.length - 1]
}
/**
 * 获取添加商品列表
 */
async function initstorageForm() {
    const id = $route.query.id as string
    if (!id) return
    const { code, data, msg } = await doGetStorageOrderDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取数据失败')
    storageForm.value = data
    organizedata(data.storageManagementOrderItems)
}
const organizedata = (storageManagementOrderItems: any) => {
    storageTableData.value = storageManagementOrderItems.map((item: ManagementOrderItemsType) => {
        const storageSkus = item.skuStockItems.map((sku) => {
            return {
                productId: sku.productId,
                id: sku.skuId,
                StorageNum: Math.abs(sku.num) || 0,
                stockType: sku.stockType,
                stock: sku.stock,
                specs: sku.specs,
            }
        })
        return {
            id: item.productId,
            productName: item.productName,
            albumPics: item.pic,
            totalStorage: storageSkus.reduce((pre: number, item) => {
                return pre + item.StorageNum
            }, 0),
            storageSkus,
        }
    })
    commodityInfo.productIds = storageTableData.value.map((item) => item.id)
}
/**
 * 删除凭证
 * @param {number} index
 */
const delImgHandle = (index: number) => {
    storageForm.value.evidence.splice(index, 1)
}
/**
 * 新增凭证
 * @returns {UploadProps}
 */
const addNewMainSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    storageForm.value.evidence.push(response)
}
/**
 * 获取添加商品列表
 */
async function initComList() {
    commodityInfo.loading = true
    const { code, data } = await doGetInventoryCommodity({
        current: commodityInfo.current,
        size: commodityInfo.size,
        productName: commodityInfo.productName,
        productId: commodityInfo.productId,
        shopCategoryId: commodityInfo.shopCategoryId,
        sellType: commodityInfo.sellType,
        productIds: commodityInfo.productIds.join(','),
        stockChangeType: storageForm.value.stockChangeType,
    })
    if (code === 200) {
        commodityInfo.list = data.records
        // commodityInfo.size = data.pageSize
        // commodityInfo.current = data.pageNum
        commodityInfo.total = data.total
    } else {
        ElMessage.error('获取商品失败')
    }
    commodityInfo.loading = false
}
const handleChooseCom = () => {
    selectComDialog.value = true
    initComList()
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
 * 初始化选择商品
 */
function resetComInfo() {
    commodityInfo.current = 1
    commodityInfo.list = []
    commodityInfo.total = 0
    commodityInfo.sellType = ''
    commodityInfo.shopCategoryId = ''
    commodityInfo.productName = ''
    commodityInfo.productId = ''
    commodityInfo.loading = false
    selectGoodsList.value = []
}
const handleChangeCurrent = (e: number) => {
    commodityInfo.current = e
    initComList()
}
const handleChangeSize = (e: number) => {
    commodityInfo.size = e
    initComList()
}
const commoditySearch = () => {
    commodityInfo.current = 1
    initComList()
}
//选择商品tableCheck选中
const handleGoodsTableSelect = (e: ApiCommodityType[]) => {
    selectGoodsList.value = e
}
const handleSelectGood = () => {
    if (selectGoodsList.value.length + storageTableData.value.length > 10) {
        return ElMessage.error(`最多选择10种商品`)
    }
    const arr = selectGoodsList.value.map((item) => {
        return item.id
    })
    commodityInfo.productIds = [...commodityInfo.productIds, ...arr]
    storageTableData.value = [...storageTableData.value, ...selectGoodsList.value]
    selectComDialog.value = false
}
const handleBatchStorage = async () => {
    try {
        storageForm.value.storageManagementOrderItems = storageTableData.value.map((item) => {
            const skuStockItems = item.storageSkus.map((sku) => {
                if (!sku.StorageNum && sku.stockType !== 'UNLIMITED') throw new Error('请确保已填入出入库数值')
                return {
                    productId: sku.productId,
                    skuId: sku.id,
                    num: sku.StorageNum,
                    stockType: sku.stockType,
                }
            }) as any[]
            return {
                productId: item.id,
                productName: item.productName,
                pic: item.albumPics.split(',')[0],
                skuStockItems,
            }
        })
        const { code, msg } = $route.query.id ? await doPostStorageOrderedit(storageForm.value) : await doPostStorageOrderCreate(storageForm.value)
        if (code !== 200) {
            return ElMessage.error(msg || '新增出入库失败')
        }
        $router.push('/inventory/storage')
    } catch (e: any) {
        ElMessage.error(e.message)
    }
}
const expandableTable = ref()
const handleRowClick = (row: ApiCommodityType) => {
    expandableTable.value.toggleRowExpansion(row)
}
</script>

<template>
    <el-form ref="formRef" :model="storageForm" label-width="100px" style="padding-top: 16px">
        <el-form-item label="类型">
            <el-radio-group v-model="storageForm.stockChangeType">
                <el-radio value="ALLOCATION_INBOUND">调拨入库</el-radio>
                <el-radio value="OTHER_INBOUND">其它入库</el-radio>
                <el-radio value="ALLOCATION_OUTBOUND">调拨出库</el-radio>
                <el-radio value="OTHER_OUTBOUND">其它出库</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="相关凭证">
            请上传相关(出入库)凭证等，支持 png\jpg\jpeg 文件，最多上传 4 张
            <el-row style="width: 100%">
                <div v-for="(item, index) in storageForm.evidence" :key="item" style="position: relative; margin-right: 20px">
                    <q-upload v-model:src="storageForm.evidence[index]" :width="100" :height="100" :format="{ size: 1 }" />
                    <el-icon
                        v-if="item"
                        style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%"
                        color="#7f7f7f"
                        size="20px"
                        @click="delImgHandle(index)"
                        ><i-ep-circle-close
                    /></el-icon>
                </div>
                <q-upload v-show="storageForm.evidence.length <= 3" :width="100" :height="100" :format="{ size: 1 }" @change="addNewMainSuccess" />
            </el-row>
        </el-form-item>
        <el-form-item label="备注">
            <el-input v-model="storageForm.remark" :rows="3" type="textarea" :maxlength="200" placeholder="请输入备注" />
        </el-form-item>
    </el-form>
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0 20px; margin-bottom: 10px">
        <div>每个订单最多添加<span style="color: #fd0505; font-size: 16px; font-weight: 700">10</span> 种商品</div>
        <el-button type="primary" round @click="handleChooseCom">添加商品</el-button>
    </div>
    <el-table
        ref="expandableTable"
        :data="storageTableData"
        row-key="id"
        style="width: 97%; margin: auto"
        :header-cell-style="{
            'background-color': '#F0F8FA',
            'font-weight': 'normal',
            color: '#515151',
        }"
        @row-click="handleRowClick"
    >
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column type="expand">
            <template #default="{ row, $index }">
                <div class="batch__expand">
                    <div class="batch__form">
                        <el-form>
                            <el-form-item label="出入库数(批量)">
                                <el-input-number
                                    v-model="row.batchNum"
                                    :max="
                                        storageForm.stockChangeType === 'ALLOCATION_OUTBOUND' || storageForm.stockChangeType === 'OTHER_OUTBOUND'
                                            ? Number(maxRowBatchNum(row))
                                            : 99999
                                    "
                                    :min="0"
                                    :precision="0"
                                    @change="(batchNum) => changeRowBatchNum(row.id, batchNum)"
                                />
                            </el-form-item>
                        </el-form>
                    </div>
                    <el-table
                        :data="row.storageSkus"
                        row-key="id"
                        border
                        style="width: 800px; margin: 0 auto"
                        :header-cell-style="{
                            'background-color': '#F6F8FA',
                            'font-weight': 'normal',
                            color: '#515151',
                        }"
                    >
                        <template #empty> <ElTableEmpty /> </template>
                        <el-table-column label="商品规格">
                            <template #default="{ row: childRow }">{{ childRow?.specs?.join(',') || '单规格' }}</template>
                        </el-table-column>
                        <el-table-column label="库存" prop="stock" width="100">
                            <template #default="{ row: childRow }">
                                {{ childRow?.stockType === 'UNLIMITED' ? '无限库存' : childRow.stock }}
                            </template>
                        </el-table-column>
                        <el-table-column label="出入库数" prop="StorageNum" align="center">
                            <template #default="{ row: childRow }">
                                <el-input-number
                                    v-model="childRow.StorageNum"
                                    :disabled="childRow.stockType === 'UNLIMITED'"
                                    :min="0"
                                    :max="
                                        storageForm.stockChangeType === 'ALLOCATION_OUTBOUND' || storageForm.stockChangeType === 'OTHER_OUTBOUND'
                                            ? Number(childRow.stock)
                                            : 99999
                                    "
                                    :precision="0"
                                    @change="handleChangeStorageNum(row.id)"
                                />
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" fixed="right" width="80">
                            <template #default="{ $index: skuIndex }">
                                <el-link type="danger" @click="handleRemoveSku(skuIndex, $index)">移出</el-link>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="商品名称" width="350">
            <template #default="{ row }">
                <div class="batch__commodity">
                    <img :src="row.albumPics.split(',')[0]" />
                    <span class="batch__commodity--name">{{ row.productName }}</span>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="库存">
            <template #default="{ row }">{{ computedSuplier(row?.storageSkus) }}</template>
        </el-table-column>
        <el-table-column label="出入库数(正整数)" prop="totalStorage" />
        <el-table-column label="操作">
            <template #default="{ $index }">
                <el-link type="danger" @click="handleRemoveBatch($index)">移出</el-link>
            </template>
        </el-table-column>
    </el-table>
    <div style="display: flex; justify-content: center; margin-top: auto; padding: 10px 0; box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.12)">
        <router-link to="/inventory/storage" style="margin-right: 20px"> <el-button round>取消 </el-button></router-link>
        <el-button type="primary" round @click="handleBatchStorage">确定</el-button>
    </div>
    <!-- 选择商品 -->
    <el-dialog v-model="selectComDialog" title="选择商品" :width="900" @close="resetComInfo">
        <el-form ref="form" :model="commodityInfo" label-width="90px">
            <el-row>
                <el-col :span="7">
                    <el-form-item label="商品名称">
                        <el-input v-model="commodityInfo.productName" placeholder="请输入商品名称" maxlength="20"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="商品ID">
                        <el-input v-model="commodityInfo.productId" placeholder="请输入商品ID" maxlength="20"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="销售类型">
                        <el-select v-model="commodityInfo.sellType" placeholder="请选择" style="width: 224px">
                            <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="店铺类目">
                        <el-cascader
                            style="width: 224px"
                            :options="categoryList"
                            :props="shopCascaderProps"
                            placeholder="请选择展示分类"
                            :show-all-levels="false"
                            @change="handleChangeCascader"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="2" :push="1"> <el-button class="from_btn" type="primary" round @click="commoditySearch">搜索</el-button></el-col>
            </el-row>
        </el-form>
        <el-table v-loading="commodityInfo.loading" height="370" :data="commodityInfo.list" @selection-change="handleGoodsTableSelect">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="50" />
            <el-table-column label="商品信息">
                <template #default="{ row }">
                    <div class="flex">
                        <div class="tableCom df">
                            <el-image :src="row.albumPics.split(',')[0]" fit="cover" style="width: 50px; height: 50px; margin-right: 20px" />
                            <div>
                                <div class="f12 color51">{{ row.productName }}</div>
                            </div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="总库存" align="center">
                <template #default="{ row }">
                    <div class="f12 color51">
                        {{ handleComputeStocks(row.storageSkus) }}
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <page-manage
            :page-size="commodityInfo.size"
            :page-num="commodityInfo.current"
            :total="commodityInfo.total"
            @handle-current-change="handleChangeCurrent"
            @handle-size-change="handleChangeSize"
        />
        <template #footer>
            <el-button @click="selectComDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSelectGood">确定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(batch) {
    @include e(commodity) {
        display: flex;
        align-items: center;
        img {
            width: 60px;
            height: 60px;
            flex-shrink: 0;
        }
        span {
            overflow: hidden;
            text-overflow: ellipsis;
            display: box;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-left: 10px;
        }
    }
    @include e(expand) {
        margin-left: 70px;
        margin-right: 70px;
        padding: 10px;
        border: 1px solid #efefef;
    }
    @include e(form) {
        display: flex;
        justify-content: flex-end;
        height: 40px;
        margin-right: 170px;
    }
}
.flex {
    display: flex;
}
</style>
