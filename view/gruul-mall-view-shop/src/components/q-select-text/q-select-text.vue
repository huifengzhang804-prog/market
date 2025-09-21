<script setup lang="ts">
import useGetPageData from '@/views/decoration/pc/components/menu/action-menu/views/custom-page/useGetPageData'
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
const { getText, textData, textNoMore, textLoading } = useGetPageData()

onBeforeMount(() => {
    getText(false, 20)
})
</script>

<template>
    <select-main v-model:select="selectPage" :table-data="textData" :loading="textLoading" :no-more="textNoMore" @get-data="getText" />
</template>

<style lang="scss" scoped></style>
