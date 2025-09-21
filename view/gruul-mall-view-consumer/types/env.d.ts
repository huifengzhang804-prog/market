/**
 * imort.meta 类型声明
 */
interface ImportMeta {
  readonly env: ImportMetaEnv
}

/**
 * nvue 类型文件声明
 */
declare module '*.nvue' {
  import type { DefineComponent } from 'vue'
  const vueComponent: DefineComponent<{}, {}, any>
  export default vueComponent
}

declare module 'vue' {
  interface ComponentCustomProperties {
    $getCdnUrl: (url: string) => string
  }
}

export {}
