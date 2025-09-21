<script setup lang="ts">
import EditorFormData from '../packages/components/index/formModel'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { cloneDeep } from 'lodash-es'
import type { ComponentItem } from '../packages/components/index/formModel'
import { ElMessage } from 'element-plus'
import { VueDraggableNext } from 'vue-draggable-next'
import type { PropType } from 'vue'
import { useBaseSwiper } from '@/composables/useSwiper'

const $decorationStore = useDecorationStore()
const $props = defineProps({
    activePagesType: {
        type: String,
        default: '',
    },
    components: {
        type: Array as PropType<ComponentItem[]>,
        default() {
            return []
        },
    },
})
const lazyShow = ref(false)
onMounted(async () => {
    await nextTick()
    lazyShow.value = true
})
const $emit = defineEmits(['change'])
const second = ref(true)
const basicComponentList: ComponentItem[] = [
    {
        icon: 'paihangbang_paiming',
        value: 'compose',
        label: '组合组件',
    },
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
        label: '占位',
    },
    {
        icon: 'fengefu',
        value: 'separator',
        label: '分隔符',
    },
    {
        icon: 'dianpudaohang',
        value: 'navigation',
        label: '导航模块',
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
    {
        icon: 'shangchengshezhi',
        value: 'shopGoods',
        label: '店铺商品',
    },
    {
        icon: 'dizhi',
        value: 'positioningStyle',
        label: '定位',
    },
]
const componentList = computed(() => {
    console.log($props.activePagesType === 'CUSTOMIZED_PAGE' ? basicComponentList.filter((item) => item.value !== 'shopGoods') : basicComponentList)
    return $props.activePagesType === 'CUSTOMIZED_PAGE' ? basicComponentList.filter((item) => item.value !== 'shopGoods') : basicComponentList
})
// 营销组件
const marketComponents = ref<ComponentItem[]>([
    {
        icon: 'miaosha1',
        value: 'secKill',
        label: '秒杀',
    },
    {
        icon: 'zhibo1',
        value: 'live',
        label: '微信直播',
    },
])
const activePageType = computed(() => {
    return $decorationStore.activePageType
})

const { onMove, onEnd, onStart, onValidClick } = useBaseSwiper(componentList.value, $props.components)

/**
 * 点击添加组件
 */
const handleAddComponent = (currentComponent: ComponentItem) => {
    if (activePageType.value !== 'customize') {
        ElMessage.warning('该页面无法添加组件')
        return
    } else if (onValidClick(currentComponent)) {
        const FormData = cloneDeep(EditorFormData[currentComponent.value])
        // 只有在FormData存在showStyle属性时才赋值，避免类型错误
        if (currentComponent.value === 'shopGoods' && $props.activePagesType === 'SAME_CITY_MALL_HOME_PAGE' && FormData && 'showStyle' in FormData) {
            ;(FormData as { showStyle: string }).showStyle = 'automatic'
        }
        $emit('change', { ...currentComponent, id: Date.now(), formData: FormData })
    }
}
</script>

<template>
    <div class="editor__component editor__component_new">
        <div class="editor_component_wrap">
            <el-scrollbar style="height: 85%">
                <div class="editor_component_wrap_main">
                    <el-tabs class="demo-tabs">
                        <el-tab-pane label="基本组件">
                            <VueDraggableNext
                                v-if="lazyShow"
                                :list="componentList"
                                class="component_box"
                                :group="{ name: 'custom', pull: 'clone', put: false }"
                                item-key="value"
                                :sort="false"
                                :move="onMove"
                                @start="onStart"
                                @end="onEnd"
                            >
                                <div v-for="item in componentList" :key="item.value" style="width: 93px" @click="handleAddComponent(item)">
                                    <div class="component--item">
                                        <QIcon class="component--item--icon" :name="`icon-${item.icon}`" size="28px"></QIcon>
                                        <div>{{ item.label }}</div>
                                    </div>
                                </div>
                            </VueDraggableNext>
                        </el-tab-pane>
                        <el-tab-pane label="营销组件">
                            <VueDraggableNext
                                v-if="lazyShow"
                                v-show="second"
                                :list="marketComponents"
                                class="component_box"
                                :group="{ name: 'custom', pull: 'clone', put: false }"
                                item-key="value"
                                :sort="false"
                            >
                                <div v-for="item in marketComponents" :key="item.value" style="width: 93px" @click="handleAddComponent(item)">
                                    <div class="component--item">
                                        <QIcon class="component--item--icon" :name="`icon-${item.icon}`" size="28px"></QIcon>
                                        <div>{{ item.label }}</div>
                                    </div>
                                </div>
                            </VueDraggableNext>
                        </el-tab-pane>
                    </el-tabs>
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
