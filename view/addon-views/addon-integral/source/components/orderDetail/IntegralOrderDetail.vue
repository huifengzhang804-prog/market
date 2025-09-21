<script setup lang="ts">
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import { doGetIntegralOrderInfoByNo } from '../../apis'
import { ElMessage } from 'element-plus'
import { IntegralOrderStatusCn } from '../ts'
import type { IntegralOrderInfo } from '../types/order'
import useConvert from '@/composables/useConvert'
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const { divTenThousand } = useConvert()
const stepsActive = ref(0)
const $route = useRoute()
const orderNo = $route.query.no as string | null
// 订单备注信息
const remark = ref<{ [x: string]: string }>({})
const orderInfo = ref<IntegralOrderInfo>({
  sellerRemark: '',
  buyerRemark: '',
  payTime: '',
  deliveryTime: '',
  accomplishTime: '',
  buyerId: '',
  buyerNickname: '',
  createTime: '',
  freightPrice: 0,
  image: '',
  integralOrderReceiver: {
    address: '',
    area: [],
    createTime: '',
    id: '',
    mobile: '',
    name: '',
    orderNo: '',
    updateTime: '',
  },
  no: '',
  num: 1,
  price: '',
  productName: '',
  status: 'PAID',
  salePrice: '',
  productType: 'REAL_PRODUCT',
  affiliationPlatform: 0,
})

initOrderInfo()

async function initOrderInfo() {
  if (orderNo) {
    const { code, msg, data } = await doGetIntegralOrderInfoByNo(orderNo)
    if (code !== 200 || !data) {
      ElMessage.error(msg || '订单详情获取失败')
      return
    }
    orderInfo.value = data
    initStepsActive()
  }
}
/**
 * 步骤条初始化
 */
function initStepsActive() {
  if (orderInfo.value?.accomplishTime) {
    stepsActive.value = 2
  } else if (orderInfo.value?.deliveryTime) {
    stepsActive.value = 1
  } else if (orderInfo.value?.payTime) {
    stepsActive.value = 0
  }
}
/**
 * 总价格计算
 */
const totalPrice = computed(() => {
  if (orderInfo.value.price && orderInfo.value.num) {
    return orderInfo.value.price
  }
  return 0
})
const freightPrice = computed(() => {
  if (orderInfo.value.freightPrice) {
    return divTenThousand(orderInfo.value.freightPrice)
  }
  return 0
})
const totalSalePrice = computed(() => {
  if (orderInfo.value.salePrice && orderInfo.value.num) {
    return orderInfo.value.salePrice
  }
  return 0
})
</script>

