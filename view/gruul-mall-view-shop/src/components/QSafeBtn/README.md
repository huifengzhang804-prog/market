# QSafeBtn 防抖按钮组件

基于 Element Plus 的 el-button 封装，增加了点击防抖功能，透传所有 el-button 的属性、事件和插槽。

## 功能特点

-   基于 el-button 组件，保留其所有特性
-   支持自定义防抖延迟时间
-   支持在 template 中使用
-   自动取消组件卸载时的待执行防抖函数

## 使用方法

### 在模板中使用

```vue
<template>
    <QSafeBtn type="primary" :delay="500" @click="handleClick"> 点击提交 </QSafeBtn>
</template>

<script setup lang="ts">
import QSafeBtn from '@/components/QSafeBtn.vue.vue'

const handleClick = () => {
    console.log('按钮被点击')
}
</script>
```

## 参数说明

| 参数  | 说明                      | 类型   | 默认值 |
| ----- | ------------------------- | ------ | ------ |
| delay | 防抖延迟时间（毫秒）      | number | 300    |
| ...   | 支持所有 el-button 的属性 | -      | -      |

## 事件

| 事件名 | 说明                       | 参数       |
| ------ | -------------------------- | ---------- |
| click  | 点击按钮后的回调（已防抖） | MouseEvent |
| ...    | 支持所有 el-button 的事件  | -          |

## 插槽

| 插槽名  | 说明                      |
| ------- | ------------------------- |
| default | 按钮内容                  |
| ...     | 支持所有 el-button 的插槽 |
