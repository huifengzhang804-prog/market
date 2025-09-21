<script setup lang="ts">
import shopSing from './shop-sign'
import { shopType } from './shop-sign'
import type { PropType } from 'vue'
import { doGetShopInfo } from '@/apis/afs'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage } from 'element-plus'

defineProps({
    formData: {
        type: Object as PropType<typeof shopSing>,
        default: shopSing,
    },
})

const shopStore = useShopInfoStore()
const shopInfo = ref<{ newTips?: string; shopType: keyof typeof shopType; name?: string }>({ shopType: 'ORDINARY' })

const getShopInfo = async () => {
    try {
        const { data, success } = await doGetShopInfo(shopStore.shopInfo.shopId)
        if (success) {
            shopInfo.value = data
        }
    } catch (err) {
        ElMessage.error(err as string)
    }
}

onBeforeMount(() => {
    getShopInfo()
})
</script>

<template>
    <div class="shop-sign">
        <div class="title main">
            <div class="title__text ellipsis">
                <div v-show="formData.newTips">
                    <q-icon name="icon-gonggao" size="16px" />
                    {{ shopInfo.newTips }}
                </div>
            </div>
            <div class="title__info">
                <div class="title__info-name">
                    <div class="title__info-name--tag m-r-8">{{ shopType[shopInfo.shopType] }}</div>
                    {{ shopInfo.name }}
                </div>
                <div class="title__info--service m-l-16 cp" :class="{ opacity: !formData.service }">
                    <q-icon name="icon-lianxikefu" size="20px" class="m-r-8" color="#F54319" /> 联系客服
                </div>
                <div class="title__info--shop m-l-16 cp" :class="{ opacity: !formData.follow }">
                    <q-icon svg name="icon-yiguanzhu" size="22px" class="m-r-6 icon-top-3" color="#F54319" /> 关注店铺
                </div>
            </div>
        </div>
        <div class="img" :style="{ backgroundImage: `url(${formData.img})` }"></div>
    </div>
</template>

<style lang="scss" scoped>
@include b(shop-sign) {
    background-color: #f6f6f6;
}

@include b(title) {
    display: flex;
    align-items: center;
    height: 40px;

    @include e(text) {
        color: #e90000;
        font-size: 16px;
        flex: 1;
    }

    @include e(info) {
        width: 436px;
        display: flex;
        justify-content: flex-end;
        color: #999999;
        font-size: 14px;
        line-height: 14px;

        @include m(shop) {
            display: flex;
            align-items: center;
        }

        @include m(service) {
            display: flex;
            align-items: center;
        }
    }

    @include e(info-name) {
        color: #141414;
        font-size: 16px;
        display: flex;
        align-items: center;

        @include m(tag) {
            font-size: 12px;
            text-align: center;
            line-height: 16px;
            width: 32px;
            height: 16px;
            border-radius: 2px;
            background-color: #e00500;
            color: #fff;
        }
    }
}

@include b(opacity) {
    opacity: 0;
}

@include b(img) {
    background-size: 100% 100%;
    height: 200px;
}
</style>
