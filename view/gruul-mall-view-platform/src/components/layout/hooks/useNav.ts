import { useRouter } from 'vue-router'
import { useFullscreen } from '@vueuse/core'
import { useAppStoreHook } from '@/store/modules/setting'
import Storage from '@/utils/Storage'
import { getConfig } from '@/libs/config'

const storageLocal = () => new Storage()

export function useNav() {
    const pureApp = useAppStoreHook()
    const routers = useRouter().options.routes
    const { isFullscreen, toggle } = useFullscreen()
    /** 平台`layout`中所有`el-tooltip`的`effect`配置，默认`light` */
    const tooltipEffect = getConfig().TooltipEffect

    const isCollapse = computed(() => {
        return !pureApp.getSidebarStatus
    })

    const layout = computed(() => {
        return storageLocal().getItem(`layout`)
    })

    function toggleSideBar() {
        pureApp.toggleSideBar()
    }

    const device = computed(() => {
        return pureApp.getDevice
    })

    return {
        device,
        layout,
        routers,
        isFullscreen,
        toggle,
        toggleSideBar,
        isCollapse,
        pureApp,
        tooltipEffect,
    }
}
