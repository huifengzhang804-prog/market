<script lang="ts" setup>
import { ref, reactive } from 'vue'
import RoomSearch from './room-search.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import ViolationReason from './violation-reason.vue'
import { ElMessage } from 'element-plus'
import { doGetRoomList } from '../apis'

const roomList = ref([])
const activeTab = ref('')
const searchObj = ref({
  liveTitle: '', // 直播主题
  anchorNickname: '', // 主播昵称
  createTime: '', // 直播时间范围
  liveId: '', // 直播ID
  phone: '', // 手机号
  beginTime: '', //开始时间
  endTime: '', //结束时间
})
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const showViolationReasonDialog = ref(false)
const tabs = [
  { name: '', label: '全部' },
  { name: 'PROCESSING', label: '直播中' },
  { name: 'NOT_STARTED', label: '预告' },
  { name: 'OVER', label: '已结束' },
  { name: 'ILLEGAL_SELL_OFF', label: '违规下播' },
]
const reasonparams = ref({
  id: '',
})

initRoomList()

async function initRoomList() {
  if (Array.isArray(searchObj.value.createTime)) {
    searchObj.value.beginTime = searchObj.value.createTime[0]
    searchObj.value.endTime = searchObj.value.createTime[1]
    searchObj.value.createTime = ''
  }
  const params = { ...pageConfig, ...searchObj.value, status: activeTab.value }
  const { code, data } = await doGetRoomList(params)
  if (code !== 200) return ElMessage.error('获取直播间列表失败')
  roomList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}

/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initRoomList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initRoomList()
}
const handleSearch = (e) => {
  searchObj.value = e
  pageConfig.current = 1
  initRoomList()
}
const handleChangeTab = () => {
  pageConfig.current = 1
  initRoomList()
}

const previewReason = (id: string) => {
  showViolationReasonDialog.value = true
  reasonparams.value.id = id
}
</script>

<template>
  <RoomSearch @on-search-params="handleSearch" />
  <div class="tab_container">
    <el-tabs v-model="activeTab" @tab-change="handleChangeTab">
      <el-tab-pane v-for="tab in tabs" :key="tab.name" :label="tab.label" :name="tab.name" />
    </el-tabs>
  </div>
  <q-table :data="roomList" class="table" no-border>
    <q-table-column label="直播信息" width="410">
      <template #default="{ row }">
        <div class="table-info">
          <img :src="row.pic" />
          <div class="table-info__wrapper">
            <div class="table-info__wrapper--name">
              <span v-if="row.status === 'PROCESSING'" class="label in-live">直播中</span>
              <span v-else-if="row.status === 'NOT_STARTED'" class="label foretell">预告</span>
              <span v-else-if="row.status === 'OVER'" class="label finish">已结束</span>
              <span v-else-if="row.status === 'ILLEGAL_SELL_OFF'" class="label violation">违规下播</span>
              <el-tooltip v-if="row.liveTitle.length >= 15" class="box-item" effect="dark" :content="row.liveTitle" placement="top">
                <span class="ellipsis" style="display: inline-block; width: 180px; line-height: 14px">{{ row.liveTitle }}</span>
              </el-tooltip>
              <span v-else class="ellipsis" style="display: inline-block; width: 180px; line-height: 14px">{{ row.liveTitle }}</span>
            </div>
            <div class="table-info__wrapper--description">直播ID：{{ row.id }}</div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="主播简介" width="310">
      <template #default="{ row }">
        <div class="ellipsis2">{{ row.liveSynopsis }}</div>
      </template>
    </q-table-column>
    <q-table-column label="主播昵称" width="180">
      <template #default="{ row }">
        <div class="table-anchor">
          <div class="table-anchor__info">{{ row.anchor.anchorNickname }}</div>
          <div class="table-anchor__info">{{ row.anchor.phone }}</div>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="操作" prop="action" align="right" fixed="right" width="120">
      <template #default="{ row }">
        <!-- <el-link type="primary">运营</el-link> -->
        <el-link v-if="row.status === 'ILLEGAL_SELL_OFF'" type="primary" @click="previewReason(row.id)">违禁原因 </el-link>
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
    <violation-reason :id="reasonparams.id" :type="'LIVE'" />
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
.table {
  overflow: scroll;
}

@include b(table-info) {
  display: flex;
  align-items: center;
  width: 350px;
  overflow: hidden;
  img {
    width: 56px;
    height: 56px;
    flex-shrink: 0;
  }
  @include e(wrapper) {
    margin-left: 15px;
    overflow: hidden;
    flex: 1;
    @include m(name) {
      display: flex;
      align-items: center;
      .label {
        display: inline-block;
        line-height: 22px;
        padding: 0 3px;
        border-radius: 4px;
        color: #fff;
        margin-right: 5px;
        font-size: 12px;

        &.in-live {
          background-color: #555cfd;
        }

        &.foretell {
          background-color: #00bb2c;
        }

        &.finish {
          background-color: #c7c7c7;
        }

        &.violation {
          background-color: #f54319;
        }
      }
    }
  }
}

@include b(table-anchor) {
  line-height: 22px;
}

@include b(table-performance) {
  width: 100%;
  line-height: 22px;
}

.ellipsis {
  @include utils-ellipsis;
}
.ellipsis2 {
  width: 270px;
  @include utils-ellipsis(2);
}

.el-link + .el-link {
  margin-left: 5px;
}
</style>
