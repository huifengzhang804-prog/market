<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import seckill from './seckill'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof seckill>,
        default: seckill,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)
</script>

<template>
    <el-form>
        <el-form-item label="活动名称">
            <el-input v-model="formData.name" placeholder="限时秒杀" show-word-limit :maxlength="4" />
            <div class="tip">系统自动将【进行中、预热中】的活动获取过来</div>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped>
@include b(tip) {
    color: #999999;
    font-size: 12px;
}
</style>
