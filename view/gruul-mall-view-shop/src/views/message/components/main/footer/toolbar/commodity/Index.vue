<template>
    <div>
        <QIcon name="icon-shangpin4" class="sendOut" size="20px" @click="dialogTableVisible = true"></QIcon>
        <el-dialog
            v-model="dialogTableVisible"
            title=""
            width="522"
            center
            top="8vh"
            :style="{ backgroundColor: searchType.total > 0 ? '#f3f3f3' : ' #fff', height: ' 765px' }"
        >
            <template #title>
                <span class="title">请选择要发送的商品</span>
            </template>
            <div class="commodity_con">
                <div class="search">
                    <el-input
                        v-model="searchType.supplierGoodsName"
                        style="width: 100%; border: none"
                        size="large"
                        placeholder="请输入搜索内容"
                        @keydown.enter="searchList"
                    >
                        <template #prepend>
                            <QIcon name="icon-sousuo" size="16px" @click="searchList" />
                        </template>
                    </el-input>
                </div>
                <div v-if="searchType.total > 0" ref="scrollContainer" class="commodity_list" @scroll="handleScroll">
                    <div v-for="(item, index) in commodityList" :key="index" class="commodity_item">
                        <div class="commodity_item_cont">
                            <img :src="item.albumPics" alt="" />
                            <div class="commodity_item_info">
                                <p class="commodity_item_info_name">{{ item.productName }}</p>
                                <div class="commodity_item_info_price">
                                    <span style="color: #f54319">
                                        ￥<text style="font-size: 14px; font-weight: bold">{{ range(item.salePrices) }}</text>
                                    </span>
                                    <span>库存:{{ renderLimited(checkLimited(item), item) }}</span>
                                    <span>销量:{{ renderSalesVolume(item.storageSkus) }}</span>
                                </div>
                            </div>
                        </div>
                        <div class="commodity_item_foot">
                            <p @click="sendProduct(item)">发送商品</p>
                        </div>
                    </div>
                    <div v-if="loading" class="footer"><span>加载中...</span></div>
                    <div v-else class="footer">
                        <span>已经到底</span>
                    </div>
                </div>
            </div>
            <div v-if="searchType.total * 1 === 0" class="commodity_noData">
                <div class="noData">
                    <img src="@/assets/images/no_shop.png" alt="" />
                    <p>没有关联的商品</p>
                </div>
            </div>
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { doGetRetrieveCommodity, getSupplierProductList } from '@/apis/decoration'
import { ApiGoodItemType } from '@/views/message/customerService/types'
import { MessageType } from '@/views/message/components/types'

const emits = defineEmits(['submitMessage'])
const dialogTableVisible = ref(false)

const searchType = reactive({
    supplierGoodsName: '',
    current: 1,
    size: 10,
    total: 0,
})
const searchList = () => {
    loadMoreItems(searchType.supplierGoodsName)
}

watch(
    () => dialogTableVisible.value,
    (newVal) => {
        if (newVal) {
            if (!commodityList.value.length) {
                loadMoreItems()
            }
        }
    },
)
const { range } = usePriceRange()
const commodityList = ref<any[]>([])
const loading = ref(false)
const scrollContainer = ref()

const loadMoreItems = async (str?: string) => {
    if (loading.value) return
    loading.value = true
    const { code, data } = await getSupplierProductList({ ...searchType, sellType: '', supplierGoodsName: str })
    if (code === 200) {
        loading.value = false
        searchType.total = data.total
        if (str) {
            commodityList.value = data.records
        } else {
            commodityList.value = [...commodityList.value, ...data.records]
        }
    }
}

const handleScroll = () => {
    const container = scrollContainer.value
    if (container.scrollTop + container.clientHeight >= container.scrollHeight) {
        searchType.current++
        loadMoreItems()
    }
}

const computedSuplier = computed(() => (storageSkus?: any[]) => {
    if (storageSkus?.[0]?.stockType === 'UNLIMITED') return '无限库存'
    return storageSkus?.reduce((prev, item) => (prev += Number(item.stock)), 0)
})

