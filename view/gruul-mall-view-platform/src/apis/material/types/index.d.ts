export interface Category {
    id: string
    name: string
    paths?: Path[]
    selectHistory?: boolean
    shopId?: number
    hasChildren?: boolean
}

export interface Path {
    id: string
    level: number
}
export interface SuggestParams {
    categoryId: string[]
    format: string[]
    size: string[]
}
