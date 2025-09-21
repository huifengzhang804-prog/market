export interface ShopListRecords {
    id: number
    companyName: string
    name: string
    no: string
    contractNumber: string
    status: string
    address: string
    location: Location
    logo: string
    userId: number
    briefing: string
    createTime: string
    updateTime: string
    version: number
    registerInfo: RegisterInfo
    bankAccount: BankAccount
    score: string
    shopBalance: ShopBalance
    shopType: string
    mode: string
    extractionType: string
    drawPercentage: number
    newTips: string
}
// 位置 S
export interface Location {
    envelope: Envelope
    factory: Factory
    SRID: number
    userData: any
    coordinates: Coordinates
}

export interface Envelope {
    minx: number
    maxx: number
    miny: number
    maxy: number
}

export interface Factory {
    precisionModel: PrecisionModel
    coordinateSequenceFactory: any
    SRID: number
}

export interface PrecisionModel {
    modelType: ModelType
    scale: number
}

export interface ModelType {
    name: string
}

export interface Coordinates {
    dimension: number
}
// 位置 E

export interface RegisterInfo {
    license: string
    legalPersonIdFront: string
    legalPersonIdBack: string
    createTime: string
    updateTime: string
    version: number
}

export interface BankAccount {
    payee: string
    bankName: string
    bankAccount: string
    openAccountBank: string
    createTime: string
    updateTime: string
    version: number
}

export interface ShopBalance {
    total: number
    undrawn: number
    uncompleted: number
}
