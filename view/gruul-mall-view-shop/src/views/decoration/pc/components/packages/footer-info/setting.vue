<script setup lang="ts">
import footerInfo from './footer-info'
import logo from './views/logo.vue'
import module from './views/module.vue'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof footerInfo>,
        default: footerInfo,
    },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const activeName = ref('logoPane')

const logoPane = ref<InstanceType<typeof logo>>()
const modulePane = ref<InstanceType<typeof module>>()

/**
 * 校验
 */
const validate = async () => {
    const arr = [
        { formRef: logoPane.value?.formRef, name: 'logoPane' },
        { formRef: modulePane.value?.formRef, name: 'modulePane' },
    ]

    for (let i = 0; i < arr.length; i++) {
        try {
            await arr[i].formRef?.validate()
        } catch (error) {
            activeName.value = arr[i].name
            return false
        }
    }
    return true
}

defineExpose({
    formRef: { validate },
})
</script>

<template>
    <el-tabs v-model="activeName">
        <el-tab-pane name="logoPane">
            <template #label> <div class="label">平台LOGO</div> </template>

            <logo ref="logoPane" v-model:form-data="formData.logo" />
        </el-tab-pane>

        <el-tab-pane name="modulePane">
            <template #label> <div class="label">模块信息</div></template>

            <module ref="modulePane" v-model:form-data="formData.module" />
        </el-tab-pane>

        <el-tab-pane name="QRcodePane">
            <template #label> <div class="label">二维码</div> </template>

            <el-form :model="formData">
                <el-form-item label="二维码">
                    <div style="height: 115px; padding-top: 5px">
                        <div class="QRcode">
                            <upload v-model="formData.QRcode" width="88px" height="88px" />

                            <el-icon v-if="formData.QRcode" class="QRcode__icon" @click="formData.QRcode = ''">
                                <CircleCloseFilled />
                            </el-icon>
                        </div>

                        <span class="text">88*88</span>
                    </div>
                </el-form-item>
            </el-form>
        </el-tab-pane>
    </el-tabs>
</template>

<style lang="scss" scoped>
@include b(el-tabs) {
    height: 434px;
    .el-tabs__nav-wrap::after {
        background-color: #e5e6eb;
        height: 1px;
    }
}

@include b(label) {
    padding: 0 14px;
}

@include b(text) {
    color: #999999;
}

@include b(QRcode) {
    width: 88px;
    height: 88px;
    position: relative;

    @include e(icon) {
        cursor: pointer;
        position: absolute;
        top: -5px;
        right: -5px;
    }
}
</style>
