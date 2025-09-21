<script setup lang="ts">
import { ref, reactive, type PropType, onMounted } from 'vue'
import { isVideo, imgPreview } from '@/libs/formatImage'

const $props = defineProps({
  max: {
    //展示图片最大值
    type: Number,
    default: 1,
  },
  chooseNum: {
    //选择图片数
    type: Number,
    default: 6,
  },
  modelMin: { type: Boolean, default: false },
  sourceType: { type: Array as PropType<string[]>, default: () => ['album', 'camera'] },
  compress: { type: Boolean, default: true },
  camera: { type: String as PropType<'front' | 'back'>, default: 'back' },
  maxDuration: {
    validator(val) {
      return Number(val) >= 60
    },
    default: 60,
  },
  // 限制上传的视频时常 单位秒
  uploadMaxDuration: { type: Number, default: 70 },
  // 限制上传的视频大小 单位M
  uploadMaxSize: { type: Number, default: 9999 },
  action: { type: String, default: '' },
  name: {
    //发到后台的文件参数名
    type: String,
    default: 'file',
  },
  formData: {
    //HTTP 请求中其他额外的 form data
    type: Object,
    default: () => {},
  },

  headers: {
    //上传的请求头部
    type: Object,
    default: () => {},
  },
  modelValue: {
    //受控图片列表
    type: Array,
    default: () => [],
  },
  remove: {
    //是否展示删除按钮
    type: Boolean,
    default: true,
  },
  appVideoPoster: {
    //app端视频展示封面 只对app有效
    type: String,
    default: '/static/htz-image-upload/play.png',
  },
  maxleng: {
    type: Number,
    default: 6,
  },
  delRight: {
    type: Number,
    default: 10,
  },
})
const $emit = defineEmits(['chooseSuccess', 'uploadSuccess', 'uploadFail', 'update:modelValue', 'input', 'imgDelete'])
const data = reactive<{ uploadLists: string[]; mediaTypeData: string[]; previewVideoSrc: string }>({
  uploadLists: [], // 网络路径
  mediaTypeData: ['image', 'video', 'all'],
  previewVideoSrc: '',
})
const show = ref(false)
const autoplayVideo = ref(false)
const currentIndex = ref(0)
const videoContext = ref()

onMounted(() => {
  videoContext.value = uni.createVideoContext('assessVideo')
})

/**
 * 选择图片或视频
 */
const handleChooseFile = () => {
  if (data.uploadLists.length >= $props.maxleng) {
    uni.showToast({ title: `最多可上传${$props.maxleng}张图片`, icon: 'none' })
    return
  }
  uni.showActionSheet({
    itemList: ['相册', '视频'],
    success: (res) => {
      if (res.tapIndex === 1) {
        videoAdd()
      } else if (res.tapIndex === 0) {
        imgAdd()
      }
    },
    fail: (res) => {
      console.log(res.errMsg)
    },
  })
}
/**
 * 添加视频
 */
function videoAdd() {
  uni.chooseVideo({
    compressed: $props.compress,
    sourceType: $props.sourceType,
    camera: $props.camera,
    maxDuration: $props.maxDuration,
    success: (res) => {
      // 这里是添加了视频
      console.log('videoAdd', res)
      if (res.duration > $props.uploadMaxDuration) {
        uni.showToast({
          title: `上传视频需低于${$props.uploadMaxDuration}秒`,
          icon: 'none',
          duration: 2000,
        })
        return
      }
      if (res.size > $props.uploadMaxSize * 1024 * 1024) {
        uni.showToast({
          title: `上传视频需小于${$props.uploadMaxSize}M`,
          icon: 'none',
          duration: 2000,
        })
        return
      }
      console.log('res', res)
      chooseSuccessMethod([res.tempFilePath], 1)
    },
  })
}
/**
 * 添加图片
 */
function imgAdd() {
  let nowNum = Math.abs(data.uploadLists.length - $props.max)
  let thisNum = $props.chooseNum > data.uploadLists.length ? nowNum : 0 //可选数量
  console.log(thisNum, 'thisNum')
  uni.chooseImage({
    count: thisNum,
    sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
    sourceType: $props.sourceType,
    success: (res) => {
      console.log('res', res)
      chooseSuccessMethod(res.tempFilePaths as string[], 0)
    },
  })
}
/**
 * 选择成功的方法
 * @param {*} filePaths 路径
 * @param {*} type 0 为图片 1为视频
 */
