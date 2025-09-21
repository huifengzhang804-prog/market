import blankPaceholder from '../blankHolder/blankPaceholder'
import coupon from '../coupon/coupon'
import cubeBox from '../cube-box/cubeBox'
import goods from '../goods/goods'
import navigation from '../navigation/navigation'
import resizeImage from '../resize-image/resize-image'
import richText from '../rich-text/rich-text'
import search from '../search/search'
import separator from '../separator/separator'
import swiper from '../swiper/swiper'
import titleBar from '../title-bar/title-bar'
import video from '../video/video'
import navBar from '../navBar/nav-bar'
import secKill from '../sec-kill/sec-kill'
import classification from '../classification/classification'

const defaultData = {
    blankPaceholder,
    classification,
    coupon,
    cubeBox,
    goods,
    navigation,
    resizeImage,
    richText,
    search,
    separator,
    swiper,
    titleBar,
    video,
    navBar,
    secKill,
}
const defaultDataArr = Object.values(defaultData)
type FormDataType = (typeof defaultDataArr)[number]

export interface ComponentItem {
    icon: string
    id?: string
    label: string
    value: keyof typeof defaultData
    formData?: FormDataType
    showType?: boolean
    type?: number
}
export default defaultData
