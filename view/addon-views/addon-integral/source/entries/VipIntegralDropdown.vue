<script lang="ts" setup>
import { ref, defineProps, PropType } from 'vue'
import IntegralUpdateDialog from '../components/integral-update-dialog.vue'

interface PropertiesType {
  userId: string
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const showDialog = ref(false)
</script>
<template>
  <el-dropdown-item @click.stop="showDialog = true">积分调整</el-dropdown-item>
  <integral-update-dialog :show-dialog="showDialog" :user-id="props.properties.userId" @reset="showDialog = false" />
</template>
