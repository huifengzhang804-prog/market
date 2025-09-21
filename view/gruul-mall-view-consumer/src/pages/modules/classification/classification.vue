<script setup lang="ts">
import QNav from '@/components/q-nav/q-nav.vue'
import ClassSearch from './components/class-search.vue'
import SlideNav from './components/slide-nav.vue'
import ClassOne from './components/class-one.vue'
import ClassFive from './components/class-five.vue'
import { useStatusBar } from '@/hooks'
import { useSettingStore } from '@/store/modules/setting'
import { doGetEnablePageByType } from '@/apis/decoration/platform'
import { doGetCategoryLevelFromPlatform } from '@/apis/decoration/platform'
import type { DeCategoryType, ApiCategoryData } from '@/pages/platform/types'
import { doGetRetrieveCommodity } from '@/apis/good'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import type { RetrieveCommodity } from '@/apis/good/model'
import NoDecoration from '@/components/no-decoration/index.vue'
import { throttle } from 'lodash'

const pages = getCurrentPages()
const { SET_LOADING } = useSettingStore()
// 状态栏高度
const statusBarHeight = useStatusBar()
let headerNodeHeight = uni.upx2px(20)
// #ifndef H5
headerNodeHeight = uni.upx2px(100)
// #endif
// 底部导航安全距离高度
const safeHeight = useBottomSafe()
// 底部导航高度(rpx)
const tabbarNode = uni.upx2px(100)

// slide-nav的下标
const slideNavIndex = ref(0)
// navTab的下标
const navIndex = ref(0)
// 获取当前分类列表
const currentCategoryInfo = ref<ApiCategoryData>()
// 获取商品列表
const commodityList = ref<RetrieveCommodity[]>([])
// 样式2，5商品列表
const commodity2List = ref<RetrieveCommodity[]>([])
// 分类页配置
const categoryConfig = ref<DeCategoryType>({
  style: 1,
  listStyle: 1,
  buyBtnStyle: 1,
  goodsMargin: 0,
  fontSelected: '#FF0000',
  bgSelected: '#0E0202',
  fontNotSelected: '#000000',
  bgNotSelected: '#F5F5F5',
  categoryList: [],
  goodsShow: [],
  searchShow: 0,
  commodityShow: 1,
  classificationTitle: '',
  navShow: true,
  categoryShow: true,
})
// 分页信息
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
  total: 0,
})

const classOneRef = ref()
const isShowNoDecoration = ref(false)

// 分类标题
const classTitleRef = ref()

// 搜索模块
const headerSearchRef = ref()

// 标题高度
const classTitleHeight = ref(0)
// 搜索模块高度
const headerSearchHeight = ref(uni.upx2px(68))

// nav高度
const navHeight = ref(87)

// 获取两个模块高度的方法
const getModuleHeights = () => {
  nextTick(() => {
    // 获取分类标题高度
    if (classTitleRef.value) {
      const query = uni.createSelectorQuery().in(classTitleRef.value)
      query
        .select('.class-title')
        .boundingClientRect((data: any) => {
          if (data && data.height) {
            classTitleHeight.value = data.height
            console.log('分类标题高度:', classTitleHeight.value)
          }
        })
        .exec()
    }

    // 获取搜索模块高度
    if (headerSearchRef.value) {
      const query = uni.createSelectorQuery().in(headerSearchRef.value)
      query
        .select('.header-search')
        .boundingClientRect((data: any) => {
          if (data && data.height) {
            headerSearchHeight.value = data.height
            console.log('搜索模块高度:', headerSearchHeight.value)
          }
        })
        .exec()
    }
  })
}

// 在组件挂载后获取高度
onMounted(() => {
  getModuleHeights()
})

// 分类高度
const classNodeHeight = computed(() => {
  // 屏幕高度
  let height = 0
  // #ifdef H5
  height =
    useScreenHeight().value -
    (categoryConfig.value.searchShow === 2 ? headerSearchHeight.value : 0) -
    tabbarNode -
    statusBarHeight.value -
    safeHeight.value -
    (categoryConfig.value.classificationTitle ? classTitleHeight.value : 0)
  // #endif
  // #ifndef H5
  height =
    useScreenHeight().value -
    (categoryConfig.value.searchShow === 2 ? headerSearchHeight.value : 0) -
    tabbarNode -
    statusBarHeight.value -
    safeHeight.value
  // #endif
  return height
})

initCategoryConfig()

const handleChangeSlideTab = async (e: number) => {
  slideNavIndex.value = e
  // 切换slide初始化数据
  commodityList.value = []
  navIndex.value = 0
  classOneRef.value?.handleBackTop()
  // 重置滚动到底部状态
  isReachedBottom.value = false
  initPageConfig()
  await initfirstBelowCate()
  if (categoryConfig.value.style === 2 || categoryConfig.value.style === 5) {
    initCommodity2BySecId()
  }
}

const handleChangeTwoTab = (e: string, index: number) => {
  initPageConfig()
  initCommodity2BySecId(true, e)
}

const handleChangeCategory = async (id: string) => {
  initPageConfig()
  commodity2List.value = await getGoodList2(id)
}

