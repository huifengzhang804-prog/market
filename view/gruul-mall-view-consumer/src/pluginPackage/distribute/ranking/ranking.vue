<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { doGetdisRank } from '../apis'
import { useUserStore } from '@/store/modules/user'

const { divTenThousand } = useConvert()
const $useUserStore = useUserStore().getterUserInfo
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const disranklist = ref<any>([])
const myrank = ref(0)
const bonus = ref(0)

const loadMore = () => {
  pageConfig.current++
  initRankList()
}
initRankList()

async function initRankList() {
  const { code, data, msg } = await doGetdisRank({ ...pageConfig })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取分销排行失败'}`, icon: 'none' })
  }
  disranklist.value = [...disranklist.value, ...data.records]
  myrank.value = data.rank.rank
  bonus.value = data.rank.total
}
const height = computed(() => {
  let contentHeight = useScreenHeight().value - useBottomSafe().value
  return contentHeight
})
</script>

<template>
  <scroll-view scroll-y :style="{ height: `${height}px` }" @scrolltolower="loadMore">
    <view class="head">
      <image :src="$useUserStore.info.avatar" class="head__pic"></image>
      <view class="head__info" style="flex: 1">
        <view style="display: flex; align-items: center">
          <p style="font-size: 53rpx; font-weight: 700; margin-bottom: 10rpx">
            {{ divTenThousand(bonus) }} <span style="font-size: 26rpx; font-weight: 600; display: block; text-align: center">累计佣金</span>
          </p>
        </view>
      </view>
      <view class="head__info"
        ><p style="font-size: 53rpx; font-weight: 700; text-align: center; margin-bottom: 10rpx">{{ myrank }}</p>
        <p style="font-weight: 700; font-size: 26rpx">我的排名</p>
      </view>
      <view class="head__trophy">
        <!-- <q-icon v-if="myrank === 1" color="FFFFFF4F" name="icon-a-Icon_Medal1_Gold" size="80rpx" />
            <q-icon v-if="myrank === 2" color="#D1D1D6" name="icon-jiangbei-" size="80rpx" />
            <q-icon v-if="myrank === 3" color="#C5723B" name="icon-jiangbei-" size="80rpx" /> -->
        <img
          v-if="myrank === 1"
          style="width: 66rpx; height: 66rpx"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230720/b1405524ff7b44c7a836f4438f596bca.png"
        />
        <img
          v-if="myrank === 2"
          style="width: 66rpx; height: 66rpx"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/official-website/20230720/fce10d345fa74872861c276ce4055753.png"
        />
        <img
          v-if="myrank === 3"
          style="width: 66rpx; height: 66rpx"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/official-website/20230720/533f24926ff14c8d927ce274e77e2789.png"
        />
      </view>
    </view>
    <view class="table">
      <view class="table__title"> <span>名次</span><span>分销员</span><span>累计佣金 </span> </view>
      <view class="table__content">
        <view v-for="(item, index) in disranklist" :key="item.userId" class="table__content-item">
          <view class="table__content-num">
            <img
              v-if="index === 0"
              style="width: 66rpx; height: 66rpx"
              src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230720/b1405524ff7b44c7a836f4438f596bca.png"
            />
            <img
              v-else-if="index === 1"
              style="width: 66rpx; height: 66rpx"
              src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/official-website/20230720/fce10d345fa74872861c276ce4055753.png"
            />
            <img
              v-else-if="index === 2"
              style="width: 66rpx; height: 66rpx"
              src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/official-website/20230720/533f24926ff14c8d927ce274e77e2789.png"
            />
            <!-- <q-icon v-if="index === 0" color="FFFFFF4F" name="icon-a-Icon_Medal1_Gold" size="50rpx" /> -->
            <!-- <q-icon v-else-if="index === 1" name="icon-a-Icon_Medal1_Silver" size="50rpx" />
                    <q-icon v-else-if="index === 2" color="" name="icon-a-Icon_Medal1_Bronze" size="50rpx" /> -->
            <view v-else>{{ index + 1 }}</view>
          </view>
          <view class="table__content-name">
            <image :src="item.avatar" class="table__content-name--pic"> </image>
            <view class="table__content-name--word">{{ item.name }} </view>
          </view>
          <view class="table__content-money">{{ divTenThousand(item.total) }} </view>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
#app {
  background-color: #f2f2f2;
}
@include b(head) {
  font-size: 24rpx;
  display: flex;
  justify-content: space-between;
  background: #fd5e37;
  height: 167rpx;
  padding: 30rpx 56rpx 30rpx 30rpx;
  @include e(pic) {
    border-radius: 50%;
    width: 90rpx;
    height: 90rpx;
    margin-right: 20rpx;
  }
  @include e(info) {
    color: #fff;
  }
  @include e(trophy) {
    color: #fd5e37 !important;
  }
}
@include b(table) {
  color: #333;
  @include e(title) {
    display: flex;
    justify-content: space-around;
    align-items: center;
    height: 96rpx;
    font-size: 28rpx;
    font-weight: 400;
    background-color: #fff;
    border-bottom: 1px solid rgb(231, 231, 231);
  }
  @include e(content-item) {
    display: flex;
    align-items: center;
    height: 112rpx;
    margin-bottom: 2rpx;
    background-color: #fff;
  }
  @include e(content-num) {
    width: 66rpx;
    height: 66rpx;
    text-align: center;
    width: 204rpx;
    // color: ;
  }
  @include e(content-name) {
    padding: 6rpx 0 7rpx 26rpx;
    display: flex;
    align-items: center;
    width: 274rpx;
    @include m(pic) {
      margin-right: 5rpx;
      width: 90rpx;
      height: 90rpx;
      border-radius: 50%;
    }
    @include m(word) {
      font-size: 28rpx;
      font-weight: 400;
      @include utils-ellipsis(1);
    }
  }
  @include e(content-money) {
    width: 256rpx;
    text-align: center;
    font-size: 28rpx;
    font-weight: 400;
  }
}
</style>
