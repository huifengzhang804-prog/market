<script lang="ts" setup>
import { ref, inject, reactive, onActivated, computed } from 'vue'
import GoodNorms from './goodNorms/GoodNorms.vue'
import GoodNormTable from './goodNorms/GoodNormTable.vue'
import GoodOnly from './goodNorms/GoodOnly.vue'
import { NormListType, NormType } from './goodNorms'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetCommoditySku } from '../../../../../apis'
import { useRoute } from 'vue-router'
import useConvert from '@/composables/useConvert'
import ReleaseTitle from './ReleaseTitle.vue'

const $emit = defineEmits(['changeSpecificationType'])
const $route = useRoute()
const { divTenThousand } = useConvert()
const currentFormRef = ref()
const GoodOnlyRef = ref()
const $parent: any = inject('form')
const submitForm = $parent.submitForm
const memoSpecList = ref([])
const goodNormTable = ref()
// 单规格实售价校验
const realPriceValidatePass = (rule: any, value: any, callback: any) => {
  if (value <= 0) {
    callback(new Error('输入数值请大于0'))
  } else if (Number(submitForm.value.skus[0].price) < Number(value)) {
    callback(new Error('实售价大于指导价'))
  } else {
    callback()
  }
}
// 单规格指导价校验
const validatePass = (rule: any, value: any, callback: any) => {
  if (value <= 0) {
    callback(new Error('输入数值请大于0'))
  } else if (Number(submitForm.value.skus[0].salePrice) > Number(value)) {
    callback(new Error('实售价大于指导价'))
  } else {
    callback()
  }
}
const formRules = reactive({
  'skus[0].price': [
    {
      required: true,
      validator: validatePass,
      trigger: 'blur',
    },
  ],
  'skus[0].salePrice': [
    {
      required: true,
      validator: realPriceValidatePass,
      trigger: 'blur',
    },
  ],
})

onActivated(() => {
  dataDisplay()
})

const changeClassHandle = async (e: { type: string; list: NormType[] }) => {
  const { type, list } = e
  // 删除多规格转换单规格时候为skus中添加一条单规格数据
  if (!list.length) {
    submitForm.value.skus = [
      {
        id: '',
        image: '',
        initSalesVolume: 0,
        limitNum: 0,
        initStock: 0,
        limitType: 'UNLIMITED',
        price: 0,
        productId: '',
        salePrice: 0,
        shopId: '',
        stockType: 'UNLIMITED',
        weight: 0,
      },
    ]
    return
  }
  submitForm.value.specGroups = list
  if (type !== 'addSpec') {
    submitForm.value.skus = caleDescartes(list)
  }
}
/**
 * 改变规格列表
 */
const changeNormList = (e: NormListType[]) => {
  submitForm.value.skus = e
}

/**
 * 是否是多规格
 * skus长度大于1或者skus中第一个规格的长度大于1
 */
const isMutiSpec = computed(() => {
  return submitForm.value?.specGroups?.length
})

const radio = ref<'SINGLE_SPEC' | 'MUTI_SPEC'>('SINGLE_SPEC')

const handleChange = (e: any) => {
  $emit('changeSpecificationType', e)
  radio.value = e
}
async function dataDisplay() {
  if ($route.query.id) {
    // 当是代销商品的时候，查询sku信息的时候商家ID是供应商ID，其他情况为当前店铺的id号
    const shopId = submitForm.value?.sellType === 'CONSIGNMENT' ? submitForm.value?.supplierId : useShopInfoStore().shopInfo.id
    const { code: skuCode, data: skuData } = await doGetCommoditySku(shopId, $route.query.id)
    if (skuCode !== 200) {
      ElMessage.error('获取商品sku失败')
      return
    }
    console.log(!submitForm.value.skus?.[0].id)
    if (!submitForm.value.skus?.[0].id) {
      // 处理价格以厘作为单位
      skuData.skus.forEach((item: any) => {
        item.initStock = 0
        item.price = Number(divTenThousand(item.price))
        item.salePrice = Number(divTenThousand(item.salePrice))
      })
      submitForm.value = { ...submitForm.value, ...skuData }
    }
    handleChange(isMutiSpec.value ? 'MUTI_SPEC' : 'SINGLE_SPEC')
  } else {
    handleChange(isMutiSpec.value ? 'MUTI_SPEC' : 'SINGLE_SPEC')
  }
}
/**
 * 规格组合
 */
