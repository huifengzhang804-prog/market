<script setup lang="ts">
import { ref, nextTick, computed, onUnmounted } from 'vue'
import Popup from '@/pluginPackage/liveModule/views/components/chatRoom/components/send/popup.vue'
import MessageMuted from '@/pluginPackage/liveModule/views/components/chatRoom/components/message/message-muted.vue'
import MessageText from '@/pluginPackage/liveModule/views/components/chatRoom/components/message/message-text.vue'
import MessageTip from '@/pluginPackage/liveModule/views/components/chatRoom/components/message/message-tip.vue'
import MessageRemind from '@/pluginPackage/liveModule/views/components/chatRoom/components/message/message-remind.vue'
import FaceEmoji from '@/pluginPackage/liveModule/views/components/chatRoom/components/message/face-emoji.vue'
import { socketTask, type MessageType, send } from '../../socket'
import {
  handleTextMessageShowContext,
  handleTipMessageShowContext,
  handleMutedContext,
} from '@/pluginPackage/liveModule/views/components/chatRoom/utils/untis'
// #ifdef APP-PLUS
import { tim, TIM } from '@/pluginPackage/liveModule/views/components/chatRoom/hooks/index'
// #endif
import { loginIM } from '@/pluginPackage/liveModule/views/components/chatRoom/index'
import { doDismissGroup } from '@/pluginPackage/liveModule/views/components/chatRoom/apis'

const $props = defineProps({
  disableInput: {
    type: Boolean,
    default: false,
  },
})
let time = null as NodeJS.Timeout | null
const EMOJI_URL =
  'data:image/png;base64,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'
const like = ref(false)
const emojiShow = ref(false)
const showSendMessage = ref(false)
const emit = defineEmits(['keyboardheightchange'])
const likeCount = ref(0)
const sendText = ref('发 送')
const inputUpHeight = ref(0)
// px 单位
const inputUpHeightOnce = ref(0)
const inputRef = ref()
// footer 组件高度 rpx
const chat_room_footerHeight = 140
// 弹窗 padding 值 rpx
const popupPadding = 20 * 2
// 弹窗 popup_send_input_Height 值 rpx
const popup_send_input_Height = 80
// 自动回弹
const messageBottom = ref('')
// 消息列表
const messageList = ref<any[]>([])
// 输入框文字
const textM = ref('')
// 禁言定时器
let mutedTimer: NodeJS.Timeout
// 禁言时间
let mutedSecond = 0
let onMessageReceived = function (event: any) {
  messageBottom.value = ''
  event.data.forEach((item: any) => {
    messageList.value = [...messageList.value, { ...item, flow: 'in' }]
  })
  nextTick(() => {
    messageBottom.value = 'message-bottom'
  })
}
tim.on(TIM.EVENT.MESSAGE_RECEIVED, onMessageReceived)
//
const chat_room_content_computed = computed(() => {
  if (showSendMessage.value) {
    // 弹窗打开时
    const chatMarginButtom = 10
    const height = uni.upx2px(popupPadding) + uni.upx2px(popup_send_input_Height) + chatMarginButtom + inputUpHeightOnce.value + 'px'
    return height
  }
  // 默认 作为 chat_room_content 组件的 margin-buttom
  return chat_room_footerHeight + 'rpx'
})

init()
onUnmounted(() => {
  doDismissGroup({ success: () => {}, error: () => {} })
  if (time) {
    clearInterval(time)
  }
  socketTask.value?.close()
})

const handleLive = () => {
  like.value = true
  let time = setTimeout(() => {
    likeCount.value++
    like.value = false
    clearTimeout(time)
  }, 300)
}

