import CalculateForCoupon from '@/pluginPackage/coupon/hooks/Calculate'
import CalculateForPlatformCoupon from '@/pluginPackage/coupon/hooks/calculatePlatform'
import CalculateForMember from '@pluginPackage/member/hooks/Calculate'
import type { ProductInfo } from '@plugin/types'
import type { AppPluginName } from '@/apis/sys/model'

type ProcessHandler = CalculateForMember | CalculateForCoupon | CalculateForPlatformCoupon

/**
 * @param name 优惠项名称
 * @param productInfoArr 商品数组(平台优惠)
 * @param rule 优惠规则
 * @param priority 权重
 */
export interface Process {
  name: AppPluginName
  productInfoArr?: ProductInfo[]
  rule: any
  priority: number
  handler?: ProcessHandler
}

export default class RegisterDisPlugin {
  private process: Process[] = []

  // 设置流程
  setProcess(process: Process) {
    this.process.push(this.initPlugin(process))
  }

  // 生成流程 什么玩意儿 防御性编程??????
  generateChain() {
    const sortProcess = this.sortProcess()
    if (sortProcess.length) {
      sortProcess.reduce((pre, cur) => {
        if (pre.handler) {
          // @ts-ignore
          return pre.handler.setNext(cur.handler)
        }
        // @ts-ignore
        return pre.setNext(cur.handler)
      })
      return sortProcess[0].handler
    }
    return null
  }

  // 流程排序
  sortProcess() {
    return this.process.sort((a, b) => {
      return a.priority - b.priority
    })
  }

  // 初始化优惠项
  initPlugin(process: Process) {
    switch (process.name) {
      // 店铺优惠券
      case 'addon-coupon':
        process.handler = new CalculateForCoupon(process)
        break
      // 平台优惠券
      case 'addon-platform-coupon':
        process.handler = new CalculateForPlatformCoupon(process)
        break
      case 'addon-member':
        process.handler = new CalculateForMember(process)
        break
      default:
        process.handler = new CalculateForMember(process)
        break
    }
    return process
  }
}
