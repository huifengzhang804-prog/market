<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { ApiParameters, PAYTYPE, PLATFORMS, platforms } from '@/views/set/components/WechatPay'
import { doEditmerchant, doGetMerchant } from '@/apis/set/WechatPay'
import { useAdminInfo } from '@/store/modules/admin'
import { uuidHandle } from '@/apis/request'
import { elementUploadRequest } from '@/apis/upload'

const $props = defineProps({
    platformType: { type: String, default: 'WECHAT' },
})

const formRef = ref<FormInstance>()
const validateFile = (rule: any, value: any, callback: any) => (uploadText.value ? callback() : callback(new Error('请上传支付证书')))
const validateFileKeyPublic = (rule: any, value: any, callback: any) => (uploadText.value ? callback() : callback(new Error('请上传支付宝公钥证书')))
const validateFileAlipayRootCert = (rule: any, value: any, callback: any) =>
    uploadTextTwo.value ? callback() : callback(new Error('请上传支付宝根证书'))
const validateFileAppCertPublicKey = (rule: any, value: any, callback: any) =>
    uploadTextThree.value ? callback() : callback(new Error('请上传应用公钥证书'))
const rules = reactive<FormRules>({
    appid: [{ required: true, message: '请填写Appid', trigger: 'blur' }],
    subjectName: [
        { required: true, message: '请填写商户名称', trigger: 'blur' },
        { max: 20, message: '输入长度20字以内', trigger: 'blur' },
    ],
    platforms: [{ required: true, message: '请选择渠道', trigger: 'change' }],
    mchId: [
        { required: true, message: '请填写商户号', trigger: 'blur' },
        { max: 20, message: '输入长度20字以内', trigger: 'blur' },
    ],
    keyPrivate: [{ required: true, message: '请填写商户APIv3密钥签名', trigger: 'blur' }],
    upload: [{ required: true, validator: validateFile, trigger: 'change' }],
    keyPublic: [{ required: true, validator: validateFileKeyPublic, trigger: 'change' }],
    alipayRootCert: [{ required: true, validator: validateFileAlipayRootCert, trigger: 'change' }],
    appCertPublicKey: [{ required: true, validator: validateFileAppCertPublicKey, trigger: 'change' }],
    keyPublicId: [
        {
            required: true,
            validator(_r, _v, callback) {
                return callback(wechatKeyPublic.value.keyPublicId ? undefined : new Error('请填写微信支付公钥ID'))
            },
            trigger: 'blur',
        },
    ],
    keyPublicText: [
        {
            required: true,
            validator(_r, _v, callback) {
                return callback(wechatKeyPublic.value.keyPublicText ? undefined : new Error('请填写微信支付公钥文本内容'))
            },
            trigger: 'blur',
        },
    ],
})
/**
 * 添加/编辑时携带的参数
 */
const params = ref<ApiParameters>({
    subjectName: '',
    appid: '',
    detailsId: '',
    keyCert: '',
    keyPrivate: '',
    keyPublic: '',
    mchId: '',
    payType: $props.platformType as keyof typeof PAYTYPE,
    platforms: [],
    appCertPublicKey: '',
    alipayRootCert: '',
})
const listParams = ref<ApiParameters[]>()
const dialogTableVisible = ref(false)
const isEdit = ref(false)
// 已上传提示文本
const uploadText = ref(false)
const uploadTextTwo = ref(false)
const uploadTextThree = ref(false)

initMerchant()

/**
 * 初始化商户配置信息
 */

async function initMerchant() {
    const { code, data } = await doGetMerchant($props.platformType)
    if (code === 200) {
        listParams.value = data
        return
    }
    ElMessage.error('获取信息失败')
    console.log('listParams.value', listParams.value)
}

