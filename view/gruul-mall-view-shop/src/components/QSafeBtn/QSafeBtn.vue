<script setup lang="ts">
import { h, useSlots, computed, onBeforeUnmount } from 'vue'
import { ElButton } from 'element-plus'
import { debounce } from 'lodash-es'
import type { ButtonProps } from 'element-plus'

defineOptions({
    name: 'QSafeBtn',
})

// 定义props，继承Element Plus Button的props
interface Props extends Partial<ButtonProps> {
    delay?: number
    leading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    delay: 1000,
    leading: true, // 首次点击立即触发
})

// 定义事件
const emit = defineEmits(['click'])

// 获取插槽
const slots = useSlots()

// 创建防抖函数
const debouncedClickHandler = debounce(
    (e: MouseEvent) => {
        emit('click', e)
    },
    props.delay,
    { leading: props.leading },
)

// 在组件卸载前取消防抖函数中的等待调用
onBeforeUnmount(() => {
    debouncedClickHandler.cancel()
})

// 使用render函数生成内容
const renderButton = computed(() =>
    h(
        ElButton,
        {
            ...props,
            onClick: debouncedClickHandler,
        },
        slots,
    ),
)
</script>

<template>
    <component :is="renderButton" />
</template>
