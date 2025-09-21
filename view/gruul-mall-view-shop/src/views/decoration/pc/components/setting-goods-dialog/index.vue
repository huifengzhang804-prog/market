<script setup lang="ts">
import { cloneDeep } from 'lodash-es'
import type { itemType } from '@/components/q-select-goods/type'

const props = withDefaults(defineProps<{ formRef?: any; validKey?: string }>(), {
    formRef: {},
    validKey: 'list',
})

const visible = ref(false)

const cancel = () => {
    visible.value = false
}

const emit = defineEmits(['confirm'])

/**
 * 点击确定
 */
const selectGoodsRef = ref()
const confirm = () => {
    visible.value = false
    props.formRef?.clearValidate(props.validKey)

    const goods = isClass ? selectGoodsRef.value.getCheckedGoodsClass() : selectedGoods.value
    emit('confirm', goods)
}

/**
 * 打开选择商品弹框
 */
let isClass = true
const selectedGoods = ref<itemType[]>([])

type ClassGoods = { [key: string]: itemType[] }
type Goods = itemType[]
const open = (goods: ClassGoods | Goods, isGetClass = true) => {
    isClass = isGetClass

    selectedGoods.value = isClass ? checkedGoods(goods as ClassGoods) : cloneDeep(goods as Goods)

    visible.value = true
}

/**
 * 转换已选商品去除分类名称
 */
const checkedGoods = (goods: { [key: string]: any[] }) => {
    const keys = Object.keys(goods)
    return keys.reduce((pre: itemType[], item) => {
        return [...goods[item], ...pre]
    }, [])
}

defineExpose({ open, selectGoodsRef })
</script>

<template>
    <el-dialog v-model="visible" title="选择商品" width="804px" append-to-body destroy-on-close>
        <q-select-goods ref="selectGoodsRef" v-model:selected-goods="selectedGoods" :goods-max="300" :class-max="8" />

        <!-- 底部 -->
        <template #footer>
            <div class="center__dialog-footer">
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="confirm"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
