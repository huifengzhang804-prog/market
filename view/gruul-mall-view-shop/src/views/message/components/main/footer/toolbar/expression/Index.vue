<template>
    <div>
        <el-popover placement="top-start" width="200px" trigger="hover">
            <div style="width: 100%">
                <el-scrollbar height="130px">
                    <el-row>
                        <el-col v-for="emoji in emojis" :key="emoji" :span="4" @click="expressionSelect(emoji)">{{ emoji }}</el-col>
                    </el-row>
                </el-scrollbar>
            </div>
            <template #reference>
                <QIcon name="icon-biaoqing" size="24px"></QIcon>
            </template>
        </el-popover>
    </div>
</template>

<script setup lang="ts">
import { emojis } from './expression'
const $emit = defineEmits(['expressionSelect'])

const expressionSelect = (expression: string) => {
    $emit('expressionSelect', expression)
}
</script>

<style scoped lang="scss">
.el-col {
    text-align: center;
    font-size: 20px;
}
.el-col:hover {
    background-color: $rows-bg-color-hover;
}
</style>
