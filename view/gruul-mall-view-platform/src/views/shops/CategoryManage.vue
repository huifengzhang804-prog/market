<script lang="ts" setup>
import { doGetCategory, doNewCategory, doSortCategory, doUpdateCategory, doDelCategory, doGetCategoryLevelByParentId } from '@/apis/shops'
import { ElMessage, ElMessageBox } from 'element-plus'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { SelectLevelType, Tree } from './types'
import type Node from 'element-plus/es/components/tree/src/model/node'
import type { NodeDropType } from 'element-plus/es/components/tree/src/tree.type'
import type { DragEvents } from 'element-plus/es/components/tree/src/model/useDragNode'
import type { FormInstance, UploadProps } from 'element-plus'
import CategoryForm from './components/category/CategoryForm'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { LinkSelectItem } from '@/components/link-select/linkSelectItem'
import QUpload from '@/components/q-upload/q-upload.vue'
import LinkSelect from '@/components/link-select/link-select.vue'

const dialogFlag = ref(false)
// 是否编辑
const isEdit = ref(false)
// 记录id
// 二/三级分类
const submitForm = reactive({
    level: '0',
    fLevelInput: '', // 第一层输入
    fLevelSelect: 0, // 第一层选择框
    sLevelInput: '', // 第二层输入框
    sLevelSelect: 0, //  第二层选择框
    deductionRatio: null as null | number,
    tLevelTable: [
        {
            name: '',
            categoryImg: '',
        },
    ],
})
// 层级区分 1 一级 2二级
const radioCategory = ref(0)
const currentEditId = ref()
const formRef = ref<FormInstance>()
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})
// 类目广告弹窗
const dialogTableVisible = ref(false)
const list = ref<SelectLevelType['firstArr']>([])
// 一级二级下拉 数据集
const selectLevel = reactive<SelectLevelType>({
    firstArr: [],
    secondArr: [],
})
const currentUploadIndex = ref(0)
// 二级分类数组
const sLevelInputArr = ref([
    {
        sLevelInput: '',
        categoryImg: '',
        deductionRatio: 0,
    },
])
// 类目广告
const categoryAdVisible = ref(false)

getCategory()

/**
 * 初始化列表
 * @param {*} list target
 * @param {*} str attribute
 */
function initList(list: any[], str: string) {
    list.forEach((item) => {
        if (item[str]) {
            item.children = item[str]
            delete item[str]
            if (item.children.length) {
                initList(item.children, 'categoryThirdlyVos')
            }
        }
    })
}
/**
 * 新增三级table的item
 */
const handleAddNewThird = () => {
    const tempObj = {
        name: '',
        categoryImg: '',
    }
    submitForm.tLevelTable.push(tempObj)
}
/**
 * 删除当前三级的item
 * @param {number} index
 */
const hadnleDelThird = (index: number) => {
    if (submitForm.tLevelTable.length === 1) {
        return ElMessage.error('请至少保留一项')
    }
    submitForm.tLevelTable.splice(index, 1)
}
const handleAddUploadBigProductImg: UploadProps['onSuccess'] = async (res) => {
    submitForm.tLevelTable[currentUploadIndex.value].categoryImg = res.data
}
const handleCloseDialog = () => {
    resetForm()
}
/**
 * 获取类目列表
 * @param {number} current
 * @param {number} size
 */
async function getCategory() {
    const { data } = await doGetCategory({
        current: pageConfig.current,
        size: pageConfig.size,
    })
    pageConfig.total = data.total
    initList(data.records, 'secondCategoryVos')
    list.value = data.records
}
function resetForm() {
    if (!formRef.value) return
    formRef.value.resetFields()
    isEdit.value = false
    submitForm.fLevelInput = '' // 第一层输入
    submitForm.fLevelSelect = 0 // 第一层选择框
    submitForm.sLevelInput = '' // 第二层输入框
    submitForm.sLevelSelect = 0 //  第二层选择框
    submitForm.deductionRatio = null
    radioCategory.value = 0
    submitForm.level = '0'
    submitForm.tLevelTable = [
        {
            name: '',
            categoryImg: '',
        },
    ]
    sLevelInputArr.value = [
        {
            sLevelInput: '',
            categoryImg: '',
            deductionRatio: 0,
        },
    ]
}
/**
 * 获取一级select类目
 */
