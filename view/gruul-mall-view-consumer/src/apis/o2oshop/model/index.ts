import type { ApiGoodSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { DistributionKeyType } from '@/apis/good/model'
import { ShopStatus } from '@/basePackage/pages/merchant/types'
import { ShopMode } from '@/constant/global'
import { ShopTypeEnum } from '@/decoration/components/good/good'
import type { Geometry } from '@/apis/address/type'

export interface GoodItemSpec {
  productId: Long
  productName: string
  id: string
  image: string
  price: Long
  productImage?: string
  salePrice: Long
  num: number
  specs: string[]
  uniqueId: string
  freightTemplateId: string
  skuStock: ApiGoodSku
  finalPrice: string
  distributionMode: DistributionKeyType[]
  productAttributes: productAttribute[]
  weight: number
  sellType?: string
}

export interface productAttribute {
  featureName: string
  featureValues: { firstValue: string; secondValue: string; featureValueId: string }[]
  id: string
  isMultiSelect: boolean
  isRequired: boolean
}

export enum ShopType {
  SHOP_HOME = 'SHOP_HOME', // 店铺首页
  PRODUCT_DETAIL = 'PRODUCT_DETAIL', // 商品详情页面
}

export interface GetShopInfoRes {
  id?: Long
  /**
   * 店铺状态
   */
  status: keyof typeof ShopStatus
  /**
   * 店铺运营模式
   */
  shopMode: keyof typeof ShopMode
  /**
   * 店铺类型
   */
  shopType: keyof typeof ShopTypeEnum
  /**
   * 店铺名称
   */
  name: string
  /**
   * 店铺logo
   */
  logo: string
  /**
   * 定点经纬度
   */
  location: Geometry
  /**
   * 关注人数
   */
  follow: Long
  /**
   * 当前用户是否已关注
   */
  isFollow: boolean
  /**
   * 起送费 （O2O）
   */
  minLimitPrice: Long
  /**
   * 店铺直线距离
   */
  distance: number
  //todo --------------- 店铺首页需要 -----------------
  /**
   * 商户号
   */
  no: string
  /**
   * 公司名称
   */
  companyName: string
  /**
   * 店铺介绍
   */
  briefing: string
  /**
   * 店铺头部背景
   */
  headBackground: string
  /**
   * 联系方式
   */
  contractNumber: string
  /**
   * 省市区
   */
  area?: string[]
  /**
   * 联系地址
   */
  address: string
  /**
   * 营业时间
   */
  openTime: {
    start: string
    end: string
  }
  /**
   * 上新提示
   */
  newTips: string
  /**
   * 在售商品数量
   */
  productNum: Long
  /**
   * 销量
   */
  sales: Long
  //todo --------------- 商品详情需要 -----------------
  /**
   * 店铺综合体验评分
   */
  score: string
}
