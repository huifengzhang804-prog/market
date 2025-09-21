<script lang="ts" setup>
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage, UploadProps } from 'element-plus'
import { getsettings, postsettings } from '@/apis/mall/settings'
import type { FormInstance } from 'element-plus'
import type { ShopInfoStore } from '@/store/modules/shopInfo/state'

// 选择素材 e
import SelectMaterial from '@/views/material/selectMaterial.vue'

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const shopType = {
    SELF_OWNED: '自营',
    PREFERRED: '优选',
    ORDINARY: '普通',
}

const shopMode = {
    COMMON: '线上模式',
    SUPPLIER: '供应商模式',
    O2O: 'O2O模式',
}
const buttonlFn = (val: string) => {
    dialogVisible.value = true
    parameterId.value = val
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string[]) => {
    if (parameterId.value === 'logo') queryForm.value.logo = val?.shift() || ''
    else if (parameterId.value === 'headBackground') queryForm.value.headBackground = val?.shift() || ''
}
const checkedFileLists = (val: string[]) => {
    if (parameterId.value === 'logo') queryForm.value.logo = val?.shift() || ''
    else if (parameterId.value === 'headBackground') queryForm.value.headBackground = val?.shift() || ''
}

const queryForm = ref({
    id: '',
    logo: '',
    headBackground: '',
    name: '',
    businessHours: ['00:00:00', '23:59:59'],
    shopType: 'SELF_OWNED',
    contractNumber: '',
    newTips: '',
    briefing: '',
    start: '00:00:00',
    end: '23:59:59',
    shopMode: '',
    no: '',
    score: '',
})
const queryFormRef = ref<FormInstance>()
const basicRules = reactive({
    logo: [
        {
            required: true,
            message: '请填写店铺logo',
            trigger: 'blur',
        },
    ],
    headBackground: [
        {
            required: true,
            message: '请填写头部背景',
            trigger: 'blur',
        },
    ],
    name: [
        {
            required: true,
            message: '请填写店铺名称',
            trigger: 'blur',
        },
    ],
    businessHours: [
        {
            required: true,
            message: '请填写营业时间',
            trigger: 'blur',
        },
    ],
    contractNumber: [
        {
            required: true,
            message: '请填写店铺电话',
            trigger: 'blur',
        },
    ],
    newTips: [
        {
            required: true,
            message: '请填写上新公告',
            trigger: 'blur',
        },
    ],
    briefing: [
        {
            required: true,
            message: '请填写店铺介绍',
            trigger: 'blur',
        },
    ],
})

onMounted(() => {
    Getsettings()
})

/**
 * 获取数据
 */
const Getsettings = () => {
    getsettings({}).then((res) => {
        if (res.data.start && res.data.end) {
            res.data.businessHours = [res.data.start, res.data.end]
        }
        const { logo, name, shopMode } = res.data
        updateShopInfo({ logo, name, shopMode })
        queryForm.value = res.data
    })
}
const updateShopInfo = (patams: ShopInfoStore | any) => {
    useShopInfoStore().SET_SHOP_INFO(patams)
}
/**
 * 保存
 */
const handleSave = () => {
    queryFormRef.value!.validate((valid) => {
        if (!valid) {
            return
        }
        const { businessHours } = queryForm.value
        if (Array.isArray(businessHours)) {
            queryForm.value.start = businessHours[0]
            queryForm.value.end = businessHours[1]
        }
        postsettings(queryForm.value)
            .then(({ code }) => {
                if (code === 200) {
                    ElMessage.success('保存成功')
                    Getsettings()
                }
            })
            .catch((err) => {
                ElMessage.error(err)
            })
    })
}
</script>

