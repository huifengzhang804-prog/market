<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-22 14:28:53
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:54:35
-->
<template>
  <m-layout>
    <template #breadcrumb>
      <div class="flex" style="align-items: center">
        <el-breadcrumb separator="/" class="m__breadcrumb">
          <el-breadcrumb-item
            v-for="(item, i) of breadcrumb"
            :class="[
              'm__breadcrumb--item',
              { 'm__breadcrumb--last': i !== breadcrumb.length - 1 },
            ]"
            :to="
              i !== breadcrumb.length - 1 && i !== 0
                ? { path: item.path }
                : null
            "
            :key="i"
            >{{ item.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
        <el-button style="margin-left: auto" @click="logout"
          >退出登录</el-button
        >
      </div>
    </template>
    <template v-slot:aside>
      <Menu :routes="routes">
        <template v-slot:dropmenu>
          <el-dropdown
            placement="bottom-start"
            :hide-on-click="false"
            @command="commandHandle"
          >
            <div class="lineFlex">
              <img
                @click.stop="goChange"
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211217/1920184934464fc8833918c9bca2e95a.png"
                class="shop--logo"
              />
              <div class="shop--name">
                <span class="el-dropdown-link">
                  <span>开发管理</span>
                  <!-- <i class="el-icon-caret-bottom"></i> -->
                </span>
              </div>
            </div>
            <!-- <template v-slot:dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="console">
                  <div class="dorp-cell">
                    <span>控制台</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="index">
                  <div class="dorp-cell">
                    <span>官网首页</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="setting">
                  <div class="dorp-cell">
                    <span>商家中心</span>
                    <span class="c6">测试用户</span>
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <div class="dorp-cell noborder">
                    <span>退出登录</span>
                    <i class="el-icon-switch-button"></i>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template> -->
          </el-dropdown>
        </template>
      </Menu>
    </template>
    <template #view>
      <router-view></router-view>
    </template>
  </m-layout>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import MLayout from "./MLayout.vue";
import Menu from "./Menu.vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { useStore } from "vuex";
import { doPostLogout } from "@/apis/sign";
/**
 * reactive variable
 */
const $router = useRouter();
const $route = useRoute();
const routes = computed(() => {
  return $router.options.routes;
});
const breadcrumb = computed(() => {
  // console.log(
  return $route.matched.map((route) => {
    return {
      title: route.meta.title,
      path: route.path,
    };
  });
  // )
});
const $store = useStore();

/**
 * lifeCircle
 */

/**
 * function
 */

const logout = () => {
  doPostLogout();
  $store.commit("user/clear");
  $router.push("/sign");
};

const commandHandle = async (command: string) => {
  // 退出登录
  if (command === "logout") {
    // modifySignStatus("", null);
    // logout();
    try {
      // const response = await getSystemConfig();
      // const { code, data } = response;
      // if (code === 200 && data.systemConfig && data.systemConfig.consoleLog) {
      //   open(data.systemConfig.consoleLog, "_top");
      // }
      $router.push("/sign");
    } catch (e: any) {
      ElMessage.warning(e);
    }
  }
  // 账号信息
  if (command === "setting") {
    await $router.push("/business");
  }

  // 控制台
  // if (command === "console") {
  //   await this.$router.push("/console");
  // }

  // 官网
  if (command === "index") {
    const url = process.env.VUE_APP_BASEURL.replace(/\/api/, "");
    open(`${url}`, "_top");
  }

  // 订购
  // if (command === "order") {
  //   await this.$router.push("/meal");
  // }

  // 升级/续费
  // if (command === "update") {
  //   await this.$router.push("/meal/update");
  // }
};
function goChange() {
  $router.push({
    path: "/setting",
    query: {
      tabName: "Store",
    },
  });
}
</script>
<style lang="scss">
.lineFlex {
  display: flex;
  align-items: center;
  height: 60px;
  cursor: pointer;
}

.m__breadcrumb {
  &--item {
    .is-link {
      font-weight: 400 !important;
      color: #606266 !important;
      cursor: pointer;
    }

    &:last-child .el-breadcrumb__inner {
      font-weight: 700 !important;
      text-decoration: none;
      transition: color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
      color: #303133;
    }
  }
}
.business {
  position: fixed;
  border-radius: 50px;
  width: 106px;
  height: 38px;
  background-color: #fff;
  bottom: 11px;
  right: 13px;
  p {
    position: absolute;
    font-family: "SimHei";
    font-size: 15px;
    font-weight: 600;
    top: 11px;
    left: 21px;
    color: #59a9f5;
  }
  .img {
    position: absolute;
    right: 20px;
    top: 13px;
    width: 14px;
    height: 14px;
  }
}
.el-dropdown-link {
  display: flex;
  height: 50px;
  align-items: center;
}
</style>
