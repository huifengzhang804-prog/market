<script lang="ts" setup>
import 'uno.css'
import { ref, reactive, computed, watch, withDefaults } from 'vue'
import { doGetCouponListPc, doPostConsumerCollectCouponPc } from '../apis'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const $route = useRoute()

const props = withDefaults(defineProps<{ properties?: any }>(), { properties: { couponList: [], cartCoupon: true } })

// 优惠券 促销
const pageConfig = reactive({
  size: 100,
  current: 1,
})
const doGetCouponListInt = async () => {
  const shopId = $route.query.shopId
  const res = await doGetCouponListPc({
    isPlatform: false,
    size: pageConfig.size,
    current: pageConfig.current,
    shopId: shopId,
    status: 'UNUSED',
  })
  return res
}
// 优惠券
const couponIndexType = [
  { type: 'PRICE_DISCOUNT', value: '无门槛折扣券' },
  { type: 'PRICE_REDUCE', value: '无门槛现金券' },
  { type: 'REQUIRED_PRICE_DISCOUNT', value: '满折券' },
  { type: 'REQUIRED_PRICE_REDUCE', value: '满减券' },
]
const discountList = ref([{ value: '', discount: '' }])
const doGetGoodsDetailsCouponPageInt = async () => {
  if (!props.properties) return
  const records = props.properties.couponList
  for (let i = 0; i < records.length; i++) {
    const item = records[i]
    for (let j = 0; j < couponIndexType.length; j++) {
      const ite = couponIndexType[j]
      if (item.type === ite.type) {
        if (ite.type === 'PRICE_REDUCE') {
          const obj = { value: '', discount: '' }
          obj.discount = ite.value + ' ' + item.amount / 10000 + '元'
          discountList.value.push(obj)
        } else if (ite.type === 'PRICE_DISCOUNT') {
          const obj = { value: '', discount: '' }
          obj.discount = ite.value + ' ' + item.discount + '折'
          discountList.value.push(obj)
        } else if (ite.type === 'REQUIRED_PRICE_DISCOUNT') {
          const obj = { value: '', discount: '' }
          obj.discount = '满' + Number(item.requiredAmount) / 10000 + '元打' + item.discount + '折'
          discountList.value.push(obj)
        } else if (ite.type === 'REQUIRED_PRICE_REDUCE') {
          const obj = { value: '', discount: '' }
          obj.discount = '满' + Number(item.requiredAmount) / 10000 + '减' + Number(item.amount || item.discountAmount) / 10000 + '元'
          discountList.value.push(obj)
        }
      }
    }
  }
}
/**
 * 优惠券有效期格式化
 * item: 优惠券对象
 */
function formattingTime(item: any) {
  if (item.effectType === 'IMMEDIATELY' && item.days) {
    return item.days <= 1 ? '领券当天内可用' : `领券当日起${item.days}天内可用`
  }
  return `${formatTime(item.startDate)}-${formatTime(item.endDate)}`
}
function formatTime(time: string) {
  return time ? time.replace(/[-]/g, '.') : ''
}

// 获取组装优惠券数据
watch(() => props.properties?.couponList, doGetGoodsDetailsCouponPageInt, { deep: true, immediate: true })

const discountHide = computed(() => discountList.value.every((item) => !item.discount))

// 领取完毕
const doReceiveCoupon = async (item: any) => {
  const { code, msg } = await doPostConsumerCollectCouponPc(item.shopId, item.id)
  if (code !== 200) {
    item.receiveFinish = true
    ElMessage(msg ? msg : '领取失败')
  } else {
    ElMessage({
      message: '领取成功',
      type: 'success',
    })
  }
}

// 添加展开收起控制变量
const isExpanded = ref(false)

