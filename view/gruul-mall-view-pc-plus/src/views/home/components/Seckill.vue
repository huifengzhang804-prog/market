<script setup lang="ts">
import { doGetSeckillProductsOfRound, doGetSeckillRounds } from '@/pluginPackage/seckill/apis'
import goodPrice from './good-price.vue'
import usePriceRange from '@/composables/usePriceRange'
import { ElMessage } from 'element-plus'
import countdown from '@/pluginPackage/seckill/components/countdown.vue'
import { ApiSecondSKillGoods, SECONDS_KILL_GOODS_STATUS } from './index'
import { toNumber } from 'lodash-es'
import useConvert from '@/composables/useConvert'

const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
})

const { divTenThousand } = useConvert()
const { range } = usePriceRange()
const $router = useRouter()

const landingPageFn = () => {
  $router.push({
    path: '/seckill',
    query: {},
  })
}
const timing = ref(0)
const SecondsKillList = ref<{ status: SECONDS_KILL_GOODS_STATUS; time: { end: string; start: string } }[]>()
/**获取秒杀活动时间 */
const doGetSecondsKillInt = async () => {
  const { code, data, msg } = await doGetSeckillRounds({
    current: 1,
    size: 1,
  })
  if (code !== 200) return ElMessage.error(msg || '获取秒杀活动失败')
  else {
    SecondsKillList.value = data.records
    timing.value = data.records?.[0]?.time.start?.split(' ')?.pop()?.split(':')?.shift() || 0
    if (data.records?.length) doGetSeckillSessionsProductInt()
  }
}
doGetSecondsKillInt()
const page = reactive({ size: 99999, current: 1, total: 0 })
const seckillList = ref<ApiSecondSKillGoods[]>()
/**获取秒杀商品 */
const doGetSeckillSessionsProductInt = async () => {
  const { code, data, msg } = await doGetSeckillProductsOfRound({
    start: SecondsKillList.value?.[0]?.time.start,
    size: page.size,
    current: page.current,
  })
  if (code !== 200) return ElMessage.error(msg || '获取秒杀活动失败')
  else {
    seckillList.value = chunkArray(data?.records, 5)
  }
}

function chunkArray(array: any[], size: number) {
  return array.reduce((result, item, index) => {
    const chunkIndex = Math.floor(index / size)
    if (!result[chunkIndex]) {
      result[chunkIndex] = []
    }
    result[chunkIndex].push(item)
    return result
  }, [])
}

/**跳转到详情页 */
const seckillItemFn = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
// 切换商品
const setActiveItemFn = (val: number) => {}
</script>
<template>
  <div v-if="SecondsKillList?.length" class="con">
    <div class="seckillBox">
      <div class="seckillBox__left">
        <div class="seckillBox__left--title">
          {{ props?.propertiesLists?.formData?.name }}
        </div>
        <div class="seckillBox__left--time">
          <span>{{ timing }}:00</span> 点场
        </div>
        <div class="seckillBox__left--line">
          <span />
        </div>
        <div class="seckillBox__left--start">距{{ SecondsKillList?.[0]?.status === 'IN_PROGRESS' ? '结束' : '开始' }}</div>
        <div class="seckillBox__left--countDown">
          <countdown :start-time="SecondsKillList?.[0]?.time.end" />
        </div>
      </div>
      <div class="seckillBox__right">
        <el-carousel height="260px" :autoplay="false" indicator-position="none" arrow="always" @change="setActiveItemFn">
          <el-carousel-item v-for="(item, indexs) in seckillList" :key="indexs" class="seckillBox__right--listBox">
            <div class="more" @click="landingPageFn">
              查看更多
              <el-icon class="more__icon">
                <ArrowRight />
              </el-icon>
            </div>
            <div v-for="(ite, index) in item" :key="index" class="seckillBox__right--itemBox">
              <div class="seckillBox__right--list">
                <div class="seckillBox__right--imgBox" @click="seckillItemFn(ite.productId, ite.shopId)">
                  <img :src="ite?.productImage" />
                </div>
                <div class="seckillBox__right--title">
                  {{ ite?.productName }}
                </div>
                <div class="seckillBox__right--price">
                  <good-price :price="range(ite?.minPrice).toString()" member-info="" />
                  <div class="seckillBox__right--delPrice">￥{{ toNumber(divTenThousand(ite?.price)).toFixed(2) }}</div>
                </div>
              </div>
              <div v-if="index + 1 !== SecondsKillList?.length" class="seckillBox__right--line" />
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
    <!-- <div class="seckillBox">
            <div class="seckillBox__left seckillBox__lefts">
                <div class="seckillBox__left--titles seckillBox__left--title">{{ props?.propertiesLists?.formData?.name }}</div>
            </div>
            <div class="seckillBox__right">
                <img
                    class="seckillBox__right--noneImg"
                    src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20240222/6cf8a702d35945b9bf477804dbfa3783.png"
                />
                <span>暂无活动~</span>
            </div>
        </div> -->
  </div>
