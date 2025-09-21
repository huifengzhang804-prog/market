<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import useConvert from '@/composables/useConvert'
import type { RetrieveParam } from '@/apis/good/model'
import type { FormInstance } from 'element-plus'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import { Long, objectSpanMethod, SeckillActivityProductDTO, GoodsListItem, getSpanId } from '../index'
import { doPostAddonSeckill, doGetSeckillInfo, doGetSeckillRound } from '../apis'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import type { SeckillActivityDTO, ApiEditSecondSKill, RoundVO } from '../index'
import 'uno.css'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'

type SearchConfig = 'maxPrice' | 'minPrice' | 'activity' | 'keyword' | 'categoryFirstId'
const loading = ref(false)
const router = useRouter()
//秒杀活动 id,查看时有值
const query = useRoute().query
const secKillId = query.secKillId as string
const shopId = query.shopId as string
//是否禁止输入修改表单 查看时为 true 新增时为 false
const isDisable = !!secKillId
const { mulTenThousand, divTenThousand } = useConvert()
const ruleFormRef = ref<FormInstance>()
const searchConfig = reactive<Pick<RetrieveParam, SearchConfig>>({
  maxPrice: '',
  minPrice: '',
  activity: {
    endTime: '',
    startTime: '',
  },
  keyword: '',
  categoryFirstId: '',
})
// 活动场次 map
const roundMap = ref(new Map<number, RoundVO>())
//弹出选择商品的模态框
const chooseGoodsPopupShow = ref(false)
//表单数据
const formData = ref<SeckillActivityDTO>({
  name: '',
  date: '',
  round: null,
  payTimeout: 15,
  stackable: {
    vip: false,
    coupon: false,
    full: false,
  },
  products: [],
  startTime: '',
  endTime: '',
})
//表单校验规则
const rules = reactive({
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  date: [{ required: false, message: '请选择活动日期', trigger: ['blur', 'change'] }],
  round: [{ required: false, message: '请选择活动时段', trigger: ['blur', 'change'] }],
  payTimeout: [{ required: true, message: '请输入订单关闭时间', trigger: 'blur' }],
})

//表格渲染数据
const goodsData = ref<GoodsListItem[]>([])

onMounted(() => {
  initSecKill()
})

//监听日期选择
watch(
  () => formData.value.date,
  (date) => {
    if (isDisable) {
      return
    }
    formData.value.round = null
    doGetSeckillRound({ date }).then(({ data }) => {
      const map = new Map<number, RoundVO>()
      ;(data as RoundVO[]).forEach((item) => {
        map.set(item.round, item)
      })
      roundMap.value = map
    })
  },
)
watch(
  () => goodsData.value,
  (products) => getSpanId(products),
)
/**
 * 表单提交
 */
const handleSubmit = async (formEl: FormInstance | undefined) => {
  loading.value = true
  try {
    if (isDisable) {
      // 状态只能查看
      router.back()
      return
    }
    if (!formEl) return
    await formEl.validate()

    if (!validate()) return
    const productMap = new Map<Long, SeckillActivityProductDTO>()
    const products: SeckillActivityProductDTO[] = []
    goodsData.value.forEach((good) => {
      let curProduct = productMap.get(good.productId)
      if (!curProduct) {
        curProduct = {
          id: good.productId,
          skus: [],
        }
        productMap.set(curProduct.id, curProduct)
        products.push(curProduct)
      }
      const sku = good.sku
      curProduct.skus.push({
        id: sku.skuId,
        stock: sku.seckillStock,
        specs: (sku.skuName || []).join(','),
        price: mulTenThousand(sku.seckillPrice),
      })
    })
    const form = formData.value
    form.products = products
    const { code, msg } = await doPostAddonSeckill(form)
    if (code !== 200) {
      ElMessage.error(msg)
      loading.value = false
      return
    }
    loading.value = false
    ElMessage.success('保存成功')
    router.push({
      path: '/marketingApp/secondsKill',
      query: {
        flag: true,
      },
    })
  } finally {
    loading.value = false
  }
}

