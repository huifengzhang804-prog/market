<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetsupplierOrderCount } from '../apis'

const prop = defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
// 供应商订单数量
const createdsupplierOrderNumber = ref(0)

// 获取供应商订单总数
initsupplierOrderCount()

/**
 * 获取订单数量
 */
async function initsupplierOrderCount() {
  const { code, data } = await doGetsupplierOrderCount()
  if (code !== 200) return ElMessage.error('获取订单数量失败')
  createdsupplierOrderNumber.value = data
}
</script>

<template>
  <div class="basic__item basicsupplier">
    <q-icon class="basic__item--icon" size="44px" name="icon-gongyingshang" />
    <div class="basic__item--cont">
      <div>供应商数量</div>
      <div class="basic__item--num">{{ prop.properties.supplierNumber }}</div>
    </div>
  </div>
  <div class="basic__item basicprocure">
    <q-icon class="basic__item--icon" size="44px" name="icon-caigou" />
    <div class="basic__item--cont">
      <div>采购订单数量</div>
      <div class="basic__item--num">{{ Number(createdsupplierOrderNumber) }}</div>
    </div>
  </div>
  <text />
</template>
<style scoped lang="scss">
@include b(basic) {
  color: #fff;
  padding-left: 29px;
  padding-right: 29px;
  background: #fff;
  margin-bottom: 10px;
  padding-top: 22px;
  padding-bottom: 20px;

  @include e(content) {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  @include e(item) {
    margin-top: 28px;
    width: 180px;
    height: 132px;
    border-radius: 5px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    @include m(cont) {
      @include flex();
      margin-top: 10px;
    }
    @include m(icon) {
      text-align: center;
      color: #fff;
    }

    @include m(num) {
      font-size: 24px;
      font-weight: 400;
      text-align: center;
      margin-left: 10px;
    }
  }
}
.basicsupplier {
  background: linear-gradient(225deg, #fab8b3 0%, #f185a2 100%);
}
.basicprocure {
  background: linear-gradient(225deg, #b4f2ce 0%, #50c580 100%);
}
</style>
