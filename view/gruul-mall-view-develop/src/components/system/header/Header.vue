<!--
  头部导航
  @author 张治保
-->
<template>
  <el-popover ref="userinfo" placement="bottom" trigger="hover" width="min-width:150px">
    <user-card/>
  </el-popover>
  <el-header>
    <el-menu style="width: 100%" class="el-menu-demo" mode="horizontal">
      <el-menu-item index="1" title="logo">
        <img style="height: 100%" src="../../../assets/image/logo.png" alt="icon"/>
      </el-menu-item>
      <el-menu-item
          index="2"
          title="写文章"
          @click="toBlogCreation"
      >
        <i class="el-icon-chicken"/>
        写文章
      </el-menu-item>
      <el-menu-item
          index="3"
          @click="toC"
      >
        新增路由
      </el-menu-item>
      <el-menu-item
          index="4"
          @click="toDirect"
      >
        跳转
      </el-menu-item>
      <!--------------------------RIGHT---------------------------------->
      <template v-if="data.isLogin">
        <el-menu-item index="0" style="margin-left: auto;margin-right: 0;padding: 0 20px" v-popover:userinfo>
          <el-avatar :src="data.avatar"/>
        </el-menu-item>
      </template>
    </el-menu>
  </el-header>
</template>

<script setup lang="ts">
import {defineComponent,defineCustomElement,reactive, computed} from 'vue'
import {useStore} from 'vuex'
import UserCard from './UserCard.vue'
import {useRouter} from "vue-router";
import {get, post} from "../../../apis/http";
const store = useStore();
const data = reactive({
  isLogin: computed(() => !!store.state.user.token),
  avatar: computed(() => store.state.user.avatar)
})
const router = useRouter();
const toBlogCreation = ()=>{
  router.push({path:'/creation/blog'})
}

const toC = ()=>{
  get({url:'http://localhost:8080/s'}).then(
     res=>{
       console.log(res)
       router.addRoute("home", {
         path: `/c`,
         name: '优惠券',
         component:defineComponent({
           name:"youhuiquan",
           template:res.data.template,
           data(){
             return res.data.data
           }
         })
       });
     }
  )
  const respData = {'a':0}

}
const toDirect = ()=>{
  router.push({path:'/c'})
}
</script>

<style scoped>
.el-header {
  padding: 0;
}

.el-card {
  border: none;
  box-shadow: none;
  text-align: center;
  width: 100%;
}

.el-badge:deep().el-badge__content {
  margin-top: 20px;
  margin-right: 5px;
}
</style>