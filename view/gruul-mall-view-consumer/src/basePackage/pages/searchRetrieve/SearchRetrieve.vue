<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import GoodsList from './components/goodsList.vue'
import ShopList from './components/shopList.vue'
import { doGetRetrieveCommodity, doGetshops } from '@/apis/good'
import type { RetrieveParam } from '@/apis/good'
import type { RetrieveCommodity, Shops } from '@/apis/good/model'

const searchParams = reactive<Partial<RetrieveParam>>({
  keyword: '',
  orderByParams: [],
  categoryThirdId: '',
  categorySecondId: '',
  platformCategoryThirdId: '',
  platformCategoryThirdName: '',
  platformCategorySecondId: '',
  shopId: '0',
})
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const loadMoreStatus = ref('nomore')
const preload = ref(false)

const list = ref<(RetrieveCommodity | Shops)[]>([])
const goodsListRef = ref()

const handleSearch = (e: string) => {
  searchParams.keyword = e
  initList()
}

const initList = async (isLoad = false) => {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    pageConfig.pages = 1
    loadMoreStatus.value = 'loadmore'
    list.value = isGoods.value ? await getGoodList() : await getShopList()
    // 自定义样式弹窗
    preload.value = true
    uni.showLoading({
      title: '加载中...',
      mask: true,
    })
    setTimeout(() => {
      preload.value = false
      uni.hideLoading()
    }, 1600)
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = [...list.value, ...(isGoods.value ? await getGoodList() : await getShopList())]
  }
}

// 获取商品列表
async function getGoodList() {
  const tempParams = {} as Partial<RetrieveParam>
  for (let [key, value] of Object.entries(searchParams)) {
    if ((value && key !== 'shopId') || (key === 'shopId' && Number(value))) {
      tempParams[key as keyof Partial<RetrieveParam>] = value as any
    }
  }
  const { code, data, msg } = await doGetRetrieveCommodity({ ...tempParams, ...pageConfig, showCoupon: true, showSku: true })
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: `${msg || '获取商品列表失败'}`,
    })
    return []
  }
  if (data) {
    pageConfig.pages = data.pages
    const temp = data.list.map((item) => {
      if (item.stockTypes.every((e) => e !== 'UNLIMITED')) {
        // 检测是否无库存
        item.sellOut = item.stocks.every((stock) => stock === 0)
      }
      const highLight = item.highLight ? item.highLight : item.productName
      return {
        ...item,
        highLight: highLight,
      }
    })
    if (data.pageNum >= data.pages) {
      loadMoreStatus.value = 'nomore'
    } else {
      loadMoreStatus.value = 'loadmore'
    }

    return temp
  }
  return []
}
// 获取店铺列表
async function getShopList() {
  const { code, data } = await doGetshops({ name: searchParams.keyword as string, ...pageConfig })
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取商品店铺失败',
    })
    return []
  }
  if (data) {
    pageConfig.pages = data.pages
    if (data.pageNum >= data.pages) {
      loadMoreStatus.value = 'loadmore'
    } else {
      loadMoreStatus.value = 'nomore'
    }
    return data.records
  }
  return []
}
const updateorderByParams = (e: any) => {
  searchParams.orderByParams = e
}

// 店铺/商品
const isGoods = ref(true)
// 切换商品或者店铺
const changeGoods = () => {
  if (String(searchParams.shopId) !== '0') {
    return
  }
  isGoods.value = !isGoods.value
  list.value = []
  initList()
}
onLoad((params) => {
  uni.$emit('updateTitle')
  if (!params) return
  if (params.isGoods !== undefined) {
    isGoods.value = JSON.parse(params.isGoods)
  }
  Object.keys(searchParams).forEach((item) => {
    if (params[item]) {
      searchParams[item as keyof typeof searchParams] = params[item]
    }
  })
  if (params) initList()
})
</script>

<template>
  <view>
    <q-nav :title="searchParams.platformCategoryThirdName ? searchParams.platformCategoryThirdName : '商品列表'" />
    <view class="title">
      <view v-if="!searchParams.platformCategoryThirdName" style="display: flex; margin-right: 13rpx" @click="changeGoods">
        {{ isGoods ? '商品' : '店铺' }}
        <q-icon v-if="String(searchParams.shopId) === '0'" name="icon-qiehuan" size="38rpx" />
      </view>
      <u-search
        v-model="searchParams.keyword"
        class="title__search"
        shape="round"
        placeholder="请填写关键词"
        action-text="搜索"
        bg-color="#F6F7F9"
        :maxlength="30"
        @custom="handleSearch"
      />
    </view>
    <GoodsList
      v-if="isGoods"
      ref="goodsListRef"
      v-model:list="list"
      :loadMoreStatus="loadMoreStatus"
      :preload="preload"
      @updateorderByParams="updateorderByParams"
      @initList="initList"
    />
    <ShopList v-else ref="goodsListRef" v-model:list="list" :loadMoreStatus="loadMoreStatus" @initList="initList" />
  </view>
</template>

<style lang="scss" scoped>
page {
  background: #fcfcfc;
}
@include b(title) {
  background-color: #fff;
  padding: 0 28rpx;
  width: 100%;
  @include flex(space-between);
  @include e(search) {
    flex: 1;
  }
  & > .icon {
    transform: rotate(90deg);
  }
}
@include b(sort) {
  background: #fff;
  height: 114rpx;
  padding: 0 40rpx;
  margin-bottom: 10rpx;
  @include flex(space-between);
  font-size: 24rpx;
  font-weight: bold;
  @include e(price) {
    @include flex();
  }
  @include e(flex) {
    display: flex;
    flex-direction: column;
  }
  @include e(top) {
    transform: rotate(180deg) translateY(-10rpx);
  }
  @include e(bottom) {
    transform: translateY(-10rpx);
  }
}
.active {
  color: #f54319;
}

:deep(.uni-toast) {
  width: 6em;
  .uni-toast__icon {
    width: 30px;
    height: 30px;
  }
}
</style>
