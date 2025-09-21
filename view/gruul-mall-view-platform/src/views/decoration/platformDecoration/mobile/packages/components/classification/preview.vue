<script setup lang="ts">
import { ElMessage } from 'element-plus'
import classificationData from './classification'
import SlideNav from './components/slide-nav.vue'
import ClassListOne from './components/class-list-one.vue'
import ClassListFive from './components/class-list-five.vue'
import type { DeCategoryType, ApiCategoryData, DeCategoryItem } from './classification'
import { doGetCategoryLevel, doGetCommodityBySecCateId } from '@/apis/decoration'
import type { PropType } from 'vue'
import { useDecorationStore } from '@/store/modules/decoration'
import { LinkSelectItem } from '../../../types'

const $props = defineProps({
    formData: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return classificationData
        },
    },
})

const $decorationStore = useDecorationStore()
// 筛选后的一级及以下分类信息
const firstCategoryList = ref<DeCategoryItem[]>([])
// 二级分类信息
const secCategoryList = ref<DeCategoryItem[]>([])
// 当前slide-nav选中id
const slideChooseId = ref('')
// 样式2导航选中id
const navChoosedId = ref('')
// 商品列表
const commodityList = ref([])
// 样式25商品列表
const commodity2List = ref([])
// 标题
const titleRef = ref<HTMLElement | null>(null)
// 搜索
const searchRef = ref<HTMLElement | null>(null)
const totalHeight = computed(() => {
    return (titleRef.value?.offsetHeight || 0) + (searchRef.value?.offsetHeight || 0)
})
// 类目广告
const categoryAd = ref<{ img: string; link: LinkSelectItem }[]>()
watch(
    () => $props.formData?.categoryList,
    (newVal) => {
        if (newVal.length > 0 && newVal[0].platformCategoryFirstId !== '') {
            console.log(newVal)
            slideChooseId.value = newVal[0].platformCategoryFirstId
            categoryAd.value = newVal[0].ads || []
            filterCategory(newVal)
        } else {
            initData()
        }
    },
    {
        deep: true,
        immediate: true,
    },
)
watch(
    () => $props.formData.style,
    (newVal) => {
        if ((newVal === 2 || newVal === 5) && slideChooseId.value) {
            initCommodity()
        }
    },
    {
        deep: true,
    },
)

/**
 * slide切换
 * @param {*} e
 */
const handleChangeTab = (e: { index: number; firstLevelId: string }) => {
    slideChooseId.value = e.firstLevelId
    secCategoryList.value = $props.formData.categoryList.find((item) => item.platformCategoryFirstId === e.firstLevelId)?.children || []
    categoryAd.value = $props.formData.categoryList.find((item) => item.platformCategoryFirstId === e.firstLevelId)?.ads || []
    if ($props.formData.style === 2 || $props.formData.style === 5) {
        initCommodity(secCategoryList.value[0].platformCategorySecondId)
    }
}
/**
 * 样式2切换导航回调
 */
const handleChangeNav = (e: { index: number; id: string }) => {
    initCommodity(e.id)
    navChoosedId.value = e.id
}
/**
 * 通过一级分类查询对应商品
 */
async function filterCategory(firstLevelArr: DeCategoryType['categoryList']) {
    // 取出所有一级分类id
    firstCategoryList.value = firstLevelArr
    // 初始化slidenav默认选中
    if (firstCategoryList.value.length && firstCategoryList.value[0].platformCategoryFirstId !== '') {
        secCategoryList.value = firstCategoryList.value.find((item) => item.platformCategoryFirstId === slideChooseId.value)?.children || []
        // 新增：进入页面时自动加载第一个二级类目的商品
        if ($props.formData.style === 2 || $props.formData.style === 5) {
            const firstSecId = secCategoryList.value[0]?.platformCategorySecondId
            if (firstSecId) {
                initCommodity(firstSecId)
                navChoosedId.value = firstSecId
            }
        }
    }
}

async function initCommodity(secNavId?: string) {
    const id = filterSecChoosedId(secNavId)
    if (!id) {
        commodityList.value = []
        navChoosedId.value = ''
        ElMessage.warning('暂无二级分类')
        return
    }
    const { code, data } = await doGetCommodityBySecCateId(id, 'LEVEL_2')
    if (code !== 200) return ElMessage.error('获取商品信息失败')
    if (!data) {
        commodityList.value = []
        return ElMessage.error('当前分类下暂无商品')
    }
    commodityList.value = data.records
}

/**
 * 筛选出样式2中nav选中id
 * @param {string} slideNavId 一级id
 * @param {string} secNavId 二级id
 */
function filterSecChoosedId(secNavId?: string) {
    if (!secNavId) {
        const filterFirstList = firstCategoryList.value.filter((item) => item.platformCategoryFirstId === slideChooseId.value)
        if (filterFirstList[0].children) {
            // 筛选出一级对象
            const currentChooseId = filterFirstList[0].children[0].platformCategorySecondId
            navChoosedId.value = currentChooseId
            return currentChooseId
        } else {
            return null
        }
    } else {
        return secNavId
    }
}
/**
 * 初始化
 */
function initData() {
    firstCategoryList.value = []
    secCategoryList.value = []
    commodityList.value = []
    commodity2List.value = []
    navChoosedId.value = ''
    slideChooseId.value = ''
}
</script>

<template>
    <div style="min-height: 300px; background-color: #f4f4f4">
        <div
            v-if="$decorationStore.getEndpointType !== 'WECHAT_MINI_APP' && $props.formData.classificationTitle"
            ref="titleRef"
            class="classification__title"
        >
            {{ $props.formData.classificationTitle }}
        </div>
        <div v-if="$props.formData.searchShow === 2" ref="searchRef" class="classification__search">
            <van-search disabled placeholder="请输入搜索关键词" shape="round" />
        </div>
        <class-list-five
            v-if="$props.formData.style === 5"
            :category-list="firstCategoryList"
            :second-category-list="secCategoryList"
            :current-choose-id="slideChooseId"
            :goods-show="$props.formData.goodsShow"
            :list="commodityList"
            :total-height="totalHeight"
            :config="$props.formData"
            @change-tab="handleChangeTab"
            @change-nav="handleChangeNav"
        />
        <div v-else style="display: flex; background: #f4f4f4">
            <slide-nav
                :current-choose-id="slideChooseId"
                :config="$props.formData"
                :category-list="firstCategoryList"
                :total-height="totalHeight"
                @change-tab="handleChangeTab"
            />
            <class-list-one
                v-if="($props.formData.style === 1 || $props.formData.style === 2) && slideChooseId"
                :second-category-list="$props.formData.categoryList.find((item) => item.platformCategoryFirstId === slideChooseId)?.children || []"
                :large="$props.formData.style"
                :list="commodityList"
                :total-height="totalHeight"
                :config="$props.formData"
                :category-ad="categoryAd"
                @change-nav="handleChangeNav"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.classification__title {
    display: inline-block;
    width: 370px;
    font-size: 18px;
    font-weight: 500;
    color: #333;
    font-family: PingFang SC;
    text-align: center;
    height: 30px;
    background-color: #fff;
}
.classification__search {
    background-color: #fff;
    height: 50px;
    display: block;
    display: flex;
    align-items: center;
    justify-content: center;
    .van-search {
        padding: 0;
        width: 94%;
    }
}
</style>
