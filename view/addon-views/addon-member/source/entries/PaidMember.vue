<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetPaidMemberList, doDelPaidMember, doPostPaidMemberSetLabel } from '../apis'
import type { ApiPaidMemberItem, ApiPaidMemberRule, EFFECTIVEDURATIONTYPE } from '../index'
import { Ref, ref, reactive } from 'vue'
import useConvert from '@/composables/useConvert'
import EditPayingMembers from '../components/EditPayingMembers.vue'
import SetLabel from '../components/SetMemberLabel.vue'

const { divTenThousand, divHundred } = useConvert()
const memberList = ref<ApiPaidMemberItem[]>([])
const currentMemberDialogConfig: { id: string; currentMemberLevel: number | undefined } = reactive({
  id: '',
  currentMemberLevel: undefined,
})
const showDialog = ref(false)
const editMemberRef: Ref<InstanceType<typeof EditPayingMembers> | null> = ref(null)

initPaidMemberList()

const handleDelClick = async (id: string) => {
  ElMessageBox.confirm('确定需要删除?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, msg } = await doDelPaidMember(id)
    if (code === 200) {
      ElMessage.success('删除成功')
      initPaidMemberList()
    } else {
      ElMessage.error(msg || '删除失败')
    }
  })
}
const handleAddLevel = () => {
  currentMemberDialogConfig.id = ''
  currentMemberDialogConfig.currentMemberLevel = memberList.value.length + 1
  showDialog.value = true
}
const handleNavToEdit = (id: string, level: number) => {
  currentMemberDialogConfig.id = id
  currentMemberDialogConfig.currentMemberLevel = level
  showDialog.value = true
}

const handleCloseDialog = () => {
  showDialog.value = false
  currentMemberDialogConfig.id = ''
  currentMemberDialogConfig.currentMemberLevel = undefined
}
const handleConfirm = async () => {
  await editMemberRef?.value?.handleSubmit()
  handleCloseDialog()
  initPaidMemberList()
}

async function initPaidMemberList() {
  const { code, data, msg } = await doGetPaidMemberList()
  if (code === 200) {
    memberList.value = data
  } else {
    ElMessage.error(msg ? msg : '获取列表失败')
  }
}

function convertPrice(params: ApiPaidMemberRule) {
  const matchPrice = {
    ONE_MONTH: '1个月',
    THREE_MONTH: '3个月',
    TWELVE_MONTH: '12个月',
    THREE_YEAR: '3年',
    FIVE_YEAR: '5年',
  } as unknown as { [K in EFFECTIVEDURATIONTYPE]: string }
  return `${matchPrice[params.effectiveDurationType]}${divTenThousand(params.price)}`
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
  labelSetInfo.currentSetLabelForm.name = row?.paidMemberName
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
  const { code, msg } = await doPostPaidMemberSetLabel(setLabelData)
  if (code === 200) {
    ElMessage.success({ message: msg || '标签设置成功' })
    initPaidMemberList()
    labelSetInfo.showLabelDialog = false
  } else {
    ElMessage.error({ message: msg || '标签设置失败' })
  }
}
</script>
<template>
  <div style="overflow-y: scroll; padding: 0 16px">
    <el-button round style="margin-bottom: 15px" type="primary" :disabled="memberList.length >= 5 ? true : false" @click="handleAddLevel"
      >添加等级</el-button
    >
    <el-table
      ref="multipleTableRef"
      :data="memberList"
      :header-row-style="{ fontSize: '14px' }"
      :header-cell-style="{ background: '#f6f8fa', color: '#333333', height: '48px' }"
      :cell-style="{ fontSize: '14px', color: '#666' }"
    >
      <el-table-column label="会员等级" width="160">
        <template #default="{ $index }">
          <div class="level">SVIP{{ $index + 1 }}</div>
        </template>
      </el-table-column>
      <el-table-column label="付费会员名称" width="220">
        <template #default="{ row }">
          <span>{{ row.paidMemberName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="定价" width="320">
        <template #default="{ row }">
          <div class="pricing">
            <div v-for="item in row.paidRuleJson" :key="item.id">{{ convertPrice(item) }}元</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="会员权益">
        <template #default="{ row }">
          <div v-for="item in row.relevancyRightsList" v-show="row.relevancyRightsList.length" :key="item.id" class="interests">
            <div v-if="item.rightsType === 'GOODS_DISCOUNT'">商品折扣{{ divHundred(item.extendValue) }}折</div>
            <div v-else-if="item.rightsType === 'INTEGRAL_MULTIPLE'">积分{{ divHundred(item.extendValue) }}倍</div>
            <div v-else>{{ item.rightsName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="right" fixed="right" label="操作" width="180">
        <template #default="{ row, $index }: { row: any, $index: number }">
          <el-link :underline="false" size="small" style="margin-right: 12px" type="primary" @click="handleNavToEdit(row.id, $index + 1)">
            编辑
          </el-link>
          <el-link :underline="false" type="primary" size="small" @click="handleLabelSet(row)">标签设置</el-link>
          <el-link style="margin-left: 12px" :underline="false" size="small" type="danger" @click="handleDelClick(row.id)"> 删除 </el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="showDialog" :title="currentMemberDialogConfig.id ? '编辑会员' : '添加会员'" destroy-on-close @close="handleCloseDialog">
      <edit-paying-members
        ref="editMemberRef"
        :member-id="currentMemberDialogConfig.id"
        :member-level="currentMemberDialogConfig.currentMemberLevel"
      />
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
  </div>
</template>

<style lang="scss" scoped>
@include b(level) {
  line-height: 43px;
  color: #999;
  line-height: 63px;
}

@include b(pricing) {
  @include utils-ellipsis(2);
  display: flex;
  flex-wrap: wrap;
  div::after {
    content: '、';
  }
  div:last-child::after {
    content: '';
  }
}

@include b(interests) {
  @include utils-ellipsis(1);
}
</style>
