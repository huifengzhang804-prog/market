<template>
    <div>
        <div :style="cBox">
            <span v-if="!hasSlot">{{ animationPercent }}%</span>
            <div v-if="hasSlot" :style="slotStyle">
                <slot name="content"></slot>
            </div>
            <div :style="faStyle">
                <div :style="leftBox">
                    <div :style="leftStyle"></div>
                </div>
                <div :style="rithStyle"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, useSlots, onUnmounted } from 'vue'

const props = defineProps({
    // 大小
    size: {
        type: Number,
        default: 60,
    },
    // 进度条颜色
    circleColor: {
        type: String,
        default: '#32CDA5',
    },
    // 圆环背景色
    defaultColor: {
        type: String,
        default: '#e9e9e9',
    },
    // 圆环宽度
    circleWidth: {
        type: Number,
        default: 5,
    },
    // 百分比
    percent: {
        type: Number,
        default: 0,
    },
    // 动画效果
    animation: {
        type: Boolean,
        default: false,
    },
    // 帧动画间隔
    animationSpeed: {
        type: Number,
        default: 1,
    },
    // 动画方向
    clockwise: {
        type: Boolean,
        default: true,
    },
    // 自定义七点位置
    direction: {
        type: Number,
        default: 0,
    },
    // 背景色
    bgColor: {
        type: String,
        default: '#ffffff',
    },
})

const emit = defineEmits(['animationPercent', 'onComplete'])
const slots = useSlots()
const animationPercent = ref(0)

// 检查是否有内容插槽
const hasSlot = computed(() => {
    return !!slots.content
})

// 确保size为偶数
const sizeAdapter = computed(() => {
    return props.size % 2 === 0 ? props.size : props.size - 1
})

// 计算样式
const cBox = computed(() => {
    const size = sizeAdapter.value
    const circleWidth = props.circleWidth
    return `
        position: relative;
        height: 50px;
        width: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
    `
})

const slotStyle = computed(() => {
    const size = sizeAdapter.value
    return `
        border-radius: 50%;
        height: ${size}px;
        width: ${size}px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: ${props.bgColor};
    `
})

const faStyle = computed(() => {
    const size = sizeAdapter.value
    const circleWidth = props.circleWidth
    return `
        position: absolute;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        top: 0;
        left: 0;
        height: 50px;
        width: 50px;
        border: ${circleWidth}px ${props.defaultColor} solid;
        transform: rotate(${props.direction}deg) rotateY(${props.clockwise ? 0 : 180}deg);
    `
})

const leftBox = computed(() => {
    const size = sizeAdapter.value
    const circleWidth = props.circleWidth
    return `
        height: 50px;
        width: 50px;
        position: absolute;
        top: -${circleWidth}px;
        left: -${circleWidth}px;
        opacity: 1;
        clip: rect(0 25px 50px 0);
    `
})

const leftStyle = computed(() => {
    const size = sizeAdapter.value
    const circleWidth = props.circleWidth
    const percent = props.animation ? animationPercent.value : props.percent
    return `
        height: 50px;
        width: 50px;
        border: ${circleWidth}px ${props.circleColor} solid;
        border-radius: 50%;
        z-index: 0;
        position: absolute;
        top: 0px;
        left: 0px;
        transform: rotate(${percent > 50 ? 180 : (percent / 100) * 360}deg);
        clip: rect(0 50px 50px 25px);
    `
})

const rithStyle = computed(() => {
    const size = sizeAdapter.value
    const circleWidth = props.circleWidth
    const percent = props.animation ? animationPercent.value : props.percent
    return `
        height: 50px;
        width: 50px;
        position: absolute;
        border: ${circleWidth}px ${percent > 50 ? props.circleColor : props.defaultColor} solid;
        border-radius: 50%;
        z-index: ${percent > 50 ? 0 : 100};
        top: -${circleWidth}px;
        left: -${circleWidth}px;
        transform: rotate(${percent > 50 ? (percent / 100) * 360 : 0}deg);
        clip: rect(0 50px 50px 25px);
    `
})

// 动画帧引用
let animationFrameId = null

// 动画加载方法
const loadAnimation = () => {
    if (animationFrameId) {
        cancelAnimationFrame(animationFrameId)
        animationFrameId = null
    }
    animationPercent.value = 0
    const start = performance.now()
    const duration = props.animationSpeed // 动画总时长，单位 ms
    const targetPercent = props.percent

    function animate(now) {
        const elapsed = now - start
        let percent = Math.min((elapsed / duration) * targetPercent, targetPercent)
        animationPercent.value = Math.round(percent)
        emit('animationPercent', animationPercent.value)
        if (percent < targetPercent) {
            animationFrameId = requestAnimationFrame(animate)
        } else {
            emit('onComplete')
            animationFrameId = null
        }
    }
    animationFrameId = requestAnimationFrame(animate)
}

// 监听动画属性变化
watch(
    [() => props.animation, () => props.percent],
    ([animation, percent], [oldAnimation, oldPercent]) => {
        if (animation) {
            // 只有在 percent 变化 或 animation 由 false->true 时才触发动画
            if (percent !== oldPercent || animation !== oldAnimation) {
                loadAnimation()
            }
        } else {
            if (animationFrameId) {
                cancelAnimationFrame(animationFrameId)
                animationFrameId = null
            }
            animationPercent.value = percent
        }
    },
    { immediate: true },
)

// 组件卸载时取消动画帧，防止内存泄漏
onUnmounted(() => {
    if (animationFrameId) {
        cancelAnimationFrame(animationFrameId)
        animationFrameId = null
    }
})
</script>
