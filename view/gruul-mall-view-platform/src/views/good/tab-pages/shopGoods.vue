<template>
    <el-config-provider :empty-values="[undefined, null]">
        <CommoditySearch ref="commoditySearchRef" :tabs-active="tabsActive" @getSearchParams="getSearchParams" />
    </el-config-provider>

    <div class="tab_container">
        <el-tabs v-model="tabsActive" class="tabs" @tab-change="tabChangeHandle">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="已上架" name="SELL_ON"></el-tab-pane>
            <el-tab-pane label="已下架" name="SELL_OFF"></el-tab-pane>
            <el-tab-pane label="违规下架" name="PLATFORM_SELL_OFF"></el-tab-pane>
        </el-tabs>
    </div>

    <q-table v-model:checked-item="tableSelectedArr" :data="tableList" class="q-table" no-border @change-sort="sortTableList">
        <template #noData>
            <div class="no_data">
                <img src="@/assets/image/no_shop.png" alt="" />
                <p class="cont">暂无商品</p>
            </div>
        </template>
        <q-table-column label="商品" prop="goodInfo" width="300" align="left">
            <template #default="{ row }">
                <CommodityInfo :info="row" :provider="false" style="height: 83px; display: flex; align-items: center; box-sizing: border-box" />
            </template>
        </q-table-column>
        <q-table-column label="销售价" align="left" width="150">
            <template #default="{ row }">
                <div v-if="row.storageSkus" style="color: #ff7417">￥{{ salePriceRange(row.storageSkus) }}</div>
                <div v-else style="color: #999">—</div>
            </template>
        </q-table-column>
        <q-table-column label="店铺名称" align="left" width="150">
            <template #default="{ row }">
                <el-tooltip v-if="row.shopName?.length >= 10" class="box-item" effect="dark" :content="row.shopName" placement="top-start">
                    <span class="shop-name">{{ row.shopName }}</span>
                </el-tooltip>
                <span v-else class="shop-name">{{ row.shopName }}</span>
            </template>
        </q-table-column>
        <q-table-column label="商品来源" prop="sellType" align="left" width="130">
            <template #default="{ row }">
                {{ SellTypeEnum[row?.sellType as keyof typeof SellTypeEnum] }}
            </template>
        </q-table-column>
        <q-table-column label="商品状态" prop="status" align="left" width="110">
            <template #default="{ row }">
                <span
                    :style="{
                        color: ['PLATFORM_SELL_OFF'].includes(row?.productStatus)
                            ? '#F54319'
                            : ['SELL_OFF', 'SUPPLIER_SELL_OFF'].includes(row?.productStatus)
                            ? '#999'
                            : '#333',
                    }"
                    >{{ usePlatformGoodStatus(row?.productStatus) }}</span
                >
            </template>
        </q-table-column>
        <q-table-column label="操作" align="right" width="200" fixed="right">
            <template #default="{ row }">
                <div v-if="row?.productStatus === 'PLATFORM_SELL_OFF'">
                    <el-link :underline="false" type="primary" round @click="showGoodDetailHandle(row)">查看</el-link>
                    <el-link :underline="false" type="primary" style="margin-left: 15px" round @click="replyToSales(row)">恢复销售</el-link>
                    <el-link :underline="false" type="primary" round style="margin-left: 15px" @click="showExplain(row?.extra?.productViolation)">
                        违规原因
                    </el-link>
                </div>
                <div v-else>
                    <el-link :underline="false" type="primary" round @click="showGoodDetailHandle(row)">查看</el-link>
                    <el-link :underline="false" type="danger" round style="margin-left: 15px" @click="handleBtnChange('PLATFORM_SELL_OFF', row)">
                        违规下架
                    </el-link>
                </div>
            </template>
        </q-table-column>
    </q-table>

    <PageManage
        :page-size="pageConfig.pageSize"
        :page-num="pageConfig.pageNum"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />

    <el-dialog v-model="dialogStatus" title="商品详情" width="60%" destroy-on-close top="5vh" @close="dialogStatus = false">
        <CommodityDetail :commodityId="tableSelectedArr?.[0].id" :shopId="tableSelectedArr?.[0].shopId" />
    </el-dialog>

    <el-dialog v-model="dialogSellOff" title="违规下架" width="650px" destroy-on-close center @close="closeSellOffDialog">
        <el-form ref="formSellOffRef" :model="sellOffData" label-width="120px" :rules="rules" label-position="left" style="padding-left: 20px">
            <el-form-item label="类型（多选）" prop="violationType" class="first-rules">
                <el-checkbox-group v-model="sellOffData.violationType">
                    <el-checkbox-button
                        v-for="(violationType, violationTypeKey) in violationTypeMap"
                        :key="violationTypeKey"
                        :label="violationTypeKey"
                    >
                        {{ violationType }}
                    </el-checkbox-button>
                </el-checkbox-group>
            </el-form-item>
            <el-form-item label="违规说明" prop="violationExplain">
                <el-input v-model="sellOffData.violationExplain" style="width: 450px" placeholder="限50个字" :maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="相关证据" prop="violationEvidence">
                <div>
                    <div class="notification">最多5张图片(jpg、gif、jpeg、png、webp、bmp)，单个图片1MB以内</div>
                    <div class="evidence">
                        <div v-for="(item, index) in evidence" :key="index" style="position: relative">
                            <q-upload
                                v-model:src="evidence[index]"
                                :width="80"
                                :height="80"
                                :format="{
                                    size: 1,
                                    types: ['image/png', 'image/jpg', 'image/jpeg', 'image/gif', 'image/webp', 'image/bmp'],
                                    width: 80,
                                    height: 80,
                                    isBeyondLimit: true,
                                }"
                                :is-cropper="false"
                            />
                            <el-icon
                                v-if="item"
                                style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%"
                                color="#7f7f7f"
                                size="20px"
                                @click="delImgHandle(index)"
                                ><i-ep-circle-close
                            /></el-icon>
                        </div>
                        <q-upload
                            v-show="evidence?.length < 5"
                            :width="80"
                            :height="80"
                            :format="{
                                size: 1,
                                types: ['image/png', 'image/jpg', 'image/jpeg', 'image/gif', 'image/webp', 'image/bmp'],
                                width: 1000,
                                height: 1000,
                                isBeyondLimit: true,
                            }"
                            :is-cropper="false"
                            @change="addNewMainSuccess"
                        />
                    </div>
                </div>
            </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogSellOff = false">取消</el-button>
                <el-button type="primary" @click="handleSellOff"> 确定 </el-button>
            </span>
        </template>
    </el-dialog>

    <el-dialog v-model="dialogExplain" title="违规原因" width="690px" destroy-on-close @close="dialogSellOff = false">
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

    <el-dialog v-model="dialogFormVisible" title="恢复销售" width="500" center>
        <div>请确认是否将商品重置为【下架】状态，商家整改后可正常销售商品！！！</div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmReplyToSales"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import CommoditySearch from '../components/shop/CommoditySearch.vue'
