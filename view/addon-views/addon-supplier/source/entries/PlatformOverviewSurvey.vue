<script lang="ts" setup>
defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
</script>

<template>
  <div class="survey__item">
    <div>今日新增供应商</div>
    <div class="survey__item--num">{{ properties.newSupplierNumber }}</div>
  </div>
</template>

<style scoped lang="scss">
@include b(survey) {
  height: 422px;
  width: 421px;
  padding-left: 20px;
  padding-top: 20px;
  background: #fff;
  margin-bottom: 10px;

  @include e(content) {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
  }

  @include e(item) {
    margin-top: 30px;
    padding: 16px;
    font-size: 16px;
    height: 130px;
    color: #666;
    width: 178px;
    margin-right: 20px;
    background: linear-gradient(to bottom right, #f2f2ff, #fff);

    @include m(num) {
      font-size: 24px;
      font-weight: 400;
      margin-top: 15px;
      color: #000;
    }
  }
}
</style>
