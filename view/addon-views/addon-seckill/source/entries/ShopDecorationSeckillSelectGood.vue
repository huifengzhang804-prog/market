<script setup lang="ts">
import { ref, shallowReactive, watch } from 'vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { doGetSecKillSessions, doGetGoodsBySessions } from '../apis'
import useConvert from '@/composables/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'

type ApiGoodItemType = {
  maxPrice: string
  minPrice: string
  productId: string
  productName: string
  productPic: string
  isCheck: boolean
}

const $props = defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
const $shopStore = useShopInfoStore()
const { divTenThousand } = useConvert()
// 弹框商品
const goodsList = ref<ApiGoodItemType[]>([])
// 选择的商品列表
const tempGoods = ref<ApiGoodItemType[]>([])
// 场次列表
const sessionOptions = ref<{ startTime: string }[]>([])
const allChecked = ref(false)
const search = shallowReactive({
  current: 1,
  size: 10,
  total: 0,
  choosedSession: '',
})
watch(
  $props.properties,
  async (newval) => {
    if (newval.goodsVisible) {
      resetDate()
      tempGoods.value = JSON.parse(JSON.stringify(newval.pointGoodsList))
      dealPointList()
      await initSessionList()
      retrieveCommodity()
    }
  },
  {
    immediate: true,
  },
)
defineExpose({
  tempGoods,
  search,
  goodsList,
  allChecked,
})
// 样式选择
const borderStyle = {
  borderGet: '2px solid #2D8CF0',
  borderNoGet: '2px solid #f2f2f2',
}
/**
 * 选择分类
 */
const handleSelectCateItem = (startTime: string) => {
  search.choosedSession = startTime
  retrieveCommodity()
}
/**
 * 选择商品
 */
const handleChooseGood = (item: ApiGoodItemType) => {
  item.isCheck = !item.isCheck
  const tempGoodsVal = tempGoods.value
  if (tempGoodsVal.length >= 16) {
    ElMessage.error('最多选择16个商品')
    return false
  } else if (item.isCheck) {
    tempGoodsVal.push(item)
  } else {
    const idx = tempGoodsVal.findIndex((i) => i.productId === item.productId)
    if (idx !== -1) {
      tempGoodsVal.splice(idx, 1)
    }
  }
}
/**
 * 全选
 */
const handleGetAll = () => {
  let flag = false
  const goodsListval = goodsList.value
  const tempGoodsval = tempGoods.value
  const allCheckedval = allChecked.value
  goodsListval.map((item) => {
    if (allCheckedval) {
      if (tempGoodsval.length >= 16) {
        return (flag = true)
      }
      if (!tempGoodsval.find((t) => t.productId === item.productId)) {
        tempGoodsval.push(item)
      }
    }
    return (item.isCheck = allCheckedval)
  })
  if (flag) return ElMessage.error('自多选择16个商品')
  if (!allCheckedval) {
    goodsListval.forEach((t) => {
      const idx = tempGoodsval.findIndex((i) => i.productId === t.productId)
      if (idx !== -1) {
        tempGoodsval.splice(idx, 1)
      }
    })
  }
}
const handleSizeChange = (val: number) => {
  search.size = val
  retrieveCommodity()
}
const handleCurrentChange = (val: number) => {
  search.current = val
  allChecked.value = false
  retrieveCommodity()
}
async function resetDate() {
  search.current = 1
  search.size = 10
  allChecked.value = false
}
/**
 * 编辑是获取已选择过的数据
 */
async function dealPointList() {
  let checkAll = true
  goodsList.value.forEach((item) => {
    const flag = checkIsSelected(item.productId)
    item.isCheck = flag
    checkAll = checkAll ? flag : checkAll
  })
  allChecked.value = checkAll
}
/**
 * 初始化场次列表
 */
async function initSessionList() {
  const { code, data } = await doGetSecKillSessions($shopStore.getterShopInfo.id)
  if (code === 200) {
    if (data.records.length) {
      sessionOptions.value = data.records
      search.choosedSession = data.records[0].startTime
    }
  } else {
    ElMessage.error('获取场次失败')
  }
}
/**
 * 检索商品列表
 */
async function retrieveCommodity() {
  const { current, size, choosedSession } = search
  if (!search.choosedSession) return ElMessage.warning('请先选中场次')
  const { code, data } = await doGetGoodsBySessions({
    current,
    size,
    startTime: choosedSession,
    shopId: $shopStore.getterShopInfo.id,
  })
  if (code !== 200) return ElMessage.error('获取商品失败')
  if (data.records.length) {
    let checkAll = true
    data.records.forEach((item: ApiGoodItemType) => {
      const flag = checkIsSelected(item.productId)
      item.isCheck = flag
      checkAll = checkAll ? flag : checkAll
    })
    allChecked.value = checkAll
    goodsList.value = data.records
    search.total = data.total
  }
}
/**
 * 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string) {
  return tempGoods.value.findIndex((i) => i.productId === id) !== -1
}
</script>

<template>
  <div>
    <div class="title">选择商品</div>
    <div class="digGoods">
      <div class="digGoods__box">
        <div class="digGoods__box--top">
          <el-select v-model="search.choosedSession" style="width: 190px" placeholder="全部分类">
            <el-option-group v-for="item in sessionOptions" :key="item.startTime">
              <el-option :label="item.startTime" :value="item.startTime" @click="handleSelectCateItem(item.startTime)"></el-option>
            </el-option-group>
          </el-select>
        </div>
        <div v-if="goodsList.length > 0" class="digGoods__box--content">
          <div
            v-for="(item, index) in goodsList"
            :key="index"
            class="digGoods__box--content--good"
            :style="{
              border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
            }"
            @click="handleChooseGood(item)"
          >
            <img :src="item.productPic" class="digGoods__box--content--good--img" />
            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
              <el-icon color="#fff" size="40px"><i-ep-check /></el-icon>
            </div>
            <div class="digGoods__box--content--good--shopName">
              <div>{{ item.productName }}</div>
              <div>￥{{ divTenThousand(item.minPrice) }}-￥{{ divTenThousand(item.maxPrice) }}</div>
            </div>
          </div>
        </div>
        <div
          v-if="goodsList.length === 0"
          class="digGoods__box--content"
          style="display: flex; justify-content: center; align-items: center; height: 250px"
        >
          暂无相关商品信息，请选择其他时段
        </div>
        <div class="digGoods__box--bottom">
          <el-checkbox v-model="allChecked" style="margin-top: 40px" @change="handleGetAll">全选</el-checkbox>
          <PageManage
            :page-size="search.size"
            :page-num="search.current"
            :total="search.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
          ></PageManage>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
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
          margin-left: 10px;
          font-size: 12px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
        }
      }
    }
    @include m(bottom) {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

.serachBtn {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdfe6;
  background-color: white;
  cursor: pointer;
  border-left: none;
  border-radius: 4px;
}
</style>