async function handleGetFirstLevel() {
    const { data, success } = await doGetCategoryLevelByParentId('LEVEL_1', 0, 1000, 1)
    if (success) {
        selectLevel.firstArr = data.records
        // 默认选中第一个
        submitForm.fLevelSelect = data.records[0].id
    } else {
        ElMessage.error('获取一级类目失败')
    }
}
/**
 * 获取二级select类目
 */
async function categoryLevelByParentIdFn(level: 'LEVEL_2' | 'LEVEL_1' | 'LEVEL_3', firstLevelId: number, isChooseTwoLevel?: boolean) {
    const { code, data } = await doGetCategoryLevelByParentId(level, firstLevelId, 1000, 1)
    if (code !== 200) return ElMessage.error('获取类目失败')
    selectLevel.secondArr = data.records
    if (isChooseTwoLevel) {
        if (data.records[0]) {
            if (radioCategory.value !== 2) {
                radioCategory.value = 2
            }
            submitForm.sLevelSelect = data.records[0]
            return
        }
        // 如果二级类目没数据 就切换到新增二级分类
        radioCategory.value = 1
    }
}

/**
 * 增加二/三级分类
 * @param {*} data
 */
const handleAppendCategory = async (node: Node, data: Tree) => {
    if (!selectLevel.firstArr.length) {
        await handleGetFirstLevel()
    }
    if (data.parentId === 0) {
        // 增加二级分类
        submitForm.fLevelSelect = data.id
        radioCategory.value = 1
    } else {
        submitForm.tLevelTable = [
            {
                name: '',
                categoryImg: '',
            },
        ]
        submitForm.fLevelSelect = data.parentId
        submitForm.deductionRatio = data.deductionRatio as number
        await categoryLevelByParentIdFn('LEVEL_2', data.parentId)
        submitForm.sLevelSelect = data.id
        radioCategory.value = 2
    }
    formRef.value?.resetFields()
    dialogFlag.value = true
}
/**
 * 上传数据
 */
const handleSubmitTest = async () => {
    if (!formRef.value) return
    const isValidate = await formRef.value.validate()
    if (!isValidate) return
    const waitSubmitForm = dataProcessing(radioCategory.value)
    const inputEmity = waitSubmitForm.nameImgs.every((item) => item.name)
    const imgEmity = waitSubmitForm.nameImgs.every((item) => item.categoryImg)
    if (!inputEmity) {
        ElMessage.error('请输入分类名称')
        return
    }
    if (radioCategory.value !== 0 && !imgEmity) {
        // 三级分类校验图片
        ElMessage.error('请上传类目图片')
        return
    }
    const isExist = waitSubmitForm.nameImgs.find((item) => item.name?.length > 10)
    if (isExist) {
        ElMessage.error('请控制类目名称在10个字以内')
        return
    }
    const { code, success, msg } = isEdit.value ? await doUpdateCategory(currentEditId.value, waitSubmitForm) : await doNewCategory(waitSubmitForm)
    if (code === 200 && success) {
        ElMessage.success(`${isEdit.value ? '编辑' : '新增'}成功`)
        updateData(radioCategory.value)
        resetForm()
        dialogFlag.value = false
        // await initSelectList()
        formRef.value?.resetFields()
        await getCategory()
    } else {
        ElMessage.error(msg)
    }
}
/**
 * 编辑/添加更新数据
 */
const updateData = (radioCategory: number) => {
    if (isEdit.value) {
        // 编辑
        switch (radioCategory) {
            case 0:
                // 更新一级select类目
                handleGetFirstLevel()
                break

            default:
                break
        }
    }
}
/**
 * 添加类目数据处理
 * @param {*} radioCategory
 */
