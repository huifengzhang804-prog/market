<script setup lang="ts">
import { FormRules } from 'element-plus'
import { REGEX_MOBILE } from '@/libs/validate'
import UserSelect from './UserSelect.vue'
import ContractCategory from './ContractCategory/index.vue'
import type { Ref, PropType } from 'vue'
import { UserType, ShopFormType } from '../types'
import type { FormInstance } from 'element-plus'

// 选择素材 e
import SelectMaterial from '@/views/material/selectMaterial.vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { Checked, Geometry, GeometryType } from '@/apis/afs/type'
import { doGetAddressByLocation } from '@/apis/address'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = (val: string) => {
    if ($route.meta.disabled) return
    dialogVisible.value = true
    parameterId.value = val
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    // console.log(val)
    let logo = val
    if (Array.isArray(val)) logo = val?.shift()
    if (parameterId.value === 'logo') submitForm.value.logo = logo
}
const checkedFileLists = (val: any) => {
    // console.log(val)
    let logo = val
    if (Array.isArray(val)) logo = val?.shift()
    if (parameterId.value === 'logo') submitForm.value.logo = logo
}
// 选择素材 d

const $route = useRoute()
//父组件
const $parent = inject('addShops')
const submitForm = ($parent as { submitForm: Ref<ShopFormType> }).submitForm

const currentFormRef = ref<FormInstance>()
const componentFlag = ref('base')
const userSelectStatus = ref(false)
const userInfo = ref<UserType>()
const $props = defineProps({
    supplierViewModel: {
        type: String as PropType<'供应商' | '店铺'>,
        default: '店铺',
    },
})
defineExpose({
    currentFormRef,
    componentFlag,
})

const baseFormRules = reactive<FormRules>({
    area: [{ required: true, message: '请选择定位', trigger: 'change' }],
    name: [
        {
            required: true,
            message: `请填写${$props.supplierViewModel}名称`,
            trigger: 'blur',
        },
    ],
    companyName: [
        {
            required: true,
            message: `请填写商户名称`,
            trigger: 'blur',
        },
    ],
    address: [
        {
            required: true,
            message: '请选择地址',
            trigger: 'blur',
        },
        { min: 2, max: 200, message: '输入长度必须在2~200以内', trigger: 'blur' },
    ],
    logo: [
        {
            required: true,
            message: `请上传${$props.supplierViewModel}logo`,
            trigger: 'blur',
        },
    ],
    username: [
        {
            required: true,
            message: '请填写登录账户',
            trigger: 'blur',
        },
    ],
    contractNumber: [
        {
            required: true,
            validator: checkPhone,
            trigger: 'blur',
        },
    ],
    briefing: [
        {
            required: true,
            message: `请填写${$props.supplierViewModel}介绍`,
            trigger: 'blur',
        },
        { min: 2, max: 200, message: '输入长度必须在2~200以内', trigger: 'blur' },
    ],
    userId: [
        {
            required: true,
            message: '请选择用户',
            trigger: 'blur',
        },
    ],
    drawPercentage: [
        {
            validator: (_, value) => {
                if (submitForm.value.extractionType === 'ORDER_SALES_EXTRACTION') {
                    if (!value) {
                        return new Error('请输入订单金额提佣百分比')
                    } else if (!/^(?:[1-9]?\d|100)$/.test(value)) {
                        return new Error('请填写0-100的数字')
                    }
                }
                return true
            },
            trigger: 'blur',
        },
    ],
    fakeAddress: [
        {
            required: true,
            message: '请填写详细地址',
            trigger: 'change',
        },
        {
            validator: (rule: any, value: any, callback: any) => {
                if (value.includes('~')) {
                    callback(new Error('详细地址不能包含~号'))
                } else {
                    callback()
                }
            },
            trigger: 'change',
        },
    ],
})
// 校验手机号
function checkPhone(rule: any, value: any, callback: any) {
    if (value === '') {
        callback(new Error('请填写联系方式'))
    } else if (!REGEX_MOBILE(value)) {
        callback(new Error('请填写正确的手机号'))
    } else {
        callback()
    }
}
const showSelectHandle = () => {
    if ($route.meta.disabled) return
    userSelectStatus.value = true
}
const userConfirmHandle = (e: UserType[]) => {
    submitForm.value.userId = e[0].userId
    userInfo.value = e[0]
}

