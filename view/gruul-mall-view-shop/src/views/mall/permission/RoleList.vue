<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchFrom" :columns="columns" is-empty @searchHandle="reload" @handleReset="handleReset">
            <template #otherOperations>
                <el-button type="primary" round @click="newRole">新增角色</el-button>
            </template>
        </SchemaForm>
    </el-config-provider>
    <div class="table_container">
        <el-table
            :data="rolePage.records"
            row-class-name="notice-table-row"
            :header-row-style="{ fontSize: '14px', color: '#333', height: '48px' }"
            :header-cell-style="{ background: '#F7F8FA' }"
            :cell-style="{ fontSize: '14px', color: '#333333' }"
            :row-style="{ height: '60px' }"
            style="max-height: calc(100vh - 246px)"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="序号" type="index" width="60px" align="center"></el-table-column>
            <el-table-column label="角色名称" prop="name"></el-table-column>
            <el-table-column label="状态" prop="enabled">
                <template #default="{ row }">
                    <el-switch v-model="row.enabled" :disabled="row.loading" @change="handleChangeStatus(row)" />
                </template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" width="180px"></el-table-column>
            <el-table-column label="更新时间" prop="updateTime" width="180px"></el-table-column>
            <el-table-column label="操作" width="100px" align="right" fixed="right">
                <template #default="scope">
                    <div style="display: flex; justify-content: end">
                        <el-button v-if="!scope.row.enabled" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
                        <el-button v-else link type="primary" @click="editData(scope.row)">编辑</el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        :page-size="rolePage.page.size"
        :page-num="rolePage.page.current"
        :total="rolePage.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog
        v-model="rolePage.showDialog"
        :title="rolePage.roleForm.roleId ? '编辑角色' : '新增角色'"
        width="511px"
        @close="() => (rolePage.showDialog = false)"
    >
        <el-form ref="formRef" label-width="80px" :model="rolePage.roleForm" :rules="rolePage.roleRules" style="margin-top: 20px">
            <el-form-item label="角色名称" prop="roleName" style="margin-bottom: 14px">
                <el-input v-model="rolePage.roleForm.roleName" clearable maxlength="20" show-word-limit placeholder="请输入角色名称" />
            </el-form-item>
            <div style="height: 450px; overflow-y: scroll">
                <el-form-item label="授权菜单" prop="menuIds" style="position: relative">
                    <el-button
                        type="primary"
                        link
                        :style="{ position: 'absolute', left: isAllSelected ? '-70px' : '-45px', top: '30px' }"
                        @click="handleSelectAll"
                        >{{ isAllSelected ? '取消全选' : '全选' }}</el-button
                    >
                    <role-menus ref="roleMenusRef" v-model="rolePage.roleForm.menuIds" />
                </el-form-item>
            </div>
        </el-form>
        <template #footer>
            <div style="margin-top: 30px">
                <div class="dialog-footer">
                    <div>
                        <el-button @click="() => (rolePage.showDialog = false)">取消</el-button>
                        <el-button type="primary" @click="saveData">保存</el-button>
                    </div>
                </div>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import RoleMenus from './RoleMenus.vue'
import { getRolePage, saveRole, getMenuIdsByRoleId, editRole, deleteRole, updateRoleStatus, getAllMenus } from '@/apis/mall/permission'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormRules } from 'element-plus'
import { MenuType } from '@/apis/mall/permission/types'

