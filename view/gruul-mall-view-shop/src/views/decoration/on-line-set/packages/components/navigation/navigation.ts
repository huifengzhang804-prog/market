import type { LinkSelectItem } from '@/components/link-select/linkSelectItem'
/**
 * 导航类型
 * @param {string} pathLink 跳转路径
 * @param {string} linkUrl 链接跳转地址
 * @param {string} linkName 链接名称
 * @param {string} append home功能页面会有此参数
 * @param {string} append 链接类型5为自定义页面
 */
export type NavItem = {
    navName: string
    fontColor: string
    navIcon: string
    id: string
    link: LinkSelectItem
}
type NavItemType = {
    navigationList: NavItem[]
}
export const defaultNavItem = markRaw({
    navName: '导航名称',
    fontColor: '#333333',
    navIcon: 'https://qiniu-app.qtshe.com/u391.png',
    id: '',
    link: {
        id: '',
        type: null,
        append: '',
        url: '',
        name: '',
    },
})
const defaultNavData: NavItemType = {
    navigationList: Array.from([1, 2, 3, 4], (index) => {
        const temp = JSON.parse(JSON.stringify(defaultNavItem))
        // 解决渲染层 key值重复问题
        temp.id = String(index + index)
        return temp
    }),
}
export default defaultNavData
