<script lang="ts" setup>
import GoodNorms from './goodNorms/GoodNorms.vue'
import GoodNormTable from './goodNorms/GoodNormTable.vue'
import GoodOnly from './goodNorms/GoodOnly.vue'
import { NormListType, NormType } from './goodNorms/index'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetCommoditySku } from '@/apis/good'
import ReleaseTitle from './ReleaseTitle.vue'
import { FormInject } from '../types'

const $route = useRoute()
const { divTenThousand } = useConvert()
const currentFormRef = ref()
const GoodOnlyRef = ref()
const $parent = inject('form') as FormInject
const submitForm = $parent.submitForm
const memoSpecList = ref([])
const goodNormTable = ref()

const $emit = defineEmits(['changeInstance', 'changeSpecificationType'])

const instance = getCurrentInstance()
// 单规格销售价校验
const realPriceValidatePass = (rule: any, value: any, callback: any) => {
    if (value <= 0) {
        callback(new Error('输入数值请大于0'))
    } else if (Number(submitForm.value.skus[0].price) < Number(value)) {
        callback(new Error('划线价应大于等于供货价'))
    } else {
        callback()
    }
}
// 单规格划线价校验
const minimumPurchaseValidatePass = (rule: any, value: any, callback: any) => {
    if (value <= 0 && submitForm.value.sellType !== 'CONSIGNMENT') {
        callback(new Error('输入数值请大于0'))
    } else {
        callback()
    }
}
// 单规格起批数校验
const validatePass = (rule: any, value: any, callback: any) => {
    if (value <= 0) {
        callback(new Error('输入数值请大于0'))
    } else if (Number(submitForm.value.skus[0].salePrice) > Number(value)) {
        callback(new Error('划线价应大于等于供货价'))
    } else {
        callback()
    }
}
const formRules = reactive({
    'skus[0].price': [
        {
            required: true,
            validator: validatePass,
            trigger: 'blur',
        },
    ],
    'skus[0].salePrice': [
        {
            required: true,
            validator: realPriceValidatePass,
            trigger: 'blur',
        },
    ],
    'skus[0].minimumPurchase': [
        {
            required: true,
            validator: minimumPurchaseValidatePass,
            trigger: 'blur',
        },
    ],
})

onActivated(() => {
    dataDisplay()
    $emit('changeInstance', instance?.refs)
})

const changeClassHandle = async (e: { type: string; list: NormType[] }) => {
    const { type, list } = e
    // 删除多规格转换单规格时候为skus中添加一条单规格数据
    if (!list.length) {
        submitForm.value.skus = [
            {
                id: '',
                image: '',
                initSalesVolume: 0,
                limitNum: 0,
                initStock: 0,
                limitType: 'UNLIMITED',
                price: 0,
                productId: '',
                salePrice: 0,
                shopId: '',
                stockType: 'LIMITED',
                weight: 0,
                minimumPurchase: 1,
                specs: [],
            },
        ]
        return
    }
    submitForm.value.specGroups = list
    if (type !== 'addSpec') {
        submitForm.value.skus = caleDescartes(list)
    }
}

const updateChildName = (newVal: string, oldVal: string) => {
    submitForm.value.skus.forEach((v) => {
        v.specs.forEach((item: string, index) => {
            if (oldVal === item) {
                v.specs[index] = newVal
            }
        })
    })
}

/**
 * @description: 改变规格列表
 */
const changeNormList = (e: NormListType[]) => {
    submitForm.value.skus = e
}
const radio = ref<'SINGLE_SPEC' | 'MUTI_SPEC'>('SINGLE_SPEC')

const handleChange = (e: any) => {
    $emit('changeSpecificationType', e)
    radio.value = e
}
const initHandleSubmitForm = () => {
    if (submitForm.value?.productType === 'REAL_PRODUCT') {
        submitForm.value.skus?.forEach((sku) => {
            sku.stockType = 'LIMITED'
        })
    }
    handleChange(submitForm.value?.specGroups?.length ? 'MUTI_SPEC' : 'SINGLE_SPEC')
    if (radio.value === 'MUTI_SPEC') {
        submitForm.value.specGroups = submitForm.value.specGroups?.map((item: any) => ({
            ...item,
            children: item.children?.map((child: any) => ({ ...child, name: child.name?.slice(0, 64) })),
        }))
    }
}
async function dataDisplay() {
    if ($route.query.id) {
        const { code: skuCode, data: skuData } = await doGetCommoditySku(useShopInfoStore().shopInfo.id, $route.query.id)
        if (skuCode !== 200) {
            ElMessage.error('获取商品sku失败')
            return
        }
        // 处理价格以厘作为单位
        skuData.skus.forEach((item: any) => {
            item.initStock = 0
            item.price = Number(divTenThousand(item.price))
            item.salePrice = Number(divTenThousand(item.salePrice))
        })
        submitForm.value = Object.assign(submitForm.value, skuData)
        initHandleSubmitForm()
    } else {
        initHandleSubmitForm()
    }
}
/**
 * @description: 规格组合
 * @param {NormType[]} list
 */
