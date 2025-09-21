<template>
  <view>
    <u-popup v-model="isOpenCarPopup" mode="bottom" height="1050rpx" :z-index="100">
      <view class="cart-popup">
        <view class="title">
          <view style="display: flex">
            <view :class="{ active: nvalidNum && activeCar }" @click="handleSwitch('购物车')">购物车({{ validNum }})</view>
            <!-- 失效商品只显示失效商品字样，不需要显示具体的商品数 -->
            <view v-if="nvalidNum" :class="{ active: !activeCar }" style="margin-left: 40rpx" @click="handleSwitch('失效商品')">失效商品</view>
          </view>
          <view style="color: #fa3534; font-weight: normal; font-size: 24rpx" @click="dleModalController = true">清空</view>
        </view>
        <div class="cart-popup__list">
          <view v-for="item in currentList" :key="item.uniqueId">
            <u-swipe-action
              :key="item.uniqueId"
              :index="item.uniqueId"
              :swipe-action-width="swipeActionWidth"
              :options="[
                {
                  text: '删除',
                  style: {
                    backgroundColor: '#FA3534',
                    fontSize: '28rpx',
                    alignItems: 'center',
                  },
                },
              ]"
              :btn-width="208"
              :show="item.show"
              @click="handleSwipeAction(item)"
              @open="handleOpen"
            >
              <view class="list">
                <view :class="{ unActiveCar: !activeCar }" style="border-radius: 14rpx; overflow: hidden">
                  <u-image :src="item.image" :width="187" :height="187" />
                </view>
                <view style="margin-left: 19rpx">
                  <view class="list__name">{{ item.productName }}</view>
                  <view class="list__specs">
                    {{ handleSpecs(item.productAttributes, item.specs || []) }}
                  </view>
                  <view class="list__bottom">
                    <view class="list__bottom--price" :style="{ color: activeCar ? '#fa3534' : '#999', fontWeight: activeCar ? 'bold' : 'normal' }"
                      >￥{{ divTenThousand(item.finalPrice) }}
                    </view>
                    <view v-if="activeCar" class="list__bottom--stepper">
                      <count-number
                        :model-value="item.num"
                        :height="55"
                        :rule="{
                          limitNum: item.skuStock.limitNum,
                          limitType: item.skuStock.limitType,
                          stockType: item.skuStock.stockType,
                          stock: item.skuStock.stock,
                        }"
                        @change="(num: any) => handleNumChange(num, item)"
                      />
                    </view>
                  </view>
                </view>
              </view>
              <template #btn="{ option }">
                <view class="flex-space-between swipe-action-btn f12">
                  <text>{{ option.text }}</text>
                </view>
              </template>
            </u-swipe-action>
          </view>
        </div>
      </view>
    </u-popup>
    <u-modal
      v-model="dleModalController"
      :show-cancel-button="true"
      :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
      confirm-color="#333"
      confirm-text="确定"
      :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
      @cancel="() => (dleModalController = false)"
      @confirm="handleClear"
    >
      <view class="login-box"> 确认<text style="color: #e50c00">清空</text>当前购物车吗？ </view>
    </u-modal>
  </view>
</template>

<script setup lang="ts">
import { doClearCart, doDeleteCart } from '@/apis/o2oshop'
import { doUpdateShopCarGood } from '@/apis/shopCar'
import type { productAttribute } from '@/apis/o2oshop/model'
import { ref, computed, type PropType, watch } from 'vue'
import type { GoodItemSpec } from '@/apis/o2oshop/model'

const { divTenThousand } = useConvert()
const isOpenCarPopup = ref(false)

const switchTitle = ref('购物车')
const activeCar = computed(() => switchTitle.value === '购物车')

const emit = defineEmits(['initCarData', 'changeCarData', 'getPrice', 'changeSwipOpen'])

interface GoodItemSpecShow extends GoodItemSpec {
  show: boolean
}
const props = defineProps({
  shopId: {
    type: String as PropType<Long>,
    required: true,
  },
  validNum: {
    type: Number,
    required: true,
  },
  nvalidNum: {
    type: Number,
    required: true,
  },
  currentList: {
    type: Object as PropType<GoodItemSpecShow[]>,
    default: () => ({}),
  },
})

