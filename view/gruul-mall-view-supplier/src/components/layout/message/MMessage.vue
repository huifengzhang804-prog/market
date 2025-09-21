<template>
    <p class="msg">
        <!-- <img class="msg-icon" src="../../../assets/image/icon/notice.png" alt="消息中心" title="消息中心" /> -->
        <!-- <transition name="el-zoom-in-center">
            <el-link
                :underline="false"
                :class="['msg-title', { 'msg-warning': !isConnected }]"
                :title="!message ? '暂无消息' : message.title"
                @click="handleClick"
            >
                <span class="description">
                    {{ isConnected ? (!message ? '前往消息中心' : message.title) : '已断开,正在尝试重新连接...' }}
                </span>
            </el-link>
        </transition>
        <router-link :to="msgCenter" class="more">更多</router-link> -->
        <el-badge :value="messages.length" :max="10" :hidden="messages.length === 0" title="消息中心" class="msg-bell" @click="handleClick">
            <Bell style="cursor: pointer" />
        </el-badge>
        <el-badge
            :value="infoCount.download"
            :max="10"
            :hidden="infoCount.download === 0"
            class="msg-bell"
            style="margin: 0 25px"
            @click="handleJumpToDownloadCenter"
        >
            <el-icon :size="19" style="cursor: pointer"><Download /></el-icon>
        </el-badge>
    </p>
</template>
<script lang="ts" setup>
import { onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { isConnected } from '@/composables/stomp/StompHandler'
import { message, messageInit, messages } from './HeaderMessage'
import { useRouter } from 'vue-router'
import { doGetDownloadingFileCount } from '@/apis/overview'
import { Download } from '@element-plus/icons-vue'
//消息中心uri
const msgCenter = '/message/notice'
const $router = useRouter()
//钩子
const $route = useRoute()
const infoCount = reactive({
    download: 0,
})

const getDownloadCount = async () => {
    try {
        const { data } = await doGetDownloadingFileCount()
        infoCount.download = +data
    } catch (err) {
        infoCount.download = 0
    }
}
// const props = defineProps({})

const DOWNLOAD_CENTER_PATH = '/download/center'
const handleJumpToDownloadCenter = () => {
    $router.push({ path: DOWNLOAD_CENTER_PATH })
}
watch(
    () => $route.path,
    (val) => {
        if (val === DOWNLOAD_CENTER_PATH) {
            getDownloadCount()
        }
    },
)
onMounted(() => {
    getDownloadCount()
    messageInit()
})
const handleClick = () => {
    const url = !isConnected || !message.value ? msgCenter : message.value?.url
    if (url) {
        $router.push('/message/notice')
    }
}
</script>
<style lang="scss" scoped>
@import './MMessage';
.msg-bell {
    width: 20px;
}
:deep(.el-badge__content),
:deep(.is-fixed) {
    background-color: transparent;
    border: transparent;
    color: #f72020;
    font-weight: bold;
    font-size: 14px;
    padding-left: 5px;
    border: transparent;
}
</style>
