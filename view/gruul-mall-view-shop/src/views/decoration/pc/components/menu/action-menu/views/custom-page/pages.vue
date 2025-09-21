<script setup lang="ts">
import { Plus, Search } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'

const props = defineProps<{
    lineData: any[]
    name: string
    loading: boolean
    noMore: boolean
}>()

const emit = defineEmits(['add', 'del', 'edit', 'getData', 'update:name'])
const name = useVModel(props, 'name', emit)

const disabled = computed(() => props.loading || props.noMore)
</script>

<template>
    <div class="activities">
        <el-input v-model="name" placeholder="请输入页面名称" />

        <div class="activities__operate">
            <el-button :icon="Plus" type="primary" size="small" @click="emit('add')">添加</el-button>
            <el-button :icon="Search" type="primary" size="small" @click="emit('getData')">搜索</el-button>
        </div>

        <ul v-infinite-scroll="() => emit('getData', true)" :infinite-scroll-disabled="disabled" class="m-t-12 infinite-list edit-scrollbar">
            <li v-for="(item, index) in lineData" :key="index" class="activities__line">
                <div class="activities__line--name">
                    <slot name="itemName" :item="item">
                        {{ item.name }}
                    </slot>
                </div>

                <div class="activities__line--operate">
                    <el-link type="primary" :underline="false" @click="emit('edit', item)">编辑</el-link>
                    <el-link type="danger" :underline="false" class="m-l-12" @click="emit('del', item.id)">删除</el-link>
                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>
@include b(activities) {
    padding: 0 8px;

    @include e(operate) {
        display: flex;
        justify-content: space-between;
        margin-top: 10px;

        .el-button--small {
            --el-button-size: 28px;
        }
    }

    @include e(line) {
        display: flex;
        height: 37px;
        padding: 7px 10px;
        font-size: 12px;
        justify-content: space-between;
        border-bottom: 1px solid #0000000f;

        @include b(el-link) {
            font-size: 12px;
        }
    }

    @include b(el-input) {
        --el-input-height: 28px;
    }
}

@include b(infinite-list) {
    height: 268px;
    overflow: auto;
    list-style: none;
}
</style>
