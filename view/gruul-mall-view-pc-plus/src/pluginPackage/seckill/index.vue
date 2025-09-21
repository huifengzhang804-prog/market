<script lang="ts" setup>
import PcSeckillList from '@/q-plugin/seckill/PcSeckillList.vue'
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

propertiesList.value = getterPropertiesList.value
</script>

<template>
  <PcSeckillList :other-data="propertiesList?.otherData" />
</template>