const handleSubmit = async () => {
    try {
        await formRef.value!.validate()
        if (params.value.payType === 'ALIPAY') {
            params.value.keyCert = params.value.alipayRootCert
                ? `${params.value.appCertPublicKey},${params.value.alipayRootCert}`
                : `${params.value.appCertPublicKey}`
        } else {
            const publicKeyConfig = wechatKeyPublic.value
            params.value.keyPublic = publicKeyConfig.enable ? `${publicKeyConfig.keyPublicId},${publicKeyConfig.keyPublicText}` : ''
        }
        const { code, msg } = await doEditmerchant(unref(params))
        if (code === 200) {
            ElMessage.success(msg || '更新成功')
            handelBeforeClose()
            initMerchant()
            return
        }
        ElMessage.error(msg || '更新失败')
    } catch (error) {
        console.error(error)
    }
}
const handelBeforeClose = async () => {
    formRef.value!.resetFields()
    formRef.value!.clearValidate()
    // 防止画面闪动
    uploadText.value = false
    uploadTextTwo.value = false
    uploadTextThree.value = false
    isEdit.value = false
    params.value = {
        subjectName: '',
        appid: '',
        detailsId: '',
        keyCert: '',
        keyPrivate: '',
        keyPublic: '',
        mchId: '',
        payType: $props.platformType as any,
        platforms: [],
        appCertPublicKey: '',
        alipayRootCert: '',
    }
    copyParams.value = JSON.parse(JSON.stringify(params.value))
    wechatKeyPublic.value = {
        enable: false,
        keyPublicId: '',
        keyPublicText: '',
    }
    dialogTableVisible.value = false
}
const copyParams = ref<null | ApiParameters>(null)
const handelEdit = (row: ApiParameters) => {
    isEdit.value = true
    params.value = Object.assign({}, row)
    copyParams.value = JSON.parse(JSON.stringify(params.value))
    if (
        params.value.alipayRootCert !== '' &&
        params.value.appCertPublicKey !== '' &&
        (params.value.keyPublic !== '' || params.value.keyPublic !== undefined)
    ) {
        uploadText.value = true
        uploadTextTwo.value = true
        uploadTextThree.value = true
    }
    params.value.appCertPublicKey = params.value.keyCert.split(',')[0] ? params.value.keyCert.split(',')[0] : ''
    params.value.alipayRootCert =
        params.value.keyCert.split(',')[1] === 'undefined' || !params.value.keyCert.split(',')[1] ? '' : params.value.keyCert.split(',')[1]
    const keyPublic = params.value.keyPublic
    const keyPublicConfig = {
        enable: false,
        keyPublicId: '',
        keyPublicText: '',
    }
    if ($props.platformType === 'WECHAT' && keyPublic) {
        keyPublicConfig.enable = true
        const keyPublicTuple = keyPublic.split(',')
        keyPublicConfig.keyPublicId = keyPublicTuple[0]
        keyPublicConfig.keyPublicText = keyPublicTuple[1]
    }
    wechatKeyPublic.value = keyPublicConfig
    dialogTableVisible.value = true
}
/**
 * 上传证书
 * @param {*} file
 */
const token = useAdminInfo().getterToken

/**
 * 上传证书请求头参数
 */
function merchantUploadHeaders() {
    return {
        'Shop-Id': 0,
        Authorization: token,
        'Device-Id': uuidHandle(),
        'Client-Type': 'PLATFORM_CONSOLE',
        Platform: 'PC',
    }
}

//微信支付公钥模式配置
const wechatKeyPublic = ref({
    enable: false,
    keyPublicId: '',
    keyPublicText: '',
})

const handleSuccess = (res: any) => {
    if (res.code !== 200) {
        ElMessage.error(res.msg || '上传失败')
        return
    }
    params.value.keyCert = res.data
    uploadText.value = true
    ElMessage.success('上传成功')
}
const handleKeyPublic = (res: any) => {
    if (res.code !== 200) {
        ElMessage.error(res.msg || '上传失败')
        return
    }
    params.value.keyPublic = res.data
    uploadText.value = true
    ElMessage.success('上传成功')
}
const handleAlipayRootCert = (res: any) => {
    if (res.code !== 200) {
        ElMessage.error(res.msg || '上传失败')
        return
    }
    params.value.alipayRootCert = res.data
    uploadTextTwo.value = true
    ElMessage.success('上传成功')
}
const handleAppCertPublicKey = (res: any) => {
    if (res.code !== 200) {
        ElMessage.error(res.msg || '上传失败')
        return
    }
    params.value.appCertPublicKey = res.data
    uploadTextThree.value = true
    ElMessage.success('上传成功')
}

