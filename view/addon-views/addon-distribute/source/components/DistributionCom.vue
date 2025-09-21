<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Decimal from 'decimal.js'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetRetrieveProduct } from '@/apis/good'
import { ApiRetrieveComItemType } from '@/apis/good/model'
import SchemaForms from '@/components/SchemaForm.vue'
import {
  doPostDistributeCom,
  doGetDistributeCom,
  doPutDistributeCom,
  doDelDistributeCom,
  doGetDistributeConfig,
  doDelDistributeCancel,
  doDelDistributeAgain,
} from '../apis'
import type { DistributeParams, ApiDistributeComItem, DISTRIBUTESTATUS, SHARETYPE } from '../'
import type { FormInstance } from 'element-plus'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import useConvert from '@/composables/useConvert'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'

type SelectComInfo = {
  pages: number
  current: number
  list: ApiRetrieveComItemType[]
  total: number
  size: number
  name: string
  loading: boolean
  excludeProductIds: string[]
}

type DistributeListInfo = {
  list: ApiDistributeComItem[]
  current: number
  pages: number
  total: number
  size: number
  productName: string
  distributionStatus: string
}
const QChooseGoodsPopupRef = ref()
const distributeListInfo = reactive<DistributeListInfo>({
  list: [],
  current: 1,
  pages: 1,
  total: 0,
  size: 10,
  productName: '',
  // status: 'ALL',
  distributionStatus: 'ALL',
})
const { mulTenThousand, divTenThousand, divHundred } = useConvert()
const titleType = ref('add')
const showDialog = ref(false)
const platformLevel = ref('ONE')
const platforminfo = ref<any>({})

const formRef = ref<FormInstance>()
const getDialogTitle = computed(() => {
  return titleType.value === 'add' ? '新增' : titleType.value === 'edit' ? '编辑' : '查看'
})
// 选择商品数据
const commodityInfo = reactive<SelectComInfo>({
  pages: 1,
  current: 1,
  list: [],
  total: 0,
  size: 10,
  name: '',
  loading: false,
  excludeProductIds: [],
})
const distributeForm = reactive<DistributeParams>({
  shareType: 'UNIFIED' as keyof typeof SHARETYPE,
  one: '0',
  two: '0',
  three: '0',
  productIds: [],
  listId: '',
})
const rules = reactive({
  // productId: [{ required: true, message: '请选择商品', trigger: 'blur' }],
  one: [{ required: true, validator: levelOneValidatePass, trigger: 'blur' }],
  two: [{ required: true, validator: levelTwoValidatePass, trigger: 'blur' }],
  three: [{ required: true, validator: levelThreeValidatePass, trigger: 'blur' }],
})
// 分销商品选中列表
const selectDisComList = ref<ApiRetrieveComItemType[]>([])
// 添加商品列表
const addGoodsList = ref<ApiRetrieveComItemType[]>([])
// 选择商品选中列表
const selectGoodsList = ref<ApiRetrieveComItemType[]>([])
// 表单配置
const columns = [
  {
    label: '商品名称',
    prop: 'productName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
    },
  },
]

initDisComList()
initPlatformDisConfig()

const handleAddCom = () => {
  titleType.value = 'add'
  showDialog.value = true
}
const handleDisConfirm = () => {
  // 为查看跳出
  if (titleType.value === 'see') {
    showDialog.value = false
    return
  }
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (valid) {
      const tempObj = JSON.parse(JSON.stringify(distributeForm)) as DistributeParams
      if (tempObj.shareType === 'RATE') {
        tempObj.one = String(mulTenThousand(tempObj.one))
        tempObj.two = String(mulTenThousand(tempObj.two))
        tempObj.three = String(mulTenThousand(tempObj.three))
      } else if (tempObj.shareType === 'FIXED_AMOUNT') {
        tempObj.one = String(mulTenThousand(tempObj.one))
        tempObj.two = String(mulTenThousand(tempObj.two))
        tempObj.three = String(mulTenThousand(tempObj.three))
      } else {
        tempObj.shareType = platforminfo.value.shareType
        if (platforminfo.value.shareType === 'RATE') {
          tempObj.one = String(mulTenThousand(platforminfo.value.one))
          tempObj.two = String(mulTenThousand(platforminfo.value.two))
          tempObj.three = String(mulTenThousand(platforminfo.value.three))
        } else if (platforminfo.value.shareType === 'FIXED_AMOUNT') {
          tempObj.one = String(mulTenThousand(platforminfo.value.one))
          tempObj.two = String(mulTenThousand(platforminfo.value.two))
          tempObj.three = String(mulTenThousand(platforminfo.value.three))
        }
      }
      const { code, msg } =
        titleType.value === 'add'
          ? await doPostDistributeCom(deleteAttribute(tempObj))
          : await doPutDistributeCom(tempObj.listId, deleteAttribute(tempObj))
      if (code === 200) {
        ElMessage.success(titleType.value === 'add' ? '新增成功' : '修改成功')
        showDialog.value = false
        initDisComList()
      } else {
        ElMessage.error(msg ? msg : titleType.value === 'add' ? '新增失败' : '修改失败')
      }
    }
  })
}
const handleChooseCom = () => {
  chooseGoodsPopup.value = true
  initComList()
}

