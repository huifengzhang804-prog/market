<script lang="ts" setup>
import PageManage from '@/components/PageManage.vue'
import { CaretRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { doGetCategory, doGetCategoryLevel, doNewCategory, doSortCategory, doUpdateCategory, doDelCategory } from '@/apis/good'
import type { FormInstance, FormRules, TreeNode } from 'element-plus'
import type Node from 'element-plus/es/components/tree/src/model/node'
import type { DragEvents } from 'element-plus/es/components/tree/src/model/useDragNode'
import CategoryForm from './components/CategoryForm'

type SelectItemType = Record<'id' | 'name', string>
type SelectLevelType = Record<'firstArr' | 'secondArr', SelectItemType[]>
interface Tree {
    categoryId: string
    id: Long
    name: string
    parentId: Long
    secondCategoryVos?: TreeSecondary[]
    sort: number
    categoryImg?: string
    level: string
}
interface TreeSecondary {
    categoryId: string
    categoryThirdlyVos?: TreeThree[]
    id: Long
    name: string
    parentId: Long
    sort: number
}
interface TreeThree {
    categoryImg: string
    id: Long
    name: string
    parentId: Long
    productNum: number
    sort: number
}
interface ExampleData {
    level: string
    fLevelInput: string
    fLevelSelect: Long
    sLevelInput: string
    sLevelSelect: Long
    tLevelTable: [
        {
            name: string
            categoryImg: null | string
        },
    ]
}
const query = useRoute().query
const $router = useRouter()
const dialogFlag = ref(false)
const isEdit = ref(false)
const currentEditId = ref<Long>('')
const formRef = ref<FormInstance>()
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const tableHeadList = [
    {
        name: '类目名称',
        value: 'category',
    },
    {
        name: '关联商品数',
        value: 'productNumber',
    },
]
const list = ref<SelectLevelType['firstArr']>([])
// 层级区分 1 一级 2二级
const radioCategory = ref(0)
const submitForm = reactive<ExampleData>({
    level: '0',
    fLevelInput: '',
    fLevelSelect: '',
    sLevelInput: '',
    sLevelSelect: '',
    tLevelTable: [
        {
            name: '',
            categoryImg: null as null | string,
        },
    ],
})
// 二级分类数组
const sLevelInputArr = ref([
    {
        sLevelInput: '',
        categoryImg: '',
    },
])
const currentUploadIndex = ref(0)
const selectLevel = reactive<SelectLevelType>({
    firstArr: [],
    secondArr: [],
})
const rules = reactive<FormRules>({
    fLevelSelect: [
        {
            required: true,
            message: '请选择类目',
            trigger: 'change',
        },
    ],
    sLevelInput: [
        { required: true, message: '请输入二级类目', trigger: 'blur' },
        { min: 1, max: 100, message: '类目文字在1~100之间', trigger: 'blur' },
    ],
    sLevelSelect: [
        {
            required: true,
            message: '请选择类目',
            trigger: 'change',
        },
    ],
})
/**
 * 获取类目列表
 * @param {number} current
 * @param {number} size
 */
const getCategory = async () => {
    const { data } = await doGetCategory({
        current: pageConfig.current,
        size: pageConfig.size,
    })
    pageConfig.total = data.total
    list.value = data.records
}
getCategory()
const initSelectList = async (level = 0, parentId = 0) => {
    const { data: firstData } = await doGetCategoryLevel(level, parentId)
    const { data: secData } = await doGetCategoryLevel(1, firstData.records[0].id)
    selectLevel.firstArr = firstData.records
    submitForm.fLevelSelect = firstData.records[0].id
    selectLevel.secondArr = secData.records
    submitForm.sLevelSelect = secData.records[0].id
}
/**
 * 获取二级select类目/一级的change
 * @param {string} firstLevelId 一级类目id
 */
async function handleGetSecLevel(firstLevelId: Long) {
    const { data, success, code } = await doGetCategoryLevel(1, firstLevelId)
    if (success && code === 200) {
        selectLevel.secondArr = data.records
        if (data.records[0]?.id) {
            radioCategory.value = 2
            submitForm.sLevelSelect = data.records[0].id
            return
        }
        // 如果该分类下没有二级分类则切换到新增二级
        submitForm.sLevelSelect = ''
        radioCategory.value = 1
    } else {
        ElMessage.error('获取二级类目失败')
    }
}
const customNodeClass = (data: any, node: Node): string => {
    if (data.categoryImg) {
        return 'is-penultimate'
    }
    return ''
}
/**
 * 共四个参数，依次为：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）、event
 * @param {*}
 */
const nodeDrop = async (moveNode: Node, inNode: Node, type: any, event: DragEvents) => {
    const ids = inNode.parent.childNodes.map((item) => item.data.id)
    const { code, success } = await doSortCategory(ids, moveNode.data.parentId)
    if (code === 200 && success) {
        ElMessage.success('排序成功')
    }
}
/**
 * 拖拽
 * @param {*} moveNode 拖动节点
 * @param {*} inNode 放置节点
 * @param {*} type 放置方式
 * @returns {*} Boolean
 */
const collapse = (moveNode: Node, inNode: Node, type: any) => {
    return moveNode.data.parentId === inNode.data.parentId ? type !== 'inner' : false
}

/**
 *  // 增加二/三级分类
 * @param {*} data
 */
const handleAppendCategory = async (node: Node, data: Tree) => {
    formRef.value?.resetFields()
    if (!selectLevel.firstArr.length) {
        await initSelectList()
    }
    if (data.parentId === 0) {
        submitForm.fLevelSelect = data.id
        radioCategory.value = 1
    } else {
        submitForm.fLevelSelect = data.parentId
        await handleGetSecLevel(data.parentId)
        submitForm.sLevelSelect = data.id

        radioCategory.value = 2
    }
    dialogFlag.value = true
}
/**
 * 编辑回显
 */
const handleEditCategory = async (node: Node, data: Tree) => {
    formRef.value?.resetFields()
    isEdit.value = true
    currentEditId.value = data.id
    if (data.parentId === 0) {
        //   '一级类目编辑
        radioCategory.value = 0
        submitForm.fLevelInput = data.name
    } else if (data.parentId !== 0 && data.level === 'LEVEL_2') {
        // '二级'
        radioCategory.value = 1
        sLevelInputArr.value[0].sLevelInput = data.name // 二级输入
        sLevelInputArr.value[0].categoryImg = data.categoryImg as string
        submitForm.fLevelSelect = data.parentId // 一级选择
    } else {
        radioCategory.value = 2
        // 一级复制完毕 需要更新二级的下拉选择框（发请求） 否则id不匹配会出现id暴露给用户的情况
        submitForm.fLevelSelect = node.parent.data.parentId
        await handleGetSecLevel(node.parent.data.parentId)
        submitForm.sLevelSelect = data.parentId
        submitForm.tLevelTable = [{ name: data.name, categoryImg: data.categoryImg as string }]
    }
    dialogFlag.value = true
}
/**
 * 删除节点
 * @param {*} data
 */
const handleDeleteCategory = async (node: Node, data: any) => {
    //删除类目新增判断 如果一级类目下面有子类目不能删除 如果二级类目下面有商品不能删除
    if (data.children?.length) {
        switch (data.children[0].level) {
            case 'LEVEL_2':
                ElMessage.info('该分类下存在二级分类无法删除')
                return
            case 'LEVEL_3':
                ElMessage.info('该分类下存在商品无法删除')
                return
            default:
                break
        }
    }
    try {
        await ElMessageBox.confirm('确定删除吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code } = await doDelCategory(data.id)
        if (code === 200) {
            getCategory()
            ElMessage.success('已删除')
        }
    } catch (e) {
        console.log(' ')
    }
}
/**
 * 弹窗关闭初始化数据
 */
const handleCloseDialog = () => {
    resetForm()
}
/**
 * 状态初始化
 */
function resetForm() {
    if (formRef.value) {
        formRef.value.resetFields()
    }
    isEdit.value = false
    radioCategory.value = 0
    submitForm.level = '0'
    submitForm.fLevelInput = ''
    submitForm.fLevelSelect = '' as string | number
    submitForm.sLevelInput = ''
    submitForm.sLevelSelect = ''
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
        },
    ]
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
    submitForm.tLevelTable.splice(index, 1)
}
/**
 * 上传数据
 */
