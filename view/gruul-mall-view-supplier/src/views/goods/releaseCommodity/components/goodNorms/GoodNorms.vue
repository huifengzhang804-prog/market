<template>
    <div class="norm__list">
        <el-button type="primary" :disabled="!(!$route.query.id || $route.query.isCopy)" @click="addNewClass">新增规格</el-button>
        <DragTable
            :limit-pid="false"
            :disabled="!(!$route.query.id || $route.query.isCopy)"
            @change-data="(data: any) => $emit('changeClass', { type: 'sortSpecValue', list: data })"
        >
            <el-table :data="classArr" border>
                <el-table-column label="规格名称" prop="name" width="160" fixed="left">
                    <template #default="{ row, $index }">
                        <el-input
                            v-model="row.name"
                            :disabled="!(!$route.query.id || $route.query.isCopy)"
                            placeholder="规格名称"
                            style="width: 130px"
                            maxlength="20"
                            @blur="handleChangeSpec($index)"
                        ></el-input>
                    </template>
                </el-table-column>
                <el-table-column label="规格值(拖拽可调整顺序)" prop="children" min-width="720">
                    <template #default="{ row, $index }">
                        <!-- <div class="norm__item__value__content"> -->
                        <VueDraggableNext
                            v-if="row.children"
                            v-model="row.children"
                            :move="checkMove"
                            :disabled="!(!$route.query.id || $route.query.isCopy)"
                            group="tag"
                            @sort="specSortHandle"
                        >
                            <el-tag
                                v-for="(tag, tagIndex) in row.children"
                                :key="tagIndex"
                                :closable="!$route.query.id || !!$route.query.isCopy"
                                effect="plain"
                                size="large"
                                type="info"
                                :disable-transitions="false"
                                @close="handleClose(tag.name, $index, tagIndex)"
                            >
                                <el-input
                                    v-model="tag.name"
                                    :disabled="!(!$route.query.id || $route.query.isCopy)"
                                    class="input-old-tag"
                                    maxlength="64"
                                ></el-input>
                            </el-tag>
                            <el-input
                                v-if="row.inputVisble"
                                ref="saveTagInput"
                                v-model="row.inputValue"
                                :disabled="!(!$route.query.id || $route.query.isCopy)"
                                class="input-new-tag"
                                maxlength="64"
                                @keyup.enter="handleInputConfirm($index)"
                                @blur="handleInputConfirm($index)"
                            >
                            </el-input>
                            <el-button
                                v-else
                                class="button-new-tag"
                                :disabled="!(!$route.query.id || $route.query.isCopy)"
                                round
                                @click="showInput($index)"
                                >添加新规格</el-button
                            >
                        </VueDraggableNext>
                        <!-- </div> -->
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="{ $index }">
                        <el-icon size="22px" color="#FF4D4D" :disabled="!(!$route.query.id || $route.query.isCopy)" @click="delNormHandle($index)"
                            ><Delete
                        /></el-icon>
                    </template>
                </el-table-column>
            </el-table>
        </DragTable>
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive, toRaw } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import type { PropType } from 'vue'
import { NormType } from './index'
import { Delete } from '@element-plus/icons-vue'
import DragTable from '@/components/element-extends/element-plus-table-drag/drag-table.vue'

const $prop = defineProps({
    classArr: {
        type: Array as PropType<NormType[]>,
        default() {
            return []
        },
    },
})
const $emit = defineEmits(['changeClass', 'update-child-name'])
const normRules = reactive({
    name: [{ required: true, message: '请输入规格名称', trigger: 'blur' }],
})

function useWatchTagNames(classArr: NormType[]) {
    classArr.forEach((row, rowIndex) => {
        row.children.forEach((tag, tagIndex) => {
            watch(
                () => tag.name,
                (newVal, oldVal) => {
                    $emit('update-child-name', newVal, oldVal)
                },
                {
                    deep: true,
                },
            )
        })
    })
}

watch(
    $prop.classArr,
    (newVal) => {
        useWatchTagNames(newVal)
    },
    {
        deep: true,
    },
)

