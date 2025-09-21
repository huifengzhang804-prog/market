<template>
  <template v-if="properties">
    <div v-if="$route?.query?.type === 'activity'">
      <!-- 活动图 -->
      <div class="activityImgBox">
        <img :src="properties?.img" />
      </div>
      <!-- 一级活动分类 -->
      <div id="referenceVal" ref="mainBox" class="activityClassify">
        <div class="activityClassify__classify">
          <span
            v-for="(item, index) in properties?.radioKeys"
            :key="index"
            :style="headlinesArrows === index ? 'color: #f54319' : ''"
            @click="headlinesArrowsFn(index)"
            >{{ item }}</span
          >
        </div>
      </div>
      <!-- 导航 -->
      <div class="mainBoxs">
        <div class="mainBoxs__navigationsBar">
          <div class="mainBoxs__navigationsBar--bar">
            <div class="barLi" @click="handleChooseFilterStatus([{ order: '', sort: '' }])">
              <QIcon v-if="orderVal === ''" name="icon-xuanzhongzhuangtai2-copy" color="#f54319" size="16px" class="barLi__imgBox" />
              <span :style="orderVal === '' ? 'color: #f54319' : ''">综合</span>
            </div>
            <div
              class="barLi"
              @click="
                handleChooseFilterStatus([
                  {
                    order: 'salesVolume',
                    sort: 'desc',
                  },
                ])
              "
            >
              <QIcon v-if="orderVal === 'salesVolume'" name="icon-xuanzhongzhuangtai2-copy" color="#f54319" size="16px" class="barLi__imgBox" />
              <span :style="orderVal === 'salesVolume' ? 'color: #f54319' : ''">销量</span>
              <div class="barLi__imgBoxs">
                <QIcon name="icon-xiaoliang" size="14px" :color="orderVal === 'salesVolume' ? '#F54319' : '#999'" />
              </div>
            </div>
            <div
              class="barLi"
              @click="
                handleChooseFilterStatus([
                  {
                    order: 'createTime',
                    sort: 'desc',
                  },
                ])
              "
            >
              <QIcon v-if="orderVal === 'createTime'" name="icon-xuanzhongzhuangtai2-copy" color="#f54319" size="16px" class="barLi__imgBox" />
              <span :style="orderVal === 'createTime' ? 'color: #f54319' : ''">新品</span>
              <div class="barLi__imgBoxs">
                <QIcon name="icon-a-31_xinpin" size="14px" :color="orderVal === 'createTime' ? '#F54319' : '#999'" />
              </div>
            </div>
            <div
              class="barLi"
              @click="
                handleChooseFilterStatus([
                  {
                    order: 'salePrices',
                    sort: 'desc',
                  },
                ])
              "
            >
              <QIcon v-if="orderVal === 'salePrices'" name="icon-xuanzhongzhuangtai2-copy" color="#f54319" size="16px" class="barLi__imgBox" />
              <span :style="orderVal === 'salePrices' ? 'color: #f54319' : ''">价格</span>
              <el-icon size="11" style="transform: translateY(-2px)" :color="sortBool && orderVal === 'salePrices' ? '#f54319' : '#999'">
                <i-ep-arrowUpBold />
              </el-icon>
              <el-icon size="11" style="transform: translate(-11px, 4px)" :color="!sortBool && orderVal === 'salePrices' ? '#f54319' : '#999'">
                <i-ep-arrowDownBold />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
      <!-- 商品 -->
      <div class="recommendBox">
        <div v-for="(item, index) in likeDatas" :key="index" class="recommendBox__item" @click="gotoDetail(item?.productId, item?.shopId)">
          <div class="recommendBox__item--imgBox">
            <img :src="item?.pic" />
          </div>
          <div class="recommendBox__item--price">
            <div class="Price">
              <good-price :price="range(item?.salePrices?.[0]).toString()" />
            </div>
            <div class="delPrice">
              <div style="height: 17px; margin-top: 2px" />
              <div class="delPrice__del">￥{{ divTenThousand(item?.prices?.[0]).toFixed(2) }}</div>
            </div>
          </div>
          <div class="recommendBox__item--title">
            {{ item?.productName }}
          </div>
          <div class="recommendBox__item--discountCouponList">
            <span v-for="(coupon, inde) in item?.couponList" :key="inde" class="discountCoupon">{{ coupon?.sourceDesc }}</span>
          </div>
          <div class="recommendBox__item--paymentNum">
            <!-- <span>包邮</span> -->
            <span v-if="+item?.salesVolume > 0">已售 {{ item?.salesVolume }}</span>
          </div>
        </div>
        <!-- <ToTopGoCar /> -->
      </div>
      <!-- 分页 -->
      <el-pagination
        v-if="+page.total > 0"
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        class="pagination"
        small
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="total, jumper, prev, pager, next, sizes"
        :total="+page.total"
        @size-change="handleSizeChange"
        @current-change="handleChangePage"
      />
      <ToTopGoCar :top-go="topGo" />
    </div>
    <!-- 图文页面 -->
    <div v-else-if="$route?.query?.type === 'custom'">
      <!-- 活动图 -->
      <div v-if="properties?.img" class="activityImgBoxs">
        <img :src="properties?.img" />
      </div>
      <!-- 配置的图文 -->
      <div ref="mainBox" class="imageTextBoxs">
        <div class="imageTextBox">
          <div class="imageTextBox__box">
            <div class="imageTextBox__box--texts">
              <div v-dompurify-html="properties?.text" />
            </div>
          </div>
          <!-- <div class="imageTextBox__box">
                    <div class="imageTextBox__box--images">
                        <div>
                            <img
                                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20231129/c70513795cbb4de3adf5e6d46cdc3e7c.png"
                            />
                        </div>
                    </div>
                </div> -->
        </div>
        <ToTopGoCar :top-go="topGo" />
      </div>
    </div>
  </template>
  <div v-else c-h-600 flex justify-center items-center>
    <div>
      <img src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e8.png" c-w-200 c-pb-20 />
      <p c-fs-12 c-mt-20 c-c-000>请前往平台端装修！！！</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import usePriceRange from '@/composables/usePriceRange'
