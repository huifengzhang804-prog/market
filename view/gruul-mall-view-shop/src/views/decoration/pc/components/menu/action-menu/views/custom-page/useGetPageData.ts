import { ElMessage } from 'element-plus'
import { doGetShopPages } from '@/apis/decoration/index'
import { ActiveName, ComponentsItem } from './type'
import { useShopInfoStore } from '@/store/modules/shopInfo'

export default () => {
    /**
     * 获取数据页面数据
     */
    const shopStore = useShopInfoStore()
    const getData = async (customType: ActiveName = 'activity', current = 1, size = 10, name?: string) => {
        const params = {
            shopId: shopStore.shopInfo.id,
            type: 'CUSTOMIZED_PAGE',
            templateType: 'SHOP_HOME_PAGE',
            businessType: 'ONLINE',
            endpointType: 'PC_MALL',
            customType,
            name: name || null,
            current: current,
            size,
        }

        const { data, success } = await doGetShopPages(params)

        if (!success) return ElMessage.error('获取页面数据失败，请稍后刷新重试！')
        return data
    }

    /**
     * 分页获取图文页面数据
     */
    const textData = ref<ComponentsItem[]>([])
    let textName = ref('')
    let textNoMore = ref(false)
    const textLoading = ref(false)
    let textCurrent = 1
    const getText = async (loadMore = false, size = 10) => {
        if (!loadMore) textCurrent = 1
        textLoading.value = true
        try {
            const { records, total } = await getData('text', textCurrent, size, textName.value)
            const data = (records as []) || []

            if (loadMore) {
                textData.value.push(...data)
            } else {
                textData.value = data
                textNoMore.value = false
            }

            textNoMore.value = textData.value.length >= total
            if (!textNoMore.value) textCurrent++
        } catch (error) {
            console.log('error', error)
        }

        textLoading.value = false
    }

    /**
     * 获取活动页面数据
     */
    const activityData = ref<ComponentsItem[]>([])
    const activityName = ref('')
    const activeNoMore = ref(false)
    const activeLoading = ref(false)
    let activeCurrent = 1
    const getActivity = async (loadMore = false, size = 10) => {
        if (!loadMore) activeCurrent = 1
        activeLoading.value = true
        try {
            const { records, total } = await getData('activity', activeCurrent, size, activityName.value)
            const data = (records as []) || []
            if (loadMore) {
                activityData.value.push(...data)
            } else {
                activityData.value = data
                activeNoMore.value = false
            }
            activeNoMore.value = activityData.value.length >= total

            if (!activeNoMore.value) activeCurrent++
        } catch (error) {
            console.log('error', error)
        }
        activeLoading.value = false
    }

    // 获取数据没有依赖关系可以每个函数都可以单独调用，根据自己的需求选择
    return {
        getData, // 获取数据

        getText, // 滚动获取图文页面数据 -> 配合Element-plus指令v-infinite-scroll 效果更佳
        textData,
        textName,
        textNoMore,
        textLoading,

        getActivity, // 滚动获取活动页面数据 -> 配合Element-plus指令v-infinite-scroll 效果更佳
        activityData,
        activityName,
        activeNoMore,
        activeLoading,
    }
}
