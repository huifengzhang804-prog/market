<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { doGetInvoiceHeader, doPutsetDefault, doDeleteInvoiceHeader } from '@/apis/invoice'
import { useUserStore } from '@/store/modules/user'
import { useInvoiceStore } from '@/store/modules/invoice'
import type { InvoiceHeadeItem } from '@/apis/invoice/model'
import Auth from '@/components/auth/auth.vue'

const $userStore = useUserStore()
const InvoiceHeadelist = ref<InvoiceHeadeItem[]>([])

onShow(() => {
  initInvoiceHeadelist()
})

async function initInvoiceHeadelist() {
  const { code, data, msg } = await doGetInvoiceHeader()
  if (code !== 200) {
    uni.showToast({
      title: '获取抬头列表失败',
      icon: 'none',
      duration: 2000,
    })
    return
  }
  InvoiceHeadelist.value = data.records
}
const navToHandle = (id?: string) => {
  if (id) {
    uni.navigateTo({
      url: `/basePackage/pages/InvoiceHeaderAdd/InvoiceHeaderAdd?id=${id}`,
    })
  } else {
    uni.navigateTo({
      url: `/basePackage/pages/InvoiceHeaderAdd/InvoiceHeaderAdd`,
    })
  }
}
/**
 * 选中check回调
 * @param {string} e 抬头id
 */
const handleChangeCheck = async (e: any) => {
  const { code, success } = await doPutsetDefault({
    id: e.detail.value,
    ownerType: 'USER',
    ownerId: $userStore.userInfo.info.userId,
  })
  if (code === 200 && success) {
    uni.showToast({
      icon: 'none',
      title: '设置成功',
    })
    initInvoiceHeadelist()
  }
}
/**
 * 删除
 */
const handleDelHead = (id: string) => {
  uni.showModal({
    title: '提示',
    content: '确定要删除该抬头?',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doDeleteInvoiceHeader(id)
        if (code === 200 && success) {
          uni.showToast({
            title: '删除成功',
            icon: 'none',
            duration: 1000,
          })
          initInvoiceHeadelist()
        } else {
          uni.showToast({
            title: '删除失败',
            icon: 'none',
            duration: 1000,
          })
        }
      }
    },
  })
}
const chooseHead = async (id: string) => {
  useInvoiceStore().SET_INVOICE_HEADER_ID(id)
  uni.navigateTo({
    url: `/basePackage/pages/Invoicing/Invoicing`,
  })
}
</script>

<template>
  <radio-group style="width: 100%" @change="handleChangeCheck">
    <view class="choose">
      <view v-for="item in InvoiceHeadelist" :key="item.id" class="choose__item" @click="chooseHead(item.id)">
        <view class="choose__item-head">
          <view class="choose__item-head--name">{{ item.header }} </view>
          <view class="choose__item-head--edit" @click.stop="navToHandle(item.id)">
            <q-icon size="35rpx" style="margin-right: 8rpx" name="icon-wenbenshuru" />
          </view>
        </view>
        <view class="choose__item--number">{{ item.taxIdentNo }}</view>
        <view class="choose__item-bottom">
          <radio class="choose__item-bottom--radio" :value="item.id" :checked="item.isDefault" color="#F54319">
            <text style="color: rgba(0, 0, 0, 0.5)">设置为默认</text>
          </radio>
          <view class="choose__item-bottom--opration" @click.stop="handleDelHead(item.id)">
            <text>删除</text>
          </view>
        </view>
      </view>
    </view>
    <view style="width: 1rpx; height: 1rpx"></view>
  </radio-group>
  <view class="fix" @click="navToHandle()"> 添加抬头 </view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(choose) {
  margin-bottom: 150rpx;
  @include e(item) {
    width: 710rpx;
    height: 235rpx;
    background: #fff;
    margin: 20rpx auto 0;
    border-radius: 20rpx;
    padding: 20rpx;
    font-size: 28rpx;
    @include m(number) {
      color: rgb(102, 102, 102);
      margin-bottom: 20rpx;
      height: 38rpx;
    }
  }
  @include e(item-head) {
    @include flex(space-between);
    margin-bottom: 20rpx;
    @include m(name) {
      font-size: 34rpx;
      color: rgb(34, 34, 34);
    }
    @include m(edit) {
    }
  }
  @include e(item-bottom) {
    @include flex(space-between);
    border-top: 1px solid rgba(0, 0, 0, 0.1);
    padding-top: 20rpx;
    @include m(radio) {
    }
    @include m(opration) {
      color: rgb(229, 12, 0);
    }
  }
}
@include b(fix) {
  width: 100%;
  height: 98rpx;
  position: fixed;
  bottom: 0;
  left: 0;
  background: #f54319;
  font-size: 32rpx;
  line-height: 98rpx;
  text-align: center;
  color: #fff;
}
</style>
