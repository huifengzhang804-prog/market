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
