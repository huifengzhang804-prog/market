<script setup lang="ts">
import { VueDraggableNext } from 'vue-draggable-next'
import { useVModel } from '@vueuse/core'
import goods from './goods'
import goodsDialog from '@/views/decoration/pc/components/setting-goods-dialog/index.vue'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'
import { useCommon } from '@/views/decoration/pc/components/hooks'
import type { FormInstance, FormRules } from 'element-plus'
import type { Goods } from './goods'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<Goods>,
        default: goods,
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
        callback(new Error('活动商品为必选项！'))
    }
}

/**
 * 校验
 */
const rules: FormRules = {
    mainTitle: [{ required: true, trigger: 'blur', message: '主标题为必填项！' }],
    list: [{ required: true, trigger: 'blur', message: '活动商品为必选项！' }, { validator: checkList }],
}

/**
 * 添加商品
 */
const goodsDialogRef = ref<InstanceType<typeof goodsDialog>>()
const add = () => {
    goodsDialogRef.value?.open(formData.value.list, false)
}

/**
 * 删除商品
 */
const delGoods = (index: number) => {
    formData.value.list.splice(index, 1)
}

const goodsId = computed(() => props.formData.list.map((item) => item.id))

const { getGoodList, unGoods } = useCommon()

onBeforeMount(() => {
    getGoodList(goodsId.value)
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

        <el-form-item :label="`商品列表（${formData.list.length}/300）`" prop="list" label-position="left">
            <div class="add-line">
                <span class="add-line__text"> 可通过(拖拉拽)调整顺序 </span>

                <el-button :icon="Plus" type="primary" @click="add"> 添加 </el-button>
            </div>
        </el-form-item>

        <!-- 商品 -->
        <div class="goods">
            <!-- 商品 -->
            <el-scrollbar height="466px" class="m-t-10">
                <vue-draggable-next class="draggable" :list="formData.list">
                    <div
                        v-for="(item, index) in formData.list"
                        :key="item.id"
                        class="goods__item"
                        :class="{ 'un-goods': unGoods.includes(item.id) }"
                        @mouseover="activeId = item.id"
                        @mouseleave="activeId = '-1'"
                    >
                        <img :src="item.pic" class="goods__item--img" />
                        <el-icon v-if="activeId === item.id" class="goods__item--icon" size="20" @click="delGoods(index)">
                            <CircleCloseFilled />
                        </el-icon>
                    </div>
                </vue-draggable-next>
            </el-scrollbar>
        </div>

        <!-- 商品弹框 -->
        <goods-dialog ref="goodsDialogRef" :form-ref="formRef" @confirm="formData.list = $event" />
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
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-gap: 10px;
}

@include b(goods) {
    margin-top: -9px;

    @include e(item) {
        width: 105px;
        height: 105px;
        position: relative;

        @include m(img) {
            width: 105px;
            height: 105px;
        }

        @include m(icon) {
            position: absolute;
            cursor: pointer;
            top: -10px;
            right: -10px;
        }
    }
}
</style>
