<script setup lang="ts">
import type { ORDERSTATUS } from '../'
import Decimal from 'decimal.js'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
import useListCalculation from './hooks/useListCalculation'
import useConvert from '@/composables/useConvert'
import { useRouter } from 'vue-router'
import { Obj } from '@/utils/types'

const { useSingleCalculationFormula, useTotalPrice } = useListCalculation()

const { toClipboard } = useClipboard()
const { divTenThousand, divHundred } = useConvert()
const $props = defineProps({
  orderInfo: {
    type: Object,
    default() {
      return {
        id: null,
        type: null,
        name: '',
        url: '',
        append: '',
      }
    },
  },
  toolsDisabled: {
    type: Boolean,
    default: false,
  },
  checked: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:checked', 'change'])

const handleChange = (val: boolean) => {
  emit('update:checked', val)
  emit('change')
}

function getBonus(num: string, purchase?: boolean) {
  let total = new Decimal(0)
  if ('items' in $props.orderInfo && $props.orderInfo.items.length) {
    $props.orderInfo.items.forEach((item: any) => {
      if (purchase || item[num].userId) {
        total = total.add(divTenThousand(item[num].bonus || 0))
      }
    })
  }

  return total
}
function getcount(orderStatus?: string) {
  let count = new Decimal(0)
  if ('items' in $props.orderInfo && $props.orderInfo.items.length) {
    $props.orderInfo.items.forEach((item: any) => {
      if (orderStatus && item.orderStatus !== orderStatus) return
      if (item.one.bonus) {
        count = count.add(divTenThousand(item.one.bonus))
      }
      if (item.two.bonus) {
        count = count.add(divTenThousand(item.two.bonus))
      }
      if (item.three.bonus) {
        count = count.add(divTenThousand(item.three.bonus))
      }
      if (item.purchase && item.one.userId) {
        count = count.add(divTenThousand(item.one.bonus))
      }
    })
  }

  return count.toNumber()
}
function getOrderStatus(status: keyof typeof ORDERSTATUS) {
  return status === 'COMPLETED' ? '已赚' : status === 'CLOSED' ? '已失效' : '待结算'
}
async function copy(orderNo: string) {
  try {
    await toClipboard(orderNo)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
/**
 * 查看详情
 */
const $router = useRouter()
const handleCheckDetails = (orderNo: string, shopItems: any) => {
  const query: Obj = { orderNo }
  const firstItem = shopItems[0]
  const packageId = firstItem?.packageId ? firstItem.packageId : ''
  packageId && (query.packageId = packageId)
  $router.push({ name: 'detailsIndex', query: query })
}
</script>

<template>
  <div style="margin-top: 10px">
    <div class="head">
      <el-checkbox :model-value="checked" :disabled="toolsDisabled" style="margin-right: 12px" @update:model-value="handleChange"></el-checkbox>
      <div>订单号：{{ orderInfo.orderNo }}</div>
      <div>下单时间：{{ orderInfo.createTime }}</div>
      <div class="buyer-container">
        <el-tooltip :disabled="toolsDisabled" :content="'买家：' + orderInfo.buyerNickname" :raw-content="true">
          <span class="buyer">买家：{{ orderInfo.buyerNickname }}</span>
        </el-tooltip>
        <el-tooltip
          v-if="orderInfo.items[0].purchase"
          :disabled="toolsDisabled"
          :raw-content="true"
          :content="
            orderInfo.items[0].purchase
              ? useSingleCalculationFormula(orderInfo.items?.[0], 'one', getBonus('one', true), orderInfo.items)
              : '该层级无分销员，不计算对应佣金'
          "
          placement="bottom"
          effect="light"
        >
          <span>
            <span>(</span>
            <span style="color: #f00"
              >￥{{ orderInfo.items[0].one.userId || orderInfo.items?.[0].purchase ? getBonus('one', orderInfo.items?.[0].purchase) : 0 }}</span
            >
            <span>)</span>
            <span v-if="orderInfo.items[0].purchase" style="color: #f54319; margin-left: 5px">内购</span>
          </span>
        </el-tooltip>
      </div>
      <div class="last">
        实付款：<span style="color: #f54319">￥{{ divTenThousand(orderInfo.payAmount).toFixed(2) }}</span>
      </div>
      <!-- <div>{{ getOrderStatus(items[0].orderStatus) }}</div> -->
      <el-button
        type="primary"
        size="small"
        text
        style="margin-left: auto; position: sticky; right: 0"
        @click="handleCheckDetails(orderInfo.orderNo, orderInfo)"
      >
        查看详情
      </el-button>
    </div>
    <div class="content">
      <el-table :data="orderInfo.items" border style="width: 433px; flex-shrink: 0" :row-style="{ height: '100px' }">
        <el-table-column prop="name">
          <template #default="{ row }">
            <div class="goods">
              <div class="goods__pic">
                <img :src="row.image" style="width: 68px; height: 68px" />
              </div>

              <div class="goods__info">
                <div class="goods__info-flex">
                  <el-tooltip
                    v-if="row.productName.length >= 32"
                    :disabled="toolsDisabled"
                    class="box-item"
                    effect="dark"
                    :content="row.productName"
                    placement="bottom-end"
                  >
                    <div class="goods__info-flex--name" style="line-height: 16px">
                      <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                      {{ row.productName }}
                    </div>
                  </el-tooltip>
                  <div v-else class="goods__info-flex--name" style="line-height: 16px">
                    <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                    {{ row.productName }}
                  </div>
                  <div class="goods__info-flex--num">￥{{ divTenThousand(row.dealPrice) }}</div>
                </div>
                <div class="goods__info-flex">
                  <el-tooltip
                    v-if="row.specs.join('、').length > 15"
                    :disabled="toolsDisabled"
                    class="box-item"
                    effect="dark"
                    :content="row.specs.join('、')"
                    placement="bottom"
                  >
                    <div class="goods__info-flex--specs">{{ row.specs && row.specs.join('、') }}</div>
                  </el-tooltip>
                  <div v-else class="goods__info-flex--specs">{{ row.specs && row.specs.join('、') }}</div>
                  <div class="goods__info-flex--price">x{{ row.num }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="content__right1">
        <div class="content__right1--item">
          <span class="name">{{ orderInfo.items[0].one.name }}</span>
          <span>{{ orderInfo.items[0].one.mobile }}</span>

          <span>
            <el-tooltip
              :disabled="toolsDisabled"
              :raw-content="true"
              :content="
                orderInfo.items[0].one.userId
                  ? useSingleCalculationFormula(orderInfo.items?.[0], 'one', getBonus('one', orderInfo.items?.[0].purchase), orderInfo.items)
                  : '该层级无分销员，不计算对应佣金'
              "
              placement="bottom"
              effect="light"
            >
              <span>
                一级佣金：<span style="color: #f54319" class="cup"
                  >￥{{ orderInfo.items[0].one.userId ? getBonus('one', orderInfo.items?.[0].purchase) : 0 }}</span
                >
              </span>
            </el-tooltip>
          </span>
        </div>
        <div class="content__right1--item">
          <span class="name">{{ orderInfo.items[0].two.name }}</span>
          <span>{{ orderInfo.items[0].two.mobile }}</span>
          <span>
            <el-tooltip
              :disabled="toolsDisabled"
              :raw-content="true"
              :content="
                orderInfo.items[0].two.userId
                  ? useSingleCalculationFormula(orderInfo.items?.[0], 'two', getBonus('two', orderInfo.items?.[0].purchase), orderInfo.items)
                  : '该层级无分销员，不计算对应佣金'
              "
              placement="bottom"
              effect="light"
            >
              <span>
                二级佣金：<span style="color: #f54319">￥{{ getBonus('two', orderInfo.items?.[0].purchase) }}</span>
              </span>
            </el-tooltip>
          </span>
        </div>
        <div class="content__right1--item">
          <span class="name">{{ orderInfo.items[0].three.name }}</span>
          <span>{{ orderInfo.items[0].three.mobile }}</span>
          <span>
            <el-tooltip
              :disabled="toolsDisabled"
              :raw-content="true"
              :content="
                orderInfo.items[0].three.userId
                  ? useSingleCalculationFormula(orderInfo.items?.[0], 'three', getBonus('three', orderInfo.items?.[0].purchase), orderInfo.items)
                  : '该层级无分销员，不计算对应佣金'
              "
              placement="bottom"
              effect="light"
            >
              <span>
                三级佣金：<span style="color: #f54319">￥{{ getBonus('three', orderInfo.items?.[0].purchase) }}</span>
              </span>
            </el-tooltip>
          </span>
        </div>
      </div>
      <div class="content__right2">
        <div
          v-for="item in orderInfo.items"
          :key="item"
          class="content__right2--item"
          :style="{
            color: getOrderStatus(item.orderStatus) === '待结算' ? '#FD9224' : getOrderStatus(item.orderStatus) === '已赚' ? '#00BB2C' : '#999',
          }"
        >
          {{ getOrderStatus(item.orderStatus) }}
        </div>
      </div>
      <div class="content__right3">
        <div v-if="getcount('PAID')" class="content__right3--item">
          <el-tooltip
            v-if="orderInfo?.items?.[0]?.one?.bonus ? getcount('PAID') : 0"
            :disabled="toolsDisabled"
            :raw-content="true"
            :content="useTotalPrice(orderInfo).toSelledPrice.toString()"
            placement="bottom"
            effect="light"
            >待结算：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('PAID') : 0 }}</el-tooltip
          >
          <div v-else>待结算：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('PAID') : 0 }}</div>
        </div>
        <div v-if="getcount('COMPLETED')" class="content__right3--item">
          <el-tooltip
            v-if="orderInfo?.items?.[0]?.one?.bonus ? getcount('COMPLETED') : 0"
            :disabled="toolsDisabled"
            :raw-content="true"
            :content="useTotalPrice(orderInfo).completePrice.toString()"
            placement="bottom"
            effect="light"
            >已赚：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('COMPLETED') : 0 }}</el-tooltip
          >
          <div v-else>已赚：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('COMPLETED') : 0 }}</div>
        </div>
        <div v-if="getcount('CLOSED')" class="content__right3--item">
          <el-tooltip
            v-if="orderInfo?.items?.[0]?.one?.bonus ? getcount('CLOSED') : 0"
            :disabled="toolsDisabled"
            :raw-content="true"
            :content="useTotalPrice(orderInfo).closedPrice.toString()"
            placement="bottom"
            effect="light"
            >已失效：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('CLOSED') : 0 }}</el-tooltip
          >
          <div v-else>已失效：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount('CLOSED') : 0 }}</div>
        </div>
        <div class="content__right3--item">
          <el-tooltip
            :disabled="toolsDisabled"
            :raw-content="true"
            :content="useTotalPrice(orderInfo).totalPrice.toString()"
            placement="bottom"
            effect="light"
            >佣金合计：{{ orderInfo?.items?.[0]?.one?.bonus ? getcount() : 0 }}</el-tooltip
          >
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
@include b(head) {
  width: 1256px;
  // position: relative;
  display: flex;
  align-items: center;
  background-color: #f7f8fa;
  height: 52px;
  padding-left: 16px;
  color: #333;
  div::after {
    content: '|';
    padding: 0 8px;
    position: relative;
    top: -1px;
    color: #333 !important;
  }
  .last::after {
    content: '';
  }
}
@include b(content) {
  display: flex;
  @include e(right1) {
    border: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    justify-content: space-around;
    line-height: 26px;
    width: 470px;
    flex-shrink: 0;
    padding-left: 10px;
    @include m(item) {
      display: flex;
      flex-direction: column;
      span {
        line-height: 18px;
      }
    }
  }
  @include e(right2) {
    width: 157px;
    flex-shrink: 0;
    font-size: 13px;
    text-align: center;
    line-height: 26px;
    @include m(item) {
      height: 100px;
      line-height: 100px;
      border: 1px solid #ebeef5;
    }
  }
  @include e(right3) {
    width: 196px;
    flex-shrink: 0;
    font-size: 13px;
    border: 1px solid #ebeef5;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    flex-direction: column;
    padding-left: 5px;
    line-height: 26px;
    @include m(item) {
      padding: 0 10px;
    }
  }
}
@include b(goods) {
  width: 410px;
  height: 68px;
  display: flex;
  @include e(pic) {
    margin-right: 10px;
    width: 60px;
    height: 50px;
    position: relative;
    @include m(state) {
      background: #7f83f7;
      position: absolute;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      top: 5px;
      left: 10px;
      color: #fff;
      line-height: 40px;
      text-align: center;
      font-size: 10px;
    }
  }
  @include e(info) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin-left: 10px;
  }
  @include e(info-flex) {
    width: 310px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    @include m(name) {
      width: 220px;
      @include utils-ellipsis(2);
      margin-right: 10px;
    }
    @include m(price) {
      font-size: 12px;
    }
    @include m(specs) {
      color: #999;
      width: 220px;
      @include utils-ellipsis(1);
    }
    @include m(num) {
      position: relative;
      top: 7px;
      font-size: 12px;
    }
  }
}
:deep(.el-table thead) {
  display: none;
}
.amount {
  &::before {
    content: '￥';
    display: inline-block;
  }
}
.percentage {
  &::after {
    content: '%';
    display: inline-block;
  }
}
.ellipsis {
  max-width: 125px;
  @include utils-ellipsis();
}
.buyer {
  max-width: 100px;
  display: inline-block;
  @include utils-ellipsis();
}
.buyer-container {
  @include flex(flex-start, center);
}
.name {
  width: 109px;
  @include utils-ellipsis();
  color: #555cfd;
}
</style>
