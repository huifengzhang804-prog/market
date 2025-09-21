import Decimal from 'decimal.js'
// import CalculateHandler from '@pluginPackage/hooks/calculate'
// import type { ChainHandleParam } from '@plugin/types'
// import type { Process } from '@pluginPackage/hooks/registerDisPlugin'
import CalculateHandler from '../../hooks/CalculateHandler'
import { ChainHandleParam } from '../../types'
import { Process } from '@/views/settlement/hooks/RegisterDisPlugin'
import useConvert from '@/composables/useConvert'
import useMember from '@/composables/useMember'
export default class CalculateForMember extends CalculateHandler {
  private discountPrice = '0'
  constructor(process: Process) {
    super(process.productInfoArr || [])
  }
  public handle(params?: Partial<ChainHandleParam>) {
    const totalPrice = params?.totalPrice || this.totalPrice
    const { memberPrice } = useMember()
    const { mulTenThousand } = useConvert()
    this.discountPrice = new Decimal(totalPrice).sub(mulTenThousand(memberPrice(totalPrice))).toString()
    const disPrice = new Decimal(this.discountPrice)
    const resultPrice = Number(params?.discountPrice) ? disPrice.add(Number(params?.discountPrice)).toString() : disPrice.toString()
    const tempObj: ChainHandleParam = {
      shopId: '0',
      totalPrice,
      discountPrice: resultPrice,
      preferTreatment: Object.assign(params && params.preferTreatment ? params.preferTreatment : {}, {
        'addon-member': {
          discount: disPrice.toString(),
        },
      }),
    }
    return super.handle(Object.assign(params || {}, tempObj))
  }
}
