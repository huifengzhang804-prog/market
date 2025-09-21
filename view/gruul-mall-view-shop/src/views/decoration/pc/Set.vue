<script setup lang="ts">
import leftMenu from './components/menu/index.vue'
import editorPreview from './components/editor-preview.vue'
import setting from './components/packages/index/setting'
import messageBox from './components/message-box.vue'
import sidebar from './components/sidebar/index.vue'
import { getDefaultStartComponents, getDefaultFixedEndComponents, getDefaultFixedStartComponents } from './components/components-list'
import { Close } from '@element-plus/icons-vue'
import { doGetShopOpeningUp, doPostShopPagesSave, doPutShopPages, doGetOpeningUp } from '@/apis/decoration'
import { cloneDeep, isEqual } from 'lodash-es'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import type { ComponentItem } from './components/packages/index/formModel'

const visible = ref(false)

// 当前编辑组件
const currentComponent = ref<ComponentItem>()

// 组件列表
const components = ref<ComponentItem[]>([])

// 顶部数据
const startComponents = ref<ComponentItem[]>(getDefaultStartComponents())

// 全部数据
const allComponents = computed(() => [...startComponents.value, ...components.value])

// 引用活动页面或图文页面的组件
const includePage = ['navigation', 'swiper', 'recommend']
const pageComponents = computed(() => allComponents.value.filter((item) => includePage.includes(item.value)))

const shopStore = useShopInfoStore()

/**
 * 获取组件列表
 */
let id: any = null
let originData: any = { startComponents: [], components: [] }
const getComponents = async () => {
    startWatch = false
    try {
        const { data, success } = await doGetShopOpeningUp(shopStore.shopInfo.id)
        if (success) {
            const properties = JSON.parse(data?.properties ?? '{}')
            const start = properties.topComponents ?? getDefaultStartComponents()
            const center = properties.components ?? []
            components.value = center

            startComponents.value = start

            originData = cloneDeep({ startComponents: start, components: center })
            id = data.id

            nextTick(() => {
                startWatch = true
            })
        } else {
            ElMessage.error('装修数据获取失败！')
        }
    } catch (error) {
        console.log('error', error)
    }
}

onBeforeMount(() => {
    getPlatformComponents()
    getComponents()
    // doGetSecondsKill() //勿删 用于校验是否登录 判断是否有token不准缺因为token有可能有但不正确 想的歪办法，修复方法获取组件时后端添加token校验
})

/**
 * 点击保存按钮
 */
let startWatch = false
const save = async () => {
    if (!editing.value) {
        startWatch = true
        return (editing.value = true)
    }

    const properties = {
        components: components.value.filter((item) => item.formData),
        topComponents: startComponents.value,
    }
    const payload = {
        name: '商城首页',
        properties,
        type: 'SHOP_HOME_PAGE',
        businessType: 'ONLINE',
        endpointType: 'PC_MALL',
        id,
    }

    try {
        const saveFn = id ? doPutShopPages : doPostShopPagesSave
        const { success } = await saveFn(payload)
        if (!success) return ElMessage.error('保存装修失败！')
        editing.value = false
        closeUnload() // 关闭监听并初始化监听状态
        getComponents() // 重新获取数据并开启监听
    } catch (error) {
        console.log('error', error)
    }
}

/**
 * 点击编辑
 */
let isEdit = ref(false)
let originalData: any
const setCurrentComponent = (component: ComponentItem) => {
    currentComponent.value = component

    originalData = cloneDeep(component.formData)

    isEdit.value = true
    visible.value = true
}

/**
 * 新增组件
 */
const handleAddComponent = (component: ComponentItem, index?: number) => {
    if (component && typeof index === 'number') {
        // 拖拽添加
        components.value[index] = component
    } else {
        // 点击添加
        components.value.push(component)
    }
}

/**
 * 打开弹框
 */
let addIndex = -1
const unOnce: string[] = ['recommend', 'goods']
const showAddDialog = (component: ComponentItem, index?: number) => {
    // 判断是否是只能添加一次的组件
    if (!unOnce.includes(component.value)) {
        const currentList = components.value.filter((item) => item.formData)
        const values = currentList.map((item) => item.value)

        if (values.includes(component.value)) {
            return ElMessage.error(component.label + '只能添加一次！')
        }
    }

    if (typeof index === 'number') {
        addIndex = index
    }

    currentComponent.value = component
    visible.value = true
}

