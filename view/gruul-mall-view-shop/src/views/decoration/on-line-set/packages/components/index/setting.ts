import { defineAsyncComponent } from 'vue'
export default {
    blankPaceholder: defineAsyncComponent(() => import('../blankHolder/setting.vue')),
    coupon: defineAsyncComponent(() => import('../coupon/setting.vue')),
    cubeBox: defineAsyncComponent(() => import('../cube-box/setting.vue')),
    goods: defineAsyncComponent(() => import('../goods/setting.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/setting.vue')),
    resizeImage: defineAsyncComponent(() => import('../resize-image/setting.vue')),
    richText: defineAsyncComponent(() => import('../rich-text/setting.vue')),
    search: defineAsyncComponent(() => import('../search/setting.vue')),
    separator: defineAsyncComponent(() => import('../separator/setting.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/setting.vue')),
    titleBar: defineAsyncComponent(() => import('../title-bar/setting.vue')),
    video: defineAsyncComponent(() => import('../video/setting.vue')),
    navBar: defineAsyncComponent(() => import('../navBar/setting.vue')),
    classification: defineAsyncComponent(() => import('../classification/setting.vue')),
    secKill: defineAsyncComponent(() => import('../sec-kill/setting.vue')),
}
