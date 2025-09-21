<script setup lang="ts">
import { ref } from 'vue'
import HelpInfoTop from '../components/helpInfo/helpInfoTop.vue'
import BargainInfoList from '../components/helpInfo/bargainInfoList.vue'
import { doGetBargainOrderDetail } from '../apis'
import { BargainOrderList } from '../index'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const $route = useRoute()
const bargainListRef = ref<InstanceType<typeof BargainInfoList>>()
const bargainOrderDetail = ref<BargainOrderList>({
  activityId: '',
  activityName: '',
  bargainStatus: 'BARGAINING',
  bargainingPeople: 0,
  endTime: '',
  floorPrice: '',
  id: '',
  productId: '',
  productName: '',
  productPic: '',
  publishBargainingTime: '',
  shopId: '',
  skuId: '',
  sponsorId: '',
  userHeadPortrait: '',
  userNickname: '',
  salePrice: '',
  createTime: '',
  updateTime: '',
})
getBargainOrderDetail()
async function getBargainOrderDetail() {
  if ($route.query.id) {
    const { code, data, msg } = await doGetBargainOrderDetail($route.query.id + '')
    if (code !== 200) {
      ElMessage.error(msg || '获取砍价订单详情失败')
      return
    }
    if (!data) return
    bargainOrderDetail.value = data
    getBargainHelpPeople()
  }
}
function getBargainHelpPeople() {
  const { id, activityId } = bargainOrderDetail.value
  bargainListRef.value?.getBargainHelpPeople({ activityId, bargainOrderId: id })
}
</script>

<template>
  <div style="padding: 16px; height: 100%; overflow-y: scroll">
    <help-info-top :bargain-order-detail="bargainOrderDetail" />
    <bargain-info-list ref="bargainListRef" @reload="getBargainHelpPeople" />
  </div>
</template>

<style scoped lang="scss"></style>
