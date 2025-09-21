const cacheMap = new Map<string, { value: any; count: number }>()

/**
 * 尝试获取缓存的值，如果不存在则返回null 存在count+1 返回缓存的值
 * @param key {string} 缓存的key
 * @returns {any} 缓存的值
 */
const tryGet = (key: string) => {
  let current = cacheMap.get(key)
  if (!current) {
    current = { value: null, count: 0 }
    cacheMap.set(key, current)
  }
  current.count = current.count + 1
  return current.value
}
/**
 * 获取缓存的值 如果不存在则返回null 存在 返回缓存的值
 * @param key {string} 缓存的key
 * @returns {any} 缓存的值
 */
const get = (key: string) => {
  let current = cacheMap.get(key)
  return current && current.value
}

/**
 * 设置缓存的值 如果存在则返回缓存的值 不存在则返回设置的值
 * @param key {string} 缓存的key
 * @param value {any} 缓存的值
 * @returns {any} 缓存的值
 */
const set = (key: string, value: any) => {
  const currentValue = cacheMap.get(key)
  if (!currentValue) {
    return value
  }
  currentValue.value = value
  return value
}

/**
 * 移除缓存的值 如果存在则count-1 如果count<=0 则删除缓存
 * @param key {string} 缓存的key
 */
const remove = (key: string) => {
  return new Promise<boolean>((resolve) => {
    const currentValue = cacheMap.get(key)
    if (!currentValue) {
      return
    }
    currentValue.count--
    if (currentValue.count <= 0) {
      cacheMap.delete(key)
      resolve(false)
      return
    }
    resolve(true)
  })
}

const size = () => cacheMap.size

export default {
  tryGet,
  get,
  set,
  remove,
  size,
}
