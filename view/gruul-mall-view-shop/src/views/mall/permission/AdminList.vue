<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchFrom" :columns="columns" is-empty @searchHandle="reload" @handleReset="handleReset">
            <template #otherOperations>
                <el-button round type="primary" @click="newAdmin">新增用户</el-button>
            </template>
        </SchemaForm>
    </el-config-provider>
    <div class="table_container">
        <el-table
            :cell-style="{ fontSize: '14px', color: '#333333' }"
            :data="adminPage.records"
            :header-cell-style="{ background: '#F7F8FA' }"
            :header-row-style="{ fontSize: '14px', color: '#333', height: '48px' }"
            style="max-height: calc(100vh - 246px)"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
            <el-table-column align="left" label="姓名" prop="nickname"></el-table-column>
            <el-table-column align="left" label="电话" prop="mobile" width="130"></el-table-column>
            <el-table-column align="left" label="关联角色" prop="userRole.role.name">
                <template #default="scope">
                    <span v-if="scope.row?.userRole?.role?.enable === false" style="color: #f54319">角色已禁用</span>
                    <span v-else>{{ scope.row?.userRole?.role?.name }}</span>
                </template>
            </el-table-column>
            <el-table-column align="left" label="状态" width="120">
                <template #default="scope">
                    <span v-if="scope.row?.userRole?.role?.name === '管理员'">启用</span>
                    <el-switch v-else v-model="scope.row.userRole.enable" :disabled="scope.row.loading" @change="changeAdminStatus(scope.row)" />
                </template>
            </el-table-column>
            <el-table-column align="left" label="创建时间" prop="createTime" width="170" />
            <el-table-column align="left" label="更新时间" prop="updateTime" width="170" />
            <el-table-column align="right" fixed="right" label="操作" width="120px">
                <template #default="scope">
                    <template v-if="scope.row.userRole.role?.name !== '管理员'">
                        <template v-if="scope.row.userRole.enable">
                            <el-link type="primary" @click="editAdmin(scope.row, false)">编辑</el-link>&nbsp;
                            <el-link type="primary" @click="editAdmin(scope.row, true)">修改密码</el-link>&nbsp;
                        </template>
                        <el-link v-else type="danger" @click="handleDelete(scope.row.id)">删除</el-link>
                    </template>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <PageManage
        :page-num="adminPage.page.current"
        :page-size="adminPage.page.size"
        :total="adminPage.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />

    <el-dialog
        v-model="adminPage.showDialog"
        :title="selectedUser.selected && !isEditPassword ? '编辑' : isEditPassword && selectedUser.selected ? '修改密码' : '新增用户'"
        header-class="dialog-title"
        width="511px"
    >
        <el-form ref="formRef" :model="adminPage.form" :rules="adminPage.rules" label-width="80px" style="width: 460px; margin-top: 20px">
            <template v-if="isEditPassword && selectedUser.selected">
                <el-form-item label="新密码" prop="password">
                    <div style="display: flex; align-items: center">
                        <el-input
                            v-model.trim="adminPage.form.password"
                            autocomplete="new-password"
                            clearable
                            maxlength="20"
                            show-password
                            type="password"
                            placeholder="请输入密码"
                            style="width: 248px"
                            @keyup="handleStrengthShow"
                        />
                        <div class="strength">
                            <span class="weak" :class="{ danger: strength.weak }">弱</span>
                            <span class="medium" :class="{ success: strength.medium }">中</span>
                            <span class="strong" :class="{ brand: strength.strong }">强</span>
                        </div>
                    </div>
                    <p class="password_rules">
                        <QIcon
                            :name="passwordRules ? 'icon-dui1' : 'icon-guanbi11'"
                            :color="passwordRules ? '#00BB2C' : passwordRules !== null ? '#F54319' : '#999'"
                            :size="passwordRules ? '16px' : '14px'"
                            style="margin-right: 5px"
                        />6~10位，数字、字母、字符至少包含两种
                    </p>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                        v-model.trim="adminPage.form.confirmPassword"
                        clearable
                        maxlength="20"
                        show-password
                        type="password"
                        placeholder="请输入确认密码"
                    />
                </el-form-item>
            </template>
            <template v-else>
                <el-form-item label="姓名" prop="nickname">
                    <el-input v-model="adminPage.form.nickname" :clearable="true" maxlength="10" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item v-if="!selectedUser.selected" label="手机号" prop="mobile">
                    <el-autocomplete
                        v-model="adminPage.form.mobile"
                        :debounce="850"
                        :fetch-suggestions="queryUserByKeywords"
                        :maxlength="11"
                        :teleported="false"
                        :trigger-on-focus="false"
                        clearable
                        hide-loading
                        style="width: 100%"
                        placeholder="请输入手机号"
                        @select="selectUser"
                    >
                        <template #default="{ item }">
                            <div>{{ item.mobile }}</div>
                        </template>
                    </el-autocomplete>
                </el-form-item>
                <el-form-item label="角色" prop="roleId">
                    <el-select v-model="adminPage.form.roleId" :placeholder="rolePlaceholder">
                        <el-option
                            v-for="role in adminPage.roles"
                            :key="role.id"
                            :label="role.name"
                            :value="role.id"
                            :disabled="!role.enabled"
                            placeholder="请选择角色"
                        />
                    </el-select>
                </el-form-item>
            </template>
        </el-form>
        <template #footer>
            <div style="margin-top: 50px">
                <div class="dialog-footer">
                    <div>
                        <el-button @click="handleClose">取消</el-button>
                        <el-button type="primary" @click="saveData">确认</el-button>
                    </div>
                </div>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="adminPage.showDeleteDialog" title="请确认" width="400px" center>
        <div style="text-align: center; padding: 10px 0">请确认是否对角色删除？？？</div>
        <template #footer>
            <span>
                <el-button @click="adminPage.showDeleteDialog = false">取消</el-button>
                <el-button type="primary" @click="deleteData(adminPage.selectedId)">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import PageManage from '@/components/PageManage.vue'