<template>
    <div class="shoplist fdc">
        <el-form ref="queryFormRef" :model="queryForm" :rules="basicRules" label-position="right" label-width="90px" style="color: #666">
            <el-form-item label="店铺ID"> {{ queryForm.no }}</el-form-item>
            <el-form-item label="店铺类型">
                {{ shopType[queryForm.shopType as keyof typeof shopType] }}
            </el-form-item>
            <el-form-item label="经营模式"> {{ shopMode[queryForm.shopMode as keyof typeof shopMode] }}</el-form-item>
            <el-form-item label="店铺评分"> {{ queryForm.score }}</el-form-item>
            <div style="display: flex; align-items: end">
                <el-form-item label="店铺logo" prop="logo">
                    <div v-if="!queryForm.logo" alt="" class="selectMaterialStyle" @click="buttonlFn('headBackground')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <video
                        v-else-if="queryForm.logo?.[0] && queryForm.logo?.[0].split('.').pop() === 'mp4'"
                        :src="queryForm.logo"
                        class="selectMaterialStyle"
                        @click="buttonlFn('logo')"
                    ></video>
                    <img v-else :src="queryForm.logo" alt="" class="selectMaterialStyle" @click="buttonlFn('logo')" />
                    <!-- <q-upload v-model:src="queryForm.logo" :format="{ size: 1 }" :height="93" :width="93" /> -->
                </el-form-item>
                <div class="shoplist__logo--word">尺寸建议大小1M以下</div>
            </div>
            <div style="display: flex; align-items: end">
                <el-form-item label="头部背景" prop="headBackground">
                    <div v-if="!queryForm.headBackground" alt="" class="selectMaterialStyle" @click="buttonlFn('headBackground')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <video
                        v-else-if="queryForm.headBackground?.[0] && queryForm.headBackground?.[0].split('.').pop() === 'mp4'"
                        :src="queryForm.headBackground"
                        class="selectMaterialStyle"
                        @click="buttonlFn('headBackground')"
                    ></video>
                    <img v-else :src="queryForm.headBackground" alt="" class="selectMaterialStyle" @click="buttonlFn('headBackground')" />
                    <!-- <q-upload v-model:src="queryForm.headBackground" :format="{ size: 1 }" :height="90" :width="178" /> -->
                </el-form-item>
                <div class="shoplist__headBackground--word">尺寸建议750*300px，大小1M以下</div>
            </div>

            <el-form-item label="店铺名称" prop="name">
                <el-input v-model="queryForm.name" maxlength="20" placeholder="请填写店铺名称" style="width: 405px" type="text" />
            </el-form-item>
            <el-form-item label="营业时间" prop="businessHours" style="width: 525px">
                <el-time-picker
                    v-model="queryForm.businessHours"
                    end-placeholder="结束时间"
                    is-range
                    placeholder="选择营业时间"
                    range-separator="-"
                    size="large"
                    start-placeholder="开始时间"
                    value-format="HH:mm:ss"
                >
                </el-time-picker>
            </el-form-item>
            <el-form-item label="店铺电话" prop="contractNumber">
                <el-input v-model="queryForm.contractNumber" maxlength="11" placeholder="请填写店铺电话" style="width: 405px" type="text" />
            </el-form-item>
            <el-form-item label="上新公告" prop="newTips">
                <el-input v-model="queryForm.newTips" maxlength="40" placeholder="请填写上新公告" style="width: 405px" type="textarea" />
            </el-form-item>
            <el-form-item label="店铺介绍" prop="briefing">
                <el-input v-model="queryForm.briefing" maxlength="150" placeholder="请填写店铺介绍" style="width: 405px" type="textarea" />
            </el-form-item>
        </el-form>
        <div class="shoplist__save ccenter">
            <el-button round type="primary" @click="handleSave">保存</el-button>
        </div>
        <!-- 选择素材 e -->
        <SelectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            @checkedFileLists="checkedFileLists"
            @croppedFileChange="croppedFileChange"
            @selectMaterialFn="selectMaterialFn"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

@include b(shoplist) {
    padding-top: 15px;
    flex: 1;
    position: relative;
    overflow-y: scroll;
    .el-form {
        padding-left: 35px;
        flex: 1;
    }
    @include e(logo) {
        @include m(word) {
            margin-left: 15px;
            margin-bottom: 20px;
            font-size: 12px;
            font-family: sans-serif, sans-serif-Normal;
            font-weight: Normal;
            text-align: LEFT;
            color: #999;
        }
    }

    @include e(headBackground) {
        @include m(word) {
            margin-left: 15px;
            margin-bottom: 20px;
            font-size: 12px;
            font-family: sans-serif, sans-serif-Normal;
            font-weight: Normal;
            text-align: LEFT;
            color: #999;
        }
    }

    @include e(save) {
        position: sticky;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 52px;
        box-shadow: 0px 4px 20px 4px rgba(0, 0, 0, 0.1);
        background: rgb(255, 255, 255);
        flex-shrink: 0;
    }
}

@include b(selectMaterialStyle) {
    width: 93px;
    height: 93px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 20px;
    }
}

:deep(.el-form-item__label) {
    margin-right: 20px;
    color: #666;
}
</style>
