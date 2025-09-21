<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  color: {
    type: String,
    default: '#fff',
  },
  fontSize: {
    type: String,
    default: '40rpx',
  },
  // <!--图标内容使用data里的变量 nvue 页面引入 icon 独特写法 实例 ：e607 写成  \ue607 即可-->
  iconName: {
    type: String,
    default: '\ue607',
  },
  scale: {
    type: Boolean,
    default: false,
  },
  active: { type: Boolean, default: false },
  activeColor: {
    type: String,
    default: '#fa3534',
  },
})
const activeflag = ref(false)
const click = () => {
  if ($props.active) {
    activeflag.value = !activeflag.value
    setTimeout(() => {
      activeflag.value = false
    }, 100)
  }
}
</script>

<template>
  <text :style="{ fontFamily: 'iconfont', color: activeflag ? activeColor : color, fontSize: fontSize, lineHeight: fontSize }" @click="click">
    {{ iconName }}
  </text>
</template>

<style scoped></style>