const handleSubmitTest = async () => {
    if (!formRef.value) return
    const isValidate = await formRef.value.validate()
    if (!isValidate) return
    const waitSubmitForm = dataProcessing(radioCategory.value)
    const inputEmity = waitSubmitForm.nameImages.every((item) => item.name)
    const inputOutOf = waitSubmitForm.nameImages.every((item) => item.name.length > 8)
    const imgEmity = waitSubmitForm.nameImages.every((item) => item.categoryImg)
    if (!inputEmity) {
        ElMessage.error('请输入分类名称')
        return
    }
    if (inputOutOf) {
        ElMessage.error('请控制类目名称在8个字以内')
        return
    }
    if (radioCategory.value !== 0 && !imgEmity) {
        // 三级分类校验图片
        ElMessage.error('请上传类目图片')
        return
    }
    const { code, msg, success } = isEdit.value ? await doUpdateCategory(currentEditId.value, waitSubmitForm) : await doNewCategory(waitSubmitForm)
    if (code === 200 && success) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogFlag.value = false
        await getCategory()
        await initSelectList()
        resetForm()
    } else {
        ElMessage.error(msg || (isEdit.value ? '修改失败' : '新增失败'))
    }
}
const defaultExpandedArr = ref<Long[]>([])
/**
 * 添加类目数据处理
 * @param {*} radioCategory
 */
