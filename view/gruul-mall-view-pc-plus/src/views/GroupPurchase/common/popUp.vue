<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-mutating-props -->
<script setup lang="ts">
import { ref, onMounted, watch, PropType, reactive } from 'vue'
import { doGetGoodSku, doGetGoodDetail, doGetShopBaseInfo } from '@/apis/goods'
import { groupPurchaseNum } from '@/apis/GroupPurchase/index'
import GoodSpec from '@/components/good-spec/good-spec.vue'
import { ElMessage } from 'element-plus'
import { ApiGoodSkus } from '@/views/detail/types/index'
import Storage from '@/libs/storage'
import router from '../../../router/index'
import useConvert from '@/composables/useConvert'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

const storage = new Storage()

const { divTenThousand } = useConvert()

const $route = useRoute()
const $router = useRouter()
export interface IData {
  value: string
  discount: string
}
const prop = defineProps({
  isShow: {
    type: Boolean,
    default: false,
  },
  // 查询商品sku
  skuList: {
    type: Object,
    default: () => {},
  },
  group: {
    type: String,
    default: () => '',
  },
  // 说明
  services: {
    type: Array,
    default: () => [],
  },
  areaCode: {
    type: Array,
    default: () => [],
  },
  // 运费
  freight: {
    type: String,
    default: () => '',
  },
  itemList: {
    type: Object,
    default: () => {},
  },
  type: {
    type: String,
    default: () => '',
  },
  amount: {
    type: String,
    default: () => '',
  },
  discountCoupon: {
    type: Array,
    default: () => [],
  },
  discountList: {
    type: Array as PropType<IData[]>,
    default: () => [],
  },
  limitNum: {
    type: String,
    default: () => '',
  },
  pricesPrice: {
    type: String,
    default: () => '',
  },
  name: {
    type: String,
    default: () => '',
  },
})

const propIsShow = computed({
  get() {
    return prop.isShow
  },
  set(val: boolean) {
    emit('update:isShow')
  },
})
const propAreaCode = computed({
  get() {
    return prop.areaCode
  },
  set(val: any) {
    emit('update:areaCode')
  },
})

const emit = defineEmits(['closeFn', 'update:isShow', 'update:areaCode', 'priceValue'])
const closeFn = () => {
  emit('closeFn', false)
}
const priceValue = (val) => {
  emit('priceValue', val)
}
// 选择尺寸 checkSize
const checkSizeValue = ref('')
const Arr = ref(['', ''])
const skuArr = ref(['', ''])
const skuListList = ref()
const groupPurchaseNumList = ref()
const activityId = ref('')
let price = ref()
let stock = ref()

// 拼团的sku
const skuListRef = ref()
const groupPurchaseNumInt = async () => {
  const res = await groupPurchaseNum($route.query.shopId as string, $route.query.productId as string)
  skuListRef.value = res.data.skus

  activityId.value = res.data.activityId
  const minPrice = ref()
  minPrice.value = prop.pricesPrice
  price.value = divTenThousand(minPrice.value).toFixed(2)
  stock.value = res.data?.skus?.[0]?.stock?.[0]
}
const ListPrice = ref()
const skuListPrice = (skuId?: string) => {
  if (!skuId) return
  ListPrice.value = skuListRef.value?.filter((item) => item.skuId === skuId)
  priceValue(ListPrice.value)
  return ListPrice.value
}

