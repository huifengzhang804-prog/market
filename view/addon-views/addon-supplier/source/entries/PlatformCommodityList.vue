<script setup lang="ts">
import CommoditySearch from '../components/supplier/CommoditySearch.vue'
import CommodityInfo from '../components/supplier/CommodityInfo.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManageTwo from '@/components/PageManage.vue'
import CommodityDetail from '../components/supplier/commodityDetail.vue'
import { usePlatformGoodStatus } from '@/composables/usePlatformGoodStatus'
import { doUpdateSupplierSellStatus, doGetSupplierList, doPostSupplierRestoreSale } from '@/apis/good'
import { useRoute } from 'vue-router'
import { ref, reactive, defineProps } from 'vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { CircleClose } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ElTable, UploadProps, FormInstance } from 'element-plus'
import type { SupplierListInterface, searchFormType } from '../types/supplier'
import { cloneDeep } from 'lodash-es'
import UseConvert from '@/composables/useConvert'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const props = defineProps({
  properties: {
    type: Object,
    default: () => ({}),
  },
})
const VITE_PLATFORM_NAME = props.properties.VITE_PLATFORM_NAME as any

const { divTenThousand } = UseConvert()
const violationTypeMap = {
  PROHIBITED: '违禁品',
  COUNTERFEIT: '假冒伪劣',
  EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
  TITLE_IRREGULARITY: '标题有问题',
  OTHER: '其他',
}
enum SellTypeEnum {
  CONSIGNMENT = '代销商品',
  PURCHASE = '采购商品',
  OWN = '自有商品',
}

const $route = useRoute()
const tabsActive = ref($route.query.name ? String($route.query.name) : '')
const tableSelectedArr = ref<SupplierListInterface[]>([])
const tableList = ref<SupplierListInterface[]>([])
const pageConfig = reactive({
  pageSize: 20,
  pageNum: 1,
  total: 0,
})

const searchParams = ref<searchFormType>({
  platformCategoryId: '',
  sellType: '',
  productType: '',
  status: '',
  shopId: '',
  supplierGoodsName: '',
  sort: '',
})
const dialogFormVisible = ref(false)

// 搜索框组件
const commoditySearchRef = ref<InstanceType<typeof CommoditySearch>>()
const multipleTableRef = ref<InstanceType<typeof ElTable>>()
const dialogStatus = ref(false)

initList()

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

const platform = VITE_PLATFORM_NAME || '启山智软'

const sellOffData = ref({
  violationType: [],
  violationExplain: '',
  violationEvidence: '',
  rummager: platform,
})
const evidence = ref<string[]>([])
/**
 * 删除商品主图
 */
const delImgHandle = (index: number) => {
  evidence.value.splice(index, 1)
  sellOffData.value.violationEvidence = evidence.value.join(',')
}

