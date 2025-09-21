<template>
  <view class="membership">
    <SuperMember v-if="memberInfo?.memberType === 'PAID_MEMBER'" />
    <NormalMember v-else />
  </view>
</template>

<script lang="ts" setup>
import { useUserStore } from '@/store/modules/user'
import NormalMember from './normalMember.vue'
import SuperMember from './superMember.vue'
const memberInfo = useUserStore().getterMemberInfo
</script>

<style lang="scss" scoped>
@include b(membership) {
  padding: 41rpx 20rpx;
}
</style>
