<template>
  <div class="login-box">
    <el-tabs stretch>
      <el-tab-pane>
        <template #label>
          <span><i class="el-icon-lock"></i>密码登录</span>
        </template>
        <password-form/>
      </el-tab-pane>
      <el-tab-pane>
        <template #label>
          <span slot="label"><i class="el-i-anquan"></i>快捷登录</span>
        </template>
        <sms-code-form/>
      </el-tab-pane>
    </el-tabs>

    <el-row style="margin-top: 20px;">
      <span class="link-s">忘记密码?</span>
    </el-row>
  </div>
</template>

<script setup>
import PasswordForm from './PasswordForm.vue'
import SmsCodeForm from './SmsCodeForm.vue'
</script>

<style lang="scss" scoped>
.link-s{
  color:$rows-color-primary;
  text-decoration: none;
}
.login-box{
  width: 350px;
  height: 350px;
  padding: 10px;
  margin: 150px auto 0;
  border: 1px solid #b8c0b8;
  border-radius: 9px;
}



.login-page{
  width: 100%;
  height: 100%;
}
</style>