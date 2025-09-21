<script setup lang="ts">
import EditorComponent from './components/editor-component.vue'
import EditorPreview from './components/editor-preview.vue'
import EditorForm from './components/editor-form.vue'
import MallTemplate from './template/index.vue'
import SetPages from './pages/index.vue'
import userCenterDefaultData from './packages/components/userCenter/user-center'
import NavBar from './packages/components/navBar/nav-bar'
import classification from './packages/components/classification/classification'
import { ElMessage, TabPaneName } from 'element-plus'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { doPostPlatformPagesSave, doPutPlatformPages } from '@/apis/decoration'
import type { ComponentItem } from './packages/components/index/formModel'
import ChromeTab from '@/components/chrome-tab/index.vue'
import OpenAdvertisement from './openAdvertisement.vue'
import HomeBulletFrame from './homeBulletFrame.vue'
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
const activePageType = computed(() => {
    return $decorationStore.activePageType
})

//当前编辑类型  template模板 pages页面
const activeType = ref('template')
//当前编辑页面类型
const activePagesType = ref('RECOMMENDED_MALL_HOME_PAGE')

const activePagesTypeList = {
    RECOMMENDED_MALL_HOME_PAGE: '推荐',
    SAME_CITY_MALL_HOME_PAGE: '同城',
    PRODUCT_CATEGORY_PAGE: '商品分类',
    BOTTOM_NAVIGATION_PAGE: '底部导航',
    PERSONAL_CENTER_PAGE: '个人中心',
    CUSTOMIZED_PAGE: '自定义页面',
}
//当前编辑页面名称
const name = ref('推荐')
//当前编辑页面说明
const remark = ref('')
//当前编辑页面id
const id = ref('')
//是否在编辑/新增页面
const iseditor = ref(false)

// 头部tabs切换列表
const tabList = [
    {
        label: '商城模板',
        name: 'template',
    },
    {
        label: '设计页面',
        name: 'pages',
    },
    {
        label: '开屏广告',
        name: 'openAdvertisement',
    },

    {
        label: '首页弹窗',
        name: 'homeBulletFrame',
    },
]

const setPage = ref<InstanceType<typeof SetPages>>()

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
    // if (component.value === 'compose') {
    //     const composeComp = components.value.find((item) => item.value === 'compose' && item.formData)
    //     if (composeComp) {
    //         ElMessage.warning('每个页面只允许添加1个组合组件!!!')
    //         return
    //     }
    // }
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
    editorPreviewRef.value?.setCurrentFlag?.(index)
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

    if (
        components.value.length === 1 &&
        components.value[0].value === 'classification' &&
        components.value[0].formData &&
        'categoryList' in components.value[0].formData &&
        !components.value[0].formData.categoryList[0].platformCategoryFirstId
    ) {
        ElMessage.warning('请选择分类')
        return
    }

    // // 校验所有店铺商品组件
    for (const component of components.value) {
        if (component.value === 'shopGoods' && components.value[editorPreviewRef.value?.currentFlag]?.value === 'shopGoods') {
            const shopGoodsFormData = component.formData as { validateForm?: () => Promise<boolean> }
            if (shopGoodsFormData?.validateForm) {
                const valid = await shopGoodsFormData.validateForm()
                if (!valid) {
                    return
                }
            }
        }
    }

    const submitForm = {
        id: id.value,
        name: name.value,
        remark: remark.value,
        properties: components.value,
        type: activePagesType.value,
        templateType: 'PLATFORM',
        businessType: '',
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
        setPage.value?.initPages()
    } else {
        ElMessage.warning('保存失败,请重试')
    }
}

/**
 * 初始化组件配置页和预览页
 */
