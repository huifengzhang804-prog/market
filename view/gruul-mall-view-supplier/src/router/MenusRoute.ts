import { RouteRecordRaw } from 'vue-router'
import MContent from '../components/layout/MContent.vue'

const redirect = '/'
//经营概况
export const overview: RouteRecordRaw = {
    path: '/overview',
    name: '经营概况',
    component: () => import('@views/overview.vue'),
    meta: {
        title: '经营概况',
        icon: 'iconfont icon-gaikuang',
    },
}
// 商家中心
export const business: RouteRecordRaw = {
    path: '/',
    name: 'busines',
    component: MContent,
    meta: {
        title: '供应商中心',
    },
    children: [
        {
            path: '/business',
            name: 'business',
            component: () => import('@views/business/business.vue'),
            meta: {
                title: '供应商中心',
            },
        },
    ],
}
//商品管理
// 商品路由变更后需在发布商品组件中调整对应关系
export const goods: RouteRecordRaw = {
    path: '/',
    name: 'goods',
    component: MContent,
    meta: {
        title: '商品管理',
        icon: 'iconfont icon-shangpinguanli',
    },
    children: [
        {
            path: '/goods/list',
            name: 'goodsList',
            meta: {
                title: '商品中心',
            },
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
            ],
        },
        {
            path: '/goods/examine',
            name: 'goodsExamine',
            meta: { title: '商品审核' },
            children: [{ path: '', name: 'goodsExamineList', component: () => import('@views/goods/examine/index.vue') }],
        },
        {
            path: '/goods/para',
            name: 'goodsPara',
            meta: {
                title: '属性参数',
            },
            children: [
                {
                    path: '',
                    name: 'goodsPara',
                    component: () => import('@views/goods/para/index.vue'),
                },
            ],
        },
    ],
}

