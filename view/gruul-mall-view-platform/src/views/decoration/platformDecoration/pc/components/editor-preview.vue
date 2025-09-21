<script setup lang="ts">
import messageBox from './message-box.vue'
import renderComponents from '@/views/decoration/platformDecoration/pc/components/render-components.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { cloneDeep } from 'lodash-es'
import editorFormData from './packages/index/formModel'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'
import type { ComponentItem } from './packages/index/formModel'

const props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },

    isEdit: {
        type: Boolean,
        required: true,
    },

    highlightCurrent: {
        type: Boolean,
        required: true,
    },

    startComponents: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },

    endComponents: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },

    dragging: {
        type: Boolean,
        required: true,
    },
})
const emit = defineEmits(['change', 'add', 'del', 'update:startComponents', 'update:endComponents'])

const startComponents = useVModel(props, 'startComponents', emit)
const endComponents = useVModel(props, 'endComponents', emit)

/**
 * 添加
 */
const handleAddComponent = async (e: any) => {
    const index = e.newDraggableIndex
    const com = props.components[index]
    const FormData = editorFormData[com.value]
    const cloneFormData = cloneDeep(FormData)
    emit(
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
 */
let deli = -1
const messageBoxRef = ref<InstanceType<typeof messageBox>>()
const handleDelCurCom = (i: number) => {
    messageBoxRef.value?.open()
    delTip()
    deli = i
}

const del = () => {
    emit('del', { index: deli })
    deli = -1
}

/**
 * 监听组件设置切换
 */
function handleCurrentComponent(currentComponent: ComponentItem) {
    emit('change', currentComponent)
}

function delTip() {
    if (document.querySelector('.el-popover')) {
        document.querySelector('.el-popover')?.remove()
    }
}

/**
 * 隐藏顶部组件
 */
const hideStartComponents = (i: number) => {
    const label = startComponents.value[i].label
    hideMessage(label)
    startComponents.value[i].show = false
}

/**
 * 隐藏底部组件
 */
const hideEndComponents = (i: number) => {
    const label = endComponents.value[i].label
    hideMessage(label)
    endComponents.value[i].show = false
}

/**
 * 隐藏组件的提示信息
 */
const hideMessage = (text: string) => {
    ElMessage.success(`${text}组件已隐藏,您可以从 “基础设置--固定组件” 中重新启用。`)
}

/**
 * 监听组件设置切换
 */
</script>

<template>
    <el-scrollbar class="preview" min-height="calc(100vh - 80px)">
        <!-- 顶部不可拖动组件 -->
        <render-components
            :components="startComponents"
            :highlight-current="highlightCurrent"
            :fixed="true"
            :dragging="dragging"
            :edit="isEdit"
            @del="hideStartComponents"
            @change="handleCurrentComponent"
        />

        <!-- 中间可拖动编辑组件 -->
        <VueDraggableNext
            :list="components"
            :disabled="!isEdit"
            :sort="true"
            ghost-class="ghost"
            group="custom"
            animation="500"
            :scroll="true"
            @add="handleAddComponent"
        >
            <render-components
                :components="components"
                :highlight-current="highlightCurrent"
                :fixed="false"
                :dragging="dragging"
                :edit="isEdit"
                @del="handleDelCurCom"
                @change="handleCurrentComponent"
            />
        </VueDraggableNext>

        <!-- 底部部不可拖动组件 -->
        <render-components
            :components="endComponents"
            :highlight-current="highlightCurrent"
            :fixed="true"
            :dragging="dragging"
            :edit="isEdit"
            @del="hideEndComponents"
            @change="handleCurrentComponent"
        />

        <message-box ref="messageBoxRef" title="确认删除" message="当前内容删除后无法恢复，确认删除吗？" @confirm="del" />
        <div v-show="highlightCurrent" class="modal"></div>
    </el-scrollbar>
</template>

<style lang="scss" scoped>
@include b(preview) {
    background-color: #f6f6f6;
    box-sizing: border-box;
    div[group='custom'] {
        min-height: 80px;
    }
    :deep(.el-scrollbar__view) {
        padding-top: 50px;
    }
}

@include b(item) {
    position: relative;
    box-sizing: border-box;
    border: 1px solid transparent;

    :deep(.main) {
        width: 1200px;
        margin: 0 auto;
    }
}

@include b(modal) {
    position: absolute;
    z-index: 100;
    inset: 0;
    top: 50px;
    background-color: #000;
    opacity: 0.4;
}
</style>
