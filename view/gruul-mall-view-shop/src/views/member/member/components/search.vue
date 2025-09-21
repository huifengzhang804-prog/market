<script lang="ts" setup>
import { cloneDeep } from 'lodash-es'
import SchemaForm from '@/components/SchemaForm.vue'

export interface SearchOptions {
    userNickname: string
    userPhone: string
    birthdayRange: string
    memberType: string
    rankCode: string
    tagId: string
}

const $emit = defineEmits(['onSearchParams'])
const searchOptions = reactive({
    userNickname: '',
    userPhone: '',
    birthdayRange: '',
    memberType: '',
    rankCode: '',
    tagId: '',
})
const props = defineProps({
    shopTags: {
        type: Array<any>,
        default: () => [],
    },
})
const columns = computed(() => {
    return [
        {
            label: '昵称',
            prop: 'userNickname',
            valueType: 'copy',
            fieldProps: {
                placeholder: '请输入昵称',
            },
        },
        {
            label: '手机号',
            prop: 'userPhone',
            valueType: 'copy',
            fieldProps: {
                placeholder: '请输入手机号',
            },
        },
        {
            label: '生日',
            prop: 'birthdayRange',
            valueType: 'date-picker',
            fieldProps: {
                type: 'daterange',
                startPlaceholder: '开始时间',
                endPlaceholder: '结束时间',
                format: 'YYYY/MM/DD',
                valueFormat: 'YYYY-MM-DD',
            },
        },
        {
            label: '会员类型',
            prop: 'memberType',
            valueType: 'select',
            options: [
                { label: '全部', value: '' },
                { label: '免费会员', value: 'FREE_MEMBER' },
                { label: '付费会员', value: 'PAID_MEMBER' },
            ],
            fieldProps: {
                placeholder: '请选择',
            },
        },
        {
            label: '会员等级',
            prop: 'rankCode',
            valueType: 'select',
            fieldProps: {
                placeholder: '请选择会员等级',
            },
            options: [
                { label: '全部', value: '' },
                ...new Array(10).fill(0).map((_, index) => {
                    return {
                        label: `等级${index + 1}`,
                        value: index + 1,
                    }
                }),
            ],
        },
        {
            label: '标签',
            prop: 'tagId',
            valueType: 'select',
            options: [
                { label: '全部', value: '' },
                ...props.shopTags.map((item) => {
                    return {
                        label: item.tagName,
                        value: item.id,
                    }
                }),
            ],
            fieldProps: {
                placeholder: '请选择标签',
            },
        },
    ]
})

const handleSearch = () => {
    const searchType = cloneDeep(searchOptions) as any
    searchType.birthdayStartTime = searchType.birthdayRange?.[0] || ''
    searchType.birthdayEndTime = searchType.birthdayRange?.[1] || ''
    delete searchType.birthdayRange
    $emit('onSearchParams', searchType)
}
const handleReset = () => {
    searchOptions.birthdayRange = ''
    searchOptions.userNickname = ''
    searchOptions.userPhone = ''
    searchOptions.memberType = ''
    searchOptions.rankCode = ''
    searchOptions.tagId = ''
    handleSearch()
}
</script>

<template>
    <SchemaForm v-model="searchOptions" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset" />
</template>
