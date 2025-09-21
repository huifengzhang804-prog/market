<script setup lang="ts">
import { ref, watch } from 'vue'
import { useEventListener } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import { useConversionPrice, useConvertString } from '@/hooks'
import { doGetCommodityByLevel } from '@/apis/classification'
import { doGetRetrieveCom, doPostBrandName, doGetBrandDetail, doGetBrandlist, postBrandFollow } from '@/apis/goods'
import { doGetGuessYouLike } from '@/apis/user'
import { ArrowRight, Search } from '@element-plus/icons-vue'
import { useSearchBykeyword } from '@/store/modules/search'
import type { ClassItemType, RetrieveComItemType, SortType } from './types'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import { useUserStore } from '@/store/modules/user'
import { useRouterNewWindow } from '@/utils/useRouter'

document.querySelector('#toTop')?.scrollIntoView()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const { openNewWindow } = useRouterNewWindow()
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

const keyword = ref('')
const $router = useRouter()
const $route = useRoute()
// const page = reactive({ current: 1, size: 10 })
const levelOnePageConfig = reactive({
  current: 1,
  size: 30,
  pages: 1,
  level: 'LEVEL_1',
  parentId: '0',
})
const levelTwoPageConfig = reactive({
  current: 1,
  size: 30,
  pages: 1,
  level: 'LEVEL_2',
  parentId: '0',
})
const levelThrPageConfig = reactive({
  current: 1,
  size: 30,
  pages: 1,
  level: 'LEVEL_3',
  parentId: '0',
})
const commodityPageConfig = reactive({
  current: 1,
  size: 20,
  pages: 1,
  total: 0,
})
const $searchBykeyword = useSearchBykeyword()
const topKeyword = computed(() => $searchBykeyword.keyword)
// 排序
// const commoditySortConfig = reactive<SortType[]>([])
// 1级分类
const levelOneArr = ref<ClassItemType[]>([])
// 2级分类
const levelTwoArr = ref<ClassItemType[]>([])
// 3级分类
const levelThrArr = ref<ClassItemType[]>([])
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
const classOneRef = ref<HTMLDivElement>()
const classTwoRef = ref<HTMLDivElement>()
const classThrRef = ref<HTMLDivElement>()
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
const likeData = ref<
  Array<{
    lowestPrice: string
    productAlbumPics: string
    productId: string
    productName: string
    salesVolume: string
    shopId: string
    evaluated: string
  }>
>([])
const Alphabet = ref<string[]>([])
const brandIdList = ref<string[]>([])
const brand = ref<any>({}) //所有品牌数据
const showbrandList = ref<any>([])
const brandInfo = ref<any>({})
const choosebrandIdList = ref<string[]>([]) //选中的品牌id
const openType = ref(true)
const MultipleType = ref(false)
const AlphabetChooseIndex = ref(0)

useEventListener(classOneRef, 'scroll', function (e) {
  if (classOneRef.value) {
    const isLower = classOneRef.value.scrollLeft + classOneRef.value.clientWidth === classOneRef.value.scrollWidth
    if (isLower && levelOnePageConfig.current <= levelOnePageConfig.pages) {
      initClassOne(true)
    }
  }
})
useEventListener(classTwoRef, 'scroll', function (e) {
  if (classTwoRef.value) {
    const isLower = classTwoRef.value.scrollLeft + classTwoRef.value.clientWidth === classTwoRef.value.scrollWidth
    if (isLower && levelTwoPageConfig.current <= levelTwoPageConfig.pages) {
      initClassTwo(levelTwoPageConfig.parentId, true)
    }
  }
})
useEventListener(classThrRef, 'scroll', function (e) {
  if (classThrRef.value) {
    const isLower = classThrRef.value.scrollLeft + classThrRef.value.clientWidth === classThrRef.value.scrollWidth
    if (isLower && levelThrPageConfig.current <= levelThrPageConfig.pages) {
      initClassThr(levelThrPageConfig.parentId, true)
    }
  }
})
initClassOne()
initGuessYouLike()

onBeforeMount(() => {
  init()
})

watch(
  () => $route.query.id,
  () => {
    init()
  },
)
watch(
  () => topKeyword.value,
  (val) => {
    keyword.value = val
    initRetreveCom()
  },
  {
    immediate: true,
  },
)

/**
 * @description: 初始化
 */
