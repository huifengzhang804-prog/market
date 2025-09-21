<template>
    <div v-if="message" style="padding: 0 16px; overflow-y: scroll">
        <div class="detail-title">
            {{ message.title }}
        </div>
        <div class="detail-summary">
            <div v-show="message.createTime" class="detail-summary-tips">
                {{ message.createTime }}
            </div>
            <div class="detail-summary-content">
                {{ message.summary }}
            </div>
        </div>
        <div v-dompurify-html="message.content" class="detail-content" />
    </div>
</template>
<script setup lang="ts">
import { getMessageById, messagePage } from '@/apis/message'
import { StompMessage } from '@/components/layout/message/message'
import { configurePlatform } from '@/store/modules/configurePlatform'

import { useRoute } from 'vue-router'

const route = useRoute()
const message = ref<StompMessage | null>(null)
onMounted(() => {
    const messageId = route.query.id
    if (!messageId) {
        return
    }
    getMessageById(messageId).then((result) => {
        message.value = result.data
        console.log(result.data)
    })
})
const getDownloadCount = async () => {
    const { data } = (await messagePage({ read: false })) as any
    configurePlatform().SET_NEWS(data.total)
}
getDownloadCount()
</script>
<style scoped lang="scss">
@import '../../../../assets/css/global';

.detail-title {
    padding-top: 15px;
    text-align: center;
    color: $rows-color-title;
    font-size: 28px;
}

.detail-summary {
    padding: 20px 20px;
    color: $rows-color-subtitle;
    font-size: 10px;
}

.detail-summary-tips {
    text-align: right;
    color: $rows-text-color-grey;
    font-size: 10px;
}
.detail-summary-content {
    text-align: center;
    font-size: 8px;
    color: $rows-text-color-disable;
    margin-bottom: 10px;
}
.detail-content {
    letter-spacing: 1.1px;
    color: $rows-color-paragraph;
    font-size: 14px;
    line-height: 200%;
    overflow-wrap: break-word;
    padding-bottom: 16px;
}
:deep(table) {
    border-collapse: collapse;
    border: 2px solid rgb(204, 204, 204);
}
:deep(th) {
    border: 2px solid rgb(204, 204, 204);
}
:deep(td) {
    border: 2px solid rgb(204, 204, 204);
}
</style>
