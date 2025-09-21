<script lang="ts" setup>
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { uploadFile } from '@/components/q-edit/upload'

// 表单
const subjecForm = reactive<any>({
    licenseCopy: '',
    licenseUrl: '',
    licenseNumber: '',
    licenseAddress: '',
    apply1: '1',
    apply2: '1',
    contactPeriodBegin: '',
    contactPeriodEnd: '',
    merchantName: '',
    legalPerson: '',
    idCardInfo: '',
    idCardCopy: '',
    idCardUrl: '',
    idCardNational: '',
    idCardNationalUrl: '',
    idCardName: '',
    idCardNumber: '',
    cardPeriodBegin: '',
    cardPeriodEnd: '',
})
const prop = defineProps({
    subject: {
        type: Boolean,
        default: false,
    },
})
const emit = defineEmits(['subjectClose', 'update:subject', 'subjec'])

// 取消按钮
const closeFn = (formEl: FormInstance | undefined) => {
    emit('subjectClose', !prop.subject)
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
            emit('subjectClose', false)
            console.log(subjecForm.licenseNumber)
        } else {
            console.log('error submit!', fields)
        }
    })
    emit('subjec', subjecForm)
}
const closeFn1 = () => {
    emit('subjectClose', !prop.subject)
    ruleFormRef.value?.resetFields()
}

const propsSubject = computed({
    get() {
        return prop.subject
    },
    set(value: boolean) {
        emit('update:subject', value)
    },
})

// 表单校验规则
const rules = reactive<FormRules>({
    licenseCopy: [{ required: true, message: '请上传营业执照照片', trigger: 'blur' }],
    licenseNumber: [
        { required: true, message: '请填写注册号', trigger: 'blur' },
        { pattern: /^([0-9]{15}|[0-9]{18}|[A-Z]{18})$/, message: '请填写15位数字或者18位阿拉伯数字或大写英文字母', trigger: 'blur' },
    ],
    licenseAddress: [
        { required: true, message: '请填写营业执照注册地址', trigger: 'blur' },
        { min: 4, max: 128, message: '示例：广东省深圳市南山区xx路xx号', trigger: 'blur' },
    ],
    apply1: [{ required: true, message: '请选择有效期类型', trigger: 'blur' }],
    apply2: [{ required: true, message: '请选择有效期类型', trigger: 'blur' }],
    contactPeriodBegin: [{ required: true, message: '请选择执照生效日期', trigger: 'blur' }],
    contactPeriodEnd: [{ required: true, message: '请选择执照失效日期', trigger: 'blur' }],
    merchantName: [
        { required: true, message: '请填写商户名称', trigger: 'blur' },
        { min: 2, max: 128, message: '示例：腾讯科技有限公司', trigger: 'blur' },
    ],
    legalPerson: [
        { required: true, message: '请填写经营者姓名', trigger: 'blur' },
        { min: 2, max: 100, message: '示例：张三', trigger: 'blur' },
    ],
    idCardCopy: [{ required: true, message: '请上传身份证人像面照片', trigger: 'blur' }],
    idCardNational: [{ required: true, message: '请上传身份证国徽面照片', trigger: 'blur' }],
    idCardName: [
        { required: true, message: '请填写身份证姓名', trigger: 'blur' },
        { min: 2, max: 100, message: '请正确填写身份证姓名', trigger: 'blur' },
    ],
    idCardNumber: [
        { required: true, message: '请填写身份证件号码', trigger: 'blur' },
        {
            pattern: /^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|10|11|12)(?:0[1-9]|[1-2]\d|30|31)\d{3}[\dXx]$/,
            message: '请正确填写身份证号码',
            trigger: 'blur',
        },
    ],
    cardPeriodBegin: [{ required: true, message: '请选择证件生效日期', trigger: 'blur' }],
    cardPeriodEnd: [{ required: true, message: '请选择证件失效日期', trigger: 'blur' }],
})