const handleChangeListCurrent = (e: number) => {
  distributeListInfo.current = e
  initDisComList()
}
const handleChangeListSize = (e: number) => {
  distributeListInfo.size = e
  initDisComList()
}

const handleCloseDisDialog = () => {
  resetDistribute()
}

const handleChangeTab = () => {
  distributeListInfo.current = 1
  initDisComList()
}

// tableCheck选中
const handleTableSelect = (selection: ApiRetrieveComItemType[]) => {
  selectDisComList.value = selection
}

// 重新分销
const doDelDistributeAgainFn = async (info: ApiDistributeComItem) => {
  if ((info.status === 'SELL_ON' || info.status === 'UNUSABLE') && info.distributionStatus === 'CANCEL_DISTRIBUTION') {
    const { code, success } = await doDelDistributeAgain(info.id)
    if (code === 200 && success) {
      ElMessage.success('重新分销成功')
      initDisComList()
    } else {
      ElMessage.error('重新分销失败')
    }
    initDisComList()
  }
}
const showDialogId = ref('')
// 编辑
const handleEditDisCom = async (info: ApiDistributeComItem) => {
  titleType.value = 'edit'
  distributeForm.productIds = [info.productId]
  distributeForm.shareType = info.shareType
  distributeForm.listId = info.id
  if (info.one || info.two || info.three) {
    distributeForm.one = info.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(info.one)) : String(divTenThousand(info.one))
    distributeForm.two = info.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(info.two)) : String(divTenThousand(info.two))
    distributeForm.three = info.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(info.three)) : String(divTenThousand(info.three))
  }
  showDialog.value = true
  showDialogId.value = info.distributionStatus
}

/**
 * 取消分销列表商品
 */
const handleDelDisListCom = (id: string) => {
  ElMessageBox.confirm('请确认是否取消分销，确定后商品将从停止分销！！！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, success } = await doDelDistributeCancel([id])
    if (code === 200 && success) {
      ElMessage.success('取消分销成功')
      initDisComList()
    } else {
      ElMessage.error('取消分销失败')
    }
  })
}
/**
 * 移除分销商品
 */
const handleEditDisdel = (id: string) => {
  console.log(id)

  ElMessageBox.confirm('请确认是否移除商品，确定后商品将从该列表中移除！！！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, success } = await doDelDistributeCom([id])
    if (code === 200 && success) {
      ElMessage.success('商品移除成功')
      initDisComList()
    } else {
      ElMessage.error('商品移除失败')
    }
  })
}

/**
 * 批量取消分销列表商品
 */
const handleBatchDelDisListCom = () => {
  if (!selectDisComList.value.length) {
    return ElMessage.warning('请选中商品')
  }
  ElMessageBox.confirm('请确认是否取消分销，确定后商品将从该列表中移除！！！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const ids = selectDisComList.value.map((item) => {
      return item.id
    })
    const { code, success } = await doDelDistributeCancel(ids)
    if (code === 200 && success) {
      ElMessage.success('取消分销成功')
      initDisComList()
    } else {
      ElMessage.error('取消分销失败')
    }
  })
}

/**
 * 表格排序
 */
let sortEnum = ref('')
const sortTableList = (label: string) => {
  switch (label) {
    case '总库存':
      sortEnum.value = sortEnum.value === '' ? '' : ''
      initDisComList()
      break
    case '加入分销时间':
      sortEnum.value = sortEnum.value === '' ? '' : ''
      initDisComList()
      break
    default:
      break
  }
}

async function initComList() {
  commodityInfo.loading = true
  const { code, data } = await doGetRetrieveProduct({
    current: commodityInfo.current,
    size: commodityInfo.size,
    excludeProductIds: commodityInfo.excludeProductIds,
  })
  if (code === 200) {
    commodityInfo.list = data.list
    commodityInfo.size = data.pageSize
    commodityInfo.current = data.pageNum
    commodityInfo.total = data.total
  } else {
    ElMessage.error('获取商品失败')
  }
  commodityInfo.loading = false
}

