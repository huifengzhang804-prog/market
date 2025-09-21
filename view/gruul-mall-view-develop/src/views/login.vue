<template>
  <el-container direction="vertical" style="height: 100%" >
    <Header/>
    <el-container style="height:calc(100% - 63px)" >
      <el-main>
        <login-box/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Header from 'comps/system/header/Header.vue'
import LoginBox from 'comps/login/LoginBox.vue'
</script>

<style scoped>

</style>