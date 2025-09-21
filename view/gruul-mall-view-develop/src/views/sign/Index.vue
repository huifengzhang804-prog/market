<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-21 15:46:43
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-22 09:19:46
-->
<template>
  <div class="SignFlowHomepage">
    <img
      @click="linkTo"
      src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200407/logo.png"
      class="SignFlowHomepage__logo pointer"
    />
    <div class="SignFlowHomepage__content">
      <div class="Card SignContainer">
        <img
          class="SignContainer__img"
          src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200318/index.jpg"
        />
        <div class="SignContainer__content">
          <SignInByPhone/>
          <!-- <SignInByPhone
            v-if="currenType === 'SignInByPhone'"
            @change="changeLoginType"
          />
          <SignInByQrCode
            v-if="currenType === 'SignInByQrCode'"
            @change="changeLoginType"
          />
          <SignUpByPhone
            v-if="currenType === 'SignUpByPhone'"
            @change="changeLoginType"
          />
          <SignUpByQrCode
            v-if="currenType === 'SignUpByQrCode'"
            @change="changeLoginType"
          /> -->
        </div>
      </div>
    </div>

    <footer class="SignFlowHomepage__footer">
      <p class="medusa__rights">
        Copyright © 2020 布谷鸟. All Rights Reserved
        宁波启山科技有限公司版权所有
      </p>
      <p>
        <img
          style="width: 14px"
          src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200321/gongan.jpg"
        />
        <span @click="goPolice" class="pointer"
          >浙公网安备33020502000496号</span
        >
      </p>
    </footer>
  </div>
</template>
<script lang="ts" setup>
import SignInByPhone from './components/SignInByPhone.vue'
</script>
<style lang="scss">
@import "@/assets/css/sign/sign.scss";
</style>
