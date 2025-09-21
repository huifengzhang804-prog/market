<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SelectGood from '../select-good/select-good.vue'
import { ElMessageBox } from 'element-plus'
import Category from './category.vue'
import defaultGoodData from './goods'
import { ListStyle } from './goods'
import type { CategoryItem } from './goods'
import type { ApiRetrieveComItemType } from '@/views/decoration/platformDecoration/mobile/types'
type SubFormGoodItem = {
    pic: string
    productId: string
    productName: string
    salePrices: string[]
    salesVolume: string
    shopId: string
    shopName: string
    specs: string
    status: string
}

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultGoodData>,
        default: defaultGoodData,
    },
})
const $emit = defineEmits(['update:formData'])
const subForm = useVModel($props, 'formData', $emit)

watch(subForm.value, (newval) => {
    if (!newval.firstCatList) {
        subForm.value.firstCatList = []
    }
    if (!newval.currentCategoryId === undefined) {
        subForm.value.currentCategoryId = '-1'
    }
})
const selectGoods = ref()
// 选择商品分类弹窗
const dialogShow = ref(false)
// 类目拖动下角标
const classDragIndex = ref(-1)
// 列表样式
const listStyles = [
    {
        label: '大图',
        value: 'goods-style--one',
    },
    {
        label: '一行两个',
        value: 'goods-style--two',
    },
    {
        label: '瀑布流',
        value: 'goods-style--five',
    },
    {
        label: '横向滑动',
        value: 'goods-style--four',
    },
    {
        label: '详细列表',
        value: 'goods-style--three',
    },
]
// 商品样式
const goodsStyles = [
    {
        label: '无底白边',
        value: 'is-none',
    },
    {
        label: '卡片投影',
        value: 'is-shadow',
    },
    {
        label: '描底白边',
        value: 'is-border',
    },
]
//图片倒角
const angleStyles = [
    {
        label: '直角',
        value: 'is-straight',
    },
    {
        label: '圆角',
        value: 'is-circle',
    },
]
// 文本样式
const textStyles = [
    {
        label: '常规',
        value: 'is-normal',
    },
    {
        label: '加粗',
        value: 'is-bold',
    },
]
// 购物按钮样式
const buttonStyles = [
    {
        label: '加购一',
        value: '1',
        isGeneral: false,
    },
    {
        label: '加购二',
        value: 'button-style--two',
        isGeneral: false,
    },
    {
        label: '立购一',
        value: 'button-style--three',
        isGeneral: false,
    },
    {
        label: '立购二',
        value: 'button-style--four',
        isGeneral: false,
    },
    {
        label: '图标',
        value: '',
        isGeneral: true,
    },
]
// 商品角标样式
const tagStyles = [
    {
        label: '新品',
        value: 'tag-style--one',
    },
    {
        label: '热卖',
        value: 'tag-style--two',
    },
    {
        label: '抢购',
        value: 'tag-style--three',
    },
]
// 选择商品弹窗
const goodsVisible = ref(false)
// 选择商品数组
const pointGoodsList = ref<ApiRetrieveComItemType[]>([])
const dragStarIndex = ref(0)

/**
 * 选择商品分组
 */
const handleChooseCategory = () => {
    dialogShow.value = true
}
/**
 * 移除商品
 */
const handleRemoveGoods = (index: number) => {
    subForm.value.goods.splice(index, 1)
    subForm.value.goodsCount--
}
/**
 * 增加商品
 */
const handleAddGoods = () => {
    pointGoodsList.value = []
    subForm.value.goods.forEach((i) => {
        pointGoodsList.value.push(i)
    })
    goodsVisible.value = true
}
/**
 * 取消操作
 */
const handleCancel = () => {
    goodsVisible.value = false
    selectGoods.value.search = {
        maxPrice: null,
        minPrice: null,
        name: null,
        current: 1,
        size: 10,
        saleMode: '',
        showCategoryId: null,
    }
    selectGoods.value.goodsList.map((item: { isCheck: boolean }) => {
        return (item.isCheck = false)
    })
}
/**
 * 确认选择商品
 */
