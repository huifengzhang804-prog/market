<script setup lang="ts">
import { getConfig } from '@/libs/config'
import { Menu, MenuType } from '@/components/layout/layout'
import { useNav } from '@/components/layout/hooks/useNav'
import { type PropType, type CSSProperties, toRaw } from 'vue'
import { ArrowDownBold, ArrowUpBold, ArrowRightBold, ArrowLeftBold } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const attrs = useAttrs()
const { layout, isCollapse } = useNav()
const route = useRoute()

defineProps({
    item: {
        type: Object as PropType<Menu>,
        required: true,
    },
    isNest: {
        type: Boolean,
        default: false,
    },
    marginBottom: {
        type: String,
        default: '0',
    },
})

const getNoDropdownStyle = computed((): CSSProperties => {
    return {
        width: '100%',
        display: 'flex',
        alignItems: 'baseLine',
    }
})

const getSubMenuIconStyle = computed((): CSSProperties => {
    return {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        margin: layout.value === 'horizontal' ? '0 12px 0 0' : isCollapse.value ? '0 auto' : '0 12px 0 0',
    }
})

const expandCloseIcon = computed(() => {
    if (!getConfig()?.MenuArrowIconNoTransition) return ''
    return {
        'expand-close-icon': ArrowDownBold,
        'expand-open-icon': ArrowUpBold,
        'collapse-close-icon': ArrowRightBold,
        'collapse-open-icon': ArrowLeftBold,
    }
})

/**
 * 判断当前菜单/目录是否是激活状态
 * @param menu
 */
const isActive = (menu: Menu): boolean => {
    if (menu.type === MenuType.MENU) {
        return menu.path === route.matched[2]?.path || menu.path === route.matched[1].path
    }

    return menu.children?.some(isActive) ?? false
}
</script>

<template>
    <div class="outer-most select-none">
        <!-- 没子菜单 -->
        <router-link v-if="!item.children" :to="item.path">
            <el-menu-item
                :data-index="item.id.toString()"
                :index="`${item.order}`"
                :class="{
                    noHover: true,
                    'submenu-title-noDropdown': !isNest,
                    'active-menu': isActive(item) && item.parentId !== 0 && !isCollapse,
                    'popper-active-menu': isActive(item) && item.parentId !== 0 && isCollapse,
                }"
                :style="getNoDropdownStyle"
                v-bind="attrs"
            >
                <div v-if="toRaw(item.icon) && item.parentId === 0" :style="getSubMenuIconStyle">
                    <q-icon :class="item?.icon" class="place" />
                    <div v-if="isCollapse" style="margin-left: 12px">{{ item.name.substring(0, 2) }}</div>
                </div>

                <template #title>
                    <el-badge
                        :class="{
                            father_menu_badge: item.parentId === 0 && !isCollapse,
                            child_menu_badge: item.parentId !== 0 && !isCollapse,
                            show_menu_badge: isCollapse,
                        }"
                        :show-zero="false"
                    >
                        <div
                            :class="{
                                default_class: true,
                                'active-popper-icon': isActive(item),
                            }"
                        >
                            {{ item.name }}
                        </div>
                    </el-badge>
                </template>
            </el-menu-item>
        </router-link>

        <!-- 有子菜单 -->
        <el-sub-menu
            v-else
            ref="subMenu"
            :data-index="item.id.toString()"
            teleported
            :index="`${item.order}`"
            v-bind="expandCloseIcon"
            :style="{
                width: !isCollapse ? '180px' : 'unset',
            }"
        >
            <template #title>
                <div
                    v-if="toRaw(item.icon)"
                    :style="getSubMenuIconStyle"
                    :class="{
                        'active-popper-icon': isActive(item),
                    }"
                >
                    <q-icon :class="item.icon" class="place" />
                    <div v-if="isCollapse" style="margin-left: 12px">{{ item.name.substring(0, 2) }}</div>
                </div>
                <el-badge
                    :class="{
                        father_menu_badge: item.parentId === 0 && !isCollapse,
                        child_menu_badge: item.parentId !== 0 && !isCollapse,
                        show_menu_badge: isCollapse,
                    }"
                    :show-zero="false"
                >
                    <div
                        v-if="!isCollapse"
                        :class="{
                            default_class: true,
                            'active-popper-icon': isActive(item),
                        }"
                    >
                        {{ item.name }}
                    </div>
                </el-badge>
            </template>
            <sidebar-item v-for="child in item.children" :key="child.id" :is-nest="true" :item="child" class="nest-menu" />
        </el-sub-menu>
    </div>
</template>

<style lang="scss" scoped>
.father_menu_badge {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
    color: #333;
    :deep(.el-badge__content) {
        position: relative;
        transform: translateX(8px);
        right: unset;
    }
}
.child_menu_badge {
    height: 100%;
    display: flex;
    align-items: center;
    & > div {
        font-size: 13px !important;
        font-weight: 500;
    }
}

.show_menu_badge {
    :deep(.el-badge__content) {
        display: none;
    }
}
.default_class {
    font-size: 14px;
    font-weight: 500;
}
:deep(.el-menu--inline) {
    display: grid;
    grid-template-columns: 74px 74px;
    grid-row-gap: 16px;
    grid-column-gap: 6px;
    justify-content: center;
    margin-bottom: v-bind(marginBottom);
    overflow: hidden;
    .nest-menu {
        width: 74px;
        height: 30px;
        border-radius: 50px;
        background: rgb(246, 246, 246);
        // overflow: hidden;
        padding-left: 0 !important;
        padding-right: 0 !important;
        text-align: center;
        justify-content: center;
        font-size: 13px;
        font-weight: 600;
        color: #666;
    }
}
.active-menu {
    border: 1px solid rgba(85, 92, 253, 0.8) !important;
    border-radius: 50px;
    color: rgb(85, 92, 253) !important;
    background-color: rgba(85, 92, 253, 0.05) !important;
}
.popper-active-menu {
    border-radius: 10px;
    background: rgb(85, 92, 253, 0.1);
    color: rgb(85, 92, 253) !important;
}
.active-popper-icon {
    color: rgb(85, 92, 253);
    font-weight: 600;
}
.place {
    width: 16px;
}
:deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;
}
:deep(.el-submenu .el-submenu__title) {
    height: 48px;
    line-height: 48px;
}
:deep(.el-sub-menu__title) {
    color: #333;
    height: 48px;
    line-height: 48px;
}
.noHover:hover {
    background-color: transparent !important;
}
:deep(.el-sub-menu__title:hover) {
    background-color: transparent;
}
</style>
