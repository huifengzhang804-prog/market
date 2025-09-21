<script setup lang="ts">
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
</script>

<template>
  <view class="withdrawal-money-container"></view>
  <q-round-default text="提现"></q-round-default>
</template>

<style scoped lang="scss">
@include b(withdrawal-money-container) {
  /* padding: ; */
}
</style>
