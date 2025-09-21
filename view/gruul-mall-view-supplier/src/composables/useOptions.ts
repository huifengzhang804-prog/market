import { doGetShopSearchList } from '@apis/shops'
import { ShopListVO } from '@apis/shops/types/response'
import { R } from '@apis/http.type'
import { Ref, ref } from 'vue'
import { SearchProps, SearchType } from '@components/types'

/**
 * 店铺搜索功能的返回值接口
 */
export interface ShopOptionsResult {
    /** 店铺搜索列表 */
    shopSearchList: Ref<ShopListVO[]>
    /** 远程搜索店铺的方法 */
    shopSearchRemote: (query: string) => Promise<void>
    /** 店铺搜索选项配置 */
    shopSearchOptions: Partial<SearchProps>
}

/**
 * 所有搜索选项的组合接口 通过&方式合并
 */
export type OptionsResult = ShopOptionsResult

/**
 * 提供店铺搜索相关功能
 * @returns ShopOptionsResult 店铺搜索相关的选项和方法
 */
export function useShopSearchOptions(): ShopOptionsResult {
    const shopSearchList = ref<ShopListVO[]>([])

    /**
     * 远程搜索店铺的方法
     * @param query 搜索关键词
     */
    const shopSearchRemote = async (query: string): Promise<void> => {
        try {
            if (!query?.trim()) {
                shopSearchList.value = []
                return
            }

            const { data } = (await doGetShopSearchList({ shopName: query })) as R<ShopListVO[]>
            shopSearchList.value = data || []
        } catch (error) {
            console.error('获取店铺列表失败:', error)
            shopSearchList.value = []
        }
    }

    // 店铺配置信息
    const shopSearchOptions: Partial<SearchProps> = {
        valueType: 'select' as SearchType,
        options: shopSearchList,
        fieldProps: {
            props: {
                value: 'id',
                label: 'name',
                expandTrigger: 'hover',
            },
            filterable: true,
            remote: true,
            reserveKeyword: true,
            emptyValues: [null, undefined, ''],
            remoteMethod: shopSearchRemote,
        },
    }

    return {
        shopSearchOptions,
        shopSearchRemote,
        shopSearchList,
    }
}

/**
 * 提供所有选项相关功能的组合式函数
 * @returns 包含所有选项相关的功能
 */
export function useOptions(): OptionsResult {
    const shopOptions = useShopSearchOptions()

    return {
        ...shopOptions,
    }
}
