<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-22 13:59:12
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:38:59
-->
<template>
  <div class="admin">
    <div class="admin__header">
      <div class="admin__center admin__center--flex">
        <slot name="logo"></slot>
        <slot name="user"></slot>
      </div>
    </div>
    <div class="admin__center admin__main--breadcrumb-wrap">
      <div class="admin__main--breadcrumb">
        <slot name="breadcrumb"></slot>
      </div>
    </div>
    <div class="admin__center_wrap">
      <el-scrollbar :noresize="false" viewStyle="width:100vw" class="h100">
        <div class="admin__content admin__center">
          <div class="admin__aside">
            <slot name="aside"></slot>
          </div>
          <el-main class="admin__main">
            <div
              class="admin__main--content"
              :class="noPadding && 'no--padding'"
              ref="admin_content"
              :style="{
                'min-height': minHeight + `px`,
                padding: !changeBC ? '20px 15px' : '0px',
              }"
            >
              <slot name="view"></slot>
            </div>
          </el-main>
        </div>
      </el-scrollbar>
    </div>
    <slot name="notify"></slot>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, nextTick, watch } from "vue";
import { useRoute } from "vue-router";
/**
 * reactive variable
 */
const minHeight = ref(780);
const changeBC = ref(false);
const route = useRoute();
const noPadding = computed(() => {
  return route.meta && route.meta.noPadding;
});
/**
 * lifeCircle
 */
onMounted(() => {
  nextTick(() => {
    minHeight.value = document.documentElement.clientHeight - 84;
    changeBC.value = route.name === "OverView" ? true : false;
  });
});
/**
 * function
 */
watch(
  () => route,
  () => {
    if (document.querySelector(".el-scrollbar__wrap")) {
      document.querySelector(".el-scrollbar__wrap").scrollTop = 0;
    }
    changeBC.value = route.name === "OverView" ? true : false;
  }
);
</script>

<style lang="scss">
@import "@/assets/css/layout/layout.scss";
.side-nav-wrap-main {
  ::-webkit-scrollbar {
    display: none;
  }
}
</style>
