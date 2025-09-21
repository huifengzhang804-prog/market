<script setup lang="ts">
import type { PropType } from 'vue'
import type { DeCategoryItem, DeCategoryType } from '../classification'
import { useVModel } from '@vueuse/core'
import classification from '../classification'
import { doGetCategoryLevel, doGetPlatformLevelAndComNum } from '@/apis/decoration'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import { doUpdateCategory } from '@/apis/shops'

const $props = defineProps({
    formData: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return classification
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formModel = useVModel($props, 'formData', $emit)

// 一级类目是否可选
const isFirstPossibleChoose = (ExistingCategories: any[], newCategory: DeCategoryItem, id: string) => {
    // 判断 ExistingCategories 数组中的 id 与 newCategory 数据中的 id 是否相同
    return (
        ExistingCategories.some((category) => category.platformCategoryFirstId === newCategory.platformCategoryFirstId) &&
        id !== newCategory.platformCategoryFirstId
    )
}

// 二级类目是否可选
const isSecondPossibleChoose = (ExistingCategories: any[], newCategory: DeCategoryItem, id: string) => {
    // 判断 ExistingCategories 数组中的 id 与 newCategory 数据中的 id 是否相同
    return (
        ExistingCategories.some((category) => category.platformCategorySecondId === newCategory.platformCategorySecondId) &&
        id !== newCategory.platformCategorySecondId
    )
}

// 三级类目是否可选
const isThirdPossibleChoose = (ExistingCategories: any[], newCategory: DeCategoryItem, id: string) => {
    // 判断 ExistingCategories 数组中的 id 与 newCategory 数据中的 id 是否相同
    return (
        ExistingCategories.some((category) => category.platformCategoryThirdId === newCategory.platformCategoryThirdId) &&
        id !== newCategory.platformCategoryThirdId
    )
}

watch(
    () => formModel.value.categoryList,
    (newVal) => {
        if (newVal.length <= 0) {
            // handleAdd(0)
        }
    },
    {
        immediate: true,
    },
)

// 新的折叠状态管理
const activeNames = ref<number[]>([])
const activeSecondNames = ref<{ [key: number]: number[] }>({})

// 操作按钮方法
function handleAdd(index: number) {
    // 添加一级类目
    const newCategory = {
        platformCategoryFirstId: '',
        platformCategoryFirstName: '',
        platformCategoryFirstProductNumber: 0,
        children: [
            {
                platformCategorySecondId: '',
                platformCategorySecondName: '',
                platformCategorySecondImg: '',
                platformCategorySecondOldImg: '',
                platformCategorySecondProductNumber: 0,
                platformCategorySecondChildren: [],
                children: [
                    {
                        platformCategoryThirdId: '',
                        platformCategoryThirdName: '',
                        platformCategoryThirdImg: '',
                        platformCategoryThirdOldImg: '',
                        platformCategoryThirdProductNumber: 0,
                        platformCategoryThirdChildren: [],
                        children: [],
                    },
                ],
            },
        ],
    } as unknown as DeCategoryItem
    formModel.value.categoryList.splice(index + 1, 0, newCategory)
}
function handleDel(index: number) {
    if (formModel.value.categoryList.length > 1) {
        formModel.value.categoryList.splice(index, 1)
    } else {
        ElMessage.warning('至少保留一个类目')
    }
}
function handleAddSecond(parentIndex: number, index: number) {
    // 添加二级类目
    const newSecondCategory = {
        platformCategorySecondId: '',
        platformCategorySecondName: '',
        platformCategorySecondImg: '',
        platformCategorySecondChildren: [],
        children: [],
    } as unknown as DeCategoryItem
    formModel.value.categoryList[parentIndex].children.splice(index + 1, 0, newSecondCategory)
}
function handleDelSecond(parentIndex: number, index: number) {
    if (formModel.value.categoryList[parentIndex].children.length > 1) {
        formModel.value.categoryList[parentIndex].children.splice(index, 1)
    } else {
        ElMessage.warning('至少保留一个二级类目')
    }
}

function handleAddThird(parentIndex: number, index: number, thirdIndex: number) {
    // 添加三级类目
    const newThirdCategory = {
        platformCategoryThirdId: '',
        platformCategoryThirdName: '',
        platformCategoryThirdImg: '',
        children: [],
        platformCategoryThirdChildren: [],
    } as unknown as DeCategoryItem
    formModel.value.categoryList[parentIndex].children[index].children.push(newThirdCategory)
}
function handleDelThird(parentIndex: number, index: number, thirdIndex: number) {
    if (formModel.value.categoryList[parentIndex].children[index].children.length > 1) {
        formModel.value.categoryList[parentIndex].children[index].children.splice(thirdIndex, 1)
    } else {
        ElMessage.warning('至少保留一个三级类目')
    }
}

// 修改三级类目
const changeThird = (twoitem: any, threeitem: any, e: string) => {
    threeitem.platformCategoryThirdId = e
    threeitem.platformCategoryThirdName = twoitem.platformCategoryThirdChildren?.find(
        (item: any) => item.platformCategoryThirdId === e,
    )?.platformCategoryThirdName
    threeitem.platformCategoryThirdImg = twoitem.platformCategoryThirdChildren?.find(
        (item: any) => item.platformCategoryThirdId === e,
    )?.platformCategoryThirdImg
}

const goodList = ref()
async function initList() {
    const { code, data } = await doGetPlatformLevelAndComNum({ current: 1, size: 1000 })
    if (code === 200) {
        goodList.value = data.records
    } else {
        ElMessage.error('获取商品分类失败')
    }
}
initList()
const twoChildrenList = ref()
const getChildren = async (id: string, level: string, index: number, flag: boolean) => {
    if (flag) {
        return
    }
    const { code, data } = await doGetCategoryLevel([id], level)
    if (code === 200) {
        twoChildrenList.value = (data[0].children || []).filter((val: any) => val.children && val.children.length > 0)
        formModel.value.categoryList[index].platformCategoryFirstId = data[0].id
        formModel.value.categoryList[index].platformCategoryFirstName = data[0].name
        formModel.value.categoryList[index].platformCategoryFirstProductNumber = data[0].productNumber
        formModel.value.categoryList[index].ads = data[0].ads
        formModel.value.categoryList[index].children = twoChildrenList.value.map((val: any) => {
            return {
                platformCategorySecondId: val.id,
                platformCategorySecondName: val.name,
                platformCategorySecondImg: val.categoryImg,
                platformCategorySecondOldImg: val.categoryImg,
                platformCategorySecondProductNumber: val.productNumber,
                parentId: val.parentId,
                sort: val.sort,
                platformCategoryThirdChildren: (val.children || []).map((val2: any) => {
                    return {
                        platformCategoryThirdId: val2.id,
                        platformCategoryThirdName: val2.name,
                        platformCategoryThirdImg: val2.categoryImg,
                        platformCategoryThirdOldImg: val2.categoryImg,
                    }
                }),
                children: (val.children || []).map((val2: any) => {
                    return {
                        platformCategoryThirdId: val2.id,
                        platformCategoryThirdName: val2.name,
                        platformCategoryThirdImg: val2.categoryImg,
                        platformCategoryThirdOldImg: val2.categoryImg,
                        platformCategoryThirdProductNumber: val2.productNumber,
                        parentId: val2.parentId,
                        sort: val2.sort,
                    }
                }),
            }
        })
        formModel.value.categoryList[index].platformCategorySecondChildren = twoChildrenList.value.map((val: any) => {
            return {
                platformCategorySecondId: val.id,
                platformCategorySecondName: val.name,
                platformCategorySecondImg: val.categoryImg,
                platformCategorySecondOldImg: val.categoryImg,
                platformCategoryThirdChildren: (val.children || []).map((val2: any) => {
                    return {
                        platformCategoryThirdId: val2.id,
                        platformCategoryThirdName: val2.name,
                        platformCategoryThirdImg: val2.categoryImg,
                        platformCategoryThirdOldImg: val2.categoryImg,
                    }
                }),
            }
        })
    }
}

const changeSecond = (twoitem: any, children: any, e: string) => {
    twoitem.platformCategorySecondId = e
    twoitem.platformCategorySecondName = children.find((item: any) => item.platformCategorySecondId === e)?.platformCategorySecondName
    twoitem.platformCategorySecondImg = children.find((item: any) => item.platformCategorySecondId === e)?.platformCategorySecondImg
    twoitem.platformCategoryThirdChildren = children.find((item: any) => item.platformCategorySecondId === e)?.platformCategoryThirdChildren
    twoitem.children = children.find((item: any) => item.platformCategorySecondId === e)?.platformCategoryThirdChildren
}

const handleBlur = (item: any, level: string, id: string, name: string, img: string) => {
    doUpdateCategory(item[id], {
        level: level,
        nameImgs: [{ name: item[name], categoryImg: item[img] || '', deductionRatio: item.sort || undefined }],
        parentId: item.parentId,
    })
}
</script>

<template>
    <div class="category-table-wrapper-box">
        <div class="category-table-wrapper">
            <div class="category-table-wrapper-header">
                <span style="margin: 0 120px 0 100px">关联类目</span>
                <span>展示</span>
                <span style="margin: 0 30px 0 140px">商品数</span>
                <span>操作</span>
            </div>
            <el-collapse v-model="activeNames" accordion>
                <VueDraggableNext v-model="formModel.categoryList" style="width: 100%">
                    <el-collapse-item v-for="(item, index) in formModel.categoryList" :key="'level1-' + index" :name="index" class="category-item">
                        <template #title>
                            <div class="category-header">
                                <div class="category-select">
                                    <el-select v-model="item.platformCategoryFirstId" style="width: 180px" @click.stop>
                                        <el-option
                                            v-for="country in goodList"
                                            :key="country.platformCategoryFirstId"
                                            :value="country.platformCategoryFirstId"
                                            :label="country.platformCategoryFirstName"
                                            :disabled="isFirstPossibleChoose(formModel.categoryList, country, item.platformCategoryFirstId)"
                                            @click="
                                                getChildren(
                                                    item.platformCategoryFirstId,
                                                    'LEVEL_3',
                                                    index,
                                                    isFirstPossibleChoose(formModel.categoryList, country, item.platformCategoryFirstId),
                                                )
                                            "
                                        ></el-option>
                                    </el-select>
                                    <el-input
                                        v-model="item.platformCategoryFirstName"
                                        placeholder="请输入类目名称"
                                        style="margin-left: 8px; width: 160px"
                                        maxlength="10"
                                        show-word-limit
                                        @click.stop
                                        @blur="
                                            handleBlur(
                                                item,
                                                'LEVEL_1',
                                                'platformCategoryFirstId',
                                                'platformCategoryFirstName',
                                                'platformCategoryFirstImg',
                                            )
                                        "
                                    />
                                </div>
                                <div class="category-info">
                                    <span class="count">{{ item.platformCategoryFirstProductNumber }}</span>
                                    <div class="actions">
                                        <el-button link @click.stop="handleAdd(index)">+</el-button>
                                        <el-button link @click.stop="handleDel(index)">-</el-button>
                                    </div>
                                </div>
                            </div>
                        </template>

                        <!-- 二级类目 -->
                        <div class="second-level">
                            <VueDraggableNext v-model="item.children" style="width: 100%">
                                <div v-for="(twoitem, twoIndex) in item.children" :key="'level2-' + index + '-' + twoIndex" class="second-item">
                                    <el-collapse v-model="activeSecondNames[index]" accordion>
                                        <el-collapse-item :name="twoIndex">
                                            <template #title>
                                                <div class="category-header">
                                                    <div class="category-select">
                                                        <el-image
                                                            v-if="formModel.style !== 2"
                                                            style="width: 37px; height: 37px; margin-right: 8px"
                                                            :src="twoitem.platformCategorySecondOldImg"
                                                        >
                                                            <template #error>
                                                                <div class="image-slot">
                                                                    <el-icon><icon-picture /></el-icon>
                                                                </div>
                                                            </template>
                                                        </el-image>
                                                        <el-select
                                                            v-model="twoitem.platformCategorySecondId"
                                                            style="width: 140px"
                                                            @click.stop
                                                            @change="(e) => changeSecond(twoitem, item.platformCategorySecondChildren, e)"
                                                        >
                                                            <el-option
                                                                v-for="province in item.platformCategorySecondChildren"
                                                                :key="province.platformCategorySecondId"
                                                                :label="province.platformCategorySecondName"
                                                                :value="province.platformCategorySecondId"
                                                                :disabled="
                                                                    isSecondPossibleChoose(item.children, province, twoitem.platformCategorySecondId)
                                                                "
                                                            />
                                                        </el-select>
                                                        <q-upload
                                                            v-if="formModel.style !== 2"
                                                            v-model:src="twoitem.platformCategorySecondImg"
                                                            style="margin-left: 8px; height: 37px"
                                                            :width="34"
                                                            :height="34"
                                                            @change="
                                                                handleBlur(
                                                                    twoitem,
                                                                    'LEVEL_2',
                                                                    'platformCategorySecondId',
                                                                    'platformCategorySecondName',
                                                                    'platformCategorySecondImg',
                                                                )
                                                            "
                                                        ></q-upload>
                                                        <el-input
                                                            v-model="twoitem.platformCategorySecondName"
                                                            placeholder="请输入类目名称"
                                                            style="margin-left: 8px; width: 140px"
                                                            maxlength="10"
                                                            show-word-limit
                                                            @click.stop
                                                            @blur="
                                                                handleBlur(
                                                                    twoitem,
                                                                    'LEVEL_2',
                                                                    'platformCategorySecondId',
                                                                    'platformCategorySecondName',
                                                                    'platformCategorySecondImg',
                                                                )
                                                            "
                                                        />
                                                    </div>
                                                    <div class="category-info">
                                                        <span class="count">{{ twoitem.platformCategorySecondProductNumber || 0 }}</span>
                                                        <div class="actions">
                                                            <el-button link @click.stop="handleAddSecond(index, twoIndex)">+</el-button>
                                                            <el-button link @click.stop="handleDelSecond(index, twoIndex)">-</el-button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </template>

                                            <!-- 三级类目 -->
                                            <div class="third-level">
                                                <VueDraggableNext v-model="twoitem.children" style="width: 100%">
                                                    <div
                                                        v-for="(threeitem, threeIndex) in twoitem.children"
                                                        :key="'level3-' + index + '-' + twoIndex + '-' + threeIndex"
                                                        class="third-item"
                                                    >
                                                        <div class="category-header">
                                                            <div class="category-select">
                                                                <el-image
                                                                    v-if="formModel.style !== 2"
                                                                    style="width: 37px; height: 37px; margin-right: 8px"
                                                                    :src="threeitem.platformCategoryThirdOldImg"
                                                                >
                                                                    <template #error>
                                                                        <div class="image-slot">
                                                                            <el-icon><icon-picture /></el-icon>
                                                                        </div>
                                                                    </template>
                                                                </el-image>
                                                                <el-select
                                                                    v-model="threeitem.platformCategoryThirdId"
                                                                    style="width: 134px"
                                                                    @click.stop
                                                                    @change="(e) => changeThird(twoitem, threeitem, e)"
                                                                >
                                                                    <el-option
                                                                        v-for="city in twoitem.platformCategoryThirdChildren"
                                                                        :key="city.platformCategoryThirdId"
                                                                        :value="city.platformCategoryThirdId"
                                                                        :label="city.platformCategoryThirdName"
                                                                        :disabled="
                                                                            isThirdPossibleChoose(
                                                                                twoitem.children,
                                                                                city,
                                                                                threeitem.platformCategoryThirdId,
                                                                            )
                                                                        "
                                                                    />
                                                                </el-select>
                                                                <q-upload
                                                                    v-if="formModel.style !== 2"
                                                                    v-model:src="threeitem.platformCategoryThirdImg"
                                                                    style="margin-left: 8px"
                                                                    :width="34"
                                                                    :height="34"
                                                                    @change="
                                                                        handleBlur(
                                                                            threeitem,
                                                                            'LEVEL_3',
                                                                            'platformCategoryThirdId',
                                                                            'platformCategoryThirdName',
                                                                            'platformCategoryThirdImg',
                                                                        )
                                                                    "
                                                                ></q-upload>
                                                                <el-input
                                                                    v-model="threeitem.platformCategoryThirdName"
                                                                    placeholder="请输入类目名称"
                                                                    style="margin-left: 8px; width: 134px"
                                                                    maxlength="10"
                                                                    show-word-limit
                                                                    @click.stop
                                                                    @blur="
                                                                        handleBlur(
                                                                            threeitem,
                                                                            'LEVEL_3',
                                                                            'platformCategoryThirdId',
                                                                            'platformCategoryThirdName',
                                                                            'platformCategoryThirdImg',
                                                                        )
                                                                    "
                                                                />
                                                            </div>
                                                            <div class="category-info">
                                                                <span class="count">{{ threeitem.platformCategoryThirdProductNumber || 0 }}</span>
                                                                <div class="actions">
                                                                    <el-button link @click.stop="handleAddThird(index, twoIndex, threeIndex)"
                                                                        >+</el-button
                                                                    >
                                                                    <el-button link @click.stop="handleDelThird(index, twoIndex, threeIndex)"
                                                                        >-</el-button
                                                                    >
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </VueDraggableNext>
                                            </div>
                                        </el-collapse-item>
                                    </el-collapse>
                                </div>
                            </VueDraggableNext>
                        </div>
                    </el-collapse-item>
                </VueDraggableNext>
            </el-collapse>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.category-table-wrapper-box {
    width: 100%;
    overflow-x: scroll;
    overflow-y: hidden;

    // 隐藏yzhou
}
.category-table-wrapper {
    width: 100%;
    background: #fff;
    border-radius: 6px;
    margin-top: 10px;
}

.category-table-wrapper-header {
    display: flex;
    align-items: center;
    padding: 8px 0;
    background-color: #f5f7fa;
}

.category-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 8px 0;
}
.category-item {
    padding: 3px 0;
    &:hover {
        background: #fafbfc;
    }
}
.category-select {
    display: flex;
    align-items: center;
    flex: 1;
}

.category-info {
    display: flex;
    align-items: center;
    gap: 16px;
}

.actions {
    display: flex;
    gap: 12px;
}

.count {
    font-weight: bold;
    color: #222;
    min-width: 60px;
    text-align: center;
}

.second-level {
    margin-left: 20px;
    margin-top: 8px;
}

.second-item {
    margin-bottom: 8px;
    &:hover {
        background: #fafbfc;
        border-radius: 4px;
    }
}

.third-level {
    margin-left: 20px;
    margin-top: 8px;
}

.third-item {
    margin-bottom: 8px;
    padding: 8px 5px;
    border-radius: 4px;
    margin-left: 20px;
    &:hover {
        background: #fafbfc;
    }
}

// 保持原有的输入框样式
.el-input__wrapper,
.el-select .el-input__wrapper {
    box-shadow: none !important;
    border: none !important;
    background: transparent !important;
}

.el-input__inner,
.el-select__selected-item {
    border: none !important;
    background: transparent !important;
}

// 折叠面板样式
:deep(.el-collapse-item__header) {
    background: transparent;
    border: none;
    padding: 0;
}

:deep(.el-collapse-item__content) {
    padding: 0;
    border: none;
}

:deep(.el-collapse) {
    border: none;
}

// 确保箭头图标在最前方
:deep(.el-collapse-item__arrow) {
    order: -1;
    margin-right: 8px;
    flex-shrink: 0;
    margin-left: 10px;
}

:deep(.el-collapse-item__header) {
    display: flex;
    align-items: center;
}

:deep(.el-collapse-item__header .category-header) {
    flex: 1;
    margin-left: 0;
}

:deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-secondary);
    font-size: 30px;
}
:deep(.image-slot .el-icon) {
    font-size: 15px;
}
</style>