const showChooseMap = ref(false)
const toChooseAddress = () => {
    if ($route.meta.disabled) {
        return
    }
    showChooseMap.value = true
}
const handleChooseRes = async (res: Checked) => {
    if (!res.longitude) return
    if (!res.name) return
    if (!res.address) return
    const checkedLocation: Geometry = {
        type: GeometryType.Point,
        coordinates: [Number(res.longitude), Number(res.latitude)],
    }
    submitForm.value.location = checkedLocation
    // 逆地理编码 获取省市区 submitForm.value.area []
    const { area, address } = await doGetAddressByLocation(checkedLocation.coordinates)
    if (area) {
        submitForm.value.area = area
    }
    if (address) {
        submitForm.value.address = `${address}~${res.name}`
    }
}
const placeChoosed = (place: Checked) => {
    handleChooseRes(place)
}
</script>

<template>
    <div class="shopBaseForm">
        <el-form ref="currentFormRef" :model="submitForm" :rules="baseFormRules" :disabled="!!$route.meta.disabled" label-width="95px">
            <el-form-item label="商户名称" prop="companyName">
                <el-input v-model="submitForm.companyName" placeholder="请输入商户名称" maxlength="20" show-word-limit></el-input>
            </el-form-item>
            <el-form-item :label="`${supplierViewModel}名称`" prop="name">
                <el-input v-model="submitForm.name" :placeholder="`请输入${supplierViewModel}名称`" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item :label="`${supplierViewModel}logo`" prop="logo">
                <!-- <q-upload v-model:src="submitForm.logo" /> -->
                <div :style="{ cursor: $route.meta.disabled ? 'not-allowed' : 'pointer' }">
                    <div v-if="!submitForm.logo" class="selectMaterialStyle" @click="buttonlFn('logo')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <el-image v-else class="selectMaterialStyle" :src="submitForm.logo" alt="" @click="buttonlFn('logo')" />
                </div>
            </el-form-item>
            <el-form-item label="管理员" prop="userId">
                <div v-if="userInfo" class="info" @click="showSelectHandle">
                    <el-image
                        :src="userInfo?.avatar"
                        :style="{ cursor: $route.meta.disabled ? 'not-allowed' : 'pointer' }"
                        class="info__img"
                    ></el-image>
                    <div class="info__name">
                        <div>{{ userInfo?.username }}</div>
                        <div>{{ userInfo?.mobile }}</div>
                    </div>
                    <el-icon style="color: rgb(85, 92, 253); margin-left: 10px"><ArrowRight /></el-icon>
                </div>
                <el-button v-else type="primary" @click="showSelectHandle">添加系统管理员</el-button>
            </el-form-item>
            <el-form-item label="联系方式" prop="contractNumber">
                <el-input v-model="submitForm.contractNumber" placeholder="请输联系方式" maxlength="11"></el-input>
            </el-form-item>
            <el-form-item label="定位地址" prop="area" class="address_form_item">
                <div class="fcenter" :style="{ cursor: $route.meta.disabled ? 'not-allowed' : 'pointer' }" @click="toChooseAddress">
                    <div class="address_area">
                        <div v-if="submitForm.address" class="address_name">{{ submitForm.address.split('~')[1] || '' }}</div>
                        <div v-else style="color: rgb(85, 92, 253)">打开地图选择定位</div>
                        <div v-if="submitForm.area" class="address_detail_name">
                            {{ submitForm.area?.join?.('') }}{{ submitForm.address.split('~')[0] }}
                        </div>
                    </div>
                    <el-icon style="color: rgb(85, 92, 253)" class="mla"><ArrowRight /></el-icon>
                </div>
            </el-form-item>

            <el-form-item label="详细地址" prop="address">
                <el-input v-model="submitForm.address" :rows="1" type="textarea" placeholder="例如: [3单元203室]" :maxlength="20" />
            </el-form-item>

            <el-form-item :label="`${supplierViewModel}介绍`" prop="briefing">
                <el-input v-model="submitForm.briefing" :rows="4" type="textarea" maxlength="150" />
            </el-form-item>
            <el-form-item v-if="supplierViewModel === '店铺'" label="经营模式" props="shopMode">
                <el-radio-group v-model="submitForm.shopMode" :disabled="submitForm.status && submitForm.status !== 'UNDER_REVIEW'">
                    <el-radio value="COMMON" size="large">线上模式</el-radio>
                    <el-radio value="O2O" size="large">O2O模式</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item :label="`${supplierViewModel}类型`" props="shopType">
                <el-radio-group v-model="submitForm.shopType">
                    <el-radio value="ORDINARY" size="large">普通{{ supplierViewModel }}</el-radio>
                    <el-radio value="PREFERRED" size="large">优选{{ supplierViewModel }}</el-radio>
                    <el-radio value="SELF_OWNED" size="large">自营{{ supplierViewModel }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="平台服务费" props="extractionType">
                <el-radio-group v-model="submitForm.extractionType">
                    <el-radio value="CATEGORY_EXTRACTION" size="large">类目提佣</el-radio>
                    <el-radio value="ORDER_SALES_EXTRACTION" size="large">
                        <span class="mr-8">订单金额提佣，按订单实付金额的</span>
                        <el-form-item prop="drawPercentage" style="display: inline-flex">
                            <el-input v-model="submitForm.drawPercentage" placeholder="请输入0~100的正整数" style="width: 160px" class="mr-8" />
                        </el-form-item>
                        <span>% ，进行提佣结算</span>
                    </el-radio>
                </el-radio-group>
                <el-tooltip effect="dark" content="" placement="top-start">
                    <template #content>
                        1、所有商品必须挂载到平台类目(签约类目)下，才能正常展示和购买<br />
                        2、平台从商家每笔交易中，按商品类目扣率或订单实付金额计算提成，以保障平台正常运营
                    </template>
                    <QIcon name="icon-wenhao" color="#333" style="margin-left: 5px" />
                </el-tooltip>
            </el-form-item>
            <el-form-item label="签约类目">
                <ContractCategory
                    :list="submitForm.signingCategory"
                    @update:list="
                        (val) => {
                            submitForm.signingCategory = val
                        }
                    "
                />
            </el-form-item>
        </el-form>
        <AmapChooseDialog v-model="showChooseMap" :initLocation="submitForm.location.coordinates" @placeChoose="placeChoosed"></AmapChooseDialog>
        <UserSelect v-model:show="userSelectStatus" :current-choose="submitForm.userId" @user-confirm="userConfirmHandle" />
        <!-- 选择素材 e -->
        <SelectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style scoped lang="scss">
.address_form_item {
    .fcenter {
        width: 100%;
        .address_area {
            padding: 1px 11px;
            display: flex;
            flex-direction: column;
            width: calc(100% - 80px);
            text-align: left;
            line-height: 18px;
            .address_name {
                font-size: 16px;
                font-weight: bold;
            }
            .address_detail_name {
                color: #a8abb2;
            }
        }
    }
}
@include b(shopBaseForm) {
    padding: 20px 25px 70px 46px;
}

@include b(info) {
    @include flex(flex-start);
    cursor: pointer;

    @include e(img) {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        margin-right: 10px;
    }

    @include e(name) {
        font-size: 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }
}
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}
.mr-8 {
    margin-right: 8px;
}
@include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 20px;
    }
}
.avatar-uploader {
    :deep(.el-upload) {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
        &:hover {
            border-color: var(--el-color-primary);
        }
    }
}
:deep(.el-icon.avatar-uploader-icon) {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
}
</style>
