<script lang="ts" setup>
import { nextTick, type Ref, ref } from 'vue'

const barrage: Ref<any[]> = ref([])
const barrageListRef: Ref<HTMLElement | null> = ref(null)
const addBarrage = (content: string) => {
  const barrageOptions = {
    content,
  }
  barrage.value.push(barrageOptions)
  nextTick(() => {
    if (barrageListRef.value) {
      barrageListRef.value.scrollTop = barrageListRef.value.scrollHeight
    }
  })
}
defineExpose({ addBarrage })
</script>

<template>
  <div class="barrage">
    <div class="barrage__notice">
      直播间严禁出现危害国家安全，破坏政治和社会稳定的言论，严禁出现低俗色情、售假售劣、售卖违禁品，引导私下交易等行为，不要在直播中从事挂机、录播、盗播等行为。若违反，平台有权依据规则对您采取相关管控。
    </div>
    <div class="barrage__main">
      <div ref="barrageListRef" class="barrage__list">
        <div v-for="(item, i) in barrage" :key="i" class="barrage__list--main">
          <span class="nickname">取个名字好难：</span>
          <span>{{ item.content }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(barrage) {
  position: absolute;
  bottom: 50px;
  left: 15px;
  @include e(notice) {
    font-size: 13px;
    color: #999;
    padding: 0 10px;
    line-height: 1.3;
  }
  @include e(main) {
    height: 300px;
    overflow: hidden;
    width: 180px;
    position: relative;
    margin-top: 15px;
    font-size: 12px;
    padding-left: 10px;
    padding-bottom: 10px;
  }
  @include e(list) {
    width: 100%;
    height: 100%;
    overflow-y: auto;
    &::-webkit-scrollbar {
      width: 2px; /* 设置滚动条的宽度 */
    }
    @include m(main) {
      line-height: 20px;
      margin-top: 10px;
      padding: 8px;
      border-radius: 10px;
      background-color: rgba(0, 0, 0, 0.3);
      color: #fff;
      .nickname {
        color: #f90;
      }
    }
  }
}
</style>
