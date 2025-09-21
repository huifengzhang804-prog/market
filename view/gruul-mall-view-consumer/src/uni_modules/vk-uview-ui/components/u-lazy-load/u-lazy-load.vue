<template>
	<view class="u-wrap" :style="{
			  opacity: Number(opacity),
			  borderRadius: borderRadius + 'rpx',
			  // 因为time值需要改变,所以不直接用duration值(不能改变父组件prop传过来的值)
			  transition: `opacity ${time / 1000}s ease-in-out`
		   }"
	 :class="'u-lazy-item-' + elIndex">
		<view :class="'u-lazy-item-' + elIndex">
			<image :style="{borderRadius: borderRadius + 'rpx', height: imgHeight}" v-if="!isError" class="u-lazy-item"
			 :src="isShow ? image : loadingImg" :mode="imgMode" @load="imgLoaded" @error="loadError" @tap="clickImg"></image>
			<image :style="{borderRadius: borderRadius + 'rpx', height: imgHeight}" class="u-lazy-item error" v-else :src="errorImg"
			 :mode="imgMode" @load="errorImgLoaded" @tap="clickImg"></image>
		</view>
	</view>
</template>

<script>
	/**
	 * lazyLoad 懒加载
	 * 懒加载使用的场景为：页面有很多图片时，APP会同时加载所有的图片，导致页面卡顿，各个位置的图片出现前后不一致等.
	 * @tutorial https://www.uviewui.com/components/lazyLoad.html
	 * @property {String Number} index 用户自定义值，在事件触发时回调，用以区分是哪个图片
	 * @property {String} image 图片路径
	 * @property {String} loading-img 预加载时的占位图
	 * @property {String} error-img 图片加载出错时的占位图
	 * @property {String} threshold 触发加载时的位置，见上方说明，单位 rpx（默认300）
	 * @property {String Number} duration 图片加载成功时，淡入淡出时间，单位ms（默认）
	 * @property {String} effect 图片加载成功时，淡入淡出的css动画效果（默认ease-in-out）
	 * @property {Boolean} is-effect 图片加载成功时，是否启用淡入淡出效果（默认true）
	 * @property {String Number} border-radius 图片圆角值，单位rpx（默认0）
	 * @property {String Number} height 图片高度，注意：实际高度可能受img-mode参数影响（默认450）
	 * @property {String Number} mg-mode 图片的裁剪模式，详见image组件裁剪模式（默认widthFix）
	 * @event {Function} click 点击图片时触发
	 * @event {Function} load 图片加载成功时触发
	 * @event {Function} error 图片加载失败时触发
	 * @example <u-lazy-load :image="image" :loading-img="loadingImg" :error-img="errorImg"></u-lazy-load>
	 */
	export default {
		name: 'u-lazy-load',
    emits: ["click", "load", "error"],
		props: {
			index: {
				type: [Number, String]
			},
			// 要显示的图片
			image: {
				type: String,
				default: ''
			},
			// 图片裁剪模式
			imgMode: {
				type: String,
				default: 'aspectFill'
			},
			// 占位图片路径
			loadingImg: {
				type: String,
				default: 'data:image/png;base64,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'
			},
			// 加载失败的错误占位图
			errorImg: {
				type: String,
				default: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAMAAAC3Ycb+AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6ODdDMjhENDYyQUQ2MTFFQTlDQ0VBODgxQjFFOEEyMEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6ODdDMjhENDcyQUQ2MTFFQTlDQ0VBODgxQjFFOEEyMEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4N0MyOEQ0NDJBRDYxMUVBOUNDRUE4ODFCMUU4QTIwQyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4N0MyOEQ0NTJBRDYxMUVBOUNDRUE4ODFCMUU4QTIwQyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PhLwhikAAAAzUExURZWVldfX162t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'
			},
			// 图片进入可见区域前多少像素时，单位rpx，开始加载图片
			// 负数为图片超出屏幕底部多少距离后触发懒加载，正数为图片顶部距离屏幕底部多少距离时触发(图片还没出现在屏幕上)
			threshold: {
				type: [Number, String],
				default: 100
			},
			// 淡入淡出动画的过渡时间
			duration: {
				type: [Number, String],
				default: 500
			},
			// 渡效果的速度曲线，各个之间差别不大，因为这是淡入淡出，且时间很短，不是那些变形或者移动的情况，会明显
			// linear|ease|ease-in|ease-out|ease-in-out|cubic-bezier(n,n,n,n);
			effect: {
				type: String,
				default: 'ease-in-out'
			},
			// 是否使用过渡效果
			isEffect: {
				type: Boolean,
				default: true
			},
			// 圆角值
			borderRadius: {
				type: [Number, String],
				default: 0
			},
			// 图片高度，单位rpx
			height: {
				type: [Number, String],
				default: '450'
			}
		},
		data() {
			return {
				isShow: false,
				opacity: 1,
				time: this.duration,
				loadStatus: '', // 默认是懒加载中的状态
				isError: false, // 图片加载失败
				elIndex: this.$u.guid()
			}
		},
		computed: {
			// 将threshold从rpx转为px
			getThreshold() {
				// 先取绝对值，因为threshold可能是负数，最后根据this.threshold是正数或者负数，重新还原
				let thresholdPx = uni.upx2px(Math.abs(this.threshold));
				console.log(this.threshold < 0 ? -thresholdPx : thresholdPx);
				return this.threshold < 0 ? -thresholdPx : thresholdPx;
			},
			// 计算图片的高度，可能为auto，带%，或者直接数值
			imgHeight() {
				return this.$u.addUnit(this.height);
			}
		},
		created() {
			// 由于一些特殊原因，不能将此变量放到data中定义
			this.observer = {};
		},
		watch: {
			isShow(nVal) {
				// 如果是不开启过渡效果，直接返回
				if (!this.isEffect) return;
				this.time = 0;
				// 原来opacity为1(不透明，是为了显示占位图)，改成0(透明，意味着该元素显示的是背景颜色，默认的白色)，再改成1，是为了获得过渡效果
				this.opacity = 0;
				// 延时30ms，否则在浏览器H5，过渡效果无效
				setTimeout(() => {
					this.time = this.duration;
					this.opacity = 1;
				}, 30)
			},
			// 图片路径发生变化时，需要重新标记一些变量，否则会一直卡在某一个状态，比如isError
			image(n) {
				if(!n) {
					// 如果传入null或者''，或者undefined，标记为错误状态
					this.isError = true;
				} else {
					this.init();
					this.isError = false;
				}
			}
		},
		methods: {
			// 用于重新初始化
			init() {
				this.isError = false;
				this.loadStatus = '';
			},
			// 点击图片触发的事件,loadlazy-还是懒加载中状态，loading-图片正在加载，loaded-图片加加载完成
			clickImg() {
				let whichImg = '';
				// 如果isShow为false，意味着图片还没开始加载，点击的只能是最开始的占位图
				if (this.isShow == false) whichImg = 'lazyImg';
				// 如果isError为true，意味着图片加载失败，这是只剩下错误的占位图，所以点击的只能是错误占位图
				// 当然，也可以给错误的占位图元素绑定点击事件，看你喜欢~
				else if (this.isError == true) whichImg = 'errorImg';
				// 总共三张图片，除了两个占位图，剩下的只能是正常的那张图片了
				else whichImg = 'realImg';
				// 只通知当前图片的index
				this.$emit('click', this.index);
			},
			// 图片加载完成事件，可能是加载占位图时触发，也可能是加载真正的图片完成时触发，通过isShow区分
			imgLoaded() {
				// 占位图加载完成
				if (this.loadStatus == '') {
					this.loadStatus = 'lazyed';
				}
				// 真正的图片加载完成 
				else if (this.loadStatus == 'lazyed') {
					this.loadStatus = 'loaded';
					this.$emit('load', this.index);
				}
			},
			// 错误的图片加载完成
			errorImgLoaded() {
				this.$emit('error', this.index);
			},
			// 图片加载失败
			loadError() {
				this.isError = true;
			},
			disconnectObserver(observerName) {
				const observer = this[observerName];
				observer && observer.disconnect();
			},
		},
    // #ifndef VUE3
    // 组件销毁前，将实例从u-form的缓存中移除
    beforeDestroy() {
      // 销毁页面时，可能还没触发某张很底部的懒加载图片，所以把这个事件给去掉
      //observer.disconnect();
    },
    // #endif
    
    // #ifdef VUE3
    beforeUnmount() {
      
    },
    // #endif
		mounted() {
			
			// mounted的时候，不一定挂载了这个元素，延时30ms，否则会报错或者不报错，但是也没有效果
			setTimeout(() => {
				// 这里是组件内获取布局状态，不能用uni.createIntersectionObserver，而必须用this.createIntersectionObserver
				this.disconnectObserver('contentObserver');
				const contentObserver = uni.createIntersectionObserver(this);
				// 要理解这里怎么计算的，请看这个：
				// https://blog.csdn.net/qq_25324335/article/details/83687695
				contentObserver.relativeToViewport({
					bottom: 300,
				}).observe('.u-lazy-item-' + this.elIndex, (res) => {
					if (res.intersectionRatio > 0) {
						// 懒加载状态改变
						this.isShow = true;
						// 如果图片已经加载，去掉监听，减少性能的消耗
						this.disconnectObserver('contentObserver');
					}
				})
				this.contentObserver = contentObserver;
			}, 30)
		}
	}
</script>

<style scoped lang="scss">
	@import "../../libs/css/style.components.scss";
	
	.u-wrap {
		background-color: #eee;
		overflow: hidden;
	}

	.u-lazy-item {
		width: 100%;
		// 骗系统开启硬件加速
		transform: transition3d(0, 0, 0);
		// 防止图片加载“闪一下”
		will-change: transform;
		/* #ifndef APP-NVUE */
		display: block;
		/* #endif */
	}
</style>
