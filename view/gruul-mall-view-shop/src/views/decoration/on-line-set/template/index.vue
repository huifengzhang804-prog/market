<script lang="ts" setup>
import EditorPreview from '../components/editor-preview.vue'
import PageManage from '@/components/PageManage.vue'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { ElMessage, FormRules, ElMessageBox } from 'element-plus'
import {
    doGetShopTemplates,
    doGetShopTemplatesDetail,
    doPostShopTemplates,
    doDelShopTemplates,
    doPostShopTemplatesClone,
    doPutShopTemplates,
    doPutShopTemplatesState,
    doGetShopTemplatesPages,
    doGetShopPagesDetail,
    doGetPlatformTemplates,
    doPostClonePlatformTemplate,
} from '@/apis/decoration'
import { PAGES_TYPE } from '../types'
import type { ComponentItem } from '../packages/components/index/formModel'

const $decorationStore = useDecorationStore()
// 模板列表
const templateList = ref([])
const platformTemplateList = ref([])
// 当前预览模板
const currentTemplate = ref<any>({})
// 所有页面列表
const templatePagesList = ref<any>({})
// 模板分页信息
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
    businessType: 'ONLINE',
    endpointType: $decorationStore.getEndpointType,
})
// 当前预览页面组件列表
const components = ref<ComponentItem[]>([])
// 当前预览页面下标
const currentIndex = ref(0)
// 弹窗标识
const dialogTemplate = ref(false)
//平台模板弹窗标识
const dialogPlatformTemplate = ref(false)
// 弹窗ref
const RefTemplate = ref()
// 弹窗表单
const templateForm = ref({
    id: '',
    name: '',
    description: '',
    pages: [
        {
            pageId: '',
            pageType: 'SHOP_HOME_PAGE',
        },
        {
            pageId: '',
            pageType: 'SHOP_CATEGORY_PAGE',
        },
        {
            pageId: '',
            pageType: 'SHOP_BOTTOM_NAVIGATION_PAGE',
        },
    ],
    businessType: 'ONLINE',
    endpointType: $decorationStore.getEndpointType,
})
// 表单验证
const rules: FormRules = reactive({
    name: { required: true, message: '请填写模板名称', trigger: 'blur' },
    description: { required: true, message: '请填写模板说明', trigger: 'blur' },
    'pages[0].pageId': { required: true, message: '请选择店铺首页', trigger: 'change' },
    'pages[1].pageId': { required: true, message: '请选择店铺分类', trigger: 'change' },
    'pages[2].pageId': { required: true, message: '请选择底部导航', trigger: 'change' },
})

initTemplate()
initTemplatePages()

/**
 * 初始化模板列表
 */
async function initTemplate() {
    const { code, data, msg } = await doGetShopTemplates(pageConfig)
    if (code !== 200) return ElMessage.error(msg || '获取模板列表失败')
    templateList.value = data.records
    pageConfig.total = +data.total
    changeCurrentTemplate(data.records[0])
}
/**
 * 初始化页面列表
 */
async function initTemplatePages() {
    const { code, data, msg } = await doGetShopTemplatesPages({
        businessType: 'ONLINE',
        endpointType: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '获取模板列表失败')
    templatePagesList.value = data
}
/**
 * 切换启用
 */
const handleChangeSwitch = async (id: string) => {
    const { code, data, msg } = await doPutShopTemplatesState({ templateId: id })
    initTemplate()
    if (code === 800015) return ElMessage.error('启用模板数不能为0')
    if (code !== 200) return ElMessage.error(msg || '修改启用失败')
    ElMessage.success('修改启用成功')
}
/**
 * 复制模板
 */
const handleClone = async (id: string) => {
    const { code, data, msg } = await doPostShopTemplatesClone(id)
    if (code === 800027) return ElMessage.error('模板名称过长，复制失败')
    if (code !== 200) return ElMessage.error(msg || '复制模板失败')
    ElMessage.success('复制成功')
    initTemplate()
}
/**
 * 编辑模板
 */
const handleOpenDialog = async (id: string) => {
    const { code, data, msg } = await doGetShopTemplatesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取模板详情失败')
    // 处理data.page的信息保持一致
    const pagesMap = new Map(data.pages.map((item: { pageType: string; pageId: string }) => [item.pageType, item.pageId]))
    data.pages = templateForm.value.pages.map((item) => ({ ...item, pageId: pagesMap.get(item.pageType) }))
    templateForm.value = data
    dialogTemplate.value = true
}
/**
 * 删除模板
 */
const handleDel = async (id: string) => {
    ElMessageBox.confirm('确定删除该模板吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, data, msg } = await doDelShopTemplates(id)
        if (code !== 200) return ElMessage.error(msg || '删除模板失败')
        ElMessage.success('删除成功')
        initTemplate()
    })
}
const handleChangeCurrent = (e: number) => {
    pageConfig.current = e
    initTemplate()
}
const handleChangeSize = (e: number) => {
    pageConfig.current = 1
    pageConfig.size = e
    initTemplate()
}
/**
 * 提交模板
 */
