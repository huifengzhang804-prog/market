<script setup lang="ts">
// import { ElMessage } from 'element-plus'
// import { useDecorationStore } from '@/store/modules/decoration'
// import { doGetEnumOfDecoration } from '@/apis/decoration'
type DecorationType = {
    aggregationPlatform: string
    homePageName: string
}

const decorationType = ref<DecorationType[]>([])
// const $decorationStore = useDecorationStore()

// async function initDecorationType() {
//     const { code, data } = await doGetEnumOfDecoration(['PC'])
//     if (code !== 200) return ElMessage.error('获取装修类型错误')
//     decorationType.value = data
// }
const handleNavToSet = (info?: DecorationType) => {
    // if (info) {
    // 暂存写死
    // $decorationStore.SET_DEC_TYPE('PC')
    // }
}
</script>

<template>
    <router-link target="_blank" :to="{ path: '/decoration/pc/set' }" @click="handleNavToSet(decorationType[0])">
        <el-button class="choose-card"> PC商城端 </el-button>
    </router-link>
</template>

<style scoped></style>
