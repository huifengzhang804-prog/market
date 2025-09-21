<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import { ApiSecondSkillItem, Long, PageParams, SearchParam, SeckillQueryStatus, SeckillStatus } from '../index'
import { doGetsecKillList, doPostActivitySetUp, doGetActivity, doPutsecKillOff } from '../apis'
import { ElMessage } from 'element-plus'
import 'uno.css'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

const router = useRouter()

interface ContentType {
  showDialog: boolean
  row?: ApiSecondSkillItem
  violation?: string
  edit: boolean
}

const activeType = ref('activity')

const activityTime = ref('')

const activityTimeList = [1, 2, 3, 4, 6, 8, 12, 24]

const content = reactive<ContentType>({
  showDialog: false,
  row: undefined,
  violation: '',
  edit: false,
})
const scondsKillList = ref<ApiSecondSkillItem[]>([])
const searchParam = reactive<SearchParam>({
  name: '',
  status: SeckillQueryStatus.ALL,
})
const pageConfig = reactive<PageParams>({
  size: 10,
  current: 1,
  total: 0,
})
const illegalReasonFormRef = ref()
const tabList = [
  {
    label: '秒杀活动',
    name: 'activity',
  },
  {
    label: '活动设置',
    name: 'setUp',
  },
]
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
    options: Object.entries(SeckillStatus).map(([key, value]) => {
      return {
        label: value,
        value: key,
      }
    }),
    fieldProps: {
      placeholder: '请选择',
    },
  },
]

const changeTab = (value: any) => {
  if (value) activeType.value = value
}
async function initSecKillList() {
  const { code, data } = await doGetsecKillList({
    ...searchParam,
    ...pageConfig,
  })
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  scondsKillList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}
initSecKillList()

const resetHandle = () => {
  Object.keys(searchParam).forEach((key) => ((searchParam as any)[key] = ''))
  initSecKillList()
}

/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initSecKillList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initSecKillList()
}
const handleEdit = (shopId?: Long, secKillId?: Long) => {
  router.push({
    name: 'secondsKillEdit',
    query: {
      shopId,
      secKillId,
    },
  })
}

const handleIllegal = async (row: ApiSecondSkillItem) => {
  content.showDialog = true
  content.row = row
  content.edit = row.status !== SeckillQueryStatus.VIOLATION_OFF_SHELF
}

/**
 * 提交违规下架表单
 */

const handleSubmitViolation = () => {
  illegalReasonFormRef.value!.validate(async (valid: boolean, fields: string) => {
    if (valid) {
      const row = content.row
      const { code } = await doPutsecKillOff({
        shopId: row!.shopId,
        activityId: row!.activityId,
        reason: content.violation,
      })
      if (code !== 200) {
        ElMessage.error('下架失败')
        return
      }
      ElMessage.success('下架成功')
      content.showDialog = false
      row!.violation = content.violation
      row!.status = SeckillQueryStatus.VIOLATION_OFF_SHELF
    } else {
      console.log('error submit!', fields)
    }
  })
}

/**
 * 关闭违规弹出框
 */
const handleCloseViolation = () => {
  content.showDialog = false
  content.row = undefined
  content.violation = ''
  content.edit = false
}
/**
 * 活动设置回显
 */
const getActivity = async () => {
  const { code, data } = await doGetActivity()
  console.log(code, data)
  if (code === 200) {
    activityTime.value = data.duration
  }
}
getActivity()

/**
 * 活动设置保存
 */
