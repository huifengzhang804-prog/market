<template>
    <div class="chat_main_interface">
        <div class="chat_header">
            <MainHeader :user="props.user" />
        </div>
        <div class="chat">
            <div class="chat_main" :style="{ height: chatMainHeight, maxHeight: 'calc(100% - 130px)' }">
                <MainBody :shop-info="props.shopInfo" :user="props.user" :messages="props.messages" @load-more="() => emits('loadMore')" />
            </div>
            <div id="fuDiv" class="chat_footer" @mousedown="dragEagle">
                <div class="top-border"></div>
                <MainFooter :user="props.user" :searchFocus="props.searchFocus" @messageSubmit="messageSubmit" />
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, PropType } from 'vue'
import MainHeader from './header/Index.vue'
import MainBody from './body/Index.vue'
import MainFooter from './footer/Index.vue'
import { MessageUser, ChatMessage, MessageAndShopAdmin } from '../../types'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
    messages: {
        type: Object as PropType<Array<MessageAndShopAdmin>>,
        default: () => {},
    },
    shopInfo: {
        type: Object,
        default: () => {},
    },
    searchFocus: {
        type: Boolean,
        default: false,
    },
})
const emits = defineEmits(['messageSubmit', 'loadMore'])
const messageSubmit = (chatMessage: ChatMessage) => {
    emits('messageSubmit', chatMessage)
}
</script>
<style scoped lang="scss">
.chat_main_interface {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 0;
    overflow: hidden;
    .chat_header {
        height: 60px;
        flex-shrink: 0;
        border-bottom: 2px solid $cust_service_border_color;
    }
    .chat {
        display: flex;
        flex-direction: column;
        height: calc(100% - 60px);
        border: 1px solid $cust_service_border_color;
        position: relative;
        .chat_main {
            border-top: 1px solid $cust_service_border_color;
            overflow: auto;
            position: relative;
        }
        .chat_footer {
            flex-shrink: 0;
            display: flex;
            justify-content: center;
            width: 100%;
            position: absolute;
            bottom: 20px;
            min-height: 87px;
            .top-border {
                width: 100%;
                height: 5px;
                position: absolute;
                top: 0;
                left: 0;
                z-index: 99;
                cursor: n-resize;
            }
        }
    }
}
</style>
