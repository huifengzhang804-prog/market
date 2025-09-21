<script setup lang="ts">
import { ref, onMounted, type PropType, reactive, nextTick, watch, computed } from 'vue'
import type { GoodFormData, FirstCateItem } from '../types'
import { doGetRetrieveCommodity } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
// import { cropImg } from '@/utils'
import styleOne from './good-template/style-one.vue'
import styleThree from './good-template/style-three/index.vue'
import styleFour from './good-template/style-four/index.vue'
import { onUnload, onLoad } from '@dcloudio/uni-app'
import type { GoodItemType } from './good'
import GoodItemFoot from './good-template/good-item-foot.vue'
import { cloneDeep } from 'lodash'

const lazyLoadRef = ref()
const loadingImg = ref('')

enum ListStyle {
  '大图' = 'goods-style--one', // 大图
  '一行两个' = 'goods-style--two', // 一行两个
  '详细列表' = 'goods-style--three', // 详细列表
  '横向滑动' = 'goods-style--four', // 横向滑动
  '瀑布流' = 'goods-style--five', // 瀑布流
}
type SearchConfig = {
  platformCategoryFirstId: string | null
  categoryFirstId: string | null
  productIds: Long[] | null
}
// TODO: 商品装修样式边缘线和阴影配置
// TODO: 商品组件加载分页
// TODO: 添加商品售罄

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<GoodFormData>,
    default: () => {},
  },
})
/**
 * 检索条件
 * @param platformCategoryFirstId 平台分类id检索
 * @param categoryFirstId 商家id检索
 * @param ids 选中商品id检索
 */
const searchConfig = reactive<SearchConfig>({
  platformCategoryFirstId: null,
  categoryFirstId: null,
  productIds: null,
})
const isPlatform = ref(true)
const formData = ref<GoodFormData>({
  /** 1展示分类  2不展示分类 */
  ponentType: 1,
  /** 商品 */
  goods: [],
  /** 类目样式 1新  2下划线 */
  // titleStyle: 1,
  /** 列表样式 */
  listStyle: ListStyle['大图'],
  /** 页面边距 */
  // pagePadding: 0,
  /** 商品间距 */
  // goodPadding: 0,
  /** 商品样式 */
  // goodsStyle: '',
  /** 图片倒角 */
  // angle: '',
  /** 文本样式 */
  // textStyle: '',
  /** 显示内容 */
  showContent: {
    /** 商品名称 */
    // nameShow: true,
    /** 商品价格 */
    // priceShow: true,
    /** 购物按钮 */
    // buttonShow: true,
    /** 购物按钮样式 */
    buttonStyle: 1,
    /** 购物车按钮文案 */
    buttonText: '',
    /** 商品角标 */
    tagShow: true,
    /** 商品角标样式 */
    tagStyle: 1,
  },
  /** 商品对象 */
  goodsItem: {},
  /** 商品数量 */
  goodsCount: 0,
  firstCatList: [],
  extraConfig: {
    coupon: false,
    freeMail: false,
    historyData: false,
    memberTag: '',
    sellPointDesc: false,
    shopName: false,
    shopTag: false,
  },
  goodsNameConfig: {
    showTag: false,
    rows: 0,
  },
})
const currentCategoryId = ref('-1')
const GoodsCornerMark = ref({
  // 商品角标地址及样式
  url: '',
  class: '',
})
const goodsList = ref<GoodItemType[]>([])
const GoodsNameClass = ref('')
const ClassBugCard3 = ref('')
const GoodsItemStyle = ref({})
const GoodsLastStyle = ref({})
const scrollXLeft = ref(0)
const scrollViewRef = ref(null)

