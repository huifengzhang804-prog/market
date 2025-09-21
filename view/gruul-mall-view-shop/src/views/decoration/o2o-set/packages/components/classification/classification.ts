export type DeCategoryType = {
    style: number
    // listStyle: number
    // buyBtnStyle: number
    // goodsMargin: number
    // fontSelected: string
    // bgSelected: string
    // fontNotSelected: string
    // bgNotSelected: string
    // shopType:number
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
    style: 2,
    listStyle: 1,
    buyBtnStyle: 1,
    goodsMargin: 0,
    fontSelected: '#FF0000',
    bgSelected: '#0E0202',
    fontNotSelected: '#000000',
    bgNotSelected: '#F5F5F5',
    categoryList: [],
}
