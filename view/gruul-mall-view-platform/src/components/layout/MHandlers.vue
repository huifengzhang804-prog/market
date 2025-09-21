<template>
    <div>
        <el-badge :value="infoCount.download" :max="10" :hidden="infoCount.download === 0" class="msg-bell" @click="handleJumpToDownloadCenter">
            <QIcon id="download_icon" name="icon-xiazai" style="margin-right: 8px" size="20px" class="badge_container"></QIcon> </el-badge
        ><span style="color: #fff; position: relative; top: 1px">|</span>
        <MAvatar class="badge_container" />
    </div>
</template>

<script setup lang="ts">
import { doGetDownloadingFileCount } from '@/apis/overview'
import { useRoute } from 'vue-router'
import MAvatar from './MAvatar.vue'

const route = useRoute()
const router = useRouter()
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
onMounted(() => getDownloadCount())

const DOWNLOAD_CENTER_PATH = '/download/center'
const handleJumpToDownloadCenter = () => {
    router.push({ path: DOWNLOAD_CENTER_PATH })
}
watch(
    () => route.path,
    (val) => {
        if (val === DOWNLOAD_CENTER_PATH) {
            getDownloadCount()
        }
    },
)
</script>

<style scoped lang="scss">
.badge_container {
    color: white;
    cursor: pointer;
}
</style>
