<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doPostDistribute, doGetDistributeConfig } from '../apis'
import type { DistributeConfigParamsType } from '../index'
import type { FormInstance, FormRules } from 'element-plus'
import useConvert from '@/composables/useConvert'
import { ref, reactive, computed } from 'vue'
import QEdit from '@/components/q-editor/q-edit.vue'

const { divHundred, mulHundred, mulTenThousand, divTenThousand } = useConvert()
// 协议显隐开关
const agreementType = ref(false)
// 攻略显隐开关
const MethodType = ref(false)
// 分销配置
const distributeConfig = reactive<{ config: DistributeConfigParamsType }>({
  config: {
    level: 'ONE',
    condition: {
      types: ['APPLY', 'CONSUMPTION'],
      requiredAmount: 0,
    },
    precompute: 'DISTRIBUTOR',
    protocol: '111',
    playMethods: '333',
    purchase: false,
    // poster: 'https://obs.xiaoqa.cn/gruul/20221118/63837a0427294d169073d0478fdad173.jpeg',
    shareType: 'FIXED_AMOUNT',
    one: 0,
    two: 0,
    three: 0,
  },
})
const getDisLevel = computed(() => {
  return distributeConfig.config.level === 'ONE' ? 1 : distributeConfig.config.level === 'TWO' ? 2 : 3
})
const rules = reactive<FormRules>({
  level: [{ required: true, message: '', trigger: 'blur' }],
  poster: [{ required: true, message: '请设置推广海报', trigger: 'blur' }],
  purchase: [{ required: true, message: '', trigger: 'blur' }],
  precompute: [{ required: true, message: '', trigger: 'blur' }],
  condition: [{ required: true, validator: conditionValidatePass, trigger: 'blur' }],
  protocol: [{ required: true, message: '请设置协议', trigger: 'blur' }],
  playMethods: [{ required: true, message: '请设置攻略玩法', trigger: 'blur' }],
  shareType: [{ required: true, message: '', trigger: 'blur' }],
  one: [{ required: true, validator: levelOneValidatePass, trigger: 'change' }],
  two: [{ required: true, validator: levelTwoValidatePass, trigger: 'change' }],
  three: [{ required: true, validator: levelThreeValidatePass, trigger: 'change' }],
})
const formRef = ref<FormInstance | undefined>()
initDisConfig()
const handleShowAgreement = () => {
  agreementType.value = true
}
const handleShowMethod = () => {
  MethodType.value = true
}
const handleSaveConfig = async () => {
  if (formRef.value) {
    formRef.value.validate(async (valid, fields) => {
      if (valid) {
        const tempObj = JSON.parse(JSON.stringify(distributeConfig.config))
        tempObj.condition.requiredAmount = mulTenThousand(tempObj.condition.requiredAmount)
        if (distributeConfig.config.shareType === 'RATE') {
          tempObj.one = mulHundred(tempObj.one)
          tempObj.two = mulHundred(tempObj.two)
          tempObj.three = mulHundred(tempObj.three)
        } else {
          tempObj.one = mulTenThousand(tempObj.one)
          tempObj.two = mulTenThousand(tempObj.two)
          tempObj.three = mulTenThousand(tempObj.three)
        }
        const { code, msg } = await doPostDistribute(deleteAttribute(tempObj))
        if (code === 200) {
          ElMessage.success('保存成功')
        } else {
          ElMessage.error(msg || '保存失败')
        }
      }
    })
  }
}
/**
 * 切换佣金设置
 */
const handleChangeShareType = () => {
  // 切换配置初始化数据
  distributeConfig.config.one = 0
  distributeConfig.config.two = 0
  distributeConfig.config.three = 0
}
function conditionValidatePass(rule: any, value: any, callback: any) {
  if (!value.types.length) {
    return callback(new Error('请选择分销商条件'))
  } else if (value.types.includes('CONSUMPTION') && Number(value.requiredAmount) <= 0) {
    return callback(new Error('设置金额应大于零'))
  }
  callback()
}

function levelOneValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeConfig.config.shareType
  if (shareType === 'UNIFIED') {
    return callback()
  }
  const numberValue = Number(value)
  if (numberValue <= 0) {
    return callback(new Error('一级佣金应大于等于零'))
  }
  if (distributeConfig.config.level !== 'ONE' && distributeConfig.config.two && numberValue <= Number(distributeConfig.config.two)) {
    return callback(new Error('一级佣金值应大于二级佣金'))
  }
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在0-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  callback()
}
function levelTwoValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeConfig.config.shareType
  if (shareType === 'UNIFIED' || distributeConfig.config.level === 'ONE') return callback()
  const numberValue = Number(value)
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在0-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  if (distributeConfig.config.one && numberValue >= Number(distributeConfig.config.one)) {
    return callback(new Error('二级佣金应小于一级佣金'))
  }
  if (distributeConfig.config.level === 'THREE' && distributeConfig.config.three && numberValue <= Number(distributeConfig.config.three)) {
    return callback(new Error('二级佣金应大于三级佣金'))
  }
  callback()
}
function levelThreeValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeConfig.config.shareType
  if (shareType === 'UNIFIED' || distributeConfig.config.level !== 'THREE') return callback()
  const numberValue = Number(value)
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在1-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  if (distributeConfig.config.two && numberValue >= Number(distributeConfig.config.two)) {
    return callback(new Error('三级佣金应小于二级佣金'))
  }
  callback()
}
/**
 * 删除校验参数
 */
function deleteAttribute(config: DistributeConfigParamsType) {
  if (config.level === 'ONE') {
    delete config.two
    delete config.three
  } else if (config.level === 'TWO') {
    delete config.three
  }
  return config
}
async function initDisConfig() {
  const { code, data } = await doGetDistributeConfig()
  if (code === 200) {
    if (data.shareType === 'RATE') {
      data.one = divHundred(data.one)
      data.two = divHundred(data.two)
      data.three = divHundred(data.three)
    } else {
      data.one = divTenThousand(data.one)
      data.two = divTenThousand(data.two)
      data.three = divTenThousand(data.three)
    }
    data.condition.requiredAmount = divTenThousand(data.condition.requiredAmount)
    distributeConfig.config = data
  } else {
    ElMessage.error('获取分销配置失败')
  }
}
</script>

