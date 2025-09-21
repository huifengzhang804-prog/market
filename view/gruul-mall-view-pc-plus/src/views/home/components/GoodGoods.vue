<script setup lang="ts">
import { doPostProduct } from '@/apis/user'
import goodPrice from './good-price.vue'
import { ElMessage } from 'element-plus'
import usePriceRange from '@/composables/usePriceRange'
import useConvert from '@/composables/useConvert'
import { ProductResponse } from '.'
const { divTenThousand } = useConvert()
const { range } = usePriceRange()

const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
  shopVal: {
    type: Boolean,
    default: false,
  },
})

const $router = useRouter()
const headlinesArrows = ref(0)
const headlinesArrowsFn = (index: number) => {
  headlinesArrows.value = index
  productListInt()
}

// 落地页
const landingPageFn = (pageId: string, page: string, id: number) => {
  let shopVal = props?.shopVal
  // if (!page)
  $router.push({
    path: '/activityClassIfication',
    query: { id: id, link: pageId, type: 'activity', shopVal: shopVal as any, links: 'false' },
  })
  // else
  //     $router.push({
  //         path: '/activityClassIfication',
  //         query: { link: pageId, type: 'activity', shopVal: shopVal as any },
  //     })
}

const likeDatas = ref<any[]>()
async function productListInt() {
  /**处理装修商品数据 取ID 调接口过滤 取出还有库存的商品 */
  let goodsIdList: string[] = []
  let list =
    props?.propertiesLists?.formData?.list[props?.propertiesLists?.formData?.radioKeys?.[headlinesArrows.value]].map(
      (item: { shopId: string; productId: string }) => item?.shopId + ':' + item?.productId,
    ) || []
  goodsIdList = [...goodsIdList, ...list]

  if (!goodsIdList.length) return (likeDatas.value = [])
  const { code, data, msg } = (await doPostProduct({
    platformCategoryFirstId: '',
    showCoupon: true,
    size: 6,
    current: 1,
    ids: goodsIdList,
    searchTotalStockGtZero: true,
  })) as ProductResponse
  if (code !== 200) return ElMessage.error(msg || '获取商品失败')
  likeDatas.value = data?.list
}
productListInt()

const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
</script>

<template>
  <div v-if="props?.propertiesLists?.show" style="margin-top: 24px" class="con">
    <div class="title">
      <div class="headline">
        <span class="headline__frist">{{ props?.propertiesLists?.formData?.name }}</span>
        <span class="headline__fristLine" />
      </div>
      <div class="headlines">
        <span
          v-for="(item, index) in props?.propertiesLists?.formData?.radioKeys"
          :key="index"
          class="headlines__assistant"
          :class="headlinesArrows === index ? 'headlines__assistant--checked' : ''"
          @click="headlinesArrowsFn(index)"
        >
          {{ item }}
          <span class="headlines__assistant--arrows">
            <QIcon
              v-if="headlinesArrows === index"
              name="icon-xuanzhongzhuangtai"
              size="20px"
              style="display: inline-block; transform: translateY(3px)"
            />
          </span>
        </span>
      </div>
      <div
        class="more"
        @click="landingPageFn(props?.propertiesLists?.formData?.pageId, props?.propertiesLists?.formData?.page, props?.propertiesLists?.id)"
      >
        查看更多
        <el-icon class="more__icon">
          <ArrowRight />
        </el-icon>
      </div>
    </div>
    <div class="products">
      <div v-for="(item, index) in likeDatas" :key="index" class="products__item" @click="gotoDetail(item?.productId, item?.shopId)">
        <div class="products__item--imgBox">
          <img :src="item?.pic" />
        </div>
        <div class="products__item--title">
          {{ item?.productName }}
        </div>
        <div class="products__item--priceBox">
          <good-price :price="range(item?.salePrices?.[0]).toString()" member-info="" />
          <div class="products__item--delprice">￥{{ divTenThousand(item?.prices?.[0]).toFixed(2) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
@include b(con) {
  width: 1200px;
  margin: 0 auto;
  position: relative;
}
@include b(title) {
  position: relative;
  display: flex;
  height: 28px;
  line-height: 28px;
  margin-bottom: 16px;
}
@include b(headline) {
  @include e(frist) {
    font-size: 20px;
    font-weight: bold;
    color: #333333;
  }
  @include e(fristLine) {
    display: inline-block;
    width: 88%;
    height: 6px;
    transform: translateY(-22px);
    opacity: 0.9;
    background: linear-gradient(270deg, rgba(245, 67, 25, 0.5) 0%, rgb(245, 67, 25) 100%);
  }
}
@include b(headlines) {
  margin-left: 40px;
  @include e(assistant) {
    position: relative;
    font-size: 16px;
    display: inline-block;
    margin: 5px 24px 0 0;
    cursor: pointer;
    @include m(arrows) {
      position: absolute;
      top: 10px;
      left: 50%;
      transform: translateX(-50%);
    }
    @include m(checked) {
      line-height: 22px;
      color: #f54319 !important;
    }
  }
}
@include b(more) {
  position: absolute;
  right: 16px;
  bottom: 0;
  color: #8c8c8c;
  font-size: 12px;
  cursor: pointer;
  @include e(icon) {
    transform: translateY(2px);
  }
}
@include b(products) {
  display: flex;
  height: 250px;
  overflow: hidden;
  text-align: left;
  @include e(item) {
    width: 180px;
    height: 250px;
    padding: 16px;
    background-color: #fff;
    cursor: pointer;
    margin-right: 24px;
    @include m(imgBox) {
      width: 148px;
      height: 148px;
      overflow: hidden;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(title) {
      font-size: 12px;
      color: #333333;
      line-height: 17px;
      height: 34px;
      overflow: hidden;
      margin: 8px 0;
    }
    @include m(priceBox) {
      display: flex;
    }
    @include m(delprice) {
      margin: 3px 0 0 12px;
      color: #8c8c8c;
      font-size: 10px;
      text-decoration-line: line-through;
    }
  }
}
</style>
