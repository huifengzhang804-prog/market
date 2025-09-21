<script setup lang="ts">
import { computed, inject, onMounted, onUnmounted, ref, type PropType } from 'vue'
import RemarkImage from '@/pluginPackage/order/confirmOrder/components/order/components/remark-image.vue'
import RemarkDate from '@/pluginPackage/order/confirmOrder/components/order/components/remark-date.vue'
import { REGEX } from '@/constant'
import type { ApishopDealSetting, OrderShopPackage } from '../../types'

const $props = defineProps({
  recordErrGoods: { type: String, default: '' },
  idx: { type: Number, default: 0 },
  shopId: { type: String as PropType<Long>, default: '' },
  shopDealSetting: { type: Object as PropType<ApishopDealSetting>, required: true },
})
const $parent = inject('praentShop') as {
  shopPackages: OrderShopPackage[]
  shopDealSetting: ApishopDealSetting
}
const formRef = ref()
const rulesValidator = computed(() => {
  let rulesObj: { [x: string]: any[] } = {}
  $props.shopDealSetting[$props.shopId]?.forEach((item) => {
    rulesObj[item.key] = []
    if (item.required) {
      rulesObj[item.key].push({
        required: true,
        message: `请${item.type !== 'IMAGE' ? '填写' : '上传'}${item.key}`,
        trigger: ['change', 'blur'],
      })
    }
    console.log('item.type', item.type)
    switch (item.type) {
      case 'TEXT':
        rulesObj[item.key].push({
          pattern: REGEX.BLANK,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'TIME':
        rulesObj[item.key].push({
          pattern: REGEX.TIME,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'DATE':
        rulesObj[item.key].push({
          pattern: REGEX.DATE,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'CITIZEN_ID':
        rulesObj[item.key].push({
          pattern: REGEX.CITIZEN_ID,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'DATETIME':
        rulesObj[item.key].push({
          pattern: REGEX.TIME_DATE,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'IMAGE':
        rulesObj[item.key].push({
          pattern: REGEX.HTTP_URL,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'MOBILE':
        rulesObj[item.key].push({
          pattern: REGEX.MOBILE,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'NUMBER':
        rulesObj[item.key].push({
          pattern: REGEX.NUMBERS,
          message: `${item.key}不正确`,
          trigger: ['change', 'blur'],
        })
        break
      case 'REMARK':
        rulesObj[item.key].push({
          max: 150,
          message: `${item.key}不能超过150字`,
          trigger: ['change', 'blur'],
        })
        break
      default:
        break
    }
  })
  return Object.values(rulesObj).length ? rulesObj : {}
})

onMounted(() => {
  formRef.value?.setRules?.(rulesValidator)
})

uni.$on('validate', async (isVilidate) => {
  if (formRef.value) {
    const res = await formRef.value.validate()
    isVilidate.value = res
  }
})
onUnmounted(() => {
  uni.$off('validate')
})

/**
 * 输入框表单判断 公民身份证 数量
 * @param {string} type
 * @returns {boolean}
 */
function isInputItem(type: string): boolean {
  return ['MOBILE', 'CITIZEN_ID', 'TEXT', 'NUMBER', 'REMARK'].indexOf(type) !== -1
}

/**
 * 时间表单判断
 * @param {string} type
 * @returns {boolean}
 */
function isDateItem(type: string): boolean {
  return ['DATE', 'DATETIME', 'TIME'].indexOf(type) !== -1
}
</script>

<template>
  <u-form
    v-if="$parent.shopPackages[$props.idx]?.remark"
    ref="formRef"
    :error-type="['toast']"
    :model="$parent.shopPackages[$props.idx]?.remark"
    :rules="rulesValidator"
  >
    <u-form-item
      v-for="(item, inde) in $props.shopDealSetting[$props.shopId]"
      :key="item.key"
      :border-bottom="!($props.shopDealSetting[$props.shopId].length - 1 === inde)"
      :label="item.key"
      :prop="item.key"
      :required="item.required"
      label-width="auto"
    >
      <u-input
        v-if="isInputItem(item.type)"
        v-model.trim="$parent.shopPackages[$props.idx].remark[item.key]"
        :maxlength="item.type === 'MOBILE' ? 11 : 140"
        :placeholder="item.placeholder"
        trim
      />
      <RemarkDate
        v-else-if="isDateItem(item.type)"
        v-model="$parent.shopPackages[$props.idx].remark[item.key]"
        :formType="item.type"
        :placeholder="item.placeholder"
      />
      <RemarkImage v-else v-model="$parent.shopPackages[$props.idx].remark[item.key]" :placeholder="item.placeholder" class="form__item" />
    </u-form-item>
  </u-form>
</template>

<style lang="scss" scoped>
@include b(form) {
  font-size: 28rpx;
  color: #000;
  @include e(item) {
    width: 100%;
  }
  @include e(item-right) {
  }
}
</style>
