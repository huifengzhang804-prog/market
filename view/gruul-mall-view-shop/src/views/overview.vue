<script setup lang="ts">
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
import { useMenuList } from '@/store/modules/menu'
import { storeToRefs } from 'pinia'

const { getterIsAdmin } = storeToRefs(useMenuList())

echarts.use([GridComponent, LineChart, CanvasRenderer, UniversalTransition, TooltipComponent, LegendComponent, ToolboxComponent])
type EChartsOption = echarts.ComposeOption<GridComponentOption | LineSeriesOption>
type GoodItemType = {
    productId: string
    productName: string
    realTradeVolume: string
    realTradingVolume: string
    shopId: string
}
type RankAggregationType = {
    tradeVolumeList: GoodItemType[]
    tradeAmountList: GoodItemType[]
    tradeVolumeIndex: number
    tradeAmountIndex: number
}
type TradeStatisticItem = {
    date: string
    realTradeVolume: string
    realTradingVolume: string
}

const { divTenThousand } = useConvert()
// 新增商品数量
const newGoodsCount = ref(0)
const orderCountInfo = ref({
    undelivered: 0,
    unpaid: 0,
    unreceived: 0,
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
// 获取交易统计
let oneWeekAgo = new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000)
// 交易统计筛选
const tradeFilterTime = ref<[Date, Date]>([oneWeekAgo, new Date()])
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
    echartsRef.value.clear()
    initEcharts([])
    if (getterIsAdmin.value) {
        handleChangeTradePick([oneWeekAgo, new Date()])
    }
})

if (getterIsAdmin.value) {
    // 获取商品交易量rank
    initRankOftradeVolume()
    // 获取商品交易额rank
    initRankOftradeAmount()
    // 获取访客数
    initVisitNumber()
    // 获取新增商品数量
    initNewGoodsNumber()
    // 获取交易统计
    // initTradeStatistis()
    // 获取订单信息
    initOrderInfo()
}

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
 * 获取交易量rank
 */
async function initRankOftradeVolume() {
    const { code, data } = await doGetTradeVolume({ dateType: enumTimeArr[rankAggregation.tradeVolumeIndex].value })
    if (code !== 200) return ElMessage.error('获取商品交易量失败')
    if (data) {
        rankAggregation.tradeVolumeList = data
    } else {
        rankAggregation.tradeVolumeList = []
    }
}
/**
 * 获取交易额rank
 */
async function initRankOftradeAmount() {
    const { code, data } = await doGetTransactionAmount({ dateType: enumTimeArr[rankAggregation.tradeAmountIndex].value })
    if (code !== 200) return ElMessage.error('获取商品交易额失败')
    if (data) {
        rankAggregation.tradeAmountList = data
    } else {
        rankAggregation.tradeAmountList = []
    }
}
/**
 * 获取新增访客数
 */
async function initVisitNumber() {
    const { code, data } = await doGetVisitNumber()
    if (code !== 200) return ElMessage.error('获取访客数失败')
    visitUserNumber.value = data
}
/**
 * 获取新增商品数量
 */
async function initNewGoodsNumber() {
    const { code, data } = await doGetNewCommodityNumber()
    if (code !== 200) return ElMessage.error('获取新增商品数失败')
    newGoodsCount.value = data
}
async function initEcharts(data: TradeStatisticItem[]) {
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
                top: '26%',
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
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'cross',
            },
        },
    }
    echartsRef.value.setOption(option)
}
/**
 * 获取交易统计
 */
async function initTradeStatistis(startTime?: string, endTime?: string) {
    const currentStamp = new Date().getTime()
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const startDate = !startTime && !endTime ? dateTool.getSubtracteDays(currentStamp, 7) : startTime!
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
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
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            return mapData.get(item.date)!
        }
        return item
    })
    initEcharts(resultArr)
}
/**
 * 获取订单信息
 */
async function initOrderInfo() {
    const { code, data } = await doGetOrderInfo()
    if (code !== 200) return ElMessage.error('获取订单信息失败')
    orderCountInfo.value = data
}
/**
 * 计算canvas grid宽度
 * @param {number} len 最大数位数
 */
