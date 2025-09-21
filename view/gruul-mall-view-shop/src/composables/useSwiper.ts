import { ElMessage } from 'element-plus'
import type { ComponentItem as _ComponentItem } from '@/views/decoration/on-line-set/packages/components/index/formModel'
import type { ComponentItem as _ComponentItem1 } from '@/views/decoration/o2o-set/packages/components/index/formModel'
import { ref } from 'vue'

// Constants
export const ONLY_ONE_COMPONENTS = ['swiper', 'compose']
const ONLY_TOP_COMPONENTS = ['swiper', 'compose']

type ComponentItem = _ComponentItem | _ComponentItem1

interface DragState {
    error: string
    currentValue?: string
    isBanner?: boolean
}

interface DragMoveEvent {
    draggedContext: {
        element: ComponentItem
        futureIndex: number
    }
}

/**
 * 基本组件的swiper
 * @param baseCompList - List of base components
 * @param previewComList - List of preview components
 */
export function useBaseSwiper(baseCompList: ComponentItem[], previewComList: ComponentItem[]) {
    const state = ref<DragState>({
        error: '',
        currentValue: '',
        isBanner: false,
    })

    // Helper function to check if a value is an only-one component
    const isOnlyOneComponent = (value: string) => ONLY_ONE_COMPONENTS.includes(value)

    // 开始拖拽
    const onStart = (e: { oldDraggableIndex: number }) => {
        const element = baseCompList[e.oldDraggableIndex]
        state.value.currentValue = element.value

        if (!isOnlyOneComponent(element.value)) {
            state.value.isBanner = false
            return
        }

        // 验证是否存在轮播图组件
        state.value.isBanner = true
        const hasRestrictedComponent = previewComList.some((item) => isOnlyOneComponent(item.value))
        if (hasRestrictedComponent) {
            state.value.error = '已存在轮播图组件'
        }
    }

    // 拖拽组件
    const onMove = ({ draggedContext: { futureIndex } }: DragMoveEvent) => {
        if (!previewComList.length) return true

        // 验证第一个要是轮播图不能添加在头部
        if (!state.value.isBanner) {
            return isOnlyOneComponent(previewComList[0]?.value) ? futureIndex !== 0 : true
        }

        return !state.value.error && futureIndex === 0
    }

    // 结束拖拽
    const onEnd = (e: { newDraggableIndex: number }) => {
        console.log(e)
        // 验证轮播组件是否放在第一个
        if (state.value.isBanner && !state.value.error && e.newDraggableIndex !== 0) {
            state.value.error = '该组件仅放在页面顶部'
        }

        if (state.value.error) {
            ElMessage.error(state.value.error)
            state.value.error = ''
        }
    }

    // 验证点击事件
    const onValidClick = (component: ComponentItem) => {
        // 验证如果预览组件列表中是否存在轮播组件
        // if (isOnlyOneComponent(component.value) && previewComList.some((item) => isOnlyOneComponent(item.value))) {
        //     ElMessage.error('已存在轮播图组件')
        //     return false
        // }

        return true
    }

    return { onMove, onEnd, onStart, onValidClick }
}

/**
 * 预览的swiper
 * @param previewComList
 */
export function usePreviewSwiper(previewComList: ComponentItem[]) {
    const state = ref<DragState>({ error: '' })
    const isTopComponent = (value?: string) => value && ONLY_TOP_COMPONENTS.includes(value)

    // 移动组件
    const onMove = ({ draggedContext: { element, futureIndex } }: DragMoveEvent) => {
        // 如果第一个元素是轮播图则不能移动
        if (isTopComponent(element.value)) {
            state.value.error = '顶部组件仅为轮播组件'
            return false
        }

        // 如果第一个是轮播组件并且要将其他组件拖到第一个
        const firstComponent = previewComList?.[0]
        if (firstComponent && isTopComponent(firstComponent.value) && futureIndex === 0) {
            state.value.error = '顶部组件仅为轮播组件'
            return false
        }

        return true
    }

    // 结束拖拽
    const onEnd = () => {
        if (state.value.error) {
            ElMessage.error(state.value.error)
            state.value.error = ''
        }
    }

    return { onMove, onEnd }
}
