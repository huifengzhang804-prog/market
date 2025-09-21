import { reactive, toRefs } from 'vue'

export const navDate = reactive({
  nameS: [
    { name: '商品', id: '#swiper' },
    { name: '评价', id: '#eval' },
    { name: '推荐', id: '#see' },
    { name: '详情', id: '#detail' },
  ],
  heightS: [] as number[],
  navH: 0,
  styleOpacity: 0,
  trigger: 0,
})
const { heightS, navH } = toRefs(navDate)
export function getSystemInfo() {
  // 状态栏高度
  uni.getSystemInfo({
    success: ({ statusBarHeight }) => {
      navH.value = statusBarHeight || 0
    },
    fail: () => {},
  })
}

export function selectorQuery(key: string) {
  let params: any
  const query = uni.createSelectorQuery()
  query
    .selectAll(key)
    .boundingClientRect((data) => {
      params = data
    })
    .exec()
  return params
}
export const reset = () => {
  navDate.heightS = []
  navDate.navH = 0
  navDate.styleOpacity = 0
  navDate.trigger = 0
}
