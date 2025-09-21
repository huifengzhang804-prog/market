<script setup lang="ts">
import QNav from '@/components/q-nav/q-nav.vue'
import ServeChat from '@/basePackage/pages/customerService/cmponents/serve-chat.vue'
import { doPutPigeonMessageShopRead, doSendMessagePlatform, doGetPlatformChatRoom, doGetMessagesChatRoom } from '@/apis/consumerSever'
import { onLoad, onPageScroll } from '@dcloudio/uni-app'
import ProductPopup from '@/basePackage/pages/customerService/cmponents/product-popup.vue'
import Expression from '@/basePackage/pages/customerService/expression/Index.vue'
import Auth from '@/components/auth/auth.vue'
import { useCustomerServiceStore } from '@/store/modules/message'
import type { ShopData, Message, CurrentProduct, ShopChatMessage, ChatMessage } from '@/basePackage/pages/customerService/types'
import { ConnectType } from '@/hooks/stomp/typs'
import { playSound } from '@/libs/MediaPlayer'
import { UPLOAD_URL } from '@/hooks/useOrderStatus'
import { getheader } from '@/libs/request/returnHeader'
import { useUserStore } from '@/store/modules/user'
import { MessageType } from '@/hooks/stomp/typs'
import { StompStarter } from '@/hooks/stomp/StompStarter'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import QIcon from '@/components/q-icon/q-icon.vue'
import storage from '@/utils/storage'
import { onBeforeUnmount, onMounted, onUnmounted, reactive, ref } from 'vue'

const userInfo = useUserStore().getterUserInfo

const chatParams = ref<{ messageType: keyof typeof MessageType; message: string }>({
  messageType: 'TEXT',
  message: '',
})
const shopData = ref<ShopData>({
  shopId: '',
  shopLogo: '',
  shopName: '',
})
const pageConfig = reactive({
  pages: 1,
  current: 1,
  size: 10,
})
const showLoading = ref(false)
const uploadUrl = UPLOAD_URL
const adminMessages = ref<Message[]>([])
const expressionPopup = ref(false)
const productPopupShow = ref(true)
const inputFocusIndex = ref(0)
const focus = ref(false)
const currentProductInfo: CurrentProduct = initCurrentProduct()

let unsubscribe = () => {}

onLoad(async (res) => {
  uni.$emit('updateTitle')
  if (!res?.shopName) return
  shopData.value = res as ShopData
  try {
    if (useUserStore().userInfo.info.userId) {
      await doGetMessagesChatRoom(res.shopId || 0, useUserStore().userInfo.info.userId)
    }
  } finally {
    initPigeonMessageShopRead()
    initPigeonMessageShopChat().then(() => pageScrollTo())
    unsubscribe = useCustomerServiceStore().$subscribe((mutation, state) => {
      const connectType = state.value.connectType
      switch (connectType) {
        case ConnectType.FAIL:
          return
        default:
          initPigeonMessageShopChat().then(() => pageScrollTo())
          break
      }
      const msg = state.value.msg
      if (msg.shopId !== shopData.value.shopId) return
      playSound().then(() => {})
      initPigeonMessageShopChat().then(() => pageScrollTo())
    })
  }
})

onBeforeUnmount(() => {
  unsubscribe()
})

function pageScrollTo(num = 999) {
  const time = setTimeout(() => {
    uni.pageScrollTo({
      selector: '#chat-bottom',
    })
    clearTimeout(time)
  }, 100)
}

/**
 * 页面下拉
 */
onPageScroll(async ({ scrollTop }) => {
  if (scrollTop === 0) {
    try {
      showLoading.value = true
      await initPigeonMessageShopChat(true)
      // pageScrollTo(0)
    } catch (error) {
      console.log(error)
    }
    showLoading.value = false
  }
})
onUnmounted(() => {
  storage.remove('cSProduct')
})
onMounted(() => {
  pageScrollTo()
})

/**
 * 消息已读
 */
async function initPigeonMessageShopRead() {
  if (shopData.value.shopId) {
    // 消息已读商铺
    const { code, data } = await doPutPigeonMessageShopRead(shopData.value.shopId)
    if (code !== 200) return
    StompStarter.msgCount(useUserStore().userInfo)
  }
}
function initCurrentProduct(): CurrentProduct {
  if (storage.get('cSProduct')) {
    return storage.get('cSProduct')
  }
  return {
    pic: '',
    id: '',
    name: '',
    price: {
      underlined: '0',
      estimate: '0',
      items: [],
    },
  } as any
}
/**
 * 聊天列表
 */
