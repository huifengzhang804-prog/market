import { itemType } from '@/components/q-select-goods/type'

export type Recommend = {
    page: string
    name: string
    style: string
    img: string
    list: { [key: string]: itemType[] }
    radioKeys: string[]
    pageId?: string
}

const recommend: Recommend = {
    page: '',
    name: '',
    style: 'row',
    img: '',
    list: {},
    radioKeys: [],
}

export default recommend
