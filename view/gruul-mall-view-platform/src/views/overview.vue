<script setup lang="ts">
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts/core'
import { GridComponent, GridComponentOption, TooltipComponent, LegendComponent } from 'echarts/components'
import { LineChart, LineSeriesOption } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
import QIcon from '@/components/q-icon/q-icon.vue'
import DateUtil from '@/utils/date'
import {
    doGetShopStatistics,
    doGetCommodityStatistics,
    doGetCommodityNumber,
    doGetNewCommodityNumber,
    doGetShopNumber,
    doGetNewShopNumber,
    doGetVisitNumber,
    doGetTradeStatistics,
    doGetOrderCount,
} from '@/apis/overview'
import OverviewBasic from '@/q-plugin/supplier/OverviewBasic.vue'
import OverviewSchedule from '@/q-plugin/supplier/OverviewSchedule.vue'
import OverviewSurvey from '@/q-plugin/supplier/OverviewSurvey.vue'
import type { DATE_TYPE } from '@/apis/overview'
import { useMenuList } from '@/store/modules/menu'
import { storeToRefs } from 'pinia'

const { getterIsAdmin } = storeToRefs(useMenuList())

echarts.use([GridComponent, LineChart, CanvasRenderer, UniversalTransition, TooltipComponent, LegendComponent])

type EChartsOption = echarts.ComposeOption<GridComponentOption | LineSeriesOption>
type HotShopItemType = {
    shopId: string
    realTradingVolume: number
    shopName: string
}
type HotGoodItemType = {
    productId: string
    productName: string
    realTradeVolume: string
    realTradingVolume: string
    shopId: string
}
type RankAggregation = {
    hotShopList: HotShopItemType[]
    hotShopIndex: number
    hotGoodIndex: number
    hotCommodityList: HotGoodItemType[]
}
type TradeStatisticItem = {
    date: string
    realTradeVolume: string
    realTradingVolume: string
}

const $router = useRouter()
const { divTenThousand } = useConvert()
const enumTimeArr: { title: string; value: keyof typeof DATE_TYPE }[] = [
    { title: '今天', value: 'TODAY' },
    { title: '近七天', value: 'NEARLY_A_WEEK' },
    { title: '近一个月', value: 'NEARLY_A_MONTH' },
]
// 获取交易统计
let oneWeekAgo = new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000)
// 交易统计时间筛选
const tradeFilterTime = ref<[Date, Date]>([oneWeekAgo, new Date()])
// const tradeStatisticList = ref<TradeStatisticItem[]>([])
// 商品相关聚合
const comAggregation = reactive({
    newGoodNumber: 0,
    violationGoods: 0,
    goodsNumber: 0,
})
// 店铺相关聚合
const shopAggregation = reactive({
    newShopNumber: 0,
    shopNumber: 0,
    willverifyShopNumber: 0,
})
const supplierAggregation = reactive({
    newSupplierNumber: 0,
    supplierNumber: 0,
    willverifySupplierNumber: 0,
})
// 订单数量
const createdOrderNumber = ref(0)

// 新增访客数量
const newVisitUserNumber = ref(0)
// 排行相关聚合
const rankAggregation = reactive<RankAggregation>({
    // 热卖店铺数局
    hotShopList: [],
    // 热卖店铺选中下标
    hotShopIndex: 0,
    // 热卖商品选中下标
    hotGoodIndex: 0,
    // 热门商品
    hotCommodityList: [],
})
//echarts实例
const echartsRef = ref()
const dateTool = new DateUtil()

onMounted(() => {
    const chartNode = document.getElementById('main')!
    const tradeChart = echarts.init(chartNode)
    echartsRef.value = tradeChart
    echartsRef.value.clear()
    initEcharts([])
    if (getterIsAdmin.value) {
        handleChangeTradePick([oneWeekAgo, new Date()])
    }
})
if (getterIsAdmin.value) {
    // 初始化商品数量和新增商品数量
    initCommodityNumber()
    // 获取新增商品商品数量
    initNewCommodityNumber()
    // 初始化店铺数量和新增店铺数量
    initShopNumber()
    // 初始化新增店铺数量
    initNewShopNumber()

    // 获取新增访客数
    initVisitUserNumber()
    // 获取交易统计
    // initTradeStatistis()
    // 获取订单总数
    initOrderCount()

    // 初始化热卖店铺rank
    initTradeRankStatistics()
    // 初始化热卖商品rank
    initCommodityRankStatistics()
    // 初始化交易统计
}

