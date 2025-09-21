import { computed, ref } from 'vue'
const goodSkuFatherPadding = uni.upx2px(40)
const carItemPadding = uni.upx2px(30)
const windowWidth = ref(uni.upx2px(750))
const swipeActionWidth = computed(() => windowWidth.value - goodSkuFatherPadding - carItemPadding)
// 将rpx单位值转换成px
export { swipeActionWidth }
