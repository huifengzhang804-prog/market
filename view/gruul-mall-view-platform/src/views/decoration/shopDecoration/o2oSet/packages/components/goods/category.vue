<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import { useVModel } from '@vueuse/core'
import defaultGoodData from './goods'
import { doGetHighestCategoryLevel } from '@/apis/good'
type CategoryItem = {
    productNum: number
    name: string
}
const $props = defineProps({
    dialogShow: {
        type: Boolean,
        default: false,
    },
    subForm: {
        type: Object as PropType<typeof defaultGoodData>,
        default() {
            return defaultGoodData
        },
    },
})
const $emit = defineEmits(['update:dialogShow', 'choose'])
const syncDialogShow = useVModel($props, 'dialogShow', $emit)
//TODO: 勾选商品 类型待完善
const chooseItem = ref<CategoryItem[]>([])
// 最后已选择数据
const emitList = ref([])
const goodList = ref([])
const multipleTable = ref()
const pageConfig = shallowReactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
watch(syncDialogShow, (newVal) => {
    if (newVal) {
        initList()
    }
})

initList()

/**
 * 勾选商品
 * @param {*} item
 */
const handleGoodIdsChange = (item: CategoryItem[]) => {
    chooseItem.value = item
}
const handleSubmit = () => {
    $emit('choose', toRaw(chooseItem.value))
    syncDialogShow.value = false
}
const handleChangeSize = (e: number) => {
    pageConfig.pageNum = 1
    pageConfig.pageSize = e
    initList()
}
const handleChangeCur = (e: number) => {
    pageConfig.pageNum = e
    initList()
}
async function initList() {
    const { pageSize, pageNum, total } = toRefs(pageConfig)
    const { code, data } = await doGetHighestCategoryLevel({ current: pageNum.value, size: pageSize.value })
    if (code === 200) {
        goodList.value = data.records
        total.value = data.total
    } else {
        ElMessage.error('获取商品分类失败')
    }
}
</script>

<template>
    <el-dialog
        v-model="syncDialogShow"
        title="选择展示分类"
        append-to-body
        top="10vh"
        lock-scroll
        :close-on-click-modal="false"
        width="480px"
        class="select__category-dialog"
    >
        <div class="page--top row-wrap">
            <el-table ref="multipleTable" :data="goodList" style="width: 100%" max-height="350" @selection-change="handleGoodIdsChange">
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column type="selection" width="55"> </el-table-column>
                <el-table-column prop="name" label="分类名称"> </el-table-column>
                <el-table-column prop="productNum" label="商品数量" width="140"> </el-table-column>
            </el-table>
            <page-manage
                :total="pageConfig.total"
                :page-num="pageConfig.pageNum"
                :page-size="pageConfig.pageSize"
                @handle-size-change="handleChangeSize"
                @handle-current-change="handleChangeCur"
            />
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="syncDialogShow = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
:deep(.el-pagination) {
    float: right;
}

.row-wrap {
    padding: 0 20px;
    position: relative;
    &-btn {
        height: 32px;
        border: 1px solid #000;
    }
    &-input {
        width: 200px;
        height: 33px;
        position: absolute;
        top: 0;
        right: 24px;
        :deep(.el-input__inner) {
            outline: none;
            border: 1px solid #000;
        }
    }
    &-selected {
        height: 40px;
        line-height: 40px;
        color: rgb(199, 22, 22);
        text-align: right;
        user-select: none;
    }
}
.page--top {
    .pro__list {
        width: 100%;
        &__tr2 {
            height: 62px;
            line-height: 62px;
            border-top: 1px solid #f2f2f2;
            &__td {
                position: relative;
                padding-left: 20px;
                &__check {
                    position: absolute;
                    top: -4px;
                    left: 0px;
                }
            }
            td {
                width: 210px;
            }
        }
        &__tr1 {
            background: #f2f2f2;
            height: 40px;
            line-height: 40px;
            td {
                width: 210px;
            }
        }
    }
}
</style>
<style lang="scss">
.select__category-dialog {
    .search__category-input {
        float: right;
        width: 160px;
    }

    .el-dialog__body {
        padding: 15px 20px;
    }
}
</style>
