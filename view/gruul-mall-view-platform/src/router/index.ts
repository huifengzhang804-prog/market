import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'

import Main from '@/components/layout/Main.vue'
import MContent from '@/components/layout/MContent.vue'
import { useAdminInfo } from '@/store/modules/admin'

const routes: Array<RouteRecordRaw> = [
    //匹配不到路由,默认跳转首页
    {
        path: '/',
        redirect: '/overview',
        meta: {
            isShow: 1,
        },
    },
    {
        path: '',
        redirect: '/overview',
        meta: {
            isShow: 1,
        },
    },
    {
        path: '/hello',
        component: Main,
        meta: {
            title: '......',
        },
        children: [
            {
                path: '',
                name: 'hello',
                component: () => import('@views/^_^.vue'),
                meta: {
                    title: '......',
                },
            },
        ],
    },
    {
        name: 'sign',
        path: '/sign',
        meta: {
            title: '登录',
            isShow: 0,
        },
        component: () => import('@views/sign/Index.vue'),
    },

    {
        path: '/overview',
        component: Main,
        meta: {
            title: '经营概况',
        },
        children: [
            {
                path: '',
                name: 'overview',
                component: () => import('@views/overview.vue'),
                meta: {
                    title: '经营概况',
                },
            },
        ],
    },
    {
        path: '/business',
        component: Main,
        meta: {
            title: '平台中心',
        },
        children: [
            {
                path: '',
                name: 'business',
                component: () => import('@views/business/components/business.vue'),
                meta: {
                    title: '平台中心',
                },
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '平台管理',
        },
        children: [
            {
                path: 'shopList',
                component: MContent,
                meta: {
                    title: '店铺管理',
                },
                children: [
                    {
                        path: '',
                        name: 'shopList',
                        component: () => import('@views/shops/ShopsList.vue'),
                    },
                    {
                        path: 'addShop',
                        name: 'addShop',
                        meta: {
                            title: '新增店铺',
                        },
                        component: () => import('@views/shops/AddShops.vue'),
                    },
                    {
                        path: 'editShop',
                        name: 'editShop',
                        meta: {
                            title: '编辑店铺',
                        },
                        component: () => import('@views/shops/AddShops.vue'),
                    },
                    {
                        path: 'previewShop',
                        name: 'previewShop',
                        meta: {
                            title: '查看店铺',
                            disabled: true,
                        },
                        component: () => import('@views/shops/PreviewShops.vue'),
                    },
                ],
            },
            {
                path: 'store',
                name: 'store',
                component: MContent,
                meta: {
                    title: '门店管理',
                },
                children: [
                    {
                        name: 'storePage',
                        path: '',
                        component: () => import('@/q-plugin/store/StoreList.vue'),
                    },
                    {
                        path: 'AddStore',
                        meta: {
                            title: '门店详情',
                        },
                        component: () => import('@/q-plugin/store/StoreInfo.vue'),
                    },
                ],
            },
            {
                path: 'supplier',
                name: 'supplier',
                component: MContent,
                meta: {
                    title: '供应商管理',
                },
                children: [
                    {
                        name: 'supplierPage',
                        path: '',
                        component: () => import('@/q-plugin/supplier/SupplierList.vue'),
                    },
                    {
                        path: 'addSupplier',
                        name: 'addsupplier',
                        meta: {
                            title: '新增供应商',
                        },
                        component: () => import('@views/shops/AddShops.vue'),
                    },
                    {
                        path: 'editSupplier',
                        name: 'editSupplier',
                        meta: {
                            title: '编辑供应商',
                        },
                        component: () => import('@views/shops/AddShops.vue'),
                    },
                    {
                        path: 'previewSupplier',
                        name: 'previewSupplier',
                        meta: {
                            title: '查看供应商',
                            disabled: true,
                        },
                        component: () => import('@views/shops/PreviewShops.vue'),
                    },
                ],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '系统配置',
        },
        children: [
            {
                path: 'news',
                name: 'news',
                component: MContent,
                meta: {
                    title: '平台公告',
                },
                children: [
                    {
                        name: 'newsPage',
                        path: '',
                        component: () => import('@views/set/news.vue'),
                    },
                ],
            },
            {
                path: 'customerService',
                name: 'customerService',
                component: MContent,
                meta: {
                    title: '平台客服',
                },
                children: [
                    {
                        name: 'customerServicePage',
                        path: '',
                        component: () => import('@views/customerService/customerService.vue'),
                    },
                ],
            },
            {
                path: 'generalSet',
                name: 'generalSet',
                component: MContent,
                meta: {
                    title: '通用设置',
                },
                children: [
                    {
                        name: 'generalSetPage',
                        path: '',
                        component: () => import('@views/set/GeneralSet.vue'),
                    },
                ],
            },
            {
                path: 'decoration',
                name: 'decoration',
                component: MContent,
                meta: {
                    title: '装修',
                },
                children: [
                    {
                        name: 'decorationPage',
                        path: '',
                        component: () => import('@views/decoration/chooseType/index.vue'),
                    },
                ],
            },
            {
                path: 'perm',
                name: 'perm',
                component: MContent,
                meta: {
                    title: '系统管理',
                },
                children: [
                    {
                        name: 'permPage',
                        path: '',
                        component: () => import('@views/perm/Index.vue'),
                    },
                ],
            },
            {
                path: '/material/management',
                name: 'materialManagement',
                component: MContent,
                meta: {
                    title: '素材管理',
                },
                children: [
                    {
                        path: '',
                        name: 'materialManagementList',
                        component: () => import('@views/material/index.vue'),
                    },
                ],
            },
            {
                path: '/platform/delivery',
                name: 'PlatformDelivery',
                component: MContent,
                meta: { title: '平台发货' },
                children: [{ path: '', name: 'PlatformDeliverySet', component: () => import('@views/platformDelivery/index.vue') }],
            },
            {
                path: '/download/center',
                name: 'DownloadCenter',
                component: MContent,
                meta: { title: '下载中心' },
                children: [{ path: '', name: 'DownloadCenterPage', component: () => import('@views/system/download-center/index.vue') }],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '商品管理',
        },
        children: [
            {
                path: 'commodity',
                component: MContent,
                meta: {
                    title: '商品中心',
                },
                children: [
                    {
                        path: '',
                        name: 'commodityList',
                        component: () => import('@views/good/CommodityList.vue'),
                    },
                ],
            },
            {
                path: 'commodity/examine',
                component: MContent,
                meta: {
                    title: '商品审核',
                },
                children: [{ path: '', name: 'CommodityExamine', component: () => import('@views/good/examine/index.vue') }],
            },
            {
                path: 'sortManage',
                component: MContent,
                meta: {
                    title: '平台类目',
                },
                children: [
                    {
                        path: '',
                        name: 'sortManage',
                        component: () => import('@views/shops/CategoryManage.vue'),
                    },
                ],
            },
            {
                path: 'brandManage',
                component: MContent,
                meta: {
                    title: '品牌管理',
                },
                children: [
                    {
                        path: '',
                        name: 'brandManage',
                        component: () => import('@views/brand/Index.vue'),
                    },
                    {
                        path: 'AddBrand',
                        name: 'AddBrand',
                        component: () => import('@views/brand/AddBrand.vue'),
                    },
                ],
            },
            {
                path: 'commodity/tag',
                component: MContent,
                meta: { title: '商品标签' },
                children: [{ path: '', name: 'CommodityTag', component: () => import('@views/good/tag/index.vue') }],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '订单管理',
        },
        children: [
            {
                path: 'order',
                component: MContent,
                meta: {
                    title: '订单列表',
                },
                children: [
                    {
                        path: '',
                        name: 'order',
                        component: () => import('@views/order/Index.vue'),
                    },
                    {
                        path: 'details',
                        component: MContent,
                        name: 'deliveryDetails',
                        meta: {
                            title: '订单详情',
                        },
                        children: [
                            {
                                path: '',
                                name: 'detailsIndex',
                                component: () => import('@views/order/orderDetails/Index.vue'),
                            },
                        ],
                    },
                    {
                        path: '/order/deliveryList',
                        component: MContent,
                        name: 'orderDeliverylist',
                        meta: {
                            title: '批量发货',
                        },
                        children: [
                            {
                                path: '',
                                name: 'deliveryList',
                                component: () => import('@views/order/deliveryList/deliveryList.vue'),
                            },
                        ],
                    },
                ],
            },
            {
                path: 'order/afs',
                component: MContent,
                meta: {
                    title: '售后工单',
                },
                children: [
                    {
                        path: '',
                        name: 'afsIndex',
                        component: () => import('@views/afs/Index.vue'),
                    },
                    {
                        path: 'details',
                        component: MContent,
                        name: 'afsDetails',
                        meta: {
                            title: '售后工单详情',
                        },
                        children: [
                            {
                                path: '',
                                name: 'afsDetailsIndex',
                                component: () => import('@views/afs/components/AfsDetails.vue'),
                            },
                        ],
                    },
                ],
            },
            // 储值
            {
                path: 'payment',
                component: MContent,
                meta: {
                    title: '储值订单',
                },
                children: [
                    {
                        path: '',
                        name: 'payment',
                        component: () => import('@views/payment/Index.vue'),
                    },
                ],
            },
            {
                path: 'order/purchase',
                component: MContent,
                meta: {
                    title: '采购订单',
                },
                children: [
                    {
                        path: '',
                        name: 'orderPurchase',
                        component: () => import('@/q-plugin/supplier/PurchaseList.vue'),
                    },
                    {
                        path: 'details',
                        name: 'orderPurchaseDetails',
                        meta: {
                            title: '采购订单详情',
                        },
                        component: () => import('@/q-plugin/supplier/PurchaseInfo.vue'),
                    },
                ],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '会员管理',
        },
        children: [
            {
                path: 'vip/base',
                component: MContent,
                meta: {
                    title: '会员列表',
                },
                children: [
                    {
                        path: '',
                        name: 'baseVip',
                        component: () => import('@views/baseVip/BaseVip.vue'),
                    },
                    {
                        path: 'vip/details',
                        name: 'vipDetailsIndex',
                        meta: {
                            title: '会员详情',
                        },
                        component: () => import('@views/baseVip/VipDetails.vue'),
                        children: [],
                    },
                    {
                        path: '/member/coupons/baseinfo/Edit',
                        component: () => import('@/q-plugin/coupon/CouponGift.vue'),
                        name: 'memberCouponBaseInfo',
                        meta: {
                            title: '送优惠券',
                        },
                        children: [],
                    },
                ],
            },
            {
                path: 'memberRecords',
                name: 'memberRecordsPage',
                meta: { title: '会员记录' },
                component: () => import('@views/finance/member-records/index.vue'),
                children: [],
            },
            {
                path: 'vip/setting',
                component: MContent,
                meta: {
                    title: '会员设置',
                },
                children: [
                    {
                        path: '',
                        name: 'vipSetting',
                        component: () => import('@views/vipSetting/VipSetting.vue'),
                    },
                ],
            },
            {
                path: 'vip/blacklist',
                component: MContent,
                meta: {
                    title: '黑名单',
                },
                children: [
                    {
                        path: '',
                        name: 'vipBlacklist',
                        component: () => import('@views/vipBlacklist/VipBlacklist.vue'),
                    },
                ],
            },
            {
                path: 'vip/money',
                component: MContent,
                meta: {
                    title: '储值管理',
                },
                children: [
                    {
                        path: '',
                        name: 'vipMoney',
                        component: () => import('@views/vipMoney/VipMoney.vue'),
                    },
                ],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '营销应用',
        },
        children: [
            {
                path: 'bargain',
                name: 'bargain',
                meta: {
                    title: '砍价',
                },
                component: MContent,
                children: [
                    {
                        path: '',
                        name: 'bargainIndex',
                        component: () => import('@/q-plugin/bargain/bargain.vue'),
                    },
                    {
                        path: 'baseinfo',
                        name: 'bargainBaseinfo',
                        meta: {
                            title: '基本信息',
                        },
                        component: () => import('@/q-plugin/bargain/AddBargain.vue'),
                        children: [],
                    },
                ],
            },
            {
                path: 'applyDiscount',
                name: 'applyDiscount',
                component: MContent,
                meta: {
                    title: '满减活动',
                },
                children: [
                    {
                        path: '',
                        name: 'applyDiscountIndex',
                        component: () => import('@/q-plugin/applyDiscount/applyDiscount.vue'),
                    },
                    {
                        path: 'baseinfo',
                        name: 'applyDiscountBaseinfo',
                        meta: {
                            title: '基本信息',
                        },
                        component: () => import('@/q-plugin/applyDiscount/AddDiscountActive.vue'),
                        children: [],
                    },
                ],
            },
            {
                path: '/marketingApp/liveApp',
                name: 'liveApp',
                meta: {
                    title: 'APP直播',
                },
                component: MContent,
                children: [
                    {
                        path: '',
                        name: 'appLive',
                        component: () => import('@/q-plugin/liveApp/AppLiveList.vue'),
                    },
                ],
            },
            {
                path: 'secondsKill',
                component: MContent,
                meta: {
                    title: '限时秒杀',
                },
                children: [
                    {
                        path: '',
                        name: 'secondsKill',
                        component: () => import('@/q-plugin/secondsKill/SeckillList.vue'),
                    },
                    {
                        path: 'edit',
                        name: 'secondsKillEdit',
                        meta: {
                            title: '秒杀详情',
                        },
                        component: () => import('@/q-plugin/secondsKill/SeckillInfo.vue'),
                    },
                ],
            },
            {
                path: 'fatetrue/integral',
                component: MContent,
                meta: {
                    title: '积分商城',
                },
                children: [
                    {
                        path: '',
                        name: 'integralMall',
                        component: () => import('@/q-plugin/integral/integral.vue'),
                    },
                    {
                        path: 'add',
                        name: 'addIntegralMallGoods',
                        component: () => import('@/q-plugin/integral/add-integral-goods.vue'),
                    },
                    {
                        path: 'detail',
                        name: 'integralMallOrderDetail',
                        component: () => import('@/q-plugin/integral/orderDetail.vue'),
                    },
                    {
                        path: 'delivery',
                        name: 'integralMallOrderDelivery',
                        component: () => import('@/q-plugin/integral/deliveryList.vue'),
                    },
                    {
                        path: 'rulse',
                        name: 'integralMallOrderRulse',
                        component: () => import('@/q-plugin/integral/integralRules.vue'),
                    },
                ],
            },
            {
                path: 'fatetrue/coupon',
                component: MContent,
                meta: {
                    title: '优惠券',
                },
                children: [
                    {
                        path: '',
                        name: 'coupon',
                        component: () => import('@/q-plugin/coupon/CouponList.vue'),
                    },
                    {
                        path: '/coupons/baseinfo/Edit',
                        component: () => import('@/q-plugin/coupon/CouponInfo.vue'),
                        name: 'couponBaseInfo',
                        meta: {
                            title: '基本信息',
                        },
                        children: [],
                    },
                ],
            },
            {
                path: 'distribution/index',
                component: MContent,
                meta: {
                    title: '分销',
                },
                children: [
                    {
                        path: '',
                        name: 'distribution',
                        component: () => import('@/q-plugin/distribution/distribution.vue'),
                    },
                ],
            },
            {
                path: 'group',
                component: MContent,
                meta: {
                    title: '拼团',
                },
                children: [
                    {
                        path: '',
                        name: 'group',
                        component: () => import('@/q-plugin/group/group.vue'),
                    },
                    {
                        path: 'detail',
                        meta: {
                            title: '活动详情',
                        },
                        name: 'groupDetail',
                        component: () => import('@/q-plugin/group/GroupDetail.vue'),
                    },
                ],
            },
            {
                path: 'bundlePrice/Index',
                // name: 'bundlePrice',
                component: MContent,
                meta: {
                    title: '套餐',
                },
                children: [
                    {
                        path: '',
                        name: 'bundlePriceIndex',
                        component: () => import('@/q-plugin/setMeal/setMeal.vue'),
                    },
                    {
                        path: 'baseinfo',
                        name: 'bundlePriceBaseinfo',
                        component: () => import('@/q-plugin/setMeal/addSetMeal.vue'),
                    },
                ],
            },
            {
                path: 'marketingApp/liveStream',
                component: MContent,
                meta: {
                    title: '直播',
                },
                children: [
                    {
                        path: '',
                        name: 'liveStream',
                        component: () => import('@/q-plugin/liveStream/views/LiveStream.vue'),
                    },
                    // {
                    //     path: '/coupons/baseinfo/Edit',
                    //     component: () => import('@/q-plugin/coupon/views/coupon/components/add-coupon/AddCoupon.vue'),
                    //     name: 'couponBaseInfo',
                    //     meta: {
                    //         title: '基本信息',
                    //     },
                    //     children: [
                    //     ],
                    // },
                ],
            },
            {
                path: 'consumerRebates',
                name: 'consumerRebates',
                meta: {
                    title: '消费返利',
                },
                component: MContent,
                children: [
                    {
                        path: '',
                        name: 'rebatePage',
                        component: () => import('@/q-plugin/rebatePage/rebate.vue'),
                    },
                ],
            },
        ],
    },
    {
        path: '',
        component: Main,
        meta: {
            title: '财务管理',
        },
        children: [
            {
                path: 'reconciliation',
                name: 'reconciliation',
                component: MContent,
                meta: {
                    title: '对账单',
                },
                children: [
                    {
                        name: 'reconciliationPage',
                        path: '',
                        component: () => import('@views/finance/reconciliation.vue'),
                    },
                ],
            },
            {
                path: 'withdrawOrder',
                name: 'withdrawOrder',
                component: MContent,
                meta: {
                    title: '提现工单',
                },
                children: [
                    {
                        name: 'withdrawOrderPage',
                        path: '',
                        component: () => import('@views/withdrawOrder/Index.vue'),
                    },
                ],
            },
            {
                path: 'storedFlow',
                name: 'storedFlow',
                component: MContent,
                meta: { title: '储值流水' },
                children: [{ name: 'storedFlowPage', path: '', component: () => import('@views/finance/stored-flow/index.vue') }],
            },
        ],
    },
    {
        path: '',
        component: MContent,
        meta: {},
        children: [
            {
                path: 'decoration/set',
                name: 'decorationSet',
                meta: {},
                component: () => import('@views/decoration/platformDecoration/mobile/Set.vue'),
            },
            {
                path: 'decoration/pc/set',
                name: 'decorationPcSet',
                meta: {},
                // component: () => import('@views/decoration/shopDecoration/o2o-set/o2o-set.vue'),
                component: () => import('@views/decoration/platformDecoration/pc/Set.vue'),
            },
            {
                path: 'decoration/shop/set',
                name: 'decorationShopSet',
                meta: {},
                // component: () => import('@views/decoration/shopDecoration/Set.vue'),
                component: () => import('@views/decoration/shopDecoration/onLineSet/Set.vue'),
            },
            {
                path: 'decoration/shop/o2o/set',
                name: 'decorationShopO2oSet',
                meta: {},
                // component: () => import('@views/decoration/shopDecoration/o2o-set/o2o-set.vue'),
                component: () => import('@views/decoration/shopDecoration/o2oSet/Set.vue'),
            },
        ],
    },
    {
        path: '/:pathMatch(.*)',
        redirect: '/',
        meta: {
            isShow: 1,
        },
    },
    // {
    //     path: '/404',
    //     component: Main,
    //     children: [
    //         {
    //             path: '',
    //             component: () => import('@views/404.vue'),
    //             meta: {
    //                 title: '404',
    //             },
    //         },
    //     ],
    // },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
})

router.beforeEach((to) => {
    const whiteList = ['/sign', '/changePass']
    if (!useAdminInfo().getterToken && !whiteList.includes(to.path)) {
        router
            .push({
                path: '/sign',
            })
            .catch((fail) => {
                console.log('跳转失败', fail)
            })
        return false
    }
    return true
})

export default router
