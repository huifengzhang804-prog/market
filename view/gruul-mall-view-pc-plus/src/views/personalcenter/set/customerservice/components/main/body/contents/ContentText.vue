<template>
  <div class="message-content" :class="clazz">
    {{ props.message.message }}
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { MessageAndShopAdmin } from '@/views/personalcenter/set/customerservice/types'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const props = defineProps({
  message: {
    type: Object as PropType<MessageAndShopAdmin>,
    required: true,
  },
  isMine: {
    type: Boolean,
    default: false,
  },
})
const clazz = computed(() => (props.isMine ? 'msg-mine' : 'msg-other'))
</script>

<style scoped lang="scss">
$not-mine-bg: #555cfd;
$mine-bg: #fff;
.message-content {
  font-size: 14px;
  font-weight: 500;
  height: 40px;
  margin-top: 2px;
  &.msg-other {
    background-color: $mine-bg !important;
    color: #333333;
    border-radius: 0 8px 8px 8px;
    border: 1px solid #e5e5e5;
  }
  &.msg-mine {
    background-color: $not-mine-bg !important;
    color: #fff;
    border-radius: 8px 0px 8px 8px;
    margin-right: $rows-spacing-row-sm;
  }
}
</style>
