import { tim, TIM, options } from '../hooks'
import { doGetLiveRoomUserSig } from '../../../../apis/CreateLive'
// 登录
const doLoginIM = async ({ success = () => {}, error = () => {} }) => {
  try {
    const { code, data: userSig, msg } = await doGetLiveRoomUserSig(options.userID)
    if (code !== 200) {
      uni.showToast({ title: `${msg || 'userSig获取失败'}`, icon: 'none' })
      return
    }
    const imResponse = await tim.login({ userID: options.userID, userSig })
    if (imResponse.data.repeatLogin === true) {
      console.log('标识帐号已登录，本次登录操作为重复登录')
    }
    success()
  } catch (imError) {
    console.warn('login error:', imError) // 登录失败的相关信息
    error()
  }
}
// 创建群组
const doCreateGroup = async ({ success = (res: object) => {}, error = () => {} }) => {
  try {
    const imResponse = await tim.createGroup({
      // @ts-ignore

      name: getApp().globalData.userInfo.nickname,
      type: TIM.TYPES.GRP_AVCHATROOM,
      // @ts-ignore

      groupID: getApp().globalData.groupID,
      introduction: '',
      notification: '',
      avatar: '',
    })
    success(imResponse.data.group)
  } catch (imError) {
    console.warn('createGroup error:', imError) // 登录失败的相关信息
    error()
  }
}
// 加入群聊
const doJoinGroup = async ({ success = (res: object) => {}, error = (err: any) => {} }) => {
  try {
    const imResponse = await tim.joinGroup({
      // @ts-ignore

      groupID: getApp().globalData.groupID,
    })
    switch (imResponse.data.status) {
      case TIM.TYPES.JOIN_STATUS_WAIT_APPROVAL: // 等待管理员同意
        error('加入群聊,等待管理员同意')
        break
      case TIM.TYPES.JOIN_STATUS_SUCCESS: // 加群成功
        // 加入的群组资料
        success(imResponse.data.group)
        break
      case TIM.TYPES.JOIN_STATUS_ALREADY_IN_GROUP: // 已经在群中
        success({})
        break
      default:
        break
    }
  } catch (imError) {
    console.warn('申请加群失败的相关信息 :', imError) // 申请加群失败的相关信息}
    error(imError)
  }
}
// 解散群聊
const doDismissGroup = async ({ success = (res: object) => {}, error = (err: any) => {} }) => {
  try {
    const imResponse = await tim.dismissGroup(
      // @ts-ignore

      getApp().globalData.groupID,
    )
    // @ts-ignore

    success({ groupID: getApp().globalData.groupID })
  } catch (imError) {
    console.warn('解散群组失败的相关信息:', imError) // 解散群组失败的相关信息
    error(imError)
  }
}

export { doLoginIM, doCreateGroup, doJoinGroup, doDismissGroup }
