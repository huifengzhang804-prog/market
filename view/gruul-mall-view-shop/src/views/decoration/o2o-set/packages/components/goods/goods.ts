enum ListStyle {
    'goods-style--one' = 'goods-style--one',
    'goods-style--two' = 'goods-style--two',
    'goods-style--three' = 'goods-style--three',
    'goods-style--four' = 'goods-style--four',
    'goods-style--five' = 'goods-style--five',
}
type ListStyles = [
    {
        label: '大图模式'
        value: 'goods-style--one'
    },
    {
        label: '一行两个'
        value: 'goods-style--two'
    },
    {
        label: '详细列表'
        value: 'goods-style--three'
    },
    {
        label: '横向滑动'
        value: 'goods-style--four'
    },
    {
        label: '瀑布流'
        value: 'goods-style--five'
    },
]
type GoodType = {
    ponentType: number
    goods: any[]
    listStyle: keyof typeof ListStyle
    // pagePadding: number
    // titleStyle: number
    // goodPadding: number
    // goodsStyle: string
    // angle: string
    // textStyle: string
    firstCatList: CategoryItem[]
    currentCategoryId: string
    showContent: GoodShowContentType
    goodsItem: GoodEnumItemType
    goodsCount: number
    goodsTemp: GoodTempItemType
}
type GoodShowContentType = {
    // nameShow: boolean
    // priceShow: boolean
    // buttonShow: boolean
    buttonStyle: number
    buttonText: string
    tagShow: boolean
    tagStyle: number
    pointShow: boolean
}
type GoodEnumItemType = {
    id: number
    img: string
    name: string
    price: string
}
type GoodTempItemType = {
    id: number
    name: string
    img: string
    endTime: string
    price: number
    guide: number
    soldCount: number
    inventory: number
    deliveryTime: string
}
export default {
    /** 1展示分类  2不展示分类 */
    ponentType: 1,

    /** 商品 */
    goods: [] as ApiGoodItemType[],

    /** 列表样式 */
    listStyle: 'goods-style--one',

    /** 页面边距 */
    // pagePadding: 17,

    /** 类目样式 1新  2下划线 */
    // titleStyle: 1,

    /** 商品间距 */
    // goodPadding: 12,

    /** 商品样式 */
    // goodsStyle: 'is-none',

    /** 图片倒角 */
    // angle: 'is-straight',

    /** 文本样式 */
    // textStyle: 'is-normal',

    /** 一级类目 */
    firstCatList: [],

    /** 当前分类ID */
    currentCategoryId: '',

    /** 显示内容 */
    showContent: {
        // nameShow: true,
        // priceShow: true,
        // buttonShow: true,
        buttonStyle: 1,
        buttonText: '',
        tagShow: true,
        tagStyle: 1,
        pointShow: true,
    },

    /** 商品对象 */
    goodsItem: {
        id: 1,
        img: 'https://qiniu-app.qtshe.com/u391.png',
        name: '商品名称',
        price: '99.90',
    },

    /** 商品数量 */
    goodsCount: 1,

    goodsTemp: {
        id: 1,
        name: '商品名称',
        img: 'https://qiniu-app.qtshe.com/u391.png',
        endTime: '20:15:14',
        price: 99,
        guide: 219,
        soldCount: 10,
        inventory: 120,
        deliveryTime: '06月24日 14:00',
    },
} as GoodType
type CategoryItem = {
    productNum: number
    name: string
    id: string
}
// 选择商品时检索出的商品类型
type ApiGoodItemType = {
    categoryFirstId: string
    categorySecondId: string
    categoryThirdId: string
    createTime: string
    hotScore: string
    id: string
    isCheck?: boolean
    productName: string
    pic: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    salePrices: string[]
    salesVolume: string
    shopId: string
    shopName: string
}
