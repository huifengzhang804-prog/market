<script setup lang="ts">
import type { PropType } from 'vue'
import defaultVideoData from './video'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultVideoData>,
        default() {
            return defaultVideoData
        },
    },
})
</script>

<template>
    <div class="videoCom" :style="{ padding: $props.formData.radioTC === 2 ? '10px' : '' }">
        <img src="https://qiniu-app.qtshe.com/u391.png" style="width: 100%; height: 200px" />
        <i
            class="iconfont iconzhibo1 videoIcon"
            :style="{ bottom: $props.formData.radioTC === 2 ? '25px' : '10px', left: $props.formData.radioTC === 2 ? '20px' : '5px' }"
        ></i>
    </div>
</template>

<style lang="scss" scoped></style>
