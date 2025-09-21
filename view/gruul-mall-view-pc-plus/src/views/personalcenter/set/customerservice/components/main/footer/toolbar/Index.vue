<template>
  <div class="customer-service-tool">
    <Expression @expression-select="expressionSelect" />
    <ImageUploader @image-select="imageSelect" />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { ToolbarMessageType, ToolbarMessage, MessageUser } from '@/views/personalcenter/set/customerservice/types'
import Expression from './expression/Index.vue'
import ImageUploader from './image/Index.vue'

const props = defineProps({
  user: {
    type: Object as PropType<MessageUser>,
    default: () => {},
  },
})
const emits = defineEmits(['contentChange'])

const emitMessage = (message: ToolbarMessage) => {
  emits('contentChange', message)
}
const expressionSelect = (expression: string) => {
  emitMessage({ type: ToolbarMessageType.EXPRESSION, content: expression })
}
const imageSelect = (imageUrl: string) => {
  emitMessage({ type: ToolbarMessageType.IMAGE, content: imageUrl })
}
</script>

<style scoped lang="scss">
.customer-service-tool {
  display: flex;
  align-items: center;
  height: 100%;
  color: $rows-text-color;
  height: 21px;
  > div {
    margin-left: 12px;
  }
  .el-button {
    font-size: $rows-font-size-sm;
  }
}
</style>