function dataProcessing(radioCategory: number) {
    const { sLevelSelect, fLevelSelect, tLevelTable } = toRaw(submitForm)
    let waitSubmitForm = {
        parentId: 0,
        nameImgs: [
            {
                name: '',
                categoryImg: '' as null | string,
            },
        ],
        level: 'LEVEL_1',
    }
    switch (radioCategory) {
        case 0:
            // 提交一级分类
            waitSubmitForm.nameImgs[0].name = submitForm.fLevelInput
            break
        case 1:
            // 提交二级分类
            waitSubmitForm.nameImgs = sLevelInputArr.value.map((item) => ({
                name: item.sLevelInput,
                categoryImg: item.categoryImg,
                deductionRatio: item.deductionRatio,
            }))
            waitSubmitForm.parentId = fLevelSelect
            waitSubmitForm.level = 'LEVEL_2'
            break
        case 2:
            // 提交三级分类
            waitSubmitForm.nameImgs = tLevelTable
            waitSubmitForm.parentId = sLevelSelect
            waitSubmitForm.level = 'LEVEL_3'
            break
        default:
            break
    }
    // 返回提交数据
    return waitSubmitForm
}
/**
 * 编辑回显
 */
const handleEditCategory = async (node: Node, data: Tree) => {
    isEdit.value = true
    formRef.value?.resetFields()
    currentEditId.value = data.id
    if (data.parentId === 0) {
        //   '一级类目编辑
        editLevelOneCategories(data)
    } else if (data.parentId !== 0 && 'children' in data) {
        // '二级'
        editLevelTowCategories(data)
    } else {
        radioCategory.value = 2
        // 一级复制完毕 需要更新二级的下拉选择框（发请求） 否则id不匹配会出现id暴露给用户的情况
        submitForm.fLevelSelect = node.parent.data.parentId
        submitForm.deductionRatio = node.parent.data.deductionRatio
        await categoryLevelByParentIdFn('LEVEL_2', node.parent.data.parentId)
        submitForm.sLevelSelect = data.parentId
        submitForm.tLevelTable = [{ name: data.name, categoryImg: data.categoryImg as string }]
    }
    dialogFlag.value = true
}
/**
 * 编辑一级分类
 */
const editLevelOneCategories = (data: Tree) => {
    radioCategory.value = 0
    submitForm.fLevelInput = data.name
}
/**
 * 编辑二级分类
 */
const editLevelTowCategories = (data: Tree) => {
    radioCategory.value = 1
    sLevelInputArr.value[0].sLevelInput = data.name // 二级输入
    sLevelInputArr.value[0].deductionRatio = data.deductionRatio as number
    sLevelInputArr.value[0].categoryImg = data.categoryImg as string
    submitForm.fLevelSelect = data.parentId // 一级选择
}
/**
 * 删除节点
 * @param {*} data
 */
const handleDeleteCategory = async (node: Node, data: any) => {
    try {
        await ElMessageBox.confirm('将删除该分类下所有信息?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code, success, msg } = await doDelCategory(data.id)
        if (code === 200 && success) {
            await getCategory()
            ElMessage.success('删除成功')
        } else {
            ElMessage.error(msg || '删除失败')
        }
    } catch (e) {
        console.log(' ')
    }
}
/**
 * 拖拽
 * @param {*} moveNode 拖动节点
 * @param {*} inNode 放置节点
 * @param {*} type 放置方式
 * @returns {*} Boolean
 */
const collapse = (moveNode: Node, inNode: Node, type: NodeDropType) => {
    return moveNode.data.parentId === inNode.data.parentId ? type !== 'inner' : false
}
/**
 * 共四个参数，依次为：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）、event
 * @param {*}
 */
const nodeDrop = async (moveNode: Node, inNode: Node, type: NodeDropType, event: DragEvents) => {
    const ids = inNode.parent.childNodes.map((item) => item.data.id)
    const { code, success } = await doSortCategory(ids, moveNode.data.parentId)
    if (code === 200 && success) {
        ElMessage.success('排序成功')
    }
}
const customNodeClass = (data: any, node: Node): string => {
    if (data.parentId === 0) {
        return 'is-parentId'
    }
    if (data.parentId !== 0 && data.children) {
        return 'is-children'
    }
    if (data.categoryImg) {
        return 'is-penultimate'
    }
    return ''
}

const defaultExpandedArr = ref<number[]>([])
const defaultProps = { children: 'children', class: customNodeClass }
const disabled = (val: Tree) => val.children && val.children.length

const handleClose = () => {
    // resetForm()
    dialogFlag.value = false
}
const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    getCategory()
}