/**
 * 检查库存
 */
const checkLimited = (row: any) => {
    const unLimited = row.storageSkus.find((item: any) => item.stockType === 'UNLIMITED')
    if (unLimited) return 'UNLIMITED'
    const stock = row.storageSkus.some((item: any) => Number(item.stock) > 0)
    if (stock) return 'STOCK'
    return false
}

/**
 * 列表库存计算
 */
const renderLimited = (stock: string | boolean, row: any) => {
    if (!stock) return 0
    if (stock === 'UNLIMITED') return '无限库存'
    if (stock === 'STOCK') return computedSuplier.value(row?.storageSkus)
}

/**
 * 计算销量
 */
const renderSalesVolume = (storageSkus: any) => {
    let salesVolume = 0
    storageSkus.forEach((item: any) => {
        salesVolume += Number(item.salesVolume)
    })
    return salesVolume
}

const sendProduct = (item: any) => {
    emits('submitMessage', {
        messageType: MessageType.PRODUCT,
        message: JSON.stringify({
            id: item.id,
            name: item.productName,
            pic: item.albumPics,
            price: { estimate: Number(range(item.salePrices)) * 1000 },
            my: true,
            path: item.sellType === 'CONSIGNMENT' ? '/goods/consignment' : '/goods/purchase',
        }),
    })
    dialogTableVisible.value = false
}
</script>
<style lang="scss" scoped>
.title {
    color: #333333;
    font-weight: bold;
    font-size: 16px;
}
:deep(.el-dialog) {
    padding: 16px 5px;
}
:deep(.el-dialog__headerbtn) {
    position: absolute;
    right: 0;
    top: 0;
}
:deep(.el-dialog__header) {
    padding-right: 0;
}
:deep(.el-dialog__close) {
    font-size: 24px;
    color: #666;
    position: absolute;
    top: 8px;
    right: 10px;
}
:deep(.el-input-group__prepend) {
    border: none;
    padding: 0 0 0 14px;
    box-shadow: none;
    background-color: #fff;
    border-radius: 20px 0 0 20px;
}
:deep(.el-input__wrapper) {
    box-shadow: none;
    border-radius: 0 20px 20px 0;
}
.commodity_con {
    display: flex;
    flex-direction: column;
    align-items: center;

    .search {
        width: 100%;
        padding: 0 10px;
    }

    .commodity_list::-webkit-scrollbar {
        width: 5px; /* 设置滚动条的宽度 */
        height: 10px;
    }
    .commodity_list {
        width: 100%;
        height: 670px;
        overflow-y: auto;
        padding: 0 6px 0 12px;

        .commodity_item {
            background-color: #fff;
            margin-top: 20px;
            border-radius: 5px;
            display: flex;
            flex-direction: column;
            padding: 16px;

            .commodity_item_cont {
                display: flex;
                height: 70px;

                img {
                    width: 70px;
                    height: 70px;
                }

                .commodity_item_info {
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    margin-left: 16px;
                    flex: 1;
                    padding: 3px 0;

                    .commodity_item_info_name {
                        width: 369px;
                        font-size: 14px;
                        color: #333;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        display: -webkit-box;
                        -webkit-line-clamp: 2;
                        -webkit-box-orient: vertical;
                    }

                    .commodity_item_info_price {
                        font-size: 13px;
                        color: #999;

                        span {
                            margin-right: 12px;
                        }
                    }
                }
            }
        }

        .commodity_item_foot {
            width: 100%;
            display: flex;
            justify-content: end;
            p {
                background-color: rgba(85, 92, 253, 0.1);
                color: #555cfd;
                padding: 5px 14px;
                border-radius: 20px;
                cursor: pointer;
            }
        }
    }
    .footer {
        text-align: center;
        font-size: 12px;
        color: #333;
        padding: 15px 0;
    }
}
.commodity_noData {
    height: 500px;
    display: flex;
    align-items: center;
    justify-content: center;
    .noData {
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #999;
        img {
            width: 180px;
        }
        p {
            margin-top: 30px;
        }
    }
}
</style>
