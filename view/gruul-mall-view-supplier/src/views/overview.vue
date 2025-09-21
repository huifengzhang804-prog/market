<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import * as echarts from 'echarts/core'
import { GridComponent, GridComponentOption, TooltipComponent, LegendComponent, ToolboxComponent } from 'echarts/components'
import { LineChart, LineSeriesOption } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'

import DateUtil from '@/utils/date'
import {
    doGetTradeVolume,
    doGetTransactionAmount,
    doGetVisitNumber,
    doGetNewCommodityNumber,
    doGetTradeStatistics,
    doGetOrderInfo,
} from '@/apis/overview'
import type { DATE_TYPE } from '@/apis/overview'
import { ElMessage } from 'element-plus'
echarts.use([GridComponent, LineChart, CanvasRenderer, UniversalTransition, TooltipComponent, LegendComponent, ToolboxComponent])

type EChartsOption = echarts.ComposeOption<GridComponentOption | LineSeriesOption>
type GoodItemType = {
    productId: string
    productName: string
    shopId: string
    amount: number
    number: number
}
type RankAggregationType = {
    tradeVolumeList: GoodItemType[]
    tradeAmountList: GoodItemType[]
    tradeVolumeIndex: number
    tradeAmountIndex: number
}
type TradeStatisticItem = {
    XDate: string
    tradeAmount: number
    tradeNumber: number
}

const { divTenThousand } = useConvert()
// 新增商品数量
const newGoodsCount = ref(0)
// const visitUserCount = ref(-1)
const orderCountInfo = ref({
    pendingPaymentOrders: 0,
    pendingDeliveredOrders: 0,
    pendingReceivedOrders: 0,
})
const rankAggregation = reactive<RankAggregationType>({
    tradeVolumeList: [],
    tradeAmountList: [],
    tradeVolumeIndex: 0,
    tradeAmountIndex: 0,
})
const $router = useRouter()
// 今天访客数
const visitUserNumber = ref(0)
// 交易统计筛选
const tradeFilterTime = ref([])
const enumTimeArr: { title: string; value: keyof typeof DATE_TYPE }[] = [
    { title: '今天', value: 'TODAY' },
    { title: '近七天', value: 'NEARLY_A_WEEK' },
    { title: '近一个月', value: 'NEARLY_A_MONTH' },
]
const dateTool = new DateUtil()
const echartsRef = ref()

onMounted(() => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const chartNode = document.getElementById('main')!
    const tradeChart = echarts.init(chartNode)
    echartsRef.value = tradeChart
    initEcharts([])
})
// 获取商品交易量rank
initRankOftradeVolume()
// 获取商品交易额rank
initRankOftradeAmount()
// 获取访客数
initVisitNumber()
// 获取新增商品数量
initNewGoodsNumber()
// 获取交易统计
initTradeStatistis()
// 获取订单信息
initOrderInfo()

const handleChangeVolumeIndex = (idx: number) => {
    rankAggregation.tradeVolumeIndex = idx
    initRankOftradeVolume()
}
const handleChangeTradePick = (e: Date[]) => {
    if (e) {
        initTradeStatistis(dateTool.getYMDs(e[0]), dateTool.getYMDs(e[1]))
    } else {
        initTradeStatistis()
    }
}
const handleChangeAmountIndex = (idx: number) => {
    rankAggregation.tradeAmountIndex = idx
    initRankOftradeAmount()
}
const handleNavToUnpaid = () => {
    $router.push({
        name: 'orderIndex',
        query: {
            name: 'UNPAID',
        },
    })
}
const handleNavToUnDelivery = () => {
    $router.push({
        name: 'orderIndex',
        query: {
            name: 'UN_DELIVERY',
        },
    })
}
const handleNavToUnReceive = () => {
    $router.push({
        name: 'orderIndex',
        query: {
            name: 'UN_RECEIVE',
        },
    })
}
/**
 * @description: 获取交易量rank
 * @returns {*}
 */
async function initRankOftradeVolume() {
    const { code, data } = await doGetTradeVolume({
        dateRangeType: enumTimeArr[rankAggregation.tradeVolumeIndex].value,
        tradeStaticType: 'TRADE_NUMBER',
    })
    if (code !== 200) return ElMessage.error('获取商品交易量失败')
    if (data) {
        rankAggregation.tradeVolumeList = data
    } else {
        rankAggregation.tradeVolumeList = []
    }
}
/**
 * @description: 获取交易额rank
 */
