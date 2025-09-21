<script setup lang="ts">
import { VueDraggableNext } from 'vue-draggable-next'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import { useVModel, useDebounceFn } from '@vueuse/core'
import recommend from './recommend'
import { useCommon } from '@/views/decoration/pc/components/hooks'
import goodsDialog from '@/views/decoration/pc/components/setting-goods-dialog/index.vue'
import useGetPageData from '@/views/decoration/pc/components/menu/action-menu/views/custom-page/useGetPageData'
import { ComponentsItem } from '@/views/decoration/pc/components/menu/action-menu/views/custom-page/type'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Recommend } from './recommend'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<Recommend>,
        default: recommend,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const formRef = ref<FormInstance>()

/**
 * 单选框数据
 */
const radio = ref('')
const activeId = ref('-1')

/**
 * 处理列表数据
 */
const handleList = () => {
    formData.value.radioKeys = Object.keys(formData.value.list)

    // 修改完后调整对象key的位置
    const originalObject = formData.value.list
    const newObject: { [key: string]: any[] } = {}

    formData.value.radioKeys.forEach((item) => {
        newObject[item] = originalObject[item]
    })

    formData.value.list = newObject
}

watch(() => Object.keys(formData.value.list).length, useDebounceFn(handleList, 100))

watch(() => formData.value.pageId, useDebounceFn(handleList, 100))

/**
 * 选择数量
 */
const count = computed(() => {
    changeRadio()
    return formData.value.radioKeys.reduce((num, key) => {
        return num + formData.value.list[key]?.length
    }, 0)
})

const changeRadio = () => {
    if (!radio.value) radio.value = formData.value.radioKeys[0]
}

/**
 * 校验规则
 */
const checkList = (_: any, __: any, callback: any) => {
    if (formData.value.radioKeys?.length) {
        callback()
    } else {
        callback(new Error('活动商品为必选项！'))
    }
}

/**
 * 校验
 */
const rules: FormRules = {
    name: [{ required: true, trigger: 'blur', message: '展示名称为必填项！' }],
    img: [{ required: true, trigger: 'blur', message: '活动主图为必填项！' }],
    style: [{ required: true, trigger: 'blur', message: '展示样式为必填项！' }],
    list: [{ required: true, trigger: 'blur', message: '活动商品为必选项！' }, { validator: checkList }],
}

const { getData } = useGetPageData()

const options = ref<ComponentsItem[]>([])

const getOptions = async () => {
    try {
        const { records } = await getData('activity', 1, 9999)

        options.value = records
        if (formData.value.pageId) {
            const obj = options.value.find((item) => item.id === formData.value.pageId)
            selected.value = obj || {}
            if (!obj) {
                formData.value.page = ''
                formData.value.pageId = ''
            }
        }
    } catch (error) {
        console.log('error', error)
    }
}

onBeforeMount(() => {
    getOptions()
})

/**
 * 添加商品
 */
const goodsDialogRef = ref<InstanceType<typeof goodsDialog>>()
const add = () => {
    goodsDialogRef.value?.open(formData.value.list)
}

/**
 * 删除商品
 */
const delGoods = (index: number) => {
    formData.value.list[radio.value].splice(index, 1)
    if (!formData.value.list[radio.value]?.length) {
        Reflect.deleteProperty(formData.value.list, radio.value)
        radio.value = formData.value.radioKeys[0] ?? ''
    }
}
/**
 * 选择商品
 */
const selected = ref({})
const changeSelect = (val: any) => {
    if (!val) return
    const { img, list, radioKeys, name } = JSON.parse(val.properties)
    formData.value.list = list
    formData.value.img = img
    formData.value.radioKeys = radioKeys
    formData.value.name = name
    formData.value.pageId = val.id
}
/**
 * 商品id
 */
const goodsId = computed(() => {
    return Object.values(props.formData.list)
        .flat()
        .map((item) => item.id)
})

/**
 * 清空
 */
const clear = () => {
    formData.value = recommend
}

// 获取商品数据
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
        <el-form-item label="活动页面" prop="page" label-width="63px">
            <el-select
                v-if="options.length"
                v-model="selected"
                style="width: 100%"
                placeholder="请选择活动页面"
                clearable
                value-key="id"
                @change="changeSelect"
                @clear="clear"
            >
                <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item" />
            </el-select>
            <el-input v-else v-model="formData.page" placeholder="暂无活动页面" />
        </el-form-item>

        <el-form-item label="展示名称" prop="name">
            <el-input v-model="formData.name" placeholder="推荐好物" show-word-limit :maxlength="5" />
        </el-form-item>

        <el-form-item label="活动主图" prop="img">
            <div style="height: 136px">
                <upload v-model="formData.img" width="849px" height="112px" :form-ref="formRef" />
                <span class="text">1920*450~550</span>
            </div>
        </el-form-item>

        <el-form-item label="展示样式" prop="style" class="style">
            <el-radio-group v-model="formData.style">
                <el-radio value="row" border>
                    <template #default>
                        <div class="style__radio row"></div>
                    </template>
                </el-radio>

                <el-radio value="column" border>
                    <template #default>
                        <div class="style__radio column"></div>
                    </template>
                </el-radio>
            </el-radio-group>
        </el-form-item>

        <el-form-item :label="`活动商品（${count}/300）`" prop="list" label-position="left">
            <div class="add-line">
                <span class="add-line__text"> 可通过(拖拉拽)调整顺序 </span>

                <el-button :icon="Plus" type="primary" @click="add"> 添加 </el-button>
            </div>
        </el-form-item>

        <!-- 商品 -->
        <div class="goods">
            <!-- 分类 -->
            <div class="goods__class">
                <el-radio-group v-model="radio">
                    <vue-draggable-next :list="formData.radioKeys">
                        <el-radio v-for="item in formData.radioKeys" :key="item" :label="item" :value="item" border />
                    </vue-draggable-next>
                </el-radio-group>
            </div>

            <!-- 商品 -->
            <el-scrollbar height="230px" class="m-t-10">
                <vue-draggable-next class="draggable" :list="formData.list[radio]">
                    <div
                        v-for="(item, index) in formData.list[radio]"
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
@include b(el-form) {
    .el-radio {
        margin-right: 10px;
        --el-radio-text-color: #999;

        :deep(.el-radio__input) {
            display: none;
        }
    }
}

@include b(text) {
    color: #999;
    line-height: 24px;
}

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

@include b(style) {
    .el-radio {
        height: 42px;
        width: 64px;
        padding: 0;
        border-width: 2px;
        :deep(.el-radio__label) {
            padding: 0;
            padding-left: 0;
        }
    }

    @include e(radio) {
        width: 60px;
        height: 38px;
    }
}

@include b(row) {
    background: url('@/assets/images/decoration/row.png') center center;
}

@include b(column) {
    background: url('@/assets/images/decoration/column.png') center center;
}
</style>