// 添加状态来跟踪是否已经滚动到底部，用于"再次上拉切换"逻辑
const isReachedBottom = ref(false)

const handleScrollToNextCategory = throttle((forceImmediateSwitch = false) => {
  // #ifndef H5
  if (slideNavIndex.value < categoryConfig.value.categoryList.length - 1) {
    if (forceImmediateSwitch) {
      // 强制立即切换下一个分类
      setTimeout(() => {
        slideNavIndex.value++
        handleChangeSlideTab(slideNavIndex.value)
        // 重置状态
        isReachedBottom.value = false
      }, 1000)
    } else {
      // 应用"再次上拉切换"逻辑
      // eslint-disable-next-line no-lonely-if
      if (isReachedBottom.value) {
        setTimeout(() => {
          slideNavIndex.value++
          handleChangeSlideTab(slideNavIndex.value)
          // 重置状态
          isReachedBottom.value = false
        }, 1000)
      } else {
        // 第一次滚动到底部，设置状态但不切换
        isReachedBottom.value = true
      }
    }
  } else {
    uni.showToast({ icon: 'none', title: '已经是最后一个分类' })
  }
  // #endif
}, 1000)

/**
 * 样式25下分页加载商品列表
 */
const handleListLoadMore2 = (liveTwoId?: string, isSwitch = false) => {
  initCommodity2BySecId(true, liveTwoId)
  console.log(commodity2List.value.length, pageConfig.total)
  if (isSwitch && (commodity2List.value.length === 0 || commodity2List.value.length === pageConfig.total)) {
    handleScrollToNextCategory(true)
  }
}
/**
 * 根据一级分类查询id查询下级分类信息
 */
async function initfirstBelowCate() {
  if (categoryConfig.value?.categoryList?.[slideNavIndex.value]?.platformCategoryFirstId) {
    const { code, data } = await doGetCategoryLevelFromPlatform(
      [categoryConfig.value.categoryList[slideNavIndex.value].platformCategoryFirstId!],
      'LEVEL_3',
    )
    if (code !== 200)
      return uni.showToast({
        icon: 'none',
        title: '获取分类失败',
      })
    if (data[0]) {
      const children = categoryConfig.value?.categoryList?.[slideNavIndex.value].children
      if (data[0]) {
        currentCategoryInfo.value = data[0]

        // 递归过滤函数：根据配置过滤子分类及其children
        const filterChildrenRecursively = (dataChildren: ApiCategoryData[], configChildren: any[]): ApiCategoryData[] => {
          return (
            dataChildren?.filter((child: ApiCategoryData) => {
              // 检查当前层级是否在配置中存在
              const isInConfig = configChildren?.some((item: any) => item.platformCategorySecondId === child.id)

              if (isInConfig && child.children) {
                // 如果当前分类在配置中且有自己的children，递归过滤其children
                const configChildItem = configChildren?.find((item: any) => item.platformCategorySecondId === child.id)
                if (configChildItem?.children) {
                  // 根据三级分类ID过滤children
                  child.children = child.children.filter((subChild: ApiCategoryData) =>
                    configChildItem.children.some((configSubItem: any) => configSubItem.platformCategoryThirdId === subChild.id),
                  )
                }
              }

              return isInConfig
            }) || []
          )
        }

        // 过滤出在children配置中存在的子分类，并递归处理其children
        const filteredChildren = filterChildrenRecursively(data[0].children || [], children || [])

        if (currentCategoryInfo.value) {
          currentCategoryInfo.value.children = filteredChildren
        }
      } else {
        currentCategoryInfo.value = void 0
      }
    }
  }
}

async function getGoodList() {
  if (!currentCategoryInfo.value) {
    uni.showToast({
      icon: 'none',
      title: '分类加载失败',
    })
    return []
  }
  const { code, data } = await doGetRetrieveCommodity({
    platformCategorySecondId: currentCategoryInfo.value?.children?.[navIndex.value].id,
    current: pageConfig.current,
    size: pageConfig.size,
  })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return []
  }
  pageConfig.pages = data.pages
  return data.list
}
/**
 * 根据二级id查询商品列表
 */
async function initCommodity2BySecId(isLoad = false, liveTwoId?: string) {
  isShowNoDecoration.value = true
  if (!isLoad) {
    // 刷新
    commodity2List.value = await getGoodList2()
  } else if (isLoad && pageConfig.current < pageConfig.pages && liveTwoId) {
    // 更新
    pageConfig.current++
    commodity2List.value = commodity2List.value.concat(await getGoodList2(liveTwoId))
  } else if (isLoad && liveTwoId && pageConfig.current === 1) {
    commodity2List.value = await getGoodList2(liveTwoId)
  }
}
async function getGoodList2(liveTwoId?: string) {
  if (!currentCategoryInfo.value) {
    uni.showToast({
      icon: 'none',
      title: '分类加载失败',
    })
    return []
  }
  const platformCategorySecondId = liveTwoId || currentCategoryInfo.value.children?.[0]?.id
  const { code, data } = await doGetRetrieveCommodity({
    platformCategoryFirstId: currentCategoryInfo.value.id,
    platformCategorySecondId: platformCategorySecondId,
    current: pageConfig.current,
    size: pageConfig.size,
    showCoupon: true,
    showSku: true,
  })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return []
  }

  pageConfig.pages = data.pages
  pageConfig.total = data.total
  return data.list
}
/**
 * 获取分类配置数据
 */
