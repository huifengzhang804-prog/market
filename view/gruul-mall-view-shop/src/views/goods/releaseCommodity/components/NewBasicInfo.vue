<script lang="ts" setup>
import { CascaderProps, CheckboxValueType, ElMessage, FormRules, UploadProps } from 'element-plus'
import { Search, Close } from '@element-plus/icons-vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import CityDistributionCheckbox from '@/q-plugin/cityDistribution/CityDistributionCheckbox.vue'
import FreightCheckBox from '@/q-plugin/freight/FreightCheckBox.vue'
import { ApiSupplierType, ProductLabel } from '../../types'
import {
    doGetSupList,
    doGetPlatformCategory,
    doGetCategory,
    doGetSingleCommodity,
    doGetCopyGoods,
    getBrandList,
    doGetLabelList,
    checkCategoryEnable,
    GetStoreList,
} from '@/apis/good'

//引入api获取同城配送的数据
import type { Ref } from 'vue'
import { FormInject } from '../types'
import ReleaseTitle from './ReleaseTitle.vue'
import useServiceAssuranceList from './hooks/useServiceAssuranceList'
// 选择素材 e
import SelectMaterial from '@/views/material/selectMaterial.vue'

const dialogVisibles = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisibles.value = val
}
const parameterId = ref('')
const buttonlFn = (val: string) => {
    dialogVisibles.value = true
    parameterId.value = val
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    if (parameterId.value === 'widePic') submitForm.value.widePic = val
    else if (parameterId.value === 'videoUrl') submitForm.value.videoUrl = val
}
const checkedFileLists = (val: string[]) => {
    if (parameterId.value === 'widePic') submitForm.value.widePic = val?.shift() || ''
    else if (parameterId.value === 'videoUrl') submitForm.value.videoUrl = val?.shift() || ''
}
const videoLimit = ref(false)
watch(
    () => parameterId.value,
    (val) => {
        if (val === 'videoUrl') videoLimit.value = true
        else videoLimit.value = false
    },
)
// 选择素材 d
// 选择素材 e
const dialogVisiblesHost = ref(false)
const selectMaterialFnHost = (val: boolean) => {
    if (commodityImgList.value.length > 6) return
    dialogVisiblesHost.value = val
}
const indexs = ref<number>(-1)
let num = 0
const buttonlFnHost = (val: number) => {
    dialogVisiblesHost.value = true
    indexs.value = val
    num++
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChangeHost = (val: string) => {
    if (num === 0) commodityImgList.value = [val]
    else commodityImgList.value[indexs.value] = val
    // commodityImgList.value.push(val)
    submitForm.value.albumPics = arrToString(commodityImgList)
}
const checkedFileListsHost = (val: string[]) => {
    if (num === 0) {
        commodityImgList.value = val
    } else if (Array.isArray(val)) {
        val.forEach((item) => {
            commodityImgList.value[indexs.value++] = item
        })
    } else {
        commodityImgList.value[indexs.value] = val
    }
    // commodityImgList.value.push(val)
    submitForm.value.albumPics = arrToString(commodityImgList)
}
// 选择素材 d

type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>

interface ShopCategoryList extends ShopCategoryItem {
    disabled?: boolean
    children: ShopCategoryList[]
}
/**
 * variable
 */
const $route = useRoute()
const $router = useRouter()
const currentFormRef = ref()
// @ts-ignore
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'
const $parent = inject('form') as FormInject
const submitForm = $parent.submitForm

const commodityImgList = $parent.commodityImgList
const supplierList = ref<ApiSupplierType[]>([])
const LabelList = ref<ProductLabel[]>([])
const platformCategoryList = ref<ShopCategoryList[]>([])
const shopCategoryList = ref<ShopCategoryList[]>([])
const dialogVisible = ref(false)
const brandName = ref('')
const BrandList = ref<any[]>([])
const BrandPageConfig = reactive({
    size: 1000,
    current: 1,
    total: 0,
})
const BrandRadio = ref('')
const BrandShowName = ref('')
// 回显标识
const copyGoodsTB = $parent.copyGoodsTB
const copyGoodsAL = $parent.copyGoodsAL
const copyGoodsJD = $parent.copyGoodsJD
/**
 * 级联选择器 ref
 */
const platformCategoryRef = ref(null)
const shopCategoryRef = ref(null)

const shopCascaderProps: CascaderProps = {
    expandTrigger: 'hover',
    label: 'name',
    value: 'id',
}
const basicRules: FormRules = {
    name: [
        {
            required: true,
            message: '请填写商品名称',
            trigger: 'blur',
        },
    ],
    categoryId: [
        {
            required: true,
            message: '请选择店铺分类',
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
    distributionMode: [{ required: true, message: '请选择配送方式', trigger: 'change', type: 'array' }],
}

const shopInfo = useShopInfoStore()
const notO2O = computed(() => shopInfo.shopInfo.shopMode !== 'O2O')

const instance = getCurrentInstance()
const $emit = defineEmits(['changeInstance', 'changeSpecificationType'])

onActivated(async () => {
    submitForm.value.labelId = ''
    const forwardPath: string = $router.options.history.state.forward as string
    if (forwardPath !== '/freight/logistics?from=releaseGoods' && !$parent.editProductPreviousStep.value) {
        // 如果是从设置页面回来的不需要再次进行回显
        await dataDisplay()
    }
    // initBrandList()
    Promise.all([getPlatformCategory(), getShopCategory()]).then(() => {
        categoryChange('platformCategory').then(() => {})
        categoryChange('shopCategory').then(() => {})
    })
    getSupplier()
    getLabelList()
    $emit('changeInstance', instance?.refs)
})
const { serviceAssuranceList, updateserviceIds, resetServiceAssuranceList, initServiceAssuranceList } = useServiceAssuranceList(submitForm)
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

watch(
    () => submitForm.value?.id,
    () => {
        initServiceAssuranceList()
        updateserviceIds()
    },
)

const uploadVideoSuccess: UploadProps['onSuccess'] = (response) => {
    submitForm.value.videoUrl = response.data
}
/**
 * 新增商品主图
 * @returns {UploadProps}
 */
const addNewMainSuccess: UploadProps['onSuccess'] = (response) => {
    commodityImgList.value.push(response)
    submitForm.value.albumPics = arrToString(commodityImgList)
}
/**
 * 删除商品主图
 * @param {number} index
 */
const delImgHandle = (index: number) => {
    commodityImgList.value.splice(index, 1)
    submitForm.value.albumPics = arrToString(commodityImgList)
}

/**
 * 数组转字符串
 * @param {Ref<string[]>} arr
 */
function arrToString(arr: Ref<string[]>) {
    return arr.value.join(',')
}

/**
 * 获取供应商
 */
async function getSupplier() {
    const { code, data } = await doGetSupList({
        current: 1,
        size: 1000,
        status: 'REVIEW',
    })
    if (code !== 200) {
        ElMessage.error('获取供应商失败')
        return
    }
    supplierList.value = data.records
}

/**
 * 获取商品标签
 */
async function getLabelList() {
    const { code, data } = await doGetLabelList(shopInfo.shopInfo.shopType)
    if (code !== 200) {
        ElMessage.error('获取商品标签失败')
        return
    }
    LabelList.value = data
    if ($parent.editProductPreviousStep.value) {
        submitForm.value.labelId = $parent.productLabel.value
    }
}

/**
 * 获取平台类目
 */
async function getPlatformCategory() {
    const { code, data } = await doGetPlatformCategory()
    if (code !== 200) {
        ElMessage.error('获取平台分类失败')
        return
    }
    platformCategoryList.value = checkCategoryEnable(1, data)
}

/**
 * 获取店铺分类
 */
async function getShopCategory() {
    const { code, data } = await doGetCategory({
        current: 1,
        size: 500,
    })
    if (code !== 200) {
        ElMessage.error('获取店铺分类失败')
        return
    }
    shopCategoryList.value = checkCategoryEnable(1, data.records)
}

/**
 * 数据回显
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
        submitForm.value = { ...submitForm.value, ...data }
        if (data?.distributionMode?.includes('VIRTUAL')) {
            submitForm.value.distributionMode = ['VIRTUAL']
        }
        isDisabledDistributionWay.value = data?.distributionMode?.includes('VIRTUAL')
    }
}

function imageConversion() {
    let tmp: string[] = []
    if (submitForm.value.albumPics) {
        tmp = submitForm.value.albumPics.split(',')
    }
    commodityImgList.value = tmp
}

const categoryChange = async (categoryType: 'platformCategory' | 'shopCategory') => {
    const iscategoryType = categoryType === 'platformCategory'
    const value = (iscategoryType ? platformCategoryRef.value : shopCategoryRef.value) as any
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

/**
 * 获取品牌列表
 */
async function initBrandList() {
    const { code, data } = await getBrandList({
        ...BrandPageConfig,
        brandName: brandName.value,
    })
    if (code !== 200) {
        ElMessage.error('获取品牌列表失败')
        return
    }
    BrandList.value = data.records
    BrandPageConfig.total = data.total
    data.records.forEach((item: any) => {
        if (item.id === submitForm.value.brandId) {
            BrandShowName.value = item.brandName
            BrandRadio.value = item.id
        }
    })
}

const Searchlist = () => {
    BrandPageConfig.current = 1
    initBrandList()
}
const handleClose = () => {
    dialogVisible.value = false
}

const handleChoose = () => {
    submitForm.value.brandId = BrandRadio.value
    BrandList.value.forEach((item) => {
        if (item.id === BrandRadio.value) {
            BrandShowName.value = item.brandName
        }
    })
    dialogVisible.value = false
}

/*
 * 商品一键复制
 */
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

const isDisabledDistributionWay = ref(false)
const changeDistributionMode = (distributionMode: CheckboxValueType[]) => {
    if (distributionMode.includes('VIRTUAL')) {
        submitForm.value.distributionMode = ['VIRTUAL']
    }
    isDisabledDistributionWay.value = distributionMode.includes('VIRTUAL')
}

defineExpose({
    currentFormRef,
    resetServiceAssuranceList,
})
const handleSortCommodityList = () => {
    submitForm.value.albumPics = arrToString(commodityImgList)
}
const templateIdChange = (templateId: string) => {
    submitForm.value.freightTemplateId = templateId
}
const handleChangeProductType = (val: string) => {
    if (val === 'REAL_PRODUCT') {
        submitForm.value.skus?.forEach((sku) => (sku.stockType = 'LIMITED'))
    } else {
        submitForm.value.distributionMode = []
        submitForm.value.freightTemplateId = '0'
    }
}
/**
 * 获取并且判断是否是视频格式
 * @param { any } 接口传入的地址，可能为数组字符串也可能为普通字符串
 */
const doGetRealVideoUrl = (url: any) => {
    let videoArray: string[] = []
    if (Array.isArray(url)) {
        videoArray = url
    } else {
        try {
            videoArray = JSON.parse(url)
        } catch (err) {
            videoArray = [url]
        }
    }
    const realUrl = videoArray.shift() || ''
    const videoExtensions = ['mp4', 'webm', 'ogg', 'avi', 'mkv', 'mov', 'flv', 'wmv']
    const extension = realUrl.split('.').pop()?.toLowerCase() || ''
    return {
        isVideo: videoExtensions.includes(extension),
        realUrl,
    }
}

/**
 * 获取门店列表
 */
const storeList = ref([{}])
async function getStoreList() {
    const { code, data } = await GetStoreList()
    if (code === 200 && data) {
        storeList.value = data.records.filter((value: any) => {
            return value.status === 'NORMAL'
        })
    } else {
        ElMessage.error('获取列表失败')
    }
}
getStoreList()
</script>
<template>
    <ReleaseTitle title="基本信息" />
    <el-form ref="currentFormRef" :model="submitForm" :rules="basicRules">
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="商品类型" label-width="100px">
                    <el-select v-model="submitForm.productType" :disabled="!!$route.query.id" class="inputWidth" @change="handleChangeProductType">
                        <el-option label="实物商品（快递/同城/自提）" value="REAL_PRODUCT" />
                        <el-option v-if="notO2O" label="虚拟商品（无需物流）" value="VIRTUAL_PRODUCT" />
                    </el-select>
                    <el-tooltip content="不同的商品类型可编辑的字段内容不同，商品类型一旦发布后将不可更改！" effect="effect-tooltip" placement="top">
                        <QIcon name="icon-wenhao1" color="#999" size="16px" style="margin-left: 14px" />
                    </el-tooltip>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="商品名称" label-width="100px" prop="name">
                    <el-input v-model="submitForm.name" class="inputWidth" maxlength="35" placeholder="请填写商品名称"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="平台类目" label-width="100px" prop="platformCategoryId">
                    <template #default>
                        <el-cascader
                            ref="platformCategoryRef"
                            v-model="submitForm.platformCategoryId"
                            :disabled="!(!$route.query.id || $route.query.isCopy)"
                            :options="platformCategoryList"
                            :props="shopCascaderProps"
                            class="inputWidth"
                            clearable
                            placeholder="请选择平台类目"
                            show-all-levels
                            style="width: calc(100% - 120px)"
                            @change="() => categoryChange('platformCategory')"
                            @focus="
                                () => {
                                    if (platformCategoryList.length === 0) {
                                        ElMessage.error('请在“通用设置/类目设置”中添加签约类目')
                                        return
                                    }
                                }
                            "
                        >
                        </el-cascader>
                    </template>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="卖点描述" label-width="100px" prop="saleDescribe">
                    <el-input v-model="submitForm.saleDescribe" class="inputWidth" maxlength="13" placeholder="请填写卖点描述"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="店铺类目" label-width="100px" prop="categoryId">
                    <template #default>
                        <el-cascader
                            ref="shopCategoryRef"
                            v-model="submitForm.categoryId"
                            :options="shopCategoryList"
                            :props="shopCascaderProps"
                            class="inputWidth"
                            clearable
                            placeholder="请选择店铺类目"
                            show-all-levels
                            style="width: calc(100% - 120px)"
                            @change="() => categoryChange('shopCategory')"
                            @focus="
                                () => {
                                    if (shopCategoryList.length === 0) {
                                        ElMessage.error('请确保类目完整，商品需挂载至三级类目')
                                        return
                                    }
                                }
                            "
                        >
                        </el-cascader>
                        <el-link
                            :underline="false"
                            style="margin-left: 15px"
                            type="primary"
                            @click="
                                $router.push({
                                    name: 'goodsCategory',
                                    query: {
                                        from: 'releaseGoods',
                                    },
                                })
                            "
                        >
                            前往设置
                        </el-link>
                    </template>
                </el-form-item>
            </el-col>
            <el-col v-if="!$route.query.id" :span="12">
                <el-form-item label="淘宝一键采集" label-width="100px">
                    <el-input v-model="copyGoodsTB" class="inputWidth" clearable></el-input>
                    <el-link
                        :disabled="!copyGoodsTB"
                        :underline="false"
                        style="margin-left: 15px"
                        type="primary"
                        @click="copyGoods('TaoBao', copyGoodsTB)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
            <el-col v-if="!$route.query.id" :span="12">
                <el-form-item label="1688一键采集" label-width="100px">
                    <el-input v-model="copyGoodsAL" class="inputWidth" clearable></el-input>
                    <el-link
                        :disabled="!copyGoodsAL"
                        :underline="false"
                        style="margin-left: 15px"
                        type="primary"
                        @click="copyGoods('AliBaBa', copyGoodsAL)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
            <el-col v-if="!$route.query.id" :span="12">
                <el-form-item label="京东一键采集" label-width="100px">
                    <el-input v-model="copyGoodsJD" class="inputWidth" clearable></el-input>
                    <el-link
                        :disabled="!copyGoodsJD"
                        :underline="false"
                        style="margin-left: 15px"
                        type="primary"
                        @click="copyGoods('JD', copyGoodsJD)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="自有货源" label-width="100px" prop="providerId">
                    <el-select v-model="submitForm.providerId" class="inputWidth" placeholder="请选择供应商" style="">
                        <el-option v-for="item in supplierList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                    <el-link
                        :underline="false"
                        style="margin-left: 15px"
                        type="primary"
                        @click="
                            $router.push({
                                name: 'goodsSupplier',
                                query: {
                                    from: 'releaseGoods',
                                },
                            })
                        "
                    >
                        前往设置
                    </el-link>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="商品标签" label-width="100px">
                    <el-select v-model="submitForm.labelId" class="inputWidth" placeholder="请选择商品标签">
                        <el-option v-for="item in LabelList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="商品视频" label-width="100px">
                    <el-row style="width: 100%">
                        <div v-if="!submitForm.videoUrl" alt="" class="selectMaterialStyle" @click="buttonlFn('videoUrl')">
                            <span class="selectMaterialStyle__span">+</span>
                        </div>
                        <video
                            v-else-if="doGetRealVideoUrl(submitForm.videoUrl).isVideo"
                            controls
                            class="selectMaterialStyle"
                            :src="doGetRealVideoUrl(submitForm.videoUrl).realUrl"
                            @click="buttonlFn('videoUrl')"
                        ></video>
                        <div v-if="submitForm.videoUrl" class="delete_video_icon">
                            <el-button text :icon="Close" circle @click="submitForm.videoUrl = ''" />
                        </div>
                    </el-row>
                    <div style="color: #9d9d9d; font-size: 14px">大小为10M以下</div>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="商品主图" label-width="100px" prop="albumPics">
            <el-row style="width: 100%">
                <VueDraggableNext v-if="commodityImgList.length" v-model="commodityImgList" style="display: flex" @sort="handleSortCommodityList">
                    <div v-for="(item, index) in commodityImgList" :key="index" style="position: relative; margin-right: 20px">
                        <img
                            :src="commodityImgList[index]"
                            alt=""
                            class="selectMaterialStyle"
                            style="width: 105px; height: 105px"
                            @click="buttonlFnHost(index)"
                        />
                        <el-icon
                            v-if="item"
                            color="#7f7f7f"
                            size="20px"
                            style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%; cursor: pointer"
                            @click="delImgHandle(index)"
                        >
                            <i-ep-circle-close />
                        </el-icon>
                        <div v-if="index === 0" class="com__imgText">封面图</div>
                    </div>
                </VueDraggableNext>
                <div
                    v-if="commodityImgList.length !== 6"
                    class="selectMaterialStyle"
                    style="width: 100px; height: 100px"
                    @click="buttonlFnHost(commodityImgList.length)"
                >
                    <span class="selectMaterialStyle__span">+</span>
                </div>
            </el-row>
            <div style="color: rgba(69, 64, 60, 0.6); font-size: 12px">
                尺寸建议 750x750、750x1125 像素以上，大小1M以下，最多6张 (可拖拽图片调整顺序)
            </div>
        </el-form-item>
        <div v-if="submitForm.productType === 'REAL_PRODUCT'">
            <!-- 虚拟商品只能选择无需物流 -->
            <ReleaseTitle title="物流信息" />
            <el-form-item label="配送方式（至少选一种）" label-width="200px" required style="margin: 0"> </el-form-item>
            <el-form-item label="" label-width="100px" prop="distributionMode">
                <el-checkbox-group v-model="submitForm.distributionMode" style="width: 100%" @change="changeDistributionMode">
                    <CityDistributionCheckbox />
                    <br />
                    <el-checkbox value="SHOP_STORE" :disabled="storeList.length === 0">到店自提</el-checkbox>
                    <br />
                    <FreightCheckBox
                        v-if="notO2O"
                        :disable="isDisabledDistributionWay"
                        :template-change="templateIdChange"
                        :templateId="`${submitForm.freightTemplateId}`"
                    />
                </el-checkbox-group>
            </el-form-item>
        </div>
        <release-title title="服务保障" />
        <el-form-item class="mleft-form-item" prop="serviceIds">
            <div v-for="item in serviceAssuranceList" :key="item.name" style="display: flex; flex-direction: column">
                <el-checkbox v-model="item.state" @change="updateserviceIds">
                    <div class="serveMsg">
                        <span style="width: 120px">{{ item.name }}</span>
                        <span style="color: #c6c6c6">{{ item.text }}</span>
                    </div>
                </el-checkbox>
            </div>
        </el-form-item>
    </el-form>
    <el-dialog v-model="dialogVisible" title="选择品牌" width="644px" destroy-on-close>
        <div style="display: flex; justify-content: end">
            <el-input v-model="brandName" maxlength="6" placeholder="请输入品牌名称" style="width: 208px">
                <template #append>
                    <el-button :icon="Search" @click="Searchlist"></el-button>
                </template>
            </el-input>
        </div>
        <div class="brandTable">
            <div class="brandTable__head">
                <div style="margin-left: 44px">品牌名称</div>
                <div style="margin-left: 166px">状态</div>
                <div style="margin-left: 214px">图片</div>
            </div>
            <el-radio-group v-model="BrandRadio">
                <el-radio
                    v-for="item in BrandList"
                    :key="item.id"
                    :label="item.id"
                    style="height: 70px; border-bottom: 1px solid #e6e6e6; margin-left: 15px"
                >
                    <div class="brandTable__content">
                        <div class="brandTable__content--item1">
                            {{ item.brandName }}
                        </div>
                        <div :style="item.status === 'SELL_ON' ? 'background: #f5faf3;color: #82c26b;' : ''" class="brandTable__content--item2">
                            {{ item.status === 'SELL_ON' ? '已上架' : '已下架' }}
                        </div>
                        <el-image :src="item.brandLogo" class="brandTable__content--item3"></el-image>
                    </div>
                </el-radio>
            </el-radio-group>
        </div>
        <template #footer>
            <el-button @click="handleClose">取 消</el-button>
            <el-button type="primary" @click="handleChoose">确 定</el-button>
        </template>
    </el-dialog>
    <!-- 商品大图 商品视频 选择素材 e -->
    <SelectMaterial
        :dialog-visible="dialogVisibles"
        :upload-files="1"
        :video-limit="videoLimit"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 商品大图 商品视频 选择素材 d -->
    <!-- 商品主图 选择素材 e -->
    <SelectMaterial
        :dialog-visible="dialogVisiblesHost"
        :upload-files="6 - commodityImgList.length"
        @select-material-fn="selectMaterialFnHost"
        @cropped-file-change="croppedFileChangeHost"
        @checked-file-lists="checkedFileListsHost"
    />
    <!-- 商品主图 选择素材 d -->
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@import '@/assets/css/goods/goodMultiSpec.scss';
.FreightTemplateChoose {
    position: absolute;
}
:deep(.el-tag__content) {
    max-width: 214px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

@include b(brand) {
    width: calc(100% - 120px);
    border: 1px solid #d5d5d5;
    border-radius: 2px;
    font-size: 12px;
    color: #2e99f3;
    height: 30px;
    line-height: 30px;
    padding: 0 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

@include b(brandTable) {
    border: 1px solid #e6e6e6;
    margin-top: 15px;
    max-height: 450px;
    overflow: hidden;
    overflow-y: auto;
    @include e(head) {
        height: 30px;
        background: #f6f6f6;
        display: flex;
        font-size: 14px;
        color: #6a6a6a;
        line-height: 30px;
    }
    @include e(content) {
        display: flex;
        align-items: center;
        width: 580px;
        @include m(item1) {
            margin-left: 24px;
            width: 198px;
            color: #6a6a6a;
            @include utils-ellipsis();
        }
        @include m(item2) {
            background: #fef6f3;
            border-radius: 4px;
            font-size: 14px;
            width: 76px;
            height: 26px;
            line-height: 26px;
            color: #f4a584;
            text-align: center;
        }
        @include m(item3) {
            margin-left: 179px;
            width: 50px;
            height: 50px;
        }
    }
}

@include b(inputWidth) {
    width: calc(100% - 120px);
}

@include b(com) {
    @include e(attr) {
        margin-top: 10px;
        width: 620px;
        padding: 20px 20px;
        border: 1px solid #d7d7d7;
    }
    @include e(attr-header) {
        @include flex(space-between);
    }
    @include e(attr-content) {
        @include flex(space-between);
        margin: 10px 0;
    }
    @include e(attr-input) {
        width: 230px;
    }
    @include e(attr-del) {
        color: red;
    }
    @include e(imgText) {
        position: absolute;
        right: 0;
        bottom: 0;
        font-size: 12px;
        text-align: center;
        width: 100%;
        background-color: rgba(0, 0, 0, 0.3);
        border-radius: 0 0 6px 6px;
        color: #fff;
    }
}

.copyGoods {
    width: 72px;
    height: 30px;
    background: #2e99f3;
    border-radius: 2px;
    font-size: 14px;
    color: #fff;
    text-align: center;
    line-height: 30px;
    cursor: pointer;
}

.goodsType {
    .title {
        font-weight: 400;
        font-size: 16px;
        margin-bottom: 20px;
    }

    .is-active {
        .title {
            font-weight: 700;
        }
    }
}
// @Override css
.el-link.is-disabled {
    color: #999;
}
@include b(selectMaterialStyle) {
    width: 255px;
    height: 120px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 20px;
    }
}
</style>

<style>
.mleft-form-item .el-form-item__content {
    padding-left: 100px;
}
.el-popper.is-effect-tooltip {
    background-color: #fff;
    color: #9d9d9d;
    box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.15);
}
.el-popper.is-effect-tooltip .el-popper__arrow::before {
    background: #fff;
    right: 0;
}
.goodsType .el-radio-button__original-radio:checked + .el-radio-button__inner {
    color: #333;
    background-color: unset;
}

.goodsType .el-radio-button__inner {
    padding: 20px 40px;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.main-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 100px;
    height: 100px;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.main-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 250px;
    height: 120px;
    text-align: center;
}

.el-icon.main-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
}
</style>