// sku
const defaultSkuList = ref()
const defaultSkuLists = ref()
const pic = ref('')
const goodInfo = ref()
const GoodSku = async () => {
  const res = await doGetGoodSku($route.query.productId as string, $route.query.shopId as string)
  defaultSkuList.value = res.data.skus[0]
  defaultSkuLists.value = res.data
  skuListPrice(res.data.skus[0].id)
  const { data } = await doGetGoodDetail($route.query.productId as string, $route.query.shopId as string)
  pic.value = data.pic
  goodInfo.value = data
}
// 拼团订单后跳转页面
const shopInfo = ref({
  shopId: '',
  shopLogo: '',
  shopName: '',

  newTips: '',
  status: '',
})
// 获取店铺信息
const initShopInfoInt = async () => {
  const { code, data, msg } = await doGetShopBaseInfo($route.query.shopId as string)
  if (code !== 200) return ElMessage.error(msg ? msg : '未获取店铺信息')
  shopInfo.value.shopId = data.id
  shopInfo.value.shopName = data.name
  shopInfo.value.shopLogo = data.logo
}
// 选中规格
const checkedList = ref<ApiGoodSkus>()
const handleChooseSpec = (e: ApiGoodSkus[]) => {
  checkedList.value = e[0]
}
const handleBuyNow = () => {
  if (+ListPrice.value?.[0].stock === 0) return ElMessage('该规格商品库存不足')

  const orderType = 'TEAM'
  const { id: skuId, image, price, salePrice, productId, secKillPrice, weight } = checkedList.value || defaultSkuList.value
  const { name: productName, id, freightTemplateId, pic } = goodInfo.value
  const params = {
    skuId,
    image,
    pic,
    price,
    salePrice,
    productId,
    productName,
    id,
    freightTemplateId,
    weight,
  }
  params.salePrice = +prop.pricesPrice
  if (ListPrice.value?.[0]?.prices?.[0]) params.salePrice = ListPrice.value?.[0]?.prices?.[0]
  storage.setItem(
    `commodityInfo`,
    [Object.assign(shopInfo.value, activityId.value, { orderType }, { products: [{ ...params, num: limitationNum.value }] })],
    60 * 60 * 2,
  )
  const user = ref()
  if (prop.itemList.totalNum) {
    user.value = prop.itemList.totalNum
  } else {
    user.value = prop.itemList.item
  }
  $router.push({
    path: '/settlement',
    query: { source: 'PRODUCT', activityId: activityId.value, user: user.value, orderType: orderType, teamNo: prop.itemList.teamNo },
  })
}
// 限购数量
const limitationNum = ref(1)
onMounted(() => {
  GoodSku()
  groupPurchaseNumInt()
  initShopInfoInt()
})

// 计数器
</script>

