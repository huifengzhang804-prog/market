<script setup lang="ts">
import { ref } from 'vue'
import { useConversionPrice } from '@/hooks'
import { doGetComByFoot, doDelComFoot } from '@/apis/goods'
import { ElMessage, ElMessageBox } from 'element-plus'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
type ComItem = {
  date: string
  id: string
  productId: string
  productPic: string
  productPrice: string
  shopId: string
  status: string
  evaluatePerson: string
  productName: string
}

const $router = useRouter()
const productName = ref('')
const showSelectAll = ref(false)
// 二维数组商品列表
const comList = ref<ComItem[][]>([])
// map商品列表
const comMapList = ref<Map<string, ComItem[]>>(new Map())
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
  total: 1,
})
const searchConfig = reactive({
  chooseTime: ['', ''],
})
const ids = ref<string[]>([])

initCom()

const handleChangeDate = (e: string[]) => {
  pageConfig.current = 1
  searchConfig.chooseTime = e
  initCom()
}
const handleChangePage = (e: number) => {
  pageConfig.current = e
  initCom()
}
const callinitCom = () => {
  pageConfig.current = 1
  initCom()
}
const handleClickDel = (date: string, id: string) => {
  ElMessageBox.confirm('确定需要删除足迹？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, data } = await doDelComFoot('DELETE', [id])
    if (code === 200) {
      ElMessage.success('删除成功')
      initCom()
    } else {
      ElMessage.warning('删除失败')
    }
  })
}
async function initCom() {
  const { code, data, msg } = await doGetComByFoot({
    startDate: searchConfig.chooseTime ? searchConfig.chooseTime[0] : null,
    endDate: searchConfig.chooseTime ? searchConfig.chooseTime[1] : null,
    current: pageConfig.current,
    size: pageConfig.size,
    productName: productName.value,
  })
  if (code === 200 && data) {
    ids.value = data.records.map((item: any) => {
      return item.id
    })
    comMapList.value = convertSecArray(data.records)
    pageConfig.current = data.current
    pageConfig.pages = data.pages
    pageConfig.total = data.total
  } else {
    ElMessage.error(msg || '获取足迹失败')
  }
}
/**
 * @description: 统计对应日期存在的足迹商品
 */
function convertSecArray(list: ComItem[]): Map<string, ComItem[]> {
  let map = new Map<string, ComItem[]>()
  while (list.length) {
    let current = list.shift()
    if (!current) continue
    if (map.has(current.date)) {
      const currentMap = map.get(current.date) ?? []
      currentMap.push(current)
    } else {
      map.set(current.date, [current])
    }
  }
  return map
}
const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
const handleDelAll = () => {
  ElMessageBox.confirm('确定要删除本页所有足迹吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code } = await doDelComFoot('DELETE', ids.value)
    if (code === 200) {
      ElMessage.success('删除成功')
      initCom()
    }
  })
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left>我的足迹</div>
        <div flex justify-between c-mt-17>
          <div v-if="!showSelectAll" c-w-80 c-h-30 b-1 c-bc-eaeaea c-fs-14 e-c-3 c-lh-30 c-ml-2 cursor-pointer @click="showSelectAll = true">
            批量设置
          </div>
          <div v-else c-ml-2 c-fs-14 flex items-center>
            <span c-c-e31436 c-ml-15 cursor-pointer @click="handleDelAll">删除商品</span>
            <span c-c-e31436 c-ml-20 cursor-pointer @click="showSelectAll = false">取消</span>
          </div>
          <div flex items-center>
            <div c-fs-12 e-c-3 flex items-center>
              日期：
              <div b-1 c-bc-DCDCDC>
                <el-date-picker
                  v-model="searchConfig.chooseTime"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  value-format="YYYY-MM-DD"
                  @change="handleChangeDate"
                />
              </div>
            </div>
            <div flex items-center c-w-256 bg-white b-1 c-bc-DCDCDC c-fs-12 c-ml-15>
              <el-input v-model="productName" placeholder="请输入商品名称搜索" />
              <div cursor-pointer c-w-48 c-h-32 c-lh-32 c-bg-eee b-l c-bc-DCDCDC @click="callinitCom">搜索</div>
            </div>
          </div>
        </div>
        <template v-for="[keys, value] of comMapList" :key="keys">
          <div c-fs-20 text-left fw-700 c-mt-14 c-mb-17>
            {{ keys }}
          </div>
          <div c-fs-12 text-left c-mt-17 flex flex-wrap c-gap-18>
            <div
              v-for="ite in value"
              :key="ite.id"
              b-1
              c-bc-eaeaea
              c-w-189
              c-mb-15
              relative
              cursor-pointer
              @click="gotoDetail(ite.productId, ite.shopId)"
            >
              <el-image :src="ite.productPic" style="width: 189px; height: 189px" />
              <div c-ellipsis-2 e-c-3 c-ml-9>
                {{ ite.productName }}
              </div>
              <div c-c-e31436 fw-700 c-fs-16 c-lh-24 c-ml-9>￥ {{ useConversionPrice(ite.productPrice) }}</div>
              <div v-if="ite.evaluatePerson" e-c-9 c-mb-13 c-ml-9>{{ ite.evaluatePerson }} 人已评价</div>
              <!-- 删除按钮 -->
              <div
                opacity-45
                absolute
                top-2
                right-1
                c-w-25
                c-h-25
                bg-black
                flex
                items-center
                justify-center
                class="deleteBtn"
                @click.stop="handleClickDel(ite.date, ite.id)"
              >
                <el-icon size="20" color="#fff">
                  <i-ep-delete />
                </el-icon>
              </div>
            </div>
          </div>
        </template>
        <ToTopGoCar />
        <div c-mt-28 c-mb-43 flex justify-center>
          <el-pagination
            background
            layout="prev, pager, next"
            :total="+pageConfig.total"
            :current-page="pageConfig.current"
            @current-change="handleChangePage"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.deleteBtn:hover {
  background: rgba(227, 20, 54, 1);
}
</style>
