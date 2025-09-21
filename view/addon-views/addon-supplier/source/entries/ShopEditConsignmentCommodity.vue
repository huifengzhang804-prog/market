<script lang="ts" setup>
import { LimitTypeEnum, SkuInterface } from '../components/edit-consignment/types'
import useEditConsignmentCommodity from '../components/edit-consignment/hooks/useEditConsignmentCommodity'
import BatchUnifySettings from '../components/edit-consignment/components/batch-unify-settings.vue'
import Decimal from 'decimal.js'

const {
  distributionFormModel,
  distributionSkus,
  distributionFormRules,
  platformCategoryList,
  shopCascaderProps,
  currentComodityInfo,
  shopCategoryList,
  LabelList,
  specGroups,
  formRef,
  cancelDistribution,
  handleConfirmDistribution,
} = useEditConsignmentCommodity()
</script>

<template>
  <div class="distribution">
    <el-form ref="formRef" class="distribution__form fdc1 overh" :model="distributionFormModel" :rules="distributionFormRules">
      <div class="distribution__title">基础信息</div>
      <div class="distribution__container">
        <el-form-item label="商品类型">实物商品</el-form-item>
        <el-form-item label="销售方式">代销商品（采购商品可供各店铺采购，代销商品可以让各店铺帮您将商品销售出去；）</el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="distributionFormModel.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="平台类目">
          <el-cascader
            ref="platformCategoryRef"
            v-model="currentComodityInfo.platformCategoryId"
            clearable
            class="inputWidth"
            style="width: 100%"
            :options="platformCategoryList"
            :props="shopCascaderProps"
            disabled
            placeholder="请选择平台类目"
            show-all-levels
          />
        </el-form-item>
        <el-form-item label="店铺类目" prop="shopCategory">
          <template #default>
            <el-cascader
              ref="shopCategoryRef"
              v-model="distributionFormModel.shopCategory"
              clearable
              style="width: 100%"
              class="inputWidth"
              :options="shopCategoryList"
              :props="shopCascaderProps"
              placeholder="请选择店铺类目"
              show-all-levels
            />
          </template>
        </el-form-item>
        <el-form-item label="商品标签">
          <el-select v-model="distributionFormModel.labelId" class="inputWidth" placeholder="请选择商品标签" style="width: 100%">
            <el-option v-for="item in LabelList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="卖点描述" prop="saleDescribe">
          <el-input v-model="distributionFormModel.saleDescribe" class="inputWidth" placeholder="请填写卖点描述" maxlength="60"></el-input>
        </el-form-item>
      </div>
      <!-- <template v-if="distributionSkus.length === 1">
        <div class="distribution__title">单规格信息</div>
        <div class="distribution__container">
          <el-row :gutter="8">
            <el-col :span="6">规格类型：单规格</el-col>
            <el-col :span="6">供货价：{{ distributionSkus?.[0]?.salePrice }}</el-col>
            <el-col :span="6"
              >销量（注水）：{{ Number(distributionSkus?.[0]?.initSalesVolume || 0) + Number(distributionSkus?.[0]?.salesVolume || 0) }}</el-col
            >
            <el-col :span="6">重量：{{ distributionSkus?.[0]?.weight }}</el-col>
            <el-col :span="6">库存数：{{ distributionSkus?.[0]?.stockType === 'UNLIMITED' ? '无限库存' : distributionSkus?.[0]?.stock }}</el-col>
            <el-col :span="6">限购类型：{{ LimitTypeEnum[distributionSkus?.[0]?.limitType] }}</el-col>
          </el-row>
        </div>
      </template> -->
      <div class="distribution__title">价格设置</div>
      <div class="distribution__container fdc1 overh">
        <div class="price-settings">
          <el-radio-group v-model="distributionFormModel.consignmentPriceSetting.type">
            <el-radio value="RATE">按比例设价</el-radio>
            <el-radio value="REGULAR">固定金额设价</el-radio>
          </el-radio-group>
          <template v-if="distributionFormModel.consignmentPriceSetting.type === 'RATE'">
            <el-form-item prop="consignmentPriceSetting.sale">
              <span>销售价 = 供货价 + （供货价 * &nbsp;</span>
              <el-input-number v-model="distributionFormModel.consignmentPriceSetting.sale" :controls="false" :precision="2" :max="100" />
              <span>&nbsp;% ）</span>
            </el-form-item>
            <el-form-item prop="consignmentPriceSetting.scribe">
              <span>划线价 = 销售价 + （销售价 * &nbsp;</span>
              <el-input-number v-model="distributionFormModel.consignmentPriceSetting.scribe" :controls="false" :precision="2" :max="100" />
              <span>&nbsp;% ）</span>
            </el-form-item>
          </template>
          <template v-else>
            <el-form-item prop="consignmentPriceSetting.sale">
              <span>销售价 = 供货价 + &nbsp;</span>
              <el-input-number v-model="distributionFormModel.consignmentPriceSetting.sale" :controls="false" :precision="2" />
              <span>&nbsp;元</span>
            </el-form-item>
            <el-form-item prop="consignmentPriceSetting.scribe">
              <span>划线价 = 销售价 + &nbsp;</span>
              <el-input-number v-model="distributionFormModel.consignmentPriceSetting.scribe" :controls="false" :precision="2" />
              <span>&nbsp;元</span>
            </el-form-item>
          </template>
        </div>
        <el-table :data="distributionSkus" max-height="600" :cell-style="{ height: '50px' }">
          <el-table-column v-if="distributionSkus && distributionSkus[0]?.image" fixed="left" width="150" label="规格图">
            <template #default="{ row }: { row: SkuInterface }">
              <el-image :src="row?.image" style="width: 50px; height: 50px" />
            </template>
          </el-table-column>
          <el-table-column v-for="(skuOptions, idx) in specGroups" :key="idx" width="150" :label="skuOptions?.name">
            <template #default="{ row }: { row: SkuInterface }">
              {{ Array.isArray(row.specs) ? row.specs?.[idx] : row.specs.name }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="供货价" prop="salePrice">
            <template #default="{ row }">
              {{ new Decimal(row?.salePrice).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="划线价" prop="actualPrice">
            <template #default="{ row }">
              {{ new Decimal(row?.actualPrice).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="销售价" prop="actualSalePrice">
            <template #default="{ row }">
              {{ new Decimal(row?.actualSalePrice).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="库存">
            <template #default="{ row }">
              {{ row?.stockType === 'UNLIMITED' ? '无限库存' : row?.initStock }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="限购类型">
            <template #default="{ row }: { row: SkuInterface }">
              {{ LimitTypeEnum[row?.limitType] }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="规格限购">
            <template #default="{ row }: { row: SkuInterface }">
              {{ row?.limitType !== 'UNLIMITED' ? String(row.limitNum) : '' }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="重量" prop="weight" />
          <el-table-column width="150" label="销量（注水）">
            <template #default="{ row }: { row: SkuInterface }">
              {{ Number(row?.initSalesVolume || 0) + Number(row?.salesVolume || 0) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-form>
  </div>
  <div class="distribution__btns">
    <el-button @click="cancelDistribution">取消</el-button>
    <el-button type="primary" @click="handleConfirmDistribution">确认</el-button>
  </div>
</template>

<style lang="scss" scoped>
@include b(distribution) {
  overflow-y: scroll;
  @include e(form) {
    padding: 0 16px;
  }
  @include e(title) {
    font-weight: bold;
    padding: 15px 0;
  }
  @include e(container) {
    padding: 0 20px;
  }
  @include e(btns) {
    margin-top: auto;
    display: flex;
    justify-content: center;
    align-items: center;
    position: sticky;
    bottom: 0;
    padding: 10px 0;
    box-shadow: 0 0 10px #d5d5d5;
    z-index: 9;
  }
}
</style>
