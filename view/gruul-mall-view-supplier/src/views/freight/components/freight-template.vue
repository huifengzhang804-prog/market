<script setup lang="ts">
import { ref, provide } from 'vue'
import FreightTemplateDialog from '@views/freight/components/freight-template-dialog.vue'
import FreightTemplateTable from '@views/freight/components/freight-template-table.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import PageManage from '@/components/PageManage/index.vue'
import { doGetLogisticsTemplateList, doDelLogisticsTemplate } from '@/apis/freight/freight-template'
import type { ApiFreightTemplate } from '@views/freight/components/types'

const flagDialog = ref(false)
const tableData = ref<ApiFreightTemplate[]>([])
const pageConfig = ref({
    size: 20,
    current: 1,
})
const query = useRoute().query
const $router = useRouter()
const pageConfigTotal = ref(0)
const currentId = ref('')

initLogisticsTemplateList()

async function initLogisticsTemplateList() {
    const { code, data } = await doGetLogisticsTemplateList(pageConfig.value.current, pageConfig.value.size)
    if (code !== 200) return ElMessage.error('获取物流列表失败')
    tableData.value = data.records
    pageConfigTotal.value = data.total
}
const handleSizeChange = (value: number) => {
    pageConfig.value.size = value
    initLogisticsTemplateList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.value.current = value
    initLogisticsTemplateList()
}
const handleAddTemplate = () => {
    currentId.value = ''
    flagDialog.value = true
}
const handleEditTemplate = (id: string) => {
    currentId.value = id
    flagDialog.value = true
}
/**
 * @description: 删除模板
 * @param {*} id
 */
const handleDelTemplate = (id: string) => {
    ElMessageBox.confirm('确定要删除此模板吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const { code, data, msg } = await doDelLogisticsTemplate(id)
            if (code !== 200) return ElMessage.error(msg || '删除失败')
            ElMessage.success('删除成功')
            initLogisticsTemplateList()
        })
        .catch(() => {})
}
provide('parentLogisticsTemplateList', initLogisticsTemplateList)
</script>

<template>
    <div class="handle_container">
        <el-button v-if="query.from" round @click="$router.back()">返回发布商品</el-button>
        <el-button type="primary" round @click="handleAddTemplate">新增运费模板</el-button>
    </div>

    <div class="table_container">
        <FreightTemplateTable :table-data="tableData" @handle-edit-template="handleEditTemplate" @handle-del-template="handleDelTemplate" />
    </div>
    <PageManage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfigTotal"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <FreightTemplateDialog v-if="flagDialog" :id="currentId" v-model:is-show="flagDialog" @close="initLogisticsTemplateList" />
</template>

<style lang="scss" scoped>
@include b(btn) {
    font-size: 12px;
    margin-bottom: 10px;
}
</style>