const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getCategory()
}

/**
 * 打开节点
 */
const nodeExpand = (data: Tree) => {
    defaultExpandedArr.value.push(data.id)
}

/**
 * 关闭节点
 */
const nodeCollapse = (data: Tree) => {
    const index = defaultExpandedArr.value.findIndex((val) => val === data.id)
    if (index !== -1) {
        defaultExpandedArr.value.splice(index)
    }
}
const tableHeadList = [
    {
        name: '类目名称',
        value: 'category',
    },
    {
        name: '关联商品数',
        value: 'productNumber',
    },
    {
        name: '类目扣率',
        value: 'deductionRate',
    },
]

const fileList = ref<{ img: string; isShowMask: boolean; link: LinkSelectItem }[]>([])
const fileIndex = ref(0)
const addNewMainSuccess = (response: string) => {
    if (typeof response === 'string') {
        fileList.value.push({ img: response, isShowMask: false, link: { id: '', type: '', name: '', url: '', append: '', shopId: '' } })
    }
}

const handleSubmitCategoryAd = async () => {
    const ads = fileList.value.map((item) => {
        return {
            img: item.img,
            link: item.link,
        }
    })
    const waitSubmitForm = dataProcessing(radioCategory.value)
    const { code, success, msg } = await doUpdateCategory(currentEditId.value, { ...waitSubmitForm, ads })
    if (code === 200 && success) {
        ElMessage.success('新增成功')
        getCategory()
    } else {
        ElMessage.error(msg)
    }
    fileList.value = []
    handleCloseCategoryAd()
}

const handleFileIndex = (index: number, event?: Event) => {
    if (event) {
        event.stopPropagation()
    }
    // 确保索引在有效范围内
    if (index >= 0 && index < fileList.value.length) {
        fileIndex.value = index
    }
}

const handleDeleteFile = (index: number, event?: Event) => {
    if (event) {
        event.stopPropagation()
    }
    fileList.value.splice(index, 1)
    // 如果删除的是当前选中的文件，重置 fileIndex
    if (fileIndex.value === index) {
        fileIndex.value = 0
    } else if (fileIndex.value > index) {
        // 如果删除的文件在当前选中文件之前，需要调整 fileIndex
        fileIndex.value--
    }
    // 确保 fileIndex 不超出数组范围
    if (fileList.value.length > 0 && fileIndex.value >= fileList.value.length) {
        fileIndex.value = fileList.value.length - 1
    }
}

const handleCloseCategoryAd = () => {
    categoryAdVisible.value = false
    // 重置 fileIndex 到有效范围
    if (fileList.value.length > 0 && fileIndex.value >= fileList.value.length) {
        fileIndex.value = 0
    }
}