import CommodityInfo from '../components/shop/CommodityInfo.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import CommodityDetail from '../components/shop/commodityDetail.vue'
import { usePlatformGoodStatus } from '@/composables/usePlatformGoodStatus'
import { doGetProductList, doUpdateSellStatus, doPostShopRestoreSale } from '@/apis/good'
import { ElMessage } from 'element-plus'
import type { ElTable, UploadProps, FormInstance } from 'element-plus'
import type { searchFormType } from '../types'
import type { ProductItem } from '@/apis/good/types/productList'

enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}
const violationTypeMap = {
    PROHIBITED: '违禁品',
    COUNTERFEIT: '假冒伪劣',
    EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
    TITLE_IRREGULARITY: '标题有问题',
    OTHER: '其他',
}

const $route = useRoute()
const tabsActive = ref($route.query.name ? String($route.query.name) : '')
const tableSelectedArr = ref<ProductItem[]>([])
const dialogFormVisible = ref(false)
const tableList = ref<ProductItem[]>([])
const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
const searchParams = ref<searchFormType>({
    platformCategoryId: '',
    name: '',
    createBeginTime: '',
    createEndTime: '',
    date: '',
    sellType: '',
    productType: '',
    status: '',
    shopId: '',
})
// 搜索框组件
const commoditySearchRef = ref<InstanceType<typeof CommoditySearch>>()
const dialogStatus = ref(false)