import useConvert from '@/composables/useConvert'
import { doGetPlatformPagesDetail, doGetShopPagesDetail } from '@/apis/platform'
import { ElMessage } from 'element-plus'
import { doPostProduct } from '@/apis/user'
import goodPrice from '@/views/home/components/good-price.vue'
import { toNumber } from 'lodash-es'
import Storage from '@/libs/storage'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

import { storeToRefs } from 'pinia'
import { productListInterface } from '../home/components'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

const storage = new Storage()
const { divTenThousand } = useConvert()
const { range } = usePriceRange()

const $route = useRoute()
const $router = useRouter()

const page = reactive({ current: 1, size: 20, total: 0 })
// 当前选中筛选下标
const currentChooseFilterStatus = ref([{ order: '', sort: '' }])
// 筛选配置
const orderVal = ref('')
const sortBool = ref(false)
/**
 * @description: 选择排序
 */
const handleChooseFilterStatus = (item: { order: string; sort: string }[]) => {
  if (orderVal.value === item[0].order) {
    sortBool.value = !sortBool.value
    if (item[0].sort === 'desc' && sortBool.value) item[0].sort = 'asc'
  } else sortBool.value = false
  orderVal.value = item[0].order
  currentChooseFilterStatus.value = item
  productListInt()
}

// 分页
const handleChangePage = (e: number) => {
  page.current = e
  document.querySelector('#referenceVal')?.scrollIntoView()
  productListInt()
}
const handleSizeChange = (e: number) => {
  page.size = e
  document.querySelector('#referenceVal')?.scrollIntoView()
  productListInt()
}
// 悬浮导航
const topGo = ref(false)
const mainBox = ref()
onMounted(() => {
  window.addEventListener('scroll', handleScroll, true)
})
const handleScroll = () => {
  let winHeight = mainBox.value?.getBoundingClientRect().top
  if (winHeight > 250) topGo.value = true
  else topGo.value = false
}