<template>
  <div style="background-color: #fff; overflow-y: auto">
    <el-form ref="formRef" :model="distributeConfig.config" label-width="120" :rules="rules" style="margin-left: 16px">
      <div class="col mb26">
        <div class="col__icon" :style="{ background: '#555CFD' }"></div>
        <div>基础设置</div>
      </div>
      <el-form-item label="分销层级" class="mb33" prop="level">
        <el-radio-group v-model="distributeConfig.config.level">
          <el-radio value="ONE">一级分销</el-radio>
          <el-radio value="TWO">二级分销</el-radio>
          <el-radio value="THREE">三级分销</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="成为分销商条件" class="mb33" prop="condition">
        <el-checkbox-group v-model="distributeConfig.config.condition.types">
          <el-checkbox value="APPLY">申请</el-checkbox>
          <br />
          <el-checkbox value="CONSUMPTION">
            <el-row justify="start" align="middle" style="width: 300px">
              <span>累计消费金额大于等于</span>
              <el-input
                v-model="distributeConfig.config.condition.requiredAmount"
                type="number"
                :controls="false"
                style="width: 142px; margin-left: 5px"
              >
                <template #append>元</template>
              </el-input>
            </el-row>
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="分销内购" class="mb33" prop="purchase">
        <el-radio-group v-model="distributeConfig.config.purchase">
          <el-radio :value="false">关闭</el-radio>
          <el-radio :value="true">开启</el-radio>
        </el-radio-group>
        <span style="margin-left: 30px; color: #9a9a9a">开启后分销员自己购买分销商品可获得一级佣金</span>
      </el-form-item>
      <el-form-item label="预计赚" class="mb33" prop="precompute">
        <el-radio-group v-model="distributeConfig.config.precompute">
          <el-radio value="DISTRIBUTOR">只对分销商(员)展示【预计赚】 </el-radio
          ><el-tooltip class="box-item" effect="dark" content="控制是否在商品详情页内展示【预计赚】" placement="top">
            <i class="iconfont icon-wenhao" style="position: relative; left: -30px; color: #999"></i>
          </el-tooltip>
          <el-radio value="NEVER">对所有人都不展示</el-radio>
          <el-radio value="ALL">对所有人展示【预计赚】</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="协议内容" prop="protocol">
        <el-button link type="primary" @click="handleShowAgreement">编辑</el-button>
      </el-form-item>
      <el-form-item label="赚钱攻略">
        <el-button link type="primary" @click="handleShowMethod">编辑</el-button>
      </el-form-item>
      <!-- <div class="col mb26">
            <div class="col__icon" :style="{ background: '#F57373' }"></div>
            <div>通用设置</div>
        </div>
        <el-form-item label="推广海报" prop="poster">
            <q-upload v-model:src="distributeConfig.config.poster" :width="120" :height="244"></q-upload>
        </el-form-item> -->
      <!-- <el-form-item>
            <div class="tip">请选择尺寸750*1334左右的图片，大小不超过300KB(二维码位置是固定的)</div>
        </el-form-item> -->
      <div class="col mb26" style="display: flex; justify-content: space-between; width: 100%">
        <div style="display: flex">
          <div class="col__icon" :style="{ background: '#555CFD' }"></div>
          <div>佣金设置</div>
        </div>
        <el-tooltip class="box-item" effect="dark" placement="top">
          <template #content>
            <div>
              1、按百分比：佣金 = 商品实付金额 * 购买商品数 * 百分比<br />
              2、按固定金额：佣金 = 固定金额 * 购买商品数 <br />
              3、风险提示：请谨慎使用固定金额分佣，可能存在资金损失风险
            </div>
          </template>
          <i class="iconfont icon-wenhao" style="color: #999; margin-right: 12px"></i>
        </el-tooltip>
      </div>
      <el-form-item label="佣金设置" prop="shareType">
        <el-radio-group v-model="distributeConfig.config.shareType" @change="handleChangeShareType">
          <el-radio value="FIXED_AMOUNT">固定金额</el-radio>
          <el-radio value="RATE">百分比</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="getDisLevel >= 1" label="一级佣金" prop="one">
        <el-input v-model="distributeConfig.config.one" type="number" style="width: 200px">
          <template #append>{{ distributeConfig.config.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
        </el-input>
        <!-- <q-price-input v-model="distributeConfig.config.one" /> -->
      </el-form-item>
      <el-form-item v-if="getDisLevel >= 2" label="二级佣金" prop="two">
        <el-input v-model="distributeConfig.config.two" type="number" style="width: 200px">
          <template #append>{{ distributeConfig.config.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="getDisLevel === 3" label="三级佣金" prop="three">
        <el-input v-model="distributeConfig.config.three" type="number" style="width: 200px">
          <template #append>{{ distributeConfig.config.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
        </el-input>
      </el-form-item>
    </el-form>
    <div class="save">
      <el-button type="primary" round @click="handleSaveConfig">保存</el-button>
    </div>
    <el-dialog v-model="agreementType" title="编辑协议">
      <q-edit v-model:content="distributeConfig.config.protocol"></q-edit>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="agreementType = false">取消</el-button>
          <el-button type="primary" @click="agreementType = false">确认</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="MethodType" title="编辑攻略">
      <q-edit v-model:content="distributeConfig.config.playMethods"></q-edit>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="MethodType = false">取消</el-button>
          <el-button type="primary" @click="MethodType = false">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(col) {
  margin: 16px 16px 0 0;
  background: #f6f8fa;
  height: 50px;
  @include flex(flex-start);
  font-size: 13px;
  color: #515151;
  @include e(icon) {
    width: 4px;
    height: 16px;
    margin: 0 17px 0 19px;
  }
}
:deep(.el-form-item__label) {
  margin-right: 20px;
  color: #666666;
}
@include b(save) {
  width: 100%;
  margin-top: auto;
  @include flex;
  justify-content: center;
  align-items: center;
  height: 52px;
  background-color: #fff;
  box-shadow: 0px 0px 16px 0px rgba(0, 0, 0, 0.07);
  position: sticky;
  bottom: 0;
}
.mb33 {
  margin-bottom: 33px;
}
.mb26 {
  margin-bottom: 26px;
}
.tip {
  font-size: 13px;
  color: #d5d5d5;
}
</style>
