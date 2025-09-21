<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { doSmsSet, doGetSms, getSmsList, SmsEnabled, deleteSms } from '@/apis/setting'
import { AliyunConfig } from './fileSet'

const dialogVisible = ref(false)
const title = ref('新增')
const tableList = ref<AliyunConfig[]>([])
const enumServerForm = reactive({
    TENCENT: {
        type: 'TENCENT',
        // 说明
        description: '',
        // 短信类型
        smsType: '',
        // 供应商
        providerName: '腾讯',
        // 供应商APPid
        providerAppId: '',
        // 供应商appSecrete
        providerAppSecret: '',
        // 短信签名
        signature: '',
        // 模板信息
        smsTemplate: {},
        // 图形验证
        enableCaptcha: false,
    },
    ALIYUN: {
        type: 'ALIYUN',
        // 说明
        description: '',
        // 短信类型
        smsType: '',
        // 供应商
        providerName: '阿里',
        // 供应商APPid
        providerAppId: '',
        // 供应商appSecrete
        providerAppSecret: '',
        // 短信签名
        signature: '',
        // 模板信息
        smsTemplate: {},
        // 图形验证
        enableCaptcha: false,
    },
})
const smsTemplateForm = ref(
    // templateName smsTemplateType smsTemplateContent 后端采取默认数据 不会采取前端数据的 所以无需用户填写
    {
        templateName: '禁止用户输入字段',
        templateCode: '',
        smsTemplateType: 'CAPTCHA',
        smsTemplateContent: '您的验证码为 ${code}，请勿告知他人',
    },
)
const currentRadio = ref<'TENCENT' | 'ALIYUN'>('TENCENT')
const currentRadioList = ref<any[]>([
    { name: 'TENCENT', title: '腾讯' },
    { name: 'ALIYUN', title: '阿里' },
])

const textMessageMethodMap = {
    REAL: '真实短信',
    VIRTUAL: '虚拟短信',
}

const currentForm = computed(() => {
    return enumServerForm[currentRadio.value]
})

const getServiceProvider = (name: string) => {
    const radio = currentRadioList.value.find((item) => item.name === name)
    return radio ? radio.title : ''
}

onMounted(() => {
    getTableList()
})

const handleOperate = (type: 'TENCENT' | 'ALIYUN', val: string) => {
    dialogVisible.value = true
    title.value = val
    initForm(type)
    currentRadio.value = type
}

/**
 * 启用禁用状态
 */
const handleSwitch = async (type: string) => {
    const { code, msg } = await SmsEnabled(type)
    if (code !== 200) {
        ElMessage.success(msg)
    }
    getTableList()
}

// 删除
const handleDelete = (type: string) => {
    ElMessageBox.confirm('删除后无法恢复，确定删除吗？', '删除确认', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
    })
        .then(async () => {
            const { code, msg } = await deleteSms(type)
            if (code !== 200) {
                ElMessage.error(msg)
            } else {
                ElMessage.success('删除成功')
                getTableList()
            }
        })
        .catch(() => {})
}

const validFormat = () => {
    return Object.values(enumServerForm[currentRadio.value]).includes('') || Object.values(smsTemplateForm.value).includes('')
}
const submitHandle = async () => {
    if (validFormat()) {
        ElMessage.error('请填写完成表单')
        return
    }
    enumServerForm[currentRadio.value].smsTemplate = { ...smsTemplateForm.value }
    console.log(enumServerForm[currentRadio.value], smsTemplateForm.value)
    const { code, success, msg } = await doSmsSet(enumServerForm[currentRadio.value])
    if (code === 200 && success) {
        ElMessage.success(title.value + '成功')
        dialogVisible.value = false
        getTableList()
    } else {
        ElMessage.error(msg || title.value + '失败')
    }
}

const getTableList = async () => {
    const { code, data } = await getSmsList()
    if (code === 200) {
        tableList.value = data
    }
}

async function initForm(val: any) {
    reset()
    const { code, data, success } = await doGetSms(val)
    if (code === 200 && success) {
        if (data?.smsTemplate) {
            smsTemplateForm.value = data.smsTemplate
        }
        Reflect.deleteProperty(data, 'smsTemplate')
        enumServerForm[currentRadio.value] = {
            type: val,
            ...data,
        }
    }
}
function reset() {
    smsTemplateForm.value = {
        // templateName smsTemplateType smsTemplateContent 后端采取默认数据 不会采取前端数据的 所以无需用户填写
        templateName: '禁止用户输入字段',
        templateCode: '',
        smsTemplateType: 'CAPTCHA',
        smsTemplateContent: '您的验证码为 ${code}，请勿告知他人',
    }
}

const typeList = (tableList: AliyunConfig[]) => {
    return currentRadioList.value.filter((item) => !tableList.some((excludeType: AliyunConfig) => excludeType.type === item.name))
}

const changeType = (val: string) => {
    if (val === 'VIRTUAL') {
        currentForm.value.providerAppId = '1'
        currentForm.value.providerAppSecret = '1'
        currentForm.value.signature = '1'
        smsTemplateForm.value.templateCode = '1'
    } else {
        currentForm.value.providerAppId = ''
        currentForm.value.providerAppSecret = ''
        currentForm.value.signature = ''
        smsTemplateForm.value.templateCode = ''
    }
}
</script>

