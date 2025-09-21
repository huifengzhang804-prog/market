// import * as dotenv from 'dotenv'
import replaceManifestKey from './replaceManifestKey'

export default function modifyAppId(environmentObj: ImportMetaEnv) {
  replaceManifestKey('mp-weixin.appid', `"${environmentObj.VITE_WX_APPID}"`)
  replaceManifestKey('appid', `"${environmentObj.VITE_APP_BUILDE_ID}"`)
  replaceManifestKey('app-plus.distribute.android.packagename', `"${environmentObj.VITE_APP_ANDROID_PACKAGE_NAME}"`)
}
