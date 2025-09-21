<script setup lang="ts">
import IntegralShop from '@/q-plugin/integral/PcIntegralShop.vue'
import Storage from '@/libs/storage'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

import { storeToRefs } from 'pinia'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

const storage = new Storage()
interface ProperType {
  bottomComponents: any
  components: any
  otherData: any
  topComponents: any
}
const propertiesList = ref<ProperType>()

propertiesList.value = getterPropertiesList.value as ProperType
</script>
<template>
  <IntegralShop :other-data="propertiesList?.otherData" />
</template>

<style scoped></style>
