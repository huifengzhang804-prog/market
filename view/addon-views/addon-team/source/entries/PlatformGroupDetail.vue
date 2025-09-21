<script setup lang="ts">
import { ElMessage } from 'element-plus'
import SelectGoodTable from '../components/select-good-table.vue'
import { ApiGroupForm, FlatGoodRetrieve } from '../index'
import { doGetRetrieveProduct } from '@/apis/good'
import { doGetGroupFormPlatform } from '../apis'
import { useRoute, useRouter } from 'vue-router'
import { ref } from 'vue'

const $route = useRoute()
const $router = useRouter()
const submitForm = ref<ApiGroupForm>({
  name: '',
  startTime: '',
  endTime: '',
  effectTimeout: 0,
  mode: 'COMMON',
  users: [],
  payTimeout: 0,
  simulate: false,
  huddle: false,
  stackable: {
    vip: false,
    coupon: false,
    full: false,
  },
  products: [],
})
const flatGoodList = ref<FlatGoodRetrieve[]>([])
const datePickerValue = ref<string[]>([])

fillForm()

async function fillForm() {
  const activityId = $route.query.activityId as string
  const shopId = $route.query.shopId as string
  const { code, data } = await doGetGroupFormPlatform(shopId, activityId)
  if (code !== 200) return ElMessage.error('获取表单失败')
  submitForm.value = data
  datePickerValue.value = [data.startTime, data.endTime]
  fillTable(data)
}
async function fillTable(data: ApiGroupForm) {
  const productId = data.products.map((item) => item.productId)
  const { code, data: productData } = await doGetRetrieveProduct({ productId })
  if (code !== 200) return ElMessage.error('获取活动商品信息失败')
  let tempArr: FlatGoodRetrieve[] = []
  data.products.forEach((item) => {
    const findProduct = productData.list.find((it) => it.productId === item.productId)
    if (findProduct) {
      tempArr = [
        ...tempArr,
        ...item.skus.map((it, index) => ({
          productName: findProduct.productName,
          productPic: findProduct.pic,
          productId: findProduct.productId,
          skuItem: {
            productId: findProduct.productId,
            skuId: it.skuId,
            skuName: findProduct.specs[index],
            skuPrice: findProduct.salePrices[index],
            skuStock: findProduct.stocks[index],
            stockType: findProduct.stockTypes[index],
          },
          rowTag: 0,
          stock: it.stock,
          prices: it.prices,
          isJoin: true,
        })),
      ]
    }
  })

  flatGoodList.value = tempArr
}
</script>

