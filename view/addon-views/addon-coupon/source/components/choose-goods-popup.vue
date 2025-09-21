<script lang="ts" setup>
import { ref, shallowReactive, reactive, watch, onMounted, defineExpose, PropType } from 'vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'
import { doGetHighestCategoryLevel } from '@/apis/good'
import { doGetRetrieveCommodity } from '@/apis/decoration'
import { Search, Check } from '@element-plus/icons-vue'
import type { ApiGoodsRetrieve } from '../index'
import useConvert from '@/composables/useConvert'

type categoryItme = { id: string; name: string; productNum: string }

const $props = defineProps({
  pointGoodsList: {
    type: Array as PropType<ApiGoodsRetrieve[]>,
    default() {
      return []
    },
  },
  goodsVisible: {
    type: Boolean,
    default: false,
  },
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const pageConfigGoods = reactive({
  size: 10,
  current: 1,
  total: 0,
})
// 弹框商品
const goodsList = ref<ApiGoodsRetrieve[]>([])
// 选择的商品列表
const tempGoods = ref<ApiGoodsRetrieve[]>([])
// 分类数组
const categoryList = ref<categoryItme[]>([])
// 分类选中值
const categoryVal = ref('')
const allChecked = ref(false)
const search = shallowReactive({
  maxPrice: '',
  minPrice: '',
  salePrice: '',
  keyword: '',
  categoryFirstId: '',
})
watch(
  $props,
  (newval) => {
    if (tempGoods.value.length !== newval.pointGoodsList.length) {
      const delGoods = tempGoods.value.filter((item) => {
        const id = item.id
        return newval.pointGoodsList.findIndex((goods) => goods.id === id) === -1
      })
      // 取消选中
      tempGoods.value.forEach((item) => {
        delGoods.forEach((goods) => {
          if (goods.id === item.id) {
            item.isCheck = false
          }
        })
      })
      // 过滤
      tempGoods.value = tempGoods.value.filter((item) => item.isCheck)
    }
  },
  {
    deep: true,
  },
)
defineExpose({
  tempGoods,
  search,
  goodsList,
})
// 样式选择
const borderStyle = {
  borderGet: '2px solid #2D8CF0',
  borderNoGet: '2px solid #f2f2f2',
}
onMounted(() => {
  initCategoryList()
})
/**
 * 选择分类
 */
const handleSelectCateItem = (item: categoryItme) => {
  search.categoryFirstId = item.id
  retrieveCommodity()
}
const handleSearchByInput = () => {
  retrieveCommodity()
}
/**
 * 选择商品
 */
const handleChooseGood = (item: ApiGoodsRetrieve) => {
  item.isCheck = !item.isCheck
  const tempGoodsVal = tempGoods.value
  if (item.isCheck) {
    tempGoodsVal.push(item)
  } else {
    const idx = tempGoodsVal.findIndex((i) => i.id === item.id)
    if (idx !== -1) {
      tempGoodsVal.splice(idx, 1)
    }
  }
}
/**
 * 全选
 */
const handleGetAll = () => {
  const goodsListval = goodsList.value
  const tempGoodsval = tempGoods.value
  const allCheckedval = allChecked.value
  goodsListval.map((item) => {
    if (allCheckedval) {
      if (!tempGoodsval.find((t) => t.id === item.id)) {
        tempGoodsval.push(item)
      }
    }
    return (item.isCheck = allCheckedval)
  })
  if (!allCheckedval) {
    goodsListval.forEach((t) => {
      const idx = tempGoodsval.findIndex((i) => i.id === t.id)
      if (idx !== -1) {
        tempGoodsval.splice(idx, 1)
      }
    })
  }
}
const handleSizeChange = (val: number) => {
  pageConfig.size = val
  retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  allChecked.value = false
  retrieveCommodity()
}

async function resetDate() {
  search.categoryFirstId = ''
  search.keyword = ''
  search.maxPrice = ''
  search.minPrice = ''
  search.salePrice = ''
  pageConfig.current = 1
  pageConfig.size = 10
  pageConfig.total = 0
  allChecked.value = false
}

/**
 * 编辑是获取已选择过的数据
 */
async function dealPointList() {
  let checkAll = true
  goodsList.value.forEach((item) => {
    const flag = checkIsSelected(item.id)
    item.isCheck = flag
    checkAll = checkAll ? flag : checkAll
  })
  allChecked.value = checkAll
}

/**
 * 初始化分类列表
 */
async function initCategoryList() {
  const { code, data } = await doGetHighestCategoryLevel({ size: 1000 })
  if (code === 200) {
    categoryList.value = data.records
    pageConfig.total = data.total
  } else {
    ElMessage.error('获取分类列表失败')
  }
}

/**
 * 检索商品列表
 */
async function retrieveCommodity() {
  const shopId = useShopInfoStore().shopInfo.id
  if (search.minPrice || search.maxPrice) {
    search.salePrice = `${search.minPrice}_${search.maxPrice}`
  }
  const { code, data } = await doGetRetrieveCommodity({
    ...search,
    ...pageConfigGoods,
    searchTotalStockGtZero: true,
    shopId,
  })
  if (code === 200) {
    let checkAll = true
    data.list.forEach((item: ApiGoodsRetrieve) => {
      const flag = checkIsSelected(item.id)
      item.isCheck = flag
      checkAll = checkAll ? flag : checkAll
    })
    allChecked.value = checkAll
    goodsList.value = data.list
    pageConfigGoods.total = data.total
  } else {
    ElMessage.error('获取商品失败')
  }
}

/**
 * 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string) {
  return tempGoods.value.findIndex((i) => i.id === id) !== -1
}

function formatPrice(priceArr: number[]) {
  const max = divTenThousand(Math.max(...priceArr)).toString()
  const min = divTenThousand(Math.min(...priceArr)).toString()
  if (priceArr.length > 1) {
    return `${min}~${max}`
  }
  return min
}
retrieveCommodity()
</script>

<template>
  <div>
    <div class="title">选择商品</div>
    <div class="digGoods">
      <div class="digGoods__box">
        <div class="digGoods__box--top">
          <el-select v-model="categoryVal" placeholder="全部分类" style="width: 120px">
            <el-option-group v-for="group in categoryList" :key="group.id">
              <el-option :label="group.name" :value="group.name" @click="handleSelectCateItem(group)" />
            </el-option-group>
          </el-select>
          <div>
            <span style="margin: 0px 10px 0px 25px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
            <el-input v-model="search.minPrice" maxlength="20" style="width: 60px"></el-input>
            <span style="margin: 0px 5px; line-height: 32px">-</span>
            <el-input v-model="search.maxPrice" maxlength="20" style="width: 60px"></el-input>
          </div>
          <el-input
            v-model="search.keyword"
            class="input-with-select"
            maxlength="20"
            placeholder="请输入关键词"
            style="width: 200px; margin-left: 10px"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearchByInput" />
            </template>
          </el-input>
        </div>
        <div v-if="goodsList.length > 0" class="digGoods__box--content">
          <div
            v-for="(item, index) in goodsList"
            :key="index"
            :style="{
              border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
            }"
            class="digGoods__box--content--good"
            @click="handleChooseGood(item)"
          >
            <img :src="item.pic" class="digGoods__box--content--good--img" />
            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
              <el-icon color="#fff" size="40px">
                <Check />
              </el-icon>
            </div>
            <div class="digGoods__box--content--good--shopName">
              <div class="digGoods__box--content--good--shopName--name">{{ item.productName }}</div>
              <!-- <div v-if="item.limitType === 0">
                  <span v-if="item.minPrice > 0">￥{{ item.minPrice }}</span>
                  <span v-else>￥{{ item.maxPrice }}</span>
              </div> -->
              <div>
                ￥{{ item.salePrices && formatPrice(item.salePrices) }}
                <!-- ~￥{{ item.maxPrice }} -->
              </div>
            </div>
          </div>
        </div>
        <div
          v-if="goodsList.length === 0"
          class="digGoods__box--content"
          style="display: flex; justify-content: center; align-items: center; height: 250px"
        >
          暂无相关商品信息，请选择其他分类
        </div>
        <div class="bottom">
          <el-checkbox v-model="allChecked" style="height: 48px; background-color: #fff; padding-left: 5px" @change="handleGetAll">全选</el-checkbox>
          <page-manage
            v-model="pageConfigGoods"
            :load-init="true"
            :total="pageConfigGoods.total"
            background
            @reload="retrieveCommodity"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '../../../mixins/mixins.scss';

.title {
  font-size: 15px;
  font-weight: bold;
  display: flex;
  margin-bottom: 20px;
  margin-top: -20px;
}

@include b(digGoods) {
  border-top: 1px solid #d7d7d7;
  padding-top: 10px;
  @include e(box) {
    background-color: #f2f2f2;
    padding: 10px;
    @include m(top) {
      display: flex;
      justify-content: space-between;
    }
    @include m(content) {
      margin-top: 10px;
      background-color: white;
      border-radius: 5px;
      display: flex;
      flex-wrap: wrap;
      padding: 5px;
      @include m(good) {
        width: 33%;
        margin-left: 2px;
        margin-bottom: 4px;
        height: 80px;

        border-radius: 5px;
        padding: 5px;
        display: flex;
        @include m(img) {
          width: 65px;
          height: 65px;
          position: relative;
        }
        @include m(imgShadow) {
          width: 65px;
          height: 65px;
          position: absolute;
          background-color: rgba(0, 0, 0, 0.6);
          @include flex(center, center);
        }
        @include m(shopName) {
          @include flex;
          flex-direction: column;
          justify-content: space-between;
          align-items: flex-start;
          margin-left: 10px;
          padding: 5px;
          font-size: 12px;
          @include m(name) {
            width: 150px;
            @include utils-ellipsis(2);
          }
        }
      }
    }
  }
}

@include b(bottom) {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

@include b(bottom-container) {
  @include flex;
  flex-direction: column;
}
:deep(.pagination) {
  padding: 8px;
}
</style>
