<template>
    <ReleaseTitle title="商品属性">
        <span style="color: rgba(108, 108, 108, 1); font-size: 12px; font-weight: 400; margin-left: 50px">
            下单时选择诸如甜度、口味、加料等商品属性不同属性值价格不同，可提升商品客单价及用户体验，拖动调整顺序
        </span>
    </ReleaseTitle>
    <el-button
        v-if="!attributeList.length"
        type="primary"
        style="margin: 15px 0"
        @click="
            () => {
                ElMessage.warning('请先在属性参数中添加')
            }
        "
        >添加属性</el-button
    >
    <el-button v-else type="primary" style="margin: 15px 0" :disabled="!attributeList.length" @click="handleAddAttr">添加属性</el-button>
    <drag-table :limit-pid="false" @change-data="(data: any) => (spacObj.productAttribute = data)">
        <el-table v-if="spacObj.productAttribute.length" :data="spacObj.productAttribute" border>
            <el-table-column label="属性名称" width="130">
                <template #default="{ row, $index }">
                    <el-select :model-value="row" value-key="id" @change="(e) => (spacObj.productAttribute[$index] = cloneDeep(e))">
                        <el-option
                            v-for="ite in attributeList"
                            :key="ite.id"
                            :disabled="spacObj.productAttribute.some((ele) => ele.id === ite.id)"
                            :label="ite.featuresValue.featureName"
                            :value="ite"
                        />
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="属性类型" width="100">
                <template #default="{ row }">
                    <div style="text-align: center; font-size: 12px">
                        {{ handleText(row?.featuresValue?.isRequired, '必选', '选填') }}
                        ，
                        {{ handleText(row?.featuresValue?.isMultiSelect, '多选', ' 单选') }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="属性值">
                <template #default="{ row, $index }">
                    <VueDraggableNext v-if="row?.featuresValue?.featureValues" v-model="row.featuresValue.featureValues" :group="`arrTag${$index}`">
                        <el-tag
                            v-for="(tag, tagIndex) in row?.featuresValue?.featureValues"
                            :key="tagIndex"
                            closable
                            effect="plain"
                            size="large"
                            type="info"
                            :disable-transitions="false"
                            style="margin-right: 10px; margin-bottom: 10px"
                            @close="handleClose('productAttribute', $index, tagIndex)"
                        >
                            <span v-if="String(tag.secondValue).includes('-')">
                                {{ tag.firstValue }} - {{ divTenThousand(String(tag.secondValue).split('-')[1]) }}元
                            </span>
                            <span v-else>{{ tag.firstValue }} + {{ divTenThousand(tag.secondValue) }}元</span>
                        </el-tag>
                    </VueDraggableNext>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ $index }">
                    <el-icon size="22px" color="#FF4D4D" @click="delNormHandle('productAttribute', $index)"><Delete /></el-icon>
                </template>
            </el-table-column>
        </el-table>
    </drag-table>
    <ReleaseTitle title="商品参数">
        <span style="color: rgba(108, 108, 108, 1); font-weight: 400; font-size: 12px; margin-left: 50px"> 可拖动调整顺序 </span>
    </ReleaseTitle>
    <el-button
        v-if="!parameterList.length"
        type="primary"
        style="margin: 15px 0"
        @click="
            () => {
                ElMessage.warning('请先在属性参数中添加')
            }
        "
        >添加参数</el-button
    >
    <el-button v-else type="primary" style="margin: 15px 0" :disabled="!parameterList.length" @click="handleAddProductParameter">添加参数</el-button>
    <drag-table :limit-pid="false" @change-data="(data: any) => (spacObj.productParameter = data)">
        <el-table v-if="spacObj.productParameter.length" :data="spacObj.productParameter" border>
            <el-table-column label="属性名称" width="130">
                <template #default="{ row, $index }">
                    <el-select :model-value="row" value-key="id" @change="(e) => (spacObj.productParameter[$index] = cloneDeep(e))">
                        <el-option
                            v-for="ite in parameterList"
                            :key="ite.id"
                            :disabled="spacObj.productParameter.some((ele) => ele.id === ite.id)"
                            :label="ite.featuresValue.featureName"
                            :value="ite"
                        />
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="属性值">
                <template #default="{ row, $index }">
                    <VueDraggableNext v-if="row.featuresValue.featureValues" v-model="row.featuresValue.featureValues" :group="`goodArrTag${$index}`">
                        <el-tag
                            v-for="(tag, tagIndex) in row.featuresValue.featureValues"
                            :key="tagIndex"
                            closable
                            effect="plain"
                            size="large"
                            type="info"
                            :disable-transitions="false"
                            @close="handleClose('productParameter', $index, tagIndex)"
                        >
                            {{ tag.firstValue }}
                        </el-tag>
                    </VueDraggableNext>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ $index }">
                    <el-icon size="22px" color="#FF4D4D" @click="delNormHandle('productParameter', $index)"><Delete /></el-icon>
                </template>
            </el-table-column>
        </el-table>
    </drag-table>
    <!-- <div class="spac">
        <div class="goodsAttribut">
            <div class="title">
                商品属性
                <span style="color: rgba(108, 108, 108, 1); font-size: 12px; font-weight: 400; margin-left: 50px">
                    下单时选择诸如甜度、口味、加料等商品属性不同属性值价格不同，可提升商品客单价及用户体验，拖动调整顺序
                </span>
            </div>
            <el-row style="text-align: center; padding: 5px; background-color: #f8f8f8">
                <el-col :span="9" style="line-height: 33px">属性名称</el-col>
                <el-col :span="11" style="line-height: 33px">属性值</el-col>
                <el-col :span="4"> <el-button type="primary" @click="spacObj.productAttribute.push(defaultSpac())">添加属性</el-button></el-col>
            </el-row>

            <VueDraggableNext v-model="spacObj.productAttribute" group="arr">
                <el-row
                    v-for="(item, index) in spacObj.productAttribute"
                    :key="index"
                    style="background-color: #fff; border-bottom: 1px dashed #eee; padding: 10px; cursor: move"
                    align="middle"
                >
                    <el-col :span="8">
                        <div class="norm__item__name">
                            <el-select :model-value="item" value-key="id" @change="(e) => (spacObj.productAttribute[index] = cloneDeep(e))">
                                <el-option
                                    v-for="ite in attributeList"
                                    :key="ite.id"
                                    :disabled="spacObj.productAttribute.some((ele) => ele.id === ite.id)"
                                    :label="ite.featuresValue.featureName"
                                    :value="ite"
                                />
                            </el-select>
                            <el-button type="danger" class="norm__item__name--del" round @click="delNormHandle('productAttribute', index)">
                                删除
                            </el-button>
                        </div>
                        <div style="text-align: center; width: 210px; font-size: 12px; color: #bbb; margin-top: 10px">
                            {{ handleText(item.featuresValue.isRequired, '必选', '选填') }}
                            ，
                            {{ handleText(item.featuresValue.isMultiSelect, '多选', ' 单选') }}
                        </div>
                    </el-col>
                    <el-col :span="16">
                        <div>
                            <VueDraggableNext
                                v-if="item.featuresValue.featureValues"
                                v-model="item.featuresValue.featureValues"
                                :group="`arrTag${index}`"
                            >
                                <el-tag
                                    v-for="(tag, tagIndex) in item.featuresValue.featureValues"
                                    :key="tagIndex"
                                    closable
                                    :disable-transitions="false"
                                    @close="handleClose('productAttribute', index, tagIndex)"
                                >
                                    {{ tag.firstValue }} + {{ divTenThousand(tag.secondValue) }}元
                                </el-tag>
                            </VueDraggableNext>
                        </div>
                    </el-col>
                </el-row>
            </VueDraggableNext>
        </div>
        <div class="goodsAttribut">
            <div class="title">
                商品参数
                <span style="color: rgba(108, 108, 108, 1); font-weight: 400; font-size: 12px; margin-left: 50px"> 可拖动调整顺序 </span>
            </div>
            <el-row style="text-align: center; padding: 5px; background-color: #f8f8f8">
                <el-col :span="9" style="line-height: 33px">参数名称</el-col>
                <el-col :span="11" style="line-height: 33px">参数值</el-col>
                <el-col :span="4"> <el-button type="primary" @click="spacObj.productParameter.push(defaultSpac())">添加属性</el-button></el-col>
            </el-row>
            <VueDraggableNext v-model="spacObj.productParameter" group="goodArr">
                <el-row
                    v-for="(item, index) in spacObj.productParameter"
                    :key="index"
                    style="background-color: #fff; border-bottom: 1px dashed #eee; padding: 10px; cursor: move"
                    align="middle"
                >
                    <el-col :span="8">
                        <div class="norm__item__name">
                            <el-select :model-value="item" value-key="id" @change="(e) => (spacObj.productParameter[index] = cloneDeep(e))">
                                <el-option
                                    v-for="ite in parameterList"
                                    :key="ite.id"
                                    :disabled="spacObj.productParameter.some((ele) => ele.id === ite.id)"
                                    :label="ite.featuresValue.featureName"
                                    :value="ite"
                                />
                            </el-select>
                            <el-button type="danger" class="norm__item__name--del" round @click="delNormHandle('productParameter', index)"
                                >删除</el-button
                            >
                        </div>
                    </el-col>
                    <el-col :span="16">
                        <div>
                            <VueDraggableNext
                                v-if="item.featuresValue.featureValues"
                                v-model="item.featuresValue.featureValues"
                                :group="`goodArrTag${index}`"
                            >
                                <el-tag
                                    v-for="(tag, tagIndex) in item.featuresValue.featureValues"
                                    :key="tagIndex"
                                    closable
                                    :disable-transitions="false"
                                    @close="handleClose('productParameter', index, tagIndex)"
                                >
                                    {{ tag.firstValue }}
                                </el-tag>
                            </VueDraggableNext>
                        </div>
                    </el-col>
                </el-row>
            </VueDraggableNext>
        </div>
    </div> -->
