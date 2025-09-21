<script lang="ts" setup>
import { configurePlatform } from '@/store/modules/configurePlatform'
import MHandlers from './MHandlers.vue'
import Navigate from './Navigate.vue'
import { useNav } from '@/components/layout/hooks/useNav'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import ChangePassword from '@/components/changePassword/index.vue'

const { isCollapse } = useNav()
const route = useRoute()
const { getPlatformLogo: logo, getPlatformName: platform } = storeToRefs(configurePlatform())

const noPadding = computed(() => {
    return route.meta && route.meta.noPadding
})
</script>

<template>
    <el-container class="app_container">
        <!-- 顶部bar -->
        <el-header class="app_header">
            <div class="app_header_left">
                <div class="admin__aside--shop" style="outline: none">
                    <div class="avatar--box">
                        <img :src="logo" class="shop--logo" />
                        <div class="shop--name">
                            <span class="el-dropdown-link">
                                <span :title="platform">{{ platform }}</span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="app_header_right">
                <MHandlers />
            </div>
        </el-header>
        <!-- 主体区域 -->
        <el-container class="app_container--main">
            <!-- 左侧菜单栏 -->
            <el-aside :style="`background: ${isCollapse ? 'transparent' : '#fff'};min-width: ${isCollapse ? '90px' : '180px'};`" class="app_nav">
                <Navigate />
            </el-aside>
            <!-- 主内容区 -->
            <el-main class="app_main">
                <MBreadcrumb />
                <div id="admin__main--mask" ref="admin_content" :class="noPadding && 'no--padding'" class="admin__main--content">
                    <router-view></router-view>
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

.admin {
    &__aside {
        margin-right: 12px;
        z-index: 898;

        &--shop {
            @include flex(space-between, center);
            padding-right: 20px;
            width: 100%;
            color: #fff;
            height: 48px;
            cursor: pointer;

            .shop--logo {
                margin-right: 8px;
                border-radius: 5px;
                width: 30px;
                height: 30px;
                border: 1px solid rgba($color: #fff, $alpha: 0.5);
            }

            .shop--name .el-dropdown-link {
                color: #fff;
                font-weight: bold;
                cursor: pointer;

                span {
                    display: inline-block;
                    max-width: 155px;
                    font-size: 14px;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    vertical-align: middle;
                }
            }
        }
    }
}

.avatar--box {
    display: flex;
    align-items: center;
    height: 100%;
}
</style>
