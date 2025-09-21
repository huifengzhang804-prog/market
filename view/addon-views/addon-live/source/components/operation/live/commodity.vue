<script lang="ts" setup></script>

<template>
  <div class="commodity">
    <img src="" />
    <div class="commodity__title">商品名称</div>
    <div class="commodity__price">
      <span class="currency">￥</span>
      <span>5.78</span>
    </div>
    <div class="commodity__btn">
      <el-button type="primary">讲解中</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(commodity) {
  position: absolute;
  bottom: 60px;
  right: 10px;
  width: 100px;
  background-color: rgba(255, 255, 255, 0.7);
  overflow: hidden;
  border-radius: 10px;
  & > img {
    width: 100px;
    height: 100px;
  }
  @include e(title) {
    font-size: 13px;
    margin-top: 12px;
    line-height: 18px;
    padding: 0 5px;
  }
  @include e(price) {
    color: #f00;
    .currency {
      font-size: 16px;
    }
  }
  @include e(btn) {
    text-align: center;
    margin-top: 5px;
    margin-bottom: 10px;
  }
}
</style>
