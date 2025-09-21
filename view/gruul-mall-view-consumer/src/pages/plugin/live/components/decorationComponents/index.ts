/**
 * 微信直播间url
 * @param {string} wechatRoomId
 */
export const wxLiveRoomUrlFn = (wechatRoomId: string[]) => {
  let customParams = encodeURIComponent(JSON.stringify({ path: 'pages/index/index', pid: 1 }))
  let path = `plugin-private://${import.meta.env.VITE_WX_LIVE_ID}/pages/live-player-plugin?room_id=${[wechatRoomId]}&custom_params=${customParams}`
  return path
}
