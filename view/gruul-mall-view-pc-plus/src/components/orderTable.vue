<script setup lang="ts">
import { ref, PropType, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Decimal from 'decimal.js'
import { Clock } from '@element-plus/icons-vue'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { getAfsStatusCn } from '@/utils/useAfssStatus'
import { usePaymentCn } from '@/utils/usePaymentCn'
import Countdown from './countdown.vue'
import storage from '@/libs/storage'
import { ApiOrder, ShopOrder, ShopOrderItem } from '@/views/personalcenter/order/myorder/types'
import { orderStatusHandle, packageStatusHandle, orderStatusPlus } from '@/utils/OrderStatusHelper'
import { doPutCloseOrderByOrderNo } from '@/apis/order'
import { doconfirmGoods } from '@/apis/afs'
import PcGroupOrder from '@/q-plugin/team/PcGroupOrder.vue'
import { storeToRefs } from 'pinia'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import { useUserStore } from '@/store/modules/user'
import { useRouterNewWindow } from '@/utils/useRouter'
import { queryConfigByModule } from '@/apis/platform'

const $props = defineProps({
  orderList: {
    type: Array<ApiOrder>,
    default() {
      return []
    },
  },
})
const $router = useRouter()
const { openNewWindow } = useRouterNewWindow()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const info: any = computed(() => (order: any) => {
  return order.shopOrders.map((item: any) => item.shopOrderItems).flat(1)
})
const platformName = ref('启山智软')
const getConfigByModule = async () => {
  const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING')
  if (code === 200 && data) {
    platformName.value = data.PLATFORM_NAME
  }
}
getConfigByModule()
/**
 * @description: 商品价格计算
 * @param {*} data
 * @returns {*}
 */
function computeOrderPrice(data: ShopOrder) {
  return data.shopOrderItems.reduce((pre, cur) => {
    return pre + cur.num * Number(useConversionPrice(cur.dealPrice))
  }, 0)
}
/**
 * @description: 运费价格计算
 * @param {*} data
 * @returns {*}
 */
function computefreightPrice(data: ShopOrder) {
  return data.shopOrderItems.reduce((pre, cur) => {
    return pre + Number(useConversionPrice(cur.freightPrice))
  }, 0)
}
/**
 * @description: 确认收货
 * @returns {*}
 */
const handleConfirmGoods = (order: ApiOrder, shop: ShopOrder) => {
  const showModalProps = {
    content: '是否确认收货',
    showClose: true,
    isSubmit: true,
  }
  // 通过每一个商品的售后得到 可以改变包状态的数组
  if (!shop.shopOrderItems.every((order) => getAfsStatusCn(order.afsStatus).canChangePackageStatus)) {
    showModalProps.content = '该订单中存在退款宝贝，等待商家确认收货'
    showModalProps.isSubmit = false
    showModalProps.showClose = false
  } else if (!shop.shopOrderItems.every((order) => !getAfsStatusCn(order.afsStatus).canChangePackageStatusText)) {
    showModalProps.content = '该订单中存在退款宝贝，确认收货将关闭退款'
  }
  // 该订单中存在退款宝贝，等待商家确认收货  该订单中存在退款宝贝，确认收货将关闭退款

  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!showModalProps.isSubmit) return
    const { packageId } = shop.shopOrderItems[0]
    const { code, msg } = await doconfirmGoods(order.no, shop.no, shop.shopId as string)

    if (code !== 200) return ElMessage.error(`${msg ? msg : '确认收货失败'}`)
    $router.push({
      path: '/personalcenter/order/writecomments',
      query: { orderNo: order.no, shopId: shop.shopId, packageId },
    })
  })
}
/**
 * @description: 取消订单
 * @returns {*}
 */