function initPreviewAndSet() {
    currentComponent.value = void 0
    editorPreviewRef.value?.setCurrentFlag?.(-1)
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
            case 'PRODUCT_CATEGORY_PAGE':
                components.value = [
                    {
                        icon: '',
                        value: 'classification',
                        label: '商品分类',
                        formData: getFreshClassification(),
                    },
                ]
                break
            case 'PERSONAL_CENTER_PAGE':
                components.value = [
                    {
                        icon: '',
                        value: 'userCenter',
                        label: '用户中心',
                        formData: userCenterDefaultData,
                    },
                ]
                break
            case 'BOTTOM_NAVIGATION_PAGE':
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

function getFreshClassification() {
    return {
        style: 1,
        listStyle: 1,
        buyBtnStyle: 1,
        goodsMargin: 0,
        fontSelected: '#FF0000',
        bgSelected: '#0E0202',
        fontNotSelected: '#000000',
        bgNotSelected: '#F5F5F5',
        goodsShow: [],
        categoryList: [
            {
                platformCategoryFirstId: '',
                platformCategoryFirstName: '',
                platformCategorySecondChildren: [],
                ads: [],
                children: [
                    {
                        platformCategorySecondId: '',
                        platformCategorySecondName: '',
                        platformCategoryThirdChildren: [],
                        children: [
                            {
                                platformCategoryThirdId: '',
                                platformCategoryThirdName: '',
                            },
                        ],
                    },
                ],
            },
        ],
        categoryShow: true,
        navShow: true,
        searchShow: 4,
        commodityShow: 2,
        classificationTitle: '',
    }
}

/**
 * tab改变事件
 */
function handleTabchange(tabName: TabPaneName) {
    if (tabName) activeType.value = tabName as string
    if (iseditor.value) {
        initPreviewAndSet()
    }
    iseditor.value = false

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
    remark.value = ''
    id.value = ''
    nextTick(() => {
        name.value = activePagesTypeList[activePagesType.value as keyof typeof activePagesTypeList]
    })
    if (type.paneName === activePagesType.value) setPage.value?.initPages()
}
/**
 * 定义能添加 左侧 组件的类型
 */
const showLeft = computed(() => {
    return ['RECOMMENDED_MALL_HOME_PAGE', 'SAME_CITY_MALL_HOME_PAGE', 'CUSTOMIZED_PAGE'].includes(activePagesType.value)
})
</script>

<template>
    <div class="editorWrapper">
        <!-- 头部 -->
        <div class="head">
            <div class="head__top">
                <div style="flex: 1">
                    <ChromeTab :tab-list="tabList" :value="activeType" width="130px" @handle-tabs="handleTabchange" />
                </div>
                <div class="head__top--terminal">平台端装修({{ $decorationStore.getEndpointType === 'H5_APP' ? 'H5、APP 端' : '微信小程序端' }})</div>
            </div>
            <div v-if="activeType === 'pages'" class="head__pagesType">
                <el-tabs v-model="activePagesType" stretch class="demo-tabs" @tab-click="handleTabClick">
                    <el-tab-pane label="推荐" name="RECOMMENDED_MALL_HOME_PAGE"></el-tab-pane>
                    <el-tab-pane label="同城" name="SAME_CITY_MALL_HOME_PAGE"></el-tab-pane>
                    <el-tab-pane label="商品分类" name="PRODUCT_CATEGORY_PAGE"></el-tab-pane>
                    <el-tab-pane label="底部导航" name="BOTTOM_NAVIGATION_PAGE"></el-tab-pane>
                    <el-tab-pane label="个人中心" name="PERSONAL_CENTER_PAGE"></el-tab-pane>
                    <el-tab-pane label="自定义页面" name="CUSTOMIZED_PAGE"></el-tab-pane>
                </el-tabs>
            </div>
            <div v-if="activeType === 'pages' && iseditor" class="head__editor">
                <div class="head__editor_left">
                    <div>页面名称</div>
                    <el-input v-model="name" style="width: 200px; margin-left: 10px" maxlength="4" />

                    <div style="margin-left: 20px">页面说明</div>
                    <el-input v-model="remark" style="width: 500px; margin-left: 10px" maxlength="20" />
                </div>
                <el-button type="primary" style="margin-right: 20px; width: 100px" @click="handleSavePage"> 保存</el-button>
            </div>
        </div>
        <!-- 商城模板 -->
        <MallTemplate v-if="activeType === 'template'" />
        <!-- 页面列表 -->
        <SetPages v-show="activeType === 'pages' && !iseditor" ref="setPage" :type="activePagesType" @editor-pages="handleEditorPages" />
        <!-- 页面详情 -->
        <div v-if="activeType === 'pages' && iseditor" class="editorPage">
            <div v-if="showLeft" class="editorPage_left">
                <!-- <editor-tab @change="handleSelectTab" /> -->
                <div class="editor_view">
                    <!-- 组件列表 -->
                    <EditorComponent :activePagesType="activePagesType" :components="components" @change="handleAddComponent" />
                    <!-- 页面列表 -->
                    <!-- <editor-page v-show="currentTab === 2" @change="handleChangePage" /> -->
                </div>
            </div>
            <div class="editorPage_main">
                <div class="tab_con">
                    <!-- 控件预览 -->
                    <!-- <editor-preview
                        v-if="activePagesType === 'BOTTOM_NAVIGATION_PAGE'"
                        :components="controllerComponentList"
                        @change="setCurrentComponent"
                    /> -->
                    <!-- 组件以及页面预览 -->
                    <EditorPreview
                        ref="editorPreviewRef"
                        :components="components"
                        :canAddComponent="showLeft"
                        :activePagesType="activePagesType"
                        @change="setCurrentComponent"
                        @add="handleAddComponent"
                        @del="handleDelComponent"
                    />
                </div>
            </div>
            <div v-if="currentComponent" class="editorPage_right">
                <div class="editorPage_right_wrap">
                    <!-- 样式编辑 -->
                    <EditorForm :currentComponent="currentComponent" :activePagesType="activePagesType" />
                </div>
            </div>
        </div>
        <OpenAdvertisement v-if="activeType === 'openAdvertisement'" />
        <HomeBulletFrame v-if="activeType === 'homeBulletFrame'" />
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
@include b(head) {
    background: #f5f7fa;
    @include e(top) {
        display: flex;
        justify-content: space-between;
        @include m(terminal) {
            height: 45px;
            font-size: 14px;
            text-align: center;
            line-height: 45px;
            color: #000;
            padding-right: 20px;
            width: 190px;
        }
        @include m(tabs) {
            height: 40px;
            font-size: 14px;
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
    width: 600px;
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
:deep(.el-tabs__header) {
    margin-bottom: 0;
}
</style>
