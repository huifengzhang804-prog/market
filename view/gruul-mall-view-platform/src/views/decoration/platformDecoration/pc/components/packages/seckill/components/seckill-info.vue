<script lang="ts" setup>
import CountDown from '@/components/order/count-down/index.vue'
import { SeckillRoundVO, SeckillStatus } from '@/apis/decoration/type'
import type { PropType } from 'vue'
import DateUtil from '@utils/date'

const props = defineProps({
    name: {
        type: String,
        default: '',
    },
    round: {
        type: Object as PropType<SeckillRoundVO>,
        default: () => null,
    },
})

const times: ['hours', 'minutes', 'seconds'] = ['hours', 'minutes', 'seconds']

//获取当前场次 几点场
const currentRound = computed(() => {
    const start = props.round?.time?.start
    if (!start) {
        return ''
    }
    const time = start.split(' ')[1]
    return time.split(':')[0]
})
//日期工具
const dateUtil = new DateUtil()
//计算计时器所需参数
const timer = computed<{
    //计时器基础时间
    startTime: string
    //计时器倒计时超时时间 单位：秒
    timeout: number
}>(() => {
    const startTime = props.round?.time?.start
    if (!startTime) {
        return {
            startTime: '',
            timeout: 0,
        }
    }
    const now = new Date()
    return {
        startTime: dateUtil.getYMDHMSs(now),
        timeout: (new Date(SeckillStatus[props.round.status].timePick(props.round)).getTime() - now.getTime()) / 1000,
    }
})
</script>

<template>
    <div v-if="round" class="info">
        <div class="info__name">
            {{ name || '限时秒杀' }}
        </div>

        <div class="info__time">{{ currentRound }}点场</div>

        <div class="info__line" />

        <div class="info__text">{{ SeckillStatus[round?.status]?.timerDesc }}</div>

        <div class="info__countdown">
            <count-down :create-time="timer.startTime" :pay-timeout="timer.timeout + ''">
                <template #default="{ timeTable }">
                    <div class="countdown">
                        <div v-for="item in times" :key="item" class="countdown__time">
                            <div class="countdown__time--item">{{ timeTable[item] }}</div>
                            <div v-if="item !== 'seconds'" class="countdown__time--dot">:</div>
                        </div>
                    </div>
                </template>
            </count-down>
        </div>
    </div>

    <div v-else class="info un-seckill">
        <div class="info__name">
            {{ name || '限时秒杀' }}
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(info) {
    padding: 40px 24px 33px;
    min-width: 190px;
    height: 100%;
    color: #fff;
    background: url('@/assets/image/decoration/seckill.png');
    text-align: center;
    font-size: 16px;

    @include e(name) {
        font-size: 22px;
    }

    @include e(time) {
        margin-top: 27px;
    }

    @include e(line) {
        height: 2px;
        width: 16px;
        margin: 16px auto;
        background-color: #fff;
    }

    @include e(text) {
        margin: 16px auto 22px;
    }
}

@include b(countdown) {
    display: flex;
    justify-content: space-between;

    @include e(time) {
        display: flex;
        align-items: center;
        color: #fff;
        font-size: 20px;
        flex: 1;
        @include m(item) {
            width: 32px;
            height: 32px;
            border-radius: 2px;
            background: #000;
            line-height: 32px;
            text-align: center;
        }
        @include m(dot) {
            padding: 0 9px;
        }
    }
}

@include b(un-seckill) {
    line-height: 190px;
}
</style>
