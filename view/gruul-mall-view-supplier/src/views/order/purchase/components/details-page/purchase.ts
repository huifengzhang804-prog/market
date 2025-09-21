export interface DeliveredPackage {
    createTime: string
    express: {
        expressCompanyCode: string
        expressCompanyName: string
        expressNo: string
    }
    id: string
    receiver: {
        address: string
        mobile: string
        name: string
    }
    packageStatus: 'WAITING_FOR_DELIVER' | 'WAITING_FOR_RECEIVE' | 'COMPLETED'
    type: 'PRINT_EXPRESS' | 'EXPRESS' | 'WITHOUT'
    orderItems: OrderItem[]
}

interface OrderItem {
    id: string
    image: string
    num: number
    productId: string
    productName: string
    specs: string[]
}
