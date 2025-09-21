<template>
    <el-scrollbar ref="scrollbarRef" class="messages-admin-box" @scroll="scroll">
        <ul ref="contentRef" class="messages-admin-list">
            <BodyContents :shop-info="props.shopInfo" :user="props.user" :messages="props.messages" />
        </ul>
    </el-scrollbar>
</template>
<script setup lang="ts">
import { MessageUser, MessageAndShopAdmin } from '../../../types'
import BodyContents from './contents/Index.vue'
import { ref, PropType, computed, watch } from 'vue'

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
const scrollbarRef = ref(null)
const contentRef = ref(null)

const scroll2Bottom = () => setTimeout(() => scrollbarRef.value?.setScrollTop(contentRef.value?.clientHeight), 200)
watch(() => props.user?.chatWithShopInfo.shopId, scroll2Bottom)
watch(() => props.messages, scroll2Bottom)

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
