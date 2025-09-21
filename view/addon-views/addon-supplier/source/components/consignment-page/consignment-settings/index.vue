<script lang="ts" setup>
import useConsignmentConfig from './useConsignmentSettings'

const { rules, formModel, formRef, handleChangeType, handleSubmitSettingData } = useConsignmentConfig()
</script>

<template>
  <div class="settings">
    <div class="settings__explain">
      <div class="settings__title">代销设置说明</div>
      <div class="settings__container">
        <p class="settings__container--line">
          1、设价方式： 统一设价 、自定义设价 ； 价格优先级：<span class="text-red">自定义设价 > 统一设价</span>
        </p>
        <p class="settings__container--line">2、自定义设价：在上架(铺货) 或 编辑 时可对具体的某个商品进行自定义价格设定</p>
        <p class="settings__container--line">3、统一设价：统一设价为默认方案，其优先级低于自定义设价；即有自定义价格则以自定义价格为准</p>
        <p class="settings__container--line">
          4、无论何种设价方式，系统均可实现随供货价的调整而<span class="text-red">自动调整</span>代销商品的划线价、销售价 (划线价 >= 销售价)
          ，避免亏损
        </p>
      </div>
    </div>
    <div class="settings__form">
      <span class="setting_form--title">统一设价</span>
      <el-form ref="formRef" :model="formModel" :rules="rules">
        <el-form-item>
          <el-radio-group v-model="formModel.type" @change="handleChangeType">
            <el-radio value="RATE">按比例设价</el-radio>
            <el-radio value="REGULAR">固定金额设价</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="formModel.type === 'RATE'">
          <el-form-item prop="sale">
            <span>销售价 = 供货价 + （供货价 * &nbsp;</span>
            <el-input-number v-model="formModel.sale" :controls="false" :precision="2" :min="0" :max="100" />
            <span>&nbsp;% ）</span>
          </el-form-item>
          <el-form-item prop="scribe">
            <span>划线价 = 销售价 + （销售价 * &nbsp;</span>
            <el-input-number v-model="formModel.scribe" :controls="false" :precision="2" :min="0" :max="100" />
            <span>&nbsp;% ）</span>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item prop="sale">
            <span>销售价 = 供货价 + &nbsp;</span>
            <el-input-number v-model="formModel.sale" :controls="false" :precision="2" :min="0" />
            <span>&nbsp;元</span>
          </el-form-item>
          <el-form-item prop="scribe">
            <span>划线价 = 销售价 + &nbsp;</span>
            <el-input-number v-model="formModel.scribe" :controls="false" :precision="2" :min="0" />
            <span>&nbsp;元</span>
          </el-form-item>
        </template>
      </el-form>
    </div>
  </div>
  <div class="settings__footer">
    <el-button type="primary" @click="handleSubmitSettingData">保存</el-button>
  </div>
</template>

<style lang="scss" scoped>
@include b(settings) {
  padding: 16px;
  @include e(explain) {
    padding: 25px 20px;
    background-color: rgb(85, 92, 253, 0.06);
    color: #333;
  }
  @include e(title) {
    padding: 0 15px;
    line-height: 1.3;
  }
  @include e(container) {
    padding: 0 15px;
    @include m(line) {
      line-height: 1.3;
    }
    .text-red {
      color: #f00;
    }
  }
  @include e(form) {
    margin-top: 30px;
    display: flex;
    & > span {
      margin-right: 15px;
      margin-top: 8px;
    }
  }
}
@include b(settings__footer) {
  margin-top: auto;
  width: 100%;
  padding: 17px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 10px rgba(46, 44, 44, 0.3);
}
</style>
