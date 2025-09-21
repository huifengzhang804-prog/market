<template>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <!-- 展示样式 -->
        <el-form-item label="展示样式">
            <el-radio-group v-model="formData.showStyle" @change="handleShowStyleChange">
                <el-radio :value="'automatic'">自动获取</el-radio>
                <el-radio :value="'appoint'">指定店铺</el-radio>
            </el-radio-group>
        </el-form-item>

        <el-form-item :label="formData.showStyle === 'automatic' ? '店铺商品' : '自动获取'">
            <div style="width: 100%; display: flex; align-items: center; flex-wrap: wrap">
                <div v-if="formData.showStyle === 'appoint'" style="width: 300px">指定店铺后，根据定位数据由近到远，展示前</div>
                <div v-else>根据定位数据由近到远，展示前</div>
                <el-input v-model="formData.firstFew" placeholder="正整数" type="number" min="1" style="width: 80px" />
                <div>家店铺</div>
            </div>
        </el-form-item>

        <el-form-item v-if="formData.showStyle === 'appoint'" label="指定店铺">
            <div style="width: 100%; display: flex; align-items: center; justify-content: space-between">
                <el-button type="primary" @click="addShop('店铺')">添加</el-button>
                <span style="color: #888">可拖动排序</span>
            </div>
        </el-form-item>
        <!-- 动态店铺商品列表支持拖拽排序 -->
        <VueDraggableNext v-model="formData.shopInfo">
            <el-form-item v-if="formData.showStyle === 'appoint'" style="width: 100%" prop="shopInfo">
                <div v-for="(item, index) in formData.shopInfo" :key="index" class="shop-block">
                    <q-icon name="icon-shanchu" color="red" size="24px" class="shop-block__delete" @click="deleteShop(index)" />
                    <div class="shop-block__header">
                        <div class="shop-block__header-title">
                            <span>店铺&emsp;</span>
                            <el-link v-if="!item.shop.id" :underline="false" type="primary" @click="changeShop(index)">{{ '请选择店铺' }}</el-link>
                            <span v-else :style="{ color: item.shop.id ? '#0ca715' : '' }">{{ item.shop.name }}</span>
                        </div>
                        <el-link v-if="item.shop.name" :underline="false" type="primary" @click="changeShop(index)">变更</el-link>
                    </div>

                    <div class="shop-block__goods">
                        <div class="shop-block__goods-header">
                            <span>商品 ({{ item.goods?.length }}/6)</span>
                            <span>操作</span>
                        </div>
                        <template v-if="item.goods?.length > 0">
                            <div v-for="(good, gIndex) in item.goods" :key="gIndex" class="goods-item">
                                <span>{{ good.name }}</span>
                                <el-link :underline="false" style="color: red" @click="removeGoods(index, gIndex)">移除</el-link>
                            </div>
                        </template>
                        <div v-if="item.shop.id" class="shop-block__goods-add">
                            <el-link :disabled="item.goods?.length >= 6" :underline="false" type="primary" @click="addGoods(index)">添加商品</el-link>
                        </div>
                    </div>
                </div>
            </el-form-item>
        </VueDraggableNext>
        <!-- 选择店铺/商品对话框 -->
        <el-dialog v-model="shopDialogVisible" :title="`选择${title}`" append-to-body center destroy-on-close>
            <select-dialog
                :is-select-goods="isSelectGoods"
                :activePagesType="activePagesType"
                :shop-id="formData.shopInfo[shopIndex]?.shop?.id"
                @cancel="shopDialogVisible = false"
                @confirm="confirm"
            />
        </el-dialog>
    </el-form>
</template>

<script setup lang="ts">
import defaultshopGoods from './shopGoods'
import { useVModel } from '@vueuse/core'
import selectDialog from './components/select-dialog.vue'
import shopGood from './shopGoods'
import { doGetShopCoupon } from '@/apis/shops'
import { doGetShopList } from '@/apis/shops'

