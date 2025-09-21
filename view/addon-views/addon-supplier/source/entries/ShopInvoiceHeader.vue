<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  doGetInvoicePageInvoiceHeader,
  doPutDefaultInvoiceHeader,
  doPostinvoiceHeader,
  doDeleteInvoiceHeader,
  doGetInvoiceHeaderDetail,
} from '../apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { REGEX } from '@/constant'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManageTwo from '@/components/PageManage.vue'

interface ApiInvoiceHeaderAddItem {
  id: string
  ownerId: string
  ownerType: 'USER' | 'SHOP'
  type: 'PERSONAL' | 'ENTERPRISE'
  invoiceHeaderType: 'PERSONAL' | 'ENTERPRISE'
  isDefault: boolean
  header: string
  taxIdentNo: string
  openingBank: string
  bankAccountNo: string
  enterprisePhone: string
  enterpriseAddress: string
  email: string
}
const $shopInfoStore = useShopInfoStore()
const InvoiceHeadelist = ref<ApiInvoiceHeaderAddItem[]>([])
const InvoiceHeaderDialog = ref(false)
const FormRef = ref()
const InvoiceHeaderAddForm = ref({
  id: '',
  ownerType: 'SHOP',
  ownerId: $shopInfoStore.getterShopInfo.id,
  invoiceHeaderType: 'ENTERPRISE',
  header: '',
  taxIdentNo: '',
  openingBank: '',
  bankAccountNo: '',
  enterprisePhone: '',
  enterpriseAddress: '',
  email: '',
})
const rules = reactive({
  invoiceHeaderType: [{ required: true, message: '请选择抬头类型', trigger: 'blur' }],
  header: [{ required: true, message: '请输入抬头', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: REGEX.EMAIL, message: '请输入正确的邮箱', trigger: 'blur' },
  ],
  taxIdentNo: [{ required: true, message: '请输入税号', trigger: 'blur' }],
  enterprisePhone: [{ pattern: REGEX.MOBILE, message: '请输入正确的手机号', trigger: 'blur' }],
})

const pages = reactive<{ size: number; current: number; total: number }>({
  size: 10,
  current: 1,
  total: 0,
})

const handleSizeChange = (val: number) => {
  pages.current = 1
  pages.size = val
  initInvoiceHeadelist()
}

const handleCurrentChange = (val: number) => {
  pages.current = val
  initInvoiceHeadelist()
}
async function initInvoiceHeadelist() {
  const { code, data, msg } = await doGetInvoicePageInvoiceHeader(pages)
  if (code !== 200) {
    return ElMessage.error(msg || '获取发票抬头列表失败')
  }
  InvoiceHeadelist.value = data.records
  pages.current = data.current
  pages.total = data.total
}
initInvoiceHeadelist()
async function handleDefault(id: string) {
  const { code, data, msg } = await doPutDefaultInvoiceHeader({
    id,
    ownerType: 'SHOP',
    ownerId: $shopInfoStore.getterShopInfo.id,
  })
  if (code !== 200) {
    return ElMessage.error(msg || '设置默认抬头失败')
  }
  initInvoiceHeadelist()
}
const changeRadio = () => {
  InvoiceHeaderAddForm.value = {
    ...InvoiceHeaderAddForm.value,
    openingBank: '',
    bankAccountNo: '',
    enterprisePhone: '',
    enterpriseAddress: '',
  }
}
async function submitHandle() {
  FormRef.value.validate(async (valid: any) => {
    if (valid) {
      newInvoiceHeader()
    }
  })
}
async function newInvoiceHeader() {
  if (InvoiceHeaderAddForm.value.invoiceHeaderType === 'PERSONAL') {
    InvoiceHeaderAddForm.value.taxIdentNo = ''
  }
  const { code, data, msg } = await doPostinvoiceHeader(InvoiceHeaderAddForm.value)
  if (code !== 200) {
    return ElMessage.error(msg || '保存失败')
  }
  ElMessage.success('保存成功')
  initInvoiceHeadelist()
  InvoiceHeaderDialog.value = false
}
const onClose = () => {
  InvoiceHeaderDialog.value = false
  InvoiceHeaderAddForm.value = {
    ...InvoiceHeaderAddForm.value,
    id: '',
    invoiceHeaderType: 'ENTERPRISE',
    header: '',
    email: '',
    taxIdentNo: '',
    openingBank: '',
    bankAccountNo: '',
    enterprisePhone: '',
    enterpriseAddress: '',
  }
  FormRef.value.resetFields()
}
/**
 * 删除抬头
 */
