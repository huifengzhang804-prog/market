<script lang="ts" setup>
import { reactive, PropType, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { doGetNewSupplierNumber } from '../apis'
// @ts-ignore-next-line
import supplierToBeAudited from '@/assets/image/supplier_to_be_audited.png'

const $router = useRouter()
const supplierAggregation = reactive({
  newSupplierNumber: 0,
  supplierNumber: 0,
  willverifySupplierNumber: 0,
})
const prop = defineProps({
  properties: {
    type: Object as PropType<{ loadSupplierData: (e: any) => void }>,
    // eslint-disable-next-line vue/require-valid-default-prop
    default: {},
  },
})

// 初始化新增供应商数量
initSupplierNumber()

/**
 * 获取供应商数量和待审核供应商数量
 */
async function initSupplierNumber() {
  const { code, data } = await doGetNewSupplierNumber()
  if (code !== 200) return ElMessage.error('获取供应商数据失败')
  supplierAggregation.newSupplierNumber = data[1].number
  supplierAggregation.willverifySupplierNumber = data[0].number
  supplierAggregation.supplierNumber = data[2].number
  prop.properties.loadSupplierData(supplierAggregation)
}
const handleNavToSupplier = () => {
  $router.push({
    path: 'supplier',
    query: {
      name: 'UNDER_REVIEW',
    },
  })
}
</script>

<template>
  <div class="schedule__outborder" @click="handleNavToSupplier">
    <img style="position: absolute; left: 0; top: 0" :src="supplierToBeAudited" alt="" />
    <div class="schedule__item">
      <div>待审核供应商</div>
      <div class="schedule__item--num">{{ supplierAggregation.willverifySupplierNumber || 0 }}</div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(schedule) {
  color: rgb(155, 162, 171);
  padding-left: 29px;
  padding-right: 85px;
  padding-top: 20px;
  background: #fff;
  margin-bottom: 10px;
  padding-bottom: 20px;

  @include e(content) {
    display: flex;
    align-items: center;
    justify-content: space-around;
    margin-top: 38px;
  }
  @include e(outborder) {
    width: 250px;
    height: 112px;
    flex-shrink: 0;
    position: relative;
    cursor: pointer;
  }
  @include e(item) {
    font-size: 16px;
    display: flex;
    flex-direction: column;
    margin: 16px;
    position: absolute;
    top: 0;
    left: 0;
    @include m(num) {
      font-size: 24px;
      font-weight: 400;
      color: rgb(40, 45, 48);
      margin-top: 15px;
    }
  }
}
</style>
