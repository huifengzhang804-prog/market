<script setup lang="ts">
import type { PropType } from 'vue'
import PageManage from '@/components/PageManage.vue'
import { typeNameMap } from '../linkSelectItem'
import { useVModel } from '@vueuse/core'
import type { LinkSelectItem } from '../linkSelectItem'

const $props = defineProps({
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
    visible: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:link'])
const linkSelectItem = useVModel($props, 'link', $emit)
const name = 'classification'
const selectId = ref(0)
const loading = ref(false)
const tableData = ref([])
const pageConfig = shallowReactive({
    pageNum: 1,
    pageSize: 20,
    total: 0,
})
const searchName = ref('')
const list = ref([])
watch($props, () => {
    getRegionList()
})
defineExpose({
    selectId,
})

onMounted(() => {
    getRegionList()
})

const handleRefresh = () => {
    getRegionList()
}
const handleSearch = () => {
    getRegionList()
}
const handleSizeChange = (val: number) => {
    pageConfig.pageNum = 1
    pageConfig.pageSize = val
    getRegionList()
}

const handleCurrentChange = (val: number) => {
    pageConfig.pageNum = val
    getRegionList()
}
const handleSelect = () => {
    const currentItem = tableData.value.find((item: LinkSelectItem) => item.id === String(selectId.value))
    Object.assign(linkSelectItem.value, currentItem)
}
const sameJudge = (): boolean => {
    return (
        typeNameMap[linkSelectItem.value.type as keyof typeof typeNameMap] &&
        typeNameMap[linkSelectItem.value.type as keyof typeof typeNameMap].name === name
    )
}
// TODO:差商品专区接口
async function getRegionList() {
    //    const param = {
    //   modeName: this.searchName,
    //   current: this.pageNum,
    //   size: this.pageSize,
    // };
    // const res = await getRegionList(param);
    // this.list = res.data.list;
    // this.total = res.data.total;
    // this.tableData = this.list.map(item => {
    //   return {
    //     id: Number(item.id),
    //     type: 2,
    //     name: item.modeName,
    //     url: "/pages/index/index",
    //     append: "mall",
    //   };
    // });
    // // 初始相同设为选中状态
    // if (this.sameJudge()) {
    //   this.selectId = this.linkSelectItem.id;
    // }
}
</script>

<template>
    <div>
        <div class="search-wrap">
            <el-button @click="handleRefresh">刷新</el-button>
            <div class="search-wrap-input">
                <el-input v-model="searchName" placeholder="请输入关键词" maxlength="100">
                    <template #append>
                        <el-button icon="el-icon-search" @click="handleSearch"></el-button>
                    </template>
                </el-input>
            </div>
        </div>
        <el-table v-loading="loading" :data="tableData" height="369">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="专区名称" prop="name"></el-table-column>
            <el-table-column label="操作" width="100px">
                <template #default="scope">
                    <el-radio v-model="selectId" :label="scope.row.id" @change="handleSelect">
                        <span></span>
                    </el-radio>
                </template>
            </el-table-column>
        </el-table>
        <page-manage
            :page-size="pageConfig.pageSize"
            :page-num="pageConfig.pageNum"
            :total="pageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
</template>

<style lang="scss" scoped>
.search-wrap {
    display: flex;
    justify-content: space-between;
    justify-items: center;
}
.search-wrap-input {
    width: 180px;
}
</style>
