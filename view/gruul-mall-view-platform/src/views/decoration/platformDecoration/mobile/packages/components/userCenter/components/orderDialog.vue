<script setup lang="ts">
import type { PropType } from 'vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { useVModel } from '@vueuse/core'
import type { OrderItem } from '../user-center'
import { ElMessageBox } from 'element-plus'
// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisibles = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisibles.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisibles.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    currentOrderItem.value.url = val
}
const checkedFileLists = (val: string[]) => {
    currentOrderItem.value.url = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    orderVisible: {
        type: Boolean,
        default: false,
    },
    orderProp: {
        type: Object as PropType<OrderItem>,
        default() {
            return {}
        },
    },
})
const $emit = defineEmits(['update:orderVisible', 'updateOrder'])
const dialogVisible = useVModel($props, 'orderVisible', $emit)
const currentOrderItem = ref({
    name: '',
    url: '',
})
watch(dialogVisible, (newVal) => {
    if (newVal) {
        currentOrderItem.value = $props.orderProp
    }
})

const comfirmClickHandle = () => {
    $emit('updateOrder', toRaw(currentOrderItem.value))
    dialogVisible.value = false
}
const closeHandle = () => {
    dialogVisible.value = false
}
</script>

<template>
    <el-dialog v-model="dialogVisible" anpend-to-body :modal-append-to-body="false" :before-close="closeHandle" width="450px" title="订单栏图标编辑">
        <el-form ref="form" v-model="currentOrderItem" label-width="120px">
            <el-form-item label="订单名称">
                {{ currentOrderItem.name }}
            </el-form-item>
            <el-form-item label="订单图标">
                <!-- <q-upload v-model:src="currentOrderItem.url" :width="120" :height="120" /> -->

                <div v-if="!currentOrderItem.url" class="selectMaterialStyle" @click="buttonlFn">
                    <span class="selectMaterialStyle__span">+</span>
                </div>
                <div v-else class="selectMaterialStyle" @click="buttonlFn">
                    <img :src="currentOrderItem.url" alt="" style="width: 120px" />
                </div>

                <div class="el-upload__tip" style="color: #9797a1">(图片格式支持尺寸15×15px,格式 png、jpg、jpeg，gif。)</div>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog--footer">
                <el-button @click="closeHandle">取 消</el-button>
                <el-button type="primary" @click="comfirmClickHandle">确 定</el-button>
            </div>
        </template>
    </el-dialog>
    <!-- 选择素材 e -->
    <selectMaterial
        :dialog-visible="dialogVisibles"
        :upload-files="1"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>

<style lang="scss" scoped>
.form--item {
    display: flex;
    justify-content: flex-start;
    align-content: flex-end;
    padding: 10px;

    .form-item__label {
        width: 120px;
        text-align: left;
        vertical-align: middle;
        float: left;
        font-size: 14px;
        color: #606266;
        line-height: 14px;
        box-sizing: border-box;
    }
}

.el-upload__tip {
    color: #9797a1;
}
@include b(selectMaterialStyle) {
    width: 120px;
    height: 120px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    @include e(span) {
        color: #a7a7a7;
        font-size: 20px;
    }
}
</style>
