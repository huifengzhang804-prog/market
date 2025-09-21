import type { GoodsListItem } from '@/components/q-choose-goods-popup/types'

const { divTenThousand } = useConvert()
/**
 * 商品价格格式化
 * @param {GoodsListItem} goods
 */
export function formatGoodsPrice(goods: GoodsListItem) {
    // 获取商品最高价格
    const highestPrice = divTenThousand(goods.highestPrice)
    // 获取商品最低价格
    const lowestPrice = divTenThousand(goods.lowestPrice)
    // 返回商品价格
    return `${lowestPrice}~${highestPrice}`
}