function computeGridWidth(len: number) {
    return len <= 2 ? len * 30 : len * 12
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
</script>

<template>
    <div class="overview">
        <div style="display: flex; height: 422px; width: 100%">
            <div class="survey">
                <span class="title">实时概况</span>
                <div class="survey__content">
                    <div class="survey__item">
                        <div>今日访客数</div>
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
                <div id="main" style="height: 320px; width: 100%; margin-top: 18px"></div>
            </div>
        </div>
        <div class="schedule">
            <span class="title">待办事项</span>
            <div class="schedule__content">
                <div class="schedule__outborder">
                    <div class="schedule__item" @click="handleNavToUnpaid">
                        <div>待付款订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.unpaid || 0 }}</div>
                    </div>
                </div>
                <div class="schedule__outborder">
                    <div class="schedule__item" @click="handleNavToUnDelivery">
                        <div>待发货订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.undelivered || 0 }}</div>
                    </div>
                </div>
                <div class="schedule__outborder">
                    <div class="schedule__item" @click="handleNavToUnReceive">
                        <div>待收货订单</div>
                        <div class="schedule__item--num">{{ orderCountInfo.unreceived || 0 }}</div>
                    </div>
                </div>
            </div>
        </div>
        <div style="display: flex; width: 100%">
            <div class="shop">
                <div class="com">
                    <span class="title">商品交易量TOP10</span>
                    <div class="com__top">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.tradeVolumeIndex === index && 'com__top--active']"
                            style="margin-left: 12px"
                            @click="handleChangeVolumeIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.tradeVolumeList"
                    style="width: 100%; margin-top: 22px"
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
                    :cell-style="{ fontSize: '12px', color: '#333' }"
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
                    <el-table-column label="商品名称" width="300">
                        <template #default="{ row }">
                            <div style="display: flex; align-items: center">
                                <img :src="row.pic" alt="" style="width: 40px; height: 40px; object-fit: cover" />
                                <div class="shopName-overflow">{{ row.productName }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="销量" align="right">
                        <template #default="{ row }">
                            <div>{{ row.realTradeVolume }}</div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <div class="goods">
                <div class="com">
                    <span class="title">商品交易额TOP10</span>
                    <div class="com__top">
                        <span
                            v-for="(item, index) in enumTimeArr"
                            :key="item.title"
                            :class="[rankAggregation.tradeAmountIndex === index && 'com__top--active']"
                            style="margin-left: 12px"
                            @click="handleChangeAmountIndex(index)"
                            >{{ item.title }}</span
                        >
                    </div>
                </div>
                <el-table
                    :data="rankAggregation.tradeAmountList"
                    style="width: 100%; margin-top: 22px"
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
                    :cell-style="{ fontSize: '12px', color: '#333' }"
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
                    <el-table-column label="商品名称" width="300">
                        <template #default="{ row }">
                            <div style="display: flex; align-items: center">
                                <img :src="row.pic" alt="" style="width: 40px; height: 40px; object-fit: cover" />
                                <div class="shopName-overflow">{{ row.productName }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="实付金额(元)" align="right">
                        <template #default="{ row }">
                            <div>￥{{ divTenThousand(row.realTradingVolume) }}</div>
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
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        color: #797979;
        cursor: pointer;
        @include m(active) {
            color: #555cfd;
        }
    }
}
@include b(statistics) {
    flex: 1;
    background: #ffffff;
    padding: 7px 20px 0 30px;
    margin-bottom: 12px;
    @include e(top) {
        height: 46px;
        @include flex(space-between);
    }
}

@include b(survey) {
    margin-right: 12px;
    width: 41%;
    padding: 20px;
    background: #fff;
    margin-bottom: 12px;

    @include e(content) {
        display: flex;
        flex-direction: column;
        margin-top: 35px;
    }

    @include e(item) {
        margin-top: 20px;
        font-size: 16px;
        width: 100%;
        padding: 16px;
        height: 100px;
        box-sizing: border-box;
        background: linear-gradient(to right, rgba(85, 92, 253, 0.1), rgba(85, 92, 253, 0));

        @include m(num) {
            font-size: 24px;
            font-weight: bold;
            margin-top: 10px;
            color: #000;
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
        justify-content: flex-start;
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
            font-weight: bold;
            color: rgb(40, 45, 48);
            margin-top: 15px;
        }
    }
}

$overviewHotHeight: 690px;

@include b(shop) {
    height: $overviewHotHeight;
    width: calc(50% - 6px);
    background: #ffffff;
    border-radius: 0px;
    font-size: 12px;
    color: #797979;
    padding: 20px 30px 0;
    margin-right: 12px;

    @include e(head) {
        width: 439px;
        height: 30px;
        background: #f7f8fa;
        display: flex;
        align-items: center;

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

@include b(goods) {
    height: $overviewHotHeight;
    width: calc(50% - 6px);
    background: #ffffff;
    border-radius: 0px;
    font-size: 12px;
    color: #797979;
    padding: 20px 30px 0;

    @include e(head) {
        height: 30px;
        background: #f7f8fa;
        display: flex;
        align-items: center;

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
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    margin-left: 5px;
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
</style>