onMounted(() => {
    initList()
})
/**
 * 操作按钮上下架
 */
const formSellOffRef = ref<FormInstance>()
const rules = {
    violationType: [{ required: true, message: '类型为必选项', trigger: 'change' }],
    violationExplain: [{ required: true, message: '违规说明为必填项', trigger: 'blur' }],
    violationEvidence: [{ required: true, message: '相关证据为必选项', trigger: 'change' }],
}

const dialogSellOff = ref(false)

const platform = import.meta.env.VITE_PLATFORM_NAME ? import.meta.env.VITE_PLATFORM_NAME : '启山智软'

const sellOffData = ref({
    violationType: [],
    violationExplain: '',
    violationEvidence: '',
    rummager: platform,
})
const evidence = ref<string[]>([])
const sortState = ref('')
/**
 * 删除商品主图
 * @param {number} index
 */
const delImgHandle = (index: number) => {
    evidence.value.splice(index, 1)
    sellOffData.value.violationEvidence = evidence.value.join(',')
}
// 价钱排序
const sortTableList = () => {
    const values = ['SALE_PRICE_ASC', 'SALE_PRICE_DESC', '']
    const currentIndex = values.indexOf(sortState.value)
    const nextIndex = (currentIndex + 1) % values.length
    sortState.value = values[nextIndex]
    initList(sortState.value)
}
const salePriceRange = computed(() => (storageSkus: any[] = []) => {
    const min = Math.min(...storageSkus.map((item) => parseInt(item.salePrice))) / 10000
    const max = Math.max(...storageSkus.map((item) => parseInt(item.salePrice))) / 10000
    if (max === min) {
        return max.toFixed(2)
    } else {
        return `${min.toFixed(2)} ~￥${max.toFixed(2)}`
    }
})
/**
 * 新增商品主图
 * 当上传图片成功后，将图片的响应信息添加到commodityImgList数组中，
 * 并将该数组转换成字符串，赋值给submitForm.value.albumPics。
 * @returns {UploadProps}
 */
const addNewMainSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    evidence.value.push(response)
    sellOffData.value.violationEvidence = evidence.value.join(',')
}
// 下架状态
const commandList = reactive([
    {
        label: 'PLATFORM_SELL_OFF',
        name: '下架',
    },
])
// 点击下架商品
let sellOffRow: any = {}
const handleBtnChange = async (val: string, goods: ProductItem) => {
    if (goods?.productStatus === val) {
        const type = commandList.find((item) => val)?.name
        ElMessage.error(`该商品已${type}`)
        return
    }
    sellOffRow = goods
    dialogSellOff.value = true
}