const handleChangeShopIndex = (idx: number) => {
    rankAggregation.hotShopIndex = idx
    initTradeRankStatistics()
}
const handleChangeGoodIndex = (idx: number) => {
    rankAggregation.hotGoodIndex = idx
    initCommodityRankStatistics()
}
const handleChangeTradePick = (e: Date[]) => {
    if (e) {
        initTradeStatistis(dateTool.getYMDs(e[0]), dateTool.getYMDs(e[1]))
    } else {
        initTradeStatistis()
    }
}
const handleNavToGood = () => {
    $router.push({
        name: 'commodityList',
        query: {
            name: 'PLATFORM_SELL_OFF',
        },
    })
}
const handleNavToShop = () => {
    $router.push({
        name: 'shopList',
        query: {
            name: 'UNDER_REVIEW',
        },
    })
}

/**
 * 计算canvas grid宽度
 * @param {number} len 最大数位数
 */
function computeGridWidth(len: number) {
    return len <= 2 ? len * 30 : len * 13
}

/**
 * 获取热卖商铺top5
 */
async function initTradeRankStatistics() {
    const { code, data } = await doGetShopStatistics({ dateType: enumTimeArr[rankAggregation.hotShopIndex].value })
    if (code !== 200) return ElMessage.error('获取商铺数据失败')
    rankAggregation.hotShopList = data
}

/**
 * 获取热卖商品top5
 */
async function initCommodityRankStatistics() {
    const { code, data } = await doGetCommodityStatistics({ dateType: enumTimeArr[rankAggregation.hotGoodIndex].value })
    if (code !== 200) return ElMessage.error('获取商品数据失败')
    rankAggregation.hotCommodityList = data
}

/**
 * 获取商品数量/违规商品
 */
async function initCommodityNumber() {
    const { code, data } = await doGetCommodityNumber()
    if (code !== 200) return ElMessage.error('获取商品数据失败')
    comAggregation.goodsNumber = data.SELL_ON
    comAggregation.violationGoods = data.PLATFORM_SELL_OFF
}

/**
 * 获取今日新增商品数量
 */
async function initNewCommodityNumber() {
    const { code, data } = await doGetNewCommodityNumber()
    if (code !== 200) return ElMessage.error('获取商品数据失败')
    comAggregation.newGoodNumber = data
}

/**
 * 获取店铺数量和待审核店铺数量
 */
async function initShopNumber() {
    const { code, data } = await doGetShopNumber()
    if (code !== 200) return ElMessage.error('获取店铺数据失败')
    shopAggregation.shopNumber = data.NORMAL
    shopAggregation.willverifyShopNumber = data.UNDER_REVIEW
}

/**
 * 获取新增店铺数量
 */
async function initNewShopNumber() {
    const { code, data } = await doGetNewShopNumber()
    if (code !== 200) return ElMessage.error('获取店铺数据失败')
    shopAggregation.newShopNumber = data
}

/**
 * 获取新增访客数
 */
async function initVisitUserNumber() {
    const { code, data } = await doGetVisitNumber()
    if (code !== 200) return ElMessage.error('获取访客数失败')
    newVisitUserNumber.value = data
}

/**
 * 获取交易统计
 */