onMounted(async () => {
  loadingImg.value = lazyLoadRef.value.loadingImg
  const decoretionProperties = $props.decorationProperties
  judgePlatform(decoretionProperties.firstCatList)
  formData.value = decoretionProperties
  try {
    if (decoretionProperties.ponentType === 1) {
      filterCategory($props.decorationProperties.firstCatList)
    } else if (decoretionProperties.ponentType === 2) {
      goodsList.value = decoretionProperties.goods
      const { data } = await doGetRetrieveCommodity({
        productId: goodsList.value.map((item) => item.productId),
        size: goodsList.value.length,
        showCoupon: $props.decorationProperties.extraConfig?.coupon || false,
        showHistory: $props.decorationProperties.extraConfig?.historyData || false,
      })
      const list = data?.list || []
      goodsList.value = goodsList.value.map((item) => {
        const findOptions = list.find((li: any) => li.productId === item.productId)
        if (findOptions) {
          item.salePrices = findOptions.salePrices || []
          item.freightTemplateId = findOptions.freightTemplateId || 0
          item.couponList = findOptions.couponList || []
          item.saleDescribe = findOptions.saleDescribe || ''
          item.productLabel = findOptions.productLabel || {}
          item.shopType = findOptions.shopType ?? item.shopType
          item.shopOperationHistory = findOptions.shopOperationHistory || ''
          item.memberInfo = findOptions?.memberInfo ?? item.memberInfo
          item.productName = findOptions?.productName || item.productName || ''
        }
        return item
      })
    }
  } finally {
    getGoodsCornerMark(decoretionProperties)
    getClassBugCard3(decoretionProperties)
    getGoodsItemStyle(decoretionProperties)
    getGoodsLastStyle(decoretionProperties)
  }

  // 在挂载完成后监听scrolltolower事件
  uni.$on('scrolltolower', async () => {
    if (loadmoreStatus.value === 'loadmore' && showClassification.value) {
      pagesInfo.value.page++
      await nextTick()
      getGoodList()
    }
  })
})
/**
 * 判断是平台组件还是商家组件
 */
function judgePlatform(cate: FirstCateItem[]) {
  if (!cate.length) return true
  isPlatform.value = cate[0].platformCategoryFirstId ? true : false
}

/**
 * 根据平台或商家分类检索商品
 */
async function filterCategory(cate: FirstCateItem[]) {
  if (!cate.length) return
  unifyCategory(cate[0].id || cate[0].platformCategoryFirstId)
  clearWaterFall()
  getGoodList()
}
/**
 * 统一处理平台和商家分类检索赋值
 */
function unifyCategory(cateId: string | undefined) {
  if (isPlatform.value) {
    searchConfig.platformCategoryFirstId = cateId || null
  } else {
    searchConfig.categoryFirstId = cateId || null
  }
  currentCategoryId.value = cateId || '-1'
}
const loadmoreStatus = ref('loadmore')
const pagesInfo = ref({
  page: 1,
  size: 20,
})
const clearWaterFall = () => {
  pagesInfo.value.page = 1
  waterFallRef.value?.clear()
}
/**
 * 获取商品
 */
async function getGoodList(flag?: boolean) {
  loadmoreStatus.value = 'loading'
  const { code, data } = await doGetRetrieveCommodity({
    ...searchConfig,
    size: pagesInfo.value.size,
    current: pagesInfo.value.page,
    searchTotalStockGtZero: true,
    showCoupon: $props.decorationProperties.extraConfig?.coupon || false,
    showHistory: $props.decorationProperties.extraConfig?.historyData || false,
    orderByParams: [{ sort: 'DESC', order: 'createTime' }],
  })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return
  }
  const tempArr = data.list.map((item: any) => {
    const {
      productId,
      productName,
      pic,
      salePrices,
      shopId,
      freightTemplateId = '',
      couponList = [],
      saleDescribe = '',
      productLabel = {},
      shopType = '',
      shopOperationHistory = '',
      memberInfo = {},
      shopName = '',
    } = item
    return {
      ...item,
      productId,
      productName,
      pic,
      salePrices,
      shopId,
      freightTemplateId,
      couponList,
      saleDescribe,
      productLabel,
      shopType,
      shopOperationHistory,
      memberInfo,
      shopName,
    }
  })
  if (flag) {
    goodsList.value = [...tempArr]
  } else {
    goodsList.value = [...goodsList.value, ...tempArr]
  }
  await nextTick()
  setTimeout(() => {
    // goodsList.value = batchFileImg(tempArr)
    if (data.total <= goodsList.value.length) {
      loadmoreStatus.value = 'nomore'
    } else {
      loadmoreStatus.value = 'loadmore'
    }
  }, 500)
}

