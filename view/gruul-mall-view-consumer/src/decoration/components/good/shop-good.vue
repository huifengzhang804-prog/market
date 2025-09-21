<script setup lang="ts">
import { ref, onMounted, type PropType, reactive } from 'vue'
import type { GoodFormData, FirstCateItem } from '../types'
import { doGetRetrieveCommodity } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { cropImg } from '@/utils'
import { useSettingStore } from '@/store/modules/setting'
import GoodItem from './shop-goodItem.vue'
import ShoppingButton from './shoppingButton.vue'
import { onUnload } from '@dcloudio/uni-app'
import Coner from './coner.vue'
import type { RetrieveCommodity } from '@/apis/good/model'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

enum ListStyle {
  '大图' = 'goods-style--one',
  '一行两个' = 'goods-style--two',
  '详细列表' = 'goods-style--three',
  '横向滑动' = 'goods-style--four',
  '瀑布流' = 'goods-style--five',
}
type GoodItemKey = 'productId' | 'productName' | 'salesVolume' | 'shopName' | 'pic' | 'shopId' | 'specs' | 'status'
type GoodItemType = Record<GoodItemKey, string> & {
  salePrices: Long[]
}
type SearchConfig = {
  platformCategoryFirstId: string | null
  categoryFirstId: string | null
  productIds: Long[] | null
}
// TODO: 商品装修样式边缘线和阴影配置
// TODO: 商品组件加载分页
// TODO: 添加商品售罄

const $settingStore = useSettingStore()
const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<GoodFormData>,
    default() {
      return {}
    },
  },
})
const { range } = usePriceRange()
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
})
const currentCategoryId = ref('-1')
const GoodsCornerMark = ref({
  // 商品角标地址及样式
  url: '',
  class: '',
})
const goodsList = ref<GoodItemType[]>([])
const GoodsNameClass = ref('')
const GoodsBoxClass = ref('')
const ClassBugCard3 = ref('')
const GoodsItemStyle = ref({})
const GoodsLastStyle = ref({})

