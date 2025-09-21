import classification from '../classification/classification'
import swiper from '../swiper/swiper'

const defaultData = {
    swiper,
    classification,
}
const defaultDataArr = Object.values(defaultData)
type FormDataType = (typeof defaultDataArr)[number]

export interface ComponentItem {
    icon: string
    id?: string
    label: string
    value: string
    formData?: FormDataType
    showType?: boolean
    type?: number
}
export default defaultData