function dataProcessing(radioCategory: number) {
    const { sLevelSelect, fLevelSelect, tLevelTable } = toRaw(submitForm)
    let waitSubmitForm = {
        parentId: '0' as Long,
        nameImages: [
            {
                name: '',
                categoryImg: null as null | string,
            },
        ],
        level: 'LEVEL_1',
    }
    switch (radioCategory) {
        case 0:
            // 提交一级分类
            waitSubmitForm.nameImages[0].name = submitForm.fLevelInput
            break
        case 1:
            // 提交一级分类
            waitSubmitForm.nameImages = sLevelInputArr.value.map((item) => ({ name: item.sLevelInput, categoryImg: item.categoryImg }))
            waitSubmitForm.parentId = fLevelSelect
            waitSubmitForm.level = 'LEVEL_2'
            break
        case 2:
            // 提交一级分类
            waitSubmitForm.nameImages = tLevelTable
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

const handleClose = () => {
    dialogFlag.value = false
}

// 分页方法
const handleSizeChange = (val: number) => {
    pageConfig.size = val
    pageConfig.current = 1
    getCategory()
}
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getCategory()
}
</script>
<template>
    <div class="cate handle_container" style="margin-bottom: 0">
        <div>
            <el-button v-if="query.from" round @click="$router.back()">返回发布商品</el-button>
            <el-button round type="primary" @click="dialogFlag = true">新增一级类目</el-button>
        </div>
        <span style="color: #f54319">请确保已添加第三级类目</span>
    </div>
    <div class="tree-container f1">
        <div class="tab-head">
            <div class="head-item">
                <span v-for="item in tableHeadList" :key="item.value" :style="{ marginLeft: item.name === '关联商品数' ? '120px' : 'unset' }">{{
                    item.name
                }}</span>
            </div>
            <div class="head-item">
                <span>操作</span>
            </div>
        </div>
        <el-tree
            :allow-drop="collapse"
            :data="list"
            :default-expanded-keys="defaultExpandedArr"
            :expand-on-click-node="true"
            :icon="CaretRight"
            :indent="30"
            :props="{ children: 'children', class: customNodeClass }"
            draggable
            empty-text="没有数据了呢~~~~"
            node-key="id"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            @node-drop="nodeDrop"
        >
            <template #default="{ node, data }">
                <img class="arrAndDrop" src="@/assets/images/icon/aragAndDrop.png" />
                <span class="custom-tree-node">
                    <div v-if="data.parentId === 0" class="cate__img">
                        <span class="level-name1">{{ data.name }}</span>
                        <span class="level-1-count">{{ data.productNumber || 0 }}</span>
                    </div>
                    <div v-else-if="data.parentId !== 0 && data.level === 'LEVEL_2'" class="cate__img">
                        <el-image :src="data.categoryImg" fit="fill" style="width: 40px; height: 40px; margin-left: 12px; margin-right: 8px" />
                        <span class="level-name2">{{ data.name }}</span>
                        <span>{{ data.productNumber || 0 }}</span>
                    </div>
                    <div v-else class="cate__img">
                        <el-image :src="data.categoryImg" fit="fill" style="width: 40px; height: 40px; margin-left: 12px; margin-right: 8px" />
                        <span class="level-name3">{{ data.name }}</span>
                        <span>{{ data.productNumber || 0 }}</span>
                    </div>

                    <span>
                        <el-link
                            v-if="data.level !== 'LEVEL_3'"
                            :underline="false"
                            class="mr_30"
                            type="primary"
                            @click.stop="handleAppendCategory(node, data)"
                        >
                            新增{{ data.parentId === 0 ? '二' : '三' }}级类目
                        </el-link>
                        <el-link :underline="false" class="mr_30" type="primary" @click.stop="handleEditCategory(node, data)"> 编辑 </el-link>
                        <el-link :underline="false" class="mr_30" type="danger" @click.stop="handleDeleteCategory(node, data)">删除</el-link>
                    </span>
                </span>
            </template>
        </el-tree>
    </div>
    <div class="pagination">
        <PageManage
            :page-num="pageConfig.current"
            :page-size="pageConfig.size"
            :total="pageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <!-- test eee  -->
    <el-dialog v-model="dialogFlag" :title="isEdit ? '编辑类目' : '新增类目'" width="550px" @closed="handleCloseDialog">
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
            <el-button @click="handleClose">取消</el-button>
            <el-button type="primary" @click="handleSubmitTest">提交</el-button>
        </template>
    </el-dialog>
</template>
<style lang="scss" scoped>
:deep(.custom__img) {
    display: flex;
    justify-content: flex-end;
    align-items: center;
}
:deep(.el-tree-node__content) {
    height: 60px;
    border-bottom: 1px solid var(--el-border-color);
}
:deep(.el-tree-node__content) .el-icon {
    font-size: 12px;
}
:deep(.el-tree-node__label) {
    flex: 1;
}

:deep(.mr_30) {
    margin-right: 30px;
}
:deep(.custom-tree-node) {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
    order: -1;
}
</style>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
:deep(.el-tree-node__content) {
    height: 60px;
    border-bottom: 1px solid var(--el-border-color);
}
@include b(tree-container) {
    padding: 0 16px;
    overflow-y: auto;
    @include b(tab-head) {
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-weight: 550;
        color: #333;
        font-size: 14px;
        background-color: #f7f8fa;
        position: sticky;
        top: 0;
        z-index: 10;
        .head-item span:first-child {
            margin: 0 168px 0 55px;
        }
        .head-item span:nth-child(3) {
            margin-left: 220px;
        }
        .head-item:last-child span {
            margin-right: 40px;
        }
        .head-item:last-child {
            // width: 250px;
        }
    }
}
.arrAndDrop {
    order: -3;
    height: 22px;
    width: 16px;
    position: absolute;
    left: 16px;
}
.custom__name {
    width: 200px;
    @include utils-ellipsis();
}
@include b(cate) {
    color: #999;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-size: 14px;
    padding-top: 20px;
}
@include b(borderBox) {
    @include e(first) {
        background-color: #f2f2f6;
        // padding: 0px 10px;
        display: flex;
        justify-content: space-between;
        @include m(deal) {
            margin-right: 10px;
            display: flex;
            justify-content: space-between;
            width: 200px;
            cursor: pointer;
        }
    }
    @include e(child) {
        padding: 15px 10px 15px 20px;
        // border-bottom: 1px solid #f2f2f2;
        display: flex;
        justify-content: space-between;
        align-items: center;
        @include m(left) {
            display: flex;
            justify-content: space-between;
            width: 350px;
            padding-left: 10px;
            align-items: center;
        }
        @include m(info) {
            display: flex;
            align-items: center;
        }
        @include m(right) {
            display: flex;
            justify-content: space-between;
            width: 88px;
            cursor: pointer;
        }
    }
    v-deep .el-icon-arrow-right:before {
        content: '\e791';
    }
    v-deep .el-collapse-item__content {
        padding-bottom: 0;
    }
    v-deep .el-collapse-item__header {
        background-color: #f2f2f6;
        padding-left: 10px;
    }
}
@include b(dialog) {
    width: 363px;
    border: 1px solid #bababa;
    @include e(header) {
        padding: 0 5px;
        height: 30px;
        background: #bababa;
        font-size: 14px;
        color: #6a6a6a;
        @include m(name) {
            display: inline-block;
            width: 180px;
        }
        @include m(img) {
            display: inline-block;
            width: 110px;
        }
    }
    @include e(content) {
        padding: 0 5px;
        @include flex(flex-start);
        height: 60px;
        @include m(tool) {
            width: 100px;
            @include flex(space-around);
        }
    }
}
@include b(add-btn) {
    color: #2e99f3;
    cursor: pointer;
    &:hover {
        color: #128cf0;
    }
}
@include b(del-btn) {
    color: #ff7417;
    cursor: pointer;
    &:hover {
        color: #ff7417;
    }
}
:deep(.el-tree-node__content > .el-tree-node__expand-icon) {
    order: -2;
    font-size: 20px;
    margin-left: 40px;
}
@include b(level-name1) {
    width: 289px;
}
@include b(level-name2) {
    width: 205px;
}
@include b(level-name3) {
    width: 180px;
}

.borderBox__child:hover {
    background-color: #f5f5f5;
    cursor: move;
}

.emptyLine {
    width: 100%;
    height: 80px;
    background-color: white;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: center;
    justify-content: center;
    font-size: 14px;
    color: #b3b3b3;
    border-bottom: 1px solid #ebeef5;
    border-top: 1px solid #ebeef5;
}
.avatar-uploader {
    margin: 0 30px 0 20px;
}
</style>
