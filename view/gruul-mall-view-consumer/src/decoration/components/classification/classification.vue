<script setup lang="ts">
import { computed, type PropType, ref, reactive, watch } from 'vue'
import QNav from '@/components/q-nav/q-nav.vue'
import ClassSearch from './components/class-search.vue'
import SlideNav from './components/slide-nav.vue'
import ClassOne from './components/class-one.vue'
import ClassThree from './components/class-three.vue'
import ClassFive from './components/class-five.vue'
import { useStatusBar } from '@/hooks'
import { doGetCategoryLevel, doGetCommodityBySecCateId, doGetEnablePageByType } from '@/apis/decoration/shop'
import type { ApiCategoryData } from '@/basePackage/pages/merchant/types'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import type { DeCategoryType } from '@/pages/platform/types'

const $props = defineProps({
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    default() {
      return {}
    },
  },
  isO2o: {
    type: Boolean,
    defatlu: true,
  },
  decorationProperties: {
    type: Object as PropType<any>,
    default() {
      return {}
    },
  },
})
// 状态栏高度
const statusBarHeight = useStatusBar()
const headerNodeHeight = uni.upx2px(88)
// 底部导航高度(rpx)
const navNode = uni.upx2px(100)
// 底部导航安全距离高度
const safeHeight = useBottomSafe()
// 搜索组件高度(rpx)
const searchNode = uni.upx2px(90)
// 分类高度
const classNodeHeight = computed(() => {
  // 屏幕高度
  let height = 0
  // #ifdef MP-WEIXIN
  height = useScreenHeight().value - searchNode - navNode - statusBarHeight.value - safeHeight.value
  // #endif
  // #ifndef MP-WEIXIN
  height = useScreenHeight().value - searchNode - navNode - statusBarHeight.value - headerNodeHeight - safeHeight.value
  // #endif
  return height
})
// slide-nav的下标
const slideNavIndex = ref(0)
// navTab的下标
const navIndex = ref(0)
// 获取当前分类列表
const currentCategoryInfo = ref<ApiCategoryData>()
// 获取商品列表
const commodityList = ref([])
// 样式2，5商品列表
const commodity2List = ref([])
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
})
// 分页信息
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})

watch(
  $props,
  () => {
    if ($props.shopInfo.id) {
      initCategoryConfig($props.shopInfo.id)
    }
  },
  {
    immediate: true,
  },
)

const handleChangeSlideTab = async (e: number) => {
  slideNavIndex.value = e
  // 切换slide初始化数据
  commodityList.value = []
  navIndex.value = 0
  initPageConfig()
  await initfirstBelowCate()
  if (categoryConfig.value.style === 3 || categoryConfig.value.style === 4) {
    initCommodityBySecId()
  } else if (categoryConfig.value.style === 2 || categoryConfig.value.style === 5) {
    initCommodity2BySecId()
  }
}
const handleChangeNavTab = (e: number) => {
  navIndex.value = e
  initPageConfig()
  initCommodityBySecId()
}
/**
 * 样式34下分页加载商品列表
 */
const handleListLoadMore = () => {
  initCommodityBySecId(true)
}
/**
 * 样式25下分页加载商品列表
 */
const handleListLoadMore2 = () => {
  initCommodity2BySecId(true)
}
/**
 * 根据一级分类查询id查询下级分类信息
 */
async function initfirstBelowCate() {
  if (!categoryConfig.value.categoryList.length) return
  const { code, data } = await doGetCategoryLevel([categoryConfig.value.categoryList[slideNavIndex.value].id], 'LEVEL_3', $props.shopInfo.id)
  if (code !== 200)
    return uni.showToast({
      icon: 'none',
      title: '获取分类失败',
    })
  currentCategoryInfo.value = data[0]
}
/**
 * 根据二级id查询商品列表
 */
async function initCommodityBySecId(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    commodityList.value = await getGoodList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    commodityList.value = commodityList.value.concat(await getGoodList())
  }
}
/**
 * 获取分类配置数据
 */
