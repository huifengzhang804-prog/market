<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import SelectCoupponType from '../components/select-couppon-type.vue'
import { doGetCouponInfoById } from '../apis'
import { doGetRetrieveProduct } from '@/apis/good'
import { ProductType, dataFormat, EffectType, couponIndexType } from '../index'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()

const $router = useRouter()
const $route = useRoute()
const goodsData = ref([])
const ruleForm = ref({
  name: '',
  days: '',
  endDate: '',
  productType: ProductType.ALL,
  type: 'PRICE_DISCOUNT',
  requiredAmount: '',
  discount: '',
  amount: '',
  effectType: EffectType.PERIOD,
  startDate: '',
  num: '',
  limit: '',
  shopId: '',
  productIds: [],
})
initCouponInfoById()

async function initCouponInfoById() {
  if ($route.query.id && $route.query.shopId) {
    const { code, data } = await doGetCouponInfoById($route.query.shopId.toString(), $route.query.id.toString())
    if (code !== 200) {
      ElMessage.error('获取优惠券信息失败')
      return
    }
    ruleForm.value = dataFormat(data) as any
    getParticipateInActivitiesGoods(data)
    return
  }
}

function formatGoodsPrice(salePrices: string[]) {
  const maxPrice = Math.max(...salePrices.map((item) => Number(item)))
  const minPrice = Math.min(...salePrices.map((item) => Number(item)))
  // 返回商品价格
  return `${divTenThousand(minPrice)}~${divTenThousand(maxPrice)}`
}

/**
 * 获取参与活动的商品
 */
async function getParticipateInActivitiesGoods(resData: any) {
  // 判断商品id是否存在
  if (resData.productIds && resData.productIds.length) {
    // 发起请求获取商品信息
    const { code, data } = await doGetRetrieveProduct({ productId: resData.productIds })
    // 判断请求是否成功
    if (code !== 200) {
      ElMessage.error('获取商品信息失败')
      return
    }
    // 设置商品列表
    goodsData.value = data.list.map((item) => {
      return { ...item, name: item.productName, salePrice: formatGoodsPrice(item.salePrices || []), isCheck: true }
    }) as any
    console.log('goodsData.value', goodsData.value)
  }
}
</script>

