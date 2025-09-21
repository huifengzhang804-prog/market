import { CascaderInstance, CascaderProps, ElMessage, FormInstance, FormRules, UploadProps } from 'element-plus'
import { FormInject } from '../types'
import { ApiFreightTemplate } from '@/views/freight/components/types'
import { ShopCategoryList } from '../types/basic'
import { doGetCopyGoods, doGetPlatformCategory, doGetSingleCommodity } from '@/apis/good'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetLogisticsTemplateList } from '@/apis/freight/freight-template'
import { SellTypeEnum } from '../../types'

const basicRules: FormRules = {
    name: [
        {
            required: true,
            message: '请填写商品名称',
            trigger: 'blur',
        },
    ],
    albumPics: [
        {
            required: true,
            message: '请添加商品主图',
            trigger: 'blur',
        },
    ],
    platformCategoryId: [{ required: true, message: '请选择平台分类', trigger: 'change' }],
}

const shopCascaderProps: CascaderProps = {
    expandTrigger: 'hover',
    label: 'name',
    value: 'id',
}

const beforeVideoUpload: UploadProps['beforeUpload'] = (rawfile) => {
    const whiteList = ['video/mp4']
    const isLt = rawfile.size < 5 * 1024 * 1024
    if (!whiteList.includes(rawfile.type)) {
        ElMessage.error('上传视频只能是 mp4 格式!')
        return false
    }
    if (!isLt) {
        ElMessage.error('上传视频大小不超过5M!')
        return false
    }
    return true
}

