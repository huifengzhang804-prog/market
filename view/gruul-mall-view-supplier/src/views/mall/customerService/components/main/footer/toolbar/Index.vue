<template>
    <div class="customer-service-tool">
        <Expression @expression-select="expressionSelect" />
        <ImageUploader @image-select="imageSelect" />
        <!-- TODO: 供应商聊天暂时不要商品和订单的发送 -->
        <!-- <CommodityUploader @submit-message="sendProduct" />
        <OrderUploader :user="props.user" @order-handle="sendProduct" /> -->
    </div>
</template>

<script setup lang="ts">
import { ToolbarMessageType, ToolbarMessage } from '../../../../types'
import Expression from './expression/Index.vue'
import ImageUploader from './image/Index.vue'

const emits = defineEmits(['contentChange'])

const emitMessage = (message: ToolbarMessage) => {
    emits('contentChange', message)
}
const expressionSelect = (expression: string) => {
    emitMessage({ type: ToolbarMessageType.EXPRESSION, content: expression })
}
const imageSelect = (imageUrl) => {
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
