<script setup lang="ts">
const props = defineProps({
  sWidth: {
    type: String,
    default: '400px',
  },
  sHeight: {
    type: String,
    default: '400px',
  },
  maskW: {
    type: String,
    default: '200px',
  },
  maskH: {
    type: String,
    default: '200px',
  },
  alt: {
    type: String,
    default: '',
  },
  bigW: {
    type: String,
    default: '800px',
  },
  bigH: {
    type: String,
    default: '800px',
  },
  src: {
    type: String,
    // default: 'https://t7.baidu.com/it/u=2963767354,870442698&fm=193&f=GIF',
    default: '',
  },
  isEmpty: {
    type: Boolean,
    default: true,
  },
})
</script>

<template>
  <div id="magnifyingGlass-main" v-magnifyingGlass class="main" :style="{ width: sWidth, height: sHeight }">
    <!-- 小图片区域 s -->
    <div id="magnifyingGlass-s-img" class="s-img" :style="{ width: sWidth, height: sHeight }">
      <img :src="src" :alt="alt" />
      <div id="magnifyingGlass-mask" class="mask" :style="{ width: maskW, height: maskH }" />
      <div v-if="isEmpty" class="err-mask">
        <q-icon name="icon-a-zuhe9781" size="120px" color="#fff" />
      </div>
    </div>
    <!-- 小图片区域 e -->
    <!-- 大图区域 s -->
    <div id="magnifyingGlass-p-img" class="p-img" :style="{ width: sWidth, height: sHeight, left: sWidth }">
      <div class="p-img__box">
        <img id="magnifyingGlass-pic-img" :src="src" :alt="alt" :style="{ width: bigW, height: bigH }" class="p-img__box--img" />
      </div>
    </div>
    <!-- 大图区域 e -->
  </div>
</template>

<style scoped lang="scss">
@include b(main) {
  position: relative;
  background: #fff;
}
@include b(s-img) {
  position: relative;
  & > img {
    width: 100%;
    height: 100%;
  }
}

@include b(err-mask) {
  position: absolute;
  inset: 0;
  background-color: #00000080;
  display: flex;
  align-items: center;
  justify-content: center;
}

@include b(mask) {
  position: absolute;
  left: 0;
  top: 0;
  background: #fff;
  opacity: 0.5;
  display: none;
}
@include b(p-img) {
  position: absolute;
  display: none;
  top: 0;
  overflow: hidden;
  z-index: 9999;
  margin-left: 10px;
  background: #000;
  @include e(box) {
    position: relative;
    height: 100%;
    width: 100%;
    @include m(img) {
      position: absolute;
      left: 0;
      top: 0;
    }
  }
}
</style>
