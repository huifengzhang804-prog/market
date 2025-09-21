import { decodeText } from './decodeText'
import { TIM } from '../../chatRoom/hooks'
export const newMembersToInform =
  '直播间严禁出现危害国家安全，破坏政治和社会稳定的言论，严禁出现低俗色情、售假售劣、售卖违禁品，引导私下交易等行为，不要在直播中从事挂机、录播、盗播等行为。若违反，平台有权依据规则对您采取相关管控。'
export const ZB_MSAGE = `[直播间消息]`
// 处理头像
export function handleAvatar(item: any) {
  let avatar = ''
  if (item.type === TIM.TYPES.CONV_C2C) {
    avatar = item?.userProfile?.avatar || 'https://web.sdk.qcloud.com/component/TUIKit/assets/avatar_21.png'
  } else {
    avatar = item?.groupProfile?.avatar || 'https://web.sdk.qcloud.com/component/TUIKit/assets/group_avatar.png'
  }
  return avatar
}
// 处理系统提示消息展示
export function handleTipMessageShowContext(message: any) {
  const options: any = {
    message,
    text: '',
  }
  // @ts-ignore

  const userName = message.nick || '游客'
  switch (message.payload.operationType) {
    case TIM.TYPES.GRP_TIP_MBR_JOIN:
      options.text = `欢迎${userName} 加入直播间`
      break
    // 有成员加群
    case TIM.TYPES.GRP_TIP_MBR_QUIT:
      options.text = newMembersToInform
      break
    default:
      options.text = ZB_MSAGE
      break
  }
  return options
}

export function handleMutedContext(item: any) {
  let text = ''
  if (item.payload.muteTime > 0) {
    // options.text =  `群成员：${member.userID}被禁言${member.muteTime}秒`;
    text = `您已被禁言！`
  } else {
    text = `您已解除禁言！`
  }
  return text
}

// 解析处理文本消息展示
export function handleTextMessageShowContext(item: any) {
  const options: any = {
    text: decodeText(item.payload),
  }
  return options
}