/**
 * 新增商品主图
 * 当上传图片成功后，将图片的响应信息添加到commodityImgList数组中，
 * 并将该数组转换成字符串，赋值给submitForm.value.albumPics。
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
const handleBtnChange = async (val: string, goods: SupplierListInterface) => {
  if (goods['status'] === val) {
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
        productIds: [sellOffRow.id],
        productViolation: sellOffData.value,
      }
      try {
        const { code, success } = await doUpdateSupplierSellStatus(data, 'PLATFORM_SELL_OFF')
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

const queryReplyToSales = ref()
const resumeSales = async (row: SupplierListInterface) => {
  dialogFormVisible.value = true
  queryReplyToSales.value = { shopId: row.shopId, productId: row.id }
}
const confirmReplyToSales = async () => {
  const { code } = await doPostSupplierRestoreSale(queryReplyToSales.value)
  if (code === 200) {
    ElMessage.success('恢复成功')
    dialogFormVisible.value = false
    initList()
  } else {
    ElMessage.error('恢复失败')
  }
}

const showExplain = async (inputProductViolation: any = {}) => {
  const productViolation = cloneDeep(inputProductViolation)
  productViolation.violationEvidence = productViolation?.violationEvidence ? productViolation.violationEvidence.split(',') : []
  const list: string[] = []
  productViolation?.violationType?.forEach((item: keyof typeof violationTypeMap) => {
    list.push(violationTypeMap[item])
  })
  productViolation.violationType = list.join(',')
  explainData.value = productViolation
  dialogExplain.value = true
}

const handleSizeChange = (val: number) => {
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
async function initList() {
  let params = {
    ...searchParams.value,
    current: pageConfig.pageNum,
    size: pageConfig.pageSize,
  }
  let code = 200
  let success = true
  let data = {
    records: [],
    total: 0,
  }
  // 供应商商品接口
  const { code: goodsCode, data: goodsData, success: goodsSuccess } = await doGetSupplierList({ ...params, supplierProductStatus: tabsActive.value })
  code = goodsCode
  data = goodsData
  success = goodsSuccess
  if (code === 200) {
    tableList.value = data.records
    console.log(tableList.value, 'tableList.value')

    pageConfig.total = data.total
  } else {
    ElMessage.error('获取商品列表失败')
  }
}

const tabChangeHandle = () => {
  initList()
}

const showGoodDetailHandle = (goodItem: SupplierListInterface) => {
  tableSelectedArr.value = [goodItem]
  dialogStatus.value = true
}
const price = (info: any) => {
  const salePrices = info?.salePrices || []
  const maxPrice = Math.max(...salePrices)
  const minPrice = Math.min(...salePrices)
  if (maxPrice === minPrice) {
    return divTenThousand(minPrice).toFixed(2)
  } else {
    return `${divTenThousand(minPrice).toFixed(2)}~${divTenThousand(maxPrice).toFixed(2)}`
  }
}

const sortTableList = () => {
  const values = ['SALE_PRICE_ASC', 'SALE_PRICE_DESC', '']
  const currentIndex = values.indexOf(searchParams.value.sort)
  const nextIndex = (currentIndex + 1) % values.length
  searchParams.value.sort = values[nextIndex]
  initList()
}
</script>

<template>
  <CommoditySearch ref="commoditySearchRef" :tabs-active="tabsActive" @get-search-params="getSearchParams" />
  <div class="tab_container">
    <el-tabs v-model="tabsActive" class="tabs" @tab-change="tabChangeHandle">
      <el-tab-pane label="全部" name=""></el-tab-pane>
      <el-tab-pane label="已上架" name="SELL_ON"></el-tab-pane>
      <el-tab-pane label="已下架" name="SELL_OFF"></el-tab-pane>
      <el-tab-pane label="违规下架" name="PLATFORM_SELL_OFF"></el-tab-pane>
    </el-tabs>
  </div>
  <QTable
    ref="multipleTableRef"
    v-model:checked-item="tableSelectedArr"
    :data="tableList"
    style="margin-top: 10px"
    no-border
    class="q-table"
    @change-sort="sortTableList"
  >
    <template #noData>
      <ElTableEmpty />
    </template>
    <QTableColumn label="商品" prop="goodInfo" align="left" width="350">
      <template #default="{ row }">
        <CommodityInfo :info="row" style="height: 83px" />
      </template>
    </QTableColumn>
    <QTableColumn label="销售价" align="left" width="180">
      <template #default="{ row }">
        <div class="commodity__right--price">￥{{ price(row) }}</div>
      </template>
    </QTableColumn>
    <QTableColumn label="所属供应商" align="left" width="180">
      <template #default="{ row }">
        <el-tooltip v-if="row.supplierName?.length >= 10" class="box-item" effect="dark" :content="row.supplierName" placement="bottom">
          <span :title="row.supplierName" class="shop-name">{{ row.supplierName }}</span>
        </el-tooltip>
        <span v-else :title="row.supplierName" class="shop-name">{{ row.supplierName }}</span>
      </template>
    </QTableColumn>
    <QTableColumn label="销售方式" prop="sellType" align="left" width="170">
      <template #default="{ row }">
        {{ SellTypeEnum[row?.sellType as keyof typeof SellTypeEnum] }}
      </template>
    </QTableColumn>
    <QTableColumn label="商品状态" prop="status" align="left" width="110">
      <template #default="{ row }">
        <span
          :style="{
            color: ['PLATFORM_SELL_OFF'].includes(row?.status)
              ? '#F54319'
              : ['SELL_OFF', 'SUPPLIER_SELL_OFF'].includes(row?.status)
              ? '#999'
              : '#333',
          }"
          >{{ usePlatformGoodStatus(row?.status) }}</span
        >
      </template>
    </QTableColumn>
    <QTableColumn label="操作" align="right" width="200" fixed="right">
      <template #default="{ row }">
        <div v-if="row?.status === 'PLATFORM_SELL_OFF'">
          <el-link :underline="false" type="primary" round @click="showGoodDetailHandle(row)">查看</el-link>
          <el-link :underline="false" type="primary" round style="margin-left: 15px" @click="resumeSales(row)"> 恢复销售 </el-link>
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
    </QTableColumn>
  </QTable>
  <PageManageTwo
    class="pagination"
    :page-size="pageConfig.pageSize"
    :page-num="pageConfig.pageNum"
    :total="pageConfig.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="dialogStatus" title="商品详情" width="60%" top="5vh" destroy-on-close @close="dialogStatus = false">
    <CommodityDetail :commodity-id="tableSelectedArr?.[0]?.id" :shop-id="tableSelectedArr?.[0]?.shopId" />
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

  <el-dialog v-model="dialogSellOff" title="违规下架" width="650px" center destroy-on-close @close="closeSellOffDialog">
    <el-form ref="formSellOffRef" :model="sellOffData" label-width="120px" :rules="rules">
      <el-form-item label="类型（多选）" prop="violationType" class="first-rules">
        <el-checkbox-group v-model="sellOffData.violationType">
          <el-checkbox-button v-for="(violationType, violationTypeKey) in violationTypeMap" :key="violationTypeKey" :label="violationTypeKey">
            {{ violationType }}
          </el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="违规说明" prop="violationExplain">
        <el-input v-model="sellOffData.violationExplain" style="width: 450px; margin-left: 28px" :maxlength="50" placeholder="只限50字"></el-input>
      </el-form-item>
      <el-form-item label="相关证据" prop="violationEvidence">
        <div style="margin-left: 28px">
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
                ><circle-close
              /></el-icon>
            </div>

            <q-upload
              v-show="evidence.length < 5"
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
  <el-dialog v-model="dialogExplain" title="违规原因" width="500px" center @close="dialogSellOff = false">
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
</template>

<style scoped lang="scss">
* {
  font-size: 14px;
}
@include b(q-table) {
  overflow: auto;
  transition: height 0.5s;
}

@include b(shop-name) {
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
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: contain;
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
  flex-wrap: wrap;
}
:deep(.el-form-item__error) {
  padding: 6px 26px;
}

.first-rules {
  margin-left: 28px;
  :deep(.el-form-item__error) {
    padding: 5px 0px !important;
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
    width: 80px;
    text-align: right;
    margin-right: 8px;
  }
}
</style>
