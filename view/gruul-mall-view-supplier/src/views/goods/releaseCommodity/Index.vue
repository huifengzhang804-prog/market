<template>
    <div class="release">
        <div class="release__step">
            <el-steps :active="stepIndex" align-center>
                <el-step title="编辑基本信息" />
                <el-step title="编辑销售信息" />
                <el-step title="编辑商品信息" />
            </el-steps>
        </div>
        <!-- 排除富文本组件 否则富文本组件将报错 -->
        <div class="tab_container">
            <keep-alive :exclude="['NewProductInfo']">
                <component
                    :is="reactiveComponent[currentStep]"
                    ref="componentRef"
                    @change-instance="changeInstance"
                    @changeSpecificationType="changeSpecificationType"
                ></component>
            </keep-alive>
        </div>
        <div class="release__tool">
            <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
            <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
            <el-button v-if="nextStep === '' && submitForm?.status === 'PLATFORM_SELL_OFF'" type="primary" round @click.once="navToList">
                返回
            </el-button>
            <el-button v-if="nextStep === '' && submitForm?.status !== 'PLATFORM_SELL_OFF'" type="primary" round @click="submitHandle">
                {{ $route.query.id && !($route.query.isCopy === 'true') ? '更新' : '上架' }}
            </el-button>
        </div>
    </div>
</template>
<script lang="ts" setup name="ReleaseCommodity">
import { useRoute } from 'vue-router'
import useReleaseCommodity from './hooks/useReleaseCommodity'
const {
    submitForm,
    reactiveComponent,
    currentStep,
    componentRef,
    preHandle,
    nextHandle,
    prevStep,
    nextStep,
    stepIndex,
    changeSpecificationType,
    navToList,
    submitHandle,
} = useReleaseCommodity()

const $route = useRoute()

const changeInstance = (instance: any) => {
    componentRef.value = instance
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@include b(navLine) {
    margin: 25px 0;
    height: 40px;
    line-height: 40px;
    background-color: #f8f8f8;
    padding-left: 15px;
    font-weight: 700;
}
@include b(release) {
    width: 100%;
    box-sizing: border-box;
    padding-bottom: 62px;
    overflow: hidden;
    overflow-y: auto;
    scrollbar-width: none;
    @include e(step) {
        width: 400px;
        margin: 0 auto;
    }
    @include e(tool) {
        width: 100%;
        @include flex;
        position: absolute;
        bottom: 0px;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        z-index: 100;
    }
}
// @Override css
:deep(.el-step__head.is-process) {
    color: #fff;
    .el-step__icon.is-text {
        background-color: #4b80ff;
        border-color: #4b80ff;
    }
}
:deep(.el-step__title.is-process) {
    font-weight: 400;
}
:deep(.el-step__title.is-finish) {
    color: var(--el-text-color-primary);
}
:deep(.el-step__head.is-finish) {
    color: #fff;
    .el-step__icon.is-text {
        background-color: #4b80ff;
        border-color: #4b80ff;
    }
}
:deep(.el-step__head.is-wait) {
    color: #fff;
    .el-step__icon.is-text {
        background-color: #d7dce4;
        border-color: #d7dce4;
    }
}
:deep(.el-step.is-center .el-step__line) {
    left: 65%;
    right: -30%;
}
</style>
