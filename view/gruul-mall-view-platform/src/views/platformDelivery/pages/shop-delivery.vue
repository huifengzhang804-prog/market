<template>
    <div class="content_container">
        <el-form :model="shopDeliveryConfigModel" class="form" :rules="rules">
            <el-form-item label="自营店铺用户订单" prop="shopDeliver">
                <el-radio-group v-model="shopDeliveryConfigModel.shopDeliver">
                    <el-radio value="PLATFORM">平台发货</el-radio>
                    <el-radio value="OWN">店铺自行发货</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="自营供应商用户订单" prop="supplierDeliver">
                <el-radio-group v-model="shopDeliveryConfigModel.supplierDeliver">
                    <el-radio value="PLATFORM">平台发货</el-radio>
                    <el-radio value="OWN">供应商自行发货</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <div class="common-tips">平台发货是指平台可协助自营商家处理快递/虚拟配送的用户订单，不包括采购、同城、自提订单</div>
    </div>
    <div class="submit">
        <el-button type="primary" @click="handleSave">保存</el-button>
    </div>
</template>

<script lang="ts" setup>
import { doGetShopDeliveryConfig, doPutShopDeliveryConfig } from '@/apis/set/platformDelivery'
import { ElMessage } from 'element-plus'

const shopDeliveryConfigModel = reactive({
    id: '',
    shopDeliver: '',
    supplierDeliver: '',
})

const rules = reactive({
    shopDeliver: [{ required: true, message: '', trigger: 'blur' }],
    supplierDeliver: [{ required: true, message: '', trigger: 'blur' }],
})

const initialDeliveryConfig = async () => {
    const { data } = await doGetShopDeliveryConfig()
    Object.keys(shopDeliveryConfigModel).forEach((key) => {
        const modelKey = key as keyof typeof shopDeliveryConfigModel
        shopDeliveryConfigModel[modelKey] = data?.[modelKey] || shopDeliveryConfigModel[modelKey]
    })
}

const handleSave = async () => {
    const { code, msg } = await doPutShopDeliveryConfig(shopDeliveryConfigModel)
    if (code === 200) {
        ElMessage.success({ message: msg || '更新成功' })
        initialDeliveryConfig()
    } else {
        ElMessage.error({ message: msg || '更新失败' })
    }
}
initialDeliveryConfig()
</script>

<style lang="scss" scoped>
.desc {
    color: #999;
}
@include b(submit) {
    position: absolute;
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    height: 52px;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
i {
    position: relative;
    left: -22px;
}
</style>
