<template>
  <el-card style="width:375px;text-align: left">
    <div slot="header" style="font-weight: bolder;">
      <span style="font-size: 16px">{{data.name}}</span>
      <el-tag size="small" >{{data.deptName}}/{{data.roleName}}</el-tag>
      <el-link style="float: right; padding:0;font-size:20px;color: #969896"
               icon="el-icon-setting"
               :underline="false"
               href="#/query/345"
      />
    </div>
    <div>
      <div style="color:#8c8c8c;font-size: 13px">
        <p>
          <i class="el-icon-office-building"/>
          {{data.companyName}}
        </p>
        <p>
          <i class="el-icon-mobile-phone"/>
          {{data.mobile}}
        </p>
      </div>
      <div style="text-align: center">
        <el-link type="primary" @click="logout"  icon="el-icon-warning" >退出账号</el-link>
      </div>
    </div>

  </el-card>
</template>

<script setup lang="ts">
 import {reactive} from 'vue'
 import {useStore} from 'vuex'
 import {get} from 'apis/http'

 const store = useStore();

 const data = reactive({
   name:'',
   deptName:'',
   roleName:'',
   companyName:'',
   mobile:''
 })
 // get({
 //   url:'/user'
 // }).then(
 //     (result)=>{
 //       data.name = result.data.name;
 //       data.deptName = result.data.deptName;
 //       data.roleName = result.data.roleName;
 //       data.companyName = result.data.companyName;
 //       data.mobile = result.data.mobile;
 //     }
 // )
 const logout = ()=>{
   store.commit('user/clear')
 }
</script>

<style scoped>
</style>