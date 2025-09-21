export interface newLogisticsFormType {
    id?: string
    address: string
    cityCode?: string
    contactName: string
    contactPhone: string
    provinceCode?: string
    regionCode?: string
    zipCode: string
    defSend?: 'YES' | 'NO' | boolean
    defReceive?: 'YES' | 'NO' | boolean
    defSelfShop?: 'YES' | 'NO' | boolean
    defSelfSupplier?: 'YES' | 'NO' | boolean
    area: string[]
}
export interface logisticsFormType {
    id?: string
    Provinces: string[]
    address: string
    contactName: string
    contactPhone: string
    zipCode: string
    defSend?: boolean
    defReceive?: boolean
    defSelfShop?: boolean
    defSelfSupplier?: boolean
    area: string[]
}
