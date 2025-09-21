<template>
  <div>
    <div v-if="useSearchBykeyword().selectValue === 'product'">
      <div v-if="+total > 0">
        <div class="recommendBox">
          <div
            v-for="(item, index) in doGetRetrieveComList?.list"
            :key="index"
            class="recommendBox__item"
            @click="gotoDetail(item.productId, item.shopId)"
          >
            <div class="recommendBox__item--imgBox">
              <img :src="item.pic" />
            </div>
            <div class="recommendBox__item--price">
              <div class="Price">
                <good-price :price="range(item.salePrices?.[0]).toString()" :member-info="item?.memberInfo" />
              </div>
              <div class="delPrice">
                <div style="height: 17px; margin-top: 2px">
                  <!-- <div
                                        v-if="item?.memberInfo?.memberLabel"
                                        class="delPrice__label"
                                        :style="{
                                            backgroundColor: item?.memberInfo?.memberLabel?.priceLabelColor,
                                            color: item?.memberInfo?.memberLabel?.priceFontColor,
                                        }"
                                    >
                                        {{ item.memberInfo?.memberLabel?.priceLabelName }}
                                    </div> -->
                </div>

                <div class="delPrice__del">￥{{ divTenThousand(item?.prices?.[0]) }}</div>
              </div>
            </div>
            <div class="recommendBox__item--title">
              {{ item.productName }}
            </div>
            <div class="recommendBox__item--discountCouponList">
              <span v-for="(coupon, inde) in item?.couponList" :key="inde" class="discountCoupon">{{ coupon.sourceDesc }}</span>
            </div>
            <div class="recommendBox__item--paymentNum">
              <!-- <span>包邮</span> -->
              <span v-if="+item.salesVolume > 0">已售 {{ item.salesVolume }} </span>
            </div>
          </div>
          <!-- <ToTopGoCar /> -->
        </div>
        <el-pagination
          v-if="+total > 0"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
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
      <div v-else class="productNull">
        <img src="https://test.bgniao.cn/minio/gruul/2024/02/28/65de9ad7e4b0dd237b83cdbd.png" />
        <span>暂无商品~</span>
      </div>
    </div>
    <div v-else class="shopBox">
      <div v-if="+totals > 0">
        <div v-for="(item, index) in doGetshopsList" :key="index" class="shopBoxList">
          <div class="shopBoxList__shopLogo">
            <img :src="item?.logo" alt="" />
          </div>
          <div class="shopBoxList__shopInfo">
            <div class="shopBoxList__shopInfo--titleBox">
              <span v-if="item?.shopType === 'SELF_OWNED'" c-bg-fb232f class="shop_tag">自营</span>
              <span v-if="item?.shopType === 'PREFERRED'" c-bg-8239f6 class="shop_tag">优选</span>
              <!-- <span v-if="item?.shopType === 'ORDINARY'">普通</span> -->
              <p>{{ item?.name }}</p>
              <div>
                <el-rate v-model="item.score" disabled show-score text-color="#ff9900" score-template="{value}" />
              </div>
            </div>
            <div class="shopBoxList__shopInfo--announcement">
              <QIcon name="icon-gonggao" size="14px" color="#F54319" />
              <span>
                {{ item?.newTips || '暂无公告' }}
              </span>
            </div>
            <div class="shopBoxList__shopInfo--inShop">
              <div @click="gotoshop(item?.id)">
                <QIcon name="icon-dianpu4" size="12px" color="#F54319" style="margin-right: 2px" />
                <span>进入店铺</span>
              </div>
            </div>
          </div>
          <div class="shopBoxList__shopGoods">
            <div
              v-for="(ite, inde) in item?.products"
              :key="inde"
              class="shopBoxList__shopGoods--goods"
              @click="gotoDetail(ite?.productId, item?.id)"
            >
              <div>
                <div>
                  <img :src="ite?.pic" />
                </div>
                <div>
                  <good-price :price="range(ite?.salePrices?.[0]).toString()" member-info="" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-pagination
          v-if="+totals > 0"
          v-model:current-page="currentPageShop"
          v-model:page-size="pageSizeShop"
          class="pagination"
          small
          :page-sizes="[6, 12, 30, 60]"
          background
          layout="total, jumper, prev, pager, next, sizes"
          :total="+totals"
          @size-change="handleSizeChangeShop"
          @current-change="handleChangePageShop"
        />
      </div>
      <div v-else>
        <div class="shopNull">
          <img src="@/assets/images/noStore.png" />
        </div>
      </div>
    </div>
    <ToTopGoCar />
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import usePriceRange from '@/composables/usePriceRange'
import useConvert from '@/composables/useConvert'
import { doGetRetrieveCom, doGetshops } from '@/apis/goods'
import goodPrice from '@/views/home/components/good-price.vue'
import { useSearchBykeyword } from '@/store/modules/search'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
const { divTenThousand } = useConvert()
const { range } = usePriceRange()
const $router = useRouter()
const total = ref('0')
const page = reactive({ current: 1, size: 20 })
const doGetRetrieveComList = ref()
// 搜索商品
const doGetRetrieveComFn = async () => {
  const searchParams = {
    size: page.size,
    current: page.current,
    keyword: useSearchBykeyword().keyword,
    orderByParams: null,
    showCoupon: true,
  }
  const { data, msg, code } = await doGetRetrieveCom(searchParams)
  if (code !== 200) return ElMessage.error(msg || '暂无商品')
  doGetRetrieveComList.value = data
  total.value = data.total
  selectVal.value = false
  selectVals.value = false
  selectValues.value = false
  currentPage.value = page.current
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
// 分页
const currentPage = ref(0)
const pageSize = ref(20)
const handleChangePage = (e: number) => {
  page.current = e
  document.querySelector('#toTop')?.scrollIntoView()
  doGetRetrieveComFn()
}
const handleSizeChange = (val: number) => {
  page.size = val
  document.querySelector('#referenceVal')?.scrollIntoView()
  doGetRetrieveComFn()
}

// 搜索店铺
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 6,
  scored: true,
  userSearch: true,
  salesRanking: 5,
})
const rateValue = ref(3)
const totals = ref('0')
const doGetshopsList = ref()
const doGetshopsInt = async () => {
  const { code, data, msg } = await doGetshops({ name: useSearchBykeyword().keyword.trim(), ...pageConfig })
  if (code === 200) {
    doGetshopsList.value = data.records
    totals.value = data.total
    selectVal.value = false
    selectVals.value = false
    selectValues.value = false
    currentPageShop.value = pageConfig.current
  } else ElMessage.error(msg || '暂无店铺')
}
// 分页
const currentPageShop = ref(0)
const pageSizeShop = ref(6)
const handleChangePageShop = (e: number) => {
  pageConfig.current = e
  document.querySelector('#toTop')?.scrollIntoView()
  doGetshopsInt()
}
const handleSizeChangeShop = (val: number) => {
  pageConfig.size = val
  document.querySelector('#toTop')?.scrollIntoView()
  doGetshopsInt()
}

