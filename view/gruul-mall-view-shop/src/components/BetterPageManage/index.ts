// 分页参数
export interface Page {
    size: number
    current: number
}

interface DefaultConfig extends Page {
    sizes: number[]
    total: number
}

export const defaultConfig = reactive<DefaultConfig>({
    size: 10,
    current: 1,
    total: 0,
    sizes: [10, 20, 50, 100],
})
