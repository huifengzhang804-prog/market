class Storage {
  constructor(name = import.meta.env.VITE_LOCAL_STORAGE_KEY_PREFIX) {
    this.name = name
    this.init()
  }
  private name: string
  private init() {
    Object.keys(localStorage).forEach((item) => {
      if (item.startsWith(this.name)) {
        this.hasItem(item.replace(this.name, ''))
      }
    })
  }
  public setItem(key: string, value: any, time?: number) {
    if (value === undefined) value = null
    const _key = this.name + key
    const _value = { value, time }
    if (time) {
      _value.time = time * 1000 + new Date().getTime()
    }
    localStorage.setItem(_key, JSON.stringify(_value))
  }
  public getItem(key: string) {
    if (this.hasItem(key)) {
      return JSON.parse(localStorage.getItem(this.name + key)!).value
    }
    return null
  }
  public removeItem(key: string) {
    localStorage.removeItem(this.name + key)
  }

  /**
   * 清除当前项目缓存(不影响其他端的缓存)
   */
  public clear() {
    Object.keys(localStorage).forEach((item) => {
      if (item.startsWith(this.name)) {
        localStorage.removeItem(item)
      }
    })
  }
  public getLength() {
    return Object.keys(localStorage).length
  }
  public hasItem(key: string) {
    const _key = this.name + key
    if (localStorage.getItem(_key)) {
      // const _time = JSON.parse(localStorage.getItem(_key)!).time
      // if (new Date().getTime() > _time) {
      //   localStorage.removeItem(_key)
      //   return false
      // }
      return true
    } else {
      return false
    }
  }
}
export default Storage