function caleDescartes(list: NormType[]) {
  let assemble = []
  assemble = list.map((item) => {
    return item.children
  })
  if (assemble.length <= 1) {
    assemble = assemble[0]
  } else {
    assemble = assemble.reduce((total, currentValue) => {
      let res: any[] = []
      total.forEach((t) => {
        currentValue.forEach((cv) => {
          if (t instanceof Array) {
            res.push([...t, cv])
          } else {
            res.push([t, cv])
          }
        })
      })
      return res
    })
  }
  return assemble.map((item) => {
    return addSku(item)
  })
}
/**
 * 添加规格
 */
function addSku(productSpecNames: any) {
  let tempArr = []
  if (Array.isArray(productSpecNames)) {
    tempArr = productSpecNames.map((item) => {
      return item.name
    })
  } else {
    tempArr.push(productSpecNames.name)
  }
  const havespecs = submitForm.value.skus.find((item) => {
    return item.specs.every((item) => tempArr.includes(item))
  })
  if (havespecs) {
    havespecs.specs = tempArr
  }
  return havespecs
    ? havespecs
    : {
        specs: tempArr,
        stockType: 'LIMITED',
        initSalesVolume: 0,
        limitType: 'UNLIMITED',
        limitNum: 0,
        image: '',
        price: 0,
        initStock: 0,
        salePrice: 0,
        weight: '0',
      }
}
defineExpose({
  currentFormRef,
  GoodOnlyRef,
})
</script>
<template>
  <el-form ref="currentFormRef" :model="submitForm" :rules="formRules" label-width="100px">
    <release-title title="规格类型" />
    <el-form-item label="规格类型" style="margin-top: 10px">
      <el-radio-group v-model="radio" size="small" :disabled="$route.query.type === 'ProcurementRelease'" @change="handleChange">
        <el-radio value="SINGLE_SPEC">单规格</el-radio>
        <el-radio value="MUTI_SPEC">多规格</el-radio>
      </el-radio-group>
    </el-form-item>
    <div v-if="radio === 'MUTI_SPEC'">
      <GoodNorms :class-arr="submitForm.specGroups" @change-class="changeClassHandle" />
      <GoodNormTable
        v-if="submitForm.specGroups && submitForm.specGroups.length > 0"
        ref="goodNormTable"
        :list="submitForm.skus"
        :class-arr="submitForm.specGroups"
        :memo-spec-list="memoSpecList"
        @change-norm-list="changeNormList"
      />
    </div>
    <div v-if="radio === 'SINGLE_SPEC' && submitForm.skus?.length">
      <el-row :gutter="8">
        <el-col :span="12">
          <el-form-item :label="'划线价'" prop="skus[0].price">
            <div class="inputnumber">
              <el-input-number
                v-model="submitForm.skus[0].price"
                :controls="false"
                :max="999999"
                :precision="2"
                class="input_number com__input--width"
              >
              </el-input-number>
              <div class="inputnumber__icon">元</div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="销售价" prop="skus[0].salePrice">
            <div class="inputnumber">
              <el-input-number
                v-model="submitForm.skus[0].salePrice"
                :max="999999"
                :precision="2"
                :controls="false"
                class="input_number com__input--width"
              ></el-input-number>
              <div class="inputnumber__icon">元</div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="初始销量">
            <div class="inputnumber">
              <el-input-number
                v-model="submitForm.skus[0].initSalesVolume"
                placeholder="0"
                :controls="false"
                class="input_number com__input--width"
              ></el-input-number>
              <div class="inputnumber__icon">个</div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="重量">
            <div class="inputnumber">
              <el-input-number v-model="submitForm.skus[0].weight" :controls="false" class="input_number com__input--width">
                <template #append>.com</template>
              </el-input-number>
              <div class="inputnumber__icon">kg</div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="限购类型">
            <el-select v-model="submitForm.skus[0].limitType" style="width: 200px">
              <el-option :label="'限购'" value="PRODUCT_LIMITED"></el-option>
              <el-option v-if="submitForm.specGroups.length" label="规格限购" value="SKU_LIMITED"></el-option>
              <el-option :label="'不限购'" value="UNLIMITED"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库存类型">
            <el-select
              v-model="submitForm.skus[0].stockType"
              :disabled="$route.query.type === 'ProcurementRelease' || submitForm.productType !== 'REAL_PRODUCT'"
              style="width: 200px"
            >
              <el-option :label="'有限库存'" value="LIMITED"></el-option>
              <el-option :label="'无限库存'" value="UNLIMITED"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="限购数量">
            <div class="inputnumber">
              <el-input-number
                v-model="submitForm.skus[0].limitNum"
                :disabled="submitForm.skus[0].limitType === 'UNLIMITED'"
                :min="0"
                :controls="false"
                class="input_number com__input--width"
              >
              </el-input-number>
              <div class="inputnumber__icon">个</div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="'库存'" prop="skus[0].initStock">
            <div class="inputnumber">
              <el-input-number
                v-model="submitForm.skus[0].initStock"
                style="width: 200px"
                :disabled="submitForm.skus[0].stockType === 'UNLIMITED'"
                :max="999999"
                :min="0"
                :precision="0"
                :controls="false"
                class="input_number com__input--width"
              ></el-input-number>
              <div class="inputnumber__icon">个</div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <GoodOnly ref="GoodOnlyRef" />
  </el-form>