function chooseSuccessMethod(filePaths: string[], type: 0 | 1) {
  if ($props.action === '') {
    //未配置上传路径
    console.log('未配置上传路径')
    $emit('chooseSuccess', filePaths, type)
  } else if (type === 1) {
    // 上传视频
    imgUpload(filePaths, type)
  } else {
    // 上传图片
    imgUpload(filePaths, type)
  }
}
/**
 * 选择成功的方法
 * @param {*} tempFilePaths 路径
 * @param {*} type 0 为图片 1为视频
 */
function imgUpload(tempFilePaths: string[], type: 0 | 1) {
  if ($props.action === '') {
    uni.showToast({
      title: '未配置上传地址',
      icon: 'none',
      duration: 2000,
    })
    return false
  }
  let uploadImgs: any[] = []
  tempFilePaths.forEach((item, index) => {
    uploadImgs.push(
      new Promise((resolve, reject) => {
        uni.showLoading({ title: '上传中' })
        // uploadFile 上传本地文件
        const uploadTask = uni.uploadFile({
          url: import.meta.env.VITE_BASE_URL + $props.action,
          filePath: item, //  本地文件路径
          name: $props.name,
          fileType: 'image',
          formData: $props.formData,
          header: $props.headers,
          success: (uploadFileRes: any) => {
            uploadFileRes.fileType = type
            if (uploadFileRes.statusCode !== 200) {
              reject('服务器内部错误')
              $emit('uploadFail', '服务器内部错误')
              return
            }
            if (uploadFileRes.data) {
              const url: string = JSON.parse(uploadFileRes.data).data
              data.uploadLists.push(url)
              const index = data.uploadLists.length - $props.maxleng
              if (index > 0) {
                // 如果上传的图片或视频超出最大限制需要截取
                data.uploadLists.splice(0, index)
              }
              $emit('input', data.uploadLists)
              $emit('update:modelValue', data.uploadLists)
              resolve(uploadFileRes)
              $emit('uploadSuccess', uploadFileRes)
              return
            }
          },
          fail: (err) => {
            reject(err)
            $emit('uploadFail', err)
          },
          complete: (res) => {
            console.log('res', res)
            uni.hideLoading()
          },
        })
        uploadTask.onProgressUpdate((res) => {
          // 先执行
          console.log('res', res)
          uni.showLoading({
            title: '上传中' + res.progress + '%',
          })
          if (res.progress === 100) {
            uni.hideLoading()
          }
        })
      }),
    )
  })
  Promise.all(uploadImgs) //执行所有需请求的接口
    .then((results) => {
      uni.hideLoading()
    })
    .catch((res) => {
      uni.hideLoading()
      $emit('uploadFail', res)
    })
}
function getFileUrl(item: string) {
  return item
}
function previewVideo(src: string) {
  data.previewVideoSrc = src
  autoplayVideo.value = true
}

function previewVideoClose() {
  autoplayVideo.value = false
  data.previewVideoSrc = ''
}
function imgDel(index: number) {
  show.value = true
  currentIndex.value = index
  // uni.showModal({
  //     title: '提示',
  //     content: '您确定要删除么?',
  //     success: (res) => {
  //         if (res.confirm) {
  //             let delUrl = data.uploadLists[index]
  //             data.uploadLists.splice(index, 1)
  //             $emit('input', data.uploadLists)
  //             $emit('update:modelValue', data.uploadLists)
  //             $emit('imgDelete', {
  //                 del: delUrl,
  //                 tempFilePaths: data.uploadLists,
  //             })
  //         } else if (res.cancel) {
  //             console.log('res', res)
  //         }
  //     },
  // })
}
const handleDelImage = () => {
  let delUrl = data.uploadLists[currentIndex.value]
  data.uploadLists.splice(currentIndex.value, 1)
  $emit('input', data.uploadLists)
  $emit('update:modelValue', data.uploadLists)
  $emit('imgDelete', {
    del: delUrl,
    tempFilePaths: data.uploadLists,
  })
  show.value = false
}
</script>

