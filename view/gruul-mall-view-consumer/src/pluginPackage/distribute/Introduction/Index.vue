<script setup lang="ts">
import { ref } from 'vue'
import { doGetDistributeConfig } from '@pluginPackage/distribute/apis'

const playMethods = ref()

initDisConfig()

async function initDisConfig() {
  const { code, data } = await doGetDistributeConfig()
  if (code === 200) {
    // 定义一个正则表达式来匹配 "<img"
    let regex = /<img/g
    // 定义要添加的字符串
    let appendString = ' style="max-width: 100%;"'
    // 使用 replace 方法将匹配到的 "<img" 替换为 "<img style="max-width: 750rpx;"
    playMethods.value = data.playMethods.replace(regex, (match: any) => match + appendString).replace(/style=""/g, '')
  } else {
    uni.showToast({
      icon: 'none',
      title: '获取赚钱攻略失败',
    })
  }
}
</script>

<template>
  <rich-text :nodes="playMethods" class="rich"></rich-text>
</template>
