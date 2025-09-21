<script setup lang="ts">
import { ref, reactive, provide, computed, inject, watch } from 'vue'
import { useVModel } from '@vueuse/core'
import FreightTemplateForm from '@/views/freight/components/freight-template-form.vue'
import uuid from '@/utils/uuid'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doAddLogisticsTemplate, doGetLogisticsTemplateInfoById, doPutLogisticsTemplateInfoById } from '@/apis/freight/freight-template'
import type { FreightTemplateFormSubmitData, ApiFreightTemplate } from '@/views/freight/components/types'

const $props = defineProps({
    isShow: {
        type: Boolean,
        default() {
            return false
        },
    },
    id: {
        type: String,
        default: '',
    },
})
const emit = defineEmits(['update:isShow', 'close'])
const _isShow = useVModel($props, 'isShow', emit)
const freightTemplateFormSubmitData = ref<FreightTemplateFormSubmitData>({
    isEdit: false,
    choiceConditionPostage: 0, // 选择条件邮费
    logisticsBaseModelDTO: [
        //物流基础
        {
            id: uuid(10),
            firstAmount: 0, //第一个金额
            firstQuantity: 0, //第一数量
            logisticsId: 0, //物流编号
            regionJson: [], // 地区
            secondAmount: 0, // 第二个金额
            secondQuantity: 0, // 第二个数量
            valuationModel: 'PKGS', // 计价模式
        },
    ],
    logisticsIncludePostageDTO: [
        //物流包邮
        {
            id: uuid(10),
            amountNum: 0, //数量
            logisticsId: 0, // 物流编号
            pieceNum: 0, //件数
            postType: 'PKGS',
            region: [], //地区
            weight: 0, //重量
        },
    ],
    templateName: '', // 模板名称
    valuationModel: 'PKGS', // 计价模式
})
const isPKGS = computed(() => freightTemplateFormSubmitData.value.valuationModel === 'PKGS')
provide('parentFreightTemplateForm', freightTemplateFormSubmitData)

initTemplateEditInfo()
watch(
    () => freightTemplateFormSubmitData.value.choiceConditionPostage,
    (val) => {
        if (val && !freightTemplateFormSubmitData.value.logisticsIncludePostageDTO[0]) {
            freightTemplateFormSubmitData.value.logisticsIncludePostageDTO.push({
                id: uuid(10),
                amountNum: 0, //数量
                logisticsId: 0, // 物流编号
                pieceNum: 0, //件数
                postType: 'PKGS',
                region: [], //地区
                weight: 0, //重量
            })
        }
    },
    { immediate: true, deep: true },
)

async function initTemplateEditInfo() {
    if ($props.id) {
        const { code, data } = await doGetLogisticsTemplateInfoById($props.id)
        if (code !== 200) return ElMessage.error('获取运费模板失败')
        const { logisticsBaseModelVos, logisticsIncludePostageVos, templateName, valuationModel, id, choiceConditionPostage } = data
        freightTemplateFormSubmitData.value.isEdit = true
        freightTemplateFormSubmitData.value.logisticsBaseModelDTO = logisticsBaseModelVos
        freightTemplateFormSubmitData.value.logisticsIncludePostageDTO = logisticsIncludePostageVos
        freightTemplateFormSubmitData.value.templateName = templateName
        freightTemplateFormSubmitData.value.valuationModel = valuationModel
        freightTemplateFormSubmitData.value.choiceConditionPostage = choiceConditionPostage
        freightTemplateFormSubmitData.value.id = id
    }
}
const handleClose = () => {
    removeData()
    _isShow.value = false
}
/**
 * @description: 提表单数据
 * @returns {*}
 */
const onSubmit = async () => {
    if (!_isShow.value) return
    const isValidate = submitDataValidate()
    if (isValidate) {
        handleSubmitDataDelId()
        if (freightTemplateFormSubmitData.value.isEdit) {
            handleEditTemplateSubmit()
            return
        }
        const { code, data } = await doAddLogisticsTemplate(freightTemplateFormSubmitData.value)
        if (code !== 200) return ElMessage.error('添加运费模板失败')
        removeDataInitTemplateList('添加运费模板成功')
    }
}
/**
 * @description: 编辑提交 方法不支持
 */
async function handleEditTemplateSubmit() {
    const { choiceConditionPostage } = freightTemplateFormSubmitData.value
    if (!choiceConditionPostage) {
        freightTemplateFormSubmitData.value.logisticsIncludePostageDTO = undefined
    }
    freightTemplateFormSubmitData.value.isEdit = undefined
    const { code, data } = await doPutLogisticsTemplateInfoById(freightTemplateFormSubmitData.value)
    if (code !== 200) return ElMessage.error('编辑失败')
    removeDataInitTemplateList('编辑运费模板成功')
}
/**
 * @description: 校验
 */
