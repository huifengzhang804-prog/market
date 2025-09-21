// FIXME 缓存添加 返回类型为reactive
import { ref, onMounted } from 'vue'
let node = ref()
export function useSelector(selector: string, isCach = false) {
  onMounted(async () => {
    const res = await getNode(selector)
    if (res) {
      node.value = res
    }
  })
  return node
}

function getNode(selector: string) {
  return new Promise((resolve, reject) => {
    try {
      uni
        .createSelectorQuery()
        .select(selector)
        .boundingClientRect((res) => {
          resolve(res)
        })
        .exec()
    } catch (error) {
      reject(error)
    }
  })
}
