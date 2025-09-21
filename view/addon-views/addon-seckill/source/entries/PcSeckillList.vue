<script setup lang="ts">
import 'uno.css'
import { ref, reactive, onMounted } from 'vue'
import { useConversionPrice } from '../index'
import { ElMessage, ElMessageBox } from 'element-plus'
import { strategyPatternHandler, countdownStrategyPatternHandler, secondsKillStatus } from '../index'
import { doGetSeckillSessionsProduct, doGetSeckillRounds, doGetSeckillProductsOfRound } from '../apis'
import Countdown from '../components/Pccountdown.vue'
import type { ApiSecondSKill, ApiSecondSKillGoods } from '../index'
import { useRouter } from 'vue-router'

const $router = useRouter()
const acitve = ref(0)
const secodsKillSession = ref<ApiSecondSKill[]>([])
const currentSecodsKill = ref<ApiSecondSKill>()
const secodsKillGoodsList = ref<ApiSecondSKillGoods[]>([])
const currentShopId = ref('')
const getSeckillQuery = reactive({
  startTime: '',
  size: 16,
  current: 1,
  total: 1,
})
const countdownRef = ref()
const affixType = ref(false)
onMounted(async () => {
  await initSecondsKill()
  await initSeckillSessionsProduct()
})
/**
 * 查询秒杀场次
 */
async function initSecondsKill() {
  const { code, data, msg } = await doGetSeckillRounds({
    current: 1,
    size: 4,
  })
  if (code !== 200) {
    return ElMessage.error(msg ? msg : '秒杀活动获取失败')
  }
  secodsKillSession.value = data.records
  currentSecodsKill.value = data.records[0]
}
/**
 * 获取秒杀活动商品列表
 */
async function initSeckillSessionsProduct() {
  if (!currentSecodsKill.value?.time.start) return
  getSeckillQuery.startTime = currentSecodsKill.value.time.start
  const { code, data, msg } = await doGetSeckillProductsOfRound({
    start: getSeckillQuery.startTime,
    size: getSeckillQuery.size,
    current: getSeckillQuery.current,
  })
  if (code !== 200) {
    return ElMessage.error(msg ? msg : '获取商品列表失败')
  }
  getSeckillQuery.total = Number(data.total)
  secodsKillGoodsList.value = data.records
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
/**
 * 分页切换
 */
const handleCurrentChange = (val: number) => {
  getSeckillQuery.current = val
  initSeckillSessionsProduct()
}
function startTimeFormat(time: string) {
  return time.split(' ')[1], time.split(' ')[1].substring(0, 5)
}
const handleEnd = () => {
  ElMessageBox.alert(`本次秒杀活动结束啦，看看最新的秒杀活动吧~`, '', {
    confirmButtonText: '前往查看',
    center: true,
    showClose: false,
  }).then(async () => {
    await initSecondsKill()
    await initSeckillSessionsProduct()
  })
}
const handleClick = (item: ApiSecondSKill, index: number) => {
  currentSecodsKill.value = item
  initSeckillSessionsProduct()
  acitve.value = index
}
const props = defineProps({
  properties: { type: Object, default: () => ({}) },
})
const seckillImg = ref('')
setTimeout(() => (seckillImg.value = props?.properties?.otherData?.seckillImg), 400)

const startTime = (row: any) => {
  if (row.status === 'NOT_START') {
    return row.time.start
  } else {
    return row.time.end
  }
}
</script>

<template>
  <el-carousel height="482px" c-mb-18>
    <el-carousel-item style="background-color: #fff">
      <img :src="seckillImg" style="width: 100%; height: 100%" />
    </el-carousel-item>
  </el-carousel>
  <div>
    <div flex ma cursor-pointer c-mb-8 :class="affixType ? 'c-w-1190' : 'c-w-1190'" style="padding: 0 65px; overflow: auto">
      <div
        v-for="(item, index) in secodsKillSession"
        :key="index"
        c-h-56
        c-lh-56
        c-fs-14
        fw-700
        relative
        flex
        items-center
        justify-center
        style="width: 350px; flex-shrink: 0"
        :class="[acitve === index ? 'activeTime c-bg-e31436' : 'c-bg-ffffff', affixType ? 'c-w-397' : 'c-w-354 time']"
        @click="handleClick(item, index)"
      >
        <span c-fs-20 c-mr-11 :class="acitve === index ? 'e-c-f' : 'e-c-3'">{{ startTimeFormat(item.time.start) }}</span>
        <template v-if="acitve === index">
          <span e-c-f c-mr-2> {{ currentSecodsKill?.status && countdownStrategyPatternHandler[currentSecodsKill.status].text }}</span>
          <Countdown
            ref="countdownRef"
            :start-time="startTime(item)"
            :is-start="currentSecodsKill?.status === 'NOT_START' || currentSecodsKill?.status === 'IN_PROGRESS'"
            @end="handleEnd"
          />
        </template>
        <div v-else c-w-92 c-h-31 c-lh-31 c-c-e31436 c-bg-fde9ed b-1 c-bc-e31436 text-center c-br-16>
          {{ secondsKillStatus[item.status] }}
        </div>
      </div>
    </div>
    <!-- </el-affix> -->
  </div>
  <div flex items-center flex-wrap c-w-1190 m-auto c-gap-10>
    <div
      v-for="(item, index) in secodsKillGoodsList"
      :key="index"
      c-w-290
      c-h-432
      bg-white
      text-left
      c-pt-27
      c-mb-11
      @click="gotoDetail(item.productId, item.shopId)"
    >
      <img :src="item.productImage" c-w-234 c-h-234 m-auto display-block />
      <div c-mt-11 c-ml-15 c-c-ff1e32 fw-700 c-fs-22><span c-fs-16>￥</span> {{ useConversionPrice(item.minPrice) }}</div>
      <div c-mt-6 c-ml-15 c-ellipsis-2 c-w-257 c-fs-13 c-c-101010 c-h-46 c-lh-20>
        {{ item.productName }}
      </div>
      <div flex justify-between items-center>
        <div></div>
        <div
          c-mr-7
          c-w-94
          c-h-39
          c-lh-39
          e-c-f
          text-center
          c-br-50
          :style="{
            background: strategyPatternHandler[currentSecodsKill.status as keyof typeof strategyPatternHandler].color,
            cursor: 'pointer',
          }"
        >
          {{ strategyPatternHandler[currentSecodsKill.status as keyof typeof strategyPatternHandler].text }}
        </div>
      </div>
    </div>
  </div>
  <div c-mt-38 c-mb-21 flex justify-center c-w-1190 ma>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="getSeckillQuery.total"
      :current-page="getSeckillQuery.current"
      :hide-on-single-page="true"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<style lang="scss" scoped>
.time::after {
  display: block;
  content: '';
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 0 56px 65px;
  border-color: transparent transparent #fff;
  position: absolute;
  left: -65px;
  top: 0;
}
.activeTime::after {
  border-color: transparent transparent #e31436;
}
.time::before {
  display: block;
  content: '';
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 56px 65px 0 0;
  border-color: #fff transparent transparent;
  position: absolute;
  right: -64px;
  top: 0;
}

.activeTime::before {
  border-color: #e31436 transparent transparent;
}
</style>