async function init() {
  const { id, level, name, parentId, firstid, firstname, secondid, secondname } = $route.query
  const classInfo = {
    id,
    level,
    name,
    parentId,
  } as ClassItemType
  const first = {
    id: firstid,
    name: firstname,
    level: 'LEVEL_1',
  } as any
  const second = {
    id: secondid,
    name: secondname,
    level: 'LEVEL_2',
  } as any
  switch (level) {
    case 'LEVEL_1':
      handleClickClassOne(classInfo)
      break
    case 'LEVEL_2':
      handleClickClassOne(first)
      handleClickClassSec(classInfo)
      break
    case 'LEVEL_3':
      handleClickClassOne(first)
      handleClickClassSec(second)
      handleClickClassThr(classInfo)
      break
    default:
      break
  }
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
/**
 * @description: 点击一级
 */
function handleClickClassOne(classInfo: ClassItemType) {
  levelTwoArr.value = []
  levelThrArr.value = []
  levelTwoPageConfig.current = 1
  levelTwoPageConfig.pages = 1
  levelTwoPageConfig.parentId = classInfo.id
  levelThrPageConfig.current = 1
  levelThrPageConfig.pages = 1
  levelThrPageConfig.parentId = ''
  currentChooseClassOne.id = classInfo.id
  currentChooseClassOne.name = classInfo.name
  currentChooseClassSec.id = ''
  currentChooseClassSec.name = ''
  currentChooseClassThr.id = ''
  currentChooseClassThr.name = ''
  showbrandList.value = []
  initClassTwo(classInfo.id)
  initRetreveCom()
}
/**
 * @description: 点击二级分类
 */
function handleClickClassSec(classInfo: ClassItemType) {
  levelThrArr.value = []
  levelThrPageConfig.current = 1
  levelThrPageConfig.pages = 1
  levelThrPageConfig.parentId = classInfo.id
  currentChooseClassSec.id = classInfo.id
  currentChooseClassSec.name = classInfo.name
  currentChooseClassThr.id = ''
  currentChooseClassThr.name = ''
  showbrandList.value = []
  initClassThr(classInfo.id)
  initRetreveCom()
}

const GetBrandlist = async (Id: string) => {
  Alphabet.value = []
  brand.value = {}
  showbrandList.value = []
  const { code, data } = await doGetBrandlist(Id || '0')
  if (code !== 200) return
  if (JSON.stringify(data) === '{}') return
  if (data) {
    Alphabet.value = Object.keys(data)
    Alphabet.value.unshift('全部')
    Alphabet.value.pop()
    brand.value = data
    showbrandList.value = brand.value.allBrands
  }
}

/**
 * @description: 点击三级分类
 */
function handleClickClassThr(classInfo: ClassItemType) {
  currentChooseClassThr.id = classInfo.id
  currentChooseClassThr.name = classInfo.name
  keyword.value = ''
  showbrandList.value = []
  initRetreveCom()
  if (!brandInfo.value.id) {
    GetBrandlist(classInfo.id)
  }
}
/**
 * @description: 分页变化
 */
const handleChangePage = (e: number) => {
  commodityPageConfig.current = e
  initRetreveCom()
  document.querySelector('#toTop')?.scrollIntoView()
}
/**
 * @description: 选择分类
 */
const handleChooseFilterStatus = (item: FilterConfigItem) => {
  if (currentChooseFilterStatus.value[0].order !== item.value[0].order) {
    // 切换排序
    currentChooseFilterStatus.value = item.value
  } else if (currentChooseFilterStatus.value[0].order === item.value[0].order && item.children) {
    // 点击同一个排序切换
    const currentStatusIndex = item.children.findIndex((item) => item.value[0].sort === currentChooseFilterStatus.value[0].sort)
    currentChooseFilterStatus.value = currentStatusIndex ? item.children[currentStatusIndex - 1].value : item.children[currentStatusIndex + 1].value
  }
  initRetreveCom()
}
/**
 * @description: 获取一级分类
 */
async function initClassOne(isLoad = false) {
  const { code, data } = await doGetCommodityByLevel(levelOnePageConfig)
  if (code === 200 && data) {
    levelOneArr.value = isLoad ? levelOneArr.value.concat(data.records) : data.records
    levelOnePageConfig.pages = data.pages
    levelOnePageConfig.current = data.current + 1
  }
}
/**
 * @description: 获取二级分类
 */
async function initClassTwo(parentId: string, isLoad = false) {
  const { code, data } = await doGetCommodityByLevel({ ...levelTwoPageConfig, parentId })
  if (code === 200 && data) {
    levelTwoArr.value = isLoad ? levelTwoArr.value.concat(data.records) : data.records
    levelTwoPageConfig.pages = data.pages
    levelTwoPageConfig.current = data.current + 1
  }
}
/**
 * @description: 获取三级分类
 */
async function initClassThr(parentId: string, isLoad = false) {
  const { code, data } = await doGetCommodityByLevel({ ...levelThrPageConfig, parentId })
  if (code === 200 && data) {
    levelThrArr.value = isLoad ? levelThrArr.value.concat(data.records) : data.records
    levelOnePageConfig.pages = data.pages
    levelOnePageConfig.current = data.current + 1
  }
}
async function initRetreveCom() {
  const searchParams = {
    platformCategoryFirstId: currentChooseClassOne.id,
    platformCategorySecondId: currentChooseClassSec.id,
    platformCategoryThirdId: currentChooseClassThr.id,
    current: commodityPageConfig.current,
    size: commodityPageConfig.size,
    keyword: keyword.value,
    orderByParams: [{ order: '', sort: '' }] as Array<{ order: string; sort: string }> | null,
    brandId: choosebrandIdList.value,
  }
  // 兼容综合排序
  if (currentChooseFilterStatus.value[0].order === '') {
    searchParams.orderByParams = null
  } else {
    searchParams.orderByParams = currentChooseFilterStatus.value
  }
  const { code, data } = await doGetRetrieveCom({ ...searchParams, showCoupon: true })
  retrieveComList.value = data.list
  commodityPageConfig.total = data.total
  commodityPageConfig.pages = data.pages
  if (keyword.value) {
    data.list.forEach((item: any) => {
      if (item.brandId) {
        brandIdList.value.push(item.brandId)
      }
    })
    brandIdList.value = Array.from(new Set(brandIdList.value))
    postBrandName()
  }
}
async function postBrandName() {
  Alphabet.value = []
  brand.value = {}
  showbrandList.value = []
  const { code, data } = await doPostBrandName(brandIdList.value)
  if (code !== 200) return
  if (data) {
    Alphabet.value = Object.keys(data)
    Alphabet.value.unshift('全部')
    Alphabet.value.pop()
    brand.value = data
    showbrandList.value = brand.value.allBrands
  }
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
/**
 * @description: 猜你喜欢
 * @returns {*}
 */
async function initGuessYouLike() {
  // const { code, data } = await doGetGuessYouLike({ size: 15 })
  const { code, data } = await doGetGuessYouLike({ size: 8 })
  if (code !== 200) return
  likeData.value = data.records
}
const gotoshop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId },
  })
}
const gotoCustomerService = async (shopId: string, productId: string) => {
  await doGetMessagesChatRoom(shopId, useUserStore().getterUserInfo.userId)
  openNewWindow('/personalcenter/set/customerservice', { shopId: shopId, productId: productId })
}
const handleAlphabet = (item: any, index: number) => {
  AlphabetChooseIndex.value = index
  if (item === '全部') {
    showbrandList.value = brand.value.allBrands
    return
  }
  showbrandList.value = eval(`brand.value.${item}`)
}
const handlebrand = async (brandId: string, index: number) => {
  if (MultipleType.value) {
    showbrandList.value[index].choosetype = !showbrandList.value[index].choosetype
    choosebrandIdList.value.push(brandId)
  } else {
    getBrandDetail(brandId)
  }
}
const changeMultipleType = async () => {
  MultipleType.value = true
  showbrandList.value.forEach((item: any) => {
    item.choosetype = false
  })
}
const getBrandDetail = async (brandId: string) => {
  const { code, data } = await doGetBrandDetail(brandId)
  if (code !== 200) return
  brandInfo.value = data
  choosebrandIdList.value = [brandId]
  keyword.value = ''
  showbrandList.value = []
  initRetreveCom()
}

