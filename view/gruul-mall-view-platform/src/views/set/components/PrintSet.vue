<script setup lang="ts">
import { ElMessage } from 'element-plus'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import { doPrintUpdateAndEdit, doGetPrintInfo } from '@/apis/set/PrintSet'
import DescStep from '@/views/set/components/DescStep.vue'
import StepDialog from '@/views/set/components/StepDialog.vue'
import { PrinterSetConfig } from '@/apis/set/PrintSet/types'

const printSetFormRef = ref()
const rules = reactive({
    user: [{ required: true, message: '请输入用户名(User)', trigger: 'blur' }],
    ukey: [{ required: true, message: '请输入秘钥(UKEY)', trigger: 'blur' }],
})
const printSetForm = reactive<PrinterSetConfig>({ ukey: '', user: '' })

initCourierInfo()

async function initCourierInfo() {
    const { code, data } = await doGetPrintInfo()
    if (code !== 200) return
    printSetForm.user = data.user
    printSetForm.ukey = data.ukey
}
const handleSubmit = async () => {
    try {
        await printSetFormRef.value.validate()
        const { ukey, user } = printSetForm
        const { code, msg } = await doPrintUpdateAndEdit({ user, ukey })
        if (code !== 200) return ElMessage.error(msg || '打印机参数配置失败')
        ElMessage.success('打印机参数配置成功')
    } catch (error: any) {
        console.log(error)
    }
}
const openThirdPartyOfficialWebsite = () => {
    window.open('https://feieyun.com/')
}
const dialogVisible = ref(false)
const printerRegistrationInstructionsSteps = [
    {
        title: '注册飞鹅账号',
        steps: [
            {
                text: '登录飞鹅官网（https://feieyun.com/）注册账号',
                link: 'https://feieyun.com/',
            },
            {
                text: '选择【中国站】进入登录页面',
            },
            {
                text: '点击立即注册，填写完整信息提交即可',
            },
        ],
        imgs: [
            {
                url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/21671616dfe4b0dd23b7a8b44a.png',
                width: 100,
            },
            {
                url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167161962e4b0dd23b7a8b44b.png',
                width: 50,
            },
            {
                url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167161978e4b0dd23b7a8b44c.png',
                width: 50,
            },
        ],
    },
    {
        title: '实名制认证',
        steps: [
            {
                text: '使用注册成功的账号（邮箱）登录',
            },
            {
                text: '登录成功后点击左侧【个人中心】→【实名认证】',
            },
            {
                text: '填写真实信息，等待飞鹅工作人员审核',
            },
            {
                text: '认证完成后，分别复制【USER、UKEY】',
            },
        ],
        imgs: [
            {
                url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167161a28e4b0dd23b7a8b44d.png',
                width: 100,
            },
            {
                url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167161a2ee4b0dd23b7a8b44e.png',
                width: 100,
            },
        ],
    },
]
</script>

<template>
    <div class="tab_container fdc1">
        <div class="describe_bar">
            <div class="top">
                开通流程：
                <DescStep
                    :steps="['①访问打印机品牌官网', '②完成注册并认证以获取必要参数', '③将获得的参数填写至指定位置', '④完成配置流程']"
                ></DescStep>
            </div>
            <div class="bottom">
                配置说明：此页面用于配置【同城配送、到店自提】
                业务的打印机参数。平台需先完成打印机平台的注册及实名制认证，之后商家才能正常添加打印机。
            </div>
        </div>
        <TitleBar name="飞鹅打印机">
            <template #right>
                <el-button type="primary" text @click="openThirdPartyOfficialWebsite">官网</el-button>
                <el-button type="primary" text @click="dialogVisible = true">注册说明</el-button>
            </template>
        </TitleBar>
        <el-form ref="printSetFormRef" :model="printSetForm" label-width="120px" label-position="left" :rules="rules">
            <el-form-item label="用户名(User)" label-width="120px" prop="user">
                <el-input v-model.trim="printSetForm.user" placeholder="请输入登录飞鹅云后台的用户名" style="width: 90%" maxlength="40"></el-input>
            </el-form-item>
            <el-form-item label="秘钥(UKEY)" label-width="120px" prop="ukey">
                <el-input
                    v-model.trim="printSetForm.ukey"
                    placeholder="请输入飞鹅云后台实名制认证后生成的UKEY"
                    style="width: 90%"
                    maxlength="60"
                ></el-input>
            </el-form-item>
        </el-form>
    </div>
    <el-dialog v-model="dialogVisible" title="注册说明" top="6vh">
        <StepDialog :steps="printerRegistrationInstructionsSteps"> </StepDialog>
    </el-dialog>
    <div class="btn_bottom">
        <el-button style="margin-left: 4%" type="primary" @click="handleSubmit">保存</el-button>
    </div>
</template>

<style scoped lang="scss">
.describe_bar {
    background: rgba(85, 92, 253, 0.05);
    padding-top: 20px;
    padding-left: 16px;
    padding-right: 16px;
    padding-bottom: 13px;
    .top {
        display: flex;
        flex-wrap: wrap;
    }
    .bottom {
        margin-top: 11px;
        color: rgb(51, 51, 51);
        font-size: 13px;
        font-weight: 400;
    }
}
.btn_bottom {
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    height: 52px;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
</style>
