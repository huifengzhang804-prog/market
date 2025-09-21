<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { groupListApi, doGetGoodDetail } from '../apis'
import { useRouter } from 'vue-router'

const list = ref<any>([])
const total = ref<number>()
interface params {
  current: number
  size: number
}
const param = ref<params>({
  current: 1,
  size: 8,
})
const $router = useRouter()
// 商品列表
const groupList = async () => {
  const { data, code, msg } = await groupListApi(param.value)
  if (code === 200) {
    list.value = data.records
    total.value = data.total || 0
  } else {
    ElMessage(msg || '获取拼团列表失败')
  }
}
// 点击跳转商品详情
const productDetailsFn = async (productId: string, shopId: string, minPrice: string, price: string, activityId: string) => {
  const { msg } = await doGetGoodDetail(productId, shopId)
  if (productId && shopId) {
    $router.push({
      path: '/detail',
      name: 'detail',
      query: { productId: productId, shopId: shopId },
    })
  } else {
    ElMessage(msg)
  }
}
// 分页切换
const handleCurrentChange = (val: number) => {
  param.value.current = val
  groupList()
}
onMounted(() => {
  groupList()
})
</script>

<template>
  <div class="con">
    <div class="shop">
      <div
        v-for="(item, index) in list"
        :key="index"
        class="li"
        @click="productDetailsFn(item.productId, item.shopId, item.minPrice, item.price, item.activityId)"
      >
        <div class="img">
          <img :src="item.productImage" alt="" />
        </div>
        <div class="title">
          <div class="til">{{ item.productName }}</div>
          <div class="price">
            <span>{{ item.userNum }}人拼</span>
            <p>
              ￥<i>{{ (item.minPrice / 10000).toFixed(2) }}</i>
            </p>
          </div>
          <div class="oldPrice">
            <span>¥{{ (item.price / 10000).toFixed(2) }}</span>
          </div>
          <div class="btn" :style="item.status === 'FINISHED' ? 'background: #9997' : ''">
            <div class="num">
              <span
                ><svg
                  t="1685933506707"
                  class="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="8987"
                  width="24"
                  height="24"
                >
                  <path
                    d="M819.099 732.022c30.484-53.063 40.484-112.306 58.805-183.936-15.496 60.306-123.457 66.833-177.566 89.604 96.2-40.466 173.815-136.134 177.48-257.043-39.873 37.779-62.295 80.164-122.845 84.675 51.912-68.288 67.477-151.375 77.773-237.271-0.733-51.712 3.751-102.402-7.94-162.761-19.055 37.936-29.402 78.244-72.417 138.986-56.326 79.614-138.026 156.07-363.212 210.818-6.787 1.658-16.14 10.251-25.302-3.882-6.91-10.323-28.896-41.967 104.208-103.608 23.54-10.907 43.903-22.188 64.268-44-22.598 24.211-182.872 5.087-290.693 122.933-57.444 62.757-94.542 141.219-94.856 240.264-0.698 51.781-5.548 102.778 24.447 161.565 11.08-112.943 56.554-196.439 160.885-239.54-14.012 31.89-40.204 49.662-28.984 99.48 26.698-54.982 57.094-94.925 103.44-120.331-20.206 39.087-7.922 70.365-7.922 70.365C501.656 508.711 611.221 493.4 611.221 493.4S442.135 647.827 459.358 806.863c42.367-30.607 63.587-66.291 77.773-107.664 16.28 10.73 35.824 140.556-70.042 203.087-33.346 19.718-72.242 28.966-105.169 49.382 132.792 23.13 234.82-12.974 324.074-75.47 58.544-34.569 99.864-86.218 133.105-144.177z m0 0"
                    fill="#FBE480"
                    p-id="8988"
                  ></path>
                </svg>
              </span>
            </div>
            <div class="start">去开团</div>
          </div>
        </div>
      </div>
    </div>
    <div class="paging">
      <el-pagination
        :background="true"
        layout="prev, pager, next"
        :total="Number(total)"
        :current-page="param.current"
        class="mt-4"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.con {
  width: 1200px;
  margin: 0 auto;
}
.shop {
  padding-top: 18px;
  display: flex;
  flex-wrap: wrap;
  .li {
    width: 288px;
    height: 432px;
    margin-bottom: 16px;
    background-color: #fff;
    overflow: hidden;
    margin-right: 16px;
    &:nth-child(4n) {
      margin: 0;
    }
  }
  .img {
    width: 189px;
    height: 199px;
    overflow: hidden;
    margin: 50px 48px 12px 51px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  .title {
    padding: 0 16px 0 16px;
    .til {
      color: #101010;
      font-size: 13px;
      text-align: left;
      font-family: AlibabaSans-regular;
      line-height: 20px;
      height: 40px;
      width: 256px;
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    .price {
      overflow: hidden;
      padding-top: 8px;
      span {
        float: left;
        padding: 3px 8px 3px 9px;
        color: #ff1e32;
        font-size: 12px;
        font-family: AlibabaSans-regular;
        border: 1px solid #e31436;
        background-color: #fcf0f0;
      }
      p {
        display: block;
        height: 32px;
        color: #ff1e32;
        font-size: 12px;
        font-family: SourceHanSansSC-bold;
        float: left;
        margin-left: 10px;
        font-weight: bold;
        i {
          font-size: 22px;
          font-style: normal;
        }
      }
    }
    .oldPrice {
      margin-top: 10px;
      text-align: left;
      span {
        text-decoration: line-through;
        color: rgba(167, 167, 167, 1);
        font-size: 12px;
        font-family: SourceHanSansSC-regular;
      }
    }
    .btn {
      margin-top: 8px;
      overflow: hidden;
      width: 256px;
      height: 40px;
      line-height: 40px;
      border-radius: 4px;
      color: #fff;
      background: linear-gradient(89.08deg, rgba(255, 40, 40, 1) 1.27%, rgba(255, 40, 100, 1) 99.42%);
      .num {
        font-size: 14px;
        text-align: left;
        font-family: SourceHanSansSC-regular;
        float: left;
        width: 150px;
        span {
          display: block;
          float: left;
          width: 24px;
          height: 24px;
          margin: 3px 5px 3px 6px;
          svg {
            margin-top: 3px;
            transform: rotate(-40deg);
            width: 100%;
            height: 100%;
          }
        }
      }
      .start {
        font-size: 18px;
        font-family: SourceHanSansSC-bold;
        float: right;
        font-weight: bold;
        margin-right: 19px;
      }
    }
  }
}
.paging {
  margin: 57px 0 81px 0;
  display: flex;
  justify-content: center;
}
</style>
