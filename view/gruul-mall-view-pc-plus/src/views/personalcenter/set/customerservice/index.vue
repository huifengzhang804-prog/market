<script setup lang="ts">
import ChatContent from './components/chatContent.vue'
import MerchantCustomerService from './components/merchantCustomerService.vue'
import ProductOrder from './components/productOrder.vue'
import {
  userInfo,
  messageUsersPage,
  currentSelectUser,
  searchFocus,
  onChange,
  onKeywordsChange,
  onSearchFocus,
  adminMessagesPage,
  messageSubmit,
  initCustomerService,
  contentLoadMore,
} from './index'

onMounted(initCustomerService)
</script>

<template>
  <div class="customerservice">
    <div class="customerservice_box">
      <MerchantCustomerService
        :messageUsers="messageUsersPage.records"
        @change="onChange"
        @keywordChange="onKeywordsChange"
        @searchFocus="onSearchFocus"
      />
      <ChatContent
        v-if="currentSelectUser?.chatWithShopInfo?.shopId"
        :userInfo="userInfo"
        :user="currentSelectUser"
        :messages="adminMessagesPage.records"
        :searchFocus="searchFocus"
        @messageSubmit="messageSubmit"
        @loadMore="contentLoadMore"
      />
      <div v-else class="empty">
        <el-empty description="暂无消息" />
      </div>
      <!-- <ProductOrder :user="currentSelectUser" @messageSubmit="messageSubmit" /> -->
    </div>
  </div>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
  text-align: left;
}
.customerservice {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100vh);
  background: linear-gradient(to bottom right, #cab5fd, #dac7fe, #c6fbff); /* 渐变背景 */

  .customerservice_box {
    width: 1230px;
    height: 77%;
    display: flex;
    border-radius: 20px;
    background-color: #fff;
    box-sizing: border-box;
    .empty {
      flex: 1;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
</style>
