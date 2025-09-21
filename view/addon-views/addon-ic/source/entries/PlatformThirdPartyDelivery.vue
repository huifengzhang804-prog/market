<script setup lang="ts">
import DescStep from '@/views/set/components/DescStep.vue'
import StepDialog from '@/views/set/components/StepDialog.vue'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import { ref, defineProps, type PropType, reactive } from 'vue'
import { Obj } from '@/utils/types'
import { R } from '@/apis/http'
import { ElMessage } from 'element-plus'

interface PropertiesType {
  doUuptOpenConfigUpdateAndEdit: (data: { appid: string; appKey: string; openId: string }) => Promise<R<Obj>>
  doGetUuptOpenConfigInfo: () => Promise<R<Obj>>
}
const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const rules = reactive({
  appid: [{ required: true, message: '请输入app id', trigger: 'blur' }],
  appKey: [{ required: true, message: '请输入app key', trigger: 'blur' }],
  openId: [{ required: true, message: '请输入开发者 openid', trigger: 'blur' }],
})
const printSetForm = reactive({ appid: '', appKey: '', openId: '' })

initCourierInfo()

async function initCourierInfo() {
  const { code, data } = await props.properties.doGetUuptOpenConfigInfo()
  if (code !== 200) return

  printSetForm.appid = data.appid
  printSetForm.appKey = data.appKey
  printSetForm.openId = data.openId
}
const printSetFormRef = ref()
const handleSubmit = async () => {
  try {
    await printSetFormRef.value.validate()
    const { appid, appKey, openId } = printSetForm

    const { code, msg } = await props.properties.doUuptOpenConfigUpdateAndEdit({ appid, appKey, openId })
    if (code !== 200) return ElMessage.error(`${msg}` || 'UU跑腿参数配置失败')
    ElMessage.success('UU跑腿参数配置成功')
    initCourierInfo()
  } catch (error: any) {
    console.log(error)
  }
}
const openThirdPartyOfficialWebsite = () => {
  window.open('https://open.uupt.com/#/')
}
const dialogVisible = ref(false)
const uuptRegistrationInstructionsSteps = [
  {
    title: '注册开放平台',
    steps: [
      {
        text: '登陆UU跑腿官网（https://open.uupt.com/#/）注册开放平台账号',
        link: 'https://open.uupt.com/#/',
      },
      {
        text: '选择【开发者登录】点击立即注册，填写必填信息并提交',
      },
    ],
    imgs: [
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/21671622dbe4b0dd23b7a8b44f.png',
        width: 100,
      },
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/21671622dbe4b0dd23b7a8b450.png',
        width: 100,
      },
    ],
  },
  {
    title: '申请成为开发者',
    steps: [
      {
        text: '使用注册成功的账号（开发者）登录填写真实信息，等待UU跑腿工作人员审核',
      },
      {
        text: '登录成功后点击顶部【管理中心】申请成为开发者',
      },
      {
        text: '填写真实信息，等待UU跑腿工作人员审核',
      },
    ],
    imgs: [
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167162350e4b0dd23b7a8b451.png',
        width: 100,
      },
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2167162350e4b0dd23b7a8b452.png',
        width: 100,
      },
    ],
  },
  {
    title: '添加应用',
    steps: [
      {
        text: '点击左侧菜单栏“我的应用',
      },
      {
        text: '创建应用并获得秘钥，分别复制【Appid、Appkey、Openid】',
      },
    ],
    imgs: [
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/21671623f3e4b0dd23b7a8b453.png',
        width: 100,
      },
      {
        url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/21671623f3e4b0dd23b7a8b454.png',
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
        <DescStep :steps="['①访问第三方配送服务官网', '②完成注册并认证以获取必要参数', '③将获得的参数填写至指定位置', '④完成配置流程']"></DescStep>
      </div>
      <div class="bottom">
        配置说明：此页面用于配置【同城配送】业务的第三方配送服务参数。平台需先完成第三方配送服务的注册，之后商家方可激活并使用该服务进行配送。
      </div>
    </div>
    <TitleBar name="UU跑腿">
      <template #right>
        <el-button type="primary" text @click="openThirdPartyOfficialWebsite">官网</el-button>
        <el-button type="primary" text @click="dialogVisible = true">注册说明</el-button>
      </template>
    </TitleBar>
    <el-form ref="printSetFormRef" :model="printSetForm" label-width="120px" label-position="left" :rules="rules">
      <el-form-item label="Appid" label-width="120px" prop="appid">
        <el-input v-model.trim="printSetForm.appid" placeholder="请输入Appid" style="width: 90%" maxlength="40"></el-input>
      </el-form-item>
      <el-form-item label="AppKey" label-width="120px" prop="appKey">
        <el-input v-model.trim="printSetForm.appKey" placeholder="请输入AppKey" style="width: 90%" maxlength="60"></el-input>
      </el-form-item>
      <el-form-item label="Openid" label-width="120px" prop="openId">
        <el-input v-model.trim="printSetForm.openId" placeholder="请输入Openid" style="width: 90%" maxlength="60"></el-input>
      </el-form-item>
    </el-form>
  </div>
  <el-dialog v-model="dialogVisible" title="注册说明" top="6vh">
    <StepDialog :steps="uuptRegistrationInstructionsSteps"> </StepDialog>
  </el-dialog>
  <div class="btn_bottom">
    <el-button style="margin-left: 4%" type="primary" @click="handleSubmit">保存</el-button>
  </div>
</template>

<style lang="scss" scoped>
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
