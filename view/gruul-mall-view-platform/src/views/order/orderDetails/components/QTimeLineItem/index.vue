<script setup lang="ts">
export interface QTimeLineItemProps {
    data: QTimeLineItemData
    curColor?: string
}

export interface QTimeLineItemData {
    title: string
    content: string
    time: string
    // 是否是当前状态
    isCurrent?: boolean
    // 是否是最后一条
    isLast?: boolean
}
const props = withDefaults(defineProps<QTimeLineItemProps>(), {
    data: () => {
        return {
            title: '',
            content: '',
            time: '',
            isCurrent: false,
            isLast: false,
        }
    },
    curColor: 'rgb(85, 92, 253)',
})
</script>

<template>
    <div class="q_time_line_item">
        <div class="q_time_line_item_line_container">
            <slot name="icon">
                <div
                    class="q_time_line_item_line_icon"
                    :class="{
                        q_time_line_item_line_current: data.isCurrent,
                    }"
                ></div>
            </slot>
            <div v-if="!data.isLast" class="q_time_line_item_line"></div>
        </div>
        <div class="q_time_line_item_right_container">
            <div
                class="q_time_line_item_title"
                :class="{
                    q_time_line_item_title_current: data.isCurrent,
                }"
            >
                <slot name="title">
                    {{ data.title }}
                </slot>
            </div>
            <div class="q_time_line_item_content_container">
                <div class="q_time_line_item_content">{{ data.content }}</div>
                <div class="q_time_line_item_time">{{ data.time }}</div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.q_time_line_item {
    display: flex;
    width: 100%;
    min-width: 314px;
    .q_time_line_item_line_container {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex-shrink: 0;
        margin-right: 8px;
        .q_time_line_item_line_icon {
            width: 16px;
            height: 16px;
            border-radius: 50%;
            border: 1px solid v-bind(curColor);
            position: relative;
            &.q_time_line_item_line_current {
                border: 1px solid v-bind(curColor);
                // 放大动画
                animation: enlargeAnimation 2s ease-in-out infinite;
                @keyframes enlargeAnimation {
                    0% {
                        transform: scale(0.8);
                    }
                    50% {
                        transform: scale(1.2);
                    }
                    100% {
                        transform: scale(0.8);
                    }
                }
                &::before {
                    background: v-bind(curColor);
                }
            }
            &::before {
                position: absolute;
                content: '';
                width: 8px;
                height: 8px;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
                border-radius: 50%;
                background: v-bind(curColor);
            }
        }
        .q_time_line_item_line {
            flex: 1;
            border: 1px solid rgb(235, 235, 235);
        }
    }
    .q_time_line_item_right_container {
        display: flex;
        flex-direction: column;
        flex: 1;
        margin-bottom: 20px;
        transform: translateY(-2px);
        .q_time_line_item_title {
            color: rgb(102, 102, 102);
            font-size: 14px;
            font-weight: bold;
            line-height: 20px;
            margin-bottom: 4px;
            &.q_time_line_item_title_current {
                color: #333333;
            }
        }
        .q_time_line_item_content_container {
            display: flex;
            align-items: center;
            justify-content: space-between;
            color: rgb(153, 153, 153);
            font-size: 12px;
            line-height: 20px;
        }
    }
}
</style>
