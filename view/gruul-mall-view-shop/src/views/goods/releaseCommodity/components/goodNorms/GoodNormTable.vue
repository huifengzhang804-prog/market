<template>
    <div>
        <!-- 批量设置 -->
        <release-title title="批量设置" />
        <div class="table-container">
            <el-table :data="[{}]">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column label="sku图" align="center">
                    <template #default>
                        <!-- <q-upload
                            :width="50"
                            :height="50"
                            append-to-body
                            :format="{ types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif', 'image/webp'] }"
                            @update:src="changeBatchFormSku($event, 'image')"
                        /> -->

                        <div class="selectMaterialStyle" style="margin: 0 auto" @click="buttonlFn('image')">
                            <span class="selectMaterialStyle__span">+</span>
                        </div>
                        <!-- <img alt="" class="selectMaterialStyle" @update:src="changeBatchFormSku($event, 'image')" @click="buttonlFn('image')" /> -->
                    </template>
                </el-table-column>
                <el-table-column label="划线价" align="center">
                    <template #default>
                        <div class="input-group">
                            <div class="input-group__prefix">￥</div>
                            <el-input-number
                                :max="999999"
                                :precision="2"
                                :controls="false"
                                style="width: 70px"
                                @change="changeBatchFormSku($event, 'price')"
                            />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="销售价" align="center">
                    <template #default>
                        <div class="input-group">
                            <div class="input-group__prefix">￥</div>
                            <el-input-number
                                :max="999999"
                                :precision="2"
                                :controls="false"
                                style="width: 70px"
                                @change="changeBatchFormSku($event, 'salePrice')"
                            />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="库存" align="center">
                    <template #default>
                        <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'initStock')" />
                    </template>
                </el-table-column>
                <el-table-column label="初始销量" align="center">
                    <template #default>
                        <el-input-number
                            :precision="0"
                            :controls="false"
                            style="width: 70px"
                            @change="changeBatchFormSku($event, 'initSalesVolume')"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="限购数量" align="center">
                    <template #default>
                        <el-input-number
                            :precision="0"
                            :controls="false"
                            :min="1"
                            style="width: 70px"
                            @change="changeBatchFormSku($event, 'limitNum')"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="重量" align="center">
                    <template #default>
                        <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'weight')" />
                        <div class="input-group__prefix">kg</div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <release-title title="规格明细" />
        <div class="table-container">
            <el-table :data="getSpecList" max-height="500">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column
                    v-for="(headerStr, index) in computedSpecHeader.slice(0, 1)"
                    :key="index"
                    :label="headerStr"
                    width="150"
                    fixed="left"
                    align="center"
                >
                    <template #default="{ row, column, $index }">
                        <!-- <q-upload
                            v-model:src="row.image"
                            :width="50"
                            :height="50"
                            append-to-body
                            :format="{ types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif', 'image/webp'] }"
                        /> -->

                        <div v-if="!row.image" class="selectMaterialStyle" style="margin: 0 auto" @click="buttonlFn('image', column, $index)">
                            <span class="selectMaterialStyle__span">+</span>
                        </div>
                        <img
                            v-else
                            alt=""
                            class="selectMaterialStyle"
                            style="margin: 0 auto"
                            :src="row.image"
                            @click="buttonlFn('image', column, $index)"
                        />
                    </template>
                </el-table-column>
                <el-table-column v-for="(classAr, idx) in $prop.classArr" :key="idx" align="center" width="180" :label="classAr.name">
                    <template #default="{ row }">
                        {{ Array.isArray(row.specs) ? row.specs?.[idx] : row.specs.name }}
                    </template>
                </el-table-column>
                <el-table-column v-for="(headerStr, index) in computedSpecHeader.slice(1)" :key="index" align="center" width="180" :label="headerStr">
                    <template #default="{ row }">
                        <template v-if="ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum] === 'limitType'">
                            <el-select v-model="row.limitType">
                                <el-option label="不限购" value="UNLIMITED"></el-option>
                                <!-- <el-option label="商品限购" value="PRODUCT_LIMITED"></el-option> -->
                                <el-option label="规格限购" value="SKU_LIMITED"></el-option>
                            </el-select>
                        </template>
                        <template v-else-if="ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum] === 'stockType'">
                            <el-select v-model="row.stockType">
                                <el-option label="有限库存" value="LIMITED"></el-option>
                                <el-option label="无限库存" value="UNLIMITED"></el-option>
                            </el-select>
                        </template>
                        <template v-else-if="ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum] === 'limitNum'">
                            <el-input-number v-model="row.limitNum" :disabled="row.limitType === 'UNLIMITED'" :precision="0" :controls="false" />
                        </template>
                        <template v-else-if="ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum] === 'initStock'">
                            <el-input-number v-if="row.stockType === 'UNLIMITED'" :model-value="0" disabled :precision="0" :controls="false" />
                            <el-input-number v-else v-model="row.initStock" :precision="0" :controls="false" />
                        </template>
                        <template v-else-if="['price', 'salePrice'].includes(ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum])">
                            <el-input-number
                                v-model="row[ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum]]"
                                :controls="false"
                                :max="999999"
                                :min="0"
                                :precision="2"
                            />
                        </template>
                        <template v-else>
                            <el-input-number
                                v-model="row[ExtraLabelKeyEnum[headerStr as keyof typeof ExtraLabelKeyEnum]]"
                                :precision="0"
                                :controls="false"
                            />
                        </template>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 选择素材 e -->
            <selectMaterial
                :dialog-visible="dialogVisible"
                :upload-files="1"
                @select-material-fn="selectMaterialFn"
                @cropped-file-change="croppedFileChange"
                @checked-file-lists="checkedFileLists"
            />
            <!-- 选择素材 d -->
        </div>
        <!-- {{ $props.classArr }}
        <div class="specDetail">
            <div class="specDetail__title">规格明细</div>
            <el-scrollbar max-height="650px" style="position: relative">
                <div class="specDetail__table">
                    <div class="specDetail__table__title">
                        <div v-for="(item, index) in filterHeader" :key="index">
                            <div
                                v-if="
                                    ($Route.name === 'releaseCommodityEdit' && !['库存类型', '设置库存'].includes(item)) ||
                                    $route.name === 'releaseCommodityCopy' ||
                                    !$Route.query.id
                                "
                                :class="[!specHeader.includes(item) ? 'specDetail__table__title--custom' : 'specDetail__table__title--primary']"
                            >
                                {{ item }}
                            </div>
                        </div>
                    </div>
                    <div v-for="(item, index) in getSpecList" :key="index" class="specDetail__table__item">
                        <div v-if="!Array.isArray(item.specs)" class="specDetail__table__item--font">
                            {{ item.specs.name }}
                        </div>
                        <div v-for="(ite, inde) in item.specs" v-else :key="inde" class="specDetail__table__item--font">
                            {{ ite }}
                        </div>
                        <div class="specDetail__table__item--input">
                            <q-upload
                                v-model:src="item.image"
                                :width="50"
                                :height="50"
                                :format="{ types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif', 'image/webp'] }"
                            />
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-input-number v-model="item.price" :controls="false" :max="999999" :min="0" :precision="2" />
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-input-number v-model="item.salePrice" :controls="false" :max="999999" :min="0" :precision="2" />
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-input-number v-model="item.weight" :precision="0" :controls="false" />
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-select v-model="item.limitType">
                                <el-option label="规格限购" value="SKU_LIMITED"></el-option>
                                <el-option label="不限购" value="UNLIMITED"></el-option>
                            </el-select>
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-input-number v-model="item.limitNum" :disabled="item.limitType === 'UNLIMITED'" :precision="0" :controls="false" />
                        </div>
                        <div v-if="$route.name !== 'releaseCommodityEdit'" class="specDetail__table__item--input">
                            <el-select v-model="item.stockType">
                                <el-option label="有限库存" value="LIMITED"></el-option>
                                <el-option label="无限库存" value="UNLIMITED"></el-option>
                            </el-select>
                        </div>
                        <div v-if="$route.name !== 'releaseCommodityEdit'" class="specDetail__table__item--input">
                            <el-input-number v-if="item.stockType === 'UNLIMITED'" :model-value="0" disabled :precision="0" :controls="false" />
                            <el-input-number v-else v-model="item.initStock" :precision="0" :controls="false" />
                        </div>
                        <div class="specDetail__table__item--input">
                            <el-input-number v-model="item.initSalesVolume" :precision="0" :controls="false" />
                        </div>
                    </div>
                </div>
            </el-scrollbar>
        </div> -->
    </div>
