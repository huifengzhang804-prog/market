import type { LinkType } from '@decoration/components/types'
/**
 * 用户中心配置
 * @param {CustomStyleType} customStyle 头部自定义风格
 * @param {string} getCartText 领卡文案
 * @param {number} headStyle 头部风格 1系统风格 2自定义风格
 * @param {number} hideCartInlet 非会员显示领卡入口 0为隐藏 1为显示
 * @param {number} scanCode 扫码入口 0为隐藏 1为显示
 * @param {number} membershipCode 会员码入口 0为隐藏 1为显示
 * @param {number} menuStyle 菜单栏样式 1列表式 2位九宫格
 * @param {UserCenterMenuItem[]} menuVos 菜单栏列表
 */
export interface UserCenterType {
  customStyle: CustomStyleType
  getCartText: string
  headStyle: number
  id: string
  hideCartInlet: number
  scanCode: number
  membershipCode: number
  menuStyle: number
  menuList: UserCenterMenuItem[]
  menuScratchable: UserCenterMenuItem[]
  orderInfo: OrderItem[]
}
/**
 * 菜单栏类型
 * @param {number} allowUse 0不可用 1可用
 * @param {string} defaultIcon 默认图标地址
 * @param {boolean} hideMenu 菜单是否展示 0隐藏1显示
 * @param {string} menuIconUrl 菜单当前图标url
 * @param {string} menuName 菜单名称
 * @param {string} sortIndex 排序位置
 * @param {boolean} splitFlag 分隔
 */
export interface UserCenterMenuItem {
  id: number | null
  allowUse: number
  defaultIcon: string
  showMenu: boolean
  linkSelectItem: LinkType
  menuIconUrl: string
  menuName: string
  sortIndex: number
  splitFlag: boolean
}
/**
 * 自定义头部类型
 * @param {string} backgroundImage 背景图片
 * @param {string} cardColor 卡面颜色
 * @param {string} textColor 文字颜色
 * @param {number} infoColor （昵称/储值/返利/收藏/足迹）颜色
 * @param {number} scanCode 扫码入口颜色
 * @param {number} membershipCode 会员码入口颜色
 * @param {number} activateNowColor 付费会员立即开通按钮文本颜色
 * @param {number} activateNowBtnColor 付费会员立即开通按钮颜色
 */
interface CustomStyleType {
  backgroundImage: string
  cardColor: string
  textColor: string
  infoColor: string
  scanCodeColor: string
  membershipCodeColor: string
  activateNowColor: string
  activateNowBtnColor: string
}
interface OrderItem {
  name: string
  url: string
  link: LinkType
  key: keyof typeof OrderItemKey
}
enum OrderItemKey {
  unpaid,
  undelivered,
  unreceived,
  unhandledAfs,
  uncommented,
}
