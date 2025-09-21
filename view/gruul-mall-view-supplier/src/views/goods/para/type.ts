export interface FeatureValue {
    firstValue: string
    secondValue: number
}

export interface Feature {
    featureName: string
    featureValues: FeatureValue[]
    isMultiSelect: boolean
    isRequired: boolean
}

export interface Record {
    featuresType: string
    featuresValue: Feature
    id: string
    shopId: string
}
interface Page {
    size: number
    current: number
}

export interface Goods {
    page: Page
    loading: boolean
    goods: Record[]
    total: number
}
