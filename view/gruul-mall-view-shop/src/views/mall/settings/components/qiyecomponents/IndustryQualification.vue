<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { uploadFile } from '@/q-plugin/liveStream/apis/upload'
// 图片上传组件q-upload
const handleItemImgChange = async (e: File) => {
    industryForm.qualifications = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}

const prop = defineProps({
    qualification: {
        type: Boolean,
        default: false,
    },
})
console.log(prop.qualification)

const emit = defineEmits(['qualificationClose', 'update:qualification', 'industryQ'])
// 取消按钮
const closeFn = (formEl: FormInstance | undefined) => {
    emit('qualificationClose', !prop.qualification)
    if (!formEl) return
    formEl.resetFields()
}
// 确认按钮
const ruleFormRef = ref<FormInstance>()
const primaryFn = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            console.log('submit!')
            emit('qualificationClose', false)
        } else {
            console.log('error submit!', fields)
        }
    })
    emit('industryQ', industryForm)
}
const closeFn1 = () => {
    emit('qualificationClose', !prop.qualification)
    ruleFormRef.value!.resetFields()
}

const propsQualification = computed({
    get() {
        return prop.qualification
    },
    set(value: boolean) {
        emit('update:qualification', value)
    },
})

const industryForm = reactive({
    settlementInfo: '',
    qualifications: '',
})
const rules = reactive<FormRules>({
    settlementInfo: [{ required: true, message: '请选择所属行业', trigger: 'blur' }],
    activitiesRate: [{ required: true, trigger: 'blur' }],
    qualifications: [{ required: true, trigger: 'blur' }],
})
// 级联选择器
const value = ref([])

const props = {
    expandTrigger: 'hover' as const,
}

const options = [
    {
        value: '719',
        label: '餐饮/零售',
        children: [
            {
                value: ['719', '餐饮'],
                label: '餐饮',
                qualification: false,
            },
            {
                value: ['719', '零售'],
                label: '零售',
                qualification: false,
            },
            {
                value: ['719', '食品生鲜'],
                label: '食品生鲜',
                qualification: false,
            },
        ],
    },
    {
        value: '746',
        label: '互联网服务',
        children: [
            {
                value: ['746', '电商平台'],
                label: '电商平台',
                qualification: false,
            },
            {
                value: ['720', '婚介平台/就业信息平台/其他信息服务平台'],
                label: '婚介平台/就业信息平台/其他信息服务平台',
                qualification: false,
            },
            {
                value: ['746', '门户论坛/网络广告及推广/软件开发/其他互联网服务'],
                label: '门户论坛/网络广告及推广/软件开发/其他互联网服务',
                qualification: false,
            },
            {
                value: ['746', '网络直播/直播平台'],
                label: '网络直播/直播平台',
                qualification: true,
            },
            {
                value: ['746', '游戏'],
                label: '游戏',
                qualification: true,
            },
        ],
    },
]
const isShow = ref<boolean>(false)
const activitiesRate = ref<number>()
const rate = ref<number>()
const rateShow = ref<boolean>(false)
const certificate = ref<string>('')
// 判断 利率的费用 是否需要资质
const handleChange = (value: any) => {
    isShow.value = true
    if (value[1][1] === '门户论坛/网络广告及推广/软件开发/其他互联网服务' || value[1][1] === '网络直播/直播平台' || value[1][1] === '游戏') {
        if (value[1][1] === '游戏') {
            rateShow.value = true
            certificate.value = '请提供有效期内的游戏版号（《网络游戏电子出版物审批》）'
        } else if (value[1][1] === '网络直播/直播平台') {
            rateShow.value = true
            certificate.value = '需提供《网络文化经营许可证》，且许可证的经营范围应当明确包括网络表演，PC/wap网站需要有ICP备案'
        } else {
            rateShow.value = false
        }
        activitiesRate.value = 1
    } else {
        rateShow.value = false
        activitiesRate.value = 0.6
    }
    console.log(value[1][1])
}
</script>

<template>
    <div>
        <el-dialog v-model="propsQualification" :close-on-click-modal="false" :before-close="closeFn1">
            <el-form ref="ruleFormRef" :model="industryForm" label-width="130px" style="padding: 0 20px" :rules="rules">
                <h3 style="margin-top: -25px">行业资质</h3>
                <el-divider border-style="dashed" />
                <el-form-item label="行业资质"><p style="font-size: 12px; color: #ccc">请填写商家的所属行业、特殊资质等信息</p></el-form-item>
                <el-form-item label="所属行业" prop="settlementInfo">
                    <div class="m-4">
                        <el-cascader v-model="industryForm.settlementInfo" :options="options" :props="props" @change="handleChange" />
                    </div>
                </el-form-item>
                <el-form-item v-if="isShow" label="结算费率">
                    <p>{{ activitiesRate }}%</p>
                </el-form-item>
                <el-form-item v-if="rateShow" label="特殊资质" prop="qualifications">
                    <q-upload
                        v-model:src="industryForm.qualifications"
                        :width="100"
                        :height="100"
                        @before-update="(e: any) => handleItemImgChange(e)"
                    />
                </el-form-item>
                <el-form-item>
                    <p style="font-size: 12px; color: #ccc">{{ certificate }}</p>
                </el-form-item>
                <div class="btn">
                    <el-button type="warning" @click="closeFn(ruleFormRef)">返回</el-button>
                    <el-button type="primary" @click="primaryFn(ruleFormRef)">确认</el-button>
                </div>
            </el-form>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.btn {
    display: flex;
    justify-content: center;
}
</style>
