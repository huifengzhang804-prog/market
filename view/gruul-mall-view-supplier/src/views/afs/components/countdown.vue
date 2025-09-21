<script setup lang="ts">
import { ref, onBeforeUnmount, watch } from 'vue'

const $props = defineProps({
    ms: {
        type: Boolean,
        default: true,
    },
    miao: { type: Boolean, default: true },
    timeout: { type: [String, Number], default: 0 },
    unmounted: { type: Boolean, default: false },
})

const time = ref('')
const end = ref(false)
let _T: number | null

watch(
    () => $props.unmounted,
    (val) => {
        if (_T) {
            clearTimeout(_T)
        }
    },
)
watch(
    () => $props.timeout,
    (val) => {
        clock(Number(val) - new Date().getTime())
    },
    { immediate: true },
)

function clock(times) {
    if ($props.ms) {
        millisecond(times)
    }
    //页面加载时设置需要倒计时的秒数，计算小时
}
/**
 * 毫秒
 * @param {*} times
 */
function millisecond(times: number) {
    time.value = doGetTime(times)
    //计算秒
    if (times > 0) {
        times = times - 1000
        _T = setTimeout(function () {
            clearTimeout(_T)
            clock(times)
        }, 1000)
        return
    }
    end.value = true
}
function doGetTime(millisecond: number) {
    var days = Math.floor(millisecond / (1000 * 60 * 60 * 24))
    var hours = Math.floor((millisecond % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    var minutes = Math.floor((millisecond % (1000 * 60 * 60)) / (1000 * 60))
    var seconds = Math.floor((millisecond % (1000 * 60)) / 1000)
    return days + '天' + hours + '时' + minutes + '分' + seconds + '秒'
}
</script>

<template>
    <time v-if="!end" class="time">剩余{{ time }}</time>
</template>

<style scoped lang="scss">
@include b(time) {
    font-size: 14px;
    font-weight: normal;
    color: #ff7417;
}
</style>
