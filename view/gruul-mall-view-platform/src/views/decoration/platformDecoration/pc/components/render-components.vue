<script setup lang="ts">
import preview from './packages/index/preview'
import type { PropType } from 'vue'
import type { ComponentItem } from './packages/index/formModel'
const props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
    highlightCurrent: {
        type: Boolean,
        required: true,
    },
    fixed: {
        type: Boolean,
        required: true,
    },
    edit: {
        type: Boolean,
        required: true,
    },
    dragging: {
        type: Boolean,
        required: true,
    },
})
const emit = defineEmits(['change', 'del'])
const actionFlag = ref(-1)

/**
 * 监听组件设置切换
 */
const highlightIndex = ref(-1)
function handleCurrentComponent(currentComponent: ComponentItem, index: number) {
    highlightIndex.value = index
    emit('change', currentComponent)
}

/**
 * 监听高亮发生变化，回归默认值
 */
watch(
    () => props.highlightCurrent,
    () => {
        if (!props.highlightCurrent) highlightIndex.value = -1
    },
)
</script>

<template>
    <div v-for="(item, i) of components" v-show="item.show" :key="item.id" @mouseover="actionFlag = i" @mouseleave="actionFlag = -1">
        <!-- select__item  样式：左右边线。 激活场景：1.在没有触发某一个组件高亮的样式 2.整个页面在编辑的状态中 -->
        <!-- select__active  样式：组件高。 激活场景：1.当前有正在编辑的组件 2.编辑的组件是这个组件 3.整个页面在编辑的状态中 -->
        <div
            v-if="item.formData"
            class="item"
            :class="{
                select__item: !highlightCurrent && edit,
                select__active: highlightCurrent && highlightIndex === i && edit,
            }"
            :style="{ marginTop: item.space + 'px', borderColor: item.borderColor }"
        >
            <!-- 编辑和删除按钮 激活场景：1.鼠标划入当前的这个元素 2.当前处于编辑状态 3.不在拖拽侧边栏的状态中  -->
            <div v-show="actionFlag === i && edit && !dragging" class="item__tan" :style="{ top: item.top ? '-46px' : '5px' }">
                <div class="item__tan--text" @click="handleCurrentComponent(item, i)">编辑</div>

                <div class="item__tan--icon" @click="emit('del', i)">
                    <q-icon v-if="fixed" name="icon-yanjing_yincang" size="20px" />
                    <el-icon v-else class="item__tan--icon"><i-ep-delete /></el-icon>
                </div>
            </div>

            <!-- 上下边线。 激活场景：1.在没有触发某一个组件高亮的样式 2.整个页面在编辑的状态中 -->
            <div v-show="!highlightCurrent && edit" class="top edit-line"></div>
            <div v-show="!highlightCurrent && edit" class="bottom edit-line"></div>

            <component :is="preview[item.value]" v-model:form-data="item.formData" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(item) {
    position: relative;
    box-sizing: border-box;

    @include e(tan) {
        position: absolute;
        right: 193px;
        z-index: 999;
        width: 184px;
        height: 46px;
        background-color: #fff;
        border-radius: 9px;
        box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.08);
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #999;

        @include m(icon) {
            text-align: center;
            flex: 1;
            cursor: pointer;
            &:hover {
                color: #e90000;
            }
        }

        @include m(text) {
            width: calc(50% + 1px);
            text-align: center;
            cursor: pointer;
            border-right: 1px solid #e7e6e6;
            &:hover {
                color: #f54319;
            }
        }
    }

    :deep(.main) {
        width: 1200px;
        margin: 0 auto;
    }
}

@include b(edit-line) {
    position: absolute;
    width: 100%;
    border-bottom: 1px dashed #f54319;
    z-index: 1;
}

@include b(top) {
    top: 1px;
}

@include b(bottom) {
    bottom: 0px;
}

@include b(select__active) {
    z-index: 200;
}

@include b(select__item) {
    border-left: 1px dashed #f54319 !important;
    border-right: 1px dashed #f54319 !important;
}
</style>
