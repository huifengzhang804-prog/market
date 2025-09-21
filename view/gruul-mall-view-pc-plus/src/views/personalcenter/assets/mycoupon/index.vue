<script setup lang="ts">
import PcMyCouponList from '@/q-plugin/coupon/PcMyCouponList.vue'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma>
      <div flex>
        <center-nav />
        <div c-w-1024 c-ml-49>
          <PcMyCouponList />
        </div>
      </div>
      <ToTopGoCar />
    </div>
  </div>
</template>
