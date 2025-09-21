<script setup lang="ts">
import editorFormData from '@/views/decoration/pc/components/packages/index/formModel'
import { basicComponentList } from '@/views/decoration/pc/components/components-list'
import { VueDraggableNext } from 'vue-draggable-next'
import { cloneDeep } from 'lodash-es'
import type { ComponentItem } from '@/views/decoration/pc/components/packages/index/formModel'

defineProps({
    activePagesType: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['change'])

/**
 * 点击添加组件
 */
const showAddDialog: ((component: ComponentItem, index?: number) => void) | undefined = inject('showAddDialog')
const handleAddComponent = (currentComponent: ComponentItem) => {
    const FormData = cloneDeep(editorFormData[currentComponent.value])
    showAddDialog?.({ ...currentComponent, id: Date.now() + '', formData: FormData })
}

const dragStart = ref(false)

const activeIndex = ref(-1)
</script>

<template>
    <el-scrollbar>
        <VueDraggableNext
            :list="basicComponentList"
            :sort="false"
            class="component"
            :group="{ name: 'custom', pull: 'clone', put: false }"
            :force-fallback="true"
            :fallback-on-body="true"
            :disabled="dragStart"
            drag-class="drag"
            @start="dragStart = true"
            @end="dragStart = false"
        >
            <div
                v-for="(item, index) in basicComponentList"
                :key="index"
                class="component__item"
                :class="{ 'item-hover': activeIndex === index && !dragStart }"
                @mouseover="activeIndex = index"
                @mouseleave="activeIndex = -1"
                @click="handleAddComponent(item)"
            >
                <div class="iconfont component__item--icon icon" :class="`icon-${item.icon}`"></div>
                <div class="f12 text">{{ item.label }}</div>
            </div>
        </VueDraggableNext>
    </el-scrollbar>
</template>

<style lang="scss" scoped>
@include b(component) {
    display: grid;
    grid-template-columns: 60px 60px 60px;
    grid-template-rows: 90px 90px 90px;
    padding: 26px;
    justify-content: space-between;

    @include e(item) {
        text-align: center;
        padding: 6px;
        margin-bottom: 30px;
        cursor: pointer;
        border-radius: 4px;

        @include m(icon) {
            margin-bottom: 6px;
            font-size: 24px;
        }
    }
}

@include b(item-hover) {
    box-shadow: 0px 0px 16px 0px rgba(252, 112, 50, 0.3);
    color: #f54319;
}

@include b(drag) {
    width: 80px !important;
    height: 80px !important;
    padding: 14px;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0px 0px 16px 0px rgba(252, 112, 50, 0.3);
    color: #f54319;
}

@include b(ghost) {
    --ghost-color: #e90000;
    margin-top: 20px;
    height: 100px;
    border: 2px dashed var(--ghost-color);
    box-shadow: 0px 0px 4px 0px var(--ghost-color);

    @include b(icon) {
        margin-top: 0px;
        color: var(--ghost-color);
        font-size: 46px;
    }

    @include b(text) {
        font-size: 18px;
        color: var(--ghost-color);
    }
}
</style>
