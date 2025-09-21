<template>
    <div class="line">
        <span v-for="item in lineArr" :key="item" :class="{ activeStyle: active === item }" class="spanItem" @click="activeClick(item)">
            {{ item }}
        </span>
        <div class="operate">
            <slot>
                <el-button v-if="operateText" type="primary" @click="emit('operateClick')">{{ operateText }}</el-button>
            </slot>
        </div>
    </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
const props = defineProps({
    lineArr: {
        type: Array as PropType<string[]>,
        default: () => [],
    },
    defaultActive: {
        type: String,
        default: '',
    },
    operateText: {
        type: String,
        default: '',
    },
})
const active = ref(props.defaultActive)
const emit = defineEmits(['tabClick', 'operateClick'])
const activeClick = (item: string) => {
    active.value = item
    emit('tabClick', item)
}
</script>

<style scoped lang="scss">
.line {
    background-color: #f8f8f8;
    margin-top: 10px;
    display: flex;
    position: relative;
    .operate {
        position: absolute;
        right: 20px;
        top: 5px;
    }
}
.spanItem {
    border-bottom: 2px solid transparent;
    padding: 12px 2px 8px;
    margin-left: 18px;
    cursor: pointer;
}

.activeStyle {
    border-bottom-color: #1890ff;
    color: #1890ff;
}
</style>
