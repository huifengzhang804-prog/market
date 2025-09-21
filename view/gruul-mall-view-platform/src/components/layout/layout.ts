export enum MenuType {
    CATALOG = 'CATALOG',
    MENU = 'MENU',
}

export interface Menu {
    id: string
    name: string
    icon: string
    type: MenuType
    path: string
    children?: Array<Menu>
    noShowingChildren?: boolean
    parentId?: Long
    order: string
}

export type menuType = {
    id?: number
    name?: string
    path?: string
    noShowingChildren?: boolean
    children?: menuType[]
    value: unknown
    meta?: {
        icon?: string
        title?: string
        rank?: number
        showParent?: boolean
        extraIcon?: string
    }
    showTooltip?: boolean
    parentId?: number
    pathList?: number[]
    redirect?: string
}
