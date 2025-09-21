export interface MenuType {
    abbreviationName: string
    children?: MenuType[]
    id: string
    name: string
    order: number
    parentId: string | number
    path: string
    perms: string[]
    type: string
    unshared: boolean
    version: number
    icon?: string
}

export interface DefaultFormType {
    roleId: string
    userId: string | null
    nickname: string | null
    username: string
    password: string | null
    confirmPassword: string | null
    mobile: string | null
    email: string | null
}

export interface User {
    email?: string
    mobile: string
    userId: number | string
    username?: string
    avatar?: string
    gender?: 'UNKNOWN' | 'MALE' | 'FEMALE'
    nickname?: string
}

export interface Pagination {
    current: number
    pages: number
    size: number
    total: number
}

export interface ApiResponse {
    clientType: string
    current: number
    excludeShopId: string
    keywords: string
    pages: number
    records: User[]
    size: number
    total: number
}
