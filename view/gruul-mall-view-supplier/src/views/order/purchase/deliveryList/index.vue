<template>
    <div class="delivery">
        <el-form :model="deliverDialogFormData" :rules="formRules">
            <el-form-item prop="deliverType" label="发货方式">
                <el-radio-group v-model="deliverDialogFormData.deliverType" @change="changeDeliverType">
                    <el-radio v-for="deliveryType in deliverTypeMap" :key="deliveryType.key" :label="deliveryType.key">{{
                        deliveryType.value
                    }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <template v-if="deliverDialogFormData.deliverType !== 'WITHOUT'">
                <el-form-item prop="logisticsCompanyCode" label="物流服务">
                    <el-select
                        v-model="deliverDialogFormData.expressCompany.logisticsCompanyCode"
                        placeholder="请选择物流服务"
                        style="width: 444px"
                        @change="changeGlobalExpress"
                    >
                        <el-option
                            v-for="item in companySelectList"
                            :key="item.logisticsCompanyName"
                            :label="item.logisticsCompanyName"
                            :value="item.logisticsCompanyCode"
                        />
                    </el-select>
                </el-form-item>
                <template v-if="deliverDialogFormData.deliverType === 'PRINT_EXPRESS'">
                    <el-form-item label="发货地址" prop="addressaddress">
                        <el-select v-model="deliverDialogFormData.addressaddress" placeholder="请选择发货地址" style="width: 444px">
                            <el-option
                                v-for="item in deliveryAddressData"
                                :key="item.id"
                                :value="item.id"
                                :label="`${item.area?.join('')}${item.address}`"
                            />
                        </el-select>
                    </el-form-item>
                </template>
            </template>
        </el-form>
        <div class="delivery__export">
            <el-link type="primary" @click="exportAllToDeliveryOrder">导入全部待发货订单</el-link>
        </div>
        <split-table class="order-table" :data="orderDataList">
            <template #header="{ row }">
                <div class="order-table__header">
                    <span class="order-table__no">
                        <span>{{ row.no }}</span>
                    </span>
                    <span class="order-table__order-time">配送方式：快递配送</span>
                    <span class="order-table__order-time">下单：{{ row.createTime }}</span>
                </div>
            </template>

            <split-table-column label="商品" width="280px">
                <template #default="{ shopOrderItems }">
                    <div class="order-table__commodity">
                        <el-image style="width: 63px; height: 63px" fits="cover" :src="shopOrderItems?.[0]?.image" />
                        <div class="order-table__commodity--name">
                            <p>{{ shopOrderItems?.[0]?.productName }}</p>
                            <p>{{ shopOrderItems?.[0]?.specs?.join(';') }}</p>
                        </div>
                        <div class="order-table__commodity--info">
                            <span>￥{{ divTenThousand(shopOrderItems?.[0]?.salePrice) }}</span>
                            <span>{{ shopOrderItems?.[0]?.num }}件</span>
                        </div>
                    </div>
                </template>
            </split-table-column>
            <split-table-column label="待发数" width="100px">
                <template #default="{ shopOrderItems }">{{ shopOrderItems?.[0]?.num }}</template>
            </split-table-column>
            <split-table-column label="实付金额" width="150px" :is-mixed="true">
                <template #default="{ row }">
                    <div class="order-table__amount">
                        <div class="order-table__amount--price">{{ divTenThousand(row.payAmount) }}元</div>
                        <div class="order-table__amount--num">共{{ row.orderItems.reduce((pre: number, item: any) => pre + item.num, 0) }}件</div>
                    </div>
                </template>
            </split-table-column>
            <split-table-column label="收件人信息" width="150px" :is-mixed="true">
                <template #default="{ row }">
                    <div class="order-table__receiver">
                        <span>{{ row?.extra?.receiver?.name }}</span>
                        <span>{{ row?.extra?.receiver?.mobile }}</span>
                        <div>{{ row?.extra?.receiver?.area?.join('') }} {{ row?.extra?.receiver?.address }}</div>
                    </div>
                </template>
            </split-table-column>
            <split-table-column label="操作" width="250px" :is-mixed="true">
                <template #default="{ row }">
                    <div>
                        <el-form v-if="deliverDialogFormData.deliverType === 'EXPRESS'">
                            <el-form-item label="物流服务">
                                <el-select
                                    v-model="row.express.logisticsCompanyCode"
                                    placeholder="请选择物流服务"
                                    clearable
                                    @update:model-value="changeRowExpress($event, row?.no, 'logisticsCompanyCode')"
                                >
                                    <el-option
                                        v-for="item in companySelectList"
                                        :key="item.logisticsCompanyName"
                                        :label="item.logisticsCompanyName"
                                        :value="item.logisticsCompanyCode"
                                    />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="物流单号">
                                <el-input
                                    v-model="row.express.expressNo"
                                    placeholder="请输入物流单号"
                                    @update:model-value="changeRowExpress($event, row?.no, 'expressNo')"
                                />
                            </el-form-item>
                        </el-form>
                        <div style="text-align: center">
                            <el-link type="danger" @click="handleRemoveRow(row)">移除</el-link>
                        </div>
                    </div>
                </template>
            </split-table-column>
        </split-table>
        <el-row justify="center" style="width: 100%">
            <el-button round type="primary" @click="handleSubmit">确认</el-button>
            <!-- <el-button round @click="cancelBatchDeliver">取消</el-button> -->
            <el-popconfirm title="确定退出批量发货?" @confirm="cancelBatchDeliver">
                <template #reference>
                    <el-button round>取消</el-button>
                </template>
            </el-popconfirm>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import useBatchDelivery from './useBatchDelivery'
import SplitTable from '../components/split-table/SplitTable'
import SplitTableColumn from '../components/split-table/split-table-column.vue'
const changeDeliverType = (val) => {
    deliverDialogFormData.deliverType = val
}
const {
    deliverTypeMap,
    formRules,
    companySelectList,
    deliverDialogFormData,
    deliveryAddressData,
    AddressFn,
    regionData,
    orderDataList,
    divTenThousand,
    exportAllToDeliveryOrder,
    changeGlobalExpress,
    handleSubmit,
    cancelBatchDeliver,
    handleRemoveRow,
    changeRowExpress,
} = useBatchDelivery()
</script>

<style lang="scss" scoped>
@include b(delivery) {
    padding: 10px 20px;
    overflow-y: scroll;
    @include e(export) {
        text-align: right;
        padding: 15px;
    }
}
@include b(order-table) {
    height: 74%;
    transition: height 0.5s;
    word-break: break-all;
    margin-bottom: 20px;
    @include e(commodity) {
        width: 280px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        @include m(name) {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            display: box;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin: 0 10px;
        }
        @include m(info) {
            flex-shrink: 0;
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
    }
    @include e(receiver) {
        display: flex;
        justify-content: flex-start;
        align-items: flex-start;
        flex-direction: column;
        line-height: 1.5;
    }
    @include e(actions) {
        display: flex;
        flex-wrap: wrap;
        .el-link + .el-link {
            margin-left: 8px;
        }
    }
    @include e(header) {
        font-size: 11px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        width: 100%;
    }
}
</style>
