/**
 * 是否为小程序端
 * @return {boolean}
 */
export function isMpWeixin(): boolean {
  return uni.getSystemInfoSync().uniPlatform === 'mp-weixin'
}

export function initStyleMarginTop(): number {
  if (isMpWeixin()) {
    return uni.getMenuButtonBoundingClientRect().bottom
  }
  return 0
}

/**
 * 判断商品是否售罄
 * true  无库存 已售罄
 * false 有库存 未售罄
 * todo 可以为商品数据处理 单独设计一个工具文件
 * @param stockTypes 库存类型数组
 * @param stocks 库存数量数组 和 库存类型数组下标 一一对应
 */
export function isSoldOut(stockTypes: string[], stocks: Long[]) {
  if (!stockTypes || stockTypes.length === 0) {
    return true
  }
  //如果库存类型包含无限库存 sku 则返回 false
  if (stockTypes.includes('UNLIMITED')) {
    return false
  }
  //校验库存 只要有一个库存大于 0
  if (!stocks || stocks.length === 0) {
    return true
  }
  //只要有一个库存  大于 0 就返回 false 否则 返回 true(已售罄)
  return stocks.every((stock: number | string) => Number(stock) <= 0)
}
