type ApiEnumLogisticCompany = 'id' | 'logisticsCompanyCode' | 'logisticsCompanyName' | 'logisticsCompanyStatus'
export type ApiLogisticCompany = Record<ApiEnumLogisticCompany, string>

export interface ApiPrint {
    deviceNo: string
    id: string
    printName: string
}