<template>
  <el-dialog v-model="propIsShow" class="box-card" width="800px" :show-close="false" :close-on-click-modal="false">
    <template #header>
      <!-- 我要拼团弹窗 -->
      <div class="title">
        <!-- 快速成团  -->
        <h3 v-if="group === '我要开团' ? true : false">我要开团</h3>
        <h3 v-else>快速成团</h3>
        <!-- xianshi -->
        <h4 v-if="group === '我要开团' ? true : false" class="xianshi">
          <li>
            <i>{{ itemList.item }}</i> 人团 拼团价
            <!-- <i>¥{{ (skuListPrice(checkedList?.id)?.[0].prices?.[0]).toFixed(2) || prop.pricesPrice }}</i> -->
            <i
              >¥{{
                skuListPrice(checkedList?.id) ? divTenThousand(ListPrice?.[0]?.prices?.[0]).toFixed(2) : divTenThousand(prop.pricesPrice).toFixed(2)
              }}</i
            >
          </li>
        </h4>
        <!-- 快速成团 s  xianshi -->
        <div v-else class="h4-copy xianshi">
          <span><img :src="prop.itemList.commanderAvatar" alt="" /> </span>
          <i>{{ prop.itemList.commanderNickname }}</i
          ><em
            >还差<i>{{ +prop.itemList.totalNum - +prop.itemList.currentNum }}人</i>拼成</em
          >
        </div>
        <!-- 快速成团 e -->

        <i style="cursor: pointer" @click="closeFn"
          ><svg
            t="1686128889407"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="9052"
            width="24"
            height="24"
          >
            <path
              d="M576 512l277.333 277.333-64 64L512 576 234.667 853.333l-64-64L448 512 170.667 234.667l64-64L512 448l277.333-277.333 64 64L576 512z"
              fill="#101010"
              p-id="9053"
            ></path>
          </svg>
        </i>
      </div>
    </template>
    <!-- 快速成团弹窗 -->
    <div class="shop">
      <div class="img fl">
        <!-- || defaultSkuList.image -->
        <img :src="checkedList?.image || skuListList?.image || defaultSkuList.image || pic" alt="" />
      </div>
      <ul class="info fl">
        <li>
          <h4>拼团价</h4>
          <p>
            ￥<span>{{
              skuListPrice(checkedList?.id)
                ? divTenThousand(skuListPrice(checkedList?.id)?.[0]?.prices?.[0]).toFixed(2)
                : divTenThousand(prop.pricesPrice).toFixed(2)
            }}</span>
            <i v-if="+divTenThousand(skuListPrice(checkedList?.id)?.[0]?.prices?.[0]) === 0"> 起</i>
          </p>
        </li>
        <li>
          <div style="position: relative; display: flex">
            <h4>优惠券</h4>
            <p class="yhq-p">
              <span v-if="!type || !amount ? false : true">{{ amount }} {{ type }}</span>
              <span v-for="(item, index) in prop.discountList" v-show="!item.discount || !item.value ? false : true" :key="index"
                >{{ item.discount }} {{ item.value }}
              </span>
            </p>
            <!-- <span
                            v-if="!type || (!amount && prop.discountList.discount === '' && prop.discountList.value === '') ? true : false"
                            style="background-color: #faabb3"
                            >暂无可用优惠券</span
                        > -->
          </div>
        </li>
        <!--<li>
                    <h4>促销</h4>
                    <p>
                         <span>满减</span><i>满2000元减30元</i><span>满减</span><i>满2000元减30元</i> 
                    </p>
                </li>-->
      </ul>
    </div>
    <!-- 规格 -->
    <ul class="specification">
      <li>
        <span>送至</span>
        <div class="m-4" style="display: inline-block">
          <el-cascader
            v-model="propAreaCode"
            :style="{ width: '250px', height: '34px', border: '1px solid #bbb' }"
            placeholder="请选择省/市/区"
            :options="regionData"
            filterable
          />
        </div>
        <i style="color: #333">{{ prop.freight ? `快递：${Number(prop.freight).toFixed(2)}` : '所选地区免运费' }}</i>
      </li>

      <li c-pl-50 c-pb-20>
        <GoodSpec :sku-group="defaultSkuLists" :img-boolean="false" @choose-spec="handleChooseSpec" />
      </li>
      <li>
        <span>数量</span>
        <el-input-number v-model="limitationNum" :min="1" :max="+ListPrice?.[0]?.stock || 1" size="small" />
        <em v-show="ListPrice?.[0].stock < 11">剩余库存：{{ ListPrice?.[0].stock }}</em>
      </li>
      <li v-if="group === '我要开团' ? true : false">
        <span /><el-button
          color="#ee1e32"
          :disabled="!stock ? true : false"
          plain
          style="width: 146px; height: 40px; transform: translateY(-20px)"
          @click="handleBuyNow"
        >
          立即开团
        </el-button>
      </li>
      <li v-else>
        <span /><el-button
          color="#ee1e32"
          :disabled="!stock ? true : false"
          plain
          style="width: 146px; height: 40px; transform: translateY(-20px)"
          @click="handleBuyNow"
        >
          立即拼团
        </el-button>
      </li>
      <li>
        <span v-if="prop.services.length === 0 ? false : true">说明</span>
        <div v-for="(item, index) in prop.services" :key="index" style="display: inline-block">
          <i>
            <svg
              t="1686195591182"
              class="icon"
              viewBox="0 0 1029 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="8239"
              width="12"
              height="12"
            >
              <path
                d="M960.609667 766.815154C915.369476 848.313333 851.995706 911.514088 770.492796 956.497865 688.871881 1001.483712 603.617094 1024 514.566068 1024 425.518886 1024 340.146391 1001.483712 258.643184 956.497865 177.02227 911.514088 113.534933 848.313333 68.132375 766.815154 22.729817 685.433205-2.0e-06 600.547513-2.0e-06 512.392312-2.0e-06 423.220324 23.066676 337.664464 69.151527 255.495821 115.228098 173.43217 179.401869 110.336405 261.708627 66.206457 343.897972 22.06527 428.235049 0 514.566068 0 600.88851 0 685.124145 22.06527 767.427353 66.206457 849.616403 110.336405 913.67631 173.43217 959.58667 255.495821 1005.500874 337.664464 1028.449843 423.220324 1028.449843 512.392312 1028.449843 600.547513 1005.837733 685.433205 960.609667 766.815154ZM885.615313 298.273899C847.237099 229.756752 793.734261 177.050072 725.151162 140.191418 656.551797 103.342228 586.332317 84.883918 514.566068 84.883918 442.292017 84.883918 372.011612 103.342228 303.647959 140.191418 235.284011 177.050072 181.781173 229.756752 143.183513 298.273899 104.582008 366.79282 85.24266 438.176962 85.24266 512.334345 85.24266 586.100451 104.232728 656.755866 142.103436 724.372456 180.075882 791.98875 233.136279 844.69543 301.268656 882.512904 369.401032 920.204684 440.481143 939.114604 514.566068 939.114604 588.598053 939.114604 659.734948 920.204684 727.867325 882.512904 795.995857 844.69543 848.94239 791.98875 886.638606 724.372456 924.330682 656.755866 943.207182 586.100451 943.207182 512.334345 943.207182 438.176962 923.981402 366.79282 885.615313 298.273899ZM479.35178 833.443349C405.257096 716.034394 318.088515 626.579078 257.067432 598.623367L400.89775 486.804075 470.637229 660.124747C470.637229 660.124747 583.956858 307.893739 762.656916 190.484488 758.301415 274.347774 740.864918 347.031142 771.375312 436.486458 692.921282 458.850435 531.654467 710.442364 479.35178 833.443349Z"
                p-id="8240"
              />
            </svg>
          </i>
          <em style="margin-left: 10pxm; margin-right: 15px">{{ item }}</em>
        </div>
      </li>
    </ul>
    <!-- </el-card> -->
  </el-dialog>
  <!-- tank 加上类名弹框隐藏，去掉弹框显示 -->
