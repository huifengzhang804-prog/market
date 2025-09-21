const { divTenThousand } = useConvert()
function range(salePrices: Long[] | Long) {
  if (Array.isArray(salePrices)) {
    if (!salePrices.length) return '0'
    let priceArr: string[] = []
    salePrices.forEach((item) => {
      priceArr.push(String(divTenThousand(item)))
    })
    const delRepetPriceArr = Array.from(new Set(priceArr))
    return delRepetPriceArr.length > 1 ? `${delRepetPriceArr[0]}-${delRepetPriceArr[delRepetPriceArr.length - 1]}` : delRepetPriceArr[0]
  } else {
    return salePrices
  }
}
function min(salePrices: Long[] | Long) {
  if (Array.isArray(salePrices)) {
    if (!salePrices.length) return '0'
    return Math.min(...salePrices.map((item) => divTenThousand(item).toNumber()))
  } else {
    return divTenThousand(salePrices)
  }
}
export default function usePriceRange() {
  return {
    range,
    min,
  }
}
