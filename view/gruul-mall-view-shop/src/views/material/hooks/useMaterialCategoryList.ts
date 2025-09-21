import { doGetMaterialCategoryList, doPostMaterialInfo, doPutMaterialInfo, doDelMaterialInfo } from '@/apis/material'
import handleCategoryVue from '../components/handle-category.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Storage from '@/utils/Storage'

interface TreeData {
    name: string
    children: TreeData[]
    id: Long
    hasChildren?: boolean
    parentId?: Long
}

const useMaterialCategoryList = () => {
    const allClassifications = [{ hasChildren: false, name: '全部', id: ' ', parentId: ' ', children: [] }]
    const materialCategoryList = ref<TreeData[]>([])
    const currentFormModel = reactive({
        parentId: new Storage().getItem('shopParentId') || ' ',
        name: '',
        id: ' ',
    })
    const handleCategoryRef = ref<InstanceType<typeof handleCategoryVue> | null>(null)
    const getMaterialCategoryList = async (parentId = '', isCover = true) => {
        const result = await doGetMaterialCategoryList<TreeData[]>(parentId)
        if (isCover) {
            materialCategoryList.value = [...allClassifications, ...result.data] as any
        } else {
            return result?.data?.map((item: { id: Long; name: string }) => ({
                value: item?.id,
                label: item?.name,
            }))
        }
    }

    const showDialog = ref(false)
    const handleCloseDialog = () => {
        Object.keys(currentFormModel).forEach((key) => {
            currentFormModel[key as keyof typeof currentFormModel] = ''
            currentFormModel.name = ''
        })
    }
    const handleAddCategory = (item?: { hasChildren: boolean; name: string; id: Long }) => {
        showDialog.value = true
        if (item) {
            currentFormModel.name = item.name
            currentFormModel.id = item.id as string
        } else {
            currentFormModel.name = ''
            currentFormModel.id = ''
        }
    }
    const handleConfirm = async () => {
        await handleCategoryRef.value?.validateFormRules()
        // 判断是否存在ID号
        let fn: any
        if (currentFormModel.id) {
            // 编辑接口
            fn = doPutMaterialInfo
        } else {
            // 添加接口
            fn = doPostMaterialInfo
        }
        const { code, msg } = await fn(currentFormModel)
        // 存在ID号，则为编辑
        if (code === 200) {
            ElMessage.success(`${currentFormModel.id ? '编辑' : '添加'}成功`)
            showDialog.value = false
            getMaterialCategoryList()
        } else {
            ElMessage.error(msg || `${currentFormModel.id ? '编辑' : '添加'}失败`)
        }
        // 不存在ID号，则为新增
    }
    // 删除分类
    const delMaterialInfo = async (id: string) => {
        ElMessageBox.confirm('你确定删除该分类吗?')
            .then(async () => {
                const { code, msg } = await doDelMaterialInfo(id)
                if (code === 200) ElMessage.success('删除成功')
                else ElMessage.error(msg || '删除失败')
                getMaterialCategoryList()
            })
            .catch(() => {
                ElMessage('已取消')
            })
    }

    return {
        materialCategoryList,
        handleAddCategory,
        currentFormModel,
        showDialog,
        handleCloseDialog,
        handleConfirm,
        handleCategoryRef,
        getMaterialCategoryList,
        delMaterialInfo,
    }
}

export default useMaterialCategoryList
