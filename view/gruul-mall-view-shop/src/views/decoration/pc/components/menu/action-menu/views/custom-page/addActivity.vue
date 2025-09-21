<script setup lang="ts">
import upload from '@/views/decoration/pc/components/upload/index.vue'
import goodsDialog from '@/views/decoration/pc/components/setting-goods-dialog/index.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { useCommon } from '@/views/decoration/pc/components/hooks'
import { useVModel } from '@vueuse/core'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'
import { doPostShopPagesSave, doPutShopPages } from '@/apis/decoration'
import { ElMessage, type FormRules } from 'element-plus'
import { ComponentsItem } from './type'

const props = defineProps<{
    formData: FormData
}>()

type FormData = {
    name: string
    img: string
    list: { [key: string]: any[] }
    radioKeys: string[]
}

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

/**
 * 单选框数据
 */
const radio = ref('')
const activeId = ref('-1')

watch(
    () => Object.keys(formData.value.list).length,
    () => {
        formData.value.radioKeys = Object.keys(formData.value.list)

        // 修改完后调整对象key的位置
        const originalObject = formData.value.list
        const newObject: { [key: string]: any[] } = {}

        formData.value.radioKeys.forEach((item) => {
            newObject[item] = originalObject[item]
        })

        formData.value.list = newObject
    },
)

/**
 * 选择数量
 */
const count = computed(() => {
    changeRadio()
    return formData.value.radioKeys.reduce((num, key) => {
        return num + formData.value.list[key].length
    }, 0)
})

const changeRadio = () => {
    if (!radio.value) radio.value = formData.value.radioKeys[0]
}

/**
 * 添加商品
 */
const goodsDialogRef = ref<InstanceType<typeof goodsDialog>>()
const add = () => {
    goodsDialogRef.value?.open(formData.value.list)
}

/**
 * 校验规则
 */
const checkList = (_: any, __: any, callback: any) => {
    if (formData.value.radioKeys.length) {
        callback()
    } else {
        callback(new Error('活动商品为必选项！'))
    }
}

/**
 * 校验
 */
const rules: FormRules = {
    name: [{ required: true, trigger: 'blur', message: '活动页面名称为必填项！' }],
    img: [{ required: true, trigger: 'blur', message: '活动主图为必填项！' }],
    style: [{ required: true, trigger: 'blur', message: '展示样式为必填项！' }],
    list: [{ required: true, trigger: 'blur', message: '活动商品为必选项！' }, { validator: checkList }],
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

const formRef = ref()

const shopStore = useShopInfoStore()
/**
 * 新增页面
 */
const pageSave = async () => {
    try {
        await formRef.value.validate()
        const payload = {
            shopId: shopStore.shopInfo.id,
            name: formData.value.name,
            type: 'CUSTOMIZED_PAGE',
            templateType: 'SHOP_HOME_PAGE',
            businessType: 'ONLINE',
            endpointType: 'PC_MALL',
            properties: formData.value,
            customType: 'activity',
        }
        const { success } = await doPostShopPagesSave(payload)
        if (!success) {
            ElMessage.error('新增活动页面失败！')

            return Promise.reject()
        }
    } catch (error) {
        return Promise.reject()
    }
}

/**
 * 编辑页面
 */
const editPage = async (data: ComponentsItem) => {
    try {
        await formRef.value.validate()
        const payload = {
            ...data,
            name: formData.value.name,
            properties: formData.value,
        }

        const { success } = await doPutShopPages(payload)
        if (!success) {
            ElMessage.error('编辑活动页面失败！')

            return Promise.reject()
        }
    } catch (error) {
        return Promise.reject()
    }
}

const goodsId = computed(() => {
    return Object.values(props.formData.list)
        .flat()
        .map((item) => item.id)
})

const { getGoodList, unGoods } = useCommon()

onBeforeMount(() => {
    getGoodList(goodsId.value)
})

defineExpose({
    pageSave,
    editPage,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules">
        <el-form-item label="页面名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入页面名称" show-word-limit maxlength="5" />
        </el-form-item>

        <el-form-item label="活动主图" prop="img">
            <div style="height: 140px">
                <upload v-model="formData.img" :form-ref="formRef" width="846px" height="112px" />
                <span class="text">1920*450~550</span>
            </div>
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
                        <el-radio v-for="item in formData.radioKeys" :key="item" :value="item" :label="item" border />
                    </vue-draggable-next>
                </el-radio-group>
            </div>

            <!-- 商品 -->
            <el-scrollbar height="340px" class="m-t-10">
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
.text {
    color: #999999;
}

@include b(el-form) {
    .el-radio {
        margin-right: 10px;
        --el-radio-text-color: #999;

        :deep(.el-radio__input) {
            display: none;
        }
    }
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

@include b(el-scrollbar) {
    margin-right: -20px;

    :deep(.el-scrollbar__view) {
        padding-top: 10px;
        padding-right: 20px;
    }
}
</style>