/**
 * 点击确定
 */
const settingRef = ref()
const confirm = async () => {
    try {
        const isOk = await settingRef.value.formRef?.validate()
        if (isOk === false) return

        if (!isEdit.value) {
            // 判断点击还是拖动
            const index = addIndex === -1 ? undefined : addIndex
            handleAddComponent(currentComponent.value!, index)
        }
        visible.value = false
    } catch (error) {
        console.log('error', error)
    }
}

/**
 * 关闭修改弹框
 */
const dialogMessageBoxRef = ref<InstanceType<typeof messageBox>>()
const cancel = () => {
    if (isEdit.value) {
        if (!isEqual(toRaw(currentComponent.value!.formData), originalData)) {
            dialogMessageBoxRef.value?.open()
        } else {
            visible.value = false
        }
    } else {
        visible.value = false
        handleDelComponent({ index: addIndex })
    }
}

/**
 * 修改弹框数据不进行保存
 */
const dialogUnSave = () => {
    visible.value = false
    currentComponent.value!.formData = originalData
}

/**
 * 关闭后钩子初始化弹框数据
 */
const close = () => {
    addIndex = -1
    currentComponent.value = undefined
    isEdit.value = false
}

/**
 * 删除组件
 */
const handleDelComponent = (e: { index: number }) => {
    if (e.index === -1) return
    components.value.splice(e.index, 1)
}

/**
 * 返回后台
 */
const editing = ref(false)
const router = useRouter()
const goBack = () => {
    if (editing.value) {
        if (isUpdate) return open()

        return (editing.value = false)
    }
    router.push('/decoration')
}

/**
 * 打开更改提示弹框
 */
const messageBoxRef = ref<InstanceType<typeof messageBox>>()
const open = () => {
    messageBoxRef.value?.open()
}

/**
 * 退出编辑不保存数据
 */
const unSave = () => {
    // 关闭监听状态
    closeUnload()
    startWatch = false

    // 更改原始数据 监听已关闭数据不在监听数据更改
    components.value = originData.components
    startComponents.value = originData.startComponents

    // 关闭编辑状态
    editing.value = false
}

/**
 * 监测数据是否改变
 */
let isUpdate = false
watch(
    () => allComponents,
    () => {
        if (startWatch) {
            openUnload()
        }
    },
    { deep: true },
)

/**
 * 监听的事件
 */
const beforeunload = (event: BeforeUnloadEvent) => {
    event.preventDefault()
    event.returnValue = ''
}
/**
 * 打开监听
 */
const openUnload = () => {
    closeUnload()
    window.addEventListener('beforeunload', beforeunload)
    isUpdate = true
}
/**
 * 关闭监听
 */
const closeUnload = () => {
    window.removeEventListener('beforeunload', beforeunload)
    isUpdate = false
}

const other = ref({})
const fixedTopComponents = ref<ComponentItem[]>([]) // 顶部固定并且不能装修的组件
const fixedBottomComponents = ref<ComponentItem[]>([]) // 低部固定并且不能装修的组件
/**
 * 从平台端获取不能装修的组件
 */
const getPlatformComponents = async () => {
    try {
        const { data, success } = await doGetOpeningUp()
        if (data && success) {
            const { topComponents, bottomComponents, otherData } = JSON.parse(data.properties)

            const start = topComponents ?? getDefaultFixedStartComponents()

            fixedTopComponents.value = start.filter((item: ComponentItem) => item.value !== 'navigation')

            fixedBottomComponents.value = bottomComponents ?? getDefaultFixedEndComponents()
            other.value = otherData
        } else {
            ElMessage.error('装修数据获取失败！')
        }
    } catch (error) {
        console.log('error', error)
    }
}

const dragging = ref(false)

provide('showAddDialog', showAddDialog) // 打开新增弹框 用于添加组件 点击添加组件
provide('otherData', other) // 用于基础设置 配置项
provide('startComponents', startComponents) // 顶部固定组件 用于基础设置 操作栏开关
provide('pageComponents', pageComponents) // 用到自定义页面的组件 用于删除时判断是否引用到自定义页面
</script>

