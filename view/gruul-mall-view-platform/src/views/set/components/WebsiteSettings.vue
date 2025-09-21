<script setup lang="ts">
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import type { FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { doUpdateSaveOr, queryConfigByModule } from '@/apis/set/websiteSettings'
import type { SaveOrUpdateData } from '@/apis/set/websiteSettings/types'
import QUpload from '@/components/q-upload/q-upload.vue'
import { configurePlatform } from '@/store/modules/configurePlatform'
import WebSetting from '@/q-plugin/supplier/views/components/WebSetting.vue'

const configure = configurePlatform()
/**
 * 响应式变量
 */
let websiteForm = ref<SaveOrUpdateData>({
    PLATFORM_NAME: '', // 公共设置、平台名称
    COPYRIGHT_INFO: '', // 版权信息
    COPYRIGHT_URL: '', // 版权信息、链接地址
    RECORDER_INFO: '', // 备案信息
    RECORDER_URL: '', // 备案信息、链接地址
    WEB_SIT_ICON: '', // 网站图标
    LOGIN_LOGO: '', // 登录logo
    PLATFORM_WEB_SIT_NAME: '', // 平台端、网站名称
    PLATFORM_LOGIN_PAGE_BG: '', // 平台端、背景图
    PLATFORM_LOGO: '', // 平台logo
    SHOP_WEB_SIT_NAME: '', // 商家端、网站名称
    SHOP_LOGIN_PAGE_BG: '', // 商家端、背景图
    SUPPLIER_WEB_SIT_NAME: '', // 供应商端、网站名称
    SUPPLIER_LOGIN_PAGE_BG: '', // 供应商端、背景图
    H5_MALL_NAME: '', //H5商城名称
    PC_MALL_NAME: '', // PC商城名称
    SHOP_MOBILE_NAME: '', // 商家移动端名称
    STORE_MOBILE_NAME: '', // 门店移动端名称
})

const rules = reactive<FormRules<SaveOrUpdateData>>({
    PLATFORM_NAME: [{ required: true, message: '请输入平台名称', trigger: 'blur' }],
    COPYRIGHT_INFO: [{ required: true, message: '请输入版权信息', trigger: 'blur' }],
    RECORDER_INFO: [{ required: true, message: '请输入备案信息', trigger: 'blur' }],
    RECORDER_URL: [{ required: true, message: '请输入链接地址', trigger: 'blur' }],
    WEB_SIT_ICON: [{ required: true, message: '请选择网站图标', trigger: 'blur' }],
    LOGIN_LOGO: [{ required: true, message: '请选择登录logo', trigger: 'blur' }],
    PLATFORM_WEB_SIT_NAME: [{ required: true, message: '请输入平台端网站名称', trigger: 'blur' }],
    PLATFORM_LOGIN_PAGE_BG: [{ required: true, message: '请选择平台端背景图', trigger: 'blur' }],
    PLATFORM_LOGO: [{ required: true, message: '请选择平台logo', trigger: 'blur' }],
    SHOP_WEB_SIT_NAME: [{ required: true, message: '请输入商家端网站名称', trigger: 'blur' }],
    SHOP_LOGIN_PAGE_BG: [{ required: true, message: '请选择商家端背景图', trigger: 'blur' }],
    SUPPLIER_WEB_SIT_NAME: [{ required: true, message: '请输入供应商端网站名称', trigger: 'blur' }],
    SUPPLIER_LOGIN_PAGE_BG: [{ required: true, message: '请选择供应商端背景图', trigger: 'blur' }],
    H5_MALL_NAME: [{ required: true, message: '请输入H5商城网站名称', trigger: 'blur' }],
    PC_MALL_NAME: [{ required: true, message: '请输入PC商城网站名称', trigger: 'blur' }],
    SHOP_MOBILE_NAME: [{ required: true, message: '请输入商家移动端网站名称', trigger: 'blur' }],
    STORE_MOBILE_NAME: [{ required: true, message: '请输入门店移动端网站名称', trigger: 'blur' }],
})
const dialogImageUrl = ref()
const upLoadKey = ref('')
const dialogVisible = ref(false)
const dialogTableVisible = ref(false)
const ruleFormRef = ref()
const fieldsList = ref()

onMounted(() => {
    init()
})

const init = async () => {
    const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING, OTHERS_SETTING')
    if (code === 200) {
        configure.SET_OPEN_ADV(data)
        document.title = configure.getPlatformWebSitName
        document.getElementById('website_icon')?.setAttribute('href', configure.getWebSitIcon)
        websiteForm.value = JSON.parse(JSON.stringify(data))
    }
}

// 公共设置网站图标上传
const changeKey = (key: string) => {
    upLoadKey.value = key
}
const handleChange = (imgSrc: string) => {
    websiteForm.value[upLoadKey.value as keyof typeof websiteForm.value] = imgSrc
    dialogImageUrl.value = imgSrc
}
const submitHandle = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
        if (valid) {
            const res = await doUpdateSaveOr(websiteForm.value)
            ElMessage.success('保存成功')
            init()
        } else {
            console.log('error submit!', fields)
            fieldsList.value = fields
        }
    })
}
const viewImg = (e: string) => {
    dialogVisible.value = true
    dialogImageUrl.value = e
}

