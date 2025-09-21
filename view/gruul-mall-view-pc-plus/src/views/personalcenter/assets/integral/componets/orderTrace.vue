<script setup lang="ts">
const props = defineProps({
  orderLocation: {
    type: Object,
    default: () => {},
  },
  integralOrder: {
    type: Object,
    default: () => {},
  },
})
const isExpress = computed(() => {
  return props.integralOrder?.expressCompanyName && props.integralOrder?.expressNo
})
</script>
<template>
  <!-- 商品信息 s -->
  <div class="goods-container">
    <div style="padding: 0 20px">商品信息</div>
    <div class="goods-container__image_box">
      <div style="display: flex">
        <img class="image-scroll" :width="100" :height="100" :src="props.integralOrder?.image" />
      </div>
    </div>
  </div>
  <!-- 商品信息 e -->
  <!-- 物流信息 s -->
  <div class="logistics-container">
    <div style="padding: 0 0 0 20px">物流信息</div>
    <div style="padding: 20px 50px">
      <div v-if="!isExpress">无需物流</div>
      <div v-else>
        <div class="logistics-container__label">
          快递公司：<span class="logistics-container__value">
            {{ props?.deliveryPackages?.expressCompanyName }}
          </span>
        </div>
        <div class="logistics-container__label">
          快递单号：
          <div class="logistics-container__no">
            <span class="logistics-container__value">{{ props?.deliveryPackages?.expressNo }}</span>
          </div>
        </div>
        <div class="logistics-container__label">官方电话：0987-923834234</div>
      </div>
    </div>
  </div>
  <!-- 物流信息 e -->
  <div v-if="!isExpress" class="logistics-steps">
    <logistics-steps :context="orderLocation" :loading="loading" />
  </div>
  <!-- 物流步骤条 -->
</template>
<style scoped lang="scss">
@include b(goods-container) {
  text-align: left;
  padding: 20px 0;
  background: #fff;
  @include e(image_box) {
    margin: 20px 52px;
  }
}
@include b(logistics-container) {
  text-align: left;
  @include e(label) {
    margin-bottom: 15px;
    display: flex;
    color: #666;
  }
  @include e(value) {
    color: #333333;
  }
  @include e(no) {
    flex: 1;
    display: flex;
    justify-content: space-between;
    @include m(copy) {
      color: #005cf4;
    }
  }
}
@include b(logistics-steps) {
  margin-top: 20px;
  background: #fff;
}
@include b(logistics) {
  background: #fff;
  overflow-y: scroll;
  padding: 40rpx;
}
@include b(active) {
  color: #fe4e63;
}
@include b(logistics-steps) {
  margin-top: 20rpx;
  background: #fff;
}
</style>
