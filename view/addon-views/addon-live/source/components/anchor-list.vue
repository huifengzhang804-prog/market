<script lang="ts" setup>
import { type Ref, ref, reactive } from 'vue'
import AnchorSearch from './anchor-search.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import ViolationReason from './violation-reason.vue'
import AddAnchor from './add-anchor.vue'
import { ElMessage } from 'element-plus'
import { doGetAnchorList, putAnchorStaus, postAddAnchor } from '../apis'

const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const addAnchorRef: Ref<InstanceType<typeof AddAnchor> | null> = ref(null)
const activeTab = ref<'' | 'NORMAL' | 'FORBIDDEN' | 'VIOLATION'>('')
const tabs = [
  { name: '', label: '全部' },
  { name: 'NORMAL', label: '启用' },
  { name: 'FORBIDDEN', label: '禁用' },
  { name: 'VIOLATION', label: '违规禁播' },
]
const showAddDialog = ref(false)
const anchorList = ref([])
const showViolationReasonDialog = ref(false)
const reasonparams = ref({
  id: '',
})
const searchObj = ref({
  id: '', // 主播id
  anchorNickname: '', // 主播昵称
  phone: '', // 手机号
})

initAnchorList()

async function initAnchorList() {
  const params = { ...pageConfig, ...searchObj.value, status: activeTab.value }
  const { code, data } = await doGetAnchorList(params)
  if (code !== 200) return ElMessage.error('获取直播间列表失败')
  anchorList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}

/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initAnchorList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initAnchorList()
}
const handleSearch = (e) => {
  searchObj.value = e
  pageConfig.current = 1
  initAnchorList()
}
const handleChangeTab = () => {
  pageConfig.current = 1
  initAnchorList()
}
const changeswitch = async (id: string, staus: string) => {
  const isEnable = !(staus === 'NORMAL')
  const { code, data } = await putAnchorStaus(id, isEnable)
  if (code !== 200) return ElMessage.error(isEnable ? '启用主播失败' : '禁用主播失败')
  initAnchorList()
}
const previewReason = (id: string) => {
  showViolationReasonDialog.value = true
  reasonparams.value.id = id
}
const handleConfirmAnchor = async () => {
  const formModel = await addAnchorRef.value?.getAnchorModel()
  showAddDialog.value = true
  const { code } = await postAddAnchor(formModel)
  if (code !== 200) return
  initAnchorList()
  showAddDialog.value = false
}
</script>

<template>
  <AnchorSearch @on-search-params="handleSearch" />
  <div class="tab_container">
    <el-tabs v-model="activeTab" @tab-change="handleChangeTab">
      <el-tab-pane v-for="tab in tabs" :key="tab.name" :label="tab.label" :name="tab.name" />
    </el-tabs>
  </div>

  <div class="handle_container">
    <el-button type="primary" @click="showAddDialog = true">新增主播</el-button>
  </div>
  <q-table :data="anchorList" no-border class="table">
    <q-table-column label="主播信息" width="410">
      <template #default="{ row }">
        <div class="table-info">
          <img :src="row.anchorIcon" />
          <div class="table-info__wrapper">
            <div class="table-info__wrapper--name">
              <span>{{ row.anchorNickname }}</span>
              <span>({{ row.phone }})</span>
            </div>
            <div class="table-info__wrapper--description">主播ID：{{ row.id }}</div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column align="left" label="主播简介" width="520">
      <template #default="{ row }">
        <el-tooltip v-if="row.anchorSynopsis.length >= 40" class="box-item" effect="dark" :content="row.anchorSynopsis" placement="top">
          <div class="table-info__wrapper--description ellipsis2">{{ row.anchorSynopsis }}</div>
        </el-tooltip>
        <div v-else class="table-info__wrapper--description ellipsis2">{{ row.anchorSynopsis }}</div>
      </template>
    </q-table-column>
    <q-table-column align="left" label="状态" width="120">
      <template #default="{ row }">
        <span v-if="row.status === 'NORMAL'" style="color: #00bb2c">启用中</span>
        <span v-else-if="row.status === 'FORBIDDEN'" style="color: #999">已禁用</span>
        <span v-else-if="row.status === 'VIOLATION'" style="color: #f54319">违规禁播</span>
      </template>
    </q-table-column>
    <q-table-column label="操作" prop="action" fixed="right" width="100">
      <template #default="{ row }">
        <div v-if="row.status !== 'VIOLATION'">
          <el-button v-if="row.status === 'NORMAL'" type="primary" text @click="changeswitch(row.id, 'NORMAL')">关闭</el-button>
          <el-button v-if="row.status === 'FORBIDDEN'" type="primary" text @click="changeswitch(row.id, 'FORBIDDEN')">开启</el-button>
        </div>
        <el-button v-else type="primary" text @click="previewReason(row.id)">禁播原因</el-button>
      </template>
    </q-table-column>
  </q-table>

  <PageManage
    :page-num="pageConfig.current"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="showViolationReasonDialog" title="违禁原因" width="550px">
    <violation-reason :id="reasonparams.id" :type="'ANCHOR'" />
  </el-dialog>
  <el-dialog v-model="showAddDialog" :close-on-click-modal="false" destroy-on-close title="新增主播" width="550px">
    <add-anchor ref="addAnchorRef" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAnchor">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.table {
  overflow-y: scroll;
}
* {
  font-size: 14px;
}
@include b(table-info) {
  display: flex;
  width: 350px;
  overflow: hidden;
  padding: 8px 0;

  img {
    width: 40px;
    height: 40px;
    flex-shrink: 0;
  }

  @include e(wrapper) {
    margin-left: 15px;
    overflow: hidden;
    height: 40px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;

    @include m(name) {
      span + span {
        margin-left: 5px;
      }
    }
  }
}

@include b(table-performance) {
  width: 100%;
  line-height: 22px;
}

.ellipsis2 {
  width: 420px;
  @include utils-ellipsis(2);
}

.ellipsis {
  @include utils-ellipsis;
}
</style>
