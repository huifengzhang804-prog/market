<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage, UploadProps } from 'element-plus'
import { getsettings, postsettings } from '@/apis/mall/settings'
import type { FormInstance } from 'element-plus'
import type { ShopInfoStore } from '@/store/modules/shopInfo/state'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = (id: string) => {
    dialogVisible.value = true
    parameterId.value = id
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    if (parameterId.value === 'logo') queryForm.value.logo = val?.shift() || ''
    else if (parameterId.value === 'headBackground') queryForm.value.headBackground = val?.shift() || ''
}
const checkedFileLists = (val: string[]) => {
    if (parameterId.value === 'logo') queryForm.value.logo = val?.shift() || ''
    else if (parameterId.value === 'headBackground') queryForm.value.headBackground = val?.shift() || ''
}
// 选择素材 d

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
    no: '',
})
const queryFormRef = ref<FormInstance>()
const basicRules = reactive({
    logo: [
        {
            required: true,
            message: '请填写供应商logo',
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
            message: '请填写供应商名称',
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
            message: '请填写供应商电话',
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
            message: '请填写供应商介绍',
            trigger: 'blur',
        },
    ],
})

onMounted(() => {
    Getsettings()
})

/**
 * @description: 获取数据
 * @returns
 */
const Getsettings = () => {
    getsettings({}).then((res) => {
        if (res.data.start && res.data.end) {
            res.data.businessHours = [res.data.start, res.data.end]
        }
        const { logo, name, newTips, status, id } = res.data
        updateShopInfo({ logo, name, newTips, status, id })
        queryForm.value = res.data
    })
}
const updateShopInfo = (patams: ShopInfoStore) => {
    useShopInfoStore().SET_SHOP_INFO(patams)
}
/**
 * @description: 添加/修改供应商logo
 * @returns {UploadProps}
 */
const logoSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    queryForm.value.logo = response.data
}
/**
 * @description: 保存
 * @returns
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
            .then(() => {
                ElMessage.success('保存成功')
                Getsettings()
            })
            .catch((err) => {
                ElMessage.error(err)
            })
    })
}
const shopType = {
    SELF_OWNED: '自营',
    PREFERRED: '优选',
    ORDINARY: '普通',
}
</script>

<template>
    <div class="shoplist fdc">
        <el-form ref="queryFormRef" :model="queryForm" :rules="basicRules" label-width="100px" label-position="right">
            <el-form-item label="供应商ID"> {{ queryForm.no }} </el-form-item>
            <el-form-item label="供应商类型">
                {{ shopType[queryForm.shopType as keyof typeof shopType] }}
            </el-form-item>
            <!-- <el-form-item label="经营模式"> {{ queryForm.mode }} </el-form-item> -->
            <el-form-item label="供应商logo" prop="logo">
                <!-- <q-upload v-model:src="queryForm.logo" :format="{ size: 1 }" :width="93" :height="93" /> -->

                <div style="display: flex; align-items: end">
                    <div v-if="!queryForm.logo" class="selectMaterialStyle" @click="buttonlFn('logo')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img v-else :src="queryForm.logo" class="selectMaterialStyle" alt="" @click="buttonlFn('logo')" />
                    <div class="shoplist__logo--word">尺寸建议大小1M以下</div>
                </div>
            </el-form-item>
            <el-form-item label="头部背景" prop="headBackground">
                <!-- <q-upload v-model:src="queryForm.headBackground" :format="{ size: 1 }" :width="178" :height="90" /> -->

                <div style="display: flex; align-items: end">
                    <div
                        v-if="!queryForm.headBackground"
                        style="width: 230px; height: 90px"
                        class="selectMaterialStyle"
                        @click="buttonlFn('headBackground')"
                    >
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img
                        v-else
                        style="width: 230px; height: 90px"
                        :src="queryForm.headBackground"
                        class="selectMaterialStyle"
                        alt=""
                        @click="buttonlFn('headBackground')"
                    />
                    <div class="shoplist__headBackground--word">尺寸建议750*300px，大小1M以下</div>
                </div>
            </el-form-item>
            <el-form-item label="供应商名称" prop="name">
                <el-input v-model="queryForm.name" type="text" style="width: 405px" placeholder="请填写供应商名称" maxlength="20" />
            </el-form-item>
            <el-form-item label="供应商电话" prop="contractNumber">
                <el-input v-model="queryForm.contractNumber" type="text" style="width: 405px" placeholder="请填写供应商电话" maxlength="11" />
            </el-form-item>
            <el-form-item label="营业时间" prop="businessHours" style="width: 525px">
                <el-time-picker
                    v-model="queryForm.businessHours"
                    size="large"
                    is-range
                    value-format="HH:mm:ss"
                    range-separator="-"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    placeholder="选择营业时间"
                >
                </el-time-picker>
            </el-form-item>
            <el-form-item label="上新公告" prop="newTips">
                <el-input v-model="queryForm.newTips" type="textarea" :rows="4" style="width: 405px" placeholder="请填写上新公告" maxlength="40" />
            </el-form-item>
            <el-form-item label="供应商介绍" prop="briefing">
                <el-input
                    v-model="queryForm.briefing"
                    type="textarea"
                    :rows="4"
                    style="width: 405px"
                    placeholder="请填写供应商介绍"
                    maxlength="150"
                />
            </el-form-item>
        </el-form>
        <div class="shoplist__save ccenter">
            <el-button round type="primary" @click="handleSave">保存</el-button>
        </div>
    </div>
    <!-- 选择素材 e -->
    <selectMaterial
        :upload-files="1"
        :dialog-visible="dialogVisible"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
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
    width: 90px;
    height: 90px;
    border-radius: 5px;
    background-color: #f6f6fa;
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
