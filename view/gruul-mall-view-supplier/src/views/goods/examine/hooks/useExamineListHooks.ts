import { doGetExamineGoodsList, doPutReviewGoodsInfo } from '@/apis'
import useExamineRejectReason from './useExamineRejectReason'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableList = reactive({
    page: { size: 10, current: 1 },
    goods: [],
    total: 0,
})
const useExamineListHooks = () => {
    const { showReasonDialog, rejectReson, handleShowRejectReason } = useExamineRejectReason()
    const $router = useRouter()
    const searchParams = reactive({
        name: '',
        platformCategoryId: '',
        sellType: '',
    })
    const currentTab = ref<'' | 'ALREADY_PASSED' | 'UNDER_REVIEW' | 'REFUSE'>('')
    const tableHeight = ref('calc(100vh - 300px)')

    const initList = async () => {
        let goodsList = [],
            total = 0
        try {
            const result = await doGetExamineGoodsList({ ...searchParams, ...tableList.page, productAuditStatus: currentTab.value })
            if (result.code === 200) {
                goodsList = result.data.records
                total = result.data.total
            }
        } finally {
            tableList.goods = goodsList
            tableList.total = total
        }
    }
    initList()
    const salePriceRange = computed(() => (salePrices: string[] = []) => {
        const min = Math.min(...salePrices.map((item) => parseInt(item))) / 10000
        const max = Math.max(...salePrices.map((item) => parseInt(item))) / 10000
        if (max === min) {
            return max.toFixed(2)
        } else {
            return `${min.toFixed(2)} ~ ${max.toFixed(2)}`
        }
    })
    const getSearch = (e: typeof searchParams) => {
        Object.keys(searchParams).forEach((key) => (searchParams[key] = e[key]))
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

    enum ExamineGoodsEnum {
        ALREADY_PASSED = '已通过',
        UNDER_REVIEW = '待审核',
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
            path: '/goods/list/edit',
            query: {
                id,
                isCopy: 'true',
            },
        })
    }
    const handleCommand = (command: string, row: any) => {
        console.log('handleCommand', command, row)
        switch (command) {
            case 'copy':
                handleCopyExamineGoods(row?.id)
                break
            case 'reason':
                handleShowRejectReason(row?.explain)
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
                ElMessage.success({ message: msg || '审核成功' })
                initList()
            } else {
                ElMessage.error({ message: msg || '审核失败' })
            }
        })
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
        tableHeight,
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
        showReasonDialog,
        rejectReson,
        handleSizeChange,
        handleCurrentChange,
    }
}

export default useExamineListHooks
