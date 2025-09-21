import { getUserAvatar, getUserNickname } from '@/libs/userHelper'

export default {
    install(Vue: import('vue').App<any>) {
        Vue.provide('global', {
            getUserAvatar,
            getUserNickname,
        })
    },
}