const getShopList = async () => {
    try {
        const { data } = await doGetShopList({
            status: 'NORMAL',
            page: 1,
            size: formData.value.firstFew,
            shopModes: 'O2O',
        })
        shopList.value = data.records.map((item) => ({
            shop: item,
            goods: [],
        }))
        formData.value.shopInfo = shopList.value
    } catch (err: any) {
        // 如果是取消请求的错误，直接忽略
        if (err?.message?.includes('Cancel')) return
        // 其它错误可以打印或处理
    }
}

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultshopGoods>,
        default() {
            return defaultshopGoods
        },
    },
    activePagesType: {
        type: String,
        default: '',
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const shopList = ref<any[]>([])

onMounted(() => {
    if (formData.value.showStyle === 'is-style-one') {
        formData.value.showStyle = 'automatic'
    }
})

const handleShowStyleChange = (val: any) => {
    formData.value.firstFew = 1
    if (val === 'appoint') {
        formData.value.shopInfo = [
            {
                shop: { name: '', id: '', logo: '', shopType: '', promotion: '', advertisement: '', couponList: [] as any[], o2oInfo: {} as any },
                goods: [] as any[],
            },
        ]
    } else {
        formData.value.firstFew = 35
        getShopList()
    }
}

watch(
    () => formData.value.firstFew,
    (newVal) => {
        if (formData.value.showStyle === 'automatic') {
            getShopList()
        }
    },
    {
        deep: true,
        immediate: true,
    },
)

// 表单校验规则
const formRef = ref()
const rules = {
    shopInfo: {
        validator: (rule: any, value: any, callback: any) => {
            if (!value || value.length === 0 || !value[0].shop.id) {
                ElMessage.warning('请添加店铺')
                return
            }

            // 检查每个店铺的广告图和商品
            for (let i = 0; i < value.length; i++) {
                const shop = value[i]
                // 根据定位数据由近到远，展示前数量
                if (!formData.value.firstFew || formData.value.firstFew <= 0) {
                    ElMessage.warning(`请输入指定店铺后，根据定位数据由近到远，展示前数量`)
                    return
                }
                // 检查商品
                if (formData.value.showStyle === 'appoint' && (!shop.goods || shop.goods.length === 0)) {
                    ElMessage.warning(`请为第${i + 1}个店铺添加至少一个商品`)
                    return
                }
            }
            callback()
        },
        trigger: ['change'],
    },
}

// 选择店铺\商品
const shopDialogVisible = ref(false)
// 店铺索引
const shopIndex = ref(-1)
// 标题
const title = ref('')
// 是否选择商品
const isSelectGoods = computed(() => title.value === '商品')

// 删除店铺
function deleteShop(index: number): void {
    formData.value.shopInfo.splice(index, 1)
}

// 添加店铺
function addShop(val: string): void {
    formData.value.shopInfo = [...formData.value.shopInfo, defaultshopGoods.shopInfo[0]]
}

// 变更店铺
function changeShop(index: number): void {
    title.value = '店铺'
    shopIndex.value = index
    shopDialogVisible.value = true
    //  置空商品
    formData.value.shopInfo[index].goods = []
}

// 点击确定更改父组件中的数据
const confirm = async (row: any) => {
    if (!row) return
    const { name, id, logo, productName, pic, salePrices, productId, score } = row
    if (isSelectGoods.value) {
        console.log(shopIndex.value, 'shopIndex.value')
        formData.value.shopInfo[shopIndex.value].goods.push({
            name: productName,
            onlyId: id,
            id: productId,
            logo: pic,
            price: String(salePrices[0] / 10000),
        })
    } else {
        if (formData.value.shopInfo.some((item) => item.shop.id === id)) {
            ElMessage.warning('请勿重复选择店铺')
            return
        }
        if (!row.hasOnSaleAndStock) {
            ElMessage.warning('该店铺没有上架商品或库存为0')
            return
        }
        const couponList = await getShopCouponList(id)
        formData.value.shopInfo[shopIndex.value] = {
            goods: [],
            shop: { name, id, logo, shopType: '', promotion: '', advertisement: '', couponList: couponList as any[], o2oInfo: { score } },
        }
    }
    shopDialogVisible.value = false
}

// 获取店铺优惠券
const getShopCouponList = async (shopId: string) => {
    const res = await doGetShopCoupon(shopId)
    if (res.code === 200) return res.data
}

// 添加商品
function addGoods(index: number): void {
    if (formData.value.shopInfo[index].goods?.length < 6) {
        title.value = '商品'
        shopDialogVisible.value = true
        shopIndex.value = index
    }
}

// 删除商品
function removeGoods(index: number, goodsIndex: number): void {
    formData.value.shopInfo[index].goods.splice(goodsIndex, 1)
}

// 保存前校验
const validateForm = async () => {
    if (!formRef.value) return false
    try {
        await formRef.value.validate()
        return true
    } catch (error) {
        return false
    }
}

// 将validateForm绑定到formData.value上
formData.value.validateForm = validateForm
</script>

<style lang="scss" scoped>
.shop-block {
    width: 100%;
    border: 1px solid #eee;
    margin-bottom: 16px;
    padding: 12px;
    border-radius: 4px;
    position: relative;
    &__delete {
        position: absolute;
        right: -12px;
        top: -12px;
        cursor: pointer;
    }
    &__header {
        margin-bottom: 8px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        &-title {
            display: flex;
            align-items: center;
        }
    }
    &__adImg {
        margin-bottom: 10px;
        display: flex;
        align-items: center;
    }
    &__promotion {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        span {
            margin-right: 5px;
            width: 50px;
        }
        :deep(.el-input) {
            width: 70%;
        }
    }
    &__goods {
        margin-bottom: 8px;
        &-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            line-height: 39px;
            background-color: #dfdcdc;
            color: rgba(16, 16, 16, 1);
            :first-child {
                margin-left: 63px;
            }
            :last-child {
                margin-right: 20px;
            }
        }
        &-add {
            width: 100%;
            text-align: center;
            padding-top: 20px;
        }
    }
}
.goods-item {
    display: flex;
    align-items: center;
    height: 60px;
    display: flex;
    align-items: center;
    border-bottom: 1px dashed #dfdcdc;
    :first-child {
        margin-left: 5px;
        flex: 1;
    }
    :last-child {
        margin-right: 10px;
    }
}
</style>
