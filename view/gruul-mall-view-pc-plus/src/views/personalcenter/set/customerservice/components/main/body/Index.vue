<template>
  <el-scrollbar ref="scrollbarRef" class="messages-admin-box" @scroll="scroll">
    <ul ref="contentRef" class="messages-admin-list">
      <BodyContents :userInfo="props.userInfo" :user="props.user" :messages="props.messages" />
    </ul>
  </el-scrollbar>
</template>
<script setup lang="ts">
import { MessageUser, MessageAndShopAdmin } from '@/views/personalcenter/set/customerservice/types'
import BodyContents from './contents/Index.vue'
import type { PropType } from 'vue'
import { ElScrollbar } from 'element-plus'

const props = defineProps({
  user: {
    type: Object as PropType<MessageUser>,
    default: () => {},
  },
  messages: {
    type: Array as PropType<Array<MessageAndShopAdmin>>,
    default: () => [],
  },
  userInfo: {
    type: Object,
    default: () => {},
  },
})
const emits = defineEmits(['loadMore'])
const scrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
const contentRef = ref()
const shopId = computed(() => props.user?.chatWithShopInfo?.shopId)
const lastTime = computed(() => {
  const messages = props.messages
  return messages && messages.length > 0 ? messages[0].sendTime : ''
})

const scroll2Bottom = () =>
  setTimeout(() => {
    scrollbarRef.value?.setScrollTop(contentRef.value?.clientHeight)
  }, 200)
watch(() => shopId.value, scroll2Bottom, { immediate: true })
watch(() => lastTime.value, scroll2Bottom)

const scroll = ({ scrollTop }: { scrollTop: number; scrollLeft: number }) => {
  if (scrollTop !== 0) return
  emits('loadMore')
}
</script>
<style scoped lang="scss">
.messages-admin-box {
  padding: $rows-spacing-row-sm;
  height: 100%;
}
</style>
