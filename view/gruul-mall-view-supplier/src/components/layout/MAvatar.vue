<template>
    <el-dropdown
        ref="dropMenu"
        :disabled="showConfig"
        :hide-on-click="false"
        placement="bottom"
        trigger="hover"
        style="width: 100%"
        @command="commandHandle"
    >
        <div class="admin__aside--shop" style="outline: none">
            <div class="avatar--box">
                <img :src="shopInfo.logo" class="shop--logo" />
                <div class="shop--name">
                    <span class="el-dropdown-link">
                        <span :title="shopInfo.name">{{ shopInfo.name }}</span>
                    </span>
                </div>
            </div>
            <el-icon v-if="enableShopList.length >= 1"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
            <el-dropdown-menu v-if="enableShopList.length >= 1" style="width: 147px">
                <el-dropdown-item v-for="(item, index) in enableShopList" :key="index" :command="item.id">
                    <div class="dorp-cell">
                        <span>{{ item.name }}</span>
                    </div>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>
<script lang="ts" setup>
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ArrowDown } from '@element-plus/icons-vue'
import { myData } from '@/apis'
import { doGetEnableChangeShop } from '@/apis/afs'
import { signByUser } from '@/apis/sign'
import { GrantType } from '@apis/sign/index.type'
import useGlobalStore from '@/store/modules/global'
import { ElMessage } from 'element-plus'
import { configurePlatform } from '@/store/modules/configurePlatform'

const shopInfoStore = useShopInfoStore()
const globalStore = useGlobalStore()
const shopInfo = ref<any>({})
const showConfig = ref(false)
const dropMenu = ref(null)
const enableShopList = ref<any[]>([])
const chooseShopId = ref('')
const configure = configurePlatform()

onMounted(() => {
    initShopInfo()
    initialEnableShopList()
    shopInfoStore.$subscribe(() => initShopInfo())
})

const initialEnableShopList = async () => {
    const result = await doGetEnableChangeShop()
    if (result.code === 200) {
        enableShopList.value = result.data
        const chooseShop = enableShopList.value.find((enableShop) => enableShop.id === chooseShopId.value)
        if (chooseShop) {
            shopInfoStore.SET_SHOP_INFO(chooseShop)
        }
    }
}

const commandHandle = async (id: string) => {
    chooseShopId.value = id
    if (!chooseShopId.value) return ElMessage.error('请选择需要切换的店铺')
    const result = await signByUser(GrantType.SWITCH_SHOP, { shopId: chooseShopId.value })
    if (result.code === 200) {
        const chooseShop = enableShopList.value.find((enableShop) => enableShop.id === chooseShopId.value)
        if (chooseShop) {
            shopInfoStore.SET_SHOP_INFO(chooseShop)
        }
        const { value, refreshToken } = result.data
        const access_token = value
        const refresh_token = refreshToken?.value
        shopInfoStore.SET_SHOP_TOKEN({ access_token, refresh_token })
        ElMessage.success('切换店铺成功')
        globalStore.SET_APPLICATION_KEY(Date.now())
    }
}

const initShopInfo = () => {
    // 如果有值就不再获取
    const cacheShopInfo = shopInfoStore.getterShopInfo
    const empty = !cacheShopInfo
    shopInfo.value = empty ? {} : cacheShopInfo
    if (empty || !cacheShopInfo.token) return
    // myData().then((res) => {
    //     if (res.data) {
    //         shopInfoStore.SET_SHOP_ADMIN_DATA({ ...res.data })
    //     }
    // })
}
</script>

<style lang="scss" scoped>
.admin {
    &__aside {
        margin-right: 12px;
        z-index: 898;
        &--shop {
            @include flex(space-between, center);
            padding-right: 20px;
            width: 100%;
            color: #fff;
            height: 48px;
            cursor: pointer;
            .shop--logo {
                margin-right: 8px;
                border-radius: 5px;
                width: 30px;
                height: 30px;
                border: 1px solid rgba($color: #fff, $alpha: 0.5);
            }
            .shop--name .el-dropdown-link {
                color: #fff;
                font-weight: bold;
                cursor: pointer;
                span {
                    display: inline-block;
                    max-width: 155px;
                    font-size: 14px;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    vertical-align: middle;
                }
            }
        }
    }
}
.avatar--box {
    display: flex;
    align-items: center;
    height: 100%;
}
.dorp-cell {
    width: 100%;
    line-height: 40px;
    text-align: center;
}
.noborder {
    color: #555cfd;
}
:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
    background: #fff;
    color: #555cfd;
}
</style>