<template>
  <div style="padding: 0 16px; overflow-y: scroll">
    <div class="orderInfo">
      <div style="display: flex; align-items: center">
        <div class="orderInfo__title">{{ IntegralOrderStatusCn[orderInfo?.status] }}</div>
        <div class="orderInfo__steps">
          <el-steps :active="stepsActive" align-center process-status="finish">
            <el-step title="买家已付款" :description="orderInfo.payTime" />
            <el-step title="商家发货" :description="orderInfo.deliveryTime" />
            <el-step title="成功订单" :description="orderInfo.accomplishTime" />
          </el-steps>
        </div>
      </div>
      <div class="orderInfo__userInfo">
        <div class="orderInfo__userInfo--left">
          <div class="orderInfo__userInfo--title">物流信息</div>
          <div>用户昵称：{{ orderInfo.buyerNickname || '无' }}</div>
          <div>买家姓名：{{ orderInfo.integralOrderReceiver.name || '无' }}</div>
          <div>买家手机号：{{ orderInfo.integralOrderReceiver.mobile || '无' }}</div>
          <div>收货地址：{{ orderInfo.integralOrderReceiver.area?.join('') }} {{ orderInfo.integralOrderReceiver.address }}</div>
          <remark-view v-for="(item, val, index) in remark" :key="index" class="detail__item" :remark-key="val" :remark="remark" />
        </div>
        <div class="orderInfo__userInfo--right">
          <div class="orderInfo__userInfo--title">订单信息</div>
          <div>订单编号：{{ orderInfo.no }}</div>
          <div>创建时间：{{ orderInfo.createTime }}</div>
        </div>
      </div>
      <el-table
        :cell-style="
                    ({ row, column, rowIndex, columnIndex }:any) => {
                        if (columnIndex === 0)
                            return {
                                borderBottom: 'none',
                            }
                    }
                "
        :data="[orderInfo]"
        style="width: 100%"
        calss="orderInfo__tab"
        border
      >
        <el-table-column label="商品" width="230" align="center">
          <template #default="{ row }: { row: IntegralOrderInfo }">
            <div class="orderInfo__tab--goods">
              <el-image fits="cover" style="width: 70px; height: 70px" shape="square" size="large" :src="row.image" :title="row.productName" />
              <div class="orderInfo__tab--goods-right">
                <div class="orderInfo__tab--goods-right-show">{{ row.productName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价&amp;数量" align="center">
          <template #default="{ row }: { row: IntegralOrderInfo }">
            <div class="orderInfo__tab--price">
              <div>单价：{{ row.price }}&nbsp;积分</div>
              <div>数量：&nbsp;*{{ row.num }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="总价" align="center">
          <template #default="{ row }: { row: IntegralOrderInfo }">
            <div>
              总价： {{ row.price }}&nbsp;积分 <span v-if="row.salePrice !== '0'"> + ￥ {{ divTenThousand(row.salePrice) }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="orderInfo__priceInfo">
      <span> 运费:￥{{ freightPrice }}</span>
      <span
        >商品总价:{{ totalPrice }}&nbsp;积分 <span v-if="+totalSalePrice !== 0"> + ￥ {{ divTenThousand(totalSalePrice) }}</span>
      </span>
    </div>
    <div class="orderInfo__footer">
      实收款：<span
        >{{ totalPrice }}&nbsp;积分 <span v-if="+totalSalePrice !== 0"> + ￥ {{ +divTenThousand(totalSalePrice) + +freightPrice }}元</span>
      </span>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(orderInfo) {
  @include e(title) {
    padding: 20px;
    font-size: 18px;
    font-weight: Bold;
    color: #515151;
    width: 223px;
    text-align: center;
  }
  @include e(steps) {
    padding: 20px;
    margin: 20px;
    width: 700px;
    border-left: 1px solid #d5d5d5;
  }
  @include e(priceInfo) {
    height: 40px;
    color: #999999;
    @include flex(space-between, flex-end);
    flex-direction: column;
    margin: 50px 0 20px 0;
  }
  @include e(shopName) {
    font-size: 14px;
    color: #333333;
    font-weight: Bold;
    margin: 22px 0 12px 5px;
  }
  @include e(footer) {
    text-align: right;
    & span:nth-child(n + 1) {
      font-size: 20px;
      font-weight: bold;
      color: #ee420d;
    }
  }
  @include e(userInfo) {
    display: flex;
    background: #f7f8fa;
    margin-bottom: 22px;
    padding: 0 30px;
    @include m(left) {
      flex: 0.5;
      & div:nth-of-type(n + 2) {
        margin-bottom: 11px;
      }
    }
    @include m(right) {
      flex: 0.5;
      & div:nth-of-type(n + 2) {
        margin-bottom: 11px;
      }
    }
    @include m(title) {
      font-size: 14px;
      color: #333333;
      font-weight: Bold;
      margin: 17px 0;
    }
  }
  @include e(tab) {
    @include m(goods) {
      display: flex;
      justify-content: space-between;
    }
    @include m(goods-right) {
      font-size: 12px;
      color: #586884;
      width: 132px;
      margin-left: 10px;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      flex-direction: column;
    }
    @include m(goods-right-show) {
      @include utils-ellipsis(2);
    }
    @include m(price) {
      @include flex();
      font-size: 12px;
      color: #50596d;
      flex-direction: column;
    }
  }
}

//去除table的每一条数据的下边框
.el-table td {
  border-bottom: none;
}
//去除表格的最下面一行的边框
.tableStyle::before {
  width: 0;
}
//设置表的外边框
.el-table {
  border-radius: 9px;
  border: 1px solid #d5d5d5;
}
.el-table th.is-leaf {
  border-bottom: none;
}
</style>
