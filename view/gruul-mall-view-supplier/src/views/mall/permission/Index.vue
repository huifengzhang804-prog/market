<template>
    <ChromeTab :tab-list="tabList" :value="currentTab" style="margin-bottom: 16px" @handle-tabs="handleTabClick" />
    <component :is="map[currentTab]"></component>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import ChromeTab from '@/components/ChromeTab.vue'

enum Tab {
    ROLE = 'ROLE',
    Admin = 'Admin',
}
const tabList = [
    {
        name: 'Admin',
        label: '系统用户',
    },
    {
        name: 'ROLE',
        label: '角色管理',
    },
]
type TabMap = {
    [key in keyof typeof Tab]: Component
}

const currentTab = ref<keyof typeof Tab>('Admin')
const map: TabMap = {
    [Tab.Admin]: defineAsyncComponent(() => import('./AdminList.vue')),
    [Tab.ROLE]: defineAsyncComponent(() => import('./RoleList.vue')),
}
// 切换tab
const handleTabClick = (status: keyof typeof Tab) => {
    currentTab.value = status
}
</script>

<style scoped lang="scss"></style>
