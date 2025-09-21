<!--
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-03-15 09:54:13
-->
<template>
  <!--        <el-form ref="form" :model="form" :rules="rules">-->
  <el-form ref="form">
    <br>
    <el-form-item prop="username">
      <el-input  placeholder="用户名/手机号/邮箱" :prefix-icon="User" v-model="username"/>
    </el-form-item>
    <br>
    <el-form-item prop="password">
      <el-input  placeholder="密码" type="password" :prefix-icon="Lock" v-model="password"/>
    </el-form-item>
    <br>
    <el-row>
      <el-button type="primary" @click="login()">登录</el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import {ref,reactive} from 'vue'
import {useStore} from 'vuex'
import {useRoute,useRouter} from 'vue-router'
import { User,Lock } from '@element-plus/icons-vue'
import {post} from '../../apis/http'
const username = ref("");
const password = ref("");

const store = useStore();
const route = useRoute();
const router = useRouter();
const login = ()=>{
  post({
    showLoading:true,
    url:'/gruul-mall-uaa/uaa/get/oauth/token',
    data:{
      grant_type:"password",
      username:username.value,
      password:password.value
    }
  }).then(
      data=>{
        console.log(data);
        store.commit('user/token',data.data.access_token)
        const redirect = route.query.redirect;
        router.push({
          path:!!redirect?redirect:'/'
        })
      }
  )

}
</script>

<style scoped>
.el-form{
  width: 100%;
  height: 100%;
}
.el-button{
  width: 100%;
}
</style>