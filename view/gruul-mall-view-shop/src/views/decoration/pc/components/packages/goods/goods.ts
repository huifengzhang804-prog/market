import { itemType } from '@/components/q-select-goods/type'

export type Goods = {
    mainTitle: string
    subtitle: string
    list: itemType[]
}

const goods: Goods = {
    mainTitle: '',
    subtitle: '',
    list: [],
}

export default goods
