import { LinkSelectItem } from '../../../types'

export type DeCategoryType = {
    style: number
    classificationTitle: string
    categoryShow: boolean
    navShow: boolean
    searchShow: number
    commodityShow: number
    goodsShow: number[]
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
    platformCategoryFirstId: string
    platformCategoryFirstName: string
    platformCategorySecondId: string
    platformCategorySecondName: string
    platformCategoryThirdId: string
    platformCategoryThirdName: string
    platformCategorySecondImg?: string
    platformCategoryThirdImg?: string
    platformCategoryFirstProductNumber?: number
    platformCategorySecondProductNumber?: number
    platformCategoryThirdProductNumber?: number
    platformCategorySecondOldImg?: string
    platformCategoryThirdOldImg?: string
    productNum: number
    children: DeCategoryItem[]
    ads?: { img: string; link: LinkSelectItem }[]
    platformCategorySecondChildren?: DeCategoryItem[]
    platformCategoryThirdChildren?: DeCategoryItem[]
}
export interface ApiCategoryData {
    productNumber: number | undefined
    id: string
    name: string
    categoryImg?: string
    children: ApiCategoryData[]
    ads?: { img: string; link: LinkSelectItem }[]
}
export type CommodityItem = {
    name: string
    pic: string
    id: string
    salePrices: string[]
    salesCount: number
    specImages?: string[]
    activeImg?: number
    freeShipping?: boolean
    shopType?: string
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
    goodsShow: [],
    categoryList: [
        {
            platformCategoryFirstId: '',
            platformCategoryFirstName: '',
            platformCategorySecondChildren: [],
            ads: [],
            children: [
                {
                    platformCategorySecondId: '',
                    platformCategorySecondName: '',
                    platformCategoryThirdChildren: [],
                    children: [
                        {
                            platformCategoryThirdId: '',
                            platformCategoryThirdName: '',
                        },
                    ],
                },
            ],
        },
    ],
    categoryShow: true,
    navShow: true,
    searchShow: 4,
    commodityShow: 2,
    classificationTitle: '',
}
