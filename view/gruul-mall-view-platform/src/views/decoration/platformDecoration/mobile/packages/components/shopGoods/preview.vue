<template>
    <!-- 区分o2o和普通店铺 -->
    <div v-if="formData.showStyle === 'appoint' || formData.showStyle === 'automatic'">
        <template v-if="formData.shopInfo.length > 0">
            <div v-for="item in formData.shopInfo" :key="item.shop.id" style="padding: 10px 0">
                <shopO2o :shop-item="item" />
            </div>
        </template>
    </div>
    <div v-else>
        <div v-if="formData.showStyle === 'is-style-one' || formData.showStyle === 'is-style-two'" class="title">
            {{ formData.title }}
        </div>
        <div v-if="formData.showStyle === 'is-style-one'" class="shop-goods">
            <div v-if="formData.shopInfo.length > 0" ref="scrollLeft" class="shop">
                <div v-for="(shop, index) in formData.shopInfo" :key="shop.shop.id" class="shop__swiper--item" @click="handleChangeShopIndex(index)">
                    <div v-if="shop.shop.logo">
                        <CircleComp
                            :animation="currentShopIndex === index"
                            :animation-speed="5000"
                            :percent="currentShopIndex === index ? 100 : 0"
                            circle-color="#F54319"
                            default-color="#fff"
                            :circle-width="2"
                            bg-color="#fff"
                            :size="46"
                            @onComplete="handleCircleComplete"
                        >
                            <!-- 放置一个空的插槽，不展示进度信息 -->
                            <template #content></template>
                        </CircleComp>
                        <img v-if="shop.shop.logo" :src="shop.shop.logo" class="swiper-img" />
                    </div>
                </div>
            </div>
            <div class="shopGoods">
                <van-swipe ref="vanSwipeRef" @change="onChange">
                    <van-swipe-item v-for="item in formData.shopInfo" :key="item.shop.id">
                        <van-image
                            v-if="item.shop.advertisement"
                            width="100%"
                            height="182px"
                            :src="item.shop.advertisement"
                            class="shopGoods__shop--img"
                        />
                        <div class="good" :style="{ justifyContent: item.goods?.length === 3 ? 'space-between' : '' }">
                            <div
                                v-for="good in item.goods && item.goods?.slice(0, 3)"
                                :key="good.id"
                                class="good__item"
                                :style="{ marginRight: item.goods?.length === 3 ? '0' : '8px' }"
                            >
                                <van-image width="90%" height="100px" :src="good.logo" />
                                <div class="shopGoods__goods--name">{{ good.name }}</div>
                                <div class="shopGoods__goods--price" :style="{ textAlign: 'left' }">
                                    <text style="font-size: 13px">￥</text>{{ good.price }}
                                </div>
                            </div>
                        </div>
                    </van-swipe-item>
                </van-swipe>
            </div>
        </div>

        <div v-else-if="formData.showStyle === 'is-style-two'" class="shop-goods">
            <van-swipe ref="vanSwipeRef" @change="onChange">
                <van-swipe-item v-for="item in formData.shopInfo" :key="item.shop.id">
                    <shop :shop-item="item" />
                </van-swipe-item>
            </van-swipe>
        </div>

        <div v-else-if="formData.showStyle === 'is-style-three'" style="padding: 5px" class="shop-tile">
            <!-- <shop-o2o :positioning-style="formData.showStyle === 'is-style-four'" /> -->
            <div v-for="item in formData.shopInfo" :key="item.shop.id" class="shop-tile__item">
                <div class="shop-tile__item--shop">
                    <van-image width="45px" height="45px" :src="item.shop.logo" class="shop-tile__item--shop--logo" />
                    <div class="shop-tile__item--shop--info">
                        <div style="display: flex; align-items: center">
                            <div
                                v-if="item.shop.shopType === 'SELF_OWNED'"
                                class="shop-tile__item--shop--info--type"
                                style="background-color: #fb232f"
                            >
                                自营
                            </div>
                            <div
                                v-else-if="item.shop.shopType === 'PREFERRED'"
                                class="shop-tile__item--shop--info--type"
                                style="background-color: #8239f6"
                            >
                                优选
                            </div>
                            <div class="shop-tile__item--shop--info--name">{{ item.shop.name }}</div>
                        </div>
                        <div class="shop-tile__item--shop--info--promotion">
                            <span
                                v-for="coupon in item.shop.couponList?.length > 2 ? item.shop.couponList.slice(0, 2) : item.shop.couponList"
                                :key="coupon.id"
                                >{{ coupon.consumerDesc }}</span
                            >
                        </div>
                    </div>
                    <div class="shop-tile__item--shop--enter">进店</div>
                </div>
                <div class="shop-tile__item--goods">
                    <div v-for="good in item.goods" :key="good.id" class="shop-tile__item--goods--item">
                        <van-image width="110px" height="110px" :src="good.logo" class="shop-tile__item--goods--item--img" />
                        <div style="display: flex; flex-direction: column; justify-content: space-around; height: 50px">
                            <div class="shop-tile__item--goods--item--name">{{ good.name }}</div>
                            <div class="shop-tile__item--goods--item--price" :style="{ textAlign: 'left' }">
                                <text>￥</text>
                                <text v-if="String(good.price)?.includes('.')">{{ String(good.price).split('.')[0] }}.</text>
                                <text v-if="String(good.price)?.includes('.')" style="font-size: 12px">{{ String(good.price).split('.')[1] }}</text>
                                <text v-else>{{ good.price }}</text>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import shop from './components/shop.vue'
