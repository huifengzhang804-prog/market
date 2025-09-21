<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import defaultResizeImage from './resize-image'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultResizeImage>,
        default: () => defaultResizeImage,
    },
})
const $emit = defineEmits(['update:formData'])
const upLoadImg = useVModel($props, 'formData', $emit)
// 初始化图片位置
const intTop = ref(0)
const intLeft = ref(0)
const moveType = ref(false)
// 移动后图片位置
const vw = ref(0)
const vh = ref(0)
const top = ref(0)
const left = ref(0)
const showType = ref(false)
watch(
    () => $props.formData,
    (newval) => {
        if (newval.img && newval) {
            showType.value = true
            intTop.value = Number(newval.top)
            intLeft.value = Number(newval.left)
            vw.value = Number(newval.width.match(/(\S*)px/)?.[1])
            vh.value = Number(newval.height.match(/(\S*)px/)?.[1])
        }
    },
    { immediate: true, deep: true },
)

const resize = (newRect: { x: number; y: number; h: number; w: number }) => {
    moveType.value = true
    // intTop.value = 0
    // intLeft.value = 0
    top.value = newRect.y
    left.value = newRect.x
    upLoadImg.value.top = newRect.y
    upLoadImg.value.left = newRect.x
    if (newRect.w && newRect.h) {
        upLoadImg.value.width = newRect.w + 'px'
        upLoadImg.value.height = newRect.h + 'px'
        vw.value = newRect.w
        vh.value = newRect.h
    }
}
</script>

<template>
    <div style="margin-top: 5px" :style="{ height: `${upLoadImg.boxHeight}px` }">
        <VueDragResize :w="vw" :h="vh" :x="intLeft" :y="intTop" :parent="true" @resizing="resize" @dragging="resize">
            <div class="box" :style="{ width: +vw + 'px', height: +vh + 'px' }">
                <img
                    v-if="showType"
                    :src="upLoadImg.img || 'https://qiniu-app.qtshe.com/u391.png'"
                    :style="{
                        width: vw + 'px',
                        height: vh + 'px',
                    }"
                />
            </div>
        </VueDragResize>
    </div>
</template>

<style lang="scss" scoped></style>