async function initCategoryConfig(shopId: Long) {
  let data: any
  if ($props.isO2o) {
    data = $props.decorationProperties
  } else {
    // #ifdef MP-WEIXIN
    // @ts-ignore
    const endpointType = 'WECHAT_MINI_APP'
    // #endif
    // #ifndef MP-WEIXIN
    // @ts-ignore
    // eslint-disable-next-line no-redeclare
    const endpointType = 'H5_APP'
    // #endif
    const { code, data: res } = await doGetEnablePageByType(shopId, endpointType, 'SHOP_CATEGORY_PAGE')
    if (code !== 200) {
      return uni.showToast({
        icon: 'none',
        title: '获取分类配置失败',
      })
    }
    data = res
  }
  if (data && JSON.parse(data.properties).length) {
    categoryConfig.value = JSON.parse(data.properties)[0].formData
  } else {
    return uni.showToast({
      icon: 'none',
      title: '分类页暂无配置',
    })
  }
  await initfirstBelowCate()
  if (categoryConfig.value.style === 3 || categoryConfig.value.style === 4) {
    initCommodityBySecId()
  } else if (categoryConfig.value.style === 2 || categoryConfig.value.style === 5) {
    initCommodity2BySecId()
  }
}
async function getGoodList() {
  if (currentCategoryInfo.value) {
    const { code, data } = await doGetCommodityBySecCateId(currentCategoryInfo.value.children[navIndex.value].id, 'LEVEL_2', pageConfig)
    if (code !== 200) {
      return uni.showToast({
        icon: 'none',
        title: '获取商品失败',
      })
    }
    if (data) {
      pageConfig.pages = data.pages
      return data.records
    } else {
      return []
    }
  } else {
    return uni.showToast({
      icon: 'none',
      title: '分类加载失败',
    })
  }
}
/**
 * 根据二级id查询商品列表
 */
async function initCommodity2BySecId(isLoad = false) {
  if (!isLoad) {
    // 刷新
    commodity2List.value = await getGoodList2()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    commodity2List.value = commodity2List.value.concat(await getGoodList2())
  }
}
async function getGoodList2() {
  if (!currentCategoryInfo.value) {
    uni.showToast({
      icon: 'none',
      title: '分类加载失败',
    })
    return []
  }
  const { code, data } = await doGetCommodityBySecCateId(currentCategoryInfo.value.id, 'LEVEL_1', pageConfig)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return []
  }

  pageConfig.pages = data.pages
  return data.records
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
  <view>
    <q-nav title="分类" />
    <class-search :shop-id="$props.shopInfo.id" />
    <scroll-view scroll-y :style="{ height: classNodeHeight + 'px' }">
      <class-five
        v-if="categoryConfig.style === 5"
        :first-cate-list="currentCategoryInfo"
        :current-choose-index="slideNavIndex"
        :list="commodity2List"
        :config="categoryConfig"
        :height="classNodeHeight"
        :shopInfo="$props.shopInfo"
        @changeTab="handleChangeSlideTab"
        @listLoadMore="handleListLoadMore2"
      />
      <div v-else class="content">
        <SlideNav :current-choose-index="slideNavIndex" :height="classNodeHeight" :config="categoryConfig" @changeTab="handleChangeSlideTab" />
        <class-one
          v-if="categoryConfig.style === 1 || categoryConfig.style === 2"
          ref="classOneRef"
          :height="classNodeHeight"
          :info="currentCategoryInfo"
          :large="categoryConfig.style"
          :list="commodity2List"
          :shopInfo="$props.shopInfo"
          @listLoadMore="handleListLoadMore2"
        />
        <class-three
          v-else
          :current-choose-index="navIndex"
          :first-cate-list="currentCategoryInfo"
          :height="classNodeHeight"
          :config="categoryConfig"
          :list="commodityList"
          :shopInfo="$props.shopInfo"
          @listLoadMore="handleListLoadMore"
          @changeTab="handleChangeNavTab"
        />
      </div>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
@include b(content) {
  @include flex();
  background: #fff;
}
</style>