onMounted(async () => {
  const decoretionProperties = $props.decorationProperties
  judgePlatform(decoretionProperties.firstCatList)
  formData.value = decoretionProperties
  try {
    if (decoretionProperties.ponentType === 1) {
      filterCategory($props.decorationProperties.firstCatList)
    } else if (decoretionProperties.ponentType === 2) {
      goodsList.value = decoretionProperties.goods
      const { data } = await doGetRetrieveCommodity({ productId: goodsList.value.map((item) => item.productId), size: goodsList.value.length })
      const list = data?.list || []
      goodsList.value.map((item) => {
        const findOptions = list.find((li: any) => li.productId === item.productId)
        if (findOptions) {
          item.salePrices = findOptions.salePrices || []
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
/**
 * 获取商品
 */
async function getGoodList() {
  waterFallRef.value?.clear()
  const { code, data } = await doGetRetrieveCommodity({ ...searchConfig, size: 100, searchTotalStockGtZero: true })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return
  }
  const tempArr = data.list.map((item: RetrieveCommodity) => {
    const { productId, productName, pic, salePrices, shopId } = item
    return {
      productId,
      productName,
      pic,
      salePrices,
      shopId,
    }
  })
  goodsList.value = batchFileImg(tempArr)
}
function batchFileImg(list: any[]) {
  return list.map((item) => {
    item.pic = cropImg(item.pic, 380, 380)
    return item
  })
}
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
uni.$on('首页', () => {
  getGoodList()
})

onUnload(() => {
  uni.$off('首页')
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
  unifyCategory(categoryId)
  getGoodList()
}
const handleNavToDetail = (id: string, shopId: Long, instruction?: number) => {
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
</script>

<template>
  <view>
    <!-- 商品组件 -->
    <scroll-view
      v-if="formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length > 0"
      class="scroll-view_HPage"
      scroll-x
      enhanced
      :show-scrollbar="false"
    >
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
    </scroll-view>
    <view
      v-if="
        (formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length > 0) || (formData.ponentType === 2 && goodsList.length)
      "
      :class="['goods', formData.listStyle]"
      :style="{ padding: `0 ${(formData.listStyle === ListStyle['横向滑动'] ? 0 : 10) * 2}rpx` }"
    >
      <u-waterfall v-if="goodsList.length && formData.listStyle === ListStyle['瀑布流']" ref="waterFallRef" v-model="goodsList" idKey="productId">
        <template #left="{ leftList }">
          <view
            v-for="(item, index) in leftList"
            :key="index"
            class="water_item"
            style="position: relative"
            @click="handleNavToDetail(item.productId, item.shopId)"
          >
            <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
            <!-- #ifdef MP-WEIXIN-->
            <Coner
              :class="['goods-item__coner', 'goods-item__coner1']"
              :tag-show="formData.showContent.tagShow"
              :tag-style="formData.showContent.tagStyle"
            ></Coner>
            <!-- #endif -->
            <GoodItem
              :form-data="formData"
              :class-bug-card3="ClassBugCard3"
              :item="item"
              :goods-name-class="GoodsNameClass"
              :goods-corner-mark="GoodsCornerMark"
              :img-show="false"
              @nav-to-detail="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)"
            />
          </view>
        </template>
        <template #right="{ rightList }">
          <view
            v-for="(item, index) in rightList"
            :key="index"
            class="water_item"
            style="position: relative"
            @click="handleNavToDetail(item.productId, item.shopId)"
          >
            <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
            <!-- #ifdef MP-WEIXIN-->
            <Coner
              :class="['goods-item__coner', 'goods-item__coner1']"
              :tag-show="formData.showContent.tagShow"
              :tag-style="formData.showContent.tagStyle"
            ></Coner>
            <!-- #endif -->
            <GoodItem
              :form-data="formData"
              :class-bug-card3="ClassBugCard3"
              :item="item"
              :goods-name-class="GoodsNameClass"
              :goods-corner-mark="GoodsCornerMark"
              :img-show="false"
              @nav-to-detail="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)"
            />
          </view>
        </template>
      </u-waterfall>

      <!-- 列表 -->
      <template v-else-if="formData.listStyle === ListStyle['详细列表']">
        <template v-if="goodsList.length">
          <view
            v-for="(item, idx) in goodsList"
            :key="idx"
            :class="['goods-item', GoodsBoxClass]"
            :style="[idx === goodsList.length ? GoodsLastStyle : GoodsItemStyle] as any"
            style="padding: 0"
            @click="handleNavToDetail(item.productId, item.shopId)"
          >
            <Coner :class="['goods-item__coner']" :tag-show="formData.showContent.tagShow" :tag-style="formData.showContent.tagStyle"></Coner>

            <view class="goods-item__img goods-item__img--three" style="width: 190rpx; height: 190rpx; border-radius: 20rpx 0 0 20rpx">
              <!-- #ifndef H5 -->
              <image :src="cropImg(item.pic, 190, 190)" :lazy-load="true" mode="aspectFill" style="width: 190rpx; height: 190rpx"></image>
              <!-- #endif -->
              <!-- #ifdef H5 -->
              <lazy-load>
                <image :src="cropImg(item.pic, 190, 190)" mode="aspectFill" style="width: 190rpx; height: 190rpx"></image>
              </lazy-load>
              <!-- #endif -->
            </view>
            <view class="goods-item__foot">
              <view :class="['goods-item__name2', GoodsNameClass]">
                {{ item.productName }}
              </view>
              <view class="goods-item__bottom" style="margin: 0">
                <view class="goods-item__price">
                  <view class="price__one">
                    {{ range(item.salePrices[0]) }}
                  </view>
                </view>
                <view @click.stop="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)">
                  <ShoppingButton :button-style="formData.showContent.buttonStyle"></ShoppingButton>
                </view>
              </view>
            </view>
          </view>
        </template>

        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <!-- 横向 -->
      <template v-else-if="formData.listStyle === ListStyle['横向滑动']">
        <template v-if="goodsList.length">
          <scroll-view class="scroll-view_Goods" scroll-x enhanced :show-scrollbar="false">
            <view
              v-for="(item, idx) in goodsList"
              :key="idx"
              :class="['goods-item', GoodsBoxClass]"
              :style="[idx === goodsList.length ? GoodsLastStyle : GoodsItemStyle] as any"
              style="width: 304rpx"
              @click="handleNavToDetail(item.productId, item.shopId)"
            >
              <Coner :class="['goods-item__coner']" :tag-show="formData.showContent.tagShow" :tag-style="formData.showContent.tagStyle"></Coner>
              <view class="goods-item__img" style="width: 304rpx; height: 304rpx">
                <!-- #ifndef H5 -->
                <image :src="cropImg(item.pic, 304, 304)" style="width: 100%" mode="aspectFill" :lazy-load="true"></image>
                <!-- #endif -->
                <!-- #ifdef H5 -->
                <lazy-load>
                  <image :src="cropImg(item.pic, 304, 304)" mode="aspectFill" style="width: 100%"></image>
                </lazy-load>
                <!-- #endif -->
              </view>
              <view :class="['goods-item__foot']">
                <view :class="['goods-item__name1', GoodsNameClass]">
                  {{ item.productName }}
                </view>
                <view class="goods-item__bottom" style="margin: 0">
                  <view class="goods-item__price">
                    <view class="price__one">
                      {{ range(item.salePrices[0]) }}
                    </view>
                  </view>
                  <view @click.stop="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)">
                    <ShoppingButton :button-style="formData.showContent.buttonStyle"></ShoppingButton>
                  </view>
                </view>
              </view>
            </view>
          </scroll-view>
        </template>
        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <!-- 大图 -->
      <template v-else-if="formData.listStyle === ListStyle['大图']">
        <template v-if="goodsList.length">
          <view
            v-for="(item, idx) in goodsList"
            :key="idx"
            :class="['goods-item', GoodsBoxClass, 'goods-style--one-goods-item']"
            :style="[idx === goodsList.length ? GoodsLastStyle : GoodsItemStyle] as any"
            @click="handleNavToDetail(item.productId, item.shopId)"
          >
            <Coner :class="['goods-item__coner']" :tag-show="formData.showContent.tagShow" :tag-style="formData.showContent.tagStyle"></Coner>
            <view class="goods-item__img goods-style--one-image">
              <image :src="item.pic" :lazy-load="true" mode="aspectFill" style="width: 100%; height: 100%"></image>
            </view>
            <view :class="['goods-item__foot', 'add__f-p', 'goods-style--one-foot']">
              <view :class="['goods-item__name1', GoodsNameClass]">
                {{ item.productName }}
              </view>
              <view class="goods-item__bottom" style="margin: 0">
                <view class="goods-item__price">
                  <view class="price__one">
                    {{ range(item.salePrices[0]) }}
                  </view>
                </view>
                <view @click.stop="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)">
                  <ShoppingButton :button-style="formData.showContent.buttonStyle"></ShoppingButton>
                </view>
              </view>
            </view>
          </view>
        </template>

        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>

      <template v-else>
        <template v-if="goodsList.length">
          <view
            v-for="(item, idx) in goodsList"
            :key="idx"
            :class="['goods-item', GoodsBoxClass]"
            :style="[idx === goodsList.length ? GoodsLastStyle : GoodsItemStyle] as any"
            @click="handleNavToDetail(item.productId, item.shopId)"
          >
            <Coner :class="['goods-item__coner']" :tag-show="formData.showContent.tagShow" :tag-style="formData.showContent.tagStyle"></Coner>
            <view class="goods-item__img">
              <!-- #ifndef H5 -->
              <image :src="cropImg(item.pic, 380, 380)" :lazy-load="true" mode="aspectFill" style="width: 100%"></image>
              <!-- #endif -->
              <!-- #ifdef H5 -->
              <lazy-load>
                <img :src="cropImg(item.pic, 380, 380)" mode="aspectFill" style="width: 100%" />
              </lazy-load>
              <!-- #endif -->
            </view>
            <view :class="['goods-item__foot', 'add__f-p']">
              <view :class="['goods-item__name1', GoodsNameClass]">
                {{ item.productName }}
              </view>
              <view class="goods-item__bottom">
                <view class="goods-item__price">
                  <view class="price__one">
                    {{ range(item.salePrices[0]) }}
                  </view>
                </view>
                <view @click.stop="handleNavToDetail(item.productId, item.shopId, formData.showContent.buttonStyle)">
                  <ShoppingButton :button-style="formData.showContent.buttonStyle"></ShoppingButton>
                </view>
              </view>
            </view>
          </view>
        </template>

        <view v-else style="height: 450rpx; line-height: 450rpx; width: 100vw; text-align: center"> 暂无商品 </view>
      </template>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(goods-item) {
  position: relative;
  background-color: #fff;
  border-radius: 20rpx;
  box-shadow: rgba(0, 0, 0, 0.1) 0px 19px 28px;
  @include when(circle) {
    border-radius: 12rpx;
    overflow: hidden;
  }

  @include when(shadow) {
    // box-shadow: 0 0 4px rgba($color: #000000, $alpha: 0.4);
    box-shadow: 0px 2rpx 112rpx 12rpx rgba(109, 109, 109, 0.1);
  }

  @include when(border) {
    border: 2rpx solid #eeeeee;
  }

  @include e(img) {
    height: 360rpx;
    background-color: #eeeeee;
    overflow: hidden;
    position: relative;
    border-radius: 20rpx 20rpx 0 0;
    image {
      height: 360rpx;
      width: 100%;
    }
  }

  @include e(name1) {
    overflow: hidden;
    text-overflow: ellipsis;
    padding-top: 10rpx;
    -webkit-line-clamp: 1;
    word-wrap: break-word;
    white-space: normal !important;
    -webkit-box-orient: vertical;
    display: -webkit-box;
    font-size: 32rpx;
    font-weight: 700;
  }
  @include e(name2) {
    overflow: hidden;
    text-overflow: ellipsis;
    padding-top: 10rpx;
    -webkit-line-clamp: 2;
    word-wrap: break-word;
    white-space: normal !important;
    -webkit-box-orient: vertical;
    display: -webkit-box;
    font-size: 30rpx;
  }
  @include e(foot) {
    padding: 0 14rpx;
    position: relative;

    .is-bold {
      font-weight: 800;
    }
  }

  .add__f-p {
    .goods-item__name {
      width: 230rpx;
      overflow: hidden !important;
      text-overflow: ellipsis !important;
      white-space: nowrap !important;
      display: block;
    }
  }

  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6rpx 0;
    margin-top: 20rpx;
  }

  @include e(price) {
    line-height: 74rpx;
    flex: 1;

    view {
      display: inline-block;
      line-height: 74rpx;
      height: 74rpx;
    }

    .price__one {
      font-size: 36rpx;
      color: $qszr-red;
      &::before {
        content: '￥';
        font-weight: normal;
        font-size: 30rpx;
      }
    }

    .price__two {
      text-decoration: line-through;
      color: #bbbbbb;
      font-size: 20rpx;
      margin-left: 16rpx;
    }
  }

  @include e(icon) {
    display: inline-block;
    width: 56rpx;
    height: 56rpx;
    line-height: 56rpx;
    text-align: center;
    background-color: red;
    border-radius: 100%;
    color: #ffffff;
  }

  @include e(cart1) {
    float: right;
    height: 60rpx;
    width: 60rpx;
    background-color: rgba(252, 98, 63, 1);
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 10rpx;
  }

  @include e(cart) {
    float: right;
    height: 60rpx;
    width: 60rpx;
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 34rpx;

    image {
      display: inline-block;
      width: 40rpx;
      height: 40rpx;
    }
  }

  @include e(cart1) {
    background-color: #fff;

    image {
      display: inline-block;
      width: 60rpx;
      height: 60rpx;
    }
  }

  @include e(cart2) {
    background: linear-gradient(164deg, rgba(243, 243, 243, 1), rgba(229, 56, 46, 1), rgba(253, 78, 38, 1));
    box-shadow: 0 4rpx 14rpx 0 rgba(255, 14, 0, 0.27);
    border-radius: 50%;
  }

  @include e(cart3) {
    border: 2rpx solid rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: rgba(252, 98, 63, 1);
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(cart4) {
    border: 2rpx solid rgba(252, 98, 63, 1);
    background-color: rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: #fff;
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(coner) {
    position: absolute;
    z-index: 12;

    image,
    text {
      display: block;
      width: 100%;
      height: 100%;
      position: absolute;
    }
  }

  @include e(coner1) {
    left: 0rpx;
    top: 0rpx;
    width: 76rpx;
    height: 44rpx;
    z-index: 7;
  }

  @include e(coner2) {
    left: 0;
    top: 0;
    width: 76rpx;
    height: 82rpx;
  }

  @include e(coner3) {
    left: 0rpx;
    top: 0rpx;
    width: 84rpx;
    height: 42rpx;
  }

  @include e(delivery) {
    padding-top: 4rpx;

    .i_box {
      border: 2rpx solid rgba(233, 56, 38, 1);
      border-radius: 34rpx;
      color: #e93826;
      font-size: 22rpx;
      display: inline-block;
      padding: 0 12rpx;
    }

    .base_info {
      padding: 2rpx 12rpx;
      background-color: #e93826;
      color: #fff;
      border-radius: 34rpx;
      display: none;
    }

    .hasTip {
      padding-left: 0;

      .base_info {
        display: inline-block;
        margin-right: 2rpx;
      }
    }
  }

  .goods-item__delivery3 {
    transform: scale(0.76);
    position: absolute;
    left: 4rpx;
    top: 100rpx;
  }

  .goods-buy__cart3 {
    width: 34rpx;
    height: 36rpx;
  }

  .buy-icon2 {
    margin-top: 16rpx;

    image {
      width: 38rpx;
      height: 38rpx;
    }
  }
}

@include b(goods-style) {
  @include m(two) {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    @include b(goods-item) {
      width: 49%;
    }
  }

  @include m(three) {
    @include b(goods-item) {
      padding: 10rpx 0;
      display: flex;
      width: 100%;

      @include e(img) {
        height: 270rpx;
        width: 270rpx;
        margin-right: 20rpx;
        flex: none;
      }

      @include e(foot) {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
      }

      @include e(name) {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  @include m(four) {
    white-space: nowrap;
    overflow-x: auto;

    @include b(goods-item) {
      display: inline-block;
      width: 270rpx;

      @include e(img) {
        height: 270rpx;
        width: 270rpx;
        margin-right: 20rpx;
      }

      @include e(foot) {
      }

      @include e(name) {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

@include b(goods) {
  // background-color: #ffffff;
  box-sizing: border-box;
}

.no__goods {
  position: absolute;
  left: 50%;
  top: 50%;
  margin-left: -88rpx;
  margin-top: -88rpx;
  z-index: 11;
  width: 176rpx;
  height: 176rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-direction: column;

  text {
    display: block;
    width: 176rpx;
    text-align: center;
    font-size: 25rpx;
  }
}

.goods-style--one {
  .goods-item__cart {
    margin-top: 5rpx;
  }
}

.goods-style--two {
  .goods-item__cart {
    margin-top: 5rpx;
  }
}

.goods-style--three {
  .goods-item__cart {
    margin-top: 5rpx;
  }

  .goods-item__foot {
    position: relative;
  }

  .goods-item__delivery {
    position: absolute;
    left: 15rpx;
    top: 105rpx;
  }

  .goods-item__bottom {
    height: 86rpx;
  }

  .goods-item__name {
    padding-top: 18rpx;
  }
}

.goods-style--four {
  .goods-item__bottom {
    height: 80rpx;
  }

  .goods-item__cart {
    margin-top: 10rpx;
  }

  .goods-buy__cart3 {
    width: 60rpx;
    height: 60rpx;
  }
}
.scroll-view_Goods {
  width: 100%;
  height: 450rpx;
  white-space: nowrap;
}
.scroll-view_HPage {
  width: 100%;
  height: 90rpx;
  background-color: #fff;
  margin-bottom: 16rpx;
  white-space: nowrap;

  .tab__itam {
    display: inline-flex;
    align-items: center;
    position: relative;
    height: 80rpx;
    padding: 0rpx 22rpx;
    font-size: 30rpx;
    font-family: Hiragino Sans GB;
    font-weight: normal;
    color: #6b6b6b;
    padding-top: 12rpx;
    &::before {
      content: '';
      position: absolute;
      width: 1px;
      height: 20rpx;
      right: 3rpx;
      background: #ccc;
    }
    .new__active-class {
      z-index: 8;
      white-space: nowrap;
      display: inline-block;
      padding-bottom: 4rpx;
      border-bottom: 4rpx solid transparent;
    }
    .line {
      display: none;
    }

    .text {
      // float: left;
      z-index: 8;
      white-space: nowrap;
    }
  }

  .active {
    font-size: 30rpx;
    font-family: Hiragino Sans GB;
    font-weight: 600;
    color: $qszr-red;
    .new__active-class {
      border-bottom-color: $qszr-red;
    }

    .line {
      display: block;
      float: left;
      width: 100%;
      background: linear-gradient(164deg, #f3f3f3, #e5382e, #fd4e26);
      border-radius: 4rpx;
      margin-top: -20rpx;
      height: 18rpx;
    }
  }
}
.waterfalls-flow {
  :deep(.column-value) {
    .img {
      border-radius: 0;
    }
  }
}
.goods-style--one-goods-item {
  width: 710rpx;
  height: 720rpx;
  position: relative;
}
.goods-style--one-image {
  height: 580rpx;
}
.goods-style--one-foot {
  position: absolute;
  bottom: 10rpx;
  right: 0;
  left: 0;
  border-radius: 20rpx;
  background: #fff;
}
</style>
