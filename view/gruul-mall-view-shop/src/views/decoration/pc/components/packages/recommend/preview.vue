<script setup lang="ts">
import recommend from './recommend'
import previewGoods from './components/preview-goods.vue'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof recommend>,
        default: recommend,
    },
})

watch(
    () => props.formData.radioKeys,
    (val) => {
        active.value = val[0]
    },
)

const active = ref(props.formData.radioKeys[0])
</script>

<template>
    <div class="recommend">
        <div class="main">
            <div class="title">
                <div class="title__text">
                    <div class="title__text-left">
                        <span class="title__text-left--name">{{ formData.name || '推荐好物' }}</span>
                        <span class="title__text-left--line"></span>
                    </div>
                </div>

                <div class="title__tab">
                    <div
                        v-for="item in formData.radioKeys"
                        :key="item"
                        class="title__tab-item cp"
                        :class="{ active: active === item }"
                        @click="active = item"
                    >
                        {{ item }}
                        <q-icon v-show="active === item" class="title__tab-item--icon" color="#F54319" name="icon-xuanzhongzhuangtai" size="20px" />
                    </div>
                </div>

                <div class="title__more cp">查看更多<q-icon name="icon-chevron-right" style="top: 1.3px" /></div>
            </div>

            <div class="m-t-10">
                <preview-goods :goods="formData.list[active]" :style="formData.style" />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(recommend) {
    height: 308px;
    background: #f6f6f6;
    width: 1220px;
    margin: 0 auto;
}

@include b(title) {
    display: flex;
    align-items: flex-end;
    color: #333333;
    height: 38px;
    line-height: 36px;

    @include e(text) {
        height: 28px;
        width: 104px;
        margin-bottom: 6px;
    }

    @include e(text-left) {
        display: inline-block;
        position: relative;
        height: 100%;
        @include m(name) {
            position: relative;
            line-height: 32px;
            font-size: 20px;
            inset: 0;
            font-weight: 500;
            z-index: 3;
        }

        @include m(line) {
            position: absolute;
            bottom: 0;
            left: 5px;
            right: 5px;
            height: 6px;
            background: linear-gradient(270deg, rgba(245, 67, 25, 0.5) 0%, rgb(245, 67, 25) 100%);
        }
    }

    @include e(tab) {
        display: flex;
        height: 36px;
        margin-left: 40px;
        flex: 1;
    }

    @include e(tab-item) {
        margin-right: 24px;
        font-size: 16px;
        position: relative;

        @include m(icon) {
            position: absolute;
            bottom: -16px;
            left: 0;
            right: 0;
            text-align: center;
        }
    }

    @include e(more) {
        font-size: 12px;
        color: #8c8c8c;
    }
}

.active {
    color: #f54319;
}
</style>
