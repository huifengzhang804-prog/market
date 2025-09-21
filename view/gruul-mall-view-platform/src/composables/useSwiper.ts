import { ElMessage } from 'element-plus'
import type { ComponentItem as _ComponentItem } from '@views/decoration/platformDecoration/mobile/packages/components/index/formModel'
import type { ComponentItem as _ComponentItem1 } from '@views/decoration/shopDecoration/o2oSet/packages/components/index/formModel'
import type { ComponentItem as _ComponentItem2 } from '@views/decoration/shopDecoration/onLineSet/packages/components/index/formModel'

import { ref } from 'vue'

// Constants
export const ONLY_ONE_COMPONENTS = ['swiper', 'compose']
const ONLY_TOP_COMPONENTS = ['swiper', 'compose']
const OVER_TOP_COMPONENTS = ['blankPaceholder']

type ComponentItem = _ComponentItem | _ComponentItem1 | _ComponentItem2

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
    const isTopComponent = (value?: string) => value && ONLY_TOP_COMPONENTS.includes(value)
    const isOverTopComponent = (value?: string) => value && OVER_TOP_COMPONENTS.includes(value)

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

        // 获取当前拖动的组件类型
        const currentValue = state.value.currentValue
        if (!currentValue) return true

        // 收集占位组件和轮播/组合组件的位置信息
        const overTopComponents = [] // 占位组件位置
        const topComponents = [] // 轮播/组合组件位置

        for (let i = 0; i < previewComList.length; i++) {
            const component = previewComList[i]
            if (isOverTopComponent(component.value)) {
                overTopComponents.push(i)
            } else if (isTopComponent(component.value)) {
                topComponents.push(i)
            }
        }

        // 1. 如果当前拖拽的是占位组件 (OVER_TOP_COMPONENTS)，可以拖到任何位置
        if (isOverTopComponent(currentValue)) {
            return true
        }

        // 2. 如果当前拖拽的是轮播/组合组件 (ONLY_TOP_COMPONENTS)
        if (isTopComponent(currentValue)) {
            // 2.1 检查是否已有同类组件
            if (state.value.error) {
                return false
            }

            // 2.2 轮播/组合组件只能放在所有占位组件之后的第一个位置
            // 考虑到是新增模式，需要判断futureIndex是否是占位组件之后的第一个位置

            if (overTopComponents.length > 0) {
                // 找出最后一个占位组件的位置
                const lastOverTopPos = overTopComponents[overTopComponents.length - 1]
                // 轮播组件应该放在最后一个占位组件之后
                if (futureIndex !== lastOverTopPos + 1) {
                    state.value.error = '轮播以及组合组件仅能在占位组件之下'
                    return false
                }
            } else if (futureIndex !== 0) {
                // 如果没有占位组件，轮播组件只能放在最前面
                state.value.error = '轮播以及组合组件仅能放在首位'
                return false
            }

            return true
        }

        // 3. 如果当前拖拽的是普通组件

        // 3.1 普通组件不能放在轮播/组合组件之前
        if (topComponents.length > 0) {
            // 找出第一个轮播/组合组件的位置
            const firstTopPos = topComponents[0]

            if (futureIndex <= firstTopPos) {
                state.value.error = '普通组件不能放在轮播或组合组件之前'
                return false
            }
        }
        // 清除错误信息
        state.value.error = ''
        return true
    }

    // 结束拖拽
    const onEnd = (e: { newDraggableIndex: number }) => {
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
    const isOverTopComponent = (value?: string) => value && OVER_TOP_COMPONENTS.includes(value)

    // 移动组件
    const onMove = ({ draggedContext: { element, futureIndex } }: DragMoveEvent) => {
        const dragElementValue = element.value

        // 1. 查找所有 OVER_TOP_COMPONENTS 组件的位置
        const overTopPositions = previewComList.map((item, index) => (isOverTopComponent(item.value) ? index : -1)).filter((index) => index !== -1)

        // 2. 计算 OVER_TOP_COMPONENTS 组件之后的第一个位置
        const firstPositionAfterOverTop = overTopPositions.length > 0 ? overTopPositions[overTopPositions.length - 1] + 1 : 0

        // 3. 处理不同类型组件的拖动规则

        // 3.1 如果拖动的是 OVER_TOP_COMPONENTS，可以拖到任何位置
        if (isOverTopComponent(dragElementValue)) {
            return true
        }

        // 3.2 如果拖动的是 ONLY_TOP_COMPONENTS，必须放在 OVER_TOP_COMPONENTS 之后
        if (isTopComponent(dragElementValue)) {
            // 检查目标位置是否在所有 OVER_TOP_COMPONENTS 之后的第一个位置
            if (futureIndex !== firstPositionAfterOverTop) {
                state.value.error = '轮播以及组合组件仅能在占位组件之下'
                return false
            }
            return true
        }

        // 3.3 对于其他普通组件，不能放在 ONLY_TOP_COMPONENTS 之前
        // 找出所有顶部组件的位置
        const topComponentPositions = previewComList.map((item, index) => (isTopComponent(item.value) ? index : -1)).filter((index) => index !== -1)

        // 如果存在顶部组件，普通组件必须放在所有顶部组件之后
        if (topComponentPositions.length > 0) {
            const positionAfterTopComponents = topComponentPositions[topComponentPositions.length - 1] + 1

            if (futureIndex < positionAfterTopComponents) {
                state.value.error = '普通组件不能放在轮播或组合组件之前'
                return false
            }
        }
        // 清除错误信息
        state.value.error = ''
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