const checkMove = (evt: any) => {
    return !!evt.draggedContext.element?.name
}
/**
 * @description: 删除tag
 * @param {number} tag
 * @param {number} index
 * @param {number} tagIndex
 */
const handleClose = (tag: string, index: number, tagIndex: number) => {
    let tempArr = $prop.classArr
    if (tempArr[index].children[tagIndex].name.indexOf(tag) === 0) {
        tempArr[index].children.splice(tagIndex, 1)
    }
    $emit('changeClass', { type: 'delSpecValue', list: toRaw(tempArr) })
}

const showInput = (index: number) => {
    let tempArr = $prop.classArr
    tempArr[index].inputVisble = true
}
/**
 * @description: 添加规格值
 * @param {number} index
 */
const handleInputConfirm = (index: number) => {
    let tempArr = $prop.classArr
    if (tempArr[index].inputValue) {
        tempArr[index].children.push({
            name: tempArr[index].inputValue,
            children: [],
            inputValue: '',
            inputVisble: false,
        })
    }
    tempArr[index].inputVisble = false
    tempArr[index].inputValue = ''
    if (validateDuplicateName(tempArr)) {
        $emit('changeClass', { type: 'addSpecValue', list: toRaw(tempArr) })
    } else {
        tempArr[index].children.pop()
        ElMessage.warning('规格名称重复')
    }
}
const handleChangeSpec = (index: number) => {
    let tempArr = $prop.classArr
    $emit('changeClass', { type: 'addSpecValue', list: toRaw(tempArr) })
    if (!validateDuplicateName(tempArr)) {
        ElMessage.warning('规格名称重复')
        tempArr[index].name = ''
    }
}
/**
 * @description: 新增规格
 */
const addNewClass = () => {
    if (!$prop.classArr.every((item) => !!item.name.trim())) {
        ElMessage.warning('请输入规格名称')
        return
    }
    let tempArr = $prop.classArr
    tempArr.push({
        inputValue: '',
        inputVisble: false,
        children: [],
        name: '',
        version: '',
    })
    $emit('changeClass', { type: 'addSpec', list: toRaw($prop.classArr) })
}
const $route = useRoute()
/**
 * @description: 删除规格
 * @param {number} index
 */
const delNormHandle = (index: number) => {
    if (!(!$route.query.id || $route.query.isCopy)) return
    ElMessageBox.confirm('确认删除该规格?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        let tempArr = $prop.classArr
        tempArr.splice(index, 1)
        $emit('changeClass', { type: 'delSpec', list: toRaw(tempArr) })
        ElMessage.success('删除成功')
    })
}
/**
 * @description: 拖拽排序
 * @param {*}
 */
const specSortHandle = () => {
    $emit('changeClass', {
        type: 'sortSpecValue',
        list: toRaw($prop.classArr),
    })
}
/**
 * @description: 校验多规格名称重复
 * @param {NormType[]} list
 */
function validateDuplicateName(list: NormType[]) {
    const skuNamesArr = list.map((item) => item.name)
    const skuValueArr = list.reduce((cur, next) => {
        return cur.concat(next.children.map((item) => item.name))
    }, [] as any[])
    return skuNamesArr.concat(skuValueArr).length === Array.from(new Set(skuNamesArr.concat(skuValueArr))).length
}
</script>
<style lang="scss" scoped>
.norm__list {
    padding-bottom: 30px;
}
.el-row {
    padding: 15px;
    background-color: #f8f8f8;
}
.norm__item__name--left {
    display: flex;
}
.norm__item__name--left > .el-form-item--small.el-form-item {
    margin: 0 !important;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.el-tag {
    padding: 0px;
    :deep() {
        .el-tag__close {
            margin-right: 7px;
        }
    }
}
.el-tag + .el-tag {
    margin-left: 10px;
}

.norm__item__value__content,
.el-form-item {
    margin: 0 9px;
}
.norm__item__value__content {
    justify-content: start;
}
.el-button {
    margin: 10px 9px;
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
}
.input-old-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
    margin: 0;
    padding: 0;
    height: 28px;
    :deep() {
        .el-input__wrapper {
            box-shadow: none;
        }
    }
}
</style>