async function initCategoryConfig() {
  // #ifdef MP-WEIXIN
  // @ts-ignore
  const endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  // @ts-ignore
  // eslint-disable-next-line no-redeclare
  const endpointType = 'H5_APP'
  // #endif
  const { code, data } = await doGetEnablePageByType(endpointType, 'PRODUCT_CATEGORY_PAGE')
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取分类配置失败',
    })
    return
  }
  if (!data || !data.properties || JSON.parse(data.properties).length === 0) {
    uni.showToast({
      icon: 'none',
      title: '分类页暂无配置',
    })
    return
  }
  categoryConfig.value = JSON.parse(data.properties)[0].formData
  await initfirstBelowCate()
  if (categoryConfig.value.style === 2 || categoryConfig.value.style === 5) {
    initCommodity2BySecId(true, categoryConfig.value.categoryList[0]?.children[0].platformCategorySecondId)
  }
  SET_LOADING(false)

  // 配置更新后重新获取模块高度
  setTimeout(() => {
    getModuleHeights()
  }, 100)
}
/**
 * 初始化分页信息
 */
function initPageConfig() {
  pageConfig.current = 1
  pageConfig.pages = 1
}
</script>

<template>
  <!-- #ifdef APP-PLUS -->
  <q-nav
    v-if="pages[pages.length - 1].route !== 'pages/modules/classification/classification'"
    :show-back="false"
    bgColor="#f4f4f4"
    :title="categoryConfig.classificationTitle"
  />
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <q-nav
    v-if="pages[pages.length - 1].route !== 'pages/modules/classification/classification' && categoryConfig.searchShow !== 2"
    :show-back="false"
    bgColor="#f4f4f4"
    padding-bottom="11rpx"
  >
    <template #title>
      <view class="search-box">
        <class-search v-if="categoryConfig.searchShow === 4" style="width: 74%" bgColor="#f4f4f4" />
      </view>
    </template>
  </q-nav>
  <q-nav
    v-else-if="
      pages[pages.length - 1].route !== 'pages/modules/classification/classification' &&
      categoryConfig.searchShow !== 4 &&
      categoryConfig.classificationTitle
    "
    :show-back="false"
    :title="categoryConfig.classificationTitle"
  />
  <!-- #endif -->

  <template v-if="categoryConfig?.categoryList?.length > 0">
    <!-- #ifdef H5 -->
    <view v-if="categoryConfig.classificationTitle" ref="classTitleRef" class="class-title">{{ categoryConfig.classificationTitle }}</view>
    <!-- #endif -->
    <view v-if="categoryConfig.searchShow === 2" ref="headerSearchRef" class="header-search">
      <class-search style="width: 100%" bgColor="#f4f4f4" />
    </view>

    <!-- TODO:待完善上拉加载和下拉刷新  -->
    <ClassFive
      v-if="categoryConfig.style === 5"
      :first-cate-list="currentCategoryInfo"
      :current-choose-index="slideNavIndex"
      :list="commodity2List"
      :config="categoryConfig"
      :height="classNodeHeight"
      @change-tab="handleChangeSlideTab"
      @change-TwoTab="handleChangeTwoTab"
      @list-load-more="handleListLoadMore2"
    />
    <view v-else class="content">
      <slide-nav :current-choose-index="slideNavIndex" :height="classNodeHeight" :config="categoryConfig" @change-tab="handleChangeSlideTab" />
      <class-one
        v-if="categoryConfig.style === 1 || categoryConfig.style === 2"
        ref="classOneRef"
        :slideNavIndex="slideNavIndex"
        :height="classNodeHeight"
        :info="currentCategoryInfo"
        :large="categoryConfig.style"
        :list="commodity2List"
        :config="categoryConfig"
        :page-config="pageConfig"
        :goods-show="categoryConfig.goodsShow"
        @list-load-more="handleListLoadMore2"
        @change-category="handleChangeCategory"
        @scrolltolower="handleScrollToNextCategory"
      />
    </view>
  </template>
  <template v-if="isShowNoDecoration">
    <NoDecoration v-if="!categoryConfig || categoryConfig?.categoryList?.length <= 0" />
  </template>
</template>

<style lang="scss" scoped>
@include b(content) {
  display: flex;
}
@include b(class-title) {
  background-color: #f4f4f4;
  width: 100%;
  text-align: center;
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  line-height: 40px;
}
@include b(search-box) {
  width: 100%;
  display: flex;
  justify-content: start;
  background-color: #f4f4f4;
}
@include b(header-search) {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3px 0 20rpx 0;
  background-color: #f4f4f4;
  height: 68rpx;
}

@include b(debug-info) {
  background-color: #f0f0f0;
  padding: 10rpx 20rpx;
  margin: 10rpx 0;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;

  text {
    display: block;
    margin: 5rpx 0;
  }
}
</style>
