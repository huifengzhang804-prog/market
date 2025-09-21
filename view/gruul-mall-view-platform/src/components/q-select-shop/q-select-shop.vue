import { includes } from 'lodash-es';
<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { doGetShopList } from '@/apis/decoration/index'
import { ShopType } from '@/apis/decoration/type'
import type { PropType } from 'vue'
import type { ShopListRecords } from '@/apis/shops/model/type'
import { type ShopList, shopTypeMap } from './type'

const props = defineProps({
    selectedShop: {
        type: Object as PropType<ShopList[]>,
        default: () => {},
    },

    shopMax: {
        type: Number,
        default: 0,
    },
})

const emit = defineEmits(['update:selectedShop'])
const selectShops = useVModel(props, 'selectedShop', emit)

// 分页配置
const pageConfig = reactive({
    current: 1,
    size: 10,
})

const total = ref(0)

// 搜索配置
const retrieveConfig = ref<{ name: string; shopType: ShopType }>({
    shopType: '',
    name: '',
})

// 店铺列表
const tableData = ref<ShopList[]>([])

// 店铺类型数据
const shopTypeList = [
    { value: 'SELF_OWNED', label: '自营' },
    { value: 'PREFERRED', label: '优选' },
    { value: 'ORDINARY', label: '普通' },
]

getGoodList()

const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    getGoodList()
}

const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getGoodList()
}

/**
 * 检索店铺类型
 */
const handleSelectShop = (item: any) => {
    retrieveConfig.value.shopType = item
    getGoodList()
}

/**
 * 获取店铺列表
 */
async function getGoodList() {
    const { data, code } = await doGetShopList({
        ...pageConfig,
        ...retrieveConfig.value,
        productIsNotEmpty: true,
    })
    if (code !== 200) {
        return ElMessage.error('获取店铺失败！')
    }

    tableData.value = data.records.map((item: ShopListRecords) => {
        const { shopType, name, id, logo, score, newTips } = item
        return { shopType, name, id, logo, score, newTips }
    })
    total.value = data.total
}

/**
 * 选择店铺
 */
const select = (goods: ShopList) => {
    const index = checkInclude(goods)

    if (index === -1) {
        handleSelect(goods)
    } else {
        handleSelect(index, false)
    }
}

/**
 * 处理是否勾选
 */
const handleSelect = (item: any, isSelect = true) => {
    if (isSelect) {
        const { shopMax } = props
        if (shopMax === 1) {
            selectShops.value = [item]
            return true
        }

        if (shopMax && selectShops.value.length >= shopMax) return message()

        selectShops.value.push(item)
    } else {
        selectShops.value.splice(item, 1)
    }
    return true
}

/**
 * 是否已经选中
 */
const checkInclude = (shop: ShopList) => {
    return selectShops.value.findIndex((item) => shop.id === item.id) ?? -1
}

const message = () => {
    ElMessage.error(`店铺最多选择${props.shopMax}个!`)
    return false
}

/**
 * 全选
 */
const change = (select: any) => {
    for (let i = 0; i < tableData.value.length; i++) {
        const item = tableData.value[i]
        const index = checkInclude(item)
        const flag = index === -1

        if (select && flag) {
            if (!handleSelect(item)) return
        } else if (!select && !flag) {
            if (!handleSelect(index, false)) return
        }
    }
}

/**
 * 是否全选
 */
const checkAll = computed(() => tableData.value.length && tableData.value.every((item) => checkInclude(item) !== -1))

// hover Id
const activeId = ref('-1')
</script>

<template>
    <div>
        <!-- 搜索 -->
        <div class="search-wrap">
            <div class="search-wrap__inputs">
                <div>
                    店铺类型
                    <el-select v-model="retrieveConfig.shopType" class="m-l-6" placeholder="请选择店铺类型" clearable style="width: 146px">
                        <el-option
                            v-for="item in shopTypeList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                            @click="handleSelectShop(item.value)"
                        />
                    </el-select>
                </div>

                <div class="m-l-32">
                    店铺名称
                    <el-input v-model="retrieveConfig.name" maxlength="40" placeholder="请输入店铺名称" class="m-l-6" style="width: 180px" clearable>
                    </el-input>
                </div>
            </div>

            <el-button type="primary" :icon="Search" @click="getGoodList">查询</el-button>
        </div>

        <!-- 店铺 -->
        <el-scrollbar height="394px" always class="dialog-scroll m-t-16">
            <div class="main">
                <div
                    v-for="item in tableData"
                    :key="item.id"
                    class="main__item"
                    :class="{ checked: checkInclude(item) > -1 || activeId === item.id }"
                    @mouseover="activeId = item.id"
                    @mouseleave="activeId = '-1'"
                    @click="select(item)"
                >
                    <div class="main__item-img">
                        <img :src="item.logo" />

                        <div v-show="checkInclude(item) > -1" class="main__item-img--modal">
                            <q-icon name="icon-duigou-copy" size="40px" color="#fff" />
                        </div>
                    </div>
                    <div class="m-l-6">
                        <div class="main__item--text">{{ item.name }}</div>
                        <div class="main__item--type">{{ shopTypeMap[item.shopType] }}</div>
                    </div>
                </div>
            </div>
        </el-scrollbar>

        <!-- 分页 -->
        <div class="footer">
            <div class="footer__left">
                <template v-if="shopMax > 1">
                    <el-checkbox :model-value="checkAll" label="全选" @change="change" />
                    <div class="m-l-20">已选： {{ selectShops.length }}</div>
                </template>
            </div>
            <page-manage
                :page-size="pageConfig.size"
                :page-num="pageConfig.current"
                :total="total"
                @handle-size-change="handleSizeChange"
                @handle-current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(main) {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 16px 20px;
    @include e(item) {
        width: 372px;
        height: 99px;
        border-radius: 2px;
        border: 2px solid #e9e8ed;
        cursor: pointer;
        padding: 10px;
        font-size: 13px;
        color: #333;
        display: flex;

        @include m(text) {
            width: 267px;
            height: 40px;
            margin-bottom: 19px;
            @include utils-ellipsis(2);
        }

        @include m(type) {
            color: #f54319;
            font-size: 13px;
        }
    }

    @include e(item-img) {
        position: relative;
        overflow: hidden;
        width: 79px;
        height: 79px;
        border-radius: 2px;
        img {
            width: 79px;
            height: 79px;
        }

        @include m(modal) {
            inset: 0;
            position: absolute;
            background: #00000080;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    }
}

@include b(checked) {
    border: 2px solid #409eff;
}

@include b(footer) {
    display: flex;
    justify-content: space-between;
    @include e(left) {
        display: flex;
        align-items: center;
        padding: 12.23px 0;
        min-width: 130px;
    }
}

@include b(search-wrap) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 12px;

    @include e(inputs) {
        display: flex;
        align-items: center;
    }

    .el-button {
        margin-left: 61px;
        height: 28px;
        padding: 8px 10px;
    }
}

.demo-input-suffix {
    margin: 0 26px;
    .el-input {
        width: 98px !important;
    }
}

.ml8 {
    margin-left: 8px;
}
.mr8 {
    margin-right: 8px;
}

:deep(.el-input) {
    --el-input-height: 28px;
}

.el-input-number.is-without-controls {
    :deep(.el-input__wrapper) {
        padding-left: 10px;
        font-size: 12px;
        padding-right: 10px;
    }
}
</style>
