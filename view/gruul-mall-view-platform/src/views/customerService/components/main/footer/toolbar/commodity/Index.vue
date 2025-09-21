<template>
    <div>
        <QIcon
            name="icon-shangpin4"
            class="sendOut"
            size="20px"
            @click="
                () => {
                    dialogTableVisible = true
                    loadMoreItems()
                }
            "
        ></QIcon>
        <el-dialog
            v-model="dialogTableVisible"
            title=""
            width="522"
            center
            top="8vh"
            :style="{ backgroundColor: searchType.total * 1 > 0 ? '#f3f3f3' : ' #fff', height: ' 765px' }"
        >
            <template #title>
                <span class="title">请选择要发送的商品</span>
            </template>
            <div class="commodity_con">
                <div class="search">
                    <el-input
                        v-model="searchType.keyword"
                        style="width: 100%; border: none"
                        size="large"
                        placeholder="请输入搜索内容"
                        @keydown.enter="searchList"
                    >
                        <template #prepend>
                            <QIcon name="icon-sousuo3" size="16px" @click="searchList" />
                        </template>
                    </el-input>
                </div>
                <div v-if="searchType.total * 1 > 0" ref="scrollContainer" class="commodity_list" @scroll="handleScroll">
                    <div v-for="(item, index) in commodityList" :key="index" class="commodity_item">
                        <div class="commodity_item_cont">
                            <el-image class="product_pic" :src="item.pic" fit="fill" />
                            <div class="commodity_item_info">
                                <p class="commodity_item_info_name">{{ item.productName }}</p>
                                <div class="commodity_item_info_price">
                                    <span style="color: #f54319">
                                        ￥<text style="font-weight: bold">{{ range(item.salePrices) }}</text>
                                    </span>
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
                    <img src="@/assets/image/no_shop.png" alt="" />
                    <p>没有关联的商品</p>
                </div>
            </div>
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { doGetRetrieveProduct } from '@/apis/good'
import useConvert from '@/AutoImportCustomUse/useConvert'
import { MessageType, ApiGoodItemType } from '@/views/customerService/types'

const emits = defineEmits(['submitMessage'])
const dialogTableVisible = ref(false)
const page = ref(0)
const searchType = ref({
    keyword: '',
    current: 1,
    size: 10,
    total: 0,
})
const searchList = () => {
    searchType.value.current = 1
    searchType.value.size = 10
    commodityList.value = []
    loadMoreItems()
}

watch(
    () => dialogTableVisible.value,
    (newVal) => {
        if (newVal) {
            if (!commodityList.value.length) {
                loadMoreItems()
            }
        } else {
            searchType.value = {
                keyword: '',
                current: 1,
                size: 10,
                total: 0,
            }
        }
    },
)
const { divTenThousand } = useConvert()

function range(salePrices: string[] | string) {
    if (Array.isArray(salePrices)) {
        if (!salePrices.length) return '0'
        let priceArr: string[] = []
        salePrices.forEach((item) => {
            priceArr.push(String(divTenThousand(item)))
        })
        const delRepetPriceArr = Array.from(new Set(priceArr))
        return delRepetPriceArr.length > 1
            ? `${parseFloat(delRepetPriceArr[0]).toFixed(2)}-${parseFloat(delRepetPriceArr[delRepetPriceArr.length - 1]).toFixed(2)}`
            : parseFloat(delRepetPriceArr[0]).toFixed(2)
    } else {
        return divTenThousand(salePrices).toFixed(2)
    }
}
const commodityList = ref<ApiGoodItemType[]>([])
const loading = ref(false)
const scrollContainer = ref()

const loadMoreItems = async () => {
    if (loading.value) return
    loading.value = true
    const { code, data } = await doGetRetrieveProduct({ ...searchType.value, searchTotalStockGtZero: true })
    if (code === 200) {
        searchType.value.total = data.total
        page.value = data.pages
        commodityList.value = [...commodityList.value, ...data.list]
        loading.value = false
    }
}

const handleScroll = () => {
    const container = scrollContainer.value
    if (container.scrollTop + container.clientHeight >= container.scrollHeight) {
        if (page.value > searchType.value.current) {
            searchType.value.current++
            loadMoreItems()
        }
    }
}

const sendProduct = (item: any) => {
    emits('submitMessage', {
        messageType: MessageType.PRODUCT,
        content: JSON.stringify({
            id: item.id,
            name: item.productName,
            pic: item.pic,
            price: { estimate: range(item.salePrices) },
            my: true,
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

                .product_pic {
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