const properties = ref()
const doGetPlatformPagesDetailInt = async () => {
  if (!$route.query?.link) return
  if ($route.query?.links === 'false') return
  if ($route.query?.shopVal === 'true') {
    const res = await doGetShopPagesDetail($route.query?.link as string, $route.query?.shopId as string)
    if (res?.code !== 200) return ElMessage.error(res?.msg || '获取页面数据失败')
    else if (!res?.data) return ElMessage.error(res?.msg || '暂无页面数据')
    properties.value = JSON.parse(res?.data?.properties)
  } else {
    const { code, data, msg } = await doGetPlatformPagesDetail($route.query?.link as string)
    if (code !== 200) return ElMessage.error(msg || '获取页面数据失败')
    else if (!data) return ElMessage.error(msg || '暂无页面数据')
    properties.value = JSON.parse(data?.properties)
  }
}

const headlinesArrows = ref(0)
const headlinesArrowsFn = (index: number) => {
  headlinesArrows.value = index
  productListInt()
}
const likeDatas = ref<any[]>()
async function productListInt() {
  /**处理装修商品数据 取ID 调接口过滤 取出还有库存的商品 */
  let goodsIdList: string[] = []
  let list =
    properties.value?.list[properties.value?.radioKeys?.[headlinesArrows.value]].map(
      (item: { shopId: string; productId: string }) => item?.shopId + ':' + item?.productId,
    ) || []
  goodsIdList = [...goodsIdList, ...list]
  if (!goodsIdList.length) {
    page.total = 0
    likeDatas.value = []
    return
  }
  const searchParams = {
    platformCategoryFirstId: '',
    showCoupon: true,
    size: page.size,
    current: page.current,
    ids: goodsIdList,
    searchTotalStockGtZero: true,
    orderByParams: [{ order: '', sort: '' }] as Array<{ order: string; sort: string }> | null,
  }
  // 兼容综合排序
  if (currentChooseFilterStatus.value[0].order === '') {
    searchParams.orderByParams = null
  } else {
    searchParams.orderByParams = currentChooseFilterStatus.value
  }
  const { code, data, msg } = (await doPostProduct(searchParams)) as productListInterface
  if (code !== 200) return ElMessage.error(msg || '获取商品失败')
  likeDatas.value = data?.list
  page.total = data?.total
}

watch(
  () => $route.query,
  () => {
    setTimeout(() => {
      doGetPlatformPagesDetailInt()
      handleChooseFilterStatus([{ order: '', sort: '' }])
      headlinesArrows.value = 0
    }, 300)
    // if (NewVal?.link === oldVal?.link && NewVal?.id !== oldVal?.id) $router.go(0)
  },
  { immediate: true },
)
watch(
  () => properties.value,
  (val) => {
    if ($route.query?.type === 'activity' && val) productListInt()
  },
  { immediate: true },
)

const activityClassList = ref()
setTimeout(() => {
  if ($route.query?.shopVal === 'true' && storage.getItem('shopPropertiesList')) activityClassList.value = storage.getItem('shopPropertiesList') || ''
  else activityClassList.value = getterPropertiesList.value
  propertiesInt()
}, 300)
const propertiesInt = () => {
  if (!$route.query?.id) return
  let propertieVal = activityClassList.value?.components?.filter((item: { id: number }) => toNumber(item?.id) === toNumber($route.query?.id))
  properties.value = propertieVal?.[0]?.formData
}

// 跳转到商品详情
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
</script>

