<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetFreeMemberList, doPostAvailableMember, doPostFreeMemberSetLabel } from '@/apis/member'
import type { Ref } from 'vue'
import EditMember from './EditMember.vue'
import SetLabel from './set-label/index.vue'

const { divHundred } = useConvert()
// 分享弹窗
const memberList = ref([])
const currentMemberDialogConfig: { id: string; currentMemberLevel: number | undefined } = reactive({
    id: '',
    currentMemberLevel: undefined,
})
const showDialog = ref(false)
const editMemberRef: Ref<InstanceType<typeof EditMember> | null> = ref(null)

initStodioList()

const handleDelClick = async (row: any) => {
    ElMessageBox.confirm('确定需要删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, msg } = await doPostAvailableMember(row.id)
        if (code === 200) {
            ElMessage.success('删除成功')
            initStodioList()
        } else {
            ElMessage.error(msg || '删除失败')
        }
    })
}
const handleAddLevel = () => {
    showDialog.value = true
    currentMemberDialogConfig.currentMemberLevel = memberList.value.length + 1
}
const handleNavToEdit = (id: string, level: number) => {
    currentMemberDialogConfig.id = id
    currentMemberDialogConfig.currentMemberLevel = level
    showDialog.value = true
}
async function initStodioList() {
    const { code, data } = await doGetFreeMemberList()
    if (code !== 200) return ElMessage.error('获取免费会员失败')
    memberList.value = data
}
const handleCloseDialog = () => {
    showDialog.value = false
    currentMemberDialogConfig.id = ''
    currentMemberDialogConfig.currentMemberLevel = undefined
}
const handleConfirm = async () => {
    const check = await editMemberRef?.value?.handleSubmit()
    if (!check) {
        return
    }
    handleCloseDialog()
    initStodioList()
}

const setLabelRef = ref<InstanceType<typeof SetLabel> | null>(null)
const labelSetInfo = reactive({
    showLabelDialog: false,
    currentSetLabelForm: {
        id: '',
        name: '',
        fontColor: '',
        labelColor: '',
        priceLabelName: '',
        priceFontColor: '',
        priceLabelColor: '',
    },
})
const handleLabelSet = (row: any) => {
    Object.keys(labelSetInfo.currentSetLabelForm).forEach((key) => {
        if (row?.labelJson?.[key]) {
            // @ts-ignore
            labelSetInfo.currentSetLabelForm[key] = row?.labelJson?.[key]
        }
    })
    labelSetInfo.currentSetLabelForm.id = row?.id
    labelSetInfo.currentSetLabelForm.name = row?.freeMemberName
    labelSetInfo.showLabelDialog = true
}
const handleCloseSetLabelDialog = () => {
    Object.keys(labelSetInfo.currentSetLabelForm).forEach((key) => {
        // @ts-ignore
        labelSetInfo.currentSetLabelForm[key] = ''
    })
}
const handleConfirmSetLabel = async () => {
    const setLabelData = await setLabelRef.value?.getFormModel()
    const { code, msg } = await doPostFreeMemberSetLabel(setLabelData)
    if (code === 200) {
        ElMessage.success({ message: msg || '标签设置成功' })
        initStodioList()
        labelSetInfo.showLabelDialog = false
    } else {
        ElMessage.error({ message: msg || '标签设置失败' })
    }
}
</script>
<template>
    <div class="handle_container">
        <el-button round type="primary" :disabled="memberList.length >= 10 ? true : false" @click="handleAddLevel">添加等级</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="memberList"
            :header-row-style="{ fontSize: '14px' }"
            :header-cell-style="{ background: '#f6f8fa', color: '#333333', height: '48px' }"
            :cell-style="{ fontSize: '14px', color: '#666' }"
        >
            <template #empty><ElTableEmpty /></template>
            <el-table-column label="会员等级" align="left" width="160">
                <template #default="{ $index }">
                    <div class="level">vip{{ $index + 1 }}</div>
                </template>
            </el-table-column>
            <el-table-column label="免费会员名称" align="left" width="220">
                <template #default="{ row }">
                    <span>{{ row.freeMemberName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="升级条件" align="left" width="200">
                <template #default="{ row }">
                    <!-- <template #default="{ row, $index }"> -->
                    <!-- <div v-if="$index === 0">注册信息</div> -->
                    <!-- <div>获取{{ row.needValue }}成长值</div> -->
                    <div>{{ row.needValue ? row.needValue : '无' }}<span v-if="row.needValue">成长值</span></div>
                </template>
            </el-table-column>
            <el-table-column label="会员权益" align="left">
                <template #default="{ row }">
                    <div class="interests">
                        <div v-for="item in row.relevancyRightsList" :key="item.id">
                            <span v-if="item.rightsType === 'GOODS_DISCOUNT'">商品折扣{{ divHundred(item.extendValue) }}折</span>
                            <span v-else-if="item.rightsType === 'INTEGRAL_MULTIPLE'">积分{{ divHundred(item.extendValue) }}倍</span>
                            <span v-else>{{ item.rightsName }}</span>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="180" align="right">
                <template #default="{ row, $index }">
                    <div style="display: flex; justify-content: end">
                        <div :style="{ marginRight: $index < 1 ? '39px' : '' }">
                            <el-link
                                :underline="false"
                                style="margin-right: 12px"
                                type="primary"
                                size="small"
                                @click="handleNavToEdit(row.id, $index + 1)"
                            >
                                编辑
                            </el-link>
                            <el-link :underline="false" type="primary" size="small" @click="handleLabelSet(row)">标签设置</el-link>
                        </div>
                        <el-link
                            v-show="$index > 0"
                            style="margin-left: 12px"
                            :underline="false"
                            type="danger"
                            size="small"
                            @click="handleDelClick(row)"
                        >
                            删除
                        </el-link>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog v-model="showDialog" :title="currentMemberDialogConfig.id ? '编辑会员' : '添加会员'" destroy-on-close @close="handleCloseDialog">
        <edit-member ref="editMemberRef" :member-id="currentMemberDialogConfig.id" :member-level="currentMemberDialogConfig.currentMemberLevel" />
        <template #footer>
            <el-button @click="handleCloseDialog">取 消</el-button>
            <el-button type="primary" @click="handleConfirm">保 存</el-button>
        </template>
    </el-dialog>
    <el-dialog v-model="labelSetInfo.showLabelDialog" title="标签设置" destroy-on-close @close="handleCloseSetLabelDialog">
        <set-label ref="setLabelRef" :label-info="labelSetInfo.currentSetLabelForm" />
        <template #footer>
            <el-button @click="labelSetInfo.showLabelDialog = false">取 消</el-button>
            <el-button type="primary" @click="handleConfirmSetLabel">保 存</el-button>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(level) {
    font-size: 14px;
    line-height: 43px;
    color: #999;
}
@include b(interests) {
    display: flex;
    font-size: 12px;
    color: #333333;
    div::after {
        content: ';';
    }
    div:last-child::after {
        content: '';
    }
}
</style>
