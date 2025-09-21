<script setup lang="ts">
import { ref, reactive } from 'vue'
import HeadOperation from '../components/head-operation.vue'
import SetMealList from '../components/set-meal-list.vue'

const search = reactive({
  setMealStatus: '',
  keyword: '',
})
const mealListRef = ref()

const SetMealListHandle = () => {
  mealListRef.value.initactiveList()
}
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <HeadOperation v-model="search" @search="SetMealListHandle" />
    </el-config-provider>
    <SetMealList ref="mealListRef" :search="search" />
  </div>
</template>

<style scoped lang="scss"></style>
