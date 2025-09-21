<template>
    <div :style="{ height: '30px', lineHeight: '30px', color: $props.formData.color }">
        <i class="iconfont icon-dizhi" />
        {{ computedAddress }} &nbsp;>
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import defaultPositionStyleData from './positioningStyle'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultPositionStyleData>,
        default: defaultPositionStyleData,
    },
})
const computedAddress = computed(() => {
    if ($props.formData.showType === 'province') {
        return '浙江省宁波市江北区'
    } else if ($props.formData.showType === 'city') {
        return '宁波市江北区'
    } else if ($props.formData.showType === 'cityDetails') {
        return '宁波市江北区柯力传感科技有限公司'
    } else {
        return '柯力传感科技有限公司'
    }
})
</script>
