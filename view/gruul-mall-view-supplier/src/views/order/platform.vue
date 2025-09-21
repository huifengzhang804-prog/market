<template>
    <div class="platform">
        <QIcon :name="platformIconMap[platform]" />
        <span>{{ platformDescMap[platform] }}</span>
    </div>
</template>

<script lang="ts" setup>
import QIcon from '@/components/q-icon/q-icon.vue'
import type { PLATFORM } from './types'
import { PropType } from 'vue'
defineProps({
    platform: {
        type: String as PropType<PLATFORM>,
        default: '',
    },
})
const platformIconMap: Record<PLATFORM, string> = {
    WECHAT_MINI_APP: 'icon-xiaochengxu',
    WECHAT_MP: 'icon-weixin',
    H5: 'icon-shouji2',
    IOS: 'icon-iOS',
    PC: 'icon-zhuomian-diannaoxianshiqi',
    ANDROID: 'icon-anzhuo',
    HARMONY: 'icon-huawei',
}
const platformDescMap: Record<PLATFORM, string> = {
    WECHAT_MINI_APP: '小程序',
    WECHAT_MP: '公众号',
    H5: 'H5商城',
    IOS: 'IOS端',
    PC: 'PC商城',
    ANDROID: '安卓端',
    HARMONY: '鸿蒙端',
}
</script>

<style lang="scss" scoped>
@include b(platform) {
    flex-shrink: 0;
    margin-right: 5px;
}
</style>
