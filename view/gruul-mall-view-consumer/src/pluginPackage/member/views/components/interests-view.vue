<script setup lang="ts">
import { ref, watch } from 'vue'
import { currentMember } from '@pluginPackage/member/views/components/currentMember'
import type { RelevancyRights } from '@/apis/plugin/member/model'

const { divHundred } = useConvert()
const currentMemberRight = ref<RelevancyRights>()

watch(
  () => currentMember.value?.id,
  () => {
    currentMemberRight.value = currentMember.value?.relevancyRightsList[0]
  },
)

const handleInterestsClick = (e: RelevancyRights) => {
  currentMemberRight.value = e
}
/**
 * 会员权益扩展值
 */
const strategyPatternHandler = {
  GOODS_DISCOUNT: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}全场商品${divHundred(memberRight.extendValue)}折`
    },
  },
  INTEGRAL_MULTIPLE: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}${divHundred(memberRight.extendValue)}倍`
    },
  },
  PRIORITY_SHIPMENTS: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}`
    },
  },
  LOGISTICS_DISCOUNT: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}`
    },
  },
  QUICKNESS_AFS: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}`
    },
  },
  EXCLUSIVE_SERVICE: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}`
    },
  },
  USER_DEFINED: {
    msg: (memberRight: RelevancyRights) => {
      return `${memberRight.rightsExplain}`
    },
  },
}
</script>
<template>
  <view v-if="currentMember && currentMember.relevancyRightsList.length" class="interests-view">
    <view class="interests-view--title">会员权益</view>
    <u-grid :col="4" :border="false" @click="handleInterestsClick">
      <u-grid-item v-for="item in currentMember?.relevancyRightsList" :key="item.memberRightsId" :index="item">
        <u-icon :name="item.rightsIcon" :size="86"></u-icon>
        <view class="grid-text">{{ item.rightsName }}</view>
      </u-grid-item>
    </u-grid>
    <view class="interests-instructions">
      <view class="interests-instructions--title">{{ currentMemberRight?.rightsName }}</view>
      <text class="interests-instructions--contarner">
        {{ currentMemberRight?.rightsType ? strategyPatternHandler[currentMemberRight?.rightsType].msg(currentMemberRight) : '会员权益' }}
      </text>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(interests-view) {
  margin-top: 15rpx;
  padding: 30rpx;
  background: #fff;
  @include m(title) {
    color: #333333;
    font-size: 30rpx;
    font-weight: 700;
    margin-bottom: 20rpx;
    padding-left: 20rpx;
  }
}
@include b(interests-instructions) {
  min-height: 154rpx;
  border-radius: 4rpx;
  background-color: rgba(255, 255, 255, 1);
  border: 1px solid rgba(242, 242, 242, 1);
  padding: 20rpx;
  @include m(title) {
    font-weight: 700;
    font-size: 24rpx;
    margin-bottom: 16rpx;
  }
  @include m(contarner) {
    font-size: 24rpx;
  }
}
</style>
