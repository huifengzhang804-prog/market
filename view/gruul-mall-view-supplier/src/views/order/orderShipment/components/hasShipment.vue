<script setup lang="ts">
import { ref, PropType } from 'vue'
import { ElMessage } from 'element-plus'
import { dogetOrderNotDetailsByOrderNo } from '@/apis/order'
import { doGetDeliveryPackage } from '@/apis/afs'
import { ShopOrderItem } from '../../types/order'

const $props = defineProps({
    currentNo: {
        type: String,
        required: true,
    },
    currentShopOrderNo: {
        type: String,
        required: true,
    },
    undelivered: { type: Boolean, default: false },
    // 订单列表 如果传了订单列表 则展示订单列表 不会 load 数据
    list: {
        type: Array as PropType<ShopOrderItem[]>,
        default: () => [],
    },
})
const $emit = defineEmits(['undeliveredNum'])
const { divTenThousand } = useConvert()
const tableData = ref<ShopOrderItem[]>([])

initOrderNotDetailsByOrderNo()

/**
 * @description: 初始化已发货订单
 * @returns {*}
 */
async function initOrderNotDetailsByOrderNo() {
    if ($props.list.length) return
    const { data, code } = $props.undelivered
        ? await dogetOrderNotDetailsByOrderNo($props.currentNo, $props.currentShopOrderNo)
        : await doGetDeliveryPackage($props.currentNo, $props.currentShopOrderNo)
    if (code !== 200) return ElMessage.error('获取订单失败')
    tableData.value = $props.undelivered ? data.shopOrderItems : data
    if ($props.undelivered && tableData.value.length) {
        $emit('undeliveredNum', tableData.value.length)
    }
}
</script>

<template>
    <el-table
        :data="$props.list.length ? $props.list : tableData"
        empty-text="暂无数据~"
        class="notShipment"
        style="width: 100%; margin-bottom: 20px"
    >
        <el-table-column label="商品信息">
            <template #default="{ row }">
                <el-row>
                    <el-avatar style="width: 68px; height: 68px" shape="square" size="large" :src="row.image" />
                    <div style="width: 200px; padding-left: 10px">
                        <div>
                            <div class="notShipment__show">{{ row.productName }}</div>
                        </div>
                        <div>{{ row.specs?.join('、') }}</div>
                        <el-row style="width: 100px" justify="space-between" align="middle" class="money_text"
                            >￥{{ row.dealPrice || divTenThousand(row.dealPrice).mul(row.num) }}
                        </el-row>
                    </div>
                </el-row>
            </template>
        </el-table-column>
        <el-table-column label="数量" width="100%">
            <template #default="{ row }">
                <div>*{{ row.num }}</div>
            </template>
        </el-table-column>
    </el-table>
</template>
<style scoped lang="scss">
@import './style/table.scss';
</style>
