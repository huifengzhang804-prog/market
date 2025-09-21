<script setup lang="ts">
import { doGetAmapKey, doEditAmapKey } from '@/apis/amap'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import { AmapKeys } from '@/store/modules/amapKey'
import { ElMessage } from 'element-plus'

const form = ref<AmapKeys>({
    amapKey: '', // 高德 JSAPI key
    amapWebServiceKey: '', // 高德 Web服务 key
    amapsecretKey: '', // 高德 JSAPI 安全密钥
})

const inputCheck = (rule: any, value: any, callback: any) => {
    // 输入只能为数字和小写字母
    const reg = /^[a-z0-9]*$/
    if (!reg.test(value)) {
        callback(new Error('只能输入数字和小写字母'))
    } else {
        callback()
    }
}
const rules = ref({
    amapKey: [
        { required: true, message: '请输入高德 JSAPI key', trigger: 'change' },
        { validator: inputCheck, trigger: 'change' },
    ],
    amapWebServiceKey: [
        { required: true, message: '请输入高德 Web服务 key', trigger: 'change' },
        { validator: inputCheck, trigger: 'change' },
    ],
})
const formRef = ref()

const initAmapKey = async () => {
    const { code, data } = await doGetAmapKey()
    if (code !== 200 || !data) return
    form.value = data
}
initAmapKey()
const handleSubmit = async () => {
    await formRef.value.validate()
    await doEditAmapKey(form.value)
    ElMessage.success('保存成功')
    initAmapKey()
}
</script>

<template>
    <div class="tab_container fdc1">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <TitleBar name="Web服务">
                <template #right>
                    <el-tooltip content="使用场景: H5及PC端IP定位、逆地址解析、周边POI搜索、输入提示等" placement="top">
                        <QIcon name="icon-wenhao" class="cup" size="18px"></QIcon>
                    </el-tooltip>
                </template>
            </TitleBar>
            <el-form-item label="Key" prop="amapWebServiceKey">
                <el-input v-model.trim="form.amapWebServiceKey" placeholder="请输入高德Web服务key"></el-input>
            </el-form-item>
            <TitleBar name="Web端(JS API)">
                <template #right>
                    <el-tooltip content="使用场景: H5及PC端同城配送地图展示、H5及PC端地图选点(地址管理、商家入驻...)等" placement="top">
                        <QIcon name="icon-wenhao" class="cup" size="18px"></QIcon>
                    </el-tooltip>
                </template>
            </TitleBar>
            <el-form-item label="Key" prop="amapKey">
                <el-input v-model.trim="form.amapKey" placeholder="请输入高德 JSAPI key"></el-input>
            </el-form-item>
            <el-form-item label="安全密钥" prop="amapsecretKey">
                <template #label>
                    <el-tooltip placement="top" :offset="0">
                        <template #content>
                            填写此项将通过明文方式设置高德 JSAPI 安全密钥;
                            <br />
                            不填写则通过代理服务器转发,请及时配置代理服务器;
                            <br />
                            转发地址详见当前浏览器的 window._AMapSecurityConfig 属性
                        </template>
                        <div class="fcenter">
                            安全密钥
                            <QIcon style="margin-left: 5px" name="icon-wenhao" class="cup" size="18px"></QIcon>
                        </div>
                    </el-tooltip>
                </template>
                <el-input v-model.trim="form.amapsecretKey" placeholder="请输入高德 JSAPI 安全密钥"></el-input>
            </el-form-item>
        </el-form>
    </div>
    <div class="btn_bottom">
        <el-button style="margin-left: 4%" type="primary" @click="handleSubmit">保存</el-button>
    </div>
</template>

<style lang="scss" scoped>
.el-input {
    width: 50%;
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
