type ApiEnumLogisticCompany = 'id' | 'logisticsCompanyCode' | 'logisticsCompanyName' | 'logisticsCompanyStatus'
type ApiEnumAddress = 'address' | 'cityCode' | 'contactName' | 'contactPhone' | 'id' | 'provinceCode' | 'regionCode' | 'shopId'
export type ApiLogisticCompany = Record<ApiEnumLogisticCompany, string>
export type ApiAddress = Record<ApiEnumAddress, string>
export type ApiServeList = ApiLogisticCompany & ApiAddress

/**
 * @description: 地址管理类型
 * @param {number} current 当前页
 */
export interface ApiAddressList {
    current: number
    records: ApiAddressItem[]
    size: number
    total: number
}
/**
 * @description: 地址管理列表item
 * @param {string} provinceCode 省编码
 * @param {string} regionCode  区编码
 * @param {string} defReceive 退货地址选中状态
 * @param {string} defReceive 送货地址选中状态
 * @param {string}  shopId 分店编号
 */
export interface ApiAddressItem {
    address: string
    cityCode: string
    contactName: string
    contactPhone: string
    defReceive: string
    defSend: string
    deleted: false
    id: string
    provinceCode: string
    regionCode: string
    shopId: string
    area: string[]
}
export interface newLogisticsFormType {
    id?: string
    address: string
    cityCode?: string
    contactName: string
    contactPhone: string
    provinceCode?: string
    regionCode?: string
    zipCode: string
    defSend?: 'YES' | 'NO' | boolean
    defReceive?: 'YES' | 'NO' | boolean
    area?: string[]
}
export interface logisticsFormType {
    id?: string
    Provinces: string[]
    address: string
    contactName: string
    contactPhone: string
    zipCode: string
    defSend?: boolean
    defReceive?: boolean
}
export type filterCityDataType = { label: string; value: string; done?: boolean; children?: filterCityDataType }[]
export type cityItemType = { value: string; label: string; done?: boolean; children?: cityItemType[] }
/**
 * @description: 编辑回显时row类型支持
 * @param {string} address 地址
 * @param {string}  addressId 地址id
 * @param {string}  cityCode 城市编码
 * @param {string}  customerCod 客户号
 * @param {string}  customerPassword 客户密码
 * @param {string}  freightId 运费id
 * @param {string}  id 订单id
 * @param {string}  logisticsCompanyCode 物流公司code
 * @param {string}  logisticsCompanyName 物流公司名
 * @param {string}  logisticsCompanyStatus 物流公司启用状态
 * @param {string}  networkCode  网点编码
 * @param {string}  networkName 网点名
 * @param {string}  provinceCode 省编码
 * @param {string}  regionCode 区编码
 */
export interface EditRowType {
    address: string
    addressId: string
    cityCode: string
    customerCode: string
    customerPassword: string
    freightId: string
    id: string
    logisticsCompanyCode: string
    logisticsCompanyName: string
    logisticsCompanyStatus: string
    networkCode: string
    networkName: string
    provinceCode: string
    regionCode: string
}
/**
 * @description: 打印机数据
 * @param {string}  deviceNo  编码
 * @param {string}  id
 * @param {string}  printName
 */
export interface ApiPrint {
    deviceNo: string
    id: string
    printName: string
}
enum VALUATION_MODEL {
    'PKGS',
}
export interface FreightTemplateFormSubmitData {
    isEdit?: boolean
    choiceConditionPostage: number
    id?: number
    logisticsBaseModelDTO: {
        id: string
        firstAmount: number
        firstQuantity: number
        logisticsId: number
        regionJson: any[]
        secondAmount: number
        secondQuantity: number
        valuationModel: string
    }[]
    logisticsIncludePostageDTO?: {
        id: string
        amountNum: 0 //数量
        logisticsId: 0 // 物流编号
        pieceNum: 0 //件数
        postType: string
        region: any[] //地区
        weight: 0 //重量
    }[]
    templateName: string
    valuationModel: string
}
/**
 * @description: 后端返回模板列表信息
 * @returns {*}
 */
export interface ApiFreightTemplate {
    choiceConditionPostage: number
    id: string
    logisticsBaseModelVos: LogisticsBaseModelVos[]
    logisticsIncludePostageVos: LogisticsIncludePostageVos[]
    templateName: string
    valuationModel: string
}
export interface LogisticsBaseModelVos {
    firstAmount: number
    firstQuantity: number
    id: number
    logisticsId: number
    region: Region[]
    secondAmount: number
    secondQuantity: string
    valuationModel: string
}
export interface Region {
    upperCode: string
    upperName: string
    length: number
    lowerName: string[]
    lowerCode: string[]
}
interface LogisticsIncludePostageVos {
    amountNum: 0
    id: string
    logisticsId: string
    pieceNum: 20
    postType: string
    region: Region[]
    weight: 0
}
