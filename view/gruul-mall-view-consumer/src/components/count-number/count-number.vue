<script setup lang="ts">
import { computed, type PropType, ref, watch } from 'vue'
import type { ApiGoodSku } from '@/pluginPackage/goods/commodityInfo/types'
import { debounce } from 'lodash'

const $props = defineProps({
  modelValue: {
    type: Number,
    default: 0,
  },
  rule: {
    type: Object as PropType<ApiGoodSku & { teamStock: string }>,
    default() {
      return {
        limitNum: 0,
        limitType: 'UNLIMITED',
        stock: '0',
        stockType: 'UNLIMITED',
        teamStock: '0',
      }
    },
  },
  height: {
    type: Number,
    default: 72,
  },
  min: { type: Number, default: 1 },
})
const $emit = defineEmits(['update:modelValue', 'change', 'blur'])
const test = ref(0)
watch(
  () => $props.modelValue,
  (val) => {
    test.value = val
  },
  {
    immediate: true,
  },
)

const ruleMax = computed(() => {
  let num = 0
  const { limitType, stockType, stock, limitNum, teamStock } = $props.rule
  if (limitType !== 'UNLIMITED' && stockType !== 'UNLIMITED') {
    // 限购 +有限库存 取最小值
    return Math.min(Number(limitNum), Number(stock))
  } else if (limitType !== 'UNLIMITED') {
    // 限购 取限购的数量
    num = Number(limitNum)
  } else if (stockType !== 'UNLIMITED') {
    //  不限购限制库存 取库存的数量
    num = Number(stock)
  } else {
    return Number(teamStock) > 0 ? Number(teamStock) : 999
  }
  return num
})

const handleChangeNumber = debounce((e: { value: number }) => {
  console.log(e, '触发事件', ruleMax, $props.modelValue)
  let num = e.value
  if ($props.rule.limitType !== 'UNLIMITED' && Number($props.rule.limitNum) < e.value) {
    num = Number($props.rule.limitNum)
    uni.showToast({
      icon: 'none',
      title: `商品限购${num}件`,
    })
  } else if ($props.rule.stockType !== 'UNLIMITED' && Number($props.rule.stock) < e.value) {
    num = Number($props.rule.stock)
    uni.showToast({
      icon: 'none',
      title: `商品仅剩${num}件`,
    })
  }
  $emit('update:modelValue', num)
  $emit('change', num)
}, 500)

const handleBlur = () => {
  $emit('update:modelValue', test.value)
  $emit('blur')
}
</script>

<template>
  <u-number-box
    v-model="test"
    :input-height="height"
    :input-width="80"
    :min="$props.min"
    :max="ruleMax"
    @blur="handleBlur"
    @minus="handleChangeNumber"
    @plus="handleChangeNumber"
  />
</template>

<style lang="scss" scoped></style>
