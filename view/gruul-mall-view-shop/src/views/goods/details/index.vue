<template>
    <div class="details">
        <el-row :gutter="8">
            <el-col :span="24" style="font-weight: 600">
                <span
                    v-if="dataInfo?.labelId"
                    :style="{ color: dataInfo?.productLabel?.fontColor, background: dataInfo?.productLabel?.backgroundColor }"
                    style="padding: 5px 10px"
                >
                    {{ dataInfo?.productLabel?.name }}
                </span>
                {{ dataInfo?.name }}</el-col
            >
        </el-row>
        <el-row :gutter="8">
            <el-col :span="10">
                <el-row :gutter="8">
                    <el-col :span="6">平台类目：</el-col>
                    <el-col :span="18"
                        >{{ dataInfo?.platformCategoryName?.oneName }}/{{ dataInfo?.platformCategoryName?.twoName }}/{{
                            dataInfo?.platformCategoryName?.threeName
                        }}</el-col
                    >
                </el-row>
            </el-col>
            <el-col :span="10">
                <el-row :gutter="8">
                    <el-col :span="6">店铺类目：</el-col>
                    <el-col :span="18"
                        >{{ dataInfo?.shopCategoryName?.oneName }}/{{ dataInfo?.shopCategoryName?.twoName }}/{{
                            dataInfo?.shopCategoryName?.threeName
                        }}</el-col
                    >
                </el-row>
            </el-col>
            <el-col v-if="dataInfo.collectionUrl" :span="4">
                <el-row :gutter="8">
                    <el-col :span="24">采集地址：<span style="color: #0892ee; cursor: pointer" @click="copyadress"> 复制</span></el-col>
                </el-row>
            </el-col>
        </el-row>
        <el-row :gutter="8">
            <el-col :span="24">
                <el-table :data="dataInfo?.storageSkus" max-height="230px" size="small" :cell-style="{ height: '45px' }">
                    <template #empty> <ElTableEmpty /> </template>
                    <el-table-column label="规格">
                        <template #default="{ row }">
                            {{ row?.specs?.join(';') }}
                        </template>
                    </el-table-column>
                    <el-table-column v-if="dataInfo[0]?.image" label="SKU图">
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

const { toClipboard } = useClipboard()
const { divTenThousand } = useConvert()
const $props = defineProps({
    commodityId: {
        type: String,
        default: '',
    },
})
const dataInfo = ref<any>({})
const getCommidityDetails = async () => {
    const { code, msg, data } = await doGetCommodityDetails($props.commodityId)
    if (code === 200) {
        dataInfo.value = data
    } else {
        ElMessage.error({ message: msg })
    }
}
getCommidityDetails()
const copyadress = async () => {
    try {
        await toClipboard(dataInfo.value.collectionUrl)
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
.details-more {
    max-height: 440px;
    overflow-y: auto;
    :deep(img) {
        max-width: 100%;
    }
    :deep(video) {
        max-width: 100%;
    }
}
</style>
