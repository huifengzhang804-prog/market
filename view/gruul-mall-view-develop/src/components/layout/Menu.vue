<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-22 15:03:17
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-26 14:31:37
-->
<template>
  <div class="aside-wrap">
    <div class="admin__aside--shop">
      <slot name="dropmenu"></slot>
      <div class="iconfont iconweixiubeijing cursor"></div>
    </div>
    <div class="side-nav-wrap">
      <div class="side-nav-wrap-main">
        <div class="admin__menu" @scroll="onScroll">
          <template v-for="(route, i) of localRoutes">
            <div v-if="route.children">
              <div class="admin__menu--item">
                <div class="item--title">
                  <span>
                    {{ route.meta.title }}
                  </span>
                </div>
                <div class="item__sub--menu">
                  <template v-for="sRoute of route.children">
                    <div class="sub--item pt20" :index="sRoute.path">
                      <router-link
                        :path="sRoute.path"
                        :target="
                          sRoute.path == 'editorPage' ? '_blank' : '_self'
                        "
                        :to="'/'+sRoute.path"
                        :class="{
                          active: isCurrent2(sRoute.path),
                        }"
                        >{{ sRoute.meta.title }}
                      </router-link>
                    </div>
                  </template>
                </div>
                <div class="item--mask" v-show="!!itemActiveIndex"></div>
              </div>
            </div>
            <!-- 只渲染一级目录 -->
            <template v-else>
              <div
                v-bind:key="route.path"
                class="admin__menu--item"
                :class="{ active: route.path === $route.path && !isEditMode }"
              >
                <div class="item--title">
                  <router-link :to="route.path">
                    <span>
                      <!-- <i :class="'iconfont ' + route.meta.icon"></i> -->
                      {{ route.meta.title }}
                    </span>
                  </router-link>
                  <!-- <a href="javascript:;" v-else>
                      <span>
                        <i :class="'iconfont ' + route.meta.icon"></i>
                        {{ route.meta.title }}
                      </span>
                    </a> -->
                </div>
              </div>
            </template>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { useStore } from "vuex";
import { get } from "@/apis/http";
import { ElMessage } from "element-plus";
import pick from "lodash/pick";
let scrollVal = null;
let timer = null;
/**
 * reactive variable
 */
const $route = useRoute();
const $store = useStore();
const props = defineProps({
  routes: [],
  // userInfo: {},
});

const currentRoute = computed(() => {
  return $route.path;
});

const routerId = computed(() => {
  // return $store.state.
  return 1;
});
const localRoutes = ref([]);
/* 二级菜单定位top*/
const itemActiveIndex = ref(0);
/* 判断是不是所有导航都在左侧*/
const is_empty = ref(false);
const itemSubTop = ref(-1);
const isScrolling = ref(false);
const currentRouteShow = ref({ path: "" });
/** 编辑模式 */
const isEditMode = ref(false);
//存储dom元素
const refArray = ref<Element[]>([]);

const setRefArray = (el: any) => {
  refArray.value.push(el);
};
/**
 * lifeCircle
 */
getRouteData();
onMounted(() => {
  document.addEventListener("click", (e: any) => {
    if (
      document.querySelector(".aside-wrap") &&
      !document.querySelector(".aside-wrap").contains(e.target) &&
      isEditMode.value
    ) {
      toggleEditMode();
    }
  });
  document.addEventListener("mouseover", (e: any) => {
    if (
      !isEditMode.value &&
      document.querySelector(".aside-wrap") &&
      !document.querySelector(".aside-wrap").contains(e.target)
    ) {
      hideFloatBox();
    }
  });
});
/*
 * function
 */
/** 切换菜单编辑模式 */
function toggleEditMode() {
  isEditMode.value = !isEditMode.value;
  if (!isEditMode.value) {
    // const localRoutes = JSON.stringify(localRoutes.value);
    // upDateAsyncRouterMap({
    //   id: routersId,
    //   properties: localRoutes,
    // }).then(() => {
    //   ElMessage.success("保存成功")
    // });
  }
}
function hideFloatBox() {
  itemActiveIndex.value = 0;
}

