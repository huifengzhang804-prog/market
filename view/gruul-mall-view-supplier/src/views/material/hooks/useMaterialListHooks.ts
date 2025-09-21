import { doPostMaterialList, doDelMaterial, doPutMaterialName } from '@/apis/material'
import { ElMessageBox, ElMessage } from 'element-plus'
import materialVistVue from '../material-list.vue'

export type SearchType = {
    format: string
    name: string
    imgSize: string
    categoryId: string
}
interface MaterialList {
    initialList: () => Promise<void>
    searchCondition: Ref<SearchType>
    // 其他
    [key: string]: any
}

const useMaterialListHooks = (): MaterialList => {
    const checkedSelect = ref([])
    const tableList = ref([])
    const tableHeight = ref('calc(100vh - 260px)')
    const paginationOptions = reactive({
        page: { size: 10, current: 1 },
        total: 0,
    })
    const searchCondition = ref({
        format: '',
        name: '',
        imgSize: '',
        categoryId: '',
    })
    const changeTableHeight = (isShowCondition: boolean) => {
        tableHeight.value = isShowCondition ? 'calc(100vh - 365px)' : 'calc(100vh - 260px)'
    }
    const handleSearch = (searchType: SearchType) => {
        Object.keys(searchCondition.value).forEach((key) => (searchCondition.value[key] = searchType[key]))
        paginationOptions.page.current = 1
        initialList()
    }
    const initialList = async () => {
        let tableData = [],
            total = 0
        try {
            const result = await doPostMaterialList({ ...searchCondition.value, ...paginationOptions.page })
            tableData = result?.data?.records
            total = result?.data?.total || 0
        } finally {
            tableList.value = tableData
            paginationOptions.total = total
        }
    }
    // initialList()
    const handleSizeChange = (val: number) => {
        paginationOptions.page.current = 1
        paginationOptions.page.size = val
        initialList()
        checkedSelect.value = []
    }
    const handleCurrentChange = (val: number) => {
        paginationOptions.page.current = val
        initialList()
        checkedSelect.value = []
    }
    const lookInfoVal = ref(false)
    const lookInfos = ref()
    const delIds = ref([])
    const disable = ref(false)
    const pImgs = ref('')
    const handlePreview = (rowData: any, index: string) => {
        lookInfos.value = rowData
        if (index === 'look' || index === '') {
            lookInfoVal.value = true
            disable.value = false
            pImgs.value = rowData?.url
        }
        if (index === '') disable.value = true
        if (index === 'del') {
            ElMessageBox.confirm('你确定删除该素材吗?')
                .then(() => {
                    delIds.value.push(rowData.id)
                    doDelMaterialInt(delIds.value)
                })
                .catch(() => {
                    ElMessage('已取消')
                })
        }
        delIds.value = []
    }
    // 批量删除
    const checkedSelectIds = ref<string[]>([])
    const delBatchFn = () => {
        if (checkedSelect.value.length <= 0) return ElMessage.error('请选择需要删除的素材')
        ElMessageBox.confirm('你确定删除这些素材吗?')
            .then(() => {
                checkedSelectIds.value = checkedSelect.value.map((item: any) => item.id)
                doDelMaterialInt(checkedSelectIds.value)
            })
            .catch(() => {
                ElMessage('已取消')
            })
    }
    const doDelMaterialInt = async (ids: string[]) => {
        const { code, msg } = await doDelMaterial(ids)
        if (code === 200) {
            ElMessage.success('删除成功')
            await initialList()
            return
        } else ElMessage.error(msg || '删除失败')
    }

    // 上传
    const uploadingVal = ref(false)
    const uploadingFn = () => {
        uploadingVal.value = true
    }
    const ruleFormRef = ref<InstanceType<typeof materialVistVue> | null>(null)
    const lookInfoValFn = async (item: any) => {
        await ruleFormRef.value
            ?.validate()
            .then(async (isValid: any) => {
                if (disable.value && isValid) {
                    const { code, msg } = await doPutMaterialName(item.id, item.name)
                    if (code === 200) {
                        ElMessage.success('操作成功')
                    } else ElMessage.error(msg || '重命名失败')
                }
                lookInfoFn()
            })
            .catch((err: any) => {
                return
            })
    }
    const lookInfoFn = () => {
        lookInfoVal.value = false
        initialList()
    }
    // 移动至
    const showDia = ref(false)
    const handleAddCategoryTran = () => {
        if (checkedSelect.value.length <= 0) return ElMessage.error('请选择需要移动的素材')
        checkedSelectIds.value = checkedSelect.value.map((item: any) => item.id)
        showDia.value = true
    }
    return {
        checkedSelect,
        tableList,
        tableHeight,
        handlePreview,
        paginationOptions,
        initialList,
        changeTableHeight,
        handleSearch,
        doDelMaterial,
        lookInfoVal,
        lookInfos,
        lookInfoFn,
        disable,
        uploadingVal,
        uploadingFn,
        lookInfoValFn,
        handleSizeChange,
        handleCurrentChange,
        delBatchFn,
        handleAddCategoryTran,
        showDia,
        checkedSelectIds,
        ruleFormRef,
        searchCondition,
        pImgs,
    }
}

export default useMaterialListHooks
