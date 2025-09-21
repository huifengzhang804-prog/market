<script lang="ts" setup>
import Decimal from 'decimal.js'
import useOrderBasicDetails from '../../hooks/useOrderBasicDetails'
import { PropType } from 'vue'
import { ApiPurchaseOrder } from '../../../../../../source'
import PayOrder from '../pay-order.vue'
import useShowProof from '../../hooks/useShowProof'

const $props = defineProps({
  order: {
    type: Object as PropType<ApiPurchaseOrder>,
    default: () => ({}),
  },
  // eslint-disable-next-line vue/require-default-prop
  reload: {
    type: Object as PropType<() => any>,
    default: () => ({}),
  },
})
const {
  orderDetails,
  stepInfo,
  payTypeMap,
  divTenThousand,
  computedCalculateFreight,
  computedCalculateCommodityPrice,
  handlePayOrder,
  showPayDialog,
  handlePay,
  payOrderRef,
  copyOrderNo,
  handleCopyReceiver,
} = useOrderBasicDetails($props.order, $props.reload)
const { showProof, goToShowProof, currentProof } = useShowProof()
</script>

<template>
  <div class="details">
    <div class="details__status">
      <div class="details__status--title">
        <p style="font-size: 26px">{{ stepInfo.statusText }}</p>
        <el-button v-if="stepInfo.statusText === '待支付'" type="danger" @click="handlePay">去支付</el-button>
      </div>
      <div class="details__status--steps">
        <el-steps :active="stepInfo.activeStep" align-center finish-status="finish">
          <el-step :description="orderDetails?.createTime" title="买家下单" />
          <el-step :description="orderDetails?.timeNodes?.payTime" title="买家付款" />
          <el-step :description="orderDetails?.timeNodes?.deliveryTime" title="卖家发货" />
          <el-step :description="orderDetails?.timeNodes?.receiveTime" title="买家收货" />
        </el-steps>
      </div>
    </div>
    <div class="details__userInfo">
      <div class="details__userInfo--left">
        <div class="details__userInfo--title">订单信息</div>
        <div>
          <span>订单编号：{{ orderDetails?.no }}</span>
          <span class="copy" @click="copyOrderNo(orderDetails?.no || '')">复制</span>
        </div>
        <div>下单时间：{{ orderDetails?.createTime }}</div>
        <div>支付时间：{{ orderDetails?.timeNodes?.payTime }}</div>
        <div>
          <span>支付方式：{{ payTypeMap[orderDetails?.extra?.pay?.payType!] }}</span>
          <span
            v-if="orderDetails?.extra?.pay?.payType === 'OFFLINE'"
            class="copy"
            @click="goToShowProof({ extra: { pay: { proof: orderDetails?.extra?.pay?.proof || '' } } })"
            >付款凭证</span
          >
        </div>
        <div>配送方式：快递配送</div>
      </div>
      <div class="details__userInfo--left">
        <div class="details__userInfo--title">
          <span>收货人信息</span>
          <span class="copy" @click="handleCopyReceiver">复制</span>
        </div>
        <div>收货人姓名：{{ orderDetails?.extra?.receiver?.name }}</div>
        <div>联系人电话：{{ orderDetails?.extra?.receiver?.mobile }}</div>
        <div>
          收货地址：
          {{ orderDetails?.extra?.receiver.area?.join('') }}
          {{ orderDetails?.extra?.receiver?.address }}
        </div>
        <div>采购备注：{{ orderDetails?.extra?.remark }}</div>
      </div>
      <div class="details__userInfo--right">
        <div class="details__userInfo--title">供应商信息</div>
        <div>供应商名称：{{ orderDetails?.extraInfo?.supplierName }}</div>
        <div>联系人电话：{{ orderDetails?.extraInfo?.supplierPhone }}</div>
      </div>
    </div>
    <div class="details__table">
      <el-table :data="orderDetails?.orderItems" border class="details__table--main">
        <el-table-column align="center" label="商品" width="460">
          <template #default="{ row }">
            <div class="commodity">
              <el-image v-if="row?.image" :src="row?.image" fit="cover" style="width: 70px; height: 70px" />
              <div class="commodity__info">
                <span class="commodity__info--title">{{ row?.productName }}</span>
                <span class="commodity__info--spec">{{ row?.specs?.join(';') }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购单价">
          <template #default="{ row }">
            {{ divTenThousand(row?.salePrice) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购数量" prop="num" />
        <el-table-column align="center" label="采购金额">
          <template #default="{ row }">
            {{ divTenThousand(row?.salePrice).mul(new Decimal(row.num)) }}
          </template>
        </el-table-column>
        <el-table-column label="实际入库量" prop="used" align="center"> </el-table-column>
      </el-table>
    </div>
    <div class="details__subtotal">
      <div class="details__subtotal--title">订单总计</div>
      <div class="details__subtotal--line">采购总数：{{ orderDetails?.orderItems?.reduce((pre: number, item: any) => pre + item.num, 0) }}</div>
      <div class="details__subtotal--line">
        商品总价：<span class="text-red">{{ computedCalculateCommodityPrice(orderDetails?.orderItems) }}</span>
      </div>
      <div class="details__subtotal--line">
        运费：<span class="text-red">{{ computedCalculateFreight(orderDetails?.orderItems!) }}</span>
      </div>
      <div class="details__subtotal--line pay-price">
        采购金额({{ orderDetails?.status === 'PAID' ? '已付款' : '应付款' }})：<span class="text-red"
          >{{ divTenThousand(orderDetails?.payAmount) }}元</span
        >
      </div>
    </div>
  </div>
  <el-dialog v-model="showPayDialog" title="支付订单" width="500px">
    <PayOrder ref="payOrderRef" :price="divTenThousand(orderDetails?.payAmount)" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePayOrder"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="showProof" title="付款凭证" width="500px">
    <img :src="currentProof" class="proof-img" />
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(details) {
  overflow-y: auto;
  padding: 0 30px 30px 30px;
  @include e(status) {
    display: flex;
    align-items: center;
    @include m(steps) {
      padding: 20px;
      margin: 20px;
      width: 700px;
      border-left: 1px solid #d5d5d5;
    }
    @include m(title) {
      font-size: 28px;
      font-weight: Bold;
      color: #515151;
      width: 223px;
      text-align: center;
    }
  }
  @include e(userInfo) {
    display: flex;
    margin-bottom: 22px;
    padding: 0 30px;
    @include m(left) {
      flex: 0.5;
      & div:nth-of-type(n + 2) {
        margin-bottom: 11px;
      }
    }
    @include m(right) {
      margin-left: 30px;
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
      @include flex(space-between, center);
    }
  }
  @include e(table) {
    @include m(main) {
      height: 300px;
      @include b(commodity) {
        display: flex;
        align-items: stretch;
        @include e(info) {
          margin-left: 15px;
          flex: 1;
          overflow: hidden;
          display: flex;
          flex-direction: column;
          justify-content: flex-start;
          align-items: flex-start;
          text-align: left;
          @include m(title) {
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
            width: 100%;
          }
          @include m(spec) {
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
            font-size: 0.8em;
            width: 100%;
          }
        }
      }
    }
  }
  &__subtotal {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    width: 100%;
    margin-top: 30px;
    line-height: 1.5;
    @include m(title) {
      font-size: 1.3em;
      font-weight: 600;
    }
    @include b(pay-price) {
      font-size: 1.2em;
    }
  }
  @include b(text-red) {
    color: #f00;
  }
}

.proof-img {
  width: 350px;
  height: 350px;
  object-fit: contain;
}

.copy {
  margin-left: 5px;
  color: #409eff;
  cursor: pointer;
}
</style>