const handleSubmit = async () => {
    if (!RefTemplate.value) return
    await RefTemplate.value.validate(async (valid: any) => {
        if (valid) {
            if (templateForm.value.id) {
                const { code, data, msg } = await doPutShopTemplates(templateForm.value)
                if (code === 800009) return ElMessage.error('模板名称重复请修改')
                if (code !== 200) return ElMessage.error(msg || '修改模板失败')
                ElMessage.success('修改模板成功')
                initTemplate()
            } else {
                const { code, data, msg } = await doPostShopTemplates(templateForm.value)
                if (code === 800009) return ElMessage.error('模板名称重复请修改')
                if (code !== 200) return ElMessage.error(msg || '新增模板失败')
                ElMessage.success('新增模板成功')
                initTemplate()
            }
            dialogTemplate.value = false
        }
    })
}
/**
 * 获取平台模板列表
 */
const showPlatformTemplates = async () => {
    const { code, data, msg } = await doGetPlatformTemplates({
        templateType: 'SHOP',
        businessType: 'ONLINE',
        endpointType: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '获取平台模板失败')
    platformTemplateList.value = data.records
    dialogPlatformTemplate.value = true
}
/**
 * 同步平台模板
 */
const handlePlatformTemplate = async (row: any) => {
    const { code, msg } = await doPostClonePlatformTemplate({
        id: row.id,
        business: 'ONLINE',
        endpoint: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '同步模板失败')
    ElMessage.success('同步成功')
    dialogPlatformTemplate.value = false
    initTemplate()
    initTemplatePages()
}
/**
 * 关闭弹窗
 */
const closeDialog = () => {
    templateForm.value = {
        id: '',
        name: '',
        description: '',
        pages: [
            {
                pageId: '',
                pageType: 'SHOP_HOME_PAGE',
            },
            {
                pageId: '',
                pageType: 'SHOP_CATEGORY_PAGE',
            },
            {
                pageId: '',
                pageType: 'SHOP_BOTTOM_NAVIGATION_PAGE',
            },
        ],
        businessType: 'ONLINE',
        endpointType: $decorationStore.getEndpointType,
    }
}
/**
 * 切换选择预览模板
 */
const changeCurrentTemplate = async (row: any) => {
    if (!row) return
    const data = toRaw(row)
    data.pages = data.pages.filter((item: any) => {
        return item.pageId
    })
    currentTemplate.value = data
    GetPagesDetail(data.pages[0].pageId)
}
/**
 * 获取页面详情
 */
const GetPagesDetail = async (id: string) => {
    const { code, data, msg } = await doGetShopPagesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取页面详情失败')
    components.value = JSON.parse(data.properties)
}
/**
 * 预览走马灯切换
 */
const changeCarousel = async (index: any) => {
    currentIndex.value = index
    GetPagesDetail(currentTemplate.value.pages[index].pageId)
}
const addNew = () => {
    dialogTemplate.value = true
}
</script>

<template>
    <div class="template">
        <div class="template--left">
            <el-carousel :autoplay="false" height="900px" indicator-position="none" style="width: 375px" trigger="click" @change="changeCarousel">
                <el-carousel-item v-for="(item, index) in currentTemplate.pages" :key="item.pageId">
                    <div style="text-align: center; margin-bottom: 20px">{{ PAGES_TYPE[item.pageType] }}</div>
                    <editor-preview v-if="index === currentIndex" :components="components" :is-preview="true" />
                </el-carousel-item>
            </el-carousel>
        </div>
        <div class="template--right">
            <el-button style="width: 100px; margin-bottom: 10px" type="primary" @click="addNew">新增</el-button>
            <el-button style="width: 100px; margin-bottom: 10px" type="primary" @click="showPlatformTemplates">从平台同步</el-button>
            <el-table :data="templateList" border max-height="570" style="width: 100%">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column label="模板名称" prop="name" width="220">
                    <template #default="{ row }">
                        <div :style="row.id === currentTemplate.id ? 'color:#0F40F5' : ''" @click="changeCurrentTemplate(row)">
                            {{ row.name }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="模板说明" prop="description" width="400">
                    <template #default="{ row }">
                        <div :style="row.id === currentTemplate.id ? 'color:#0F40F5' : ''" @click="changeCurrentTemplate(row)">
                            {{ row.description }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="启用禁用" prop="enabled" width="100">
                    <template #default="{ row }">
                        <el-switch v-model="row.enabled" :disabled="row.enabled" @change="handleChangeSwitch(row.id)" />
                    </template>
                </el-table-column>
                <el-table-column align="center" label="操作" prop="address" width="180">
                    <template #default="{ row }">
                        <span style="color: #0f40f5; cursor: pointer; margin-right: 20px" @click="handleOpenDialog(row.id)">编辑</span>
                        <span style="color: #0f40f5; cursor: pointer" @click="handleClone(row.id)">复制</span>
                        <span v-if="templateList.length > 1" style="color: #fd0505; cursor: pointer; margin-left: 20px" @click="handleDel(row.id)"
                            >删除</span
                        >
                    </template>
                </el-table-column>
            </el-table>
            <page-manage
                :page-num="pageConfig.current"
                :page-size="pageConfig.size"
                :total="pageConfig.total"
                @handle-current-change="handleChangeCurrent"
                @handle-size-change="handleChangeSize"
            />
        </div>
    </div>
    <!-- 弹窗s -->
    <el-dialog v-model="dialogTemplate" :title="templateForm.id ? '编辑模板' : '新建模板'" destroy-on-close width="45%" @close="closeDialog">
        <el-form ref="RefTemplate" :model="templateForm" :rules="rules">
            <el-form-item label="模板名称" label-width="120px" prop="name" required>
                <el-input v-model="templateForm.name" maxlength="10" style="width: 90%" />
            </el-form-item>
            <el-form-item label="模板说明" label-width="120px" prop="description" required>
                <el-input v-model="templateForm.description" maxlength="20" style="width: 90%" />
            </el-form-item>
            <el-form-item label="店铺首页" label-width="120px" prop="pages[0].pageId" required>
                <el-select v-model="templateForm.pages[0].pageId" filterable placeholder="请选择商城首页" style="width: 90%">
                    <el-option v-for="item in templatePagesList.shopHomePages" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="店铺分类" label-width="120px" prop="pages[1].pageId" required>
                <el-select v-model="templateForm.pages[1].pageId" filterable placeholder="请选择商品分类" style="width: 90%">
                    <el-option v-for="item in templatePagesList.shopCategoryPages" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="底部导航" label-width="120px" prop="pages[2].pageId" required>
                <el-select v-model="templateForm.pages[2].pageId" filterable placeholder="请选择底部导航" style="width: 90%">
                    <el-option v-for="item in templatePagesList.shopBottomNavigationPages" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogTemplate = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确认</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="dialogPlatformTemplate" title="从平台同步模板">
        <p style="color: red">请注意：同步后的名称会和平台保持一致</p>
        <el-table :data="platformTemplateList" border>
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column align="center" label="模板名称" prop="name" />
            <el-table-column align="center" label="模板描述" prop="description" />
            <el-table-column align="center" label="操作">
                <template #default="{ row }">
                    <el-button size="small" text type="primary" @click="handlePlatformTemplate(row)">同步</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-dialog>
    <!-- 弹窗e -->
</template>

<style lang="scss" scoped>
@include b(template) {
    display: flex;
    justify-content: space-around;
    padding: 50px 20px 50px 45px;
}
</style>
