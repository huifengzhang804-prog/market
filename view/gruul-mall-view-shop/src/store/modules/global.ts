import { defineStore } from 'pinia'

const useGlobalStore = defineStore('globalStore', {
    state: () => ({
        applicationKey: 0,
    }),
    actions: {
        SET_APPLICATION_KEY(newApplicationKey: number) {
            this.applicationKey = newApplicationKey
        },
    },
})

export default useGlobalStore
