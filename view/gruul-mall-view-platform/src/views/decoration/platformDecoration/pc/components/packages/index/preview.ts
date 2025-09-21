export default {
    loginLine: defineAsyncComponent(() => import('../login-line/preview.vue')),
    search: defineAsyncComponent(() => import('../search/preview.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/preview.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/preview.vue')),
    seckill: defineAsyncComponent(() => import('../seckill/preview.vue')),
    recommend: defineAsyncComponent(() => import('../recommend/preview.vue')),
    goods: defineAsyncComponent(() => import('../goods/preview.vue')),
    shop: defineAsyncComponent(() => import('../shop/preview.vue')),
    guarantee: defineAsyncComponent(() => import('../guarantee/preview.vue')),
    footerInfo: defineAsyncComponent(() => import('../footer-info/preview.vue')),
    copyright: defineAsyncComponent(() => import('../copyright/preview.vue')),
}
