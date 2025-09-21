<template>
  <!-- #ifdef APP-PLUS-->
  <view v-show="data.isShow" class="content" @click.stop="handleColse">
    <!-- #endif -->
    <!--#ifdef MP-WEIXIN -->
    <view v-show="data.isShow" class="content" @click.stop="handleColse">
      <!-- #endif -->
      <!-- canvas 当canvase上有hidden或wx:if属性时，必须要保证导出图片时，canvase组件处于显示状态，否则此时依然可能会导出为空图片。 -->
      <canvas
        :style="{ width: data.canvasW + 'px', height: data.canvasH + 'px' }"
        class="content__canvas"
        canvas-id="myCanvas"
        @click="handleSaveCanvas(data.canvasImgUrl)"
      >
      </canvas>
      <!-- #ifdef H5 -->
      <image
        v-show="data.needLongTapSaveImg"
        :src="data.canvasImgUrl"
        :style="{ width: data.canvasW + 'px', height: data.canvasH + 'px' }"
        class="content__canvas"
        @click.stop="() => {}"
      />
      <view v-if="data.loadingShow" class="loading"> <u-loading mode="circle" size="100"></u-loading></view>
      <view class="save-btn" @click.stop="handleSaved">长按图片保存</view>
      <!-- #endif -->
      <!-- #ifndef H5 -->
      <view class="popup" :class="{ popup__show: data.isShow }">
        <popup-title text="分享" @click="handleColse" />
        <view class="popup__content">
          <view class="popup__content__item" @click="handleShareWx('WXSceneSession')">
            <button open-type="share" style="height: 80rpx; width: 80rpx; background-color: #fff; border: none" class="sharebutton">
              <q-icon name="icon-weixin" size="70rpx" color="#50B674"></q-icon>
            </button>
            <view class="popup__content__item--text">微信</view>
          </view>
          <!-- #ifdef APP-PLUS -->
          <view class="popup__content__item" @click="handleShareWx('WXSceneTimeline')">
            <q-icon name="icon-pengyouquan" size="70rpx" color="#50B674"></q-icon>
            <view class="popup__content__item--text">朋友圈</view>
          </view>
          <!-- #endif -->
          <view class="popup__content__item" @click="saveImage">
            <view style="height: 80rpx; width: 80rpx; background-color: #fff; border: none">
              <q-icon name="icon-chenggong" size="70rpx" color="#50B674"></q-icon>
            </view>
            <view class="popup__content__item--text">分享海报</view>
          </view>
        </view>
      </view>
      <!-- #endif -->
      <!--#ifdef MP-WEIXIN -->
    </view>
    <!-- #endif -->

    <!-- #ifdef APP-PLUS-->
  </view>
  <!-- #endif -->
</template>
<script setup lang="ts">
import { ref, watch, getCurrentInstance, onMounted, nextTick, type PropType } from 'vue'
import PopupTitle from './share-popup-title.vue'
import { useGetQrcode } from './useGetQrcode'
import { useUserStore } from '@/store/modules/user'
import type { Options_ } from './useGetQrcode'

const { handleGetQrcode } = useGetQrcode()

// @ts-ignore
const { ctx: Instance } = getCurrentInstance()
const emit = defineEmits(['close'])
const $props = defineProps({
  goodId: { type: String as PropType<Long>, default: '' },
  shopId: { type: String as PropType<Long>, default: '' },
  title: {
    type: String,
    default: '草莓千层蛋糕',
  },
  subTitle: {
    type: String,
    default: '鲜嫩多汁的草莓融合香甜奶油',
  },
  content: {
    type: String,
    default: '',
  },
  abImg: {
    type: String,
    default: 'https://zhaixiandaojia.com/public/uploads/20210123/f87969714ba1d865f2366b997cbd9fd8.jpg',
  },
  userName: { type: String, default: '' },
})
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
/**
 * 显示
 * @param {*} params
 * @returns {*}WXSceneSession
 */
async function showCanvas(headerImg: string, price: string, options?: Options_) {
  data.value.headerImg = headerImg
  data.value.price = price
  handleGetQrcode(
    (qrcode: string) => {
      data.value.qrcode = qrcode
      data.value.isShow = true
      __init()
    },
    () => {},
    options,
  )
}
/**
 * 分享商品详情地址
 * @param {Long}  shopId
 * @param {Long} goodId
 */
const SHARE_HERF = (goodId: Long, shopId: Long) => {
  const env = import.meta.env.VITE_BASE_URL
  return `${env.replace(/api/, 'h5')}/#/pluginPackage/goods/commodityInfo/InfoEntrance?shopId=${shopId}&goodId=${goodId}`
}
/**
 * APP 分享
 * @param {*} type
 */
