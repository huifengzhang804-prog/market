<script lang="ts" setup>
import { computed, ref } from 'vue'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import { onLoad } from '@dcloudio/uni-app'
import { getheader } from '@/libs/request/returnHeader'
import VideoImageUpload from '@/basePackage/pages/releaseAssess/components/video-image-upload.vue'
import { doPostbuyersEvaluation } from '@/apis/assess'
import type { ParamsAssess } from '@/basePackage/pages/releaseAssess/types'
import Auth from '@/components/auth/auth.vue'
import storage from '@/utils/storage'

const starsTextArr = [{ label: '非常差' }, { label: '差' }, { label: '一般' }, { label: '好' }, { label: '非常好' }]
// @ts-ignore
const uploadUrl = import.meta.env.VITE_UPLOAD_URI
const orderItems = ref([
  {
    orderNo: '',
    afsStatus: 'NONE',
    dealPrice: '',
    freightPrice: '0',
    freightTemplateId: '0',
    id: '',
    image: '',
    num: 1,
    packageId: '',
    packageStatus: 'BUYER_WAITING_FOR_COMMENT',
    productId: '',
    productName: '',
    salePrice: '',
    shopId: '',
    skuId: '',
    specs: [''],
    rate: 5, //星级
    status: 'OK',
    weight: 1,
    comment: '', //说明
    medias: [], //图片列表
  },
])
const isComment = computed(() => !!orderItems.value[0].comment.length)
const starsText = ref('非常好')

// @ts-ignore
onLoad(({ storageKey }) => {
  if (storageKey) {
    initAssessOrder(storageKey)
  }
})

async function initAssessOrder(storageKey: string) {
  const info = storage.get(storageKey) || {}
  orderItems.value = [info].map((order) => Object.assign(order, { rate: 5, comment: '', medias: [] }))
  console.log('orderItems.value', orderItems.value)
}

/**
 * 星星评价
 * @param {*} value
 */
const handleStarsChange = (value: number) => {
  starsText.value = starsTextArr[value - 1].label
}
/**
 * 表单提交
 */
