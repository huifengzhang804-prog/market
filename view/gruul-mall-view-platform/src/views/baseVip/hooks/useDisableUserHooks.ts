import { doPutLimitPermission } from '@/apis/vip'
import { ElMessage } from 'element-plus'

const useDisableUserHooks = () => {
    const blackDialogFormModel = reactive({
        roles: [],
        explain: '',
    })

    const handleBlackConfirm = async (currentUserId: string[], callback: any = null) => {
        if (!blackDialogFormModel.roles.length) {
            ElMessage.warning('至少选择一项权限限制')
            return
        }
        if (!blackDialogFormModel.explain) {
            ElMessage.warning('请填写拉黑原因')
            return
        }
        const { code, msg } = await doPutLimitPermission(currentUserId, blackDialogFormModel.roles, blackDialogFormModel.explain)
        if (code === 200) {
            ElMessage.success('操作成功')
            callback?.()
        } else {
            ElMessage.error(msg || '操作失败')
        }
    }

    return {
        blackDialogFormModel,
        handleBlackConfirm,
    }
}

export default useDisableUserHooks
