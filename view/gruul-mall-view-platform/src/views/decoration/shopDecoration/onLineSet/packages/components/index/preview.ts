export default {
    cubeBox: defineAsyncComponent(() => import('../cube-box/preview.vue')),
    goods: defineAsyncComponent(() => import('../goods/preview.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/preview.vue')),
    search: defineAsyncComponent(() => import('../search/preview.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/preview.vue')),
    navBar: defineAsyncComponent(() => import('../navBar/preview.vue')),
    classification: defineAsyncComponent(() => import('../classification/preview.vue')),
}
