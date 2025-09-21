<template>
    <release-title title="批量设置" />
    <div class="table-container">
        <el-table :data="[{}]">
            <el-table-column label="sku图" align="center">
                <template #default>
                    <!-- <q-upload
                            :width="50"
                            :height="50"
                            append-to-body
                            :format="{ types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif', 'image/webp'] } as any"
                            @update:src="changeBatchFormSku($event, 'image')"
                        /> -->
                    <div style="width: 100px; height: 52px; cursor: auto" class="selectMaterialStyle">
                        <div class="selectMaterialStyle" style="margin: 0 auto" @click="buttonlFn('image', true, -1)">
                            <span class="selectMaterialStyle__span">+</span>
                        </div>
                    </div>
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
            <el-table-column label="供货价" align="center">
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
            <el-table-column label="重量" align="center">
                <template #default>
                    <el-input-number :precision="0" :controls="false" style="width: 60px" @change="changeBatchFormSku($event, 'weight')" />
                    <div class="input-group__prefix">kg</div>
                </template>
            </el-table-column>
            <el-table-column label="sku销量" align="center">
                <template #default>
                    <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'initSalesVolume')" />
                </template>
            </el-table-column>
            <el-table-column label="限购数量" align="center">
                <template #default>
                    <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'limitNum')" />
                </template>
            </el-table-column>
            <el-table-column label="库存" align="center">
                <template #default>
                    <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'initStock')" />
                </template>
            </el-table-column>
            <el-table-column v-if="submitForm.sellType !== 'CONSIGNMENT'" label="起批数" align="center">
                <template #default>
                    <el-input-number :precision="0" :controls="false" style="width: 70px" @change="changeBatchFormSku($event, 'minimumPurchase')" />
                </template>
            </el-table-column>
        </el-table>
    </div>
    <release-title title="规格明细" />
    <div class="table-container">
        <el-table :data="getSpecList" max-height="500">
            <el-table-column
                v-for="(headerStr, index) in computedSpecHeader.slice(0, 1)"
                :key="index"
                :label="headerStr"
                width="150"
                fixed="left"
                align="center"
            >
                <template #default="{ row, $index }">
                    <!-- <q-upload
                            v-model:src="row.image"
                            :width="50"
                            :height="50"
                            append-to-body
                            :format="{ types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif', 'image/webp'] } as any"
                        /> -->
                    <div v-if="!row.image" class="selectMaterialStyle" style="margin: 0 auto" @click="buttonlFn('image', false, $index)">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img
                        v-else
                        alt=""
                        class="selectMaterialStyle"
                        style="margin: 0 auto"
                        :src="row.image"
                        @click="buttonlFn('image', false, $index)"
                    />
                </template>
            </el-table-column>
            <el-table-column v-for="(classAr, idx) in $prop.classArr" :key="idx" align="center" width="180" :label="classAr.name">
                <template #default="{ row }">
                    {{ Array.isArray(row.specs) ? row.specs?.[idx] : row.specs.name }}
                </template>
            </el-table-column>
            <el-table-column
                v-for="(headerStr, index) in submitForm.sellType !== 'CONSIGNMENT'
                    ? computedSpecHeader.slice(1)
                    : computedSpecHeader.filter((item) => item !== '起批数').slice(1)"
                :key="index"
                align="center"
                width="180"
                :label="headerStr"
            >
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
    </div>
    <!-- 选择素材 e -->
    <selectMaterial
        :dialog-visible="dialogVisible"
        :upload-files="1"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>
<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import type { PropType } from 'vue'
import { NormType, NormListType } from './index'
import QUpload from '@/components/q-upload/q-upload.vue'
import ReleaseTitle from '../ReleaseTitle.vue'
import { ExtraLabelKeyEnum } from './index'
import { FormInject } from '../../types'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const materialConfig = reactive({
    parameterId: '',
    isBatch: false,
    listIndex: -1,
})
// const parameterId = ref('')
const buttonlFn = (val: string, isBatch = false, listIndex: number) => {
    dialogVisible.value = true
    materialConfig.parameterId = val
    materialConfig.isBatch = isBatch
    materialConfig.listIndex = listIndex
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    // if (parameterId.value === 'image')
    if (materialConfig.isBatch === false && materialConfig.listIndex > -1) {
        getSpecList.value[materialConfig.listIndex].image = val || ''
    } else {
        changeBatchFormSku(val, materialConfig.parameterId)
    }
    resetMaterialConfig()
}
const checkedFileLists = (val: string[]) => {
    if (materialConfig.isBatch === false && materialConfig.listIndex > -1) {
        getSpecList.value[materialConfig.listIndex].image = val?.shift() || ''
    } else {
        changeBatchFormSku(val?.shift() || '', materialConfig.parameterId)
    }
    resetMaterialConfig()
}
const resetMaterialConfig = () => {
    materialConfig.isBatch = false
    materialConfig.parameterId = ''
    materialConfig.listIndex = -1
}
// 选择素材 d

const $parent = inject('form') as FormInject
const submitForm = $parent.submitForm
const specHeader = ['sku图', '划线价', '供货价', '重量', '限购类型', '限购数量', '库存类型', '库存', 'sku销量', '起批数']
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
const $Route = useRoute()
const $emit = defineEmits(['changeNormList'])
// const filterHeader = computed(() => {
//     let tempArr = $prop.classArr
//     return tempArr
//         .map((item) => {
//             return item.name
//         })
//         .concat(specHeader)
// })
const injectForm = inject('form') as FormInject
const computedSpecHeader = computed(() => {
    if (submitForm.value.productType === 'REAL_PRODUCT') {
        if ($Route.query.id) {
            return ['sku图', '划线价', '供货价', '起批数', '限购类型', '限购数量', '重量', 'sku销量']
        } else {
            return ['sku图', '划线价', '供货价', '库存', '起批数', '限购类型', '限购数量', '重量', 'sku销量']
        }
    } else if (!$Route.query.id || $Route.query.isCopy) {
        return ['sku图', '划线价', '供货价', '库存类型', '库存', '起批数', '限购类型', '限购数量', '重量', 'sku销量']
    } else {
        return ['sku图', '划线价', '供货价', '起批数', '限购类型', '限购数量', '重量', 'sku销量']
    }
})

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

/**
 * @description: 父级获取List调用方法
 */
const getNormList = () => {
    return getSpecList.value
}

const changeBatchFormSku = (val: any, key: string) => {
    console.log('key', key)
    $emit(
        'changeNormList',
        getSpecList.value.map((item) => {
            if (key === 'limitNum') {
                if (item.limitType !== 'UNLIMITED') item.limitNum = val
            } else if (key === 'initStock') {
                if (item.stockType !== 'UNLIMITED') item.initStock = val
            } else {
                item[key] = val
            }
            return item
        }),
    )
}
defineExpose({
    getNormList,
})
/**
 * @description: 对比
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

<style scoped>
.avatar-uploader .avatar {
    width: 150px;
    height: 50px;
    display: block;
}
</style>

<style lang="scss" scoped>
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

.table-container {
    padding-bottom: 30px;
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
