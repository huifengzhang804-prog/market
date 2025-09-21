export const getDefault: () => Navigation = () => ({ text: '', type: 'system', link: '', id: Date.now() })

export default [getDefault()]

/**
 * 类型
 */
export type Navigation = {
    text: string
    type: 'system' | 'activity' | 'custom'
    link: string
    id: number
}
