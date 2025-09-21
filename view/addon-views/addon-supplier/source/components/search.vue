<script lang="ts" setup>
import { defineEmits, reactive } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'
import { useRouter } from 'vue-router'

const $router = useRouter()
const $emit = defineEmits(['searchParams'])

defineProps({
  isAddShop: {
    type: Boolean,
    default: false,
  },
})

type SearchFormType = {
  no: string
  name: string
  status: string
  shopType: string
  extractionType: string
  auditUserPhone?: string | undefined
  applyUserPhone?: string | undefined
  settledStartTime?: Date | undefined
  settledEndTime?: Date | undefined
  applyStartTime?: Date | undefined
  applyEndTime?: Date | undefined
  auditStartTime?: Date | undefined
  auditEndTime?: Date | undefined
}
const searchForm = reactive<SearchFormType>({
  no: '',
  name: '',
  status: '',
  shopType: '',
  extractionType: '',
  auditUserPhone: undefined,
  applyUserPhone: undefined,
  settledStartTime: undefined,
  settledEndTime: undefined,
  applyStartTime: undefined,
  applyEndTime: undefined,
  auditStartTime: undefined,
  auditEndTime: undefined,
})

// 供应商列表搜索配置
const supplierListColumns = [
  {
    label: '供应商ID',
    labelWidth: 75,
    prop: 'no',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写供应商ID',
      maxlength: 20,
    },
  },
  {
    label: '供应商名称',
    labelWidth: 90,
    prop: 'name',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写供应商名称',
      maxlength: 20,
    },
  },
  {
    label: '状态',
    labelWidth: 45,
    prop: 'status',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部',
      },
      {
        value: 'NORMAL',
        label: '启用',
      },
      {
        value: 'FORBIDDEN',
        label: '禁用',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '提佣类型',
    prop: 'extractionType',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部',
      },
      {
        value: 'CATEGORY_EXTRACTION',
        label: '类目提佣',
      },
      {
        value: 'ORDER_SALES_EXTRACTION',
        label: '订单金额提佣',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '供应商类型',
    labelWidth: 90,
    prop: 'shopType',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部',
      },
      {
        value: 'SELF_OWNED',
        label: '自营',
      },
      {
        value: 'PREFERRED',
        label: '优选',
      },
      {
        value: 'ORDINARY',
        label: '普通',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '入驻时间',
    prop: 'data',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
      onChange: (data: Array<Date>) => {
        searchForm.settledStartTime = data ? data[0] : undefined
        searchForm.settledEndTime = data ? data[1] : undefined
      },
    },
  },
]

// 供应商审核搜索
const supplierExaminecolumns = [
  {
    label: '供应商ID',
    labelWidth: 75,
    prop: 'no',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写供应商ID',
      maxlength: 20,
    },
  },
  {
    label: '供应商名称',
    labelWidth: 90,
    prop: 'name',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写供应商名称',
      maxlength: 20,
    },
  },
  {
    label: '申请人手机',
    labelWidth: 85,
    prop: 'applyUserPhone',
    valueType: 'input', // 改为input类型
    fieldProps: {
      placeholder: '请填写申请人手机',
      maxlength: 11,
      type: 'text', // 使用text类型配合v-model.number
      'v-model.number': '', // 只允许输入数字
      oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
      pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
    },
  },
  {
    label: '审核员手机',
    labelWidth: 85,
    prop: 'auditUserPhone',
    valueType: 'input', // 改为input类型
    fieldProps: {
      placeholder: '请填写审核员手机',
      maxlength: 11,
      type: 'text', // 使用text类型配合v-model.number
      'v-model.number': '', // 只允许输入数字
      oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
      pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
    },
  },
  {
    label: '申请时间',
    prop: 'applyOfData',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
      onChange: (data: Array<Date>) => {
        searchForm.applyStartTime = data ? data[0] : undefined
        searchForm.applyEndTime = data ? data[1] : undefined
      },
    },
  },
  {
    label: '审批时间',
    prop: 'data',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
      onChange: (data: Array<Date>) => {
        searchForm.settledStartTime = data ? data[0] : undefined
        searchForm.settledEndTime = data ? data[1] : undefined
      },
    },
  },
]

const searchHandle = () => {
  $emit('searchParams', searchForm)
}
const handleReset = () => {
  Object.keys(searchForm).forEach((key) => ((searchForm as any)[key] = ''))
  searchHandle()
}

const navToNewShop = () => {
  $router.push({
    name: 'addsupplier',
  })
}
</script>

<template>
  <div>
    <SchemaForm
      v-model="searchForm"
      :columns="isAddShop ? supplierListColumns : supplierExaminecolumns"
      @searchHandle="searchHandle"
      @handleReset="handleReset"
    >
      <template v-if="isAddShop" #otherOperations>
        <el-button class="mr-20" round type="primary" @click="navToNewShop">添加供应商</el-button>
      </template>
    </SchemaForm>
  </div>
</template>

<style lang="scss" scoped>
.shop__search-visible {
  padding: 20px 20px 0 20px;
}
</style>
