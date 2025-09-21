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

export interface MenuDTO {
    isAdmin: boolean
    menuConfig: { [key: string]: any }
    menus: Menu[]
}
