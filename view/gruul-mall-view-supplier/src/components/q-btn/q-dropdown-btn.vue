<script setup lang="ts">
import type { PropType } from 'vue'

export type CommondType = Record<'name' | 'label', string>

const $emit = defineEmits(['rightClick', 'leftClick'])
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
    size: { type: String as PropType<'large ' | 'default' | 'small'>, default: 'large' },
    type: { type: String as PropType<'success' | 'warning' | 'info' | 'primary' | 'danger'>, default: 'primary' },
})
</script>

<template>
    <el-button-group>
        <el-link :type="type" round @click="$emit('leftClick')">{{ $props.title }}</el-link>
        <slot name="link"></slot>
        <el-link v-if="$props.option?.length" :type="type" round style="margin-left: 12px">
            <el-dropdown @command="$emit('rightClick', $event)">
                <span class="el-dropdown-link">更多</span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item
                            v-for="(item, index) in $props.option"
                            :key="index"
                            :command="item.name"
                            style="width: 110px; line-height: 37px"
                            >{{ item.label }}</el-dropdown-item
                        >
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </el-link>
    </el-button-group>
</template>

<style scoped lang="scss">
.el-dropdown-link {
    color: #555cfd;
    outline: unset;
    position: relative;
}
</style>