const useBasicInfo = () => {
    // 前置导入类型
    const ChooseBrandComp = () => defineAsyncComponent(() => import('../components/ChooseBrand.vue'))
    type ChooseBrandCompType = ReturnType<typeof ChooseBrandComp>
    // 引入全局标识符
    const $route = useRoute()
    const $router = useRouter()
    const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'
    const currentFormRef = ref<FormInstance>()
    const chooseBrandRef = ref<InstanceType<ChooseBrandCompType>>()
    // inject datas
    const $parent = inject('form') as FormInject
    const { submitForm, commodityImgList, copyGoodsAL, copyGoodsJD, copyGoodsTB } = $parent
    // define variables
    const logisticsData = ref<ApiFreightTemplate[]>([])
    const platformCategoryList = ref<ShopCategoryList[]>([])
    // 是否展示品牌选择页面
    const dialogVisible = ref(false)
    const BrandShowName = ref('')
    const handleChoose = () => {
        submitForm.value.brandId = chooseBrandRef.value?.BrandRadio || ''
        chooseBrandRef.value?.BrandList.forEach((item) => {
            if (item.id === chooseBrandRef.value?.BrandRadio) {
                BrandShowName.value = item.brandName
            }
        })
        dialogVisible.value = false
    }
    const copyGoods = async (CopyGoodsType: string, goodsUrl: string) => {
        if (!goodsUrl) {
            return
        }
        const { code, data, msg } = await doGetCopyGoods({
            CopyGoodsType,
            goodsUrl,
        })
        if (code !== 200) {
            ElMessage.error(msg ? msg : '商品一键复制失败')
            return
        }
        submitForm.value = { ...submitForm.value, ...data, name: data.name.slice(0, 35) }
        submitForm.value.collectionUrl = goodsUrl
        commodityImgList.value = data.albumPics.split(',')
    }

    /**
     * @description: 获取平台类目
     */
    async function getPlatformCategory() {
        const { code, data, success } = await doGetPlatformCategory()
        if (code !== 200) {
            ElMessage.error('获取平台分类失败')
            return
        }
        platformCategoryList.value = checkCategoryEnable(1, data)
    }

    const platformCategoryRef = ref<CascaderInstance | null>(null)

    /**
     * @description: 数据回显
     */
    async function dataDisplay() {
        if ($route.query.id) {
            const { code, data } = await doGetSingleCommodity($route.query.id, useShopInfoStore().shopInfo.id)
            if (code !== 200) {
                ElMessage.error('获取商品信息失败')
                return
            }
            // 商品回显图片字符转数组
            commodityImgList.value = data.albumPics.split(',')
            submitForm.value = Object.assign(submitForm.value, data)
        }
    }
    onActivated(async () => {
        const forwardPath: string = $router.options.history.state.forward as string
        if (forwardPath !== '/freight/logistics?from=releaseGoods') {
            // 如果是从设置页面回来的不需要再次进行回显
            await dataDisplay()
        } else {
            initLogisticsTemplateList()
        }
        // initBrandList()
        Promise.all([getPlatformCategory()]).then(() => {
            categoryChange('platformCategory').then(() => {})
        })
    })

    const categoryChange = async (categoryType: 'platformCategory') => {
        const iscategoryType = categoryType === 'platformCategory'
        const value = platformCategoryRef.value as any
        if (!value) {
            return
        }
        const checkedNodes = await value.getCheckedNodes()
        let category = submitForm.value[categoryType]
        if (!category) {
            const defaultCategory = {
                one: null,
                two: null,
                three: null,
            }
            submitForm.value[categoryType] = defaultCategory
            category = defaultCategory
        }
        if (!checkedNodes || checkedNodes.length === 0) {
            category.one = null
            category.two = null
            category.three = null
            return
        }
        const categoryIds = checkedNodes[0].pathValues
        const isEmpty = !categoryIds || categoryIds.length === 0
        if (iscategoryType) {
            const categoryId = checkedNodes[0].pathNodes[0].data.categoryId
            category.one = isEmpty ? null : categoryId
        } else {
            category.one = isEmpty ? null : categoryIds[0]
        }
        category.two = isEmpty ? null : categoryIds[1]
        category.three = isEmpty ? null : categoryIds[2]
    }
    const handleSortCommodityList = () => {
        submitForm.value.albumPics = commodityImgList.value.join(',')
    }
    function imageConversion() {
        let tmp: string[] = []
        if (submitForm.value.albumPics) {
            tmp = submitForm.value.albumPics.split(',')
        }
        commodityImgList.value = tmp
    }
    // 商品主图字符串转数组处理
    watch(
        submitForm.value,
        () => {
            imageConversion()
        },
        {
            immediate: true,
        },
    )
    const closeTag = () => {
        submitForm.value.brandId = ''
        BrandShowName.value = ''
        if (chooseBrandRef.value) {
            chooseBrandRef.value.BrandRadio = ''
        }
    }
    /**
     * @description: 删除商品主图
     * @param {number} index
     */
    const delImgHandle = (index: number) => {
        commodityImgList.value.splice(index, 1)
        submitForm.value.albumPics = commodityImgList.value.join(',')
    }
    /**
     * @description: 新增商品主图
     * @returns {UploadProps}
     */
    const addNewMainSuccess: UploadProps['onSuccess'] = (response) => {
        commodityImgList.value.push(response)
        submitForm.value.albumPics = commodityImgList.value.join(',')
    }
    /**
     * function
     */
    async function initLogisticsTemplateList() {
        const { code, data } = await doGetLogisticsTemplateList(1, 1000)
        if (code !== 200) return ElMessage.error('获取物流列表失败')
        logisticsData.value = data.records
    }
    onMounted(() => initLogisticsTemplateList())
    const uploadVideoSuccess: UploadProps['onSuccess'] = (response) => {
        submitForm.value.videoUrl = response.data
    }
    const handleChooseBrand = () => {
        dialogVisible.value = true
        nextTick(() => {
            chooseBrandRef.value?.initBrandList()
        })
    }
    const changeSellType = (sellType: keyof typeof SellTypeEnum) => {
        if (sellType === 'CONSIGNMENT') {
            submitForm.value.skus.forEach((sku) => (sku.minimumPurchase = 0))
            submitForm.value.productType = 'REAL_PRODUCT'
        }
    }
    return {
        copyGoodsAL,
        copyGoodsJD,
        copyGoodsTB,
        submitForm,
        commodityImgList,
        currentFormRef,
        uploadUrl,
        logisticsData,
        platformCategoryList,
        dialogVisible,
        BrandShowName,
        handleChoose,
        chooseBrandRef,
        basicRules,
        copyGoods,
        platformCategoryRef,
        categoryChange,
        beforeVideoUpload,
        handleSortCommodityList,
        closeTag,
        delImgHandle,
        addNewMainSuccess,
        shopCascaderProps,
        uploadVideoSuccess,
        handleChooseBrand,
        changeSellType,
    }
}

export default useBasicInfo

/**
 * 检查是分类否可用
 * @param currentLevel
 * @param records
 */
function checkCategoryEnable(currentLevel: number, records: any[]) {
    const isLastLevel = currentLevel === 3
    for (let index = 0; index < records.length; ) {
        const record = records[index]
        if (isLastLevel) {
            record.disabled = false
            index++
            continue
        }
        const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
        delete record.secondCategoryVos
        delete record.categoryThirdlyVos
        const disable = !children || children.length === 0
        record.disabled = disable
        if (disable) {
            records.splice(index, 1)
            continue
        }
        checkCategoryEnable(currentLevel + 1, children)
        if (children.length === 0) {
            records.splice(index, 1)
            continue
        }
        record.children = children
        index++
    }

    return records
}
