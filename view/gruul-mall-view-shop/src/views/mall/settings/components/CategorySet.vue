<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Ref } from 'vue'
import AddCategory from './AddCategory.vue'
import { doDelShopRelationCategory, doGetCurrentShopRelationCategory, doPostSaveShopRelationCategory } from '@/apis/mall/settings/index'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { getsettings } from '@/apis'

const tableData: Ref<any[]> = ref([])
const showAddDialog = ref(false)
const checkedSubCategoryIds = computed(() => tableData.value.map((item) => item.currentCategoryId))
const selectionSubList: Ref<any[]> = ref([])
const typeOfCommissionOptions = reactive<SettingType>({
    extractionType: '',
    drawPercentage: 0,
})

const selectionChange = (selectionList: any[]) => {
    selectionSubList.value = selectionList
}
const handleRemoveClick = (id: string) => {
    ElMessageBox.confirm('请确认是否移除？？？', '请确认')
        .then(async () => {
            const { success, msg } = await doDelShopRelationCategory([id])
            if (success) {
                ElMessage.success({ message: msg || '移除成功' })
                getCurrentShopRelationCategory()
            } else {
                ElMessage.error({ message: msg || '移除失败' })
            }
        })
        .catch(() => {})
}

const getCurrentShopRelationCategory = async () => {
    getSettingsData()
    const shopStore = useShopInfoStore()
    const res = await doGetCurrentShopRelationCategory({ shopId: shopStore.shopInfo.id })
    tableData.value = res?.data
}
interface SettingType {
    extractionType: string
    drawPercentage?: number
}
const getSettingsData = async () => {
    const res = await getsettings<SettingType>({})
    if (res?.data?.extractionType) {
        typeOfCommissionOptions.extractionType = res?.data?.extractionType
        typeOfCommissionOptions.drawPercentage = res?.data?.drawPercentage
    }
}
const handleSave = async (requestData: any[] = []) => {
    const { success, msg } = await doPostSaveShopRelationCategory(requestData.map((item) => item.currentCategoryId))
    if (success) {
        ElMessage.success({ message: msg || '保存成功' })
        getCurrentShopRelationCategory()
    } else {
        ElMessage.error({ message: msg })
    }
}
const confirmAddCategory = () => {
    const requestData = [...tableData.value, ...selectionSubList.value]
    showAddDialog.value = false
    if (requestData.length) handleSave(requestData)
}

/**
 * 渲染类目
 */
const renderRatio = (row: any) => {
    const ratio = row.customDeductionRatio ? row.customDeductionRatio : row.deductionRatio
    return ratio ? ratio + '%' : '-'
}

onMounted(() => getCurrentShopRelationCategory())
</script>
<template>
    <div style="padding: 0 16px">
        <div class="explain_container">
            <div class="explain_top">平台服务费：</div>
            <div class="explain_bottom">
                <span v-if="typeOfCommissionOptions.extractionType === 'CATEGORY_EXTRACTION'">按类目扣率提佣</span>
                <span v-else>按订单实付金额的【{{ typeOfCommissionOptions.drawPercentage }}%】提佣</span>
                <el-tooltip effect="dark" content="" placement="top-start">
                    <template #content>
                        1、所有商品必须挂载到平台类目(签约类目)下，才能正常展示和购买<br />
                        2、平台从商家每笔交易中，按商品类目扣率或订单实付金额计算提成，以保障平台正常运营
                    </template>
                    <QIcon name="icon-wenhao" style="margin-left: 5px; cursor: pointer" color="#333" />
                </el-tooltip>
            </div>
        </div>
    </div>

    <div class="handle_container">
        <el-button type="primary" @click="showAddDialog = true">添加签约类目</el-button>
    </div>
    <div class="table_container">
        <el-table
            :header-row-style="{ color: '#333', height: '48px' }"
            :header-cell-style="{ background: '#F7F8FA' }"
            :cell-style="{ fontSize: '14px', color: '#333333' }"
            :data="tableData"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="一级类目" prop="parentName" />
            <el-table-column label="二级类目" prop="currentCategoryName">
                <template #default="{ row }">
                    <div style="display: flex; align-items: center; height: 53px">
                        <img :src="row.imageUrl" alt="" style="width: 50px; height: 50px" />
                        <span style="margin-left: 5px">{{ row.currentCategoryName }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="类目扣率">
                <template #default="{ row }"
                    ><span class="category_deduction_rate">
                        {{ renderRatio(row) }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px" align="right">
                <template #default="{ row }">
                    <el-link :underline="false" type="danger" size="small" @click="handleRemoveClick(row.id)">移除</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog v-model="showAddDialog" title="添加类目" destroy-on-close>
        <AddCategory :checked-list="checkedSubCategoryIds" @selection-change="selectionChange" />
        <template #footer>
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddCategory">确定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.explain_container {
    width: 100%;
    height: fit-content;
    // background: rgba(245, 67, 25, 0.05);
    padding: 12px 14px 10px 14px;
    color: #333;
    font-size: 13px;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
}
.explain_bottom {
    color: red;
}
.category_deduction_rate {
    color: #f54319;
    font-size: 14px;
}
</style>