async function initPigeonMessageShopChat(isLoad = false) {
  if (!isLoad) {
    // 刷新商铺客服
    pageConfig.current = 1
    adminMessages.value = await getPigeonMessageShop()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    pageConfig.current++
    adminMessages.value = adminMessages.value.concat(await getPigeonMessageShop())
  }
}
async function getPigeonMessageShop() {
  // 获取商铺客服
  if (shopData.value.shopId) {
    const { data, code, msg } = await doGetPlatformChatRoom({ userId: userInfo.info.userId, shopId: shopData.value.shopId, ...pageConfig })
    if (code !== 200) {
      uni.showToast({ title: `${msg ? msg : '获取客服聊天列表失败'}`, icon: 'none' })
      return []
    }
    pageConfig.pages = data.pages
    return data.records
  } else {
    // 获取平台客服
    const { data, code, msg } = await doGetPlatformChatRoom({ userId: userInfo.info.userId, shopId: '0', ...pageConfig })
    if (code !== 200) {
      uni.showToast({ title: `${msg ? msg : '获取客服聊天列表失败'}`, icon: 'none' })
      return []
    }
    pageConfig.pages = data.pages
    return data.records
  }
}
/**
 * 发送文本
 */
const handleSendChat = async () => {
  if (!userInfo?.info?.userId) {
    return uni.$emit(TOKEN_DEL_KEY)
  }
  if (shopData.value.shopId) {
    // 发送商铺聊天
    const { message, messageType } = chatParams.value
    const query = {
      content: message,
      messageType,
      receiverId: shopData.value.shopId,
      senderId: userInfo.info.userId || '',
    } as ChatMessage
    const { code, data, msg } = await doSendMessagePlatform(query)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '消息发送失败'}`, icon: 'none' })
  } else {
    // 发送平台聊天
    const { message, messageType } = chatParams.value
    const params = {
      content: message,
      messageType,
      receiverId: '0',
      senderId: userInfo.info.userId || '0',
    }
    const { code, data, msg } = await doSendMessagePlatform(params)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '消息发送失败'}`, icon: 'none' })
  }
  chatParams.value.message = ''
  await initPigeonMessageShopChat()
  pageScrollTo()
}
/**
 * 插入表情
 */
const handleInsertExpression = (val: string) => {
  let str = chatParams.value.message.substring(0, inputFocusIndex.value)
  chatParams.value.message = chatParams.value.message.replace(str, str + val)
  inputFocusIndex.value += val.length // 表情位置
  focus.value = false
}
/**
 * 失去焦点记录当前光标位置
 */
const handleBlur = (e: any) => {
  focus.value = false
  inputFocusIndex.value = e.detail.cursor
  pageScrollTo()
}
const handleImageSend = () => {
  uni.chooseImage({
    sourceType: ['album'],
    count: 1,
    success: (res) => {
      uni.uploadFile({
        url: uploadUrl,
        filePath: res.tempFilePaths[0],
        header: getheader(),
        name: 'file',
        success: (uploadFileRes) => {
          console.log('uploadFileRes', uploadFileRes)
          const { data } = JSON.parse(uploadFileRes.data)
          sendMessagesImage({ messageType: 'IMAGE', message: data })
        },
      })
    },
  })
}
const sendMessagesImage = async (params: ShopChatMessage) => {
  if (shopData.value.shopId) {
    // const { message, messageType } = chatParams.value
    const { message, messageType } = params
    const query = {
      content: message,
      messageType,
      receiverId: shopData.value.shopId,
      senderId: userInfo.info.userId || '',
    } as ChatMessage
    // 发送商铺聊天
    const { code, data, msg } = await doSendMessagePlatform(query)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '消息发送失败'}`, icon: 'none' })
  } else {
    // 发送平台聊天
    const { message, messageType } = params
    const query = {
      content: message,
      messageType,
      receiverId: '0',
      senderId: userInfo.info.userId || '',
    }
    const { code, data, msg } = await doSendMessagePlatform(query)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '消息发送失败'}`, icon: 'none' })
  }
  await initPigeonMessageShopChat()
  pageScrollTo()
}

