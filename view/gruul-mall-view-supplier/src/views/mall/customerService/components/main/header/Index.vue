<template>
    <div class="chat_header_container">
        <el-avatar :size="36" shape="square" :src="user?.chatWithShopInfo.shopLogo" fit="cover"></el-avatar>
        <div class="user_name ccenter">
            {{ user ? user.chatWithShopInfo.shopName : '' }}
        </div>
    </div>
</template>
<script setup lang="ts">
import { PropType } from 'vue'
import { MessageUser } from '@/views/mall/customerService/types'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
})
</script>
<style scoped lang="scss">
.chat_header_container {
    padding: 12px 20px;
    display: flex;
    align-items: center;
    height: 100%;
    .el-avatar {
        flex-shrink: 0;
    }
    .user_name {
        color: rgb(51, 51, 51);
        font-size: 14px;
        font-weight: bold;
        margin-left: 10px;
    }
}
</style>
