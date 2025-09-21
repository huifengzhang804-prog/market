<script lang="ts" setup>
import { REBATE_USERS } from '../index'
import useRebateSet from './hooks/useRebateSet'
import { onMounted, reactive, ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import SettingInstructionsDialog from './settingInstructions-dialog.vue'

const { currentMemberRebates, initialRebates, baseRebateConfig, onSubmit } = useRebateSet()
const dialogVisible = ref(false)
const rules = reactive({
  rebateStatus: [{ required: true, message: '', trigger: 'blur' }],
  rebateUsers: [{ required: true, message: '', trigger: 'blur' }],
})
onMounted(() => initialRebates())
</script>

<template>
  <div style="width: 100%; height: calc(100vh - 200px); overflow-y: auto">
    <el-form :model="baseRebateConfig" label-width="90" style="position: relative; padding-top: 30px" :rules="rules">
      <el-form-item label="消费返利" prop="rebateStatus">
        <el-switch
          v-model="baseRebateConfig.rebateStatus"
          :active-value="true"
          :inactive-value="false"
          class="ml-2"
          style="--el-switch-on-color: #13ce66"
        />
        <QIcon name="icon-wenhao" size="18px" color="#666" style="margin-left: 10px; cursor: pointer" @click="dialogVisible = true" />
      </el-form-item>
      <template v-if="baseRebateConfig.rebateStatus">
        <el-form-item label="返利用户" prop="rebateUsers">
          <div style="display: flex; justify-content: space-between; align-items: center; width: 80%">
            <el-radio-group v-model="baseRebateConfig.rebateUsers">
              <el-radio :value="REBATE_USERS.PAID_MEMBER">付费会员</el-radio>
              <el-radio :value="REBATE_USERS.ALL_MEMBERS">所有会员（含免费会员）</el-radio>
            </el-radio-group>
          </div>
        </el-form-item>
        <div style="padding: 0 16px">
          <el-table
            :cell-style="{ fontSize: '12px', color: '#333333' }"
            :data="currentMemberRebates"
            :header-cell-style="{ background: '#f6f8fa' }"
            :header-row-style="{ fontSize: '14px', color: '#333' }"
            max-height="calc(100vh - 350px)"
          >
            <el-table-column label="等级" width="140">
              <template #default="{ row }: { row: (typeof currentMemberRebates.value)[0] }">
                <div class="name" style="color: #ce732f">
                  {{ (row.memberType === 'PAID_MEMBER' ? 'SVIP' : 'vip') + row.rankCode }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="会员名称" width="200">
              <template #default="{ row }: { row: (typeof currentMemberRebates.value)[0] }">
                <div class="name">{{ row.memberName }}</div>
              </template>
            </el-table-column>
            <el-table-column label="返利百分比(0~100%)">
              <template #default="{ row }: { row: (typeof currentMemberRebates.value)[0] }">
                <el-input-number v-model="row.rebatePercentage" :controls="false" :max="100" :min="0" :precision="2" />
                <!-- <div class="name">{{ row.shopName }}</div> -->
              </template>
            </el-table-column>
            <el-table-column label="返利支付百分比(0~90%)">
              <template #default="{ row }: { row: (typeof currentMemberRebates.value)[0] }">
                <el-input-number v-model="row.rebatePaymentPercentage" :controls="false" :max="90" :min="0" :precision="2" />
                <!-- <div class="name">{{ row.fullReductionName }}</div> -->
              </template>
            </el-table-column>
            <el-table-column label="提现门槛">
              <template #default="{ row }: { row: (typeof currentMemberRebates.value)[0] }">
                <el-input-number v-model="row.withdrawalThreshold" :precision="2" :min="0" :max="100000" :controls="false" />
                <!-- <div class="name">{{ row.fullReductionName }}</div> -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </el-form>
  </div>
  <div class="submit"><el-button type="primary" @click="onSubmit">保存</el-button></div>
  <el-dialog v-model="dialogVisible" title="消费返利说明" width="970px" center>
    <SettingInstructionsDialog />
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(submit) {
  margin-top: auto;
  width: 100%;
  height: 55px;
  text-align: center;
  align-content: center;
  box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
</style>