const handleCategoryAd = (data: Tree) => {
    categoryAdVisible.value = true
    currentEditId.value = data.id
    fileList.value = data.ads?.map((item: any) => ({ img: item.img, isShowMask: false, link: item.link })) || []
    editLevelOneCategories(data)
}
</script>
<template>
    <div class="cate">
        <el-button round type="primary" @click="dialogFlag = true">新增一级类目</el-button>
        <span style="color: #f54319">请确保已添加第三级类目</span>
    </div>
    <div class="tree-container">
        <div class="tab-head">
            <div v-for="item in tableHeadList" :key="item.value" class="head-item">
                {{ item.name }}
            </div>
            <div class="head-item">类目广告</div>
            <div class="head-item">操作</div>
        </div>
        <el-tree
            :allow-drop="collapse"
            :data="list"
            :default-expanded-keys="defaultExpandedArr"
            :expand-on-click-node="true"
            :indent="30"
            :props="defaultProps"
            draggable
            node-key="id"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            @node-drop="nodeDrop"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <template #default="{ node, data }">
                <i class="iconfont icon-tuozhuai" style="order: -3; font-size: 26px; color: #c7c7c7; position: absolute; left: 5px"></i>
                <span class="custom-tree-node">
                    <div v-if="data.parentId === 0" class="cate__img">
                        <span class="level-name">{{ data.name }}</span>
                        <span class="level-1-count">{{ data.productNumber || 0 }}</span>
                    </div>
                    <div v-else-if="data.parentId !== 0 && 'children' in data" class="cate__img">
                        <el-image :src="data.categoryImg" fit="fill" style="width: 48px; height: 48px; margin: 0 18px" />
                        <span class="level-name">{{ data.name }}</span>
                        <span class="ml_30">{{ data.productNumber || 0 }}</span>
                    </div>
                    <div v-else class="cate__img">
                        <el-image :src="data.categoryImg" fit="fill" style="width: 48px; height: 48px; margin: 0 18px" />
                        <span class="level-name">{{ data.name }}</span>
                        <span style="width: 70px; text-align: center; margin-left: 54px">{{ data.productNumber || 0 }}</span>
                    </div>

                    <span
                        :style="{
                            minWidth: '40px',
                            width: '260px',
                            textAlign: 'center',
                        }"
                        >{{ data.deductionRatio }}<span v-if="typeof data.deductionRatio === 'number'">%</span></span
                    >
                    <el-link
                        v-if="data.parentId === 0 && data.ads && data.ads.length > 0"
                        :underline="false"
                        style="margin-right: 30px"
                        type="primary"
                        :style="{
                            minWidth: '70px',
                            textAlign: 'center',
                        }"
                        @click.stop="
                            () => {
                                fileList = data.ads?.map((item: any) => ({ img: item.img, isShowMask: false, link: item.link })) || []
                                dialogTableVisible = true
                            }
                        "
                        >查看</el-link
                    >
                    <span style="flex: 1">
                        <!-- 只展示在一级类目 -->
                        <el-link
                            v-if="data.parentId === 0"
                            :underline="false"
                            style="margin-right: 20px"
                            type="primary"
                            @click.stop="handleCategoryAd(data)"
                        >
                            {{ '类目广告' }}
                        </el-link>
                        <el-link
                            v-if="'children' in data"
                            :underline="false"
                            class="mr_20"
                            type="primary"
                            @click.stop="handleAppendCategory(node, data)"
                        >
                            新增{{ data.parentId === 0 ? '二' : '三' }}级类目
                        </el-link>
                        <el-link :underline="false" class="mr_20" type="primary" @click.stop="handleEditCategory(node, data)"> 编辑 </el-link>
                        <el-link
                            :title="disabled(data) ? '该分类存在绑定商品,不能删除' : ''"
                            :underline="false"
                            type="danger"
                            @click.stop="handleDeleteCategory(node, data)"
                            >删除
                        </el-link>
                    </span>
                </span>
            </template>
        </el-tree>
    </div>
    <BetterPageManage
        :page-num="pageConfig.current"
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="dialogFlag" :title="isEdit ? '编辑类目' : '新增类目'" width="650px" @closed="handleCloseDialog">
        <CategoryForm
            v-model:currentUploadIdx="currentUploadIndex"
            v-model:sLevelInputArray="sLevelInputArr"
            v-model:submitFormModel="submitForm"
            :first-arr-list="list"
            :is-edit="isEdit"
            :radio-category="radioCategory"
            :select-level="selectLevel"
            @update:form-ref="formRef = $event"
        />
        <template #footer>
            <span>
                <el-button type="" @click="handleClose">取消</el-button>
                <el-button type="primary" @click="handleSubmitTest">提交</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="categoryAdVisible" title="类目广告" width="500px">
        <div>类目广告(532*200)</div>
        <div style="margin: 15px 0 0 0; display: flex; align-items: center; gap: 10px">
            <div v-for="(item, index) in fileList" :key="index">
                <q-upload
                    v-model:src="item.img"
                    :isCropper="false"
                    style="margin-right: 10px; border-radius: 5px"
                    :style="{ border: fileIndex === index ? '2px solid red' : 'none' }"
                    :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg', 'image/gif'] }"
                    :cropper="false"
                    :height="50"
                    :width="130.75"
                    :categoryAdvertising="true"
                    @mouseover="item.isShowMask = true"
                    @mouseleave="item.isShowMask = false"
                >
                    <template #mask>
                        <div v-if="item.isShowMask" class="mask-item" @click.stop="handleFileIndex(index, $event)">
                            <QIcon name="icon-shanchu" size="26px" @click="handleDeleteFile(index, $event)"></QIcon>
                        </div>
                    </template>
                </q-upload>
            </div>
            <q-upload
                v-show="fileList.length < 3"
                :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg'] }"
                :isCropper="false"
                :height="50"
                :width="130.75"
                @change="addNewMainSuccess"
            />
        </div>
        <div v-if="fileList && fileList.length > 0 && fileList[fileIndex] && categoryAdVisible" style="margin-top: 10px">
            <LinkSelect v-model:link="fileList[fileIndex].link" />
        </div>
        <template #footer>
            <el-button @click="handleCloseCategoryAd">取 消</el-button>
            <el-button type="primary" @click="handleSubmitCategoryAd">确 定</el-button>
        </template>
    </el-dialog>
    <el-image-viewer v-if="dialogTableVisible" :url-list="fileList.map((item) => item.img)" @close="dialogTableVisible = false" />