// socketTask.value
const keyboardheightchange = (e: any) => {
  if (inputUpHeightOnce.value === 0) {
    // 记录软键盘的高度 赋值给 emjeo 组件
    inputUpHeightOnce.value = e.detail.height
  }
  inputUpHeight.value = e.detail.height
  emit('keyboardheightchange', e)
  // 要在表情组件展示后执行
  setTimeout(() => {
    if (e.detail.height === 0 && !emojiShow.value && showSendMessage.value) {
      showSendMessage.value = false
    }
  }, 110)
}
const upper = () => {}
// 处理禁言时间
const handleMutedTime = (second: number) => {
  sendText.value = '禁 言'
  mutedSecond = second
  if (mutedTimer) {
    clearInterval(mutedTimer)
  }
  mutedTimer = setInterval(() => {
    mutedSecond--
    if (mutedSecond <= 0) {
      if (mutedTimer) {
        clearInterval(mutedTimer)
      }
      sendText.value = '发 送'
    }
  }, 1000)
}
// 关闭禁言弹框
const handleHideMuted = (id: any) => {
  messageList.value = messageList.value.filter((item) => item.ID !== id)
}
// 处理需要合并的数据
const handleSend = (emo: any) => {
  textM.value += emo.name
}
// 发送表情消息
const sendFaceMessage = async () => {
  if (!textM.value.trim() || sendText.value === '禁 言') return
  messageBottom.value = ''
  const options: any = {
    // @ts-ignore

    to: getApp().globalData.groupID,
    conversationType: TIM.TYPES.CONV_GROUP,
    payload: { text: textM.value },
    // @ts-ignore

    cloudCustomData: JSON.stringify({ name: getApp().globalData.userInfo.nickname }),
  }
  const message = tim.createTextMessage(options)
  tim
    .sendMessage(message)
    .then(() => {
      messageList.value = [...messageList.value, { ...message, flow: 'out' }]
      nextTick(() => {
        textM.value = ''
        messageBottom.value = 'message-bottom'
      })
    })
    .catch((err: any) => {
      switch (err.code) {
        case 10017:
          uni.showToast({
            icon: 'none',
            title: '禁言中！',
          })
          break
        case 10010:
          uni.showToast({
            icon: 'none',
            title: '主播暂未开启聊天室',
          })
          break
        default:
          uni.showToast({
            icon: 'none',
            title: '发送失败！',
          })
          break
      }
    })
}
const handleEmojiClick = () => {
  if (emojiShow.value) {
    emojiShow.value = false
    // 先关闭表情组件 再弹起键盘
    setTimeout(() => {
      // #ifdef MP-WEIXIN
      // #endif
      // #ifdef APP-PLUS
      inputRef.value.focus()
      // #endif
    }, 500)
  } else {
    uni.hideKeyboard()
    setTimeout(() => {
      emojiShow.value = true
    }, 100)
  }
}
const handleFocus = () => {
  if (emojiShow.value) {
    emojiShow.value = false
  }
}
const handleClickFooterInput = () => {
  if ($props.disableInput) return
  showSendMessage.value = true
  // #ifdef MP-WEIXIN
  // #endif
  // #ifdef APP-PLUS
  inputRef.value.focus()
  // #endif
}
const handleSendMessageClose = () => {
  if (emojiShow.value) {
    emojiShow.value = false
  }
  uni.hideKeyboard()
  // #ifdef MP-WEIXIN
  // #endif
}
function init() {
  loginIM()
  time = setInterval(() => {
    if (socketTask.value && likeCount.value !== 0) {
      const data = {
        type: 'LIKE',
        data: {
          // @ts-ignore

          liveId: getApp().globalData.groupID,
          count: likeCount.value,
        },
      } as MessageType
      send(data)
      likeCount.value = 0
    }
  }, 2000)
}
const handleblur = () => {
  // #ifdef MP-WEIXIN
  // #endif
}
</script>