</template>
<script lang="ts" setup>
import { useRoute } from 'vue-router'
import type { PropType } from 'vue'
import { NormType, NormListType } from './index'
import QUpload from '@/components/q-upload/q-upload.vue'
import { ElMessage, UploadProps } from 'element-plus'
import ReleaseTitle from '../ReleaseTitle.vue'
import { cloneDeep } from 'lodash-es'
import { ExtraLabelKeyEnum } from './index'
import { FormInject } from '../../types'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const itemImage = ref<number>()
const buttonlFn = (val: string, column?: any, index?: number) => {
    dialogVisible.value = true
    parameterId.value = val
    itemImage.value = index
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    // if (parameterId.value === 'image')
    changeBatchFormSku(val, parameterId.value)
}
const checkedFileLists = (val: string[]) => {
    changeBatchFormSku(val?.shift() || '', parameterId.value)
}
// 选择素材 d

const injectForm = inject('form') as FormInject
const $Route = useRoute()
const specHeader = ['sku图', '划线价', '销售价', '重量', '限购类型', '限购数量', '库存类型', '库存', '初始销量']
const computedSpecHeader = computed(() => {
    if ($Route.name === 'releaseCommodityEdit') {
        return ['sku图', '划线价', '销售价', '限购类型', '限购数量', '重量', '初始销量']
    } else if (injectForm.submitForm.value.productType === 'REAL_PRODUCT') {
        return ['sku图', '划线价', '销售价', '库存', '限购类型', '限购数量', '重量', '初始销量']
    } else {
        return ['sku图', '划线价', '销售价', '库存类型', '库存', '限购类型', '限购数量', '重量', '初始销量']
    }
})
const $prop = defineProps({
    list: {
        type: Array as PropType<NormListType[]>,
        default() {
            return []
        },
    },
    classArr: {
        type: Array as PropType<NormType[]>,
        default() {
            return []
        },
    },
    memoSpecList: {
        type: Array as PropType<NormListType[]>,
        default() {
            return []
        },
    },
})
console.log('$Route.query', $Route.query)
const $emit = defineEmits(['changeNormList'])
const batchSettingInventory = ref(0)
const productSkuIndex = ref(0)
const filterHeader = computed(() => {
    let tempArr = $prop.classArr
    const mutiSpecs = tempArr.map((item) => {
        return item.name
    })
    const specHeaderClone = cloneDeep(specHeader)
    specHeaderClone.splice(1, 0, ...mutiSpecs)
    return specHeaderClone
    // return tempArr
    // .map((item) => {
    //     return item.name
    // })
    // .concat(specHeader)
})
// 批量设置规格
const batchOriginalPrice = ref(0)
const batchPrice = ref(0)
const batchStock = ref(0)
const batchWeight = ref(0)
const batchPerLimit = ref(0)