<template>
  <div class="groupForm">
    <div class="groupForm__title">基本信息</div>
    <el-form ref="ruleFormRef" :model="submitForm" label-width="110" label-position="right" disabled>
      <el-form-item label="活动名称" prop="name">
        <el-input v-model="submitForm.name" placeholder="限10字" style="width: 550px" maxlength="10" />
      </el-form-item>
      <el-form-item label="活动时间" prop="startTime">
        <div>
          <el-date-picker
            v-model="datePickerValue"
            style="width: 550px"
            type="datetimerange"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </div>
      </el-form-item>
      <el-form-item label="拼团有效时间" prop="effectTimeout" required>
        <el-input v-model="submitForm.effectTimeout" :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))" style="width: 550px">
          <template #append> 分钟 </template>
        </el-input>
      </el-form-item>
      <el-form-item label="拼团模式" prop="mode">
        <el-radio-group v-model="submitForm.mode">
          <el-radio value="COMMON">普通拼团</el-radio>
          <el-radio value="STAIRS">阶梯拼团</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="参团人数" prop="users" required>
        <el-input
          v-if="submitForm.mode === 'COMMON'"
          v-model="submitForm.users[0]"
          :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))"
          :parser="(value:string) => `$ ${Number(value)>=100?100:value}`"
          style="width: 550px"
          :max="100"
        >
          <template #append> 人 </template>
        </el-input>
        <div v-else>
          <div v-for="(item, index) in submitForm.users" :key="index" class="groupForm__stairs">
            <span class="groupForm__stairs--title">第{{ index + 1 }}阶段人数</span>
            <el-input
              v-model="submitForm.users[index]"
              class="groupForm__stairs--input"
              style="width: 450px"
              :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))"
              :parser="(value:string) => `${Number(value)>=100?100:value}`"
              :max="100"
            >
              <template #append> 人 </template></el-input
            >
          </div>
        </div>
      </el-form-item>

      <el-form-item label="订单关闭时间" prop="payTimeout">
        <el-input-number v-model="submitForm.payTimeout" :controls="false" :max="360" :min="3" />
        <span class="tips" style="margin-left: 8px">商品按下单减库存，请设置未付款订单自动取消时间及时释放库存，可输入3-360分钟</span>
      </el-form-item>
      <el-form-item label="模拟成团" prop="simulate">
        <div>
          <el-radio-group v-model="submitForm.simulate">
            <el-radio :value="false">关闭</el-radio>
            <el-radio :value="true">开启</el-radio>
          </el-radio-group>
          <div class="tips">
            开启模拟成团后，拼团有效期内人数未满的团，系统将会以“虚拟用户”凑满人数，使该团拼团成功。你只需要对已付款参团的真实买家发货。建议合理开启，以提高成团率。
          </div>
        </div>
      </el-form-item>
      <el-form-item label="凑团模式" prop="huddle">
        <div>
          <el-radio-group v-model="submitForm.huddle">
            <el-radio :value="false">关闭</el-radio>
            <el-radio :value="true">开启</el-radio>
          </el-radio-group>
          <div class="tips">开启凑团后，活动商品详情页展示未成团的团列表，买家可以任选一个团参团，提升成团率。</div>
        </div>
      </el-form-item>
      <!-- <el-form-item label="活动预热" prop="preheat">
                <div>
                    <el-radio-group v-model="submitForm.preheat">
                        <el-radio :value="false">关闭</el-radio>
                        <el-radio :value="true">开启</el-radio>
                    </el-radio-group>
                    <div>
                        <el-input
                            v-if="submitForm.preheat"
                            v-model="submitForm.preheatHours"
                            style="width: 450px"
                            :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))"
                            :parser="(value:string) => `${Number(value)>=24?24:value}`"
                            :max="100"
                        >
                            <template #append> 小时 </template></el-input
                        >
                        <div v-else class="tips">开启后，商品详情展示未开始的拼团活动，但活动开始前用户无法拼团购买。</div>
                    </div>
                </div>
            </el-form-item> -->
      <el-form-item label="优惠叠加">
        <el-checkbox v-model="submitForm.stackable.vip" label="会员价" />
        <el-checkbox v-model="submitForm.stackable.coupon" label="优惠券" />
        <el-checkbox v-model="submitForm.stackable.full" label="满减" />
      </el-form-item>
    </el-form>
    <select-good-table ref="selectGoodsTableRef" :mode="submitForm.mode" :users="submitForm.users" :is-edit="true" :flat-good-list="flatGoodList" />
  </div>
  <div class="nav-button">
    <el-button round plain @click="$router.back()">返回</el-button>
  </div>
</template>

<style lang="scss" scoped>
@include b(groupForm) {
  padding: 30px 16px 10px 16px;
  overflow: scroll;
  @include e(title) {
    font-size: 14px;
    color: #606266;
    font-weight: bold;
    margin-bottom: 30px;
  }
  @include e(stairs) {
    margin-bottom: 16px;
    @include m(title) {
      font-size: 12px;
      color: #333;
      font-weight: bold;
    }
    @include m(input) {
      margin: 0 7px;
    }
  }
  @include e(btn) {
    width: 1010px;
    position: fixed;
    left: 292px;
    bottom: 10px;
    height: 60px;
    @include flex;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    z-index: 999;
    margin: 0 auto;
  }
}
@include b(tips) {
  font-size: 12px;
  color: #c4c4c4;
}
@include b(nav-button) {
  margin-top: auto;
  align-items: center;
  position: sticky;
  bottom: 0;
  padding: 15px 0;
  display: flex;
  justify-content: center;
  box-shadow: 0 0 10px #d5d5d5;
  background-color: #fff;
  z-index: 9;
}
</style>