</template>
<style lang="scss" scoped>
:deep(.custom__img) {
    display: flex;
    justify-content: flex-end;
    align-items: center;
}
:deep(.el-tree-node__content) {
    height: 60px !important;
    border-bottom: 1px solid var(--el-border-color);
}
:deep(.el-tree-node__content .el-icon) {
    font-size: 12px;
}
:deep(.el-tree-node__label) {
    flex: 1;
}
:deep(.ml_30) {
    text-align: center;
    width: 70px;
    margin-left: 83px;
}
:deep(.mr_20) {
    margin-right: 20px;
}

:deep(.avatar-uploader .el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}
:deep(.avatar-uploader .el-upload:hover) {
    border-color: var(--el-color-primary);
}

:deep(.el-icon.avatar-uploader-icon) {
    font-size: 28px;
    color: #8c939d;
    width: 34px;
    height: 34px;
    text-align: center;
    top: 3px;
}
:deep(.custom-tree-node) {
    flex: 1;
    display: flex;
    align-items: center;
    font-size: 14px;
    padding-right: 8px;
    order: -1;

    &:nth-child(2) {
        flex: 0 0 200px;
    }
    &:last-child {
        flex: 1;
        text-align: right;
    }
}
:deep(.el-tree-node__content > .el-tree-node__expand-icon) {
    order: -2;
    font-size: 20px;
    margin-left: 30px;
}

@include b(level-name) {
    width: 100px;
}

@include b(level-1-count) {
    width: 70px;
    text-align: center;
    margin-left: 196px;
}
@include b(tree-container) {
    flex: 1;
    padding-left: 16px;
    padding-right: 16px;
    @include b(tab-head) {
        height: 48px;
        font-weight: 550;
        color: #333;
        font-size: 14px;
        background-color: #f7f8fa;
        position: sticky;
        top: 0;
        z-index: 10;
        padding: 0 16px;
        display: flex;
        align-items: center;
        .head-item:nth-child(1) {
            flex: 0 0 350px;
        }
        .head-item:nth-child(2) {
            flex: 0 0 160px;
        }
        .head-item:nth-child(3) {
            flex: 0 0 170px;
        }
        .head-item:nth-child(4) {
            flex: 0 0 110px;
        }
        .head-item:last-child {
            flex: 1;
            text-align: right;
        }
    }
}

.mask-item {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
    z-index: 999;
}

:deep(.avatar-uploader) {
    margin: 0;
}
</style>
