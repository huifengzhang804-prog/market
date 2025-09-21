<script setup lang="ts">
import 'uno.css'
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { doGetIntegralProductList } from '../apis'
import { IntegralGoodsType } from '../index'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'

const { divTenThousand } = useConvert()
const list = ref<IntegralGoodsType[]>([])

const params = reactive({ status: 'SELL_ON', size: 25, current: 1, pages: 1, total: 0 })
// 列表
const integralProductListInt = async () => {
  const { code, data, msg } = await doGetIntegralProductList(params)
  if (code === 200) {
    list.value = data?.records
    params.total = +data?.total
    params.size = +data?.size
    params.current = +data?.current
    params.pages = +data?.pages
  } else ElMessage.error(msg)
}
integralProductListInt()
// 分页
const handleChangePage = (e: number) => {
  params.current = e
  integralProductListInt()
}
const $router = useRouter()
// 点击进入详情页
const listLiItem = (item: IntegralGoodsType) => {
  const integralId = item?.id
  $router.push({
    path: '/detail',
    query: { integralId },
  })
}

const props = defineProps({
  properties: { type: Object, default: () => ({}) },
})
const integralImg = ref('')
setTimeout(() => (integralImg.value = props?.properties?.otherData?.integralImg), 400)
</script>
<template>
  <div>
    <div class="bar">
      <img style="width: 100%; height: 100%" :src="integralImg" alt="" />
    </div>
    <div class="con">
      <div class="list">
        <div v-for="(item, index) in list" :key="index" class="listLi" @click="listLiItem(item)">
          <div class="listLi__image">
            <img :src="item.pic" alt="" style="height: 100%" />
          </div>
          <div class="listLi__text">
            <div class="listLi__text--til">
              {{ item.name }}
            </div>
            <div class="listLi__text--pri">
              <div class="listLi__text--ico">
                <img
                  src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20231025/e47826b2a45240a0bad615a6d02e3222.png"
                  alt=""
                  style="height: 100%"
                />
              </div>
              <div class="listLi__text--price">
                {{ item.integralPrice }}积分<span v-if="!item.salePrice || item.salePrice !== '0'"
                  >+￥{{ divTenThousand(item.salePrice).toFixed(2) }}</span
                >
              </div>
            </div>
            <div class="listLi__text--through">市场价:￥{{ (+item.price).toFixed(2) }}</div>
          </div>
        </div>
      </div>
      <!-- 分页 -->
      <el-pagination
        v-if="params.total"
        class="pagination"
        background
        size="small"
        :total="params.total"
        :page-count="params.pages"
        layout="prev, pager, next, total"
        @current-change="handleChangePage"
      />
      <ToTopGoCar />
    </div>
  </div>
</template>
<style scoped lang="scss">
img {
  width: 100%;
  // height: 100%;
}
@include b(bar) {
  width: 1920px;
  height: 500px;
  overflow: hidden;
  margin: 0 auto;
  background-color: #fff;
  img {
    width: 100%;
    // transform: translateY(-100px);
  }
}
@include b(con) {
  width: 1200px;
  margin: 0 auto;
}
@include b(list) {
  display: flex;
  flex-wrap: wrap;
}
@include b(listLi) {
  width: 220px;
  height: 314px;
  background-color: #fff;
  margin-top: 24px;
  margin-right: 24px;
  cursor: pointer;
  @include e(image) {
    width: 188px;
    height: 189px;
    margin: 16px;
    margin-bottom: 8px;
    border-radius: 4px;
    overflow: hidden;
  }
  @include e(text) {
    margin: 0 16px 0 15px;
    overflow: hidden;
    @include m(til) {
      font-size: 14px;
      color: #333;
      margin-bottom: 5px;
      text-align: left;
      height: 40px;
      line-height: 20px;
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    @include m(ico) {
      width: 20px;
      height: 20px;
      margin-right: 4px;
    }
    @include m(pri) {
      display: flex;
      white-space: nowrap;
      height: 22px;
      line-height: 22px;
      margin-bottom: 4px;
    }
    @include m(price) {
      font-size: 16px;
      color: #fa3534;
      font-weight: bold;
      line-height: 22px;
      // margin-bottom: 4px;
      text-align: left;
    }
    @include m(through) {
      line-height: 20px;
      font-size: 14px;
      text-align: left;
      text-decoration: line-through;
      color: #dbdbdb;
    }
  }
}
@include b(pagination) {
  margin: 24px 0;
  justify-content: center;
  height: 24px;
}
.listLi:nth-child(5n) {
  margin-right: 0;
}
</style>
