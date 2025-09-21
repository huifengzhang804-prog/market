<template>
    <div class="cust_service_container">
        <el-container class="customer-service">
            <el-aside class="cust_service_left">
                <AsideIndex
                    :message-users="messageUsersPage.records"
                    @change="onChange"
                    @keyword-change="onKeywordsChange"
                    @search-focus="onSearchFocus"
                />
            </el-aside>
            <el-main>
                <MainIndex
                    v-if="currentSelectUser?.chatWithUserInfo?.userId"
                    :shop-info="shopInfo"
                    :user="currentSelectUser"
                    :messages="adminMessagesPage.records"
                    :search-focus="searchFocus"
                    @message-submit="messageSubmit"
                    @load-more="contentLoadMore"
                />
                <div v-else class="empty">
                    <el-empty description="暂无消息" />
                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script setup lang="ts">
import AsideIndex from './components/aside/Index.vue'
import MainIndex from './components/main/Index.vue'
import {
    shopInfo,
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

<style scoped lang="scss">
.cust_service_container {
    width: 100%;
    height: 100%;
    overflow: hidden;
    padding: 28px 26px;
    .customer-service.el-container {
        height: 100%;
    }
    .cust_service_left {
        width: 316px;
        overflow: hidden;
        height: 100%;
        padding: 0;
        border: 2px solid $cust_service_border_color;
    }
    .el-main {
        flex: 1;
        padding: 0;
        background: rgb(245, 247, 251);
        .empty {
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    }
}
</style>
