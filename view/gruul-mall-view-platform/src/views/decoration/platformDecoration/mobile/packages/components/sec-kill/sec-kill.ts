/**
 * @param  goodList 商品列表id
 * @param  display 展示方式 1为横向滚动 2详细列表
 * @param  goodStyle 商品样式 1无边描底 2卡片投影 3 描边白底
 * @param  border 图片倒角
 * @param  tagStyle 1 新品 2热卖 3抢购
 * @param  sessionId 场次ID
 * @param  seckillTime 秒杀时间
 * @param  textStyle 1常规体 2加粗体
 */
export default {
    goodList: [] as seckillGoodType[],
    display: 1,
    padding: 17,
    marginBottom: 12,
    goodStyle: 1,
    border: false,
    showtag: false,
    tagStyle: 1,
    sessionId: '',
    seckillTime: '',
    seckillEndTime: '',
    textStyle: 1,
}

export type seckillGoodType = {
    maxPrice: string
    minPrice: string
    productId: string
    productName: string
    productPic: string
}