<template>
  <view class="upload-list">
    <view v-for="(item, index) in data.uploadLists" :key="item" class="upload-list__item">
      <!-- <video v-if="isVideo(item)" :src="item" object-fit="contain" class="upload-list__item--video" :controls="false" /> -->
      <u-image
        v-if="isVideo(item)"
        :src="item"
        :width="214"
        :height="214"
        class="upload-list__item--video"
        border-radius="10"
        @click="previewVideo(item)"
      />
      <u-image v-else :width="214" :height="214" :src="item" border-radius="10" @click="imgPreview(index, data.uploadLists)" />
      <view class="upload-list__del" :style="{ right: delRight + 'rpx' }" @click="imgDel(index)">
        <u-icon name="close-circle" size="40rpx" color="#ffffff" />
      </view>
      <view v-if="isVideo(item)" class="upload-list__autoplay-btn" @click="previewVideo(item)">
        <u-icon name="play-right-fill" size="40rpx" color="#ffffff" />
      </view>
    </view>

    <!-- 选择图片或视频 s -->
    <view v-if="data.uploadLists.length < $props.maxleng" @click="handleChooseFile">
      <slot>
        <view key="choose" class="choose" :class="{ 'choose--thereAre': data.uploadLists.length }">
          <u-icon name="camera" size="74" color="#c0c4cc" />
          <text class="choose--text">添加图片/视频</text>
        </view>
      </slot>
    </view>
    <!-- 选择图片或视频 e -->
    <u-mask :show="autoplayVideo" :custom-style="{ backgroundColor: '#000', display: 'flex', alignItems: 'center' }" @click="previewVideoClose">
      <video
        v-if="autoplayVideo"
        id="assessVideo"
        :src="data.previewVideoSrc"
        show-mute-btn
        x5-video-player-type="h5-page"
        object-fit="contain"
        class="preview-video"
      ></video>
    </u-mask>
  </view>
  <u-modal v-model="show" :show-title="false" :show-confirm-button="false">
    <view class="modal-close" @click="show = false">
      <u-icon name="close-circle" size="50rpx" />
    </view>
    <view class="modal-close__title">确认删除？</view>
    <view class="modal-close__btns">
      <u-button plain shape="circle" :custom-style="{ width: '200rpx', height: '70rpx', color: 'red' }" @click="show = false">取消</u-button>
      <u-button shape="circle" type="error" :custom-style="{ width: '200rpx', height: '70rpx' }" @click="handleDelImage">删除</u-button>
    </view>
  </u-modal>
</template>

<style scoped lang="scss">
@include b(upload-list) {
  display: flex;
  flex-wrap: wrap;
  color: white;
  @include e(item) {
    position: relative;
    width: calc(33% - 5 * 2rpx);
    height: 214rpx;
    margin: 5rpx;
    @include m(video) {
      width: 214rpx;
      height: 214rpx;
      border-radius: 10rpx;
    }
  }

  @include e(del) {
    position: absolute;
    right: 10rpx;
    top: 10rpx;
  }
  @include e(autoplay-btn) {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 50%;
    border: 2px solid #fff;
    padding: 5rpx;
    @include flex;
  }
}
@include b(preview-video) {
  width: 100vw;
  z-index: -1;
}
@include b(choose) {
  padding: 54rpx 0;
  width: 664rpx;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background: rgb(244, 245, 246);
  border-radius: 10rpx;
  @include m(thereAre) {
    padding: 54rpx 0;
    width: 214rpx;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    background: rgb(244, 245, 246);
    border-radius: 20rpx;
  }
  @include m(text) {
    font-size: 18rpx;
    color: #9a9a9a;
    text-align: center;
  }
}
@include b(modal-close) {
  @include flex;
  justify-content: flex-end;
  padding: 10rpx;
  padding-bottom: 0;
  @include e(title) {
    font-size: 35rpx;
    font-weight: 700;
    text-align: center;
  }
  @include e(btns) {
    margin: 50rpx 0 30rpx 0;
    @include flex;
  }
}
</style>
