import type { AppPluginName } from '@/apis/sys/model'
import type { RoleMenuRoleJointType } from '@/apis/consumer/footprint/model'

export interface StateType {
  registerPlugin: AppPluginName[]
  roleMenus: RoleMenuRoleJointType[]
}
const defaultState: StateType = {
  registerPlugin: [],
  roleMenus: [],
}
export default defaultState
