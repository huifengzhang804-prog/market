<script setup lang="ts">
import 'uno.css'
import { ref, onMounted, reactive } from 'vue'
import { doGetCouponListPc, doPostConsumerCollectCouponPc } from '../apis'
import { ApiCouponVO, ProductType, CartApiCouponVO, formattingCouponRules } from '../index'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'

const { divTenThousand } = useConvert()

const couponList = ref<CartApiCouponVO[]>([])
// 获取优惠券数据
const getCouponList = async () => {
  couponList.value = await getCouponListInt()
}

function formattingName(item: ApiCouponVO) {
  if (item.productType === ProductType.ALL) {
    return '全场商品通用'
  }
  return '限' + item.shopName + '可用'
}
// 获取优惠券接口
const total = ref('')
const param = reactive({
  isPlatform: false,
  shopId: '',
  status: 'UNCLAIMED',
  lastCreateTime: '',
  current: 1,
  size: 12,
})
async function getCouponListInt() {
  const { code, data } = await doGetCouponListPc(param)
  if (code === 200) {
    // return data.records
    total.value = data.total
    // return data.records.filter((item: CartApiCouponVO) => item.stock !== '0')
    return data.records
  } else {
    return []
  }
}
// 点击领取
const collectFn = async (id: string, shopId: string) => {
  const { code, msg } = await doPostConsumerCollectCouponPc(shopId, id)
  if (code !== 200) {
    ElMessage(msg ? msg : '领取失败')
  } else {
    couponList.value = couponList.value?.filter((item) => item.id !== id)
    total.value = couponList.value?.length.toString()
    ElMessage({
      message: '领取成功',
      type: 'success',
    })
  }
}
// 分页切换
const handleCurrentChange = (val: number) => {
  param.current = val
  getCouponList()
}
onMounted(() => {
  getCouponList()
})

const props = defineProps({
  properties: { type: Object, default: () => ({}) },
})
const couponImg = ref('')
setTimeout(() => (couponImg.value = props?.properties?.otherData?.couponImg), 400)
</script>

<template>
  <div class="coupon"><img class="coupon-img" :src="couponImg" style="width: 100%; height: 100%" /></div>
  <div class="discountCoupon">
    <div v-for="(item, index) in couponList.filter((v) => v.shopName)" :key="index" class="discount">
      <div class="discount__left">
        <p v-if="item?.amount" class="discount__left--zhe">￥{{ divTenThousand(item?.amount) }}</p>
        <p v-else class="discount__left--zhe">{{ item?.discount }}折</p>
        <span class="discount__left--text">{{ item?.type === 'PRICE_REDUCE' || item?.type === 'REQUIRED_PRICE_REDUCE' ? '现金券' : '折扣券' }}</span>
      </div>
      <div class="discount__center">
        <p class="discount__center--title">{{ formattingName(item) }}</p>
        <p class="discount__center--til">{{ formattingCouponRules(item, false) }}</p>
        <!-- <p class="discount__center--t">平台专属优惠券</p> -->
      </div>
      <div class="discount__right">
        <div class="discount__right--getClick" @click="collectFn(item?.id, item?.shopId)">
          点击
          <p>领取</p>
        </div>
        <p class="discount__right--useTime">使用时间</p>
        <div v-if="item?.startDate && item?.endDate" class="discount__right--time">
          <p>{{ item?.startDate }}</p>
          <p c-h-6 c-lh-6>-</p>
          <p>{{ item?.endDate }}</p>
        </div>
        <div v-else-if="item?.effectType === 'IMMEDIATELY' && item?.days" class="discount__right--time2">
          <p class="discount__right--timeP">{{ item?.days <= 1 ? '领券当天内 可用' : `领券当日起 ${item.days} 天内可用` }}</p>
        </div>
      </div>
    </div>
    <ToTopGoCar />
  </div>
  <div class="page">
    <el-pagination
      :background="true"
      layout="prev, pager, next, total"
      :total="Number(total)"
      :current-page="param.current"
      :page-size="param.size"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<style scoped lang="scss">
@include b(coupon) {
  margin: auto;
  width: 1920px;
  height: 500px;
  background-color: #fff;
  @include e(img) {
    width: 100%;
  }
}
@include b(discountCoupon) {
  width: 1190px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
}
@include b(discount) {
  display: flex;
  width: 384px;
  height: 130px;
  background: url(./discountCoupon.png) center center;
  background-size: cover;
  margin-top: 10px;
  margin-right: 18px;
  &:nth-of-type(3n) {
    margin-right: 0px;
  }
  @include e(left) {
    width: 130px;
    height: 130px;
    @include m(zhe) {
      font-weight: bold;
      font-size: 34px;
      color: #fff;
      padding: 25px 0 35px;
    }
    @include m(text) {
      color: #fff;
      border: 0.84px solid #fff;
      border-radius: 66.51px;
      font-size: 14px;
      padding: 1px 10px 1px 10px;
    }
  }
  @include e(center) {
    position: relative;
    width: 146px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    color: #838383;
    text-align: left;
    padding-left: 6px;
    @include m(title) {
      width: 141px;
      font-size: 22px;
      padding-bottom: 12px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    @include m(til) {
      padding-bottom: 15px;
      font-size: 12px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    @include m(t) {
      position: absolute;
      bottom: 14px;
      left: 90px;
      width: 100px;
      font-size: 12px;
      zoom: 0.8;
    }
  }
  @include e(right) {
    width: 110px;
    @include m(getClick) {
      width: 56px;
      height: 56px;
      font-size: 16px;
      padding: 8px;
      margin: 15px 0 3px 30px;
      color: #fff;
      cursor: pointer;
    }
    @include m(useTime) {
      font-size: 12px;
      margin-bottom: 5px;
    }
    @include m(time) {
      zoom: 0.8;
      font-size: 12px;
      color: #999999;
    }
    @include m(time2) {
      padding: 0 auto;
      zoom: 0.9;
      font-size: 12px;
      color: #999999;
    }
    @include m(timeP) {
      margin: 0 auto;
      width: 75px;
    }
  }
}
@include b(display) {
  display: none;
}
@include b(page) {
  display: flex;
  justify-content: center;
  margin: 35px 0;
}
</style>
