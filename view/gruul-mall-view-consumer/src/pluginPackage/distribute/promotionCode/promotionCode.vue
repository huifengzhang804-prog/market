<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { onLoad, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { useGetQrcode, SHARE_HERF } from './useGetQrcode'

// @ts-ignore
const { ctx: Instance } = getCurrentInstance()
const data = ref({
  canvasW: 0,
  canvasH: 0,
  ctx: null,
  isShow: false,
  qrcode: '',
  headerImg: '',
  canvasImgUrl: '',
  userAvatar: useUserStore().userInfo.info.avatar,
  price: '',
  needLongTapSaveImg: false,
  loadingShow: false,
})
const { handleGetQrcode } = useGetQrcode()
const codeinfo = ref<string>('')

onLoad(async ({ distributorCode }: any) => {
  console.log('distributorCode', distributorCode)
  codeinfo.value = distributorCode || ''
  await GetQrcode(distributorCode)
})
// #ifdef MP-WEIXIN
onShareAppMessage(() => {
  const path = `/pages/platform/Index?distributorCode=${codeinfo.value}`
  return {
    from: ['button', 'menu'],
    title: '推荐一个好物给你，请查收！',
    path,
    imageUrl: data.value.canvasImgUrl,
  }
})
onShareTimeline(() => {
  return {
    title: '推荐一个好物给你，请查收！',
    imageUrl: data.value.canvasImgUrl,
  }
})
// #endif

/**
 * 初始化画布
 */
async function __init() {
  data.value.loadingShow = true
  let ctx
  // #ifdef MP-WEIXIN
  ctx = uni.createCanvasContext('myCanvas', Instance)
  // #endif
  // #ifndef MP-WEIXIN
  ctx = uni.createCanvasContext('myCanvas')
  // #endif
  data.value.canvasW = uni.upx2px(750)
  data.value.canvasH = uni.upx2px(900)
  //设置画布背景透明
  ctx.setGlobalAlpha(0.1)
  ctx.setFillStyle('white')
  //设置画布大小
  ctx.fillRect(0, 0, data.value.canvasW, data.value.canvasH)
  ctx.save()
  ctx.restore()
  ctx.setGlobalAlpha(1)
  //绘制 user 头像
  const userAvatar = (await getImageInfo(data.value.userAvatar, '头像')) as {
    errMsg: string
    height: number
    path: string
    width: number
  }
  drawUserRoundImg(ctx, userAvatar.path, uni.upx2px(250), uni.upx2px(20), uni.upx2px(198), uni.upx2px(198), uni.upx2px(100))

  //小程序码
  const qrcodeImg = (await getImageInfo(data.value.qrcode, '小程序码')) as {
    errMsg: string
    height: number
    path: string
    width: number
  }
  //
  ctx.drawImage(qrcodeImg.path, uni.upx2px(80), uni.upx2px(260), uni.upx2px(550), uni.upx2px(550))
  font(ctx, 12, '#cacaca', '扫一扫二维码，开启分销返佣吧！！！', uni.upx2px(145), uni.upx2px(820))
  ctx.draw()
  const time = setTimeout(() => {
    //延迟渲染
    //  ctx.draw 和 uni.canvasToTempFilePath 同步执行 否则出现白屏
    uni.canvasToTempFilePath(
      {
        width: data.value.canvasW,
        height: data.value.canvasH,
        canvasId: 'myCanvas',
        quality: 0.8,
        complete() {
          data.value.needLongTapSaveImg = true
          data.value.loadingShow = false
          if (time) {
            clearTimeout(time)
          }
        },
        success(res) {
          data.value.canvasImgUrl = res.tempFilePath
          data.value.needLongTapSaveImg = true
          data.value.loadingShow = false
        },
      },
      // #ifdef MP-WEIXIN
      Instance,
      // #endif
    )
  }, 500)
}
/**
 * 获取图片
 * @param {*} imgSrc
 */
function getImageInfo(imgSrc: string, name: string) {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src: imgSrc,
      success: (image) => {
        return resolve(image)
      },
      fail: (err) => {
        reject(err)
        uni.showToast({ title: '获取图片失败' + name, icon: 'none' })
        data.value.loadingShow = false
      },
    })
  })
}
/**
 * user 图片
 * @param {*} params
 */
function drawUserRoundImg(ctx: any, img: string, x: number, y: number, width: number, height: number, radius: number) {
  ctx.beginPath()
  ctx.save()
  // 左上角
  ctx.arc(x + radius, y + radius, radius, Math.PI, Math.PI * 1.5)
  // 右上角
  ctx.arc(x + width - radius, y + radius, radius, Math.PI * 1.5, Math.PI * 2)
  // 右下角
  ctx.arc(x + width - radius, y + height - radius, radius, 0, Math.PI * 0.5)
  // 左下角
  ctx.arc(x + radius, y + height - radius, radius, Math.PI * 0.5, Math.PI)
  ctx.clip()
  ctx.drawImage(img, x, y, width, height)
  ctx.restore()
  ctx.closePath()
}
/**
 *  font
 * @param {*} ctx
 * @param {*} fontSize
 * @param {*} fillstyle
 * @param {*} word
 * @param {*} hW
 * @param {*} hH
 */
