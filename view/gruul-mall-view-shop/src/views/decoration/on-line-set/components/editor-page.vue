<script setup lang="ts">
import { ElMessage } from 'element-plus'
import NameEdit from './name-edit.vue'
import { useDecorationStore } from '@/store/modules/decoration'
import { doGetCustomPageList, doSubmitControl, doDelCustomPage, doChangeCutomPageName, doSetDefault, doGetNotPageInfo } from '@/apis/decoration'
import classification from '../packages/components/classification/classification'
import type { SubmitForm, PageType } from '../types'

// 页面列表伸缩标识
const togglePageTag = ref(true)
// 分类伸缩标识
const toggleClassTag = ref(true)
// 页面列表
const pageList = ref<SubmitForm[]>([
    {
        id: '1',
        functionType: 'PAGE',
        isDef: true,
        pageName: '首页',
        properties: [],
        platforms: 'H5',
    },
])
// 分类列表
const classList = ref<SubmitForm[]>([
    {
        id: '',
        functionType: 'CLASSIFY_PAGE',
        properties: classification,
        platforms: 'H5',
    },
])
const $emit = defineEmits(['change'])
const $decorationStore = useDecorationStore()

initCustomList()
initClassList()

/**
 * 新增页面
 */
const handleAddPage = () => {
    let data = {
        isDef: false,
        pageName: '自定义页面',
    }
    if (!pageList.value.length) {
        data = {
            isDef: true,
            pageName: '首页',
        }
    }
    saveCutomPage(data)
}
/**
 * 自定义页面显隐方法
 * @param {string} toggleKey
 */
const handleTogglePage = () => {
    togglePageTag.value = !togglePageTag.value
}
const handleDelCustomPage = async (id: string, idx: number) => {
    const { code } = await doDelCustomPage(id)
    if (code === 200) {
        ElMessage.success('删除成功')
        pageList.value.splice(idx, 1)
        handleSelectClass()
    } else {
        ElMessage.warning('删除失败')
    }
}
/**
 * 修改自定页面名称
 * @param {string} newName
 * @param {string} id
 */
const handleUpdateName = async ({ newName, id }: any) => {
    let name = newName
    if (newName.length > 15) {
        ElMessage.warning('自定义页面名称最大15个字符！')
        name = newName.substring(0, 15)
    }
    const { code } = await doChangeCutomPageName(name, id)
    if (code === 200) {
        ElMessage.success('修改页面名称成功')
    } else {
        ElMessage.error('修改页面名称失败')
    }
    await initCustomList()
}
/**
 * 设置默认页面
 */
const handleSetDefault = async (id: string) => {
    const { code } = await doSetDefault(id, $decorationStore.getterDecType)
    if (code === 200) {
        ElMessage.success('设置成功')
        initCustomList()
    } else {
        ElMessage.warning('设置失败')
    }
}
/**
 * 切换自定义页面
 */
const handleSelectPage = (page: SubmitForm) => {
    $decorationStore.SET_ACTIVE_PAGE(page)
    // 设置标识取消删除按钮
    $decorationStore.SET_ACTIVE_PAGE_TYPE('customize')
    $emit('change', {
        type: 'customize',
        page,
    })
}
const handleSelectClass = () => {
    $decorationStore.SET_ACTIVE_PAGE(classList.value[0])
    // 设置标识取消删除按钮
    $decorationStore.SET_ACTIVE_PAGE_TYPE('classification')
    $emit('change', {
        type: 'classification',
        page: classList.value[0],
    })
}
const handleToggleClass = () => {
    toggleClassTag.value = !toggleClassTag.value
}
/**
 * 初始化自定义页面
 */
async function initCustomList() {
    const decType = $decorationStore.getterDecType
    const { code, data } = await doGetCustomPageList(decType)
    if (code === 200) {
        pageList.value = data
        // 无缓存
        if (!$decorationStore.activePage.id) {
            if (data.length > 0) {
                // 无缓存有页面列表
                const tempPage = data.filter((item: any) => item.isDef)
                $decorationStore.SET_ACTIVE_PAGE(tempPage[0])
                $emit('change', {
                    type: 'customize',
                    page: tempPage[0],
                })
            }
        }
    } else {
        ElMessage.error('获取页面列表失败')
    }
}
async function initClassList() {
    const { code, data } = await doGetNotPageInfo($decorationStore.getterDecType, 'CLASSIFY_PAGE')
    if (code !== 200) return ElMessage.error('获取分类页失败')
    if (data)
        classList.value = [
            {
                id: data.id,
                functionType: 'CLASSIFY_PAGE',
                properties: data.properties[0].formData,
                platforms: $decorationStore.getterDecType,
            },
        ]
}
/**
 * 新增自定义页面
 * @param {*} page
 */
async function saveCutomPage(page: { isDef: boolean; pageName: string }) {
    const submitForm: SubmitForm = {
        id: '',
        functionType: 'PAGE',
        properties: [],
        platforms: $decorationStore.getterDecType,
        ...page,
    }
    const { code } = await doSubmitControl(submitForm)
    if (code === 200) {
        ElMessage.success('新增自定义页面成功')
        initCustomList()
    } else {
        ElMessage.error('新增自定义页面失败')
    }
}
</script>

<template>
    <div class="editor_control_wrap">
        <div class="editor_control_wrap_main">
            <el-scrollbar style="height: 100%; width: 100%">
                <!-- 分类页 -->
                <div class="tab_item" @click="handleToggleClass">
                    <el-icon v-if="toggleClassTag"><i-ep-caretBottom /></el-icon>
                    <el-icon v-else><i-ep-caretRight /></el-icon>
                    <span>分类页</span>
                </div>
                <div v-if="toggleClassTag">
                    <div class="page_item" @click="handleSelectClass">
                        <name-edit v-if="classList.length" name="分类" />
                    </div>
                </div>
                <!-- 自定义页面 -->
                <div class="tab_item" @click="handleTogglePage">
                    <el-icon v-if="togglePageTag"><i-ep-caretBottom /></el-icon>
                    <el-icon v-else><i-ep-caretRight /></el-icon>
                    <span>自定义页面</span>
                </div>
                <div v-if="togglePageTag">
                    <div v-for="(item, index) in pageList" :key="item.id" class="page_item" @click="handleSelectPage(item)">
                        <name-edit :name="item.pageName" :item="item" @change="handleUpdateName" />
                        <div v-if="item.isDef" style="font-size: 12px; color: #9999">首页</div>
                        <el-dropdown class="page_item_icon">
                            <el-icon class="el-icon--right"><i-ep-moreFilled /></el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item>
                                        <span v-if="!item.isDef" @click="handleSetDefault(item.id)">设为首页</span>
                                    </el-dropdown-item>
                                    <el-dropdown-item>
                                        <span v-if="!item.isDef" class="block" @click="handleDelCustomPage(item.id, index)">删除</span>
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
                <div v-if="pageList.length < 10" class="add_page_btn" @click="handleAddPage">
                    <span>+ 添加页面</span>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editControlPage.scss';
.page_item_icon {
    height: 12px;
    // margin-bottom: 23px;
}
</style>