const handleShareWx = (type: 'WXSceneSession' | 'WXSceneTimeline' | 'WXSceneFavorite') => {
  // #ifdef APP-PLUS
  uni.share({
    provider: 'weixin',
    href: SHARE_HERF($props.goodId, $props.shopId),
    imageUrl: data.value.canvasImgUrl,
    scene: type,
    title: useUserStore().userInfo.info.avatar + '分享',
    summary: '推荐一个好物给你，请查收！',
    success: function (res) {
      console.log('success:' + JSON.stringify(res))
    },
    fail: function (err) {
      console.log('fail:' + JSON.stringify(err))
    },
  })
  // #endif
}

const handleColse = () => {
  data.value.isShow = false
  emit('close')
}
/**
 * H5下载海报到本地
 */
const handleSaved = () => {
  uni.showToast({
    title: '请长按图片-保存至相册',
  })
}
watch(
  () => data.value.isShow,
  (val) => {
    if (!val) {
      data.value.needLongTapSaveImg = false
      data.value.loadingShow = false
    }
  },
)

const handleSaveCanvas = (res: string) => {
  uni.previewImage({
    urls: [res],
  })
}
/**
 * 初始化画布
 */
async function __init() {
  data.value.loadingShow = true
  let ctx
  // #ifdef MP-WEIXIN
  ctx = uni.createCanvasContext('myCanvas', Instance)
  // #endif
  // #ifdef APP-PLUS
  ctx = uni.createCanvasContext('myCanvas')
  // #endif
  data.value.canvasW = uni.upx2px(550)
  data.value.canvasH = uni.upx2px(900)
  const hW = uni.upx2px(450)
  const hH = uni.upx2px(450)
  //设置画布背景透明
  ctx.setFillStyle('rgba(255, 255, 255, 0.1)')
  //设置画布大小
  ctx.fillRect(0, 0, data.value.canvasW, data.value.canvasH)
  //绘制圆角背景
  drawRoundRect(ctx, 0, 0, data.value.canvasW, data.value.canvasH, uni.upx2px(30), '#FFFFFF')
  //绘制标题矩形
  ctx.save()
  drawRoundRect(ctx, 40, 10, data.value.canvasW - uni.upx2px(150), uni.upx2px(45), uni.upx2px(45), '#F2F2F2')
  ctx.restore()
  //绘制 user 头像
  const userAvatar = (await getImageInfo(data.value.userAvatar)) as {
    errMsg: string
    height: number
    path: string
    width: number
  }
  drawUserRoundImg(ctx, userAvatar.path, 50, 11, uni.upx2px(40), uni.upx2px(40), uni.upx2px(20))
  //绘制 msg
  font(ctx, 12, '#333333', '推荐一个好物给你，请查收', 75, 26)
  //获取标题图片
  const headerImg = (await getImageInfo(data.value.headerImg)) as {
    errMsg: string
    height: number
    path: string
    width: number
  }
  //绘制标题图
  drawRoundImg(ctx, headerImg.path, 25, 40, hW, hH, uni.upx2px(16))
  //绘制标题
  dealWords(ctx, 12, '#101010', $props.title, hW, (data.value.canvasW - hW) / 2, (data.value.canvasW - hW) / 2 + hH + uni.upx2px(70), 2)
  if (data.value.price) {
    font(ctx, 12, '#F12F22', '￥', (data.value.canvasW - hW) / 2, (data.value.canvasW - hW) / 2 + hH + uni.upx2px(70))
    font(ctx, 16, '#F12F22', data.value.price, (data.value.canvasW - hW) / 2 + uni.upx2px(20), (data.value.canvasW - hW) / 2 + hH + uni.upx2px(70))
  }
  //小程序码
  const qrcodeImg = (await getImageInfo(data.value.qrcode)) as {
    errMsg: string
    height: number
    path: string
    width: number
  }
  ctx.drawImage(qrcodeImg.path, uni.upx2px(184), (data.value.canvasW - hW) / 2 + hH + uni.upx2px(180), uni.upx2px(156), uni.upx2px(156))
  // msg
  font(ctx, 11, '#999999', '长按或扫码查看', uni.upx2px(184), (data.value.canvasW - hW) / 2 + hH + uni.upx2px(370))
  ctx.draw(true)
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
 * 带圆角图片
 * @param {*} params
 */
function drawRoundImg(ctx: any, img: string, x: number, y: number, width: number, height: number, radius: number) {
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
  ctx.stroke()
  ctx.clip()
  ctx.drawImage(img, x, y, width, height)
  ctx.restore()
  ctx.closePath()
}
/**
 * 圆角矩形
 * @param {*} ctx
 * @param {*} x
 * @param {*} y
 * @param {*} width
 * @param {*} height
 * @param {*} radius
 * @param {*} color
 */
function drawRoundRect(ctx: any, x: number, y: number, width: number, height: number, radius: number, color: string) {
  ctx.save()
  ctx.beginPath()
  ctx.setFillStyle(color)
  ctx.setStrokeStyle(color)
  ctx.setLineJoin('round') //交点设置成圆角
  ctx.setLineWidth(radius)
  ctx.strokeRect(x + radius / 2, y + radius / 2, width - radius, height - radius)
  ctx.fillRect(x + radius, y + radius, width - radius * 2, height - radius * 2)
  ctx.stroke()
  ctx.closePath()
}
/**
 * 处理文字段落问题
 * @param {*} ctx 画布上下文
 * @param {*} fontSize 字体大小
 * @param {*} fillstyle
 * @param {*} word 需要处理的文字
 * @param {*} maxWidth 一行文字最大宽度
 * @param {*} x 文字在x轴要显示的位置
 * @param {*} y 文字在y轴要显示的位置
 * @param {*} maxLine 文字最多显示的行数
 */
function dealWords(
  ctx: any,
  fontSize: number,
  fillstyle: string,
  word: string,
  maxWidth: number,
  x: number,
  y: number,
  maxLine: number,
  lineHeight = 20,
) {
  ctx.setFillStyle(fillstyle)
  ctx.setFontSize(fontSize)
  var allRow = Math.ceil(ctx.measureText(word).width / maxWidth) //实际总共能分多少行
  var count = allRow >= maxLine ? maxLine : allRow //实际能分多少行与设置的最大显示行数比，谁小就用谁做循环次数
  var endPos = 0 //当前字符串的截断点
  for (var j = 0; j < count; j++) {
    var nowStr = word.slice(endPos) //当前剩余的字符串
    var rowWid = 0 //每一行当前宽度
    if (ctx.measureText(nowStr).width > maxWidth) {
      //如果当前的字符串宽度大于最大宽度，然后开始截取
      for (var m = 0; m < nowStr.length; m++) {
        rowWid += ctx.measureText(nowStr[m]).width //当前字符串总宽度
        if (rowWid > maxWidth) {
          if (j === maxLine - 1) {
            //如果是最后一行
            ctx.fillText(nowStr.slice(0, m - 1) + '...', x, y + (j + 1) * lineHeight) //(j+1)*36这是每一行的高度
          } else {
            ctx.fillText(nowStr.slice(0, m), x, y + (j + 1) * lineHeight)
          }
          endPos += m //下次截断点
          break
        }
      }
    } else {
      //如果当前的字符串宽度小于最大宽度就直接输出
      ctx.fillText(nowStr.slice(0), x, y + (j + 1) * lineHeight)
    }
  }
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
 * 获取图片
 * @param {*} imgSrc
 */
function getImageInfo(imgSrc: string, isWxChat = false) {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src: imgSrc,
      success: (image) => {
        return resolve(image)
      },
      fail: (err) => {
        reject(err)
        uni.showToast({ title: '获取图片失败', icon: 'none' })
        data.value.loadingShow = false
      },
    })
  })
}
/**
 * 保存图片到相册
 */
