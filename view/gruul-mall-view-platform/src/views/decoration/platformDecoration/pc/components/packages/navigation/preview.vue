<script setup lang="ts">
import navigation from './navigation'
import type { Navigation } from './navigation'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<Navigation[]>,
        default: navigation,
    },
})

const check = ref()
const select = (link: string, index: number) => {
    if (classify(link)) return (check.value = undefined)
    check.value = index
}

const classify = (link: string) => link === '所有分类'
</script>

<template>
    <div class="line">
        <div class="main">
            <div
                v-for="({ text, link }, index) in formData"
                :key="text"
                class="line__col cp"
                :class="{ classify: classify(link) }"
                @click="select(link, index)"
            >
                <q-icon v-if="classify(link)" name="icon-fenlei1" size="24px" class="m-r-16" />

                <span :class="{ active: check === index, 'm-t-2': classify(link) }">
                    {{ text }}
                </span>

                <div v-if="!classify(link) && check === index" class="line__col-border">
                    <q-icon name="icon-xuanzhongzhuangtai" size="28px" color="#f54319" />
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(line) {
    background-color: #fff;
    height: 42px;
    font-size: 16px;

    .main {
        display: flex;
    }

    @include e(col) {
        padding: 0px 24px;
        overflow: hidden;
        display: flex;
        align-items: center;
        position: relative;
    }

    @include e(col-border) {
        position: absolute;
        top: 23px;
        left: 50%;
        transform: translateX(-50%);
    }
}

.line__col + .line__col {
    margin-left: 24px;
}

@include b(classify) {
    color: #fff;
    background: linear-gradient(90deg, rgb(245, 67, 25) 0%, rgb(253, 146, 36) 100%);
}

@include b(active) {
    color: #f54319;
}
</style>
