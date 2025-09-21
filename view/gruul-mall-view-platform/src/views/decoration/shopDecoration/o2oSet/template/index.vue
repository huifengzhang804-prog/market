<script setup lang="ts">
import EditorPreview from '../components/editor-preview.vue'
import PageManage from '@/components/PageManage.vue'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { ElMessage, FormRules, ElMessageBox } from 'element-plus'
import {
    doGetPlatformTemplates,
    doGetPlatformTemplatesDetail,
    doPostPlatformTemplates,
    doDelPlatformTemplates,
    doPostPlatformTemplatesClone,
    doPutPlatformTemplates,
    doPutPlatformTemplatesState,
    doGetPlatformTemplatesPages,
    doGetPlatformPagesDetail,
} from '@/apis/decoration'
import type { ComponentItem } from '../packages/components/index/formModel'
import type { PlatformTemplatesDetail, Pages } from '../../types/index'
import { TemplateType } from '@/apis/decoration/type'

const $decorationStore = useDecorationStore()
// 模板列表
const templateList = ref<TemplateType[]>([])
// 当前预览模板
const currentTemplate = ref<PlatformTemplatesDetail>()
// 所有页面列表
const templatePagesList = ref<{ shopHomePages: { id: string; name: string }[] }>()
// 模板分页信息
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
    businessType: 'O2O',
    templateType: 'SHOP',
    endpointType: $decorationStore.getEndpointType,
})
// 当前预览页面组件列表
const components = ref<ComponentItem[]>([])
// 当前预览页面下标
// const currentIndex = ref(0)
// 弹窗标识
const dialogTemplate = ref(false)
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
    ],
    businessType: 'O2O',
    templateType: 'SHOP',
    endpointType: $decorationStore.getEndpointType,
})
// 表单验证
const rules: FormRules = reactive({
    name: { required: true, message: '请填写模板名称', trigger: 'blur' },
    description: { required: true, message: '请填写模板说明', trigger: 'blur' },
    'pages[0].pageId': { required: true, message: '请选择商城首页', trigger: 'change' },
})

initTemplate()
initTemplatePages()

/**
 * 初始化模板列表
 */
async function initTemplate() {
    const { code, data, msg } = await doGetPlatformTemplates(pageConfig)
    if (code !== 200) return ElMessage.error(msg || '获取模板列表失败')
    templateList.value = data.records
    pageConfig.total = +data.total
    changeCurrentTemplate(data.records[0].id)
}
/**
 * 初始化页面列表
 */
async function initTemplatePages() {
    const { code, data, msg } = await doGetPlatformTemplatesPages({
        businessType: 'O2O',
        templateType: 'SHOP',
        endpointType: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '获取模板列表失败')
    templatePagesList.value = data
}

/**
 * 切换启用
 */
// const handleChangeSwitch = async (id: string) => {
//     const { code, data, msg } = await doPutPlatformTemplatesState({ templateId: id })
//     initTemplate()
//     if (code === 100007) return ElMessage.error('启用模板数不能为0')
//     if (code !== 200) return ElMessage.error(msg || '修改启用失败')
//     ElMessage.success('修改启用成功')
// }
/**
 * 复制模板
 */
const handleClone = async (id: string) => {
    const { code, data, msg } = await doPostPlatformTemplatesClone(id)
    if (code === 100019) return ElMessage.error('模板名称过长，复制失败')
    if (code !== 200) return ElMessage.error(msg || '复制模板失败')
    ElMessage.success('复制成功')
    initTemplate()
}
/**
 * 编辑模板
 */
const handleOpenDialog = async (id: string) => {
    const { code, data, msg } = await doGetPlatformTemplatesDetail(id)
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
        const { code, data, msg } = await doDelPlatformTemplates(id)
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
    await RefTemplate.value.validate(async (valid: boolean) => {
        if (valid) {
            if (templateForm.value.id) {
                const { code, data, msg } = await doPutPlatformTemplates(templateForm.value)
                if (code === 100002) return ElMessage.error('模板名称重复请修改')
                if (code !== 200) return ElMessage.error(msg || '修改模板失败')
                ElMessage.success('修改模板成功')
                initTemplate()
            } else {
                const { code, data, msg } = await doPostPlatformTemplates(templateForm.value)
                if (code === 100002) return ElMessage.error('模板名称重复请修改')
                if (code !== 200) return ElMessage.error(msg || '新增模板失败')
                ElMessage.success('新增模板成功')
                initTemplate()
            }
            dialogTemplate.value = false
        }
    })
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
        ],
        businessType: 'O2O',
        endpointType: $decorationStore.getEndpointType,
        templateType: 'SHOP',
    }
}
/**
 * 切换选择预览模板
 */
const changeCurrentTemplate = async (id: string) => {
    const { code, data, msg } = await doGetPlatformTemplatesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '切换预览失败')
    data.pages = data.pages.filter((item: Pages) => {
        return item.pageId
    })
    currentTemplate.value = data
    GetPagesDetail(data.pages[0].pageId)
}
/**
 * 获取页面详情
 */
const GetPagesDetail = async (id: string) => {
    const { code, data, msg } = await doGetPlatformPagesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取页面详情失败')
    components.value = JSON.parse(data.properties)
}
</script>

<template>
    <div class="template">
        <div class="template--left">
            <editor-preview :components="components" :is-preview="true" />
        </div>
        <div class="template--right">
            <el-button type="primary" style="width: 100px; margin-bottom: 10px" @click="dialogTemplate = true">新增</el-button>
            <el-table :data="templateList" border style="width: 100%" max-height="570">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column prop="name" label="模板名称" width="220">
                    <template #default="{ row }">
                        <div :style="row.id === currentTemplate?.id ? 'color:#0F40F5' : ''" @click="changeCurrentTemplate(row.id)">
                            {{ row.name }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="模板说明" width="400">
                    <template #default="{ row }">
                        <div :style="row.id === currentTemplate?.id ? 'color:#0F40F5' : ''" @click="changeCurrentTemplate(row.id)">
                            {{ row.description }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="address" label="操作" width="180" align="center">
                    <template #default="{ row }">
                        <span style="color: #0f40f5; cursor: pointer" @click="handleOpenDialog(row.id)">编辑</span>
                        <span style="color: #0f40f5; cursor: pointer; margin-left: 20px" @click="handleClone(row.id)">复制</span>
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
    <el-dialog
        v-model="dialogTemplate"
        append-to-body
        :title="templateForm.id ? '编辑模板' : '新建模板'"
        width="45%"
        destroy-on-close
        @close="closeDialog"
    >
        <el-form ref="RefTemplate" :model="templateForm" :rules="rules">
            <el-form-item prop="name" label="模板名称" label-width="120px" required>
                <el-input v-model="templateForm.name" style="width: 90%" maxlength="10" />
            </el-form-item>
            <el-form-item prop="description" label="模板说明" label-width="120px" required>
                <el-input v-model="templateForm.description" style="width: 90%" maxlength="20" />
            </el-form-item>
            <el-form-item prop="pages[0].pageId" label="店铺首页" label-width="120px" required>
                <el-select v-model="templateForm.pages[0].pageId" filterable placeholder="请选择商城首页" style="width: 90%">
                    <el-option v-for="item in templatePagesList?.shopHomePages" :key="item.id" :label="item.name" :value="item.id" />
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
    <!-- 弹窗e -->
</template>

<style lang="scss" scoped>
@include b(template) {
    display: flex;
    justify-content: space-around;
    padding: 50px 20px 50px 45px;
}
</style>
