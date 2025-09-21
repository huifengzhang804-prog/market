<template>
    <el-collapse>
        <template v-for="menu in (menus as any)" :key="menu">
            <div v-if="menu.children">
                <div class="admin__menu--item">
                    <el-collapse-item>
                        <template #title>
                            <router-link to="">
                                <div class="item--title">
                                    <q-icon :class="menu.icon" class="place" size="18px" />
                                    <span>{{ menu.name }} </span>
                                </div>
                            </router-link>
                        </template>
                        <div class="item__sub--menu">
                            <template v-for="subMenu of listMenus(menu)" :key="subMenu">
                                <div class="sub--item pt20">
                                    <router-link
                                        :class="{ active: isActive(subMenu) }"
                                        :to="routerPath(subMenu)"
                                        @click.stop="() => routerClick(true, menu.id, subMenu)"
                                    >
                                        <span style="position: relative">
                                            {{ subMenu.name }}
                                            <i v-show="props.modelValue" class="iconfont icon-youjiantou" style="position: absolute; right: -14px" />
                                        </span>
                                    </router-link>
                                </div>
                            </template>
                        </div>
                    </el-collapse-item>
                </div>
            </div>
            <!-- 只渲染一级目录 -->
            <template v-else>
                <div class="admin__menu--item" style="padding: 15px 25px">
                    <router-link :to="menu.path">
                        <div class="item--title">
                            <q-icon :class="menu.icon" class="place" size="18px" />
                            <span>{{ menu.name }}</span>
                        </div>
                    </router-link>
                </div>
            </template>
        </template>
    </el-collapse>
    <!-- <el-row style="height: 100px"></el-row> -->
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { doGetUserMenu, doPostSetUserMenuConfig } from '@/apis/sign'
import { Menu, MenuType } from '@components/layout/layout'

const menuConfig = ref<Map<string, Set<string>>>(new Map())
const menus = ref([])
const route = useRoute()
const router = useRouter()

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false,
    },
})
const emit = defineEmits(['update:modelValue', 'updateConfig'])

watch(
    () => props.modelValue,
    (val) => {
        if (!val) {
            doPostSetUserMenuConfig(markRaw(menuConfig.value))
            document.removeEventListener('click', documentClick)
            return
        }
        document.addEventListener('click', documentClick)
    },
)
const documentClick = () => {
    emit('update:modelValue', false)
}

onMounted(() =>
    doGetUserMenu().then((res) => {
        if (res.code !== 200) {
            return
        }
        const data = res.data
        menus.value = data.menus
        menuConfig.value = data.menuConfig
        const paths = getAllPath(menus.value)
        if (!paths.includes(route.path) || route.path === '/') {
            router.push(paths[0])
        }
    }),
)

/**
 * 判断当前菜单/目录是否是激活状态
 * @param menu
 */
const isActive = (menu: Menu): boolean => {
    if (props.modelValue) {
        return false
    }
    if (menu.type === MenuType.MENU) {
        return menu.path === route.matched[2]?.path || menu.path === route.matched[1].path
    }
    return menu.children?.some(isActive) ?? false
}
/**
 * 获取菜单所有的访问路径
 * @param menuArray 菜单数组
 */
const getAllPath = (menuArray: Array<Menu> | undefined): string[] => {
    return (menuArray ?? []).flatMap((menu) => (menu.type === MenuType.MENU ? [menu.path] : getAllPath(menu.children)))
}

/**
 * 获取菜单路由路径
 * @param menu 当前菜单
 */
const routerPath = (menu: Menu) => {
    return props.modelValue ? '' : menu.path
}

/**
 * 路由点击事件 兼容 菜单配置模式
 * @param parentId 父级目录id
 * @param menu 当前菜单
 */
const routerClick = (toPopover: boolean, parentId: string, menu: Menu) => {
    if (!props.modelValue) {
        return
    }
    let subMenuIds = menuConfig.value.get(parentId)
    if (toPopover) {
        if (!subMenuIds) {
            subMenuIds = new Set()
            menuConfig.value.set(parentId, subMenuIds)
        }
        subMenuIds.add(menu.id)
        return
    }
    subMenuIds?.delete(menu.id)
}

/**
 * 弹出层子菜单
 * @param menu 父菜单
 */
const popoverMenus = (menu: Menu): Array<Menu> => {
    if (!menu || !menu.children) {
        return []
    }
    const subMenuIds = menuConfig.value.get(menu.id)
    if (!subMenuIds || subMenuIds.size === 0) {
        return []
    }
    return menu.children.filter((subMenu) => subMenuIds.has(subMenu.id))
}

/**
 * 列表子菜单
 * @param menu 父菜单
 */
const listMenus = (menu: Menu): Array<Menu> => {
    if (!menu || !menu.children) {
        return []
    }
    const subMenuIds = menuConfig.value.get(menu.id)
    if (!subMenuIds || subMenuIds.size === 0) {
        return menu.children
    }
    return menu.children.filter((subMenu) => !subMenuIds.has(subMenu.id))
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/layout/mmenu.scss';

.item--title {
    display: flex;
    justify-content: left;
}

// .active {
//     color: #1e83d3 !important;
// }

.black {
    color: #676767 !important;
}
.el-collapse {
    border: none;
}
:deep(.el-collapse-item__header) {
    border-bottom: none;
}
.item--title {
    .font-icon {
        width: 27px;
    }
}
</style>
