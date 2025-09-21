<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import BlankHolder from '@decoration/components/blank-holder/blank-holder.vue'
import CubeBox from '@decoration/components/cube-box/cube-box.vue'
import Good from '@decoration/components/good/good.vue'
import ResizeImage from '@decoration/components/resize-image/resize-image.vue'
import RichTextArea from '@decoration/components/rich-text/rich-text.vue'
import Search from '@decoration/components/search/search.vue'
import Separator from '@decoration/components/separator/separator.vue'
import ShopNav from '@decoration/components/shop-nav/shop-nav.vue'
import cusSwiper from '@decoration/components/swiper/swiper.vue'
import TitleBar from '@decoration/components/title-bar/title-bar.vue'
import cusVideo from '@decoration/components/cus-video/cus-video.vue'
// 插件
import SecKill from '@plugin/secKill/components/decorationComponents/sec-kill.vue'
import { useStatusBar } from '@/hooks'
import { doGetPlatformPagesDetail } from '@/apis/decoration/platform/index'
import Auth from '@/components/auth/auth.vue'
import NoDecoration from '@/components/no-decoration/index.vue'

interface ProperType {
  formData: any
  icon: string
  id: string
  label: string
  value: string
}

const sysHeight = uni.getSystemInfoSync().screenHeight
const statusBarHeight = useStatusBar()
const scrollViewHeight = computed(() => {
  return sysHeight - statusBarHeight.value
})
const decorateList = ref<ProperType>()
const navTitle = ref('')
onLoad((params: any) => {
  uni.$emit('updateTitle')
  if (params.id) {
    initDecorationFromCus(params.id)
    navTitle.value = params.pageName
  }
})

/**
 * 获取商家自定页面
 */
/**
 * 自定义页面获取装修数据
 */
async function initDecorationFromCus(pageId: string) {
  const { code, data } = await doGetPlatformPagesDetail(pageId)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取装修失败',
    })
    return
  }
  decorateList.value = JSON.parse(data.properties)
  navTitle.value = data.name
}

const scrolltolower = () => {
  // 发布事件
  uni.$emit('scrolltolower')
}
</script>

<template>
  <!-- #ifndef MP-WEIXIN -->
  <q-nav bg-color="#fff" :title="navTitle" />
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <q-nav height="0" bg-color="#fff" :title="navTitle" />
  <!-- #endif -->
  <scroll-view v-if="decorateList" scroll-y class="scroll" :style="{ height: `${scrollViewHeight}px` }" @scrolltolower="scrolltolower">
    <view v-for="(item, index) in decorateList" :key="index">
      <blank-holder v-if="item.value === 'blankPaceholder'" :decoration-properties="item.formData" />
      <cube-box v-if="item.value === 'cubeBox'" :decoration-properties="item.formData" />
      <good v-if="item.value === 'goods'" :decoration-properties="item.formData" />
      <resize-image v-if="item.value === 'resizeImage'" :decoration-properties="item.formData" />
      <rich-text-area v-if="item.value === 'richText'" :decoration-properties="item.formData" />
      <search v-if="item.value === 'search'" :decoration-properties="item.formData" />
      <separator v-if="item.value === 'separator'" :decoration-properties="item.formData" />
      <shop-nav v-if="item.value === 'navigation'" :decoration-properties="item.formData" />
      <cus-swiper v-if="item.value === 'swiper'" :decoration-properties="item.formData" />
      <title-bar v-if="item.value === 'titleBar'" :decoration-properties="item.formData" />
      <cus-video v-if="item.value === 'video'" :decoration-properties="item.formData" />
      <sec-kill v-if="item.value === 'secKill'" :decoration-properties="item.formData" />
    </view>
  </scroll-view>
  <NoDecoration v-else />
  <!-- 授权弹窗 -->
  <Auth />
</template>

<style lang="scss" scoped></style>