import shopGoods from './shopGoods'
import shopO2o from './components/shop-o2o.vue'
import { doGetRetrieveProduct, doPostShopInfo } from '@/apis/good'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'
import CircleComp from './components/circle.vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof shopGoods>,
        default() {
            return shopGoods
        },
    },
    activePagesType: {
        type: String,
        default: '',
    },
})
const scrollLeft = ref()
const vanSwipeRef = ref()
const currentShopIndex = ref(0)

watch(
    () => props.formData.shopInfo,
    () => {
        shops.value = props.formData.shopInfo
    },
)

onMounted(() => {})

const handleChangeShopIndex = (index: number) => {
    if (currentShopIndex.value === index) return // 防止重复赋值和动画重启
    currentShopIndex.value = index
}

const computedShopAbstract = ref(props.formData.shopInfo?.map((item) => ({ ...item, shop: { ...item.shop, shopType: '' } })))

// 监听 currentShopIndex 变化，处理滚动
watch(
    currentShopIndex,
    (newIndex) => {
        try {
            // 滚动上方店铺列表
            if (scrollLeft.value) {
                const itemWidth = 56 // 每个item的宽度
                const margin = 7 // 右边距
                const containerWidth = 375 // 容器宽度
                const scrollPosition = newIndex * (itemWidth + margin)

                if (scrollPosition > containerWidth) {
                    scrollLeft.value.scrollTo({
                        left: scrollPosition - containerWidth + itemWidth + margin,
                        behavior: 'smooth',
                    })
                } else {
                    scrollLeft.value.scrollTo({
                        left: 0,
                        behavior: 'smooth',
                    })
                }
            }

            // 滚动下方商品轮播
            if (vanSwipeRef.value) {
                vanSwipeRef.value.swipeTo(newIndex)
            }
        } catch (error) {
            console.error('滚动处理出错：', error)
        }
    },
    {
        deep: true,
    },
)

// 监听轮播切换
const onChange = (index: number) => {
    try {
        if (currentShopIndex.value !== index) {
            currentShopIndex.value = index
        }
    } catch (error) {
        console.error('轮播切换出错：', error)
    }
}

/**
 * 获取商品 并组合
 */
const getGoods = async (goodsShop: { [key: string]: string }) => {
    const ids = Object.keys(goodsShop)
    const { code, data } = await doGetRetrieveProduct({ ids, searchTotalStockGtZero: true, size: ids.length })

    const goods: { [key: string]: any[] } = {}
    if (code !== 200) {
        ElMessage.error('获取商品列表失败')
        return
    }

    data.list.forEach((item: any) => {
        const { id, productName, productId, pic, salePrices } = item

        const price = Array.isArray(salePrices) ? salePrices[0] : salePrices
        const obj = {
            onlyId: id,
            id: productId,
            name: productName,
            logo: pic,
            price: divTenThousand(price) as unknown as string,
        }
        if (goods[goodsShop[item.id]]) {
            goods[goodsShop[item.id]].push(obj)
        } else {
            goods[goodsShop[item.id]] = [obj]
        }
    })

    return goods
}

