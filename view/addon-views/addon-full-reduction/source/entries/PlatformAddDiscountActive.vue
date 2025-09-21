<script lang="ts" setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fullReductionRuleCn, AddDiscountActiveParams, FullReductionRules } from '../index'
import { doGetApplyDiscountById } from '../apis'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import type { FormInstance } from 'element-plus'
import type { GoodsListItem } from '@/components/q-choose-goods-popup/types'

const $router = useRouter()
const $route = useRoute()
const shopId = $route.query.shopId
const id = $route.query.id
const disabled = true
const chooseGoodsList = ref()
const ruleFormRef = ref<FormInstance>()
const { divTenThousand } = useConvert()

const form = ref<AddDiscountActiveParams>({
  id: null,
  name: '',
  time: {
    start: '',
    end: '',
  },
  rules: [],
  productType: 'ALL_PRODUCT',
  productIds: [],
})

initActive()
/**
 * 编辑获取活动信息
 */
async function initActive() {
  if (!$route.query.id) return
  const { code, data, msg } = await doGetApplyDiscountById({ shopId, id })
  if (code !== 200) {
    ElMessage.error(msg || '获取活动信息失败')
    return
  }
  data.rules = paramsFormatRules(data.rules, priceApiFormat)
  chooseGoodsList.value = data.products.map((product) => ({
    productId: product.id,
    productName: product.name,
    pic: product.image,
  }))
  delete data.products
  form.value = data
}

/**
 * 接口价格转毫
 */
function priceApiFormat(price?: number) {
  return price ? divTenThousand(price).toNumber() : 0
}

function paramsFormatRules(arr: FullReductionRules[], priceFormat: (val?: number) => number) {
  // 1. 遍历 fullReductionRules
  return arr.map((item) => {
    // 2. 解构出 type, conditionAmount, discountAmount, discountRatio
    const { type, conditionAmount, discountAmount, discountRatio } = item
    // 3. 判断 type 是否为 FULL_DISCOUNT
    if (item.type === 'FULL_DISCOUNT') {
      // 4. 是, 返回 { type, conditionAmount, discountRatio }

      return { type, conditionAmount: priceFormat(conditionAmount), discountRatio }
    }
    // 5. 判断 type 是否为 FULL_REDUCTION
    if (item.type === 'FULL_REDUCTION') {
      // 6. 是, 返回 { type, conditionAmount, discountAmount }
      return {
        type,
        conditionAmount: priceFormat(conditionAmount),
        discountAmount: priceFormat(discountAmount),
      }
    }
    // 7. 不是, 返回 item
    return item
  })
}
</script>

