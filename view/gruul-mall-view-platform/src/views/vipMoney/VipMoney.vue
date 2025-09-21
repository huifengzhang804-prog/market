<script setup lang="ts">
import uuid from '@/utils/uuid'
import { ElMessage } from 'element-plus'
import { doGetSavingManage, doGPutSavingManage, doPostSavingManage } from '@/apis/saving'
import type { FormInstance, FormRules } from 'element-plus'
import { SavingManageItem } from '@/views/vipMoney/types'

const { mulTenThousand, divTenThousand } = useConvert()

/**
 * 表单正则
 */
const rules = reactive<FormRules>({
    discountsState: [{ required: true, message: '请选择优惠方式', trigger: 'change' }],
    switching: [{ required: true, message: '请选择储值开关', trigger: 'change' }],
    // list: [{ required: true, message: '请填写完整的储值规则', trigger: 'blur' }],
})

/**
 * 初始化获取数据
 */
const submitFormData = ref({
    id: '',
    ruleJson: [{ id: uuid(10), ladderMoney: '', presentedMoney: '', presentedGrowthValue: '' }] as SavingManageItem[],
    discountsState: true,
    switching: true,
})
initSavingManage()
async function initSavingManage() {
    const { code, data } = (await doGetSavingManage()) as any
    if (code !== 200) return ElMessage.error('获取储值管理信息失败')
    if (!data.ruleJson?.length) {
        data.ruleJson = [{ id: uuid(10), ladderMoney: '', presentedMoney: '', presentedGrowthValue: '' }]
    } else {
        data.ruleJson = data.ruleJson.map((item: SavingManageItem) => {
            return {
                id: item.id,
                ladderMoney: divTenThousand(item.ladderMoney).toString(),
                presentedMoney: divTenThousand(item.presentedMoney).toString(),
                presentedGrowthValue: item.presentedGrowthValue,
            }
        })
    }
    submitFormData.value = data
}

/**
 * 保存
 */
const ruleFormRef = ref<FormInstance>()
const handleSubmit = async () => {
    if (!ruleFormRef.value) return
    try {
        await ruleFormRef.value.validate()
        if (!listValidate()) return ElMessage.error('请填写正确的储值规则')
        const { ruleJson: ruleJson, discountsState, id } = submitFormData.value
        const { code, data } = await doPostSavingManage(
            id,
            discountsState,
            ruleJson.map((item) => ({
                ladderMoney: mulTenThousand(item.ladderMoney).toString(),
                presentedMoney: mulTenThousand(item.presentedMoney).toString(),
                presentedGrowthValue: item.presentedGrowthValue,
            })),
        )
        if (code === 200) {
            ElMessage.success('修改储值管理信息成功')
            initSavingManage()
            return
        }
        ElMessage.error('修改储值管理信息失败')
    } catch (error) {
        console.log('error', error)
        return ElMessage.error('请填写正确的储值规则')
    }
}
/**
 * 数据处理
 */
function listValidate() {
    const isTopUp = submitFormData.value.ruleJson.every((item) => item.ladderMoney !== '')
    if (!submitFormData.value.discountsState) return isTopUp
    const isSendMoney = submitFormData.value.ruleJson.every((item) => item.presentedMoney !== '')
    const isSendIntegral = submitFormData.value.ruleJson.every((item) => item.presentedGrowthValue !== '')
    return isTopUp && isSendMoney && isSendIntegral ? true : false
}

/**
 * 储值开关
 */
const loading = ref(false)
const handleChangeSwitch = async (e: boolean | string | number | undefined) => {
    loading.value = true
    const { code, data } = await doGPutSavingManage(e)
    if (code === 200) {
        ElMessage.success('修改储值管理信息成功')
    } else {
        ElMessage.error('修改储值管理信息失败')
    }
    loading.value = false
}
</script>

