<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetLogisticsList, delLogisticsById, setLogisticsDddress, doLogisticsSetDef } from '../apis'
import type { ApiAddressList, logisticsFormType, newLogisticsFormType } from '../index'
import GD_regionData from '@/assets/map.json'

let tableData = ref<ApiAddressList>({
  current: 1,
  records: [],
  size: 10,
  total: 0,
})
// tabber状态
const addressDialog = ref(false)
const regionDatas = GD_regionData // 定义地区数据

// addressDialog表单数据
let logisticsForm = ref<logisticsFormType>({
  address: '',
  contactName: '',
  contactPhone: '',
  zipCode: '',
  Provinces: [],
  defSend: false,
  defReceive: false,
})
const RowID = ref('')
const FormRef = ref()
const rules = reactive({
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { max: 10, message: '最多输入10个字符', trigger: 'blur' },
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3|5|6|7|8|9]\d{9}$/, message: '请输入正确的号码格式', trigger: 'blur' },
  ],
  Provinces: [{ type: 'array', required: true, message: '请选择类别', trigger: 'change' }],
  // 验证未写
  address: [{ required: true, message: '请填写详细地址', trigger: 'blur' }],
})

loadGetLogisticsList()

/**
 * 获取物流地址列表
 */
async function loadGetLogisticsList() {
  const { current, size, total } = tableData.value
  const { data, code } = await doGetLogisticsList({ current, size, total })
  if (code === 200) {
    tableData.value = data
  } else {
    ElMessage({ message: '请刷新重试...', type: 'warning' })
  }
}

const handleSizeChange = (value: number) => {
  tableData.value.size = value
  loadGetLogisticsList()
}
const handleCurrentChange = (value: number) => {
  tableData.value.current = value
  loadGetLogisticsList()
}
/**
 * 地址管理编辑
 */
const handleEdit = (row: any) => {
  RowID.value = row.id
  for (const key in logisticsForm.value) {
    if (['defSend', 'defReceive'].includes(key)) {
      ;(logisticsForm.value as any)[key] = row[key] === 'YES'
    } else {
      ;(logisticsForm.value as any)[key] = row[key]
    }
  }
  logisticsForm.value.Provinces = getRegionCode(row.area)
  addressDialog.value = true //打开弹窗
}

/**
 * 根据省市区的label[]获取省市区的value[]
 */
const getRegionCode = (regionList: string[]) => {
  const codeList = []
  const province = regionDatas.find((item) => item.label === regionList[0])
  if (province) {
    codeList.push(province.value)
    if (province.children) {
      const city = province.children.find((it) => it.label === regionList[1])
      if (city) {
        codeList.push(city.value)
        if (city.children) {
          const region: any = city.children.find((i) => i.label === regionList[2])
          if (region) {
            codeList.push(region.value)
          }
        }
      }
    }
  }
  return codeList
}

const handleDelete = (id: string) => {
  ElMessageBox.confirm('确定删除此项?删除后不会保留已删除地址！', {
    title: '提示',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      if (action === 'cancel') {
        return
      }
      await loadDelLogisticsById(id) // 调用删除接口
      await loadGetLogisticsList() // 重新拉取数据
    },
  })
}
/**
 * 发货地址处理
 */
const handleShipments = async (id: string) => {
  const { code } = await doLogisticsSetDef(id, 'DEF_SEND')
  if (code === 200) {
    ElMessage.success('设置成功')
    await loadGetLogisticsList() // 重新拉取数据
  } else {
    ElMessage.error({
      message: '设置失败',
    })
  }
}
/**
 * 收货地址
 */
const handleRecede = async (id: string) => {
  const { code } = await doLogisticsSetDef(id, 'DEF_RECEIVE')
  if (code === 200) {
    await loadGetLogisticsList() // 重新拉取数据
    ElMessage.success('设置成功')
  } else {
    ElMessage.error({
      message: '设置失败',
    })
  }
}
/**
 * 表单提交请求
 */
const submitHandle = async () => {
  await FormRef.value.validate()
  await loadSetLogisticsDddress()
}

/**
 * 通过id删除物流
 */
const loadDelLogisticsById = async (id: string) => {
  const { code, msg } = await delLogisticsById(id)
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '删除成功',
    })
  } else {
    ElMessage.error(msg || '删除失败')
  }
}
/**
 * 新增或者编辑地址列表
 */