</template>
<style lang="scss" scoped>
@include b(con) {
  width: 1200px;
  margin: 0 auto;
  position: relative;
}
@include b(seckillBox) {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  height: 260px;
  @include e(left) {
    background: url('https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20240218/65904676a3124ff0a50f51ad6a22a7a1.png');
    background-color: #e00500;
    padding-top: 40px;
    overflow: hidden;
    @include m(title) {
      width: 190px;
      font-size: 22px;
      color: #fff;
      line-height: 30px;
      white-space: nowrap;
      overflow: hidden;
    }
    @include m(time) {
      width: 190px;
      margin-top: 27px;
      font-size: 16px;
      color: #fff;
      line-height: 22px;
    }
    @include m(line) {
      width: 190px;
      margin-top: 2px;
      span {
        display: inline-block;
        width: 16px;
        height: 1px;
        background-color: #fff;
      }
    }
    @include m(start) {
      width: 190px;
      margin-top: 15px;
      font-size: 16px;
      color: #fff;
      line-height: 22px;
    }
    @include m(countDown) {
      width: 190px;
      margin-top: 22px;
      display: flex;
      justify-content: center;
      line-height: 32px;
    }
  }
  @include e(right) {
    width: 1010px;
    background-color: #fff;
    @include m(listBox) {
      display: flex;
    }
    @include m(itemBox) {
      display: flex;
      align-items: center;
      position: relative;
    }
    @include m(list) {
      width: 202px;
      height: 260px;
      // cursor: pointer;
      background-color: #fff;
    }
    @include m(line) {
      height: 101px;
      width: 1px;
      background-color: #000;
      opacity: 0.05;
      position: absolute;
      right: 0;
    }
    @include m(imgBox) {
      width: 154px;
      height: 154px;
      overflow: hidden;
      margin: 24px 24px 12px;
      cursor: pointer;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(title) {
      @include flex;
      margin-left: 16px;
      font-size: 12px;
      color: #333;
      width: 170px;
      height: 34px;
      overflow: hidden;
      text-align: center;
      cursor: pointer;
      @include utils-ellipsis(2);
    }
    @include m(price) {
      display: flex;
      font-size: 16px;
      justify-content: center;
      padding-top: 5px;
    }
    @include m(delPrice) {
      font-size: 12px;
      text-decoration-line: line-through;
      color: #8c8c8c;
      margin: 1px 0 0 8px;
    }
  }
}
// 秒杀暂无活动样式
@include b(seckillBox) {
  @include e(lefts) {
    padding-top: 0;
    display: flex;
    align-items: center;
  }
  @include e(right) {
    position: relative;
    overflow: hidden;
    text-align: left;
    @include m(noneImg) {
      position: absolute;
      top: 14px;
      left: 305px;
    }
    span {
      position: absolute;
      left: 427px;
      bottom: 51px;
      color: #999;
      font-size: 16px;
      line-height: 22px;
    }
  }
}
@include b(more) {
  position: absolute;
  right: 16px;
  top: 4px;
  color: #8c8c8c;
  font-size: 12px;
  cursor: pointer;
  z-index: 2;
  @include e(icon) {
    transform: translateY(2px);
  }
}
:deep().el-carousel__arrow--right {
  width: 22px;
  height: 40px;
  right: 0;
  border-radius: 100px 0px 0px 100px;
  // background: rgba(0, 0, 0, 0.2);
  padding-left: 4px;
}
:deep().el-carousel__arrow--left {
  left: 0;
  width: 22px;
  height: 40px;
  border-radius: 0px 100px 100px 0px;
  // background: rgba(0, 0, 0, 0.2);
  padding-right: 4px;
}
:deep().countdown-view--item {
  width: 32px;
  height: 32px;
  font-weight: bold;
  background-color: #000;
  border-radius: 0px;
}
:deep().countdown-view--item-pendant {
  color: #fff;
  margin: 0 9px;
  font-weight: bold;
}
</style>
