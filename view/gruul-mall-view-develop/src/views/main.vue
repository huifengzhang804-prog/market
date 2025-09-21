<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-15 10:50:16
-->
<template>
  <el-container direction="vertical" class="out-container">
    <Header/>
    <el-container class="inner-container">
      <el-aside width="200px">
        <Navigate />
      </el-aside>
      <el-main style="background-color: white">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Header from 'comps/system/header/Header.vue';
import Navigate from 'comps/Navigate.vue';
</script>

<style scoped lang="scss">
.out-container {
  height: 100%;
}

.inner-container {
  height: calc(100% - 63px);
  overflow-y: auto !important;
}



</style>