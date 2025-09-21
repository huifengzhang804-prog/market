/**
 * vuex localstorage 状态管理/状态缓存
 * @author 张治保
 */
class LocalStorage {
  private _store: Storage = window.localStorage || localStorage;
  constructor () {
    if (!this._store) {
      console.log('不支持localStorage');
      return
    }
  }

  /**
   * @function 设置值
   * @param {string} _k 必须参数，属性
   * @param {any} _v 非必须参数，属性值
   */
  set (_k:string, _v:any) {
    const store =  this._store;
    if (!store) return;
    let kType = LocalStorage.getType(_k);
    if (kType === 'string') {
      store.setItem(_k, LocalStorage.filterValue(_v))
    }
  }

  /**
   * @function 获取值
   * @param {string} _k 必须参数，属性
   */
  get (_k:string) {
    const store =  this._store;
    if (!store) return;
    let res;
    let kType = LocalStorage.getType(_k);
    if (kType === 'string') {
      res = store.getItem(_k)
    }
    return res
  }

  /**
   * @function 移除值
   * @param {string} _k 必须参数，属性
   */
  remove (_k:string) {
    if (!this._store) return;
    let kType = LocalStorage.getType(_k);
    if (kType === 'string') {
      this._store.removeItem(_k)
    }
  }

  /**
   * @function 移除所有
   */
  clear () {
    if (!this._store) return;
    this._store.clear()
  }

  /**
   * @function 判断类型
   * @param {any} para 必须参数，判断的值
   */
  static getType (para:any) {
    let type = typeof para;
    if (type === 'number' && isNaN(para)) return 'NaN';
    if (type !== 'object') return type;
    return Object.prototype.toString
      .call(para)
      .replace(/[\[\]]/g, '') // eslint-disable-line
      .split(' ')[1]
      .toLowerCase()
  }

  /**
   * @function 过滤值
   * @param {any} val 必须参数，过滤的值
   */
  static filterValue (val:any) {
    let vType = LocalStorage.getType(val);
    let nullVal = ['null', 'undefined', 'NaN'];
    let stringVal = ['boolen', 'number', 'string'];
    if (nullVal.indexOf(vType) >= 0) return '';
    if (stringVal.indexOf(vType) >= 0) return val;
    return JSON.stringify(val)
  }
}


export default new LocalStorage();
