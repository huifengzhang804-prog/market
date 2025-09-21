<script setup lang="ts">
import { ElMessage } from 'element-plus'
import classificationData from './classification'
import SlideNav from './components/slide-nav.vue'
import ClassListOne from './components/class-list-one.vue'
import ClassListThree from './components/class-list-three.vue'
import ClassListFive from './components/class-list-five.vue'
import type { DeCategoryType, ApiCategoryData } from './classification'
import { doGetCategoryLevel, doGetCommodityBySecCateId } from '@/apis/decoration'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return classificationData
        },
    },
})
// 筛选后的一级及以下分类信息
const firstCategoryList = ref<ApiCategoryData[]>([])
// 二级分类信息
const secCategoryList = ref<ApiCategoryData[]>([])
// 当前slide-nav选中id
const slideChooseId = ref('')
// 样式2导航选中id
const navChoosedId = ref('')
// 商品列表
const commodityList = ref([])
// 样式25商品列表
const commodity2List = ref([])

watch(
    () => $props.formData,
    (newVal) => {
        const categoryList = newVal?.categoryList
        if (categoryList?.length > 0) {
            filterCategory(categoryList)
            initData()
        } else {
            initData()
        }
    },
    {
        immediate: true,
    },
)
watch(
    () => $props.formData.style,
    (newVal) => {
        if ((newVal === 2 || newVal === 5) && slideChooseId.value) {
            initCommodity2()
        } else if (slideChooseId.value) {
            initCommodity()
        }
    },
    {
        immediate: true,
    },
)

/**
 * slide切换
 * @param {*} e
 */
const handleChangeTab = (e: { index: number; firstLevelId: string }) => {
    slideChooseId.value = e.firstLevelId
    if ($props.formData.style === 1) {
        // 样式1加载二三级信息
        secCategoryList.value = filterSecArrByFirstId(firstCategoryList.value, e.firstLevelId)
    } else if ($props.formData.style === 2 || $props.formData.style === 5) {
        // 样式2,5加载二三级信息 和商品信息
        secCategoryList.value = filterSecArrByFirstId(firstCategoryList.value, e.firstLevelId)
        initCommodity2()
    } else {
        // 其余样式加载商品
        const tempfirstCateList = firstCategoryList.value.filter((item) => item.id === e.firstLevelId)
        if (tempfirstCateList[0].children) {
            secCategoryList.value = tempfirstCateList[0].children
            initCommodity()
        }
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
    const firstLevelIds = firstLevelArr.map((item) => item.id)
    // 选中样式对应层级查询
    const level = $props.formData.style === 1 || $props.formData.style === 2 ? 'LEVEL_3' : 'LEVEL_2'
    const { code, data } = await doGetCategoryLevel(firstLevelIds, level)
    if (code !== 200) return ElMessage.error('获取分类失败')
    const filterCategoryArr = filterAvailbleFirstClass(firstLevelArr, data)
    firstCategoryList.value = filterCategoryArr

    // 初始化slidenav默认选中
    if (!slideChooseId.value && filterCategoryArr.length) {
        slideChooseId.value = filterCategoryArr[0].id
        secCategoryList.value = filterSecArrByFirstId(filterCategoryArr, filterCategoryArr[0].id)
    }
    if (($props.formData.style === 2 || $props.formData.style === 5) && filterCategoryArr.length) {
        initCommodity2()
    } else if ($props.formData.style !== 1 && filterCategoryArr.length) {
        initCommodity()
    }
}
/**
 * 根据一级id筛选出二级数组
 * @param {ApiCategoryData[]} firstCateArr
 * @param {string} firstCateId
 */
function filterSecArrByFirstId(firstCateArr: ApiCategoryData[], firstCateId: string) {
    const filterResult = firstCateArr.filter((item) => {
        return item.id === firstCateId
    })
    if (filterResult.length && filterResult[0].children) {
        return filterResult[0].children
    } else {
        return []
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
        return ElMessage.error('当前分类下暂无商品')
    }
    commodityList.value = data.records
}
async function initCommodity2() {
    const { code, data } = await doGetCommodityBySecCateId(slideChooseId.value, 'LEVEL_1')
    if (code !== 200) return ElMessage.error('获取商品信息失败')
    if (!data) {
        return ElMessage.error('当前分类下暂无商品')
    }
    commodity2List.value = data.records
}
/**
 * 筛选出可用一级分类
 */
function filterAvailbleFirstClass(recordCategory: DeCategoryType['categoryList'], category: ApiCategoryData[]) {
    if (!category.length) return []
    const categoryIdArr = recordCategory.map((item) => item.id)
    const filterArr = category.filter((item) => {
        return categoryIdArr.includes(item.id)
    })
    return filterArr
}
/**
 * 筛选出样式2中nav选中id
 * @param {string} slideNavId 一级id
 * @param {string} secNavId 二级id
 */
function filterSecChoosedId(secNavId?: string) {
    const categoryList = firstCategoryList.value
    if (!secNavId) {
        const filterFirstList = categoryList.filter((item) => item.id === slideChooseId.value)
        if (filterFirstList[0].children) {
            // 筛选出一级对象
            const currentChooseId = filterFirstList[0].children[0].id
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
    <div style="padding: 5px 15px">
        <div style="display: flex">
            <img class="logo" src="https://qiniu-app.qtshe.com/u391.png" />
            <div class="shopinfo">
                <div class="title">
                    <div style="font-weight: 700; font-size: 16px; display: flex; align-items: center">
                        店铺名称
                        <div class="tag">自营</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="tip">公告：公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息 公告信息</div>
    </div>
    <van-search disabled placeholder="请输入搜索关键词" shape="round" />
    <class-list-five
        v-if="$props.formData.style === 5"
        :category-list="firstCategoryList"
        :second-category-list="secCategoryList"
        :current-choose-id="slideChooseId"
        :list="commodity2List"
        @change-tab="handleChangeTab"
    />
    <div v-else style="display: flex">
        <slide-nav :current-choose-id="slideChooseId" :config="$props.formData" :category-list="firstCategoryList" @change-tab="handleChangeTab" />
        <class-list-one
            v-if="$props.formData.style === 1 || $props.formData.style === 2"
            :second-category-list="secCategoryList"
            :large="$props.formData.style"
            :list="commodity2List"
        />
        <class-list-three
            v-else-if="$props.formData.style === 3 || $props.formData.style === 4"
            :current-choose-id="navChoosedId"
            :config="$props.formData"
            :second-category-list="secCategoryList"
            :list="commodityList"
            @change-nav="handleChangeNav"
        />
    </div>
</template>

<style lang="scss" scoped>
.logo {
    width: 60px;
    height: 60px;
    margin-right: 10px;
}
.shopinfo {
    flex: 1;
    .title {
        display: flex;
        justify-content: space-between;
        align-items: center;
        .tag {
            font-weight: 400;
            font-size: 12px;
            padding: 1px 4px;
            background-color: #fb232f;
            color: #fff;
            margin-left: 5px;
            border-radius: 5px;
        }
    }
}
.tip {
    font-size: 14px;
    color: #ff5c53;
    margin-top: 6px;
    @include utils-ellipsis(2);
}
</style>
