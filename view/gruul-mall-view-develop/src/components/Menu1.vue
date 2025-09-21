<template>
  <template v-if="props.menus" v-for="menu in props.menus">
    <el-sub-menu :index="menu.id" v-if="menu.type === 'CATALOG'">
      <template #title>{{ menu.name }}</template>
      <Menu :menus="menu.children"/>
    </el-sub-menu>
    <el-menu-item v-else :index="menu.path" :route="menu.path">
      <function @loaded="()=>menuRoute(menu)"/>
      <el-badge  v-if="menu.addon" is-dot >
        {{ menu.name }}
      </el-badge>
      <template v-else>{{ menu.name }}</template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import {toRaw} from 'vue'
import {useRouter} from "vue-router";
import routers from '../router'
import {define} from '../utils/MenuConponent';



const router = useRouter();

const menuRoute = (menu:any) => {
  if (!menu.path || !menu.addon) {
    return;
  }
  routers.addRoute("Main",{
    name: menu.name,
    path:menu.path,
    component: define(toRaw(menu.addon.component)),
    meta: {
      auth: true
    }
  })
}

interface Props {
  menus: Array<Object>
}

const props = withDefaults(defineProps<Props>(), {
  menus: ()=>[]
})
</script>

<style scoped>

</style>