async function initTradeStatistis(startTime?: string, endTime?: string) {
    const currentStamp = new Date().getTime()
    const startDate = !startTime && !endTime ? dateTool.getSubtracteDays(currentStamp, 7) : startTime!
    const endDate = !startTime && !endTime ? dateTool.getYMDs(currentStamp) : endTime!
    const { code, data } = await doGetTradeStatistics({
        dateType: 'CUSTOM',
        startDate,
        endDate,
    })
    if (code !== 200) return ElMessage.error('获取交易统计失败')
    const enumTimeArr = computeDateDiff(new Date(startDate), new Date(endDate))
    if (!enumTimeArr.length) return ElMessage.error('日期未存在区间')
    const mapData = new Map<string, TradeStatisticItem>(data.map((value: TradeStatisticItem) => [value.date, value]))
    const resultArr = enumTimeArr.map((item) => {
        if (mapData.has(item.date)) {
            return mapData.get(item.date)!
        }
        return item
    })
    initEcharts(resultArr)
}

/**
 * 获取订单数量
 */
async function initOrderCount() {
    const { code, data } = await doGetOrderCount()
    if (code !== 200) return ElMessage.error('获取订单数量失败')
    createdOrderNumber.value = data
}

function initEcharts(data: TradeStatisticItem[]) {
    let xAxisData = []
    // 交易量数组
    let seriesTradeVolumeArr: string[] = []
    // 交易额数组
    let seriesTradeAmountArr: string[] = []
    // 记录表格左侧数值最大数
    let recordMaxNumber = 0
    for (let i = 0; i < data.length; i++) {
        xAxisData.push(data[i].date)
        seriesTradeVolumeArr.push(data[i].realTradeVolume)
        seriesTradeAmountArr.push(String(divTenThousand(data[i].realTradingVolume)))
        if (recordMaxNumber < Number(divTenThousand(data[i].realTradingVolume))) {
            recordMaxNumber = Number(divTenThousand(data[i].realTradingVolume))
        }
    }
    let option: EChartsOption = {
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'cross',
            },
        },
        xAxis: {
            type: 'category',
            data: xAxisData,
            axisTick: {
                show: false,
            },
        },
        yAxis: {
            type: 'value',
        },
        series: [
            {
                data: [...seriesTradeVolumeArr],
                name: '交易量',
                type: 'line',
                stack: 'Total',
                symbol: 'none',
            },
            {
                data: [...seriesTradeAmountArr],
                name: '交易额(元)',
                type: 'line',
                stack: 'Total',
                symbol: 'none',
            },
        ],
        grid: [
            {
                show: false,
                z: 0,
                left: computeGridWidth(String(parseInt(String(recordMaxNumber))).length),
                top: '25%',
                right: '2%',
                bottom: 20,
                height: '67%',
                containLabel: false,
                backgroundColor: 'rgba(0, 0, 0, 0)',
                borderWidth: 1,
                borderColor: '#ccc',
            },
        ],
        legend: {
            icon: 'roundRect',
            data: ['交易量', '交易额(元)'],
            left: '0',
            top: '0%',
            itemWidth: 5,
            itemHeight: 15,
        },
    }
    echartsRef.value.setOption(option)
}

/**
 * 计算时间戳相差天数
 * @param {number} startStamp 开始时间戳
 * @param {number} endStamp 结束时间戳
 */
