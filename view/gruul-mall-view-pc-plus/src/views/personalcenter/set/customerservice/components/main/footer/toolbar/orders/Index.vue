<template>
  <div v-if="searchType.total * 1 !== 0" class="order_con">
    <div ref="scrollContainer" class="order_list" @scroll="handleScroll">
      <div v-for="(item, index) in orderList" :key="index" class="order_item">
        <div class="order_item_head">
          <span style="color: #999">订单号：{{ item.no }}</span>
          <!-- <span style="color: #333; font-weight: 500">{{ calculate(item as any).state.status }}</span> -->
        </div>
        <div v-for="(text, key) in item.shopOrders[0].shopOrderItems" :key="key" class="order_item_goods">
          <div class="order_goods_cont">
            <img :src="text.image" alt="" />
            <div class="order_goods_info">
              <div class="order_goods_info_left">
                <p class="order_goods_info_name">{{ text.productName }}</p>
                <div class="order_goods_info_sending">
                  <p @click="SendAnOrder(item)">发送</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="order_info">
          <div>
            <div class="order_info_type">
              <p>
                订单金额：<text style="color: #f54319">￥{{ divTenThousand(item.orderPayment.totalAmount) }}</text>
              </p>
              <p v-if="orderStatusHandle[item.status]?.next">
                <template v-if="isAllItemClosed(item.shopOrders[0].shopOrderItems)">
                  <div c-c-ccc>已取消</div>
                </template>
                <template v-else>
                  <div :style="{ color: `${packageStatusHandle[item.shopOrders[0].shopOrderItems[0].packageStatus].color}` }">
                    {{ packageStatusHandle[item.shopOrders[0].shopOrderItems[0].packageStatus].desp }}
                  </div>
                </template>
              </p>
              <p v-else>
                <span :class="orderStatusHandle[item.status]?.toPaid ? 'c-c-FF6C00' : 'c-c-ccc'">
                  {{ orderStatusHandle[item.status]?.desp }}
                </span>
              </p>
            </div>
            <div class="order_info_time">
              <p>
                下单时间：<text style="color: #999">{{ item.createTime }}</text>
              </p>
            </div>
          </div>
        </div>
      </div>
      <div v-if="loading" class="footer"><span>加载中...</span></div>
      <div v-else class="footer">
        <span>已经到底</span>
      </div>
    </div>
  </div>
  <div v-else class="order_noOrder">
    <div class="noOrder">
      <p>没有关联的订单</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { doGetOrder } from '@/apis/order'
import { ApiOrder } from '@/views/personalcenter/order/myorder/types'
import { MessageType, MessageUser } from '@/views/personalcenter/set/customerservice/types'
import type { PropType } from 'vue'
import { ref, reactive, computed } from 'vue'
import { orderStatusHandle, packageStatusHandle, isAllItemClosed } from '@/utils/OrderStatusHelper'
import { useRoute } from 'vue-router'

const props = defineProps({
  user: {
    type: Object as PropType<MessageUser>,
    default: () => {},
  },
})
const route = useRoute()
const { divTenThousand } = useConvert()
const loading = ref(false)
const orderList = ref<ApiOrder[]>([])
const scrollContainer = ref()
const searchType = reactive({
  shopIds: '',
  orderNo: route.query.orderNo || '',
  current: 1,
  size: 10,
  total: 0,
})
const emits = defineEmits(['orderHandle'])

watch(
  () => props.user,
  (val) => {
    if (val) {
      searchType.shopIds = props.user.chatWithShopInfo?.shopId
      searchType.orderNo = ''
      orderList.value = []
      loadMoreItems()
    }
  },
)

const loadMoreItems = async () => {
  loading.value = true
  const { data, code } = await doGetOrder({ ...searchType })
  if (code === 200) {
    searchType.total = data.total
    loading.value = false
    orderList.value = [...orderList.value, ...data.records]
  }
}

