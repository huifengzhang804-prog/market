<script setup lang="ts">
import { doGetAvailableMemberConfig, doPostAvailableMemberConfig, doGetAvailableMember, doPutAvailableMember } from '@/apis/member'
import { ElMessage, FormInstance } from 'element-plus'
import { MEMBERBENEFITSTATUS } from '../types'
import { validateForm } from '@/utils/util'
import { MemberConfig } from '@/apis/member/types'

type BenefitType = {
    name: string
    rightsType: keyof typeof MEMBERBENEFITSTATUS
    memberRightsId: string
    extendValue?: string | number
}
type FormType = {
    id: string
    freeMemberName: string
    needValue: number
    relevancyRightsList: BenefitType[]
}
type MemberBenefit = keyof typeof MEMBERBENEFITSTATUS | string

const props = defineProps({
    memberId: {
        type: String,
        default: '',
    },
    memberLevel: {
        type: Number,
        default: undefined,
    },
})
const { mulHundred, divHundred } = useConvert()
const $router = useRouter()
const $route = useRoute()
const loading = ref(false)
const isEdit = ref(false)
const ruleFormRef = ref<FormInstance>()
const checkedMap = ref<Map<MemberBenefit, BenefitType>>(new Map())
const formData = reactive<FormType>({
    id: '',
    freeMemberName: '',
    needValue: 0,
    relevancyRightsList: [],
})
const goodsDiscount = ref(0)
const multiIntegral = ref(0)
const memberBenefitList = ref<MemberConfig[]>([])
const validateRules: Record<MemberBenefit, (val: string | number) => boolean> = {
    INTEGRAL_MULTIPLE: verifyIntegral,
    GOODS_DISCOUNT: verifypointDiscount,
}
const validateTips: Record<MemberBenefit, string> = {
    INTEGRAL_MULTIPLE: '积分值为2-9.9倍保留一位小数',
    GOODS_DISCOUNT: '商品折扣值为0.1-9.9折保留一位小数',
}
const rules = computed(() => {
    const basicRule: any = { freeMemberName: [{ required: true, message: '请输入会员名称', trigger: 'blur' }] }
    if (props.memberLevel !== 1) {
        basicRule.needValue = [
            { required: true, message: '不可为空；只能填整数', trigger: 'blur' },
            {
                validator: theInteger,
                trigger: 'blur',
            },
        ]
    }
    return basicRule
})

initMemberConfig()

const handleCheckList = (e: BenefitType) => {
    if (checkedMap.value.has(e.memberRightsId)) {
        checkedMap.value.delete(e.memberRightsId)
    } else {
        checkedMap.value.set(e.memberRightsId, e)
    }
}
const handleSubmit = async () => {
    await validateForm(ruleFormRef.value)
    const flag = await submitForm()
    return flag
}
const handleNavToBenefit = () => {
    $router.push({
        name: 'vipSetting',
        query: {
            name: 'RightsMember',
        },
    })
}
function theInteger(rule: any, value: any, callback: any) {
    if (!value) {
        return callback('请输入正确的值')
    } else if (Number.isInteger(Number(value))) {
        callback()
    } else {
        callback('请输入正整数')
    }
}
/**
 * 校验提交
 */
function validateSubmit() {
    const tempArr: { type: boolean; tips: string }[] = []
    checkedMap.value.forEach((item) => {
        if (validateRules[item.rightsType]) {
            console.log('tempArr', item.rightsType === 'GOODS_DISCOUNT' ? goodsDiscount.value : multiIntegral.value)
            tempArr.push({
                type: validateRules[item.rightsType](item.rightsType === 'GOODS_DISCOUNT' ? goodsDiscount.value : multiIntegral.value),
                tips: validateTips[item.rightsType] || '',
            })
        }
    })

    const tipsArr = tempArr.filter((item) => {
        return !item.type
    })
    if (tipsArr.length) {
        ElMessage.warning(tipsArr[0].tips)
    }
    return !tipsArr.length
}

/**
 * 校验积分输入值
 */
function verifyIntegral(val: string | number) {
    // 2-9.9
    const NumberValue = Number(val)
    return NumberValue >= 2 && NumberValue <= 9.9 && verifyRoundToOneDecimal(String(val), 1)
}
/**
 * 校验保留一位小数
 * @param {string} e 值
 * @param {number} num 保留位数
 * @returns {boolean}
 */
function verifyRoundToOneDecimal(e: string, num: number) {
    const regu = /^\d+(\.\d?)?$/
    if (e !== '') {
        return regu.test(e)
    } else {
        return false
    }
}
/**
 * 校验商品折扣
 */