import type { AutocompleteFetchSuggestionsCallback, FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import {
    deleteShopAdmin,
    enableDisableShopCustomAdmin,
    getAdminPage,
    getAdminRegisterDataById,
    getAvailableUser,
    getRolePage,
    newShopCustomAdmin,
    updateShopCustomAdmin,
} from '@/apis/mall/permission'
import { REGEX } from '@/constant'

interface SearchFromData {
    nickname: string
    mobile: string
    enable: string
}

const searchFrom = reactive<SearchFromData>({
    nickname: '',
    mobile: '',
    enable: '',
})

const defaultForm = {
    roleId: '',
    userId: null,
    nickname: null,
    username: '',
    password: null,
    confirmPassword: null,
    mobile: null,
    email: null,
}
const defaultSelectedUser = {
    selected: false,
    userId: null,
    username: null,
    mobile: null,
    email: null,
    avatar: null,
    nickname: null,
}

const columns = [
    {
        label: '姓名',
        prop: 'nickname',
        labelWidth: 40,
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入姓名',
        },
    },
    {
        label: '手机号',
        prop: 'mobile',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写申请人手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '状态',
        prop: 'enable',
        labelWidth: 40,
        valueType: 'select',
        options: [
            {
                label: '全部',
                value: '',
            },
            {
                label: '启用',
                value: 'true',
            },
            {
                label: '禁用',
                value: 'false',
            },
        ],
        fieldProps: {
            placeholder: '请选择状态',
        },
    },
]

const passwordPattern = '(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,20}'
const formRef = ref<FormInstance | null>(null)
const selectedUser = ref({ ...defaultSelectedUser })
const isEditPassword = ref(false)

