<template>
    <el-scrollbar ref="scrollbarRef" class="messages-admin-box" @scroll="scroll">
        <ul ref="contentRef" class="messages-admin-list">
            <body-contents :shop-info="props.shopInfo" :user="props.user" :messages="props.messages" />
        </ul>
    </el-scrollbar>
</template>
<script setup lang="ts">
import { MessageUser, MessageAndShopAdmin } from '@/views/customerService/types'
import BodyContents from './contents/Index.vue'
import type { PropType } from 'vue'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
    messages: {
        type: Array as PropType<Array<MessageAndShopAdmin>>,
        default: () => [],
    },
    shopInfo: {
        type: Object,
        default: () => {},
    },
})
const emits = defineEmits(['loadMore'])
const scrollbarRef = ref<any>(null)
const contentRef = ref<any>(null)
const userId = computed(() => props.user?.chatWithUserInfo.userId)

const scroll2Bottom = () => setTimeout(() => scrollbarRef.value?.setScrollTop(contentRef.value?.clientHeight), 200)
watch(() => userId.value, scroll2Bottom, { immediate: true })

const scroll = ({ scrollTop }: any) => {
    if (scrollTop !== 0) return
    emits('loadMore')
}
defineExpose({
    scroll2Bottom,
})
</script>
<style scoped lang="scss">
.messages-admin-box {
    padding: $rows-spacing-row-sm;
}
</style>