function saveImage() {
  // #ifndef H5
  uni.saveImageToPhotosAlbum({
    filePath: data.value.canvasImgUrl,
    success(res) {
      console.log('已保存到相册', 123123)
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
defineExpose({ showCanvas })
</script>

<style scoped lang="scss">
@include b(content) {
  position: fixed;
  z-index: 0;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999999;
  background: rgba(0, 0, 0, 0.2);
  background: #c9c9c9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  @include e(canvas) {
    margin: 0 0 250rpx 0;
  }
  /* #ifndef H5 */
  @include e(canvas) {
    margin: 0 0 250rpx 0;
  }
  /* #endif */
}
@include b(sharebutton) {
  @include flex;
  &::after {
    border: none;
  }
}
@include b(popup) {
  position: fixed;
  bottom: 0;
  width: 100vw;
  height: 300rpx;
  border-radius: 30rpx 30rpx 0rpx 0rpx;
  background-color: rgba(255, 255, 255, 1);
  text-align: center;
  @include e(show) {
    animation: showPop 0.4s;
  }
  @include e(content) {
    display: flex;
    flex-wrap: wrap;
  }
  @include e(content__item) {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 20rpx 40rpx;
    width: calc(25% - (40rpx * 2));
    @include m(text) {
      margin-top: 10rpx;
      color: #101010;
      font-size: 24rpx;
    }
  }
}
@keyframes showPop {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
@include b(loading) {
  position: absolute;
}
/* #ifdef H5 */
.save-btn {
  margin-top: 35rpx;
  color: #ffffff;
  background: linear-gradient(to right, #000, #000);
  padding: 15rpx 40rpx;
  border-radius: 50rpx;
}
/* #endif */
@include b(qrcode) {
  position: relative;
  width: 0;
  height: 0;
  @include e(item) {
    position: absolute;
    left: -99999px;
    top: -99999px;
    z-index: -99;
  }
}
</style>
