<template>
    <div style="margin: 16px">
        <router-link v-for="item in arr" :key="item.name" target="_blank" :to="{ path: item.path }" @click="handleNavToSet(item.key)">
            <!-- <el-button> 点此进入{{ item.name }}装修 </el-button> -->
            <el-button> {{ item.name }}端 </el-button>
        </router-link>
    </div>
</template>

<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'

const arr = ref([
    { key: 'H5_APP', name: 'H5、App', path: '/decoration/set' },
    { key: 'WECHAT_MINI_APP', name: '微信小程序', path: '/decoration/set' },
    { key: 'PC', name: 'PC', path: '/decoration/pc' },
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
