<template>
    <div class="shop-goods-settings">
        <el-form v-if="activePagesType === 'RECOMMENDED_MALL_HOME_PAGE'" ref="formRef" :model="formData" :rules="rules" label-width="80px">
            <!-- 展示样式 -->
            <el-form-item label="展示样式">
                <el-radio-group v-model="formData.showStyle" @change="changeShowStyle">
                    <el-radio :value="'is-style-one'">轮播展示</el-radio>
                    <el-radio :value="'is-style-two'">轮播展示(宣传语)</el-radio>
                    <el-radio :value="'is-style-three'">平铺展示</el-radio>
                </el-radio-group>
            </el-form-item>

            <!-- 标题 -->
            <el-form-item v-if="formData.showStyle !== 'is-style-three'">
                <template #label>
                    <span><text style="color: red">* </text>标题&emsp;</span>
                </template>
                <el-input v-model="formData.title" maxlength="15" placeholder="限15个字" />
            </el-form-item>

            <!-- 店铺商品 -->
            <el-form-item label="店铺商品" prop="shopInfo">
                <div style="width: 100%; display: flex; align-items: center; justify-content: space-between">
                    <el-button type="primary" @click="addShop('店铺')">添加</el-button>
                    <span style="color: #888">可拖动排序</span>
                </div>
            </el-form-item>

            <!-- 动态店铺商品列表支持拖拽排序 -->
            <VueDraggableNext v-model="formData.shopInfo" style="width: 97%">
                <div v-for="(item, index) in formData.shopInfo" :key="index" class="shop-block">
                    <q-icon name="icon-shanchu" color="red" size="24px" class="shop-block__delete" @click="deleteShop(index)" />
                    <div class="shop-block__header">
                        <div class="shop-block__header-title">
                            <span><text style="color: red">* </text>店铺&emsp;</span>
                            <el-link v-if="!item.shop.id" :underline="false" type="primary" @click="changeShop(index)">{{ '请选择店铺' }}</el-link>
                            <span v-else :style="{ color: item.shop.id ? '#0ca715' : '' }">{{ item.shop.name }}</span>
                        </div>
                        <el-link v-if="item.shop.name" :underline="false" type="primary" @click="changeShop(index)">变更</el-link>
                    </div>

                    <div v-if="formData.showStyle === 'is-style-two'" class="shop-block__promotion">
                        <span>宣传语</span>
                        <el-input v-model="item.shop.promotion" placeholder="限10个字" maxlength="10" />
                    </div>

                    <div v-if="formData.showStyle !== 'is-style-three'" class="shop-block__adImg">
                        <span><text style="color: red">* </text>广告图&emsp;</span>
                        <el-link v-if="!item.shop.advertisement" :underline="false" type="primary" @click="buttonlFn(index)">请上传</el-link>
                        <template v-else>
                            <el-link :underline="false" style="color: #0ca715">已上传</el-link>
                            <el-link :underline="false" type="primary" style="margin-left: 10px" @click="buttonlFn(index)">重新上传</el-link>
                            <span style="color: #999; font-size: 12px; margin-left: 10px">尺寸建议750x750、750下125像素以上，大小1M一下</span>
                        </template>
                    </div>

                    <div class="shop-block__goods">
                        <div class="shop-block__goods-header">
                            <span>商品 ({{ item.goods?.length }}/{{ formData.showStyle === 'is-style-three' ? 6 : 3 }})</span>
                            <span>操作</span>
                        </div>
                        <template v-if="item.goods?.length > 0">
                            <div v-for="(good, gIndex) in item.goods" :key="gIndex" class="goods-item">
                                <span>{{ good.name }}</span>
                                <el-link :underline="false" style="color: red" @click="removeGoods(index, gIndex)">移除</el-link>
                            </div>
                        </template>
                        <div v-if="item.shop.id" class="shop-block__goods-add">
                            <el-link
                                v-if="formData.showStyle !== 'is-style-three'"
                                :disabled="item.goods?.length >= 3"
                                :underline="false"
                                type="primary"
                                @click="addGoods(index)"
                                >添加商品</el-link
                            >
                            <el-link v-else :disabled="item.goods?.length >= 6" :underline="false" type="primary" @click="addGoods(index)"
                                >添加商品</el-link
                            >
                        </div>
                    </div>
                </div>
            </VueDraggableNext>
        </el-form>
        <div v-else style="width: 97%">
            <setting-o2o-shop :form-data="formData" :active-pages-type="activePagesType" />
        </div>

        <!-- 选择店铺/商品弹窗 -->
        <el-dialog v-model="shopDialogVisible" :title="`选择${title}`" append-to-body center destroy-on-close>
            <select-dialog
                :is-select-goods="isSelectGoods"
                :activePagesType="activePagesType"
                :shop-id="shopIndex !== -1 ? formData.shopInfo[shopIndex].shop?.id : ''"
                @cancel="shopDialogVisible = false"
                @confirm="confirm"
            />
        </el-dialog>

        <!-- 选择素材 -->
        <selectMaterial
            :upload-files="1"
            :dialog-visible="dialogVisible"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
    </div>