const selectVal = ref(false)
const selectVals = ref(false)
const selectValues = ref(false)
const doGetInt = (val: string) => {
  if (val === 'product') doGetRetrieveComFn()
  else doGetshopsInt()
}
doGetInt(useSearchBykeyword().selectValue)
watch(
  () => useSearchBykeyword().selectValue,
  (val) => {
    selectVal.value = true
    selectValues.value = true
    if (selectVals.value) return
    doGetInt(val)
  },
)
watch(
  () => useSearchBykeyword().keyword,
  () => {
    selectVals.value = true
    selectValues.value = true
    if (selectVal.value) return
    doGetInt(useSearchBykeyword().selectValue)
  },
)
watch(
  () => useSearchBykeyword().showSearchVal,
  (val) => {
    if (!val || selectValues.value) return
    doGetInt(useSearchBykeyword().selectValue)
  },
)
const gotoshop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId: shopId },
  })
}
</script>

<style scoped lang="scss">
@include b(recommendBox) {
  width: 1201px;
  display: flex;
  flex-wrap: wrap;
  text-align: left;
  margin: 24px auto 0;
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
@include b(shopBox) {
  width: 1200px;
  margin: 0 auto;
}
@include b(shopBoxList) {
  display: flex;
  height: 158px;
  padding: 24px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  @include e(shopLogo) {
    width: 110px;
    height: 110px;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 15px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  @include e(shopInfo) {
    @include m(titleBox) {
      display: flex;
      align-items: center;
      span {
        height: 17px;
        line-height: 17px;
        display: inline-block;
        font-size: 10px;
        border-radius: 3px;
        overflow: hidden;
        margin-right: 5px;
        padding: 0 4px;
        color: #fff;
      }
      p {
        width: 320px;
        height: 22px;
        overflow: hidden;
        word-wrap: normal;
        font-size: 16px;
        font-weight: bold;
        text-align: left;
        margin-right: 8px;
      }
    }
    @include m(announcement) {
      text-align: left;
      display: flex;
      margin-top: 8px;
      span {
        width: 339px;
        height: 34px;
        display: inline-block;
        font-size: 12px;
        color: #999;
        margin-left: 5px;
        line-height: 17px;
        overflow: hidden;
      }
    }
    @include m(inShop) {
      width: 100%;
      text-align: left;
      div {
        margin-top: 12px;
        display: inline-block;
        padding: 5px 8px;
        font-size: 12px;
        border: 1px solid rgb(245, 67, 25);
        border-radius: 2px;
        background: rgba(245, 67, 25, 0.1);
        overflow: hidden;
        color: #f54319;
        cursor: pointer;
        span {
          margin-left: 6px;
        }
      }
    }
  }
  @include e(shopGoods) {
    margin-left: 90px;
    display: flex;
    // width: 489px;
    @include m(goods) {
      width: 80px;
      width: 100%;
      margin-right: 23px;
      div {
        cursor: pointer;
        div {
          border-radius: 4px;
          overflow: hidden;
          margin-bottom: 8px;
          img {
            width: 80px;
            height: 80px;
          }
        }
      }
      &:nth-of-type(5n) {
        margin-right: 0;
      }
    }
  }
}
@include b(shopNull) {
  position: relative;
  padding: 55px 0 167px;
  span {
    position: absolute;
    bottom: 176px;
    left: 50%;
    font-size: 16px;
    color: #999;
    transform: translateX(-50%);
  }
}
@include b(productNull) {
  position: relative;
  padding: 104px 0 140px;
  span {
    position: absolute;
    bottom: 208px;
    left: 50%;
    font-size: 16px;
    color: #999;
    transform: translateX(-50%);
  }
}
:deep(.el-rate) {
  .el-rate__icon.is-active {
    color: #fd9224 !important;
    font-size: 21px;
  }
  .el-rate__decimal {
    color: #fd9224 !important;
  }
  .el-rate__icon {
    font-size: 21px;
  }
  .el-rate__icon {
    font-size: 21px;
  }
  .el-rate__item {
    width: 21px;
  }
  .el-rate__text {
    position: relative;
    left: 8px;
  }
  .el-pagination__total {
    color: #000;
  }
  &.is-disabled .el-rate__item {
    color: #dedede !important;
  }
}
</style>
