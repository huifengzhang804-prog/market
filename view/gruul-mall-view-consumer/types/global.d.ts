import type { Decimal } from 'decimal.js-light'
import type { Long as _GlobalLong } from '@/constant/global'

export {}
declare global {
  interface DecimalType extends Decimal {}
  interface ImportMetaEnv {
    VITE_APP_VERSION: string
    VITE_REQUEST_VERSION: string
    VITE_WX_APPID: string
    VITE_WX_LIVE_VERSION: string
    VITE_BASE_URL: string
    VITE_STOMP_CONNECT_URI: string
    VITE_LOCAL_STORAGE_KEY_PREFIX: string
    VITE_UPLOAD_URL: string
    VITE_GD_MAP_KEY: string
    VITE_GD_MAP_SAFECODE: string
    VITE_GD_ANDROID_MAP: string
    VITE_GD_IOS_MAP: string
    VITE_WX_LIVE_ID: string
    VITE_APP_BUILDE_ID: string
    VITE_CDN_URL: string
    VITE_GD_WEB_MAP_KEY: string
    VITE_RSA_PUBLIC_KEY: string
  }
  type Long = _GlobalLong
  type Obj = Record<string, any>
  // const useConvert: typeof _GlobalUseConvert
  // const useMember: typeof _GlobalUseMember
  // const usePriceRange: typeof _GlobalUsePriceRange
  // const useSafeHeight: typeof _GlobalUseSafeHeight
}
