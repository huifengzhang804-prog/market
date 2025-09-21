import useConvert from '@/composables/useConvert'

const { divTenThousand, divHundred } = useConvert()
function useCalculationFormula(shareType: string, priceMin: any, priceMax?: any, percentage1?: any, percentage2?: any, percentage3?: any) {
  if (shareType === 'FIXED_AMOUNT') {
    percentage1 = +divTenThousand(percentage1)
    percentage2 = +divTenThousand(percentage2)
    percentage3 = +divTenThousand(percentage3)
    const totalMin = (percentage1 + percentage2 + percentage3).toFixed(2)
    return `${percentage1} + ${percentage2} + ${percentage3} = ${totalMin}`
  } else {
    priceMin = +divTenThousand(priceMin)
    priceMax = +divTenThousand(priceMax)
    percentage1 = +divHundred(divTenThousand(percentage1))
    percentage2 = +divHundred(divTenThousand(percentage2))
    percentage3 = +divHundred(divTenThousand(percentage3))
    if (!percentage3) {
      const totalMax = (priceMax * percentage1 + priceMax * percentage2).toFixed(2)
      const totalMin = (priceMin * percentage1 + priceMin * percentage2).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} = ${totalMax}`
    } else if (!percentage2) {
      const totalMax = (priceMax * percentage1).toFixed(2)
      const totalMin = (priceMin * percentage1).toFixed(2)
      if (priceMin === priceMax) {
        return priceMin + ' * ' + percentage1 + '   = ' + totalMin
      }
      return `${priceMin} * ${percentage1} = ${totalMin} </br> ${priceMax} * ${percentage1} = ${totalMax}`
    } else {
      const totalMax = (priceMax * percentage1 + priceMax * percentage2 + priceMax * percentage3).toFixed(2)
      const totalMin = (priceMin * percentage1 + priceMin * percentage2 + priceMin * percentage3).toFixed(2)
      if (priceMin === priceMax) {
        return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin}`
      }
      return `${priceMin} * ${percentage1} + ${priceMin} * ${percentage2} + ${priceMin} * ${percentage3} = ${totalMin} </br> ${priceMax} * ${percentage1} + ${priceMax} * ${percentage2} + ${priceMax} * ${percentage3} = ${totalMax}`
    }
  }
}
export default useCalculationFormula
