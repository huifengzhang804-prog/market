import { AppPluginName, ChainHandleParam } from '../plugins/types'
import RegisterDisPlugin, { Process } from './RegisterDisPlugin'

const useCalculateHooks = () => {
  // 订单优惠项map
  const discountsMap = new Map()
  // 添加优惠项
  const addDiscount = (shopId: string, process: Process) => {
    if (discountsMap.has(shopId)) {
      if (isExist(shopId, process.name)) {
        editDiscount(shopId, process)
      } else {
        discountsMap.get(shopId).push(process)
      }
    } else {
      discountsMap.set(shopId, [process])
    }
  }
  // 删除优惠项
  const delDiscount = (shopId: string, disName: AppPluginName) => {
    const disArr = discountsMap.get(shopId) as Process[]
    if (!disArr || !disArr.length) return
    const filterArr = disArr.filter((item) => {
      return item.name !== disName
    })
    discountsMap.set(shopId, filterArr)
  }
  // 是否存在优惠项
  const isExist = (shopId: string, disName: AppPluginName) => {
    let returnVal = false
    if (discountsMap.size) {
      const processArr = discountsMap.get(shopId) as Process[]
      returnVal = processArr.some((item) => item.name === disName)
    }
    return returnVal
  }
  // 编辑优惠项
  const editDiscount = (shopId: string, process: Process) => {
    if (isExist(shopId, process.name)) {
      const processArr = discountsMap.get(shopId) as Process[]
      const replaceArr = processArr.map((item) => {
        if (item.name === process.name) {
          item = process
        }
        return item
      })
      discountsMap.set(shopId, replaceArr)
    }
  }
  // 生成计算链(由平台链与多个条商铺链组成)
  const resume = (shopId = '') => {
    const chainArr = []
    // 商品详情页只用计算当前商品的优惠
    if (shopId) {
      const newRegisterDisPlugin = new RegisterDisPlugin()
      const processArr = discountsMap.get(shopId) as Process[]
      processArr.forEach((item) => {
        newRegisterDisPlugin.setProcess(item)
      })
      chainArr.push(newRegisterDisPlugin.generateChain())
      return chainArr
    }
    // 购物车需要计算全部店铺的优惠
    const iterator = discountsMap.values()
    let size = discountsMap.size
    if (!size) return []
    while (size > 0) {
      const newRegisterDisPlugin = new RegisterDisPlugin()
      const processArr = iterator.next().value as Process[]
      processArr.forEach((item) => {
        newRegisterDisPlugin.setProcess(item)
      })
      chainArr.push(newRegisterDisPlugin.generateChain())
      size--
    }
    return chainArr
  }
  /**
   * @description: 激活计算
   */
  function activeResume(shopId = '') {
    // calResult数组为空直接计算所有商品价格
    const chain = resume(shopId)
    const calResult: ChainHandleParam[] = []
    if (chain && chain.length) {
      chain.forEach((item) => {
        const result = item?.handle()
        if (result) {
          calResult.push(result)
        }
      })
    }
    return calResult
  }
  return {
    addDiscount,
    delDiscount,
    resume,
    editDiscount,
    activeResume,
  }
}

export default useCalculateHooks