<template>
  <div class="q_plugin_container">
    <div class="scroll">
      <h1 class="title">基本信息</h1>
      <el-form :inline-message="false" :model="ruleForm" :validate-on-rule-change="false" label-width="auto">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model.trim="ruleForm.name" disabled maxlength="10" placeholder="请输入优惠券名称" style="width: 551px" />
          <span class="msg">优惠券名称不超过10个字</span>
        </el-form-item>
        <el-form-item label="有效时间" prop="effectType">
          <el-row style="width: 100%">
            <el-radio-group v-model="ruleForm.effectType" class="ml-4" disabled>
              <el-radio value="PERIOD">固定时间</el-radio>
              <el-radio value="IMMEDIATELY">领券立即生效</el-radio>
            </el-radio-group>
          </el-row>
          <el-row style="margin-top: 20px">
            <template v-if="ruleForm.effectType === 'PERIOD'">
              <div class="ruleform-date">
                <el-form-item :inline-message="false" label-width="0" prop="startDate">
                  <el-date-picker
                    v-model="ruleForm.startDate"
                    disabled
                    format="YYYY/MM/DD"
                    placeholder="请选择开始时间"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                  <span style="margin: 0 10px">至</span>
                </el-form-item>
                <el-form-item :inline-message="false" label-width="0" prop="endDate">
                  <el-date-picker
                    v-model="ruleForm.endDate"
                    disabled
                    format="YYYY/MM/DD"
                    placeholder="请选择结束时间"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </div>
            </template>
            <el-form-item v-else label-width="0" prop="days">
              <div class="period-validity text">
                领券当日起
                <el-input v-model="ruleForm.days" disabled style="width: 20%; margin: 0 5px" />
                天内可用
              </div>
            </el-form-item>
          </el-row>
        </el-form-item>
        <el-form-item label="活动规则" prop="type">
          <el-table :data="[{}]" :header-cell-style="{ textAlign: 'center', fontSize: '14px', color: '#606266' }" border style="width: 90%">
            <el-table-column label="选择优惠券类型">
              <template #default>
                <el-form-item label-width="0" prop="type">
                  <select-couppon-type v-model="ruleForm.type" :list="couponIndexType" disabled placeholder="全部类型" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="优惠券规则" width="240">
              <template #default>
                <el-form-item v-if="ruleForm.type === 'PRICE_DISCOUNT'" label-width="0" prop="discount">
                  <div class="rules">
                    <el-input v-model="ruleForm.discount" disabled style="flex: 1" />
                    <span style="margin-left: 10px">折，无门槛使用</span>
                  </div>
                </el-form-item>
                <el-form-item v-if="ruleForm.type === 'PRICE_REDUCE'" label-width="0" prop="amount">
                  <div class="rules">
                    <el-input v-model="ruleForm.amount" disabled style="flex: 1" />
                    <span style="margin-left: 5px">元，无门槛使用</span>
                  </div>
                </el-form-item>
                <template v-if="ruleForm.type === 'REQUIRED_PRICE_DISCOUNT'">
                  <div class="rules" style="justify-content: center">
                    <el-form-item label-width="0%" prop="requiredAmount">
                      <span>满</span>
                      <el-input v-model="ruleForm.requiredAmount" disabled style="width: 60%; margin-left: 5px" />
                    </el-form-item>
                    <el-form-item label-width="0%" prop="discount">
                      <span>元,打</span>
                      <el-input v-model="ruleForm.discount" disabled style="width: 50%; margin-left: 5px" />
                      <span style="margin-left: 5px">折</span>
                    </el-form-item>
                  </div>
                </template>
                <template v-if="ruleForm.type === 'REQUIRED_PRICE_REDUCE'">
                  <div class="rules" style="justify-content: center">
                    <el-form-item label-width="0%" prop="requiredAmount">
                      <span>满</span>
                      <el-input v-model="ruleForm.requiredAmount" disabled style="width: 60%; margin-left: 5px" />
                    </el-form-item>
                    <el-form-item label-width="0%" prop="amount">
                      <span>元,减</span>
                      <el-input v-model="ruleForm.amount" disabled style="width: 50%; margin-left: 5px" />
                      <span style="margin-left: 5px">元</span>
                    </el-form-item>
                  </div>
                </template>
              </template>
            </el-table-column>
            <el-table-column label="发行量（张）">
              <template #default>
                <el-form-item label-width="0" prop="num">
                  <el-input v-model="ruleForm.num" disabled />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="限领（张）">
              <template #default>
                <el-form-item label-width="0" prop="num">
                  <el-input v-model="ruleForm.limit" disabled />
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="商品选择" prop="productType">
          <el-radio-group v-model="ruleForm.productType" disabled>
            <el-radio value="SHOP_ALL">全部商品参与</el-radio>
            <el-radio value="ASSIGNED">指定商品参与</el-radio>
            <el-radio value="ASSIGNED_NOT">指定商品不参与</el-radio>
          </el-radio-group>
          <div :class="goodsData.length && 'goodsData'" class="goods-list">
            <el-table
              v-if="ruleForm.productType !== 'SHOP_ALL'"
              :data="goodsData"
              :header-cell-style="{ fontSize: '14px', color: '#606266', background: '#f2f2f2', height: '54px', fontWeight: 'normal' }"
              height="360px"
              style="width: 100%"
            >
              <el-table-column label="商品信息">
                <template #default="{ row }">
                  <div class="goods-list__info">
                    <el-image :preview-src-list="[row.pic]" :preview-teleported="true" :src="row.pic" fit="fit" style="width: 60px; height: 60px" />
                    <div class="goods-list__goods-list__info-name">
                      <div class="goods-list__goods-list__info-name--name">{{ row.name }}</div>
                      <div class="goods-list__goods-list__info-name--price">{{ row.salePrice }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80px"></el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div class="tool">
      <el-button plain round @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.scroll {
  overflow: scroll;
  padding-left: 16px;
  padding-right: 16px;
  margin-top: 30px;
}
.tool {
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
@include b(title) {
  font-size: 14px;
  color: #606266;
  font-weight: 700;
  margin-bottom: 30px;
}

@include b(msg) {
  font-size: 12px;
  margin-left: 12px;
  color: #c4c4c4;
}

@include b(rules) {
  display: flex;

  justify-content: space-between;
  @include e(input) {
    width: 50%;
  }
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
  margin-top: 40px;
  height: 360px;
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
}

@include b(nav-button) {
  position: fixed;
  bottom: 50px;
  left: 50%;
}
</style>