const handleScroll = () => {
  const container = scrollContainer.value
  if (container.scrollTop + container.clientHeight >= container.scrollHeight) {
    searchType.current++
    loadMoreItems()
  }
}

const SendAnOrder = (item: ApiOrder) => {
  const { shopOrderItems } = item.shopOrders[0]
  const { productName, image, productId } = item.shopOrders[0].shopOrderItems[0]
  let afsStatu = ''
  if (orderStatusHandle[item.status]?.next) {
    if (isAllItemClosed(item.shopOrders[0].shopOrderItems)) {
      afsStatu = '已取消'
    } else {
      afsStatu = packageStatusHandle[item.shopOrders[0].shopOrderItems[0].packageStatus].desp
    }
  } else {
    afsStatu = orderStatusHandle[item.status]?.desp
  }
  emits('orderHandle', {
    messageType: MessageType.ORDER,
    content: JSON.stringify({
      id: productId,
      name: productName,
      salePrices: item.orderPayment.totalAmount,
      pic: image,
      amountRealPay: divTenThousand(
        shopOrderItems.reduce((sum: any, text: any) => {
          return sum + text.dealPrice
        }, 0),
      ).toFixed(2),
      no: item.no,
      afsStatu: afsStatu,
      createTime: item.createTime,
      commodityList: item.shopOrders[0].shopOrderItems.map((text: any) => {
        return {
          ...text,
          pic: text.image,
          name: text.productName,
          amountRealPay: divTenThousand(text.salePrice)?.toFixed(2),
          dealPrice: divTenThousand(text.dealPrice)?.toFixed(2),
        }
      }),
      my: true,
    }),
  })
}
</script>

<style lang="scss" scoped>
.order_con {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  padding: 0 6px;
  .order_list::-webkit-scrollbar {
    width: 5px; /* 设置滚动条的宽度 */
    height: 10px;
  }
  .order_list {
    overflow-y: auto;
    width: 100%;
    padding: 12px 6px 0 6px;

    .order_item {
      padding: 12px;
      margin-bottom: 20px;
      border-radius: 5px;
      display: flex;
      flex-direction: column;
      border: 1px solid #e9ecf0;

      .order_item_head {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      .order_item_goods {
        border-bottom: 1px solid #e9ecf0;
        border-bottom: 1px solid #e9ecf0;
      }

      .order_goods_cont {
        display: flex;
        height: 70px;
        margin: 16px 0;

        img {
          width: 70px;
          height: 70px;
        }

        .order_goods_info {
          display: flex;
          justify-content: space-between;
          flex: 1;
          padding-left: 10px;

          .order_goods_info_left {
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .order_goods_info_name {
              font-size: 13px;
              color: #333;
              overflow: hidden;
              text-overflow: ellipsis;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
            }

            .order_goods_info_sending {
              display: flex;
              justify-content: end;
              width: 210px;
              p {
                width: 58px;
                text-align: center;
                line-height: 26px;
                color: #333;
                border-radius: 40px;
                cursor: pointer;
                border: 1px solid #c9c9c9;
              }
              span {
              }
            }
          }

          .order_item_info_right {
            color: #333;
            height: 37px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            align-items: end;
          }
        }
      }

      .order_info {
        margin-top: 15px;
        .order_info_type {
          display: flex;
          justify-content: space-between;
          margin-bottom: 10px;
          align-items: center;
        }
      }
    }

    .order_item_foot {
      display: flex;
      justify-content: end;
      p {
        background-color: rgba(85, 92, 253, 0.1);
        color: #555cfd;
        padding: 5px 14px;
        border-radius: 20px;
        cursor: pointer;
      }
    }
  }
  .footer {
    text-align: center;
    font-size: 12px;
    color: #333;
    padding: 15px 0;
  }
}
.order_noOrder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  .noOrder {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #999;
    img {
      width: 206px;
      height: 144px;
    }
    p {
      margin-top: 30px;
    }
  }
}
</style>