function verifypointDiscount(val: string | number) {
    console.log(val)
    // 0.1-9.9
    const NumberValue = Number(val)
    return NumberValue >= 0.1 && NumberValue <= 9.9 && verifyRoundToOneDecimal(String(val), 1)
}
async function initMember() {
    if (props.memberId) {
        const { code, data, msg } = await doGetAvailableMember(String(props.memberId))
        if (code === 200 && data) {
            const { id, freeMemberName, needValue, relevancyRightsList } = data
            formData.id = id
            formData.freeMemberName = freeMemberName
            formData.needValue = needValue
            formData.relevancyRightsList = relevancyRightsList
            relevancyRightsList.forEach((item: BenefitType) => {
                if (item.rightsType === 'GOODS_DISCOUNT') {
                    goodsDiscount.value = Number(divHundred(item.extendValue))
                } else if (item.rightsType === 'INTEGRAL_MULTIPLE' && item.extendValue) {
                    multiIntegral.value = Number(divHundred(item.extendValue))
                }
                checkedMap.value.set(item.memberRightsId, item)
            })
        } else {
            ElMessage.error(msg ? msg : '获取失败')
        }
    }
}
async function initMemberConfig() {
    const { code, data } = await doGetAvailableMemberConfig()
    if (code !== 200) return ElMessage.error('获取会员权益失败')
    memberBenefitList.value = data
    initMember()
}
async function submitForm() {
    if (validateSubmit()) {
        formData.relevancyRightsList = Array.from(checkedMap.value.values()).map((item) => {
            if (item.rightsType === 'GOODS_DISCOUNT') {
                item.extendValue = Number(mulHundred(goodsDiscount.value))
            } else if (item.rightsType === 'INTEGRAL_MULTIPLE') {
                item.extendValue = Number(mulHundred(multiIntegral.value))
            } else {
                delete item.extendValue
            }
            return item
        })
        const { code, msg } = formData.id ? await doPutAvailableMember(formData) : await doPostAvailableMemberConfig(formData)
        if (code === 200) {
            ElMessage.success('保存成功')
            // $router.push({
            //     name: 'vipSetting',
            // })
        } else {
            ElMessage.error(msg ? msg : '保存失败')
            return Promise.reject(msg ? msg : '保存失败')
        }
        return code === 200 ? true : false
    } else {
        return false
    }
}
defineExpose({ handleSubmit })
</script>

<template>
    <div style="padding: 0 40px">
        <h1 class="title">基本信息</h1>
        <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="auto">
            <el-form-item label="会员等级"> vip{{ props.memberLevel }} </el-form-item>
            <el-form-item label="等级名称" prop="freeMemberName">
                <el-input
                    v-model.trim="formData.freeMemberName"
                    :minlength="3"
                    :disabled="isEdit"
                    style="width: 226px"
                    :maxlength="8"
                    placeholder="请输入等级名称"
                />
            </el-form-item>
            <el-form-item v-if="props.memberLevel !== 1" label="成长值" prop="needValue">
                <el-row style="width: 100%">
                    <el-input-number v-model="formData.needValue" :min="0" :controls="false" style="width: 226px" placeholder="请输入获得条件" />
                    <!-- <span style="font-size: 14px; color: #333333; padding: 0 10px">成长值</span> -->
                </el-row>
                <div class="msg">成长值大于等于该数值时升级成为该等级会员</div>
            </el-form-item>
            <h1 class="title">权益礼包</h1>
            <el-form-item>
                <el-row v-for="item in memberBenefitList" :key="item.id" style="width: 100%">
                    <el-form-item label-width="0" style="margin: 10px 0">
                        <el-checkbox
                            :key="item.id + checkedMap.has(item.id)"
                            :checked="checkedMap.has(item.id)"
                            :label="{ name: item.rightsName, memberRightsId: item.id, extenValue: 0 }"
                            @change="
                                handleCheckList({
                                    name: item.rightsName,
                                    rightsType: item.rightsType as keyof typeof MEMBERBENEFITSTATUS,
                                    memberRightsId: item.id,
                                    extendValue: 0,
                                })
                            "
                            >{{ item.rightsName }}</el-checkbox
                        >
                        <el-input
                            v-if="item.rightsType === 'GOODS_DISCOUNT'"
                            v-model="goodsDiscount"
                            :disabled="isEdit"
                            style="width: 130px; margin: 0 20px"
                        >
                            <template #append>折</template>
                        </el-input>
                        <el-input
                            v-else-if="item.rightsType === 'INTEGRAL_MULTIPLE'"
                            v-model="multiIntegral"
                            :disabled="isEdit"
                            style="width: 130px; margin: 0 20px"
                        >
                            <template #append>倍</template>
                        </el-input>
                    </el-form-item>
                </el-row>

                <!-- <el-form-item label-width="0" style="margin: 10px 0">
                    <el-link :underline="false" type="primary" @click="handleNavToBenefit">&nbsp;&nbsp;更多权益</el-link>
                </el-form-item> -->
            </el-form-item>
        </el-form>
        <!-- <div class="nav-button">
            <el-button round plain @click="$router.back()">取消</el-button>
            <el-button type="primary" round :loading="loading" @click="handleSubmit(ruleFormRef)">保存</el-button>
        </div> -->
    </div>
</template>

<style scoped lang="scss">
@include b(title) {
    font-size: 14px;
    color: #323233;
    font-weight: 700;
    margin-bottom: 20px;
}
@include b(msg) {
    font-size: 12px;
    color: #c4c4c4;
}

@include b(nav-button) {
    width: 1010px;
    position: fixed;
    bottom: 10px;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    z-index: 999;
    margin: 0 auto;
    margin-left: -55px;
}
</style>
