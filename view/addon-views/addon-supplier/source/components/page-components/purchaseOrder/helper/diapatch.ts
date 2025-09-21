import { Ref } from 'vue'

/**
 * 展开快速搜索的监听
 * @param { boolean } isShowQuickSearch 是否展开快速搜索
 */
export const changeShowQuickSearch = (isShowQuickSearch: boolean, tableHeight: Ref<string>) => {
  if (isShowQuickSearch) {
    tableHeight.value = 'calc(100vh - 450px)'
  } else {
    tableHeight.value = 'calc(100vh - 350px)'
  }
}