function resetDistribute() {
  distributeForm.productIds = []
  distributeForm.shareType = 'UNIFIED'
  distributeForm.one = '0'
  distributeForm.two = '0'
  distributeForm.three = '0'
  commodityInfo.excludeProductIds = []
  addGoodsList.value = []
  if (QChooseGoodsPopupRef.value?.tempGoods) {
    QChooseGoodsPopupRef.value.tempGoods = []
  }
}
function removegoodsItem(productId: string) {
  addGoodsList.value.forEach((item, index, arr) => {
    if (item.productId === productId) {
      arr.splice(index, 1)
    }
  })
  commodityInfo.excludeProductIds.forEach((item, index, arr) => {
    if (item === productId) {
      arr.splice(index, 1)
    }
  })
  distributeForm.productIds.forEach((item, index, arr) => {
    if (item === productId) {
      arr.splice(index, 1)
    }
  })
}
function levelOneValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeForm.shareType
  if (shareType === 'UNIFIED') {
    return callback()
  }

  const numberValue = Number(value)
  if (numberValue <= 0) {
    return callback(new Error('一级佣金应大于等于零'))
  }
  if (platformLevel.value !== 'ONE' && distributeForm.two && numberValue <= Number(distributeForm.two)) {
    return callback(new Error('一级佣金值应大于二级佣金'))
  }
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在0-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  callback()
}
function levelTwoValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeForm.shareType
  if (shareType === 'UNIFIED' || platformLevel.value === 'ONE') return callback()
  const numberValue = Number(value)
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在0-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  if (distributeForm.one && numberValue >= Number(distributeForm.one)) {
    return callback(new Error('二级佣金应小于一级佣金'))
  }
  if (platformLevel.value === 'THREE' && distributeForm.three && numberValue <= Number(distributeForm.three)) {
    return callback(new Error('二级佣金应大于三级佣金'))
  }
  callback()
}
function levelThreeValidatePass(rule: any, value: any, callback: any) {
  const shareType = distributeForm.shareType
  if (shareType === 'UNIFIED' || platformLevel.value !== 'THREE') return callback()
  const numberValue = Number(value)
  if (shareType === 'RATE' && (numberValue > 100 || numberValue < 0)) {
    return callback(new Error('一级佣金比例应设置在0-100之间'))
  }
  if (shareType === 'FIXED_AMOUNT' && (numberValue > 9000 || numberValue < 0)) {
    return callback(new Error('一级佣金应设置在0-9000之间'))
  }
  if (distributeForm.two && numberValue >= Number(distributeForm.two)) {
    return callback(new Error('三级佣金应小于二级佣金'))
  }
  callback()
}

function conversionCount(status: keyof typeof SHARETYPE, count: string) {
  if (status === 'UNIFIED') {
    if (platforminfo.value.shareType === 'RATE') {
      return divTenThousand(count)
    } else {
      return divTenThousand(count)
    }
  } else if (status === 'RATE') {
    return divTenThousand(count)
  } else {
    return divTenThousand(count)
  }
}
/**
 * 删除校验参数
 */
function deleteAttribute(config: DistributeParams) {
  if (platformLevel.value === 'ONE') {
    delete config.two
    delete config.three
  } else if (platformLevel.value === 'TWO') {
    delete config.three
  }
  return config
}
/**
 * 获取分销列表
 */
async function initDisComList() {
  const { code, data } = await doGetDistributeCom<ApiDistributeComItem>({
    size: distributeListInfo.size,
    current: distributeListInfo.current,
    productName: distributeListInfo.productName,
    distributionStatus: distributeListInfo.distributionStatus === 'ALL' ? null : distributeListInfo.distributionStatus,
  })
  if (code === 200 && data) {
    distributeListInfo.list = data.records.map((item: any) => ({ ...item, originSalePrices: [...new Set(item.salePrices)].sort() }))
    distributeListInfo.total = data.total
    console.log(data)
  } else {
    ElMessage.error('获取分销商品列表失败')
  }
}

/**
 * 重置分销表单
 */
const resetHandle = () => {
  distributeListInfo.productName = ''
  initDisComList()
}

//  * 分销状态
//  * 0-->分销中
//  * 1-->取消
function distributionStatus(distributionStatus: string | undefined) {
  if (distributionStatus) {
    if (distributionStatus === 'IN_DISTRIBUTION') {
      return '分销中'
    } else {
      return '取消分销'
    }
  }
}
function itemStatus(status: string | undefined) {
  if (status) {
    if (status === 'REFUSE') {
      return '已拒绝'
    } else if (status === 'UNDER_REVIEW') {
      return '审核中'
    } else if (status === 'SELL_OFF') {
      return '下架'
    } else if (status === 'SELL_ON') {
      return '上架'
    } else if (status === 'SELL_OUT') {
      return '已售完'
    } else if (status === 'PLATFORM_SELL_OFF') {
      return '违规下架'
    } else {
      return '店铺不可用'
    }
  }
}
/**
 * 获取平台分销配置
 */
