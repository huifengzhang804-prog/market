<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { doPostProduct } from '@/apis/user'
import goodPrice from './good-price.vue'

import usePriceRange from '@/composables/usePriceRange'
const { range } = usePriceRange()
import useConvert from '@/composables/useConvert'
import { productListInterface } from '.'
const { divTenThousand } = useConvert()

const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
})

const $router = useRouter()
const page = reactive({ current: 1, size: 20 })
const likeData = reactive<{ list: any[] }>({
  list: [],
})
const total = ref(0)

productListInt()

async function productListInt() {
  /**处理装修商品数据 取ID 调接口过滤 取出还有库存的商品 */
  let goodsIdList =
    props?.propertiesLists?.formData?.list?.map((item: { shopId: string; productId: string }) => item?.shopId + ':' + item?.productId) || []
  if (!goodsIdList.length) return (likeData.list = [])
  const { code, data, msg } = (await doPostProduct({
    platformCategoryFirstId: '',
    showCoupon: true,
    size: page.size,
    current: page.current,
    ids: goodsIdList,
    searchTotalStockGtZero: true,
  })) as productListInterface
  if (code !== 200) return ElMessage.error(msg || '获取商品失败')
  total.value = data?.total
  likeData.list = data?.list
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
// 分页
const handleChangePage = (e: number) => {
  page.current = e
  document.querySelector('#referenceVal')?.scrollIntoView()
  productListInt()
}
const handleSizeChange = (val: number) => {
  page.size = val
  document.querySelector('#referenceVal')?.scrollIntoView()
  productListInt()
}
</script>

<template>
  <div v-if="props?.propertiesLists?.show" id="referenceVal" class="con">
    <div class="recommendTltle">
      <div class="recommendTltle__Title">
        <span class="recommendTltle__Title--line" />
        <span class="recommendTltle__Title--circle" />
        <span class="recommendTltle__Title--tle">{{ props?.propertiesLists?.formData?.mainTitle }}</span>
        <span class="recommendTltle__Title--circle" />
        <span class="recommendTltle__Title--line" />
      </div>
      <div class="recommendTltle__title">
        {{ props?.propertiesLists?.formData?.subtitle }}
      </div>
    </div>

    <!-- <div  v-infinite-scroll="handleScroll" flex items-center flex-wrap c-w-1194 m-auto c-mb-30 c-gap-11> -->
    <div class="recommendBox">
      <div v-for="(item, index) in likeData.list" :key="index" class="recommendBox__item" @click="gotoDetail(item?.productId, item?.shopId)">
        <div class="recommendBox__item--imgBox">
          <img :src="item?.pic" />
        </div>
        <div class="recommendBox__item--price">
          <div class="Price">
            <goodPrice :price="range(item?.salePrices?.[0]).toString()" :member-info="item?.memberInfo" />
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
          <span
            v-for="(coupon, inde) in item?.couponList?.slice(0, 2)"
            :key="inde"
            class="discountCoupon"
            :class="[inde === 2 && coupon.sourceDesc.length > 2 ? 'spot' : '']"
            >{{ coupon?.sourceDesc }}</span
          >
        </div>
        <div class="recommendBox__item--paymentNum">
          <!-- <span>包邮</span> -->
          <span v-if="+item?.salesVolume > 0">已售 {{ item?.salesVolume }}</span>
        </div>
      </div>
    </div>
    <el-pagination
      v-if="+total > 0"
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      class="pagination"
      small
      :page-sizes="[10, 20, 50, 100]"
      background
      layout="total, jumper, prev, pager, next, sizes"
      :total="+total"
      @size-change="handleSizeChange"
      @current-change="handleChangePage"
    />
  </div>
</template>
<style lang="scss" scoped>
@include b(con) {
  width: 1200px;
  margin: 0 auto;
  position: relative;
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
          overflow: hidden;
          display: inline-block;
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
      white-space: nowrap; /* 禁止文本换行 */
      overflow: hidden; /* 隐藏超出部分 */
      // white-space: nowrap;
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
}
:deep() {
  .el-pagination__total {
    color: #000;
  }
  .el-select .el-input.is-focus .el-input__wrapper {
    box-shadow: 0 0 0 1px transparent inset !important;
  }
  .el-select .el-input__wrapper.is-focus {
    box-shadow: 0 0 0 1px transparent inset !important;
  }
}
</style>
