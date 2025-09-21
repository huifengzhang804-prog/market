<script setup lang="ts">
import defaultNavData from './navigation'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultNavData>,
        default() {
            return defaultNavData
        },
    },
})

const computedTotalNum = computed(() => {
    if ($props.formData.rowNums === 5) {
        if ($props.formData.rows === 1) return 5
        return 10
    } else {
        if ($props.formData.rows === 1) return 4
        return 8
    }
})
</script>

<template>
    <div>
        <ul class="storeNavigation-item">
            <li
                v-for="(navItem, index) in $props.formData.navigationList.slice(0, computedTotalNum)"
                :key="index"
                :style="{
                    width: $props.formData.rowNums === 5 ? '20%' : '25%',
                }"
            >
                <img class="storeNavigation-item__img" :src="navItem.navIcon" />
                <div :style="{ color: navItem.fontColor }">
                    {{ navItem.navName }}
                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>
@include b(storeNavigation-item) {
    list-style: none;
    margin: 0;
    padding: 0;
    display: flex;
    flex-wrap: wrap;
    // overflow-x: auto;
    padding: 10px 0;

    & > li {
        flex: none;
        width: 25%;
        text-align: center;
        margin-bottom: 5px;
        margin-top: 5px;
    }

    @include e(img) {
        width: 50px;
        height: 50px;
        display: inline-block;
    }
}

@include b(storeNavigation-item-form) {
    position: relative;
    border: 1px solid #eeeeee;
    padding: 10px;
    margin-bottom: 10px;

    @include b(avatar) {
        width: 48px;
        height: 48px;
        display: block;
    }

    @include b(avatar-uploader) {
        @include e(icon) {
            font-size: 28px;
            color: #8c939d;
            width: 48px;
            height: 48px;
            line-height: 48px;
            text-align: center;
        }

        @include b(el-upload) {
            border: 1px dashed #d9d9d9;
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;

            &:hover {
                border-color: #409eff;
            }
        }
    }
}
</style>
