<template>
  <div class="info">
    <img class="info__img" :src="ExampleImg" />
    <div class="info__edit">
      <q-edit v-model:content="submitForm.detail" height="711px" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { inject } from 'vue'
// @ts-ignore-next-line
import ExampleImg from '@/assets/image/ex_img.png'
import QEdit from '@/components/q-edit/q-edit.vue'

const $parent = inject('form') as any
const submitForm = $parent.submitForm
</script>

<style scoped lang="scss">
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
