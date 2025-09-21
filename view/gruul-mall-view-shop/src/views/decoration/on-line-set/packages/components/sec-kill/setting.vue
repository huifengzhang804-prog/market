<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import defaultSeckillData from './sec-kill'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSeckillData>,
        default: defaultSeckillData,
    },
})
const $emit = defineEmits(['update:formData'])
const subForm = useVModel($props, 'formData', $emit)
</script>

<template>
    <div>
        <el-form :model="subForm">
            <el-form-item>自动获取最近的活动显示，没有则不显示此组件</el-form-item>
            <el-form-item label="列表样式">
                <el-radio-group v-model="subForm.display">
                    <el-radio :value="1">横向滚动</el-radio>
                    <el-radio :value="2">详细列表</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="组件内边距">
                <el-slider v-model="subForm.padding" :max="30" :show-input="true" :show-tooltip="false"></el-slider>
            </el-form-item>
            <el-form-item label="商品间距">
                <el-slider v-model="subForm.marginBottom" :max="30" :show-input="true" :show-tooltip="false"></el-slider>
            </el-form-item>
            <el-form-item label="商品样式">
                <el-radio-group v-model="subForm.goodStyle">
                    <el-radio :value="1">无边白底</el-radio>
                    <el-radio :value="2">卡片投影</el-radio>
                    <el-radio :value="3">描边白底</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="图片倒角">
                <el-radio-group v-model="subForm.border">
                    <el-radio :value="false">直角</el-radio>
                    <el-radio :value="true">圆角</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-if="subForm.display === 2" label="文本样式">
                <el-radio-group v-model="subForm.textStyle">
                    <el-radio :value="1">常规</el-radio>
                    <el-radio :value="2">加粗</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="显示内容">
                <el-checkbox v-model="subForm.showtag">显示内容</el-checkbox>
            </el-form-item>
            <el-form-item v-if="subForm.showtag" label="角标样式">
                <el-radio-group v-model="subForm.tagStyle">
                    <el-radio :value="1">新品</el-radio>
                    <el-radio :value="2">热卖</el-radio>
                    <el-radio :value="3">抢购</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
    </div>
</template>

<style lang="scss" scoped>
@include b(choosedGood) {
    width: 285px;
    padding: 18px;
    box-sizing: border-box;
    display: flex;
    flex-wrap: wrap;
    border: 1px dashed #f2f2f2;
    @include e(item) {
        width: 72px;
        height: 72px;
        border: 1px solid #d5d5d5;
        margin-right: 10px;
        line-height: 70px;
        text-align: center;
        font-size: 20px;
        color: #000;
        cursor: pointer;
    }
}
</style>