const handleSubmit = async () => {
  const { code, msg } = await doPostActivitySetUp(activityTime.value)
  if (code === 200) {
    ElMessage.success('活动设置成功')
    initSecKillList()
  } else {
    ElMessage.error(msg || '活动设置失败')
  }
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTab :tab-list="tabList" :value="activeType" @handle-tabs="changeTab" />
    <div v-if="activeType === 'activity'" class="fdc1 overh">
      <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchParam" :columns="columns" @searchHandle="initSecKillList" @handleReset="resetHandle"> </SchemaForm>
      </el-config-provider>

      <div class="table_container">
        <el-table
          ref="multipleTableRef"
          :data="scondsKillList"
          :header-row-style="{ fontSize: '14px', color: '#333' }"
          :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
        >
          <!-- 暂无数据 -->
          <template #empty>
            <ElTableEmpty imageIndex="7" />
          </template>
          <el-table-column label="店铺名称" width="200">
            <template #default="{ row }">
              <span class="name">{{ row.shopName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="活动名称" width="200">
            <template #default="{ row }">
              <span class="name">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="140">
            <template #default="{ row }: { row: ApiSecondSkillItem }">
              <span
                :style="{
                  color: [SeckillQueryStatus.VIOLATION_OFF_SHELF].includes(row.status)
                    ? '#F54319'
                    : [SeckillQueryStatus.FINISHED, SeckillQueryStatus.OFF_SHELF].includes(row.status)
                    ? '#999'
                    : '#333',
                }"
              >
                {{ SeckillStatus[row.status as keyof typeof SeckillStatus] }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="活动时间" width="230">
            <template #default="{ row }">
              <div>{{ row.startTime }}</div>
              <div>{{ row.endTime }}</div>
            </template>
          </el-table-column>
          <el-table-column label="活动商品" width="140">
            <template #default="{ row }">{{ row.product }}</template>
          </el-table-column>
          <el-table-column label="支付单数" width="140">
            <template #default="{ row }">{{ row.payOrder }}</template>
          </el-table-column>
          <el-table-column fixed="right" align="right" label="操作">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEdit(row.shopId, row.activityId)">查看 </el-button>
              <el-button
                v-if="row.status === SeckillQueryStatus.NOT_START || row.status === SeckillQueryStatus.IN_PROGRESS"
                link
                type="danger"
                @click="handleIllegal(row)"
              >
                违规下架
              </el-button>
              <el-tooltip
                v-if="row.status === SeckillQueryStatus.VIOLATION_OFF_SHELF"
                class="box-item"
                effect="dark"
                :content="row.violation"
                placement="bottom-end"
              >
                <el-button link type="primary">违规原因 </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 好用的分页器 -->
      <PageManage
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        :page-num="pageConfig.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
      <!-- 违规原因 s -->
      <el-dialog v-model="content.showDialog" center title="违规下架" width="500" @close="handleCloseViolation">
        <el-form
          v-if="content.edit"
          ref="illegalReasonFormRef"
          :model="content"
          :rules="{
            violation: [{ required: true, message: '请输入违规原因', trigger: 'blur' }],
          }"
          status-icon
        >
          <el-form-item label="" prop="violation">
            <el-input v-model="content.violation" maxlength="50" placeholder="请输入违规原因，必填" resize="none" show-word-limit type="textarea" />
          </el-form-item>
        </el-form>
        <template v-if="content.edit" #footer>
          <div class="dialog-footer">
            <el-button @click="handleCloseViolation">取消</el-button>
            <el-button type="primary" @click="handleSubmitViolation">确定 </el-button>
          </div>
        </template>
      </el-dialog>
      <!-- 违规原因 e -->
    </div>
    <div v-else>
      <div style="padding: 16px; display: flex; align-items: center">
        <label style="margin-right: 10px">活动时长</label>
        <el-select v-model="activityTime" placeholder="" style="width: 220px">
          <el-option v-for="item in activityTimeList" :key="item" :label="item" :value="item"> </el-option>
        </el-select>
        <span style="margin-left: 10px">小时</span>
        <div style="margin-left: 40px; color: #999">每场活动的持续时间</div>
      </div>
      <div class="submit-btn">
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(name) {
  @include utils-ellipsis;
}

:deep(.el-button.is-link) {
  padding: 0;
}

:deep(.el-button + .el-button) {
  margin-left: 10px;
}
.submit-btn {
  position: absolute;
  width: 100%;
  text-align: center;
  padding: 10px;
  bottom: 0;
  left: 0;
  background: #fff;
  z-index: 999;
  box-shadow: 0 0px 10px 0px #d5d5d5;
}
</style>
<style lang="scss">
.el-dialog__body {
  padding: 20px 20px 0 20px;
}
</style>
<style lang="scss" scoped>
:deep(.el-table__body-wrapper) {
  .el-table__body {
    .el-table__row {
      .cell {
        .el-button {
          padding: 0;
        }
      }
    }
  }
}
</style>