/**
 * 获取店铺基本信息
 */
const shops = ref<typeof shopGoods.shopInfo>([])

const { divTenThousand } = useConvert()

async function initShopInfo() {
    const shopInfo = props.formData.shopInfo
    if (!shopInfo?.[0]?.shop?.id) return
    const shopGoods: { [key: string]: string[] } = {}
    const goodsShop: { [key: string]: string } = {}

    const shopIds = props.formData.shopInfo.map((item) => {
        // 记录shop下面的商品id
        shopGoods[item.shop.id] = item.goods?.map((goods) => goods.onlyId).filter((onlyId) => onlyId)

        // 记录商品id上面的shop
        shopGoods[item.shop.id]?.forEach((onlyId) => {
            goodsShop[onlyId] = item.shop.id
        })

        return item.shop.id
    })

    const goods = await getGoods(goodsShop)
    const { data, code, msg } = await doPostShopInfo(shopIds)
    if (code !== 200) return ElMessage.error('获取商品列表失败')
    shopInfo.forEach((item) => {
        data.forEach((element: any) => {
            if (element.id === item.shop.id) {
                // 保存原有的advertisement
                const advertisement = item.shop.advertisement
                const promotion = item.shop.promotion
                const couponList = item.shop.couponList
                const { name, logo, id, shopType } = element
                item.shop = {
                    name,
                    logo,
                    id,
                    shopType,
                    promotion: element.promotion || promotion,
                    advertisement: element.advertisement || advertisement, // 优先使用服务端返回的，如果没有则保留原有的
                    couponList: element.couponList || couponList,
                    o2oInfo: element.o2oInfo || {},
                }
            }
        })
        if (goods) item.goods = goods[item.shop.id]
    })
    shops.value = shops.value.filter((item) => item.shop.shopType)
}

onBeforeMount(() => {
    initShopInfo()
})

// 新增：圆环动画完成后切换到下一个
const handleCircleComplete = () => {
    if (props.formData.shopInfo && props.formData.shopInfo.length > 0) {
        if (currentShopIndex.value === props.formData.shopInfo.length - 1) {
            currentShopIndex.value = 0
        } else {
            currentShopIndex.value++
        }
    }
}
</script>

