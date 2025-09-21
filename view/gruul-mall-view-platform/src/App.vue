<template>
    <router-view v-if="webConfig" />
</template>

<script setup lang="ts">
import { StompStarter } from '@/libs/stomp/StompStarter'
import Storage from '@/utils/Storage'
import { queryConfigByModule } from '@/apis/set/websiteSettings'
import { configurePlatform } from '@/store/modules/configurePlatform'

const webConfig = ref(false)
const configure = configurePlatform()

const getConfigByModule = async () => {
    const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING')
    if (code === 200 && data) {
        configure.SET_OPEN_ADV(data)
        document.title = configure.getPlatformWebSitName
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
    background-color: $bg-grey;
}
</style>
