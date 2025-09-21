/**
 * 播放声音
 * @param url 可以使用相对路径或全路径
 * 相对路径 相对于当前目录 ./uils目录
 */
export const playSound = async () => {
  let music = null
  // 创建播放器对象
  music = uni.createInnerAudioContext()
  music.src = `${import.meta.env.VITE_CDN_URL}/media/new-message.mp3`
  try {
    music.play()
  } catch (ex) {
    return
  }
  music.onEnded(() => {
    music = null
  })
}
