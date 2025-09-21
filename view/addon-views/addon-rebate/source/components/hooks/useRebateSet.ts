import { ElMessage } from 'element-plus'
import { doGetRebateConf, doPutRebateConf } from '../../apis'
import useConvert from '@/composables/useConvert'
import { ref, reactive, computed } from 'vue'

const { mulHundred, divHundred, mulTenThousand, divTenThousand } = useConvert()

const useRebateSet = () => {
  const paidMemberRebates = ref<RebateUser[]>([])
  const allMemberRebates = ref<RebateUser[]>([])
  const baseRebateConfig = reactive({
    id: '',
    rebateStatus: false,
    rebateUsers: 'PAID_MEMBER' as 'PAID_MEMBER' | 'ALL_MEMBERS',
  })
  const initialRebates = async () => {
    const result = await doGetRebateConf()
    if (result.code !== 200) {
      return ElMessage.error(result.msg || '获取设置失败')
    }
    const resultData: ResRebateConf = result.data
    baseRebateConfig.id = resultData?.id || ''
    baseRebateConfig.rebateStatus = resultData?.rebateStatus || false
    baseRebateConfig.rebateUsers = resultData.rebateUsers || 'PAID_MEMBER'
    paidMemberRebates.value = buildBasicPaidMemberRebates(resultData)
    allMemberRebates.value = buildBasicAllMemberRebates(resultData)
  }
  const currentMemberRebates = computed(() => (baseRebateConfig.rebateUsers === 'PAID_MEMBER' ? paidMemberRebates.value : allMemberRebates.value))
  const onSubmit = async () => {
    const res = {
      ...baseRebateConfig,
      payRebateUsers: paidMemberRebates.value?.map((paidMemberRebate) => ({
        ...paidMemberRebate,
        rebatePaymentPercentage: mulHundred(paidMemberRebate.rebatePaymentPercentage).toString(),
        rebatePercentage: mulHundred(paidMemberRebate.rebatePercentage).toString(),
        withdrawalThreshold: mulTenThousand(paidMemberRebate.withdrawalThreshold).toString(),
      })),
      allRebateUsers: allMemberRebates.value.map((allMemberRebate) => ({
        ...allMemberRebate,
        rebatePaymentPercentage: mulHundred(allMemberRebate.rebatePaymentPercentage).toString(),
        rebatePercentage: mulHundred(allMemberRebate.rebatePercentage).toString(),
        withdrawalThreshold: mulTenThousand(allMemberRebate.withdrawalThreshold).toString(),
      })),
    }
    const { code, data, msg } = await doPutRebateConf(res)
    if (code === 200) {
      ElMessage.success(msg || '修改成功')
      initialRebates()
    } else {
      ElMessage.error(msg || '修改失败')
    }
  }
  return {
    currentMemberRebates,
    initialRebates,
    baseRebateConfig,
    onSubmit,
  }
}

export default useRebateSet

const buildBasicPaidMemberRebates = (resultData: ResRebateConf) => {
  resultData.payRebateUsers.forEach((payRebateUser) => {
    payRebateUser.rebatePaymentPercentage = divHundred(payRebateUser.rebatePaymentPercentage).toNumber()
    payRebateUser.rebatePercentage = divHundred(payRebateUser.rebatePercentage).toNumber()
    payRebateUser.withdrawalThreshold = divTenThousand(payRebateUser.withdrawalThreshold).toNumber()
  })
  return resultData.payRebateUsers
}

const buildBasicAllMemberRebates = (resultData: ResRebateConf) => {
  resultData.allRebateUsers.forEach((allRebateUser) => {
    allRebateUser.rebatePaymentPercentage = divHundred(allRebateUser.rebatePaymentPercentage).toNumber()
    allRebateUser.rebatePercentage = divHundred(allRebateUser.rebatePercentage).toNumber()
    allRebateUser.withdrawalThreshold = divTenThousand(allRebateUser.withdrawalThreshold).toNumber()
  })
  return resultData.allRebateUsers
}

enum REBATE_USERS {
  PAID_MEMBER = 'PAID_MEMBER',
  FREE_MEMBER = 'FREE_MEMBER',
}
export interface RebateUser {
  id?: string
  memberType: keyof typeof REBATE_USERS
  memberName: string
  rankCode: number
  rebatePercentage: number
  rebatePaymentPercentage: number
  withdrawalThreshold: number
}

interface ResRebateConf {
  id: string
  deleted: boolean
  createTime: string
  version: number
  rebateStatus: boolean
  updateTime: string
  rebateUsers: 'PAID_MEMBER' | 'ALL_MEMBERS'
  allRebateUsers: RebateUser[]
  payRebateUsers: RebateUser[]
}