const websiteFormChange = (e: SaveOrUpdateData) => {
    if (e) {
        websiteForm.value = e
    }
}
</script>
<template>
    <div class="handle_container">
        <div>
            <el-form
                ref="ruleFormRef"
                :model="websiteForm"
                :rules="rules"
                label-width="100px"
                label-position="right"
                class="demo-ruleForm"
                size="default"
                status-icon
            >
                <el-row style="display: flex; align-items: center">
                    <el-col style="position: relative">
                        <TitleBar name="公共设置" color="#555CFD" style="margin: 0; height: 50px; background-color: #f2f2f4" />
                        <el-button type="primary" text style="position: absolute; right: 20px; top: 20%" @click="dialogTableVisible = true"
                            >示意图</el-button
                        >
                    </el-col>

                    <el-col style="margin-top: 30px">
                        <el-form-item label="平台名称" prop="PLATFORM_NAME" class="mb-25">
                            <el-input v-model="websiteForm.PLATFORM_NAME" type="text" :maxlength="30" show-word-limit placeholder="请输入平台名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="版权信息" prop="COPYRIGHT_INFO" class="mb-25">
                            <el-input
                                v-model="websiteForm.COPYRIGHT_INFO"
                                type="textarea"
                                :maxlength="100"
                                show-word-limit
                                :rows="4"
                                placeholder="请输入内容"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="链接地址" label-width="75" class="mb-25">
                            <el-input v-model="websiteForm.COPYRIGHT_URL" type="text" placeholder="请输入链接地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="备案信息" prop="RECORDER_INFO" class="mb-25">
                            <el-input
                                v-model="websiteForm.RECORDER_INFO"
                                type="textarea"
                                :maxlength="100"
                                show-word-limit
                                :rows="4"
                                placeholder="请输入内容"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="链接地址" label-width="75" class="mb-25">
                            <el-input v-model="websiteForm.RECORDER_URL" type="text" placeholder="请输入链接地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="网站图标" prop="WEB_SIT_ICON">
                            <QUpload
                                v-model:src="websiteForm.WEB_SIT_ICON"
                                :class="[
                                    !websiteForm.WEB_SIT_ICON &&
                                        fieldsList &&
                                        Object.keys(fieldsList).indexOf('WEB_SIT_ICON') !== -1 &&
                                        'borderColor',
                                ]"
                                :isAutoUpload="true"
                                :width="88"
                                :height="88"
                                tip="建议尺寸80*80px，大小1M以下"
                                @change="handleChange"
                                @click="changeKey('WEB_SIT_ICON')"
                            >
                                <template v-if="websiteForm.WEB_SIT_ICON" #mask>
                                    <div class="mask">
                                        <i class="iconfont icon-yulan1" @click.stop="viewImg(websiteForm.WEB_SIT_ICON)"></i>
                                        <i class="iconfont icon-shangchuanyunpan"></i>
                                    </div>
                                </template>
                            </QUpload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="登录logo" prop="LOGIN_LOGO">
                            <QUpload
                                v-model:src="websiteForm.LOGIN_LOGO"
                                :class="[
                                    !websiteForm.LOGIN_LOGO && fieldsList && Object.keys(fieldsList).indexOf('LOGIN_LOGO') !== -1 && 'borderColor',
                                ]"
                                :isAutoUpload="true"
                                :width="88"
                                :height="88"
                                tip="尺寸建议180*180px，大小1M以下"
                                @change="handleChange"
                                @click="changeKey('LOGIN_LOGO')"
                            >
                                <template v-if="websiteForm.LOGIN_LOGO" #mask>
                                    <div class="mask">
                                        <i class="iconfont icon-yulan1" @click.stop="viewImg(websiteForm.LOGIN_LOGO)"></i>
                                        <i class="iconfont icon-shangchuanyunpan"></i>
                                    </div>
                                </template>
                            </QUpload>
                        </el-form-item>
                    </el-col>

                    <el-col>
                        <TitleBar name="平台端" color="#555CFD" style="margin: 0; height: 50px; background-color: #f2f2f4" />
                    </el-col>
                    <el-col style="margin-top: 30px">
                        <el-form-item label="网站名称" prop="PLATFORM_WEB_SIT_NAME" class="mb-25">
                            <el-input
                                v-model="websiteForm.PLATFORM_WEB_SIT_NAME"
                                type="text"
                                :maxlength="30"
                                show-word-limit
                                placeholder="请输入网站名称"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="登录页" prop="PLATFORM_LOGIN_PAGE_BG">
                            <QUpload
                                v-model:src="websiteForm.PLATFORM_LOGIN_PAGE_BG"
                                :class="[
                                    !websiteForm.PLATFORM_LOGIN_PAGE_BG &&
                                        fieldsList &&
                                        Object.keys(fieldsList).indexOf('PLATFORM_LOGIN_PAGE_BG') !== -1 &&
                                        'borderColor',
                                ]"
                                :isAutoUpload="true"
                                :width="88"
                                :height="88"
                                tip="建议尺寸500*500px，大小1M以下"
                                @change="handleChange"
                                @click="changeKey('PLATFORM_LOGIN_PAGE_BG')"
                            >
                                <template v-if="websiteForm.PLATFORM_LOGIN_PAGE_BG" #mask>
                                    <div class="mask">
                                        <i class="iconfont icon-yulan1" @click.stop="viewImg(websiteForm.PLATFORM_LOGIN_PAGE_BG)"></i>
                                        <i class="iconfont icon-shangchuanyunpan"></i>
                                    </div>
                                </template>
                            </QUpload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="平台logo" prop="PLATFORM_LOGO">
                            <QUpload
                                v-model:src="websiteForm.PLATFORM_LOGO"
                                :class="[
                                    !websiteForm.PLATFORM_LOGO &&
                                        fieldsList &&
                                        Object.keys(fieldsList).indexOf('PLATFORM_LOGO') !== -1 &&
                                        'borderColor',
                                ]"
                                :isAutoUpload="true"
                                :width="88"
                                :height="88"
                                tip="建议尺寸180*180px，大小1M以下"
                                @change="handleChange"
                                @click="changeKey('PLATFORM_LOGO')"
                            >
                                <template v-if="websiteForm.PLATFORM_LOGO" #mask>
                                    <div class="mask">
                                        <i class="iconfont icon-yulan1" @click.stop="viewImg(websiteForm.PLATFORM_LOGO)"></i>
                                        <i class="iconfont icon-shangchuanyunpan"></i>
                                    </div>
                                </template>
                            </QUpload>
                        </el-form-item>
                    </el-col>

                    <el-col>
                        <TitleBar name="商家端" color="#555CFD" style="margin: 0; height: 50px; background-color: #f2f2f4" />
                    </el-col>
                    <el-col style="margin-top: 30px">
                        <el-form-item label="网站名称" prop="SHOP_WEB_SIT_NAME" class="mb-25">
                            <el-input
                                v-model="websiteForm.SHOP_WEB_SIT_NAME"
                                type="text"
                                :maxlength="30"
                                show-word-limit
                                placeholder="请输入网站名称"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="登录页" prop="SHOP_LOGIN_PAGE_BG">
                            <QUpload
                                v-model:src="websiteForm.SHOP_LOGIN_PAGE_BG"
                                :class="[
                                    !websiteForm.SHOP_LOGIN_PAGE_BG &&
                                        fieldsList &&
                                        Object.keys(fieldsList).indexOf('SHOP_LOGIN_PAGE_BG') !== -1 &&
                                        'borderColor',
                                ]"
                                :isAutoUpload="true"
                                :width="88"
                                :height="88"
                                tip="建议尺寸500*500px，大小1M以下"
                                @change="handleChange"
                                @click="changeKey('SHOP_LOGIN_PAGE_BG')"
                            >
                                <template v-if="websiteForm.SHOP_LOGIN_PAGE_BG" #mask>
                                    <div class="mask">
                                        <i class="iconfont icon-yulan1" @click.stop="viewImg(websiteForm.SHOP_LOGIN_PAGE_BG)"></i>
                                        <i class="iconfont icon-shangchuanyunpan"></i>
                                    </div>
                                </template>
                            </QUpload>
                        </el-form-item>
                    </el-col>

                    <!-- TODO:抽插件 -->
                    <WebSetting
                        :websiteForm="websiteForm"
                        :fieldsList="fieldsList"
                        :changeKey="changeKey"
                        :handleChange="handleChange"
                        :viewImg="viewImg"
                        :websiteFormChange="websiteFormChange"
                    ></WebSetting>

                    <el-col>
                        <TitleBar name="其他端(网站名称)" color="#555CFD" style="margin: 0; height: 50px; background-color: #f2f2f4" />
                    </el-col>
                    <el-col :span="12" style="margin-top: 30px">
                        <el-form-item label="H5商城" prop="H5_MALL_NAME" class="mb-25">
                            <el-input v-model="websiteForm.H5_MALL_NAME" type="text" :maxlength="30" show-word-limit placeholder="请输入网站名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="margin-top: 30px">
                        <el-form-item label="PC商城" prop="PC_MALL_NAME" class="mb-25">
                            <el-input v-model="websiteForm.PC_MALL_NAME" type="text" :maxlength="30" show-word-limit placeholder="请输入网站名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="商家移动端" prop="SHOP_MOBILE_NAME" class="mb-25">
                            <el-input
                                v-model="websiteForm.SHOP_MOBILE_NAME"
                                type="text"
                                :maxlength="30"
                                show-word-limit
                                placeholder="请输入网站名称"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="门店移动端" prop="STORE_MOBILE_NAME" class="mb-25">
                            <el-input
                                v-model="websiteForm.STORE_MOBILE_NAME"
                                type="text"
                                :maxlength="30"
                                show-word-limit
                                placeholder="请输入网站名称"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
    </div>
    <div class="preservation">
        <el-button type="primary" @click="submitHandle(ruleFormRef)">保存</el-button>
    </div>
    <el-dialog v-model="dialogVisible">
        <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
    <el-dialog v-model="dialogTableVisible" top="2.3vh" title="示意图" width="860">
        <img src="@/assets/image/sketchMap.png" alt="示意图" style="width: 830px; height: auto" />
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(handle_container) {
    width: 100%;
    height: 100%;
    overflow-y: auto;
}
@include b(mb-25) {
    margin-bottom: 25px;
}
.avatar-uploader .avatar {
    width: 90px;
    height: 90px;
    display: block;
}
:deep(.el-input, .el-textarea) {
    width: 470px;
}
:deep(.el-form-item__content) {
    margin-left: 10px;
}

:deep(.is-align-center) {
    align-items: center;
}
:deep(.el-upload__tip) {
    color: #ccc;
}
.borderColor:deep() .el-upload-dragger {
    border: 1px solid red;
}
:deep(.el-upload-dragger) {
    width: 90px;
    height: 90px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--el-border-color);
    div {
        display: flex;
    }
    img {
        width: 90px;
        height: 90px;
    }
}
:deep(.el-upload-dragger:hover) {
    border-color: #555cfd;
}
//遮罩层
:deep(.mask) {
    position: absolute;
    top: 0px;
    left: 0px;
    width: 88px;
    height: 88px;
    background: rgba(30, 29, 29, 0.7);
    color: #ffffff;
    opacity: 0;
    border-radius: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    &:hover {
        opacity: 1;
    }
    i {
        font-size: 20px;
        margin: 0 10px;
        &:hover {
            color: #70aeed;
        }
    }
}
@include b(preservation) {
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    height: 60px;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
</style>
