// @ts-ignore
// #ifdef APP-PLUS
import TIM from 'tim-wx-sdk'
// #endif
import TIMUploadPlugin from 'tim-upload-plugin'
// @ts-ignore
import TIMProfanityFilterPlugin from 'tim-profanity-filter-plugin'
// @ts-ignore
// eslint-disable-next-line no-undef
const userID = getApp().globalData.userInfo.userId
// @ts-ignore
const SDKAppID = Number(import.meta.env.VITE_IM_SDK_APP_ID)
let options = {
  userID,
  SDKAppID, // 接入时需要将0替换为您的即时通信 IM 应用的 SDKAppID
  // @ts-ignore
  secretKey: import.meta.env.VITE_IM_SECERT_KEY, // 接入时需要将0替换为您的即时通信 IM 应用的 SDKAppID
}
// 创建 SDK 实例，`TIM.create()`方法对于同一个 `SDKAppID` 只会返回同一份实例
let tim = TIM.create({ SDKAppID }) // SDK 实例通常用 tim 表示

// // 注册腾讯云即时通信 IM 上传插件
tim.registerPlugin({ 'tim-upload-plugin': TIMUploadPlugin })

// // 注册腾讯云即时通信 IM 本地审核插件
tim.registerPlugin({ 'tim-profanity-filter-plugin': TIMProfanityFilterPlugin })
export { tim, TIM, options }
