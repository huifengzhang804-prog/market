import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
const routes: Array<RouteRecordRaw> = [
  //匹配不到路由,默认跳转
  {
    path: '/',
    component: () => import('@/views/home/index.vue'),
  },
  {
    name: 'home',
    path: '/home',
    meta: {
      title: '主页',
    },
    component: () => import('@/views/home/index.vue'),
  },
  {
    name: 'classification',
    path: '/classification',
    meta: {
      title: '分类',
    },
    component: () => import('@/views/classification/index.vue'),
  },
  {
    name: 'detail',
    path: '/detail',
    meta: {
      title: '商品详情',
    },
    component: () => import('@/views/detail/index.vue'),
  },
  {
    name: 'pay',
    path: '/pay',
    meta: {
      title: '支付',
    },
    component: () => import('@/views/pay/index.vue'),
  },
  {
    name: 'settlement',
    path: '/settlement',
    meta: {
      title: '结算',
    },
    component: () => import('@/views/settlement/index.vue'),
  },
  {
    name: 'integralConfirm',
    path: '/integralConfirm',
    meta: {
      title: '积分结算',
    },
    component: () => import('@/views/integralShop/confirmPage.vue'),
  },
  {
    name: 'shoppingcart',
    path: '/shoppingcart',
    meta: {
      title: '购物车',
    },
    component: () => import('@/views/shoppingcart/index.vue'),
  },

  {
    name: 'shop',
    path: '/shop',
    meta: {
      title: '店铺',
    },
    component: () => import('@/views/shop/index.vue'),
    children: [
      {
        name: 'shopHome',
        path: '',
        meta: {
          title: '店铺首页',
        },
        component: () => import('@/views/shopHome/index.vue'),
      },
      {
        name: 'shopclass',
        path: 'class',
        meta: {
          title: '店铺分类',
        },
        component: () => import('@/views/shopclass/index.vue'),
      },
      {
        name: 'shopActivityClassIfication',
        path: 'activityClassIfication',
        meta: {
          title: '活动落地页',
        },
        component: () => import('@/views/activityClassIfication/index.vue'),
      },
    ],
  },
  {
    name: 'balance',
    path: '/personalcenter/assets/balance',
    meta: {
      title: '我的储值',
    },
    component: () => import('@/views/personalcenter/assets/balance/index.vue'),
  },
  {
    name: 'collectcoupons',
    path: '/personalcenter/assets/collectcoupons',
    meta: {
      title: '领卷中心',
    },
    component: () => import('@/views/personalcenter/assets/collectcoupons/index.vue'),
  },
  {
    name: 'integralShop',
    path: '/integralShop',
    meta: {
      title: '积分商城',
    },
    component: () => import('@/views/integralShop/index.vue'),
  },
  {
    name: 'integral',
    path: '/personalcenter/assets/integral',
    meta: {
      title: '我的积分',
    },
    component: () => import('@/views/personalcenter/assets/integral/index.vue'),
  },
  {
    name: 'member',
    path: '/personalcenter/assets/member',
    meta: {
      title: '会员中心',
    },
    component: () => import('@/views/personalcenter/assets/member/index.vue'),
  },
  {
    name: 'mycoupon',
    path: '/personalcenter/assets/mycoupon',
    meta: {
      title: '我的优惠卷',
    },
    component: () => import('@/views/personalcenter/assets/mycoupon/index.vue'),
  },
  {
    name: 'footprint',
    path: '/personalcenter/follow/footprint',
    meta: {
      title: '我的足迹',
    },
    component: () => import('@/views/personalcenter/follow/footprint/index.vue'),
  },
  {
    name: 'goodscollection',
    path: '/personalcenter/follow/goodscollection',
    meta: {
      title: '商品收藏',
    },
    component: () => import('@/views/personalcenter/follow/goodscollection/index.vue'),
  },
  {
    name: 'shopcollection',
    path: '/personalcenter/follow/shopcollection',
    meta: {
      title: '关注店铺',
    },
    component: () => import('@/views/personalcenter/follow/shopcollection/index.vue'),
  },
  {
    name: 'aftersales',
    path: '/personalcenter/order/aftersales',
    meta: {
      title: '售后订单',
    },
    component: () => import('@/views/personalcenter/order/aftersales/index.vue'),
  },
  {
    name: 'aftersalesdetail',
    path: '/personalcenter/order/aftersalesdetail',
    meta: {
      title: '售后详情',
    },
    component: () => import('@/views/personalcenter/order/aftersalesdetail/index.vue'),
  },
  {
    name: 'applyaftersales',
    path: '/personalcenter/order/applyaftersales',
    meta: {
      title: '售后类型',
    },
    component: () => import('@/views/personalcenter/order/applyaftersales/index.vue'),
  },
  {
    name: 'applyaftersalesservice',
    path: '/personalcenter/order/applyaftersalesservice',
    meta: {
      title: '申请售后',
    },
    component: () => import('@/views/personalcenter/order/applyaftersalesservice/index.vue'),
  },
  {
    name: 'comment',
    path: '/personalcenter/order/comment',
    meta: {
      title: '评价晒单',
    },
    component: () => import('@/views/personalcenter/order/comment/index.vue'),
  },
  {
    name: 'myorder',
    path: '/personalcenter/order/myorder',
    meta: {
      title: '我的订单',
    },
    component: () => import('@/views/personalcenter/order/myorder/index.vue'),
  },
  {
    name: 'orderdetail',
    path: '/personalcenter/order/orderdetail',
    meta: {
      title: '订单详情',
    },
    component: () => import('@/views/personalcenter/order/orderdetail/index.vue'),
  },
  {
    name: 'writecomments',
    path: '/personalcenter/order/writecomments',
    meta: {
      title: '写评价',
    },
    component: () => import('@/views/personalcenter/order/writecomments/index.vue'),
  },
  {
    name: 'address',
    path: '/personalcenter/set/address',
    meta: {
      title: '收货地址',
    },
    component: () => import('@/views/personalcenter/set/address/index.vue'),
  },
  {
    name: 'customerservice',
    path: '/personalcenter/set/customerservice',
    meta: {
      title: '联系客服',
    },
    component: () => import('@/views/personalcenter/set/customerservice/index.vue'),
  },
  {
    name: 'information',
    path: '/personalcenter/set/information',
    meta: {
      title: '个人信息',
    },
    component: () => import('@/views/personalcenter/set/information/index.vue'),
  },
  {
    name: 'seckill',
    path: '/seckill',
    meta: {
      title: '秒杀',
    },
    component: () => import('@/pluginPackage/seckill/index.vue'),
  },
  {
    name: 'discountCoupon',
    path: '/discountCoupon',
    meta: {
      title: '领券中心',
    },
    component: () => import('@/q-plugin/coupon/discountCoupon.vue'),
  },
  {
    name: 'merchantEnter',
    path: '/merchantEnter/merchantEnter',
    meta: {
      title: '我的订单',
    },
    component: () => import('@/views/merchantEnter/merchantEnter.vue'),
  },
  {
    name: 'activityClassIfication',
    path: '/activityClassIfication',
    meta: {
      title: '活动落地页',
    },
    component: () => import('@/views/activityClassIfication/index.vue'),
  },
  {
    name: 'searchPage',
    path: '/searchPage',
    meta: {
      title: '搜索页',
    },
    component: () => import('@/components/searchPage.vue'),
  },
]
const scrollBehavior = function scrollBehavior(to: any, from: any, savedPosition: any) {
  return document.querySelector(`#toTop`)?.scrollIntoView()
}
const router = createRouter({
  history: createWebHashHistory(),
  routes: routes,
  // scrollBehavior() {
  //     return { left: 0, top: 0 }
  // },
  scrollBehavior,
})
export default router
