<script setup lang="ts">
defineProps({
    mainTitle: {
        type: String,
        default: '',
    },
    subtitle: {
        type: String,
        default: '',
    },
})
</script>

<template>
    <div>
        <!-- 主标题 -->
        <div class="main-title">
            <div class="main-title__line left0">
                <div class="main-title__line--dot right0"></div>
            </div>

            {{ mainTitle }}

            <div class="main-title__line right0">
                <div class="main-title__line--dot left0"></div>
            </div>
        </div>

        <!-- 副标题 -->
        <div class="subtitle">{{ subtitle }}</div>
    </div>
</template>

<style lang="scss" scoped>
@include b(main-title) {
    position: relative;
    width: 232px;
    height: 33px;
    line-height: 33px;
    margin: 0 auto;
    font-size: 24px;
    font-weight: 500;
    text-align: center;
    color: #333333;

    @include e(line) {
        position: absolute;
        width: 46px;
        height: 2px;
        background-color: #f54319;
        top: 50%;
        transform: translateY(-50%);

        @include m(dot) {
            position: absolute;
            top: -4px;
            width: 10px;
            height: 10px;
            border-radius: 10px;
            background-color: #f54319;
        }
    }
}

@include b(subtitle) {
    margin-top: 6px;
    font-size: 14px;
    color: #8c8c8c;
    text-align: center;
}

.left0 {
    left: 0;
}

.right0 {
    right: 0;
}
</style>
