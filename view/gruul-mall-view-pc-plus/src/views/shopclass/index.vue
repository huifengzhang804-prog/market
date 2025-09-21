<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import { CaretBottom, CaretRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useConversionPrice, useConvertString } from '@/hooks'
import { doGetRetrieveCom, doGetShopBaseInfo } from '@/apis/goods'
import { doGetCategory } from '@/apis/classification'
import { doGetOrderConcernStatusByShopId, doCancelAttentionAndAttention } from '@/apis/consumer'
import type { RetrieveComItemType } from '@/views/classification/types'
import goodPrice from '@/views/home/components/good-price.vue'
import { useUserStore } from '@/store/modules/user'
import { Search } from '@element-plus/icons-vue'
import usePriceRange from '@/composables/usePriceRange'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import useConvert from '@/composables/useConvert'
import Storage from '@/libs/storage'

const storage = new Storage()
const { divTenThousand } = useConvert()
const { range } = usePriceRange()
const $userStore = useUserStore()
type FilterConfigItem = {
  name: string
  value: Array<{
    order: string
    sort: string
  }>

  children?: {
    name: string
    value: Array<{
      order: string
      sort: string
    }>
  }[]
}

const $router = useRouter()
const $route = useRoute()
const isConcern = ref(false)

watch(() => $userStore.getterToken, initAttentionStatus)

onMounted(() => {
  initAttentionStatus()
})

const showcategory = ref(false)
const categoryindex = ref(-1)
// 展示分类数据
const classList = ref<any>([])
// 检索商品列表
const retrieveComList = ref<RetrieveComItemType[]>([])
// 筛选配置
const filterConfig: FilterConfigItem[] = [
  {
    name: '综合排序',
    value: [{ order: '', sort: '' }],
  },
  {
    name: '销量',
    children: [
      {
        name: '销量升序',
        value: [
          {
            order: 'salesVolume',
            sort: 'asc',
          },
        ],
      },
      {
        name: '销量降序',
        value: [
          {
            order: 'salesVolume',
            sort: 'desc',
          },
        ],
      },
    ],
    value: [
      {
        order: 'salesVolume',
        sort: 'desc',
      },
    ],
  },
  {
    name: '新品',
    children: [
      {
        name: '新品升序',
        value: [
          {
            order: 'createTime',
            sort: 'asc',
          },
        ],
      },
      {
        name: '新品降序',
        value: [
          {
            order: 'createTime',
            sort: 'desc',
          },
        ],
      },
    ],
    value: [
      {
        order: 'createTime',
        sort: 'desc',
      },
    ],
  },
  {
    name: '价格',
    children: [
      {
        name: '价格升序',
        value: [
          {
            order: 'salePrices',
            sort: 'asc',
          },
        ],
      },
      {
        name: '价格降序',
        value: [
          {
            order: 'salePrices',
            sort: 'desc',
          },
        ],
      },
    ],
    value: [
      {
        order: 'salePrices',
        sort: 'desc',
      },
    ],
  },
]
// 当前选中筛选下标
const currentChooseFilterStatus = ref([{ order: '', sort: '' }])
const commodityPageConfig = reactive({
  current: 1,
  size: 16,
  pages: 1,
  total: 0,
})
const shopInfo = ref({
  shopId: '',
  shopLogo: '',
  shopName: '',
  newTips: '',
  status: '',
  shopType: '',
})
// 当前选中一级分类Id
const currentChooseClassOne = reactive({
  name: '',
  id: '',
})
// 当前选中二级分类Id
const currentChooseClassSec = reactive({
  name: '',
  id: '',
})
// 当前选中三级分类Id
const currentChooseClassThr = reactive({
  name: '',
  id: '',
})

initShopInfo()
getClassList()
// initRetreveCom()
init()

/**
 * @description: 初始化
 */
async function init() {
  const { level, firstid, firstname, secondid, secondname, thirdid, thirdname } = $route.query

  // if (firstid && firstname && secondid && secondname && thirdid && thirdname) {
  switch (level) {
    case 'LEVEL_1':
      handleClickClassOne(firstid as string, firstname as string)
      break
    case 'LEVEL_2':
      handleClickClassSec(firstid as string, firstname as string, secondid as string, secondname as string)
      break
    case 'LEVEL_3':
      handleClickClassThr(firstid as string, firstname as string, secondid as string, secondname as string, thirdid as string, thirdname as string)
      break
    default:
      break
  }
  // }
}

