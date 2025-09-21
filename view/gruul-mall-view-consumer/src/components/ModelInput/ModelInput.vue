<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import type { ApiGoodSku } from '@/pluginPackage/goods/commodityInfo/types'
import NumberJPan from './numberJpan.vue'
import { computed, type PropType, ref } from 'vue'

const props = defineProps({
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
})
const emits = defineEmits(['update:modelValue'])

const _modelValue = useVModel(props, 'modelValue', emits)
const NumberJPanRef = ref()
const ruleMax = computed(() => {
  let num = 0
  const { limitType, stockType, stock, limitNum, teamStock } = props.rule
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

const minus = () => {
  if (_modelValue.value > 1) {
    _modelValue.value--
  }
}
const plus = () => {
  if (_modelValue.value < ruleMax.value) {
    _modelValue.value++
  }
}

const showModel = ref(false)
const showModelInput = () => {
  showModel.value = true
}
</script>

<template>
  <view class="model_input_container">
    <view class="minus" :class="{ disabled: _modelValue <= 1 }" @touchstart.prevent="minus">-</view>
    <view class="value" @touchstart.prevent="showModelInput">{{ _modelValue }}</view>
    <view class="plus" :class="{ disabled: _modelValue >= ruleMax }" @touchstart.prevent="plus">+</view>
  </view>
  <Teleport to="body">
    <NumberJPan v-model:flag="showModel" v-model="_modelValue" :max="ruleMax" :min="1"></NumberJPan>
  </Teleport>
</template>

<style lang="scss" scoped>
.model_input_container {
  display: flex;
  align-items: center;
  text-align: center;
  height: 70rpx;
  color: rgb(50, 50, 51);
  font-size: 13px;
  .value {
    background-color: rgb(242, 243, 245);
    height: 100%;
    width: 80rpx;
    overflow-y: hidden;
    overflow-x: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 10rpx;
    margin: 0 6rpx;
  }
  .plus,
  .minus {
    width: 60rpx;
    background-color: rgb(242, 243, 245);
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
