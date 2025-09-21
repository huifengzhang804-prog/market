<script lang="ts" setup>
import LogisticsSetting from '@/views/goods/releaseCommodity/components/logistics-setting.vue'
import ChooseBrand from './ChooseBrand.vue'
import useBasicInfo from '../hooks/useBasicInfo'
import ReleaseTitle from './ReleaseTitle.vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import useServiceAssuranceList from './hooks/useServiceAssuranceList'
import { ElMessage } from 'element-plus'

// 选择素材 e
import SelectMaterial from '@/views/material/selectMaterial.vue'
import { Ref } from 'vue'
const dialogVisibles = ref(false)
const $emit = defineEmits(['changeInstance'])
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
const indexs = ref<number>()
let num = 0
const buttonlFnHost = (val?: number) => {
    dialogVisiblesHost.value = true
    indexs.value = val
    num++
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const HostPicList = ref<string[]>([])
const croppedFileChangeHost = (val: any) => {
    if (num === 0) commodityImgList.value = [val]
    else commodityImgList.value[indexs.value] = val
    // commodityImgList.value.push(val)
    submitForm.value.albumPics = arrToString(commodityImgList)
}
const checkedFileListsHost = (val: any) => {
    if (num === 0) {
        commodityImgList.value = val
    } else if (Array.isArray(val)) {
        val.forEach((item) => {
            commodityImgList.value[indexs.value++] = item
        })
    } else commodityImgList.value[indexs.value] = val
    // commodityImgList.value.push(val)
    submitForm.value.albumPics = arrToString(commodityImgList)
}
/**
 * @description: 数组转字符串
 * @param {Ref<string[]>} arr
 */
function arrToString(arr: Ref<string[]>) {
    return arr.value.join(',')
}
const handleChangeProductType = (val: string) => {
    if (val === 'REAL_PRODUCT') {
        submitForm.value.skus?.forEach((sku) => (sku.stockType = 'LIMITED'))
    }
}
// 选择素材 d
const {
    copyGoodsAL,
    copyGoodsJD,
    copyGoodsTB,
    submitForm,
    commodityImgList,
    currentFormRef,
    logisticsData,
    platformCategoryList,
    dialogVisible,
    handleChoose,
    chooseBrandRef,
    basicRules,
    copyGoods,
    platformCategoryRef,
    categoryChange,
    handleSortCommodityList,
    delImgHandle,
    shopCascaderProps,
    changeSellType,
} = useBasicInfo()
const { serviceAssuranceList, updateserviceIds, resetServiceAssuranceList, initServiceAssuranceList } = useServiceAssuranceList(submitForm)
const instance = getCurrentInstance()

const $route = useRoute()
const $router = useRouter()

defineExpose({
    currentFormRef,
    resetServiceAssuranceList,
})

onActivated(() => {
    $emit('changeInstance', instance?.refs)
})

watch(
    () => submitForm.value.id,
    () => {
        initServiceAssuranceList()
        updateserviceIds()
    },
)

/**
 * @description 获取并且判断是否是视频格式
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
</script>
<template>
    <ReleaseTitle title="基本信息" />
    <el-form ref="currentFormRef" :model="submitForm" :rules="basicRules">
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="销售方式" label-width="100px">
                    <el-select v-model="submitForm.sellType" class="inputWidth" :disabled="!!$route.query.id" @change="changeSellType">
                        <el-option value="PURCHASE" label="采购商品（供各店铺采购）" />
                        <el-option value="CONSIGNMENT" label="代销商品（供各店铺代销）" />
                    </el-select>
                    <el-tooltip content="采购商品可供各店铺采购，代销商品可以让各店铺帮您将商品销售出去" placement="top" effect="effect-tooltip">
                        <el-icon size="14px" color="#999" style="margin-left: 24px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="商品类型" label-width="100px" :disabled="!!$route.query.id">
                    <el-select v-model="submitForm.productType" class="inputWidth" :disabled="!!$route.query.id" @change="handleChangeProductType">
                        <el-option value="REAL_PRODUCT" label="实物商品（快递/同城/自提）" />
                        <el-option v-if="submitForm.sellType !== 'CONSIGNMENT'" value="VIRTUAL_PRODUCT" label="虚拟商品（无需物流）" />
                    </el-select>
                    <el-tooltip content="不同的商品类型可编辑的字段内容不同，商品类型一旦发布后将不可更改！" placement="top" effect="effect-tooltip">
                        <el-icon size="14px" color="#999" style="margin-left: 24px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item label="商品名称" prop="name" label-width="100px">
                    <el-input v-model="submitForm.name" class="inputWidth" placeholder="请填写商品名称" maxlength="35" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="卖点描述" prop="saleDescribe" label-width="100px">
                    <el-input v-model="submitForm.saleDescribe" class="inputWidth" placeholder="请填写卖点描述" maxlength="13"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="平台类目" prop="platformCategoryId" label-width="100px">
                    <template #default>
                        <el-cascader
                            ref="platformCategoryRef"
                            v-model="submitForm.platformCategoryId"
                            clearable
                            :disabled="!(!$route.query.id || $route.query.isCopy)"
                            style="width: 350px"
                            :options="platformCategoryList"
                            :props="shopCascaderProps"
                            placeholder="请选择平台类目"
                            show-all-levels
                            @change="() => categoryChange('platformCategory')"
                            @focus="
                                () => {
                                    if (platformCategoryList.length === 0) {
                                        ElMessage.error('请在“通用设置/类目设置”中添加签约类目')
                                        return
                                    }
                                }
                            "
                        />
                    </template>
                </el-form-item>
            </el-col>
            <!-- <el-col :span="12">
                <el-form-item label="商品品牌" label-width="100px">
                    <div class="brand">
                        <el-tag v-if="submitForm.brandId" closable @close="closeTag">
                            {{ BrandShowName }}
                        </el-tag>
                        <div v-else></div>
                        <-- <div style="cursor: pointer" @click="dialogVisible = true">选择+</div> --\>
                    </div>
                    <el-link type="primary" :underline="false" style="margin-left: 15px" @click="handleChooseBrand">选择+</el-link>
                </el-form-item>
            </el-col> -->
            <el-col :span="12">
                <el-form-item v-if="!$route.query.id" label="淘宝一键采集" label-width="100px">
                    <el-input v-model="copyGoodsTB" class="inputWidth" clearable></el-input>
                    <el-link
                        type="primary"
                        :disabled="!copyGoodsTB"
                        :underline="false"
                        style="margin-left: 15px"
                        @click="copyGoods('TaoBao', copyGoodsTB)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item v-if="!$route.query.id" label="1688一键采集" label-width="100px">
                    <el-input v-model="copyGoodsAL" class="inputWidth" clearable></el-input>
                    <el-link
                        type="primary"
                        :disabled="!copyGoodsAL"
                        :underline="false"
                        style="margin-left: 15px"
                        @click="copyGoods('AliBaBa', copyGoodsAL)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item v-if="!$route.query.id" label="京东一键采集" label-width="100px">
                    <el-input v-model="copyGoodsJD" class="inputWidth" clearable></el-input>
                    <el-link
                        type="primary"
                        :disabled="!copyGoodsJD"
                        :underline="false"
                        style="margin-left: 15px"
                        @click="copyGoods('JD', copyGoodsJD)"
                        >立即获取</el-link
                    >
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="商品视频" label-width="100px">
                    <div>
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
                        <div style="color: #9d9d9d; font-size: 14px">大小为10M以下</div>
                    </div>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="商品主图" prop="albumPics" label-width="100px">
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
            <release-title title="物流信息" />
            <el-form-item label="快递配送" label-width="100px" prop="distributionMode">
                <!-- 快递配送 -->
                <span style="margin: 0 10px" @click.stop>运费模板选择</span>
                <el-select v-model="submitForm.freightTemplateId" class="inputWidth" placeholder="请选择运费模板">
                    <el-option label="商家包邮" :value="0"></el-option>
                    <el-option v-for="item in logisticsData" :key="item.id" :label="item.templateName" :value="item.id"></el-option>
                </el-select>
                <el-link
                    type="primary"
                    :underline="false"
                    style="margin-left: 15px"
                    @click.stop.prevent="
                        $router.push({
                            name: 'freightLogisticsIndex',
                            query: {
                                from: 'releaseGoods',
                            },
                        })
                    "
                >
                    前往设置
                </el-link>

                <!-- <LogisticsSetting v-if="submitForm.freightTemplateId !== 0" :freightTemplateId="submitForm.freightTemplateId" :data="logisticsData" /> -->
            </el-form-item>
        </div>
        <ReleaseTitle title="服务保障" />
        <el-form-item class="mleft-form-item">
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
        <choose-brand ref="chooseBrandRef" :brand-id="submitForm.brandId" />
        <template #footer>
            <el-button @click="dialogVisible = false">取 消</el-button>
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
    width: 350px;
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
.goodsType .el-radio-button__original-radio:checked + .el-radio-button__inner {
    color: #333;
    background-color: unset;
}
.goodsType .el-radio-button__inner {
    padding: 20px 40px;
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
