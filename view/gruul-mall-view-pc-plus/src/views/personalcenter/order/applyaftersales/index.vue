<script setup lang="ts">
import { ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { ArrowRight } from '@element-plus/icons-vue'
import storage from '@/libs/storage'
import { storeToRefs } from 'pinia'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

const $router = useRouter()
const $route = useRoute()
const afsOrderItem = ref()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

const topComponents = ref()
topComponents.value = getterPropertiesList.value
init()

async function init() {
  afsOrderItem.value = new storage().getItem('CLIENT-applyAfterSalesOrder')
}
/**
 * @description: 导航去售后订单
 * @returns {*}
 */
const navaftersales = () => {
  $router.push({
    path: '/personalcenter/order/aftersales',
    query: {},
  })
}
/**
 * @description: 导航去我的订单
 * @returns {*}
 */
const navmyorder = () => {
  $router.push({
    path: '/personalcenter/order/myorder',
    query: {},
  })
}
/**
 * @description: 导航去填写售后表单
 * @returns {*}
 */
const navapplyaftersalesservice = (type: string) => {
  $router.push({
    path: '/personalcenter/order/applyaftersalesservice',
    query: { type },
  })
}
</script>

<template>
  <div bg-white c-pb-490>
    <div c-w-1200 c-h-110 flex ma items-center>
      <router-link to="/home">
        <div
          class="log"
          :style="{
            backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
          }"
        />
      </router-link>
    </div>
    <el-breadcrumb :separator-icon="ArrowRight" c-w-1190 ma c-mb-35>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 个人中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 订单中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navaftersales"> 售后订单 </el-breadcrumb-item>
      <el-breadcrumb-item>申请售后</el-breadcrumb-item>
    </el-breadcrumb>
    <div c-pl-26 c-pt-26 c-pr-23 c-pb-41 b-1 c-bc-eee c-w-1190 ma>
      <div flex items-center c-h-165 b-b c-bc-eee>
        <img :src="afsOrderItem.image" c-w-120 c-h-120 />
        <div e-c-3 c-fs-20 c-mb-40 c-ml-32>
          {{ afsOrderItem.productName }}
        </div>
      </div>
      <div c-mt-19 text-left>
        <div c-fs-18 e-c-3>选择售后类型</div>
        <div c-mt-23 flex justify-between>
          <div b-1 c-bc-eee c-w-560 c-h-115 flex c-pl-31 items-center cursor-pointer @click="navapplyaftersalesservice('REFUND')">
            <div c-w-72 c-h-72 c-mr-25>
              <q-icon name="icon-tuikuanshouhou" size="65px" />
            </div>
            <div c-fs-16>
              <div e-c-3 c-mb-6>仅退款</div>
              <div e-c-9>未收到货已与商家协商拦截快递；或协商同意仅退款</div>
            </div>
          </div>
          <div
            v-if="afsOrderItem.packageId"
            b-1
            c-bc-eee
            c-w-560
            c-h-115
            flex
            c-pl-31
            items-center
            cursor-pointer
            @click="navapplyaftersalesservice('RETURN_REFUND')"
          >
            <div c-w-72 c-h-72 c-mr-25>
              <q-icon name="icon-ziyuan" size="65px" />
            </div>
            <div c-fs-16>
              <div e-c-3 c-mb-6>退货退款</div>
              <div e-c-9>已收到货，需要退还已收到的货物</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
</style>
