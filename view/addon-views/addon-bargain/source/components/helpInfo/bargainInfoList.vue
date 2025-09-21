<script setup lang="ts">
import { doGetBargainHelpPeople } from '../../apis'
import type { BargainHelpPeopleList } from '../../index'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import { reactive, ref } from 'vue'

const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const helpPeopleList = ref<BargainHelpPeopleList[]>([])
const emit = defineEmits(['reload'])
const { divTenThousand } = useConvert()
async function getBargainHelpPeople(param: { activityId: string; bargainOrderId: string }) {
  const { code, data, msg } = await doGetBargainHelpPeople({ ...param, ...pageConfig })
  if (code !== 200) {
    ElMessage.error(msg || '查询砍价帮砍列表失败')
    return
  }
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
  helpPeopleList.value = data.records
}
defineExpose({ getBargainHelpPeople: getBargainHelpPeople })
</script>

<template>
  <el-table
    ref="multipleTableRef"
    :data="helpPeopleList"
    :header-row-style="{ fontSize: '12px', color: '#909399' }"
    :header-cell-style="{ background: '#f6f8fa' }"
    :cell-style="{ fontSize: '12px', color: '#333333' }"
  >
    <el-table-column label="帮砍人信息">
      <template #default="{ row }: { row: BargainHelpPeopleList }">
        <div class="bargain_origin">
          <el-image style="width: 40px; height: 40px" fit="" :src="row.userHeadPortrait" />
          <div class="bargain_origin--name" style="width: 200px; margin-left: 10px">
            {{ row.userNickName }}
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="帮砍金额">
      <template #default="{ row }: { row: BargainHelpPeopleList }">
        <span>{{ row.helpCutAmount && divTenThousand(row.helpCutAmount) }}</span>
      </template>
    </el-table-column>
    <el-table-column label="帮砍时间" width="200px" align="center">
      <template #default="{ row }: { row: BargainHelpPeopleList }">
        <time>{{ row.helpCutTime }}</time>
      </template>
    </el-table-column>
  </el-table>
  <div style="margin-top: auto">
    <el-row justify="end" align="middle">
      <!-- 好用的分页器 -->
      <page-manage v-model="pageConfig" :page-size="pageConfig.size" :total="pageConfig.total" @reload="emit('reload')" />
    </el-row>
  </div>
</template>

<style scoped lang="scss">
@include b(bargain_origin) {
  @include flex;
  justify-content: flex-start;
  @include m(name) {
    width: 120px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  @include m(shop_name) {
    @include utils-ellipsis(2);
  }
}
</style>
