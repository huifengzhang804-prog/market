import { doDeleteProductLabel, doGetProductLabel, doPostProductLabel, doPutProductLabel } from '@/apis/good/tag'
import HandleTagVue from '../components/HandleTag.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
enum SHOP_TYPE_ENUM {
    SELF_OWNED = '自营',
    PREFERRED = '优选',
    ORDINARY = '普通',
}
const useTagListHooks = () => {
    const tagTableList = ref<any[]>([])
    const pageConfig = reactive({
        pageSize: 20,
        pageNum: 1,
        total: 0,
    })
    const handleTagRef = ref<InstanceType<typeof HandleTagVue> | null>(null)
    const currentTag = ref<any>({})
    const showTagDialog = ref(false)
    const handleSizeChange = (val: number) => {
        pageConfig.pageSize = val
        initList()
    }
    const handleCurrentChange = (val: number) => {
        pageConfig.pageNum = val
        initList()
    }
    const initList = async () => {
        let dataList: any[] = [],
            total = 0
        try {
            const { data, code } = await doGetProductLabel({ current: pageConfig.pageNum, size: pageConfig.pageSize })
            if (code === 200) {
                dataList = data?.records || []
                total = data?.total || 0
            }
        } finally {
            tagTableList.value = dataList
            pageConfig.total = total
        }
    }
    const computedShopType = computed(() => (row: any) => {
        const shopTypeStrs: string[] = []
        row?.shopType?.forEach((key: keyof typeof SHOP_TYPE_ENUM) => {
            if (SHOP_TYPE_ENUM[key]) shopTypeStrs.push(SHOP_TYPE_ENUM[key])
        })
        return shopTypeStrs.join('、')
    })
    const handleEditTag = (row: any) => {
        currentTag.value = row
        showTagDialog.value = true
    }
    const handleDeleteTag = async (row: any) => {
        ElMessageBox.confirm('确认删除当前标签?').then(async () => {
            const { code, msg } = await doDeleteProductLabel(row?.id)
            if (code === 200) {
                ElMessage.success({ message: msg || '标签删除成功' })
                initList()
            } else {
                ElMessage.error({ message: msg || '标签删除失败' })
            }
        })
    }
    const handleAddTag = () => {
        showTagDialog.value = true
    }
    const handleConfirmTag = async () => {
        const data: any = await handleTagRef.value?.getFormModel()
        const requestFn = data?.id ? doPutProductLabel : doPostProductLabel
        const { code, msg } = await requestFn(data)
        if (code === 200) {
            ElMessage.success({ message: msg || `标签${data?.id ? '修改' : '编辑'}成功` })
            showTagDialog.value = false
            initList()
        } else {
            ElMessage.error({ message: msg || `标签${data?.id ? '修改' : '编辑'}失败` })
        }
    }
    initList()
    return {
        tagTableList,
        pageConfig,
        handleSizeChange,
        handleEditTag,
        handleDeleteTag,
        handleCurrentChange,
        computedShopType,
        handleAddTag,
        currentTag,
        showTagDialog,
        handleConfirmTag,
        handleTagRef,
    }
}

export default useTagListHooks
