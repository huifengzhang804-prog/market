import useConvert from './useConvert'

const { divTenThousand } = useConvert()

function range(salePrices: string[] | string) {
    if (Array.isArray(salePrices)) {
        if (!salePrices.length) return '0'
        let priceArr: string[] = []
        salePrices.forEach((item) => {
            priceArr.push(String(divTenThousand(item)))
        })
        const delRepetPriceArr = Array.from(new Set(priceArr))
        return delRepetPriceArr.length > 1
            ? `${parseFloat(delRepetPriceArr[0]).toFixed(2)}-${parseFloat(delRepetPriceArr[delRepetPriceArr.length - 1]).toFixed(2)}`
            : parseFloat(delRepetPriceArr[0]).toFixed(2)
    } else {
        return divTenThousand(salePrices).toFixed(2)
    }
}
export default function usePriceRange() {
    return {
        range,
    }
}
