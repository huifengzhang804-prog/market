import storage from '@/utils/storage'
/**
 * @param {string} selector 匹配选择器 节点
 * @param {function} fn callback
 * @param {config} cache 默认走缓存
 */
export function useSelectorQuery(selector: string, fn: (say: any) => any, config = { cache: true }) {
  const info = uni.getSystemInfoSync()
  if (config.cache) {
    let paramse = storage.get(`${info.deviceId}${selector}`)
    if (paramse) return fn(paramse)
    return
  }
  const time = setTimeout(() => {
    uni
      .createSelectorQuery()
      .select(selector)
      .boundingClientRect((res) => {
        storage.set(`${info.deviceId}${selector}`, res)
        fn(res)
        clearTimeout(time)
      })
      .exec()
  }, 0)
}