// 密码校验控制
const passwordRules = ref<boolean | null>(null)
const validatePassword = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码'))
        passwordRules.value = false
        return
    }
    if (value?.length < 6 || value?.length > 20) {
        callback(new Error('密码必须是6~20位'))
        passwordRules.value = false
        return
    }
    if (!REGEX.PASSWORD.test(value)) {
        callback(new Error('密码格式不正确'))
        passwordRules.value = false
        return
    }
    passwordRules.value = true
    callback()
}
const adminPage = reactive<any>({
    queryUsers: [],
    selectedId: null,
    form: { ...defaultForm },
    rules: {
        roleId: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
        nickname: [
            { required: true, message: '请输入用户昵称' },
            { min: 1, max: 20, message: '昵称限制在1-20个字符之间' },
        ],
        username: [
            { required: true, message: '用户名需包含两类：大写字母、小写字母、数字、特殊字符', trigger: 'blur' },
            {
                validator(rule, value, callback) {
                    if (selectedUser.value.selected) {
                        return callback()
                    }
                    if (!value) {
                        return callback(new Error('用户名需包含两类：大写字母、小写字母、数字、特殊字符'))
                    }
                    if (value?.length > 11) {
                        return callback(new Error('用户名长度限定在11个字符'))
                    }
                    if (new RegExp(passwordPattern).test(value)) {
                        return callback()
                    }
                    return callback('用户名需包含两类：大写字母、小写字母、数字、特殊字符')
                },
            },
        ],
        userId: [{ required: true, message: '请输入用户名或选择对应的用户', trigger: 'change' }],
        password: [{ required: true, message: '', validator: validatePassword, trigger: 'change' }],
        confirmPassword: [
            { required: true, message: '请输入确认密码' },
            { min: 6, max: 20, message: '长度限制在6-20个字符之间' },
            { pattern: passwordPattern, message: '密码需要在大写字母、小写字母、数字、特殊字符之间任选其二' },
            {
                validator: (rule, value, callback) => {
                    if (adminPage.form.confirmPassword !== adminPage.form.password) {
                        callback(new Error('确认密码和密码不一致'))
                        return
                    }
                    callback()
                },
                trigger: 'blur',
            },
        ],
        mobile: [
            { required: true, message: '请输入用户手机号', trigger: 'change' },
            {
                pattern: '^1[3-9]\\d{9}$',
                message: '请输入正确的手机号',
            },
        ],
    } as FormRules,
    roles: [],
    showDialog: false,
    showDeleteDialog: false,
    records: [],
    total: 0,
    page: { size: 10, current: 1 },
})

// 密码强度
const strength = reactive({
    weak: false,
    medium: false,
    strong: false,
})

const rolePlaceholder = computed(() => {
    return !adminPage.roles.some((value: any) => {
        return value.enabled && adminPage.form.roleId === value.id
    }) &&
        selectedUser.value.selected &&
        !isEditPassword.value
        ? '原角色已禁用，请选择其它角色'
        : '请选择'
})

onMounted(() => {
    reload()
})

const newAdmin = async () => {
    await reloadRoles()
    adminPage.showDialog = true
    adminPage.selectedId = null
    adminPage.form = { ...defaultForm }
    selectedUser.value = { ...defaultSelectedUser }
}

const reload = () => {
    getAdminPage({ ...adminPage.page, ...searchFrom }).then((response) => {
        const data = response.data
        adminPage.records = data.records.map((item: any) => ({ ...item, loading: false }))
        adminPage.total = data.total
    })
}

const handleReset = () => {
    ;(Object.keys(searchFrom) as (keyof SearchFromData)[]).forEach((key) => (searchFrom[key] = ''))
    reload()
}

const hideDialogAndReload = () => {
    adminPage.showDialog = false
    reload()
}
const saveData = () => {
    formRef.value?.validate(async (valid) => {
        if (!valid) {
            return
        }
        const selectedId = adminPage.selectedId
        const form = { ...adminPage.form }
        if (selectedUser.value.selected) {
            //必须设置为null
            form.username = null
        }
        if (!selectedId) {
            const { code, data, msg } = await newShopCustomAdmin(form)
            if (code !== 200) {
                ElMessage.error(msg ? msg : '创建失败')
                return
            }
            ElMessage.success('用户创建成功，初始密码:123456')
            hideDialogAndReload()
            return
        }
        const { code, data, msg } = await updateShopCustomAdmin(selectedId, form)
        if (code !== 200) {
            ElMessage.error(msg ? msg : '更新失败')
            return
        }
        ElMessage.success('用户更新成功')
        hideDialogAndReload()
    })
}
const queryUserByKeywords = (keywords: string, cb: AutocompleteFetchSuggestionsCallback) => {
    if (!keywords) {
        cb([])
        return
    }
    getAvailableUser(keywords).then(({ code, data }) => {
        console.log(data, 'data')
        cb(data.records)
    })
}
const selectUser = (data: Record<string, any>) => {
    if (!data) {
        return
    }
    const { userId, username, nickname, mobile } = data
    adminPage.form.mobile = mobile
    adminPage.form.username = nickname || username
    adminPage.form.userId = userId
}

const reloadRoles = async () => {
    const response = await getRolePage({
        current: 1,
        size: 200,
    })
    adminPage.roles = response.data.records || []
}

