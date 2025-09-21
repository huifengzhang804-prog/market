// 商品详情页套餐图片请求参数
export interface TParamsSetMealBasicInfo {
  shopId: Long
  productId: Long
}

// 套餐详情请求参数
export interface TParamsGetSetMealDetail {
  shopId: Long
  setMealId: string
}
