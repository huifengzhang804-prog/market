<script setup lang="ts">
import QUpload from '@/components/q-upload/q-upload.vue'
import DragMenu from './components/dragMenu.vue'
import OrderDialog from './components/orderDialog.vue'
import userCenterDefaultData from './user-center'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'
import type { UserCenterType, OrderItem } from './user-center'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    formData.value.customStyle.backgroundImage = val
}
const checkedFileLists = (val: string[]) => {
    formData.value.customStyle.backgroundImage = val?.shift() || ''
}
// 选择素材 d
interface ISetVisible {
    [x: string]: boolean
}

const $props = defineProps({
    formData: {
        type: Object as PropType<UserCenterType>,
        default() {
            return userCenterDefaultData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const setVisible = reactive<ISetVisible>({
    setInfoVisible: true,
    setOrderVisible: false,
    setMenuVisible: false,
})
const orderVisible = ref(false)
const recordOrderKey = ref('')
const currentOrderItem = computed(() => {
    if (formData.value.orderInfo instanceof Array) {
        return formData.value.orderInfo.find((item) => item.id === recordOrderKey.value) || {}
    }
    return formData.value.orderInfo[recordOrderKey.value] || {}
}) as unknown as Ref<OrderItem>
const tabActions = ref('Info')
const collapseVisibleHandle = (type: any) => {
    const map = ['Info', 'Order', 'Menu']
    map.forEach((name) => {
        setVisible[`set${name}Visible`] = type === name
    })
}
/**
 * 显示订单编辑详情弹窗
 */
const orderClickHandle = (key: string) => {
    recordOrderKey.value = key
    orderVisible.value = true
}
const updateOrderHandle = (e: OrderItem) => {
    formData.value.orderInfo.forEach((element) => {
        if (element.id === recordOrderKey.value) {
            element = e
        }
    })
}
</script>

<template>
    <!-- <div class="editor__form_wrap_main"> -->
    <!-- <el-scrollbar style="height: 100%">
            <div class="pd20"> -->
    <transition name="el-fade-in-linear" mode="out-in">
        <el-row class="setting" style="display: flex; flex-direction: column; align-items: center">
            <el-radio-group v-model="tabActions" @change="collapseVisibleHandle">
                <el-radio-button value="Info"><p style="padding: 1px 14px">用户信息</p></el-radio-button>
                <el-radio-button value="Order"><p style="padding: 1px 14px">我的订单</p></el-radio-button>
                <el-radio-button value="Menu"><p style="padding: 1px 14px">我的工具</p></el-radio-button>
            </el-radio-group>
            <el-form ref="formData" v-model="formData" style="width: 100%" label-width="95px">
                <div class="setting__collapse setinfo">
                    <div v-show="setVisible.setInfoVisible" class="setting__collapse--content" style="padding: 20px 0">
                        <el-form-item label="头部风格">
                            <el-radio-group v-model="formData.headStyle">
                                <el-radio :value="1">系统风格</el-radio>
                                <el-radio :value="2">自定义风格</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-row v-show="formData.headStyle === 2">
                            <el-form-item label="背景图片">
                                <!-- <q-upload v-model:src="formData.customStyle.backgroundImage" :width="120" :height="120" /> -->
                                <div v-if="!formData.customStyle.backgroundImage" class="selectMaterialStyle" @click="buttonlFn">
                                    <span class="selectMaterialStyle__span">
                                        <span>+ 添加商品大图</span><br />
                                        <span style="font-size: 12px; color: #a7a7a7">建议宽度750像素</span>
                                    </span>
                                </div>
                                <div v-else class="selectMaterialStyle" @click="buttonlFn">
                                    <img :src="formData.customStyle.backgroundImage" alt="" style="width: 200px; height: 90px" />
                                </div>
                                <div class="el-upload__tip" style="color: #9797a1">已投放会员卡：建议尺寸750px*443px</div>
                            </el-form-item>
                        </el-row>
                        <div>
                            <h4 style="margin-bottom: 8px">个人信息</h4>
                            <div v-if="formData.headStyle === 2" style="display: flex; margin: 0 0 15px 26px; align-items: center">
                                <span style="display: inline-block; color: #606266; margin-right: 10px">字体颜色(昵称/储值/返利/收藏/足迹)</span>
                                <div class="divContant">
                                    <span style="width: 76px">
                                        {{ formData.customStyle.infoColor }}
                                    </span>
                                    <el-color-picker v-model="formData.customStyle.infoColor" class="color-picker" />
                                </div>
                            </div>
                            <el-form-item label="扫码">
                                <el-switch v-model="formData.scanCode"> </el-switch>
                                <div v-if="formData.headStyle === 2" class="divContant" style="margin-left: 10px">
                                    <span style="width: 76px">
                                        {{ formData.customStyle.scanCodeColor }}
                                    </span>
                                    <el-color-picker v-model="formData.customStyle.scanCodeColor" class="color-picker" />
                                </div>
                            </el-form-item>
                            <el-form-item label="会员码">
                                <el-switch v-model="formData.membershipCode"> </el-switch>
                                <div v-if="formData.headStyle === 2" class="divContant" style="margin-left: 10px">
                                    <span style="width: 76px">
                                        {{ formData.customStyle.membershipCodeColor }}
                                    </span>
                                    <el-color-picker v-model="formData.customStyle.membershipCodeColor" class="color-picker" />
                                </div>
                            </el-form-item>
                        </div>
                        <div>
                            <h4 style="margin-bottom: 8px">付费会员</h4>
                            <el-form-item label="领卡入口">
                                <el-switch v-model="formData.hideCartInlet"> </el-switch>
                            </el-form-item>
                            <el-form-item v-if="formData.headStyle === 2" label="卡面颜色">
                                <div class="divContant">
                                    <span style="width: 76px">
                                        {{ formData.customStyle.cardColor }}
                                    </span>
                                    <el-color-picker v-model="formData.customStyle.cardColor" class="color-picker" />
                                </div>
                            </el-form-item>
                            <el-form-item label="领卡文案">
                                <div style="display: flex; align-items: center">
                                    <el-input v-model="formData.getCartText" maxlength="10" show-word-limit style="width: 200px" />
                                    <div v-if="formData.headStyle === 2" style="margin-left: 10px" class="divContant">
                                        <span style="width: 76px">
                                            {{ formData.customStyle.textColor }}
                                        </span>
                                        <el-color-picker v-model="formData.customStyle.textColor" class="color-picker" />
                                    </div>
                                </div>
                            </el-form-item>
                            <el-form-item v-if="formData.headStyle === 2" label="立即开通">
                                <div style="display: flex">
                                    <div class="divContant">
                                        <span style="width: 76px">
                                            {{ formData.customStyle.activateNowColor }}
                                        </span>
                                        <el-color-picker v-model="formData.customStyle.activateNowColor" class="color-picker" />
                                    </div>
                                    <div style="display: flex; margin-left: 10px">
                                        <span>按钮颜色</span>
                                        <div style="margin-left: 10px" class="divContant">
                                            <span style="width: 76px">
                                                {{ formData.customStyle.activateNowBtnColor }}
                                            </span>
                                            <el-color-picker v-model="formData.customStyle.activateNowBtnColor" class="color-picker" />
                                        </div>
                                    </div>
                                </div>
                            </el-form-item>
                        </div>
                    </div>
                </div>
                <div class="setting__collapse setorder">
                    <div v-if="setVisible.setOrderVisible" class="setting__collapse--content">
                        <div v-for="item in formData.orderInfo" :key="item.id" class="setorder__item">
                            <span class="setorder__item--text">
                                {{ item.name }}
                            </span>
                            <el-icon color="#9797a1" @click="orderClickHandle(item.id)"><i-ep-edit /></el-icon>
                        </div>
                    </div>
                </div>
                <div class="setting__collapse setmenu">
                    <div v-if="setVisible.setMenuVisible" class="setting__collapse--content">
                        <div class="menu__form">
                            <div class="menu__form--item">
                                <label class="el-form-item__label">选择样式:</label>
                                <el-radio-group v-model="formData.menuStyle">
                                    <el-radio :value="1">列表式</el-radio>
                                    <el-radio :value="2">九宫格</el-radio>
                                </el-radio-group>
                            </div>
                            <div class="menu__form--item" style="padding-top: 30px">
                                <label class="el-form-item__label">设置菜单:</label>
                                <span style="color: #9797a1">长按点击可拖拽调整顺序</span>
                            </div>
                        </div>
                        <!--列表式-->
                        <drag-menu v-if="formData.menuStyle === 1" v-model:menu-list="formData.menuList" :menu-style="1" split-flag />
                        <!--九宫格-->
                        <drag-menu v-if="formData.menuStyle === 2" v-model:menu-list="formData.menuScratchable" :split-flag="false" :menu-style="2" />
                    </div>
                </div>
            </el-form>
        </el-row>
    </transition>
    <order-dialog v-model:orderVisible="orderVisible" :order-prop="currentOrderItem" @update-order="updateOrderHandle" />
    <!-- </div>
        </el-scrollbar> -->
    <!-- </div> -->
    <!-- 选择素材 e -->
    <selectMaterial
        :dialog-visible="dialogVisible"
        :upload-files="1"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/userCenter.scss';
@import '@/assets/css/decoration/editPage.scss';
.editor__form_wrap_main {
    width: 100%;
    flex: 1;
    overflow: hidden;
}
.pd20 {
    padding: 20px 20px 0 20px;
}

@include b(selectMaterialStyle) {
    width: 200px;
    height: 90px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    align-items: center;
    @include e(span) {
        color: #3088f0;
        font-size: 14px;
    }
}
:deep(.el-radio-group :first-child, .el-radio-button) {
    .el-radio-button__inner {
        border-radius: 20px 0 0 20px;
    }
}
:deep(.el-radio-group :last-child, .el-radio-button) {
    .el-radio-button__inner {
        border-radius: 0 20px 20px 0;
    }
}
</style>
