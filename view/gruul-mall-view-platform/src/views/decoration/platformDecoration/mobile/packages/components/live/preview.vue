<script setup lang="ts">
import defaultLiveRoomData from './live'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultLiveRoomData>,
        default: defaultLiveRoomData,
    },
})
</script>

<template>
    <div classs="live" :style="{ margin: `0px ${formData.listMargin}px` }">
        <div class="live__title">
            <h1 class="live__title--h1">{{ formData.listTitle }}</h1>
            <span class="live__title--more">查看更多</span>
        </div>
        <!-- max-img-model 最大父盒子不添加任何类名 子级添加类名 max-img-model -->
        <!-- a-line-two-model 最大父盒子添加 a-line-two-model 类名 子级添加类名 a-line-two-model__item-->
        <!-- horizontal-scroll-model 最大父盒子添加 horizontal-scroll-model 类名 子级添加类名 horizontal-scroll-model__item-->
        <div :class="formData.codeStyle === 1 ? '' : formData.codeStyle === 2 ? 'a-line-two-model' : 'horizontal-scroll-model'">
            <div
                v-for="item in formData.listNum"
                :key="item"
                :style="{
                    margin: `${formData.upDownMargin ? formData.upDownMargin : 1}px ${formData.codeStyle === 3 ? 5 : 0}px ${
                        formData.upDownMargin ? formData.upDownMargin : 1
                    }px 0`,
                    borderRadius: `${formData.borderRadius ? 10 : 0}PX`,
                }"
                :class="
                    formData.codeStyle === 1 ? 'max-img-model' : formData.codeStyle === 2 ? 'a-line-two-model__item' : 'horizontal-scroll-model__item'
                "
            >
                <ul class="max-img-model__status">
                    <li class="max-img-model__status--animation">
                        <span></span>
                        <span></span>
                        <span></span>
                    </li>
                    <li class="max-img-model__status--text">未开始</li>
                </ul>
                <div class="max-img-model__title">直播标题</div>
                <div class="max-img-model__nickname">昵称</div>
                <div class="max-img-model__love">97</div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(live) {
    box-sizing: border-box;

    @include e(title) {
        color: #000;
        @include flex(space-between);
        padding: 15px 0;
        @include m(h1) {
            font-size: 20px;
        }
        @include m(more) {
            &::after {
                content: '>';
                display: inline-block;
                margin-left: 4px;
                font-size: 16px;
            }
        }
    }
}
/* 大图模式 */
@include b(max-img-model) {
    color: #fff;
    position: relative;
    height: 200px;
    background-color: rgba(102, 102, 102);
    margin-bottom: 1px;
    &:last-child {
        margin-bottom: 0;
    }
    @include e(status) {
        top: 10px;
        left: 10px;
        position: absolute;
        width: 120px;
        background: rgba(225, 225, 225, 0.2);
        height: 25px;
        border-radius: 20px;
        line-height: 25px;
        display: flex;
        align-items: center;
        @include m(animation) {
            display: flex;
            justify-content: space-evenly;
            align-items: center;
            width: 30px;
            border-radius: 20px;
            height: 20px;
            margin-left: 5px;
            background: darkcyan;
            & > span:nth-child(1),
            :nth-child(2),
            :nth-child(3) {
                display: inline-block;
                width: 3px;
                height: 15px;
                background: #fff;
                border-radius: 10px;
            }
            & > span:nth-child(2) {
                height: 10px;
            }
        }
        @include m(text) {
            padding: 0 10px;
            color: #fff;
        }
    }
    @include e(title) {
        font-size: 13px;
        position: absolute;
        bottom: 50px;
        left: 10px;
    }
    @include e(nickname) {
        font-size: 12px;
        position: absolute;
        left: 30px;
        bottom: 30px;
    }
    @include e(love) {
        position: absolute;
        right: 25px;
        bottom: 30px;
        height: 15px;
        line-height: 15px;
        &::before {
            content: '❤️';
            display: inline-block;
            text-align: center;
            border-radius: 50%;
            background: #fff;
            height: 20px;
            line-height: 20px;
            width: 20px;
            text-align: center;
            margin-right: 5px;
        }
    }
}
/* 一行两个 */
@include b(a-line-two-model) {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    color: #fff;
    @include e(item) {
        position: relative;
        width: calc(50% - 5px);
        height: 200px;
        background-color: rgba(102, 102, 102);
        margin-bottom: 1px;
    }
}
/* 横向滚动 */
@include b(horizontal-scroll-model) {
    display: inline-flex;
    width: 100%;
    overflow-x: auto;
    color: #fff;
    justify-content: flex-start;
    @include e(item) {
        flex-shrink: 0;
        position: relative;
        width: calc(50% - 5px);
        height: 200px;
        background-color: rgba(102, 102, 102);
    }
}
</style>
