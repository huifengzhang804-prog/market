<template>
    <div class="chat_main_interface">
        <div class="chat_header">
            <MainHeader :user="user" />
        </div>
        <div class="chat">
            <div class="chat_main" :style="{ height: chatMainHeight, maxHeight: 'calc(100% - 130px)' }">
                <MainBody :shopInfo="shopInfo" :user="user" :messages="messages" @loadMore="() => emits('loadMore')" />
            </div>
            <div id="fuDiv" class="chat_footer" @mousedown="dragEagle">
                <div class="top-border"></div>
                <MainFooter :user="user" :searchFocus="searchFocus" @messageSubmit="messageSubmit" />
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import type { PropType } from 'vue'
import MainHeader from './header/Index.vue'
import MainBody from './body/Index.vue'
import MainFooter from './footer/Index.vue'
import { MessageUser, ChatMessage, Message } from '@/views/message/components/types'

defineProps({
    user: {
        type: Object as PropType<MessageUser | null>,
        default: () => {},
    },
    messages: {
        type: Object as PropType<Array<Message>>,
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
const chatMainHeight = ref('calc(100% - 130px)')
const dragEagle = (e: { clientX: number; clientY: number }) => {
    var targetDiv = document.getElementById('fuDiv')
    //得到点击时该地图容器的宽高：
    var targetDivHeight = targetDiv!.offsetHeight
    var startX = e.clientX
    var startY = e.clientY
    document.onmousemove = function (e) {
        e.preventDefault()
        //得到鼠标拖动的宽高距离：取绝对值
        var distY = Math.abs(e.clientY - startY)
        //往上方拖动：
        if (e.clientY < startY) {
            targetDiv!.style.height = targetDivHeight + distY + 'px'
        }
        //往下方拖动：
        if (e.clientX < startX && e.clientY > startY) {
            targetDiv!.style.height = targetDivHeight - distY + 'px'
        }
        if (parseInt(targetDiv!.style.height) >= 320) {
            targetDiv!.style.height = 320 + 'px'
        }
        chatMainHeight.value = `calc(100% - ${targetDiv!.style.height} - 20px)`
    }

    document.onmouseup = function () {
        document.onmousemove = null
    }
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
