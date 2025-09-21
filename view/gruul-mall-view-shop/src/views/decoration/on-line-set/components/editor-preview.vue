<script setup lang="ts">
import type { PropType } from 'vue'
import { useDecorationStore } from '@/store/modules/decoration'
import preview from '../packages/components/index/preview'
import editorFormData from '../packages/components/index/formModel'
import type { ComponentItem } from '../packages/components/index/formModel'
import { VueDraggableNext } from 'vue-draggable-next'
import { cloneDeep } from 'lodash-es'
import { storeToRefs } from 'pinia'
import { usePreviewSwiper } from '@/composables/useSwiper'

const $props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
    isPreview: {
        type: Boolean,
        default() {
            return false
        },
    },
})
const $emit = defineEmits(['change', 'add', 'del'])
const $decorationStore = useDecorationStore()
const { activePageType } = storeToRefs(useDecorationStore())
const delFlag = ref(-1)
// 拖动的组件的下角标
const dragStarIndex = ref(-1)
// 选中的当前项
const curreentFlag = ref(-1)

const myScrollbar = ref()

const handleAddComponent = (e: any) => {
    const index = e.newDraggableIndex
    const com = $props.components[index]
    const FormData = editorFormData[com.value]
    const cloneFormData = cloneDeep(FormData)
    $emit(
        'add',
        {
            ...com,
            id: Date.now(),
            formData: cloneFormData,
        },
        index,
    )
}
/**
 * 删除当前组件
 * @param {number} i
 */
const handleDelCurCom = (i: number) => {
    delTip()
    $emit('del', { index: i })
}
/**
 * 父级调用设置当前选中的组件
 */
const setCurrentFlag = (i: number) => {
    curreentFlag.value = i
}
/**
 * 监听组件设置切换
 * @param {ComponentItem} currentComponent 当前组件信息
 * @param {number} i
 */
function handleCurrentComponent(currentComponent: ComponentItem, i: number) {
    if ($props.isPreview) return
    curreentFlag.value = i
    $decorationStore.SET_ACTIVE_COMINDEX(i)
    $emit('change', currentComponent)
}
function onWrapSroll() {
    const scrollbarEl = myScrollbar.value.wrap$
    scrollbarEl.onscroll = function () {
        if (document.querySelector('.el-popover')) {
            document.querySelector('.el-popover')?.remove()
        }
    }
}
function delTip() {
    if (document.querySelector('.el-popover')) {
        document.querySelector('.el-popover')?.remove()
    }
}

const { onMove, onEnd } = usePreviewSwiper($props.components)

defineExpose({
    setCurrentFlag,
})
</script>

<template>
    <div class="editor__preview">
        <el-scrollbar ref="myScrollbar" style="height: 100%" :noresize="true">
            <!-- 添加此节点height:calc(100vh - 150px)防止空状态无法拖入 -->
            <VueDraggableNext
                :list="$props.components"
                :sort="true"
                :group="activePageType === 'customize' ? 'custom' : ''"
                animation="500"
                :scroll="true"
                :disabled="$props.isPreview"
                style="height: calc(100vh - 150px)"
                :move="onMove"
                @add="handleAddComponent"
                @end="onEnd"
            >
                <transition-group>
                    <div
                        v-for="(component, i) of $props.components"
                        :id="`editor-preview-com-${i}`"
                        :key="i + 1"
                        :class="[
                            'component--item',
                            component.value !== undefined && delFlag === i ? 'iscur__component--item' : '',
                            curreentFlag === i ? 'select__component--item' : '',
                        ]"
                        :style="$props.isPreview ? 'border:none' : ''"
                        style="width: 375px"
                        @click="handleCurrentComponent(component, i)"
                        @mouseover="delFlag = i"
                        @mouseleave="delFlag = -1"
                    >
                        <div
                            v-show="activePageType !== 'classification' && activePageType !== 'control' && delFlag === i && !$props.isPreview"
                            class="component--item__tan"
                        >
                            <div class="component--item__text" @click="handleCurrentComponent(component, i)">编辑</div>
                            <el-icon class="component--item__icon" @click="handleDelCurCom(i)"><i-ep-delete /></el-icon>
                        </div>
                        <component :is="preview[component.value]" :form-data="component.formData"></component>
                    </div>
                </transition-group>
            </VueDraggableNext>
        </el-scrollbar>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
</style>