</template>

<style scoped lang="scss">
:deep(.el-card__body) {
  padding: 0;
}
:deep(.m-4) {
  margin: 0px;
}
.yhq-p {
  width: 500px;
  height: 20px;
  overflow: hidden;
  position: absolute;
  z-index: 4;
  left: 60px;
  &:hover {
    height: auto;
  }
  span {
    margin: 2px 0;
  }
}
li {
  list-style: none;
}
em,
i {
  font-style: normal;
}

.fl {
  float: left;
}

.flr {
  float: right;
}

.tank {
  display: inline !important;
}

.all {
  width: 100%;
  height: 100%;
  background-color: rgba(16, 16, 16, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999999;
  display: none;
}

.box-card {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999999;
  width: 833px;
  border-radius: 8px 8px 8px 8px;
  background-color: #fff;
  // 我要拼团弹窗
  .title {
    height: 44px;
    border-bottom: 1px solid #bbb;
    padding: 12px 16px 12px 24px;
    display: flex;
    font-size: 14px;

    .xianshi {
      display: inline !important;
    }

    h4 {
      flex: 1;
      margin: auto;
      display: none;
    }
    .h4-copy {
      flex: 1;
      margin: auto;
      font-weight: normal;
      color: #000;
      display: none;
      span {
        display: inline-block;
        width: 24px;
        height: 24px;
        border-radius: 50%;
        overflow: hidden;
        img {
          width: 100%;
          height: 100%;
        }
      }
      & > i {
        display: inline-block;
        width: 80px;
        height: 20px;
        margin: 0 30px 0 10px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      em {
        display: inline-block;
        width: 110px;
        height: 20px;
        vertical-align: top;
        margin-top: 4px;
        font-weight: bold;
        i {
          margin: 0 5px;
          color: #e31436;
        }
      }
    }

    li {
      width: 245px;
      list-style: none;
      margin: 0 auto;
      position: relative;
      color: #333;
      i {
        color: #ee1e32;
      }
      & > span {
        display: inline-block;
        width: 18px;
        height: 18px;
        border-radius: 50px;
        border: 2px solid #fff;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
        }

        &:nth-of-type(1) {
          position: absolute;
          left: 0;
          left: 0;
          z-index: 999;
        }

        &:nth-of-type(2) {
          position: absolute;
          top: 0;
          left: 12px;
          z-index: 199;
        }

        &:nth-of-type(3) {
          position: absolute;
          top: 0;
          left: 24px;
          z-index: 100;
        }

        &:nth-of-type(4) {
          position: absolute;
          top: 0;
          left: 34px;
          z-index: 99;
        }
      }

      i {
        margin-left: 9 px;
        display: inline-block;
      }
    }

    & > i {
      display: inline-block;
      width: 24px;
      height: 24px;
      overflow: hidden;
    }
  }
  // 快速成团弹窗
  .title-copy {
    height: 44px;
    border-bottom: 1px solid #bbb;
    padding: 12px 16px 12px 24px;
    display: flex;
    font-size: 14px;
    h4 {
      flex: 1;
      margin: auto;
      font-weight: normal;
      color: #000;
      span {
        display: inline-block;
        width: 24px;
        height: 24px;
        border-radius: 50%;
        overflow: hidden;
        img {
          width: 100%;
          height: 100%;
        }
      }
      & > i {
        display: inline-block;
        width: 80px;
        height: 20px;
        margin: 0 30px 0 10px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      em {
        line-height: 39px;
        display: inline-block;
        width: 90px;
        height: 20px;
        & > i {
          font-weight: bold;
          margin: 0 5px;
          color: #e31436;
        }
      }
    }
  }

  .shop {
    margin: 0px 0 0px 41px;
    // overflow: hidden;
    height: 160px;

    .img {
      width: 149px;
      height: 149px;
      overflow: hidden;
      border: 1px solid #d5d5d5;
      margin-right: 18px;

      img {
        width: 100%;
        height: 100%;
      }
    }

    .info {
      text-align: left;
      max-width: 70%;

      li {
        list-style: none;
        margin-bottom: 24px;

        &:nth-of-type(1) {
          font-weight: normal;
          margin-top: 5px;
          font-size: 14px;
          color: #ee1e32;

          span {
            font-weight: bold;
            font-size: 25px;
          }

          em {
            font-size: 20px;
          }
        }

        &:nth-of-type(2) {
          span {
            font-size: 12px;
            margin-right: 9px;
            display: inline-block;
            padding: 2px 5px;
            background-color: #ee1e32;
            color: #fff;
          }
        }

        &:nth-of-type(3) {
          font-size: 12px;

          span {
            margin-right: 9px;
            display: inline-block;
            padding: 2px 5px;
            border: 1px solid #ee1e32;
            color: #ee1e32;
          }

          i {
            color: #333;
            margin-right: 23px;
          }
        }
        &:last-child {
          margin: 0;
        }
      }

      h4 {
        width: 43px;
        height: 20px;
        text-align: justify;
        text-align-last: justify;
        text-justify: inter-word;
        font-size: 12px;
        color: #999;
        font-weight: normal;
        display: inline-block;
        margin-right: 27px;
      }

      p {
        display: inline-block;
      }
    }
  }

  .specification {
    li {
      text-align: left;
      font-size: 12px;

      span {
        display: inline-block;
        width: 43px;
        height: 20px;
        font-size: 12px;
        color: #999;
        text-align: justify;
        text-align-last: justify;
        text-justify: inter-word;
        margin: 0 23px 25px 52px;
      }

      &:nth-of-type(1) {
        margin-top: 20px;
        i {
          font-size: 12px;
          color: #333;
          margin-left: 9px;
        }
      }
      &:nth-of-type(2) {
        em {
          display: inline-block;
          padding: 8px 12px;
        }
      }
      &:nth-of-type(3),
      &:nth-of-type(4) {
        em {
          display: inline-block;
          padding: 8px 12px;
        }
      }
      &:nth-of-type(5) {
        & > i {
          margin-left: 12px;
          color: #333;
          & > em {
            margin: 0;
            color: #ee1e32;
          }
        }
        em {
          color: #999;
          margin-left: 10px;
        }
      }
      &:nth-of-type(6) {
        i {
          display: inline-block;
          font-weight: bold;
          line-height: 23px;
          vertical-align: top;
          margin-right: 7px;
        }
        em {
          margin-right: 20px;
        }
      }
      &:nth-of-type(7) {
        i {
          margin-top: 2px;
        }
        em {
          margin-left: 7px;
          margin-right: 20px;
        }
      }
      .xuxian {
        border: 1.5px dashed #efefef !important;
        color: #efefef !important;
        em {
          color: #efefef !important;
        }
      }
      .hongxian {
        border: 1.5px solid #e31436 !important;
        position: relative;
        i {
          position: absolute;
          bottom: 0;
          right: 0;
          display: inline-block;
          width: 0;
          height: 0;
          border: 10px solid #e31436;
          border-top: 10px solid transparent;
          border-left: 10px solid transparent;
          z-index: 9999;
        }
        span {
          display: inline-block;
          position: absolute;
          bottom: -34px;
          right: -55px;
          display: inline !important;
          z-index: 9999;
        }
      }
      .every {
        display: inline-block;
        border: 1.5px solid #e0e0e0;
        margin-right: 7px;
        .img {
          width: 38px;
          height: 38px;
          overflow: hidden;
          margin: 2px 3px;

          img {
            width: 100%;
            height: 100%;
          }
        }
        em {
          text-align: right;
          display: inline-block;
          width: 37px;
          height: 20px;
          margin: 13px 15px 8px;
          color: #333;
        }
        span {
          display: none;
        }
      }
    }
  }
}
</style>
