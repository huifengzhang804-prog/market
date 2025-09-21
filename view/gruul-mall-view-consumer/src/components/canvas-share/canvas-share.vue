<script setup lang="ts">
import { ref, type PropType, nextTick } from 'vue'
// #ifdef H5
import PosterCanvasH5 from './components/canvas-h5.vue'
// #endif
// #ifndef H5
import PosterCanvasOther from './components/canvas-other.vue'
// #endif
import type { ShareData } from './components/useGetQrcode'

const posterRef = ref<InstanceType<typeof PosterCanvasH5 | typeof PosterCanvasOther> | null>(null)

const $props = defineProps({
  sharePopUp: {
    type: Boolean,
    default: false,
  },
  shareData: {
    type: Object as PropType<ShareData>,
    default: () => ({}),
  },
})
const $emit = defineEmits(['canvas-close'])
const { divTenThousand } = useConvert()

const openShare = async () => {
  if (posterRef.value) {
    nextTick(() => {
      posterRef.value?.showCanvas($props.shareData.image, divTenThousand($props.shareData.salePrice).toString())
    })
  }
}
const handleCanvasClose = () => {
  $emit('canvas-close')
}
defineExpose({ openShare })
</script>

<template>
  <!-- #ifndef H5-->
  <PosterCanvasOther
    v-show="sharePopUp && shareData.goodId"
    ref="posterRef"
    :current-instance="posterRef"
    :shop-id="shareData.shopId"
    :good-id="shareData.goodId"
    :title="shareData.productName"
    @close="handleCanvasClose"
  />
  <!-- #endif -->
  <!-- #ifdef H5 -->
  <PosterCanvasH5
    v-if="shareData.goodId"
    ref="posterRef"
    :shop-id="shareData.shopId"
    :good-id="shareData.goodId"
    :title="shareData.productName"
    @colse="handleCanvasClose"
  />
  <!-- #endif -->
</template>

<style scoped lang="scss"></style>
