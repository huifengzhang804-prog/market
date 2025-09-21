<script setup lang="ts">
import useGetPageData from '@/views/decoration/platformDecoration/pc/components/menu/action-menu/views/custom-page/useGetPageData'
import selectMain from '../selectMain.vue'
import { useVModel } from '@vueuse/core'

const props = defineProps<{
    link: any
}>()

const emit = defineEmits(['update:link'])
const selectPage = useVModel(props, 'link', emit)

/**
 * 获取数据
 */
const { getActivity, activityData, activeNoMore, activeLoading } = useGetPageData()

onBeforeMount(() => {
    getActivity(false, 20)
})
</script>

<template>
    <select-main v-model:select="selectPage" :table-data="activityData" :loading="activeNoMore" :no-more="activeLoading" @get-data="getActivity" />
</template>

<style lang="scss" scoped></style>
