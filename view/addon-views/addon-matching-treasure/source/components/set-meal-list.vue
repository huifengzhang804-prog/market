<script setup lang="ts">
import { ref, reactive, defineProps, onBeforeMount, watch, PropType, defineExpose, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { AddSerMealStatus, MealListType, DoPostSetMealQuery } from '../index'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetSetMeal, doDelSetMeal, doSetMealDel } from '../apis'
import Storage from '@/utils/Storage'

const router = useRouter()
const route = useRoute()
const $props = defineProps({
  search: {
    type: Object as PropType<any>,
    default: () => ({}),
  },
  showOffShelfBtn: {
    type: Boolean,
    default: true,
  },
})
const activeList = ref<MealListType[]>([])
const multipleTableRef = ref()
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})

const allFlag = computed(() => {
  return activeList.value.some((row) => row.setMealStatus !== 'PROCESSING')
})

watch(
  () => route.query.flag,
  (val) => {
    if (val) {
      initactiveList()
    }
  },
)

const chooseList = ref<MealListType[]>([])

// tableCheck选中
const handleSelectionChange = (selection: MealListType[]) => {
  chooseList.value = selection
}

const handleDel = async () => {
  try {
    const isValidate = await ElMessageBox.confirm('确定批量删除套餐?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const ids = chooseList.value
      .filter((item: MealListType) => item.setMealStatus !== 'PROCESSING')
      .map((item) => ({ shopId: item.shopId, setMealId: item.id }))
    if (ids.length) {
      handleDelBatch(ids as any)
    } else {
      ElMessage.warning('进行中的活动不能被删除')
    }
  } catch (error) {
    return error
  }
}
const handleAdd = () => {
  const storage = new Storage()
  if (storage.getItem('shopStore')?.shopMode === 'O2O') {
    ElMessage.warning('O2O店铺暂不支持该活动')
    return
  }
  router.push({ name: 'bundlePriceBaseinfo' })
}

async function initactiveList() {
  const { setMealStatus, keyword } = $props.search
  let searchObj = {
    setMealStatus,
    keyword,
  }
  const params = { ...pageConfig, ...searchObj }
  const { code, data } = await doGetSetMeal(params)
  if (code !== 200) return ElMessage.error('获取套餐列表失败')
  activeList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}

const handleNavToEditPage = (row: MealListType, isLookUp: 'true' | 'false' | '') => {
  router.push({
    name: 'bundlePriceBaseinfo',
    query: { setMealId: row.id, shopId: row.shopId, isLookUp },
  })
}

const handleDelClick = async (row: MealListType) => {
  if (row.setMealStatus === 'PROCESSING') {
    ElMessage.warning('进行中的活动不能被删除')
    return
  }
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该套餐?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { code } = await doDelSetMeal([{ setMealId: row.id, shopId: row.shopId }])
    if (code !== 200) {
      ElMessage.error('删除失败')
      return
    }
    ElMessage.success('删除成功')
    initactiveList()
  } catch (error) {
    return
  }
}

const handleDelBatch = async (ids: string[]) => {
  const { code } = await doDelSetMeal(ids)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initactiveList()
}

const handleSizeChange = (value: number) => {
  pageConfig.current = 1
  pageConfig.size = value
  initactiveList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initactiveList()
}

const handlePostClick = async (row: MealListType) => {
  ElMessageBox.confirm('确定下架该活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, msg } = await doSetMealDel({
      shopId: row.shopId,
      setMealId: row.id,
    })
    if (code !== 200) {
      ElMessage.error(msg)
      return
    }
    ElMessage.success('下架成功')
    initactiveList()
  })
}
const handleViewOff = async (row: MealListType) => {
  ElMessageBox.alert(row.violationExplain || '暂无违规原因', '违规原因', {
    center: true,
    showConfirmButton: false,
  })
}
onBeforeMount(() => {
  initactiveList()
})

defineExpose({ chooseList, handleDelBatch, initactiveList })
</script>

<template>
  <div class="handle_container" style="margin-top: 16px">
    <el-button type="primary" round @click="handleAdd">新增套餐</el-button>
    <el-button round :disabled="chooseList.length === 0" @click="handleDel">批量删除</el-button>
  </div>
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      :data="activeList"
      :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
      :header-row-style="{ color: '#333' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="30" :selectable="(row:DoPostSetMealQuery) => row.setMealStatus !== 'PROCESSING'" />
      <el-table-column label="套餐信息" width="260px">
        <template #default="{ row }">
          <div class="origin">
            <el-image style="width: 40px; height: 40px" :src="row.setMealMainPicture" />
            <div class="origin--name" style="width: 154px; margin-left: 5px">{{ row.setMealName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="套餐类型" width="140">
        <template #default="{ row }"> {{ row.setMealType === 'OPTIONAL_PRODUCT' ? '自选套餐' : '固定套餐' }} </template>
      </el-table-column>
      <el-table-column label="状态" width="140">
        <template #default="{ row }: { row: MealListType }">
          <span
            :style="{
              color: ['ILLEGAL_SELL_OFF'].includes(row.setMealStatus)
                ? '#F54319'
                : ['OVER', 'MERCHANT_SELL_OFF'].includes(row.setMealStatus)
                ? '#999'
                : '#333',
            }"
            >{{ AddSerMealStatus[row.setMealStatus] }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="活动时间" width="200">
        <template #default="{ row }">
          <div>起：{{ row.startTime }}</div>
          <div>止：{{ row.endTime }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动商品">
        <template #default="{ row }">{{ row.productCount }}</template>
      </el-table-column>
      <el-table-column label="支付单数" width="140">
        <template #default="{ row }"
          ><span>{{ row.orderCount || 0 }}</span></template
        >
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="right" width="170">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleNavToEditPage(row, 'true')">查看</el-button>
          <el-button v-if="['PROCESSING'].includes(row.setMealStatus)" link type="primary" @click="handlePostClick(row)">下架</el-button>
          <el-button v-if="row.setMealStatus === 'ILLEGAL_SELL_OFF'" link type="primary" @click="handleViewOff(row)">违规原因</el-button>
          <el-button v-if="row.setMealStatus !== 'PROCESSING'" link type="danger" @click="handleDelClick(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 好用的分页器 -->
  <PageManage
    :page-num="pageConfig.current"
    :load-init="true"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    @reload="initactiveList"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
</template>

<style scoped lang="scss">
@include b(origin) {
  @include flex(start);
  @include m(name) {
    @include utils-ellipsis(2);
  }
}
:deep(.el-button.is-link) {
  padding: 0;
}
:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
