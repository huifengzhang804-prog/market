<script setup lang="ts">
const prop = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
})
const emit = defineEmits(['showClose', 'update:show'])
const propShow = computed({
    get() {
        return prop.show
    },
    set(value: boolean) {
        emit('update:show', value)
    },
})
const closeFn = () => {
    emit('showClose', !prop.show)
}
</script>

<template>
    <div>
        <el-dialog v-model="propShow" :close-on-click-modal="false" :before-close="closeFn">
            <h3 style="text-align: center">操作指引</h3>
            <p>当您看到该操作指引时说明您的微信特约商户的审核已经通过，要完成正常的微信分账还需要您做好相关设置，具体步骤如下：</p>
            <p>
                1、 微信特约商户签约设置官方地址：<a href="https://pay.weixin.qq.com/index.php/core/home/login" target="_blank"
                    >https://pay.weixin.qq.com/index.php/core/home/login</a
                >
            </p>
            <p>2、 通过上方地址登录，并依次设置完成：支付工具 中的【APP支付、Native支付、H5支付】、支付扩展工具 中的【分账】设置为30%</p>
            <p>
                3、 设置完成后在返回【商家端 --> 商城管理 --> 通用设置 --> 微信特约商户设置 】
                中勾选(我已确认本店铺微信特约商户(与本平台)签约信息无误，并可用于微信分账使用) 并【确认提交】
            </p>
            <p style="width: 100%"><img src="./b1.png" alt="" style="width: 100%" /></p>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped></style>
