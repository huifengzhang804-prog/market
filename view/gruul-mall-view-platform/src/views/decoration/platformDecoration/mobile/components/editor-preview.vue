<script setup lang="ts">
import type { PropType } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { useDecorationStore } from '@/store/modules/decoration'
import preview from '../packages/components/index/preview'
import { cloneDeep } from 'lodash-es'
import editorFormData from '../packages/components/index/formModel'
import type { ComponentItem } from '../packages/components/index/formModel'
import { usePreviewSwiper } from '@/composables/useSwiper'
import miniProgramCapsule from '@/assets/image/miniProgramCapsule.png'

const $props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
    isPreview: {
        type: Boolean,
        default() {
            return false
        },
    },
    canAddComponent: {
        type: Boolean,
        default: true,
    },
    activePagesType: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['change', 'add', 'del'])
const $decorationStore = useDecorationStore()
const delFlag = ref(-1)
// 拖动的组件的下角标
// const dragStarIndex = ref(-1)
// 选中的当前项
const currentFlag = ref(-1)
const activePageType = computed(() => {
    return $decorationStore.activePageType
})
const isShowDelBtn = computed(() => {
    return (
        $decorationStore.activePageType !== 'classification' &&
        $decorationStore.activePageType !== 'control' &&
        $decorationStore.activePageType !== 'userCenter' &&
        !$props.isPreview
    )
})

const handleAddComponent = (e: any) => {
    const index = e.newDraggableIndex
    const com = $props.components[index]
    const FormData = editorFormData[com.value]
    const cloneFormData = cloneDeep(FormData)
    $emit(
        'add',
        {
            ...com,
            id: Date.now(),
            formData: cloneFormData,
        },
        index,
    )
}
/**
 * 删除当前组件
 * @param {number} i
 */
const handleDelCurCom = (i: number) => {
    delTip()
    $emit('del', { index: i })
}
/**
 * 父级调用设置当前选中的组件
 */
const setCurrentFlag = (i: number) => {
    console.log('------')
    currentFlag.value = i
}
/**
 * 监听组件设置切换
 * @param {ComponentItem} currentComponent 当前组件信息
 * @param {number} i
 */
function handleCurrentComponent(currentComponent: ComponentItem, i: number) {
    if ($props.isPreview) return
    currentFlag.value = i
    $decorationStore.SET_ACTIVE_COMINDEX(i)
    $emit('change', currentComponent)
}
function delTip() {
    if (document.querySelector('.el-popover')) {
        document.querySelector('.el-popover')?.remove()
    }
}

const { onMove, onEnd } = usePreviewSwiper($props.components)

defineExpose({
    setCurrentFlag,
    currentFlag,
})
</script>

<template>
    <div class="editor__preview">
        <div class="editor__preview__statusBar" :style="{ height: $decorationStore.getEndpointType === 'WECHAT_MINI_APP' ? '80px' : '' }">
            <div class="editor__preview__statusBar__time">
                <!-- 时间 -->
                <div>12:00</div>
                <!-- 电量 -->
                <div>
                    <span style="color: #fff; width: 10px; height: 10px; background-color: greenyellow">100%</span>
                </div>
            </div>
            <div style="width: 100%">
                <div style="display: flex; align-items: center; width: 100%; justify-content: end">
                    <!-- 小程序胶囊 -->
                    <div v-if="$decorationStore.getEndpointType === 'WECHAT_MINI_APP'" class="editor__preview__statusBar__miniProgramCapsule">
                        <!-- 左边是三个点 -->
                        <div class="editor__preview__statusBar__miniProgramCapsule__icon">
                            <!-- <QIcon name="icon-gengduo-copy" size="16px"></QIcon> -->
                            <span style="width: 4px; height: 4px; background-color: #000; border-radius: 50%"></span>
                            <span style="width: 7px; height: 7px; background-color: #000; border-radius: 50%"></span>
                            <span style="width: 4px; height: 4px; background-color: #000; border-radius: 50%"></span>
                        </div>
                        <div class="editor__preview__statusBar__miniProgramCapsule__line"></div>
                        <!-- 右边是圆环 -->
                        <div class="editor__preview__statusBar__miniProgramCapsule__text"></div>
                    </div>
                </div>
            </div>
        </div>
        <el-scrollbar :noresize="true">
            <VueDraggableNext
                :list="$props.components"
                :sort="true"
                :group="activePageType === 'customize' ? 'custom' : ''"
                animation="500"
                :scroll="true"
                :disabled="$props.isPreview"
                :move="onMove"
                @add="handleAddComponent"
                @end="onEnd"
            >
                <transition-group>
                    <div
                        v-for="(component, i) of $props.components"
                        :id="`editor-preview-com-${i}`"
                        :key="i + 1"
                        :class="[
                            'component--item',
                            component.value !== undefined && delFlag === i ? 'iscur__component--item' : '',
                            currentFlag === i ? 'select__component--item' : '',
                        ]"
                        :style="$props.isPreview ? 'border:none' : ''"
                        style="width: 375px"
                        @click="handleCurrentComponent(component, i)"
                        @mouseover="delFlag = i"
                        @mouseleave="delFlag = -1"
                    >
                        <div
                            v-if="$decorationStore.getEndpointType === 'WECHAT_MINI_APP'"
                            style="width: calc(100% - 110px); position: absolute; top: 25px; left: 0; z-index: 100; text-align: center"
                        >
                            <van-search v-if="component.formData?.searchShow === 4" disabled placeholder="请输入搜索关键词" shape="round" />
                            <view v-else-if="component.formData?.classificationTitle" class="search-title">
                                {{ component.formData?.classificationTitle }}
                            </view>
                        </div>

                        <div v-show="isShowDelBtn && delFlag === i" class="component--item__tan">
                            <div class="component--item__text" @click="handleCurrentComponent(component, i)">编辑</div>
                            <el-icon v-if="canAddComponent" class="component--item__icon" @click="handleDelCurCom(i)"><i-ep-delete /></el-icon>
                        </div>
                        <div
                            v-if="i === 0"
                            style="width: 100%; background-color: #fff"
                            :style="{ height: $decorationStore.getEndpointType === 'WECHAT_MINI_APP' ? '80px' : '33px' }"
                        ></div>
                        <component
                            :is="preview[component.value]"
                            v-model:form-data="component.formData"
                            :activePagesType="activePagesType"
                        ></component>
                    </div>
                </transition-group>
            </VueDraggableNext>
        </el-scrollbar>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';

.search-title {
    font-size: 18px;
    position: relative;
    top: 14px;
    font-weight: 500;
    font-family: PingFang SC;
    left: 10px;
}
</style>
