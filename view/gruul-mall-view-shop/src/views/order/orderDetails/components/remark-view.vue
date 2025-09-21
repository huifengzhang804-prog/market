<script lang="ts" setup>
import type { PropType } from 'vue'
import type { RemarkItem } from '@apis/order/types'
import { FormType } from '@apis/mall/settings/types'

defineProps({
    remark: { type: Object as PropType<RemarkItem[]>, required: true },
})
</script>

<template>
    <el-descriptions v-show="remark.length > 0" :column="1" size="small">
        <el-descriptions-item v-for="item in remark" :key="item.key" :label="item.key">
            <el-image
                v-if="item.type === FormType.IMAGE"
                :preview-src-list="[item.value]"
                :src="item.value"
                fit="cover"
                style="width: 40px; height: 40px"
            />
            <span v-else> {{ item.value }}</span>
        </el-descriptions-item>
    </el-descriptions>
</template>

<style lang="scss" scoped>
:deep(.el-descriptions__body) {
    background-color: transparent;
}
</style>
