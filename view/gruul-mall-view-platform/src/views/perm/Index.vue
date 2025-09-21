<!--
 * 权限设置
-->
<template>
    <ChromeTab :tab-list="tabList" :value="currentTab" @handle-tabs="changeTab" />
    <component :is="reactiveComponent[currentTab as keyof typeof reactiveComponent]" />
</template>

<script setup lang="ts">
import ChromeTab from '@/components/chrome-tab/index.vue'

const currentTab = ref('Admin')
const tabList = [
    {
        label: '系统用户',
        name: 'Admin',
    },
    {
        label: '角色管理',
        name: 'Role',
    },
]

const changeTab = (value: any) => {
    if (value) currentTab.value = value
}
const reactiveComponent = {
    Admin: defineAsyncComponent(() => import('./AdminList.vue')),
    Role: defineAsyncComponent(() => import('./RoleList.vue')),
}
</script>

<style scoped lang="scss"></style>
