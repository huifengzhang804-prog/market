<script lang="ts" setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import SecondsKillHead from '@pluginPackage/scondsKill/components/seconds-kill-head.vue'
import SecondsKillGoods from '@pluginPackage/scondsKill/components/seconds-kill-goods.vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { onLoad } from '@dcloudio/uni-app'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { SeckillRoundVO, SeckillRoundProductPageDTO, SeckillRoundProductVO } from '@/apis/plugin/secKill/model'
import { doGetSeckillRounds, doGetSeckillProductsOfRound } from '@/apis/plugin/secKill'
import type { Nullable } from '@/constant/global'
import { EMPTY_GB } from '@/constant'

//所有场次 最多查询最近的四个
const rounds = ref<SeckillRoundVO[]>([])
//当前选择的场次
const currentRound = ref<Nullable<SeckillRoundVO>>()

//场次商品列表
const roundProducts = ref<SeckillRoundProductVO[]>([])

const heightInfo = reactive({
  windowHeight: 0,
  statusBarHeight: 0,
  reachBottomheight: 0,
  secKillHeadHeight: 0,
  scrollHeight: 0,
})
const secKillEndPopup = ref(false)
const content = '本次秒杀活动结束啦，看看最新的秒杀活动吧~'
const productPageParam = reactive<SeckillRoundProductPageDTO>({
  shopId: '',
  start: '',
  size: 5,
  current: 1,
})
const loadOver = ref(false)
const secondsKillHeadRef = ref()
const currentShopId = ref<Nullable<Long>>()

onLoad(({ shopId }: any) => (productPageParam.shopId = shopId ?? ''))

onMounted(async () => {
  await getSystemInfo()
  handleSecondsKillSatusUpdate().then((_) => {})
})

watch(
  () => currentRound.value,
  (round) => (productPageParam.start = round?.time?.start || ''),
)

/**
 * 倒计时结束拉起提示弹窗 更新活动状态
 * @param {*} params
 */
async function handleSecondsKillSatusUpdate() {
  await initSeckillRounds()
  await initSeckillRoundProduct(false)
  secondsKillHeadRef.value.updateCountdown()
}

/**
 * 查询最多前四个秒杀场次
 * 查询秒杀场次
 */
async function initSeckillRounds() {
  const { code, data } = await doGetSeckillRounds({ shopId: currentShopId.value, current: 1, size: 4 })
  if (code !== 200 || !data || !data.records?.length) {
    return
  }
  const records = data.records
  rounds.value = records
  currentRound.value = records[0]
}

/**
 * 秒杀活动商品列表
 */
async function initSeckillRoundProduct(isLoad: boolean) {
  if (!currentRound.value) {
    return
  }
  const { code, data } = await doGetSeckillProductsOfRound(productPageParam)
  if (code !== 200 || !data || !data.records?.length) {
    return
  }
  if (!isLoad) {
    roundProducts.value = data.records
    return
  }
  //上滑加载数据
  productPageParam.current++
  roundProducts.value.concat(data.records)
}

/**
 * 商品详情跳转
 */
function commodityNavMethod(product: SeckillRoundProductVO) {
  const { productId, shopId } = product
  jumpGoods(shopId, productId)
}

async function getSystemInfo() {
  await uni.getSystemInfo({
    success: ({ windowHeight, statusBarHeight }) => {
      heightInfo.windowHeight = windowHeight
      heightInfo.statusBarHeight = statusBarHeight || 0
    },
    fail: () => {},
  })

  const query = await uni.createSelectorQuery()
  query
    .select('#seconds-kill-head')
    .boundingClientRect((data) => {
      if (Array.isArray(data)) {
        data = data[0]
      }
      heightInfo.secKillHeadHeight = data?.height || 0
    })
    .exec()
  heightInfo.scrollHeight = heightInfo.windowHeight - heightInfo.secKillHeadHeight
}
const height = computed(() => {
  return useScreenHeight().value - uni.upx2px(316)
})
</script>

<template>
  <!--    当前没有秒杀活动-->
  <template v-if="!rounds.length">
    <q-empty :background="EMPTY_GB.SHOP_CLOSE" title="暂无秒杀活动" />
  </template>
  <template v-else>
    <SecondsKillHead
      id="seconds-kill-head"
      ref="secondsKillHeadRef"
      v-model="currentRound"
      :data="rounds"
      @choose-seconds-kill="initSeckillRoundProduct"
      @update-seconds-kill="secKillEndPopup = true"
    />
    <scroll-view v-if="currentRound" :style="{ height: height + 'px' }" scroll-y @scrolltolower="() => initSeckillRoundProduct(true)">
      <SecondsKillGoods
        v-for="product in roundProducts"
        :key="`${product.shopId}-${product.productId}`"
        :product="product"
        :status="currentRound!.status"
        @click="commodityNavMethod(product)"
      />
      <view class="reach-bottom">
        <text v-if="loadOver" class="reach-bottom--text">到底啦~</text>
      </view>
    </scroll-view>
    <u-modal
      v-model="secKillEndPopup"
      :confirm-style="{
        borderRadius: '32rpx',
        backgroundColor: '#e31436',
        color: '#fff',
        margin: '10px 33%',
        height: '60rpx',
        lineHeight: '60rpx',
      }"
      :content="content"
      :content-style="{ fontWeight: 700, color: '#000' }"
      :show-title="false"
      confirm-color="#E31436"
      @confirm="handleSecondsKillSatusUpdate"
    >
    </u-modal>
  </template>
</template>

<style lang="scss" scoped>
@include b(reach-bottom) {
  height: 120rpx;
  text-align: center;
  line-height: 120rpx;
  @include m(text) {
    font-size: 22rpx;
    color: #9a9a9a;
  }
}
</style>
