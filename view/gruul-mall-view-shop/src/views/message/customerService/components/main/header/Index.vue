<template>
    <div class="chat_header_container">
        <el-avatar :size="36" shape="square" :src="currentSelectUser?.chatWithUserInfo.avatar" fit="cover"></el-avatar>
        <div class="user_name ccenter">
            {{ props.user ? props.user.chatWithUserInfo.nickname : '' }}
            <q-icon v-if="currentSelectUser?.chatWithUserInfo.includeRights" class="vip_tag" size="45px" svg name="icon-a-biaoqianyangshi1"></q-icon>
        </div>
    </div>
</template>
<script setup lang="ts">
import type { PropType } from 'vue'
import { MessageUser } from '@/views/message/customerService/types'
import { currentSelectUser } from '@/views/message/customerService'

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
    .vip_tag {
        width: 250px;
        height: 55px;
        margin-left: 10px;
        flex-shrink: 0;
        transform: translateY(14px);
    }
}
</style>
