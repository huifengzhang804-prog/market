<script setup lang="ts">
import type { PropType } from 'vue'

export type CommondType = Record<'name' | 'label', string>

const dropdown = ref()
const $emit = defineEmits(['rightClick', 'leftClick'])
const dropdownVisible = ref(false)
const $props = defineProps({
    title: {
        type: String,
        default: '审核',
    },
    option: {
        type: Array as PropType<CommondType[]>,
        default() {
            return [{ label: '暂无操作', name: 'default' }]
        },
    },
    bgColor: { type: String, default: '#eaf5fe' },
    color: { type: String, default: '#fff' },
    size: { type: String as PropType<'large' | 'default' | 'small'>, default: 'large' },
    leftDisabled: {
        type: Boolean,
        default: false,
    },
    slotIndex: {
        type: Number,
        default: -1,
    },
})

const computedSlot = computed(() => ($props.slotIndex === -1 ? $props.option.length : $props.slotIndex))

const handleDropdownChange = () => {
    if (dropdownVisible.value) {
        dropdown.value.handleClose()
    } else {
        dropdown.value.handleOpen()
    }
}
</script>

<template>
    <el-button-group :size="$props.size" style="display: flex">
        <span class="add-element" @click="$emit('leftClick')">{{ $props.title }} </span>
        <el-dropdown
            ref="dropdown"
            class="mybtn__right"
            trigger="hover"
            @command="$emit('rightClick', $event)"
            @visible-change="dropdownVisible = $event"
        >
            <span class="add-element" @click="handleDropdownChange"> 更多</span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item v-for="item in $props.option.slice(0, computedSlot)" :key="item.label" :command="item.name"
                        >{{ item.label }}
                    </el-dropdown-item>
                    <slot />
                    <el-dropdown-item v-for="item in $props.option.slice(computedSlot, $props.option.length)" :key="item.label" :command="item.name"
                        >{{ item.label }}
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </el-button-group>
</template>

<style scoped lang="scss">
@include b(add-element) {
    color: #555cfd;
    cursor: pointer;
    line-height: 20px;
    font-size: 14px;
}
@include b(mybtn__right) {
    margin-left: 20px;
}
</style>