// function batchFileImg(list: any[]) {
//     return list.map((item) => {
//         item.pic = cropImg(item.pic, 380, 380)
//         return item
//     })
// }
/**
 * 获取商品角标地址及样式
 */
function getGoodsCornerMark(formData: GoodFormData) {
  const { tagStyle } = formData.showContent
  // 商品角标 1新品 2热卖 3抢购
  const srcs = [
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/37a28e49acb448fc8618d5e72851b027.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/1622a28ef72040f9a2f367a2efa7c32d.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/0f33444422b14a8e980cc50d0011e2d0.png',
  ]
  const styles = ['goods-item__coner1', 'goods-item__coner2', 'goods-item__coner3']
  GoodsCornerMark.value = {
    url: srcs[+tagStyle - 1],
    class: styles[+tagStyle - 1],
  }
}
const waterFallRef = ref()

onLoad(() => {
  uni.$on('首页', () => {
    clearWaterFall()
    getGoodList()
  })
})

onUnload(() => {
  uni.$off('首页')
  uni.$off('scrolltolower')
})

/**
 * 购买按钮样式
 */
function getClassBugCard3(formData: GoodFormData) {
  const {
    listStyle,
    showContent: { buttonStyle },
  } = formData
  let ClassBugCardTr = ''

  if (buttonStyle === 1 && listStyle === ListStyle['横向滑动']) {
    ClassBugCardTr = 'goods-buy__cart3'
  }
  if (buttonStyle === 2 && listStyle === ListStyle['横向滑动']) {
    ClassBugCardTr = 'goods-buy__cart3 buy-icon2'
  }

  ClassBugCard3.value = ClassBugCardTr
}
/**
 * 商品item样式
 */
function getGoodsItemStyle(formData: GoodFormData) {
  const { listStyle } = formData
  let margin = ''
  let width = ''
  if (listStyle === ListStyle['横向滑动']) {
    margin = `0 0 0 ${10}px`
  } else {
    margin = `0 0 ${10}px 0`
  }

  if (listStyle === ListStyle['一行两个']) {
    width = `calc(50% - ${10 / 2}px)`
  } else {
    width = ''
  }

  GoodsItemStyle.value = {
    margin,
    width,
  }
}
const goodsListCopy = ref<GoodItemType[]>([])
watch(
  () => goodsList.value,
  (val: GoodItemType[]) => {
    goodsListCopy.value = cloneDeep(val)
  },
  {
    deep: true,
    immediate: true,
  },
)
/**
 * 最后一个商品样式
 */
function getGoodsLastStyle(formData: GoodFormData) {
  const { listStyle } = formData
  let margin = ''
  if (listStyle === ListStyle['一行两个']) {
    margin = `margin: 0px 0px ${10}px 0px;`
  } else {
    margin = ''
  }
  GoodsLastStyle.value = {
    margin,
  }
}
/**
 * 切换分类
 * @param {string} categoryId
 */
const handleChangeCategory = (categoryId: string | undefined) => {
  scrollXLeft.value = 0.01
  setTimeout(() => {
    scrollXLeft.value = 0
  })
  unifyCategory(categoryId)
  clearWaterFall()
  getGoodList(true)
}
const handleNavToDetail = (id: Long, shopId: Long, instruction?: number) => {
  let url = `/pluginPackage/goods/commodityInfo/InfoEntrance?goodId=${id}&shopId=${shopId}`
  if (instruction) {
    url += `&instruction=${instruction}`
    uni.navigateTo({
      url,
    })
  } else {
    jumpGoods(shopId, id)
  }
}
// 滑动效果功能
const currentStyleFourPercent = ref(25)
//滑动监听
const handleScrollStyleFour = (e: any) => {
  const scrollLeft = e.detail.scrollLeft
  const scrollWidth = e.detail.scrollWidth
  const containerWidth = uni.upx2px(710)
  const scrollTrueWidth = scrollWidth - containerWidth
  currentStyleFourPercent.value = parseInt(((scrollLeft / scrollTrueWidth) * 100).toFixed(0)) + 25 // 所占宽度赋值
}
const showClassification = computed(() => {
  return formData.value?.ponentType === 1 && formData.value.firstCatList && formData.value.firstCatList.length > 0
})

