import { createBaseOrder, createShopOrder, createShopOrderItem } from './factory'

/**
 * 已关闭
 */
const closed = {
  // 未支付已关闭
  unpaidClosed: [
    createBaseOrder({
      status: 'SYSTEM_CLOSED',
      updateTime: '2025-02-22 13:21:14',
    }),
    createBaseOrder({
      status: 'BUYER_CLOSED',
      updateTime: '2025-02-25 15:03:16',
    }),
    createBaseOrder({
      status: 'SELLER_CLOSED',
      updateTime: '2025-01-15 14:49:36',
    }),
  ],
  // 支付退款已关闭
  paidClosed: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'BUYER_CLOSED',
          updateTime: '2025-02-13 14:40:52',
        }),
      ],
    }),
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'BUYER_CLOSED',
        }),
      ],
    }),
  ],
  // 拼团失败已关闭
  TeamFailClosed: [
    createBaseOrder({
      status: 'TEAM_FAIL',
      updateTime: '2025-01-10 13:46:00',
    }),
  ],
  // 售后关闭
  afterSalesClosed: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'BUYER_CLOSED',
          updateTime: '2025-03-05 10:30:22',
          shopOrderItems: [
            createShopOrderItem({
              status: 'CLOSED',
              afsStatus: 'RETURNED_REFUNDED',
              packageStatus: 'WAITING_FOR_DELIVER',
            }),
          ],
        }),
      ],
    }),
  ],
  // 待评价申请售后关闭
  commentAfterSaleClosed: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'BUYER_CLOSED',
          updateTime: '2025-03-08 11:25:40',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'BUYER_WAITING_FOR_COMMENT',
              status: 'CLOSED',
              afsStatus: 'RETURNED_REFUNDED',
            }),
          ],
        }),
      ],
    }),
  ],
}

/**
 * 待付款
 */
const unpaid = {
  unpaid: [
    createBaseOrder({
      status: 'UNPAID',
    }),
  ],
  // 拼团待支付
  teamUnpaid: [
    createBaseOrder({
      status: 'UNPAID',
      type: 'TEAM',
      extra: {
        teamNo: 'TN202503150002',
        distributionMode: 'EXPRESS',
      },
    }),
  ],
}

/**
 * 待发货
 */
const unshipped = {
  // 全部待发货
  allUnshipped: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_DELIVER',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 部分待发货
  partUnshipped: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_RECEIVE',
              status: 'OK',
            }),
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_DELIVER',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 同城待发货
  localUnshipped: [
    createBaseOrder({
      status: 'PAID',
      icStatus: 'PRICE_PADDING',
      extra: {
        distributionMode: 'INTRA_CITY_DISTRIBUTION',
      },
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_DELIVER',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 分批售后成功后待发货
  afterSaleUnshipped: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_DELIVER',
              status: 'OK',
            }),
            createShopOrderItem({
              status: 'CLOSED',
              afsStatus: 'RETURNED_REFUNDED',
            }),
          ],
        }),
      ],
    }),
  ],
}

/**
 * 待收货
 */
const waitDelivery = {
  // 普通待收货
  waitDelivery: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_RECEIVE',
              status: 'OK',
            }),
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_RECEIVE',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 到店自提待收货
  selfPickupWaitDelivery: [
    createBaseOrder({
      status: 'PAID',
      extra: {
        distributionMode: 'SHOP_STORE',
      },
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'WAITING_FOR_RECEIVE',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 同城待收货
  localWaitDelivery: {
    // 待接单
    waitingForAccept: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'PENDING',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 待到店
    waitingForArrival: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'TAKEN',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 待取货
    waitingForPickup: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'ARRIVED_SHOP',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 配送中
    delivering: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'PICKUP',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 已送达
    delivered: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'DELIVERED',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 配送异常
    error: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'ERROR',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                sellType: 'OWN',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 配送异常之后处理成待发货
    errorToUnshipped: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'ERROR',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_DELIVER',
                sellType: 'OWN',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 配送异常之后处理成待收货已送达
    errorToDelivered: [
      createBaseOrder({
        status: 'PAID',
        icStatus: 'DELIVERED',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'OK',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                sellType: 'OWN',
                status: 'OK',
              }),
            ],
          }),
        ],
      }),
    ],
    // 配送异常之后处理成已关闭(全额退款)
    errorToClosed: [
      createBaseOrder({
        status: 'SELLER_CLOSED',
        icStatus: 'ERROR',
        extra: {
          distributionMode: 'INTRA_CITY_DISTRIBUTION',
        },
        shopOrders: [
          createShopOrder({
            status: 'SELLER_CLOSED',
            updateTime: '2025-03-05 10:30:22',
            shopOrderItems: [
              createShopOrderItem({
                packageStatus: 'WAITING_FOR_RECEIVE',
                sellType: 'OWN',
                status: 'CLOSED',
              }),
            ],
          }),
        ],
      }),
    ],
  },
}

/**
 * 待评价
 */
const waitComment = {
  // 买家待评价
  buyerWaitComment: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'BUYER_WAITING_FOR_COMMENT',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 系统待评价
  systemWaitComment: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'SYSTEM_WAITING_FOR_COMMENT',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
}

/**
 * 已完成
 */
const completed = {
  // 买家已评价完成
  buyerCommented: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          updateTime: '2025-03-10 15:20:30',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'BUYER_COMMENTED_COMPLETED',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
  // 系统自动好评完成
  systemCommented: [
    createBaseOrder({
      status: 'PAID',
      shopOrders: [
        createShopOrder({
          status: 'OK',
          updateTime: '2025-03-12 09:15:45',
          shopOrderItems: [
            createShopOrderItem({
              packageStatus: 'SYSTEM_COMMENTED_COMPLETED',
              status: 'OK',
            }),
          ],
        }),
      ],
    }),
  ],
}

/**
 * 拼团中
 */
const teaming = {
  // 拼团中
  teaming: [
    createBaseOrder({
      status: 'TEAMING',
      type: 'TEAM',
      extra: {
        teamNo: 'TN202503150001',
        distributionMode: 'EXPRESS',
      },
    }),
  ],
}

/**
 * 订单状态 单元测试数据
 */
export default {
  closed,
  unpaid,
  unshipped,
  waitDelivery,
  waitComment,
  completed,
  teaming,
}