function computeDateDiff(startStamp: Date, endStamp: Date) {
    // 一天时间戳
    const wholeDayStamp = 24 * 60 * 60 * 1000
    if (startStamp === endStamp) {
        return []
    }
    if (Math.abs(startStamp.getTime() - endStamp.getTime()) < wholeDayStamp) {
        return []
    }
    // 开始整点时间戳
    const startWholeStamp = startStamp.setHours(0, 0, 0, 0)
    // 结束整点时间戳
    const endWholeStamp = endStamp.setHours(0, 0, 0, 0)
    const allDayArr: TradeStatisticItem[] = []
    let diffWholeStamp = endWholeStamp - startWholeStamp
    while (diffWholeStamp >= 0) {
        allDayArr.push({
            date: dateTool.getYMDs(endWholeStamp - diffWholeStamp),
            realTradeVolume: '0',
            realTradingVolume: '0',
        })
        diffWholeStamp -= wholeDayStamp
    }
    return allDayArr
}
const HandleloadSupplierData = (e: any) => {
    supplierAggregation.newSupplierNumber = e.newSupplierNumber
    supplierAggregation.supplierNumber = e.supplierNumber
    supplierAggregation.willverifySupplierNumber = e.willverifySupplierNumber
}
</script>
<template>
    <div class="overview">
        <div class="basic box">
            <span class="title">基本信息 </span>
            <div class="basic__content">
                <div class="basic__item basicgoods">
                    <q-icon class="basic__item--icon" size="44px" name="icon-shangpin2" />
                    <div class="basic__item--cont">
                        <div>商品数量</div>
                        <div class="basic__item--num">{{ comAggregation.goodsNumber }}</div>
                    </div>
                </div>
                <div class="basic__item basicshops">
                    <q-icon class="basic__item--icon" size="44px" name="icon-shangpinguanli" />
                    <div class="basic__item--cont">
                        <div>店铺数量</div>
                        <div class="basic__item--num">{{ shopAggregation.shopNumber || 0 }}</div>
                    </div>
                </div>
                <div class="basic__item basicprders">
                    <q-icon class="basic__item--icon" size="44px" name="icon-dingdan1" />
                    <div class="basic__item--cont">
                        <div>订单数量</div>
                        <div class="basic__item--num">{{ Number(createdOrderNumber) }}</div>
                    </div>
                </div>
                <OverviewBasic v-if="getterIsAdmin" :supplier-data="supplierAggregation"></OverviewBasic>
            </div>
        </div>
        <div class="schedule box">
            <span class="title">待办事项</span>
            <div class="schedule__content">
                <div class="schedule__outborder" style="">
                    <div class="schedule__item" @click="handleNavToShop">
                        <div>待审核店铺</div>
                        <div class="schedule__item--num">{{ shopAggregation.willverifyShopNumber || 0 }}</div>
                    </div>
                </div>
                <OverviewSchedule v-if="getterIsAdmin" :load-supplier-data="HandleloadSupplierData"></OverviewSchedule>
                <div class="schedule__outborder">
                    <div class="schedule__item" @click="handleNavToGood">
                        <div>商品违规</div>
                        <div class="schedule__item--num">{{ comAggregation.violationGoods || 0 }}</div>
                    </div>
                </div>
            </div>
        </div>
        <div style="display: flex">
            <div class="statistics box">
                <div class="statistics__top">
                    <span class="title">交易统计</span>
                    <div>
                        <el-date-picker
                            v-model="tradeFilterTime"
                            type="daterange"
                            style="width: 230px"
                            range-separator="-"
                            start-placeholder="开始时间"
                            end-placeholder="结束时间"
                            @change="handleChangeTradePick"
                        />
                    </div>
                </div>
                <div id="main" style="height: 330px; margin-top: 20px"></div>
            </div>
            <div class="survey box">
                <span class="title">实时概况</span>
                <div class="survey__content">
                    <div class="survey__item">
                        <div>今日注册用户数</div>
                        <div class="survey__item--num">{{ newVisitUserNumber }}</div>
                    </div>
                    <div class="survey__item">
                        <div>今日新增商品数</div>
                        <div class="survey__item--num">{{ comAggregation.newGoodNumber }}</div>
                    </div>
                    <div class="survey__item">
                        <div>今日新增店铺</div>
                        <div class="survey__item--num">{{ shopAggregation.newShopNumber }}</div>
                    </div>
                    <OverviewSurvey :supplier-data="supplierAggregation"></OverviewSurvey>
                </div>
            </div>
        </div>
        <div style="display: flex">
            <div class="hot-shop box">
                <div class="hot-shop__head">
                    <span class="title">热卖店铺TOP10</span>
                    <div class="hot-shop__date">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.hotShopIndex === index && 'com__top--active']"
                            @click="handleChangeShopIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.hotShopList"
                    style="width: 100%; margin-top: 18px"
                    stripe
                    :header-cell-style="{
                        background: '#F9F9F9',
                        height: '40px',
                        padding: 0,
                        color: '#000',
                    }"
                    table-layout="fixed"
                    :cell-style="{ color: '#333' }"
                >
                    <template #empty> <ElTableEmpty /> </template>
                    <el-table-column label="排名" width="70" align="center">
                        <template #default="{ $index }">
                            <QIcon v-if="$index === 0" svg name="icon-a-zuhe1324" style="width: 31px; height: 30px"></QIcon>
                            <QIcon v-else-if="$index === 1" svg name="icon-a-zuhe1325" style="width: 31px; height: 30px"></QIcon>
                            <QIcon v-else-if="$index === 2" svg name="icon-a-zuhe1326" style="width: 31px; height: 30px"></QIcon>
                            <span v-else>{{ $index + 1 }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="店铺名称">
                        <template #default="{ row }">
                            <div style="display: flex; align-items: center">
                                <img :src="row.shopLogo" alt="" style="width: 40px; height: 40px" />
                                <div class="shopName-overflow">{{ row.shopName }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="营业额(元)" align="right">
                        <template #default="{ row }">
                            <div>￥{{ divTenThousand(row.realTradingVolume).toFixed(2) }}</div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div class="hot-shop box">
                <div class="hot-shop__head">
                    <span class="title">热卖商品TOP10</span>
                    <div class="hot-shop__date">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.hotGoodIndex === index && 'com__top--active']"
                            @click="handleChangeGoodIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.hotCommodityList"
                    style="width: 100%; margin-top: 18px"
                    stripe
                    :header-row-style="{
                        width: '439px',
                    }"
                    :header-cell-style="{
                        background: '#F9F9F9',
                        height: '40px',
                        padding: 0,
                        color: '#000',
                    }"
                    table-layout="fixed"
                    :cell-style="{ color: '#333' }"
                >
                    <template #empty> <ElTableEmpty /> </template>
                    <el-table-column label="排名" width="70" align="center">
                        <template #default="{ $index }">
                            <QIcon v-if="$index === 0" svg name="icon-a-zuhe1324" style="width: 31px; height: 30px"></QIcon>
                            <QIcon v-else-if="$index === 1" svg name="icon-a-zuhe1325" style="width: 31px; height: 30px"></QIcon>
                            <QIcon v-else-if="$index === 2" svg name="icon-a-zuhe1326" style="width: 31px; height: 30px"></QIcon>
                            <span v-else>{{ $index + 1 }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="商品名称">
                        <template #default="{ row }">
                            <div style="display: flex; align-items: center">
                                <img :src="row.pic" alt="" style="width: 40px; height: 40px" />
                                <div class="shopName-overflow">{{ row.productName }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="实付金额(元)" width="120">
                        <template #default="{ row }">
                            <div>￥{{ divTenThousand(row.realTradingVolume).toFixed(2) }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="销量" align="center" width="90">
                        <template #default="{ row }">
                            <div>{{ row.realTradeVolume }}</div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(overview) {
    background: $bg-grey;
    height: calc(100vh - 85px);
    overflow-y: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    &::-webkit-scrollbar {
        display: none;
    }
}

@include b(com) {
    cursor: pointer;
    @include flex(space-between);
    @include e(top) {
        width: 140px;
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #797979;
        cursor: pointer;
        @include m(active) {
            color: #555cfd;
        }
    }
}

@include b(basic) {
    color: #fff;
    padding-left: 29px;
    padding-right: 29px;
    background: #fff;
    margin-bottom: 10px;
    padding-top: 22px;
    padding-bottom: 20px;

    @include e(content) {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    @include e(item) {
        margin-top: 28px;
        width: 180px;
        height: 132px;
        border-radius: 5px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        & > .iconfont {
            // margin-right: 20px;
        }
        @include m(cont) {
            @include flex();
            margin-top: 10px;
        }
        @include m(icon) {
            text-align: center;
        }

        @include m(num) {
            font-size: 24px;
            font-weight: 400;
            text-align: center;
            margin-left: 10px;
        }
    }
}

@include b(schedule) {
    height: 246px;
    color: rgb(155, 162, 171);
    padding-left: 29px;
    padding-right: 85px;
    padding-top: 20px;
    background: #fff;
    margin-bottom: 10px;
    padding-bottom: 20px;

    @include e(content) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 38px;
    }
    @include e(outborder) {
        width: 250px;
        height: 112px;
        flex-shrink: 0;
        cursor: pointer;
    }

    @include e(item) {
        margin: 16px;
        font-size: 16px;
        cursor: pointer;
        display: flex;
        flex-direction: column;

        @include m(num) {
            font-size: 24px;
            font-weight: 400;
            color: rgb(40, 45, 48);
            margin-top: 15px;
        }
    }
}
.schedule__outborder:first-child {
    background-image: url('@/assets/image/store_to_be_reviewed.png');
}
.schedule__outborder:last-child {
    background-image: url('@/assets/image/Illegal_products.png');
}
@include b(statistics) {
    height: 422px;
    width: 813px;
    background: #ffffff;
    margin-right: 10px;
    padding: 20px 30px 0 30px;
    display: flex;
    flex-direction: column;
    @include e(top) {
        @include flex(space-between);
    }
}

@include b(survey) {
    height: 422px;
    flex: 1;
    padding-left: 20px;
    padding-top: 20px;
    background: #fff;
    margin-bottom: 10px;
    min-width: 450px;

    @include e(content) {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
    }

    @include e(item) {
        margin-top: 30px;
        padding: 16px;
        font-size: 16px;
        height: 130px;
        color: #666;
        width: 178px;
        margin-right: 20px;
        background: linear-gradient(to bottom right, #f2f2ff, #fff);

        @include m(num) {
            font-size: 24px;
            font-weight: 400;
            margin-top: 15px;
            color: #000;
        }
    }
}

@include b(shop) {
    width: 501px;
    height: 291px;
    background: #ffffff;
    border-radius: 0px;
    font-size: 12px;
    color: #797979;
    padding: 20px 30px 0;
    margin-right: 10px;

    @include e(head) {
        width: 439px;
        height: 30px;
        background: #f7f8fa;
        display: flex;
        align-items: center;
        margin-top: 18px;

        @include m(ranking) {
            margin-left: 23px;
        }

        @include m(shopname) {
            margin-left: 39px;
        }

        @include m(turnover) {
            margin-left: 203px;
        }
    }
}

@include b(goods) {
    // height: 291px;
    background: #ffffff;
    border-radius: 0px;
    font-size: 12px;
    color: #797979;
    padding: 20px;
    width: 50%;
    @include e(head) {
        width: 439px;
        height: 30px;
        background: #f7f8fa;
        display: flex;
        align-items: center;
        margin-top: 18px;

        @include m(ranking) {
            margin-left: 23px;
        }

        @include m(goodsname) {
            margin-left: 39px;
        }

        @include m(money) {
            margin-left: 134px;
        }

        @include m(num) {
            margin-left: 57px;
        }
    }

    @include e(content) {
        width: 439px;
        height: 30px;
        display: flex;
        align-items: center;
        margin-top: 10px;

        @include m(ranking) {
            margin-left: 23px;
            width: 10px;
        }

        @include m(goodsname) {
            margin-left: 52px;
            width: 153px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
        }

        @include m(money) {
            margin-left: 28px;
            width: 108px;
        }

        @include m(num) {
        }
    }
}
.box {
    box-shadow: 0px 1px 6px 0px rgba(49, 52, 64, 0.2);
    border-radius: 3.66px;
}

.title {
    display: flex;
    align-items: center;
    font-size: 16px;
    color: #515151;
    font-weight: bold;
}

.basicgoods {
    background: linear-gradient(225deg, #a5e6f4 0%, #5ac1e0 100%);
}

.basicshops {
    background: linear-gradient(225deg, #a2cafd 0%, #7395ff 100%);
}

.basicprders {
    background: linear-gradient(225deg, #c59aff 0%, #a76eee 100%);
}

@include b(hot-shop) {
    height: 690px;
    width: 50%;
    padding: 20px;
    background: #fff;
    margin-right: 10px;
    @include e(head) {
        display: flex;
        justify-content: space-between;
    }
    @include e(date) {
        display: flex;
        justify-content: space-between;
        color: #797979;
        cursor: pointer;
        span {
            margin-left: 12px;
        }
    }
}

@include b(shopName-overflow) {
    width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    margin-left: 5px;
}
</style>
