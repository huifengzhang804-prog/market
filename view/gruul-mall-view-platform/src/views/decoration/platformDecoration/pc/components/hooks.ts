import { difference } from 'lodash-es'
import { doGetRetrieveProduct } from '@/apis/good'
import { ElMessage } from 'element-plus'
import { ApiRetrieve } from '@/apis/good/model'

export const useCommon = () => {
    const unGoods = ref<string[]>([])

    const getGoodList = async (ids: string[]) => {
        try {
            const idArr = ids[0] === undefined ? [] : ids
            const { data, code } = await doGetRetrieveProduct({
                size: ids.length,
                ids: idArr,
                searchTotalStockGtZero: true,
            })
            if (code !== 200) return ElMessage.error('获取商品失败')

            const list = data.list.map((item: ApiRetrieve) => item.id)
            unGoods.value = difference(idArr, list)
        } catch (error) {
            console.log('error', error)
        }
    }

    return {
        unGoods,
        getGoodList,
    }
}
