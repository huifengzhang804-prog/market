<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { doGetUserList, doGetSingleUser } from '@/apis/shops'
import { ElMessage } from 'element-plus'
import { User, Search } from '@element-plus/icons-vue'
import { UserRecord } from '@/apis/shops/types/response'

const $props = defineProps({
    show: {
        type: Boolean,
        default: true,
    },
    currentChoose: {
        type: String,
        default: '',
    },
})
// 列表搜索
const search = ref('')
watch(
    () => $props.currentChoose,
    (val) => {
        if ($props.currentChoose) {
            doGetSingleUser($props.currentChoose)
                .then((res) => {
                    $emit('userConfirm', [res.data])
                })
                .catch(() => {
                    ElMessage.error('查询用户信息失败')
                })
        }
    },
    {
        deep: true,
    },
)
const chooseRadio = ref()
const isLoad = ref(false)
const list = ref<UserRecord[]>([])
const $emit = defineEmits(['update:show', 'userConfirm'])
const pageConfig = reactive({
    pageNum: 1,
    pageSize: 20,
    total: 0,
})
const dialogStatus = computed(() => {
    return $props.show
})

initUserSelect()

const filterUserInfo = (id: string) => {
    return list.value.filter((item) => item.userId === id)
}
const confirmHandle = () => {
    $emit('update:show', false)
    $emit('userConfirm', filterUserInfo(chooseRadio.value))
}
const cancelHandle = () => {
    $emit('update:show', false)
}
async function initUserSelect() {
    const { code, data, success } = await doGetUserList(pageConfig.pageNum, pageConfig.pageSize, search.value)
    if (code === 200 && success) {
        list.value = data.records
        pageConfig.total = data.total
    }
    isLoad.value = false
}
const handleSizeChange = (e: number) => {
    isLoad.value = true
    pageConfig.pageNum = 1
    pageConfig.pageSize = e
    initUserSelect()
}
const handleCurrentChange = (e: number) => {
    isLoad.value = true
    pageConfig.pageNum = e
    initUserSelect()
}
/**
 * 模糊搜索
 */
const handleSearch = () => {
    initUserSelect()
}
</script>

<template>
    <el-dialog v-model="dialogStatus" title="选择用户" width="50%" @close="cancelHandle">
        <el-radio-group v-model="chooseRadio" v-loading="isLoad" style="width: 100%" height="300">
            <el-table
                :max-height="500"
                :data="list"
                stripe
                :header-cell-style="{
                    background: '#F7F8FA',
                    color: '#000',
                    border: 'none',
                }"
                :cell-style="{ border: 'none' }"
                border
            >
                <template #empty><ElTableEmpty /></template>
                <el-table-column label="头像" align="center">
                    <template #default="scope">
                        <el-avatar :src="scope.row.avatar" :icon="User" />
                    </template>
                </el-table-column>
                <el-table-column label="用户昵称" align="center">
                    <template #default="scope">
                        <p v-text="scope.row.nickname" />
                    </template>
                </el-table-column>
                <el-table-column label="用户名" prop="username" align="center" />
                <el-table-column label="联系方式" align="center">
                    <template #default="scope">
                        <p v-text="scope.row.mobile" />
                        <p v-text="scope.row.email" />
                    </template>
                </el-table-column>
                <el-table-column label="性别" align="center">
                    <template #default="scope">
                        {{ !scope.row.gender || scope.row.gender === 'UNKNOWN' ? '未知' : scope.row.gender === 'MALE' ? '男' : '女' }}
                    </template>
                </el-table-column>
                <el-table-column align="center" width="150px">
                    <template #header>
                        <el-input v-model.trim="search" placeholder="查询信息" maxlength="20" @keyup.enter="handleSearch">
                            <template #append>
                                <el-button :icon="Search" @click="handleSearch" />
                            </template>
                        </el-input>
                    </template>
                    <template #default="scope">
                        <el-radio :value="scope.row.userId">&nbsp;</el-radio>
                    </template>
                </el-table-column>
            </el-table>
            <el-row justify="end" style="width: 100%">
                <el-col>
                    <PageManage
                        :page-num="pageConfig.pageNum"
                        :page-size="pageConfig.pageSize"
                        :total="pageConfig.total"
                        @handle-size-change="handleSizeChange"
                        @handle-current-change="handleCurrentChange"
                    />
                </el-col>
            </el-row>
        </el-radio-group>
        <template #footer>
            <span>
                <el-button @click="cancelHandle">取消</el-button>
                <el-button type="primary" @click="confirmHandle">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
