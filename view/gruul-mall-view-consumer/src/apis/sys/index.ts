import api from '@/libs/request'
export const doGetAppPlugin = () => {
  return api.get('gruul-mall-carrier-pigeon/system/addon/addons')
}
