<script setup lang="ts">
import 'uno.css'
import { ref, reactive, onMounted } from 'vue'
import { doGetCouponListPc } from '../apis'
import { ApiCouponVO, ProductType, CartApiCouponVO, TagItem, formattingCouponRules } from '../index'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()

// 平台券 店铺券
const type = ref(1)
// 获取优惠券
const pageConfig = reactive({ current: 1, size: 10, pages: 1, total: 0 })
const getCouponList = ref<CartApiCouponVO[]>([])
/**
 * 分页切换
 */
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  CouponListInt()
}
function formattingName(item: ApiCouponVO) {
  if (item.productType === ProductType.ALL) {
    return '全场商品通用'
  }
  return '限' + item.shopName + '可用'
}
const tabList = ref<any[]>([
  {
    name: '平台券',
    key: 1,
  },
  {
    name: '店铺券',
    key: 0,
  },
])
const tagList = ref<TagItem[]>([
  {
    name: '待使用',
    key: 'UNUSED',
  },
  {
    name: '已使用',
    key: 'USED',
  },
  {
    name: '已过期',
    key: 'EXPIRED',
  },
])
async function CouponListInt() {
  getCouponList.value = await getCouponListInt()
}
async function getCouponListInt() {
  const { code, data } = await doGetCouponListPc({
    isPlatform: !!type.value,
    shopId: '',
    status: activeName.value,
    lastCreateTime: '',
    current: pageConfig.current,
    size: pageConfig.size,
  })
  if (code === 200) {
    pageConfig.pages = data.pages
    pageConfig.total = Number(data.total)
    return data.records
  } else {
    return []
  }
}
const activeName = ref('UNUSED')
// 券类型  触发
const platformChange = async () => {
  activeName.value = 'UNUSED'
  getCouponList.value = await getCouponListInt()
}

// 平台
const tabClickChang = async () => {
  pageConfig.current = 1
  getCouponList.value = await getCouponListInt()
}
onMounted(() => {
  CouponListInt()
})
</script>

<template>
  <div bg-white c-bc-ccc>
    <el-tabs v-model="type" @tab-change="platformChange">
      <el-tab-pane v-for="a in tabList" :key="a.key" :name="a.key">
        <template #label>
          <span class="custom-tabs-label" c-w-116 c-fs-18 e-c-3 fw-700>
            <span>{{ a.name }}</span>
          </span>
        </template>
        <el-tabs v-model="activeName" @tab-change="tabClickChang">
          <el-tab-pane v-for="i in tagList" :key="i.key" :name="i.key">
            <template #label>
              <span c-fs-14 e-c-3>
                <span>{{ i.name }}</span>
              </span>
            </template>
            <div flex flex-wrap>
              <div
                v-for="(item, index) in getCouponList"
                :key="index"
                c-mr-20
                c-mb-20
                c-h-88
                c-pl-10
                style="width: 185px; padding: 0 5px 0 10px"
                :class="i.key === 'UNUSED' ? 'img' : 'imgOld'"
              >
                <div c-h-64 style="color: #fff !important">
                  <div flex justify-between items-center c-h-50 c-pl-10 c-pr-6>
                    <div v-if="item.amount" flex c-w-65 c-fs-13 c-c-fff c-mt-15>
                      <span c-mt-2>￥</span><span c-fs-18 c-w-51 block class="sinLine">{{ divTenThousand(item?.amount).toFixed(2) }}</span>
                    </div>
                    <div v-else flex c-w-65 c-fs-13 c-c-fff c-mt-15>
                      <span c-fs-18 c-pl-10 c-pr-5 block class="sinLine">{{ item?.discount }}</span
                      ><span c-mt-2>折</span>
                    </div>
                    <div c-fs-14 c-c-fff>
                      <p float-right c-mt-15 c-w-100 text-right class="sinLine">{{ formattingName(item) }}</p>
                    </div>
                  </div>
                  <p c-fs-12 c-c-fff text-right c-mr-5>{{ item.startDate }}-{{ item.endDate }}</p>
                </div>

                <p text-right c-fs-12 c-lh-24 c-pr-5 c-pl-5 style="color: #999 !important">
                  {{ formattingCouponRules(item, false) }}
                </p>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="pageConfig.total"
      :current-page="pageConfig.current"
      :hide-on-single-page="true"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<style scoped lang="scss">
:deep(.el-tabs__item) {
  width: 146px;
  margin-right: 40px;
}

:deep(.el-tabs__active-bar) {
  height: 4px;
}
:deep(.el-tabs__active-bar, .el-tabs__nav-wrap::after) {
  height: 2px;
}
@include b(img) {
  background: url(./coupon-new.png);
  &:nth-of-type(5n) {
    margin-right: 0;
  }
}
@include b(imgOld) {
  background: url(./coupon-old.png);
  &:nth-of-type(5n) {
    margin-right: 0;
  }
}
@include b(sinLine) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.el-pagination {
  display: flex;
  justify-content: center;
}
</style>
