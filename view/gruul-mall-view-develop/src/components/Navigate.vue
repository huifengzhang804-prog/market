<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-15 10:48:03
-->
<template>
  <el-menu
      :collapse="isCollapse"
      router
      :default-active="route.path"
  >
    <Menu :menus="menus"/>
  </el-menu>
</template>
<script setup lang="ts">
import Menu from './Menu.vue'
import {get} from '../apis/http'
import {ref, reactive} from 'vue'
import {useRoute, useRouter} from 'vue-router'

const isCollapse = ref(false)
const route = useRoute();
const router = useRouter();
let menus = ref([]);

get({
  url: 'gruul-mall-uaa/uaa/menu/navigate',
  showLoading: false
}).then(
    data => {
      console.log(data.data)
      menus.value = data.data;
    }
)
</script>

<style lang="scss" scoped>
</style>