// 点击删除
const handleSwipeAction = async (item: GoodItemSpec) => {
  try {
    const { code, msg } = await doDeleteCart([{ shopId: props.shopId, uniqueIds: [item.uniqueId], skuIds: [item.id] }])
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '删除失败'}`, icon: 'none' })
    emit('initCarData')
  } catch (error) {
    return
  }
}

const handleOpen = (uniqueId: string) => {
  emit('changeSwipOpen', uniqueId)
}

// 更改数量
const handleNumChange = async (number: number, item: GoodItemSpec) => {
  const { productId, id, num, productAttributes, uniqueId } = item
  if (number === num) return
  const params = {
    shopId: props.shopId,
    productId,
    skuId: id,
    num: number,
    productAttributes,
  }
  try {
    const { code, msg } = await doUpdateShopCarGood(uniqueId, params)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '更改数量失败'}`, icon: 'none' })
    emit('getPrice')
    emit('initCarData')
  } catch (error) {
    return
  }
}

// 切换有效和失效商品
const handleSwitch = (title: string) => {
  emit('changeCarData', title)
  switchTitle.value = title
}
const dleModalController = ref(false)
// 清空购物车
const handleClear = async () => {
  try {
    const { code, msg } = await doClearCart(props.shopId)
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '清空失败'}`, icon: 'none' })
    emit('initCarData')
  } catch (error) {
    return
  }
}

// 处理规格数据
const handleSpecs = (productAttributes: productAttribute[], specs: string[]) => {
  const arr = productAttributes?.map((item) => item.featureValues).flat() || []
  return [...specs, ...arr.map((item) => item.firstValue + divTenThousand(item.secondValue) + '元')].join('; ')
}
watch(
  () => isOpenCarPopup.value,
  (val) => {
    if (val === false) handleSwitch('购物车')
  },
)
const swipeActionWidth = computed(() => {
  return uni.upx2px(750)
})
defineExpose({
  isOpenCarPopup,
})
</script>

<style scoped lang="scss">
@include b(swipe-action-btn) {
  /* #ifndef MP-WEIXIN */
  height: 100%;
  align-items: center;
  justify-content: center;
  /* #endif */

  flex-direction: column;
  /* #ifdef MP-WEIXIN */
  /* 微信小程序兼容 slot 误删 */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  /* 微信小程序兼容 slot 误删 */
  /* #endif */
}
@include b(list) {
  display: flex;
  padding: 20rpx 36rpx;
  border-bottom: 1rpx solid #c0c0c0;
  @include e(name) {
    color: #000;
    font-size: 28rpx;
    font-weight: 700;
    width: 464rpx;
    @include utils-ellipsis(1);
  }
  @include e(specs) {
    margin: 10rpx 0 23rpx;
    height: 65rpx;
    width: 464rpx;
    color: #666;
    @include utils-ellipsis(2);
    font-size: 24rpx;
  }
  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    width: 464rpx;
    height: 48rpx;
    @include m(price) {
      color: #fa3534;
      font-size: 40rpx;
      font-weight: 700;
    }
  }
}
@include b(title) {
  height: 100rpx;
  border-bottom: 1rpx solid #c0c0c0;
  border-top: 1rpx solid #c0c0c0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 36rpx;
  font-size: 30rpx;
  color: #333;
  box-sizing: border-box;
}
.unActiveCar {
  position: relative;
  &::after {
    position: absolute;
    content: '已失效';
    left: 0;
    top: 0;
    bottom: 0;
    right: 0;
    color: #fff;
    text-align: center;
    line-height: 187rpx;
    background: rgba(0, 0, 0, 0.5);
  }
}
.active {
  color: #fa3534;
  font-weight: 700;
}
@include b(login-box) {
  height: 120rpx;
  text-align: center;
  line-height: 120rpx;
}
.cart-popup {
  height: 100%;
  overflow: hidden;
  &__list {
    height: calc(100% - 208rpx);
    overflow-y: auto;
  }
}
</style>
