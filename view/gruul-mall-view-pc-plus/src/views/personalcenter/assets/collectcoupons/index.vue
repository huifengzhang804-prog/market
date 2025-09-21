<script setup lang="ts">
import { ref } from 'vue'
</script>

<template>
  <div bg-white>
    <div c-w-1190 ma>
      <div c-fs-18 c-c-101010 fw-700 c-pt-36 c-mb-32>为你推荐好券</div>
      <div flex justify-between flex-wrap text-left c-br-2>
        <div v-for="item in 4" :key="item" c-w-392 c-h-126 flex items-center e-bs c-mb-7>
          <div c-h-92 c-w-122 b-r b-dashed c-bc-cfcfcf>
            <div c-fs-30 fw-700 c-c-e31436 c-ml-12 c-mt-10><span c-fs-13>￥</span>10000</div>
            <div c-fs-13 c-c-555 c-ml-23>满1000可用</div>
          </div>
          <div c-fs-13 c-ml-10 c-w-205 c-h-126 relative c-pt-40>
            <div c-mb-11>限百诺恩旗舰店部分商品使用</div>
            <div c-c-888>2023-04-14~2023-04-14</div>
            <div c-w-60 c-h-60 bg-black absolute right-0 top-0 />
          </div>
          <div c-w-55 c-h-126 bg-black>立即 使用</div>
        </div>
      </div>
      <div c-mt-50 c-pb-43 flex justify-center>
        <el-pagination background layout="prev, pager, next" :total="500" />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
