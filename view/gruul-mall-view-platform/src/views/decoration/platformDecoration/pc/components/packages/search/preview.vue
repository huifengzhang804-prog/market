<script setup lang="ts">
import search from './search'
import { Search, CaretBottom } from '@element-plus/icons-vue'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof search>,
        default: search,
    },
})

const input = ref('')

const type = ref('店铺')
const handleCommand = (e: string) => {
    type.value = e
}
</script>

<template>
    <div class="row">
        <div class="main">
            <!-- logo -->
            <img v-if="formData.logo" class="row__logo" :src="formData.logo" />
            <div v-else class="row__logo"></div>

            <!-- 搜索 -->
            <el-input v-if="formData.search" v-model="input" placeholder="请输入您想找的店铺" class="row__search">
                <template #prepend>
                    <el-dropdown trigger="click" size="lager" @command="handleCommand">
                        <span class="el-dropdown-link cp">
                            {{ type }}<el-icon class="m-l-4"><caret-bottom /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="店铺">店铺</el-dropdown-item>
                                <el-dropdown-item command="商品">商品</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>

                <template #append>
                    <div class="row__search--text">
                        <el-icon size="20px" class="m-r-8" style="top: -1px"><Search /></el-icon>
                        搜索
                    </div>
                </template>
            </el-input>
            <div v-else class="row__search-placeholder"></div>

            <!-- 购物车 -->
            <div v-if="formData.car" class="row__car">
                <div class="row__car-icon">
                    <q-icon name="icon-gouwuche1" size="24px" color="#fff"></q-icon>
                </div>
                <div class="row__car-text">
                    <div class="row__car-text--badge">20</div>
                    购物车
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(row) {
    background-color: #fff;

    .main {
        display: flex;
        align-items: center;
    }

    @include e(logo) {
        width: 194px;
        height: 62px;
    }

    @include e(search) {
        width: 620px;
        height: 42px;
        margin: 0 65px 0 183px;
        border: 1px solid #f54319;
        border-radius: 2px;
        --el-fill-color-light: #fff;
        --el-input-border-color: #fff;
        --el-input-hover-border-color: #fff;
        --el-input-focus-border-color: #fff;

        :deep(.el-input-group__prepend) {
            padding: 0;
            width: 70px;
        }

        :deep(.el-input-group__append) {
            padding: 0;
            left: 1px;
        }

        @include m(text) {
            background: linear-gradient(90deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 100%);
            width: 92px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 16px;
            cursor: pointer;
        }
    }

    @include e(search-placeholder) {
        width: 620px;
        height: 42px;
        margin: 0 65px 0 183px;
    }

    @include e(car) {
        width: 138px;
        height: 42px;
        border-radius: 2px;
        display: flex;
        overflow: hidden;
        cursor: pointer;
    }

    @include e(car-icon) {
        background: linear-gradient(135deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 99.237%);
        width: 42px;
        height: 42px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    @include e(car-text) {
        font-size: 16px;
        color: #f54319;
        width: 96px;
        height: 42px;
        border: 1px solid #f54319;
        text-align: center;
        line-height: 42px;
        position: relative;
        @include m(badge) {
            position: absolute;
            top: 0px;
            right: 2px;
            background: #f54319;
            width: 24px;
            height: 16px;
            border-radius: 10px;
            color: #fff;
            line-height: 16px;
            font-size: 12px;
            transform: scale(0.8);
        }
    }
}
</style>
