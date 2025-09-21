<script setup lang="ts">
import type { PropType } from 'vue'

export type CommondType = Record<'name' | 'label', string>

const $props = defineProps({
    title: {
        type: String,
        default: '审核',
    },
    option: {
        type: Array as PropType<CommondType[]>,
        default() {
            return [{ name: 'mishandle', label: '暂无操作' }]
        },
    },
})
const $emit = defineEmits(['leftClick', 'rightClick'])
</script>

<template>
    <div class="mybtn">
        <div class="mybtn__left" @click="$emit('leftClick')">{{ $props.title }}</div>
        |
        <el-dropdown class="mybtn__right" trigger="click" @command="$emit('rightClick', $event)">
            <span class="mybtn__right--span">...</span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item v-for="item in $props.option" :key="item.label" :command="item.name">{{ item.name }}</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
</template>

<style scoped lang="scss">
@include b(mybtn) {
    width: 82px;
    height: 36px;
    font-size: 12px;
    color: #309af3;
    background: #eaf5fe;
    border-radius: 50%;
    line-height: 36px;
    text-align: center;
    @include flex;
    @include e(left) {
        width: 50px;
        height: 36px;
        background: #eaf5fe;
        border-radius: 50px 0 0 50px;
        &:hover {
            cursor: pointer;
            color: #fff;
            background: #2e99f3;
        }
        &:active {
            color: #fff;
            background: #79bbff;
        }
    }
    @include e(right) {
        width: 40px;
        height: 36px;
        background: #eaf5fe;
        border-radius: 0 50px 50px 0;
        &:hover {
            color: #fff;
            cursor: pointer;
            background: #2e99f3;
        }
        &:active {
            color: #fff;
            background: #79bbff;
        }
        @include m(span) {
            width: 30px;
            line-height: 30px;
            text-align: center;
        }
    }
}
</style>