// 确定下架
const handleSellOff = async () => {
    formSellOffRef.value?.validate(async (valid) => {
        if (valid) {
            const data = {
                // productIds: [sellOffRow.id],
                keys: [{ shopId: sellOffRow.shopId, productId: sellOffRow.id }],
                productViolation: sellOffData.value,
            }
            try {
                const { code, success } = await doUpdateSellStatus(data, 'PLATFORM_SELL_OFF')
                if (code === 200 && success) {
                    ElMessage.success('更新成功')
                    initList()
                    dialogSellOff.value = false
                } else {
                    ElMessage.error('更新失败')
                }
            } catch (error) {
                return
            }
        }
    })
}
const closeSellOffDialog = () => {
    sellOffData.value = {
        violationType: [],
        violationExplain: '',
        violationEvidence: '',
        rummager: platform,
    }
    evidence.value = []
    dialogSellOff.value = false
}
// 违规弹框
const dialogExplain = ref(false)
// 违规信息
const explainData = ref({ rummager: '', violationType: '', violationExplain: '', violationEvidence: [], examineDateTime: '' })
// 对应数据
const explainMap = {
    rummager: '检查员',
    examineDateTime: '检查时间',
    violationType: '类型',
    violationExplain: '原因',
    violationEvidence: '相关证据',
}
const showExplain = async (productViolation: any = {}) => {
    const list: string[] = []
    productViolation?.violationType?.forEach((item: keyof typeof violationTypeMap) => {
        list.push(violationTypeMap[item])
    })
    Object.keys(explainData.value).forEach((key) => {
        // @ts-ignore
        explainData.value[key] = productViolation?.[key]
    })
    explainData.value.violationEvidence = productViolation?.violationEvidence ? productViolation.violationEvidence.split(',') : []
    explainData.value.violationType = list.join(',')
    dialogExplain.value = true
}

const handleSizeChange = (val: number) => {
    pageConfig.pageNum = 1
    pageConfig.pageSize = val
    initList()
}
const handleCurrentChange = (val: number) => {
    pageConfig.pageNum = val
    initList()
}
const getSearchParams = (params: searchFormType) => {
    searchParams.value = params
    initList()
}

// 获取店铺商品/获取供应商商品
async function initList(sort?: string) {
    let params = {
        ...searchParams.value,
        current: pageConfig.pageNum,
        size: pageConfig.pageSize,
        sort: sort,
    }
    // 店铺商品接口
    let tableListData: ProductItem[] = [],
        total = 0
    try {
        const { code, data, success } = await doGetProductList({ ...params, status: tabsActive.value })
        if (code === 200 && success) {
            tableListData = data.records
            total = data.total
        } else {
            ElMessage.error('获取商品列表失败')
        }
    } catch (err) {
        console.error('gruul-mall-addon-platform/platform/product/get/all', err)
    } finally {
        tableList.value = tableListData
        pageConfig.total = Number(total)
    }
}

const tabChangeHandle = () => {
    initList()
}

const showGoodDetailHandle = (goodItem: ProductItem) => {
    tableSelectedArr.value = [goodItem]
    dialogStatus.value = true
}
const queryReplyToSales = ref()
const replyToSales = (row: ProductItem) => {
    dialogFormVisible.value = true
    queryReplyToSales.value = { shopId: row.shopId, productId: row.id }
}
const confirmReplyToSales = async () => {
    const { code } = await doPostShopRestoreSale(queryReplyToSales.value)
    if (code === 200) {
        ElMessage.success('恢复成功')
        dialogFormVisible.value = false
        initList()
    } else {
        ElMessage.error('恢复失败')
    }
}
</script>

<style scoped lang="scss">
* {
    font-size: 14px !important;
}
@include b(q-table) {
    overflow: auto;
    transition: height 0.5s;
}

@include b(shop-name) {
    text-align: center;
    @include utils-ellipsis(1);
}
.see {
    width: 82px;
    height: 36px;
    background: #eaf5fe;
    border-radius: 21px;
    font-size: 12px;
    color: #309af3;
    line-height: 36px;
    text-align: center;
    cursor: pointer;
}

.violation-evidence {
    width: 100px;
    height: 100px;
    vertical-align: top;
}

.violation-evidence + .violation-evidence {
    margin-left: 5px;
}

.notification {
    color: #666;
}

.evidence {
    display: flex;
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

:deep() .el-form-item__error {
    padding: 6px 0;
}

.first-rules:deep() {
    .el-form-item__error {
        padding: 5px 0px !important;
    }
}
:deep() {
    .el-form--label-left .el-form-item__label {
        margin-left: 20px;
    }
}
:deep() {
    .el-form el-form--default el-form--label-left {
        padding-left: 20px;
    }
}
</style>