const editAdmin = async (row: any, isEdit: boolean = false) => {
    isEditPassword.value = isEdit
    selectedUser.value.selected = true
    const userId = row.userRole.user.id
    if (adminPage.roles?.length === 0) {
        await reloadRoles()
    }
    const response = await getAdminRegisterDataById(userId)
    const data = response.data as any
    if (!data) {
        return
    }
    adminPage.selectedId = row.id
    adminPage.form = {
        roleId:
            (adminPage.roles.some((value: any) => {
                return value.enabled && row.userRole?.role?.id === value.id
            }) &&
                row.userRole?.role?.id) ||
            undefined,
        userId: userId,
        nickname: row.nickname,
        username: data.nickname,
        password: null,
        confirmPassword: null,
        mobile: row.mobile,
        email: row.email,
    }
    selectUser({ ...row, ...data })
    adminPage.showDialog = true
}
const changeAdminStatus = async (row: { loading: boolean; id: any; userRole: { enable: any; id: string } }) => {
    row.loading = true
    const { code, msg } = await enableDisableShopCustomAdmin(row.id, row.userRole.enable)
    if (code === 200) {
        ElMessage.success('已更改')
    } else {
        ElMessage.error(msg)
    }
    setTimeout(() => {
        row.loading = false
        reload()
    }, 1500)
}

const handleDelete = (dataId: string) => {
    adminPage.showDeleteDialog = true
    adminPage.selectedId = dataId
}

const deleteData = (dataId: string) => {
    deleteShopAdmin(dataId).then((res) => {
        if (res.code !== 200) {
            ElMessage.error(res.msg || '删除失败')
            return
        }
        adminPage.showDeleteDialog = false
        ElMessage.success('已删除')
        hideDialogAndReload()
    })
}
const handleClose = () => {
    formRef.value?.clearValidate()
    adminPage.showDialog = false
}
const handleSizeChange = (val: number) => {
    adminPage.page.current = 1
    adminPage.page.size = val
    reload()
}

const handleCurrentChange = (val: number) => {
    adminPage.page.current = val
    reload()
}

/**
 * 密码强度
 * @param {*}
 */
const handleStrengthShow = () => {
    // 弱密码：全是数字或全是字母，6-16个字符
    const weakReg = /^(?:[a-zA-Z]{6,16}|\d{6,16}|[~!@#$%^&*_+=:,.?[\]{}_+=:,.?[\]{}]{6,16})$/
    // 中密码：数字、26个小写英文字母或者26个大写英文字母，6-16个字符
    const mediumReg = /^(?![a-zA-Z]+$)(?!\d+$)(?![^a-zA-Z\d]+$)[a-zA-Z\d~~!@#$%^&*_+=:,.?[\]{}_+=:,.?[\]{}]{6,16}$/
    // 强密码：必须由三种字符组成（数字、字母、字符）缺一不可
    const strongReg =
        /^(?![a-zA-z]+$)(?!\d+$)(?![~!@#$%^&*_+=:,.?[\]{}]+$)(?![a-zA-z\d]+$)(?![a-zA-z~!@#$%^&*_+=:,.?[\]{}]+$)(?![\d~!@#$%^&*_+=:,.?[\]{}]+$)[a-zA-Z\d~!@#$%^&*_+=:,.?[\]{}]{6,16}$/

    const pas = adminPage.form.password
    if (pas !== '') {
        if (pas?.length >= 6 && pas?.length <= 16) {
            if (pas.match(weakReg)) {
                strength.weak = true
                strength.medium = false
                strength.strong = false
            }
            if (pas.match(mediumReg)) {
                strength.weak = true
                strength.medium = true
                strength.strong = false
            }
            if (pas.match(strongReg)) {
                strength.weak = true
                strength.medium = true
                strength.strong = true
            }
        }
    } else {
        strength.weak = false
        strength.medium = false
        strength.strong = false
    }
}
</script>

<style lang="scss" scoped>
.select-user-tips {
    font-size: 12px !important;
    color: $rows-text-color-grey;
}

.select-user-tips .new-user {
    color: #b67c03;
}
.strength {
    display: flex;
    align-items: center;
    margin-left: 15px;
    position: relative;
    top: -3px;
}
.weak,
.medium,
.strong {
    display: inline-block;
    height: 35px;
    width: 18px;
    border-bottom: 4px solid gainsboro;
    margin-left: 3px;
    font-size: 12px;
    text-align: center;
    margin-right: 14px;
}
@include b(danger) {
    color: #f54319;
    border-bottom-color: #f54319;
}
@include b(success) {
    color: #fd9224;
    border-bottom-color: #fd9224;
}
@include b(brand) {
    color: #00bb2c;
    border-bottom-color: #00bb2c;
}
.dialog-footer {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 40px;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 52px;
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
}
</style>
