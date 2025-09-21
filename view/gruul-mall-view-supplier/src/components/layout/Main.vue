<script lang="ts" setup>
import MBreadcrumb from './MBreadcrumb.vue'
import MHandlers from './MHandlers.vue'
import Navigate from './Navigate.vue'
import MAvatar from './MAvatar.vue'
import { useNav } from '@/components/layout/hooks/useNav'
import { useRoute } from 'vue-router'
import ChangePassword from '@/components/changePassword/index.vue'

const { isCollapse } = useNav()
const route = useRoute()
const noPadding = computed(() => {
    return route.meta && route.meta.noPadding
})
</script>

<template>
    <el-container class="app_container">
        <!-- 顶部bar -->
        <el-header class="app_header">
            <div class="app_header_left">
                <MAvatar />
            </div>
            <div class="app_header_right">
                <MHandlers />
            </div>
        </el-header>
        <!-- 主体区域 -->
        <el-container class="app_container--main">
            <!-- 左侧菜单栏 -->
            <el-aside class="app_nav" :style="`background: ${isCollapse ? 'transparent' : '#fff'};min-width: ${isCollapse ? '90px' : '180px'};`">
                <Navigate />
            </el-aside>
            <!-- 主内容区 -->
            <el-main class="app_main">
                <MBreadcrumb />
                <div id="admin__main--mask" class="admin__main--content" :class="noPadding && 'no--padding'">
                    <router-view v-slot="{ Component }">
                        <component :is="Component" />
                    </router-view>
                </div>
                <ChangePassword />
            </el-main>
        </el-container>
    </el-container>
</template>

<style lang="scss" scoped>
// 屏幕宽度小于1620px时候,.app_container宽度全屏
@media screen and (max-width: 1620px) {
    .app_container {
        width: 100% !important;
    }
}

.app_container {
    height: 100%;
    width: 1440px;
    margin-left: auto;
    margin-right: auto;
    .app_header {
        height: 48px;
        display: flex;
        align-items: center;
        padding-left: 21px;
        padding-top: 9px;
        padding-bottom: 9px;
        padding-right: 13.39px;
        background-color: $color-primary;
        .app_header_left {
            width: 142px;
        }
        .app_header_right {
            margin-left: auto;
        }
    }
    .app_container--main {
        height: calc(100% - 48px);
        .app_nav {
            width: unset;
            margin-right: 12px;
            overflow: unset;
        }
        .app_main {
            position: relative;
            height: 100%;
            padding: 0;
            display: flex;
            flex-direction: column;
        }
    }
}
</style>
