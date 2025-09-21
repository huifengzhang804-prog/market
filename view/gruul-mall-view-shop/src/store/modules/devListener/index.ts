import { defineStore } from 'pinia'

export const useDevListenerStore = defineStore('useDevListenerStore', {
    state: () => {
        return {
            reloadState: false,
            reloadPath: '',
        }
    },
    actions: {
        SET_RELOAD_STATE(state: boolean) {
            this.reloadState = state
        },
        SET_RELOAD_PATH(path: string) {
            this.reloadPath = path
        },
    },
    getters: {
        getReloadState: (state) => state.reloadState,
        getReloadPath: (state) => state.reloadPath,
    },
})