const handleDelHead = (id: string) => {
  ElMessageBox.confirm('确定要删除该抬头?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, success } = await doDeleteInvoiceHeader(id)
    if (code === 200 && success) {
      ElMessage.success('删除成功')
      initInvoiceHeadelist()
    } else {
      ElMessage.error('删除失败')
    }
  })
}
/**
 * 编辑抬头
 */
const handleEdit = async (id: string) => {
  const { code, msg, data } = await doGetInvoiceHeaderDetail({ invoiceHeaderId: id, invoiceHeaderOwnerType: 'SHOP' })
  if (code !== 200) {
    return ElMessage.error(msg || '获取详情失败')
  }

  InvoiceHeaderAddForm.value = { ...InvoiceHeaderAddForm.value, ...data }
  InvoiceHeaderDialog.value = true
}
</script>

<template>
  <div class="q_plugin_container">
    <div class="head handle_container">
      <div class="head__title">您向供应商申请开票时需要使用的发票信息</div>
      <el-button type="primary" round @click="InvoiceHeaderDialog = true">新增</el-button>
    </div>
    <div class="table_container">
      <el-table
        stripe
        :data="InvoiceHeadelist"
        :header-row-style="{ color: '#333', height: '48px' }"
        :header-cell-style="{ background: '#F7F8FA' }"
        :cell-style="{ fontSize: '14px', color: '#333333' }"
      >
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column prop="contactName" label="抬头">
          <template #default="{ row }">
            <div class="flex">
              <div v-if="row.isDefault" style="color: rgba(24, 144, 255, 1)">默认</div>
              <div>{{ row.header }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="taxIdentNo" label="税号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="invoiceHeaderType" label="抬头类型" width="100">
          <template #default="{ row }">
            {{ row.invoiceHeaderType === 'PERSONAL' ? '个人' : '企业' }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="right" width="250">
          <template #default="{ row }">
            <div class="right_btn">
              <el-button v-if="!row.isDefault" type="primary" size="small" link @click="handleDefault(row.id)">设为默认</el-button>
              <el-button type="primary" size="small" link @click="handleEdit(row.id)">编辑</el-button>
              <el-button type="danger" size="small" link @click="handleDelHead(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <PageManageTwo
      :page-size="pages.size"
      :page-num="pages.current"
      :total="pages.total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />

    <!-- 弹出层 -->
    <el-dialog v-model="InvoiceHeaderDialog" title="发票抬头" width="650px" center @close="onClose">
      <el-form ref="FormRef" label-position="right" label-width="90px" :model="InvoiceHeaderAddForm" :rules="rules" style="max-width: 100%">
        <el-form-item label="抬头类型" prop="invoiceHeaderType">
          <el-radio-group v-model="InvoiceHeaderAddForm.invoiceHeaderType" @change="changeRadio">
            <el-radio value="ENTERPRISE">企业</el-radio>
            <el-radio value="PERSONAL">个人 或 事业单位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发票抬头" prop="header" :required="true">
          <el-input v-model="InvoiceHeaderAddForm.header" maxlength="32" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email" :required="true">
          <el-input v-model="InvoiceHeaderAddForm.email" placeholder="请正确填写邮箱接收电子发票" />
        </el-form-item>
        <template v-if="InvoiceHeaderAddForm.invoiceHeaderType === 'ENTERPRISE'">
          <el-form-item label="税号" prop="taxIdentNo" :required="true">
            <el-input v-model="InvoiceHeaderAddForm.taxIdentNo" maxlength="20" />
          </el-form-item>
          <el-form-item label="开户行" prop="openingBank">
            <el-input v-model="InvoiceHeaderAddForm.openingBank" maxlength="255" />
          </el-form-item>
          <el-form-item label="银行账号" prop="bankAccountNo">
            <el-input v-model="InvoiceHeaderAddForm.bankAccountNo" maxlength="19" />
          </el-form-item>
          <el-form-item label="企业电话" prop="enterprisePhone">
            <el-input v-model="InvoiceHeaderAddForm.enterprisePhone" maxlength="11" onkeyup="value=value.replace(/[^\d]/g,'')" />
          </el-form-item>
          <el-form-item label="企业地址" prop="enterpriseAddress">
            <el-input v-model="InvoiceHeaderAddForm.enterpriseAddress" maxlength="255" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="InvoiceHeaderDialog = false">取消</el-button>
          <el-button type="primary" @click="submitHandle">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(head) {
  padding: 0 30px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  @include e(title) {
    color: #bebebe;
    font-size: 14px;
  }
}
.flex {
  display: flex;
}
</style>
