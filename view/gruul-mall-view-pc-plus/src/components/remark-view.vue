<script lang="ts" setup>
import type { PropType } from 'vue'
import type { RemarkItem } from '@/views/personalcenter/order/myorder/types'

defineProps({
  remark: { type: Object as PropType<Array<RemarkItem>>, required: true },
})
</script>

<template>
  <div>
    <div v-for="{ key, value, type } in remark" :key="key" class="remark-div">
      <text class="remark-div--key">{{ key }}：</text>
      <div v-if="type === 'IMAGE'" class="remark-div--image">
        <el-image :preview-src-list="[value]" :src="value" fit="cover" style="width: 40px; height: 40px" />
      </div>
      <text v-else class="remark-div--remarkKey"> {{ value }}</text>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.remark-div {
  background-color: transparent;
  padding-left: 37px;
  margin-top: 10px;
}
</style>
