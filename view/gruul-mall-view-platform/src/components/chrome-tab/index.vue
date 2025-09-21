<template>
    <ul ref="tabsRef" class="container" :style="{ height: height }">
        <li v-for="item in tabList" :key="item.name" ref="tabItemsRef" :class="value === item.name && 'active'" @click="handleTabClick(item.name)">
            <span :style="{ width: width, textAlign: 'center', display: 'inline-block' }">{{ item.label }}</span>
        </li>
    </ul>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'

const $emit = defineEmits(['handle-tabs'])

const props = defineProps({
    // 绑定值
    value: {
        type: String,
        default: '',
    },
    // 关闭按钮
    closeBtn: {
        type: Boolean,
        default: false,
    },
    // 图片链接
    imgSrc: {
        type: String,
        default: '',
    },
    // 渲染数据
    tabList: {
        type: Array as PropType<any[]>,
        default() {
            return []
        },
    },
    // 高度
    height: {
        type: String,
        default() {
            return '45px'
        },
    },
    // 宽度
    width: {
        type: String,
        default() {
            return '105px'
        },
    },
})

const tabsRef = ref<HTMLElement | null>(null)
const tabItemsRef = ref<HTMLElement[]>([])

// 处理点击事件
const handleTabClick = (name: string) => {
    $emit('handle-tabs', name)
    // 点击后滚动到中间位置
    nextTick(() => {
        scrollActiveTabToCenter()
    })
}

// 滚动活动tab到中间位置
const scrollActiveTabToCenter = () => {
    if (!tabsRef.value) return

    const container = tabsRef.value
    const activeIndex = props.tabList.findIndex((item) => item.name === props.value)

    if (activeIndex === -1 || !tabItemsRef.value[activeIndex]) return

    const activeTab = tabItemsRef.value[activeIndex]
    const containerWidth = container.clientWidth
    const activeTabLeft = activeTab.offsetLeft
    const activeTabWidth = activeTab.offsetWidth

    // 计算滚动位置，使活动tab居中
    const scrollPosition = activeTabLeft - containerWidth / 2 + activeTabWidth / 2

    // 平滑滚动到计算的位置
    container.scrollTo({
        left: Math.max(0, scrollPosition),
        behavior: 'smooth',
    })
}

// 监听窗口大小变化
const handleResize = () => {
    scrollActiveTabToCenter()
}

// 监听值变化
watch(
    () => props.value,
    () => {
        nextTick(() => {
            scrollActiveTabToCenter()
        })
    },
)

// 监听tab列表变化
watch(
    () => props.tabList.length,
    () => {
        nextTick(() => {
            scrollActiveTabToCenter()
        })
    },
)

onMounted(() => {
    // 初始时滚动到活动tab
    nextTick(() => {
        scrollActiveTabToCenter()
    })
    // 添加窗口resize事件监听
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    // 移除窗口resize事件监听
    window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.container {
    background-color: #f5f7fa;
    height: 45px;
    padding: 8px 8px 0;
    box-sizing: border-box;
    display: flex;
    justify-content: flex-start;
    overflow-x: auto;
    overflow-y: hidden;
    flex-shrink: 0;
    &::-webkit-scrollbar {
        width: 3px;
        height: 3px;
    }
}

li {
    position: relative;
    font-size: 15px;
    border-radius: 10px 10px 0 0;
    display: flex;
    align-items: center;
    z-index: 1;
    cursor: pointer;
    transition: 0.3s all;
    // padding: 0 26px;
    font-weight: 550;
    margin-right: 12px;
    img {
        width: 16px;
        height: 16px;
        margin-left: 10px;
        margin-right: 10px;
    }

    .close {
        right: 8px;
        top: 8px;
        position: absolute;
        font-size: 18px;
        transform: rotate(45deg);
        border-radius: 50%;
        cursor: pointer;
        font-weight: bold;
        &:hover {
            background: #d0d0d0;
        }
    }
}

li.active {
    background: #fff;
    z-index: 2;
    color: #555cfd;
    &::before,
    &::after {
        border-top: 8px solid #fff;
    }
}

li::before,
li::after {
    position: absolute;
    content: '';
    width: 16px;
    height: 16px;
    border-radius: 50%;
    border: 8px solid transparent;
    transition: 0.3s all;
    //border-top: 5px solid #fff;
}

li::before {
    bottom: -8px;
    left: -24px;
    transform: rotate(135deg);
}

li::after {
    bottom: -7px;
    right: -24px;
    transform: rotate(-135deg);
}

li:hover:not(.active) {
    background: #ededed;
    &::before,
    &::after {
        border-top: 8px solid #ededed;
    }
}
</style>
