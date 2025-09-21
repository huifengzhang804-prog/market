<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { uploadFile } from '@/components/q-edit/upload'

// 表单
const managementForm = reactive<any>({
    merchantShortname: '',
    servicePhone: '',
    webAuthorisation: '',
    webAuthorisationUrl: '',
    salesInfo1: 'SALES_SCENES_WEB',
})
// 图片上传组件q-upload
const handleItemImgChange = async (e: File) => {
    managementForm.webAuthorisation = await uploadFile('gruul-mall-payment/wx/service/merchant/image/upload', e)
    console.log('xxxxxxxxxxxxxxxxxxxxxx')
}

// 表单校验
const rules = reactive<FormRules>({
    merchantShortname: [
        { required: true, message: '请填写商户简称', trigger: 'blur' },
        { min: 1, max: 64, message: '示例：张三餐饮店', trigger: 'blur' },
    ],
    servicePhone: [
        { required: true, message: '请填写客服电话', trigger: 'blur' },
        { pattern: /^(\d{11}|[\d-+]{5,20})$/, message: '请输入11位数字的手机号码或者5-20位数字、连字符“-”、加号“+”', trigger: 'blur' },
    ],
    webAuthorisationUrl: [{ required: true, message: '请上传网站授权函', trigger: 'blur' }],
})
const props = defineProps({
    manage: {
        type: Boolean,
        default: false,
    },
})
const emit = defineEmits(['update:manage', 'manageClose', 'management'])
const propsMange = computed({
    get() {
        return props.manage
    },
    set(val: boolean) {
        console.log(val)

        emit('update:manage', val)
    },
})

// 取消按钮
const closeFn = (formEl: FormInstance | undefined) => {
    emit('manageClose', !props.manage)
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
            emit('manageClose', false)
        } else {
            console.log('error submit!', fields)
        }
    })
    emit('management', managementForm)
}
const closeFn1 = () => {
    emit('manageClose', !props.manage)
    ruleFormRef.value?.resetFields()
}
</script>

<template>
    <div>
        <el-dialog v-model="propsMange" :close-on-click-modal="false" :before-close="closeFn1">
            <el-form ref="ruleFormRef" :model="managementForm" :rules="rules" label-width="130px" style="padding: 0 20px">
                <h3 style="margin-top: -25px">经营信息</h3>
                <el-divider border-style="dashed" />
                <el-form-item label="商户简称" prop="merchantShortname">
                    <el-input v-model="managementForm.merchantShortname" placeholder="请输入" style="width: 220px" />
                </el-form-item>
                <el-form-item>
                    <p style="color: #ccc; font-size: 12px">
                        1、在支付完成页向买家展示，需与微信经营类目相关。<br />
                        2、简称要求： <br />①
                        不支持单纯以人名来命名，若为个体户经营，可用“个体户+经营者名称”或“经营者名称+业务”命名，如“个体户张三”或“张三餐饮店”； <br />②
                        不支持无实际意义的文案，如“XX特约商户”、“800”、“XX客服电话XXX”；
                    </p>
                </el-form-item>
                <el-form-item label="客服电话" prop="servicePhone">
                    <el-input v-model="managementForm.servicePhone" placeholder="请输入" style="width: 220px" />
                </el-form-item>
                <el-form-item>
                    <p style="color: #ccc; font-size: 12px">
                        1、请填写真实有效的客服电话，将在交易记录中向买家展示，提供咨询服务<br />
                        2、请确保电话畅通，以便入驻后平台回拨确认。
                    </p>
                </el-form-item>
                <el-divider border-style="dashed" />
                <el-form-item label="互联网网站">
                    <p style="color: #999; font-size: 13px">服务商可帮商家发起JSAPI支付、Native支付</p>
                </el-form-item>
                <el-form-item label="网站授权函照片" prop="webAuthorisationUrl">
                    <q-upload
                        v-model:src="managementForm.webAuthorisationUrl"
                        :width="100"
                        :height="100"
                        @before-update="(e: any) => handleItemImgChange(e)"
                    />
                </el-form-item>
                <el-form-item>
                    <p>若备案主体与申请主体不同，请务必上传加盖公章的网站授权函 <span style="color: #0288d1">网站授权函模板：</span></p>
                    <br />
                    <div class="book">
                        <h2 style="text-align: center">授权书</h2>
                        <p>致：财付通支付科技有限公司</p>
                        <p style="text-indent: 2em">
                            我司作为网站“<span>【请填写网站名称】</span>”（该网站首页地址为<span>【请填写网址】</span>，）合法的ICP备案主体（该网站备案/许可证号为：<span>【请ICP备案号】</span>），现我司依法授权<span>【请填写被授权公司】</span>（下称“被授权人”）通过上述网站向贵司申请开通微信支付相关服务。任何基于本授权而产生的一切法律纠纷和责任均由我司与被授权人承担连带责任，与贵司无关。
                        </p>
                        <p style="text-indent: 2em">本授权书自我司签字盖章之日起生效，自我司向贵司发出书面终止函之日起失效。</p>
                        <br />
                        <p style="text-align: center; margin-right: 10px">授权人（签章）：</p>
                        <p style="text-align: center; margin-right: 75px">时间：</p>
                        <br />
                        <p style="text-align: center">被授权人（签章）：</p>
                        <p style="text-align: center; margin-right: 75px">时间：</p>
                        <br />
                    </div>
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

.book {
    color: #000;
    font-size: 13px;
    padding-right: 110px;

    span {
        color: red;
    }
}
</style>