function validate() {
  if (!formData.value.payTimeout) {
    ElMessage.info('订单关闭时间3-360分钟')
    return false
  }
  if (!goodsData.value.length) {
    ElMessage.info('请选择适用商品')
    return false
  }
  if (!goodsData.value.every((item) => item.sku.seckillPrice)) {
    ElMessage.info('请输入秒杀价')
    return false
  }
  if (!goodsData.value.every((item) => item.sku.seckillStock)) {
    ElMessage.info('请输入秒杀库存')
    return false
  }
  return true
}

// 编辑逻辑 s
/**
 * 获取活动信息和商品信息回显
 */
async function initSecKill() {
  if (!secKillId) {
    return
  }
  const { code, data } = await doGetSeckillInfo({
    shopId,
    activityId: secKillId,
  })
  if (code !== 200) {
    ElMessage.error('获取秒杀信息失败')
    return
  }

  const { name, date, startTime, endTime, payTimeout, stackable, products } = data as ApiEditSecondSKill
  formData.value = {
    name,
    date,
    round: 0,
    payTimeout,
    stackable,
    products,
    startTime,
    endTime,
  }
  const goods: GoodsListItem[] = []
  products.forEach((product) => {
    product.skus?.forEach((sku) => {
      goods.push({
        productId: product.id,
        productName: product.name,
        productPic: product.image,
        sku: {
          skuId: sku.id,
          skuName: sku.specs?.split(','),
          seckillPrice: rulePrice(sku.price),
          seckillStock: sku.stock,
          actualPaidPrice: '',
        },
      })
    })
  })
  goodsData.value = goods
  console.log(goods)
}

// 编辑逻辑 e
/**
 * 选择商品做数据处理
 */
const handleConfirm = (e: ChoosedGoodCallBack) => {
  const newArr: GoodsListItem[] = []
  for (let index = 0; index < e.tempGoods.length; index++) {
    const item = e.tempGoods[index]
    const { productId, productName, pic, shopId, stocks, salePrices, stockTypes, specs } = item
    if (item?.skuIds?.length) {
      for (let j = 0; j < item.skuIds.length; j++) {
        const skuId = item.skuIds[j]
        const skuPrice = salePrices[j]
        const skuStock = stocks[j]
        const stockType = stockTypes[j]
        const skuName = specs[j].split(' ')
        if ((skuStock > 0 && stockType === 'LIMITED') || stockType === 'UNLIMITED') {
          newArr.push({
            sku: {
              seckillPrice: 0.01,
              seckillStock: 1,
              seckillLimit: 0,
              actualPaidPrice: 0,
              productId,
              skuId,
              skuPrice,
              skuStock,
              stockType,
              skuName,
            },
            productId,
            productName,
            productPic: pic,
            shopId,
          })
        }
      }
    }
  }
  if (newArr?.length === 0) {
    return ElMessage.error('请至少选择一个存在库存的商品')
  }
  // // 列表渲染前合并单元格
  goodsData.value = newArr
  chooseGoodsPopupShow.value = false
}
// 时间处理 e
/**
 * 筛选商品
 */
const handleChooseGoods = () => {
  const form = formData.value
  if (!form.date || form.round === null) {
    return ElMessage.warning('请选择活动时间')
  }
  const round = roundMap.value.get(form.round) as any
  // 查询活动需把活动时段传入供后端筛选商品
  const date = form.date
  searchConfig.activity.startTime = `${date} ${round.startTime}`
  searchConfig.activity.endTime = `${date} ${round.endTime}`
  chooseGoodsPopupShow.value = true
  return
}
const ruleStock = (stockType, skuStock) => {
  return stockType === 'UNLIMITED' ? 100000 : skuStock
}
const dialogVisible = ref(false)
const currentBatchSetProductId = ref('0')
const batchSet = ref({
  stock: 1,
  price: 0.01,
})
const handleClose = () => {
  batchSet.value = { stock: 1, price: 0.01 }
  dialogVisible.value = false
}

const rulePrice = (skuPrice) => {
  return divTenThousand(skuPrice).toNumber()
}

const submitBatch = () => {
  goodsData.value.forEach((item) => {
    if (item.productId === currentBatchSetProductId.value) {
      const { stockType, skuPrice, skuStock } = item.sku
      item.sku.seckillStock = batchSet.value.stock > ruleStock(stockType, skuStock) ? ruleStock(stockType, skuStock) : batchSet.value.stock
      item.sku.seckillPrice = batchSet.value.price > rulePrice(skuPrice) ? rulePrice(skuPrice) : batchSet.value.price
    }
  })
  dialogVisible.value = false
}
const handleBacthSetClick = (row: GoodsListItem) => {
  currentBatchSetProductId.value = row.productId as string
  dialogVisible.value = true
}

