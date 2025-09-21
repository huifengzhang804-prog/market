<script setup lang="ts">
import { MessageUser } from '../types'
import Commodity from './main/footer/toolbar/commodity/Index.vue'
import Order from './main/footer/toolbar/orders/Index.vue'

defineProps({
  user: {
    type: Object as PropType<MessageUser>,
    default: () => {},
  },
})

const emits = defineEmits(['messageSubmit'])
const activeName = ref('myOrder')
const tabs = [
  // {
  //   name: 'recently',
  //   label: '最近浏览',
  // },
  {
    name: 'myOrder',
    label: '我的订单',
  },
]

const sendProduct = (message: any) => {
  emits('messageSubmit', message)
}
</script>

<template>
  <div class="preSend">
    <div class="tabs">
      <p v-for="(item, index) in tabs" :key="index" :class="[activeName === item.name ? 'active' : '']" @click="activeName = item.name">
        {{ item.label }}
      </p>
    </div>
    <div class="list">
      <!-- <Commodity v-if="activeName === 'recently'" @submit-message="sendProduct" /> -->
      <Order :user="user" @order-handle="sendProduct" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preSend {
  width: 28%;
  height: 100%;
  border-left: 2px solid #f7f7f7;
  .tabs {
    height: 50px;
    display: flex;
    align-items: center;
    color: #999;
    padding-left: 17px;
    & p {
      cursor: pointer;
    }
    &:first-child p {
      margin-right: 14px;
    }
    .active {
      color: #333333;
      font-weight: 500;
    }
  }
  .list {
    height: calc(100% - 55px);
    width: 100%;
  }
}
</style>
