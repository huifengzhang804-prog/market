<template>
    <el-form ref="formRef" :model="categoryFormModel" :rules="formFules">
        <el-form-item label="移动至分类" prop="parentId">
            <el-cascader v-model="categoryFormModel.parentId" :options="materialCategoryList" :show-all-levels="false" :props="defaultProps" />
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { CascaderProps, FormInstance, FormRules } from 'element-plus'
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
const defaultProps: CascaderProps = {
    expandTrigger: 'hover',
    checkStrictly: true,
    emitPath: false,
    label: 'name',
    value: 'id',
}
const formRef = ref<FormInstance | null>(null)
const $props = withDefaults(defineProps<IProps>(), {
    formModel: () => ({
        parentId: ' ',
        name: '',
        id: '',
    }),
})
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
    parentId: {
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