const handleSure = () => {
    const list = selectGoods.value.tempGoods as ApiRetrieveComItemType[]
    const temp: SubFormGoodItem[] = []
    goodsVisible.value = false
    list.forEach((item) => {
        const { productId, pic, productName, salePrices, salesVolume, shopId, shopName, specs, status } = item
        temp.push({
            productId,
            pic,
            productName,
            salePrices,
            salesVolume,
            shopId,
            shopName,
            specs,
            status,
        })
    })
    subForm.value.goods = [...subForm.value.goods, ...temp]
    // 根据subForm.value.goods每一项的productId去重
    const tempArr = subForm.value.goods.filter((item, index, arr) => {
        return arr.findIndex((i) => i.productId === item.productId) === index
    })
    subForm.value.goods = tempArr
    subForm.value.goodsCount = temp.length
}
/**
 * 开始拖动，记录拖动的组件下角标
 */
const handleDragstart = (i: number) => {
    dragStarIndex.value = i
}
const handleDrop = (i: number) => {
    if (dragStarIndex.value === i) {
        return false
    }
    const temp = subForm.value.goods.splice(dragStarIndex.value, 1, subForm.value.goods[i])
    subForm.value.goods.splice(i, 1, ...temp)
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 * @param {*} i
 */
const handleDropClass = (i: number) => {
    if (classDragIndex.value === i) {
        return false
    }
    const temp = subForm.value.firstCatList.splice(classDragIndex.value, 1, subForm.value.firstCatList[i])
    subForm.value.firstCatList.splice(i, 1, ...temp)
}
/**
 *  阻止默认行为，否则drop事件不会触发
 */
const handleDragoverClass = (e: Event) => {
    e.preventDefault()
}
/**
 * 开始拖动，记录拖动的组件下角标
 * @param {*} i
 */
const handleDragClass = (i: number) => {
    classDragIndex.value = i
}
/**
 * 选择一级分类
 */
const handleSelectedCategory = (list: CategoryItem[] = []) => {
    console.log(list)
    const ls = subForm.value.firstCatList || []
    list.forEach((i) => {
        const temp = ls.find((t) => t.platformCategoryFirstId === i.platformCategoryFirstId)
        if (!temp) {
            ls.push(i)
        }
    })
    subForm.value.firstCatList = ls
    subForm.value.currentCategoryId = list[0].platformCategoryFirstId
}
/**
 * 删除一级分类
 */
const handleGoodGroupDel = (id: string) => {
    const firstCatList = subForm.value.firstCatList || []
    const index = firstCatList.findIndex((item) => item.platformCategoryFirstId === id)
    firstCatList.splice(index, 1)
}
/**
 * 切换显示类型
 */
const handleChangePonentType = () => {
    const { ponentType = 1 } = subForm.value
    if (ponentType === 1) {
        subForm.value.goods = []
    }
}
/**
 * 商品数量数据切换
 * @param {*} group
 */
const handleChangeProductNumber = (group: CategoryItem) => {
    if (!/^\d{1,2}$/.test(String(group.productNum))) {
        group.productNum = 5
    }
}
const handleChangeExtraConfig = (val: any, configName: string) => {
    if (subForm.value.listStyle !== 'goods-style--three') {
        // @ts-ignore
        subForm.value.extraConfig[configName] = val
        return
    }
    // let hasValKeyCount = 0
    // const currentHasValKeyList: string[] = []
    // Object.keys(subForm.value.extraConfig).forEach((key) => {
    //     // @ts-ignore
    //     if (subForm.value.extraConfig[key] && key !== 'shopTag') {
    //         currentHasValKeyList.push(key)
    //         hasValKeyCount++
    //     }
    // })
    // if (hasValKeyCount === 3 && !currentHasValKeyList.includes(configName)) {
    //     ElMessage.warning({ message: '最多选择 3 项数据' })
    //     return
    // }
    Object.keys(subForm.value.extraConfig).forEach((key) => {
        // @ts-ignore
        if (val && !['shopTag', 'memberTag'].includes(key)) {
            // @ts-ignore
            subForm.value.extraConfig[key] = key === configName
        }
    })
    // @ts-ignore
    // subForm.value.extraConfig[configName] = val
}
const changeListStyle = (listStyle: any) => {
    if (subForm.value.listStyle !== listStyle && listStyle === 'goods-style--three') {
        // TODO 清空
        Object.keys(subForm.value.extraConfig).forEach((key) => {
            if (key === 'memberTag') {
                subForm.value.extraConfig.memberTag = ''
            } else {
                // @ts-ignore
                subForm.value.extraConfig[key] = false
            }
        })
    }
    subForm.value.listStyle = listStyle
}
const updateShopName = (val: any) => {
    if (val === false) {
        subForm.value.extraConfig.shopTag = false
    }
}
</script>

<template>
    <el-form :model="subForm" label-width="70px" class="goods__page--set">
        <el-form-item label="展示分类">
            <el-radio-group v-model="subForm.ponentType" @change="handleChangePonentType">
                <el-radio :value="1">是</el-radio>
                <el-radio :value="2">否</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-if="subForm.ponentType === 1" label="商品分组">
            <el-button style="margin-left: 10px; border-radius: 7px; padding: 6px 9px" @click="handleChooseCategory">添加分类 </el-button>
            <span class="select__box-tips">最多添加30个商品分组</span>
        </el-form-item>
        <div v-show="subForm.ponentType === 1 && subForm.firstCatList && subForm.firstCatList.length > 0" class="class__box">
            <div class="info__tip-box">
                <span class="t_one">分类展示</span>
                <span class="t_two">商品数量</span>
                <span>操作</span>
            </div>
            <div style="height: 147px; overflow: scroll">
                <div v-for="(group, index) in subForm.firstCatList" :key="index">
                    <div
                        v-if="group.platformCategoryFirstId !== '-1'"
                        class="list__item"
                        :draggable="true"
                        @dragstart="handleDragClass(index)"
                        @dragover="handleDragoverClass"
                        @drop="handleDropClass(index)"
                    >
                        <span class="show__name">{{ group.platformCategoryFirstName }}</span>
                        <el-icon class="click__btn" @click="handleGoodGroupDel(group.platformCategoryFirstId)"><i-ep-delete /></el-icon>
                        <el-input-number
                            v-model="group.productNum"
                            size="small"
                            :min="0"
                            :max="50"
                            step-strictly
                            :step="1"
                            :controls="false"
                            class="input__pro-num"
                            @change="handleChangeProductNumber(group)"
                        >
                        </el-input-number>
                    </div>
                </div>
            </div>
        </div>
        <el-form-item v-if="subForm.ponentType === 2" label="添加商品">
            <span style="color: #999">鼠标拖拽调整顺序，小程序端商品按顺序显示</span>
        </el-form-item>
        <el-form-item v-if="subForm.ponentType === 2" label="商品" style="border: 1px dotted #999; padding: 12px 12px 12px 0px">
            <ul class="goods-view">
                <li
                    v-for="(item, idx) in subForm.goods"
                    :key="idx"
                    :draggable="true"
                    class="goods-view__item"
                    @dragstart="handleDragstart(idx)"
                    @dragover="handleDragoverClass"
                    @drop="handleDrop(idx)"
                >
                    <img class="goods-view__img" :src="item.pic" />
                    <el-icon class="goods-view__del" @click="handleRemoveGoods(idx)"><i-ep-circleClose /></el-icon>
                </li>
                <li class="goods-view__item goods-view__add" @click="handleAddGoods">
                    <el-icon><i-ep-plus /></el-icon>
                </li>
            </ul>
        </el-form-item>
        <el-form-item label="展示样式">
            <el-radio-group :model-value="subForm.listStyle" @change="changeListStyle">
                <el-radio v-for="(item, idx) in listStyles" :key="item.value" :class="[idx > 3 ? 'goods-view__addmart' : '']" :value="item.value">{{
                    item.label
                }}</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="商品名称">
            <el-radio-group v-model="subForm.goodsNameConfig.rows">
                <template v-if="subForm.listStyle !== 'goods-style--three'">
                    <el-radio :value="0">不展示</el-radio>
                </template>
                <el-radio :value="1">展示</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="商品标签">
            <el-switch v-model="subForm.goodsNameConfig.showTag" :disabled="subForm.goodsNameConfig.rows === 0" />
        </el-form-item>
        <el-form-item label="会员价标签" label-width="85px">
            <el-radio-group v-model="subForm.extraConfig.memberTag">
                <el-radio value="">不展示</el-radio>
                <el-radio value="ALL">所有会员</el-radio>
                <el-radio value="PAID">仅付费会员</el-radio>
                <el-radio value="FREE">仅免费会员</el-radio>
            </el-radio-group>
        </el-form-item>
        <div v-if="subForm.listStyle === 'goods-style--three'" class="desc">以下数据项最多选择 1 项：</div>
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="卖点描述">
                    <el-switch :model-value="subForm.extraConfig.sellPointDesc" @change="handleChangeExtraConfig($event, 'sellPointDesc')" />
                </el-form-item>
            </el-col>
            <!-- <el-col :span="12">
                <el-form-item label="包邮">
                    <el-switch :model-value="subForm.extraConfig.freeMail" @change="handleChangeExtraConfig($event, 'freeMail')" />
                </el-form-item>
            </el-col> -->
            <el-col :span="12">
                <el-form-item label="优惠券">
                    <el-switch :model-value="subForm.extraConfig.coupon" @change="handleChangeExtraConfig($event, 'coupon')" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="历史数据">
                    <el-switch :model-value="subForm.extraConfig.historyData" @change="handleChangeExtraConfig($event, 'historyData')" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="店铺名称">
                    <el-switch
                        :model-value="subForm.extraConfig.shopName"
                        @update:model-value="updateShopName"
                        @change="handleChangeExtraConfig($event, 'shopName')"
                    />
                </el-form-item>
            </el-col>
            <el-col v-if="subForm.extraConfig.shopName" :span="12">
                <el-form-item label="店铺标签">
                    <el-switch v-model="subForm.extraConfig.shopTag" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-dialog v-model="goodsVisible" title="选择商品" width="800px" append-to-body :before-close="handleCancel">
            <SelectGood ref="selectGoods" :point-goods-list="pointGoodsList" :goods-visible="goodsVisible" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="handleCancel">取 消</el-button>
                    <el-button type="primary" @click="handleSure">确 定</el-button>
                </span>
            </template>
        </el-dialog>
        <Category v-if="dialogShow" v-model:dialogShow="dialogShow" :subForm="subForm" @choose="handleSelectedCategory" />
    </el-form>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/goods.scss';
.goods__button-text {
    width: 100px;
    display: inline-block;
    height: 24px;
    line-height: 24px;

    .el-input__inner {
        height: 24px !important;
        line-height: 24px !important;
    }
}
.goods__page--set {
    height: 770px;
    .select__box-tips {
        font-size: 13px;
        color: #999;
        margin-left: 30px;
    }

    .class__box {
        background-color: #fff;
        border: 1px solid #ccc;
        max-height: 220px;
        overflow-y: scroll;
        border-radius: 4px;
        margin-bottom: 22px;
        padding-bottom: 10px;
    }

    .info__tip-box {
        height: 40px;
        background-color: #f5f5f5;
        margin-bottom: 3px;
        position: relative;
        z-index: 1000;

        span {
            display: inline-block;
            height: 40px;
            line-height: 40px;
        }

        .t_one {
            width: 170px;
            padding-left: 18px;
        }

        .t_two {
            width: 134px;
        }
    }

    .list__item {
        height: 35px;
        line-height: 35px;
        font-size: 14px;
        box-sizing: border-box;
        padding: 5px 10px;

        .show__name {
            display: inline-block;
            width: 135px;
            padding: 0px 10px;
            background-color: #f2f2f2;
            font-size: 12px;
            height: 27px;
            line-height: 27px;
            border-radius: 6px;
            white-space: nowrap;
            text-overflow: ellipsis;
            color: #333;
            border: 1px solid #ddd;
        }

        .click__btn {
            float: right;
            color: #333;
            height: 35px;
            line-height: 35px;
            cursor: pointer;
        }

        .input__pro-num {
            width: 80px;
            float: right;
            margin-right: 70px;
        }
    }
}
.radio-wrapper {
    @include flex(flex-start, center);
    .tag-desc {
        font-size: 14px;
        margin-right: 15px;
    }
}
</style>
@/views/decoration/platformDecoration/types