<style scoped lang="scss">
$color-active-red: #fa3534;
$color-unactive-red: #ff9e9e;
@include b(shop-goods) {
    position: relative;
    padding: 0 5px;
    background-color: #fff;
    margin-bottom: 10px;
    position: relative;
    @include e(active) {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 36px;
        width: 362px;
        border: 1px solid rgba(187, 187, 187, 1);
        border-radius: 0 0 8px 8px;
        border-top: transparent;
    }
}
@include b(shop) {
    padding-bottom: 10px;
    display: flex;
    width: 100%;
    overflow-x: auto;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
    &::-webkit-scrollbar {
        display: none;
    }

    @include e(swiper) {
        @include m(item) {
            position: relative;
            margin-right: 5px;
            flex-shrink: 0;
        }
    }
    @include b(swiper-img) {
        width: 46px;
        height: 46px;
        border-radius: 50%;
        position: absolute;
        top: 50%;
        margin-top: -23px;
        left: 50%;
        margin-left: -23px;
    }
}
:deep(.van-swipe__indicators) {
    display: none;
}
.shopGoods {
    width: 100%;
    .shopGoods__shop--img {
        :deep(.van-image__img) {
            border-radius: 15px !important;
        }
    }

    .van-swipe {
        position: relative;
        height: 300px;

        .good {
            display: flex;
            position: absolute;
            width: 100%;
            height: 160px;
            bottom: 0;
            left: 0;
            .good__item {
                width: 32%;
                background-color: #fff;
                border-radius: 15px;
                display: flex;
                flex-direction: column;
                .van-image {
                    margin: 6px auto;
                }
                .shopGoods__goods--name {
                    padding: 0 5px;
                    font-size: 13px;
                    font-family: Arial, sans-serif;
                    font-weight: 400; // 正常字重
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 1;
                    -webkit-box-orient: vertical;
                }
                .shopGoods__goods--price {
                    color: #000;
                    font-size: 16px;
                    margin-top: 5px;
                    font-family: Arial, sans-serif;
                    font-weight: 500;
                }
            }
        }
    }
}
.shop-tile {
    display: flex;
    flex-direction: column;

    .shop-tile__item {
        margin-bottom: 16px;
        padding: 0 5px;

        .shop-tile__item--shop {
            display: flex;
            align-items: center;
            justify-content: start;
            margin-bottom: 13px;

            .shop-tile__item--shop--logo {
                :deep(.van-image__img) {
                    border-radius: 5px;
                }
            }

            .shop-tile__item--shop--info {
                flex: 1;
                padding: 0 10px;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                height: 45px;

                .shop-tile__item--shop--info--type {
                    padding: 2px 4px;
                    font-size: 8px;
                    color: #fff;
                    border-radius: 2px;
                    margin-right: 5px;
                }

                .shop-tile__item--shop--info--name {
                    font-size: 16px;
                    color: #000;
                    font-weight: bold;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 1;
                    -webkit-box-orient: vertical;
                }

                .shop-tile__item--shop--info--promotion {
                    font-size: 10px;
                    color: #fb232f;

                    span {
                        padding: 2px 4px;
                        border-radius: 2px;
                        border: 1px solid #fb232f;
                        margin-right: 5px;
                        position: relative;
                        top: -2px;
                        box-sizing: border-box;
                    }
                }
            }

            .shop-tile__item--shop--enter {
                width: 55px;
                line-height: 25px;
                border-radius: 14px;
                background-color: #000;
                color: #fff;
                text-align: center;
                font-size: 12px;
            }
        }
        .shop-tile__item--goods {
            display: flex;
            flex-wrap: wrap;
            justify-content: start;

            .shop-tile__item--goods--item {
                width: 110px;
                margin-right: 10px;
                margin-bottom: 10px;

                &:nth-child(3n) {
                    margin-right: 0;
                }

                .shop-tile__item--goods--item--img {
                    :deep(.van-image__img) {
                        border-radius: 5px;
                    }
                }

                .shop-tile__item--goods--item--name {
                    font-size: 13px;
                    color: #000;
                    font-family: Arial, sans-serif;
                    font-weight: 400; // 正常字重
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 1;
                    -webkit-box-orient: vertical;
                    padding: 0 5px;
                }

                .shop-tile__item--goods--item--price {
                    font-size: 16px;
                    color: #000;
                    padding: 0 5px;
                    font-family: Arial, sans-serif;
                    font-weight: 500; // 正常字重
                }
            }
        }
    }
}
.point {
    border: 3px solid $color-active-red;
    opacity: 0.28;
    width: 2px;
    height: 1px;
    margin-left: 10px;
    cursor: pointer;
    border-radius: 50%;
}

.shop-goods--left-point {
    position: absolute;
    left: 5%;
    top: 90%;
    width: 20px;
    height: 20px;
    border: 1px solid $color-active-red;
    border-radius: 50%;
    &::before {
        content: '<';
        color: $color-active-red;
        line-height: 20px;
        display: block;
        text-align: center;
        padding-right: 2px;
    }
    &:hover {
        box-shadow: $color-active-red 0px 0px 0px 1px;
        transform: scale(1.1);
    }
}
.shop-goods--right-point {
    position: absolute;
    right: 5%;
    top: 90%;
    width: 20px;
    height: 20px;
    border: 1px solid $color-active-red;
    border-radius: 50%;
    &::before {
        content: '>';
        color: $color-active-red;
        line-height: 20px;
        display: block;
        text-align: center;
        padding-left: 2px;
    }
    &:hover {
        box-shadow: $color-active-red 0px 0px 0px 1px;
        transform: scale(1.1);
    }
}
.title {
    font-size: 16px;
    color: #333;
    margin: 16px 0;
    padding-left: 10px;
}
</style>
