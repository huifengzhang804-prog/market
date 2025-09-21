<script setup lang="ts">
import { defineProps, defineEmits, PropType, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { DoGetBargainListResponse } from '../../../source/index'

defineProps({
  item: { type: Object as PropType<DoGetBargainListResponse>, required: true },
})
const emit = defineEmits(['del'])
const router = useRouter()
const statusConfig = {
  NOT_STARTED: {
    title: '未开始',
    class: 'nots',
  },
  PROCESSING: {
    title: '进行中',
    class: 'ongoing',
  },
  OVER: {
    title: '已结束',
    class: 'hasEnded',
  },
  ILLEGAL_SELL_OFF: {
    title: '违规下架',
    class: 'off',
  },
}

const handleEdit = (item: DoGetBargainListResponse) => {
  const { id: activityId, shopId } = item
  router.push({
    name: 'bargainBaseinfo',
    query: {
      activityId,
      shopId,
    },
  })
}
const productNumComputed = computed(() => (val: string | number) => Number(val) === 0 ? '全部' : val + '件')
</script>

<template>
  <div class="column_bargain">
    <div class="column_bargain__left">
      <h1 class="column_bargain__left--title">{{ item.name }}</h1>
      <time>活动时间：{{ item.startTime }}至{{ item.endTime }}</time>
      <div>活动商品：{{ productNumComputed(item.productNum) }}</div>
      <div class="column_bargain__left--statistical">
        <span>参加人数：{{ item.peopleNum || 0 }}</span>
        <span>支付单数：{{ item.payOrder || 0 }}</span>
        <!-- <span>应收金额：{{ (item.amountReceivable && divTenThousand(item.amountReceivable)) || 0.0 }}</span> -->
      </div>
    </div>
    <div class="column_bargain__center">
      <h1 class="column_bargain__center--title" :class="statusConfig[item.status].class">
        {{ statusConfig[item.status].title }}
      </h1>
    </div>
    <div class="column_bargain__right">
      <el-button-group>
        <el-button round @click="handleEdit(item)"> 查看活动 </el-button>
        <el-button round @click="emit('del', item)">删除活动</el-button>
      </el-button-group>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(column_bargain) {
  width: 966px;
  height: 144px;
  background: #f9f9f9;
  margin-bottom: 10px;
  padding: 10px;
  @include flex;
  @include e(left) {
    @include flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    width: 50%;
    height: 100%;
    font-size: 12px;
    color: #333333;
    @include m(title) {
      font-size: 14px;
    }
    @include m(statistical) {
      width: 100%;
      color: #a9a9a9;
      @include flex;
      padding-right: 40px;
      justify-content: space-between;
    }
  }
  @include e(center) {
    width: 20%;
    height: 100%;
    @include m(title) {
      font-size: 14px;
    }
  }
  @include e(right) {
    width: 30%;
    height: 100%;
    @include flex;
  }
}
@include b(nots) {
  color: #2e99f3;
}
@include b(ongoing) {
  color: #f57373;
}
@include b(hasEnded) {
  color: #a9a9a9;
}
@include b(off) {
  color: #a9a9a9;
}
@include b(suspended) {
  color: #a9a9a9;
}
</style>
