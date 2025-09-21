<template>
  <div v-if="isGetRenovation" id="toTop" :key="appKey" class="text-center bg-light-4 e-ff" style="position: relative">
    <Top v-if="route.path !== '/merchantEnter/merchantEnter'" />
    <router-view />
    <bottom v-if="route.path !== '/merchantEnter/merchantEnter' && route.path !== '/personalcenter/set/customerservice'" />
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { doGetOpeningUp, queryConfigByModule } from './apis/platform'
import { StompStarter } from '@/composables/stomp/StompStarter'

/**
 * 装修数据获取完毕再加载页面
 */
const isGetRenovation = ref(false)
const doGetPropertiesList = async () => {
  const { code, data, msg } = await doGetOpeningUp()
  if (code !== 200) return ElMessage.error(msg || '获取装修数据失败,请刷新重试')
  else if (!data) return
  usePropertiesListStore().SET_PROPERTIESLIST(JSON.parse(data?.properties))
  isGetRenovation.value = true
}
doGetPropertiesList()

const getConfigByModule = async () => {
  const { code, data } = await queryConfigByModule('OTHERS_SETTING, PUBLIC_SETTING')
  if (code === 200 && data) {
    document.title = data.PC_MALL_NAME // 设置为你想要显示的标题文本
    document.getElementById('website_icon')?.setAttribute('href', data.WEB_SIT_ICON)
  }
}
getConfigByModule()

const $userStore = useUserStore()
const appKey = ref(0)
watch(
  () => $userStore.getterToken,
  () => {
    appKey.value = Date.now()
  },
)
const route = useRoute()
onMounted(() => {
  StompStarter.start()
  document.body.style.setProperty('--el-color-primary', '#f54319')
  document.body.style.setProperty('--el-color-primary-light-3', '#f54319')
  document.body.style.setProperty('--el-color-primary-light-5', '#f54319')
  document.body.style.setProperty('--el-color-primary-dark-2', '#f54319')
})
</script>

<style lang="scss">
@import '@/assets/css/mixins/mixins.scss';
@import '@/assets/css/element-amend.css';
#app {
  overflow-x: hidden;
  width: 100%;
}
html {
  overflow-y: scroll;
}
</style>