watch(
  () => $route.query,
  () => {
    init()
  },
)

/**
 * @description: 获取店铺信息
 * @returns {*}
 */
async function initShopInfo() {
  const { code, data, msg } = await doGetShopBaseInfo($route.query.shopId as string)
  if (code !== 200) return ElMessage.error(msg ? msg : '未获取店铺信息')
  shopInfo.value.shopId = data.id
  shopInfo.value.shopName = data.name
  shopInfo.value.shopLogo = data.logo
  shopInfo.value.newTips = data.newTips
  shopInfo.value.shopType = data.shopType
}
/**
 * @description: 查询店铺关注状态
 * @param {*} shopId
 */
async function initAttentionStatus() {
  if ($userStore.getterToken) {
    const { code, data, msg } = await doGetOrderConcernStatusByShopId($route.query.shopId as string)
    if (code !== 200) return ElMessage.error(msg ? msg : '查询关注状态失败')
    isConcern.value = data
  }
}
/**
 * @description: 关注
 * @returns {*}
 */
const handleConcern = async () => {
  const { data, code, msg } = await doCancelAttentionAndAttention(
    shopInfo.value.shopName,
    shopInfo.value.shopId,
    !isConcern.value,
    shopInfo.value.shopLogo,
  )
  if (code !== 200) {
    if (code === 2) return
    return ElMessage.error(msg ? msg : `${isConcern.value ? '取消' : '关注'}失败`)
  }
  ElMessage.success(`${isConcern.value ? '取消' : '关注'}成功`)
  initAttentionStatus()
}
/**
 * @description: 获取分类数据
 */
async function getClassList() {
  const { data, code } = await doGetCategory($route.query.shopId as string, {
    size: 9999,
  })
  if (code !== 200) {
    return ElMessage.error('分类数据获取失败')
  }
  data.records.forEach((item: any) => {
    item.secondShow = true
    if (item.children) {
      item.children.forEach((ite: any) => {
        ite.thirdShow = true
      })
    }
  })

  classList.value = data.records
}
function isChoosed(
  currentStatus: Array<{
    order: string
    sort: string
  }>,
  arr: {
    name: string
    value: Array<{
      order: string
      sort: string
    }>
  }[],
) {
  return arr.filter((item) => item.value[0].order === currentStatus[0].order).length
}
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
  // if (currentChooseFilterStatus.value[0].order !== item.value[0].order) {
  //     // 切换排序
  //     currentChooseFilterStatus.value = item.value
  // } else if (currentChooseFilterStatus.value[0].order === item.value[0].order && item.children) {
  //     // 点击同一个排序切换
  //     const currentStatusIndex = item.children.findIndex((item) => item.value[0].sort === currentChooseFilterStatus.value[0].sort)
  //     currentChooseFilterStatus.value = currentStatusIndex
  //         ? item.children[currentStatusIndex - 1].value
  //         : item.children[currentStatusIndex + 1].value
  // }
  initRetreveCom()
}
/**
 * @description: 分页变化
 */
