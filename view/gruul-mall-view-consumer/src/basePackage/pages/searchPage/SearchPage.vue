<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useNavBack, useStatusBar } from '@/hooks'
import { doGetHistoriesAndHotWords, doGetSearchSuggest, doDeleteUserHistory } from '@/apis/good'
import RichText from '@decoration/components/rich-text/rich-text.vue'
// @ts-ignore
import URow from '@/uni_modules/vk-uview-ui/components/u-row/u-row.vue'
import { useUserStore } from '@/store/modules/user'
import storage from '@/utils/storage'

// 历史搜索词
const historyWords = ref<string[]>([])
// 热门关键词
const hotWords = ref<string[]>([])
// 搜索词语
const queryName = ref('')
// 输入框提示
const placeholderVal = ref('')
const suggestsParam = reactive({
  showActions: false,
  suggests: [] as Array<{ productName: string; highLight: string }>,
})
// 商户Id
const shopId = ref<string>('0')

onLoad((option = {}) => {
  const params = JSON.parse(option.formData || '{}')
  placeholderVal.value = params?.keyWord || ''
  shopId.value = option.shopId
})
onShow(() => {
  loadHHW()
})

// 存入本地的key
const getkey = () => (isGoods.value ? 'historyWords' : 'historyShop')

//  查询热词 与 搜索历史
const loadHHW = () => {
  const histories = storage.get(getkey())
  historyWords.value = histories || []
  if (isGoods.value) {
    doGetHistoriesAndHotWords().then(({ code, data }) => {
      if (code !== 200) return
      //搜索热词
      hotWords.value = data.hotWords
      //搜索历史
      const words = data.histories
      if (words.length > 0) {
        historyWords.value = words
      }
    })
  }
}
const handleSearch = () => {
  if (!queryName.value && !placeholderVal.value) {
    // useNavBack()
  } else {
    const str = `keyword=${queryName.value || placeholderVal.value}&shopId=${shopId.value}&isGoods=${isGoods.value}`
    uni.navigateTo({
      url: `/basePackage/pages/searchRetrieve/SearchRetrieve?${str}`,
    })
    addStorage()
  }
}

const getSuggests = (value: string) => {
  if (!value) {
    suggestsParam.showActions = false
    suggestsParam.suggests = []
    return
  }
  if (isGoods.value) {
    const params: { keyword: string; shopId?: string } = {
      keyword: value,
    }
    if (shopId.value !== '0') params.shopId = shopId.value

    doGetSearchSuggest(params).then(({ code, data }) => {
      if (code !== 200) {
        suggestsParam.showActions = false
        suggestsParam.suggests = []
      }
      suggestsParam.suggests = data
      suggestsParam.showActions = true
    })
  }
}
/**
 * 选中关键词检索
 */
const handleChooseKey = (key: string) => {
  queryName.value = (key ? key.trim() : '').substring(0, 20)
  handleSearch()
}
/**
 * 清除缓存
 */

const handleClearStorage = () => {
  uni.showModal({
    title: '提示',
    content: '确定删除搜索历史吗?',
    success(res) {
      if (!res.confirm) {
        return
      }
      storage.remove(getkey())
      historyWords.value = []
      uni.showToast({
        title: '删除成功',
        icon: 'none',
        duration: 800,
      })
      if (useUserStore().userInfo.token) {
        doDeleteUserHistory()
      }
    },
  })
}
/**
 * 历史记录搜索放入storage
 */
function addStorage() {
  const value = queryName.value
  if (!value || !value.trim()) {
    return
  }

  const historyWordStorage = storage.get(getkey()) || []
  const sameWordIndex = historyWordStorage.findIndex((word: string) => word === value)
  if (sameWordIndex !== -1) {
    historyWordStorage.splice(sameWordIndex, 1)
  }
  historyWordStorage.unshift(value)
  if (historyWordStorage.length > 6) historyWordStorage.pop()
  storage.set(getkey(), historyWordStorage)
  historyWords.value = historyWordStorage
}

