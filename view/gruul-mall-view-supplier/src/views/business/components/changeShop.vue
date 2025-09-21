<template>
    <div class="shop-list">
        <div class="shop-list__login">当前登录</div>
        <div class="shop-list__container">
            <div class="shop-list__list">
                <img :src="currentShopStore.logo" :alt="currentShopStore.name" />
                <div class="shop-list__list--info">
                    <span class="name">{{ currentShopStore.name }}</span>
                </div>
            </div>
        </div>
        <template v-if="enableShopList?.length">
            <div class="shop-list__enable">可切换至</div>
            <div v-for="enableShop in enableShopList" :key="enableShop.id" class="shop-list__list" @click="changeChooseShopId(enableShop.id)">
                <img :src="enableShop.logo" :alt="enableShop.name" />
                <div class="shop-list__list--info" :class="{ checked: chooseShopId === enableShop.id }">
                    <span class="name">{{ enableShop.name }}</span>
                </div>
            </div>
            <div class="shop-list__confirm">
                <el-button type="primary" @click="confirmChangeShop">确认切换</el-button>
            </div>
        </template>
    </div>
</template>
<script lang="ts" setup>
import { doGetEnableChangeShop } from '@/apis/afs'
import { signByUser } from '@/apis/sign'
import useGlobalStore from '@/store/modules/global'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ShopInfoStore } from '@/store/modules/shopInfo/state'
import { ElMessage } from 'element-plus'
import { GrantType } from '@apis/sign/index.type'

const shopStore = useShopInfoStore()
const globalStore = useGlobalStore()
const currentShopStore = computed(() => shopStore.shopInfo)
enum SHOP_MODE {
    B2B2C = '线上店铺',
    O2O = 'O2O店铺',
}
const chooseShopId = ref('')
const enableShopList = ref<ShopInfoStore[]>([])
const changeChooseShopId = (id: string) => {
    chooseShopId.value = id
}
const confirmChangeShop = async () => {
    if (!chooseShopId.value) return ElMessage.error('请选择需要切换的店铺')
    const result = await signByUser(GrantType.SWITCH_SHOP, { shopId: chooseShopId.value })
    if (result.code === 200) {
        const chooseShop = enableShopList.value.find((enableShop) => enableShop.id === chooseShopId.value)
        if (chooseShop) {
            shopStore.SET_SHOP_INFO(chooseShop)
        }
        const accessToken = result?.data?.value
        const refreshToken = result?.data?.refreshToken?.value
        shopStore.SET_SHOP_TOKEN({ access_token: accessToken, refresh_token: refreshToken })
        ElMessage.success('切换店铺成功')
        globalStore.SET_APPLICATION_KEY(Date.now())
    }
}
const initialEnableShopList = async () => {
    const result = await doGetEnableChangeShop()
    if (result.code === 200 && result.data?.length) {
        enableShopList.value = result.data
    }
}
initialEnableShopList()
</script>
<style lang="scss" scoped>
@include b(shop-list) {
    @include e(login) {
        font-size: 14px;
        color: red;
        line-height: 40px;
    }
    @include e(container) {
        padding: 15px 0;
        border-bottom: 1px dashed #bdbdbd;
    }
    @include e(list) {
        display: flex;
        align-items: center;
        padding: 0 30px;
        img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
        }
        @include m(info) {
            flex: 1;
            margin-left: 15px;
            display: flex;
            height: 60px;
            flex-direction: column;
            justify-content: space-between;
            padding: 5px 0;
            @include b(name) {
                font-weight: 600;
                font-size: 1.2em;
            }
        }
        & + & {
            margin-top: 15px;
        }
    }
    .shop-list__enable {
        font-size: 14px;
        line-height: 40px;
    }
    .shop-list__confirm {
        margin-top: 30px;
        text-align: center;
    }
}
.checked {
    color: #f00;
}
</style>
