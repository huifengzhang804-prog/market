<script lang="ts" setup>
import { ref, reactive } from 'vue'
import RoomSearch from './room-search.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import ViolationReason from '../violation-reason.vue'
import { ElMessage } from 'element-plus'
import prohibitLive from '../prohibitLive.vue'
import { doGetRoomList, putAnchorStatusPlatform } from '../../../apis'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'

const roomList = ref([])
const activeTab = ref('')
const searchObj = ref({
  liveTitle: '', // 直播主题
  anchorNickname: '', // 主播昵称
  createTime: '', // 直播时间范围
  liveId: '', // 直播ID
  phone: '', // 手机号
  shopName: '', //店铺名称
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
const showprohibitparams = ref({
  id: '',
})
const showprohibitDialog = ref(false)
const prohibitLiveRef = ref()

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
const addReason = (id: string) => {
  showprohibitDialog.value = true
  showprohibitparams.value.id = id
}
const handleConfirmAnchor = async () => {
  const formModel = await prohibitLiveRef.value?.getprohibitModel()
  const { code, data } = await putAnchorStatusPlatform({
    ...formModel,
    type: 'LIVE',
    isEnable: false,
    sourceId: showprohibitparams.value.id,
  })
  if (code !== 200) return ElMessage.error('违规下播失败')
  initRoomList()
  showprohibitDialog.value = false
}
</script>

<template>
  <room-search @on-search-params="handleSearch" />
  <el-tabs v-model="activeTab" class="tab_container" @tab-change="handleChangeTab">
    <el-tab-pane v-for="tab in tabs" :key="tab.name" :label="tab.label" :name="tab.name" />
  </el-tabs>
  <q-table :data="roomList" class="table" no-border>
    <q-table-column label="直播信息" align="left" width="370">
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
    <q-table-column align="left" label="主播简介" width="310">
      <template #default="{ row }">
        <div class="ellipsis2">{{ row.liveSynopsis }}</div>
      </template>
    </q-table-column>
    <q-table-column align="left" label="主播昵称" width="270">
      <template #default="{ row }">
        <div>{{ row.anchor.anchorNickname }}({{ row.anchor.phone }})</div>
      </template>
    </q-table-column>
    <q-table-column label="操作" prop="action" width="150" fixed="right">
      <template #default="{ row }">
        <!-- <el-link type="primary">运营</el-link> -->
        <el-link v-if="row.status === 'PROCESSING'" type="danger" @click="addReason(row.id)">违规下播</el-link>
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
  <el-dialog v-model="showprohibitDialog" :close-on-click-modal="false" destroy-on-close title="违禁下播" width="550px">
    <prohibit-live ref="prohibitLiveRef" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showprohibitDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAnchor">确认</el-button>
      </span>
    </template>
  </el-dialog>
  <text />
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(table) {
  overflow: auto;
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
