/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:50:01
 */
/**
 * vue-router index 路由跳转
 * @author 张治保
 */
import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import store from "../store/index";
import Main from "@/components/layout/Main.vue";
import MContent from "@/components/layout/MContent.vue";
import { defineComponent } from "vue";
const routes: Array<RouteRecordRaw> = [
  //匹配不到路由,默认跳转
  {
    path: "/:pathMatch(.*)",
    redirect: "/sign",
    meta: {
      isShow: 1,
    },
  },
  {
    name: "sign",
    path: "/sign",
    meta: {
      title: "登录",
      isShow: 1,
    },
    component: () => import("views/sign/Index.vue"),
  },

  {
    path: "",
    component: Main,
    meta: {
      title: "菜单管理",
    },
    children: [
      {
        path: "platform/menu",
        name: "platformMenu",
        component: MContent,
        meta: {
          title: "平台菜单",
        },
        children: [
          {
            path: "",
            component: () => import("views/set/PermissionList.vue"),
            props: {
              clientType: "PLATFORM_CONSOLE",
            },
          },
        ],
      },
      {
        path: "shop/menu",
        name: "shopMenu",
        component: MContent,
        meta: {
          title: "商户菜单",
        },
        children: [
          {
            path: "",
            component: () => import("views/set/PermissionList.vue"),
            props: {
              clientType: "SHOP_CONSOLE",
            },
          },
        ],
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes,
});

router.beforeEach((to, from) => {
  console.log(to);
  console.log(store.state.user);
  // @ts-ignore
  const whiteList = ["/sign"];
  const authFail = !to.meta.auth && !!!store.state.user.token;
  if (authFail && !whiteList.includes(to.path)) {
    router
      .push({
        path: "/sign",
        query: {
          redirect: to.fullPath,
        },
      })
      .catch((fail) => {
        console.log("跳转失败", fail);
      });
    return false;
  }
  return true;
});

export default router;
