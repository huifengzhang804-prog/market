<template>
    <div class="info">
        <img class="info__img" :src="exampleImg" />
        <div class="info__edit">
            <q-edit v-model:content="submitForm.detail" height="711px" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { inject } from 'vue'
import '@wangeditor/editor/dist/css/style.css'
import exampleImg from '@/assets/images/ex_img.png'
import QEdit from '@/components/q-edit/q-edit.vue'
import { FormInject } from '../types'
import { defineOptions } from 'vue'

defineOptions({
    name: 'NewProductInfo',
})
const $emit = defineEmits(['changeInstance'])
const instance = getCurrentInstance()

const $parent = inject('form') as FormInject
const submitForm = $parent.submitForm

onActivated(() => {
    $emit('changeInstance', instance?.refs)
})
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
