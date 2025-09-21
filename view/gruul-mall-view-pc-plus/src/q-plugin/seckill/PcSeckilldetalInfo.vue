<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import Decimal from 'decimal.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import DateUtils from '@/utils/date'
import QIcon from '@/components/q-icon/q-icon.vue'
import countdown from '@/pluginPackage/seckill/components/countdown.vue'
import GoodSpec from '@/components/good-spec/good-spec.vue'
import storage from '@/libs/storage'

const prop = defineProps({
  choosedSpec: {
    type: Object,
    default: () => {},
  },
  interestsConfig: {
    type: Object,
    default: () => {},
  },
  secKillGoodsInfo: {
    type: Object,
    default: () => {},
  },
  seckillList: {
    type: Object,
    default: () => {},
  },
  skuGroup: {
    type: Object,
    default: () => {},
  },
  goodInfo: {
    type: Object,
    default: () => {},
  },
  seckillId: {
    type: String,
    default: () => {},
  },
  regionDatas: {
    type: Object,
    default: () => {},
  },
})
</script>

<template>
  <q-plugin
    :context="{
      Request,
      DateUtils,
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      useVModel,
      Decimal,
      ElementPlus: {
        ElMessage,
        ElMessageBox,
      },
      useConversionPrice,
      QIcon,
      countdown,
      GoodSpec,
      storage,
    }"
    hide-on-miss
    :properties="{
      choosedSpec: prop.choosedSpec,
      interestsConfig: prop.interestsConfig,
      secKillGoodsInfo: prop.secKillGoodsInfo,
      seckillList: prop.seckillList,
      skuGroup: prop.skuGroup,
      goodInfo: prop.goodInfo,
      seckillId: prop.seckillId,
      regionDatas: prop.regionDatas,
    }"
    name="PcSeckilldetalInfo"
    service="addon-seckill"
  />
</template>
