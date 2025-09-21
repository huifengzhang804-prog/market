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