// 店铺/商品
const isGoods = ref(true)
// 切换商品或者店铺
const changeGoods = () => {
  if (String(shopId.value) !== '0') {
    return
  }
  isGoods.value = !isGoods.value
  queryName.value = ''
  loadHHW()
  getSuggests('')
}
const height = computed(() => {
  // 总高度减去 nav 导航栏高度
  let navHeight = useStatusBar().value
  // #ifdef H5
  navHeight += 44
  // #endif
  return useScreenHeight().value - navHeight - useBottomSafe().value
})
</script>

<template>
  <view>
    <q-nav title="搜索" bg-color="#fff" />
    <scroll-view scroll-y :style="{ height: `${height}px` }">
      <view class="page">
        <view class="page__title">
          <view style="display: flex" @click="changeGoods">
            {{ isGoods ? '商品' : '店铺' }}
            <q-icon v-if="String(shopId) === '0' || !shopId" name="icon-qiehuan" size="38rpx" />
          </view>
          <u-search
            v-model="queryName"
            :placeholder="placeholderVal || '请填写关键词'"
            shape="round"
            focus
            class="page__title--search"
            bg-color="#F6F7F9"
            :maxlength="30"
            @custom="handleSearch"
            @change="getSuggests"
          />
        </view>
        <u-cell-group v-show="suggestsParam.showActions" :border="false">
          <u-cell-item v-for="(suggest, index) in suggestsParam.suggests" :key="index" :arrow="false" @click="handleChooseKey(suggest.productName)">
            <template #title>
              <rich-text :nodes="suggest.highLight" />
            </template>
          </u-cell-item>
        </u-cell-group>
        <view>
          <u-row class="suggest"> </u-row>
        </view>
        <view v-if="historyWords && historyWords.length > 0">
          <view class="cell mtb30">
            <view class="cell__title">搜索历史</view>
            <q-icon name="icon-shanchu1" @click="handleClearStorage" />
          </view>
          <view class="history__box">
            <view v-for="(item, index) in historyWords" :key="index" class="page__item">
              <text class="page__item--word" :data-variable="item" @click="handleChooseKey(item)">{{ item }}</text>
            </view>
          </view>
        </view>

        <view v-if="hotWords && hotWords.length > 0 && isGoods">
          <view class="cell mtb30">
            <view class="cell__title">热门搜索</view>
          </view>
          <view class="page__content">
            <view v-for="(hotWord, index) in hotWords" :key="index" class="page__item">
              <text class="page__item--word" :data-variable="hotWord" @click="handleChooseKey(hotWord)">{{ hotWord }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
page {
  background: #fff;
}
@include b(page) {
  background: #fff;
  padding: 10rpx 28rpx 0 28rpx;
  @include e(item) {
    padding: 7px 10px;
    text-align: center;
    max-width: 100%;
    @include m(word) {
      display: block;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      border-radius: 50rpx;
      background: #f5f5f5;
      font-size: 14px;
      padding: 20rpx;
      text-align: center;
    }
  }
  @include e(content) {
    display: flex;
    flex-wrap: wrap;
  }
  @include e(pop) {
    @include m(search) {
      color: #fff;
      font-weight: 800;
      margin-right: 10px;
      background: #fe5468;
      padding: 0 20px;
      border-radius: 20px;
    }
  }
  @include e(title) {
    @include flex(space-between);
    width: 100%;
    @include m(search) {
      margin-left: 13rpx;
      flex: 1;
    }
    & > .icon {
      transform: rotate(90deg);
    }
  }
}
.suggest {
  color: #8c939d;
  padding: 10rpx;
  margin: 5rpx 0;
}
.custom-icon {
  color: #bbbbbb;
  font-size: large;
}
.history__box {
  @include flex(flex-start);
  flex-wrap: wrap;
  overflow: hidden;
}
@include b(cell) {
  @include flex(space-between);
  @include e(title) {
    font-size: 30rpx;
    font-weight: bold;
  }
}
@include b(mtb30) {
  margin: 30rpx 0;
}
</style>
