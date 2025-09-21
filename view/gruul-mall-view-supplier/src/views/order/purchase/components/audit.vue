<template>
    <el-form :model="formModel">
        <el-form-item label="应付款(元)">
            <span class="currency">￥</span>
            <span class="price">{{ $props.orderInfo.totalPrice }}</span>
        </el-form-item>
        <el-form-item label="支付方式">线下付款</el-form-item>
        <el-form-item label="付款凭证">
            <div class="payof">
                <el-image style="width: 200px; height: 200px" fit="cover" :src="$props.orderInfo.payof" />
                <div class="payof__tip">请确认是否收到线下结算的款项！！！</div>
            </div>
        </el-form-item>
        <el-form-item label="审核">
            <el-radio-group v-model="formModel.success">
                <el-radio :label="true">通过：已收到线下结算款项</el-radio>
                <el-radio :label="false">拒绝：未收到线下结算款项</el-radio>
            </el-radio-group>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
type IProps = {
    orderInfo: {
        payof: string
        success: boolean
        orderNo: string
        totalPrice: string
    }
}
const $props = withDefaults(defineProps<IProps>(), {
    orderInfo: () => ({
        payof: '',
        orderNo: '',
        success: true,
        totalPrice: '',
    }),
})
const $emit = defineEmits(['update:orderInfo'])
const formModel = computed({
    get() {
        return $props.orderInfo
    },
    set(newVal) {
        $emit('update:orderInfo', newVal)
    },
})
</script>

<style lang="scss" scoped>
@include b(payof) {
    img {
        width: 200px;
        height: 200px;
    }
}
</style>
