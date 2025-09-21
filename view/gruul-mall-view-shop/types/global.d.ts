/// <reference types="vite/client" />
/// <reference types="unplugin-vue-define-options/macros-global" />
/// <reference types="vitest/globals" />

export {}
declare global {
    interface ImportMetaEnv {
        VITE_BASE_MAIN_PATH: string
        VITE_BASE_IMAGE_URL: string
        VITE_BASE_URL: string
        VITE_LOCAL_STORAGE_KEY_PREFIX: string
        VITE_REQUEST_TIME_OUT: string
        VITE_STOMP_CONNECT_URI: string
        VITE_IS_SINGLE: string
        VITE_CLIENT_TYPE: string
        VITE_RSA_PUBLIC_KEY: string
        VITE_PLUGIN_URL?: string
        VITE_GD_MAP_KEY: string
        VITE_GD_MAP_SAFECODE: string
        VITE_OSS_URL: string
        VITE_GD_WEB_MAP_KEY: string
    }

    type FN = (...arg: any[]) => void
    type Fn = () => void
    type Long = number | string
    type Obj = Record<string, any>
    type RegionData = {
        label: string
        value: string
        children?: RegionData[]
    }
}
