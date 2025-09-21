<script setup lang="ts">
import leftBar from './views/left-bar.vue'
import editorComponent from './views/editor-component.vue'
import customPage from './views/custom-page/index.vue'
import other from './views/other.vue'
import { useDraggable, useVModel } from '@vueuse/core'
import { type ActiveName, xBoundary, yBoundary, xPosition, yPosition } from '../common'

const props = defineProps<{
    activeName: ActiveName
    dragging: boolean
    position: { left: number; top: number }
}>()

/**
 * 切换
 */
const emit = defineEmits(['update:activeName', 'update:position', 'hide-menu', 'update:dragging'])

const title = useVModel(props, 'activeName', emit)
const dragging = useVModel(props, 'dragging', emit)

const el = ref<HTMLElement | null>(null)

const headerHeight = 50 // 顶部header高度
const sideWidth = 68 // 侧边宽度
const width = 360 // 整个宽度
const height = 488 // 整个高度
useDraggable(el, {
    preventDefault: true,
    onMove({ x, y }) {
        const { left: l, top: t } = props.position

        // 校验本次拖动是否小与边界
        if (xBoundary(x, width, l) || yBoundary(y, height, t, headerHeight)) return

        // 校验本次拖动是否小与边界
        const left = xPosition(x - sideWidth, width)
        const top = yPosition(y, height, headerHeight)

        emit('update:position', { left, top })
    },
    onStart() {
        dragging.value = true
    },
    onEnd() {
        dragging.value = false
    },
})

const components = {
    添加组件: editorComponent,
    自定义页面: customPage,
    基础设置: other,
} as const

const change = (name: ActiveName) => {
    title.value = name
}
</script>

<template>
    <div class="main" :style="{ transform: `translate(${position.left}px, ${position.top}px)` }">
        <left-bar :active-name="title" @change-menu="change" />

        <div class="main__title p-l-68">
            <div ref="el" class="drag">
                {{ title }}
                <q-icon name="icon-guanbi3 main__title--icon" @click="emit('hide-menu')" />
            </div>
        </div>
        <div class="p-l-68 main__pane">
            <keep-alive>
                <component :is="components[title]" />
            </keep-alive>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(drag) {
    height: 100%;
    cursor: move;
}

.p-l-68 {
    padding-left: 68px;
}

@include b(main) {
    position: fixed;
    height: 488px;
    width: 360px;
    border-radius: 9px;
    background-color: #fff;
    overflow: hidden;
    box-shadow: 2px 2px 8px -6px #f54319;

    @include e(title) {
        height: 68px;
        background-color: #f54319;
        font-size: 16px;
        color: #ffffff;
        text-align: center;
        line-height: 68px;
        border-radius: 15px 0 0 0;
        position: relative;

        @include m(icon) {
            position: absolute;
            right: 16px;
            cursor: pointer;
            transition: all 0.3s;
            width: 20px;
            height: 20px;
            top: 24px;
            line-height: 20px;
            &:hover {
                transform: rotate(180deg);
            }
        }
    }

    @include e(pane) {
        height: calc(100% - 68px);
    }
}
</style>
