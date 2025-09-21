import { doGetConsignmentSetting, doPostUpdateConsignmentSetting } from '../../../apis'
import useConvert from '@/composables/useConvert'
import { ElMessage, FormRules } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { reactive, ref } from 'vue'

const { divTenThousand, mulTenThousand } = useConvert()
const useConsignmentConfig = () => {
  const formModel = ref({
    type: 'RATE' as 'RATE' | 'REGULAR',
    sale: 0,
    scribe: 0,
  })
  const handleChangeType = () => {
    formModel.value.sale = 0
    formModel.value.scribe = 0
  }
  const formRef = ref<{
    validate: (callback?: (isValid: boolean) => void) => void
  }>()
  const rules: FormRules = {
    type: { required: true, message: '请选择代销类型', trigger: 'change' },
  }
  const handleSubmitSettingData = () => {
    formRef.value?.validate(async (isValid) => {
      if (isValid) {
        const cloneFormModel = cloneDeep(formModel.value)
        cloneFormModel.sale = mulTenThousand(formModel.value.sale).toNumber()
        cloneFormModel.scribe = mulTenThousand(formModel.value.scribe).toNumber()
        const { code, success, msg } = await doPostUpdateConsignmentSetting(cloneFormModel)
        if (code === 200 && success) {
          ElMessage.success({ message: msg || '更新代销设置信息成功' })
        } else {
          ElMessage.error({ message: msg || '更新代销设置信息失败' })
        }
      }
    })
  }
  const initialSettingsData = async () => {
    const { data, code } = await doGetConsignmentSetting()
    if (code === 200) {
      if (data) {
        formModel.value.sale = divTenThousand(data?.sale).toNumber()
        formModel.value.scribe = divTenThousand(data?.scribe).toNumber()
        formModel.value.type = data?.type
      }
    }
  }
  initialSettingsData()

  return {
    formRef,
    rules,
    formModel,
    handleChangeType,
    handleSubmitSettingData,
  }
}

export default useConsignmentConfig
