import { doGetShopExamineGoods, doPutShopExamineGoods } from '@/apis/good'
import auditGoodsVue from '../components/audit-goods.vue'
import { ElMessage } from 'element-plus'

const useExamineListHooks = () => {
    let cacheCurrentCheckRows: any[] = [] // 用于暂存当前选定的行信息
    const searchParams = reactive({
        name: '',
        platformCategoryId: '',
        productType: '',
        shopId: '',
    })
    const currentTab = ref<'' | 'ALREADY_PASSED' | 'UNDER_REVIEW' | 'REFUSE'>('')
    const selectItems = ref<any[]>([])
    const showAuditDialog = ref(false)
    const auditGoodsRefs = ref<InstanceType<typeof auditGoodsVue> | null>(null)
    const formModel = reactive({
        explain: '',
    })
    const visible = ref(false)
    const refuseVisible = ref(false)

    const initList = async (sort?: string) => {
        let goodsList = [],
            total = 0
        try {
            const result = (await doGetShopExamineGoods({
                ...searchParams,
                ...tableList.page,
                productAuditStatus: currentTab.value,
                sort: sort,
            })) as any
            if (result.code === 200) {
                goodsList = result.data.records
                total = result.data.total
            }
        } finally {
            tableList.goods = goodsList
            tableList.total = total
        }
    }

    const handleChangeCurrentChange = (current: number) => {
        tableList.page.current = current
        initList()
    }

    const salePriceRange = computed(() => (salePrices: string[] = []) => {
        const min = Math.min(...salePrices.map((item) => parseInt(item))) / 10000
        const max = Math.max(...salePrices.map((item) => parseInt(item))) / 10000
        if (max === min) {
            return max.toFixed(2)
        } else {
            return `${min.toFixed(2)} ~￥${max.toFixed(2)}`
        }
    })
    const getSearch = (e: typeof searchParams) => {
        ;(Object.keys(searchParams) as (keyof typeof searchParams)[]).forEach((key) => (searchParams[key] = e[key]))
        initList()
    }
    const goodsStatus = {
        全部: '',
        待审核: 'UNDER_REVIEW',
        已通过: 'ALREADY_PASSED',
        已拒绝: 'REFUSE',
    }

    const handleTabClick = () => {
        tableList.page.current = 1
        initList()
    }

    /**
     * 批量审核
     */
    const handleBatchExamine = () => {
        if (selectItems.value.length) {
            cacheCurrentCheckRows = selectItems.value
            showAuditDialog.value = true
            return
        }
        return ElMessage.error({ message: '请选择需要审核的商品信息' })
    }

    const tableList = reactive({
        page: { size: 10, current: 1 },
        goods: [],
        total: 0,
    })
    enum ExamineGoodsEnum {
        UNDER_REVIEW = '待审核',
        ALREADY_PASSED = '已通过',
        REFUSE = '已拒绝',
    }
    const handleAuditGoods = (goodsList: any[], flag: boolean) => {
        if (goodsList.length === 0) {
            return ElMessage.error({ message: '请选择需要审核的商品信息' })
        }
        cacheCurrentCheckRows = goodsList
        if (flag) {
            visible.value = true
        } else {
            refuseVisible.value = true
        }
    }
    const handleConfirmAudit = async (status = '', explain = '') => {
        let rs = {} as any
        if (showAuditDialog.value) {
            rs = await auditGoodsRefs.value?.validateForm()
        }
        if (status === 'REFUSE' && !explain) {
            return
        }
        const statu = rs.status ? rs.status : status
        const { code, msg } = await doPutShopExamineGoods(statu, {
            explain: explain ? explain : rs.explain,
            keys: cacheCurrentCheckRows?.map((item: any) => ({ productId: item.id, shopId: item.shopId })),
        })
        if (code === 200) {
            ElMessage.success({ message: msg || '更新状态成功' })
            if (status && explain) {
                refuseVisible.value = false
            } else if (rs.status || rs.explain) {
                showAuditDialog.value = false
            } else if (status && !explain) {
                visible.value = false
            }
            formModel.explain = ''
            initList()
        } else {
            ElMessage.error({ message: msg || '更新状态失败' })
        }
    }
    const handleCloseAuditDialog = () => {
        cacheCurrentCheckRows = []
    }
    const handleChangeSize = (pageSize: number) => {
        tableList.page.size = pageSize
        tableList.page.current = 1
        initList()
    }
    const reasonRefusalDialog = ref(false)
    const reason = ref('')
    // 拒绝原因
    const reasonRefusal = (row?: any) => {
        reasonRefusalDialog.value = !reasonRefusalDialog.value
        reason.value = row?.explain
    }
    // 时间排序
    let sort = ref('')
    const sortTableList = (label: string) => {
        if (label === '提交时间') {
            sort.value = sort.value === 'SUBMIT_TIME_ASC' ? 'SUBMIT_TIME_DESC' : 'SUBMIT_TIME_ASC'
            initList(sort.value)
        } else {
            sort.value = sort.value === 'AUDIT_TIME_ASC' ? 'AUDIT_TIME_DESC' : 'AUDIT_TIME_ASC'
            initList(sort.value)
        }
    }
    initList()
    return {
        getSearch,
        currentTab,
        goodsStatus,
        handleTabClick,
        tableList,
        salePriceRange,
        ExamineGoodsEnum,
        initList,
        selectItems,
        handleAuditGoods,
        showAuditDialog,
        auditGoodsRefs,
        handleCloseAuditDialog,
        handleConfirmAudit,
        handleChangeCurrentChange,
        handleChangeSize,
        reasonRefusal,
        reasonRefusalDialog,
        reason,
        sortTableList,
        handleBatchExamine,
        formModel,
        visible,
        refuseVisible,
    }
}

export default useExamineListHooks