<template>
    <div class="vip-money content_container">
        <el-form ref="ruleFormRef" :model="submitFormData" label-width="90px" label-position="left" :rules="rules">
            <el-form-item label="储值开关" prop="switching">
                <el-switch v-model="submitFormData.switching" :loading="loading" @change="handleChangeSwitch" />
                <span style="color: #999; margin-left: 15px; font-size: 12px">储值功能帮助平台快速回笼资金，充实资金链</span>
            </el-form-item>
            <template v-if="submitFormData.switching">
                <el-form-item label="是否优惠" prop="discountsState">
                    <el-radio-group v-model="submitFormData.discountsState">
                        <el-radio :value="true">赠送成长值</el-radio>
                        <el-radio :value="false">无赠送</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="储值规则" prop="list" :show-message="false">
                    <el-link
                        type="primary"
                        :disabled="submitFormData.ruleJson.length === 10"
                        @click="submitFormData.ruleJson.push({ id: uuid(10), ladderMoney: '1', presentedMoney: '0', presentedGrowthValue: '0' })"
                        >添加规则
                    </el-link>
                </el-form-item>
                <el-table
                    :data="submitFormData.ruleJson"
                    max-height="430"
                    style="width: 93%; margin-left: 90px"
                    :header-cell-style="{ background: '#F7F8FA', color: '#333333' }"
                >
                    <template #empty><ElTableEmpty /></template>
                    <el-table-column label="充值金额" prop="ladderMoney" width="285">
                        <template #default="{ row }">
                            <span class="mr15">充</span>
                            <el-input v-model.trim="row.ladderMoney" onkeyup="value=this.value.replace(/\D+/g,'')" class="width-20"> </el-input>
                            <span class="ml15">元<text v-if="submitFormData.discountsState">，送</text></span>
                        </template>
                    </el-table-column>
                    <el-table-column
                        v-if="submitFormData.discountsState"
                        prop="presentedMoney"
                        label="赠送金额"
                        :width="submitFormData.discountsState ? '248' : ''"
                    >
                        <template #default="{ row }">
                            <el-input v-model.trim="row.presentedMoney" onkeyup="value=this.value.replace(/\D+/g,'')" class="width-20"> </el-input>
                            <span class="ml15">元，送</span>
                        </template>
                    </el-table-column>
                    <el-table-column v-if="submitFormData.discountsState" prop="presentedGrowthValue" label="赠送成长值">
                        <template #default="{ row }">
                            <el-input
                                v-model.trim="row.presentedGrowthValue"
                                :disabled="!submitFormData.discountsState"
                                onkeyup="value=this.value.replace(/\D+/g,'')"
                                class="width-20"
                            ></el-input>
                            <span class="ml15">成长值</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" :width="100" fixed="right" :align="submitFormData.discountsState ? 'left' : 'center'">
                        <template #default="{ row }">
                            <el-link
                                type="danger"
                                :underline="false"
                                :disabled="submitFormData.ruleJson.length === 1"
                                @click="submitFormData.ruleJson = submitFormData.ruleJson.filter((i) => i.id !== row.id)"
                                >删除
                            </el-link>
                        </template>
                    </el-table-column>
                </el-table>
            </template>
        </el-form>
    </div>
    <div v-if="submitFormData.switching" class="operation">
        <el-button type="primary" round @click="handleSubmit">保存</el-button>
    </div>
</template>

<style scoped lang="scss">
@include b(vip-money) {
    height: calc(100% - 50px);
    @include e(title) {
        @include flex;
        justify-content: space-between;
        padding: 15px;
        background: #f4fcff;
        margin-bottom: 30px;
    }
}
@include b(stored-value) {
    display: flex;
    margin-bottom: 15px;
    &:last-child {
        margin-bottom: 0px;
    }
}
.width-20 {
    width: 175px;
}
.width-25 {
    width: 220px;
}
.mr15 {
    margin-right: 15px;
}
.ml15 {
    margin-left: 12px;
}
.operation {
    height: 52px;
    position: absolute;
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
:deep(.el-table__cell > .cell) {
    padding: 0;
}
:deep(.el-table__cell:first-child > .cell) {
    padding-left: 12px;
}
</style>
