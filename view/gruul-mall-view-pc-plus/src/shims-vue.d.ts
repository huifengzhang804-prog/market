/// <reference types="vite/client" />

declare module '*.vue' {
  import { defineComponent } from 'vue'
  const component: ReturnType<typeof defineComponent>
  export default component
}

declare namespace JSX {
  interface IntrinsicAttributes {
    class?: any
    style?: any
  }
}
/**
 * 环境变量名 代码提示作用
 */
interface ImportMetaEnv {
  VITE_BASE_MAIN_PATH: string
  VITE_BASE_IMAGE_URL: string
  VITE_BASE_URL: string
  VITE_LOCAL_STORAGE_KEY_PREFIX: string
  VITE_REQUEST_TIME_OUT: string
  VITE_GD_MAP_KEY: string
  VITE_RSA_PUBLIC_KEY: string
  VITE_NODE_ENV: string
  VITE_PLUGIN_URL: string
  VITE_GD_MAP_SAFECODE: string
  VITE_GD_WEB_MAP_KEY: string
}
interface ImportMeta {
  readonly env: ImportMetaEnv
}
declare module '*.mjs' {
  const mjs: any
  export default mjs
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
