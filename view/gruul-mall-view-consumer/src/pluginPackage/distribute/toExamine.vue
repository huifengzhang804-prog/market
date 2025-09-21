<script setup lang="ts">
import { doDistribution } from '@/apis/shops'
import { useSettingStore } from '@/store/modules/setting'
import { ref } from 'vue'

const underReview = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967565015e4b0dd23f6b86f33.png'
const auditFailed = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564fd1e4b0dd23f6b86f32.png'
const approvedByReview = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564f50e4b0dd23f6b86f31.png'

enum status {
  /**
   * 未申请
   */ NOT_APPLIED = '未申请',
  /**
   * 已拒绝
   */
  CLOSED = '已拒绝',
  /**
   * 审核中
   */
  APPLYING = '审核中',
  /**
   * 已通过
   */
  SUCCESS = '已通过',
}

const supplier = ref({
  /**
   * 申请状态
   * 未申请|申请中|申请关闭|申请通过
   */
  status: '',
  /**
   * 分销码
   */
  distributeCode: '',
  /**
   * 姓名
   */
  name: '',
  /**
   * 手机号
   */
  mobile: '',
  /**
   * 申请时间
   */
  applyTime: '',
  /**
   * 审核时间  通过/拒绝时显示这个字段
   */
  auditTime: '',
  /**
   * 拒绝原因
   */
  rejectReason: '',
})

const getShopInfo = async () => {
  const { code, data } = await doDistribution()
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
    url: `/pluginPackage/distribute/DistributorBackground`,
  })
}
const toDistributionBtn = () => {
  uni.redirectTo({
    url: `/pluginPackage/distribute/DistributorBackground`,
  })
}
</script>

<template>
  <view>
    <view class="info">
      <view class="info_img">
        <img
          :src="supplier.status === 'APPLYING' ? underReview : supplier.status === 'SUCCESS' ? approvedByReview : auditFailed"
          alt=""
          style="width: 300rpx; height: 170rpx"
        />
        <view class="info_con">
          <view
            :style="{
              fontSize: '18px',
              fontWeight: 'bold',
              color: supplier.status === 'APPLYING' ? '#FD9224' : supplier.status === 'SUCCESS' ? '#00BB2C' : '#F54319',
            }"
            >{{ status[supplier.status as keyof typeof status] }}</view
          >
          <view v-if="supplier.status === 'UNDER_REVIEW'" style="color: #999; margin-top: 15px; font-size: 16px">申请已提交成功，请耐心等待~</view>
        </view>
      </view>
    </view>
    <view class="content">
      <view class="content_info">
        <view>姓名：{{ supplier.name }}</view>
        <view>手机号：{{ supplier.mobile }}</view>
        <view>申请时间：{{ supplier.applyTime }}</view>
        <view v-if="supplier.status !== 'APPLYING' && supplier.auditTime">审核时间：{{ supplier.auditTime }}</view>
        <view v-if="supplier.status === 'CLOSED' && supplier?.rejectReason">拒绝原因：{{ supplier?.rejectReason }}</view>
      </view>
    </view>
    <view class="footer">
      <view v-if="supplier.status === 'CLOSED'" class="again__button" @click="againBtn">再次申请</view>
      <u-button v-if="supplier.status === 'SUCCESS'" type="error" @click="toDistributionBtn">进入分销中心</u-button>
      <u-button v-else type="error" @click="submit">去商场逛逛</u-button>
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