const handleCancelOrder = async (orderNo: string) => {
  ElMessageBox.confirm(`是否需要取消？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, data, msg } = await doPutCloseOrderByOrderNo(orderNo)

    if (code !== 200) return ElMessage.error(`${msg ? msg : '取消订单失败'}`)
    ElMessage.success(`订单已取消`)
    const cancelOrder = $props.orderList.find((order) => order.no === orderNo)
    if (cancelOrder) {
      cancelOrder.status = 'BUYER_CLOSED'
    }
  })
}
/**
 * @description: 去支付
 * @returns {*}
 */
const handlePay = (orderNo: string) => {
  $router.push({
    path: '/pay',
    query: { no: orderNo },
  })
}
/**
 * @description: 导航去订单详情(未支付的订单没有拆分店铺传orderNo即可)
 * @param {*} orderNo
 * @param {*} shopNo
 * @returns {*}
 */
const handleNavToDetail = (orderNo: string, shopId?: string, packageId?: string) => {
  if (!shopId) {
    return $router.push({
      path: '/personalcenter/order/orderdetail',
      query: { orderNo, packageId: packageId ? packageId : '' },
    })
  }
  $router.push({
    path: '/personalcenter/order/orderdetail',
    query: { orderNo, packageId: packageId ? packageId : '', shopId },
  })
}
/**
 * @description: 导航去评价
 * @returns {*}
 */
const navGoAssess = (orderItem: ShopOrderItem[], order: ApiOrder) => {
  const orderNo = order.no
  const shopId = orderItem[0].shopId
  const packageId = orderItem[0].packageId

  let idList: any = { ids: [] }
  for (let i = 0; i < orderItem.length; i++) {
    idList.ids.push(orderItem[i].id)
  }

  $router.push({
    path: '/personalcenter/order/writecomments',
    query: { orderNo, shopId, packageId, idList },
  })
}
/**
 * @description: 导航商品详情
 * @returns {*}
 */
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}

/**
 * @description: 客服
 */
const gotoCustomerService = async (shopId: string, orderNo: string) => {
  await doGetMessagesChatRoom(shopId, useUserStore().getterUserInfo.userId)
  openNewWindow('/personalcenter/set/customerservice', { shopId, orderNo })
}

/**
 * @description: 导航去申请售后
 * @returns {*}
 */
const navapplyaftersales = (afsOrderInfo: any, orderNo: string) => {
  const refundAmount = new Decimal(afsOrderInfo.dealPrice).mul(afsOrderInfo.num).sub(afsOrderInfo.fixPrice)
  const applyAfterSalesOrder = {
    ...afsOrderInfo,
    itemId: afsOrderInfo.id,
    orderNo,
    refundAmount,
  }
  new storage().setItem('CLIENT-applyAfterSalesOrder', applyAfterSalesOrder, 60 * 60 * 2)
  $router.push({
    path: '/personalcenter/order/applyaftersales',
    query: {},
  })
}

/**
 * @description: 没发货加上运费 发货后不计运费  dealPrice*num - fixPrice <= 0  （freightPrice）
 * @param {*} info 0 元 购买 不展示售后按钮
 * @returns {*}
 */
function isAfsPriceComputed(info: ShopOrderItem) {
  const { dealPrice, num, fixPrice, packageId, freightPrice } = info
  const price = new Decimal(dealPrice).div(num).sub(fixPrice)
  if (packageId) {
    return useConversionPrice(price).toNumber() <= 0
  } else {
    return useConversionPrice(price.add(freightPrice)).toNumber() <= 0
  }
}

const gotoShop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId },
  })
}
</script>

<template>
  <div flex e-c-3 c-fs-14 c-h-39 c-lh-39>
    <div c-ml-45>商品</div>
    <div c-ml-295>售价(元)</div>
    <div c-ml-100>数量</div>
    <!-- <div c-ml-67>商品操作</div> -->
    <div c-ml-20 />
    <div c-ml-89>总计(元)</div>
    <div c-ml-78>订单状态</div>
    <div c-ml-98>操作</div>
  </div>
  <template v-for="order in $props.orderList" :key="order.no">
    <template v-if="orderStatusHandle[order.status]?.next">
      <template v-for="(shop, index) in order.shopOrders" :key="index">
        <div b-1 c-bc-ebeae6 c-bg-f7f7f7 c-h-40 c-lh-40 e-c-9 c-fs-12 text-left c-w-1022 style="display: flex; justify-content: space-between">
          <div flex items-center>
            <QIcon name="icon-dianpu5" size="20px" c-ml-10 />
            <span v-if="shop?.shopType === 'SELF_OWNED'" c-ml-5 c-c-f54319 c-fs-12 c-lh-16 c-bg-fb232f class="shop_tag">自营</span>
            <span v-if="shop?.shopType === 'PREFERRED'" c-ml-5 c-c-00bb2c c-fs-12 c-lh-16 c-bg-8239f6 class="shop_tag">优选</span>
            <span c-ml-5 e-c-3 fw-700 cursor-pointer @click="gotoShop(shop.shopId)">{{ shop.shopName }}</span>
            <span c-ml-50>订单号：{{ shop.orderNo }}</span>
            <span c-ml-48>下单时间：{{ shop.createTime }}</span>
            <span v-if="order.orderPayment.type" c-ml-28>支付方式：{{ usePaymentCn(order.orderPayment.type) }}支付</span>
          </div>
          <div
            v-if="getterPropertiesList?.otherData?.service"
            style="display: flex; align-items: center; margin-right: 20px; cursor: pointer; color: #f54319"
            @click.stop="gotoCustomerService(shop.shopOrderItems[0].shopId, shop.orderNo)"
          >
            <QIcon cursor-pointer name="icon-lianxikefu" style="margin-left: 8px" />
            &ensp;联系客服
          </div>
        </div>
        <div c-fs-12 e-c-3 flex items-stretch c-mb-11>
          <div c-w-609>
            <div v-for="item in shop.shopOrderItems" :key="item.id" c-h-85 flex items-center b-1 c-bc-EBEAE6>
              <img :src="item.image" c-w-60 c-h-60 c-ml-24 cursor-pointer @click="gotoDetail(item.productId, item.shopId)" />
              <div c-w-230 text-left c-ml-12>
                <div c-ellipsis-2 cursor-pointer @click="gotoDetail(item.productId, item.shopId)">
                  {{ item.productName }}
                </div>
                <div c-ellipsis-2 cursor-pointer e-c-9>
                  {{ item.specs?.join(',') }}
                </div>
              </div>
              <div c-w-80 c-ml-20>￥{{ useConversionPrice(item.salePrice) }}</div>
              <div c-w-40 c-ml-82>
                {{ item.num }}
              </div>
            </div>
          </div>
          <div b-1 c-bc-EBEAE6 c-w-142 flex flex-col justify-center>
            <div fw-700 c-fs-14>
              {{ (computeOrderPrice(shop) + computefreightPrice(shop)).toFixed(2) }}
            </div>
            <div e-c-9>（含运费:{{ computefreightPrice(shop).toFixed(2) }}）</div>
          </div>
          <div b-1 c-bc-EBEAE6 c-w-139 flex flex-col justify-center c-lh-24>
            <div :style="{ color: `${packageStatusHandle[shop.shopOrderItems[0].packageStatus].color}` }">
              {{ orderStatusPlus(shop, order).desc }}
            </div>
          </div>
          <div b-1 c-bc-EBEAE6 c-w-132 flex flex-col justify-center items-center>
            <div
              v-if="packageStatusHandle[shop.shopOrderItems[0].packageStatus].Confirmreceipt && order.distributionMode !== 'INTRA_CITY_DISTRIBUTION'"
              c-w-87
              c-h-24
              c-lh-24
              c-c-71B247
              b-1
              c-bc-71B247
              c-mt-4
              c-mb-6
              cursor-pointer
              @click="handleConfirmGoods(order, shop)"
            >
              确认收货
            </div>
            <div
              v-if="packageStatusHandle[shop.shopOrderItems[0].packageStatus].evaluate"
              cursor-pointer
              hover-c-c-e31436
              hover-underline
              @click="navGoAssess(shop.shopOrderItems, order)"
            >
              评价晒单
            </div>
            <div cursor-pointer hover-c-c-e31436 hover-underline @click="handleNavToDetail(order.no, shop.shopId, shop.shopOrderItems[0].packageId)">
              查看详情
            </div>
          </div>
        </div>
      </template>
    </template>
    <template v-else>
      <div b-1 c-bc-ebeae6 c-bg-f7f7f7 c-h-40 c-lh-40 e-c-9 c-fs-12 text-left c-w-1022 style="display: flex; justify-content: space-between">
        <div flex items-center>
          <span v-if="order?.shopOrders?.length >= 2" c-ml-24 e-c-3 fw-700>{{ platformName }}</span>
          <template v-else>
            <QIcon name="icon-dianpu5" size="20px" c-ml-10 />
            <span v-if="order?.shopOrders[0]?.shopType === 'SELF_OWNED'" c-ml-5 c-c-f54319 c-fs-12 c-lh-16 c-bg-fb232f class="shop_tag">自营</span>
            <span v-if="order?.shopOrders[0]?.shopType === 'PREFERRED'" c-ml-5 c-c-00bb2c c-fs-12 c-lh-16 c-bg-8239f6 class="shop_tag">优选</span>
            <span c-ml-5 e-c-3 fw-700 cursor-pointer @click="gotoShop(order?.shopOrders[0]?.shopId)">{{ order?.shopOrders[0]?.shopName }}</span>
          </template>
          <span c-ml-50>订单号：{{ order.no }}</span>
          <span c-ml-48>下单时间：{{ order.createTime }}</span>
        </div>
        <div
          v-if="getterPropertiesList?.otherData?.service"
          style="display: flex; align-items: center; margin-right: 20px; cursor: pointer; color: #f54319"
          @click.stop="gotoCustomerService(info(order)[0].shopId, order.no)"
        >
          <QIcon cursor-pointer name="icon-lianxikefu" color="rgb(245, 67, 25)" style="margin-left: 8px" />
          &ensp;联系客服
        </div>
      </div>
      <div c-fs-12 e-c-3 flex items-stretch c-mb-11>
        <div c-w-609>
          <div v-for="item in info(order)" :key="item.id" c-h-85 flex items-center b-1 c-bc-EBEAE6>
            <img :src="item.image" c-w-60 c-h-60 c-ml-24 cursor-pointer @click="gotoDetail(item.productId, item.shopId)" />
            <div c-w-230 text-left c-ml-12>
              <div c-ellipsis-2 cursor-pointer @click="gotoDetail(item.productId, item.shopId)">
                {{ item.productName }}
              </div>
              <div e-c-9>
                {{ item.specs?.join(',') }}
              </div>
            </div>
            <div c-w-80 c-ml-20>￥{{ useConversionPrice(item.dealPrice) }}</div>
            <div c-w-40 c-ml-53>
              {{ item.num }}
            </div>
          </div>
        </div>
        <div b-1 c-bc-EBEAE6 c-w-142 flex flex-col justify-center>
          <div fw-700 c-fs-14>
            {{ useConversionPrice(order.orderPayment.payAmount).toFixed(2) }}
          </div>
          <div e-c-9>（含运费:{{ useConversionPrice(order.orderPayment.freightAmount).toFixed(2) }}）</div>
          <!-- <div>{{ usePaymentCn(order.orderPayment.type) }}支付</div> -->
        </div>
        <div b-1 c-bc-EBEAE6 c-w-139 flex flex-col justify-center c-lh-24>
          <div :class="orderStatusHandle[order.status]?.toPaid ? 'c-c-FF6C00' : 'c-c-ccc'">
            {{ orderStatusHandle[order.status]?.desp }}
          </div>
          <div v-if="orderStatusHandle[order.status]?.toPaid" c-c-E31436 flex items-center justify-center>
            <el-icon color="#e31436">
              <Clock />
            </el-icon>
            剩余<Countdown :order="order" />
          </div>
        </div>
        <div b-1 c-bc-EBEAE6 c-w-132 flex flex-col justify-center items-center>
          <template v-if="orderStatusHandle[order.status]?.toPaid">
            <div cursor-pointer c-w-87 c-h-24 c-lh-24 c-c-e31436 b-1 c-bc-e31436 c-mt-4 c-mb-6 @click="handlePay(order.no)">付款</div>
            <!-- <div cursor-pointer hover-c-c-e31436 hover-underline @click="handleCancelOrder(order.no)">取消订单</div> -->
          </template>
          <!-- <PcGroupOrder v-else-if="order.type === 'TEAM'" :order="order" /> -->
          <div cursor-pointer hover-c-c-e31436 hover-underline @click="handleNavToDetail(order.no)">查看详情</div>
        </div>
      </div>
    </template>
  </template>
</template>

<style lang="scss" scoped>
.shop_tag {
  display: inline-block;
  font-size: 10px;
  zoom: 0.8;
  border-radius: 3px;
  overflow: hidden;
  padding: 0 4px;
  height: 17px;
  line-height: 18px;
  color: #fff;
}
</style>
