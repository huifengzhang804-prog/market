<script setup lang="ts">
import type { PropType } from 'vue'
import { menuItem, xBoundary, yBoundary, xPosition, yPosition } from '../common'
import { useDraggable, useVModel } from '@vueuse/core'

const props = defineProps({
    position: {
        type: Object as PropType<{ left: number; top: number }>,
        required: true,
    },
    dragging: {
        type: Boolean,
        required: true,
    },
})

const emit = defineEmits(['open-menu', 'update:position', 'update:dragging'])
const dragging = useVModel(props, 'dragging', emit)

const el = ref<HTMLElement | null>(null)
const headerHeight = 50 // 顶部header高度
const width = 68 // 整个宽度
const height = 206 // 整个高度
useDraggable(el, {
    preventDefault: true,
    onMove({ x, y }) {
        const { left: l, top: t } = props.position

        // 校验本次拖动是否小与边界是否
        if (xBoundary(x, width, l) || yBoundary(y, height, t, headerHeight)) return

        // 校验本次拖动是否小与边界是否
        const left = xPosition(x, width)
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

/**
 * 鼠标移入菜单
 */
const enterName = ref('')
const enter = (name: string) => (enterName.value = name)

/**
 * 判断鼠标是否移入
 */
const isEnter = (val?: string) => {
    const flag = val ? val === enterName.value : enterName.value

    return flag && !dragging.value
}
</script>

<template>
    <ul
        class="menu"
        :style="{ transform: `translate(${position.left}px, ${position.top}px)` }"
        :class="{ 'menu-active': isEnter() }"
        @mouseleave="enter('')"
    >
        <li ref="el" class="drag"></li>
        <li
            v-for="(item, index) in menuItem"
            :key="item.text"
            class="menu__item"
            :class="{ active: isEnter(item.text) }"
            :index="item.text"
            @mouseenter="enter(item.text)"
            @click="emit('open-menu', item.text)"
        >
            <div>
                <q-icon svg :name="isEnter(item.text) ? item.activeIcon : item.icon" :class="{ 'm-r-10': enterName }" size="36px" />
                <div v-if="index !== menuItem.length - 1" class="menu__item--line" />
            </div>

            <div v-show="isEnter()" class="menu__item--text">{{ item.text }}</div>
        </li>
    </ul>
</template>

<style lang="scss" scoped>
@include b(drag) {
    position: absolute;
    height: 24px;
    top: 0;
    left: 0;
    width: 100%;
    cursor: move;
}

@include b(menu) {
    position: fixed;
    width: 68px;
    display: inline-block;
    height: 206px;
    padding: 16px;
    padding-top: 24px;
    border-radius: 9px;
    background-color: #fff;
    transition: width 0.3s, padding 0.3s;
    overflow: hidden;
    box-shadow: 2px 2px 8px -6px #f54319;

    @include e(item) {
        display: flex;
        align-items: center;
        height: 68px;
        cursor: pointer;
        position: relative;

        &:nth-child(2) {
            height: 44px;
            align-items: flex-start;

            .menu__item--text {
                line-height: 36px;
                height: 100%;
            }
        }

        &:last-child {
            height: 52px;
        }

        @include m(line) {
            width: 36px;
            height: 1px;
            position: absolute;
            margin: 0 auto;
            bottom: 0;
            background: #ebebeb;
        }

        @include m(text) {
            white-space: nowrap;
        }
    }
}

.menu-active {
    width: 148px;
}

.menu__item.active {
    color: #f54319;
}
</style>
