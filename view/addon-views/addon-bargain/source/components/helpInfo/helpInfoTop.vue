<script setup lang="ts">
import { PropType } from 'vue'
import { bargainStatusCn } from '../../apis'
import type { BargainOrderList } from '../../index'
import useConvert from '@/composables/useConvert'
import { useRouter } from 'vue-router'

const $router = useRouter()
defineProps({
  bargainOrderDetail: {
    type: Object as PropType<BargainOrderList>,
    required: true,
  },
})
const { divTenThousand } = useConvert()

const handleNavToDetails = (orderNo: string) => {
  if (orderNo) {
    $router.push({ name: 'detailsIndex', query: { orderNo } })
  }
}
</script>

<template>
  <div class="bangain_info_box">
    <div class="bangain_info_box__item bargain_sponsor">
      开团发起人：
      <div class="bargain_sponsor__info">
        <el-image style="width: 40px; height: 40px" fit="" :src="bargainOrderDetail?.userHeadPortrait" />
        <div class="bargain_origin--name" style="margin-left: 10px; font-size: 14px; color: #515151">
          {{ bargainOrderDetail.userNickname }}
        </div>
      </div>
    </div>
    <p class="bangain_info_box__item">活动名称：{{ bargainOrderDetail.activityName }}</p>
    <p class="bangain_info_box__item">
      发起时间：<time>{{ bargainOrderDetail.publishBargainingTime }}</time>
    </p>
    <p class="bangain_info_box__item">砍价人数：{{ bargainOrderDetail.bargainingPeople }}</p>
    <div class="bangain_info_box__item bargain_sponsor">
      砍价商品：
      <div class="bargain_sponsor__info">
        <el-image style="width: 40px; height: 40px" fit="" :src="bargainOrderDetail.productPic" />
        <div class="bargain_origin--name" style="margin-left: 10px; font-size: 14px; color: #515151">
          {{ bargainOrderDetail.productName }}
        </div>
      </div>
    </div>
    <p class="bangain_info_box__item">底价：{{ bargainOrderDetail.floorPrice && divTenThousand(bargainOrderDetail.floorPrice) }}</p>
    <p class="bangain_info_box__item">
      结束时间：<time>{{ bargainOrderDetail.endTime }}</time>
    </p>
    <p class="bangain_info_box__item">砍价状态：{{ bargainStatusCn[bargainOrderDetail.bargainStatus] }}</p>
    <p v-if="bargainOrderDetail.orderNo" class="bangain_info_box__item">
      订单信息：<el-link :underline="false" type="primary" @click="handleNavToDetails(bargainOrderDetail.orderNo)">查看详情</el-link>
    </p>
  </div>
</template>

<style scoped lang="scss">
@include b(bangain_info_box) {
  width: 956px;
  border: 1px solid #d5d5d5;
  padding: 25px 20px 0 20px;
  margin-bottom: 10px;
  @include e(item) {
    margin-bottom: 25px;
    font-size: 14px;
    color: #333333;
    width: 100%;
  }
}
@include b(bargain_sponsor) {
  @include flex;
  justify-content: flex-start;
  @include e(info) {
    @include flex;
    justify-content: flex-start;
  }
}
</style>
