<script setup lang="ts">
import { doGetshops } from '@/apis/goods'
import QIcon from '@/components/q-icon/q-icon.vue'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { ElMessage } from 'element-plus'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/store/modules/user'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import { useRouterNewWindow } from '@/utils/useRouter'

const $router = useRouter()
const { openNewWindow } = useRouterNewWindow()
const props = defineProps({
  propertiesLists: {
    type: Object as any,
    default: () => {},
  },
})

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

// 进入店铺
const enterTheShop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId: shopId },
  })
}

/**
 * @description: 客服
 */
const gotoCustomerService = async (shopId: string) => {
  await doGetMessagesChatRoom(shopId, useUserStore().getterUserInfo.userId)
  openNewWindow('/personalcenter/set/customerservice', { shopId: shopId })
}

const shopList = ref<any>()
/**处理装修店铺数据 取ID 调接口过滤 取出未禁用的店铺 */
const doGetshopsInt = async () => {
  let shopIds = props?.propertiesLists?.formData?.list?.map((item: { id: string }) => item?.id) || []
  if (shopIds.length <= 0) return
  const { data, code, msg } = await doGetshops({ productIsNotEmpty: true, shopIds, scored: true })
  if (code !== 200) return ElMessage.error(msg || '获取商品失败')
  shopList.value = data?.records
}
doGetshopsInt()
</script>

<template>
  <div v-if="props?.propertiesLists?.show" class="con">
    <div class="recommendTltle">
      <div class="recommendTltle__Title">
        <span class="recommendTltle__Title--line" />
        <span class="recommendTltle__Title--circle" />
        <span class="recommendTltle__Title--tle">{{ props?.propertiesLists?.formData?.mainTitle }}</span>
        <span class="recommendTltle__Title--circle" />
        <span class="recommendTltle__Title--line" />
      </div>
      <div class="recommendTltle__title">
        {{ props?.propertiesLists?.formData?.subtitle }}
      </div>
    </div>

    <div class="shopBox" :style="shopList?.length <= 3 ? 'max-height: 150px' : ''">
      <div v-for="(item, index) in shopList" :key="index" class="shopBox__shops">
        <div class="shopBox__shops--imgBox">
          <img :src="item?.logo" />
        </div>
        <div class="shopBox__shops--right">
          <div class="shopBox__shops--title">
            <div class="shopBox__shops--tagBox">
              <span v-if="item?.shopType === 'SELF_OWNED'" class="shopBox__shops--selfSupport">自营</span>
              <span v-else-if="item?.shopType === 'PREFERRED'" class="shopBox__shops--optimization">优选</span>
            </div>
            <span>{{ item?.name }}</span>
            <span v-if="getterPropertiesList?.otherData?.service" cursor-pointer @click.stop="gotoCustomerService(item?.id)">
              <QIcon name="icon-lianxikefu" color="rgb(245, 67, 25)" style="margin-left: 5px" />
            </span>
          </div>
          <div class="shopBox__shops--copywriting">
            <QIcon name="icon-gonggao" size="14px" color="#F54319" />
            <div>
              {{ item?.newTips || '暂无公告' }}
            </div>
          </div>
          <div class="shopBox__shops--infoBox">
            <div class="shopBox__shops--rate">
              <el-rate v-model="item.score" disabled show-score text-color="#ff9900" score-template="{value}" />
            </div>
            <span @click="enterTheShop(item?.id)">
              <QIcon name="icon-dianpu4" size="12px" color="#F54319" style="margin-right: 2px" />
              进入店铺
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
@include b(con) {
  width: 1200px;
  margin: 0 auto;
  position: relative;
}
@include b(recommendTltle) {
  height: 60px;
  margin: 24px auto 16px;
  @include e(Title) {
    @include m(line) {
      display: inline-block;
      width: 36px;
      border-bottom: 2px solid rgb(245, 67, 25);
      transform: translateY(-8px);
    }
    @include m(circle) {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: #f54319;
      transform: translateY(-3.5px);
    }
    @include m(tle) {
      display: inline-block;
      height: 33px;
      font-size: 24px;
      line-height: 33px;
      font-weight: 500;
      color: rgb(51, 51, 51);
      margin: 0 24px 6px;
    }
  }
  @include e(title) {
    font-size: 14px;
    line-height: 20px;
    color: #8c8c8c;
  }
}
@include b(shopBox) {
  display: flex;
  flex-wrap: wrap;
  max-height: 324px;
  overflow: hidden;
  @include e(shops) {
    width: 384px;
    height: 150px;
    background-color: #fff;
    display: flex;
    margin: 0 24px 24px 0;
    @include m(imgBox) {
      width: 96px;
      height: 96px;
      border-radius: 4px;
      overflow: hidden;
      margin: 24px 16px 30px 16px;
      flex-shrink: 0;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(right) {
      padding-right: 16px;
      flex: 1;
    }
    @include m(title) {
      height: 17px;
      font-size: 12px;
      font-weight: bold;
      color: #000;
      overflow: hidden;
      margin-top: 24px;
      display: flex;
    }
    @include m(rate) {
      padding-top: 2px;
    }
    @include m(tagBox) {
      text-align: left;
      flex-shrink: 0;
      span {
        display: inline-block;
        font-size: 10px;
        zoom: 0.9;
        border-radius: 3px;
        overflow: hidden;
        margin-right: 5px;
        padding: 2px 4px;
        color: #fff;
      }
    }
    @include m(selfSupport) {
      background-color: #fb232f;
    }
    @include m(optimization) {
      background-color: #8239f6;
    }
    @include m(copywriting) {
      display: flex;
      margin-top: 8px;
      img {
        flex-shrink: 0;
        width: 12px;
        height: 12px;
        transform: translateY(2px);
      }
      div {
        height: 34px;
        font-size: 12px;
        line-height: 17px;
        color: #999;
        text-align: left;
        padding: 0 8px 0 6px;
        overflow: hidden;
        position: relative;
        top: -1px;
      }
    }
    @include m(infoBox) {
      display: flex;
      justify-content: space-between;
      margin-top: 29px;
      padding-right: 8px;
      height: 28px;
      span {
        height: 28px;
        width: 82px;
        display: inline-block;
        color: #f54319;
        border: 1px solid rgb(245, 67, 25);
        border-radius: 2px;
        background-color: rgba(245, 67, 25, 0.1);
        font-size: 12px;
        padding: 6px 7px;
        cursor: pointer;
        line-height: 16px;
      }
    }
    @include m(attention) {
      border: 1px solid #999 !important;
      background-color: #fff !important;
      color: #999;
    }
    @include m(followed) {
      color: #f54319;
      border: 1px solid #f54319 !important;
      background-color: #fff !important;
    }
    &:nth-of-type(3n) {
      margin-right: 0;
    }
  }
}
:deep(.el-rate) {
  .el-rate__icon.is-active {
    color: #fd9224 !important;
    font-size: 21px;
  }
  .el-rate__decimal {
    color: #fd9224 !important;
  }
  .el-rate__icon {
    font-size: 21px;
  }
  .el-rate__icon {
    font-size: 21px;
  }
  .el-rate__item {
    width: 21px;
  }
  .el-rate__text {
    position: relative;
    left: 8px;
  }
  .el-pagination__total {
    color: #000;
  }
  &.is-disabled .el-rate__item {
    color: #dedede !important;
  }
}
</style>
