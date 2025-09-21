<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { doPutOrderRemark, doPutAfsOrderRemark, doPutWithdrawRemark, doPostpaymentHistoryRemark, doPostIntegralOrderRemark } from '@/apis/remark'

enum RemarkType {
    'AFS' = '售后订单',
    'GOODS' = '商品订单',
    'WITHDRAW' = '提现工单',
    'PAYMENT' = '储值工单',
    'INTEGRAL' = '积分订单',
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
const locRemark = ref('')

watch(
    () => $props.remark,
    (val) => {
        locRemark.value = val
    },
)

/**
 * 备注取消事件
 */
const handleRemarkCancel = async () => {
    try {
        await ElMessageBox.confirm('取消后备注内容不被保留，是否确定?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        $emit('update:remark', '')
        $emit('update:ids', [])
        locRemark.value = ''
    } catch (error) {
        return
    }
    _isShow.value = false
}
/**
 * 确定
 */
const handleRemarkEnter = async () => {
    if (!locRemark.value || locRemark.value === '') return ElMessage.info('请输入备注')
    let result: { code: number; data: any; msg?: string }
    switch ($props.remarkType) {
        case 'GOODS':
            result = await doPutOrderRemark($props.ids, locRemark.value)
            break
        case 'AFS':
            result = await doPutAfsOrderRemark($props.ids, locRemark.value)
            break
        case 'WITHDRAW':
            result = await doPutWithdrawRemark($props.ids, locRemark.value)
            break
        case 'PAYMENT':
            result = await doPostpaymentHistoryRemark($props.ids, locRemark.value)
            break
        case 'INTEGRAL':
            result = await doPostIntegralOrderRemark($props.ids, locRemark.value)
            break
        default:
            // 默认订单备注
            result = await doPutOrderRemark($props.ids, locRemark.value)
            break
    }
    if (result.code === 200) {
        ElMessage.success('备注成功')
        locRemark.value = ''
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
    locRemark.value = ''
    _isShow.value = false
}
</script>

<template>
    <el-dialog v-model="_isShow" width="40%" @close="handleClose">
        <template #header>
            <h3>备注</h3>
        </template>
        <div class="title">您已选择{{ $props.ids.length }}笔订单</div>
        <el-input v-model.trim="locRemark" :rows="6" type="textarea" placeholder="备注字数150个字以内" maxlength="150" />
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