const handleChangePage = (e: number) => {
  commodityPageConfig.current = e
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}
const handleSizeChange = (e: number) => {
  commodityPageConfig.pages = e
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
async function initRetreveCom(inputVal?: string) {
  const searchParams = {
    categoryFirstId: currentChooseClassOne.id,
    categorySecondId: currentChooseClassSec.id,
    categoryThirdId: currentChooseClassThr.id,
    current: commodityPageConfig.current,
    size: commodityPageConfig.size,
    shopId: $route.query.shopId as string,
    keyword: inputVal || '',
    orderByParams: [{ order: '', sort: '' }] as Array<{ order: string; sort: string }> | null,
    showCoupon: true,
  }
  // 兼容综合排序
  if (currentChooseFilterStatus.value[0].order === '') {
    searchParams.orderByParams = null
  } else {
    searchParams.orderByParams = currentChooseFilterStatus.value
  }
  const { code, data } = await doGetRetrieveCom(searchParams, $route.query.shopId as string)
  retrieveComList.value = data.list
  commodityPageConfig.total = data.total
  commodityPageConfig.pages = data.pages
}
/**
 * @description: 点击一级
 */
function handleClickClassOne(id: string, name: string) {
  currentChooseClassOne.id = id
  currentChooseClassOne.name = name
  currentChooseClassSec.id = ''
  currentChooseClassSec.name = ''
  currentChooseClassThr.id = ''
  currentChooseClassThr.name = ''
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}
/**
 * @description: 点击二级分类
 */
function handleClickClassSec(Oneid: string, Onename: string, id: string, name: string) {
  currentChooseClassOne.id = Oneid
  currentChooseClassOne.name = Onename
  currentChooseClassSec.id = id
  currentChooseClassSec.name = name
  currentChooseClassThr.id = ''
  currentChooseClassThr.name = ''
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}
/**
 * @description: 点击三级分类
 */
function handleClickClassThr(Oneid: string, Onename: string, Secid: string, Secname: string, id: string, name: string) {
  currentChooseClassOne.id = Oneid
  currentChooseClassOne.name = Onename
  currentChooseClassSec.id = Secid
  currentChooseClassSec.name = Secname
  currentChooseClassThr.id = id
  currentChooseClassThr.name = name
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}
/**
 * @description:全部分类
 */
function handleClickAll() {
  currentChooseClassOne.id = ''
  currentChooseClassOne.name = ''
  currentChooseClassSec.id = ''
  currentChooseClassSec.name = ''
  currentChooseClassThr.id = ''
  currentChooseClassThr.name = ''
  initRetreveCom()
  document.querySelector('#referenceVal')?.scrollIntoView()
}

const mainBox = ref()
const topGo = ref(true)
const handleScroll = () => {
  let winHeight = mainBox.value?.getBoundingClientRect().top
  if (winHeight > 290) topGo.value = true
  else topGo.value = false
}
onMounted(() => {
  window.addEventListener('scroll', handleScroll, true)
})

const shopProperties = ref()
const swiper = ref()
setTimeout(() => {
  shopProperties.value = storage.getItem('shopPropertiesList') || ''
  swiper.value = shopProperties.value?.components?.filter((item: { value: string }) => item?.value === 'swiper')
}, 300)

const inputVal = ref('')
const searchFn = () => {
  initRetreveCom(inputVal.value)
}

onBeforeMount(() => {
  handleClickAll()
})
</script>

<template>
  <div id="referenceVal" ref="mainBox" class="con">
    <div class="mainBoxs">
      <!-- 左 -->
      <div class="mainBoxs__left">
        <div class="recommendTltle">
          <div class="recommendTltle__Title">
            <span class="recommendTltle__Title--line" />
            <span class="recommendTltle__Title--circle" />
            <span class="recommendTltle__Title--tle" @click="handleClickAll">商品分类</span>
            <span class="recommendTltle__Title--circle" />
            <span class="recommendTltle__Title--line" />
          </div>
          <div class="recommendTltle__title">CLASSIFICATION</div>
        </div>
        <div style="height: calc(1534px - 125px); overflow-y: scroll">
          <div v-for="first in classList" :key="first.id" class="mainBoxs__left--nameBox">
            <div class="mainBoxs__left--firstNameBox" :style="!first.secondShow ? 'border-bottom: 0px' : ''">
              <div
                class="mainBoxs__left--firstName"
                :class="currentChooseClassOne.id === first.id ? 'choosed' : ''"
                @click="handleClickClassOne(first.id, first.name)"
              >
                {{ first.name }}
              </div>
              <el-icon v-if="first.secondShow" style="color: #444; cursor: pointer" @click="first.secondShow = !first.secondShow">
                <ArrowDown />
              </el-icon>
              <el-icon v-else style="color: #444; cursor: pointer" @click="first.secondShow = !first.secondShow">
                <ArrowRight />
              </el-icon>
            </div>
            <template v-if="first.secondShow">
              <div style="display: flex; flex-wrap: wrap">
                <div v-for="second in first.children" :key="second.id" class="mainBoxs__left--secondNameBox">
                  <div
                    :class="currentChooseClassSec.id === second.id ? 'choosed' : ''"
                    @click="handleClickClassSec(first.id, first.name, second.id, second.name)"
                  >
                    {{ second.name }}
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
      <!-- 右 -->
      <div class="mainBoxs__right">
        <!-- 排序 -->
        <div class="mainBoxs__navigationsBar">
          <div class="mainBoxs__navigationsBar--bar">
            <div class="barLi" style="margin-right: 58px" @click="handleChooseFilterStatus([{ order: '', sort: '' }])">
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
          <!-- 搜素框 -->
          <div>
            <el-input v-model="inputVal" placeholder="请输入商品名称">
              <template #append>
                <el-button :icon="Search" @click="searchFn" />
              </template>
            </el-input>
          </div>
        </div>
        <div style="min-height: 1492px">
          <div class="mainBoxs__shopLists">
            <div v-for="item in retrieveComList" :key="item.id" class="mainBoxs__shopLists--shopLi" @click="gotoDetail(item.productId, item.shopId)">
              <div class="mainBoxs__shopLists--imgBox">
                <img :src="item.pic" />
              </div>
              <div class="Price">
                <good-price :price="range(item.salePrices[0]).toString()" member-info="" />
                <div class="Price__delprice">
                  {{ divTenThousand(item?.prices[0]) }}
                </div>
              </div>
              <div class="mainBoxs__shopLists--titleBox">
                {{ item.productName }}
              </div>
              <div class="mainBoxs__shopLists--discountCouponList">
                <span
                  v-for="(coupon, inde) in item?.couponList"
                  :key="inde"
                  truncate
                  class="discountCoupon"
                  :class="[inde === 2 && coupon.sourceDesc.length > 2 ? 'spot' : '']"
                  >{{ coupon.sourceDesc }}</span
                >
              </div>
              <div class="mainBoxs__shopLists--paymentNum">
                <span v-if="+useConvertString(Number(item.salesVolume)) > 0">已售 {{ useConvertString(Number(item.salesVolume)) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 分页 -->
    <el-pagination
      v-if="+commodityPageConfig.total > 0"
      v-model:current-page="commodityPageConfig.current"
      v-model:page-size="commodityPageConfig.size"
      class="pagination"
      small
      :page-sizes="[16, 24, 32, 40]"
      background
      layout="total, jumper, prev, pager, next, sizes"
      :total="+commodityPageConfig.total"
      @size-change="handleSizeChange"
      @current-change="handleChangePage"
    />
    <ToTopGoCar :top-go="topGo" />
  </div>
</template>

<style lang="scss" scoped>
.choosed {
  color: rgb(245, 108, 108);
}
.arrow__top {
  transform: translateY(3px);
}
.arrow__bottom {
  transform: translateY(-3px);
}
</style>
<style lang="scss" scoped>
@include b(con) {
  width: 1200px;
  margin: 0 auto;
  position: relative;
  // padding-bottom: 24px;
}
@include b(greetingBox) {
  height: 40px;
  background-color: #f2f2f2;
  @include e(greeting) {
    display: flex;
    justify-content: space-between;
    height: 40px;
    line-height: 40px;
    width: 1200px;
    font-size: 16px;
    margin: 0 auto;
    @include m(left) {
      color: #e90000;
      display: flex;
      img {
        width: 12px;
        height: 12px;
        transform: translateY(14px);
      }
      span {
        padding-left: 10px;
        width: 650px;
        text-align: left;
      }
    }
    @include m(right) {
      display: flex;
    }
    @include m(shopName) {
      width: 172px;
      overflow: hidden;
      margin-right: 16px;
      color: #141414;
      line-height: 40px;
      span {
        display: inline-block;
        height: 18px;
        line-height: 15.5px;
        font-size: 12px;
        zoom: 0.9;
        transform: scale(0.9) translateY(-2px);
        color: #fff;
        padding: 2px 6px;
        border-radius: 2px;
        background-color: #e00500;
        margin-right: 8px;
      }
    }
    @include m(relation) {
      font-size: 14px;
      color: #999;
      cursor: pointer;
      img {
        width: 16px;
        height: 16px;
        margin-right: 8px;
        transform: translateY(3px);
      }
    }
  }
}
@include b(imageDrawing) {
  height: 200px;
  width: 1920px;
  margin: 0 auto;
  img {
    width: 100%;
    height: 100%;
  }
}
@include b(navigationBarBox) {
  height: 42px;
  background-color: #fff;
  @include e(navigationBar) {
    width: 1200px;
    line-height: 42px;
    margin: 0 auto;
    display: flex;
    @include m(navigation) {
      width: 152px;
      height: 42px;
      position: relative;
      z-index: 1;
      background: linear-gradient(90deg, rgb(245, 67, 25) 0%, rgb(253, 146, 36) 100%);
      color: #fff;
      line-height: 38px;
      margin-right: 60px;
    }
    @include m(navigations) {
      padding-left: 60px;
      div {
        display: inline-block;
        margin-right: 95px;
        cursor: pointer;
        position: relative;
        font-weight: bold;
        span {
          display: flex;
          align-items: center;
          flex-direction: column;
          line-height: 22px;
        }
        img {
          position: absolute;
          bottom: 6px;
          left: 50%;
          transform: translateX(-50%);
        }
      }
    }
    @include m(pullDown) {
      display: flex;
      position: relative;
      z-index: 3;
    }
    @include m(fristBarBox) {
      width: 152px;
      height: 520px;
      background: rgba(255, 255, 255, 0.8);
      // display: flex;
      // justify-content: space-between;
      // flex-wrap: wrap;
      border-right: 1px solid #d9d9d9;
    }
    @include m(fristBar) {
      width: 152px;
      cursor: pointer;
      font-size: 16px;
      color: #333;
      padding: 12px 16px;
      display: flex;
      justify-content: space-between;
      line-height: 22px;
      height: 46px;
      &:hover {
        color: #f54319;
        height: 46px;
      }
    }
  }
  @include e(rightBar) {
    min-width: 736px;
    height: 520px;
    padding: 24px;
    font-size: 16px;
    background: rgba(255, 255, 255, 0.8);
    overflow-x: auto;
    color: #333;
    @include m(title) {
      margin-bottom: 24px;
      font-weight: bold;
      cursor: pointer;
      text-align: left;
      span {
        display: block;
        line-height: 22px;
        margin-bottom: 24px;
      }
    }
    @include m(mian) {
      display: flex;
      font-weight: normal;
      height: 22px;
      line-height: 22px;
      div {
        margin-right: 24px;
        &:hover {
          color: #f54319;
        }
      }
    }
  }
}
.choosed {
  color: rgb(245, 108, 108);
}
.arrow__top {
  transform: translateY(3px);
}
.arrow__bottom {
  transform: translateY(-3px);
}
@include b(recommendTltle) {
  height: 60px;
  margin: 24px auto 16px;
  @include e(Title) {
    @include m(line) {
      display: inline-block;
      width: 36px;
      border-bottom: 2px solid rgb(245, 67, 25);
      transform: translateY(-8px);
    }
    @include m(circle) {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: #f54319;
      transform: translateY(-3.5px);
    }
    @include m(tle) {
      display: inline-block;
      height: 33px;
      font-size: 24px;
      line-height: 33px;
      font-weight: 500;
      color: rgb(51, 51, 51);
      margin: 0 24px 6px;
    }
  }
  @include e(title) {
    font-size: 14px;
    line-height: 20px;
    color: #8c8c8c;
  }
}
@include b(recommendBox) {
  width: 1201px;
  display: flex;
  flex-wrap: wrap;
  text-align: left;
  @include e(item) {
    width: 221px;
    height: 349px;
    background-color: #fff;
    padding: 16px;
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
          margin-top: 2px;
          height: 17px;
          background: rgb(251, 202, 58);
          border-radius: 10px;
          color: rgb(97, 70, 19);
          line-height: 14px;
          padding: 2px 6px;
        }
        @include e(del) {
          margin-top: 2px;
          color: rgb(153, 153, 153);
          text-decoration-line: line-through;
          line-height: 11px;
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
      height: 16px;
      @include b(discountCoupon) {
        font-size: 10px;
        padding: 1px 4px;
        margin-right: 8px;
        // line-height: 16px;
        color: rgb(245, 67, 25);
        border-radius: 2px;
        background-color: rgba(245, 67, 25, 0.05);
        white-space: nowrap;
        display: inline-block;
        transform: translateY(-3px);
      }
    }
    @include m(paymentNum) {
      height: 14px;
      text-align: right;
      font-size: 10px;
      color: rgb(153, 153, 153);
    }
  }
}
@include b(pagination) {
  display: flex;
  justify-content: center;
  height: 24px;
}
@include b(mainBoxs) {
  @include e(navigationsBar) {
    width: 955px;
    height: 42px;
    line-height: 42px;
    font-size: 14px;
    background-color: #fff;
    display: flex;
    margin-bottom: 16px;
    justify-content: space-between;
    padding: 0 16px 0 34px;
    @include m(bar) {
      display: flex;
    }
  }
  @include e(shopLists) {
    display: flex;
    flex-wrap: wrap;
    text-align: left;
    @include m(shopLi) {
      width: 221px;
      height: 349px;
      margin: 8px 24px 16px 0;
      padding: 16px;
      background-color: #fff;
      &:nth-of-type(4n) {
        margin-right: 0;
      }
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
    @include b(Price) {
      font-size: 12px;
      display: flex;
      height: 22px;
      margin: 12px 0 8px 0;
      @include e(delprice) {
        font-size: 12px;
        text-decoration-line: line-through;
        color: #999;
        margin-left: 4px;
        transform: translateY(3px);
      }
    }
    @include m(titleBox) {
      font-size: 14px;
      line-height: 20px;
      height: 40px;
      overflow: hidden;
    }
    @include m(discountCouponList) {
      margin-top: 8px;
      white-space: nowrap; /* 禁止文本换行 */
      overflow: hidden; /* 隐藏超出部分 */
      @include b(discountCoupon) {
        font-size: 12px;
        padding: 2px 4px;
        margin-right: 8px;
        color: rgb(245, 67, 25);
        border-radius: 2px;
        background-color: rgba(245, 67, 25, 0.05);
        white-space: nowrap;
        display: inline-block;
        overflow: hidden;
      }
      @include b(spot) {
        width: 27px;
        white-space: nowrap; /* 禁止文本换行 */
        overflow: hidden; /* 隐藏超出部分 */
        text-overflow: ellipsis; /* 超出部分显示为省略号 */
      }
    }
    @include m(paymentNum) {
      margin-top: 8px;
      height: 14px;
      text-align: right;
      font-size: 12px;
      color: rgb(153, 153, 153);
      span {
        display: inline-block;
        line-height: 14px;
      }
    }
  }
}
@include b(mainBoxs) {
  display: flex;
  margin-top: 24px;
  @include e(left) {
    width: 221px;
    height: 1534px;
    background-color: #fff;
    margin-right: 23px;
    flex-shrink: 0;
    // margin-bottom: 32px;
    @include b(recommendTltle) {
      height: 101px;
      padding-top: 21px;
      border-bottom: 1px solid rgba(153, 153, 153, 0.5);
      margin: 24px auto 0;
      @include e(Title) {
        @include m(tle) {
          cursor: pointer;
          font-size: 20px;
          margin: 5px 16px;
        }
        @include m(line) {
          width: 24px;
        }
        @include m(circle) {
        }
      }
      @include e(title) {
        padding-bottom: 16px;
      }
    }
    @include m(nameBox) {
      border-bottom: 1px solid rgba(153, 153, 153, 0.5);
      &:last-child {
        border-bottom: 1px dashed rgba(153, 153, 153, 0.5);
      }
    }
    @include m(firstNameBox) {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 16px;
      border-bottom: 1px dashed rgba(153, 153, 153, 0.5);
    }
    @include m(firstName) {
      font-size: 16px;
      font-weight: bold;
      line-height: 54px;
      color: #333;
      cursor: pointer;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
    @include m(secondNameBox) {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 15px 0;
      font-size: 16px;
      color: #999;
      font-weight: normal;
      flex-wrap: wrap;
      div {
        cursor: pointer;
        width: 80px;
        height: 22px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 16px;
      }
    }
  }
  @include e(right) {
    width: 952px;
  }
}
@include b(barLi) {
  position: relative;
  margin-right: 48px;
  cursor: pointer;
  img {
    transform: translateY(2px);
  }
  @include e(imgBox) {
    position: absolute;
    top: -8px;
    left: -20px;
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
:deep().el-input-group {
  border: 1px solid #f54319;
  border-radius: 100px;
  overflow: hidden;
  height: 26px;
}
:deep().el-input-group__append {
  border-radius: 100px;
  height: 26px;
  color: #fff;
  background: linear-gradient(90deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 100%);
  transform: translate(1px, -1px);
}
:deep().el-input__inner {
  font-size: 12px;
  &::placeholder {
    color: #d9d9d9;
  }
}
@include b(pagination) {
  display: flex;
  justify-content: center;
  height: 24px;
}
</style>
