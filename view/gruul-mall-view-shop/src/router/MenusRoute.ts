import { RouteRecordRaw } from 'vue-router'
import Main from '@/components/layout/Main.vue'
import MContent from '@/components/layout/MContent.vue'

const redirect = '/'
//经营概况
export const overview: RouteRecordRaw = {
    path: '/overview',
    name: 'BusinessStatistics',
    component: Main,
    meta: {
        title: '经营概况',
        icon: 'icon-zhuomian-diannaoxianshiqi',
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
}
// 商家中心
export const business: RouteRecordRaw = {
    path: '/business',
    name: 'busines',
    component: Main,
    meta: {
        title: '商家中心',
    },
    children: [
        {
            path: '',
            name: 'business',
            component: () => import('@views/business/business.vue'),
            meta: {
                title: '商家中心',
            },
        },
    ],
}
//商品管理
// 商品路由变更后需在发布商品组件中调整对应关系
export const goods: RouteRecordRaw = {
    path: '/',
    name: 'goods',
    component: Main,
    meta: {
        title: '商品管理',
    },
    children: [
        {
            path: '/goods/list',
            name: 'goodsList',
            meta: {
                title: '商品中心',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'goodsList',
                    component: () => import('@views/goods/Index.vue'),
                },
                {
                    path: 'new',
                    name: 'releaseCommodity',
                    component: () => import('@views/goods/releaseCommodity/Index.vue'),
                    meta: {
                        title: '新增商品',
                        keepAlive: true,
                    },
                },
                {
                    path: 'edit',
                    name: 'releaseCommodityEdit',
                    component: () => import('@views/goods/releaseCommodity/Index.vue'),
                    meta: {
                        title: '编辑商品',
                        keepAlive: true,
                    },
                },
                {
                    path: 'copy',
                    name: 'releaseCommodityCopy',
                    component: () => import('@views/goods/releaseCommodity/Index.vue'),
                    meta: {
                        title: '复制商品',
                        keepAlive: true,
                    },
                },
                {
                    path: 'edit-consignment',
                    name: 'releaseCommodityEditConsignment',
                    component: () => import('@/q-plugin/supplier/EditConsignmentCommodity.vue'),
                    meta: { title: '编辑代销商品' },
                },
            ],
        },
        {
            path: '/goods/examine',
            name: 'goodsExamine',
            meta: { title: '商品审核' },
            component: MContent,
            children: [{ path: '', name: 'goodsExamineList', component: () => import('@views/goods/examine/index.vue') }],
        },
        {
            path: '/goods/supplier',
            name: 'goodsSupplier',
            component: () => import('@views/goods/supplier/Index.vue'),
            meta: {
                title: '供货商',
            },
        },
        {
            path: '/goods/category',
            name: 'goodsCategory',
            component: () => import('@views/goods/category/Index.vue'),
            meta: {
                title: '店铺类目',
            },
        },
        {
            path: '/goods/zone',
            name: 'commodityZone',
            component: () => import('@views/goods/CommodityZone.vue'),
            meta: {
                title: '商品专区',
            },
        },
        {
            path: '/goods/para',
            name: 'goodsPara',
            meta: {
                title: '属性参数',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'goodsPara',
                    component: () => import('@views/goods/para/index.vue'),
                },
            ],
        },
        {
            path: '/goods/purchase',
            name: 'goodsPurchase',
            meta: { title: '采购管理' },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'goodsPurchaseList',
                    component: () => import('@/q-plugin/supplier/PurchaseList.vue'),
                },
                {
                    path: 'add',
                    name: 'addProcurement',
                    component: () => import('@/q-plugin/supplier/NewProcurement.vue'),
                    meta: {
                        title: '新增采购',
                    },
                },
                {
                    path: 'detail',
                    name: 'goodsPurchaseDetails',
                    component: () => import('@/q-plugin/supplier/PurchaseOrderInfo.vue'),
                    meta: {
                        title: '订单详情',
                    },
                },
                {
                    path: 'release',
                    name: 'releasePurchaseGoods',
                    meta: {
                        title: '发布商品',
                    },
                    component: () => import('@/q-plugin/supplier/PurchaseOrderRelease.vue'),
                },
            ],
        },
        {
            path: '/goods/consignment',
            name: 'goodsConsignment',
            meta: { title: '代销管理' },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'goodsConsignmentList',
                    component: () => import('@/q-plugin/supplier/Consignment.vue'),
                },
                {
                    path: 'distribution',
                    name: 'goodsConsignmentDistribution',
                    component: () => import('@/q-plugin/supplier/ConsignmentDistrubutuion.vue'),
                    meta: { title: '一键铺货' },
                },
            ],
        },
    ],
}

