<template>
    <el-dropdown
        ref="dropMenu"
        style="margin-left: 8px"
        :disabled="showConfig"
        :hide-on-click="false"
        placement="bottom-start"
        trigger="hover"
        @command="commandHandle"
    >
        <div style="position: relative; top: 2px">
            <QIcon name="icon-shezhi1" size="20px" color="#fff"></QIcon>
        </div>
        <template #dropdown>
            <el-dropdown-menu style="width: 150px">
                <el-dropdown-item command="setting" style="height: 55px">
                    <div class="userInfo noborder">
                        <QIcon name="icon-zhanghaodenglu" size="20px" />
                        <div>
                            <p>{{ userInfo?.nickname }}</p>
                            <p>{{ userInfo?.mobile }}</p>
                        </div>
                    </div>
                </el-dropdown-item>
                <el-dropdown-item command="change-password">
                    <div class="dorp-cell">
                        <span>修改密码</span>
                        <i class="el-icon-switch-button"></i>
                    </div>
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                    <div class="dorp-cell">
                        <span>退出登录</span>
                        <i class="el-icon-switch-button"></i>
                    </div>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAdminInfo } from '@/store/modules/admin'
import { doPostLogout } from '@/apis/sign'
import Storage from '@/utils/Storage'

interface UserInfo {
    mobile: string
    nickname: string
}
const router = useRouter()
const useAdmin = useAdminInfo()
const showConfig = ref(false)
const dropMenu = ref(null)
const userInfo = ref<UserInfo>(new Storage().getItem('userInfo') as UserInfo)

const commandHandle = async (command: string) => {
    // 退出登录
    if (command === 'logout') {
        useAdmin.REMOVE_ADMIN_INFO()
        doPostLogout()
        router.push('/sign').then((_) => {})
        new Storage().removeItem('userInfo')
        new Storage().removeItem('platformParentId')
    }
    // 修改密码
    if (command === 'change-password') {
        useAdmin.SET_CHANGE_PASSWORD_FLAG(true)
    }
    if (command === 'index') {
        const appBaseUrl = process.env.VUE_APP_BASEURL
        if (!appBaseUrl) return
        const url = appBaseUrl.replace(/\/api/, '')
        open(`${url}`, '_top')
    }
}
</script>

<style scoped lang="scss">
.dorp-cell {
    width: 100%;
    line-height: 40px;
    text-align: center;
}
.noborder {
    color: #555cfd;
}
.userInfo {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 100%;
}
:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
    background: #fff;
    color: #555cfd;
}
</style>
