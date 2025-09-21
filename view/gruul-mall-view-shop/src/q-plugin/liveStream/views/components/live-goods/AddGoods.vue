<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { SemiSelect } from '@element-plus/icons-vue'
import { doPostGoodsAdd, doGetGoodsAdd, doPutGoodsUpdate, doDeldeleteGoodsInfos } from '@/q-plugin/liveStream/apis/goods'
import ChoosedGoods from '@/q-plugin/liveStream/views/components/live-goods/choosed-goods.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { uploadFile } from '@/q-plugin/liveStream/apis/upload'
import { doGetCommodity } from '@/apis/good'
import type { ApiGoods } from '@/q-plugin/liveStream/views/components/live-goods/types'
import type { GoodsItemQuery } from '@/q-plugin/liveStream/views/components/live-goods/types'
import type { CommoditySpecTable, SubmitCommodityType } from '@/views/goods/types'
import type { PropType } from 'vue'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default() {
            return false
        },
    },
    currentGoodsValue: {
        type: Object as PropType<ApiGoods>,
        default: () => ({}),
    },
})

const emit = defineEmits(['update:modelValue', 'submit', 'updateList', 'delCurrentGoods'])
const { divTenThousand, mulTenThousand } = useConvert()
const _isShow = useVModel(props, 'modelValue', emit)
const choosedGoodsShow = ref(false)
const disableEdit = ref(false)
const isEdit = ref(false)
const priceError = ref('')
const loading = ref(false)
const goodsPathErrorMsg = ref('')
const ruleFormRef = ref()
const formData = ref<GoodsItemQuery>({
    productId: '',
    goodsId: '',
    productName: '',
    ossImgUrl: '',
    coverImgUrl: '',
    priceType: 1,
    price: '',
    price2: '',
    url: '',
})
const rules = reactive({
    productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
    priceType: [{ required: true, message: '请选择价格形式', trigger: ['blur', 'change'] }],
    price: [
        { required: true, message: '请输入价格', trigger: 'blur' },
        { validator: priceValidator, trigger: 'blur' },
    ],
    price2: [
        { required: false, message: '请输入价格', trigger: 'blur' },
        { validator: priceValidator, trigger: 'blur' },
    ],
    ossImgUrl: [
        {
            required: true,
            message: '请添加直播封面图',
            trigger: 'blur',
        },
    ],
})
const currentGoods = ref<SubmitCommodityType[]>([])
const updateGoodsInfo = async () => {
    if (props.currentGoodsValue.goodsId) {
        const { productId, productName, ossImgUrl, priceType, price, price2, url, auditStatus, shopId, coverImgUrl, goodsId } =
            props.currentGoodsValue
        if (['APPROVED', 'VIOLATION__OFF_SHELF'].includes(auditStatus)) {
            disableEdit.value = true
        }
        isEdit.value = true
        formData.value = {
            goodsId,
            productId,
            productName,
            coverImgUrl,
            ossImgUrl,
            priceType: priceType,
            price: price ? divTenThousand(price).toString() : '',
            price2: price2 ? divTenThousand(price2).toString() : '',
            url,
        }
        // 查询商品回显
        const { code, data } = await doGetCommodity({ id: productId, shopId, status: 'SELL_ON' })
        if (code !== 200) {
            ElMessage.error('查询商品失败')
            return
        }
        if (!data.records?.length) {
            ElMessage.error('商品不存在')
            return
        }
        currentGoods.value = [data.records[0]]
    }
}

watchEffect(() => {
    if (_isShow.value) {
        updateGoodsInfo()
    }
})

