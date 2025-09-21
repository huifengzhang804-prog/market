<script lang="ts" setup>
import { ref } from 'vue'
const tableData = ref<
  {
    name: string
    price: number
    salePrice: string
  }[]
>([{ name: '规格1', price: 100, salePrice: '' }])
</script>

<template>
  <el-table :data="tableData">
    <el-table-column prop="name" label="规格名称" />
    <el-table-column prop="price" label="指导价" />
    <el-table-column prop="salePrice" label="销售价">
      <template #default="{ row }">
        <el-input v-model="row.salePrice" placeholder="请输入销售价" />
      </template>
    </el-table-column>
  </el-table>
</template>