// 瀑布流列数
const COLUMN_COUNT = 2

// 计算瀑布流布局
const waterfallColumns = computed(() => {
  const columns: GoodItemType[][] = Array.from({ length: COLUMN_COUNT }, () => [])
  goodsListCopy.value.forEach((item, idx) => {
    const columnIndex = idx % COLUMN_COUNT
    columns[columnIndex].push(item)
  })
  return columns
})
</script>

<template>
  <view class="good-wrapper">
    <!-- 渐变效果 -->

    <view v-if="showClassification && formData.firstCatList" style="box-shadow: 0px 210px 30px 170px rgb(255, 255, 255)"></view>
    <view v-else style="box-shadow: 0px 170px 70px 240px rgb(255, 255, 255)"></view>
    <!-- 商品组件 -->
    <scroll-view v-if="showClassification" class="scroll-view_HPage" scroll-x enhanced :show-scrollbar="false">
      <view class="container">
        <view
          v-for="(item, idx) in formData.firstCatList"
          :key="idx"
          :class="['tab__itam', currentCategoryId === item.id || currentCategoryId === item.platformCategoryFirstId ? 'active' : '']"
          @click="handleChangeCategory(isPlatform ? item.platformCategoryFirstId : item.id)"
        >
          <view class="text new__active-class">
            {{ item.name || item.platformCategoryFirstName }}
          </view>
        </view>
      </view>
    </scroll-view>
    <view
      v-if="
        (formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length > 0) || (formData.ponentType === 2 && goodsList.length)
      "
      :class="['goods', formData.listStyle]"
      :style="{
        padding: `0 ${(formData.listStyle === ListStyle['横向滑动'] ? 0 : 10) * 2}rpx`,
      }"
    >
      <template v-if="goodsList.length && [ListStyle['瀑布流'], ListStyle['一行两个']].includes(formData.listStyle)">
        <view v-if="formData.listStyle === ListStyle['瀑布流']" ref="waterFallRef" class="waterfall-container">
          <view v-for="(column, columnIndex) in waterfallColumns" :key="columnIndex" class="waterfall-column">
            <view v-for="(item, index) in column" :key="index" class="waterfall-item" @click="handleNavToDetail(item.productId, item.shopId)">
              <!-- 前3张图片不使用懒加载 让左边长一点 -->
              <u-image
                v-if="index < 3"
                height="425rpx"
                width="100%"
                :customStyle="{
                  borderTopLeftRadius: '10rpx',
                  borderTopRightRadius: '10rpx',
                }"
                :src="item.pic"
                :index="index"
              >
                <template #loading>
                  <image
                    style="width: 100%; height: 400rpx; border-top-left-radius: 10rpx; border-top-right-radius: 10rpx"
                    :src="loadingImg"
                    mode="aspectFill"
                  />
                </template>
              </u-image>
              <u-lazy-load v-else borderRadius="16" :image="item.pic" :index="index"></u-lazy-load>
              <GoodItemFoot :formData="formData" :goodsInfo="item" />
            </view>
          </view>
        </view>

        <view v-else class="two_box">
          <view v-for="(item, index) in goodsList" :key="index" class="two_item" @click="handleNavToDetail(item.productId, item.shopId)">
            <!-- 前4张图片不使用懒加载 -->
            <u-image
              v-if="index < 4"
              height="400rpx"
              width="100%"
              :customStyle="{
                borderTopLeftRadius: '10rpx',
                borderTopRightRadius: '10rpx',
              }"
              :src="item.pic"
              :index="index"
            >
              <template #loading>
                <image
                  style="width: 100%; height: 400rpx; border-top-left-radius: 10rpx; border-top-right-radius: 10rpx"
                  :src="loadingImg"
                  mode="aspectFill"
                />
              </template>
            </u-image>
            <u-lazy-load v-else :height="400" borderRadius="16" :image="item.pic" :index="index"></u-lazy-load>
            <GoodItemFoot :formData="formData" :goodsInfo="item" />
          </view>
        </view>
      </template>

      <template v-else-if="formData.listStyle === ListStyle['详细列表']">
        <template v-if="goodsList.length">
          <style-three :goods-list="goodsList" :form-data="formData"></style-three>
        </template>

        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <template v-else-if="formData.listStyle === ListStyle['横向滑动']">
        <view v-if="goodsList.length" class="goodsList">
          <view class="four-container">
            <scroll-view
              ref="scrollViewRef"
              class="scroll-view_Goods"
              scroll-x
              enhanced
              :show-scrollbar="false"
              :scroll-left="scrollXLeft"
              @scroll="handleScrollStyleFour"
            >
              <view class="four-container__wrapper">
                <style-four v-for="(item, index) in goodsList" :key="index" :item-index="index" :item="item" :form-data="formData"></style-four>
              </view>
            </scroll-view>
          </view>
          <view class="percentageBox">
            <u-line-progress
              v-if="goodsList.length > 2"
              height="8"
              inactive-color="rgba(245, 67, 25, 0.28)"
              :percent="currentStyleFourPercent * 1"
              active-color="#F54319"
              :show-percent="false"
              class="percentage"
            ></u-line-progress>
          </view>
        </view>
        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <template v-else-if="formData.listStyle === ListStyle['大图']">
        <template v-if="goodsList.length">
          <style-one
            v-for="(goodInfo, goodIndex) in goodsList"
            :key="goodIndex"
            :form-data="formData"
            :goods-info="goodInfo"
            :class="{ mt0: goodIndex === 0 }"
          ></style-one>
        </template>

        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <view v-else style="height: 450rpx; min-height: 100vh; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>

      <!-- loadmoreStatus === 'loading' 的时候给高度 防止列表切换加载时高度塌陷 -->
      <u-loadmore
        v-if="showClassification && goodsList.length && ![ListStyle['横向滑动']].includes(formData.listStyle)"
        :height="loadmoreStatus === 'loading' ? 'calc(100vh - 100rpx)' : 'auto'"
        :status="loadmoreStatus"
      />
    </view>
    <u-lazy-load v-show="false" ref="lazyLoadRef"></u-lazy-load>
  </view>
