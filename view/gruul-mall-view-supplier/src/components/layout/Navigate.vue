<script setup lang="ts">
import { type RouteRecordName, useRoute, useRouter } from 'vue-router'
import { useNav } from '@/components/layout/hooks/useNav'
import SidebarItem from '@/components/layout/SidebarItem.vue'
import LaySidebarCenterCollapse from '@/components/layout/SidebarCenterCollapse.vue'
import { Menu } from '@components/layout/layout'
import { useMenuList } from '@/store/modules/menu/index'
import { storeToRefs } from 'pinia'
import { doGetUserMenu } from '@/apis'

const route = useRoute()
const router = useRouter()
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
 * 刷新的时候会初始化Menu组件，在店铺列表下面有一个查看页面需要排除掉，否则刷新页面会回到首页，因为这个页面不在菜单列表下面
 * @type { Array<RouteRecordName> }
 */
const whiteRouteName: RouteRecordName[] = ['previewShop']

const setDefaultOpeneds = (list: Menu[]) => {
    // 清空之前的默认展开项
    defaultOpeneds.value = []
    // 递归设置所有菜单为展开状态
    const setAllOpeneds = (menuList: Menu[]) => {
        menuList.forEach((item) => {
            // 只对有子菜单的项目设置展开
            if (item.children && item.children.length > 0) {
                defaultOpeneds.value.push(item.order)
                setAllOpeneds(item.children)
            }
        })
    }
    setAllOpeneds(list)
}

const getSubMenuList = async () => {
    const res = await doGetUserMenu()
    if (res.code !== 200) {
        return
    }
    // 存到pinia
    useMenuList().SET_MENU(res.data.menus)
    useMenuList().SET_ISADMIN(res.data.isAdmin)
    return true
}
const { getterMenu } = storeToRefs(useMenuList())

watch(
    () => getterMenu.value,
    (val) => {
        if (val?.length === 0) {
            getSubMenuList().then(() => {
                getSubMenuData()
            })
        } else if (val && val.length > 0) {
            // 当菜单数据有内容时，设置默认展开
            setDefaultOpeneds(val)
        }
    },
    {
        immediate: true,
    },
)

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

const getSubMenuData = async () => {
    menus.value = getterMenu.value
}

onMounted(() => {
    getSubMenuData()
})
</script>

<template>
    <div v-if="!loading" class="sidebar-container">
        <el-scrollbar wrap-class="scrollbar-wrapper" :class="[device === 'mobile' ? 'mobile' : 'pc']">
            <el-menu
                mode="vertical"
                popper-class="nav-popper-menu"
                class="outer-most select-none"
                :collapse="isCollapse"
                :collapse-transition="true"
                popper-effect="light"
                :default-active="defaultActive?.toString()"
                :default-openeds="defaultOpeneds"
            >
                <SidebarItem v-for="menu in menus" :key="menu.id" :item="menu" />
            </el-menu>
        </el-scrollbar>
        <!-- 中部控制左右伸缩 -->
        <LaySidebarCenterCollapse :is-active="pureApp.sidebar.opened" @toggleClick="toggleSideBar" />
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
    margin-top: 40px;
    width: 90px;
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
        padding: 20px 15px;
        border-radius: 0 10px 10px 10px;
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
