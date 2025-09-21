<script setup lang="ts">
import SchemaForm from '@/components/SchemaForm.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import qTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { doGetBlackList, doPutLimitPermission } from '@/apis/vip'
import type { SearchParamsType, TableListType } from '@/views/vipBlacklist/types/index.ts'

import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'

const { divTenThousand } = useConvert()

/**
 * 获取黑名单表格数据
 */
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})
const tableList = ref<TableListType[]>([])
const searchParams = reactive<SearchParamsType>({
    userNickname: '',
    roles: '',
})

async function initBlackList() {
    const { code, data, msg } = (await doGetBlackList({ ...pageConfig, ...searchParams })) as any
    if (code === 200) {
        tableList.value = data.records
        pageConfig.total = data.total
    } else {
        ElMessage.error(msg ? msg : '获取黑名单失败')
    }
}
initBlackList()

/**
 * 当前页全选
 */
const tableSelectedArr = ref<TableListType[]>([])

/**
 * 批量删除
 */
const handleBatchRemove = () => {
    if (!tableSelectedArr.value.length) {
        ElMessage.warning('请勾选后操作')
        return
    }
    ElMessageBox.confirm('确定需要移除所有权限吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const userIds = tableSelectedArr.value.map((item: { userId: string }) => item.userId)
        const { code, msg } = await doPutLimitPermission(userIds, ['USER'])
        if (code === 200) {
            ElMessage.success('操作成功')
            initBlackList()
        } else {
            ElMessage.error(msg ? msg : '操作失败')
        }
    })
}

/*
 * 分页器方法
 */
const handleChangeSize = (e: number) => {
    pageConfig.size = e
    pageConfig.current = 1
    initBlackList()
}
const handleChangeCurrent = (e: number) => {
    pageConfig.current = e
    initBlackList()
}

/**
 * 表单配置
 */
const columns = [
    {
        label: '用户昵称',
        labelWidth: 75,
        prop: 'userNickname',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入用户昵称',
        },
    },
    {
        label: '限制权限',
        prop: 'roles',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部权限',
            },
            {
                value: 'FORBIDDEN_COMMENT',
                label: '禁止评论',
            },
            {
                value: 'FORBIDDEN_ORDER',
                label: '禁止下单',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
]

/*
 * 搜索方法
 */
const handleSearch = () => {
    initBlackList()
}
/**
 * 搜索表单重置
 */
const handleReset = () => {
    ;(Object.keys(searchParams) as (keyof SearchParamsType)[]).forEach((key) => (searchParams[key] = '' as keyof SearchParamsType))
    handleSearch()
}

/**
 * 表格操作
 */
/**
 * 回复方法
 */
const handleRemove = (userId: string) => {
    ElMessageBox.confirm('确定需要移除所有权限吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, msg } = await doPutLimitPermission([userId], ['USER'])
        if (code === 200) {
            ElMessage.success('移除成功')
            initBlackList()
        } else {
            ElMessage.error(msg ? msg : '操作失败')
        }
    })
}
/**
 * 打开编辑弹窗
 */
const checkedArr = ref<string[]>([])
const editDialog = ref(false)
const currentUserId = ref('')

const handleEdit = (userId: string, userAuth: string[]) => {
    editDialog.value = true
    checkedArr.value = userAuth
    currentUserId.value = userId
}
/**
 * 确认编辑
 */
