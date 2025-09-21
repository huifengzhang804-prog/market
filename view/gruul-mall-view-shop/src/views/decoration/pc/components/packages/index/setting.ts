export default {
    loginLine: defineAsyncComponent(() => import('../login-line/setting.vue')),
    search: defineAsyncComponent(() => import('../search/setting.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/setting.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/setting.vue')),
    recommend: defineAsyncComponent(() => import('../recommend/setting.vue')),
    goods: defineAsyncComponent(() => import('../goods/setting.vue')),
    guarantee: defineAsyncComponent(() => import('../guarantee/setting.vue')),
    footerInfo: defineAsyncComponent(() => import('../footer-info/setting.vue')),
    copyright: defineAsyncComponent(() => import('../copyright/setting.vue')),
    shopSign: defineAsyncComponent(() => import('../shop-sign/setting.vue')),
}
