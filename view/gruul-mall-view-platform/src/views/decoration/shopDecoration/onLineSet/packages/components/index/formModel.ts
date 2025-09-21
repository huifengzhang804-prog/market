import cubeBox from '../cube-box/cubeBox'
import goods from '../goods/goods'
import navigation from '../navigation/navigation'
import search from '../search/search'
import swiper from '../swiper/swiper'
import navBar from '../navBar/nav-bar'

const defaultData = {
    cubeBox,
    goods,
    navigation,
    search,
    swiper,
    navBar,
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
