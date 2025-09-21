<script lang="ts" setup>
import { inject } from 'vue'
import QEdit from '@/components/q-edit/q-edit.vue'
import { defineOptions } from 'vue'
// @ts-ignore
import exampleImg from '@/assets/images/ex_img.png'

defineOptions({
  name: 'NewProductInfoAddon',
})

const $parent: any = inject('form')
const submitForm = $parent.submitForm
</script>

<template>
  <div class="info">
    <img :src="exampleImg" class="info__img" />
    <div class="info__edit">
      <q-edit v-model:content="submitForm.detail" height="711px" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(info) {
  display: flex;
  @include e(img) {
  }
  @include e(edit) {
    border: 1px solid #ccc;
    margin-top: 18px;
    z-index: 99;
    ::-webkit-scrollbar {
      display: none;
    }
  }
}
</style>
