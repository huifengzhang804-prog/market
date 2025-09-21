<script setup lang="ts">
import { ref } from 'vue'
import HeadOperation from '../components/head-operation-p.vue'
import SetMealList from '../components/set-meal-list-p.vue'

const search = ref({
  setMealStatus: '',
  keyword: '',
})
const mealListRef = ref()
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <HeadOperation v-model="search" />
    </el-config-provider>

    <div class="grey_bar"></div>
    <SetMealList ref="mealListRef" :search="search" />
  </div>
</template>

<style scoped lang="scss"></style>
