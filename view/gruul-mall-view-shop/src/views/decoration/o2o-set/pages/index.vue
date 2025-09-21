<script lang="ts" setup>
import EditorPreview from '../components/editor-preview.vue'
import PageManage from '@/components/PageManage.vue'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { ElMessage, FormRules, ElMessageBox } from 'element-plus'
import {
    doGetShopPages,
    doGetShopPagesDetail,
    doPostShopPagesClone,
    doDelShopPages,
    doGetPlatformPages,
    doPostClonePlatformPage,
} from '@/apis/decoration'
import type { ComponentItem } from '../packages/components/index/formModel'

const $decorationStore = useDecorationStore()
const $props = defineProps({
    type: {
        type: String,
        default: 'RECOMMENDED_MALL_HOME_PAGE',
    },
    isFirst: {
        type: Boolean,
        default: true,
    },
})
// 页面列表
const pagesList = ref([])
// 页面分页信息
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
    type: 'SHOP_HOME_PAGE',
    businessType: 'O2O',
    endpointType: $decorationStore.getEndpointType,
})
// 当前预览页面组件列表
const components = ref<ComponentItem[]>([])
// 当前预览页面id
const currentPagesId = ref()
const emits = defineEmits(['editorPages', 'showPlatformPages'])
//平台装修页面 dialog
const dialogPlatformPage = ref(false)
//平台装修页面列表
const platformPageList = ref([])

watch(
    $props,
    (newVal) => {
        if (newVal.isFirst) {
            return
        } else {
            pageConfig.type = newVal.type
            initPages()
        }
    },
    { immediate: true },
)

/**
 * 显示平台装修页面
 */
const showPlatformPages = async () => {
    const { code, data, msg } = await doGetPlatformPages({
        type: 'SHOP_HOME_PAGE',
        templateType: 'SHOP',
        businessType: 'O2O',
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
        pageType: 'SHOP_HOME_PAGE',
        business: 'O2O',
        endpoint: $decorationStore.getEndpointType,
    })
    if (code !== 200) return ElMessage.error(msg || '同步失败')
    ElMessage.success('同步成功')
    dialogPlatformPage.value = false
    initPages()
}
/**
 * 初始化页面列表
 */
async function initPages() {
    const { code, data, msg } = await doGetShopPages(pageConfig)
    if (code !== 200) return ElMessage.error(msg || '获取页面列表失败')
    pageConfig.total = data.total
    if (data.records.length) {
        pagesList.value = data.records
        changeCurrentPages(data.records[0].id)
    } else {
        pagesList.value = []
        components.value = []
    }
}

/**
 * 复制页面
 */
const handleClone = async (id: string) => {
    const { code, data, msg } = await doPostShopPagesClone(id)
    if (code === 800028) return ElMessage.error('页面名称过长，复制失败')
    if (code !== 200) return ElMessage.error(msg || '复制页面失败')
    ElMessage.success('复制成功')
    initPages()
}
/**
 * 编辑页面
 */
const handleOpenPages = async (id: string) => {
    const { code, data, msg } = await doGetShopPagesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取页面详情失败')
    emits('editorPages', data)
}
/**
 * 删除页面
 */
const handleDel = async (id: string) => {
    ElMessageBox.confirm('确定删除该页面吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, data, msg } = await doDelShopPages(id)
        if (code === 800014) return ElMessage.error('此页面被模板引用无法删除')
        if (code !== 200) return ElMessage.error(msg || '删除页面失败')
        ElMessage.success('删除成功')
        initPages()
    })
}
const handleChangeCurrent = (e: number) => {
    pageConfig.current = e
    initPages()
}
const handleChangeSize = (e: number) => {
    pageConfig.current = 1
    pageConfig.size = e
    initPages()
}
/**
 * 切换选择预览页面
 */
const changeCurrentPages = async (id: string) => {
    const { code, data, msg } = await doGetShopPagesDetail(id)
    if (code !== 200) return ElMessage.error(msg || '获取页面详情失败')
    currentPagesId.value = id
    components.value = JSON.parse(data.properties)
}
</script>

<template>
    <div class="pages">
        <div class="pages--left">
            <editor-preview :components="components" :is-preview="true" />
        </div>
        <div class="pages--right">
            <el-button style="width: 100px; margin-bottom: 10px" type="primary" @click="emits('editorPages')">新增</el-button>
            <el-button style="width: 100px; margin-bottom: 10px" type="primary" @click="showPlatformPages">从平台同步</el-button>
            <el-table :data="pagesList" border max-height="570" style="width: 100%">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column label="页面名称" prop="name" width="220">
                    <template #default="{ row }">
                        <div :style="row.id === currentPagesId ? 'color:#0F40F5' : ''" @click="changeCurrentPages(row.id)">
                            {{ row.name }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="页面说明" prop="remark" width="400">
                    <template #default="{ row }">
                        <div :style="row.id === currentPagesId ? 'color:#0F40F5' : ''" @click="changeCurrentPages(row.id)">
                            {{ row.remark }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="操作" prop="address" width="180">
                    <template #default="{ row }">
                        <span style="color: #0f40f5; cursor: pointer; margin-right: 20px" @click="handleOpenPages(row.id)">编辑</span>
                        <span style="color: #0f40f5; cursor: pointer" @click="handleClone(row.id)">复制</span>
                        <span style="color: #fd0505; cursor: pointer; margin-left: 20px" @click="handleDel(row.id)">删除</span>
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
@include b(pages) {
    display: flex;
    justify-content: space-around;
    padding: 50px 20px 50px 45px;
}
</style>