//库存管理
export const inventory: RouteRecordRaw = {
    path: '/',
    name: 'inventory',
    component: Main,
    meta: {
        title: '库存管理',
    },
    children: [
        {
            path: '/inventory/center',
            name: 'inventorycenter',
            meta: {
                title: '库存中心',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'inventoryIndex',
                    component: () => import('@views/inventory/Index.vue'),
                },
            ],
        },
        {
            path: '/inventory/storage',
            name: 'inventorystorage',
            meta: {
                title: '出入库',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'storageIndex',
                    component: () => import('@views/inventory/storage/Index.vue'),
                },
                {
                    path: 'add',
                    name: 'addstorage',
                    meta: {
                        title: '新增出入库',
                    },
                    component: () => import('@views/inventory/storage/addStorage.vue'),
                },
                {
                    path: 'detail',
                    name: 'storagedetail',
                    meta: {
                        title: '出入库详情',
                    },
                    component: () => import('@views/inventory/storage/storageDetail.vue'),
                },
            ],
        },
        {
            path: '/inventory/count',
            name: 'inventorycount',
            meta: {
                title: '库存盘点',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'inventorycountIndex',
                    component: () => import('@views/inventory/InventoryCount/Index.vue'),
                },
                {
                    path: 'add',
                    name: 'addCount',
                    meta: {
                        title: '新增盘点',
                    },
                    component: () => import('@views/inventory/InventoryCount/addCount.vue'),
                },
                {
                    path: 'detail',
                    name: 'countDetail',
                    meta: {
                        title: '盘点详情',
                    },
                    component: () => import('@views/inventory/InventoryCount/countDetail.vue'),
                },
            ],
        },
        {
            path: '/inventory/Inventoryflow',
            name: 'Inventoryflow',
            meta: {
                title: '库存明细',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'InventoryflowIndex',
                    component: () => import('@views/inventory/InventoryFlow/Index.vue'),
                },
            ],
        },
    ],
}

