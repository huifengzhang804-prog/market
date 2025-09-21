import replaceManifestKey from './replaceManifestKey'
export default function modifyMapKey(environmentObj: ImportMetaEnv) {
  replaceManifestKey('h5.sdkConfigs.maps.amap.key', `"${environmentObj.VITE_GD_MAP_KEY}"`)
  replaceManifestKey('h5.sdkConfigs.maps.amap.securityJsCode', `"${environmentObj.VITE_GD_MAP_SAFECODE}"`)
  replaceManifestKey('app-plus.distribute.sdkConfigs.maps.amap.appkey_ios', `"${environmentObj.VITE_GD_IOS_MAP}"`)
  replaceManifestKey('app-plus.distribute.sdkConfigs.maps.amap.appkey_android', `"${environmentObj.VITE_GD_ANDROID_MAP}"`)
  replaceManifestKey('app-plus.distribute.sdkConfigs.geolocation.amap.appkey_ios', `"${environmentObj.VITE_GD_IOS_MAP}"`)
  replaceManifestKey('app-plus.distribute.sdkConfigs.geolocation.amap.appkey_android', `"${environmentObj.VITE_GD_ANDROID_MAP}"`)
}
