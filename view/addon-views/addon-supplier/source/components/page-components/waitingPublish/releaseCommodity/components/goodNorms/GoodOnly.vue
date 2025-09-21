<script lang="ts" setup>
import { ref, onActivated, onBeforeMount, inject } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { doGetfeatureList } from '../../../../../../apis'
import { cloneDeep } from 'lodash-es'
import { useRoute } from 'vue-router'
import useConvert from '@/composables/useConvert'
import ReleaseTitle from '../ReleaseTitle.vue'
import { Delete } from '@element-plus/icons-vue'
import dragTable from '@/components/element-plus-table-drag/drag-table.vue'

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
  attributeList.value = arr.records
  parameterList.value = arr1.records
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
const handleText = (value: any, fail: string, success: string) => {
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
  if (route.query.id) {
    spacObj.value.productAttribute =
      $parent.submitForm.value.extra?.productAttributes?.map((item: any) => ({
        id: item.id,
        featuresValue: item,
      })) || []
    spacObj.value.productParameter =
      $parent.submitForm.value.extra?.productParameters?.map((item: any) => ({
        id: item.id,
        featuresValue: item,
      })) || []
  } else {
    spacObj.value = {
      productAttribute: [],
      productParameter: [],
    }
  }
}
// 删除小属性
const handleClose = (name: string, index: number, tagIndex: number) => {
  spacObj.value[name][index].featuresValue.featureValues = spacObj.value[name][index].featuresValue.featureValues.filter(
    (item: any, index: number) => index !== tagIndex,
  )
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
defineExpose({
  handleSpacObj,
})
</script>

<template>
  <ReleaseTitle title="商品属性">
    <span style="color: rgba(108, 108, 108, 1); font-size: 12px; font-weight: 400; margin-left: 50px">
      下单时选择诸如甜度、口味、加料等商品属性不同属性值价格不同，可提升商品客单价及用户体验，拖动调整顺序
    </span>
  </ReleaseTitle>
  <el-button type="primary" style="margin: 15px 0" @click="spacObj.productAttribute.push(defaultSpac())">添加属性</el-button>
  <drag-table :limit-pid="false" @change-data="(data) => (spacObj.productAttribute = data)">
    <el-table :data="spacObj.productAttribute" border>
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
            {{ handleText(row.featuresValue.isRequired, '必选', '选填') }}
            ，
            {{ handleText(row.featuresValue.isMultiSelect, '多选', ' 单选') }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="属性值">
        <template #default="{ row, $index }">
          <VueDraggableNext v-if="row.featuresValue.featureValues" v-model="row.featuresValue.featureValues" :group="`arrTag${$index}`">
            <el-tag
              v-for="(tag, tagIndex) in row.featuresValue.featureValues"
              :key="tagIndex"
              closable
              effect="plain"
              size="large"
              type="info"
              :disable-transitions="false"
              style="margin-right: 10px; margin-bottom: 10px"
              @close="handleClose('productAttribute', $index, tagIndex)"
            >
              {{ tag.firstValue }} + {{ divTenThousand(tag.secondValue) }}元
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
  <ReleaseTitle title="商品参数" style="margin-top: 15px">
    <span style="color: rgba(108, 108, 108, 1); font-weight: 400; font-size: 12px; margin-left: 50px"> 可拖动调整顺序 </span>
  </ReleaseTitle>
  <el-button type="primary" style="margin: 15px 0" @click="spacObj.productParameter.push(defaultSpac())">添加参数</el-button>
  <drag-table :limit-pid="false" @change-data="(data) => (spacObj.productParameter = data)">
    <el-table :data="spacObj.productParameter" border>
      <el-table-column label="参数名称" width="130">
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
      <el-table-column label="参数值">
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
</template>

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
