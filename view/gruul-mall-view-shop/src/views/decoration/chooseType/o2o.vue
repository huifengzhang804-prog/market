<template>
    <div style="margin-top: 36px" class="tab_container">
        <router-link v-for="item in arr" :key="item.name" target="_blank" :to="{ path: '/decoration/seto2o' }" @click="handleNavToSet(item.key)">
            <el-button> 点此进入{{ item.name }}装修 </el-button>
        </router-link>
    </div>
</template>

<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'

const arr = ref([
    { key: 'H5_APP', name: 'H5' },
    { key: 'WECHAT_MINI_APP', name: '微信' },
])

const $decorationStore = useDecorationStore()

const handleNavToSet = (params: string) => {
    $decorationStore.SET_ENDPOINT_TYPE(params)
}
</script>

<style scoped lang="scss">
a {
    display: inline-block;
    margin-right: 50px;
    width: 146px;
}
.el-button {
    margin-right: 55px;
    width: 146px;
    height: 82px;
    line-height: 82px;
    text-align: center;
}
</style>
