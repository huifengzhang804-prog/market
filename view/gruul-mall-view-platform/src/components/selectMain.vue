<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { ComponentsItem } from '@/views/decoration/platformDecoration/pc/components/menu/action-menu/views/custom-page/type'

const props = defineProps<{
    tableData: any[]
    select: any
    loading: boolean
    noMore: boolean
}>()

const emit = defineEmits(['update:select', 'getData'])
const select = useVModel(props, 'select', emit)

/**
 * 选中数据
 */
const change = (item: ComponentsItem) => {
    select.value = item
}

const disabled = computed(() => props.loading || props.noMore)
</script>

<template>
    <ul
        v-infinite-scroll="() => emit('getData', true)"
        class="select-main edit-scrollbar"
        :infinite-scroll-disabled="disabled"
        :infinite-scroll-distance="200"
    >
        <li
            v-for="(item, index) in tableData"
            :key="index"
            :class="{ active: select.id === item.id }"
            class="select-main__item"
            @click="change(item)"
        >
            <slot :item="item"> {{ item.name }}</slot>
        </li>
    </ul>
</template>

<style lang="scss" scoped>
@include b(select-main) {
    height: 296px;
    padding: 2px;
    display: flex;
    flex-wrap: wrap;
    grid-gap: 24px;
    align-content: flex-start;
    overflow-y: auto;
    list-style: none;

    @include e(item) {
        width: 172px;
        height: 88px;
        border-radius: 4px;
        box-shadow: 0px 2px 8px 0px #00000026;
        background: #ffffff;
        font-size: 14px;
        color: #333333;
        cursor: pointer;
        line-height: 88px;
        text-align: center;
        border: 2px solid transparent;
    }
}

@include b(active) {
    border-color: #409effb3;
    box-shadow: 0px 2px 8px 0px #409eff59;
}
</style>
