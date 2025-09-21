import { defineStore } from 'pinia'
export const useSearchBykeyword = defineStore('SearchBykeyword', {
  state() {
    return {
      keyword: '',
      selectValue: 'product',
      showSearchVal: false,
    }
  },
  actions: {
    SET_KEYWORD(payload: string, selectValue: 'product' | 'shop', showSearchVal: boolean) {
      // console.log('this', this)
      this.keyword = payload
      this.selectValue = selectValue
      this.showSearchVal = showSearchVal
    },
    DEL_KEYWORD() {
      this.keyword = ''
    },
  },
})