const getSpecList = computed(() => {
    const memoSpecList = $prop.memoSpecList
    let list = $prop.list
    if (memoSpecList.length > 0) {
        list = list.map((item, index) => {
            const tempArr = memoSpecList.filter((ite) => (equar(item.specs, ite.specs) ? true : false))
            if (tempArr.length > 0) item = tempArr[0]
            return item
        })
    }
    return list
})

const beforeImgUpload: UploadProps['beforeUpload'] = (rawFile) => {
    const whiteList = ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif']
    const isLt1M = rawFile.size < 2 * 1024 * 1024
    if (!whiteList.includes(rawFile.type)) {
        ElMessage.error('上传文件只能是 JPG或PNG 格式!')
        return false
    }
    if (!isLt1M) {
        ElMessage.error('上传文件大小不能超过 2MB!')
        return false
    }
    return true
}

const uploadImgSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    const list = $prop.list
    list[productSkuIndex.value].image = response.data
    $emit('changeNormList', list)
}
/**
 * 父级获取List调用方法
 */
const getNormList = () => {
    return getSpecList.value
}

const changeBatchFormSku = (val: any, key: string) => {
    let emitData: any = null
    parameterId.value = key
    console.log(parameterId.value === 'image' && (itemImage.value || itemImage.value === 0), key, 'initSalesVolume')
    if (parameterId.value === 'image' && (itemImage.value || itemImage.value === 0)) {
        getSpecList.value[itemImage.value].image = val
        emitData = getSpecList.value
    } else {
        emitData = getSpecList.value.map((item) => {
            item[key] = val
            return item
        })
    }
    $emit('changeNormList', emitData)
}
/**
 * 批量设置划线价
 * @param {number | string} e
 */