function font(ctx: any, fontSize: number, fillstyle: string, word: string, x: number, y: number) {
  ctx.setFontSize(fontSize)
  ctx.setFillStyle(fillstyle)
  ctx.fillText(word, x, y)
}
/**
 * 获取二维码
 * @param {*} params
 * @returns {*}WXSceneSession
 */
async function GetQrcode(code: string) {
  handleGetQrcode(
    (qrcode: string) => {
      data.value.qrcode = qrcode
      __init()
    },
    () => {},
    { path: 'pages/platform/Index' },
  )
}
const handleSaved = () => {
  var oA = document.createElement('a')
  oA.download = '下载' // 设置下载的文件名，默认是'下载'
  oA.href = data.value.canvasImgUrl
  document.body.appendChild(oA)
  oA.click()
  setTimeout(() => {
    oA.remove() // 下载之后把创建的元素删除
  }, 5000)
}
/**
 * 保存图片到相册
 */
function saveImage() {
  // #ifndef H5
  uni.saveImageToPhotosAlbum({
    filePath: data.value.canvasImgUrl,
    success(res) {
      uni.showToast({
        title: '已保存到相册',
        icon: 'none',
        duration: 2000,
      })
      setTimeout(() => {
        data.value.isShow = false
      }, 2000)
    },
  })
  // #endif
}
const handleShareWx = (type: 'WXSceneSession' | 'WXSceneTimeline' | 'WXSceneFavorite') => {
  // #ifdef APP-PLUS
  uni.share({
    provider: 'weixin',
    href: SHARE_HERF(codeinfo.value),
    imageUrl: data.value.canvasImgUrl,
    scene: type,
    title: useUserStore().userInfo.info.avatar + '分享',
    summary: '加入我的团队吧！',
    success: function (res) {
      console.log('success:' + JSON.stringify(res))
    },
    fail: function (err) {
      console.log('fail:' + JSON.stringify(err))
    },
  })
  // #endif
}
</script>

<template>
  <view v-if="data.loadingShow" class="loading"> <u-loading mode="circle" size="100"></u-loading></view>
  <view class="vicanvas">
    <canvas class="canvas" canvas-id="myCanvas"></canvas>
  </view>
  <!-- #ifdef H5 -->
  <view class="popup__content__item" style="border-radius: 30rpx" @click="handleSaved">
    <view style="height: 64rpx; width: 64rpx; border: none">
      <q-icon name="icon-a-Frame4" size="64rpx" color="#50B674"></q-icon>
    </view>
    <view class="popup__content__item--text">保存本地</view>
  </view>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <view class="popup" :class="{ popup__show: data.isShow }">
    <view class="popup__content">
      <view class="popup__content__item" @click="handleShareWx('WXSceneSession')">
        <button open-type="share" style="height: 64rpx; width: 64rpx; background-color: #fff; border: none" class="sharebutton">
          <q-icon name="icon-a-Frame2" size="64rpx" color="#0ABC64"></q-icon>
        </button>
        <view class="popup__content__item--text">微信</view>
      </view>
      <!-- #ifdef APP-PLUS -->
      <view class="popup__content__item" @click="handleShareWx('WXSceneTimeline')">
        <q-icon name="icon-a-Frame3" size="64rpx" color="#0ABC64"></q-icon>
        <view class="popup__content__item--text">朋友圈</view>
      </view>
      <!-- #endif -->
      <view class="popup__content__item" @click="saveImage">
        <view style="height: 64rpx; width: 64rpx; background-color: #fff; border: none">
          <q-icon name="icon-a-Frame4" size="64rpx" color="#0ABC64"></q-icon>
        </view>
        <view class="popup__content__item--text">保存本地</view>
      </view>
    </view>
  </view>
  <!-- #endif -->
</template>

<style lang="scss" scoped>
.vicanvas {
  margin: 30rpx;
  border-radius: 20rpx;
  background-color: #fff;
}
.canvas {
  margin: 0 auto;
  width: 710rpx;
  height: 940rpx;
}
@include b(loading) {
  display: flex;
  justify-content: center;
}
@include b(popup) {
  // position: fixed;
  // bottom: 0;
  width: 700rpx;
  height: 185rpx;
  margin: 0 auto;
  border-radius: 30rpx;
  overflow: hidden;
  background-color: #fff;
  text-align: center;
  @include e(show) {
    animation: showPop 0.4s;
  }
  @include e(content) {
    display: flex;
    // align-items: center;
    justify-content: space-around;
    // flex-wrap: wrap;
  }
  @include e(content__item) {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20rpx 40rpx;
    margin: 0 auto;
    width: 710rpx;
    height: 185rpx;
    background-color: #fff;
    // border-radius: 20rpx;
    @include m(text) {
      margin-top: 10rpx;
      color: #333;
      font-size: 28rpx;
    }
  }
}
@include b(sharebutton) {
  @include flex;
  &::after {
    border: none;
  }
}
</style>
