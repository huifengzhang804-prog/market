<script setup lang="ts">
import EditorFormData from '../packages/components/index/formModel'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { cloneDeep } from 'lodash-es'
import { ElMessage } from 'element-plus'
import { VueDraggableNext } from 'vue-draggable-next'
import type { ComponentItem } from '../packages/components/index/formModel'
import type { PropType } from 'vue'
import { useBaseSwiper } from '@/composables/useSwiper'

const $decorationStore = useDecorationStore()
const $props = defineProps({
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
})
const $emit = defineEmits(['change'])
const first = ref(true)
const second = ref(true)
const componentList = ref([
    {
        icon: 'lunbotu',
        value: 'swiper',
        label: '轮播图',
    },
    {
        icon: 'shangpin',
        value: 'goods',
        label: '商品',
    },
    {
        icon: 'sousuo',
        value: 'search',
        label: '搜索',
    },
    {
        icon: 'biaotilan',
        value: 'titleBar',
        label: '标题栏',
    },
    {
        icon: 'zhanweifu',
        value: 'blankPaceholder',
        label: '空白占位',
    },
    {
        icon: 'fengefu',
        value: 'separator',
        label: '分隔符',
    },
    {
        icon: 'dianpudaohang',
        value: 'navigation',
        label: '金刚区',
    },
    {
        icon: 'mofang',
        value: 'cubeBox',
        label: '魔方',
    },
    {
        icon: '28fuwenbenkuang',
        value: 'richText',
        label: '富文本',
    },
    {
        icon: 'tupian',
        value: 'resizeImage',
        label: '图片',
    },
    {
        icon: 'zhibo1',
        value: 'video',
        label: '视频',
    },
])
// 营销组件
const marketComponents = ref([
    {
        icon: 'youhuiquan',
        value: 'coupon',
        label: '优惠券',
        showType: true,
    },
    {
        icon: 'miaosha1',
        value: 'secKill',
        label: '秒杀',
    },
])
const activePageType = computed(() => {
    return $decorationStore.activePageType
})

const { onMove, onEnd, onStart, onValidClick } = useBaseSwiper(componentList.value, $props.components)

/**
 * 点击添加组件
 * @param {*} currentComponent
 */
const handleAddComponent = (currentComponent: ComponentItem) => {
    if (activePageType.value !== 'customize') {
        ElMessage.warning('该页面无法添加组件')
        return
    } else if (onValidClick(currentComponent)) {
        const FormData = cloneDeep(EditorFormData[currentComponent.value])
        $emit('change', { ...currentComponent, id: Date.now(), formData: FormData })
    }
}
</script>

<template>
    <div class="editor__component editor__component_new">
        <!-- <div class="editor_component_top">
            <div class="top_button" :class="activeName === 'first' ? 'top_button_change' : ''" @click="activeName = 'first'">基本组件</div>
            <div class="top_button" :class="activeName === 'second' ? 'top_button_change' : ''" @click="activeName = 'second'">营销组件</div>
        </div> -->
        <div class="editor_component_wrap">
            <el-scrollbar style="height: 85%">
                <div class="editor_component_wrap_main">
                    <div class="component_title" @click="first = !first">
                        <div :class="first ? 'jiantou_bot' : 'jiantou_right'"></div>
                        <div class="component_title_text">基本组件</div>
                    </div>
                    <VueDraggableNext
                        v-show="first"
                        :list="componentList"
                        class="component_box"
                        :group="{ name: 'custom', pull: 'clone', put: false }"
                        item-key="label"
                        :sort="false"
                        :move="onMove"
                        @start="onStart"
                        @end="onEnd"
                    >
                        <div v-for="item in componentList" :key="item.label" style="width: 93px" @click="handleAddComponent(item)">
                            <div class="component--item">
                                <div class="iconfont component--item--icon" :class="`icon-${item.icon}`"></div>
                                <div>{{ item.label }}</div>
                            </div>
                        </div>
                    </VueDraggableNext>
                    <div class="component_title" @click="second = !second">
                        <div :class="second ? 'jiantou_bot' : 'jiantou_right'"></div>
                        <div class="component_title_text">营销组件</div>
                    </div>
                    <VueDraggableNext
                        v-show="second"
                        :list="marketComponents"
                        class="component_box"
                        :group="{ name: 'custom', pull: 'clone', put: false }"
                        item-key="label"
                    >
                        <div v-for="item in marketComponents" :key="item.label" style="width: 93px" @click="handleAddComponent(item)">
                            <div class="component--item">
                                <div class="iconfont component--item--icon" :class="`icon-${item.icon}`"></div>
                                <div>{{ item.label }}</div>
                            </div>
                        </div>
                    </VueDraggableNext>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
.component--item--icon {
    font-size: 28px;
    text-align: center;
    margin-bottom: 19px;
}
.editor__component_new .is-horizontal {
    display: none;
    overflow-x: hidden;
}
</style>
