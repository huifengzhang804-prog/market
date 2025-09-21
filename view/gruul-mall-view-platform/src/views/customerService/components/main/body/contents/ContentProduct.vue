<script setup lang="ts">
import type { PropType } from 'vue'
import { MessageAndShopAdmin } from '@/views/customerService/types'

import CommodityDetail from '@/views/good/components/shop/commodityDetail.vue'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const $router = useRouter()
const props = defineProps({
    message: {
        type: Object as PropType<MessageAndShopAdmin>,
        required: true,
    },
    isMine: {
        type: Boolean,
        default: false,
    },
})
const { divTenThousand } = useConvert()
const product = computed(() => {
    const productMsg = props.message.message
    if (!productMsg) {
        return { id: '', name: '未正确获取商品信息', salePrices: [], pic: '' }
    }
    return JSON.parse(productMsg)
})
const dialogStatus = ref(false)
</script>
<template>
    <div
        class="message-content-product"
        :class="{
            is_mine: isMine,
        }"
    >
        <div class="product-box">
            <el-image class="product_pic" :src="product.pic" fit="fill" />
            <div class="product-info">
                <div class="product-name">
                    {{ product.name ? product.name : '未正确获取商品信息' }}
                </div>
                <span v-if="product.price?.estimate">￥{{ product.price?.estimate }}</span>
            </div>
        </div>
        <div v-if="product.my" class="foot">
            <p @click="dialogStatus = true">查看商品</p>
        </div>
        <el-dialog v-model="dialogStatus" title="商品详情" width="60%" destroy-on-close top="5vh" @close="dialogStatus = false">
            <CommodityDetail v-if="product.id" :commodityId="product.id.split(':')[1]" :shopId="product.id.split(':')[0]" />
        </el-dialog>
    </div>
</template>
<style scoped lang="scss">
.message-content-product {
    margin-left: $rows-spacing-row-sm;
    width: 378px;
    padding: 14px 18px 8px 14px;
    background: #fff;
    border-radius: 0px 8px 8px 8px;
    border: 1px solid rgb(85, 92, 253);
    &.is_mine {
        margin-left: unset;
        margin-right: $rows-spacing-row-sm;
        border-radius: 8px 0px 8px 8px;
    }
    .product-box {
        display: flex;
        .product_pic {
            width: 70px;
            height: 70px;
        }
        .product-info {
            height: 70px;
            padding: 3px 0;
            color: #333;
            flex: 1;
            display: flex;
            padding-left: 12px;
            flex-direction: column;
            justify-content: space-between;
            min-width: 0;
            font-size: 14px;
            .product-name {
                height: 40px;
                width: 100%;
                @include utils-ellipsis(2);
            }
            .product-prices {
                color: #333;
            }
        }
    }
    .foot {
        border-top: 1px solid #e9ecf0;
        text-align: center;
        color: #555cfd;
        margin-top: 10px;
        p {
            margin-top: 10px;
            cursor: pointer;
        }
    }
}
</style>
