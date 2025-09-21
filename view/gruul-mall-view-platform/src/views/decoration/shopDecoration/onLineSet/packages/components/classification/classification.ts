export type DeCategoryType = {
    style: number
    categoryList: DeCategoryItem[]
}
export type DeCategoryItem = {
    id: string
    name: string
    productNum: number
}
export interface ApiCategoryData {
    id: string
    name: string
    categoryImg?: string
    children: ApiCategoryData[]
}
export type CommodityItem = {
    name: string
    pic: string
    id: string
    salePrices: string[]
}
export default {
    style: 1,
    listStyle: 1,
    buyBtnStyle: 1,
    goodsMargin: 0,
    fontSelected: '#FF0000',
    bgSelected: '#0E0202',
    fontNotSelected: '#000000',
    bgNotSelected: '#F5F5F5',
    categoryList: [],
}
