<script setup lang="ts">
/**
 * micon 属性
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
})
const $emit = defineEmits(['click'])

const clickHandler = (event?: Event) => {
  $emit('click', event)
}
</script>

<template>
  <text :class="'icon iconfont ' + [$props.name]" :style="'font-size:' + $props.size + '; color:' + $props.color" @click="clickHandler"></text>
</template>

<style lang="scss" scoped>
@import '../../assets/css/font/iconfont.css';
</style>
