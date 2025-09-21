<script lang="ts" setup>
import LeftMenu from './components/menu/index.vue'
import EditorPreview from './components/editor-preview.vue'
import setting from './components/packages/index/setting'
import messageBox from './components/message-box.vue'
import Sidebar from './components/sidebar/index.vue'
import { defaultOtherData } from './components/menu/common'
import { getDefaultEndComponents, getDefaultStartComponents } from './components/components-list'
import { Close } from '@element-plus/icons-vue'
import { doGetOpeningUp, doPostPlatformPagesSave, doPutPlatformPages } from '@/apis/decoration'
import { cloneDeep, isEqual } from 'lodash-es'
import { ElMessage } from 'element-plus'
import type { ComponentItem } from './components/packages/index/formModel'

const visible = ref(false)

// 当前编辑组件
const currentComponent = ref<ComponentItem>()

// 组件列表
const components = ref<ComponentItem[]>([])

// 顶部数据
const startComponents = ref<ComponentItem[]>(getDefaultStartComponents())

// 底部数据
const endComponents = ref<ComponentItem[]>(getDefaultEndComponents())

// 全部数据
const allComponents = computed(() => [...startComponents.value, ...components.value, ...endComponents.value])

// 引用活动页面或图文页面的组件
const includePage = ['navigation', 'swiper', 'recommend']
const pageComponents = computed(() => allComponents.value.filter((item) => includePage.includes(item.value)))

/**
 * 获取组件列表
 */
let id: any = null
let otherData = ref(defaultOtherData())

let originData: any = { startComponents: [], endComponents: [], components: [], otherData: {} }
const getComponents = async () => {
    startWatch = false
    try {
        const { data, success } = await doGetOpeningUp()
        if (success) {
            const properties = JSON.parse(data?.properties ?? '{}')

            const start = properties.topComponents ?? getDefaultStartComponents()
            const center = properties.components ?? []
            const end = properties.bottomComponents ?? getDefaultEndComponents()
            const other = properties.otherData ?? defaultOtherData()
            components.value = center
            startComponents.value = start
            endComponents.value = end
            otherData.value = other

            originData = cloneDeep({ startComponents: start, endComponents: end, components: center, otherData: other })
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
    getComponents()
})

/**
 * 校验基础设置
 */
let validateFn: () => void
const getValidate = (fn: () => void) => {
    validateFn = fn
}

const leftMenuRef = ref<InstanceType<typeof LeftMenu>>()
const validateOtherData = () => {
    const { couponImg, integralImg, seckillImg } = otherData.value

    if (!couponImg || !integralImg || !seckillImg) {
        ElMessage.error('请前往 基础设置 -> 上传必传图片！')
        leftMenuRef.value?.openMenu('基础设置')

        nextTick(() => {
            validateFn()
        })
        return false
    }
    return true
}

/**
 * 点击保存按钮
 */
let startWatch = false
const save = async () => {
    if (!editing.value) {
        startWatch = true
        return (editing.value = true)
    }
    if (!validateOtherData()) return

    const properties = {
        components: components.value.filter((item) => item.formData),
        otherData: otherData.value,
        topComponents: startComponents.value,
        bottomComponents: endComponents.value,
    }
    const payload = {
        name: '商城首页',
        properties,
        type: 'RECOMMENDED_MALL_HOME_PAGE',
        templateType: 'PLATFORM',
        endpointType: 'PC_MALL',
        id,
    }

    try {
        const saveFn = id ? doPutPlatformPages : doPostPlatformPagesSave
        const { success } = await saveFn(payload)
        if (!success) return ElMessage.error('保存装修失败！')

        ElMessage.success('装修保存成功！')
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
    originalData = cloneDeep(toRaw(component.formData))

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
    if (Array.isArray(component?.formData?.list)) {
        currentComponent.value = cloneDeep({ ...component, formData: { ...component.formData, list: [] } })
    } else {
        currentComponent.value = cloneDeep(component)
    }
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
 * 关闭
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
const { href } = router.resolve({ name: 'decoration' })
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
    endComponents.value = originData.endComponents
    otherData.value = originData.otherData

    // 关闭编辑状态
    editing.value = false
}

/**
 * 监测数据是否改变
 */
let isUpdate = false
watch(
    () => {
        return { allComponents, otherData }
    },
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

const dragging = ref(false)

provide('getValid', getValidate) // 用于获取其他组件校验 用于在外部打开校验
provide('showAddDialog', showAddDialog) // 打开新增弹框 用于添加组件 点击添加组件
provide('otherData', otherData) // 活动主图 用于基础设置 配置项
provide('startComponents', startComponents) // 顶部固定组件 用于基础设置 操作栏开关
provide('endComponents', endComponents) // 底部固定组件 用于基础设置 操作栏开关
provide('pageComponents', pageComponents) // 用到自定义页面的组件 用于删除时判断是否引用到自定义页面
</script>

<template>
    <div class="panel">
        <!-- 顶部操作 -->
        <div class="title f18">
            <span> 当前设计：PC商城 </span>

            <span> 商城首页装修 </span>

            <span class="title__action">
                <el-button id="q-btn-action-save" class="m-r-30 title__action--save" type="primary" @click="save">
                    {{ editing ? '保存' : '编辑' }}
                </el-button>

                <el-link type="primary" @click="goBack"> {{ editing ? '退出编辑' : '返回后台' }} </el-link>
            </span>
        </div>

        <!-- 组件菜单 -->
        <LeftMenu v-show="editing" ref="leftMenuRef" v-model:dragging="dragging" />

        <!-- 操作栏 -->
        <Sidebar />

        <!-- 预览 -->
        <EditorPreview
            v-model:endComponents="endComponents"
            v-model:startComponents="startComponents"
            :components="components"
            :dragging="dragging"
            :highlight-current="visible"
            :is-edit="editing"
            @add="showAddDialog"
            @change="setCurrentComponent"
            @del="handleDelComponent"
        />

        <!-- 编辑弹框 -->
        <el-dialog
            v-model="visible"
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            :modal="false"
            :show-close="false"
            :width="currentComponent?.width"
            align-center
            class="set-edit-dialog"
            destroy-on-close
            draggable
            @closed="close"
        >
            <!-- 头部 -->
            <template #header>
                <div class="my-header">
                    <div class="f16">{{ currentComponent?.label }}</div>
                    <el-icon class="my-header__icon" size="16px" @click="cancel">
                        <Close />
                    </el-icon>
                </div>
            </template>

            <!-- 主体 -->
            <Suspense>
                <template #default>
                    <component :is="setting[currentComponent!.value]" ref="settingRef" v-model:formData="currentComponent!.formData" />
                </template>
                <template #fallback>
                    <div :style="{ height: currentComponent?.loadingHeight || '0px' }" class="loading">
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
                    <el-button type="primary" @click="confirm"> 确定</el-button>
                </div>
            </template>
        </el-dialog>

        <message-box ref="messageBoxRef" message="取消后将会放弃本次填写的内容，是否确认取消？" title="数据未保存" @confirm="unSave" />
        <message-box
            ref="dialogMessageBoxRef"
            :min="true"
            :width="400"
            message="取消后将会放弃本次填写的内容"
            title="数据未保存，确定取消吗？"
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
        background: url('@/assets/image/decoration/loading.gif') no-repeat center / 98px;

        @include m(text) {
            margin-top: 50px;
            color: #888;
        }
    }
}
:deep(.set-edit-dialog) {
    --el-text-color-regular: #333;
    color: #333;

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