const handleSubmit = async () => {
    if (disableEdit.value) {
        // 查看不可编辑
        emit('delCurrentGoods')
        reset()
        return
    }

    try {
        const isValidate = await ruleFormRef.value.validate()
        if (!isValidate) return
        if (!validate()) return
        priceError.value = ''
        const { price, price2 } = formData.value
        const priceQuery = {
            price: price ? mulTenThousand(price).toString() : price,
            price2: price2 ? mulTenThousand(price2).toString() : price2,
            productId: currentGoods.value[0].id,
        }
        formData.value.url = 'pages/index/index'
        loading.value = true
        const edit = isEdit.value && !disableEdit.value
        console.log('formData.value', formData.value)

        const { code, data, msg } = edit
            ? await doPutGoodsUpdate({ ...formData.value, ...priceQuery })
            : await doPostGoodsAdd({ ...formData.value, ...priceQuery })
        if (code !== 200) {
            ElMessage.error(msg ? msg : `${edit ? '修改' : '添加'}商品失败`)
            loading.value = false
            return
        }
        if (edit) {
            emit('delCurrentGoods')
        }
        ElMessage.success(`${edit ? '修改' : '添加'}商品成功`)
        emit('submit', formData.value)
        emit('updateList', formData.value)
        reset()
    } catch (error) {
        loading.value = false
        console.error(error)
    }
}
const contentTypeToExtensionMap = {
    'image/jpeg': 'jpg',
    'image/png': 'png',
    'image/gif': 'gif',
    'image/webp': 'webp',
    // 可以添加更多的映射关系
}
function getPossibleExtension(file: File) {
    const contentType = file.type
    return contentTypeToExtensionMap[contentType as keyof typeof contentTypeToExtensionMap] || null
}
const handleUploadImgSuccess = async (key: 'ossImgUrl', valueKey: 'coverImgUrl', e: File) => {
    if (e) {
        const res = await uploadFile(`gruul-mall-live/live/broadcast/${getPossibleExtension(e)}/uploadSourceMaterial`, e)
        if (formData.value[key]) {
            formData.value[valueKey] = res
        }
    }
}
function validate() {
    if (!currentGoods.value.length) {
        goodsPathErrorMsg.value = goodsPathErrorMsg.value === '请选择商品路径' ? '请选择商品路径 ' : '请选择商品路径'
        return false
    }
    if (formData.value.priceType === 2 && formData.value.price > formData.value.price2) {
        priceError.value = priceError.value === '前面的价格不能大于后面的价格' ? '前面的价格不能大于后面的价格 ' : '前面的价格不能大于后面的价格'
        return false
    }
    if (formData.value.priceType === 3 && formData.value.price < formData.value.price2) {
        priceError.value =
            priceError.value === '价格输入不合规,现价不能大于原价' ? '价格输入不合规,现价不能大于原价 ' : '价格输入不合规,现价不能大于原价'
        return false
    }
    return true
}
function priceValidator(rule: any, value: any, callback: any) {
    if (value < 0.01) {
        return callback(new Error('最小价格为0.01'))
    }
    callback()
}
/**
 * 价格处理展示
 */
const formatPrice = (storageSkus: CommoditySpecTable[]) => {
    if (!storageSkus) {
        return
    }
    const tempArr = storageSkus.map((item) => divTenThousand(item.salePrice).toNumber())
    const min = Math.min(...tempArr)
    const max = Math.max(...tempArr)
    return max === min ? max : `${min}-${max}`
}
/**
 * 获取到商品
 */