const handleSubmit = async () => {
  if (!isComment.value) return
  const { shopId, orderNo } = orderItems.value[0]
  const items = orderItems.value.map((order) => {
    const { comment, medias, rate, productId, skuId, specs } = order
    const key = { productId, skuId }
    return { key, comment, medias, rate, specs }
  })
  if (validator()) {
    const params: ParamsAssess = { shopId, orderNo, items }
    const { code, msg } = await doPostbuyersEvaluation(params)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '提交失败'}`, icon: 'none' })
    uni.showToast({ title: `评价成功`, icon: 'none' })
    // 使用 naviback 加 uni.$emit修改参数
    // uni.$emit('pathIndex', {
    //     msg: 4,
    // })
    uni.redirectTo({ url: '/pluginPackage/order/orderList/orderList?id=4' })
  }
}

function validator() {
  if (isComment.value) return true
  uni.showToast({ title: `请输入商品的评价内容`, icon: 'none' })
  return false
}

// 上传失败
const handleUploadFail = (e: string) => {
  uni.showToast({ title: `${e || `文件上传失败`}`, icon: 'none' })
}
</script>

<template>
  <view class="form">
    <view class="form__msg f14">客观真实的评价能帮助用户做决策</view>
    <view v-for="order in orderItems" :key="order.id" class="form__container">
      <view class="form__content">
        <view>
          <u-image :border-radius="15" :height="100" :src="order.image" :width="100" />
        </view>
        <view class="form__content-right">
          <view class="u-line-1">{{ order.productName }}</view>
          <view class="form__content-right--bottom">
            <text style="margin-right: 20rpx">请评分</text>
            <u-rate v-model="order.rate" :min-count="1" gutter="32" size="40" @change="handleStarsChange" />
          </view>
        </view>
      </view>
      <view class="form__input">
        <u-input
          v-model="order.comment"
          :border="true"
          :height="170"
          maxlength="30"
          placeholder="亲，您对这个商品满意吗？您的评价会帮助我们选择更好的商品~"
          trim
          type="textarea"
        />
        <view class="form__input--msg">{{ `${order.comment.length}/30` }}</view>
      </view>
      <view class="form__upload-title">
        <view> 商品图片（最多6张）</view>
      </view>
      <view style="height: 450rpx">
        <video-image-upload
          v-model="order.medias"
          :action="uploadUrl"
          :del-right="20"
          :headers="getheader()"
          :max="6"
          :upload-max-size="5"
          @upload-fail="handleUploadFail"
        >
          <view class="form__upload">
            <u-icon color="#c0c4cc" name="camera" size="74" />
          </view>
        </video-image-upload>
      </view>
    </view>
  </view>
  <view style="height: 84rpx"></view>
  <q-footer-default :bg-color="`${isComment ? '#fa3534' : '#999'}`" text="提交评价" @click="handleSubmit"></q-footer-default>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(form) {
  @include e(container) {
    background: #fff;
    margin-top: 20rpx;
    border-radius: 20rpx;
    padding: 10rpx;
  }
  @include e(upload) {
    display: flex;
    width: 214rpx;
    height: 214rpx;
    justify-content: center;
    align-items: center;
    background: #f3f4f6;
    border-radius: 4rpx;
    margin-top: 10rpx;
  }
  @include e(upload-title) {
    margin: 20rpx 0;
    color: #333;
    font-size: 26rpx;
    font-weight: 400;
  }
  @include e(input) {
    position: relative;
    margin-top: 20rpx;
    @include m(msg) {
      position: absolute;
      right: 10rpx;
      bottom: 10rpx;
      color: #c0c4cc;
    }
  }
  @include e(msg) {
    display: flex;
    padding: 26rpx;
    justify-content: flex-start;
    align-items: center;
    background: #ffebeb;
  }
  @include e(content) {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    @include e(content-right) {
      display: flex;
      flex-direction: column;
      height: 100rpx;
      justify-content: space-between;
      padding: 5rpx;
      margin-left: 20rpx;
      flex: 1;
      overflow: hidden;
      @include m(bottom) {
        display: flex;
        justify-content: flex-start;
      }
    }
  }
}

@include b(level) {
  position: relative;
  margin: 96rpx 18rpx 0 18rpx;
  height: 208rpx;
  background: #fff;
  border-radius: 20rpx;
  padding-top: 60rpx;
  text-align: center;
  font-size: 18rpx;
  color: #9a9a9a;
  @include e(image) {
    position: absolute;
    left: 50%;
    top: -40rpx;
    margin-left: -50rpx;
  }
  @include e(sku) {
  }
  @include e(assess) {
    margin-top: 30rpx;
  }
}

@include b(main) {
  position: relative;
  background: #fff;
  border-radius: 20rpx;
  padding: 52rpx 0 15rpx 24rpx;
  margin: 0 18rpx;
  border-top: 1px dashed #ccc;
  @include e(text) {
    position: absolute;
    top: -20rpx;
    left: 50%;
    transform: translateX(-50%) scale(0.8);
    font-size: 16rpx;
    color: #9a9a9a;
    text-align: center;
    width: 400rpx;
    background: #fff;
  }
  @include e(title) {
    font-size: 18rpx;
    color: #9a9a9a;
  }
  @include e(upload) {
    margin: 16rpx 0;
    @include m(slot) {
      padding: 54rpx 0;
      width: 664rpx;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      background: rgb(244, 245, 246);
      border-radius: 10rpx;
    }
    @include m(thereAre) {
      padding: 54rpx 0;
      width: 214rpx;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      background: rgb(244, 245, 246);
      border-radius: 20rpx;
    }
    @include m(hover) {
      background-color: rgb(235, 236, 238);
    }
    @include m(text) {
      font-size: 18rpx;
      color: #9a9a9a;
      text-align: center;
    }
  }
}
</style>
