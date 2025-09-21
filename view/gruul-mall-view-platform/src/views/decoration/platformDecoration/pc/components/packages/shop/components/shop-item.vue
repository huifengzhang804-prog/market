<script setup lang="ts">
import shopTypeTag from './shop-type-tag.vue'
import { type ShopList, shopTypeMap } from '@/components/q-select-shop/type'

defineProps<{
    item: ShopList
}>()
</script>

<template>
    <div class="item">
        <img class="item__img" :src="item.logo" />
        <div class="item__info">
            <div class="item__info-name">
                <shop-type-tag :shop-type="item.shopType" />
                <div class="item__info-name--text">{{ item.name }}</div>
            </div>
            <div class="item__info-new-tip m-t-8">
                <q-icon name="icon-notice" class="item__info-new-tip--icon" color="#F54319" />
                <div class="item__info-new-tip--text">{{ item.newTips ?? '暂无公告' }}</div>
            </div>
            <div class="item__info-score">
                <div class="item__info-score-icon">
                    <q-icon
                        v-for="i in 5"
                        :key="i"
                        name="icon-shoucang1"
                        size="16px"
                        class="m-r-2"
                        :class="{ active: i <= parseFloat(item.score) }"
                    />
                </div>
                <div class="item__info-score-btn">
                    <q-icon name="icon-dianpu1" color="#F54319" class="m-r-6" />
                    进入店铺
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(item) {
    padding: 16px;
    display: flex;
    align-items: center;
    width: 384px;
    height: 150px;
    font-size: 12px;
    background: #fff;

    @include e(img) {
        width: 96px;
        height: 96px;
        margin-right: 18px;
    }

    @include e(info-name) {
        display: flex;
        font-weight: 500;
        color: #000;
        text-align: justify;
        @include m(text) {
            width: 200px;
            @include utils-ellipsis(1);
        }
    }

    @include e(info-new-tip) {
        color: #999999;
        display: flex;

        @include m(text) {
            height: 34px;
            width: 212px;
            margin-left: 4px;
            @include utils-ellipsis(2);
        }
    }

    @include e(info-score) {
        margin-top: 29px;
        display: flex;
        justify-content: space-between;
    }

    @include e(info-score-btn) {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 82px;
        height: 28px;
        cursor: pointer;
        color: #f54319;
        border: 1px solid #f54319;
        border-radius: 2px;
        background: #f543191a;
        margin-right: 8px;
    }
}

@include b(active) {
    color: #fd9224;
}
</style>
