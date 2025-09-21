<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-21 15:54:07
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:36:02
-->
<template>
  <div class="SignFlow" v-loading="loading">
    <div class="SignFlow__tabs">
      <div class="SignFlow__tabs--title">LOG IN</div>
      <div
        class="SignFlow__tabs--switch"
        @click="changeSignType('SignInByQrCode')"
      >
        <!-- <div class="toolTip-right">扫码登录</div>
        <i class="iconfont iconQR_Code"></i> -->
      </div>
    </div>
    <el-form
      :model="signForm"
      :rules="signRules"
      ref="signFormRef"
      hide-required-asterisk
      label-position="left"
      label-width="85px"
      class="SignInForm"
    >
      <div class="SignInForm__tabs">
        <div
          @click="switchStatus(1)"
          class="SignInForm__tab"
          :class="{ 'SignInForm__tab--active': signForm.loginType === 1 }"
        >
          登录
        </div>
        <!-- <div
          @click="switchStatus(2)"
          class="SignInForm__tab"
          :class="{ 'SignInForm__tab--active': signForm.loginType === 2 }"
        >
          验证码登录
        </div> -->
      </div>
      <el-form-item label="账号" prop="phone">
        <el-input
          v-model="signForm.phone"
          @keyup.enter.native="submitForm"
          @focus="handleFocusEvent"
          autocomplete="on"
        >
        </el-input>
      </el-form-item>
      <el-form-item
        label="密码"
        prop="password"
        v-if="signForm.loginType === 1"
      >
        <el-input
          :type="inputType ? 'password' : 'text'"
          @focus="handleFocusEvent"
          v-model="signForm.password"
          autocomplete="on"
        ></el-input>
        <span @click="inputType = !inputType" class="mb8">
          <i class="iconfont iconeye" v-if="inputType"></i>
          <i class="iconfont iconicon_password_eye_on" v-if="!inputType"></i>
        </span>
      </el-form-item>
      <el-form-item
        label="验证码"
        prop="phoneCode"
        v-if="signForm.loginType === 2"
      >
        <div class="SignInForm__code">
          <el-input
            v-model="signForm.phoneCode"
            size="small"
            placeholder="输入验证码"
          >
          </el-input>
          <el-button
            type="primary"
            plain
            round
            class="mb8"
            @click="getVertifyCode('signForm', 1001)"
            :disabled="signForm.isDisabled"
            size="small"
            >{{ signForm.buttonName }}
          </el-button>
        </div>
      </el-form-item>
      <div class="SignInForm__option">
        <el-checkbox v-model="signForm.savePassword">记住密码</el-checkbox>
        <!-- <el-button type="text" @click="forgotPassword">忘记密码 ?</el-button> -->
      </div>
      <el-button class="SignInForm__button" type="primary" @click="submitForm">
        登录
      </el-button>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, defineEmits } from "vue";
import { useRoute, useRouter } from "vue-router";
import { signByUser } from "@/apis/sign";
import { useStore } from "vuex";
import { GrantType } from "@/apis/sign/index.type.ts";

/**
 * reactive variable
 */
const inputType = ref(true);

const signForm = reactive({
  // 登录类型 1-密码登录  2-验证码登录 3-扫码登录
  loginType: 1,
  // 登录账号
  phone: "",
  // 密码
  password: "",
  phoneCode: "",
  // 校验码凭证
  certificate: "",
  code: "",
  buttonName: "获取验证码",
  isDisabled: false,
  time: 60,
  savePassword: false,
});

const signRules = reactive({
  phone: [
    {
      required: true,
      validator: validPhone,
      trigger: "blur",
    },
  ],
  phoneCode: [
    {
      required: true,
      message: "请输入验证码",
      trigger: "blur",
    },
  ],
  password: [
    {
      required: true,
      message: "请输入登录密码",
      trigger: "blur",
    },
  ],
});

const loading = ref(false);
const emitHandle = defineEmits(["change"]);
const router = useRouter();
const route = useRoute();
const $store = useStore();

/**
 * lifeCircle
 */
addListenKeyDown();
/**
 * function
 */
function validPhone(
  _rule: any,
  value: string,
  callback: (arg?: Error) => void
) {}

function isvalidPhone(str: string) {
  const reg = /^1[0-9][0-9]\d{8}$/;
  return reg.test(str);
}

function switchStatus(status: number) {
  signForm.loginType = status;
}

function addListenKeyDown() {
  document.onkeydown = (e: KeyboardEvent) => {
    // 验证在登录界面和按得键是回车键enter
    if (
      route.path.lastIndexOf("login") &&
      (e.code === "Enter" || e.code === "enter")
    ) {
      // submitForm();
    }
  };
}

function changeSignType(value: string) {
  emitHandle("change", value);
}

function handleFocusEvent(event: {
  target: { removeAttribute: (arg0: string) => void };
}) {
  if (event.target) {
    event.target.removeAttribute("readonly");
  }
}

function forgotPassword() {
  router.push({
    name: "changePass",
    query: {
      type: "forget",
    },
  });
}
function submitForm() {
  console.log(signForm);
  signByUser(GrantType.PASSWORD, {
    username: signForm.phone,
    password: signForm.password,
  }).then((res: any) => {
    $store.commit("user/token", res.data.value);
    const redirect = route.query.redirect as string;
    router.push({
      path: !!redirect ? redirect : "/",
    });
  });
}
</script>

<style lang="scss" scoped>
// 覆盖样式
:deep(.el-input__inner) {
  border-color: transparent;
}
:deep(.el-form-item) {
  border-bottom: 1px solid #f8f5f9;
}

:deep(.el-checkbox__inner) {
  width: 12px;
  height: 12px;
}

:deep(.el-checkbox__inner)::after {
  height: 6px;
  left: 3px;
  top: 0;
}

:deep(.el-checkbox__label) {
  color: #909399;
  padding-left: 6px;
  font-size: 12px;
}

:deep(.SignInForm__option .el-button--text) {
  color: #909399;
}
</style>