</template>

<script setup lang="ts">
import { onActivated, onBeforeMount } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { doGetfeatureList } from '@/apis/good'
import { cloneDeep } from 'lodash-es'
import { useRoute } from 'vue-router'
import ReleaseTitle from '../ReleaseTitle.vue'
import { Delete } from '@element-plus/icons-vue'
import dragTable from '@/components/element-extends/element-plus-table-drag/drag-table.vue'
import { ElMessage } from 'element-plus'
const { divTenThousand } = useConvert()

const route = useRoute()
onActivated(() => {
    handleGetfeatureList()
    handleExtra()
})
onBeforeMount(() => {
    handleGetfeatureList()
    handleExtra()
})
const $parent: any = inject('form')

// 属性下拉框
const attributeList = ref<any[]>([])
// 参数下拉框
const parameterList = ref<any[]>([])
// 获取下拉框数据
const handleGetfeatureList = async () => {
    // 属性
    const { data: arr } = await doGetfeatureList({ featuresType: 'ATTRIBUTE', size: 100 })
    // 参数
    const { data: arr1 } = await doGetfeatureList({ featuresType: 'ARGUMENTS', size: 100 })
    attributeList.value = arr.records?.filter((item: any) => !!item?.featuresValue?.featureValues?.length)
    parameterList.value = arr1.records?.filter((item: any) => !!item?.featuresValue?.featureValues?.length)
}

