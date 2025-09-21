<script lang="ts" setup>
import { doGetSupplierCommodityDetails } from '@/apis/good'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import UseConvert from '@/composables/useConvert'
import VueClipboard3 from 'vue-clipboard3'

const { divTenThousand } = UseConvert()
const { toClipboard } = VueClipboard3()
const $props = defineProps({
  commodityId: {
    type: String,
    default: '',
  },
  shopId: {
    type: String,
    default: '',
  },
})
enum SellTypeEnum {
  CONSIGNMENT = '代销商品',
  PURCHASE = '采购商品',
  OWN = '自有商品',
}
const dataInfo = ref<any>({})
const getCommidityDetails = async () => {
  const { code, msg, data } = await doGetSupplierCommodityDetails($props.commodityId, { shopId: $props.shopId })
  if (code === 200) {
    dataInfo.value = data
  } else {
    ElMessage.error({ message: msg })
  }
}
getCommidityDetails()
const handleCopy = async (url: string) => {
  try {
    await toClipboard(url)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>

<template>
  <div class="details">
    <el-row :gutter="8">
      <el-col :span="24" style="font-weight: 600">{{ dataInfo?.name }}</el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="dataInfo?.collectionUrl ? 8 : 12">
        <el-row :gutter="8">
          <el-col :span="6">平台类目：</el-col>
          <el-col :span="18"
            >{{ dataInfo?.platformCategoryName?.oneName }}/{{ dataInfo?.platformCategoryName?.twoName }}/{{
              dataInfo?.platformCategoryName?.threeName
            }}</el-col
          >
        </el-row>
      </el-col>
      <el-col :span="dataInfo?.collectionUrl ? 8 : 12">
        <el-row :gutter="8">
          <el-col :span="6">销售方式：</el-col>
          <el-col :span="18">{{ SellTypeEnum[dataInfo?.sellType as keyof typeof SellTypeEnum] }}</el-col>
        </el-row>
      </el-col>
      <el-col v-if="dataInfo?.collectionUrl" :span="8">
        <el-row :gutter="8">
          <el-col :span="6">采集地址：</el-col>
          <el-col :span="18"><el-link type="primary" @click="handleCopy(dataInfo.collectionUrl)">复制</el-link></el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="24">
        <el-table :data="dataInfo?.storageSkus" max-height="230px" size="small" :cell-style="{ height: '48px' }">
          <el-table-column label="规格">
            <template #default="{ row }">
              {{ row?.specs?.length ? row?.specs?.join(';') : '单规格' }}
            </template>
          </el-table-column>
          <el-table-column v-if="dataInfo?.storageSkus && dataInfo?.storageSkus[0]?.image" label="SKU图">
            <template #default="{ row }">
              <el-image :src="row?.image" style="width: 50px; height: 50px" />
            </template>
          </el-table-column>
          <el-table-column label="销售价(元)">
            <template #default="{ row }">
              {{ divTenThousand(row?.salePrice) }}
            </template>
          </el-table-column>
          <el-table-column label="划线价(元)">
            <template #default="{ row }">
              {{ divTenThousand(row?.price) }}
            </template>
          </el-table-column>
          <el-table-column label="重量(kg)" prop="weight" />
        </el-table>
      </el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="24">
        <!-- eslint-disable-next-line vue/no-v-html -->
        <div class="details-more" v-html="dataInfo?.detail" />
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.el-row {
  padding: 5px 0;
  line-height: 1.5;
}
.details {
  max-height: 770px;
}
@include b(details-more) {
  max-height: 440px;
  overflow-y: auto;
  :deep(img) {
    max-width: 100%;
  }
}
</style>
