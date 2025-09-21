const toString = Object.prototype.toString

function is(val: unknown, type: string) {
    return toString.call(val) === `[object ${type}]`
}

export function isObject(val: any): val is Record<any, any> {
    return val !== null && is(val, 'Object')
}

export function isFunction(val: unknown): val is FN {
    return typeof val === 'function'
}
