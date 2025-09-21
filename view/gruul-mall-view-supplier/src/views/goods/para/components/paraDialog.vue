<template>
    <div>
        <el-row align="middle">
            <el-col :span="3"> {{ isAttribute ? '属性' : '参数' }}名称 </el-col>
            <el-col :span="10">
                <el-input v-model="params.featuresValue.featureName" show-word-limit type="text" placeholder="15字以内" maxlength="15" />
            </el-col>
        </el-row>
        <el-row v-if="isAttribute" style="margin-top: 10px" align="middle">
            <el-col :span="3"> 是否必选 </el-col>
            <el-col :span="4">
                <el-radio-group v-model="params.featuresValue.isRequired" size="small">
                    <el-radio :label="true">是</el-radio>
                    <el-radio :label="false">否</el-radio>
                </el-radio-group>
            </el-col>
            <el-col :span="2" />
            <el-col :span="3"> 是否多选 </el-col>
            <el-col :span="4">
                <el-radio-group v-model="params.featuresValue.isMultiSelect" size="small">
                    <el-radio :label="true">是</el-radio>
                    <el-radio :label="false">否</el-radio>
                </el-radio-group>
            </el-col>
        </el-row>
        <el-row class="tableTitle">
            <el-col :span="isAttribute ? 8 : 18">{{ isAttribute ? '属性值' : '参数值' }} </el-col>
            <el-col v-if="isAttribute" :span="10"> 加价 (正数则加，负数则减，零则不变) </el-col>
            <el-col :span="6"> 操作 </el-col>
        </el-row>
        <div>
            <el-row v-for="(item, index) in params.featuresValue.featureValues" :key="index" align="middle" style="margin-top: 10px" :gutter="20">
                <el-col :span="isAttribute ? 8 : 18"> <el-input v-model="item.firstValue" type="text" :maxlength="isAttribute ? 10 : 20" /> </el-col>
                <el-col v-if="isAttribute" :span="10"> <el-input-number v-model="item.secondValue" :controls="false" style="width: 100%" /> </el-col>
                <el-col :span="6" style="text-align: center">
                    <el-link
                        v-if="index === params.featuresValue.featureValues.length - 1"
                        type="primary"
                        style="margin-right: 10px"
                        @click="handleAdd"
                        >添加</el-link
                    >
                    <el-link type="danger" @click="handleDel(index)">删除</el-link>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const props = defineProps({
    titleStatus: {
        type: String,
        default: '',
    },
    rowInfo: {
        type: Object,
        required: true,
    },
})

const isAttribute = computed(() => props.titleStatus === '属性')
const params = ref(props.rowInfo)

// {
//     featuresType: isAttribute.value ? 'ATTRIBUTE' : 'ARGUMENTS',
//     featuresValue: {
//         featureName: '',
//         isRequired: true,
//         isMultiSelect: true,
//         featureValues: [{ firstValue: '', secondValue: 0 }],
//     },
// }
const handleAdd = () => {
    params.value.featuresValue.featureValues = [...params.value.featuresValue.featureValues, { firstValue: '', secondValue: 0 }]
}
const handleDel = (index: number) => {
    params.value.featuresValue.featureValues = params.value.featuresValue.featureValues.filter((_: any, i: number) => index !== i)
}

defineExpose({
    params,
})
</script>

<style scoped lang="scss">
.tableTitle {
    margin-top: 20px;
    background-color: #efefef;
    text-align: center;
    padding: 10px 0;
    font-weight: 700;
}
</style>
