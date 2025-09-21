import { useAppStore } from '@/store/modules/app'
import { doGetAppPlugin } from '@/apis/sys'
export async function getPlugin() {
  const { data } = await doGetAppPlugin()
  useAppStore().SET_PLUGIN_LIST(data)
}
