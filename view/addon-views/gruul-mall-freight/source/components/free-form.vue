<script lang="ts" setup>
import { ref, defineProps, defineEmits, PropType, computed, watch, nextTick } from 'vue'
import AreaChoose from '@/components/q-area-choose/area-choose.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import uuid from '@/utils/uuid'
import { Obj } from '@/utils/types'

interface ChooseAreaItem {
  upperName: string
  length: number
  lowerName: string[]
}

interface RegionList {
  label: string
  value: string
  isCheck?: boolean
  isDisable?: boolean
  isIndeterminate?: boolean
  children: RegionList[]
}
interface TableData {
  id: string
  amountNum: number
  logisticsId: number
  pieceNum: number
  postType: string
  region: Obj[]
  weight: number
}

const $props = defineProps({
  tableData: {
    type: Array as PropType<TableData[]>,
    // eslint-disable-next-line vue/require-valid-default-prop
    default: [
      {
        id: uuid(10),
        amountNum: 0, //数量
        logisticsId: 0, // 物流编号
        pieceNum: 0, //件数
        postType: 'PKGS',
        region: [], //地区
        weight: 0, //重量
      },
    ],
  },
  canChooseArea: {
    type: Array as PropType<{ regionJson: ChooseAreaItem[] }[]>,
    default: () => [],
  },
  isPKGS: { type: Boolean, required: true },
  isEdit: { type: Boolean, required: true },
})
const emit = defineEmits(['update:tableData'])
const _tableData = useVModel($props, 'tableData', emit)
const currentId = ref('0')
const freeDialogTag = ref(false)
/**
 * 包邮条件
 */
const postType = [
  {
    value: 'PKGS',
    label: '件数',
  },
  {
    value: 'MONEY',
    label: '金额',
  },
  {
    value: 'PKGS_MONEY',
    label: '件数+金额',
  },
]
const postType_ = [
  {
    value: 'WEIGHT',
    label: '重量',
  },
  {
    value: 'MONEY',
    label: '金额',
  },
  {
    value: 'WEIGHT_MONEY',
    label: '重量+金额',
  },
]

watch(
  () => $props.isPKGS,
  (val, oldval) => {
    if (!$props.isEdit) {
      if (val !== oldval && val) {
        _tableData.value.forEach((item) => {
          item.postType = 'PKGS'
        })
      } else {
        _tableData.value.forEach((item) => {
          item.postType = 'WEIGHT'
        })
      }
    }
  },
  {
    immediate: true,
  },
)
const isFirstShow = ref(true)
watch(
  () => $props.canChooseArea,
  () => {
    // 如果是第一次编辑回显 则不清空
    if ($props.isEdit && isFirstShow.value) {
      // 记录一下 之后的 watch 需要清空
      setTimeout(() => {
        isFirstShow.value = false
      }, 800)
      return
    }
    // 清空table
    _tableData.value.forEach((item) => {
      item.region = []
    })
  },
  {
    deep: true,
  },
)

const handleAddRegion = (id: string) => {
  freeDialogTag.value = true
  currentId.value = id
}

const handleShowDialog = (id: string) => {
  freeDialogTag.value = true
  currentId.value = id
}

// checkbox 数据 预先处理好 isDisable  isCheck
const checkboxData = computed<RegionList[]>(() => {
  const currentItem = _tableData.value.find((item) => item.id === currentId.value)
  // 合并 $props.canChooseArea 的 每一项的 regionJson
  const canChooseArea: RegionList[] = []
  // 扁平化 $props.canChooseArea
  const flatCanChooseArea = $props.canChooseArea.map((item) => item.regionJson).flat()
  flatCanChooseArea.forEach((item) => {
    const findItem = canChooseArea.find((i) => item.upperName === i.label)
    if (!findItem) {
      canChooseArea.push({
        label: item.upperName,
        value: item.upperName,
        children: item.lowerName.map((i) => ({
          label: i,
          value: i,
          children: [],
        })),
      })
    } else {
      item.lowerName.forEach((i) => {
        if (!findItem.children.find((j) => j.label === i)) {
          findItem.children.push({
            label: i,
            value: i,
            children: [],
          })
        }
      })
    }
  })
  if (!currentItem) {
    return canChooseArea
  } else {
    // 非当前行选中的区域不能选择 打上 isDisable
    // 合并非当前行的region
    const otherRegionJson = _tableData.value
      .filter((item) => item.id !== currentId.value)
      .map((item) => item.region)
      .flat(1)
    // 当前行选中的区域可以继续选择 打上 isCheck
    canChooseArea.forEach((item) => {
      item.isCheck = currentItem.region.some((i) => i.upperName === item.label)
      item.isDisable = otherRegionJson.some((i) => i.upperName === item.label)
      item.children?.forEach((i) => {
        i.isCheck = currentItem.region.some((j) => j.lowerName.includes(i.label))
        i.isDisable = otherRegionJson.some((j) => j.lowerName.includes(i.label))
      })
    })
    return canChooseArea
  }
})
/**
 * 获取选中类名
 */
const getAreaName = (data: ChooseAreaItem[]) => {
  return data
    .map((item) => {
      if (item.lowerName && item && item.lowerName?.length === item?.length) {
        return item.upperName
      }
      return `${item.upperName}(${item.lowerName && item.lowerName.length ? item.lowerName.length : ''}/${item && item.length ? item.length : ''})`
    })
    .join(',')
}
/**
 * 区域选择回调
 */