<template>
    <div class="handle_container">
        <el-button :disabled="!typeList(tableList)[0]?.name" type="primary" @click="handleOperate(typeList(tableList)[0]?.name as any, '新增')"
            >新增</el-button
        >
        <span style="color: #999; margin-left: 12px">只需配置1个服务商即可，处于开启状态的只能有1个；</span>
    </div>
    <div class="table_container">
        <el-table
            :data="tableList"
            :header-row-style="{ fontSize: '14px' }"
            :header-cell-style="{ background: '#F7F8FA', color: '#333333', height: '48px' }"
            :cell-style="{ fontSize: '14px', color: '#333' }"
        >
            <template #empty><ElTableEmpty /></template>
            <el-table-column label="服务商" width="120">
                <template #default="{ row }">{{ getServiceProvider(row.type) }}</template>
            </el-table-column>
            <el-table-column label="短信类型" width="120">
                <template #default="{ row }">{{ textMessageMethodMap[row.smsType as keyof typeof textMessageMethodMap] }}</template>
            </el-table-column>
            <el-table-column label="说明">
                <template #default="{ row }">{{ row.description }}</template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
                <template #default="{ row }">{{ row.createTime }}</template>
            </el-table-column>
            <el-table-column label="更新时间" width="180">
                <template #default="{ row }">{{ row.updateTime }}</template>
            </el-table-column>
            <el-table-column label="状态" width="140">
                <template #default="{ row }">
                    <el-switch v-model="row.using" :disabled="row.using" @change="handleSwitch(row.type)" />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="right">
                <template #default="{ row }">
                    <el-button type="primary" link @click="handleOperate(row.type, '编辑')">编辑</el-button>
                    <el-button v-if="!row.using" type="danger" style="margin-left: 10px" link @click="handleDelete(row.type)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog v-model="dialogVisible" :title="title + `短信`" width="779">
        <div style="padding: 16px">
            <el-tabs v-model="currentRadio" tab-position="left" class="demo-tabs" @tab-click="initForm">
                <el-tab-pane v-for="item in currentRadioList" :key="item.name" :label="item.title" :name="item.name">
                    <el-form ref="formRef" :model="currentForm" label-width="210px">
                        <el-form-item label="服务商" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                            <el-select v-model="currentRadio" :disabled="title === '编辑'" placeholder="请选择服务商" class="cusInput">
                                <el-option
                                    v-for="item in title === '新增' ? typeList(tableList) : currentRadioList"
                                    :key="item.name"
                                    :label="item.title"
                                    :value="item.name"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="短信类型" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                            <el-select v-model="currentForm.smsType" placeholder="请选择服务商" class="cusInput" @change="changeType">
                                <el-option v-for="(item, key) in textMessageMethodMap" :key="key" :label="item" :value="key" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="说明" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                            <el-input v-model.trim="currentForm.description" placeholder="请输入说明" maxlength="30" class="cusInput" />
                        </el-form-item>
                        <template v-if="currentForm.smsType !== 'VIRTUAL'">
                            <el-form-item label="应用ID (providerAppId)" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                                <el-input v-model.trim="currentForm.providerAppId" placeholder="请输入应用ID（providerAppId）" class="cusInput" />
                            </el-form-item>
                            <el-form-item label="应用KEY (providerAppSecret)" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                                <el-input
                                    v-model.trim="currentForm.providerAppSecret"
                                    placeholder="请输入应用KEY（providerAppSecret）"
                                    class="cusInput"
                                />
                            </el-form-item>
                            <el-form-item label="签名内容 (signature)" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                                <el-input v-model.trim="currentForm.signature" placeholder="请输入签名内容（signature）" class="cusInput" />
                            </el-form-item>
                            <el-form-item label="模板ID (templateCode)" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                                <el-input v-model.trim="smsTemplateForm.templateCode" placeholder="请输入模板ID（templateCode）" class="cusInput" />
                            </el-form-item>
                        </template>
                        <el-form-item label="图形验证" :rules="[{ required: true, message: '', trigger: 'blur' }]">
                            <el-switch
                                v-model="currentForm.enableCaptcha"
                                style="--el-switch-on-color: #13ce66"
                                :active-value="true"
                                :inactive-value="false"
                            />
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </div>
        <template #footer>
            <div class="preservation">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitHandle">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>
<style lang="scss">
@import './messageBox.scss';
</style>
<style lang="scss" scoped>
@include b(handle_container) {
    display: flex;
    justify-content: start;
    align-items: center;
}
@include b(tab_container) {
    width: 100%;
    height: 100%;
}
@include b(custom-tabs-label) {
    width: 45px;
    text-align: left;
}
@include b(cusInput) {
    height: 30px;
}

@include b(btn-icon) {
    position: absolute;
    inset: 0;
}

@include b(text-color) {
    position: relative;
    color: #fff;
    background-color: transparent;
    z-index: 3;
}

@include b(active) {
    background-color: #409eff;
    color: #fff;
    @include b(el-icon) {
        position: absolute;
        top: 0;
        right: 0;
    }
}

@include b(el-radio-group) {
    :deep(.el-radio-button__inner) {
        --el-radio-button-checked-text-color: --el-text-color-regular;
        --el-radio-button-checked-bg-color: #fff;
    }
}

@include b(m-t-20) {
    margin-top: 20px;
}

@include b(el-form-item) {
    :deep(.el-form-item__label) {
        display: flex;
        align-items: center;
    }
}

@include b(label-icon) {
    margin: 0 5px;
    font-size: 18px;
    cursor: pointer;
}
@include b(preservation) {
    text-align: center;
}
</style>