const disabledJudge = (key: PLATFORMS) => {
    if (!isEdit.value) {
        // 如果不在编辑状态 ，则只要列表中存在的key都不可编辑
        return listParams.value?.some((item) => item.platforms.includes(key))
    } else {
        // 如果在编辑状态，则只要列表中存在的key且不是当前编辑的key都不可编辑
        return listParams.value?.filter((item) => item.detailsId !== copyParams.value?.detailsId).some((item) => item.platforms.includes(key))
    }
}
</script>

<template>
    <div class="handle_container">
        <el-button class="WechatPay-add" round type="primary" @click="dialogTableVisible = true">添加账号</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="listParams"
            :header-cell-style="{ background: '#f6f8fa', color: '#333' }"
            class="WechatPay-table"
            empty-text="暂无数据~"
            size="large"
            style="max-height: calc(100vh - 270px); width: 100%"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <el-table-column label="账号名称" prop="subjectName" />
            <el-table-column label="APPID" prop="appid" />
            <el-table-column label="使用渠道">
                <template #default="{ row }: { row: ApiParameters }">
                    {{ row.platforms?.map((plat) => platforms[plat]).join('、') }}
                </template>
            </el-table-column>
            <el-table-column align="right" fixed="right" label="操作">
                <template #default="{ row }">
                    <el-button link type="primary" @click="handelEdit(row)">编辑</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog
        v-model="dialogTableVisible"
        :before-close="() => handelBeforeClose()"
        :title="isEdit ? '编辑' : '添加'"
        destroy-on-close
        width="550px"
        @closed="() => handelBeforeClose()"
    >
        <el-form ref="formRef" :label-width="$props.platformType === 'ALIPAY' ? '120px' : '120px'" :model="params" :rules="rules">
            <el-form-item :label="$props.platformType === 'ALIPAY' ? '商户名称' : '账号名称'" prop="subjectName">
                <el-input v-model="params.subjectName" maxlength="20" />
            </el-form-item>
            <el-form-item label="选择渠道" prop="platforms">
                <el-checkbox-group v-model="params.platforms">
                    <el-checkbox v-for="(val, key) in platforms" :key="key" :disabled="disabledJudge(key)" :label="key">
                        {{ val }}
                    </el-checkbox>
                </el-checkbox-group>
            </el-form-item>
            <el-form-item label="Appid" prop="appid">
                <el-input v-model="params.appid" maxlength="40" />
            </el-form-item>
            <!-- 支付宝 -->
            <template v-if="$props.platformType === 'ALIPAY'">
                <el-form-item label="支付宝私钥" prop="keyPrivate">
                    <el-input v-model="params.keyPrivate" :rows="2" placeholder="请输入支付宝私钥" type="textarea" />
                </el-form-item>
                <el-form-item label="支付宝公钥证书" prop="keyPublic">
                    <!-- <el-input v-model="params.keyPublic" :rows="2" type="textarea" placeholder="请输入支付宝私钥" /> -->
                    <el-upload
                        ref="keyPublic"
                        :action="`gruul-mall-payment/merchant/upload`"
                        :headers="merchantUploadHeaders()"
                        :http-request="elementUploadRequest"
                        :on-error="() => ElMessage.error('上传失败')"
                        :on-success="handleKeyPublic"
                        accept=".crt"
                    >
                        <template #trigger>
                            <el-button round type="primary">上传</el-button>
                            <div :class="{ 'el-upload__text-show': uploadText }" class="el-upload__text">已上传</div>
                        </template>
                    </el-upload>
                </el-form-item>
                <el-form-item label="支付宝根证书" prop="alipayRootCert">
                    <el-upload
                        ref="alipayRootCert"
                        :action="`gruul-mall-payment/merchant/upload`"
                        :headers="merchantUploadHeaders()"
                        :http-request="elementUploadRequest"
                        :on-error="() => ElMessage.error('上传失败')"
                        :on-success="handleAlipayRootCert"
                        accept=".crt"
                    >
                        <template #trigger>
                            <el-button round type="primary">上传</el-button>
                            <div :class="{ 'el-upload__text-show': uploadTextTwo }" class="el-upload__text">已上传</div>
                        </template>
                    </el-upload>
                </el-form-item>
                <el-form-item label="应用公钥证书" prop="appCertPublicKey">
                    <el-upload
                        ref="appCertPublicKey"
                        :action="`gruul-mall-payment/merchant/upload`"
                        :headers="merchantUploadHeaders()"
                        :http-request="elementUploadRequest"
                        :on-error="() => ElMessage.error('上传失败')"
                        :on-success="handleAppCertPublicKey"
                        accept=".crt"
                    >
                        <template #trigger>
                            <el-button round type="primary">上传</el-button>
                            <div :class="{ 'el-upload__text-show': uploadTextThree }" class="el-upload__text">已上传</div>
                        </template>
                    </el-upload>
                </el-form-item>
            </template>
            <!-- 微信支付 -->
            <template v-else>
                <el-form-item label="商户号" prop="mchId">
                    <el-input v-model="params.mchId" maxlength="60" />
                </el-form-item>
                <el-form-item label="商户APIv3密钥" prop="keyPrivate">
                    <el-input v-model="params.keyPrivate" />
                </el-form-item>
                <el-form-item label="公钥模式" prop="enable">
                    <el-switch v-model="wechatKeyPublic.enable" />
                    <el-popover placement="top" trigger="hover" width="200px">
                        <el-link :underline="false" href="https://pay.weixin.qq.com/doc/v3/merchant/4012153196" target="_blank" type="primary">
                            微信支付新增了对公钥模式的支持，新商户使用公钥模式。
                        </el-link>
                        <template #reference>
                            <QIcon name="icon-wenhao" class="cup" size="16px" style="margin-left: 8px"></QIcon>
                        </template>
                    </el-popover>
                </el-form-item>
                <el-form-item v-if="wechatKeyPublic.enable" label="公钥ID" prop="keyPublicId">
                    <el-input v-model="wechatKeyPublic.keyPublicId" placeholder="请填写微信支付公钥ID" />
                </el-form-item>
                <el-form-item v-if="wechatKeyPublic.enable" label="公钥" prop="keyPublicText">
                    <el-input
                        v-model="wechatKeyPublic.keyPublicText"
                        :rows="2"
                        placeholder="请填写微信支付公钥文本内容（支付公钥下载并打开，复制内容）"
                        type="textarea"
                    />
                </el-form-item>
                <el-form-item label="支付证书" prop="upload">
                    <el-upload
                        ref="upload"
                        :action="`gruul-mall-payment/merchant/upload`"
                        :headers="merchantUploadHeaders()"
                        :http-request="elementUploadRequest"
                        :on-error="() => ElMessage.error('上传失败')"
                        :on-success="handleSuccess"
                        :show-file-list="false"
                        accept=".p12"
                    >
                        <template #trigger>
                            <el-button round type="primary">上传</el-button>
                            <!-- <div style="color: #bcbcbc; font-size: 13px; margin-left: 10px">微信支付 <span style="color: red">APIV3</span> 证书</div> -->
                        </template>
                    </el-upload>
                    <div :class="{ 'el-upload__text-show': uploadText }" class="el-upload__text">已上传</div>
                </el-form-item>
            </template>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="() => handelBeforeClose()">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确认</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(WechatPay-add) {
    font-size: 12px;
    width: 101px;
    height: 36px;
}

@include b(WechatPay-table) {
}

@include b(el-upload) {
    @include e(text) {
        margin-left: 10px;
        display: none;
    }
    @include e(text-show) {
        display: block;
        color: #67c23a;
    }
}
</style>