<style scoped lang="scss">
@include b(activityImgBox) {
  width: 1920px;
  height: 520px;
  background-color: #fff;
  position: relative;
  // z-index: 10;
  img {
    width: 100%;
    height: 100%;
  }
}
@include b(activityClassify) {
  width: 100%;
  height: 42px;
  line-height: 42px;
  color: #333;
  box-shadow: 0px 1px 0px 0px rgba(0, 0, 0, 0.25);
  background-color: #fff;
  @include e(classify) {
    width: 1200px;
    margin: 0 auto;
    text-align: left;
    span {
      display: inline-block;
      margin: 0 32px;
      font-size: 16px;
      cursor: pointer;
    }
  }
}
@include b(mainBoxs) {
  width: 1200px;
  height: 42px;
  line-height: 42px;
  margin: 0 auto;
  text-align: left;
  margin-top: 1px;
  background-color: #fff;
  @include e(navigationsBar) {
    width: 955px;
    height: 42px;
    line-height: 42px;
    font-size: 14px;
    display: flex;
    margin-bottom: 24px;
    justify-content: space-between;
    @include m(bar) {
      display: flex;
    }
  }
}
@include b(barLi) {
  position: relative;
  // margin-right: 48px;
  width: 96px;
  text-align: center;
  cursor: pointer;
  @include e(imgBox) {
    position: absolute;
    top: -8px;
    left: 8px;
    transform: translateY(0);
  }
  span {
    cursor: pointer;
    color: #333;
    margin-right: 4px;
    display: inline-block;
    font-size: 14px;
    line-height: 20px;
  }
  @include e(imgBoxs) {
    display: inline-block;
    width: 16px;
    img {
      width: 100%;
    }
  }
}
@include b(recommendBox) {
  width: 1201px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  text-align: left;
  padding-top: 24px;
  @include e(item) {
    width: 220px;
    height: 352px;
    background-color: #fff;
    padding: 16px 16px 12px 16px;
    margin: 0 24px 24px 0;
    cursor: pointer;
    &:nth-of-type(5n) {
      margin-right: 0;
    }
    @include m(imgBox) {
      width: 189px;
      height: 189px;
      border-radius: 4px;
      overflow: hidden;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(price) {
      font-weight: bold;
      color: rgb(224, 5, 0);
      line-height: 22px;
      display: flex;
      height: 34px;
      overflow: hidden;
      @include b(Price) {
        padding: 12px 4px 0 0;
        font-size: 12px;
        @include e(primary) {
          font-size: 16px;
          line-height: 17px;
        }
      }
      @include b(delPrice) {
        font-size: 12px;
        font-weight: 400;
        @include e(label) {
          margin-top: -3px;
          height: 17px;
          background: rgb(251, 202, 58);
          border-radius: 10px;
          color: rgb(97, 70, 19);
          line-height: 14px;
          padding: 2px 6px;
        }
        @include e(del) {
          margin-top: -2px;
          color: rgb(153, 153, 153);
          text-decoration-line: line-through;
          line-height: 17px;
          font-size: 8px;
        }
      }
    }
    @include m(title) {
      height: 40px;
      margin-top: 8px;
      color: rgb(20, 20, 20);
      font-size: 14px;
      line-height: 20px;
      overflow: hidden;
    }
    @include m(discountCouponList) {
      margin-top: 8px;
      height: 20px;
      overflow: hidden;
      // white-space: nowrap;
      @include b(discountCoupon) {
        font-size: 12px;
        padding: 2px 4px;
        margin-right: 8px;
        // line-height: 16px;
        color: rgb(245, 67, 25);
        border-radius: 2px;
        background-color: rgba(245, 67, 25, 0.05);
        white-space: nowrap;
        display: inline-block;
        overflow: hidden;
        // transform: translateY(-3px);
      }
    }
    @include m(paymentNum) {
      margin-top: 8px;
      height: 17px;
      text-align: right;
      font-size: 12px;
      color: rgb(153, 153, 153);
      span {
        display: inline-block;
        line-height: 17px;
      }
    }
  }
}
@include b(pagination) {
  display: flex;
  justify-content: center;
  height: 24px;
  margin-bottom: 24px;
}
// 图文
@include b(activityImgBoxs) {
  width: 1920px;
  height: 600px;
  margin: 0 auto;
  img {
    width: 100%;
    height: 100%;
  }
}
@include b(imageTextBoxs) {
  // padding-bottom: 24px;
  // background-color: #f2f5f9;
  // background-color: #fff;
}
@include b(imageTextBox) {
  // background-color: #fff;
  @include e(box) {
    width: 100%;
    @include m(images) {
      width: 1200px;
      margin: 0 auto;
      overflow: hidden;
    }
    @include m(texts) {
      width: 1200px;
      margin: 0 auto;
      overflow: hidden;
    }
    img {
      width: 100%;
    }
    span {
    }
  }
  &:nth-of-type(2n) {
    background-color: #f2f5f9;
  }
}
</style>
