enum goodStatus {
    REFUSE,
    UNDER_REVIEW,
    SELL_OFF,
    SELL_ON,
    SELL_OUT,
    PLATFORM_SELL_OFF,
    UNUSABLE,
}
const statusToChinese = {
    REFUSE: '已拒绝',
    UNDER_REVIEW: '审核中',
    SELL_OFF: '已下架',
    SELL_ON: '已上架',
    SELL_OUT: '已售完',
    PLATFORM_SELL_OFF: '违规下架',
    UNUSABLE: '店铺不可用',
    SUPPLIER_SELL_OFF: '供应商下架',
}
export function usePlatformGoodStatus(str: keyof typeof goodStatus) {
    return statusToChinese[str]
}
