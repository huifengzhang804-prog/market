<template>
    <div class="norm__list">
        <el-button type="primary" :disabled="!(!$route.query.id || $route.query.isCopy)" @click="addNewClass">新增规格</el-button>
        <drag-table :limit-pid="false" :disabled="!(!$route.query.id || $route.query.isCopy)" @change-data="changeDataCallback">
            <el-table :data="classArr" border>
                <template #empty> <ElTableEmpty /> </template>
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
                        <div class="spec-container">
                            <VueDraggableNext
                                v-if="row.children"
                                v-model="row.children"
                                :move="checkMove"
                                :disabled="!(!$route.query.id || $route.query.isCopy)"
                                group="tag"
                                class="tag-group"
                                @sort="specSortHandle"
                            >
                                <el-tag
                                    v-for="(tag, tagIndex) in row.children"
                                    :key="tagIndex"
                                    :closable="!!(!$route.query.id || $route.query.isCopy)"
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
                                    class="input-new-tag"
                                    maxlength="64"
                                    :disabled="!(!$route.query.id || $route.query.isCopy)"
                                    style="vertical-align: middle; margin-bottom: 10px"
                                    @keyup.enter="handleInputConfirm($index)"
                                    @blur="handleInputConfirm($index)"
                                >
                                </el-input>
                                <el-button
                                    v-else
                                    :disabled="!(!$route.query.id || $route.query.isCopy)"
                                    class="button-new-tag spec-container-action"
                                    round
                                    @click="showInput($index)"
                                    >添加新规格</el-button
                                >
                            </VueDraggableNext>
                        </div>
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
        </drag-table>
    </div>
</template>
<script lang="ts" setup>
import { ElMessageBox, ElMessage } from 'element-plus'
import type { PropType } from 'vue'
import { NormType } from './index'
import { Delete } from '@element-plus/icons-vue'
import dragTable from '@/components/element-plus-table-drag/drag-table.vue'

const $route = useRoute()
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
            )
        })
    })
}
useWatchTagNames($prop.classArr)

watch($prop.classArr, (newVal) => {
    useWatchTagNames(newVal)
})

const classSpac = ref($prop.classArr)
/**
 * 删除tag
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
const changeDataCallback = (data: any) => {
    $emit('changeClass', {
        type: 'sortSpecValue',
        list: data,
    })
}

const showInput = (index: number) => {
    let tempArr = $prop.classArr
    tempArr[index].inputVisble = true
    // $prop.classArr = tempArr;
    // this.$emit("changeClass", tempArr);
    useWatchTagNames($prop.classArr)
}
const checkMove = (evt: any) => {
    return !!evt.draggedContext.element?.name
}
/**
 * 添加规格值
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
    if (!validateDuplicateName(tempArr)) {
        ElMessage.warning('规格名称重复')
        tempArr[index].name = ''
    }
}
/**
 * 新增规格
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
/**
 * 删除规格
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
 * 拖拽排序
 * @param {*}
 */
const specSortHandle = () => {
    $emit('changeClass', {
        type: 'sortSpecValue',
        list: toRaw($prop.classArr),
    })
}
/**
 * 校验多规格名称重复
 * @param {NormType[]} list
 */
function validateDuplicateName(list: NormType[]) {
    const skuNamesArr = list.map((item) => item.name)
    const skuValueArr = list.reduce((cur: any[], next) => {
        return cur.concat(next.children.map((item) => item.name))
    }, [])
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
    margin-left: 10px !important;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
    margin-top: 0 !important;
}
.el-tag {
    margin-bottom: 10px;
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
