<script lang="ts" setup>
import { computed, type PropType, reactive, ref } from 'vue'
import { doGetBargainHelpPeopleList } from '@/pluginPackage/bargain/apis'
import { EMPTY_GB } from '@/constant'
import type { BargainHelpPeopleItemRes, BargainHelpPeopleListRes, BargainPagePathQuery } from '@/pluginPackage/bargain/apis/model'
import { Decimal } from 'decimal.js-light'

const $props = defineProps({
  bargainInfo: {
    type: Object as PropType<BargainHelpPeopleListRes['sponsorProductSku']>,
    default: () => {
      return {
        userId: '',
        activityId: '',
        userHeadPortrait: '',
        userNickName: '',
        endTime: '',
        shopId: '',
        productId: '',
        skuId: '',
        skuName: '',
        skuImage: '',
        skuPrice: '',
        floorPrice: '',
        amountCut: '',
        bargainSponsorSkuStatus: 'END',
      }
    },
  },
})
const $emit = defineEmits(['navToBargain'])
const { divTenThousand } = useConvert()
const pageConfig = reactive({
  size: 5,
  current: 1,
  pages: 1,
})
const helpPeopleList = ref<BargainHelpPeopleItemRes[]>([])
// （优惠金额 ÷ 原价）× 100%
const percent = computed(() => {
  if (!$props.bargainInfo?.activityId) {
    return 0
  }
  const bargain = $props.bargainInfo
  return new Decimal(bargain.amountCut).div(new Decimal(bargain.skuPrice).sub(bargain.floorPrice)).mul(100).toNumber()
})
const customStyle = {
  backgroundColor: '#DE3224', // 注意驼峰命名，并且值必须用引号包括，因为这是对象
  color: '#fff',
  height: '68rpx',
  fontSize: '28rpx',
}

initHelpPeopleList()

/**
 * 请求帮砍列表
 */
async function initHelpPeopleList(isLoad = false) {
  // 无砍价信息不去请求列表接口
  if (!$props.bargainInfo?.activityId) return
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    helpPeopleList.value = await getBargainHelpPeopleList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    helpPeopleList.value = helpPeopleList.value.concat(await getBargainHelpPeopleList())
  }
}

async function getBargainHelpPeopleList() {
  if (!$props.bargainInfo?.activityId) return []
  const { userId: sponsorId, activityId, productId, shopId } = $props.bargainInfo
  const params = {
    sponsorId,
    shopId,
    productId,
    activityId,
    bargainOrderId: $props.bargainInfo.bargainOrderId,
  }
  const { code, data, msg } = await doGetBargainHelpPeopleList({ ...params, ...pageConfig })

  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取收藏列表失败'}`, icon: 'none' })
    return []
  }
  if (!data) {
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
</script>

<template>
  <view class="my_bargain">
    <text class="fontBold f14">我的砍价</text>
    <!-- 步进条 -->
    <view class="my_bargain__progress">
      <u-line-progress
        :percent="percent"
        :show-percent="false"
        :striped-active="true"
        active-color="#FF2828"
        height="14"
        striped
        type="error"
      ></u-line-progress>
    </view>
    <!-- 步进条 -->
    <!-- 价格 -->
    <view class="my_bargain__progress_price f12">
      <text>原价￥{{ bargainInfo && divTenThousand(bargainInfo.skuPrice) }}</text>
      <text>底价￥{{ bargainInfo && divTenThousand(bargainInfo.floorPrice) }}</text>
    </view>
    <!-- 价格 -->
    <view class="my_bargain__record">
      <text class="fontBold f14">帮砍记录</text>
      <scroll-view scroll-y @scrolltolower="initHelpPeopleList(true)">
        <view class="my_bargain__record_list">
          <u-empty v-if="!helpPeopleList.length" :src="EMPTY_GB.EVALUATION_EMPTY" text="暂无帮砍记录"></u-empty>
          <template v-else>
            <view v-for="(item, index) in helpPeopleList" :key="index" class="my_bargain__record_list_item">
              <view class="my_bargain__record_list_item_left">
                <u-image :src="item.userHeadPortrait" height="80" shape="circle" width="80"></u-image>
                <text class="my_bargain__record_list_item_left--name">{{ item.userNickName }}</text>
              </view>
              <view class="my_bargain__record_list_item_right">
                <text class="my_bargain__record_list_item_right--price f12">
                  {{ item.helpCutAmount && divTenThousand(item.helpCutAmount) }}
                </text>
              </view>
            </view>
          </template>
        </view>
      </scroll-view>
    </view>
    <view class="my_bargain__btn">
      <u-button :custom-style="customStyle" type="error" @click="$emit('navToBargain')">我的砍价</u-button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(my_bargain) {
  padding: 30rpx;
  @include e(progress) {
    margin: 20rpx;
  }
  @include e(progress_price) {
    @include flex();
    justify-content: space-between;
    color: #999999;
    padding: 0 20rpx;
  }
  @include e(record) {
    margin: 60rpx 0 0 0;
  }
  @include e(record_list) {
    height: 350rpx;
    padding-top: 25rpx;
  }
  @include e(record_list_item) {
    @include flex();
    justify-content: space-between;
    margin-bottom: 28rpx;
  }
  @include e(record_list_item_left) {
    @include flex();
    @include m(name) {
      margin-left: 25rpx;
    }
  }
  @include e(record_list_item_right) {
    @include m(price) {
      color: #e31436;
      &::before {
        content: '￥';
      }
    }
  }
  @include e(btn) {
    margin: 0 15%;
  }
}
</style>
