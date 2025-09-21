<script setup lang="ts">
import { type RouteRecordName, useRoute, useRouter } from 'vue-router'
import { useNav } from '@/components/layout/hooks/useNav'
import SidebarItem from '@/components/layout/SidebarItem.vue'
import LaySidebarCenterCollapse from '@/components/layout/SidebarCenterCollapse.vue'
import { Menu, MenuType } from '@components/layout/layout'
import { useMenuList } from '@/store/modules/menu'
import { storeToRefs } from 'pinia'
import { doGetUserMenu } from '@/apis/sign'

const route = useRoute()
const menus = ref<Menu[]>([])
const defaultOpeneds = ref<string[]>([])
const { device, pureApp, isCollapse, toggleSideBar } = useNav()

const loading = computed(() => (pureApp.layout === 'mix' ? false : menus.value.length === 0 ? true : false))

/** 查找对应 `path` 的路由信息 */
function findRouteByPath(path: string, routes: Menu[] | undefined): Menu | null {
    if (!routes) {
        return null
    }
    let res = routes.find((item: { path: string }) => item.path === path) || null
    if (res) {
        return isProxy(res) ? toRaw(res) : res
    } else {
        for (let i = 0; i < routes.length; i++) {
            if (Array.isArray(routes[i].children) && (routes[i].children as Menu[]).length > 0) {
                res = findRouteByPath(path, routes[i]?.children)
                if (res) {
                    return isProxy(res) ? toRaw(res) : res
                }
            }
        }
        return null
    }
}

const defaultActive = computed(() => {
    return findRouteByPath(route.path, menus.value)?.id
})

const appendFatherTitle = async () => {
    await nextTick()
    const targetDom = Array.from(document.querySelectorAll('.nav-popper-menu > .nav-popper-menu > .el-menu'))
    if (targetDom.length === 0) {
        return
    }
    targetDom.forEach((item) => {
        const parentMenuName =
            menus.value.find((menu) => {
                return !!menu.children?.find((child) => child.name === item.children[0]?.querySelector('.el-badge div')?.textContent)
            })?.name || ''
        const div = document.createElement('div')
        div.innerHTML = `
        <div class="nav_popper--title">
        ${parentMenuName}
        </div>`
        // item 之前插入 dom
        item.insertBefore(div, item.firstChild)
    })
}

watch(
    () => isCollapse.value,
    (val) => {
        setTimeout(() => {
            appendFatherTitle()
        }, 500)
    },
    {
        immediate: true,
    },
)

/**
 * @description 刷新的时候会初始化Menu组件，在店铺列表下面有一个查看页面需要排除掉，否则刷新页面会回到首页，因为这个页面不在菜单列表下面
 * @type { Array<RouteRecordName> }
 */
const whiteRouteName: RouteRecordName[] = ['previewShop']

/**
 * 获取菜单所有的访问路径
 * @param menuArray 菜单数组
 */
const getAllPath = (menuArray: Array<Menu> | undefined): string[] => {
    return (menuArray ?? []).flatMap((menu) => (menu.type === MenuType.MENU ? [menu.path] : getAllPath(menu.children)))
}

const setDefaultOpeneds = (list: Menu[]) => {
    list.forEach((item) => {
        defaultOpeneds.value.push(item.order)
        if (item.children) {
            setDefaultOpeneds(item.children)
        }
    })
}

const { getterMenu } = storeToRefs(useMenuList())
const asyncGetSubMenuData = async () => {
    const res = await doGetUserMenu()
    if (res.code !== 200) {
        return
    }
    // 存到pinia
    useMenuList().SET_MENU(res.data.menus)
    useMenuList().SET_ISADMIN(res.data.isAdmin)
    updateOrder(res.data.menus)
    menus.value = res.data.menus
    await nextTick()
    setDefaultOpeneds(res.data.menus)
}

function updateOrder(items: Menu[]) {
    items.forEach((item) => {
        item.order = `${item.order}`
        if (item.children) {
            item.children.forEach((child) => {
                child.order = `${item.order}-${child.order}`
            })
            updateOrder(item.children) // 递归调用
        }
    })
}
updateOrder(getterMenu.value)
setDefaultOpeneds(getterMenu.value)

const getSubMenuData = () => {
    menus.value = getterMenu.value
}

onMounted(() => {
    if (Object.keys(getterMenu.value).length === 0) {
        asyncGetSubMenuData()
    }
    getSubMenuData()
})
</script>

<template>
    <div v-loading="loading" class="sidebar-container">
        <el-scrollbar wrap-class="scrollbar-wrapper" :class="[device === 'mobile' ? 'mobile' : 'pc']">
            <el-menu
                mode="vertical"
                popper-class="nav-popper-menu"
                class="outer-most select-none"
                :collapse="isCollapse"
                :collapse-transition="true"
                popper-effect="light"
                :default-active="defaultActive"
                :default-openeds="defaultOpeneds"
            >
                <SidebarItem v-for="menu in menus" :key="menu.id" :item="menu" />
            </el-menu>
        </el-scrollbar>
        <!-- 中部控制左右伸缩 -->
        <LaySidebarCenterCollapse :is-active="pureApp.sidebar.opened" @toggleClick="toggleSideBar" />
        <!-- <LaySidebarCenterCollapse
            v-if="device !== 'mobile' && (isShow || isCollapse)"
            :is-active="pureApp.sidebar.opened"
            @toggleClick="toggleSideBar"
        /> -->
    </div>
</template>

<style lang="scss" scoped>
:deep(.el-scrollbar) {
    &.pc {
        height: calc(100vh - 48px);
    }
}
:deep(.el-menu) {
    background: transparent !important;
    border-right: unset;
}
:deep(.el-loading-mask) {
    opacity: 0.45;
}
.sidebar-container {
    position: relative;
}
.select-none {
    user-select: none;
}
:deep(.el-sub-menu) {
    &:has(.active-menu) {
        .el-sub-menu__title {
            font-weight: bold;
            color: rgb(85, 92, 253);
        }
    }
}
:deep(.el-menu-item.is-active.submenu-title-noDropdown) {
    font-weight: bold;
    color: rgb(85, 92, 253);
}
:deep(.el-menu.el-menu--inline) {
    padding: 16px 0;
    background-color: #fbfcff !important;
}
:deep(.el-menu--collapse) {
    width: 90px;
    margin-top: 40px;
    .el-sub-menu__icon-arrow {
        display: none;
    }
}
</style>
<style lang="scss">
.el-popper.is-light {
    &:has(> .el-badge.show_menu_badge) {
        display: none;
    }
    &.nav-popper-menu {
        &:has(.el-badge.show_menu_badge) {
            display: unset;
        }
    }
}
.nav-popper-menu {
    border: none !important;
    .el-menu--popup {
        display: flex;
        min-width: 160px;
        border-radius: 0 10px 10px 10px;
        padding: 20px 15px;
        align-content: center;
        justify-content: center;
        flex-direction: column;
        box-shadow: 3px 3px 15px 3px rgba(0, 0, 0, 0.1);
        .el-menu-item {
            width: 120px !important;
            height: 40px;
            line-height: 40px;
            color: rgb(138, 140, 153);
        }
        .el-menu-item:hover {
            background-color: rgb(85, 92, 253, 0.1);
            color: #555cfd;
            border-radius: 10px;
        }
        .nav_popper--title {
            display: flex;
            transform: translateX(20px);
            align-items: center;
            color: #000;
            font-weight: bold;
            font-size: 16px;
            height: 40px;
            cursor: not-allowed;
            user-select: none;
        }
    }
}
</style>
