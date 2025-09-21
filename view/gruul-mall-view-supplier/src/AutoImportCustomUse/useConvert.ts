import Decimal from 'decimal.js'
type ConversionType = string | number | Decimal | undefined | null

/**
 * @description: 除一百
 */
function divHundred(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).div(100)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 除1000
 */
function divThousand(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).div(1000)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 除一万
 */
function divTenThousand(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).div(10000)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 乘100
 * @param {ConversionType} val
 */
function mulHundred(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).mul(100)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 乘1000
 * @param {ConversionType} val
 */
function mulThousand(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).mul(1000)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 除一百万
 */
function divTenThouMillion(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).div(1000000)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 乘10000
 * @param {ConversionType} val
 */
function mulTenThousand(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        return new Decimal(val).mul(10000)
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 保留两位小数向上取整去除尾数为0
 */
function fixedUp(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        const unRoundOff = new Decimal(val).toFixed(2, Decimal.ROUND_UP)
        return new Decimal(unRoundOff).toPrecision()
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 保留两位小数向下取整去除尾数为0
 */
function fixedDown(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        const unRoundOff = new Decimal(val).toFixed(2, Decimal.ROUND_DOWN)
        return new Decimal(unRoundOff).toPrecision()
    } else {
        return new Decimal('0')
    }
}
/**
 * @description: 将销量转为字符串
 */
function salesVolumeToStr(val: ConversionType) {
    if (val && typeof val !== 'undefined' && val !== null && Number(val) !== 0) {
        const NumberVal = Number(val)
        const k = 10000
        const sizes = ['', '万', '亿', '万亿']
        let i = 0
        if (NumberVal < k) {
            return NumberVal.toString()
        } else {
            i = Math.floor(Math.log(NumberVal) / Math.log(k))
            return `${parseFloat((NumberVal / Math.pow(k, i)).toFixed(2))}${sizes[i]}+`
        }
    } else {
        return '0'
    }
}

export default function useConvert() {
    return {
        divHundred,
        divThousand,
        divTenThousand,
        mulHundred,
        mulThousand,
        mulTenThousand,
        fixedUp,
        fixedDown,
        salesVolumeToStr,
        divTenThouMillion,
    }
}
