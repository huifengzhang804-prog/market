export default {
    cubeBox: defineAsyncComponent(() => import('../cube-box/setting.vue')),
    goods: defineAsyncComponent(() => import('../goods/setting.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/setting.vue')),
    search: defineAsyncComponent(() => import('../search/setting.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/setting.vue')),
    navBar: defineAsyncComponent(() => import('../navBar/setting.vue')),
    classification: defineAsyncComponent(() => import('../classification/setting.vue')),
}
