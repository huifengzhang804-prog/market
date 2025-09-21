import { get } from '../http'
export const doGetAppPlugin = () => {
  return get({ url: 'gruul-mall-carrier-pigeon/system/addon/addons' })
}
