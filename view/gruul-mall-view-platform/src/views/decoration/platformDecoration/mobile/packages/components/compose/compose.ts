import swiperFormData from './compose/swiper/swiper'
import searchFormData from './compose/search/search'
import positionStyleData from './compose/positioningStyle/positioningStyle'

const composeData = {
    search: searchFormData,
    swiper: swiperFormData,
    positionStyle: { type: 'positionStyle', ...positionStyleData },
}
export default composeData
export type ComposeDataType = typeof composeData
