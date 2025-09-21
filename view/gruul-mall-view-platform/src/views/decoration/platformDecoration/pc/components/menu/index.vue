<script setup lang="ts">
import toolbar from './toolbar/index.vue'
import actionMenu from './action-menu/index.vue'
import { ActiveName, xPosition, yPosition } from './common'
import { useVModel } from '@vueuse/core'

const props = defineProps({
    dragging: {
        type: Boolean,
        required: true,
    },
})

const emit = defineEmits(['update:dragging'])

const dragging = useVModel(props, 'dragging', emit)

const headerHeight = 50 // 顶部header高度
const sideWidth = 68 // 侧边宽度
const width = 360 // 整个宽度
const height = 488 // 整个高度
/**
 * 打开菜单
 */
const toolbarShow = ref(true)
const activeName = ref<ActiveName>('添加组件')
const openMenu = (name: ActiveName) => {
    toolbarShow.value = false
    // 校验展开后是否超过边界
    const left = xPosition(position.value.left - sideWidth, width)
    const top = yPosition(position.value.top, height, headerHeight)
    position.value = { left, top }
    activeName.value = name
}

const position = ref({
    top: 200,
    left: 40,
})

defineExpose({
    openMenu,
})
</script>

<template>
    <div class="menu-box">
        <!-- 工具栏 -->
        <toolbar v-if="toolbarShow" v-model:position="position" v-model:dragging="dragging" @open-menu="openMenu" />

        <!-- 操作菜单 -->
        <action-menu
            v-else
            v-model:active-name="activeName"
            v-model:dragging="dragging"
            v-model:position="position"
            @hide-menu="toolbarShow = true"
        />
    </div>
</template>

<style lang="scss" scoped>
@include b(menu-box) {
    position: fixed;
    z-index: 6;
}
</style>