const handleConfirm = async () => {
    if (!checkedArr.value.length) {
        ElMessage.warning('至少选择一项')
        return
    }
    const { code, msg } = await doPutLimitPermission([currentUserId.value], checkedArr.value)
    if (code === 200) {
        ElMessage.success('操作成功')
        editDialog.value = false
        checkedArr.value = []
        initBlackList()
    } else {
        ElMessage.error(msg ? msg : '操作失败')
    }
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchParams" :columns="columns" label-width="90" @searchHandle="handleSearch" @handleReset="handleReset"> </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <el-button round plain @click="handleBatchRemove">批量移除</el-button>
    </div>
    <q-table v-model:checked-item="tableSelectedArr" :data="tableList" class="base-vip-table" :selection="true" no-border>
        <q-table-column label="客户信息" width="280">
            <template #default="{ row }">
                <div class="customer-Infor">
                    <el-image
                        class="customer-Infor__img"
                        fit="cover"
                        style="width: 52px; height: 52px; margin-right: 10px; flex-shrink: 0"
                        :src="row.userHeadPortrait"
                    />
                    <div class="customer-nick-Infor">
                        <div class="ellipsis" style="width: 120px">{{ row.userNickname }}</div>
                        <div v-show="row.userPhone">{{ row.userPhone }}</div>
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="注册时间" align="left" width="200">
            <template #default="{ row }">
                <span>{{ row.createTime }}</span>
            </template>
        </q-table-column>
        <q-table-column label="会员等级" align="left" width="160">
            <template #default="{ row }">
                <span>{{ row.member?.memberType === 'PAID_MEMBER' ? '付费会员' : '免费会员' }}(VIP{{ row.member?.rankCode }})</span>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="限制权限" align="left" width="140">
            <template #default="{ row }">
                <div>
                    <div v-for="item in row.userAuthority" :key="item">
                        {{ item === 'FORBIDDEN_COMMENT' ? '禁止评论' : '禁止下单' }}
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="储值余额(元)" align="left" width="140">
            <template #default="{ row }">
                <el-row justify="space-between" align="middle">
                    <div>{{ divTenThousand(row.balance) }}</div>
                </el-row>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="累计消费(元)" align="left" width="140">
            <template #default="{ row }">
                <el-row justify="space-between" align="middle">
                    <div>{{ divTenThousand(row.dealTotalMoney) }}</div>
                </el-row>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="操作" width="150" align="right">
            <template #default="{ row }">
                <el-tooltip v-if="row.explain" class="box-item" placement="bottom-end">
                    <template #content
                        ><p style="max-width: 440px">{{ row.explain }}</p>
                    </template>
                    <el-link :underline="false" type="primary">原因</el-link>
                </el-tooltip>
                <el-link :underline="false" type="primary" style="margin: 0 15px" @click="handleEdit(row.userId, row.userAuthority)">编辑</el-link>
                <el-link :underline="false" type="danger" @click="handleRemove(row.userId)">恢复</el-link>
            </template>
        </q-table-column>
    </q-table>
    <BetterPageManage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        background
        @reload="initBlackList"
        @handle-size-change="handleChangeSize"
        @handle-current-change="handleChangeCurrent"
    />
    <el-dialog v-model="editDialog" title="编辑权限">
        <el-form>
            <el-form-item label="权限">
                <el-checkbox-group v-model="checkedArr">
                    <el-checkbox label="FORBIDDEN_COMMENT">禁止评论</el-checkbox>
                    <el-checkbox label="FORBIDDEN_ORDER">禁止下单</el-checkbox>
                </el-checkbox-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="editDialog = false">取消</el-button>
            <el-button @click="handleConfirm">确定</el-button>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
* {
    font-size: 14px;
}
@include b(base-vip-table) {
    transition: height 0.5s;
    overflow-y: auto;
}
@include b(base-vip-table-top) {
    @include flex(flex-start);
    width: 100%;
    @include m(no) {
    }
    @include m(time) {
        padding: 0 20px;
    }
}
@include b(customer-Infor) {
    height: 63px;
    @include flex(flex-start);
    @include e(img) {
        width: 40px;
        height: 40px;
        border-radius: 50%;
    }
}
@include b(customer-nick-Infor) {
    @include flex(space-around, flex-start);
    flex-direction: column;
    height: 45px;
    @include m(label) {
        cursor: pointer;
        &::after {
            content: '';
            display: inline-block;
            margin: 0 0 2px 2px;
            width: 0;
            height: 0;
            vertical-align: middle;
            border-top: 5px solid #000;
            border-left: 5px solid transparent;
            border-right: 5px solid transparent;
        }
    }
}
</style>
