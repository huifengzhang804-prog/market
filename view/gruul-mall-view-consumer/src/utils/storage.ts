import DateUtil from './date'

const storageKey = import.meta.env.VITE_LOCAL_STORAGE_KEY_PREFIX

class Storage {
  /**
   * 设置缓存
   * @param key 键名
   * @param data 数据
   * @param expired 持续时间（可选）
   */
  set(key: string, data: any, expired = 1) {
    // import.meta.env.VITE_BASE_URL
    uni.setStorageSync(`${storageKey}_${key}`, JSON.stringify({ data, time: +new Date(), expired: expired * 60 * 60 * 24 * 1000 }))
  }

  /**
   * 获取缓存
   * @param key 键名
   */
  get(key: string): any {
    try {
      const { data, time = 0, expired = 0 } = JSON.parse(uni.getStorageSync(`${storageKey}_${key}`))

      if (!expired) return null

      if (time + expired > +new Date()) return data

      this.remove(key)
      return null
    } catch {
      return null
    }
  }

  /**
   * 清除缓存
   * @param key 键名
   */
  remove(key: string) {
    return uni.removeStorageSync(`${storageKey}_${key}`)
  }

  /**
   * 清除当前项目缓存(不影响其他端的缓存)
   */
  removeThis() {
    const res = uni.getStorageInfoSync()
    res.keys.forEach((key) => {
      if (key.startsWith(storageKey)) {
        uni.removeStorageSync(key)
      }
    })
  }
}

export default new Storage()
