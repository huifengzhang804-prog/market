<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
import { useRouter } from 'vue-router'
import type { ApiGroupList } from '../'

defineProps({
  item: { type: Object as PropType<ApiGroupList>, required: true },
})
const emit = defineEmits(['del', 'cell-off', 'check-reason'])
const router = useRouter()
const statusConfig = {
  OPENING: {
    title: '未开始',
    class: 'nots',
  },
  PREHEAT: {
    title: '预售中',
    class: 'preheat',
  },
  OPEN: {
    title: '进行中',
    class: 'ongoing',
  },
  FINISHED: {
    title: '已结束',
    class: 'hasEnded',
  },
  SHOP_OFF_SHELF: {
    title: '已下架',
    class: 'off',
  },
  VIOLATION: {
    title: '违规下架',
    class: 'off',
  },
}

const handleEdit = (id: string, isLookUp: 'true' | 'false' | '') => {
  router.push({
    name: 'marketingAppGroupForm',
    query: {
      id,
      isLookUp,
    },
  })
}
</script>

<template>
  <div class="column">
    <el-checkbox :value="item" class="m-r-14" />
    <div class="column__left">
      <h1 class="column__left--title">{{ item.name }}</h1>
      <time>活动时间：{{ item.startTime }} 至 {{ item.endTime }}</time>
      <div>活动商品：{{ item.productNum || 0 }}件</div>
      <!-- <div class="column__left--statistical">
                <span>参加人数：{{ item.users || 0 }}</span>
                <span>支付单数：{{ item.orders || 0 }}</span>
            </div> -->
    </div>
    <div class="column__center">
      <h1 class="column__center--title" :class="statusConfig[item.status].class">
        {{ statusConfig[item.status].title }}
      </h1>
    </div>
    <div class="column__right">
      <el-button-group>
        <el-button round @click="handleEdit(item.id, true)">查看 </el-button>
        <!-- <el-button v-if="item.status === 'OPENING'" round @click="handleEdit(item.id)">编辑 </el-button> -->
        <el-button v-if="item.status === 'VIOLATION'" round @click="emit('check-reason', item.id)"> 违规原因 </el-button>
        <el-button v-if="item.status !== 'OPEN'" round @click="emit('del', { shopId: item.shopId, activityId: item.id })"> 删除 </el-button>
        <el-button v-else round @click="emit('cell-off', item.id)"> 下架 </el-button>
      </el-button-group>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(column) {
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
@include b(preheat) {
  color: '#29C90A';
}

@include b(m-r-14) {
  margin-right: 14px;
}
</style>
