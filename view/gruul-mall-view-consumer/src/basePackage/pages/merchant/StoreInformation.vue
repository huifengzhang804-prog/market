<script setup lang="ts">
import { doGetShopInfo } from '@/apis/good'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import { onLoad } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'

onLoad((res) => {
  if (res?.shopId) {
    getShopInfo(res?.shopId)
  }
})
const shopInfo = ref<GetShopInfoRes>()
const getShopInfo = async (shopId: string) => {
  const { code, data } = await doGetShopInfo({
    shopId,
    type: 'SHOP_HOME',
  })
  if (code !== 200 || !data) {
    return uni.showToast({
      icon: 'none',
      title: '获取商家信息失败',
    })
  }
  shopInfo.value = { id: shopId, ...data }
}
const newShopInfo = computed(() => {
  if (shopInfo.value?.no) {
    return {
      ...shopInfo.value,
      time: shopInfo.value.openTime.start && shopInfo.value.openTime.end ? `${shopInfo.value.openTime.start} - ${shopInfo.value.openTime.end}` : '-',
    }
  }
  return {}
})
const formLabels = [
  {
    key: 'no',
    label: '店铺ID：',
  },
  {
    key: 'companyName',
    label: '商户名称：',
  },
  {
    key: 'time',
    label: '营业时间：',
  },
  {
    key: 'contractNumber',
    label: '联系方式：',
  },
  {
    key: 'address',
    label: '定位地址：',
  },
  {
    key: 'briefing',
    label: '店铺介绍：',
  },
  {
    key: 'newTips',
    label: '上新公告：',
  },
]

const goBack = () => {
  uni.navigateBack()
}
</script>

<template>
  <view style="padding: 24rpx">
    <view class="content">
      <view class="content__head">
        <image :src="shopInfo?.logo" class="img" mode="scaleToFill" />
        <view class="content__head--name fontBold">{{ shopInfo?.name }}</view>
      </view>
      <view v-for="(item, index) in formLabels" :key="index" class="content__item">
        <view class="content__item--label">{{ item.label }}</view>
        <view :class="{ 'content__item--newTips': item.key === 'newTips' }" class="content__item--info">
          {{ newShopInfo[item.key as keyof typeof newShopInfo] ? newShopInfo[item.key as keyof typeof newShopInfo] : '-' }}
        </view>
      </view>
      <!-- 底部按钮占位 -->
      <view style="height: 10px"></view>
    </view>
    <view class="content__btn" @click="goBack"> 去看看全部商品<QIcon name="icon-youjiantou_huaban1" /></view>
  </view>
</template>

<style lang="scss" scoped>
@include b(content) {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 26rpx;
  .img {
    width: 108rpx;
    height: 108rpx;
    border-radius: 10rpx;
    margin-right: 10rpx;
  }
  @include e(head) {
    padding: 20rpx 0;
    @include flex(flex-start);
    @include m(name) {
      margin-left: 20rpx;
      font-size: 30rpx;
    }
  }
  @include e(item) {
    margin-top: 45rpx;
    @include flex(flex-start);
    align-items: flex-start;
    @include m(label) {
      width: 150rpx;
      flex-shrink: 0;
    }
    @include m(info) {
      color: #666;
      @include utils-ellipsis(4);
    }
    @include m(newTips) {
      color: #f29100;
    }
  }
}
.content__btn {
  text-align: center;
  margin-top: 25rpx;
  line-height: 90rpx;
  background-color: #fff;
  border-radius: 10rpx;
  color: #f54319;
}
</style>
