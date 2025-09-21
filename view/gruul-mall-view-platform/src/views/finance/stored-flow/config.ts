enum STORED_FLOW_ENUM {
    '' = '全部',
    SYSTEM_GIFT = '系统赠送',
    USER_RECHARGE = '用户充值',
    SYSTEM_RECHARGE = '系统充值',
    SYSTEM_DEDUCTION = '系统扣除',
    SHOPPING_CONSUMPTION = '购物消费',
    PURCHASE_MEMBERSHIP = '购买会员',
    RENEWAL_MEMBERSHIP = '续费会员',
    UPGRADE_MEMBERSHIP = '升级会员',
    REFUND_SUCCESSFUL = '退款成功',
}

const storeFlowConfig: { value: keyof typeof STORED_FLOW_ENUM; label: string }[] = [
    { value: '', label: '全部' },
    { value: 'SYSTEM_GIFT', label: '充值赠送' },
    { value: 'USER_RECHARGE', label: '用户充值' },
    { value: 'SYSTEM_RECHARGE', label: '系统充值' },
    { value: 'SYSTEM_DEDUCTION', label: '系统扣除' },
    { value: 'SHOPPING_CONSUMPTION', label: '购物消费' },
    { value: 'PURCHASE_MEMBERSHIP', label: '购买会员' },
    { value: 'RENEWAL_MEMBERSHIP', label: '续费会员' },
    // { value: 'UPGRADE_MEMBERSHIP', label: '升级会员' },
    { value: 'REFUND_SUCCESSFUL', label: '退款成功' },
]
export const columns = [
    {
        label: '关联订单',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关联订单',
        },
    },
    {
        label: '手机号',
        labelWidth: 85,
        prop: 'userPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写申请人手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '操作类型',
        prop: 'operatorType',
        valueType: 'select',
        options: storeFlowConfig,
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '流水编号',
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入流水编号',
        },
    },
    {
        label: '用户昵称',
        prop: 'userNickName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入用户昵称',
        },
    },
    {
        label: '操作时间',
        prop: 'createTime',
        valueType: 'date-picker',
        fieldProps: {
            type: 'datetimerange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
    },
]
