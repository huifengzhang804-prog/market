<script lang="ts" setup>
import type { PropType } from 'vue'
import type { SupplierListInterface } from '../../types/supplier'

const $props = defineProps({
  info: {
    type: Object as PropType<SupplierListInterface>,
    default() {
      return {}
    },
  },
})
</script>

<template>
  <div class="commodity">
    <div class="commodity__left">
      <el-image style="width: 68px; height: 68px" :src="$props.info.albumPics?.split(',')?.shift()" fit="fill"></el-image>
    </div>
    <div class="commodity__right">
      <el-tooltip v-if="$props.info.productName.length >= 30" class="box-item" effect="dark" :content="$props.info.productName" placement="bottom">
        <div class="commodity__right--name" :title="$props.info.productName">
          <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
          {{ $props.info.productName }}
        </div>
      </el-tooltip>
      <div v-else class="commodity__right--name" :title="$props.info.productName">
        <span v-if="$props.info?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
        {{ $props.info.productName }}
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@include b(commodity) {
  @include flex();
  font-size: 12px;
  text-align: left;
  justify-content: flex-start;
  @include e(left) {
    width: 68px;
    height: 68px;
    margin-right: 10px;
  }
  @include e(right) {
    @include m(name) {
      @include utils-ellipsis(2);
    }
    @include m(price) {
      color: #ff7417;
      margin: 4px 0;
    }
    @include m(sup) {
      width: 120px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
}
</style>
