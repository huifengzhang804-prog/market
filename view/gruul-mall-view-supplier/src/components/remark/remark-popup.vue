<script setup lang="ts">
import { ref, PropType, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { doPutOrderRemark, doPutAfsOrderRemark } from '@/apis/remark'
enum RemarkType {
    'AFS' = '售后订单',
    'GOODS' = '商品订单',
}

const $props = defineProps({
    isShow: {
        type: Boolean,
        default() {
            return false
        },
    },
    ids: {
        type: Array as PropType<string[]>,
        default() {
            return []
        },
    },
    remark: {
        type: String,
        default: '',
    },
    remarkType: {
        type: String as PropType<keyof typeof RemarkType>,
        required: true,
    },
})
const $emit = defineEmits(['update:isShow', 'update:ids', 'success', 'update:remark'])
const _isShow = useVModel($props, 'isShow', $emit)
const remarkRef = ref('')

watch(
    () => $props.remark,
    (val) => {
        remarkRef.value = val
    },
)

/**
 * @description: 备注取消事件
 * @returns {*}
 */
const handleRemarkCancel = async () => {
    _isShow.value = false
}
/**
 * @description: 确定
 * @returns {*}
 */
const handleRemarkEnter = async () => {
    if (!remarkRef.value) return ElMessage.info('请输入备注')
    let result: { code: number; data: any; msg?: string }
    switch ($props.remarkType) {
        case 'GOODS':
            result = await doPutOrderRemark($props.ids, remarkRef.value)
            break
        case 'AFS':
            result = await doPutAfsOrderRemark($props.ids, remarkRef.value)
            break
        default:
            result = { code: 11, data: '' }
            break
    }
    if (result.code === 200) {
        ElMessage.success('备注成功')
        remarkRef.value = ''
        $emit('update:remark', '')
        $emit('success')
        _isShow.value = false
    } else {
        ElMessage.error('备注失败')
    }
}
const handleClose = () => {
    $emit('update:remark', '')
    $emit('update:ids', [])
    remarkRef.value = ''
    _isShow.value = false
}

// _isShow = false
</script>

<template>
    <el-dialog v-model="_isShow" width="40%" @close="handleClose">
        <template #header>
            <h3>备注</h3>
        </template>
        <div class="title">您已选择{{ $props.ids.length }}笔订单</div>
        <el-input v-model.trim="remarkRef" :rows="6" type="textarea" placeholder="请输入备注" maxlength="150" />
        <template #footer>
            <el-button @click="handleRemarkCancel">取消</el-button>
            <el-button type="primary" @click="handleRemarkEnter">确认</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(title) {
    font-size: 14px;
    color: #333333;
    padding: 0 0 14px 0;
}
</style>
