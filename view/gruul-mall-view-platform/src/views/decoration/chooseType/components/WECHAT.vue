<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'

const $props = defineProps({
    templateType: {
        type: String,
        default() {
            return 'PLATFORM'
        },
    },
    isO2o: {
        type: Boolean,
        default() {
            return false
        },
    },
})
const $decorationStore = useDecorationStore()
const toPath = computed(() => {
    return $props.templateType === 'PLATFORM' ? '/decoration/set' : $props.isO2o ? '/decoration/shop/o2o/set' : '/decoration/shop/set'
})

const handleNavToSet = () => {
    $decorationStore.SET_ENDPOINT_TYPE('WECHAT_MINI_APP')
}
</script>

<template>
    <router-link target="_blank" :to="{ path: toPath }" @click="handleNavToSet">
        <el-button class="choose-card"> 微信小程序端 </el-button>
    </router-link>
</template>

<style scoped></style>
