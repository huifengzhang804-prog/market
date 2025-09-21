import { doGetExamineGoodsList, doPutReviewGoodsInfo } from '@/apis'
import { ElMessage, ElMessageBox } from 'element-plus'

const useExamineListHooks = () => {
    const $router = useRouter()
    const searchParams = reactive({
        name: '',
        categoryId: '',
        productType: '',
    })
    const currentTab = ref<'' | 'ALREADY_PASSED' | 'UNDER_REVIEW' | 'REFUSE'>('')

    const initList = async (sort?: string) => {
        let goodsList = [],
            total = 0
        try {
            const result = await doGetExamineGoodsList({ ...searchParams, ...tableList.page, productAuditStatus: currentTab.value, sort: sort })
            if (result.code === 200) {
                goodsList = result.data.records
                total = result.data.total
            }
        } finally {
            tableList.goods = goodsList
            tableList.total = total
        }
    }

    const salePriceRange = computed(() => (salePrices: string[] = []) => {
        const min = Math.min(...salePrices.map((item) => parseInt(item))) / 10000
        const max = Math.max(...salePrices.map((item) => parseInt(item))) / 10000
        if (max === min) {
            return max
        } else {
            return `${min}-${max}`
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
    initList()
    const handleEditExamineGoods = (id: string) => {
        $router.push({
            path: '/goods/list/edit',
            query: {
                id,
            },
        })
    }
    const handleCopyExamineGoods = (id: string) => {
        $router.push({
            path: '/goods/list/copy',
            query: {
                id,
                isCopy: 'true',
            },
        })
    }
    const handleCommand = (command: string, row: any) => {
        switch (command) {
            case 'copy':
                handleCopyExamineGoods(row?.id)
                break
            case 'reason':
                break
            case 'commit':
                handleReCommitExamineGood(row?.id)
                break
            default:
                break
        }
    }
    const handleReCommitExamineGood = (id: string) => {
        ElMessageBox.confirm('请确认是否提交商品审核 ？？？', '请确认').then(async () => {
            const { code, msg } = await doPutReviewGoodsInfo(id)
            if (code === 200) {
                ElMessage.success({ message: msg || '提交审核成功' })
                initList()
            } else {
                ElMessage.error({ message: msg || '提交审核失败' })
            }
        })
    }
    // 价钱排序
    const sort = ref('')
    const sortTableList = (label: string) => {
        switch (label) {
            case '价格':
                sort.value = sort.value === 'SALE_PRICE_ASC' ? 'SALE_PRICE_DESC' : 'SALE_PRICE_ASC'
                initList(sort.value)
                break
            case '提交时间':
                sort.value = sort.value === 'SUBMIT_TIME_ASC' ? 'SUBMIT_TIME_DESC' : 'SUBMIT_TIME_ASC'
                initList(sort.value)
                break
            case '审核时间':
                sort.value = sort.value === 'AUDIT_TIME_ASC' ? 'AUDIT_TIME_DESC' : 'AUDIT_TIME_ASC'
                initList(sort.value)
                break
            default:
                break
        }
    }
    // 分页方法
    const handleSizeChange = (val: number) => {
        tableList.page.current = 1
        tableList.page.size = val
        initList()
    }
    const handleCurrentChange = (val: number) => {
        tableList.page.current = val
        initList()
    }
    return {
        getSearch,
        currentTab,
        goodsStatus,
        handleTabClick,
        tableList,
        salePriceRange,
        ExamineGoodsEnum,
        initList,
        handleEditExamineGoods,
        handleCopyExamineGoods,
        handleCommand,
        sortTableList,
        handleSizeChange,
        handleCurrentChange,
    }
}

export default useExamineListHooks