//订单管理
export const order: RouteRecordRaw = {
    path: '/',
    name: 'order',
    component: Main,
    meta: {
        title: '订单管理',
    },
    children: [
        {
            path: '/order/delivery',
            name: 'orderdelivery',
            meta: {
                title: '订单中心',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'orderIndex',
                    component: () => import('@views/order/Index.vue'),
                },
                {
                    path: '/order/details',
                    name: 'deliveryDetails',
                    meta: {
                        title: '订单详情',
                    },
                    component: MContent,
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
                    name: 'deliverylist',
                    meta: {
                        title: '批量发货',
                    },
                    component: MContent,
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
            path: '/order/sale',
            name: 'orderSale',
            meta: {
                title: '售后工单',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'saleIndex',
                    component: () => import('@/views/afs/Index.vue'),
                },
                {
                    path: '/afs/Detail',
                    name: 'orderSaleDetail',
                    meta: {
                        title: '售后工单详情',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'orderSaleDetailIndex',
                            component: () => import('@views/afs/components/orderAfs.vue'),
                        },
                    ],
                },
            ],
        },
        {
            path: '/order/appraise',
            name: 'orderAppraise',
            meta: {
                title: '评价管理',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'appraiseIndex',
                    component: () => import('@views/order/appraise/Index.vue'),
                },
            ],
        },
    ],
}
export const marketingApp: RouteRecordRaw = {
    path: '/',
    name: 'marketingApp',

    component: Main,
    meta: {
        title: '营销应用',
    },
    children: [
        {
            path: '/marketingApp/bargain',
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
                    name: 'marketingAppBargainBaseinfo',
                    meta: {
                        title: '',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'bargainBaseinfo',
                            component: () => import('@/q-plugin/bargain/addBargain.vue'),
                        },
                    ],
                },
                {
                    path: 'helpInfo',
                    name: 'marketingAppBargainHelpInfo',
                    meta: {
                        title: '帮砍信息',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'BargainHelpInfo',
                            component: () => import('@/q-plugin/bargain/helpInfo.vue'),
                        },
                    ],
                },
            ],
        },
        {
            path: '/marketingApp/applyDiscount',
            name: 'applyDiscount',
            meta: {
                title: '满减活动',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'applyDiscountIndex',
                    component: () => import('@/q-plugin/applyDiscount/ApplyDiscountList.vue'),
                },
                {
                    path: '/baseinfo',
                    name: 'marketingAppDiscountBaseinfo',
                    meta: {
                        title: '',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'applyDiscountBaseinfo',
                            component: () => import('@/q-plugin/applyDiscount/AddDiscountInfo.vue'),
                        },
                    ],
                },
            ],
        },
        {
            path: '/marketingApp/secondsKill',
            name: 'secondsKill',
            meta: {
                title: '限时秒杀',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'seckill',
                    component: () => import('@/q-plugin/secondsKill/SeckillList.vue'),
                },
                {
                    path: 'create',
                    name: 'secondsKillCreate',
                    meta: {
                        title: '',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'seckillCreate',
                            component: () => import('@/q-plugin/secondsKill/SeckillInfo.vue'),
                        },
                    ],
                },
            ],
        },
        {
            path: '/marketingApp/LiveStream',
            name: 'liveStream',
            meta: {
                title: '直播',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'live',
                    component: () => import('@/q-plugin/liveStream/views/liveStream.vue'),
                },
                {
                    path: 'liveStream/baseinfo',
                    name: 'liveStreamBaseinfo',
                    meta: {
                        title: '基本信息',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'streamBaseinfo',
                            component: () => import('@/q-plugin/liveStream/views/components/studio-list/AddStudio.vue'),
                        },
                    ],
                },
                {
                    path: 'liveStream/goods',
                    name: 'liveStreamGoods',
                    meta: {
                        title: '基本信息',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'liveStreamAddGoods',
                            component: () => import('@/q-plugin/liveStream/views/components/live-goods/AddGoods.vue'),
                        },
                    ],
                },
                {
                    path: 'liveStream/look/goods',
                    name: 'liveStreamLookGoods',
                    meta: {
                        title: '查看商品',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'lookGoods',
                            component: () => import('@/q-plugin/liveStream/views/components/live-goods/look-goods.vue'),
                        },
                    ],
                },
            ],
        },
        {
            path: '/marketingApp/coupons',
            name: 'marketingAppCoupons',
            meta: {
                title: '优惠券',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'coupons',
                    component: () => import('@/q-plugin/coupon/CouponList.vue'),
                },
                {
                    path: '/coupons/baseInfo',
                    name: 'marketingAppCouponsBaseInfo',
                    meta: {
                        title: '',
                    },
                    component: MContent,
                    children: [
                        {
                            path: '',
                            name: 'couponsBaseInfo',
                            component: () => import('@/q-plugin/coupon/CouponInfo.vue'),
                        },
                    ],
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
                {
                    path: 'operation',
                    name: 'liveOperation',
                    meta: { title: '运营' },
                    component: () => import('@/q-plugin/liveApp/AppLiveOperation.vue'),
                },
            ],
        },
        {
            path: '/distribution/Index',
            name: 'distribution',
            meta: {
                title: '分销',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'distributionChildren',
                    component: () => import('@/q-plugin/distribution/Index.vue'),
                },
            ],
        },
        {
            path: '/marketingApp/group',
            meta: {
                title: '拼团',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'marketingAppGroup',
                    component: () => import('@/q-plugin/group/Group.vue'),
                },
                {
                    path: 'new',
                    name: 'marketingAppGroupNewForm',
                    meta: {
                        title: '',
                    },
                    component: () => import('@/q-plugin/group/GroupForm.vue'),
                },
                {
                    path: 'form',
                    name: 'marketingAppGroupForm',
                    meta: {
                        title: '',
                    },
                    component: () => import('@/q-plugin/group/GroupForm.vue'),
                },
                {
                    path: 'order/detail',
                    name: 'marketingAppGroupOrderDetail',
                    meta: {
                        title: '',
                    },
                    component: () => import('@/q-plugin/group/GroupOrderDetail.vue'),
                },
            ],
        },
        {
            path: '/bundlePrice/Index',
            name: 'bundlePrice',
            meta: {
                title: '套餐',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'bundlePriceIndex',
                    component: () => import('@/q-plugin/setMeal/setMeal.vue'),
                },
                {
                    path: 'baseinfo',
                    name: 'bundlePriceBaseinfo',
                    meta: {
                        title: '',
                    },
                    component: () => import('@/q-plugin/setMeal/addSetMeal.vue'),
                },
            ],
        },
    ],
}
//系统配置
export const system: RouteRecordRaw = {
    path: '/',
    name: 'system',

    component: Main,
    meta: {
        title: '系统配置',
    },
    children: [
        {
            path: '/system/config/sms',
            name: 'systemConfigSms',
            meta: {
                title: '短信配置',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'systemConfigSmsIndex',
                    component: () => import('@views/system/sms.vue'),
                },
            ],
        },
    ],
}