// 日期
const holidays: any = []
const isHoliday = ({ dayjs }) => {
    return holidays.includes(dayjs.format('YYYY-MM-DD'))
}
// 图片上传组件q-upload
const businessLicenseInfoImgChange = async (e: File) => {
    subjecForm.licenseCopy = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}
// 图片上传组件q-upload
const idCardCopyImgChange = async (e: File) => {
    subjecForm.idCardCopy = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}
// 图片上传组件q-upload
const idCardNationalImgChange = async (e: File) => {
    subjecForm.idCardNational = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}

const SubjecInformation = ref(null)
const isShow = ref<boolean>(true)
const lose = ref<boolean>(true)
</script>

<template>
    <el-dialog v-model="propsSubject" :close-on-click-modal="false" :before-close="closeFn1">
        <el-form ref="ruleFormRef" :model="subjecForm" :rules="rules" label-width="130px" style="padding: 0 20px">
            <h3 style="margin-top: -25px">主体信息</h3>
            <el-divider border-style="dashed" />
            <el-form-item label="营业执照请上传">"营业执照"，需年检章齐全，当年注册除外</el-form-item>
            <el-form-item label="营业执照照片" prop="licenseCopy">
                <q-upload
                    v-model:src="subjecForm.licenseUrl"
                    :width="100"
                    :height="100"
                    @before-update="(e: any) => businessLicenseInfoImgChange(e)"
                />
            </el-form-item>
            <el-form-item>
                <p style="color: #ccc">请上传彩色照片 or 彩色扫描件 or 加盖公章鲜章的复印件，可添加“微信支付”相关水印（如微信支付认证）</p>
            </el-form-item>
            <el-form-item label="注册号" prop="licenseNumber">
                <el-input v-model="subjecForm.licenseNumber" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-form-item label="营业执照注册地址" prop="licenseAddress">
                <el-input v-model="subjecForm.licenseAddress" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-form-item label="执照有效期类型" prop="apply1">
                <div class="mb-2 flex items-center text-sm" style="line-height: 40px">
                    <el-radio-group v-model="subjecForm.apply1" class="ml-4">
                        <label><el-radio label="1" size="large" @click="lose = true">定期</el-radio></label>
                        <label style="margin-left: 20px"><el-radio label="2" size="large" @click="lose = false">长期</el-radio></label>
                    </el-radio-group>
                </div>
            </el-form-item>
            <el-form-item label="执照生效日期" prop="contactPeriodBegin">
                <div class="demo-date-picker">
                    <el-date-picker
                        v-model="subjecForm.contactPeriodBegin"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                    >
                        <template #default="cell">
                            <div class="cell" :class="{ current: cell.isCurrent }">
                                <span class="text">{{ cell.text }}</span>
                                <span v-if="isHoliday(cell)" class="holiday" />
                            </div>
                        </template>
                    </el-date-picker>
                </div>
            </el-form-item>
            <el-form-item v-if="lose" label="执照失效日期" prop="contactPeriodEnd">
                <div class="demo-date-picker">
                    <el-date-picker
                        v-model="subjecForm.contactPeriodEnd"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                    >
                        <template #default="cell">
                            <div class="cell" :class="{ current: cell.isCurrent }">
                                <span class="text">{{ cell.text }}</span>
                                <span v-if="isHoliday(cell)" class="holiday" />
                            </div>
                        </template>
                    </el-date-picker>
                </div>
            </el-form-item>
            <el-form-item label="商户名称" prop="merchantName">
                <el-input v-model="subjecForm.merchantName" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-form-item label="经营者姓名" prop="legalPerson">
                <el-input v-model="subjecForm.legalPerson" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-divider border-style="dashed" />
            <el-form-item label="经营者证件">请上传经营者的证件信息</el-form-item>
            <el-form-item label="证件类型" prop="idCardInfo"
                ><el-text v-model="subjecForm.idCardInfo" class="mx-1">中国大陆居民-身份证</el-text></el-form-item
            >
            <el-form-item label="身份证人像面照片" prop="idCardCopy">
                <q-upload
                    v-model:src="subjecForm.idCardUrl"
                    :width="100"
                    :height="100"
                    :format="{ size: 1 }"
                    @before-update="(e: any) => idCardCopyImgChange(e)"
                />
            </el-form-item>
            <el-form-item label="身份证国徽面照片" prop="idCardNational">
                <q-upload
                    v-model:src="subjecForm.idCardNationalUrl"
                    :width="100"
                    :height="100"
                    :format="{ size: 1 }"
                    @before-update="(e: any) => idCardNationalImgChange(e)"
                />
            </el-form-item>
            <el-form-item>
                <p style="color: #ccc">请上传彩色照片 or 彩色扫描件 or 加盖公章鲜章的复印件，可添加“微信支付”相关水印（如微信支付认证）</p>
            </el-form-item>
            <el-form-item label="身份证姓名" prop="idCardName">
                <el-input v-model="subjecForm.idCardName" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-form-item label="身份证件号码" prop="idCardNumber">
                <el-input v-model="subjecForm.idCardNumber" placeholder="请输入" style="width: 220px" />
            </el-form-item>
            <el-form-item label="证件有效期类型">
                <div class="mb-2 flex items-center text-sm" style="line-height: 40px" prop="apply2">
                    <el-radio-group v-model="subjecForm.apply2" class="ml-4">
                        <label><el-radio label="1" size="large" @click="isShow = true">定期</el-radio></label>
                        <label style="margin-left: 20px"><el-radio label="2" size="large" @click="isShow = false">长期</el-radio></label>
                    </el-radio-group>
                </div>
            </el-form-item>
            <el-form-item label="证件生效日期" prop="cardPeriodBegin">
                <div class="demo-date-picker">
                    <el-date-picker
                        v-model="subjecForm.cardPeriodBegin"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                    >
                        <template #default="cell">
                            <div class="cell" :class="{ current: cell.isCurrent }">
                                <span class="text">{{ cell.text }}</span>
                                <span v-if="isHoliday(cell)" class="holiday" />
                            </div>
                        </template>
                    </el-date-picker>
                </div>
            </el-form-item>
            <el-form-item v-if="isShow" label="证件失效日期" prop="cardPeriodEnd">
                <div class="demo-date-picker">
                    <el-date-picker
                        v-model="subjecForm.cardPeriodEnd"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                    >
                        <template #default="cell">
                            <div class="cell" :class="{ current: cell.isCurrent }">
                                <span class="text">{{ cell.text }}</span>
                                <span v-if="isHoliday(cell)" class="holiday" />
                            </div>
                        </template>
                    </el-date-picker>
                </div>
            </el-form-item>
            <div class="btn">
                <el-button type="warning" @click="closeFn(ruleFormRef)">返回</el-button>
                <el-button type="primary" @click="primaryFn(ruleFormRef)">确认</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>
<style scoped>
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: inline-block;
    margin-right: '80%';
}

.cell {
    height: 30px;
    padding: 3px 0;
    box-sizing: border-box;
}

.cell .text {
    width: 24px;
    height: 24px;
    display: block;
    margin: 0 auto;
    line-height: 24px;
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    border-radius: 50%;
}

.cell.current .text {
    background: #626aef;
    color: #fff;
}

.cell .holiday {
    position: absolute;
    width: 6px;
    height: 6px;
    background: var(--el-color-danger);
    border-radius: 50%;
    bottom: 0px;
    left: 50%;
    transform: translateX(-50%);
}
</style>

<style>
.el-form-item__content {
    display: block !important;
}

.el-form-item__label {
    justify-content: start !important;
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
    width: 60px;
    height: 60px;
    text-align: center;
}
</style>
<style lang="scss" scoped>
.btn {
    display: flex;
    justify-content: center;
}
</style>
