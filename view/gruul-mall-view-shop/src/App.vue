<template>
    <router-view v-if="webConfig" :key="globalStore.applicationKey" />
</template>

<script lang="ts" setup>
import { StompStarter } from '@/composables/stomp/StompStarter'
import useGlobalStore from './store/modules/global'
import Storage from '@/utils/Storage'
import { queryConfigByModule } from '@/apis'
import { configurePlatform } from '@/store/modules/configurePlatform'

const webConfig = ref(false)
const configure = configurePlatform()

const getConfigByModule = async () => {
    const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING')
    if (code === 200 && data) {
        configure.SET_OPEN_ADV(data)
        document.title = configure.getShopWebSitName
        document.getElementById('website_icon')?.setAttribute('href', configure.getWebSitIcon)
    }
    webConfig.value = true
}
getConfigByModule()

const storageLocal = () => new Storage()

onMounted(() => {
    StompStarter.start()
    if (!storageLocal().getItem('layout')) {
        storageLocal().setItem(`layout`, {
            layout: 'vertical',
            theme: 'light',
            darkMode: false,
            sidebarStatus: true,
            epThemeColor: '#555CFD',
            themeColor: 'light',
            overallStyle: 'system',
        })
    }
})
const globalStore = useGlobalStore()
</script>

<style lang="scss">
@import '@/assets/css/mixins/mixins.scss';
.main {
    height: 100%;
}
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: $rows-text-color;
    background-color: $rows-bg-color-grey;
}
</style>
