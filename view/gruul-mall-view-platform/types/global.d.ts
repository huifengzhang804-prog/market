/// <reference types="unplugin-vue-define-options/macros-global" />
/// <reference types="vitest/globals" />
/// <reference types="vite/client" />

export {}
declare global {
    interface ImportMetaEnv {
        VITE_BASE_MAIN_PATH: string
        VITE_BASE_IMAGE_URL: string
        VITE_BASE_URL: string
        VITE_LOCAL_STORAGE_KEY_PREFIX: string
        VITE_REQUEST_TIME_OUT: string
        VITE_GD_MAP_KEY: string
        VITE_GD_MAP_SAFECODE: string
        VITE_RSA_PUBLIC_KEY: string
        VITE_PLUGIN_URL?: string
    }

    type FN = (...arg: any[]) => void
    type Fn = () => void
    //数字类型（超过js数字安全范围是 string ，否则是 number）
    type Long = number | string
    //可空类型
    type Nullable<T> = null | T | undefined
    // any对象
    type Obj = Record<string, any>
    //分页查询条件
    interface PageParam {
        //页码
        current: number
        //每页数据量
        size: number
    }

    //分页查询结果
    interface Pagination<T> {
        //总页数
        pages: number
        //页码
        current: number
        //每页数据量
        size: number
        //总数据量
        total: number
        //当前页的数据
        records: T[]
    }

    //范围 如 日期范围, 时间范围，数字范围等等
    //eg 日期范围 从2024-07-05 到 2024-07-30
    //eg 数字范围 从 1 到 9
    interface IRange<T> {
        //开始
        start: T
        //结束
        end: T
    }
}