function submitDataValidate() {
    const { templateName, logisticsBaseModelDTO, choiceConditionPostage, logisticsIncludePostageDTO, valuationModel } =
        freightTemplateFormSubmitData.value
    if (!templateName.trim()) {
        ElMessage.error('请输入运费模板名称')
        return false
    }
    if (!logisticsBaseModelDTO.every((item) => item.regionJson.length)) {
        ElMessage.error('请选择地区')
        return false
    }
    if (choiceConditionPostage && !logisticsIncludePostageDTO.every((item) => item.region.length)) {
        ElMessage.error('请选择包邮地区')
        return false
    }
    // 首件 续件（重）必须大于 1
    const isQuantityBeGreaterThanOne = logisticsBaseModelDTO.every((item) => {
        return Number(item.firstQuantity) <= 0 || Number(item.secondQuantity) <= 0
    })
    if (isQuantityBeGreaterThanOne) {
        if (valuationModel === 'PKGS') {
            ElMessage.error('件数最少1')
        } else {
            ElMessage.error('重量最少0.1')
        }
        return false
    }
    // 首费用
    const isPriceThanOne = logisticsBaseModelDTO.every((item) => {
        return Number(item.firstAmount) <= 0 || Number(item.secondAmount) <= 0
    })
    if (isPriceThanOne) {
        ElMessage.error('首费或续费必须大于0')
        return false
    }
    let msg = ''
    //包邮 首件 续件（重）必须大于 1
    if (logisticsIncludePostageDTO && choiceConditionPostage) {
        // 是否通过校验
        // PKGS 件数
        // PKGS_MONEY 件数+金额
        // WEIGHT 重量
        // WEIGHT_MONEY 重量+金额
        for (let index = 0; index < logisticsIncludePostageDTO.length; index++) {
            const item = logisticsIncludePostageDTO[index]
            switch (item.postType) {
                case 'PKGS':
                    {
                        msg = Number(item.pieceNum) <= 0 ? '包邮件数必须大于0' : ''
                        console.log(Number(item.pieceNum) <= 0, msg, item.pieceNum, item)
                    }
                    break
                case 'PKGS_MONEY':
                    {
                        msg = Number(item.pieceNum) <= 0 ? '包邮件数必须大于0' : ''
                        if (msg) {
                            break
                        }
                        msg = Number(item.amountNum) <= 0 ? '包邮金额必须大于0' : ''
                    }
                    break
                case 'WEIGHT':
                    {
                        msg = Number(item.weight) <= 0 ? '包邮重量必须大于0,保留一位小数' : ''
                    }
                    break
                case 'WEIGHT_MONEY':
                    {
                        msg = Number(item.weight) <= 0 ? '包邮重量必须大于0,保留一位小数' : ''
                        if (msg) {
                            break
                        }
                        msg = Number(item.amountNum) <= 0 ? '包邮金额必须大于0' : ''
                    }
                    break
                case 'MONEY':
                    {
                        msg = Number(item.amountNum) <= 0 ? '包邮金额必须大于0' : ''
                    }
                    break

                default:
                    break
            }
            if (msg) {
                // 如果 msg 存在 跳出循环
                break
            }
        }
    }
    if (msg) {
        ElMessage.error(msg)
        return false
    }
    return true
}
/**
 * @description: 删除id
 */
function handleSubmitDataDelId() {
    const { logisticsBaseModelDTO, logisticsIncludePostageDTO, choiceConditionPostage } = freightTemplateFormSubmitData.value
    freightTemplateFormSubmitData.value.logisticsBaseModelDTO = logisticsBaseModelDTO.map((item) => {
        item.id = undefined
        return item
    })
    if (choiceConditionPostage) {
        freightTemplateFormSubmitData.value.logisticsIncludePostageDTO = logisticsIncludePostageDTO.map((item) => {
            item.id = undefined
            return item
        })
        return
    }
    freightTemplateFormSubmitData.value.logisticsIncludePostageDTO = undefined
}
/**
 * @description:删除数据初始化模板列表
 */
const removeDataInitTemplateList = (str: string) => {
    ElMessage.success(`${str}`)
    removeData()
    emit('close')
    _isShow.value = false
}
const removeData = () => {
    freightTemplateFormSubmitData.value = {
        choiceConditionPostage: 0, // 选择条件邮费
        logisticsBaseModelDTO: [
            //物流基础
            {
                id: uuid(10),
                firstAmount: 0, //第一个金额
                firstQuantity: '0', //第一数量
                logisticsId: 0, //物流编号
                regionJson: [], // 地区
                secondAmount: 0, // 第二个金额
                secondQuantity: '0', // 第二个数量
                valuationModel: 'PKGS', // 计价模式
            },
        ],
        logisticsIncludePostageDTO: [
            //物流包邮
            {
                id: uuid(10),
                amountNum: 0, //数量
                logisticsId: 0, // 物流编号
                pieceNum: 0, //件数
                postType: 'PKGS',
                region: [], //地区
                weight: 0, //重量
            },
        ],
        templateName: '', // 模板名称
        valuationModel: 'PKGS', // 计价模式
    }
}
</script>

<template>
    <el-dialog v-model="_isShow" width="50%" class="dialog" :before-close="handleClose">
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">{{ `${$props.id ? '编辑' : '添加'}` }}运费模板</h4>
            </div>
        </template>
        <FreightTemplateForm :isPKGS="isPKGS" :isEdit="!!$props.id" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取消</el-button>
                <el-button type="primary" @click="onSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(templateName) {
    display: inline-block;
    margin-left: 100px;
    color: #ccc;
}
</style>