<template>
  <div class="q_plugin_container">
    <div class="add" style="padding: 40px">
      <h1 class="title">基本信息</h1>
      <el-form ref="ruleFormRef" :inline-message="false" :model="form" label-width="auto">
        <el-form-item label="活动名称">
          <el-input v-model.trim="form.name" :disabled="disabled" maxlength="10" placeholder="请输入活动名称" style="width: 551px" />
          <span class="msg">活动名称不超过10个字</span>
        </el-form-item>
        <el-form-item label="活动时间" required>
          <div class="ruleform-date">
            <el-form-item :inline-message="false">
              <el-date-picker
                v-model="form.time.start"
                :disabled="disabled"
                format="YYYY/MM/DD HH:mm:ss"
                placeholder="请选择开始时间"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
              <span style="margin: 0 10px">至</span>
            </el-form-item>
            <el-form-item :inline-message="false">
              <el-date-picker
                v-model="form.time.end"
                :disabled="disabled"
                format="YYYY/MM/DD HH:mm:ss"
                placeholder="请选择结束时间"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </div>
        </el-form-item>
        <el-form-item label="活动规则">
          <el-table
            :cell-style="{ height: '60px' }"
            :data="form.rules"
            :header-cell-style="{ textAlign: 'center', fontSize: '14px', color: '#606266' }"
            border
            style="width: 80%"
          >
            <el-table-column label="满减条件" width="170">
              <template #default="{ row }: { row: FullReductionRules }">
                <el-form-item>
                  <el-select v-model="row.type" :disabled="disabled" placeholder="全部类型">
                    <el-option v-for="item in fullReductionRuleCn" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="满减规则">
              <template #default="{ row }: { row: FullReductionRules }">
                <template v-if="row.type === 'FULL_DISCOUNT'">
                  <div class="fcenter" style="width: 100%">
                    <el-form-item label-width="0%">
                      <span>满</span>
                      <el-input-number
                        v-model.number="row.conditionAmount"
                        :controls="false"
                        :disabled="disabled"
                        :max="99999"
                        :min="1"
                        :precision="2"
                        style="width: 60%; margin: 0 5px"
                      />
                      <span>元,打</span>
                    </el-form-item>
                    <el-form-item label-width="0%" prop="discountRatio">
                      <el-input-number
                        v-model.number="row.discountRatio"
                        :controls="false"
                        :disabled="disabled"
                        :max="9.9"
                        :min="0.1"
                        :precision="1"
                        style="width: 80%"
                      />
                      <span style="margin-left: 5px">折</span>
                    </el-form-item>
                  </div>
                </template>
                <template v-if="row.type === 'FULL_REDUCTION'">
                  <div class="flex" style="width: 100%">
                    <el-form-item :label-width="0" prop="requiredAmount">
                      <span>满</span>
                      <el-input-number
                        v-model.number="row.conditionAmount"
                        :controls="false"
                        :disabled="disabled"
                        :max="999999"
                        :min="1"
                        :precision="2"
                        style="width: 90px; margin: 0 5px"
                      />
                    </el-form-item>
                    <el-form-item :label-width="0" prop="discountAmount">
                      <span>元,减</span>
                      <el-input-number
                        v-model.number="row.discountAmount"
                        :controls="false"
                        :disabled="disabled"
                        :max="row.conditionAmount"
                        :min="1"
                        :precision="2"
                        style="width: 90px; margin: 0 5px"
                      />
                      <span style="margin-left: 5px">元</span>
                    </el-form-item>
                  </div>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="商品选择" prop="productType">
          <el-radio-group v-model="form.productType" :disabled="disabled">
            <el-radio value="ALL_PRODUCT">全部商品参与</el-radio>
            <el-radio value="SPECIFIED_PRODUCT_PARTICIPATE">指定商品参与</el-radio>
            <el-radio value="SPECIFIED_PRODUCT_NOT_PARTICIPATE">指定商品不参与</el-radio>
          </el-radio-group>
          <div v-if="form.productType !== 'ALL_PRODUCT' && chooseGoodsList.length" :class="chooseGoodsList.length && 'goodsData'" class="goods-list">
            <el-table
              :data="chooseGoodsList"
              :header-cell-style="{ fontSize: '14px', color: '#606266', background: '#f2f2f2', height: '54px', fontWeight: 'normal' }"
              height="260px"
              style="width: 100%"
            >
              <el-table-column label="商品信息">
                <template #default="{ row }: { row: GoodsListItem }">
                  <div class="fcenter">
                    <el-image :preview-src-list="[row.pic]" :preview-teleported="true" :src="row.pic" fit="" style="width: 60px; height: 60px" />
                    {{ row.productName }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div class="nav-button">
      <el-button plain round @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(add) {
  overflow-y: scroll;
}

@include b(title) {
  font-size: 14px;
  color: #606266;
  font-weight: 700;
  margin-bottom: 40px;
}

@include b(msg) {
  font-size: 12px;
  margin-left: 12px;
  color: #c4c4c4;
}

@include b(rules) {
  display: flex;
  margin-top: 10px;
  height: 50px;
}

@include b(period-validity) {
  width: 300px;
  display: flex;
}

@include b(text) {
  font-size: 14px;
  color: #333333;
}

@include b(goodsData) {
  border: 1px solid #ccc;
}

@include b(goods-list) {
  width: 90%;
  margin-top: 20px;
  height: 300px;
  border: 1px solid transparent;
  @include e(info) {
    display: flex;
  }
  @include e(goods-list__info-name) {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    padding: 0 16px;
    @include m(name) {
      width: 462px;
      font-size: 14px;
      color: #2e99f3;
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

@include b(my-header) {
  font-size: 16px;
}

@include b(ruleform-date) {
  width: 100%;
  display: flex;
  align-items: center;
}

@include b(flex) {
  margin-top: 10px;
  height: 60px;
}

@include b(flex-item) {
  width: 40%;
}

@include b(coupon-rules) {
  height: 60px;
  @include flex;
}

@include b(nav-button) {
  margin-top: auto;
  align-items: center;
  position: sticky;
  bottom: 0;
  padding: 15px 0;
  display: flex;
  justify-content: center;
  box-shadow: 0 0 10px #d5d5d5;
  background-color: #fff;
  z-index: 9;
}
</style>
