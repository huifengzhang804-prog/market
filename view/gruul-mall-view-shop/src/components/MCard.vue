<script lang="ts" setup>
const $props = defineProps({
    modelValue: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:modelValue'])
const handleShow = () => {
    $emit('update:modelValue', !$props.modelValue)
}
</script>
<template>
    <transition name="card">
        <div v-show="$props.modelValue" id="card" class="card">
            <slot></slot>
        </div>
    </transition>
    <view class="fix" @click="handleShow">
        <span>{{ $props.modelValue ? '收起' : '展开' }}搜索条件</span>
        <q-icon name="icon-shangjiantou" size="12" color="#5b6982" :class="['fix__arrow', $props.modelValue ? '' : 'fix__down']" />
    </view>
</template>

<style lang="scss" scoped>
.card-enter-from,
.card-leace-to {
    opacity: 1;
}
.card-enter-active {
    /* transform: height 0.5s; */
    transition: height 0.5;
    animation: bounce 0.5s ease;
}
.card-leave-active {
    /* transform: height 1s; */
    transition: height 0.5;
    animation: bounce 0.5s ease reverse;
}
@keyframes bounce {
    0% {
        transform: scale(0);
    }
    50% {
        /* transform: scale(1.01); */
    }

    100% {
        transform: scale(1);
    }
}
@include b(card) {
    background: #f9f9f9;
    padding: 20px 20px 0 20px;
    position: relative;
}
@include b(fix) {
    width: 100%;
    cursor: pointer;
    @include flex();
    height: 40px;
    background: #f9f9f9;
    @include e(arrow) {
        margin-left: 4px;
    }
    @include e(down) {
        transform: rotate(180deg);
    }
}
</style>
