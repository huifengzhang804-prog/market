export interface EnablePageByType {
  businessType: string
  endpointType: 'WECHAT_MINI_APP' | 'H5_APP'
  id: Long
  name: string
  properties: string
  remark: string
  shopId: Long
  type: keyof typeof EnablePageByTypeType
}

enum EnablePageByTypeType {
  SHOP_CATEGORY_PAGE = 'SHOP_CATEGORY_PAGE',
  SHOP_BOTTOM_NAVIGATION_PAGE = 'SHOP_BOTTOM_NAVIGATION_PAGE',
  SHOP_HOME_PAGE = 'SHOP_HOME_PAGE',
}