async function initRankOftradeAmount() {
    const { code, data } = await doGetTransactionAmount({
        dateRangeType: enumTimeArr[rankAggregation.tradeAmountIndex].value,
        tradeStaticType: 'TRADE_AMOUNT',
    })
    if (code !== 200) return ElMessage.error('获取商品交易额失败')
    if (data) {
        rankAggregation.tradeAmountList = data
    } else {
        rankAggregation.tradeAmountList = []
    }
}
/**
 * @description: 获取新增咨询数
 */
async function initVisitNumber() {
    const { code, data } = await doGetVisitNumber()
    if (code !== 200) return ElMessage.error('获取咨询数失败')
    visitUserNumber.value = data as number
}
/**
 * @description: 获取新增商品数量
 */
async function initNewGoodsNumber() {
    const { code, data } = await doGetNewCommodityNumber()
    if (code !== 200) return ElMessage.error('获取新增商品数失败')
    newGoodsCount.value = data as number
}
async function initEcharts(data: TradeStatisticItem[]) {
    let xAxisData = []
    // 交易量数组
    let seriesTradeVolumeArr: number[] = []
    // 交易额数组
    let seriesTradeAmountArr: string[] = []
    // 记录表格左侧数值最大数
    let recordMaxNumber = 0
    for (let i = 0; i < data.length; i++) {
        xAxisData.push(data[i].XDate)
        seriesTradeVolumeArr.push(data[i].tradeNumber)
        seriesTradeAmountArr.push(String(divTenThousand(data[i].tradeAmount)))
        if (recordMaxNumber < Number(divTenThousand(data[i].tradeAmount))) {
            recordMaxNumber = Number(divTenThousand(data[i].tradeAmount))
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
                data: seriesTradeVolumeArr,
                name: '交易量',
                type: 'line',
                stack: 'Total',
                symbol: 'none',
            },
            {
                data: seriesTradeAmountArr,
                name: '交易额(元)',
                stack: 'Total',
                type: 'line',
                symbol: 'none',
            },
        ],
        grid: [
            {
                show: false,
                z: 0,
                left: computeGridWidth(String(parseInt(String(recordMaxNumber))).length),
                top: '22%',
                right: '7%',
                height: '67%',
                width: '100%',
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
 * @description: 获取交易统计
 */
async function initTradeStatistis(startTime?: string, endTimes?: string) {
    const currentStamp = new Date().getTime()
    const beginTime = !startTime && !endTimes ? dateTool.getSubtracteDays(currentStamp, 7) : startTime!
    const endTime = !startTime && !endTimes ? dateTool.getYMDs(currentStamp) : endTimes!
    const { code, data } = await doGetTradeStatistics({
        dateRangeType: 'CUSTOM',
        beginTime,
        endTime,
    })
    if (code !== 200) return ElMessage.error('获取交易统计失败')
    const enumTimeArr = computeDateDiff(new Date(beginTime), new Date(endTime))
    if (!enumTimeArr.length) return ElMessage.error('日期未存在区间')
    const mapData = new Map<string, TradeStatisticItem>(data.map((value: TradeStatisticItem) => [value.XDate, value]))
    const resultArr = enumTimeArr.map((item) => {
        if (mapData.has(item.XDate)) {
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            return mapData.get(item.XDate)!
        }
        return item
    })
    initEcharts(resultArr)
}
/**
 * @description: 获取订单信息
 */
async function initOrderInfo() {
    const { code, data } = await doGetOrderInfo()
    if (code !== 200) return ElMessage.error('获取订单信息失败')
    orderCountInfo.value = data
}
/**
 * @description: 计算canvas grid宽度
 * @param {number} len 最大数位数
 */
function computeGridWidth(len: number) {
    return len <= 2 ? len * 30 : len * 12
}
/**
 * @description: 计算时间戳相差天数
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
            XDate: dateTool.getYMDs(endWholeStamp - diffWholeStamp),
            tradeAmount: 0,
            tradeNumber: 0,
        })
        diffWholeStamp -= wholeDayStamp
    }
    return allDayArr
}
</script>

<template>
    <div class="overview">
        <div style="display: flex">
            <div class="survey f1">
                <span class="title">实时概况</span>
                <div class="survey__content">
                    <div class="survey__item">
                        <div>今日咨询数</div>
                        <div class="survey__item--num">{{ visitUserNumber }}</div>
                    </div>
                    <div class="survey__item">
                        <div>今日新增商品数</div>
                        <div class="survey__item--num">{{ newGoodsCount }}</div>
                    </div>
                </div>
            </div>
            <div class="statistics">
                <div class="statistics__top">
                    <span class="title">交易统计</span>
                    <el-date-picker
                        v-model="tradeFilterTime"
                        type="daterange"
                        style="width: 213px; flex-grow: 0"
                        range-separator="-"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        @change="handleChangeTradePick"
                    />
                </div>
                <div id="main" style="height: 360px; width: 762px; margin-top: 20px"></div>
            </div>
        </div>
        <div class="schedule">
            <span class="title">待办事项</span>
            <div class="schedule__content">
                <div class="schedule__outborder" @click="handleNavToUnpaid">
                    <div class="schedule__item">
                        <div>待付款订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.pendingPaymentOrders || 0 }}</div>
                    </div>
                </div>
                <div class="schedule__outborder" @click="handleNavToUnDelivery">
                    <div class="schedule__item">
                        <div>待发货订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.pendingDeliveredOrders || 0 }}</div>
                    </div>
                </div>
                <div class="schedule__outborder" @click="handleNavToUnReceive">
                    <div class="schedule__item">
                        <div>待收货订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.pendingReceivedOrders || 0 }}</div>
                    </div>
                </div>
            </div>
        </div>
        <div style="display: flex">
            <div class="hot-shop box">
                <div class="hot-shop__head">
                    <span class="title">商品交易量TOP10</span>
                    <div class="hot-shop__date">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.tradeVolumeIndex === index && 'com__top--active']"
                            @click="handleChangeVolumeIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.tradeVolumeList"
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
                    <el-table-column label="商品名称">
                        <template #default="{ row }">
                            <div style="display: flex; align-items: center">
                                <img :src="row.pic" alt="" style="width: 40px; height: 40px" />
                                <div class="shopName-overflow">{{ row.productName }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="销量" width="80" align="right">
                        <template #default="{ row }">
                            <div>{{ row.number }}</div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div class="hot-shop box">
                <div class="hot-shop__head">
                    <span class="title">商品交易额TOP10</span>
                    <div class="hot-shop__date">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.tradeAmountIndex === index && 'com__top--active']"
                            @click="handleChangeAmountIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.tradeAmountList"
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
                    <el-table-column label="实付金额(元)" width="120" align="right">
                        <template #default="{ row }">
                            <div>{{ row.amount.toFixed(2) }}</div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(overview) {
    background: #f2f2f2;
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
        width: 130px;
        @include flex(space-between);
        @include m(active) {
            position: relative;
            color: #555cfd;
            font-weight: bold;
        }
    }
}
@include b(statistics) {
    width: 813px;
    height: 445px;
    background: #ffffff;
    padding: 7px 20px 0 30px;
    @include e(top) {
        height: 46px;
        @include flex(space-between);
    }
}

@include b(survey) {
    margin-right: 10px;
    height: 445px;
    padding-left: 20px;
    padding-top: 20px;
    background: #fff;
    margin-bottom: 10px;

    @include e(content) {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
    }

    @include e(item) {
        margin-top: 38px;
        font-size: 16px;
        width: 178px;
        padding: 14px;
        height: 130px;
        background: linear-gradient(to right bottom, rgba(85, 92, 253, 0.1), rgba(85, 92, 253, 0));
        margin-right: 20px;
        @include m(num) {
            font-size: 24px;
            font-weight: bold;
            margin-top: 15px;
            color: #515151;
        }
    }
}

@include b(schedule) {
    height: 246px;
    color: #666666;
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
        margin-right: 100px;
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
    background-image: url('@/assets/images/store_to_be_reviewed.png');
}
.schedule__outborder:nth-child(2) {
    background-image: url('@/assets/images/supplier_to_be_audited.png');
}
.schedule__outborder:last-child {
    background-image: url('@/assets/images/Illegal_products.png');
}
@include b(shop) {
    width: 50%;
    // height: 291px;
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

        @include m(turnover) {
            flex: 1;
            text-align: right;
            margin-right: 85px;
        }
    }
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

@include b(goods) {
    width: 50%;
    // height: 291px;
    background: #ffffff;
    border-radius: 0px;
    font-size: 12px;
    color: #797979;
    padding: 20px 30px 0;

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
            margin-left: 203px;
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
            cursor: pointer;
            margin-left: 52px;
            width: 153px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
        }

        @include m(money) {
            margin-left: 100px;
            width: 108px;
        }
    }
}

.title {
    font-size: 16px;
    color: #515151;
    font-weight: bold;
}

.basicgoods {
    background: #f7f5ff;
}

.basicshops {
    background: #f5fffc;
}

.basicprders {
    background: #fff9f1;
}
@include b(shopName-overflow) {
    width: 292px;
    margin-left: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
}
</style>