const changeBrandFollow = async () => {
  const { code, data, msg } = await postBrandFollow({ brandId: brandInfo.value.id, isFollow: !brandInfo.value.isFollow })
  if (code !== 200) return ElMessage.error(msg ? msg : '关注失败')
  const { code: detailcode, data: detaildata } = await doGetBrandDetail(brandInfo.value.id)
  if (detailcode !== 200) return
  brandInfo.value = detaildata
}
const closebrand = async () => {
  brandInfo.value = {}
  choosebrandIdList.value = []
  handleClickClassThr(currentChooseClassThr as ClassItemType)
}
</script>

<template>
  <!-- 面包屑导航 -->
  <div flex items-center c-w-1285 m-auto e-c-3 c-fs-12 c-h-56>
    <el-breadcrumb :separator-icon="ArrowRight" flex items-center>
      <el-breadcrumb-item c-fs-12> 全部结果 </el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentChooseClassOne.name" c-fs-12>
        {{ currentChooseClassOne.name }}
      </el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentChooseClassSec.name" c-fs-12>
        {{ currentChooseClassSec.name }}
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <el-tag
          v-if="currentChooseClassThr.name"
          :key="currentChooseClassThr.id"
          class="mx-1"
          closable
          type="danger"
          effect="plain"
          @close="handleClickClassSec(currentChooseClassSec as ClassItemType)"
        >
          分类:{{ currentChooseClassThr.name }}
        </el-tag>
        <el-tag v-if="brandInfo.brandName" :key="brandInfo.brandName" class="mx-1" closable type="danger" effect="plain" @close="closebrand">
          品牌:{{ brandInfo.brandName }}
        </el-tag>
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <div e-c-9 flex items-center>
          <div b-1 c-bc-d5d5d5 c-w-193 c-mr-10>
            <el-input v-model="keyword" placeholder="在当前条件下搜索" size="small" @change="initRetreveCom">
              <template #suffix>
                <el-icon class="el-input__icon" cursor-pointer @click="initRetreveCom">
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </div>
          商品共<span c-c-FF1E32>{{ commodityPageConfig.total }}</span
          >个
        </div>
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
  <!-- 选中品牌 -->
  <div v-if="brandInfo.id" ma c-w-1285 bg-white flex c-pt-17 c-pl-23 c-pb-20 c-mb-15>
    <el-image c-w-120 c-h-120 c-mr-28 :src="brandInfo.brandLogo" />
    <div>
      <div flex items-center>
        <div c-fs-22 fw-700>
          {{ brandInfo.brandName }}
        </div>
        <div c-bg-CF2C48 text-center c-w-68 c-h-24 c-lh-24 e-c-f c-br-33 c-ml-14 c-mr-16 c-fs-12 cursor-pointer @click="changeBrandFollow">
          {{ brandInfo.isFollow ? '已关注' : '+关注' }}
        </div>
        <div c-fs-14 c-c-b2b2b2>
          <span fw-700 c-c-101010 c-mr-6>{{ brandInfo.followers }} </span>人关注该品牌
        </div>
      </div>
      <div c-fs-14 c-w-1072 e-c-6 c-ellipsis-3 text-left c-mt-10>
        {{ brandInfo.brandDesc }}
      </div>
    </div>
  </div>
  <!-- 品牌 -->
  <div v-if="showbrandList?.length && !brandInfo.id" ma c-w-1285 text-center c-fs-12 select-none>
    <div flex items-center b-1 c-bc-eaeaea c-bg-f8f8f8>
      <div flex-shrink-0 c-w-133 e-c-6>品牌</div>
      <div e-c-3 c-fs-12 c-pl-40 c-pt-10 c-pb-15 bg-white>
        <div flex items-center c-w-1100 justify-between>
          <div v-if="!openType" flex c-gap-24 items-center>
            <div
              v-for="(item, index) in Alphabet"
              :key="item"
              cursor-pointer
              c-pl-5
              c-pr-5
              :class="AlphabetChooseIndex === index ? 'c-c-ff1e32 b-1 c-bc-ff1e32' : ''"
              @click="handleAlphabet(item, index)"
            >
              {{ item }}
            </div>
          </div>
          <div v-else />
          <div flex items-center c-mr-30>
            <div v-if="!MultipleType" c-w-44 c-h-20 c-lh-20 b-1 c-bc-d5d5d5 cursor-pointer @click="changeMultipleType">+多选</div>
            <div c-ml-23 cursor-pointer @click="openType = !openType">
              {{ openType ? '展开' : '收起' }}
            </div>
          </div>
        </div>
        <div c-mt-10 overflow-hidden e-c-3 flex flex-wrap text-left c-gap-20 :class="openType ? ' c-max-height-145  overflow-y-auto' : ' c-h-65'">
          <div
            v-for="(item, index) in showbrandList"
            :key="item.brandId"
            cursor-pointer
            c-w-150
            c-h-20
            c-lh-20
            flex
            justify-between
            :class="item.choosetype && MultipleType ? 'b-1 c-bc-ff1e32 c-c-ff1e32' : ''"
            @click="handlebrand(item.brandId, index)"
          >
            <div c-ellipsis-1>
              {{ item.brandName }}
            </div>
            <div v-if="item.choosetype && MultipleType" c-w-18 c-h-18 c-bg-ff1e32 e-c-f c-fs-22 c-lh-18 text-center>×</div>
          </div>
        </div>
        <div v-if="MultipleType" flex justify-center items-center c-mt-20>
          <div e-c-f c-bg-FF1E32 c-w-48 c-h-24 c-lh-24 @click="initRetreveCom()">确定</div>
          <div e-c-0 b-1 c-bc-bbb c-w-48 c-h-24 c-lh-24 c-ml-14 @click="MultipleType = false">取消</div>
        </div>
      </div>
    </div>
  </div>
  <!-- 分类 -->
  <div ma c-w-1285 text-center c-fs-12 select-none>
    <div flex items-center b-1 c-bc-eaeaea c-bg-fff>
      <div flex-shrink-0 c-w-133 e-c-6>分类</div>
      <div overflow-hidden bg-white>
        <div ref="classOneRef" flex e-c-3 overflow-x-auto class="box">
          <div
            v-for="item in levelOneArr"
            :key="item.id"
            :class="{ choosed: currentChooseClassOne.id === item.id }"
            flex-shrink-0
            c-min-width-110
            c-lh-55
            cursor-pointer
            @click="handleClickClassOne(item)"
          >
            {{ item.name }}
          </div>
        </div>
        <div ref="classTwoRef" flex e-c-3 overflow-x-auto>
          <div
            v-for="item in levelTwoArr"
            :key="item.id"
            :class="{ choosed: currentChooseClassSec.id === item.id }"
            flex-shrink-0
            c-min-width-110
            c-lh-55
            cursor-pointer
            @click="handleClickClassSec(item)"
          >
            {{ item.name }}
          </div>
        </div>
        <div ref="classThrRef" flex e-c-3 overflow-x-auto>
          <div
            v-for="item in levelThrArr"
            :key="item.id"
            :class="{ choosed: currentChooseClassThr.id === item.id }"
            flex-shrink-0
            c-min-width-110
            c-lh-55
            cursor-pointer
            @click="handleClickClassThr(item)"
          >
            {{ item.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 排序 -->
  <div ma c-w-1285 text-center bg-white c-fs-12>
    <div flex c-h-55 b-1 c-bc-eaeaea>
      <div c-w-133 c-lh-55 e-c-6>排序</div>
      <div flex c-lh-55 e-c-3 select-none>
        <div v-for="item in filterConfig" :key="item.name" c-w-110 @click="handleChooseFilterStatus(item)">
          <div v-if="!item.children" :class="{ choosed: currentChooseFilterStatus[0].order === '' }">
            {{ item.name }}
          </div>
          <div v-else flex items-center justify-center>
            <div v-if="item" :class="{ choosed: isChoosed(currentChooseFilterStatus, item.children) }">
              {{ item.name }}
            </div>
            <div flex flex-col c-ml-4>
              <el-icon
                :color="
                  item.children[0].value[0].order === currentChooseFilterStatus[0].order &&
                  item.children[0].value[0].sort === currentChooseFilterStatus[0].sort
                    ? 'rgb(245, 108, 108)'
                    : '#000'
                "
                class="arrow__top"
              >
                <i-ep-arrowUpBold />
              </el-icon>
              <el-icon
                :color="
                  item.children[1].value[0].order === currentChooseFilterStatus[0].order &&
                  item.children[1].value[0].sort === currentChooseFilterStatus[0].sort
                    ? 'rgb(245, 108, 108)'
                    : '#000'
                "
                class="arrow__bottom"
              >
                <i-ep-arrowDownBold />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 商品 -->
  <div ma c-w-1285 flex justify-center c-mt-9 c-fs-12>
    <!-- 左 c-h-2176 style="overflow: auto" -->
    <!-- <div c-w-190 c-mr-10 c-h-4257> -->
    <div c-w-190 c-mr-10 c-h-2176>
      <div b-1 c-bc-e0e0e0 c-fs-16 c-h-41 c-lh-41 c-pl-14 text-left fw-700 bg-white>商品热销</div>
      <div b-1 c-bc-e0e0e0 text-left bg-white>
        <div
          v-for="item in likeData"
          :key="item.productId"
          c-ml-16
          c-mr-16
          c-pb-10
          c-pt-10
          class="likeData"
          @click="gotoDetail(item.productId, item.shopId)"
        >
          <img :src="item.productAlbumPics" c-w-160 c-h-160 c-mb-7 />
          <div c-w-160 c-ellipsis-2 e-c-3>
            {{ item.productName }}
          </div>
          <div c-c-e31436 fw-700 c-fs-18 c-lh-34>￥ {{ useConversionPrice(item.lowestPrice) }}</div>
          <div v-if="item.evaluated" e-c-9>
            已评价 <text c-c-e31436>{{ item.evaluated }}</text>
          </div>
        </div>
      </div>
    </div>
    <!-- 右 -->
    <div>
      <div c-w-1085 flex flex-wrap content-start c-gap-9>
        <div
          v-for="item in retrieveComList"
          :key="item.id"
          c-w-264
          c-h-418
          bg-white
          c-pt-17
          c-pb-15
          text-left
          c-pl-15
          c-mb-10
          @click="gotoDetail(item.productId, item.shopId)"
        >
          <el-image :src="item.pic" style="width: 234px; height: 234px" />
          <div c-c-ff1e32 c-fs-22 c-mt-21>
            <span c-fs-16>￥</span>{{ useConversionPrice(Number(item.salePrices[0])).toFixed(2) }}
            <span c-c-ccc c-fs-13 c-mt-18 style="text-decoration-line: line-through"
              >￥{{ useConversionPrice(Number(item.prices[0])).toFixed(2) }}</span
            >
          </div>
          <div class="product_name" c-w-236 c-c-101010 c-fs-12>
            {{ item.productName }}
          </div>
          <div class="discountCouponList">
            <span
              v-for="(coupon, inde) in item?.couponList"
              :key="inde"
              truncate
              class="discountCoupon"
              :class="[inde === 3 && coupon.sourceDesc.length > 2 ? 'spot' : '']"
              >{{ coupon.sourceDesc }}</span
            >
          </div>
          <!-- <div c-h-16 c-w-61 c-bg-e31436 e-c-f c-lh-16 text-center c-mt-7>满400减20</div> -->
          <div v-if="+item.salesVolume > 0" c-c-ff1e32 c-mt-6><span c-c-a7a7a7>已售 </span>{{ useConvertString(Number(item.salesVolume)) }}</div>
          <div e-c-6 c-mt-6 style="display: flex; align-items: center">
            <span underline cursor-pointer @click.stop="gotoshop(item.shopId)">{{ item.shopName }}</span>
            <span
              v-if="getterPropertiesList?.otherData?.service"
              style="position: relative; top: 1px"
              @click.stop="gotoCustomerService(item.shopId, item.productId)"
            >
              <QIcon cursor-pointer name="icon-lianxikefu" color="#F54319" size="18px" style="margin-left: 5px" />
            </span>
          </div>
        </div>
      </div>
      <!-- 分页 -->
      <div flex content-center>
        <el-pagination
          v-if="commodityPageConfig.total"
          background
          layout="prev, pager, next"
          class="ma c-mt-18 c-mb-19"
          :total="+commodityPageConfig.total"
          :page-count="commodityPageConfig.pages"
          @current-change="handleChangePage"
        />
      </div>
      <ToTopGoCar />
    </div>
  </div>
</template>

<style scoped lang="scss">
.choosed {
  color: rgb(245, 108, 108);
}
.arrow__top {
  transform: translateY(3px);
}
.arrow__bottom {
  transform: translateY(-3px);
}
.product_name {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin: 3px;
  line-height: 18px;
}
@include b(likeData) {
  &:last-child {
    border: 0;
  }
}
@include b(discountCouponList) {
  width: 235px;
  margin-top: 3px;
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
    width: 47px;
    white-space: nowrap; /* 禁止文本换行 */
    overflow: hidden; /* 隐藏超出部分 */
    text-overflow: ellipsis; /* 超出部分显示为省略号 */
  }
}
/* 自定义整个滚动条 */
.box::-webkit-scrollbar {
  /*滚动条整体样式*/
  height: 5px;
}
</style>
