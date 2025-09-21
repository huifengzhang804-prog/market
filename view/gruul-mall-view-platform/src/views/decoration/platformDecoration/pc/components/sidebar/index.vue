<script setup lang="ts">
import { defaultOtherData } from '../menu/common'

const other = inject('otherData', ref(defaultOtherData()))
</script>

<template>
    <div class="sidebar">
        <div v-show="other.service" class="sidebar__service cp">
            <q-icon name="icon-lianxikefu" color="#F54319" size="28px"></q-icon>
            <div class="m-t-10">联系客服</div>
        </div>
        <div v-show="other.car" class="sidebar__car cp">
            <div class="sidebar__car--line"></div>

            <el-badge :value="12">
                <q-icon name="icon-gouwuche6" size="28px"></q-icon>
            </el-badge>
            <div class="m-t-10">购物车</div>
        </div>
        <div class="sidebar__top cp">
            <q-icon name="icon-Top" color="#F54319" size="28px"></q-icon>
            <div class="m-t-10">Top</div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(sidebar) {
    position: fixed;
    top: 50%;
    right: 0px;
    width: 76px;
    padding: 12px 10px;
    background-color: #fff;
    z-index: 3;
    text-align: center;
    border-radius: 0px 0px 2px 2px;
    box-shadow: 0px 0px 2px 2px rgba(0, 0, 0, 0.05);
    @include e(service) {
        padding-bottom: 14px;
    }

    @include e(car) {
        position: relative;
        padding: 16px 0;

        @include m(line) {
            position: absolute;
            width: 26px;
            left: 50%;
            top: 0;
            bottom: 0;
            transform: translateX(-50%);
            border: 1px solid rgba(0, 0, 0, 0.1);
            border-left: none;
            border-right: none;
        }
    }

    @include e(top) {
        color: #f54319;
        padding-top: 16px;
    }
}
</style>
