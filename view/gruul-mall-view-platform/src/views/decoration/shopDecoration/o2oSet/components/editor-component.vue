<script setup lang="ts">
import EditorFormData from '../packages/components/index/formModel'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { cloneDeep } from 'lodash-es'
import { ElMessage } from 'element-plus'
import { VueDraggableNext } from 'vue-draggable-next'
import type { ComponentItem } from '../packages/components/index/formModel'
import type { PropType } from 'vue'
import { useBaseSwiper } from '@/composables/useSwiper'

const $decorationStore = useDecorationStore()
const $props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
})
const $emit = defineEmits(['change'])
const componentList = ref<ComponentItem[]>([
    {
        icon: 'lunbotu',
        value: 'swiper',
        label: '轮播图',
    },
    {
        icon: 'dianpudaohang',
        value: 'classification',
        label: '分类',
    },
])

const activePageType = computed(() => {
    return $decorationStore.activePageType
})

const { onMove, onEnd, onStart, onValidClick } = useBaseSwiper(componentList.value, $props.components)

/**
 * 点击添加组件
 * @param {*} currentComponent
 */
const handleAddComponent = (currentComponent: ComponentItem) => {
    if (activePageType.value !== 'customize') {
        ElMessage.warning('该页面无法添加组件')
        return
    } else if (onValidClick(currentComponent)) {
        const FormData = cloneDeep(EditorFormData[currentComponent.value])
        $emit('change', { ...currentComponent, id: Date.now(), formData: FormData })
    }
}
</script>

<template>
    <div class="editor__component editor__component_new">
        <div class="editor_component_wrap">
            <div style="height: 5%"></div>
            <el-scrollbar style="height: 85%">
                <div class="editor_component_wrap_main">
                    <VueDraggableNext
                        :list="componentList"
                        class="component_box"
                        :group="{ name: 'custom', pull: 'clone', put: false }"
                        item-key="label"
                        :sort="false"
                        :move="onMove"
                        @start="onStart"
                        @end="onEnd"
                    >
                        <div v-for="item in componentList" :key="item.label" style="width: 93px" @click="handleAddComponent(item)">
                            <div class="component--item">
                                <div class="iconfont component--item--icon" :class="`icon-${item.icon}`"></div>
                                <div>{{ item.label }}</div>
                            </div>
                        </div>
                    </VueDraggableNext>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
.component--item--icon {
    font-size: 28px;
    text-align: center;
    margin-bottom: 19px;
}
.editor__component_new .is-horizontal {
    display: none;
    overflow-x: hidden;
}
</style>
