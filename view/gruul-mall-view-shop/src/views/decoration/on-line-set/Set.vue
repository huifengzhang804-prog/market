<script lang="ts" setup>
import EditorComponent from './components/editor-component.vue'
import EditorPreview from './components/editor-preview.vue'
import EditorForm from './components/editor-form.vue'
import MallTemplate from './template/index.vue'
import EditorPage from '@/views/decoration/on-line-set/components/editor-page.vue'
import EditorControl from '@/views/decoration/on-line-set/components/editor-control.vue'
import SetPages from './pages/index.vue'
import NavBar from './packages/components/navBar/nav-bar'
import classification from './packages/components/classification/classification'
import { ElMessage } from 'element-plus'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { doGetPlatformPages, doPostShopPagesSave, doPutShopPages, doPostClonePlatformPage } from '@/apis/decoration'
import type { ComponentItem } from './packages/components/index/formModel'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { storeToRefs } from 'pinia'
import { ONLY_ONE_COMPONENTS } from '@/composables/useSwiper'

const $decorationStore = useDecorationStore()
// 左侧导航栏下标
// const currentTab = ref(0)
// 当前编辑组件
const currentComponent = ref<ComponentItem>()
// 组件列表
const components = ref<ComponentItem[]>([])
// 控件列表
// const controllerComponentList = ref<ComponentItem[]>([])
const editorPreviewRef = ref<typeof EditorPreview>()
// 获取当前页面操作栏
const { activePageType } = storeToRefs(useDecorationStore())

//当前编辑类型  template模板 pages页面
const activeType = ref('template')
//当前编辑页面类型
const activePagesType = ref('SHOP_HOME_PAGE')
const activePagesTypeList = {
    SHOP_HOME_PAGE: '店铺首页',
    SHOP_CATEGORY_PAGE: '店铺分类',
    SHOP_BOTTOM_NAVIGATION_PAGE: '底部导航',
    SHOP_CUSTOMIZED_PAGE: '自定义页面',
}
//当前编辑页面名称
const name = ref('店铺首页')
//当前编辑页面说明
const remark = ref('')
//当前编辑页面id
const id = ref('')
//是否在编辑/新增页面
const iseditor = ref(false)
//平台装修页面 dialog
const dialogPlatformPage = ref(false)
//平台装修页面列表
const platformPageList = ref([])
const setPage = ref()
const { shopMode } = useShopInfoStore().getterShopInfo

/**
 * 显示平台装修页面
 */
