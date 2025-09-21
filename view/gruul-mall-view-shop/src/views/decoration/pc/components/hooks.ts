import { difference } from 'lodash-es'
import { doGetRetrieveProduct } from '@/apis/good'
import { ElMessage } from 'element-plus'

export const useCommon = () => {
    const unGoods = ref<string[]>([])

    const getGoodList = async (ids: string[]) => {
        try {
            const { data, code } = await doGetRetrieveProduct({
                size: ids.length,
                ids: ids,
                searchTotalStockGtZero: true,
            })
            if (code !== 200) return ElMessage.error('获取商品失败')

            const list = data.list.map((item) => item.id)
            unGoods.value = difference(ids, list)
        } catch (error) {
            console.log('error', error)
        }
    }

    return {
        unGoods,
        getGoodList,
    }
}
