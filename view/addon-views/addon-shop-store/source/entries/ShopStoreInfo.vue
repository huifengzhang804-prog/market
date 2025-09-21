<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import QMap from '@/components/q-map/q-map.vue'
import { FormRules, ElMessage } from 'element-plus'
import { REGEX_MOBILE } from '@/libs/validate'
import { StoreFormType } from '../index'
//引入api
import { PostAddStore, getstoreDetail, PostUpdateStore } from '../apis'
// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
  dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = (val: string) => {
  if (disabledType.value) {
    return
  }
  dialogVisible.value = true
  parameterId.value = val
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集  showstoreImg0
const croppedFileChange = (val: any) => {
  if (parameterId.value === 'storeLogo') submitForm.value.storeLogo = val?.shift()
  if (parameterId.value === 'showstoreImg0') showstoreImg.value[0] = val
  if (parameterId.value === 'showstoreImg1') showstoreImg.value[1] = val
  if (parameterId.value === 'showstoreImg2') showstoreImg.value[2] = val
}
const checkedFileLists = (val: any) => {
  if (parameterId.value === 'storeLogo') submitForm.value.storeLogo = val?.shift()
  if (parameterId.value === 'showstoreImg0') showstoreImg.value[0] = val
  if (parameterId.value === 'showstoreImg1') showstoreImg.value[1] = val
  if (parameterId.value === 'showstoreImg2') showstoreImg.value[2] = val
}
// 选择素材 d

const mapKey = ref(0)
const $route = useRoute()
const $router = useRouter()
const StoreFormRef = ref()
const showstoreImg = ref<string[]>([])
//收集表单
const submitForm = ref<StoreFormType>({
  id: '',
  storeName: '',
  storeLogo: '',
  storeImg: '',
  storePhone: '',
  functionaryName: '',
  functionaryPhone: '',
  businessStartTime: '00:00:00',
  businessEndTime: '23:59:59',
  detailedAddress: '',
  startDeliveryDay: 0,
  endDeliveryDay: 0,
  location: {
    type: 'Point',
    coordinates: ['121.583336', '29.990282'],
  },
  shopAssistantList: [],
})
//表单rules
const storeFormRules = reactive<FormRules>({
  storeName: [
    {
      required: true,
      message: '请填写门店名称',
      trigger: 'blur',
    },
  ],
  storeLogo: [
    {
      required: true,
      message: '请上传门店logo',
      trigger: 'blur',
    },
  ],
  storeImg: [
    {
      required: true,
      message: '请上传门店图片',
      trigger: 'blur',
    },
  ],
  functionaryName: [
    {
      required: true,
      message: '请填写负责人姓名',
      trigger: 'blur',
    },
  ],
  functionaryPhone: [
    {
      required: true,
      validator: checkPhone,
      trigger: 'blur',
    },
  ],
  businessStartTime: [
    {
      required: true,
      message: '请填写营业开始时间',
      trigger: 'blur',
    },
  ],
  businessEndTime: [
    {
      required: true,
      message: '请填写营业结束时间',
      trigger: 'blur',
    },
  ],
  startDeliveryDay: [
    {
      required: true,
      message: '请填写开始提货时间',
      trigger: 'blur',
    },
  ],
  endDeliveryDay: [
    {
      required: true,
      message: '请填写结束提货时间',
      trigger: 'blur',
    },
  ],
  detailedAddress: [
    {
      required: true,
      message: '请选择地址',
      trigger: 'blur',
    },
    { min: 2, max: 200, message: '输入长度必须在2~200以内', trigger: 'blur' },
  ],
})
//地图组件初始化标识
const firstAssignment = ref(true)
const disabledType = ref(false)

getStoreInfo()

async function getStoreInfo() {
  if ($route.query.shopId && $route.query.id) {
    if ($route.query.lookType === 'OnlyLook') {
      disabledType.value = true
    }
    const { code, data, msg } = await getstoreDetail($route.query.shopId as string, { id: $route.query.id })
    if (code === 200 && data) {
      showstoreImg.value = data.storeImg.split(',')
      submitForm.value = data
      mapKey.value = Date.now()
    } else {
      ElMessage.error(msg ? msg : '获取失败')
    }
  }
}

const changeMapHandle = (e: { address: string; position: string[] }) => {
  // 查看不对详细地址进行重写
  if (disabledType.value) {
    return
  }
  // 编辑状态下初始化不对详细地址进行重写
  if ($route.query.id && firstAssignment.value) {
    firstAssignment.value = false
    return
  }
  submitForm.value.detailedAddress = e.address
  submitForm.value.location.coordinates = e.position
}

// 校验手机号
function checkPhone(rule: any, value: any, callback: any) {
  if (value === '') {
    callback(new Error('请填写手机号'))
  } else if (!REGEX_MOBILE(value)) {
    callback(new Error('请填写正确的手机号'))
  } else {
    callback()
  }
}

//提交新增门店回调
const submitHandle = async () => {
  if (!StoreFormRef.value) return
  if (!showstoreImg.value.length) {
    ElMessage.error('请上传门店图片')
    return
  }
  submitForm.value.storeImg = showstoreImg.value
    .map((item) => {
      if (Array.isArray(item) && item.length) {
        return item[0].trim()
      } else {
        return item.trim()
      }
    })
    .join(',')
  const isValidate = await StoreFormRef.value.validate()
  if (!isValidate) return
  if (submitForm.value.startDeliveryDay > submitForm.value.endDeliveryDay) return ElMessage.error('结束提货时间应大于等于开始提货时间')

  if (!submitForm.value.id) {
    handlePostStore()
  } else {
    handleUpdateStore()
  }
}
/**
 * 保存新增门店
 */
const handlePostStore = async () => {
  const { code, msg } = await PostAddStore(submitForm.value)
  if (code === 200) {
    ElMessage.success('新增门店成功')
    $router.push({ path: '/shop/store' })
  } else {
    ElMessage.error(msg ? msg : '新增失败')
  }
}
/**
 * 修改门店信息
 */
const handleUpdateStore = async () => {
  const { code, msg } = await PostUpdateStore(submitForm.value)
  if (code === 200) {
    ElMessage.success('修改门店信息成功')
    $router.push({ path: '/shop/store' })
  } else {
    ElMessage.error(msg ? msg : '修改门店信息失败')
  }
}
const makeRange = (start: number, end: number) => {
  const result: number[] = []
  for (let i = start; i <= end; i++) {
    result.push(i)
  }
  return result
}
const disabledHours = () => {
  if (submitForm.value.businessEndTime) {
    const endTime = Number(submitForm.value.businessEndTime.split(':')[0])
    return makeRange(endTime + 1, 24)
  }
  return []
}
const disabledMinutes = () => {
  return makeRange(1, 59)
}
const disabledSeconds = () => {
  return makeRange(1, 59)
}
const disabledHoursEnd = () => {
  if (submitForm.value.businessStartTime) {
    const endTime = Number(submitForm.value.businessStartTime.split(':')[0])
    return makeRange(0, endTime - 1).concat(endTime - 1, 24)
  }
  return []
}
const disabledMinutesEnd = () => {
  return makeRange(0, 58)
}
const disabledSecondsEnd = () => {
  return makeRange(0, 58)
}
</script>

<template>
  <div class="q_plugin_container f1">
    <el-form ref="StoreFormRef" :model="submitForm" :rules="storeFormRules" label-width="100px">
      <el-form-item label="门店名称" prop="storeName">
        <el-input v-model="submitForm.storeName" :disabled="disabledType" maxlength="25" placeholder="请输入门店名称"></el-input>
      </el-form-item>
      <el-form-item label="负责人姓名" prop="functionaryName">
        <el-input v-model="submitForm.functionaryName" :disabled="disabledType" maxlength="8" placeholder="请输入负责人姓名"></el-input>
      </el-form-item>
      <el-form-item label="负责人电话" prop="functionaryPhone">
        <el-input v-model="submitForm.functionaryPhone" :disabled="disabledType" maxlength="11" placeholder="请输入负责人电话"></el-input>
      </el-form-item>
      <el-form-item label="营业时间">
        <el-row style="display: flex; width: 100%">
          <div style="width: 150px">
            <el-form-item label-width="0px" prop="businessStartTime">
              <el-time-picker
                v-model="submitForm.businessStartTime"
                :disabled="disabledType"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                :disabled-seconds="disabledSeconds"
                format="HH:mm:ss"
                placeholder="开始时间"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </div>
          <div style="margin: 0 10px">至</div>
          <div style="width: 150px">
            <el-form-item label-width="0px" prop="businessEndTime">
              <el-time-picker
                v-model="submitForm.businessEndTime"
                :disabled="disabledType"
                :disabled-hours="disabledHoursEnd"
                :disabled-minutes="disabledMinutesEnd"
                :disabled-seconds="disabledSecondsEnd"
                format="HH:mm:ss"
                placeholder="结束时间"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </div>
        </el-row>
      </el-form-item>
      <el-form-item label="提货时间">
        <el-form-item label-width="0px" prop="startDeliveryDay">
          <div style="display: flex">
            <el-input-number
              v-model="submitForm.startDeliveryDay"
              :controls="false"
              :disabled="disabledType"
              :min="0"
              :step="1"
              step-strictly
              style="width: 110px"
            />
            <div class="DeliveryDay">天后开始提货</div>
          </div>
        </el-form-item>
        <el-form-item label-width="0px" prop="endDeliveryDay">
          <div style="display: flex; margin-left: 13px">
            <el-input-number
              v-model="submitForm.endDeliveryDay"
              :controls="false"
              :disabled="disabledType"
              :min="submitForm.startDeliveryDay"
              :step="1"
              step-strictly
              style="width: 110px"
            />
            <div class="DeliveryDay">天后结束提货</div>
          </div>
        </el-form-item>
      </el-form-item>
      <el-form-item label="详细地址" prop="detailedAddress">
        <el-input v-model="submitForm.detailedAddress" :disabled="disabledType" maxlength="60" placeholder="请选择经纬度"></el-input>
      </el-form-item>
      <el-form-item>
        <q-map :key="mapKey" :coordinates="submitForm.location.coordinates" :disabled="disabledType" @change="changeMapHandle" />
      </el-form-item>
      <el-form-item label="门店logo" prop="storeLogo">
        <div
          v-if="!submitForm.storeLogo && !disabledType"
          :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
          @click="buttonlFn('storeLogo')"
        >
          <span class="selectMaterialStyle__span">+</span>
        </div>
        <img
          v-else-if="submitForm.storeLogo"
          :src="submitForm.storeLogo"
          alt=""
          :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
          @click="buttonlFn('storeLogo')"
        />

        <el-form-item label="门店图片" prop="storeImg">
          <div
            v-if="!showstoreImg[0] && !disabledType"
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg0')"
          >
            <span class="selectMaterialStyle__span">+</span>
          </div>
          <img
            v-else-if="showstoreImg[0]"
            :src="showstoreImg[0]"
            alt=""
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg0')"
          />

          <div
            v-if="!showstoreImg[1] && !disabledType"
            style="margin: 0 20px"
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg1')"
          >
            <span class="selectMaterialStyle__span">+</span>
          </div>
          <img
            v-else-if="showstoreImg[1]"
            :src="showstoreImg[1]"
            style="margin: 0 20px"
            alt=""
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg1')"
          />

          <div
            v-if="!showstoreImg[2] && !disabledType"
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg2')"
          >
            <span class="selectMaterialStyle__span">+</span>
          </div>
          <img
            v-else-if="showstoreImg[2]"
            :src="showstoreImg[2]"
            alt=""
            :class="{ selectMaterialStyle: true, disabled_point: disabledType }"
            @click="buttonlFn('showstoreImg2')"
          />
        </el-form-item>
      </el-form-item>
    </el-form>
    <div class="storeForm__tool">
      <el-button round @click="$router.go(-1)"> {{ !disabledType ? '取消' : '返回' }}</el-button>
      <el-button v-if="!disabledType" style="margin-left: 40px" round type="primary" @click="submitHandle"> 保存</el-button>
    </div>
    <!-- 选择素材 e -->
    <selectMaterial
      :dialog-visible="dialogVisible"
      :upload-files="1"
      @select-material-fn="selectMaterialFn"
      @cropped-file-change="croppedFileChange"
      @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
  </div>
</template>

<style lang="scss" scoped>
.q_plugin_container {
  padding-top: 30px;
  overflow-y: scroll;
  :deep(.el-form) {
    padding-left: 16px;
    padding-right: 16px;
  }
  .disabled_point {
    cursor: not-allowed !important;
    position: relative;
    &::after {
      position: absolute;
      width: 100%;
      height: 100%;
      content: '';
      top: 0;
      left: 0;
      background: rgba($color: #000000, $alpha: 0.2);
      z-index: 100;
    }
  }
  @include b(storeForm) {
    overflow: hidden;
    @include e(tool) {
      @include flex();
      position: sticky;
      bottom: 0;
      padding: 15px 0px;
      justify-content: center;
      box-shadow: 0 0px 10px 0px #d5d5d5;
      background-color: white;
      z-index: 999;
      margin-top: auto;
    }
  }

  .DeliveryDay {
    width: 108px;
    background: #d5d5d5;
    text-align: center;
    font-size: 14px;
    color: #333333;
  }

  @include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
      color: #999;
      font-size: 20px;
    }
  }
}
</style>
