<template>
    <div class="count-down">
        <span v-if="timeTable.day !== '00'">{{ timeTable.day }}天</span>
        <span> {{ timeTable.hours }}:{{ timeTable.minutes }}:{{ timeTable.seconds }}</span>
    </div>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, reactive, ref } from 'vue'
import useSetInterval from '../../hooks/useSetInterval'
import usePaymentCountdown from '../../hooks/usePaymentCountdown'
const $props = withDefaults(defineProps<{ createTime: string; payTimeout?: string }>(), {
    createTime: '',
    payTimeout: '',
})
const timeTable = reactive({
    day: '00',
    hours: '00',
    minutes: '00',
    seconds: '00',
})
const isFinish = ref(true)
const { clean } = useSetInterval(
    () => {
        console.log('donging')
        const { day, hours, minutes, seconds } = usePaymentCountdown($props.createTime, $props.payTimeout)
        timeTable.day = day.value
        timeTable.hours = hours.value
        timeTable.minutes = minutes.value
        timeTable.seconds = seconds.value
        if (day.value === '00' && hours.value === '00' && minutes.value === '00' && seconds.value === '00') {
            isFinish.value = true
            nextTick(() => clean())
        } else {
            isFinish.value = false
        }
    },
    1000,
    { immediate: true, immediateCallback: true },
)
onBeforeUnmount(() => clean())
</script>

<style scoped>
.count-down {
    color: red;
}
</style>
