<template>
    <el-form ref="formRef" :model="categoryFormModel" :rules="formFules" label-width="85px">
        <el-form-item v-if="!categoryFormModel.id" label="上级分类" prop="parentId" style="margin-bottom: 30px">
            <el-cascader v-model="categoryFormModel.parentId" :options="materialCategoryList" :show-all-levels="false" :props="defaultProps" />
        </el-form-item>
        <el-form-item label="分类名称" prop="name" style="width: 500px">
            <el-input v-model="categoryFormModel.name" :maxlength="8" placeholder="请输入分类名称" />
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { FormInstance, FormRules } from 'element-plus'
import useMaterialCategoryList from '../hooks/useMaterialCategoryList'
const { materialCategoryList, getMaterialCategoryList } = useMaterialCategoryList()
getMaterialCategoryList()
interface IProps {
    formModel: {
        parentId: string
        name: string
        id?: string
    }
}
const formRef = ref<FormInstance | null>(null)
const $props = withDefaults(defineProps<IProps>(), {
    formModel: () => ({
        parentId: '',
        name: '',
        id: '',
    }),
})
const defaultProps = {
    expandTrigger: 'hover',
    checkStrictly: true,
    emitPath: false,
    label: 'name',
    value: 'id',
}
const $emit = defineEmits(['update:formModel'])
const categoryFormModel = computed({
    get() {
        return $props.formModel
    },
    set(newVal) {
        $emit('update:formModel', newVal)
    },
})
const formFules: FormRules = {
    name: {
        required: true,
        message: '请输入分类名称',
        trigger: 'blur',
    },
}
const load = async (node: any, resolve: any) => {
    if (node.level === 0) return
    const data = await getMaterialCategoryList(node.data.value, false)
    resolve(data)
}
const validateFormRules = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid) => {
                if (isValid) {
                    resolve('validate success')
                } else {
                    reject('validate error')
                }
            })
        } else {
            reject('none form inst')
        }
    })
}
defineExpose({ validateFormRules })
</script>
