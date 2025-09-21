<script setup lang="ts">
import { QuestionFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { uploadFile } from '@/q-plugin/liveStream/apis/upload'
// 图片上传组件q-upload
const contactIdDocCopyImgChange = async (e: File) => {
    superQForm.contactIdDocCopy = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}
// 图片上传组件q-upload
const contactIdDocCopyBackImgChange = async (e: File) => {
    superQForm.contactIdDocCopyBack = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}
// 图片上传组件q-upload
const businessAuthorizationLetterImgChange = async (e: File) => {
    superQForm.businessAuthorizationLetter = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
}
const prop = defineProps({
    administrator: {
        type: Boolean,
        default: false,
    },
})
const emit = defineEmits(['AdministratorClose', 'update:administrator', 'superQ'])
// 取消按钮
const closeFn = (formEl: FormInstance | undefined) => {
    emit('AdministratorClose', !prop.administrator)
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
            emit('AdministratorClose', false)
        } else {
            console.log('error submit!', fields)
        }
    })
    emit('superQ', superQForm)
}
const closeFn1 = () => {
    emit('AdministratorClose', !prop.administrator)
    ruleFormRef.value.resetFields()
}
const propsAdministrator = computed({
    get() {
        return prop.administrator
    },
    set(value: boolean) {
        emit('update:administrator', value)
    },
})

const isShow1 = ref<boolean>(true)
const isShow2 = ref<boolean>(false)
// const contactIdDocType = ref('IDENTIFICATION_TYPE_IDCARD')
const phone = ref<number>()
const superQForm = reactive({
    contactType: 'LEGAL',
    contactIdDocType: 'IDENTIFICATION_TYPE_IDCARD',
    contactName: '',
    contactIdNumber: '',
    mobilePhone: '',
    contactEmail: '',
    contactIdDocCopy: '',
    contactIdDocCopyBack: '',
    contactPeriodBegin: '',
    contactPeriodEnd: '',
    businessAuthorizationLetter: '',
    contact: '',
})
const rules = reactive<FormRules>({
    contactType: [{ required: true, message: '请选择超管身份', trigger: 'blur' }],
    contactName: [
        { required: true, message: '请输入超管姓名', trigger: 'blur' },
        { min: 2, max: 100, message: '请输入2-100个字符', trigger: 'blur' },
    ],
    contactIdDocType: [{ required: true }],
    contactIdNumber: [
        { required: true, message: '请输入证件号码', trigger: 'blur' },
        {
            pattern: /^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|10|11|12)(?:0[1-9]|[1-2]\d|30|31)\d{3}[\dXx]$/,
            message: '请输入正确的证件号码',
            trigger: 'blur',
        },
    ],
    mobilePhone: [
        { required: true, message: '请输入手机号码', trigger: 'blur' },
        { pattern: /^(\d{11}|[\d-+]{5,20})$/, message: '请输入正确的证件号码', trigger: 'blur' },
    ],
    contactEmail: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { pattern: /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/, message: '请输入正确的邮箱', trigger: 'blur' },
    ],
    contactIdDocCopy: [{ required: true, message: '请上传身份证人像面照片', trigger: 'blur' }],
    contactIdDocCopyBack: [{ required: true, message: '请上传身份证国徽面照片', trigger: 'blur' }],
    contactPeriodBegin: [{ required: true, message: '请选择证件生效日期', trigger: 'blur' }],
    contactPeriodEnd: [{ required: true, message: '请选择证件失效日期', trigger: 'blur' }],
    businessAuthorizationLetter: [{ required: true, message: '请上传业务办理授权函', trigger: 'blur' }],
})
const cut1 = () => {
    isShow2.value = false
    isShow1.value = true
}
const cut2 = () => {
    isShow1.value = false
    isShow2.value = true
}

// 证件有效期
const indate = ref(1)
// 证件的失效日期
const value1 = ref('')
const holidays: any = []

const isHoliday = ({ dayjs }) => {
    return holidays.includes(dayjs.format('YYYY-MM-DD'))
}
</script>

