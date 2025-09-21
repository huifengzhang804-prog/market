<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import Portrait from '@/components/portrait/portrait.vue'
import { doGetOrderEvaluateInfoList } from '@/apis/good'
import TextToggle from '@/components/text-toggle/text-toggle.vue'
import EvaluationFilterButtons from '@/components/q-btns/evaluation-filter-buttons.vue'
import PreviewImage from '@/components/preview-image/preview-image.vue'
import DefaultEmpty from '@/components/qszr-core/packages/components/q-empty/default-empty.vue'
import { EMPTY_GB } from '@/constant'
import type { EvaluationType, Evaluate } from '@/pluginPackage/goods/commodityInfo/types'
import { cropImg } from '@/utils'

const butsCountObj = ref({ totalCount: '0', contentCount: '0', mediaCount: '0' })
const evaluateInfoList = ref<Evaluate[]>([])
const page = reactive({ current: 1, size: 10 })
// 是否已经在加载中
const loadEvaluation = ref(false)
const currentShopId = ref('')
const currentProductId = ref('')
const loadStatus = ref('')

onLoad(({ shopId, productId, totalCount, contentCount, mediaCount }: any) => {
  if (shopId && productId) {
    currentShopId.value = shopId
    currentProductId.value = productId
    initEvaluateInfoList()
  }
  if (totalCount && contentCount && mediaCount) {
    butsCountObj.value = { totalCount, contentCount, mediaCount }
  }
})

/**
 * 获取评价列表
 * @param {*} shopId
 * @param {*} productId
 * @param {*} type
 */
async function initEvaluateInfoList(type?: keyof typeof EvaluationType) {
  loadEvaluation.value = true
  const { code, msg, data } = await doGetOrderEvaluateInfoList(currentShopId.value, currentProductId.value, page.current, type)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取评价概述列表失败'}`, icon: 'none' })
    loadEvaluation.value = false
    return []
  }
  evaluateInfoList.value = [...evaluateInfoList.value, ...data.records]
  loadEvaluation.value = false
}
/**
 * 处理筛选评价
 */
const handleAssessFilter = (type: { name: string; label: string | number; key: string }) => {
  evaluateInfoList.value = []
  initEvaluateInfoList(type.key as keyof typeof EvaluationType)
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(34 * 2) - uni.upx2px(58)
})
const loadMore = () => {
  if (page.current * page.size > evaluateInfoList.value.length) {
    loadStatus.value = 'nomore'
    return
  }
  loadStatus.value = 'loading'
  page.current++
  initEvaluateInfoList()
}
</script>

<template>
  <evaluation-filter-buttons
    :total-count="butsCountObj.totalCount"
    :content-count="butsCountObj.contentCount"
    :media-count="butsCountObj.mediaCount"
    @click="handleAssessFilter"
  />
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="loadMore">
    <Portrait
      v-for="(evaluateItem, idx) in evaluateInfoList"
      :key="idx"
      class="main"
      :src="evaluateItem.avatar ? evaluateItem.avatar : cropImg($getCdnUrl('/image/header.webp'), 100, 100)"
      :title="evaluateItem.nickname"
      :time="evaluateItem.createTime"
      :sku="evaluateItem.specs?.join(' ')"
      :rate="evaluateItem.rate"
    >
      <view class="main__comment">{{ evaluateItem.comment }}</view>
      <preview-image :images="evaluateItem.medias" />
      <view v-if="evaluateItem.shopReply" class="main__reply">
        <text-toggle :text="evaluateItem.shopReply">商家回复：</text-toggle>
      </view>
    </Portrait>
  </scroll-view>
  <!-- #ifndef MP-WEIXIN -->
  <DefaultEmpty v-if="!evaluateInfoList.length && !loadEvaluation" :background="EMPTY_GB.EVALUATION_EMPTY" height="500rpx" title="暂无评价内容~" />
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <u-empty
    v-if="!evaluateInfoList.length && !loadEvaluation"
    :src="EMPTY_GB.EVALUATION_EMPTY"
    text="暂无评价内容~"
    margin-top="200"
    icon-size="550"
  />
  <!-- #endif -->
  <Auth />
</template>
<style scoped lang="scss">
@include b(tags) {
  display: flex;
  padding: 34rpx 36rpx;
  background: #fff;

  @include e(tag) {
    min-width: 136rpx;
    margin-right: 15rpx;
    padding: 0 5rpx;
    height: 58rpx;
    background: #ff2e27;
    border-radius: 30rpx;
    font-size: 26rpx;
    font-weight: normal;
    text-align: CENTER;
    color: #ffffff;
    line-height: 58rpx;
  }
}
@include b(main) {
  margin-top: 10rpx;
  @include e(comment) {
    margin: 30rpx 0 14rpx 0;
  }
  @include e(reply) {
    margin-top: 26rpx;
    background: #f2f2f2;
    padding: 20rpx 0 20rpx 20rpx;
    color: #838383;
    font-size: 26rpx;
    @include m(text) {
      @include utils-ellipsis(2);
      &::before {
        content: '';
        float: right;
        width: 20rpx;
        height: 34rpx;
      }
    }
    @include m(btn-hide) {
      float: right;
      color: #000;
      font-weight: 700;
      clear: both;
      margin-right: 15rpx;
    }
    @include m(btn-show) {
      color: #000;
      font-weight: 700;
    }
  }
}
.wrap {
  padding: 24rpx;
}

.item {
  padding: 24rpx 0;
  color: $u-content-color;
  font-size: 28rpx;
}
</style>
