<template>
    <ul class="container">
        <li v-for="item in tabList" :key="item.name" :class="value === item.name && 'active'" @click="$emit('handle-tabs', item.name)">
            <span>{{ item.label }}</span>
        </li>
    </ul>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'

const $emit = defineEmits(['handle-tabs'])

defineProps({
    // 绑定值
    value: {
        type: String,
        default: '',
    },
    // 关闭按钮
    closeBtn: {
        type: Boolean,
        default: false,
    },
    // 图片链接
    imgSrc: {
        type: String,
        default: '',
    },
    // 渲染数据
    tabList: {
        type: Array as PropType<any[]>,
        default: () => [],
    },
})
</script>

<style lang="scss" scoped>
.container {
    background-color: #f5f7fa;
    height: 45px;
    padding: 8px 8px 0;
    box-sizing: border-box;
    display: flex;
    justify-content: flex-start;
    overflow: hidden;
    flex-shrink: 0;
}

li {
    position: relative;
    font-size: 15px;
    border-radius: 10px 10px 0 0;
    display: flex;
    align-items: center;
    z-index: 1;
    cursor: pointer;
    transition: 0.3s all;
    padding: 0 26px;
    font-weight: 550;
    img {
        width: 16px;
        height: 16px;
        margin-left: 10px;
        margin-right: 10px;
    }

    .close {
        right: 8px;
        top: 8px;
        position: absolute;
        font-size: 18px;
        transform: rotate(45deg);
        border-radius: 50%;
        cursor: pointer;
        font-weight: bold;
        &:hover {
            background: #d0d0d0;
        }
    }
}

li.active {
    background: #fff;
    z-index: 2;
    color: #555cfd;
    &::before,
    &::after {
        border-top: 8px solid #fff;
    }
}

li::before,
li::after {
    position: absolute;
    content: '';
    width: 16px;
    height: 16px;
    border-radius: 50%;
    border: 8px solid transparent;
    transition: 0.3s all;
    //border-top: 5px solid #fff;
}

li::before {
    bottom: -8px;
    left: -24px;
    transform: rotate(135deg);
}

li::after {
    bottom: -7px;
    right: -24px;
    transform: rotate(-135deg);
}

li:hover:not(.active) {
    background: #ededed;
    &::before,
    &::after {
        border-top: 8px solid #ededed;
    }
}
</style>
