// 菜单配置项
export const menuItem = [
    {
        icon: 'icon-zujian-weixuanzhong',
        activeIcon: 'icon-zujian-xuanzhong',
        text: '添加组件',
    },
    {
        icon: 'icon-zidingyi-weixuanzhong',
        activeIcon: 'icon-zidingyi-xuanzhong',
        text: '自定义页面',
    },
    {
        icon: 'icon-shezhi-weixuanzhong',
        activeIcon: 'icon-shezhi-xuanzhong',
        text: '基础设置',
    },
]

// 切换名称
export type ActiveName = '添加组件' | '自定义页面' | '基础设置'

// 默认基础设置数据
export const defaultOtherData = () => ({ couponImg: '', integralImg: '', seckillImg: '', service: true, car: false })

const innerWidth = window.innerWidth // 可视窗口宽度
const innerHeight = window.innerHeight // 可视窗口高度
/**
 * 判断超出X轴边界 返回boolean
 */
export const xBoundary = (val: number, width: number, left: number) => {
    const w = innerWidth - width
    return (left <= 0 && val <= 0) || (left >= w && val >= w)
}

/**
 * 判断超出Y轴边界 返回boolean
 */
export const yBoundary = (val: number, height: number, top: number, headerH: number) => {
    const h = innerHeight - height

    return (top <= headerH && val <= headerH) || (val >= h && top >= h)
}

/**
 * 判断超出X边界 返回位置
 */
export const xPosition = (val: number, width: number) => {
    const w = innerWidth - width

    if (val <= 0) return 0

    if (val >= w) return w

    return val

    // return (val <= 0) ? 0 : (val >= w) ? w : val 不易读放弃
}

/**
 * 判断超出Y边界 返回位置
 */
export const yPosition = (val: number, height: number, headerH: number) => {
    const h = innerHeight - height

    if (val <= headerH) return headerH

    if (val >= h) return h

    return val

    // return (val <= 0) ? 0 : (val >= h) ? h : val 不易读放弃
}