// 默认规格
const defaultSpac = (): any => ({
    featuresType: '',
    id: '',
    shopId: '',
    featuresValue: {
        featureName: '',
        isRequired: '',
        isMultiSelect: '',
        featureValues: [],
    },
})
// 处理必选字段
const handleText = (value: any, success: string, fail: string) => {
    return value === '' ? '' : value ? success : fail
}

// 商品规格
const spacObj = ref<{
    productAttribute: any[]
    productParameter: any[]
}>({
    productAttribute: [],
    productParameter: [],
})

// 处理回显
const handleExtra = () => {
    // if (route.query.id) {
    //     spacObj.value.productAttribute = $parent.submitForm.value.extra.productAttributes.map((item: any) => ({
    //         id: item.id,
    //         featuresValue: item,
    //     }))
    //     spacObj.value.productParameter = $parent.submitForm.value.extra.productParameters.map((item: any) => ({
    //         id: item.id,
    //         featuresValue: item,
    //     }))
    // } else {
    //     spacObj.value.productAttribute = $parent.submitForm.value.productAttributes.map((item: any) => ({
    //         id: item.id,
    //         featuresValue: item,
    //     }))
    //     spacObj.value.productParameter = $parent.submitForm.value.productParameters.map((item: any) => ({
    //         id: item.id,
    //         featuresValue: item,
    //     }))
    // }
    let productAttributesData = [],
        productParametersData = []
    if ($parent.submitForm.value?.productAttributes?.length) {
        productAttributesData = $parent.submitForm.value?.productAttributes
        productAttributesData?.forEach((item: any) => {
            item.featuresValue = cloneDeep(item)
        })
    } else if ($parent.submitForm.value.extra?.productAttributes?.length) {
        productAttributesData = $parent.submitForm.value.extra?.productAttributes
        productAttributesData.forEach((item: any) => {
            item.featuresValue = cloneDeep(item)
        })
    }
    if ($parent.submitForm.value?.productParameters?.length) {
        productParametersData = $parent.submitForm.value?.productParameters
        productParametersData?.forEach((item: any) => {
            item.featuresValue = cloneDeep(item)
        })
    } else if ($parent.submitForm.value.extra?.productParameters?.length) {
        productParametersData = $parent.submitForm.value.extra?.productParameters
        productParametersData.forEach((item: any) => {
            item.featuresValue = cloneDeep(item)
        })
    }
    spacObj.value.productAttribute = productAttributesData
    spacObj.value.productParameter = productParametersData
}
// 删除小属性
const handleClose = (name: string, index: number, tagIndex: number) => {
    const filterValues = spacObj.value[name][index].featuresValue.featureValues.filter((item: any, index: number) => index !== tagIndex)
    if (filterValues?.length) {
        spacObj.value[name][index].featuresValue.featureValues = filterValues
    } else {
        delNormHandle(name, index)
    }
}
// 删除整条属性
const delNormHandle = (name: string, index: number) => {
    spacObj.value[name] = spacObj.value[name].filter((_: any, i: number) => i !== index)
}
// 处理商品规格
const handleSpacObj = () => {
    return {
        productAttribute: spacObj.value.productAttribute.map((item: any) => ({ ...item.featuresValue, id: item.id })),
        productParameter: spacObj.value.productParameter.map((item: any) => ({ ...item.featuresValue, id: item.id })),
    }
}
const handleAddAttr = () => {
    if (spacObj.value.productAttribute.length) {
        let isAllChoose = true
        spacObj.value.productAttribute.forEach((item) => {
            if (!item.id) isAllChoose = false
        })
        if (!isAllChoose) {
            return ElMessage.error({ message: '存在未选择的属性名称' })
        }
    }
    spacObj.value.productAttribute.push(defaultSpac())
}

const handleAddProductParameter = () => {
    if (spacObj.value.productParameter.length) {
        let isAllChoose = true
        spacObj.value.productParameter.forEach((item) => {
            if (!item.id) isAllChoose = false
        })
        if (!isAllChoose) {
            return ElMessage.error({ message: '存在未选择的商品参数' })
        }
    }
    spacObj.value.productParameter.push(defaultSpac())
}
defineExpose({
    handleSpacObj,
})
</script>

<style scoped lang="scss">
.spac {
    background-color: #f8f8f8;
    padding-top: 1px;
    .goodsAttribut {
        background-color: #fff;
        padding: 10px;
        border-radius: 5px;
        margin-top: 20px;
        .title {
            font-weight: 700;
            background-color: #fff;
            padding: 15px;
            margin: 0 -10px;
        }
        .el-button {
            margin: 0 20px;
        }
        .line {
            display: flex;
            align-items: center;
            margin: 20px 0;
            .item {
                border: 1px solid #bbbbbb;
                padding: 5px;
                margin-right: 7px;
                border-radius: 3px;
            }
            .deleteItem {
                cursor: pointer;
                font-weight: 700;
            }
        }
        .el-tag {
            margin: 5px;
        }
    }
}
</style>
