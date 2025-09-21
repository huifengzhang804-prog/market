<template>
  <div>
    <Specials v-if="props?.propertiesLists?.formData?.style === 'row'" :properties-lists="props?.propertiesLists" :shop-val="props?.shopVal" />
    <GoodGoods v-if="props?.propertiesLists?.formData?.style === 'column'" :properties-lists="props?.propertiesLists" :shop-val="props?.shopVal" />
  </div>
</template>

<script setup lang="ts">
import Specials from './Specials.vue'
import GoodGoods from './GoodGoods.vue'

const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
  shopVal: {
    type: Boolean,
    default: false,
  },
})
</script>

<style scoped></style>