const batchSetOriginal = (e: number) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        item.price = e
        return item
    })
    $emit('changeNormList', tempArr)
}
/**
 * 批量设置销售价
 * @param {number | string} e
 */
const batchSetPrice = (e: string) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        item.salePrice = e
        return item
    })
    $emit('changeNormList', tempArr)
}
/**
 * 批量设置销量
 * @param {number | string} e
 */
const batchSetSales = (e: number) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        item.initSalesVolume = e
        return item
    })
    $emit('changeNormList', tempArr)
}
/**
 * 批量设置重量
 * @param {number | string} e
 */
const batchSetWeight = (e: number) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        item.weight = e
        return item
    })
    $emit('changeNormList', tempArr)
}
/**
 * 批量设置限购
 * @param {number | string}
 */
const batchSetPerlimit = (e: number) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        if (item.limitType !== 'UNLIMITED') item.limitNum = e
        return item
    })
    $emit('changeNormList', tempArr)
}
/**
 * 批量设置库存
 * @param {number | string}
 */
const batchSettingInventoryFn = (e: number) => {
    let tempArr = []
    tempArr = getSpecList.value.map((item) => {
        if (item.stockType !== 'UNLIMITED') item.initStock = e
        return item
    })
    $emit('changeNormList', tempArr)
}
defineExpose({
    getNormList,
})
/**
 * 对比
 * @param {string | string[]} a
 * @param {string | string[]} b
 */
function equar(a: string[] | string, b: string[] | string) {
    if (a.length !== b.length) {
        return false
    } else {
        // 循环遍历数组的值进行比较
        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                return false
            }
        }
        return true
    }
}
</script>

<style lang="scss" scoped>
.avatar-uploader .avatar {
    width: 150px;
    height: 50px;
    display: block;
}
.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 50px;
    height: 50px;
    text-align: center;
    border: 1px dashed #ccc;
    border-radius: 10px;
}
.input-group {
    display: flex;
    align-items: center;
    &__prefix {
        background-color: #f7f7f7;
        height: 32px;
        line-height: 32px;
        padding: 0 9px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        display: inline-block;
    }
}
.table-container {
    padding-bottom: 30px;
}
:deep(.qupload) {
    width: fit-content;
    margin: 0 auto;
}
@include b(selectMaterialStyle) {
    width: 54px;
    height: 54px;
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