</template>

<style lang="scss" scoped>
.good-wrapper {
  background: #f5f5f5;
  overflow: hidden;
  .two_box {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .two_item {
      width: calc(50% - 10rpx);
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      margin-bottom: 20rpx;
      .goods-item__foot {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
      }
    }
  }
}
.scroll-view_HPage {
  white-space: nowrap;
  width: 750rpx;
  height: 100rpx;
  background-color: #fff;
  box-sizing: border-box;
  padding: 20rpx 20rpx;
  margin: 20rpx 0;

  ::-webkit-scrollbar {
    width: 0;
    height: 0;
    color: transparent;
  }

  .container {
    @include flex();
    width: max-content;
    background: '#fff';
  }

  .tab__itam {
    flex-shrink: 0;
    display: inline-flex;
    height: 56rpx;
    line-height: 40rpx;
    color: #333;
    padding: 10rpx 20rpx;
    margin-left: 32rpx;

    &:first-child {
      margin: 0;
    }
  }

  .active {
    background-color: #f54319;
    color: #fff;
    border-radius: 200rpx;
  }
}

.four-container {
  display: flex;
  background: linear-gradient(180deg, rgb(253, 124, 79), rgb(255, 255, 255) 50%);
  width: 710rpx;
  margin: 20rpx auto 0;
  overflow: hidden;
  border-top-left-radius: 10rpx;
  border-top-right-radius: 10rpx;
  box-sizing: border-box;
  padding: 20rpx;

  .four-container__wrapper {
    width: fit-content;
    display: flex;
  }
}

.waterfall-container {
  display: flex;
  gap: 20rpx;
  min-height: 100%;
  width: 100%;
  overflow-x: hidden;
}

.waterfall-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  min-width: 0;
}

@include b(percentageBox) {
  padding: 20rpx 0;
  margin: 0 20rpx;
  background-color: #fff;
  display: flex;
  justify-content: center;
  padding-right: 20rpx;

  @include b(percentage) {
    width: 76rpx !important;
  }
}

@include b(mt0) {
  margin-top: 0;
}
</style>