const handleDelGoods = async (productId: Long, skuId: Long) => {
  const isValidate = await ElMessageBox.confirm('确定移除该商品?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (isValidate) {
    const goodsIndex = goodsData.value.findIndex((item) => productId === item.productId && skuId === item.sku.skuId)
    if (goodsIndex !== -1) {
      goodsData.value.splice(goodsIndex, 1)
      getSpanId(goodsData.value)
    }
  }
}
// 时间处理 s
const disabledDate = (time: string) => {
  if (new Date(time).toLocaleDateString() === new Date().toLocaleDateString()) {
    return false
  }
  return new Date(time).getTime() < new Date().getTime()
}
</script>

<template>
  <div class="position_container">
    <h1 class="title">{{ isDisable ? '基本信息' : '新增秒杀' }}</h1>
    <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="auto" class="f1">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model.trim="formData.name" :disabled="isDisable" :maxlength="10" :minlength="3" placeholder="请输入活动名称" style="width: 60%" />
        <span class="msg">活动名称不超过10个字</span>
      </el-form-item>
      <el-form-item label="活动日期" prop="date" required>
        <el-date-picker
          v-model="formData.date"
          :disabled="isDisable"
          :disabled-date="disabledDate"
          format="YYYY-MM-DD"
          placeholder="请选择日期"
          type="date"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="活动时段" prop="round" required>
        <p v-if="isDisable">
          {{ formData.startTime + ' - ' + formData.endTime }}
        </p>
        <el-select v-else v-model="formData.round" placeholder="请选择选择时段" style="width: 240px">
          <el-option v-for="item in roundMap.values()" :key="item.round" :label="item.startTime + ' - ' + item.endTime" :value="item.round" />
        </el-select>
      </el-form-item>
      <el-form-item label="未支付订单" prop="payTimeout" required>
        <el-input-number v-model="formData.payTimeout" :controls="false" :disabled="isDisable" :max="360" :min="3" style="width: 25%" />
        <span class="msg"><span style="color: #000; margin-right: 20px">分钟后关闭订单</span> 可输入3-360分钟</span>
      </el-form-item>
      <el-form-item label="优惠叠加" prop="stackable">
        <el-checkbox v-model="formData.stackable.vip" :disabled="isDisable" label="会员价" />
        <el-checkbox v-model="formData.stackable.coupon" :disabled="isDisable" label="优惠券" />
        <el-checkbox v-model="formData.stackable.full" :disabled="isDisable" label="满减" />
        <span class="msg">买家下单时仅可用与勾选的活动类型同时使用</span>
      </el-form-item>
      <el-form-item label="适用商品" required>
        <el-row style="width: 100%">
          <el-link :disabled="isDisable" :underline="false" type="primary" @click="handleChooseGoods">选择商品 </el-link>
        </el-row>
        <div v-show="goodsData.length" class="goods-list">
          <el-table
            :cell-style="{ height: '80px' }"
            :data="goodsData"
            :header-cell-style="{
              fontSize: '14px',
              color: '#606266',
              background: '#f2f2f2',
              height: '54px',
              fontWeight: 'normal',
            }"
            :span-method="objectSpanMethod"
            border
            height="100%"
          >
            <el-table-column label="商品信息">
              <template #default="{ row }: { row: GoodsListItem }">
                <div class="goods-list__info">
                  <div style="width: 60px; height: 60px">
                    <el-image
                      :preview-src-list="[row.productPic]"
                      :preview-teleported="true"
                      :src="row.productPic"
                      fit="cover"
                      style="width: 60px; height: 60px"
                    />
                  </div>
                  <div class="goods-list__goods-list__info-name">
                    <el-button v-show="!isDisable" :disabled="isDisable" link type="primary" @click="handleBacthSetClick(row)">批量设置 </el-button>
                    <div class="goods-list__goods-list__info-name--name">
                      {{ row.productName }}
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="规格" width="100px">
              <template #default="{ row }: { row: GoodsListItem }">
                <div class="table-msg">
                  {{ row.sku.skuName?.join('') || '-' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="秒杀价（元）" width="120px">
              <template #default="{ row }: { row: GoodsListItem }">
                <el-input-number
                  v-model="row.sku.seckillPrice"
                  :controls="false"
                  :disabled="isDisable"
                  :max="row.sku.skuPrice && rulePrice(row.sku.skuPrice)"
                  :min="0.01"
                  :precision="2"
                  style="width: 80%"
                />
                <div v-if="!isDisable" class="table-msg">销售价：￥{{ rulePrice(row.sku.skuPrice) }}</div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="秒杀库存" width="120px">
              <template #default="{ row }: { row: GoodsListItem }">
                <el-input-number
                  v-model="row.sku.seckillStock"
                  :controls="false"
                  :disabled="isDisable"
                  :max="row.sku.stockType && ruleStock(row.sku.stockType, row.sku.skuStock)"
                  :min="0"
                  :precision="0"
                  style="width: 80%"
                />
                <div v-if="!isDisable" class="table-msg">
                  {{ row.sku.stockType === 'UNLIMITED' ? '无限库存' : `${'库存' + row.sku.skuStock}` }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80px">
              <template #default="{ row }: { row: GoodsListItem }">
                <el-link :disabled="isDisable" :underline="false" type="primary" @click="handleDelGoods(row.productId, row.sku.skuId)">
                  删除
                </el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form-item>
    </el-form>
    <div v-if="isDisable" class="nav-button">
      <el-button plain round @click="router.back()">返回</el-button>
    </div>
    <div v-else class="nav-button">
      <el-button plain round @click="router.back()">取消</el-button>
      <QSafeBtn :loading="loading" round type="primary" @click="handleSubmit(ruleFormRef)">确定 </QSafeBtn>
    </div>
    <!-- 选择商品弹出 s-->
    <QChooseGoodsPopup
      v-model="chooseGoodsPopupShow"
      v-model:searchParam="searchConfig"
      :pointGoodsList="goodsData"
      :searchConsignmentProduct="true"
      @onConfirm="handleConfirm"
    />
    <!-- 选择商品弹出 e-->
    <!-- 批量处理 s-->
    <el-dialog v-model="dialogVisible" center destroy-on-close title="批量设置" top="30vh" width="500" @close="handleClose">
      <div class="flex">
        秒杀价
        <el-input-number v-model="batchSet.price" :controls="false" :min="0.01" :precision="2" @change="handleChange" />
        库存
        <el-input-number v-model="batchSet.stock" :controls="false" :min="0" :precision="0" @change="handleChange" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <QSafeBtn type="primary" @click="submitBatch"> 确定</QSafeBtn>
        </div>
      </template>
    </el-dialog>
    <!-- 批量处理 e-->
  </div>
</template>

<style lang="scss" scoped>
@include b(title) {
  font-size: 14px;
  color: #323233;
  font-weight: 700;
  margin-bottom: 30px;
  text-align: center;
}

@include b(msg) {
  padding-left: 15px;
  font-size: 12px;
  color: #c4c4c4;
}

.position_container {
  scrollbar-width: none;
  -ms-overflow-style: none;
  height: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: scroll;
  padding: 30px 40px;
  padding-bottom: 0;
  &::-webkit-scrollbar {
    display: none;
  }
}

@include b(nav-button) {
  position: sticky;
  bottom: 0;
  padding: 15px 0px;
  justify-content: center;
  box-shadow: 0 0px 10px 0px #d5d5d5;
  background-color: white;
  z-index: 999;
  margin-top: auto;
  width: calc(100% + 80px);
  transform: translateX(-40px);
  display: flex;
  justify-content: center;
}

@include b(goods-list) {
  width: 100%;
  height: 500px;
  overflow-x: scroll;
  @include e(info) {
    width: 100%;
    display: flex;
  }
  @include e(goods-list__info-name) {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    padding: 0 16px;
    @include m(name) {
      width: 260px;
      font-size: 14px;
      @include utils-ellipsis(1);
    }
    @include m(price) {
      font-size: 14px;
      text-align: LEFT;
      color: #f12f22;
      &::before {
        content: '￥';
        font-size: 12px;
        text-align: LEFT;
        color: #f12f22;
      }
    }
  }
}

@include b(table-msg) {
  font-size: 12px;
  color: #838383;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
