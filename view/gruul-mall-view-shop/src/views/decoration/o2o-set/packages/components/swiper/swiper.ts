import { LinkSelectItem } from '@/components/link-select/linkSelectItem'

export interface SwiperListItem {
    title: string
    img: string
    link: LinkSelectItem
    linkName: string
}

/**
 * 轮播图默认值
 * @param {SwiperListItem[]} swiperList
 * @param {number} imageStyle 1常规 2投影
 * @param {number} imageAngle 1直角 2圆角
 * @param {number} indicator 1样式一 2样式二 3样式三
 */
const swiperFormData = {
    type: 'swiper',
    swiperList: [] as SwiperListItem[],
    padding: 14,
    imageStyle: 1,
    imageAngle: 1,
    indicator: 1,
    height: 185,
}
export default swiperFormData
export type SwiperFormDataType = typeof swiperFormData