async function initPlatformDisConfig() {
  const { code, data } = await doGetDistributeConfig()
  if (code === 200) {
    platformLevel.value = data.level
    if (data.one || data.two || data.three) {
      data.one = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.one)) : String(divHundred(data.one))
      data.two = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.two)) : String(divHundred(data.two))
      data.three = data.shareType === 'FIXED_AMOUNT' ? String(divTenThousand(data.three)) : String(divHundred(data.three))
      platforminfo.value = data
    }
  } else {
    ElMessage.error('获取分销配置失败')
  }
}
const expectPrice = (salePrices: Decimal) => {
  if (distributeForm.shareType !== 'UNIFIED') {
    if (distributeForm.shareType === 'RATE') {
      if (platformLevel.value === 'ONE') {
        return salePrices.mul(divHundred(distributeForm.one)).toFixed(2)
      } else if (platformLevel.value === 'TWO') {
        return salePrices
          .mul(divHundred(distributeForm.one))
          .add(salePrices.mul(divHundred(distributeForm.two)))
          .toFixed(2)
      } else {
        return salePrices
          .mul(divHundred(distributeForm.one))
          .add(salePrices.mul(divHundred(distributeForm.two)))
          .add(salePrices.mul(divHundred(distributeForm.three)))
          .toFixed(2)
      }
    } else if (platformLevel.value === 'ONE') {
      return new Decimal(distributeForm.one!).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return new Decimal(distributeForm.one!).add(distributeForm.two!).toFixed(2)
    } else {
      return new Decimal(distributeForm.one!).add(distributeForm.two!).add(distributeForm.three!).toFixed(2)
    }
  } else if (platforminfo.value.shareType === 'RATE') {
    if (platformLevel.value === 'ONE') {
      return salePrices.mul(divHundred(platforminfo.value.one)).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .toFixed(2)
    } else {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .add(salePrices.mul(divHundred(platforminfo.value.three)))
        .toFixed(2)
    }
  } else if (platformLevel.value === 'ONE') {
    return new Decimal(platforminfo.value.one).toFixed(2)
  } else if (platformLevel.value === 'TWO') {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).toFixed(2)
  } else {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).add(platforminfo.value.three).toFixed(2)
  }
}
const TableexpectPrice = (salePrices: Decimal, row: ApiDistributeComItem) => {
  if (row.shareType !== 'UNIFIED') {
    if (row.shareType === 'RATE') {
      if (platformLevel.value === 'ONE') {
        return salePrices.mul(divTenThousand(divHundred(row.one))).toFixed(2)
      } else if (platformLevel.value === 'TWO') {
        return salePrices
          .mul(divTenThousand(divHundred(row.one)))
          .add(salePrices.mul(divHundred(divHundred(row.two))))
          .toFixed(2)
      } else {
        return salePrices
          .mul(divTenThousand(divHundred(row.one)))
          .add(salePrices.mul(divTenThousand(divHundred(row.two))))
          .add(salePrices.mul(divTenThousand(divHundred(row.three))))
          .toFixed(2)
      }
    } else if (platformLevel.value === 'ONE') {
      return divTenThousand(row.one).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return divTenThousand(row.one).add(divTenThousand(row.two)).toFixed(2)
    } else {
      return divTenThousand(row.one).add(divTenThousand(row.two)).add(divTenThousand(row.three)).toFixed(2)
    }
  } else if (platforminfo.value.shareType === 'RATE') {
    if (platformLevel.value === 'ONE') {
      return salePrices.mul(divHundred(platforminfo.value.one)).toFixed(2)
    } else if (platformLevel.value === 'TWO') {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .toFixed(2)
    } else {
      return salePrices
        .mul(divHundred(platforminfo.value.one))
        .add(salePrices.mul(divHundred(platforminfo.value.two)))
        .add(salePrices.mul(divHundred(platforminfo.value.three)))
        .toFixed(2)
    }
  } else if (platformLevel.value === 'ONE') {
    return new Decimal(platforminfo.value.one).toFixed(2)
  } else if (platformLevel.value === 'TWO') {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).toFixed(2)
  } else {
    return new Decimal(platforminfo.value.one).add(platforminfo.value.two).add(platforminfo.value.three).toFixed(2)
  }
}
function name(shareType: string, priceMin: any, priceMax?: any, percentage1?: any, percentage2?: any, percentage3?: any) {
  if (shareType === 'FIXED_AMOUNT') {
    percentage1 = +divTenThousand(percentage1)
    percentage2 = +divTenThousand(percentage2)
    percentage3 = +divTenThousand(percentage3)
    const totalMin = (percentage1 + percentage2 + percentage3).toFixed(2)
    return `${percentage1} + ${percentage2} + ${percentage3} = ${totalMin}`
  } else {
    priceMin = +divTenThousand(priceMin)
    priceMax = +divTenThousand(priceMax)
    percentage1 = +divHundred(divTenThousand(percentage1))
    percentage2 = +divHundred(divTenThousand(percentage2))
    percentage3 = +divHundred(divTenThousand(percentage3))
    if (!percentage3) {
      const totalMax = (priceMax * percentage1 + priceMax * percentage2).toFixed(2)
      const totalMin = (priceMin * percentage1 + priceMin * percentage2).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} = ${totalMax}`
    } else if (!percentage2) {
      const totalMax = (priceMax * percentage1).toFixed(2)
      const totalMin = (priceMin * percentage1).toFixed(2)
      if (priceMin === priceMax) {
        return priceMin + ' * ' + percentage1 + '   = ' + totalMin
      }
      return `${priceMin} * ${percentage1} = ${totalMin} </br> ${priceMax} * ${percentage1} = ${totalMax}`
    } else {
      const totalMax = (priceMax * percentage1 + priceMax * percentage2 + priceMax * percentage3).toFixed(2)
      const totalMin = (priceMin * percentage1 + priceMin * percentage2 + priceMin * percentage3).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} + ${priceMax} * ${percentage3} = ${totalMax}`
    }
  }
}

