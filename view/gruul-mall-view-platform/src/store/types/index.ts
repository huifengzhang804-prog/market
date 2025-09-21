export interface Menu {
    id: number
    name: string
    icon: string
    type: number
    path: string
    children?: Array<Menu>
}

export type appType = {
    sidebar: {
        opened: boolean
        withoutAnimation: boolean
        // 判断是否手动点击Collapse
        isClickCollapse: boolean
    }
    layout: string
    device: string
    viewportSize: { width: number; height: number }
    sortSwap: boolean
}
