<script setup lang="ts">
import pages from './pages.vue'
import addText from './addText.vue'
import addActivity from './addActivity.vue'
import useGetPageData from '@/views/decoration/pc/components/menu/action-menu/views/custom-page/useGetPageData'
import messageBox from '@/views/decoration/pc/components/message-box.vue'
import { doDelShopPages } from '@/apis/decoration/index'
import { Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { Ref } from 'vue'
import type { ComponentsItem, ActiveName } from './type'
import type { ComponentItem } from '@/views/decoration/pc/components/packages/index/formModel'

const activeName = ref<ActiveName>('activity')

const components = {
    activity: addActivity,
    text: addText,
} as const

const isActivity = computed(() => activeName.value === 'activity')
const title = computed(() => (isActivity.value ? '活动页面' : '图文页面'))

const { getText, textData, textName, textNoMore, textLoading, getActivity, activityData, activityName, activeNoMore, activeLoading } =
    useGetPageData()

const pageComponents = inject('pageComponents', ref([])) as Ref<ComponentItem[]>

/**
 * 添加页面
 */
const visible = ref(false)

const cancel = () => {
    visible.value = false
}

const add = () => {
    visible.value = true
}

/**
 * 点击确定按钮
 */
const pageRef = ref<InstanceType<typeof addText>>()
const confirm = async () => {
    try {
        if (isEdit) {
            await pageRef.value?.editPage(currentEdit.value!)
        } else {
            await pageRef.value?.pageSave()
        }
        handleGetData()
        visible.value = false
    } catch (error) {
        console.log('error', error)
    }
}

/**
 * 校验页面是否被引用
 */
const validPage = (id: string) => {
    for (let i = 0; i < pageComponents.value.length; i++) {
        const item = pageComponents.value[i]

        if (item.value === 'navigation') {
            const flag = item.formData.some((ite: any) => ite.link === id)

            if (flag) {
                message('导航')
                return false
            }
        } else if (item.value === 'swiper') {
            const flag = item.formData.some((ite: any) => {
                if (typeof ite.link !== 'string' && (ite.link.type === '活动页面' || ite.link.type === '图文页面')) {
                    return ite.link.item.id === id
                }
                return false
            })
            if (flag) {
                message('轮播图')

                return false
            }
        } else if (item.formData.page?.id === 'id') {
            message('推荐商品')
            return false
        }
    }

    return true
}

/**
 * 删除页面
 */
let delId = '-1'
const messageBoxRef = ref<InstanceType<typeof messageBox>>()
const delPage = async (id: string) => {
    if (!validPage(id)) return // 校验
    messageBoxRef.value?.open()
    delId = id
}

/**
 * 删除提示
 */
const message = (name: string) => {
    ElMessage.error(`此页面已被${name}组件引用，无法删除!`)
}

const del = async () => {
    try {
        const { success } = await doDelShopPages(delId)
        if (!success) return ElMessage.error('删除活动页面失败！')
        handleGetData()
        delId = '-1'
    } catch (error) {
        console.log('error', error)
    }
}

/**
 * 初始化数据
 */
const initActivityForm = () => ({ name: '', img: '', list: {}, radioKeys: [] })
const initTextForm = () => ({ name: '', img: '', text: '' })

/**
 * 初始值
 */
const activityForm = ref(initActivityForm())
const textForm = ref(initTextForm())

/**
 * 初始化所有表单数据
 */
const initFormData = () => {
    textForm.value = initTextForm()
    activityForm.value = initActivityForm()
    isEdit = false
}

const currentForm = computed<any>(() => (isActivity.value ? activityForm.value : textForm.value))

/**
 * 编辑
 */
const currentEdit = ref<ComponentsItem>()
let isEdit = false
const edit = (row: ComponentsItem) => {
    isEdit = true
    currentEdit.value = row
    if (isActivity.value) {
        activityForm.value = JSON.parse(row.properties)
    } else {
        textForm.value = JSON.parse(row.properties)
    }
    visible.value = true
}

/**
 * 调用获取数据函数
 */
const handleGetData = () => {
    if (isActivity.value) {
        getActivity()
    } else {
        getText()
    }
}

onBeforeMount(() => {
    getText()
})
</script>

<template>
    <div class="custom-page">
        <el-tabs v-model="activeName" class="tabs">
            <el-tab-pane name="activity">
                <template #label> <div class="tabs__label">活动页面</div> </template>

                <pages
                    v-model:name="activityName"
                    :line-data="activityData"
                    :loading="activeLoading"
                    :no-more="activeNoMore"
                    @add="add"
                    @get-data="getActivity"
                    @del="delPage"
                    @edit="edit"
                />
            </el-tab-pane>

            <el-tab-pane name="text">
                <template #label> <div class="tabs__label">图文页面</div> </template>

                <pages
                    v-model:name="textName"
                    :line-data="textData"
                    :loading="textLoading"
                    :no-more="textNoMore"
                    @del="delPage"
                    @add="add"
                    @get-data="getText"
                    @edit="edit"
                />
            </el-tab-pane>
        </el-tabs>

        <el-dialog
            v-model="visible"
            :title="title"
            destroy-on-close
            class="add-dialog"
            append-to-body
            width="950px"
            :show-close="false"
            :close-on-press-escape="false"
            :close-on-click-modal="false"
            @closed="initFormData"
        >
            <!-- 头部 -->
            <template #header>
                <div class="my-header">
                    <div class="f16">{{ title }}</div>
                    <el-icon size="16px" class="my-header__icon" @click="cancel"><Close /></el-icon>
                </div>
            </template>

            <component :is="components[activeName]" ref="pageRef" v-model:formData="currentForm" />

            <!-- 底部 -->
            <template #footer>
                <div class="center__dialog-footer">
                    <el-button @click="cancel">取消</el-button>
                    <el-button type="primary" @click="confirm"> 确定 </el-button>
                </div>
            </template>
        </el-dialog>
        <message-box ref="messageBoxRef" title="确认删除" message="当前内容删除后无法恢复，确认删除吗？" @confirm="del" />
    </div>
</template>

<style lang="scss" scoped>
@include b(tabs) {
    :deep(.el-tabs__header) {
        --el-color-primary: #f54319;
        --el-text-color-primary: #666666;
        .el-tabs__nav-wrap::after {
            height: 1px;
            background-color: #dcdae2;
        }

        @include e(label) {
            padding: 0 6px;
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
</style>

<style lang="scss">
@include b(add-dialog) {
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
