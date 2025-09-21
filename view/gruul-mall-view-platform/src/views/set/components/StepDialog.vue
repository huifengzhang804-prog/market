<script setup lang="ts">
import { PropType } from 'vue'

interface StepItem {
    text: string
    link?: string
}
interface Step {
    title: string
    steps: StepItem[]
    imgs: { url: string; width: number }[]
}
defineProps({
    steps: {
        type: Array as PropType<Step[]>,
        default: () => [],
    },
})
const nav = (step: StepItem) => {
    if (step.link) {
        window.open(step.link)
    }
}
</script>

<template>
    <div class="step_container">
        <template v-for="(item, index) in steps" :key="index">
            <div class="step_item_container">
                <div class="step_item_left">
                    <div class="step_icon">第{{ index + 1 }}步</div>
                </div>
                <div class="step_item_right">
                    <div class="step_title">{{ item.title }}</div>
                    <div class="step_desc_contaienr">
                        <div v-for="(it, i) in item.steps" :key="i" class="step" :style="`cursor: ${it.link ? 'pointer' : 'unset'}`" @click="nav(it)">
                            {{ i + 1 }}. {{ it.text }}
                        </div>
                    </div>
                    <div class="image_container">
                        <div v-for="(img, i) in item.imgs" :key="i" class="img_item" :style="`width:${img.width}%`">
                            <img class="img" :src="img.url" alt="" />
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </div>
</template>

<style lang="scss" scoped>
.step_container {
    overflow-y: scroll;
    height: 75vh;
    .step_item_container {
        display: flex;
        margin-bottom: 30px;
        .step_item_left {
            margin-right: 9px;
            flex-shrink: 0;
            .step_icon {
                border-radius: 40px;
                background: rgba(85, 92, 253, 0.1);
                border-radius: 40px;
                width: fit-content;
                padding: 4px 13px;
                color: rgb(85, 92, 253);
                font-size: 12px;
                font-weight: 400;
                line-height: 16px;
            }
        }
        .step_item_right {
            display: flex;
            flex-direction: column;
            .step_title {
                color: rgb(51, 51, 51);
                font-size: 20px;
                font-weight: 500;
                margin-bottom: 15px;
            }
            .step_desc_contaienr {
                .step {
                    margin-bottom: 10px;
                }
            }
            .image_container {
                display: flex;
                flex-wrap: wrap;
                .img_item {
                    .img {
                        width: 100%;
                    }
                }
            }
        }
    }
}
</style>