<template>
  <!-- 聊天记录 -->
  <view class="chat_room_content" :style="{ marginBottom: chat_room_content_computed }">
    <scroll-view scroll-y="true" :show-scrollbar="false" class="scroll-Y" :scroll-into-view="messageBottom" @scrolltoupper="upper">
      <view class="chat_room_content_message">
        <template v-for="item in messageList" :key="item.ID">
          <message-muted
            v-if="item.payload?.operationType === 16"
            :text="handleMutedContext(item)"
            :item="item"
            :handle-muted-time="handleMutedTime(item.payload.muteTime)"
            @hide-muted="handleHideMuted"
          />
          <message-remind v-if="!item.isRevoked && item.type === TIM.TYPES.MSG_GRP_SYS_NOTICE" :data="handleTipMessageShowContext(item)" />
          <message-tip v-if="!item.isRevoked && item.type === TIM.TYPES.MSG_GRP_TIP" :data="handleTipMessageShowContext(item)" />
          <message-text
            v-if="item.type === TIM.TYPES.MSG_TEXT"
            :id="item.flow + '-' + item.ID"
            :data="handleTextMessageShowContext(item)"
            :message-data="item"
          />
        </template>
      </view>
      <view id="message-bottom" style="height: 2rpx"></view>
    </scroll-view>
    <!-- 讲解中商品 -->
    <view style="width: 100rpx; height: 1rpx; opacity: 0"></view>
    <!-- <ongoing-goods /> -->
    <!-- 讲解中商品 -->
  </view>
  <!-- 聊天记录 -->
  <!-- 底部输入 点赞 -->
  <view class="chat_room_footer" :style="{ height: chat_room_footerHeight + 'rpx' }">
    <view class="chat_room_footer__left">
      <view class="uni-input" @click="handleClickFooterInput"> <text style="color: #bdb2b2">说点什么吧！</text> </view>
    </view>
    <view class="chat_room_footer__right" :style="{ justifyContent: 'center' }">
      <!-- <view class="chat_room_footer__right__round" @click="popupShow = true">
                        <image
                            class="image_round image_round_left"
                            mode="aspectFit"
                            src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC4AAAAuCAYAAABXuSs3AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAMKSURBVHgB7Zk/TBNRHMe/jz+JgGBlINEJ2CQQxQUjJpawOCDB6OJkHFxFN9QBOmCbOEhnHWo0YXCpgMbBRgY1OqgdjG7QSSILHTSS1Pb5+/X12rtS27539azJfZLLvffu3vV7v/u+X9vfCTQIeS0YQGfPLKQMUvcYbYHCkSSESCKTjYo7z5JoEAIuyQvu6JnnVs2TBWLIZUIi8jwFl7gSLufO9EO0v6Rmv8a0FH5lz7mNvrFwFenuj6gomuwBkaI92UXYbFMkDZkZdRP5NpjS2X0Xsky0lFHsti2IpXjaMXzjLNuI7FS8gUDhSQ3AEKOIFyyy6RzNXRfhp0t/njNDc7L8hErRl7gsIqsxGNACI9qcC1EiVk00IyLxFO1CzkFcgiFmwgVOOwdaQ3VNC6/yzdltFMyvFQMMIy76bZ10IZr1IeUTR7+r9wAMcHhczk0F6ctiFnuzQDlBW5sjqJPayrJMPgOlq86QeFC+ForC5c2peUixgKZFJkV4bdTq5YVXzhLNSClzKY+L9hn8D8iWo1ZTCZfSaGV7jih94RlmlX+PL9xrfOFe4wv3Gl+41/jCvcYX7jW+cK/RKwgd7ANOTqv26xX6t7kNV+zrAsbpers/gPcJta8TPeHnqZwyOKzah6gIdf8WXHE1Sn+b+1T7yJjW9fSs0ttXag+OAJMXYQzPDdiu17EfOugJf7Wy98NNxFea9yEBHfSs8oaED42paNtFsPdfLNf2PJ9nt5vFl7dqzWigX619dBu4skgeHyyNHZ9U2+d3tJGILap07HxTxzpoAQ6MqOPlgpmtDeBxFLroC+eVf48W0QUqeA2dcB7jp8FbvWx+Ah4uamUTC7P6OH8QR55T46lp5yKrd35iWdsedswL+wx7nv3JkR+v4wZYMIvlzSDKdtwJZ3a2S2IOk+8HhtUNWOnt53e1aNkWXzfQKNwLt8PCGiiuGv6PLK8pVGtrFNabEKta27BX1X+XXPE1TF64iKytk/p1NDcpyPa41Sl6nF5TTFDkQ/kTmguysYxDtk7YX5L9BsTv491PkTWsAAAAAElFTkSuQmCC"
                        />
                    </view> -->
      <view class="chat_room_footer__right__round" @tap="handleLive">
        <image
          class="image_round"
          :class="{ image_round_like: like }"
          mode="widthFix"
          src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADoAAAA6CAYAAADhu0ooAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAATmSURBVHgB7Zo/bBtVHMe/75xEbRHIWSoxcWFDKoKyIMFQhy4ISGpgQkJqIrrHHZASZ2gyJERiwIG1qOkES6lDBGJJYgbYSIPE3utUiQWrqE5J43v8fuea5v753rt75zgoHylKfPcu58/9fu//ASeccCwR6AOyUiriVLEIHNi+E223icctR9QaTeRMLqJytmxDtMv03y/QLV6lQ3bCJbt0lUO/1+E+boiVnxwYxpioF7Uzz81AyBKkKCEbLL6K1t91U9HOLPpUEBVIFGEWh37W4O7fzBrlTKKyOsmCCzkIBnHobotieWMNKUklKmfftlEYvmEgRTURFN1/FtNEV1tUVt+hxmXoNpIbmLxwKJXHdWUtncJyfvIyxNA2jk4S3r2tkTvyU37g6ihH1JOU1DAMDtQHH4yLz3/cVSmsJOqlK0cy/0ZHlyal8XmVNE5MXa/h4To5eJJMkdJ42+viEkiuo9y6Hm2dTMLG6WdvJBXqKSqrE1P970JSIERZzr1X7lkk7oSXspQWGOxoHqaJ1oOxuCFjfESt4Ws4PpIMD0UrcScjI/okmndx/IiN6lBkcWu4hDSMnQNeeh148WXg9DPA3kPgPj2vnU3g7h/x17x2ka451/msck08RWqYpuh3LXgiOqLVSY6mDR3evQK8MRF/fmcL2PoG+OvPzudT9CA+rnZEe13zw3Xg0UMoI2VDfLYxHjwcEn0ylr0DHT6coai8lVyOJb+e70TtyhLw/FjyNRzd6/N6sq0Ho8H0DTdGslCCDiyoIsmMngU+WVKXZLhcr0yJopO+PsKilnUJOlz8SKu4J6sq2eXNyU6qqyKsV4KHIroXqT4r4PpVPIvcYUlu5JSh5ZwAPlG5UC5qjWlH+yCZ7l528IA/ovv7NnTQSac+I+c/eOHwZ7+oa+nNULpdRT9oat5r3x09/FFrhSEEd+g6zX7We+lQOPAFLZsoS/62idzhgUPG7Mkmymx9q59WOrAgj6gy4he1XP1VcY4qj1zykO2OpNJEs+CflPhFR0YcpP1CpmX5AaaVJMTSd/cOf/aJioV6k0a/6fY6TMp2syRtvZQIrQxGjHVlA2kxIduVvJ9hOixxL3goQhQ/IwtZZbNKesh68EhYVLQbyArLflXR/8K3Vg1IMoVG8EhIVCx7K99Kq9890U1BluT+MiuCJt4rdSd4OK4fXYcJurJJoxpeRTAhybRxM+pwtOiIVUvd+gbpyu7EjKA2aTDw6waMIOGIleg91EhRr5uRWIVJbn0ZlmVJHlmZQkRHk4kfApqMaheW/eX7zt+mJSmaaFm1uNM9d9Pk3ESFlvu/gGl4ZUJ/KbM3rpyOS1smcdtQzl3a9t40GWjEmlhen+5VInn2IsW0lxaDCn83VywmFVPcCC7TRrA7oBvB1vmofjOI0nxULNd30ZZXMVhQQ2mNq0gyyhNvr6JL933jLXE6OpIcAEVSvH5DaSzd23SljaOA66RUj2QX7aUU7ynSjeivOvoO3XNPrU6GrkQG5Cxt/QtxrQ/RpUZHXu3VTyaR/aVHfmXVcqcopS7nIMztwSqPeEStnqltMPcaKwujXUJBzJC01ltdIWiqBZdmUHuFtayC//1L5MDTF5OtC/SlbQVxh6oAybm/07ZlPU0dTCIX0SCyQptXZyit3YPAgGPIwSM0TUXthBP+h/wLX+bbf9itoQEAAAAASUVORK5CYII="
        />
      </view>
    </view>
  </view>
  <!-- 底部输入 点赞 -->
  <!-- 输入框表情弹窗 s -->
  <popup v-model="showSendMessage" border-radius="0" @close="handleSendMessageClose">
    <view class="popup_send">
      <input
        ref="inputRef"
        v-model="textM"
        class="popup_send_input"
        :adjust-position="false"
        placeholder="跟用户说点什么吧！"
        :maxlength="50"
        :placeholder-style="{ color: '#fff' }"
        @keyboardheightchange="keyboardheightchange"
        @focus="handleFocus"
        @blur="handleblur"
      />
      <view class="emoji_css" @click="handleEmojiClick"> <image class="emoji_css" :src="EMOJI_URL"></image> </view>
      <view class="send" :style="{ background: textM && sendText !== '禁 言' ? '#FA3534' : '#FFA1A0' }" @click="sendFaceMessage">
        <text style="color: #fff"> {{ sendText }}</text>
      </view>
    </view>
    <view :style="{ height: inputUpHeightOnce + 'px', width: '100rpx' }"></view>
    <!-- 表情 -->
    <view :style="{ height: (emojiShow ? inputUpHeightOnce : 0) + 'px', position: 'fixed', bottom: 0 }">
      <face-emoji :show="emojiShow" :height="inputUpHeightOnce" @send="handleSend" @handle-send-emoji="sendFaceMessage"></face-emoji>
    </view>
    <!-- 表情 -->
  </popup>
  <!-- 输入框表情弹窗 e -->
  <!-- 商品 弹窗 -->
  <!-- <goods-popup v-model="popupShow">
            <view class="title">
                <text>111</text>
                <nvue-icon font-size="30px" :icon-name="cartIconName" color="red" />
            </view>
        </goods-popup> -->
