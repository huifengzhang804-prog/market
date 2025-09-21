<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import MenuDialog from './menuDialog.vue'
import uuid from '@/utils/uuid'
import type { PropType } from 'vue'
import type { UserCenterMenuItem } from '../user-center'

const $props = defineProps({
    menuList: {
        type: Array as PropType<UserCenterMenuItem[]>,
        default() {
            return []
        },
    },
    menuStyle: {
        type: Number,
        default: 1,
    },
    splitFlag: {
        type: Boolean,
        defalut: true,
    },
})
const $emit = defineEmits(['update:menuList'])
const menu = useVModel($props, 'menuList', $emit)
// 1修改菜单 2新增菜单
const optionType = ref(1)
const menuProp: UserCenterMenuItem = {
    id: null,
    allowUse: 1,
    defaultIcon: '',
    showMenu: false,
    linkSelectItem: {
        id: '',
        name: '',
        url: '',
        type: 0,
    },
    menuIconUrl: '',
    menuName: '',
    sortIndex: 0,
}
// 菜单设置弹窗
const menuVisible = ref(false)
// 展开式菜单分隔栏
const dragItemVisible = ref(false)

/**
 * 新增或编辑菜单
 * @param {UserCenterMenuItem} item
 * @param {number} type
 */
const menuClickHandle = (item: UserCenterMenuItem, type: number) => {
    Object.assign(menuProp, item)
    optionType.value = type
    menuVisible.value = true
}
/**
 * 删除菜单
 */
const expandMenuDeleteHandle = (index: number) => {
    menu.value.splice(index, 1)
}

/**
 * 展开式菜单分隔栏现隐
 */
const createDragItem = (visible: boolean) => {
    addMenu({
        showMenu: true,
        linkSelectItem: {
            id: '',
            name: '',
            url: '',
            type: 0,
        },
        menuIconUrl: '',
        menuName: '',
    } as UserCenterMenuItem)
}
/**
 * 前端更新菜单数据值
 * @param 1.修改菜单
 * @param 2.新增菜单
 */
const updateMenuHandle = (menuItem: UserCenterMenuItem) => {
    if (optionType.value === 1) {
        updateMenu(menuItem)
    } else {
        addMenu(menuItem)
    }
}
/**
 * 更新菜单信息
 */
function updateMenu(menuItem: UserCenterMenuItem) {
    const len = menu.value.filter((item) => item.id === menuItem.id).length
    const index = menu.value.findIndex((item) => item.id === menuItem.id)
    menu.value[index].menuName = menuItem.menuName
    menu.value[index].menuIconUrl = menuItem.menuIconUrl
    menu.value[index].linkSelectItem = menuItem.linkSelectItem
}
/**
 * 新增
 */
function addMenu(menuItem: UserCenterMenuItem) {
    const menuItems = {
        id: uuid(8),
        menuName: menuItem.menuName,
        menuIconUrl: menuItem.menuIconUrl,
        defaultIcon: '',
        sortIndex: menu.value.length,
        showMenu: true,
        allowUse: 1,
        linkSelectItem: menuItem.linkSelectItem,
    }
    menu.value.push(menuItems)
}
</script>

<template>
    <div class="drag--menu">
        <div class="draggable__menu">
            <VueDraggableNext v-model="menu">
                <div v-for="(item, index) in menu" :key="item.menuName">
                    <div v-if="item.menuName" :id="String(item.id)" class="menu__item error-menuItem">
                        <el-checkbox v-model="item.showMenu">
                            {{ item.menuName }}
                        </el-checkbox>
                        <span>
                            <el-icon class="menu__item--icon" @click="menuClickHandle(item, 1)"><i-ep-edit /></el-icon>
                            <el-icon class="el-icon-error" @click="expandMenuDeleteHandle(index)"><i-ep-circleCloseFilled /></el-icon>
                        </span>
                    </div>
                    <div v-if="!item.menuName && $props.splitFlag" class="drag--menuList drag--shadow__item error-menuItem">
                        <div class="drag--menuitem drag--shadow"></div>
                        <el-icon class="el-icon-error" @click="expandMenuDeleteHandle(index)"><i-ep-circleCloseFilled /></el-icon>
                    </div>
                </div>
            </VueDraggableNext>
        </div>
        <div class="footer">
            <div class="footer__button">
                <el-button type="primary" style="color: #fff" plain @click="menuClickHandle(menuProp, 2)">添加菜单 </el-button>
                <el-button v-if="$props.splitFlag" type="primary" style="color: #fff" plain @click="createDragItem(true)">添加分隔栏 </el-button>
            </div>
        </div>
        <menu-dialog v-model:menu-visible="menuVisible" :menu-prop="menuProp" :type="optionType" @update-menu="updateMenuHandle" />
    </div>
</template>

<style lang="scss" scoped>
@include b(menu) {
    @include e(item) {
        position: relative;

        @include flex(space-between);
        height: 48px;
        padding: 0 10px;
        margin: 8px 0;
        background-color: #ffffff;

        @include m(icon) {
            margin-right: 0;
            color: #9797a1;
            font-size: 16px;
        }
    }
}

@include b(footer) {
    padding: 10px 0 15px 0;
    background-color: #ffffff;

    @include e(button) {
        padding: 10px 0 0 10px;
    }
}

.drag--menuList {
    background-color: #ffffff;
    padding: 10px;
}

.draggable__menu {
    background-color: #f1f2f6;
    padding: 2px 10px;

    .drag--menuitem {
        position: relative;
        height: 15px;
        width: 100%;
        background-color: #e9e9e9;
    }
}
.draggable__menu--fold {
    background-color: #f1f2f6;
    padding: 8px 10px;

    .drag--menuitem {
        position: relative;
        height: 15px;
        width: 100%;
        background-color: #e9e9e9;
    }
}

.drag--shadow__item {
    @include flex(space-between);
    height: 48px;
    padding: 0 15px;
    margin: 8px 0;
    background-color: #ffffff;
}

.drag--shadow {
    position: relative;
    height: 15px;
    width: 100%;
    background-color: #e9e9e9;
}

.drag--shadow:hover {
    box-shadow: #9797a1 0 0 5px;
}

// hover显示隐藏
.error-menuItem {
    position: relative;
    font-size: 16px;
    z-index: 2;

    .el-icon-error {
        opacity: 0;
        color: #9797a1;
        position: absolute;
        right: -6px;
        top: -6px;
        z-index: 9999;
    }
}

.error-menuItem:hover {
    position: relative;
    z-index: 2;
    cursor: pointer;

    .el-icon-error {
        opacity: 1;
    }
}
</style>
