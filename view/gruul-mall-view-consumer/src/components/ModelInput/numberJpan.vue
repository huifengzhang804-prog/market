<template>
  <view v-show="flag" class="numberJpan" :style="{ ...wc, height: `${windowHeight}px` }">
    <view class="myshuru" :style="obj">
      <view class="gb" :style="gsbstyle" @tap="close()">
        <u-icon name="close" size="30"></u-icon>
      </view>
      <view class="shuruTitle"> 请输入数量 </view>
      <view class="center-x">
        <view class="srk">
          {{ number }}
        </view>
      </view>
      <view class="confirm_btn" :class="{ disabled: zeroDisabled }" @touchstart.prevent="confirm">确认</view>
    </view>
    <view class="jpan" :style="tsfY">
      <view class="nav" @tap="close()">
        <u-icon name="close" size="30"></u-icon>
      </view>
      <view class="main">
        <view
          v-for="i in 9"
          :key="i"
          :class="{
            disabled: disabled,
          }"
          @touchstart.prevent="numshuzi(i)"
          >{{ i }}</view
        >
        <view> </view>
        <view
          :class="{
            disabled: zeroDisabled || disabled,
          }"
          @touchstart.prevent="numshuzi(0)"
          >0</view
        >
        <view
          :class="{
            disabled: zeroDisabled,
          }"
          @touchstart.prevent="del()"
        >
          <u-icon name="arrow-leftward" size="30"></u-icon>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { computed, onMounted, reactive, ref, watch } from 'vue'

const props = defineProps({
  flag: {
    type: Boolean,
    default: false,
  },
  modelValue: {
    type: Number,
    default: 0,
  },
  max: {
    type: Number,
    default: 99999,
  },
  min: {
    type: Number,
    default: 1,
  },
})
const emits = defineEmits(['update:flag', 'update:modelValue'])
const flag = useVModel(props, 'flag', emits)
const modelValue = useVModel(props, 'modelValue', emits)
watch(
  () => flag.value,
  (val) => {
    if (val) {
      open()
    } else {
      close()
    }
  },
)
const wc = reactive({
  backgroundColor: 'rgba(0, 0, 0,.0)',
})
const obj = reactive({
  opacity: 0,
  bottom: '0',
})
const number = ref('')
const gsbstyle = reactive({
  opacity: 0,
})
const tsfY = reactive({
  transform: 'translateY(100%)',
})
const windowHeight = ref(0)

// 计算属性
const disabled = computed(() => {
  if (Number(number.value) >= props.max) {
    number.value = props.max.toString()
  }
  return Number(number.value) >= props.max
})
const zeroDisabled = computed(() => number.value === '')

// 方法
const del = () => {
  if (number.value) {
    number.value = number.value.slice(0, number.value.length - 1)
  }
  uni.vibrateShort()
}

const numshuzi = (num: number) => {
  if (num === 0 && zeroDisabled.value) {
    return
  }
  if (disabled.value) {
    return
  }
  number.value += num
  uni.vibrateShort()
}

const open = () => {
  flag.value = true
  number.value = modelValue.value.toString()
  setTimeout(() => {
    wc.backgroundColor = 'rgba(0, 0, 0, 0.5)'
    obj.opacity = 1
    obj.bottom = '500rpx'
    gsbstyle.opacity = 1
    tsfY.transform = 'translateY(0%)'
  }, 100)
}

const close = () => {
  wc.backgroundColor = 'rgba(0, 0, 0, 0)'
  obj.opacity = 0
  obj.bottom = '0'
  gsbstyle.opacity = 0
  tsfY.transform = 'translateY(100%)'
  flag.value = false
}

const confirm = () => {
  if (zeroDisabled.value) {
    return
  }
  modelValue.value = Number(number.value)
  close()
}

// 生命周期钩子
onMounted(() => {
  uni.getSystemInfo({
    success: (res) => {
      windowHeight.value = res.windowHeight
    },
  })
})
defineExpose({
  open,
  close,
})
</script>

<style lang="scss" scoped>
.numberJpan {
  width: 750rpx;
  position: fixed;
  top: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
  transition: all 0.5s;
  z-index: 999999999999999999999;
  .myshuru {
    transition: all 0.5s;
    position: absolute;
    width: 500rpx;
    height: 300rpx;
    opacity: 0;
    transform: translate(-50%, -50%);
    background-color: #ffffff;
    left: 50%;
    color: #000000;
    border-radius: 20rpx;
    overflow: hidden;
    .gb {
      position: absolute;
      font-size: 50rpx;
      top: 0;
      color: #aaaaaa;
      left: 30rpx;
      transition: all 0.5s;
    }
    .shuruTitle {
      margin: 25rpx auto;
      font-weight: 900;
      text-align: center;
      font-size: 30rpx;
    }
    .confirm_btn {
      width: 90%;
      height: 80rpx;
      background: linear-gradient(95.47deg, #fa3534 0%, #ff794d 78.13%);
      color: #fff;
      font-size: 28rpx;
      text-align: center;
      line-height: 80rpx;
      height: 80rpx;
      border-radius: 60rpx;
      position: absolute;
      top: 200rpx;
      left: 50%;
      transform: translateX(-50%);
    }
    .disabled {
      background: #ccc;
    }
    .center-x {
      width: 90%;
      height: 80rpx;
      border: 1px solid #eeeeee;
      border-radius: 20rpx;
      position: absolute;
      overflow: hidden;
      left: 50%;
      transform: translateX(-50%);
      top: 100rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      .srk {
        position: relative;
      }
      .srk::after {
        position: absolute;
        content: '|';
        top: 50%;
        right: 0;
        color: blue;
        transform: translate(6rpx, -22.5rpx);
        // 闪烁动画
        animation: blink 0.5s infinite alternate;
      }
      @keyframes blink {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }
    }
  }
  .jpan {
    width: 750rpx;
    height: 500rpx;
    background-color: #ffffff;
    position: absolute;
    bottom: 0;
    transition: all 0.5s;
    z-index: 9999999999999999999999;
    overflow: hidden;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    .nav {
      text-align: center;
      line-height: 50rpx;
      box-sizing: border-box;
      border-bottom: 1px solid #eeeeee;
    }
    .main {
      width: 100%;
      flex: 1;
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      grid-template-rows: repeat(4, 1fr);
      place-items: center;
      text-align: center;
      .disabled {
        color: #999;
      }
      > view {
        box-sizing: border-box;
        font-size: 40rpx;
        text-align: center;
        border: 1px solid #f4f4f4;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      view:active {
        background-color: #eeeeee;
      }
    }
  }
}
</style>
