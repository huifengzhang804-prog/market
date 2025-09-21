<template>
    <el-scrollbar ref="scrollbarRef" class="messages-admin-box" @scroll="scroll">
        <ul ref="contentRef" class="messages-admin-list">
            <BodyContents :shop-info="props.shopInfo" :user="props.user" :messages="props.messages" />
        </ul>
    </el-scrollbar>
</template>
<script setup lang="ts">
import { MessageUser, Message } from '@/views/message/components/types'
import BodyContents from './contents/Index.vue'
import type { PropType } from 'vue'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser | null>,
        default: () => {},
    },
    messages: {
        type: Array as PropType<Array<Message>>,
        default: () => [],
    },
    shopInfo: {
        type: Object,
        default: () => {},
    },
})
const emits = defineEmits(['loadMore'])
const scrollbarRef = ref(null)
const contentRef = ref(null)
const userId = computed(() => props.user?.chatWithShopInfo.shopId)

const scroll2Bottom = () => setTimeout(() => scrollbarRef.value?.setScrollTop(contentRef.value?.clientHeight), 200)
watch(() => userId.value, scroll2Bottom, { immediate: true })
watch(() => props.messages.length, scroll2Bottom)

const scroll = ({ scrollTop }) => {
    if (scrollTop !== 0) return
    emits('loadMore')
}
</script>
<style scoped lang="scss">
.messages-admin-box {
    padding: $rows-spacing-row-sm;
    // height: 50vh;
}
</style>