// 计算展示的优惠券列表
const displayCouponList = computed(() => {
  if (!props.properties?.couponList) return []
  // item.couponUserId存在排序在前面
  const couponList = [...(props.properties.couponList || [])]
  const sortedCouponList = couponList.sort((a: any, b: any) => {
    if (a.couponUserId && !b.couponUserId) return 1
    if (!a.couponUserId && b.couponUserId) return -1
    return 0
  })
  return isExpanded.value ? sortedCouponList : sortedCouponList.slice(0, 3)
})

// 是否显示展开按钮
const showExpandButton = computed(() => {
  return props.properties?.couponList?.length > 3
})
</script>

<template>
  <div v-show="!discountHide" class="yhq">
    <div v-if="props.properties!.cartCoupon" style="margin-right: 23px">优 惠 券</div>
    <div class="yhq-list">
      <div class="yhq-box">
        <div v-for="(item, index) in props.properties!.cartCoupon ? displayCouponList : props.properties!.couponList" :key="item" class="yhq-item">
          <div class="tag">
            {{ item.type === 'PRICE_REDUCE' || item.type === 'REQUIRED_PRICE_REDUCE' ? '现金券' : '折扣券' }}
          </div>
          <div class="amount">{{ discountList[index + 1].discount }}</div>
          <div class="validity">{{ formattingTime(item) }}</div>
          <el-button
            v-if="(!!item.couponUserId || item.cheeck) && props.properties!.cartCoupon"
            link
            disabled
            class="receive"
            style="color: #666; cursor: not-allowed"
            size="small"
            >已领取</el-button
          >
          <el-button v-else-if="item.receiveFinish" link disabled class="receive" style="color: #666; cursor: not-allowed" size="small"
            >已领取</el-button
          >
          <div
            v-else
            class="receive"
            style="border-bottom: 1px solid #e31436; line-height: 18px"
            @click="
              () => {
                doReceiveCoupon(item)
                item.cheeck = true
              }
            "
          >
            领取
          </div>
        </div>
      </div>
      <!-- 添加展开/收起按钮 -->
      <div v-if="showExpandButton&&props.properties!.cartCoupon" class="expand-button" @click="isExpanded = !isExpanded">
        {{ isExpanded ? '收起' : '更多优惠' }}
        <el-button type="text" :icon="isExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.yhq {
  width: 100%;
  display: flex;
  margin-top: 15px;
  font-size: 12px;

  .yhq-list {
    width: 550px;
    display: flex;
    color: #333;
    align-items: center;
    justify-content: space-between;
    position: relative;
    .yhq-box {
      max-height: 270px;
      overflow-y: auto;
      width: 450px;
      .yhq-item {
        margin-bottom: 12px;
        line-height: 25px;
        font-style: normal;
        margin-right: 8px;
        display: flex;
        align-items: center;
        position: relative;

        .tag {
          background-color: #e93323;
          color: #fff;
          padding: 0 10px;
          position: relative;
          line-height: 27px;

          &::before {
            content: '';
            position: absolute;
            left: -6.5px;
            top: 50%;
            transform: translateY(-50%);
            width: 10px;
            height: 10px;
            background: #f2f2f2;
            border-radius: 50%;
          }
        }

        .amount {
          text-align: center;
          max-width: 230px;
          border: 1px solid #e31436;
          position: relative;
          box-sizing: border-box;
          padding: 0 20px;
          background-color: #fff;

          &::after {
            content: '';
            position: absolute;
            right: -6.5px;
            top: 50%;
            transform: translateY(-50%);
            width: 10px;
            height: 10px;
            background: #f2f2f2;
            border-radius: 50%;
            border-left: 1px solid #e93323;
            border-right: none;
          }
        }

        .validity {
          color: #666;
          margin-left: 15px;
        }

        .receive {
          color: #e93323;
          cursor: pointer;
          position: absolute;
          right: 0px;
        }
      }
    }

    .expand-button {
      color: #666;
      cursor: pointer;
      position: absolute;
      top: 38px;
      right: 0;
      &:hover {
        color: #e31436;
      }
      i {
        margin-left: 5px;
      }
    }
  }
}
</style>