/**
 * 商品信息转json发送
 */
const handleSendProduct = () => {
  if (!userInfo?.info?.userId) {
    return uni.$emit(TOKEN_DEL_KEY)
  }
  productPopupShow.value = false
  if (!currentProductInfo.id) return
  storage.remove('cSProduct')
  const newData = !currentProductInfo.no
    ? {
        id: currentProductInfo.id,
        pic: currentProductInfo.pic,
        name: currentProductInfo.name,
        price: currentProductInfo.price,
        my: true,
        h5: true,
      }
    : { ...currentProductInfo, createTime: currentProductInfo.afsStatu.closeTime, h5: true }
  const data = JSON.stringify(newData)
  //TODO: 客服发送订单信息
  const messageType = currentProductInfo.no ? 'ORDER' : 'PRODUCT'
  console.log(newData, 'newData')
  sendMessagesImage({ messageType, message: data })
}
const handleFocus = () => {
  expressionPopup.value = false
}

const handleExpressionPopup = () => {
  expressionPopup.value = !expressionPopup.value
  pageScrollTo()
}
const handleCancel = () => {
  uni.navigateBack()
}
</script>

<template>
  <view>
    <q-nav :title="shopData.shopName" style="position: fixed" class="nav-container" />
    <!-- #ifndef APP-PLUS -->
    <view class="nav-container--place"></view>
    <!-- #endif -->
    <view class="chat-container">
      <view class="chat-loading"><u-loading v-show="showLoading" mode="flower"></u-loading></view>
      <ServeChat :message="adminMessages" :shopInfo="shopData" />
      <view id="chat-bottom" style="height: 1rpx"></view>
    </view>
    <!-- footer btn start -->
    <view class="chat-input-placeholder" :class="expressionPopup ? 'chat-expression' : ''" />
    <view class="chat-input" :class="expressionPopup ? 'chat-expression' : ''">
      <ProductPopup v-if="currentProductInfo.id && productPopupShow" :info="currentProductInfo" @send-click="handleSendProduct" />
      <view class="chat-input__container" @click="productPopupShow = false">
        <view class="chat-input--input">
          <input
            v-model.trim="chatParams.message"
            :focus="focus"
            :cursor-spacing="50"
            placeholder="请输入内容"
            confirm-type="search"
            @blur="handleBlur"
            @focus="handleFocus"
            @confirm="handleSendChat"
          />
        </view>
        <view class="chat-input--send">
          <q-icon name="icon-biaoqing" size="24px" @click="handleExpressionPopup" />
        </view>
        <view class="chat-input--send" @click="handleImageSend">
          <q-icon name="icon-tupian2" size="20px"></q-icon>
        </view>
        <view class="chat-input--send">
          <u-button type="error" shape="circle" size="mini" :disabled="!chatParams.message.length" @click="handleSendChat">发送</u-button>
        </view>
      </view>
      <Expression @expression-select="handleInsertExpression" />
    </view>
    <!-- footer btn end -->
    <Auth @cancel="handleCancel" />
  </view>
</template>

<style scoped lang="scss">
@include b(place) {
  height: 300rpx;
  background: #000;
}
@include b(nav-container) {
  top: 0;
  left: 0;
  right: 0;
  height: 80rpx;
  background: fff;
  z-index: 99;
  @include m(place) {
    height: 80rpx;
  }
}
@include b(chat-container) {
  padding: 10rpx;
}
@include b(chat-input-placeholder) {
  height: 100rpx;
}
@include b(chat-input) {
  position: fixed;
  height: 100rpx;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 99;
  background: #fff;
  padding: 0 20rpx;
  @include e(container) {
    @include flex;
    justify-content: space-between;
    align-items: center;
    height: 100rpx;
  }
  @include m(send) {
    padding: 10rpx;
    border-radius: 50%;
  }
  @include m(input) {
    width: 500rpx;
    border: 1rpx solid #ccc;
    padding: 10rpx;
    border-radius: 10rpx;
  }
}
@include b(chat-loading) {
  text-align: center;
  height: 60rpx;
  line-height: 60rpx;
}
@include b(chat-expression) {
  height: 500rpx;
  align-items: flex-start;
}
</style>
