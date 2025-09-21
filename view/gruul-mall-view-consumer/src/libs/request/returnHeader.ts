import { useUserStore } from '@/store/modules/user'
export function getheader() {
  const header = {
    'Device-Id': uni.getSystemInfoSync().deviceId,
    'Client-Type': 'CONSUMER',
    'Shop-Id': 0,
    // WECHAT_MINI_APP
    // #ifdef H5
    Platform: 'H5',
    // #endif
    // #ifdef MP-WEIXIN
    //@ts-ignore
    Platform: 'WECHAT_MINI_APP',
    // #endif
    // #ifdef APP-PLUS
    //@ts-ignore
    Platform: 'ANDROID',
    // #endif
    Authorization: useUserStore().getterToken,
  }
  return header
}
