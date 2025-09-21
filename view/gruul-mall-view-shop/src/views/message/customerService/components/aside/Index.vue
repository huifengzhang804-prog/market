<template>
    <div class="h100p">
        <AsideHeader @keywordChange="(val) => emits('keywordChange', val)" @searchFocus="(val) => emits('searchFocus', val)" />
        <AsideMain :messageUsers="messageUsers" @change="(val) => emits('change', val)" />
    </div>
</template>
<script setup lang="ts">
import AsideHeader from './AsideHeader.vue'
import AsideMain from './AsideMain.vue'
import { MessageUser } from '@/views/message/customerService/types'
import type { PropType } from 'vue'

defineProps({
    messageUsers: {
        type: Array as PropType<Array<MessageUser>>,
        default: () => [],
    },
})
const emits = defineEmits(['change', 'keywordChange', 'searchFocus'])
</script>
<style scoped>
.h100p {
    height: 100%;
}
</style>
