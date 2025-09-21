<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import classification from './classification'
import type { DeCategoryType, DeCategoryItem } from './classification'
import type { PropType } from 'vue'
import { useDecorationStore } from '@/store/modules/decoration'
import categoryTableWrapper from './components/category-table-wrapper.vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return classification
        },
    },
})
const $decorationStore = useDecorationStore()
const $emit = defineEmits(['update:formData'])
const formModel = useVModel($props, 'formData', $emit)

/**
 * 如果端类型是H5_APP，则搜索展示样式设置为标题下
 */
watch(
    () => formModel.value,
    (newVal) => {
        if ($decorationStore.getEndpointType === 'H5_APP' && newVal.searchShow === 4) {
            formModel.value.searchShow = 2
        }
    },
    {
        immediate: true,
    },
)
</script>

<template>
    <el-form v-model="formModel">
        <el-form-item label="样式">
            <el-radio-group v-model="formModel.style">
                <el-radio :value="1">类目</el-radio>
                <el-radio :value="2">类目商品</el-radio>
                <el-radio :value="5">类目导航</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="标题">
            <el-input v-model="formModel.classificationTitle" placeholder="请输入类目标题" maxlength="10" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="类目广告">
            <el-switch v-model="formModel.categoryShow" :active-value="true" :inactive-value="false" />
        </el-form-item>
        <el-form-item v-if="formModel.style !== 5" label="导航">
            <el-radio-group v-model="formModel.navShow">
                <el-radio :value="true">展示</el-radio>
                <el-radio :value="false">不展示</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="搜索">
            <el-radio-group v-model="formModel.searchShow">
                <el-radio v-if="$decorationStore.getEndpointType === 'WECHAT_MINI_APP'" :value="4">沉浸式</el-radio>
                <el-radio :value="2">标题下</el-radio>
                <el-radio :value="3">不展示</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formModel.style === 5 || formModel.style === 2" label="商品展示">
            <template v-if="formModel.style === 5">
                <text style="color: #101010">&ensp;样式&emsp;</text>
                <el-radio-group v-model="formModel.commodityShow">
                    <el-radio :value="1">列表</el-radio>
                    <el-radio :value="2">瀑布流</el-radio>
                </el-radio-group>
            </template>
            <div style="display: flex">
                <div style="color: #101010; width: 70px">&ensp;内容&ensp;</div>
                <el-checkbox-group v-model="formModel.goodsShow">
                    <el-checkbox :value="1">店铺类型</el-checkbox>
                    <el-checkbox :value="2">规格图</el-checkbox>
                    <el-checkbox :value="3">已售</el-checkbox>
                    <el-checkbox :value="4">优惠券/包邮</el-checkbox>
                    <el-checkbox :value="5">购物车</el-checkbox>
                </el-checkbox-group>
            </div>
        </el-form-item>
        <el-form-item>
            <category-table-wrapper v-model:formData="formModel" />
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
