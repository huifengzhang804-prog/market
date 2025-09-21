<script lang="ts" setup>
import SupplierSearch from '../components/SupplierSearch.vue'
import { useRoute, useRouter } from 'vue-router'
import PageManage from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { REGEX_MOBILE } from '@/libs/validate'
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import QTooltip from '@/components/q-tooltip/q-tooltip.vue'
import { doGetSupList, doDelSupplier, doSaveSupplier, doUpdateSupplier, doVerifySupplier } from '@/apis/good'
import type { FormRules } from 'element-plus'
import type { SearchFormType, AddressInfo } from '../types'

const formRef = ref(null)
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat
const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
const $router = useRouter()
const query = useRoute().query
const dialogFlag = ref(false)
const isEdit = ref(false)
const tableList = ref<AddressInfo[]>([])
const submitForm = ref({
    name: '',
    mobile: '',
    address: '',
    productInfo: '',
    area: '',
    province: '',
    city: '',
    addressCode: [],
})
const checkedArr = ref<AddressInfo[]>([])
const onChange = (val: any[]) => {
    submitForm.value.province = chinaData[val[0]].label
    submitForm.value.city = chinaData[val[1]].label
    submitForm.value.area = chinaData[val[2]].label
}
const phoneValidatePass = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入手机号'))
    } else if (!REGEX_MOBILE(value)) {
        callback(new Error('手机号格式有误'))
    } else {
        callback()
    }
}
const areaCodeValidatePass = (rule: any, value: string, callback: any) => {
    console.log(value)
    if (!value.length) {
        callback(new Error('请选择地址'))
    } else {
        callback()
    }
}
const formRule = reactive<FormRules>({
    name: [
        {
            required: true,
            message: '请输入供应商名称',
            trigger: 'blur',
        },
    ],
    mobile: [
        {
            required: true,
            validator: phoneValidatePass,
            trigger: 'blur',
        },
    ],
    address: [
        {
            required: true,
            message: '请输入详细地址',
            trigger: 'blur',
        },
    ],
    productInfo: [
        {
            required: true,
            message: '请输入产品标题信息',
            trigger: 'blur',
        },
    ],
    addressCode: [
        {
            required: true,
            validator: areaCodeValidatePass,
            trigger: 'blur',
        },
    ],
})

const initList = async (current?: number) => {
    const { code, data } = await doGetSupList({
        current: current || pageConfig.pageNum,
        size: pageConfig.pageSize,
    })
    if (code !== 200) return ElMessage.error('获取供应商列表失败')
    tableList.value = data.records
    pageConfig.total = data.records.length
}
initList()
const getSearchHandle = async (params: SearchFormType) => {
    const { data } = await doGetSupList({
        current: 1,
        size: pageConfig.pageSize,
        name: params.name,
        mobile: params.mobile,
        supplierSn: params.supplierSn,
    })
    tableList.value = data.records
    pageConfig.total = data.records.length
}

interface Validatable {
    validate(): boolean
}
const submitHandle = async () => {
    const isValidate = await (formRef.value! as Validatable).validate()
    if (isValidate) {
        const { code, success, msg } = isEdit.value ? await doUpdateSupplier(submitForm.value) : await doSaveSupplier(submitForm.value)
        if (code === 200 && success) {
            ElMessage.success(`${isEdit.value ? '更新成功' : '添加成功'}`)
            initList()
            dialogCloseHandle()
        } else {
            ElMessage.error(`${msg || (isEdit.value ? '更新失败' : '添加失败')}`)
        }
    }
}
const editHandle = (val: any) => {
    isEdit.value = true
    submitForm.value = { ...val }
    dialogFlag.value = true
}
/**
 * 删除供应商
 * @param {string} id
 */
const deleteHandle = (id: string) => {
    ElMessageBox.confirm('确定要删除该供应商吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const { code, success } = await doDelSupplier(id)
            if (code === 200 && success) {
                ElMessage.success('删除成功')
                initList()
            }
        })
        .catch(() => {
            console.log('取消删除')
        })
}
/**
 * 批量删除
 * @param {*}
 */
const multiDeleteHandle = async () => {
    if (!checkedArr.value.length) {
        ElMessage.warning('请选择供应商')
        return
    }
    ElMessageBox.confirm('确定要删除所选供应商吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const ids = checkedArr.value.map((item: any) => item.id).join(',')
        const { code, success } = await doDelSupplier(ids)
        if (code === 200 && success) {
            ElMessage.success('删除成功')
            initList()
        }
    })
}
const dialogCloseHandle = () => {
    isEdit.value = false
    dialogFlag.value = false
    submitForm.value = { name: '', mobile: '', address: '', productInfo: '', area: '', province: '', city: '', addressCode: [] }
}
const handleSizeChange = (value: number) => {
    pageConfig.pageSize = value
    initList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.pageNum = value
    initList()
}
const changeStatus = async (status: AddressInfo['status'], row: AddressInfo) => {
    console.log(status, row.status)
    const { code, msg } = await doUpdateSupplier(row)
    if (code === 200) {
        if (status === 'FORBIDDEN') {
            ElMessage.success('已启用')
        } else {
            ElMessage.success('已禁用')
        }
        initList()
    } else {
        ElMessage.error(msg)
    }
}

