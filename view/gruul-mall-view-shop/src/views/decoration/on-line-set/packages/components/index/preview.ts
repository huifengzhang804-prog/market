import { defineAsyncComponent } from 'vue'
export default {
    blankPaceholder: defineAsyncComponent(() => import('../blankHolder/preview.vue')),
    coupon: defineAsyncComponent(() => import('../coupon/preview.vue')),
    cubeBox: defineAsyncComponent(() => import('../cube-box/preview.vue')),
    goods: defineAsyncComponent(() => import('../goods/preview.vue')),
    navigation: defineAsyncComponent(() => import('../navigation/preview.vue')),
    resizeImage: defineAsyncComponent(() => import('@/views/decoration/on-line-set/packages/components/resize-image/preview.vue')),
    richText: defineAsyncComponent(() => import('../rich-text/preview.vue')),
    search: defineAsyncComponent(() => import('../search/preview.vue')),
    separator: defineAsyncComponent(() => import('../separator/preview.vue')),
    swiper: defineAsyncComponent(() => import('../swiper/preview.vue')),
    titleBar: defineAsyncComponent(() => import('../title-bar/preview.vue')),
    video: defineAsyncComponent(() => import('../video/preview.vue')),
    navBar: defineAsyncComponent(() => import('../navBar/preview.vue')),
    classification: defineAsyncComponent(() => import('../classification/preview.vue')),
    secKill: defineAsyncComponent(() => import('../sec-kill/preview.vue')),
}