const nameTan = computed(() => (shareType: string, priceMin: any, priceMax?: any, percentage1?: any, percentage2?: any, percentage3?: any) => {
  if (shareType === 'FIXED_AMOUNT') {
    const totalMin = +percentage1 + +percentage2 + +percentage3
    return `${percentage1} + ${percentage2} + ${percentage3} = ${totalMin}`
  } else {
    priceMin = +divTenThousand(priceMin)
    priceMax = +divTenThousand(priceMax)
    if (distributeForm.shareType === 'RATE' || (distributeForm.shareType === 'UNIFIED' && platforminfo.value.shareType !== 'FIXED_AMOUNT')) {
      percentage1 = +divHundred(percentage1)
      percentage2 = +divHundred(percentage2)
      percentage3 = +divHundred(percentage3)
    }
    if (!percentage3) {
      const totalMax = (priceMax * percentage1 + priceMax * percentage2).toFixed(2)
      const totalMin = (priceMin * percentage1 + priceMin * percentage2).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} = ${totalMax}`
    } else if (!percentage2) {
      const totalMax = (priceMax * percentage1).toFixed(2)
      const totalMin = (priceMin * percentage1).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} = ${totalMin} </br> ${priceMax} * ${percentage1} = ${totalMax}`
    } else {
      const totalMax = +(priceMax * percentage1 + priceMax * percentage2 + priceMax * percentage3).toFixed(2)
      const totalMin = +(priceMin * percentage1 + priceMin * percentage2 + priceMin * percentage3).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} + ${priceMax} * ${percentage3} = ${totalMax}`
    }
  }
})

const chooseGoodsPopup = ref(false)
const handleConfirm = (data: ChoosedGoodCallBack) => {
  console.log(data)
  addGoodsList.value = data.tempGoods
  const arr = data.tempGoods.map((item) => {
    return item.productId
  })
  distributeForm.productIds = [...distributeForm.productIds, ...arr]
}
</script>

<template>
  <SchemaForms v-model="distributeListInfo" :columns="columns" @searchHandle="initDisComList" @handleReset="resetHandle" />
  <el-tabs v-model="distributeListInfo.distributionStatus" class="tab_container" @tab-change="handleChangeTab">
    <el-tab-pane label="全部" name="ALL" />
    <el-tab-pane label="分销中" name="IN_DISTRIBUTION" />
    <el-tab-pane label="取消分销" name="CANCEL_DISTRIBUTION" />
  </el-tabs>
  <div class="handle_container">
    <el-button type="primary" @click="handleAddCom">新增商品</el-button>
    <el-button v-show="distributeListInfo.distributionStatus !== 'CANCEL_DISTRIBUTION'" @click="handleBatchDelDisListCom">批量取消分销</el-button>
  </div>
  <div class="table_container">
    <el-table
      :data="distributeListInfo.list"
      :header-cell-style="{ color: '#333', fontSize: '14px', background: '#F6F8FA', height: '48px' }"
      class="table-height-fit"
      @selection-change="handleTableSelect"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="商品信息" width="400">
        <template #default="scope">
          <div class="com f12 color51">
            <el-image class="com__img" :src="scope.row.pic" />
            <div class="com__right">
              <el-tooltip v-if="scope.row.name.length > 19" effect="dark" :content="scope.row.name" placement="top">
                <div class="com__right--name">
                  <span v-if="scope.row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                  {{ scope.row.name }}
                </div>
              </el-tooltip>
              <div v-else class="com__right--name">
                <span v-if="scope.row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ scope.row.name }}
              </div>
              <div v-if="scope.row.originSalePrices.length > 1">
                ￥{{ divTenThousand(scope.row.salePrices[0]).toFixed(2) }} ~ ￥{{
                  divTenThousand(scope.row.salePrices[scope.row.salePrices.length - 1]).toFixed(2)
                }}
              </div>
              <div v-else>￥{{ divTenThousand(scope.row.salePrices[0]).toFixed(2) }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="140">
        <template #header>
          <div>
            <span>库存</span>
            <!-- <QIcon name="icon-path" size="12px" style="margin-left: 5px; cursor: pointer" @clicl="sortTableList('总库存')" /> -->
          </div>
        </template>
        <template #default="scope"
          ><span class="f12 color51">
            {{ scope.row.stock }}
          </span></template
        >
      </el-table-column>

      <el-table-column label="分佣参数" width="140">
        <template #default="scope">
          <div class="f12 color51 column">
            <div class="f12 color51">
              一级:<span
                :class="[
                  scope.row.shareType === 'UNIFIED'
                    ? platforminfo.shareType === 'FIXED_AMOUNT'
                      ? 'amount'
                      : 'percentage'
                    : scope.row.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage',
                ]"
                >{{ conversionCount(scope.row.shareType, scope.row.one) }}</span
              >
            </div>
            <div v-if="platformLevel !== 'ONE' && scope.row.two" class="f12 color51">
              二级:<span
                :class="[
                  scope.row.shareType === 'UNIFIED'
                    ? platforminfo.shareType === 'FIXED_AMOUNT'
                      ? 'amount'
                      : 'percentage'
                    : scope.row.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage',
                ]"
                >{{ conversionCount(scope.row.shareType, scope.row.two) }}</span
              >
            </div>
            <div v-if="platformLevel === 'THREE' && scope.row.three" class="f12 color51">
              三级:<span
                :class="[
                  scope.row.shareType === 'UNIFIED'
                    ? platforminfo.shareType === 'FIXED_AMOUNT'
                      ? 'amount'
                      : 'percentage'
                    : scope.row.shareType === 'FIXED_AMOUNT'
                    ? 'amount'
                    : 'percentage',
                ]"
                >{{ conversionCount(scope.row.shareType, scope.row.three) }}</span
              >
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品状态" width="140">
        <template #default="{ row }: { row: ApiDistributeComItem }"
          ><span
            class="f12"
            :style="{
              color: ['SELL_ON'].includes(row.status) ? '#333' : ['SELL_OFF'].includes(row.status) ? '#999' : '#F54319',
            }"
            >{{ itemStatus(row.status) }}</span
          ></template
        >
      </el-table-column>
      <el-table-column label="分销状态" width="140">
        <template #default="{ row }: { row: ApiDistributeComItem }"
          ><span class="f12" :style="{ color: ['IN_DISTRIBUTION'].includes(row.distributionStatus) ? '#333' : '#999' }">{{
            distributionStatus(row.distributionStatus)
          }}</span></template
        >
      </el-table-column>
      <el-table-column label="分销佣金" width="210">
        <template #default="scope">
          <div style="display: flex">
            <div
              v-if="scope.row.salePrices[0] !== scope.row.salePrices[scope.row.salePrices.length - 1] && scope.row.salePrices.length > 1"
              placement="bottom"
              effect="light"
              class="colorRed"
            >
              <span>￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[0]), scope.row) }}</span>
              ~
              <span>￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[scope.row.salePrices.length - 1]), scope.row) }}</span>
            </div>
            <div v-else class="f12 colorRed">￥{{ TableexpectPrice(divTenThousand(scope.row.salePrices[0]), scope.row) }}</div>
            <el-tooltip
              :raw-content="true"
              :content="
                name(
                  scope.row.shareType,
                  scope.row.salePrices[0],
                  scope.row.salePrices[scope.row.salePrices.length - 1],
                  scope.row.one,
                  scope.row?.two,
                  scope.row?.three,
                )
              "
              placement="bottom"
              effect="light"
            >
              <QIcon class="cup" name="icon-wenhao" size="18px" style="cursor: pointer; margin-left: 5px" color="rgba(77, 77, 77, 0.32)" />
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="加入分销时间" width="160">
        <template #header>
          <div>
            <span>加入分销时间</span>
            <!-- <QIcon name="icon-path" size="12px" style="margin-left: 5px; cursor: pointer" @clicl="sortTableList('加入分销时间')" /> -->
          </div>
        </template>
        <template #default="scope"
          ><span class="f12 color51">{{ scope.row.createTime }}</span></template
        >
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="right">
        <template #default="{ row }">
          <!-- distributionStatus -->
          <!-- <el-button v-if="row.status === 'FORBIDDEN'" link type="primary" class="f12" @click="handleSeeDisCom(row)">查看</el-button> -->
          <el-button v-if="row.status !== 'FORBIDDEN' && row.canEdit" link type="primary" class="f12" @click="handleEditDisCom(row)">编辑</el-button>
          <el-button
            v-if="(row.status === 'SELL_ON' || row.status === 'UNUSABLE') && row.distributionStatus === 'CANCEL_DISTRIBUTION'"
            link
            :disabled="row.stock === 0"
            type="primary"
            class="f12"
            @click="doDelDistributeAgainFn(row)"
            >重新分销</el-button
          >
          <el-button
            v-if="row.distributionStatus === 'IN_DISTRIBUTION' && row.status === 'SELL_ON'"
            link
            type="primary"
            class="f12 colorRed"
            @click="handleDelDisListCom(row.id)"
            >取消分销</el-button
          >
          <el-button
            v-if="
              ((row.status === 'SELL_OFF' || row.status === 'PLATFORM_SELL_OFF' || row.status === 'SELL_OUT' || row.status === 'UNUSABLE') &&
                row.distributionStatus === 'CANCEL_DISTRIBUTION') ||
              row.distributionStatus === 'CANCEL_DISTRIBUTION'
            "
            style="color: red"
            link
            type="primary"
            class="f12"
            @click="handleEditDisdel(row.id)"
            >移除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
  <page-manage
    :page-size="distributeListInfo.size"
    :page-num="distributeListInfo.current"
    :total="distributeListInfo.total"
    @handle-current-change="handleChangeListCurrent"
    @handle-size-change="handleChangeListSize"
  />

  <!-- 弹窗 新增商品 -->
  <el-dialog v-model="showDialog" :title="getDialogTitle" width="900px" @close="handleCloseDisDialog">
    <el-form ref="formRef" :model="distributeForm" :rules="rules">
      <el-form-item label="分销类型" style="position: relative">
        <el-radio-group v-model="distributeForm.shareType">
          <el-radio value="UNIFIED" :disabled="titleType === 'see'">与平台一致</el-radio>
          <el-radio value="FIXED_AMOUNT" :disabled="titleType === 'see'">固定金额</el-radio>
          <el-radio value="RATE" :disabled="titleType === 'see'">百分比</el-radio>
        </el-radio-group>
        <el-tooltip class="box-item" effect="dark" placement="top">
          <template #content>
            <div>
              1、一级佣金 > 二级佣金 > 三级佣金；且 一级佣金 >= 0 <br />
              2、按百分比：佣金 = 商品实付金额 * 购买商品数 * 百分比<br />
              3、按固定金额：佣金 = 固定金额 * 购买商品数<br />
              4、风险提示：请谨慎使用固定金额分佣，可能存在资金损失风险
            </div>
          </template>
          <i class="iconfont icon-wenhao" style="color: #999; font-size: 16px; cursor: pointer; position: absolute; right: 0px"></i>
        </el-tooltip>
      </el-form-item>
      <div style="display: flex; gap: 20px">
        <el-form-item v-if="distributeForm.shareType !== 'UNIFIED'" label="一级佣金" prop="one">
          <el-input
            v-model="distributeForm.one"
            :min="0.01"
            :max="distributeForm.shareType === 'FIXED_AMOUNT' ? 10000 : 100"
            type="number"
            style="width: 200px"
            :disabled="titleType === 'see'"
          >
            <template #append>{{ distributeForm.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item
          v-if="distributeForm.shareType !== 'UNIFIED' && (platformLevel === 'TWO' || platformLevel === 'THREE')"
          label="二级佣金"
          prop="two"
        >
          <el-input v-model="distributeForm.two" type="number" style="width: 200px" :disabled="titleType === 'see'">
            <template #append>{{ distributeForm.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="distributeForm.shareType !== 'UNIFIED' && platformLevel === 'THREE'" label="三级佣金" prop="three">
          <el-input v-model="distributeForm.three" type="number" style="width: 200px" :disabled="titleType === 'see'">
            <template #append>{{ distributeForm.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="distributeForm.shareType === 'UNIFIED'" label="一级佣金" prop="one">
          <el-input
            v-model="platforminfo.one"
            :min="0.01"
            :max="platforminfo.shareType === 'FIXED_AMOUNT' ? 10000 : 100"
            type="number"
            style="width: 200px"
            :disabled="true"
          >
            <template #append>{{ platforminfo.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item
          v-if="distributeForm.shareType === 'UNIFIED' && (platformLevel === 'TWO' || platformLevel === 'THREE')"
          label="二级佣金"
          prop="two"
        >
          <el-input v-model="platforminfo.two" type="number" style="width: 200px" :disabled="true">
            <template #append>{{ platforminfo.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="distributeForm.shareType === 'UNIFIED' && platformLevel === 'THREE'" label="三级佣金" prop="three">
          <el-input v-model="platforminfo.three" type="number" style="width: 200px" :disabled="true">
            <template #append>{{ platforminfo.shareType === 'FIXED_AMOUNT' ? '元' : '%' }}</template>
          </el-input>
        </el-form-item>
      </div>
      <el-form-item v-if="titleType !== 'edit'" label="关联商品" prop="productId">
        <template #default>
          <el-button type="primary" round @click="handleChooseCom">选择商品</el-button>
        </template>
      </el-form-item>
    </el-form>
    <el-table v-if="titleType !== 'edit'" height="370" :data="addGoodsList">
      <el-table-column label="商品信息">
        <template #default="{ row }: { row: ApiRetrieveComItemType }">
          <div class="flex">
            <div class="tableCom flex">
              <el-image class="tableCom__img" :src="row.pic" fit="cover" style="width: 50px; height: 50px" />
              <div>
                <div class="f12 color51">{{ row.productName }}</div>
                <div v-if="row.salePrices[0] !== row.salePrices[row.salePrices.length - 1] && row.salePrices.length > 1" class="f12 colorRed">
                  <span>￥{{ divTenThousand(row.salePrices[0]) }}</span>
                  ~
                  <span>￥{{ divTenThousand(row.salePrices[row.salePrices.length - 1]) }}</span>
                </div>
                <div v-else class="f12 colorRed">￥{{ divTenThousand(row.salePrices[0]) }}</div>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分销佣金" align="center" width="200">
        <template #default="{ row }: { row: ApiRetrieveComItemType }">
          <div v-if="row.salePrices[0] !== row.salePrices[row.salePrices.length - 1] && row.salePrices.length > 1" class="f12 colorRed">
            <span>￥{{ expectPrice(divTenThousand(row.salePrices[0])) }}</span>
            ~
            <span>￥{{ expectPrice(divTenThousand(row.salePrices[row.salePrices.length - 1])) }}</span>
          </div>
          <div v-else class="f12 colorRed">￥{{ expectPrice(divTenThousand(row.salePrices[0])) }}</div>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="80">
        <template #default="{ row }: { row: ApiRetrieveComItemType }">
          <div class="f12 colorRed" style="cursor: pointer" @click="removegoodsItem(row.productId)">移出</div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="showDialog = false">取消</el-button>
      <el-button type="primary" @click="handleDisConfirm">确定</el-button>
    </template>
  </el-dialog>
  <!-- 选择商品 -->
  <!-- 选择商品弹出 s-->
  <QChooseGoodsPopup
    ref="QChooseGoodsPopupRef"
    v-model="chooseGoodsPopup"
    :searchConsignmentProduct="true"
    distributeProduct
    :pointGoodsList="addGoodsList"
    @onConfirm="handleConfirm"
  />
  <!-- 选择商品弹出 e-->
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
.table-height-fit {
  height: calc(100vh - 370px);
  overflow: auto;
}
@include b(dis) {
  padding: 0;
  @include e(header) {
    @include flex(space-between);
    margin-bottom: 14px;
  }
}
@include b(com) {
  color: #515151;
  display: flex;
  @include e(img) {
    width: 68px;
    height: 68px;
    margin-right: 12px;
  }
  @include e(right) {
    height: 68px;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    @include m(name) {
      line-height: normal;
      width: 217px;
      @include utils-ellipsis(2);
    }
  }
}
@include b(dialogCom) {
  @include flex();
  @include e(img) {
    width: 60px;
    height: 60px;
    margin-right: 12px;
    border-radius: 10px;
  }
  @include e(right) {
    @include m(name) {
      width: 210px;
      font-size: 14px;
      color: #333;
      line-height: 20px;
    }
  }
}
@include b(tableCom) {
  @include e(img) {
    width: 36px;
    height: 36px;
    margin-right: 12px;
    border-radius: 10px;
  }
}
.color51 {
  color: #515151;
}
.colorRed {
  color: #fd0505;
}
.color33 {
  color: #333;
}
.column {
  display: flex;
  flex-direction: column;
  align-items: start;
  justify-content: center;
  & button {
    margin: 0;
  }
}
.flex {
  @include flex(flex-start);
}
.amount {
  &::before {
    content: '￥';
    display: inline-block;
  }
}
.percentage {
  &::after {
    content: '%';
    display: inline-block;
  }
}
.tiShi {
  color: #fd0505;
  font-size: 12px;
  margin: 10px 0 20px;
}
:deep(.el-button.is-link) {
  padding: 0;
}
:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
