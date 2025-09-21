<script setup lang="ts">
import { doPostShopPagesSave, doPutShopPages } from '@/apis/decoration'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import QEdit from '@/components/q-edit/q-edit.vue'
import { useVModel } from '@vueuse/core'
import { ElMessage, type FormRules } from 'element-plus'
import { ComponentsItem } from './type'

const props = defineProps<{
    formData: {
        name: string
        img: string
        text: string
    }
}>()

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const formRef = ref()

/**
 * 校验
 */
const rules: FormRules = {
    name: [{ required: true, trigger: 'blur', message: '展示名称为必填项！' }],
    text: [{ required: true, trigger: 'blur', message: '活动主图为必填项！' }],
}

/**
 * 新增页面
 */
const shopStore = useShopInfoStore()
const pageSave = async () => {
    try {
        await formRef.value.validate()
        const payload = {
            shopId: shopStore.shopInfo.id,
            name: formData.value.name,
            templateType: 'SHOP_HOME_PAGE',
            businessType: 'ONLINE',
            endpointType: 'PC_MALL',
            type: 'CUSTOMIZED_PAGE',
            properties: formData.value,
            customType: 'text',
        }
        const { success } = await doPostShopPagesSave(payload)
        if (!success) {
            ElMessage.error('新增图文页面失败！')
            return Promise.reject()
        }
    } catch (error) {
        return Promise.reject()
    }
}

/**
 * 编辑页面
 */
const editPage = async (data: ComponentsItem) => {
    try {
        await formRef.value.validate()
        const payload = {
            ...data,
            name: formData.value.name,
            properties: formData.value,
        }

        const { success } = await doPutShopPages(payload)
        if (!success) {
            ElMessage.error('编辑活动页面失败！')

            return Promise.reject()
        }
    } catch (error) {
        return Promise.reject()
    }
}

defineExpose({
    pageSave,
    editPage,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="64">
        <el-form-item label="页面名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入页面名称" show-word-limit maxlength="5" />
        </el-form-item>
        <el-form-item label="页面主图">
            <div style="height: 230px">
                <upload v-model="formData.img" width="846px" height="200px" />
                <span class="text">1920*450~550</span>
            </div>
        </el-form-item>

        <el-form-item label="页面内容" prop="text">
            <div class="editor">
                <q-edit v-model:content="formData.text" height="207px" />
            </div>
        </el-form-item>
    </el-form>
</template>

<style lang="scss">
.editor {
    width: 100%;
    border: 1px solid #ccc;
}

.text {
    color: #999999;
}
</style>
