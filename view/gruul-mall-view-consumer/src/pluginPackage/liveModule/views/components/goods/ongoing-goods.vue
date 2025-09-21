<script setup lang="ts"></script>
<template>
  <view class="chat_room_goods">
    <view class="chat_room_goods_img">
      <image
        style="border-radius: 20rpx"
        src="https://pic.rmb.bdstatic.com/d4bc322f2402946746d27d96f00c9a43.jpeg@c_1,w_1200,h_768,x_0,y_0"
        mode="aspectFill"
      />
      <view class="angle">
        <text class="angle_text">1</text>
      </view>
    </view>
    <text class="chat_room_goods_name"> 迪奥梅红极致奢华阿道的的的订单打发夫的订单打发夫</text>
    <view class="chat_room_goods_footer">
      <view class="chat_room_goods_footer_left">
        <text class="chat_room_goods_footer_rmb">￥</text>
        <text class="chat_room_goods_footer--price">19.9</text>
      </view>
      <view class="chat_room_goods_footer_right">
        <text class="chat_room_goods_footer_right--text">抢</text>
      </view>
    </view>
  </view>
</template>

<style scoped>
.chat_room_goods {
  height: 350rpx;
  width: 270rpx;
  background: #ffffff;
  border-radius: 20rpx;
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  margin-left: 20rpx;
}
.chat_room_goods_img {
  position: relative;
  height: 200rpx;
}
.chat_room_goods_name {
  lines: 2;
  text-overflow: ellipsis;
}
.angle {
  position: absolute;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 80rpx;
  height: 50rpx;
  background: rgba(4, 4, 4, 0.5);
  border-radius: 40rpx 0 100rpx;
}
.angle_text {
  font-size: 24rpx;
  color: #fff;
}
.chat_room_goods_footer {
  position: relative;
  width: 242rpx;
  /* 主色 */
  background: #fa3534;
  margin-bottom: 10rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: row;
  border-radius: 5rpx;
}
.chat_room_goods_footer_left {
  display: flex;
  justify-content: flex-start;
  align-items: flex-end;
  flex-direction: row;
  padding-left: 10rpx;
}
.chat_room_goods_footer_rmb {
  color: #fff;
  font-size: 24rpx;
}
.chat_room_goods_footer--price {
  font-weight: 700;
  font-size: 30rpx;
  color: #ffffff;
}
.chat_room_goods_footer_right {
  opacity: 0.8;
  display: flex;
  justify-content: center;
  align-content: center;
  width: 119rpx;
  height: 119rpx;
  background: #d9d9d9;
  border-radius: 50%;
  position: absolute;
  right: -40rpx;
  top: 25rpx;
  transform: translateY(-32rpx);
}
.chat_room_goods_footer_right--text {
  color: #fff;
  font-size: 35rpx;
  padding-left: 25rpx;
}
</style>