function isCurrent(path: string, item: { path: string }) {
  // const { matched } = this.$route;
  const matched = $route.matched;
  if (currentRoute.value === path || (matched[1] && matched[1].path === path)) {
    currentRouteShow.value = item;
    return true;
  }
  return false;
}
/** 是否为当前路由 indeof 针对三级*/
function isCurrent2(path: string) {
  const matched = $route.matched;
  if (
    (matched[1] && matched[1].path === path) ||
    currentRoute.value === path ||
    (path.indexOf("/copartner/order/") !== -1 &&
      path.indexOf("/copartner/setting/") !== -1 &&
      currentRoute.value.indexOf(path) !== -1)
  ) {
    return true;
  }
  return false;
}
watch(
  () => isEditMode.value,
  (newVal) => {
    if (newVal) {
    }
  }
);
function checkItem() {
  let a = false;
  const localRoute = localRoutes.value;
  for (let i = 0; i < localRoute.length; i++) {
    if (localRoute[i].children && localRoute[i].children.length) {
      for (let n = 0; n < localRoute[i].children.length; n++) {
        if (localRoute[i].children[n].show) {
          a = true;
          break;
        }
      }
    }
  }
  return a;
}

function getRouteData() {
  // localRoutes.value = handleRouteData($store.menuStore.routers);

  // get({
  //   url: "gruul-mall-uaa/menu/navigate",
  //   showLoading: false,
  // }).then((data) => {
  //   console.log(data.data);
  //   menus.value = data.data;
  //   localRoutes.value = handleRouteData(data.data);
  // });
  // console.log(handleRouteData(props.routes));
  localRoutes.value = handleRouteData(props.routes);
}

function handleRouteData(routes: RouteRecordRaw[]) {
  return routes.map((item) => {
    if (item.children) {
      item.children = handleRouteData(item.children);
    }
    item = pick(item, ["path", "meta", "name", "children", "version", "show"]);
    return item;
  });
}
function onSelect(item: { show: boolean }, index: any) {
  item.show = !item.show;
  showFloatBox(index, localRoutes.value[itemActiveIndex.value].children);
}
function hideLength(items: {
  filter: (arg0: (item: any) => boolean) => {
    (): any;
    new (): any;
    length: any;
  };
}) {
  return items.filter(
    (item: { show: any; meta: { isShow: any } }) =>
      !item.show && !item.meta.isShow
  ).length;
}
function parentIsActive(
  items: {
    filter: (arg0: (item: any) => boolean) => {
      (): any;
      new (): any;
      length: any;
    };
  },
  path: string
) {
  return items.filter((item: { path: string }) => {
    return isCurrent(path + "/" + item.path, item);
  }).length;
}
function showFloatBox(index: number, arr: any) {
  if (isScrolling.value) {
    return;
  }
  if (arr && hideLength(arr)) {
    itemActiveIndex.value = index;
    // const itemSub = this.$refs[`item_sub_${index}`][0];
    // const menuItem = this.$refs[`menu_item_${index}`][0];
    const menuItem = refArray.value[0];
    const clientHeight = document.documentElement.clientHeight;
    const wrapHeight = clientHeight - 88;
    const itemHeight = 38 + 40 * hideLength(arr);
    const scrollTop = document.querySelector(".side-nav-wrap-main").scrollTop;
    const domToclientHeight = menuItem.offsetTop - scrollTop;
    const dValue = wrapHeight - (domToclientHeight + itemHeight);
    if (dValue < 0) {
      itemSubTop.value = wrapHeight - itemHeight;
    } else {
      itemSubTop.value = domToclientHeight;
    }
  } else {
    hideFloatBox();
  }
}
function onScroll(val: any) {
  if (timer) clearTimeout(timer);
  if (!isScrolling.value) {
    isScrolling.value = true;
  }
  hideFloatBox();
  timer = setTimeout(() => {
    /* 判断是否已经滚动结束，不能写在scrollEnd，有bug */
    if (val === scrollVal) {
      //实现上移
      isScrolling.value = false;
    }
  }, 500);
  scrollVal = val;
}
function onMouseLeave() {
  if (!isEditMode.value) {
    hideFloatBox();
  }
}
</script>
<style lang="scss" scoped>
@import "@/assets/css/layout/mmenu.scss";
.black {
  color: #676767 !important;
}
</style>