const showPlatformPages = async () => {
    const { code, data, msg } = await doGetPlatformPages({
        type: activePagesType.value,
        templateType: 'SHOP',
        businessType: shopMode === 'COMMON' ? 'ONLINE' : 'O2O',
        endpointType: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '获取页面列表失败')
    platformPageList.value = data.records
    dialogPlatformPage.value = true
}

/**
 * 同步平台页面
 */
const handlePlatformPage = async (row: any) => {
    const { code, msg } = await doPostClonePlatformPage({
        id: row.id,
        pageType: activePagesType.value,
        business: 'ONLINE',
        endpoint: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '同步失败')
    ElMessage.success('同步成功')
    dialogPlatformPage.value = false
    setPage.value.initPages()
}
/**
 * 点击左侧切换按钮
 * @param {number} currentTab
 */
// const handleSelectTab = (index: number) => {
//     currentTab.value = index
//     // 切换预览和设置 至空
//     initPreviewAndSet()
// }
/**
 * 设置当前组件
 * @param {*} component
 */
const setCurrentComponent = (component: ComponentItem) => {
    currentComponent.value = component
}
/**
 * 新增组件
 * @param {ComponentItem} component
 * @param {number} index
 */
const handleAddComponent = (component: ComponentItem, index: number) => {
    if (activePageType.value === 'control' || activePageType.value === 'classification') {
        ElMessage.warning('该页面无法添加组件')
        return
    }
    if (component && index !== undefined) {
        // 拖拽添加
        components.value[index] = component
    } else if (ONLY_ONE_COMPONENTS.includes(component.value)) {
        // 轮播图组件点击添加
        components.value.unshift(component)
    } else {
        // 点击添加
        components.value.push(component)
    }
    currentComponent.value = component
    editorPreviewRef.value?.setCurrentFlag(index)
}
/**
 * 删除组件
 */
const handleDelComponent = (e: { index: number }) => {
    components.value.splice(e.index, 1)
}
/**
 * 控件变化
 * @param {*} controlComponent
 */
// const handleAddControl = (controlComponent: ComponentItem) => {
//     controllerComponentList.value = []
//     controllerComponentList.value.push(controlComponent)
//     currentComponent.value = controlComponent
// }
/**
 * 保存控件
 */
// const handleSaveControl = async () => {
//     const currentChooseCom = currentComponent.value
//     if (!currentChooseCom) return ElMessage.warning('请选择控件')
//     const submitForm: SubmitForm = {
//         id: currentChooseCom.id!,
//         functionType: 'TABBAR',
//         properties: [currentChooseCom],
//         platforms: $decorationStore.getterDecType,
//     }
//     const { data, code } = await doSubmitControl(submitForm)
//     if (code === 200) {
//         ElMessage.success('保存控件成功')
//     } else {
//         ElMessage.error('保存控件失败')
//     }
// }
/**
 * 页面切换
 */
// const handleChangePage = (e: { type: PageType; page: SubmitForm }) => {
//     const { page, type } = e
//     switch (type) {
//         case 'classification':
//             components.value = [
//                 {
//                     icon: '',
//                     value: 'classification',
//                     label: '商品分类',
//                     formData: page.properties,
//                 },
//             ]
//             break
//         case 'userCenter':
//             components.value = [
//                 {
//                     icon: '',
//                     value: 'userCenter',
//                     label: '用户中心',
//                     formData: page.properties,
//                 },
//             ]
//             break
//         default:
//             getCurrentPageComponent(page.id)
//     }
//     initPreviewAndSet()
// }
/**
 * 保存页面
 */
const handleSavePage = async () => {
    if (!components.value.length) {
        ElMessage.warning('请添加组件')
        return
    }
    if (!name.value) {
        ElMessage.warning('请填写页面名称')
        return
    }
    const submitForm = {
        id: id.value,
        name: name.value,
        remark: remark.value,
        properties: components.value,
        type: activePagesType.value,
        businessType: shopMode === 'COMMON' ? 'ONLINE' : 'O2O',
        endpointType: $decorationStore.getEndpointType,
    }
    const { code, data } = id.value ? await doPutShopPages(submitForm) : await doPostShopPagesSave(submitForm)
    if (code === 800018) {
        ElMessage.warning('页面名称重复，请修改')
    } else if (code === 200) {
        ElMessage.success('保存成功')
        iseditor.value = false
        initPreviewAndSet()
        setPage.value.initPages()
    } else {
        ElMessage.warning('保存失败,请重试')
    }
}

/**
 * 初始化组件配置页和预览页
 */
function initPreviewAndSet() {
    currentComponent.value = undefined
    editorPreviewRef.value?.setCurrentFlag(-1)
}

/**
 * 页面编辑回调
 */
function handleEditorPages(data: any) {
    iseditor.value = true
    $decorationStore.SET_ACTIVE_PAGE_TYPE('customize')
    if (data) {
        name.value = data.name
        remark.value = data.remark
        id.value = data.id
        components.value = JSON.parse(data.properties)
    } else {
        switch (activePagesType.value) {
            case 'SHOP_CATEGORY_PAGE':
                components.value = [
                    {
                        icon: '',
                        value: 'classification',
                        label: '商品分类',
                        formData: classification,
                    },
                ]
                break
            case 'SHOP_BOTTOM_NAVIGATION_PAGE':
                components.value = [
                    {
                        icon: '',
                        value: 'navBar',
                        label: '底部导航',
                        formData: NavBar,
                    },
                ]
                break
            default:
                components.value = []
        }
    }
}
/**
 * tab改变事件
 */
function handleTabchange(tabName: any) {
    if (iseditor.value) {
        initPreviewAndSet()
    }
    iseditor.value = false
    nextTick(() => {
        name.value = activePagesTypeList[activePagesType.value as keyof typeof activePagesTypeList]
    })
    remark.value = ''
    id.value = ''
    if (tabName === 'pages') setPage.value?.initPages()
}
/**
 * tab点击事件
 */
function handleTabClick(type: any) {
    if (iseditor.value) {
        initPreviewAndSet()
    }
    iseditor.value = false
    nextTick(() => {
        name.value = activePagesTypeList[activePagesType.value as keyof typeof activePagesTypeList]
    })
    remark.value = ''
    id.value = ''
    if (type.paneName === activePagesType.value) setPage.value.initPages()
}
</script>

<template>
    <div class="editorWrapper">
        <!-- 头部 -->
        <div class="head">
            <div class="head__top">
                <el-tabs v-model="activeType" class="head__top--tabs" type="card" @tab-change="handleTabchange">
                    <el-tab-pane label="店铺模板" name="template"></el-tab-pane>
                    <el-tab-pane label="设计页面" name="pages"></el-tab-pane>
                </el-tabs>
                <div class="head__top--terminal">商家端装修({{ $decorationStore.getEndpointType === 'H5_APP' ? 'H5、APP 端' : '微信小程序端' }})</div>
            </div>
            <div v-if="activeType === 'pages'" class="head__pagesType">
                <el-tabs v-model="activePagesType" class="demo-tabs" stretch @tab-click="handleTabClick">
                    <el-tab-pane label="店铺首页" name="SHOP_HOME_PAGE"></el-tab-pane>
                    <el-tab-pane label="店铺分类" name="SHOP_CATEGORY_PAGE"></el-tab-pane>
                    <el-tab-pane label="底部导航" name="SHOP_BOTTOM_NAVIGATION_PAGE"></el-tab-pane>
                    <el-tab-pane label="自定义页面" name="SHOP_CUSTOMIZED_PAGE"></el-tab-pane>
                </el-tabs>
            </div>
            <div v-if="activeType === 'pages' && iseditor" class="head__editor">
                <div class="head__editor_left">
                    <div>页面名称</div>
                    <el-input v-model="name" maxlength="10" style="width: 200px; margin-left: 10px" />

                    <div style="margin-left: 20px">页面说明</div>
                    <el-input v-model="remark" maxlength="20" style="width: 500px; margin-left: 10px" />
                </div>
                <el-button style="margin-right: 20px; width: 100px" type="primary" @click="handleSavePage"> 保存</el-button>
            </div>
        </div>
        <!-- 商城模板 -->
        <MallTemplate v-if="activeType === 'template'" />
        <!-- 页面列表 -->
        <SetPages
            v-show="activeType === 'pages' && !iseditor"
            ref="setPage"
            :type="activePagesType"
            @editorPages="handleEditorPages"
            @show-platform-pages="showPlatformPages"
        />
        <!-- 页面详情 -->
        <div v-if="activeType === 'pages' && iseditor" class="editorPage">
            <div v-if="activePagesType === 'SHOP_HOME_PAGE' || activePagesType === 'SHOP_CUSTOMIZED_PAGE'" class="editorPage_left">
                <!-- <editor-tab @change="handleSelectTab" /> -->
                <div class="editor_view">
                    <!-- 组件列表 -->
                    <EditorComponent :components="components" @change="handleAddComponent" />
                    <!-- 控件列表 -->
                    <!-- <EditorControl v-if="activePagesType === 'BOTTOM_NAVIGATION_PAGE'" @change="handleAddControl" /> -->
                    <!-- 页面列表 -->
                    <!-- <EditorPage v-show="currentTab === 2" @change="handleChangePage" /> -->
                </div>
            </div>
            <div class="editorPage_main">
                <div class="tab_con">
                    <!-- 控件预览 -->
                    <!-- <EditorPreview
                        v-if="activePagesType === 'BOTTOM_NAVIGATION_PAGE'"
                        :components="controllerComponentList"
                        @change="setCurrentComponent"
                    /> -->
                    <!-- 组件以及页面预览 -->
                    <EditorPreview
                        ref="editorPreviewRef"
                        :components="components"
                        @add="handleAddComponent"
                        @change="setCurrentComponent"
                        @del="handleDelComponent"
                    />
                </div>
            </div>
            <div v-if="currentComponent" class="editorPage_right">
                <div class="editorPage_right_wrap">
                    <!-- 样式编辑 -->
                    <EditorForm :current-component="currentComponent" />
                </div>
            </div>
        </div>
    </div>
    <!-- 弹窗s -->
    <el-dialog v-model="dialogPlatformPage" title="从平台同步模板">
        <p style="color: red">请注意：同步后的名称会和平台保持一致</p>
        <el-table :data="platformPageList" border>
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column align="center" label="模板名称" prop="name" />
            <el-table-column align="center" label="模板描述" prop="remark" />
            <el-table-column align="center" label="操作">
                <template #default="{ row }">
                    <el-button size="small" text type="primary" @click="handlePlatformPage(row)">同步</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-dialog>
    <!-- 弹窗e -->
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
@include b(head) {
    background: #fff;
    @include e(top) {
        display: flex;
        justify-content: space-between;
        border: 1px solid #bbbbbb;
        @include m(terminal) {
            width: 200px;
            height: 40px;
            border-right: 1px solid #bbbbbb;
            font-size: 14px;
            text-align: center;
            line-height: 40px;
            color: #000;
        }
        @include m(tabs) {
            height: 40px;
            font-size: 14px;
            :deep(.el-tabs__header) {
                margin-bottom: 0;
            }
        }
    }
    @include e(pagesType) {
        height: 50px;
    }
    @include e(editor) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 45px;
    }
    @include e(editor_left) {
        display: flex;
        align-items: center;
        margin-left: 60px;
    }
}
#editor__preview_position {
    position: relative;
    padding-bottom: 15px;
    overflow: auto;
    width: 375px;
    height: calc(100vh - 80px);
    border: 1px solid #ccc;
    background-color: #fff;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin-bottom: 100px;
    visibility: hidden;
}
#editor__from_position {
    width: 435px;
    height: 667px;
    overflow-y: scroll;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    padding-bottom: 50px;
}
/**新的编辑样式**/
.editorPage_right_wrap {
    width: 405px;
    display: flex;
    flex-direction: column;
    height: 100vh;
}
div::-webkit-scrollbar {
    width: 0 !important;
    height: 0 !important;
}

#savetip {
    font-size: 14px;
    color: #888;
    margin-left: 6px;
}
.no-l-b {
    border-right: none !important;
}
:deep(.el-tabs__header) {
    margin-bottom: 0;
}
</style>
