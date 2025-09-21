import { defineStore } from 'pinia'
import type { Menu } from '@/components/layout/layout'
import storage from '@/utils/Storage'

const storageExample = new storage()

export const useMenuList = defineStore('useMenuList', {
    state: () => ({
        isAdmin: storageExample.hasItem('isAdmin') ? storageExample.getItem('isAdmin') : true,
        menu: storageExample.getItem('menuList') || ([] as Menu[]),
    }),
    actions: {
        SET_ISADMIN(isAdmin: boolean) {
            this.isAdmin = isAdmin
            storageExample.setItem('isAdmin', isAdmin, 60 * 60 * 24 * 30)
        },
        SET_MENU(menu: Menu[]) {
            this.menu = menu
            storageExample.setItem('menuList', menu, 60 * 60 * 24 * 30)
        },
        REMOVE_MENU_DATA() {
            this.isAdmin = true
            this.menu = []
            storageExample.removeItem('isAdmin')
            storageExample.removeItem('menuList')
        },
    },
    getters: {
        getterMenu: (state) => state.menu,
        getterIsAdmin: (state) => state.isAdmin,
    },
})
