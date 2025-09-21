import { defineStore } from 'pinia'
import { ApiTagItem } from '@/views/baseVip/types'
export const useVipTagsStore = defineStore('vipTagsStore', {
    state() {
        return {
            tags: [] as ApiTagItem[],
        }
    },
    actions: {
        SET_TAGS(payload: ApiTagItem[]) {
            this.tags = payload
        },
    },
})
