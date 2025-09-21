<script setup lang="ts">
import type { PropType } from 'vue'
import { MessageAndShopAdmin } from '@/views/personalcenter/set/customerservice/types'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const $router = useRouter()
const props = defineProps({
  message: {
    type: Object as PropType<MessageAndShopAdmin>,
    required: true,
  },
  isMine: {
    type: Boolean,
    default: false,
  },
})
const product = computed(() => {
  const productMsg = props.message.message
  if (!productMsg) {
    return { id: '', name: '未正确获取商品信息', salePrices: [], pic: '' }
  }
  return JSON.parse(productMsg)
})
const handleNavToOrderList = (row: any) => {
  if (row) {
    const routeUrl = $router.resolve({
      name: 'orderdetail',
      query: { orderNo: row.no, packageId: row.commodityList[0].packageId, shopId: row.commodityList[0].shopId },
    })
    window.open(routeUrl.href, '_blank')
  }
}
const time = (dateString: any) => {
  if (dateString) {
    const date = new Date(dateString)
    const formattedDate = date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
    // 移除日期中的分隔符
    return formattedDate.replace(/\//g, '-')
  }
}
</script>
<template>
  <div :class="['order', isMine ? 'isMine' : 'noMine']">
    <div class="order_head">
      <span>订单号：{{ product.no }}</span>
      <span>{{ time(product?.createTime) }}</span>
    </div>
    <div v-for="(item, index) in product.commodityList" :key="index" class="order_info">
      <el-image class="product_pic" :src="item.pic" fit="fill" />
      <div class="order_info_right">
        <p class="order_info_right_name">{{ item.name ? item.name : '未正确获取商品信息' }}</p>
        <div>
          <span
            >￥<span style="font-weight: bold">{{ item.amountRealPay }}</span></span
          >
          <span style="margin-left: 16px">共{{ item.num }}件商品</span>
        </div>
      </div>
    </div>
    <div class="order_btn">
      <span :style="{ color: product.afsStatu?.length > 6 ? '#F54319' : product.afsStatu === '待支付' ? '#FD9224' : '#333' }">{{
        product.afsStatu.desc ? product.afsStatu.desc : product.afsStatu
      }}</span>
      <p @click="handleNavToOrderList(product)">查看订单</p>
    </div>
  </div>
</template>
<style scoped lang="scss">
.message-content-product {
  margin: $rows-spacing-row-sm;
  height: 200px;
}
.product-box {
  background: $rows-text-color-inverse;
  border-radius: $rows-border-radius-sm;
  padding: $rows-spacing-row-sm;
  border: 1px solid var(--el-border-color);
  height: 100%;
  display: flex;
}
.product-box__right {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.product-box__right--footer {
  display: flex;
  justify-content: space-between;
}
.product-info {
  color: $rows-text-color-grey;
  flex: 1;
  display: flex;
  padding-left: $rows-spacing-col-lg;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}
.product-info .product-name {
  height: 40px;
  font-size: 15px;
  width: 100%;
  @include utils-ellipsis(2);
}
.product-info .product-prices {
  height: 60px;
  font-size: 20px;
  color: $rows-color-error;
  @include utils-ellipsis(2);
}
.isMine {
  border: 1px solid rgb(85, 92, 253);
  margin-right: 10px;
  border-radius: 8px 0 8px 8px;
}
.noMine {
  border: 1px solid #e5e5e5;
  margin-left: 10px;
  border-radius: 0px 8px 8px 8px;
}

.order {
  width: 378px;
  padding: 16px;
  background-color: #fff;
  .order_head {
    width: 100%;
    line-height: 25px;
    padding-bottom: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #999;
  }
  .order_info {
    width: 100%;
    display: flex;
    padding-bottom: 20px;
    .product_pic {
      width: 70px;
      height: 70px;
    }
    .order_info_right {
      height: 70px;
      margin-left: 10px;
      padding: 2px 0;
      width: 258px;
      font-size: 14px;
      color: #333;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      .order_info_right_name {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
  .order_btn {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    p {
      background-color: rgba(85, 92, 253, 0.1);
      color: #555cfd;
      padding: 5px 14px;
      border-radius: 20px;
      cursor: pointer;
    }
  }
}
</style>
