type LinkSelectItem = LinkType
/**
 * 链接选择器
 * @param {number | string} id 1为首页 2为分类 3为购物车 4为个人中心
 * @param {number | string} type 链接类型 0 -功能页面 1-商超商品 Goods 2活动营销 3自定义页面 6自定义链接 7小程序跳转
 * @param {string} name 链接名称
 * @param {string} url 链接地址
 * @param {string} append 附加参数
 */
// id为字符串适配C端swiper item-id为string
export type LinkType = {
  id: string
  type: number
  name: string
  url: string
  append: string
  shopId?: string
}
/**
 * 魔方类型
 * @param borderWidth 图片间隙
 * @param layoutWidth 列数
 * @param layoutHeight 行数
 * @param showMethod 行数index
 * @param pageMargin 页面边距离
 * @param width 展示个数
 * @param subEntry 图片数组
 */
export interface CubeBoxFormData {
  borderWidth: number
  layoutWidth: number
  layoutHeight: number
  showMethod: number
  pageMargin: number
  width: number
  subEntry: IBanners[]
  showCubeListWrap: any[]
  pageMarginStyle: any
}
export interface IBanners {
  x: number
  y: number
  width: number
  height: number
  img: string
  link: LinkSelectItem
  linkName: string
}

/**
 * 轮播图类型
 * @param {SwiperListItem[]} swiperList
 * @param {number} imageStyle 1常规 2投影
 * @param {number} imageAngle 1直角 2圆角
 * @param {number} indicator 1样式一 2样式二 3样式三
 */
export type SwiperFormData = {
  type: string
  swiperList: SwiperListItem[]
  padding: number
  imageStyle: number
  imageAngle: number
  indicator: number
  height: number
  bgType: string
}
export type SwiperListItem = {
  title: string
  img: string
  link: LinkSelectItem
  linkName: string
}
/**
 * 分隔符
 */
export type SeparatorFormData = {
  borderColor: string
  hasMargin: boolean
  borderStyle: string
}
/**
 * 图片类型
 * @param boxHeight 外层元素高度
 */
export type ResizeImageFormData = {
  img: string
  width: string
  height: string
  boxHeight: number
  boxWidth: number
  top: number
  left: number
  link: LinkSelectItem
}
/**
 * 搜索组件类型
 * @param keyWord 关键词语
 * @param hotWord 热门搜索词
 */
export type SearchFormData = {
  keyWord: string
  shopId: Long
  color: string
  background: string
  borderColor: string
  borderRadius: number
  height: number
  btnBorderColor: string
  btnBorderRadius: number
  btnFontColor: string
}

/**
 * 标题栏类型
 */
export type TitleFormData = {
  showStyle: string
  titleName: string
  backgroundColor: string
  color: string
  link?: LinkType
}
/**
 * 视频类型
 */
export type VideoFormData = {
  VideoCom: string
  radio: number
  radioBL: number
  radioTC: number
  video: string
  videoLink: string
  poster: string
  width: string
  height: string
  link: LinkSelectItem
}
/**
 * 导航组件
 */
type NavigationFormData = {
  navName: string
  fontColor: string
  navIcon: string
  id: string
  link: LinkSelectItem
}

export type NavigationDecorationProp = {
  rows: number
  rowNums: number
  navigationList: NavigationFormData[]
}
export enum ListStyle {
  '大图' = 'goods-style--one',
  '一行两个' = 'goods-style--two',
  '详细列表' = 'goods-style--three',
  '横向滑动' = 'goods-style--four',
  '瀑布流' = 'goods-style--five',
}
/**
 * 商品组件
 */
export type GoodFormData = {
  /** 1展示分类  2不展示分类 */
  ponentType: number
  /** 商品 */
  goods: any[]
  /** 类目样式 1新  2下划线 */
  // titleStyle: number
  /** 列表样式 */
  listStyle: ListStyle
  /** 页面边距 */
  // pagePadding: number
  /** 商品间距 */
  // goodPadding: number
  /** 商品样式 */
  // goodsStyle: string
  /** 图片倒角 */
  // angle: string
  /** 文本样式 */
  // textStyle: string
  /** 显示内容 */
  showContent: IShowContent
  /** 商品对象 */
  goodsItem: any
  /** 商品数量 */
  goodsCount: number
  firstCatList: FirstCateItem[]
  goodsNameConfig?: {
    rows: number
    showTag: boolean
  }
  extraConfig?: {
    coupon: boolean
    freeMail: boolean
    historyData: boolean
    memberTag: string
    sellPointDesc: boolean
    shopName: boolean
    shopTag: boolean
  }
}

interface IShowContent {
  /** 商品名称 */
  // nameShow: boolean
  /** 商品价格 */
  // priceShow: boolean
  /** 购物按钮 */
  // buttonShow: boolean
  /** 购物按钮样式 */
  buttonStyle: number
  /** 购物车按钮文案 */
  buttonText: string
  /** 商品角标 */
  tagShow: boolean
  /** 商品角标样式 */
  tagStyle: number
}
export type FirstCateItem = {
  platformCategoryFirstId?: string
  platformCategoryFirstName?: string
  name?: string
  id?: string
  productNum: string
}
export type NavBarType = {
  codeStyle: number
  selectColor: string
  underfillColor: string
  defaultColor: string
  menuList: NavBarMenuType[]
}

export type NavBarMenuType = {
  text: string
  iconType: number
  iconPath: string
  selectedIconPath: string
  defIcon: string
  actIcon: string
  isHome: boolean
  id: string
  isAdd: boolean
  codeStyle: number
  link: LinkType
}

enum ShowType {
  'province' = '省市区',
  'city' = '市区',
  'cityDetails' = '市区详细地址',
  'details' = '详细地址',
}

// 组合定位
export type styleFormData = {
  color: string
  showType: keyof typeof ShowType
  show?: boolean
  type?: 'positionStyle'
}
