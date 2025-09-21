<template>
    <div class="details">
        <el-row :gutter="8">
            <!-- <el-col :span="3">商品名称：</el-col> -->
            <el-col :span="24" style="font-weight: 600">{{ dataInfo?.name }}</el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="dataInfo?.collectionUrl ? 8 : 12">
                <el-row :gutter="8"
                    >平台类目：{{ dataInfo?.platformCategoryName?.oneName }}/{{ dataInfo?.platformCategoryName?.twoName }}/{{
                        dataInfo?.platformCategoryName?.threeName
                    }}
                </el-row>
            </el-col>
            <el-col :span="dataInfo?.collectionUrl ? 8 : 12">
                <el-row :gutter="8"
                    >店铺类目：{{ dataInfo?.shopCategoryName?.oneName }}/{{ dataInfo?.shopCategoryName?.twoName }}/{{
                        dataInfo?.shopCategoryName?.threeName
                    }}
                </el-row>
            </el-col>
            <el-col v-if="dataInfo?.collectionUrl" :span="8">
                <el-row :gutter="8">采集地址：<el-link type="primary" @click="handleCopy(dataInfo.collectionUrl)">复制</el-link> </el-row>
            </el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="24">
                <el-table :data="dataInfo?.storageSkus" max-height="230px" size="small" :cell-style="{ height: '48px' }">
                    <template #empty> <ElTableEmpty /> </template>
                    <el-table-column label="规格">
                        <template #default="{ row }">
                            {{ row?.specs?.length ? row?.specs?.join(';') : '单规格' }}
                        </template>
                    </el-table-column>
                    <el-table-column v-if="dataInfo?.storageSkus && dataInfo?.storageSkus[0]?.image" label="SKU图">
                        <template #default="{ row }">
                            <el-image :src="row?.image" style="width: 50px; height: 50px" />
                        </template>
                    </el-table-column>
                    <el-table-column label="销售价(元)">
                        <template #default="{ row }">
                            {{ divTenThousand(row?.salePrice) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="划线价(元)">
                        <template #default="{ row }">
                            {{ divTenThousand(row?.price) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="重量(kg)" prop="weight" />
                </el-table>
            </el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="24">
                <!-- eslint-disable-next-line vue/no-v-html -->
                <div class="details-more" v-html="dataInfo?.detail" />
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import { doGetCommodityDetails } from '@/apis/good'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const $props = defineProps({
    commodityId: {
        type: String,
        default: '',
    },
    shopId: {
        type: String,
        default: '',
    },
})
const dataInfo = ref<any>({})
const getCommidityDetails = async () => {
    const { code, msg, data } = await doGetCommodityDetails($props.commodityId, { shopId: $props.shopId })
    if (code === 200) {
        dataInfo.value = data
    } else {
        ElMessage.error({ message: msg })
    }
}
getCommidityDetails()

const handleCopy = async (url: string) => {
    try {
        await toClipboard(url)
        ElMessage.success('复制成功')
    } catch (e) {
        ElMessage.error('复制失败')
    }
}
</script>

<style lang="scss" scoped>
.el-row {
    padding: 5px 0;
    line-height: 1.5;
}
@include b(details-more) {
    max-height: 500px;
    overflow-y: auto;
    :deep(img) {
        max-width: 100%;
    }
    :deep(video) {
        max-width: 100%;
    }
}
</style>
