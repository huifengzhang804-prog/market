<template>
    <el-dropdown
        ref="dropMenu"
        :disabled="showConfig"
        :hide-on-click="false"
        placement="bottom-end"
        style="margin-left: 10px; position: relative; top: 2px"
        trigger="hover"
        @command="commandHandle"
    >
        <div>
            <QIcon name="icon-shezhi1" size="20px"></QIcon>
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

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { myData, doPostLogout } from '@/apis'
import Storage from '@/utils/Storage'
import { useMenuList } from '@/store/modules/menu'

interface UserInfo {
    shopId: string
    mobile: string
    nickname: string
}
const userInfo = ref<UserInfo>()
const router = useRouter()
const shopInfoStore = useShopInfoStore()
const shopInfo = ref<any>({})
const showConfig = ref(false)
const dropMenu = ref(null)
onMounted(() => {
    initShopInfo()
    shopInfoStore.$subscribe(() => initShopInfo())
})

const commandHandle = async (command: string) => {
    // 退出登录
    if (command === 'logout') {
        doPostLogout().finally(() => {
            shopInfoStore.DEL_SHOP_INFO()
            useMenuList().SET_MENU([])
            useMenuList().SET_ISADMIN(false)
            router.push('/sign')
            new Storage().removeItem('supplyParentId')
        })
    }
    // 修改密码
    if (command === 'change-password') {
        shopInfoStore.SET_CHANGE_PASSWORD_FLAG(true)
    }
    if (command === 'index') {
        const appBaseUrl = process.env.VUE_APP_BASEURL
        if (!appBaseUrl) return
        const url = appBaseUrl.replace(/\/api/, '')
        open(`${url}`, '_top')
    }
}
const initShopInfo = () => {
    // 如果有值就不再获取
    const cacheShopInfo = shopInfoStore.getterShopInfo
    const empty = !cacheShopInfo
    shopInfo.value = empty ? {} : cacheShopInfo
    if (empty || !cacheShopInfo.token) return
    myData().then((res) => {
        if (res.data) {
            userInfo.value = res.data
            shopInfoStore.SET_SHOP_ADMIN_DATA({ ...res.data })
        }
    })
}
</script>

<style lang="scss" scoped>
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
