<script setup lang="ts">
import { ref, reactive } from 'vue'
import { packageStatusHandle } from '../index'
import { doGetGroupActivity, doGetGroupUser } from '../apis'
import MyTeam from '../components/PcMyTeam.vue'
const props = defineProps({
  properties: { type: Object, default: () => {} },
})

const pageConfig = reactive({
  current: 1,
  size: 10,
})

//  导航我的拼团弹窗
const isShow = ref(false)
const myTeamKey = ref(0)
const teamNo = ref()
const records = ref()
const handleMyGroup = async (id: string) => {
  const res = await doGetGroupActivity(id)
  if (res.code === 200) {
    teamNo.value = res.data
    isShow.value = true
  }
  const { data, code } = await doGetGroupUser(id)
  records.value = data
  myTeamKey.value = Date.now()
}
const isShowFalse = (val: boolean) => {
  isShow.value = val
}
</script>
<template>
  <div class="text" @click="handleMyGroup(props.properties?.order?.extra.teamNo)">我的拼团</div>
  <MyTeam v-if="isShow ? true : false" :key="myTeamKey" :is-show="isShow" :team-no="teamNo" :records="records" @is-show-false="isShowFalse"></MyTeam>
</template>

<style scoped lang="scss">
@include b(text) {
  width: 87px;
  height: 24px;
  line-height: 24px;
  color: #e31436;
  margin: 4px 0 6px 0;
  cursor: pointer;
  border: 1px solid #e31436;
}
</style>
