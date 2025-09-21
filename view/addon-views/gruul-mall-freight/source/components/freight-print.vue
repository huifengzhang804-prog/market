<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { doUpdatePrint, doAddPrint, doGetPrintList, doDelPrintById } from '../apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import type { ApiPrint } from '../index'
// @ts-ignore
import InfoImg from './delivery_info.png'

const dialogVisible = ref(false)
const showInfoDialog = ref(false)
const printList = ref<ApiPrint[]>([])
const rowID = ref()
const FormRef = ref()
const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
const printForm = reactive({
  printName: '',
  printCode: '',
})

const rules = {
  printCode: [
    { required: true, message: '请输入SIID', trigger: 'blur' },
    {
      validator(_, value: string, callback: any) {
        const reg = new RegExp('[\\u4E00-\\u9FFF]+', 'g')
        if (reg.test(value)) {
          callback(new Error('SIID不能包含汉字'))
        } else {
          callback()
        }
      },
    },
  ],
  printName: [{ required: true, message: '请输入打印机名称', trigger: 'blur' }],
}

initList()

async function initList() {
  const { code, data } = await doGetPrintList(pageConfig)
  if (code !== 200) ElMessage.error('列表获取失败')
  const { records, current, size, total, searchCount } = data
  printList.value = records
  pageConfig.current = current
  pageConfig.size = size
  pageConfig.total = total
}

const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initList()
}

const handleDelete = async (row: ApiPrint) => {
  await ElMessageBox.confirm('确定删除此项?', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  const { code } = await doDelPrintById(row.id)
  if (code !== 200) return ElMessage.error('删除失败')
  ElMessage.success('删除成功')
  initList()
}
const handleCloseDialog = () => {
  rowID.value = ''
  printForm.printCode = ''
  printForm.printName = ''
  FormRef.value.resetFields()
  initList()
}
const HandleFormSubmit = async () => {
  if (!rowID.value) {
    await FormRef.value.validate()
    const { printName, printCode } = printForm
    const { code } = await doAddPrint(printCode, printName)
    if (code !== 200) return ElMessage.error('新增失败')
    ElMessage.success('新增成功')
    dialogVisible.value = false
    return
  }
  const { printCode, printName } = printForm
  const { code } = await doUpdatePrint(printCode, printName, rowID.value)
  if (code !== 200) return ElMessage.error('编辑失败')
  ElMessage.success('编辑成功')
  dialogVisible.value = false
}
const handleEdit = async (row: ApiPrint) => {
  rowID.value = row.id
  printForm.printCode = row.deviceNo
  printForm.printName = row.printName
  dialogVisible.value = true
}
</script>

<template>
  <div>
    <div class="handle_container">
      <el-button round type="primary" @click="dialogVisible = true">添加打印机</el-button>
      <span style="color: #555cfd" @click="showInfoDialog = true">打印机使用说明</span>
    </div>
    <!-- 表格部分 -->
    <div class="table_container">
      <el-table
        :data="printList"
        :header-row-style="{ color: '#333' }"
        :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
        empty-text="暂无打印机"
        stripe
        style="width: 100%"
      >
        <el-table-column label="打印机名称">
          <template #default="{ row }">
            <span style="margin-left: 10px">{{ row.printName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="SIID(设备码)">
          <template #default="{ row }">
            <span style="margin-left: 10px">{{ row.deviceNo }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="right" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <PageManage
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
    <!-- 弹出层 -->
    <el-dialog v-model="dialogVisible" width="30%" @close="handleCloseDialog">
      <template #header="{ titleId, titleClass }">
        <div class="my-header">
          <h4 :id="titleId" :class="titleClass">{{ rowID ? '编辑打印机' : '新增打印机' }}</h4>
        </div>
      </template>
      <el-form ref="FormRef" :model="printForm" :rules="rules" label-position="right" label-width="130px" style="max-width: 90%">
        <el-form-item label="打印机名称" prop="printName">
          <el-input v-model.trim="printForm.printName" maxlength="20" />
        </el-form-item>
        <el-form-item label="SIID(设备码)" prop="printCode">
          <el-input v-model.trim="printForm.printCode" maxlength="32" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="HandleFormSubmit">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <el-dialog v-model="showInfoDialog" title="推荐使用【 快递100 打印机 】" :width="550">
    <el-image :src="InfoImg" style="width: 500px" />
  </el-dialog>
</template>

<style lang="scss" scoped>
.handle_container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
@include b(info) {
  margin-left: 15px;
  cursor: pointer;
}
</style>
