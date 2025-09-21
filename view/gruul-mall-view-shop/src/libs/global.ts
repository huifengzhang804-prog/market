import { getUserAvatar, getUserNickname } from '@/libs/userHelper'

export type GlobalProperties = {
    getUserAvatar: (avatar: string) => string
    getUserNickname: (userId: string, nickname: string) => string
}

export default {
    install(Vue: import('vue').App<any>) {
        Vue.provide<GlobalProperties, 'global'>('global', {
            getUserAvatar,
            getUserNickname,
        })
    },
}
