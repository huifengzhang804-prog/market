import { get, post, put, del } from '@/apis/http'

// 获取物流地址列表
export const doGetLogisticsList = (data: any): Promise<any> => {
  return get({
    url: 'gruul-mall-shop/shop/logistics/address/list',
    params: data,
  })
}
// 根据id删除地址
export const delLogisticsById = (id: string) => {
  return del({
    url: `gruul-mall-shop/shop/logistics/address/del/${id}`,
  })
}
//修改或者新增地址
export const setLogisticsDddress = (data: any) => {
  return post({
    url: 'gruul-mall-shop/shop/logistics/address/set',
    data,
  })
}
/**
 * 获取物流公司列表
 */
export const doGetLogisticsCompany = (): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/fright/list',
    params: {
      current: 1,
      size: 1000,
    },
  })
}
/**
 * 获取物流服务列表
 */
export const doGetLogisticsCompanyByShopIdList = () => {
  return get({
    url: 'gruul-mall-freight/fright/by/shopId/list',
  })
}
/**
 * 获取物流服务收货地址
 */
export const doGetDeliveryAddress = (): Promise<any> => {
  return get({
    url: 'gruul-mall-shop/shop/logistics/address/list',
    params: {
      current: 1,
      size: 1000,
    },
  })
}
/**
 * 查询可用的物流服务
 */
export const doGetLogisticsExpressUsableList = (params: any): Promise<any> => {
  return get({
    url: `gruul-mall-freight/logistics/express/usable/list`,
    params,
  })
}
/**
 * 新增物流服务
 */
export const doSaveLogisticsServe = (data: any) => {
  return post({
    url: 'gruul-mall-freight/logistics/express/save',
    data,
  })
}
/**
 * 获取物流服务列表
 */
export const doGetLogisticServeList = (current: number, size: number): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/logistics/express/page',
    params: {
      current,
      size,
    },
  })
}
/**
 * 修改物流服务
 */
export const doUpdateLogisticServe = (data: any) => {
  return post({
    url: 'gruul-mall-freight/logistics/express/update',
    data,
  })
}
/**
 * 删除物流服务
 */
export const doDelLogisticsServe = (ids: string) => {
  return del({
    url: `gruul-mall-freight/logistics/express/del/${ids}`,
  })
}
/**
 * 设置默认收发货地址
 */
export const doLogisticsSetDef = (id, type) => {
  return put({ url: `gruul-mall-shop/shop/logistics/address/set/def/address/${id}/${type}` })
}

/**
 * 获取模板列表
 */
export const doGetLogisticsTemplateList = (current: number, size: number): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/logistics/template/get/list',
    params: {
      current,
      size,
    },
  })
}
/**
 * 添加物流模板
 */
export const doAddLogisticsTemplate = (data) => {
  return post({
    url: 'gruul-mall-freight/logistics/template/save/info',
    data,
  })
}
/**
 * 删除物流模板
 */
export const doDelLogisticsTemplate = (id) => {
  return del({
    url: `gruul-mall-freight/logistics/template/delete/info/${id}`,
  })
}
/**
 * 获取单个物流模板信息
 */
export const doGetLogisticsTemplateInfoById = (id): Promise<any> => {
  return get({
    url: `gruul-mall-freight/logistics/template/get/info`,
    params: { id },
  })
}

/**
 * 编辑单个物流模板信息
 */
export const doPutLogisticsTemplateInfoById = (data: any) => {
  return post({
    url: `gruul-mall-freight/logistics/template/update/info`,
    data,
  })
}

/**
 * 快递设置新增/修改
 */
export const doCourierUpdateAndEdit = (customer: string, key: string, secret: string, id?: string) => {
  return post({ url: 'gruul-mall-freight/logistics/settings/edit', data: { customer, id, key, secret } })
}
/**
 * 快递设置信息获取
 */
export const doGetCourierInfo = (): Promise<any> => {
  return get({ url: 'gruul-mall-freight/logistics/settings/get' })
}

/**
 * 打印机信息新增
 */
export const doAddPrint = (deviceNo: string, printName: string) => {
  return post({
    url: 'gruul-mall-freight/logistics/print/save',
    data: { deviceNo, printName },
  })
}
/**
 * 打印机信息修改
 */
export const doUpdatePrint = (deviceNo: string, printName: string, id: string) => {
  return post({
    url: 'gruul-mall-freight/logistics/print/update',
    data: { deviceNo, printName, id },
  })
}
/**
 * 打印机列表
 */
export const doGetPrintList = (params: any): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/logistics/print/list',
    params,
  })
}
/**
 * 打印机删除
 */
export const doDelPrintById = (id: string) => {
  return del({
    url: `gruul-mall-freight/logistics/print/del/${id}`,
  })
}