const handleChangeArea = (e: ChooseAreaItem[]) => {
  const findItem = _tableData.value.find((item) => item.id === currentId.value)
  if (findItem) {
    findItem.region = e
  }
}
/**
 * 删除
 */
const HandleSecondTabItem = (index: number, row: any) => {
  if (index === -1) {
    const item = {
      id: uuid(10),
      amountNum: 0, //数量
      logisticsId: 0, // 物流编号
      pieceNum: 0, //件数
      postType: $props.isPKGS ? 'PKGS' : 'WEIGHT',
      region: [], //地区
      weight: 0, //重量
    }
    _tableData.value.push(item)
  } else {
    if (index === 0) return ElMessage.error('至少保留一个配送区域')
    _tableData.value = _tableData.value.filter((item) => item.id !== row.id)
  }
}

function weightMoneyAndParcelMoney(type: any) {
  return ['PKGS_MONEY', 'WEIGHT_MONEY'].includes(type)
}

function weightAndMoney(type: any) {
  return ['PKGS', 'WEIGHT'].includes(type)
}
</script>

<template>
  <el-table :cell-style="{ padding: '30px 0' }" :data="_tableData" :header-cell-style="{ fontSize: '14px', color: '#333' }">
    <el-table-column label="选择区域" prop="name" width="300">
      <template #default="{ row }">
        <div v-if="row.region.length" @click="handleShowDialog(row.id)">{{ getAreaName(row.region) }}</div>
        <el-button link type="primary" @click="handleAddRegion(row.id)">
          {{ row.region.length ? '编辑' : '添加区域' }}
        </el-button>
      </template>
    </el-table-column>
    <el-table-column :label="$props.isPKGS ? '首件数（件）' : '首重（kg）'" width="500" align="center" prop="name">
      <template #default="{ row }">
        <el-row :gutter="20" align="middle">
          <el-col :span="6">
            <el-select v-if="$props.isPKGS" v-model="row.postType" placeholder="Select" size="small">
              <el-option v-for="item in postType" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-select v-else v-model="row.postType" placeholder="Select" size="small">
              <el-option v-for="item in postType_" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-col>
          <el-col :span="18">
            <el-row v-if="weightAndMoney(row.postType)" align="middle">
              <el-col :span="1"><span> 在 </span></el-col>
              <el-col :span="5">
                <el-input-number
                  v-if="$props.isPKGS"
                  v-model="row.pieceNum"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="0"
                  :step="0.1"
                  class="number-input"
                />
                <el-input-number
                  v-else
                  v-model="row.weight"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="1"
                  :step="0.1"
                  class="number-input"
                />
              </el-col>
              <el-col :span="6">
                <span>{{ $props.isPKGS ? '件' : 'kg' }}以上包邮 </span></el-col
              >
            </el-row>
            <el-row v-else-if="row.postType === 'MONEY'" align="middle">
              <el-col :span="1"><span> 满 </span></el-col>
              <el-col :span="5">
                <el-input-number
                  v-model="row.amountNum"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="2"
                  :step="0.1"
                  class="number-input"
                />
              </el-col>
              <el-col :span="6"><span>元以上包邮 </span></el-col>
            </el-row>
            <!-- -->
            <el-row v-if="weightMoneyAndParcelMoney(row.postType)" align="middle">
              <el-col :span="1"><span>在</span></el-col>
              <el-col :span="5">
                <el-input-number
                  v-if="$props.isPKGS"
                  v-model="row.pieceNum"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="0"
                  :step="0.1"
                  class="number-input"
                />
                <el-input-number
                  v-else
                  v-model="row.weight"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="1"
                  :step="0.1"
                  class="number-input"
                />
              </el-col>
              <el-col :span="4">
                <span>{{ $props.isPKGS ? '件' : 'kg' }}以上,</span>
              </el-col>
              <el-col :span="1"><span>满</span></el-col>
              <el-col :span="5">
                <el-input-number
                  v-model="row.amountNum"
                  :controls="false"
                  :input-style="{ textAlign: 'center' }"
                  :min="0"
                  :precision="2"
                  :step="0.1"
                  class="number-input"
                />
              </el-col>
              <el-col :span="6"><span>元以上包邮</span></el-col>
            </el-row>
          </el-col>
        </el-row>
      </template>
    </el-table-column>
    <el-table-column label="操作" prop="name" align="center" width="120" fixed="right">
      <template #default="{ row, $index }">
        <div class="dialogTab_container__right_btn">
          <el-button v-if="_tableData.length - 1 === $index" link type="primary" @click="HandleSecondTabItem(-1, row)"> 添加 </el-button>
          <el-button :disabled="_tableData.length === 1" link type="danger" @click="HandleSecondTabItem(_tableData.length - 1, row)">删除 </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
  <AreaChoose v-model:show="freeDialogTag" :checkboxData="checkboxData" @change="handleChangeArea" />
</template>

<style lang="scss" scoped>
@include b(number-input) {
  width: 80px;
}

@include b(dialogTab_container) {
  @include e(input) {
    text-align: center;
  }
  @include e(right_btn) {
    display: flex;
    justify-content: space-evenly;
  }
}
</style>
