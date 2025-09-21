import useConvert from './useConvert'
const { divTenThousand } = useConvert()
export default function usePriceRange() {
    return {
        range,
    }
}

function range(salePrices: string[] | string) {
    if (Array.isArray(salePrices)) {
        if (!salePrices.length) return '0'
        let priceArr: string[] = []
        salePrices.forEach((item) => {
            priceArr.push(String(divTenThousand(item)))
        })
        const delRepetPriceArr = Array.from(new Set(priceArr))
        return delRepetPriceArr.length > 1 ? `${delRepetPriceArr[0]}-${delRepetPriceArr[delRepetPriceArr.length - 1]}` : delRepetPriceArr[0]
    } else {
        return divTenThousand(salePrices)
    }
}
