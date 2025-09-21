import loginLine from '../login-line/loginLine'
import search from '../search/search'
import navigation from '../navigation/navigation'
import swiper from '../swiper/swiper'
import seckill from '../seckill/seckill'
import recommend from '../recommend/recommend'
import goods from '../goods/goods'
import shop from '../shop/shop'
import guarantee from '../guarantee/guarantee'
import footerInfo from '../footer-info/footer-info'
import copyright from '../copyright/copyright'

const defaultData = {
    loginLine,
    search,
    navigation,
    swiper,
    seckill,
    recommend,
    goods,
    shop,
    guarantee,
    footerInfo,
    copyright,
}

const defaultDataArr = Object.values(defaultData)

// type FormDataType = typeof defaultDataArr[number]

export interface ComponentItem {
    icon: string
    id?: string
    label: string
    value: keyof typeof defaultData
    formData?: any
    top?: boolean
    width?: number
    space?: number
    borderColor?: string
    loadingHeight?: string
    show?: boolean
}

export default defaultData