<template>
    <div class="panel">
        <!-- 顶部操作 -->
        <div class="title f18">
            <span> 当前设计：PC商城 </span>

            <span> 商城首页装修 </span>

            <span class="title__action">
                <el-button type="primary" class="m-r-30 title__action--save" @click="save">
                    {{ editing ? '保存' : '编辑' }}
                </el-button>

                <el-link type="primary" @click="goBack"> {{ editing ? '退出编辑' : '返回后台' }} </el-link>
            </span>
        </div>

        <!-- 组件菜单 -->
        <left-menu v-show="editing" ref="leftMenuRef" v-model:dragging="dragging" />

        <!-- 操作栏 -->
        <sidebar />

        <!-- 预览 -->
        <editor-preview
            v-model:startComponents="startComponents"
            :fixed-top-components="fixedTopComponents"
            :fixed-bottom-components="fixedBottomComponents"
            :components="components"
            :is-edit="editing"
            :dragging="dragging"
            :highlight-current="visible"
            @change="setCurrentComponent"
            @add="showAddDialog"
            @del="handleDelComponent"
        />

        <!-- 编辑弹框 -->
        <el-dialog
            v-model="visible"
            align-center
            destroy-on-close
            :width="currentComponent?.width"
            :show-close="false"
            :modal="false"
            draggable
            class="set-edit-dialog"
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            @closed="close"
        >
            <!-- 头部 -->
            <template #header>
                <div class="my-header">
                    <div class="f16">{{ currentComponent?.label }}</div>
                    <el-icon size="16px" class="my-header__icon" @click="cancel"><Close /></el-icon>
                </div>
            </template>

            <!-- 主体 -->
            <Suspense>
                <template #default>
                    <component :is="setting[currentComponent!.value]" ref="settingRef" v-model:form-data="currentComponent!.formData" />
                </template>
                <template #fallback>
                    <div class="loading" :style="{ height: currentComponent?.loadingHeight || '0px' }">
                        <div class="loading__text">
                            <div class="loading__text--text">编辑组件加载中...</div>
                        </div>
                    </div>
                </template>
            </Suspense>

            <!-- 底部 -->
            <template #footer>
                <div class="center__dialog-footer">
                    <el-button @click="cancel">取消</el-button>
                    <el-button type="primary" @click="confirm"> 确定 </el-button>
                </div>
            </template>
        </el-dialog>

        <message-box ref="messageBoxRef" title="数据未保存" message="取消后将会放弃本次填写的内容，是否确认取消？" @confirm="unSave" />

        <message-box
            ref="dialogMessageBoxRef"
            :width="400"
            :min="true"
            title="数据未保存，确定取消吗？"
            message="取消后将会放弃本次填写的内容"
            @confirm="dialogUnSave"
        />
    </div>
</template>

<style lang="scss" scoped>
@include b(title) {
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #fff;
    padding: 0 30px;
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    z-index: 9;
    @include e(action) {
        font-size: 14px;
        @include m(save) {
            width: 69px;
            height: 36px;
        }
    }
}

@include b(my-header) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #333;
    font-weight: 500;

    @include e(icon) {
        cursor: pointer;
        transition: all 0.3s;
        &:hover {
            transform: rotate(180deg);
        }
    }
}

@include b(panel) {
    height: 100%;
    color: #888;
}

@include b(loading) {
    display: flex;
    justify-content: center;
    width: 100%;
    @include e(text) {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;
        width: 100%;
        background: url('@/assets/images/decoration/loading.gif') no-repeat center / 98px;

        @include m(text) {
            margin-top: 50px;
            color: #888;
        }
    }
}
</style>

<style lang="scss">
.set-edit-dialog {
    --el-text-color-regular: #333;
    color: #333;
    padding: 0;

    & > .el-dialog__header {
        margin-right: 0;
        padding: 12px 20px;
        padding-bottom: 12px;
    }

    .el-dialog__body {
        padding: 12px 20px;
        --el-font-size-base: 12px;

        .el-form-item {
            margin-bottom: 16px;

            .el-form-item__label {
                padding-right: 6px;
            }
            .el-switch {
                padding: 0 8px;
            }
        }

        .el-form-item.is-error {
            .uploader {
                border-color: var(--el-color-danger);
            }
        }
    }

    .el-dialog__footer {
        padding: 0px 20px 12px;
    }
}
</style>
