<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import DiscountList from '../components/discountList.vue'
import { SearchParam, AllStatus } from '../index'
import SchemaForm from '@/components/SchemaForm.vue'

const searchParam = reactive<SearchParam>({
  name: null,
  status: '',
})
// 表单配置项
const columns = [
  {
    label: '活动名称',
    prop: 'name',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入活动名称',
    },
  },
  {
    label: '活动状态',
    labelWidth: 75,
    prop: 'status',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部状态',
      },
      {
        value: 'NOT_STARTED',
        label: '未开始',
      },
      {
        value: 'IN_PROGRESS',
        label: '进行中',
      },
      {
        value: 'FINISHED',
        label: '已结束',
      },
      {
        value: 'OFF_SHELF',
        label: '已下架',
      },
      {
        value: 'VIOLATION_OFF_SHELF',
        label: '违规下架',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]
const listRef = ref()

const handleSearch = () => {
  listRef.value.initDiscountActiveList()
}

const resetHandle = () => {
  Object.keys(searchParam).forEach((key) => ((searchParam as any)[key] = ''))
  handleSearch()
}
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <SchemaForm v-model="searchParam" :columns="columns" @searchHandle="handleSearch" @handleReset="resetHandle"> </SchemaForm>
    </el-config-provider>

    <DiscountList ref="listRef" :search="searchParam" />
  </div>
</template>

<style lang="scss" scoped></style>
