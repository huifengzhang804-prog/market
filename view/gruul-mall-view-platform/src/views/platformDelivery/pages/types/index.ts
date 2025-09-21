export type filterCityDataType = { label: string; value: string; done?: boolean; children?: filterCityDataType }[]
export type cityItemType = { value: string; label: string; done?: boolean; children?: cityItemType[] }

interface Region {
    upperCode: string
    upperName: string
    length: number
    lowerName: string[]
    lowerCode: string[]
}
interface LogisticsIncludePostageVos {
    amountNum: 0
    id: string
    logisticsId: string
    pieceNum: 20
    postType: string
    region: Region[]
    weight: 0
}
export interface PrintList {
    defSelfShop: 'YES' | 'NO'
    defSelfSupplier: 'YES' | 'NO'
    id: string
    deviceNo: string
    printName: string
}
export interface PrintForm {
    printName: string
    printCode: string
    defSelfShop: boolean
    defSelfSupplier: boolean
}
export interface PageConfig {
    size: number
    current: number
}
