<template>
    <component :is="'div'" ref="componentRef">
        <slot :key="componentKey"></slot>
    </component>
</template>

<script lang="ts" setup>
import Sortablejs from 'sortablejs'
import useTransformData from './useTransformData'
import { cloneDeep } from 'lodash-es'

const $props = withDefaults(defineProps<{ pidKey?: string; limitPid: boolean; disabled?: boolean }>(), {
    pidKey: 'parentId',
    limitPid: false,
    disabled: false,
})
const componentKey = ref(0)
const { parseData, toTreeData, buildRelationData } = useTransformData()
const slot = useSlots()
const componentRef = ref<HTMLDivElement | null>(null)
const $emit = defineEmits(['changeData'])
const initialTable = () => {
    let startParseData: any[] = [] // 起始一级的全部列表
    const el = componentRef.value?.children
    const tableDom = el?.[0]
    console.log(slot.default?.())
    const sortableEl = tableDom?.querySelector('.el-table__body tbody') as HTMLElement
    if (sortableEl && $props.disabled === false) {
        const sortable = Sortablejs.create(sortableEl, {
            handle: '.el-table__row',
            onStart() {
                // 打散全部的数据
                const tableData = slot?.default?.()?.[0]?.props?.data
                startParseData = $props.limitPid ? parseData(tableData) : tableData
            },
            onMove(evt) {
                const { draggedData, relatedData } = buildRelationData(evt, startParseData)
                console.log(draggedData[$props.pidKey] === relatedData[$props.pidKey])
                if ($props.limitPid) return draggedData[$props.pidKey] === relatedData[$props.pidKey]
                return true
            },
            onEnd(e) {
                const finishData = cloneDeep(startParseData)
                if (typeof e.oldIndex !== 'undefined' && typeof e.newIndex !== 'undefined') {
                    const deleteData = finishData.splice(e.oldIndex, 1)
                    finishData.splice(e.newIndex, 0, deleteData?.[0])
                    startParseData = finishData
                }
                const changedData = $props.limitPid ? toTreeData(finishData) : finishData
                console.log(changedData)
                $emit('changeData', changedData)
                componentKey.value = Date.now()
                sortable.destroy()
                nextTick(() => initialTable())
            },
        })
    }
}
onMounted(() => {
    nextTick(() => initialTable())
})
</script>