const agreeHandle = async (row: AddressInfo) => {
    row.status = 'REVIEW'
    const { code, success } = await doVerifySupplier(row)
    if (code === 200 && success) {
        ElMessage.success('审核通过')
        initList()
    }
}

const rejectHandle = async (row: AddressInfo) => {
    row.status = 'CLOSED'
    const { code, success } = await doVerifySupplier(row)
    if (code === 200 && success) {
        ElMessage.success('已拒绝')
        initList()
    }
}
</script>

<template>
    <SupplierSearch @onSearchParam="getSearchHandle" />
    <div class="grey_bar"></div>
    <div class="handle_container" style="display: flex; margin-top: 16px">
        <el-button v-if="query.from" round @click="$router.back()">返回发布商品</el-button>
        <el-button style="margin-right: 10px" type="primary" round @click="dialogFlag = true">新增供应商</el-button>
        <el-button @click="multiDeleteHandle">批量删除</el-button>
    </div>
    <QTable v-model:checkedItem="checkedArr" :data="tableList" :selection="true" no-border class="table">
        <QTableColumn label="供应商ID" prop="supplierSn" width="200">
            <template #default="{ row }">{{ row.supplierSn }} </template>
        </QTableColumn>
        <QTableColumn label="供应商名称" prop="name" width="220"> </QTableColumn>
        <QTableColumn label="评分" prop="score" width="80">
            <template #default="{ row }">
                <div style="padding: 30px 0">{{ row.score }}</div>
            </template>
        </QTableColumn>
        <QTableColumn label="手机号" prop="mobile" width="140"> </QTableColumn>
        <QTableColumn label="供应商地址" width="250">
            <template #default="{ row }">
                <q-tooltip :content="`${row.province}${row.city}${row.area}${row.address}`" width="182px" two-lines />
            </template>
        </QTableColumn>
        <QTableColumn label="状态" prop="score" width="80">
            <template #default="{ row }">
                <el-switch
                    v-model="row.status"
                    inline-prompt
                    active-value="REVIEW"
                    inactive-value="FORBIDDEN"
                    @change="() => changeStatus(row.status, row)"
                />
            </template>
        </QTableColumn>
        <QTableColumn label="操作" align="right" width="150" fixed="right">
            <template #default="scope">
                <div v-if="scope.row.status === 'REVIEW' || scope.row.status === 'FORBIDDEN'" class="sup__handle pointer">
                    <el-link style="margin-right: 10px; font-size: 12px" :underline="false" type="primary" @click="editHandle(scope.row)"
                        >编辑
                    </el-link>
                    <el-link style="margin-right: 10px; font-size: 12px" :underline="false" type="danger" @click="deleteHandle(scope.row.id)"
                        >删除
                    </el-link>
                </div>
                <div v-if="scope.row.status === 'UNDER_REVIEW'" class="sup__handle pointer">
                    <el-link style="margin-right: 10px; font-size: 12px" :underline="false" type="primary" @click="editHandle(scope.row)">
                        编辑
                    </el-link>
                    <el-link style="margin-right: 10px; font-size: 12px" :underline="false" type="primary" @click="agreeHandle(scope.row)">
                        同意
                    </el-link>
                    <el-link style="margin-right: 10px; font-size: 12px" :underline="false" type="danger" @click="rejectHandle(scope.row)">
                        拒绝
                    </el-link>
                </div>
                <el-link
                    v-if="scope.row.status === 'CLOSED'"
                    :underline="false"
                    type="danger"
                    style="font-size: 12px"
                    @click="deleteHandle(scope.row.id)"
                    >删除
                </el-link>
            </template>
        </QTableColumn>
    </QTable>
    <PageManage
        :page-size="pageConfig.pageSize"
        :page-num="pageConfig.pageNum"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-if="dialogFlag" v-model="dialogFlag" :title="isEdit ? '编辑供应商' : '新增供应商'" @close="dialogCloseHandle">
        <el-form ref="formRef" :model="submitForm" :rules="formRule" label-width="100px">
            <el-form-item label="供应商名称" prop="name">
                <el-input v-model="submitForm.name" placeholder="请填写供应商名称" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="mobile">
                <el-input v-model="submitForm.mobile" placeholder="请填写手机号码" maxlength="11"></el-input>
            </el-form-item>
            <el-form-item label="地区选择" prop="addressCode">
                <EluiChinaAreaDht v-model="submitForm.addressCode" @change="onChange"></EluiChinaAreaDht>
            </el-form-item>
            <el-form-item label="供应商地址" prop="address">
                <el-input v-model="submitForm.address" placeholder="请填写供应商地址" maxlength="60"></el-input>
            </el-form-item>
            <el-form-item label="产品信息" prop="productInfo">
                <el-input v-model="submitForm.productInfo" type="textarea" placeholder="请填写产品信息" maxlength="150"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogCloseHandle">取消</el-button>
                <el-button type="primary" @click="submitHandle">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss">
@import '@/assets/css/mixins/mixins.scss';
* {
    font-size: 14px;
}
@include b(name) {
    width: 160px;
    @include utils-ellipsis;
}
@include b(sup) {
    @include e(handle) {
        @include flex;
    }
}
@include b(pointer) {
    cursor: pointer;
}
@include b(del) {
    color: red;
}
@include b(table) {
    transition: height 0.5s;
    overflow-y: auto;
}
@include b(more) {
    @include flex();
    &:hover {
        color: #000;
    }
}
</style>