</template>

<script setup lang="ts">
import selectMaterial from '@/views/material/selectMaterial.vue'
import defaultshopGoods from './shopGoods'
import { useVModel } from '@vueuse/core'
import selectDialog from './components/select-dialog.vue'
import Storage from '@/utils/Storage'
import settingO2oShop from './settingO2oShop.vue'
import { doGetShopCoupon } from '@/apis/shops'

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

// 表单校验规则
const formRef = ref()
const rules = {
    shopInfo: {
        validator: (rule: any, value: any, callback: any) => {
            if (!value || value.length === 0) {
                ElMessage.warning('请添加店铺')
                return
            }

            // 检查每个店铺的广告图和商品
            for (let i = 0; i < value.length; i++) {
                const shop = value[i]
                // 检查店铺
                if (!shop.shop.id) {
                    ElMessage.warning(`请选择第${i + 1}个店铺`)
                    return
                }
                // 检查广告图
                if (formData.value.showStyle !== 'is-style-three' && !shop.shop.advertisement) {
                    ElMessage.warning(`请上传第${i + 1}个店铺的广告图`)
                    return
                }
                // 检查商品
                if (!shop.goods || shop.goods.length === 0) {
                    ElMessage.warning(`请为第${i + 1}个店铺添加至少一个商品`)
                    return
                }
            }
            callback()
        },
        trigger: ['blur', 'change'],
    },
}

// 选择素材
const dialogVisible = ref(false)
// 选择店铺\商品
const shopDialogVisible = ref(false)
// 店铺索引
const shopIndex = ref(-1)
// 标题
const title = ref('')
// 是否选择商品
const isSelectGoods = computed(() => title.value === '商品')
const storageLocal = () => new Storage()

const changeShowStyle = (val: any) => {
    if (val !== 'is-style-three') {
        formData.value.shopInfo?.forEach((item) => {
            if (item.goods?.length > 3) {
                item.goods = item.goods.slice(0, 3)
            }
        })
    } else {
        formData.value.shopInfo = storageLocal().getItem('shopGoods') || formData.value.shopInfo
    }
}

watch(
    () => formData.value.showStyle,
    (newVal, oldVal) => {
        if (newVal !== 'is-style-three' && oldVal === 'is-style-three') {
            storageLocal().setItem('shopGoods', formData.value.shopInfo)
        }
    },
    {
        immediate: true,
        deep: true,
    },
)

// 删除店铺
const deleteShop = (index: number): void => {
    formData.value.shopInfo.splice(index, 1)
}

// 添加店铺
const addShop = (val: string): void => {
    formData.value.shopInfo = [...formData.value.shopInfo, defaultshopGoods.shopInfo[0]]
}

// 变更店铺
const changeShop = (index: number): void => {
    title.value = '店铺'
    shopIndex.value = index
    shopDialogVisible.value = true
    //  置空商品
    formData.value.shopInfo[index].goods = []
}

// 上传广告图
const buttonlFn = (index: number) => {
    shopIndex.value = index
    dialogVisible.value = true
}

// 选择素材
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}

// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    formData.value.shopInfo[shopIndex.value].shop.advertisement = val
}
const checkedFileLists = (val: string[]) => {
    formData.value.shopInfo[shopIndex.value].shop.advertisement = val?.shift() || ''
}

// 点击确定更改父组件中的数据
const confirm = async (row: any) => {
    if (!row) return
    const { name, id, logo, productName, pic, salePrices, productId, score } = row
    if (isSelectGoods.value) {
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
        const couponList = await getShopCouponList(id)
        formData.value.shopInfo[shopIndex.value] = {
            goods: [],
            shop: { name, id, logo, shopType: '', promotion: '', advertisement: '', couponList: couponList as any[], o2oInfo: {} },
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
const addGoods = (index: number): void => {
    if (formData.value.shopInfo[index].goods?.length < (formData.value.showStyle === 'is-style-three' ? 6 : 3)) {
        title.value = '商品'
        shopDialogVisible.value = true
        shopIndex.value = index
    }
}

// 删除商品
const removeGoods = (index: number, goodsIndex: number): void => {
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
.shop-goods-settings {
    width: 100%;
    height: 100%;
    overflow: auto;
}

.shop-block {
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
