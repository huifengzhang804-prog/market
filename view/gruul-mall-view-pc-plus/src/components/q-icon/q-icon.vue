<script setup lang="ts">
import '@/assets/css/font/iconfont.js'

/**
 * @description: micon 属性
 * @property {String} name font-class
 * @property {String} size 尺寸默认为16px 需传默认单位
 * @property {String} color icon颜色
 */
const $props = defineProps({
  name: {
    type: String,
    default: '',
  },
  size: {
    type: String,
    default: '16px',
  },
  color: {
    type: String,
    default: '',
  },
  svg: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['click'])

const clickHandler = () => {
  $emit('click')
}
</script>

<template>
  <svg v-if="svg" class="icon" aria-hidden="true" :style="'font-size:' + $props.size + '; color:' + $props.color" @click="clickHandler">
    <use :xlink:href="`#${$props.name}`" />
  </svg>
  <text v-else :class="'icon iconfont ' + [$props.name]" :style="'font-size:' + $props.size + '; color:' + $props.color" @click="clickHandler" />
</template>

<style lang="scss" scoped>
@import '@/assets/css/font/iconfont.scss';
</style>
