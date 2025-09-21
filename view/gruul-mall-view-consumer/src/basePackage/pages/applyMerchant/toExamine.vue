<script setup lang="ts">
import { doShopInfo } from '@/apis/shops'
import { useSettingStore } from '@/store/modules/setting'
import { ref } from 'vue'

const underReview = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967565015e4b0dd23f6b86f33.png'
const auditFailed = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564fd1e4b0dd23f6b86f32.png'
const approvedByReview = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564f50e4b0dd23f6b86f31.png'

enum status {
  /**
   * 已拒绝
   */
  REJECT = '已拒绝',
  /**
   * 审核中
   */
  UNDER_REVIEW = '审核中',
  /**
   * 已通过
   */
  NORMAL = '已通过',
}

const supplier = ref({
  companyName: '',
  createTime: '',
  name: '',
  status: '',
  extra: {
    auditTime: '',
    reasonForRejection: '',
  },
})

const getShopInfo = async () => {
  const { code, data } = await doShopInfo('COMMON')
  if (code === 200) {
    supplier.value = data
  }
}

getShopInfo()

const $settingStore = useSettingStore()
const submit = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
const againBtn = () => {
  uni.redirectTo({
    url: `/basePackage/pages/applyMerchant/ApplyMerchant`,
  })
}
</script>

<template>
  <view>
    <view class="info">
      <view class="info_img">
        <img
          :src="supplier.status === 'UNDER_REVIEW' ? underReview : supplier.status === 'NORMAL' ? approvedByReview : auditFailed"
          alt=""
          style="width: 300rpx; height: 170rpx"
        />
        <view class="info_con">
          <view
            :style="{
              fontSize: '18px',
              fontWeight: 'bold',
              color: supplier.status === 'UNDER_REVIEW' ? '#FD9224' : supplier.status === 'NORMAL' ? '#00BB2C' : '#F54319',
            }"
            >{{ status[supplier.status as keyof typeof status] }}</view
          >
          <view v-if="supplier.status === 'UNDER_REVIEW'" style="color: #999; margin-top: 15px; font-size: 16px">申请已提交成功，请耐心等待~</view>
        </view>
      </view>
    </view>
    <view class="content">
      <view class="content_info">
        <view>商户名称：{{ supplier.companyName }}</view>
        <view>店铺名称：{{ supplier.name }}</view>
        <view>申请时间：{{ supplier.createTime }}</view>
        <view v-if="supplier.status !== 'UNDER_REVIEW' && supplier?.extra?.auditTime">审核时间：{{ supplier?.extra?.auditTime }}</view>
        <view v-if="supplier.status !== 'UNDER_REVIEW' && supplier?.extra?.reasonForRejection !== 'null' && supplier?.extra?.reasonForRejection"
          >拒绝原因：{{ supplier?.extra?.reasonForRejection }}</view
        >
      </view>
    </view>
    <view class="footer">
      <view v-if="supplier.status !== 'UNDER_REVIEW'" class="again__button" @click="againBtn">再次申请</view>
      <u-button type="error" @click="submit">去商场逛逛</u-button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.info {
  height: 45vh;
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: center;
  .info_img {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    top: 50rpx;
  }
  .info_con {
    margin-top: 35px;
    text-align: center;
  }
}

.content {
  width: 100%;
  display: flex;
  justify-content: center;
  .content_info {
    width: 85%;
    line-height: 28px;
    view {
      flex-wrap: wrap;
    }
  }
}

.footer {
  position: fixed;
  width: 100%;
  text-align: center;
  bottom: 18px;
  left: 0;
  .u-btn {
    width: 93%;
    border-radius: 25px;
    height: 42px;
    background-color: #f54319;
  }
  .again__button {
    line-height: 90px;
    font-size: 14px;
    color: #333;
  }
}
</style>