const formRef = ref<HTMLFormElement | null>(null)
const searchFrom = reactive({
    name: '',
    enable: '',
})
const roleMenusRef = ref<InstanceType<typeof RoleMenus> | null>(null)
const isAllSelected = ref(false)
const columns = [
    {
        label: '角色',
        prop: 'name',
        labelWidth: 40,
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入姓名',
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

const rolePage = reactive({
    showDialog: false,
    showDeleteDialog: false,
    roleForm: {
        roleId: '',
        roleName: null,
        menuIds: [],
    },
    roleRules: {
        roleName: [
            { required: true, message: '请输入角色名', trigger: 'blur' },
            { min: 1, max: 50, message: '角色名限制在1-50之间', trigger: 'blur' },
        ],
        menuIds: [
            {
                type: 'array',
                required: true,
                validator: (rule: any, value: any, callback: any) => {
                    if (rolePage.roleForm.menuIds.length === 0) {
                        ElMessage.error('请选择至少一个有效权限')
                    } else {
                        callback()
                    }
                },
                message: '请选择至少一个有效权限',
            },
        ],
    } as FormRules,
    records: [],
    total: 0,
    page: { size: 10, current: 1 },
})
const menus = ref<MenuType[]>([])
onMounted(() => {
    reload()
    getAllMenus().then((response) => {
        menus.value = response.data as unknown as MenuType[]
    })
})
const newRole = () => {
    rolePage.roleForm.roleId = ''
    rolePage.roleForm.roleName = null
    rolePage.roleForm.menuIds = []
    rolePage.showDialog = true
}
const hideDialogAndReload = () => {
    rolePage.showDialog = false
    reload()
}

const handleReset = () => {
    searchFrom.name = ''
    searchFrom.enable = ''
    reload()
}

const handleSelectAll = () => {
    isAllSelected.value = !isAllSelected.value
    if (isAllSelected.value) {
        roleMenusRef.value?.handleCheckAll()
        rolePage.roleForm.menuIds = roleMenusRef.value?.checkedMenuIds || ([] as any)
    } else {
        roleMenusRef.value?.handleUncheckAll()
        rolePage.roleForm.menuIds = []
    }
}

// 监听角色菜单是否为全选中
watch(
    () => rolePage.roleForm.menuIds,
    (newVal) => {
        nextTick(() => {
            isAllSelected.value =
                menus.value?.every((item: MenuType) => {
                    return (
                        item.children?.every((child: MenuType) => {
                            return newVal.includes(child.id as never)
                        }) ?? false
                    )
                }) ?? false
        })
    },
    { immediate: true, deep: true },
)

watch(
    () => rolePage.showDialog,
    (newVal) => {
        if (newVal) {
            // 判断是否为全选中
            nextTick(() => {
                isAllSelected.value =
                    menus.value?.every((item: MenuType) => {
                        return (
                            item.children?.every((child: MenuType) => {
                                return rolePage.roleForm.menuIds.includes(child.id as never)
                            }) ?? false
                        )
                    }) ?? false
            })
        }
    },
    { immediate: true, deep: true },
)

const saveData = () => {
    formRef.value?.validate((valid: any) => {
        if (valid) {
            const roleId = rolePage.roleForm.roleId
            if (!roleId) {
                saveRole(markRaw(rolePage.roleForm)).then((res) => {
                    if (res?.code === 200) {
                        ElMessage.success('已保存')
                        hideDialogAndReload()
                    } else {
                        ElMessage.error(res?.msg || '新增失败')
                    }
                })
                return
            }
            editRole(roleId, rolePage.roleForm).then((res) => {
                if (res?.code === 200) {
                    ElMessage.success('已更新')
                    hideDialogAndReload()
                } else {
                    ElMessage.error(res?.msg || '更新失败')
                }
            })
        }
    })
}
const editData = (role: any) => {
    rolePage.roleForm.roleId = role.id
    rolePage.roleForm.roleName = role.name
    getMenuIdsByRoleId(role.id).then((response) => {
        rolePage.roleForm.menuIds = response.data as any
        rolePage.showDialog = true
    })
}

const handleChangeStatus = (row: { loading: boolean; id: string; enabled: boolean }) => {
    row.loading = true
    updateRoleStatus(row.id, row.enabled).then((res) => {
        if (res.code !== 200) {
            ElMessage.error(res.msg || '更新失败')
        } else {
            ElMessage.success('已更改')
        }
        setTimeout(() => {
            row.loading = false
            reload()
        }, 1500)
    })
}
const handleDelete = (roleId: string) => {
    ElMessageBox.confirm('角色删除后无法恢复，确定要删除吗？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteRole(roleId).then((res) => {
            if (res.code !== 200) {
                ElMessage.error(res.msg || '删除失败')
                return
            }
            rolePage.showDeleteDialog = false
            ElMessage.success('已删除')
            reload()
        })
    })
}

const reload = () => {
    getRolePage({ ...rolePage.page, ...searchFrom }).then((response) => {
        const data = response.data
        rolePage.records = data.records.map((item: any) => ({ ...item, loading: false }))
        rolePage.total = data.total
    })
}
reload()

const handleSizeChange = (val: number) => {
    rolePage.page.current = 1
    rolePage.page.size = val
    reload()
}

const handleCurrentChange = (val: number) => {
    rolePage.page.current = val
    reload()
}
</script>

<style scoped lang="scss">
.dialog-footer {
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 52px;
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
}
</style>
