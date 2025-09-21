import Decimal from 'decimal.js'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()

const useListCalculation = () => {
  /**
   * @description 获取各级分销佣金的计算公式
   * @param itemInfo 当前子项数据
   * @param distributeLevel 层级
   * @param bonus 佣金结果
   * @returns
   */
  const useSingleCalculationFormula = (itemInfo: any, distributeLevel: 'one' | 'two' | 'three', bonus: Decimal, orderInfo: any) => {
    // const price = divTenThousand(itemInfo?.dealPrice * itemInfo?.num)
    return orderInfo
      .map(
        (info: any) =>
          `${info?.num}* ${divTenThousand(info?.bonusShare?.[distributeLevel])}${info.bonusShare?.shareType === 'RATE' ? '%' : ''}${
            info.bonusShare?.shareType === 'RATE' ? '*' + divTenThousand(info.dealPrice) : ''
          }`,
      )
      .join(' + ')
      .concat(` = ${bonus}`)
  }
  function getBonus(num: string, purchase: boolean, orderInfo: any) {
    let total = new Decimal(0)
    if ('items' in orderInfo && orderInfo.items.length) {
      orderInfo.items.forEach((item: any) => {
        if (purchase || item[num].userId) {
          total = total.add(divTenThousand(item[num].bonus || 0))
        }
      })
    }

    return total
  }
  /**
   * @description 获取总结的计算公式
   * @param itemInfo 订单数据
   * @returns
   */
  const useTotalPrice = (itemInfo: any) => {
    let completePrice: Decimal[] = []
    let closedPrice: Decimal[] = []
    let toSelledPrice: Decimal[] = []
    let totalPrice: Decimal[] = []
    const itemInfoItems: any[] = itemInfo.items
    itemInfoItems.forEach((item) => {
      let decimalPrice: Decimal[] = []
      if (item.orderStatus === 'PAID') {
        decimalPrice = toSelledPrice
      } else if (item.orderStatus === 'COMPLETED') {
        decimalPrice = completePrice
      } else if (item.orderStatus === 'CLOSED') {
        decimalPrice = closedPrice
      }
      if (item.one.bonus) {
        // totalPrice.push(item?.one?.userId ? divTenThousand(item.one.bonus) : new Decimal(0))
        decimalPrice.push(item?.one?.userId ? divTenThousand(item.one.bonus) : new Decimal(0))
      }
      if (item.two.bonus) {
        // totalPrice.push(item?.two?.userId ? divTenThousand(item.two.bonus) : new Decimal(0))
        decimalPrice.push(item?.two?.userId ? divTenThousand(item.two.bonus) : new Decimal(0))
      }
      if (item.three.bonus) {
        // totalPrice.push(item?.two?.userId ? divTenThousand(item.three.bonus) : new Decimal(0))
        decimalPrice.push(item?.two?.userId ? divTenThousand(item.three.bonus) : new Decimal(0))
      }
      if (item.purchase && item.one.bonus) {
        // totalPrice.push(divTenThousand(item.one.bonus))
        decimalPrice.push(divTenThousand(item.one.bonus))
      }
    })
    const itemInfoItem: any[] = [itemInfo.items[0]]
    itemInfoItem.forEach((item) => {
      if (item.one.bonus) {
        totalPrice.push(item?.one?.userId ? getBonus('one', itemInfo.items?.[0].purchase, itemInfo) : new Decimal(0))
      }
      if (item.two.bonus) {
        totalPrice.push(item?.two?.userId ? getBonus('two', itemInfo.items?.[0].purchase, itemInfo) : new Decimal(0))
      }
      if (item.three.bonus) {
        totalPrice.push(item?.two?.userId ? getBonus('three', itemInfo.items?.[0].purchase, itemInfo) : new Decimal(0))
      }
      if (item.purchase && item.one.bonus) {
        totalPrice.push(getBonus('one', itemInfo.items?.[0].purchase, itemInfo))
      }
    })
    return {
      completePrice: completePrice.join(' + ') + ' = ' + completePrice.reduce((pre, item) => pre.plus(item), new Decimal(0)),
      closedPrice: closedPrice.join(' + ') + ' = ' + closedPrice.reduce((pre, item) => pre.plus(item), new Decimal(0)),
      toSelledPrice: toSelledPrice.join(' + ') + ' = ' + toSelledPrice.reduce((pre, item) => pre.plus(item), new Decimal(0)),
      totalPrice: totalPrice.join(' + ') + ' = ' + totalPrice.reduce((pre, item) => pre.plus(item), new Decimal(0)),
    }
  }
  return {
    useSingleCalculationFormula,
    useTotalPrice,
  }
}

export default useListCalculation
