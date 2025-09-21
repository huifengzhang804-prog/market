import { defineStore } from 'pinia'
import { doGetShopCarList } from '@/apis/shopCar'
import { ElMessage } from 'element-plus'
import type { GoodListType } from '@/views/shoppingcart/types'
import Storage from '@/libs/storage'

const storage = new Storage()
export const useCardInfo = defineStore('CardInfo', {
  state() {
    return {
      validList: storage.getItem('validList') || [],
      inValidList: storage.getItem('inValidList') || [],
    } as { validList: GoodListType[]; inValidList: GoodListType[] }
  },
  actions: {
    SET_CARD(validList: GoodListType[], inValidList: GoodListType[]) {
      this.validList = validList
      this.inValidList = inValidList
      storage.setItem('validList', validList)
      storage.setItem('inValidList', inValidList)
    },
    DEL_CARD() {
      // 用户退出登录记得清除缓存
      storage.removeItem('validList')
      storage.removeItem('inValidList')
      this.validList = []
      this.inValidList = []
    },
    async INIT_CARD() {
      // 初始化购物车
      const { code, data, success, msg } = await doGetShopCarList()
      if (code === 200 && success) {
        this.SET_CARD(injectionOfTag(data.valid), injectionOfTag(data.invalid))
      } else {
        ElMessage.error(msg)
      }
    },
  },
  getters: {
    count: (state: { validList: GoodListType[] }) => {
      if (state.validList.length) {
        return state.validList.flatMap((item) => item.products).length
      } else {
        return 0
      }
    },
  },
})
function injectionOfTag(data: GoodListType[]) {
  return data.map((item) => {
    item.isAllChecked = false
    if (item.products && item.products.length > 0) {
      item.products = item.products.map((ite) => {
        ite.isChecked = false
        ite.isCountNumberComponentShow = true
        return ite
      })
    }
    return item
  })
}
