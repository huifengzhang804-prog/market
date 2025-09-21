<template>
    <div class="h100p">
        <AsideHeader @keyword-change="(val) => emits('keywordChange', val)" @search-focus="(val) => emits('searchFocus', val)" />
        <AsideMain :currentSelectUser="currentSelectUser" :message-users="messageUsers" @change="(val) => emits('change', val)" />
    </div>
</template>
<script setup lang="ts">
import AsideHeader from './AsideHeader.vue'
import AsideMain from './AsideMain.vue'
import { MessageUser } from '@/views/message/components/types'
import type { PropType } from 'vue'

defineProps({
    currentSelectUser: {
        type: Object as PropType<null | MessageUser>,
        default: null,
    },
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
