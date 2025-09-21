<script setup lang="ts">
import { ref, watch, reactive, toRefs, computed, type PropType } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetHistoriesAndHotWords, doDeleteUserHistory } from '@/apis/good'
import { useUserStore } from '@/store/modules/user'
import { STORAGE_KEY, type JointStorageKey } from './config'
import storage from '@/utils/storage'

const $props = defineProps({
  usedHeight: { type: Number, default: 0 },
  hotWords: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  isGoods: {
    type: Boolean,
    default: true,
  },
  modelValue: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})
const emit = defineEmits(['update-hotwords', 'update:modelValue'])
// 单位 rpx
const history_height = 80
const historyData = reactive({
  historyKey: STORAGE_KEY.historyWords as JointStorageKey,
})
const { historyKey } = toRefs(historyData)
const isSearchGoods = computed(() => historyKey.value === STORAGE_KEY.historyWords)

watch(
  () => $props.isGoods,
  (val) => {
    historyKey.value = val ? STORAGE_KEY.historyWords : STORAGE_KEY.liveWords
    initHistory()
  },
  {
    immediate: true,
  },
)

function initHistory() {
  const history = storage.get(historyKey.value) || []
  emit('update:modelValue', history)
  if (isSearchGoods.value) {
    doGetHistoriesAndHotWords().then(({ code, data }) => {
      if (code !== 200) return
      //搜索热词
      emit('update-hotwords', data.hotWords)
      //搜索历史
      const words = data.histories
      if (words.length > 0) {
        emit('update:modelValue', words)
      }
    })
  }
}

const handleClearStorage = () => {
  uni.showModal({
    title: '提示',
    content: '确定删除搜索历史吗?',
    success(res) {
      if (!res.confirm) {
        return
      }
      storage.remove(historyKey.value)
      emit('update:modelValue', [])
      uni.showToast({
        title: '删除成功',
        icon: 'none',
        duration: 800,
      })
      if (useUserStore().userInfo.token) {
        doDeleteUserHistory()
      }
    },
  })
}
</script>

<template>
  <view class="history">
    <view class="history__h1">
      <text class="history__h1--text">搜索记录</text>
      <q-icon v-if="modelValue.length" name="icon-shanchu" size="45rpx" color="#999999" @click="handleClearStorage" />
    </view>
  </view>
  <scroll-view scroll-y :style="{ height: `calc(100vh - ${usedHeight}rpx - ${history_height}rpx)` }" class="scroll">
    <view v-if="modelValue.length" class="history_list">
      <view v-for="item in modelValue" :key="item" class="history_list__item">{{ item }}</view>
    </view>
    <view v-else class="empty">还没有搜索记录~</view>
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(history) {
  background: #fff;
  @include e(h1) {
    @include flex;
    justify-content: space-between;
    padding: 0 40rpx;
    height: 80rpx;
    align-items: center;
    @include m(text) {
      font-size: 30rpx;
    }
  }
}
@include b(scroll) {
  background: #fff;
}
@include b(history_list) {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
  padding-left: 20rpx;
  @include e(item) {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10rpx 20rpx;
    background: #f2f2f2;
    border-radius: 52rpx;
    margin: 0 20rpx 20rpx 20rpx;
    min-width: 80rpx;
    color: #0907078c;
  }
}
.empty {
  height: 100rpx;
  line-height: 120rpx;
  text-align: center;
  color: #201e1e93;
}
</style>