// 财务管理
export const finance: RouteRecordRaw = {
    path: '/',
    name: 'finance',

    component: Main,
    meta: {
        title: '财务管理',
    },
    children: [
        {
            path: '/finance/reconciliation',
            name: 'reconciliation',
            meta: {
                title: '对账单',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'reconciliationChildren',
                    component: () => import('@views/finance/reconciliation.vue'),
                },
            ],
        },
        {
            path: '/finance/settle',
            name: 'shopSettle',
            meta: {
                title: '店铺结算',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'shopsAndSettlement',
                    component: () => import('@views/finance/Settle.vue'),
                },
            ],
        },
        {
            path: '/finance/InvoicingRequest',
            name: 'InvoicingRequest',
            meta: {
                title: '开票申请',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'InvoicingRequestChildren',
                    component: () => import('@views/finance/InvoicingRequest.vue'),
                },
            ],
        },
        {
            path: '/finance/splitBill',
            name: 'splitBill',
            meta: {
                title: '分账单',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'splitBillChildren',
                    component: () => import('@views/finance/splitBill.vue'),
                },
            ],
        },
    ],
}
//商城管理
export const mall: RouteRecordRaw = {
    path: '/',
    name: 'mall',

    component: Main,
    meta: {
        title: '商城管理',
    },
    children: [
        {
            path: '/mall/permission',
            name: 'mallPermission',
            meta: {
                title: '系统管理',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'mallPermission',
                    component: () => import('@views/mall/permission/Index.vue'),
                },
            ],
        },
        {
            path: '/mall/general/setting',
            name: 'mallGeneralSetting',
            meta: {
                title: '通用设置',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'mallGeneralSetting',
                    component: () => import('@views/mall/settings/Index.vue'),
                },
            ],
        },
        {
            path: '/decoration',
            name: 'decoration',
            meta: {
                title: '装修',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'decoration',
                    component: () => import('@views/decoration/chooseType/index.vue'),
                },
            ],
        },
        {
            path: '/material/management',
            name: 'materialManagement',
            meta: { title: '素材管理' },
            component: MContent,
            children: [{ path: '', name: 'materialManagementList', component: () => import('@views/material/index.vue') }],
        },
        {
            path: '/download/center',
            name: 'DownloadCenter',
            meta: { title: '下载中心' },
            component: MContent,
            children: [{ path: '', name: 'DownloadCenterPage', component: () => import('@views/system/download-center/index.vue') }],
        },
    ],
}
//公告
export const notice: RouteRecordRaw = {
    path: '/',
    name: 'notice',
    component: Main,
    meta: {
        title: '消息中心',
    },
    children: [
        {
            path: '/message/customer/service',
            name: 'messageCustomerService',
            meta: {
                title: '客服消息',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'messageCustomerServiceIndex',
                    component: () => import('@views/message/customerService/Index.vue'),
                },
            ],
        },
        {
            path: '/message/customer/supplierService',
            name: 'messageSupplierService',
            meta: {
                title: '供应商消息',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'messageSupplierService',
                    component: () => import('@/q-plugin/supplier/SupplierMessage.vue'),
                },
            ],
        },
        {
            path: '/message/notice',
            name: 'messageNotice',
            meta: {
                title: '平台公告',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'messageNoticeIndex',
                    component: () => import('@views/message/notice/Index.vue'),
                },
                {
                    path: 'detail',
                    name: 'messageNoticeDetail',
                    component: () => import('@views/message/notice/detail/Index.vue'),
                    meta: {
                        title: '通知详情',
                    },
                },
            ],
        },
    ],
}
//配送管理
export const freight: RouteRecordRaw = {
    path: '/',
    name: 'freight',

    component: Main,
    meta: {
        title: '配送管理',
    },
    children: [
        {
            path: '/shop/store',
            name: 'shopStore',
            meta: {
                title: '门店管理',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'shopStoreIndex',
                    component: () => import('@/q-plugin/store/StoreList.vue'),
                },
                {
                    path: 'AddStore',
                    name: 'AddshopStore',
                    meta: {
                        title: '门店信息',
                    },
                    component: () => import('@/q-plugin/store/StoreInfo.vue'),
                },
            ],
        },
        {
            path: '/freight/logistics',
            name: 'freightLogistics',
            meta: {
                title: '快递配送',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'freightLogisticsIndex',
                    component: () => import('@/q-plugin/freight/Freight.vue'),
                },
            ],
        },
        {
            path: '/intra/city',
            name: 'intraCity',
            meta: {
                title: '同城配送',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'intraCityIndex',
                    component: () => import('@/q-plugin/cityDistribution/CityDistributionIndex.vue'),
                },
            ],
        },
    ],
}
// 装修
export const decoration: RouteRecordRaw = {
    path: '/decoration/set',
    name: 'decorationSet',
    component: () => import('@views/decoration/on-line-set/Set.vue'),
}

