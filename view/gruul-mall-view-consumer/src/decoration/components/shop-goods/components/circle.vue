<template>
  <view>
    <view :style="cBox">
      <text v-if="!slot">{{ animationPercent }}%</text>
      <view v-if="slot" :style="slotStyle"><slot name="content"></slot></view>
      <view :style="faStyle">
        <view :style="leftBox">
          <view :style="leftStyle"></view>
        </view>
        <view :style="rightStyle"></view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  options: { styleIsolation: 'shared' },
  props: {
    //大小
    size: {
      type: Number,
      default: 60,
    },
    //进度条颜色
    circleColor: {
      type: String,
      default: '#32CDA5',
    },
    //圆环背景色
    defaultColor: {
      type: String,
      default: '#e9e9e9',
    },
    //圆环宽度
    circleWidth: {
      type: Number,
      default: 5,
    },
    //百分比
    percent: {
      type: Number,
      default: 0,
    },
    //动画效果
    animation: {
      type: Boolean,
      default: false,
    },
    //帧动画间隔
    animationSpeed: {
      type: Number,
      default: 1,
    },
    //动画方向
    clockwise: {
      type: Boolean,
      default: true,
    },
    //自定义七点位置
    direction: {
      type: Number,
      default: 0,
    },
    bgColor: {
      type: String,
      default: '#ffffff',
    },
  },
  emits: ['animationPercent', 'onComplete'],
  data() {
    return {
      animationPercent: 0,
    }
  },
  computed: {
    sizeAdapter() {
      return this.size % 2 === 0 ? this.size : this.size - 1
    },
    slot() {
      if (this.$slots.content) {
        return true
      }
      return false
    },
    cBox() {
      var size = this.sizeAdapter
      var circleWidth = this.circleWidth
      var style = `	
						position:relative !important;
						height:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
						width:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
						display:flex !important;
						justify-content: center !important;
						align-items: center !important;
					`
      return style
    },
    slotStyle() {
      var size = this.sizeAdapter
      var circleWidth = this.circleWidth
      var style = `
						border-radius:50% !important;
						height:${uni.upx2px(size * 2)}px !important;
						width:${uni.upx2px(size * 2)}px !important;
						display:flex !important;
						justify-content: center !important;
						align-items: center !important;
                        background-color: ${this.bgColor}
					`
      return style
    },
    faStyle() {
      var size = this.sizeAdapter
      var circleWidth = this.circleWidth
      var defaultColor = this.defaultColor
      var direction = this.direction
      var clockwise = this.clockwise
      var style = `
							 position:absolute !important;
							 border-radius:50% !important;
							 display:flex !important;
							 justify-content: center !important;
							 align-items: center !important;
							 top:0 !important;
							 left:0 !important;
							 height:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
							 width:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
							 border:${uni.upx2px(circleWidth * 2)}px ${defaultColor} solid !important;
							 transform:rotate(${direction}deg) rotateY(${clockwise ? 0 : 180}deg) !important;
							`
      return style
    },
    leftBox() {
      var size = this.sizeAdapter
      var circleWidth = this.circleWidth
      var style = `
					height:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
					width:${uni.upx2px((circleWidth * 2 + size) * 2)}px !important;
					position:absolute !important;
					top:-${uni.upx2px(circleWidth * 2)}px !important;
					left:-${uni.upx2px(circleWidth * 2)}px !important;
					opacity:1 !important;
					clip:rect(0 ${uni.upx2px(circleWidth * 2 + size)}px ${uni.upx2px((circleWidth * 2 + size) * 2)}px 0) !important;
				`
      return style
    },
    leftStyle() {
      var size = this.sizeAdapter
      var circleColor = this.circleColor
      var circleWidth = this.circleWidth
      var percent = this.animation ? this.animationPercent : this.percent
      var style
      style = `
					height:${uni.upx2px((size + this.circleWidth * 2) * 2)}px !important;
					width:${uni.upx2px((size + this.circleWidth * 2) * 2)}px !important;
					border:${uni.upx2px(circleWidth * 2)}px ${circleColor} solid !important;
					border-radius:50% !important; 
					z-index:${percent > 50 ? 1 : 0} !important;
					position:absolute !important;
					top:0px !important;
					left:0px !important;
					transform:rotate(${percent > 50 ? 180 : (percent / 100) * 360}deg) !important;
					clip:rect(0 ${uni.upx2px((circleWidth * 2 + size) * 2)}px ${uni.upx2px((circleWidth * 2 + size) * 2)}px ${uni.upx2px(
            circleWidth * 2 + size,
          )}px ) !important;
					box-shadow: 0 0 2px rgba(0,0,0,0.1) !important;
					`
      return style
    },
    rightStyle() {
      var direction = this.direction
      var size = this.sizeAdapter
      var circleColor = this.circleColor
      var defaultColor = this.defaultColor
      var circleWidth = this.circleWidth
      var percent = this.animation ? this.animationPercent : this.percent
      var style = `
							 height:${uni.upx2px((size + this.circleWidth * 2) * 2)}px !important;
							 width:${uni.upx2px((size + this.circleWidth * 2) * 2)}px !important;
							 position:absolute !important;
							 border:${uni.upx2px(circleWidth * 2)}px ${percent > 50 ? circleColor : defaultColor} solid !important;
							 border-radius:50% !important;
							 z-index:${percent > 50 ? 2 : 100} !important;
							 top:-${uni.upx2px(circleWidth * 2)}px !important;
							 left:-${uni.upx2px(circleWidth * 2)}px !important;
							 transform:rotate(${percent > 50 ? (percent / 100) * 360 : 0}deg) !important;
							 clip:rect(0 ${uni.upx2px((circleWidth * 2 + size) * 2)}px ${uni.upx2px((circleWidth * 2 + size) * 2)}px ${uni.upx2px(
                 circleWidth * 2 + size,
               )}px ) !important; 
							 box-shadow: 0 0 2px rgba(0,0,0,0.1) !important;
							`

      return style
    },
  },
  watch: {
    animation: {
      handler(val) {
        if (this.animation) {
          this.loadAnimation()
        } else {
          this.animationPercent = this.percent
        }
      },
      immediate: true,
    },
  },
  methods: {
    //动画加载方法
    loadAnimation() {
      this.animationPercent = 0
      // eslint-disable-next-line @typescript-eslint/no-this-alias
      const that = this
      var i = setInterval(() => {
        if (that.animationPercent >= that.percent) {
          clearInterval(i)
          that.$emit('onComplete')
        } else {
          that.animationPercent += 1
          that.$emit('animationPercent', that.animationPercent)
        }
      }, that.animationSpeed / that.percent)
    },
  },
}
</script>
