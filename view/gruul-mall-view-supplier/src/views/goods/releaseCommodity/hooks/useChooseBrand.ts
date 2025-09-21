import { getBrandList } from '@/apis/good'
import { ElMessage } from 'element-plus'

const useChooseBrand = (brandId: string) => {
    const brandName = ref('')
    const BrandList = ref<any[]>([])
    const BrandPageConfig = reactive({
        size: 1000,
        current: 1,
        total: 0,
    })
    const BrandShowName = ref('')
    const BrandRadio = ref('')
    const Searchlist = () => {
        BrandPageConfig.current = 1
        initBrandList()
    }
    const initBrandList = async () => {
        const { code, data } = await getBrandList({
            ...BrandPageConfig,
            brandName: brandName.value,
        })
        if (code !== 200) {
            ElMessage.error('获取品牌列表失败')
            return
        }
        BrandList.value = data.records
        BrandPageConfig.total = data.total
        data.records.forEach((item: any) => {
            if (item.id === brandId) {
                BrandShowName.value = item.brandName
                BrandRadio.value = item.id
            }
        })
    }
    return {
        Searchlist,
        brandName,
        BrandRadio,
        BrandList,
        initBrandList,
    }
}

export default useChooseBrand