</template>
<style scoped>
.chat_room_footer {
  position: absolute;
  bottom: 0;
  width: 750rpx;
  height: 140rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: row;
  padding: 0 30rpx;
}
.uni-input {
  color: #fff;
  width: 500rpx;
  height: 80rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 189rpx;
  padding: 0 30rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: row;
}
.chat_room_footer__right {
  flex: 1;
  display: flex;
  justify-content: space-between;
  /* justify-content: center; */
  align-items: center;
  flex-direction: row;
  margin-left: 20rpx;
}
.chat_room_footer__right__round {
  height: 80rpx;
  width: 80rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
.image_round {
  height: 60rpx;
  width: 60rpx;
  border-radius: 50%;
  transition: all 0.3s;
  transform: scale(1);
}

.image_round_like {
  transform: scale(0.9);
}
.image_round_left {
  height: 50rpx;
  width: 50rpx;
  border-radius: 0%;
}
.chat_room_content {
  /* max-height: 500rpx; */
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-end;
  padding: 0 30rpx;
  /* overflow: hidden; */
  /* margin-bottom: 140rpx; */
}
.scroll-Y {
  height: 400rpx;
  flex: 1;
}
.send {
  width: 134rpx;
  height: 64rpx;
  background: #fa3534;
  border-radius: 39rpx;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  color: #fff;
}
.popup_send {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
.popup_send_input {
  color: #fff;
  width: 450rpx;
  height: 80rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 189rpx;
  padding: 0 30rpx;
}
.chat_room_content_message {
  display: flex;
  flex-direction: column;
}
</style>
<style scoped>
.title {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
.emoji_css {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  /* background: #ffa843; */
  position: relative;
}
</style>
