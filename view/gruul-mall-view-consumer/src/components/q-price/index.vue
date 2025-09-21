<script lang="ts" setup>
import type { Nullable } from '@/constant/global'
import { computed, ref, watch, type PropType } from 'vue'
import { Decimal } from 'decimal.js-light'

const { divTenThousand } = useConvert()
const $props = defineProps({
  // 价格
  price: {
    type: [String, Number, Decimal] as PropType<Nullable<Long | Decimal>>,
    default() {
      return 0
    },
  },
  //价格单位符号 如：￥、$
  unit: {
    type: String,
    default: '',
  },
  //字体尺寸
  fontSize: {
    type: String,
    default() {
      return null
    },
  },
  //显示的符号
  // 不为空且当价格大于 0 是显示
  symbol: {
    type: String as PropType<'+' | '-' | ''>,
    default: '',
  },
})
const curPrice = ref({
  //原价 单位元
  price: new Decimal(0),
  // 整数部分
  integer: '0',
  //小数部分
  decimal: '00',
})
watch(
  () => $props.price,
  (amount) => {
    if (!amount) {
      curPrice.value = {
        price: new Decimal(0),
        integer: '0',
        decimal: '00',
      }
      return
    }
    //转成 decimal 保留两位小数 转 String 根据小数点分割
    const originalPrice = divTenThousand(amount)
    const priceSplit = originalPrice.toFixed(2).toString().split('.')
    curPrice.value = {
      price: originalPrice,
      integer: priceSplit[0],
      decimal: priceSplit[1],
    }
  },
  { immediate: true },
)

//整数部分字体大小
const intSize = computed(() => {
  return $props.fontSize || 'inherit'
})
//小数部分字体大小
const decimalSize = computed(() => {
  const fontSize = $props.fontSize
  return fontSize ? `calc(${fontSize}*2/3)` : 'inherit'
})
</script>
<template>
  <span class="q-price">
    <span class="symbol">{{ (symbol && curPrice.price.cmp(0) > 0 ? symbol : '') + unit }}</span>
    <span>{{ curPrice.integer }}</span>
    <span class="decimal">{{ '.' + curPrice.decimal }}</span>
  </span>
</template>

<style lang="scss" scoped>
.q-price {
  font-size: v-bind(intSize);

  .symbol {
    padding-right: 2px;
    font-size: v-bind(decimalSize);
  }

  .decimal {
    font-size: v-bind(decimalSize);
  }
}
</style>