<template>
    <div>
        <el-dialog v-model="propsAdministrator" :close-on-click-modal="false" :before-close="closeFn1">
            <el-form ref="ruleFormRef" :model="superQForm" :rules="rules" label-width="130px" style="padding: 0 20px">
                <h3 style="margin-top: -25px">超级管理员</h3>
                <el-divider border-style="dashed" />
                <el-form-item label="超级管理员信息">
                    <p style="font-size: 12px; color: #999">
                        超级管理员将接收开户信息及完成签约，并可进行商户号的日常重要管理及资金操作。请确定超级管理员为商户法定代表人或经办人再进行操作。
                    </p>
                </el-form-item>
                <el-form-item label="超管身份" prop="contactType">
                    <el-radio-group v-model="superQForm.contactType">
                        <el-radio value="LEGAL" @click="cut1">法定代表人/经营者</el-radio
                        ><el-tooltip
                            content="若超管为法定代表人/经营者，则该商户入驻流程为：提交申请 - 平台审核 - 超管签约 - 完成入驻"
                            placement="top"
                            effect="light"
                            ><el-icon :size="20"> <QuestionFilled /> </el-icon
                        ></el-tooltip>
                        <el-radio :value="2" @click="cut2">经办人</el-radio>
                        <el-tooltip
                            content="若超管为经办人，则该商户入驻流程为：提交申请 - 平台审核 -账户验证- 超管签约 - 完成入驻"
                            placement="top"
                            effect="light"
                            ><el-icon :size="20"> <QuestionFilled /> </el-icon
                        ></el-tooltip>
                    </el-radio-group>
                </el-form-item>

                <div v-if="isShow1">
                    <el-form-item label="超管姓名" prop="contactName">
                        <el-input v-model="superQForm.contactName" placeholder="请输入" style="width: 220px" />
                    </el-form-item>
                    <el-form-item label="超管资料类型" prop="contact">
                        <el-radio v-model="superQForm.contact" disabled>证件号码</el-radio>
                    </el-form-item>
                    <el-form-item label="证件号码" prop="contactIdNumber">
                        <el-input v-model="superQForm.contactIdNumber" placeholder="请输入" style="width: 220px" />
                    </el-form-item>
                    <el-form-item>
                        <p style="font-size: 12px; color: #999">请填写超级管理员的 身份证 证件号码</p>
                    </el-form-item>
                    <el-form-item label="手机号码" prop="mobilePhone">
                        <el-input v-model="superQForm.mobilePhone" placeholder="请输入" style="width: 300px">
                            <template #prepend>+86</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <p style="font-size: 12px; color: #999">
                            用于接收微信支付的重要管理信息及日常操作验证码，超管签约时可自助修改手机号，并进行短信验证超管修改手机号指引
                        </p>
                    </el-form-item>
                    <el-form-item label="邮箱" prop="contactEmail">
                        <el-input v-model="superQForm.contactEmail" placeholder="请输入" style="width: 300px"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <p style="font-size: 12px; color: #999">用于接收微信支付的开户邮件及日常业务通知</p>
                    </el-form-item>
                </div>
                <div v-if="isShow2">
                    <el-form-item label="证件类型" prop="contactIdDocType">
                        <el-text v-model="superQForm.contactIdDocType" label="IDENTIFICATION_TYPE_IDCARD" class="mx-1">中国大陆居民-身份证</el-text>
                    </el-form-item>
                    <el-form-item label="身份证人像面照片" prop="contactIdDocCopy">
                        <q-upload
                            v-model:src="superQForm.contactIdDocCopy"
                            :width="100"
                            :height="100"
                            @before-update="(e: any) => contactIdDocCopyImgChange(e)"
                        />
                    </el-form-item>
                    <el-form-item label="身份证国徽面照片" prop="contactIdDocCopyBack">
                        <q-upload
                            v-model:src="superQForm.contactIdDocCopyBack"
                            :width="100"
                            :height="100"
                            @before-update="(e: any) => contactIdDocCopyBackImgChange(e)"
                        />
                    </el-form-item>
                    <el-form-item>
                        <p style="color: #ccc">
                            请上传2M内的彩色照片 or 彩色扫描件 or 加盖公章鲜章的复印件，可添加“微信支付”相关水印（如微信支付认证）
                        </p>
                    </el-form-item>
                    <el-form-item label="证件号码" prop="contactIdNumber">
                        <el-input v-model="superQForm.contactIdNumber" placeholder="请输入" style="width: 300px" />
                    </el-form-item>
                    <el-form-item label="证件持有人姓名" prop="contactName">
                        <el-input v-model="superQForm.contactName" placeholder="请输入" style="width: 300px" />
                    </el-form-item>
                    <el-form-item label="证件有效期类型" prop="indate">
                        <el-radio-group v-model="indate">
                            <el-radio :value="1">定期</el-radio>
                            <el-radio :value="2">长期</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="证件生效日期" prop="contactPeriodBegin">
                        <el-date-picker
                            v-model="superQForm.contactPeriodBegin"
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
                    </el-form-item>
                    <el-form-item label="证件失效日期" prop="contactPeriodEnd">
                        <el-date-picker
                            v-model="superQForm.contactPeriodEnd"
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
                    </el-form-item>
                    <el-form-item label="业务办理授权函" prop="businessAuthorizationLetter">
                        <q-upload
                            v-model:src="superQForm.businessAuthorizationLetter"
                            :width="100"
                            :height="100"
                            @before-update="(e: any) => businessAuthorizationLetterImgChange(e)"
                        />
                    </el-form-item>
                    <el-form-item>
                        <p style="color: #ccc">
                            1.请上传法定代表人授权函扫描件
                            <a href="https://kf.qq.com/faq/220509Y3Yvym220509fQvYR7.html" target="_block">（下载模板）</a><br />
                            2.请上传2M以内的图片
                        </p>
                    </el-form-item>
                    <el-form-item label="手机号码" prop="mobilePhone">
                        <el-input v-model="superQForm.mobilePhone" placeholder="请输入" style="width: 300px">
                            <template #prepend>+86</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <p style="font-size: 12px; color: #999">
                            用于接收微信支付的重要管理信息及日常操作验证码，超管签约时可自助修改手机号，并进行短信验证
                        </p>
                    </el-form-item>
                    <el-form-item label="邮箱" prop="contactEmail">
                        <el-input v-model="superQForm.contactEmail" placeholder="请输入" style="width: 300px"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <p style="font-size: 12px; color: #999">用于接收微信支付的开户邮件及日常业务通知</p>
                    </el-form-item>
                </div>
                <div class="btn">
                    <el-button type="warning" @click="closeFn(ruleFormRef)">返回</el-button>
                    <el-button type="primary" @click="primaryFn(ruleFormRef)">确认</el-button>
                </div>
            </el-form>
        </el-dialog>
    </div>
</template>
<style lang="scss">
.el-radio {
    margin-left: 20px !important;
    margin-right: 5px !important;
}
</style>

<style lang="scss" scoped>
.btn {
    display: flex;
    justify-content: center;
}
</style>
