<script lang="ts" setup>
import { defineProps, PropType, onMounted, ref } from 'vue'

import { getIntraCityInfo } from '../apis'

interface PropertiesType {
  disable: boolean
}

const disabled = ref<boolean>(false)

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})

onMounted(() => {
  getIntraCityInfo().then((res) => {
    disabled.value = !res.data?.icStatus || props.properties.disable
  })
})
</script>
<template>
  <el-checkbox :disabled="disabled" value="INTRA_CITY_DISTRIBUTION">同城配送</el-checkbox>
</template>
