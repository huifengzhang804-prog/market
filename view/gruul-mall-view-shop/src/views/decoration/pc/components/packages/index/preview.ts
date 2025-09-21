export default {
    loginLine: defineAsyncComponent(() => import('../login-line/preview.vue')),
    search: defineAsyncComponent(() => import('../search/preview.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/preview.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/preview.vue')),
    recommend: defineAsyncComponent(() => import('../recommend/preview.vue')),
    goods: defineAsyncComponent(() => import('../goods/preview.vue')),
    guarantee: defineAsyncComponent(() => import('../guarantee/preview.vue')),
    footerInfo: defineAsyncComponent(() => import('../footer-info/preview.vue')),
    copyright: defineAsyncComponent(() => import('../copyright/preview.vue')),
    shopSign: defineAsyncComponent(() => import('../shop-sign/preview.vue')),
}
