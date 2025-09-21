<script setup lang="ts">
import loginLineData from './loginLine'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof loginLineData>,
        default: loginLineData,
    },
})
</script>

<template>
    <div class="line">
        <div class="main">
            <div class="line__message">
                {{ formData.message }}
            </div>

            <div class="line__menu">
                <span>用户名称</span>
                <span class="line__menu--center">个人中心</span>
                <span v-if="formData.settledIn" class="line__menu--end">商家入驻</span>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(line) {
    background: #666;
    height: 32px;
    line-height: 32px;
    .main {
        display: flex;
        justify-content: space-between;
        color: #fff;
    }
    @include e(message) {
        width: 500px;
    }

    @include e(menu) {
        span {
            cursor: pointer;
            &:hover {
                color: #f54319;
            }
        }

        @include m(center) {
            margin-left: 41px;
        }

        @include m(end) {
            margin-left: 41px;
        }
    }
}
</style>
