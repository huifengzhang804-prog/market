<script setup lang="ts">
import { doGetUserTag } from '@/apis/vip'
import { useVipTagsStore } from '@/store/modules/vipSetting'
import { ElMessage } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import type { SearchFromDataType } from '@/views/baseVip/types/index.ts'
import { UserTagVo } from '@/apis/vip/types'

const $emit = defineEmits(['search-data', 'changeShow'])
useVipTagsStore()
// 下拉选择状态初始数据
const searchFromData = reactive<SearchFromDataType>({
    userCardNum: '', // 会员卡号
    userNickname: '', // 用户名称
    tagId: '', //标签id
    userPhone: '', // 用户手机
    memberType: '', // 用户类型
    clinchTime: ['', ''],
    rankCode: '',
    consigneeName: '', // 收货人姓名
})
const userTagList = ref<{ label: string; value: string }[]>([])
async function initGetUserTag() {
    const { code, data } = await doGetUserTag({ bound: true })
    if (code !== 200) return ElMessage.error('会员标签获取失败')
    const list = [{ label: '全部', value: '' }]
    list.push(
        ...data.map((v: UserTagVo) => {
            return {
                ...v,
                value: v.id as any,
                label: v.tagName,
            }
        }),
    )
    userTagList.value = list
    return list
}
initGetUserTag()
// 表单配置
const columns = computed(() => {
    return [
        {
            label: '用户名称',
            prop: 'userNickname',
            valueType: 'copy',
            fieldProps: {
                placeholder: '请输入用户名称',
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
            label: '会员类型',
            prop: 'memberType',
            valueType: 'select',
            options: [
                {
                    value: '',
                    label: '全部',
                },
                {
                    value: 'PAID_MEMBER',
                    label: '付费会员',
                },
                {
                    value: 'FREE_MEMBER',
                    label: '免费会员',
                },
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
            label: '注册时间',
            prop: 'clinchTime',
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
            label: '标签',
            prop: 'tagId',
            valueType: 'select',
            options: userTagList.value,
            fieldProps: {
                placeholder: '请选择',
            },
        },
    ]
})

const HandleSearch = () => {
    const { userCardNum, userNickname, consigneeName, tagId, userPhone, memberType, rankCode } = searchFromData
    const params = {
        userCardNum,
        userNickname,
        consigneeName,
        registrationStartTime: '',
        registrationEndTime: '',
        tagId,
        userPhone,
        memberType,
        rankCode,
    }
    if (Array.isArray(searchFromData.clinchTime)) {
        params.registrationStartTime = searchFromData.clinchTime[0]
        params.registrationEndTime = searchFromData.clinchTime[1]
    }
    $emit('search-data', params)
}
const handleReset = () => {
    searchFromData.clinchTime = ['', '']
    searchFromData.consigneeName = ''
    searchFromData.userCardNum = ''
    searchFromData.userNickname = ''
    searchFromData.tagId = ''
    searchFromData.userPhone = ''
    searchFromData.memberType = ''
    searchFromData.rankCode = ''
    HandleSearch()
}
</script>

<template>
    <!-- 搜索部分s -->
    <div>
        <SchemaForm v-model="searchFromData" :columns="columns" label-width="90" @searchHandle="HandleSearch" @handleReset="handleReset">
        </SchemaForm>
    </div>
    <!-- 搜索部分e -->
</template>

<style lang="scss" scoped></style>