const handleChooseGoods = (goods: SubmitCommodityType[]) => {
    currentGoods.value = goods
    if (currentGoods.value.length) {
        goodsPathErrorMsg.value = ''
    }
}
const handlePriceTypeChange = () => {
    if (formData.value.priceType === 1) {
        rules.price2[0].required = false
        formData.value.price2 = ''
    } else {
        rules.price2[0].required = true
    }
}
const handleCancel = () => {
    ElMessageBox.confirm('取消商品信息不会保存', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(() => {
            if (disableEdit.value || isEdit.value) {
                // 查看不可编辑
                emit('delCurrentGoods')
            }
            reset()
        })
        .catch(() => {})
}
const reset = () => {
    ruleFormRef.value.resetFields()
    formData.value = { productId: '', productName: '', ossImgUrl: '', coverImgUrl: '', priceType: 1, price: '', price2: '', url: '', goodsId: '' }
    currentGoods.value = []
    _isShow.value = false
    loading.value = false
    disableEdit.value = false
    isEdit.value = false
}
</script>

<template>
    <el-dialog
        v-model="_isShow"
        @close="
            () => {
                if (disableEdit || isEdit) {
                    // 查看不可编辑
                    emit('delCurrentGoods')
                }
                reset()
            }
        "
    >
        <template #header="{ titleId, titleClass }">
            <h4 :id="titleId" :class="titleClass">{{ disableEdit ? '查看' : isEdit ? '编辑' : '新增' }}商品</h4>
        </template>
        <div style="padding: 0 40px">
            <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="auto" :inline-message="false" :validate-on-rule-change="false">
                <el-form-item label="直播封面图" prop="ossImgUrl" label-width="100px">
                    <el-row style="width: 100%">
                        <q-upload
                            v-model:src="formData.ossImgUrl"
                            :width="100"
                            :disabled="disableEdit"
                            :height="100"
                            :format="{ size: 1, width: 301, height: 301 }"
                            @before-update="(e:any) => handleUploadImgSuccess('ossImgUrl', 'coverImgUrl', e)"
                        />
                    </el-row>
                    <span class="msg" style="margin: 0">最大尺寸:300像素 * 300像素，图片大小不得超过1M</span>
                </el-form-item>
                <el-form-item label="商品名称" prop="productName">
                    <el-input
                        v-model.trim="formData.productName"
                        :disabled="disableEdit"
                        style="width: 551px"
                        minlength="4"
                        maxlength="14"
                        placeholder="请输入商品名称"
                        show-word-limit
                    />
                    <span class="msg" style="margin: 0">商品名称最少4个汉字或6个字符，最大14个汉字</span>
                </el-form-item>
                <el-form-item label="价格形式" prop="priceType">
                    <el-row style="width: 100%">
                        <el-radio-group v-model="formData.priceType" :disabled="disableEdit" @change="handlePriceTypeChange">
                            <el-radio :value="1">一口价</el-radio>
                            <el-radio :value="2">价格区间</el-radio>
                            <el-radio :value="3">显示折扣价</el-radio>
                        </el-radio-group>
                    </el-row>
                </el-form-item>
                <el-form-item label="价格" prop="price" :error="priceError">
                    <div v-if="formData.priceType === 1" class="inputnumber">
                        <el-input-number
                            v-model="formData.price"
                            :disabled="disableEdit"
                            :controls="false"
                            :max="999999"
                            :min="0"
                            step-strictly
                            :step="0.01"
                            style="width: 150px"
                            :precision="2"
                            class="input_number com__input--width"
                        >
                        </el-input-number>
                        <div class="inputnumber__icon">元</div>
                    </div>
                    <template v-else-if="formData.priceType === 2">
                        <div class="inputnumber">
                            <el-input-number
                                v-model="formData.price"
                                :disabled="disableEdit"
                                :controls="false"
                                :max="999999"
                                :min="0"
                                step-strictly
                                :step="0.01"
                                style="width: 150px"
                                :precision="2"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                        <el-icon style="margin: 0 10px"><SemiSelect /></el-icon>
                        <div class="inputnumber">
                            <el-input-number
                                v-model="formData.price2"
                                :disabled="disableEdit"
                                :controls="false"
                                :max="999999"
                                :min="0"
                                step-strictly
                                :step="0.01"
                                style="width: 150px"
                                :precision="2"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                    </template>
                    <template v-else>
                        <span style="margin-right: 5px">市场价</span>
                        <div class="inputnumber">
                            <el-input-number
                                v-model="formData.price"
                                :disabled="disableEdit"
                                :controls="false"
                                :max="999999"
                                :min="0"
                                step-strictly
                                :step="0.01"
                                style="width: 150px"
                                :precision="2"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                        <span style="margin: 0 5px">,</span>
                        <span style="margin: 0 5px">现价</span>
                        <div class="inputnumber">
                            <el-input-number
                                v-model="formData.price2"
                                :disabled="disableEdit"
                                :controls="false"
                                :max="999999"
                                :min="0"
                                step-strictly
                                :step="0.01"
                                style="width: 150px"
                                :precision="2"
                                class="input_number com__input--width"
                            >
                            </el-input-number>
                            <div class="inputnumber__icon">元</div>
                        </div>
                    </template>
                </el-form-item>
                <el-form-item label="商品路径" required :error="goodsPathErrorMsg">
                    <el-button v-if="!currentGoods.length" round plain :disabled="disableEdit" @click="choosedGoodsShow = true">选择商品</el-button>
                    <div v-else style="border: 1px solid #ccc; width: 100%">
                        <el-table :data="currentGoods" style="width: 100%" :header-cell-style="{ background: '#f2f2f2' }">
                            <template #empty> <ElTableEmpty /> </template>
                            <el-table-column label="商品信息">
                                <template #default="{ row }">
                                    <div class="goods">
                                        <el-image style="width: 60px; height: 60px" :src="row.pic" />
                                        <div class="goods__info">
                                            <div class="goods__info--name">{{ row.name }}</div>
                                            <div class="goods__info--price">{{ formatPrice(row.storageSkus) }}</div>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作" width="130">
                                <template #default>
                                    <el-link :underline="false" type="primary" :disabled="disableEdit" @click="currentGoods.splice(0, 1)">
                                        删除
                                    </el-link>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-form-item>
            </el-form>
        </div>
        <choosed-goods v-model="choosedGoodsShow" @choose-goods="handleChooseGoods" />
        <template #footer>
            <span v-if="!disableEdit" class="dialog-footer">
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" :loading="loading" @click="handleSubmit"> 确定 </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(msg) {
    font-size: 12px;
    margin-left: 12px;
    color: #c4c4c4;
}
@include b(goods) {
    width: 310px;
    display: flex;
    justify-content: flex-start;
    @include e(info) {
        padding: 0 0 0 10px;
        width: 150px;
        line-height: 30px;
        color: #515151;
        font-size: 12px;
        @include m(name) {
            @include utils-ellipsis(1);
            color: #2e99f3;
        }
        @include m(price) {
            color: #f12f22;
            &::before {
                content: '￥';
            }
        }
    }
}
@include b(inputnumber) {
    position: relative;
    width: 100%;
    @include e(icon) {
        width: 33px;
        height: 30px;
        position: absolute;
        right: 1.5px;
        top: 2px;
        background: #e6e8eb;
        color: #909399;
        text-align: center;
        /* border: 1px solid #e6e8eb; */
    }
}
.input_number {
    width: 100%;

    .el-input__inner {
        text-align: left;
    }
}
</style>
