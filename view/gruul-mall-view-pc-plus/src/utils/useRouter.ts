// 创建一个路由跳转的组合式函数 useRouterNewWindow
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export function useRouterNewWindow() {
  const router = useRouter()
  // 存储已打开窗口的Map
  const windowMap = ref<Map<string, Window>>(new Map())

  /**
   * 在新窗口打开路由
   * @param path 路由路径
   * @param query 路由参数
   */
  const openNewWindow = (path: string, query: Record<string, any> = {}) => {
    // 生成唯一key (可以根据实际需求修改key的生成规则)
    const key = `${path}${JSON.stringify(query)}`

    // 检查窗口是否已经打开
    const existingWindow = windowMap.value.get(key)

    if (existingWindow && !existingWindow.closed) {
      // 如果窗口已打开且未关闭，则激活该窗口
      existingWindow.focus()
    } else {
      // 如果窗口未打开或已关闭，则打开新窗口
      const routeUrl = router.resolve({
        path,
        query,
      }).href

      const newWindow = window.open(routeUrl, '_blank')

      if (newWindow) {
        // 保存窗口引用
        windowMap.value.set(key, newWindow)

        // 监听窗口关闭事件
        newWindow.onbeforeunload = () => {
          windowMap.value.delete(key)
        }
      }
    }
  }

  return {
    openNewWindow,
  }
}
