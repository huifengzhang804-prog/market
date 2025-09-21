<script setup lang="ts">
import { VueDraggableNext } from 'vue-draggable-next'
import { useVModel } from '@vueuse/core'
import shop, { handleGetGoodList } from './shop'
import shopDialog from './components/setting-shop-dialog.vue'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'
import { type ShopList, shopTypeMap } from '@/components/q-select-shop/type'
import type { FormInstance, FormRules } from 'element-plus'
import type { Shop } from './shop'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<Shop>,
        default: shop,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const formRef = ref<FormInstance>()

/**
 * 单选框数据
 */
const activeId = ref('-1')

/**
 * 校验规则
 */
const checkList = (_: any, __: any, callback: any) => {
    if (formData.value.list.length) {
        callback()
    } else {
        callback(new Error('店铺为必选项！'))
    }
}

/**
 * 校验
 */
const rules: FormRules = {
    mainTitle: [{ required: true, trigger: 'blur', message: '主标题为必填项！' }],
    list: [{ required: true, trigger: 'blur', message: '店铺为必选项！' }, { validator: checkList }],
}

/**
 * 添加店铺
 */
const shopDialogRef = ref<InstanceType<typeof shopDialog>>()
const add = () => {
    shopDialogRef.value?.open(formData.value.list, '选择店铺')
}

/**
 * 删除店铺
 */
const delGoods = (index: number) => {
    formData.value.list.splice(index, 1)
}

/**
 * @ 确认添加弹框
 */
const confirm = (list: ShopList[]) => {
    formData.value.list = list
}

/**
 * 获取店铺列表
 */
async function getGoodList() {
    formData.value.list = await handleGetGoodList(formData.value.list)
}

onBeforeMount(() => {
    getGoodList()
})

defineExpose({
    formRef,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules">
        <el-form-item label="主标题" prop="mainTitle">
            <el-input v-model="formData.mainTitle" placeholder="请输入主标题" maxlength="5" show-word-limit />
        </el-form-item>

        <el-form-item label="副标题" prop="subtitle">
            <el-input v-model="formData.subtitle" placeholder="请输入副标题" maxlength="12" show-word-limit />
        </el-form-item>

        <el-form-item :label="`店铺列表（${formData.list.length}/6）`" prop="list" label-position="left">
            <div class="add-line">
                <span class="add-line__text"> 可通过(拖拉拽)调整顺序 </span>
                <el-button :icon="Plus" type="primary" @click="add"> 添加 </el-button>
            </div>
        </el-form-item>

        <!-- 店铺 -->
        <vue-draggable-next class="draggable" :list="formData.list">
            <div
                v-for="(item, index) in formData.list"
                :key="item.id"
                class="draggable__item m-b-16"
                @mouseover="activeId = item.id"
                @mouseleave="activeId = '-1'"
            >
                <img :src="item.logo" class="draggable__item--img" />
                <div class="m-l-6" style="height: 79px">
                    <div class="draggable__item--text">{{ item.name }}</div>
                    <div class="draggable__item--type">{{ shopTypeMap[item.shopType] }}</div>
                </div>

                <el-icon v-if="activeId === item.id" class="draggable__item--icon" size="20" @click="delGoods(index)">
                    <CircleCloseFilled />
                </el-icon>
            </div>
        </vue-draggable-next>

        <!-- 店铺弹框 -->
        <shop-dialog ref="shopDialogRef" @confirm="confirm" />
    </el-form>
</template>

<style lang="scss" scoped>
@include b(add-line) {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    @include e(text) {
        margin-left: 10px;
        font-size: 12px;
        color: #999;
    }
}

@include b(el-scrollbar) {
    margin-right: -20px;

    :deep(.el-scrollbar__view) {
        padding-top: 10px;
        padding-right: 20px;
    }
}

@include b(draggable) {
    padding-top: 10px;
    display: flex;
    justify-content: start;
    align-content: flex-start;
    flex-wrap: wrap;
    height: 457px;
    font-size: 13px;
    color: #333;
    overflow-y: scroll;
    overflow-x: hidden;

    @include e(item) {
        position: relative;
        border-radius: 2px;
        border: 2px solid #e9e8ed;
        cursor: pointer;
        padding: 8px;
        display: flex;
        width: 345px;
        height: 99px;
        margin-right: 16px;
        @include m(text) {
            width: 247px;
            height: 40px;
            margin-bottom: 19px;
            @include utils-ellipsis(2);
        }

        @include m(type) {
            color: #f54319;
            font-size: 13px;
        }

        @include m(icon) {
            position: absolute;
            top: -10px;
            right: -10px;
        }

        @include m(img) {
            width: 79px;
            height: 79px;
        }
    }
}
</style>
