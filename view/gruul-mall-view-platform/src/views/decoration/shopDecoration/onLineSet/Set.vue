<script setup lang="ts">
import EditorComponent from './components/editor-component.vue'
import EditorPreview from './components/editor-preview.vue'
import EditorForm from './components/editor-form.vue'
import MallTemplate from './template/index.vue'
import SetPages from './pages/index.vue'
import NavBar from './packages/components/navBar/nav-bar'
import Classification from './packages/components/classification/classification'
import { ElMessage } from 'element-plus'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { doPostPlatformPagesSave, doPutPlatformPages } from '@/apis/decoration'
import type { ComponentItem } from './packages/components/index/formModel'
import { ONLY_ONE_COMPONENTS } from '@/composables/useSwiper'

const $decorationStore = useDecorationStore()
// 当前编辑组件
const currentComponent = ref<ComponentItem>()
// 组件列表
const components = ref<ComponentItem[]>([])
// 控件列表
// const controllerComponentList = ref<ComponentItem[]>([])
const editorPreviewRef = ref<typeof EditorPreview>()
// 获取当前页面操作栏
const activePageType = computed(() => {
    return $decorationStore.activePageType
})

//当前编辑类型  template模板 pages页面
const activeType = ref('template')
//当前编辑页面类型
const activePagesType = ref('SHOP_HOME_PAGE')
//当前编辑页面名称
const name = ref('')
//当前编辑页面说明
const remark = ref('')
//当前编辑页面id
const id = ref('')
//是否在编辑/新增页面
const iseditor = ref(false)

const setPage = ref()

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
    if (!remark.value) {
        ElMessage.warning('请填写页面说明')
        return
    }
    const submitForm = {
        id: id.value,
        name: name.value,
        remark: remark.value,
        properties: components.value,
        type: activePagesType.value,
        businessType: 'ONLINE',
        templateType: 'SHOP',
        endpointType: $decorationStore.getEndpointType,
    }
    const { code, data } = id.value ? await doPutPlatformPages(submitForm) : await doPostPlatformPagesSave(submitForm)
    // if (data) {
    //     id.value = data
    // }
    if (code === 100011) {
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
                        formData: Classification,
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
    name.value = ''
    remark.value = ''
    id.value = ''
    if (tabName === 'pages') setPage.value.initPages()
}
/**
 * tab点击事件
 */
function handleTabClick(type: any) {
    if (iseditor.value) {
        initPreviewAndSet()
    }
    iseditor.value = false
    name.value = ''
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
                <div class="head__top--terminal">
                    店铺模板装修({{ $decorationStore.getEndpointType === 'H5_APP' ? 'H5、APP 端' : '微信小程序端' }})
                </div>
            </div>
            <div v-if="activeType === 'pages'" class="head__pagesType">
                <el-tabs v-model="activePagesType" stretch class="demo-tabs" @tab-click="handleTabClick">
                    <el-tab-pane label="店铺首页" name="SHOP_HOME_PAGE"></el-tab-pane>
                    <el-tab-pane label="店铺分类" name="SHOP_CATEGORY_PAGE"></el-tab-pane>
                    <el-tab-pane label="底部导航" name="SHOP_BOTTOM_NAVIGATION_PAGE"></el-tab-pane>
                    <el-tab-pane label="自定义页面" name="SHOP_CUSTOMIZED_PAGE"></el-tab-pane>
                </el-tabs>
            </div>
            <div v-if="activeType === 'pages' && iseditor" class="head__editor">
                <div class="head__editor_left">
                    <div>页面名称</div>
                    <el-input v-model="name" style="width: 200px; margin-left: 10px" maxlength="10" />

                    <div style="margin-left: 20px">页面说明</div>
                    <el-input v-model="remark" style="width: 500px; margin-left: 10px" maxlength="20" />
                </div>
                <el-button type="primary" style="margin-right: 20px; width: 100px" @click="handleSavePage"> 保存</el-button>
            </div>
        </div>
        <!-- 商城模板 -->
        <mall-template v-if="activeType === 'template'" />
        <!-- 页面列表 -->
        <set-pages v-show="activeType === 'pages' && !iseditor" ref="setPage" :type="activePagesType" @editor-pages="handleEditorPages" />
        <!-- 页面详情 -->
        <div v-if="activeType === 'pages' && iseditor" class="editorPage">
            <div v-if="activePagesType === 'SHOP_HOME_PAGE' || activePagesType === 'SHOP_CUSTOMIZED_PAGE'" class="editorPage_left">
                <div class="editor_view">
                    <!-- 组件列表 -->
                    <editor-component :components="components" @change="handleAddComponent" />
                </div>
            </div>
            <div class="editorPage_main">
                <div class="tab_con">
                    <!-- 组件以及页面预览 -->
                    <editor-preview
                        ref="editorPreviewRef"
                        :components="components"
                        @change="setCurrentComponent"
                        @add="handleAddComponent"
                        @del="handleDelComponent"
                    />
                </div>
            </div>
            <div v-if="currentComponent" class="editorPage_right">
                <div class="editorPage_right_wrap">
                    <!-- 样式编辑 -->
                    <editor-form :current-component="currentComponent" />
                </div>
            </div>
        </div>
    </div>
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
    height: 100%;
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
</style>
