<script setup lang="ts">
import { ChatMessage, MessageAndShopAdmin, MessageUser } from '@/views/personalcenter/set/customerservice/types'
import MainBody from './main/body/Index.vue'
import MainFooter from './main/footer/Index.vue'

defineProps({
  user: {
    type: Object as PropType<MessageUser>,
    default: () => {},
  },
  messages: {
    type: Object as PropType<Array<MessageAndShopAdmin>>,
    default: () => {},
  },
  userInfo: {
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
const chatMainHeight = ref('calc(100% - 155px)')
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
    chatMainHeight.value = `calc(100% - ${targetDiv!.style.height})`
  }

  document.onmouseup = function () {
    document.onmousemove = null
  }
}
</script>

<template>
  <div class="messageContent">
    <div class="shop_info">
      <div class="shopName">{{ user ? user.chatWithShopInfo?.shopName : '' }}</div>
    </div>
    <div class="chat">
      <div class="chat_main" :style="{ height: chatMainHeight, maxHeight: 'calc(100% - 155px)' }">
        <MainBody :userInfo="userInfo" :user="user" :messages="messages" @loadMore="() => emits('loadMore')" />
      </div>
      <div id="fuDiv" class="chat_footer" @mousedown="dragEagle">
        <div class="top-border"></div>
        <MainFooter :searchFocus="searchFocus" :user="user" @messageSubmit="messageSubmit" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.messageContent {
  flex: 1;
  .shop_info {
    width: 100%;
    height: 50px;
    background-color: #f7f7f7;
    padding: 15px 14px;
    text-align: left;
    font-size: 14px;
    color: #333333;
    border-radius: 0 20px 0 0;
  }
  .chat {
    display: flex;
    flex-direction: column;
    height: calc(100% - 50px);
    position: relative;
    .chat_main {
      overflow: auto;
      position: relative;
    }
    .chat_footer {
      flex-shrink: 0;
      display: flex;
      justify-content: center;
      width: 100%;
      min-height: 155px;
      border-top: 1px solid #e5e5e5;

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