function caleDescartes(list: NormType[]) {
    let assemble = []
    assemble = list.map((item) => {
        return item.children
    })
    if (assemble.length <= 1) {
        assemble = assemble[0]
    } else {
        assemble = assemble.reduce((total, currentValue) => {
            let res: any[] = []
            total.forEach((t) => {
                currentValue.forEach((cv) => {
                    if (t instanceof Array) {
                        res.push([...t, cv])
                    } else {
                        res.push([t, cv])
                    }
                })
            })
            return res
        })
    }
    return assemble.map((item) => {
        return addSku(item)
    })
}
/**
 * @description: 添加规格
 * @param {*} productSpecNames
 * @returns {*}
 */
function addSku(productSpecNames: any) {
    let tempArr: string[] = []
    if (Array.isArray(productSpecNames)) {
        tempArr = productSpecNames.map((item) => {
            return item.name
        })
    } else {
        tempArr.push(productSpecNames.name)
    }
    const havespecs = submitForm.value.skus.find((item) => {
        return item.specs.every((spec) => tempArr.includes(spec))
    })
    if (havespecs) {
        havespecs.specs = tempArr
    }
    return havespecs
        ? havespecs
        : {
              specs: tempArr,
              stockType: 'LIMITED',
              initSalesVolume: 0,
              limitType: 'UNLIMITED',
              limitNum: 0,
              image: '',
              price: 0,
              initStock: 0,
              salePrice: 0,
              weight: '0',
              minimumPurchase: 1,
          }
}

defineExpose({
    currentFormRef,
    GoodOnlyRef,
    radio,
})
</script>
<template>
    <el-form ref="currentFormRef" :model="submitForm" :rules="formRules" label-width="100px">
        <release-title title="规格类型" />
        <el-form-item label="规格类型" style="margin-top: 10px">
            <el-radio-group :model-value="radio" size="small" :disabled="!(!$route.query.id || $route.query.isCopy)" @change="handleChange">
                <el-radio label="SINGLE_SPEC">单规格</el-radio>
                <el-radio label="MUTI_SPEC">多规格</el-radio>
            </el-radio-group>
        </el-form-item>
        <div v-if="radio === 'MUTI_SPEC'">
            <GoodNorms :classArr="submitForm.specGroups" @changeClass="changeClassHandle" @update-child-name="updateChildName" />
            <GoodNormTable
                v-if="submitForm.specGroups?.length"
                ref="goodNormTable"
                :list="submitForm.skus"
                :classArr="submitForm.specGroups"
                :memoSpecList="memoSpecList"
                @change-norm-list="changeNormList"
            />
        </div>
        <div v-if="radio === 'SINGLE_SPEC' && submitForm.skus?.length">
            <el-row :gutter="8">
                <el-col :span="12">
                    <el-form-item label="划线价" prop="skus[0].price">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].price"
                                :controls="false"
                                :max="999999"
                                :precision="2"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="供货价" prop="skus[0].salePrice">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].salePrice"
                                :max="999999"
                                :precision="2"
                                :controls="false"
                                class="input_number com__input--width"
                            ></el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col v-if="!$route.query.id || $route.query.isCopy" :span="12">
                    <el-form-item label="库存" prop="skus[0].initStock">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].initStock"
                                :disabled="(submitForm.skus[0].id && !$route.query.isCopy) || submitForm.skus[0].stockType === 'UNLIMITED'"
                                :max="999999"
                                :min="0"
                                :precision="0"
                                :controls="false"
                                class="input_number com__input--width"
                            ></el-input-number>
                            <div class="inputnumber__icon">个</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="初始销量">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].initSalesVolume"
                                placeholder="0"
                                :controls="false"
                                class="input_number com__input--width"
                            ></el-input-number>
                            <div class="inputnumber__icon">个</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="限购数量">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].limitNum"
                                :disabled="submitForm.skus[0].limitType === 'UNLIMITED'"
                                :min="0"
                                :controls="false"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">个</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="重量">
                        <div class="inputnumber">
                            <el-input-number v-model="submitForm.skus[0].weight" :controls="false" class="input_number com__input--width" />
                            <div class="inputnumber__icon">kg</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="起批数" prop="skus[0].minimumPurchase">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="submitForm.skus[0].minimumPurchase"
                                :controls="false"
                                placeholder="0"
                                class="input_number com__input--width"
                                :disabled="submitForm.sellType === 'CONSIGNMENT'"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">件</div>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="限购类型">
                        <el-select v-model="submitForm.skus[0].limitType">
                            <el-option label="不限购" value="UNLIMITED"></el-option>
                            <el-option label="商品限购" value="PRODUCT_LIMITED"></el-option>
                            <el-option v-if="submitForm.specGroups.length" label="规格限购" value="SKU_LIMITED"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="库存类型">
                        <el-select
                            v-model="submitForm.skus[0].stockType"
                            :disabled="!(!$route.query.id || $route.query.isCopy) || submitForm.productType === 'REAL_PRODUCT'"
                        >
                            <el-option label="有限库存" value="LIMITED"></el-option>
                            <el-option label="无限库存" value="UNLIMITED"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
        </div>
        <GoodOnly ref="GoodOnlyRef" />
    </el-form>
</template>
<style lang="scss">
@import '@/assets/css/goods/goodMultiSpec.scss';
@include b(inputnumber) {
    position: relative;
    width: 100%;
    @include e(icon) {
        width: 34px;
        height: 30px;
        position: absolute;
        right: 5px;
        top: 1px;
        background: #e6e8eb;
        color: #909399;
        text-align: center;
        /* border: 1px solid #e6e8eb; */
    }
}
.input_number {
    width: 90%;
    .el-input__inner {
        text-align: left;
    }
}
</style>