//库存管理
export const inventory: RouteRecordRaw = {
    path: '/',
    name: 'inventory',
    component: MContent,
    meta: {
        title: '库存管理',
        icon: 'iconfont icon-yingxiaoguanli',
    },
    children: [
        {
            path: '/inventory/center',
            name: 'inventorycenter',
            meta: {
                title: '库存中心',
            },
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
    component: MContent,
    meta: {
        title: '订单管理',
        icon: 'iconfont icon-dingdan2',
    },
    children: [
        {
            path: '/order/delivery',
            name: 'orderdelivery',
            meta: {
                title: '订单中心',
            },
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
            children: [
                {
                    path: '',
                    name: 'saleIndex',
                    component: () => import('@views/afs/Index.vue'),
                },
                {
                    path: '/afs/Detail',
                    name: 'orderSaleDetail',
                    meta: {
                        title: '售后工单详情',
                    },
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
        // {
        //     path: '/order/appraise',
        //     name: 'orderAppraise',
        //     meta: {
        //         title: '评价管理',
        //     },
        //     children: [
        //         {
        //             path: '',
        //             name: 'appraiseIndex',
        //             component: () => import('@views/order/appraise/Index.vue'),
        //         },
        //     ],
        // },
        {
            path: '/order/purchase',
            name: 'orderPurchase',
            meta: { title: '采购订单' },
            children: [
                {
                    path: '',
                    name: 'orderPurchaseIndex',
                    component: () => import('@views/order/purchase/index.vue'),
                },
                {
                    path: 'detail',
                    name: 'orderPurchaseDetails',
                    component: () => import('@views/order/purchase/details.vue'),
                    meta: {
                        title: '订单详情',
                    },
                },
                {
                    path: 'batch',
                    name: 'orderPurchaseBatch',
                    component: () => import('@views/order/purchase/deliveryList/index.vue'),
                    meta: {
                        title: '批量发货',
                    },
                },
            ],
        },
    ],
}
//系统配置
// const system: RouteRecordRaw = {
//     path: '/',
//     name: 'system',

//     component: MContent,
//     meta: {
//         title: '系统配置',
//     },
//     children: [
//         {
//             path: '/system/config/sms',
//             name: 'systemConfigSms',
//             meta: {
//                 title: '短信配置',
//             },
//             children: [
//                 {
//                     path: '',
//                     name: 'systemConfigSmsIndex',
//                     component: () => import('@views/system/sms.vue'),
//                 },
//             ],
//         },
//     ],
// }

// 财务管理
export const finance: RouteRecordRaw = {
    path: '/',
    name: 'finance',

    component: MContent,
    meta: {
        title: '财务管理',
        icon: 'iconfont icon-caiwu',
    },
    children: [
        {
            path: '/finance/reconciliation',
            name: 'reconciliation',
            meta: {
                title: '对账单',
            },
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
                title: '供应商结算',
            },
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
            children: [
                {
                    path: '',
                    name: 'InvoicingRequestChildren',
                    component: () => import('@views/finance/InvoicingRequest.vue'),
                },
            ],
        },
        // {
        //     path: '/finance/splitBill',
        //     name: 'splitBill',
        //     meta: {
        //         title: '分账单',
        //     },
        //     children: [
        //         {
        //             path: '',
        //             name: 'splitBillChildren',
        //             component: () => import('@views/finance/splitBill.vue'),
        //         },
        //     ],
        // },
    ],
}
//商城管理
export const mall: RouteRecordRaw = {
    path: '/',
    name: 'mall',

    component: MContent,
    meta: {
        title: '商城管理',
        icon: 'iconfont icon-shanghuguanli',
    },
    children: [
        {
            path: '/mall/permission',
            name: 'mallPermission',
            meta: {
                title: '系统管理',
            },
            children: [
                {
                    path: '',
                    name: 'mallPermission',
                    component: () => import('@views/mall/permission/Index.vue'),
                },
            ],
        },
        {
            path: '/mall/customer/service',
            name: 'messageCustomerService',
            meta: {
                title: '店铺消息',
            },
            children: [
                {
                    path: '',
                    name: 'messageCustomerServiceIndex',
                    component: () => import('@views/mall/customerService/Index.vue'),
                },
            ],
        },
        {
            path: '/mall/general/setting',
            name: 'mallGeneralSetting',
            meta: {
                title: '通用设置',
            },
            children: [
                {
                    path: '',
                    name: 'mallGeneralSetting',
                    component: () => import('@views/mall/settings/Index.vue'),
                },
            ],
        },
        {
            path: '/freight/logistics',
            name: 'freightLogistics',
            meta: {
                title: '快递配送',
            },
            children: [
                {
                    path: '',
                    name: 'freightLogisticsIndex',
                    component: () => import('@views/freight/Index.vue'),
                },
            ],
        },
        {
            path: '/message/notice',
            name: 'messageNotice',
            meta: {
                title: '平台公告',
            },
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
        {
            path: '/material/management',
            name: 'materialManagement',
            meta: { title: '素材管理' },
            children: [
                {
                    path: '',
                    name: 'materialManagementIndex',
                    component: () => import('@views/material/index.vue'),
                },
            ],
        },
        {
            path: '/download/center',
            name: 'DownloadCenter',
            component: MContent,
            meta: { title: '下载中心' },
            children: [{ path: '', name: 'DownloadCenterPage', component: () => import('@views/system/download-center/index.vue') }],
        },
    ],
}
// //公告
// export const notice: RouteRecordRaw = {
//     path: '/',
//     name: 'notice',

//     component: MContent,
//     meta: {
//         title: '消息中心',
//     },
//     children: [
//         {
//             path: '/message/notice',
//             name: 'messageNotice',
//             meta: {
//                 title: '系统公告',
//             },
//             children: [
//                 {
//                     path: '',
//                     name: 'messageNoticeIndex',
//                     component: () => import('@views/message/notice/Index.vue'),
//                 },
//                 {
//                     path: 'detail',
//                     name: 'messageNoticeDetail',
//                     component: () => import('@views/message/notice/detail/Index.vue'),
//                     meta: {
//                         title: '通知详情',
//                     },
//                 },
//             ],
//         },
//     ],
// }
// //配送管理
// export const freight: RouteRecordRaw = {
//     path: '/',
//     name: 'freight',

//     component: MContent,
//     meta: {
//         title: '配送管理',
//     },
//     children: [
//         {
//             path: '/freight/logistics',
//             name: 'freightLogistics',
//             meta: {
//                 title: '快递配送',
//             },
//             children: [
//                 {
//                     path: '',
//                     name: 'freightLogisticsIndex',
//                     component: () => import('@views/freight/Index.vue'),
//                 },
//             ],
//         },
//     ],
// }