</template>
<style lang="scss">
// 规格
@include b(spec) {
  // 类型
  @include e(type) {
  }
}

@include b(norm) {
  font-size: 14px;
  margin: 20px 0;
  @include flex('flex-start', 'flex-start');
  @include e(title) {
    margin: 0 20px 0 30px;
  }
  @include e(item) {
    color: #606266;
  }
  @include e(item__name) {
    display: flex;
    @include m(del) {
      margin-left: 10px;
    }
  }
  @include e(item__value) {
    @include flex('flex-start');
    @include m(mg34) {
      margin-right: 34px;
    }
  }
  @include e(item__value__content) {
    @include flex();
    margin: 20px 0;
    cursor: move;
  }
}

@include b(specDetail) {
  font-size: 14px;
  text-align: center;
  @include e(table) {
    width: 800px;
    display: flex;
    flex-direction: column;
    overflow-x: auto;
    background: #fcfcfc;
  }
  @include e(title) {
    margin: 0 30px;
  }
  @include flex('flex-start', 'flex-start');
  @include e(table__title) {
    line-height: 50px;
    @include flex('flex-start', 'flex-start');
    @include m(primary) {
      flex-shrink: 0;
      width: 150px;
    }
    @include m(custom) {
      flex-shrink: 0;
      width: 100px;
    }
    @include m(header) {
      @include flex();
      flex-shrink: 0;
    }
  }
  @include e(table__item) {
    @include flex('flex-start', 'flex-start');
    @include m(font) {
      flex-shrink: 0;
      width: 100px;
      line-height: 70px;
      color: #606266;
    }
    @include m(input) {
      flex-shrink: 0;
      width: 150px;
      @include flex();
      & > .el-input {
        width: 80%;
      }
      & > .el-input__inner {
        width: 80%;
      }
    }
    @include m(upload) {
      width: 150px;
      @include flex();
    }
    & .el-form-item--small.el-form-item {
      margin: 0 !important;
    }
  }
}
@include b(batch) {
  margin: 30px;
  @include flex(flex-start, center);
  @include e(title) {
  }
  @include e(list) {
    @include flex(flex-start);
    cursor: pointer;
    color: #409eff;
    margin-left: 20px;
  }
  @include e(list-item) {
    @include flex();
    margin-right: 10px;
  }
}

@include b(com) {
  @include e(input) {
    @include m(width) {
      width: 500px;
    }
  }
}
.serveMsg {
  width: 400px;
  display: flex;
}
@include b(inputnumber) {
  position: relative;
  @include e(icon) {
    width: 34px;
    height: 31px;
    position: absolute;
    right: 1px;
    top: 2px;
    background: #e6e8eb;
    color: #909399;
    text-align: center;
  }
}
.input_number {
  .el-input__inner {
    text-align: left;
  }
}
.com__input--width {
  width: 100%;
}
</style>
