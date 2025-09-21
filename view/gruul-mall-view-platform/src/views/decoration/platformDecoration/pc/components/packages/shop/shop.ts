import { doGetShopList } from '@/apis/decoration/index'
import { ElMessage } from 'element-plus'
import type { ShopList } from '@/components/q-select-shop/type'
import type { ShopListRecords } from '@/apis/shops/model/type'

export type Shop = {
    mainTitle: string
    subtitle: string
    list: ShopList[]
}

const shop: Shop = {
    mainTitle: '',
    subtitle: '',
    list: [],
}

export default shop

/**
 * 获取店铺列表
 */
export async function handleGetGoodList(list: ShopList[]) {
    if (!list.length) {
        return []
    }
    try {
        const ids = list.map((item) => item.id)
        const { data, code } = await doGetShopList({
            shopIds: ids,
            productIsNotEmpty: true,
        })
        if (code !== 200) {
            ElMessage.error('获取店铺失败！')
            return []
        }

        return data.records.map((item: ShopListRecords) => {
            const { shopType, name, id, logo, score, newTips } = item
            return { shopType, name, id, logo, score, newTips }
        })
    } catch (error) {
        return []
    }
}