const loadSetLogisticsDddress = async () => {
  const newLogisticsForm: newLogisticsFormType = { ...logisticsForm.value }
  newLogisticsForm.area = regionCascaderRef.value.getCheckedNodes()[0].pathLabels
  Reflect.deleteProperty(newLogisticsForm, 'Provinces')
  newLogisticsForm.defReceive = newLogisticsForm.defReceive ? 'YES' : 'NO'
  newLogisticsForm.defSend = newLogisticsForm.defSend ? 'YES' : 'NO'
  if (RowID.value) {
    const { code } = await setLogisticsDddress({ ...newLogisticsForm, id: RowID.value })
    if (code === 200) {
      ElMessage.success('更新成功')
      await loadGetLogisticsList()
      addressDialog.value = false
    }
  } else {
    const { code } = await setLogisticsDddress({ ...newLogisticsForm })
    if (code === 200) {
      ElMessage.success('增加成功')
      await loadGetLogisticsList()
      addressDialog.value = false
    }
  }
}
//
/**
 * 关闭窗口的回调 移除表单验证 初始化数据
 */
const onClose = () => {
  addressDialog.value = false
  RowID.value = ''
  FormRef.value.resetFields()
  logisticsForm.value = {
    Provinces: [],
    address: '',
    contactName: '',
    contactPhone: '',
    zipCode: '',
    defReceive: false,
    defSend: false,
  }
}
const addressTypeComputed = computed(() => (row: any) => {
  const addressTypeStack: string[] = []
  if (row?.defSend === 'YES') addressTypeStack.push('发货地址')
  if (row?.defReceive === 'YES') addressTypeStack.push('收货地址')
  return addressTypeStack
})

const regionCascaderRef = ref()
</script>

<template>
  <div class="handle_container">
    <el-button round type="primary" @click="addressDialog = true">添加地址 </el-button>
  </div>
  <!-- 表格部分 -->
  <div class="freight-add_container__table_border table_container" style="width: 100%">
    <el-table
      :cell-style="{ height: '80px' }"
      :data="tableData.records"
      :header-row-style="{ color: '#333' }"
      :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
      class="freight-add_container__table"
      stripe
      style="width: 100%"
    >
      <el-table-column label="联系人" prop="contactName" />
      <el-table-column label="联系电话" prop="contactPhone" />
      <el-table-column label="地址" prop="address" width="350">
        <template #default="{ row }">
          <span class="name">{{ row.area.join('') }}{{ row.address }}</span>
        </template>
      </el-table-column>
      <el-table-column label="邮政编码" prop="zipCode" />
      <el-table-column label="地址类型(默认)" prop="address" width="150">
        <template #default="{ row }">
          {{ addressTypeComputed(row)?.join(' ') }}
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="right" min-width="80">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <PageManage
    :page-num="tableData.current"
    :page-size="tableData.size"
    :total="tableData.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  ></PageManage>
  <!-- 弹出层 -->
  <el-dialog v-model="addressDialog" width="650px" @close="onClose">
    <template #header="{ titleId, titleClass }">
      <div class="my-header">
        <h4 :id="titleId" :class="titleClass">{{ RowID ? '编辑地址' : '添加地址' }}</h4>
      </div>
    </template>
    <el-form ref="FormRef" :model="logisticsForm" :rules="rules" label-position="right" label-width="90px" style="max-width: 100%">
      <el-row :gutter="8">
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="logisticsForm.contactName" maxlength="10" placeholder="请输入联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="logisticsForm.contactPhone" maxlength="11" onkeyup="value=value.replace(/[^\d]/g,'')" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="地区选择" prop="Provinces">
            <el-cascader
              ref="regionCascaderRef"
              v-model="logisticsForm.Provinces"
              :options="regionDatas"
              :style="{ width: '100%' }"
              filterable
              placeholder="请选择省/市/区"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="详细地址" prop="address">
            <el-input v-model="logisticsForm.address" maxlength="50" placeholder="" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮政编号" prop="zipCode">
            <el-input v-model="logisticsForm.zipCode" maxlength="6" onkeyup="value=value.replace(/[^\d]/g,'')" placeholder="请输入邮政编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="地址类型">
            <el-checkbox v-model="logisticsForm.defSend">发货地址</el-checkbox>
            <el-checkbox v-model="logisticsForm.defReceive">收货地址</el-checkbox>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addressDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">提交</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(name) {
  @include utils-ellipsis(2);
}
</style>