// 装修
export const decorationo2o: RouteRecordRaw = {
    path: '/decoration/seto2o',
    name: 'decorationSeto2o',
    component: () => import('@views/decoration/o2o-set/o2o-set.vue'),
}

export const decorationPc: RouteRecordRaw = {
    path: '/decoration/pc',
    name: 'decorationPc',
    component: () => import('@views/decoration/pc/Set.vue'),
}

export const member: RouteRecordRaw = {
    path: '/',
    name: 'member',
    redirect,
    component: Main,
    meta: {
        title: '客户管理',
    },
    children: [
        {
            path: 'member/list',
            meta: {
                title: '客户列表',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'memberList',
                    component: () => import('@views/member/member/list.vue'),
                },
                {
                    path: 'details',
                    name: 'memberDetails',
                    meta: {
                        title: '客户详情',
                    },
                    component: () => import('@views/member/member/details.vue'),
                },
                {
                    path: 'coupon',
                    name: 'memberCoupon',
                    meta: {
                        title: '送优惠券',
                    },
                    component: () => import('@/q-plugin/coupon/CouponGift.vue'),
                },
            ],
        },
        {
            path: '/voucherRecord',
            name: 'VoucherRecord',
            meta: {
                title: '用券记录',
            },
            component: MContent,
            children: [
                {
                    path: '',
                    name: 'VoucherRecord',
                    component: () => import('@/q-plugin/coupon/voucherRecord.vue'),
                },
            ],
        },
    ],
}
