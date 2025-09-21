<template>
    <div class="address">
        <div class="handle_container">
            <el-button type="primary" round @click="addressDialog = true">新增地址</el-button>
        </div>
        <div class="table_container">
            <el-table
                class="address__table"
                :data="tableData.records"
                style="width: 100%; max-height: calc(100vh - 273px)"
                :cell-style="{ height: '84px' }"
                :header-cell-style="{ fontSize: '14px', fontWeight: 'bold', height: '48px', color: '#000', background: '#f6f8fa' }"
            >
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column prop="contactName" label="联系人" />
                <el-table-column prop="contactPhone" label="联系电话" />
                <el-table-column prop="address" label="地址" :width="300">
                    <template #default="{ row }">
                        <q-address :address="[row?.provinceCode, row?.cityCode, row?.regionCode]" />
                        <span>{{ row.area.join('') }}{{ row.address }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="zipCode" label="邮政编码" />
                <el-table-column prop="address" label="地址类型">
                    <template #default="{ row }">{{ addressTypeComputed(row)?.join(' ') }}</template>
                </el-table-column>
                <el-table-column prop="address" label="应用商家">
                    <template #default="{ row }">{{ addressShopTypeComputed(row)?.join(' ') }}</template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" align="right">
                    <template #default="{ row }">
                        <div class="right_btn">
                            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <PageManage
            :page-size="tableData.size"
            :page-num="tableData.current"
            :total="tableData.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        ></PageManage>
    </div>
    <el-dialog v-model="addressDialog" width="650px" destroy-on-close :title="currentRow?.id ? '编辑地址' : '添加地址'" @close="currentRow = {}">
        <addressComp ref="addressCompRef" :logistics="currentRow" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="addressDialog = false">取消</el-button>
                <el-button type="primary" @click="submitHandle">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { delLogisticsById, doGetLogisticsList, setLogisticsDddress } from '@/apis/set/platformDelivery'
import { ElMessage, ElMessageBox } from 'element-plus'
import addressComp from './address.vue'

const addressDialog = ref(false)
let tableData = ref({
    current: 1,
    records: [],
    size: 10,
    total: 0,
})
const addressCompRef = ref<InstanceType<typeof addressComp> | null>(null)
const currentRow = ref<any>({})
const addressTypeComputed = computed(() => (row: any) => {
    const addressTypeStack = []
    if (row?.defSend === 'YES') addressTypeStack.push('发货地址')
    if (row?.defReceive === 'YES') addressTypeStack.push('收货地址')
    return addressTypeStack
})
const addressShopTypeComputed = computed(() => (row: any) => {
    const addressShopTypeStack = []
    if (row?.defSelfShop === 'YES') addressShopTypeStack.push('自营商家')
    if (row?.defSelfSupplier === 'YES') addressShopTypeStack.push('自营供应商')
    return addressShopTypeStack
})
const handleEdit = (row: any) => {
    currentRow.value = row
    addressDialog.value = true
}
const handleDelete = async (id: string) => {
    try {
        await ElMessageBox.confirm('确定删除此项?删除后不会保留已删除地址！', {
            title: '提示',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code } = await delLogisticsById(id)
        if (code === 200) {
            ElMessage.success({ message: '删除成功' })
        }
        await initialAddressList() // 重新拉取数据
    } catch (e) {
        console.log(e)
    }
}

const handleSizeChange = (value: number) => {
    tableData.value.current = 1
    tableData.value.size = value
    initialAddressList()
}
const handleCurrentChange = (value: number) => {
    tableData.value.current = value
    initialAddressList()
}

const submitHandle = async () => {
    const data: any = await addressCompRef.value?.getNewLogisticFormData()
    const { code, msg } = await setLogisticsDddress(data)
    if (code === 200) {
        ElMessage.success(msg || `${data?.id ? '更新' : '增加'}成功`)
        await initialAddressList()
        addressDialog.value = false
    } else {
        ElMessage.error({ message: msg || `${data?.id ? '更新' : '增加'}失败` })
    }
}

const initialAddressList = async () => {
    try {
        const { current, size } = tableData.value
        const { data, code, msg } = await doGetLogisticsList({ current, size })
        if (code === 200) {
            tableData.value = data
        } else {
            ElMessage.error({ message: msg })
        }
    } catch (e) {
        console.log(e)
    }
}
initialAddressList()
</script>

<style lang="scss" scoped>
@include b(address) {
    @include e(btn) {
        width: 81px;
        height: 36px;
        font-size: 12px;
        margin-bottom: 10px;
    }
    @include e(table-border) {
        border-left: 1px solid #f2f2f2;
        border-right: 1px solid #f2f2f2;
    }
    @include e(table) {
        @include m(loctionbtn) {
            height: 60px;
            @include flex(space-between);
            flex-direction: column;
        }
    }
}
</style>
