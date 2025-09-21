<template>
  <!--        <el-form ref="form" :model="form" :rules="rules">-->
  <el-form ref="form">
    <el-form-item prop="mobile">
      <el-input v-model="form.mobile"  prefix-icon="el-icon-mobile-phone" placeholder="请输入手机号码" />
    </el-form-item>

    <el-form-item prop="imgCode">
      <el-input prefix-icon="el-icon-timer" v-model="form.imgcode" placeholder="请输入图片验证码"/>
      <img @click="refreshImageCaptcha"
           :src="form.imageCaptcha"
           title="已失效" class="reg-code" alt="点击刷新">
    </el-form-item>

    <el-form-item prop="smscode">
      <el-input prefix-icon="el-icon-message" v-model="form.smsCode" placeholder="请输入短信验证码"/>
      <el-button v-if="form.showTimer" disabled class="resend-button" size="small" type="text">
        {{form.waitSeconds}}秒后重新发送
      </el-button>
      <el-button v-else @click="sendSmsCode"  class="reg-code" size="small" type="text">发送验证码</el-button>
    </el-form-item>
    <el-button @click="login" type="primary" style="width: 100%">登录</el-button>
  </el-form>
</template>

<script setup>
import {reactive} from 'vue'

const form = reactive({
  showTimer:false,
  waitSeconds:180,
  imageCaptcha:'https://tse1-mm.cn.bing.net/th/id/R-C.d4d863967c99ea6c9dbe505f9e68016a?rik=l5Rvh716CtbduA&riu=http%3a%2f%2fpic.616pic.com%2fys_bnew_img%2f00%2f58%2f36%2fhETS14h5rO.jpg&ehk=8Lv%2bxlnTWQ%2fHFBvsPlY0PRxBBthF56y2vwAQ6vm4H7M%3d&risl=&pid=ImgRaw&r=0',
  mobile:'',
  imgCode:'',
  smsCode:''
})

const login = ()=>{
}
const refreshImageCaptcha = ()=>{

}
const sendSmsCode = ()=>{
}
</script>

<style scoped lang="scss">
.el-form{
  width: 100%;
  height: 100%;
}
.el-button{
  width: 100%;
}

.reg-code{
  position: absolute;
  right: 2px;
  top: 2px;
  font-size: 12px;
  color: $rows-color-primary;
  height: 34px;
  width: 75px;
}
.resend-button{
  position: absolute;
  right: 0;
  top: 2px;
  font-size: 12px;
  color: $rows-color-primary;
  height: 34px;
  width: 95px;
}
</style>