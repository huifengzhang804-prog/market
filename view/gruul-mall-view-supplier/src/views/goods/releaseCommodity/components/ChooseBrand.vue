<template>
    <div class="brand-info">
        <el-input v-model="brandName" style="width: 208px" placeholder="请输入品牌名称" maxlength="6">
            <template #append>
                <el-button :icon="Search" @click="Searchlist"></el-button>
            </template>
        </el-input>
    </div>

    <div class="brand-table">
        <div class="brand-table__head">
            <div style="margin-left: 44px">品牌名称</div>
            <div style="margin-left: 166px">状态</div>
            <div style="margin-left: 214px">图片</div>
        </div>
        <el-radio-group v-model="BrandRadio">
            <el-radio
                v-for="item in BrandList"
                :key="item.id"
                :label="item.id"
                style="height: 70px; border-bottom: 1px solid #e6e6e6; margin-left: 15px"
            >
                <div class="brand-table__content">
                    <div class="brand-table__content--item1">
                        {{ item.brandName }}
                    </div>
                    <div class="brand-table__content--item2" :style="item.status === 'SELL_ON' ? 'background: #f5faf3;color: #82c26b;' : ''">
                        {{ item.status === 'SELL_ON' ? '已上架' : '已下架' }}
                    </div>
                    <el-image class="brand-table__content--item3" :src="item.brandLogo"></el-image>
                </div>
            </el-radio>
        </el-radio-group>
    </div>
</template>

<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import useChooseBrand from '../hooks/useChooseBrand'
const $props = withDefaults(defineProps<{ brandId: string }>(), {
    brandId: '',
})
const { Searchlist, brandName, BrandRadio, BrandList, initBrandList } = useChooseBrand($props.brandId)
defineExpose({ BrandRadio, BrandList, initBrandList })
</script>

<style lang="scss" scoped>
@include b(brand-info) {
    display: flex;
    justify-content: end;
}
@include b(brand-table) {
    border: 1px solid #e6e6e6;
    margin-top: 15px;
    max-height: 450px;
    overflow: hidden;
    overflow-y: auto;
    @include e(head) {
        height: 30px;
        background: #f6f6f6;
        display: flex;
        font-size: 14px;
        color: #6a6a6a;
        line-height: 30px;
    }
    @include e(content) {
        display: flex;
        align-items: center;
        width: 580px;
        @include m(item1) {
            margin-left: 24px;
            width: 198px;
            color: #6a6a6a;
            @include utils-ellipsis();
        }
        @include m(item2) {
            background: #fef6f3;
            border-radius: 4px;
            font-size: 14px;
            width: 76px;
            height: 26px;
            line-height: 26px;
            color: #f4a584;
            text-align: center;
        }
        @include m(item3) {
            margin-left: 179px;
            width: 50px;
            height: 50px;
        }
    }
}
</style>
