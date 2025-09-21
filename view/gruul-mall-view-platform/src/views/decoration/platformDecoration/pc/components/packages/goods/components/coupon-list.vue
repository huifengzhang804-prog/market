<script setup lang="ts">
const props = defineProps<{
    couponList: any[]
}>()

const arr = [{ sourceDesc: '无优惠券' }]

const list = computed(() => {
    const value = props.couponList.length ? props.couponList : arr
    return value.slice(0, 3)
})
</script>

<template>
    <div class="tags">
        <div v-for="(item, index) in list" :key="index" class="tags__item" :class="{ last: index === 2, center: index === 1 }">
            {{ item.sourceDesc }}
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(tags) {
    display: flex;

    @include e(item) {
        padding: 2px 4px;
        background: #f543190d;
        color: #f54319;
        font-size: 12px;
        border-radius: 2px;
    }
}

@include b(center) {
    margin: 0 8px;
}

@include b(last) {
    flex: 1;
    @include utils-ellipsis;
}
</style>
