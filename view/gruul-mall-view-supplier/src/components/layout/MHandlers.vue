<template>
    <div class="icon-right">
        <el-badge
            :value="infoCount.download"
            :max="10"
            :offset="[1, 1]"
            :hidden="infoCount.download === 0"
            :style="{ marginRight: infoCount.download ? '20px' : '10px' }"
            @click="handleClick"
        >
            <QIcon name="icon-gonggao1" size="20px" class="badge_container" @click="handleClick" />
        </el-badge>
        <QIcon id="download_icon" name="icon-xiazai" size="20px" class="badge_container affter" @click="handleJumpToDownloadCenter"></QIcon>
        <MSetUp class="badge_container" />
    </div>
</template>
<script lang="ts" setup>
import { isConnected } from '@/composables/stomp/StompHandler'
import { message, messageInit } from './message/HeaderMessage'
import { useRouter } from 'vue-router'
import { doGetDownloadingFileCount } from '@/apis/overview'
import MSetUp from './MSetUp.vue'
import { configurePlatform } from '@/store/modules/configurePlatform'
import { messagePage } from '@/apis'

//消息中心uri
const msgCenter = '/message/notice'
const $router = useRouter()
const $route = useRoute()
const infoCount = reactive({
    download: 0,
})
//钩子
onMounted(() => {
    messageInit()
})
watch(
    () => configurePlatform().getSetNews,
    (val) => {
        infoCount.download = val
    },
)
const handleClick = () => {
    const url = !isConnected || !message.value ? msgCenter : message.value?.url
    if (url) {
        $router.push('/message/notice')
    }
}

const DOWNLOAD_CENTER_PATH = '/download/center'
const handleJumpToDownloadCenter = () => {
    $router.push({ path: DOWNLOAD_CENTER_PATH })
}
</script>
<style lang="scss" scoped>
@import './message/MMessage.scss';
.badge_container {
    color: white;
    cursor: pointer;
}
.icon-right {
    display: flex;
    align-items: center;
    height: 24px;
}
.affter::after {
    content: '|';
    font-size: 14px;
    position: relative;
    top: -1px;
    margin-left: 10px;
}

:deep(.el-badge__content, .is-fixed) {
    width: 20px;
    line-height: 20px;
    text-align: center;
    background-color: #f54319;
    font-weight: 400;
    border: transparent;
    color: #fff;
    font-size: 12px;
}
</style>
