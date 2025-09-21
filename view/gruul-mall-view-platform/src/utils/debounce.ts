export default function debounce(func: FN, wait: number, immediate?: boolean) {
    if (typeof func !== 'function') throw new TypeError('func must be a function!')
    if (typeof wait === 'undefined') {
        wait = 2000
        immediate = false
    }
    if (typeof immediate === 'undefined') {
        immediate = false
    }
    if (typeof wait !== 'number') throw new TypeError('wait must be a number!')
    if (typeof immediate !== 'boolean') throw new TypeError('immediate must be a boolean!')
    let timer: NodeJS.Timeout | null = null,
        result: void
    return function proxy(this: Fn, ...params: any[]) {
        let self = this,
            callNow = !timer && immediate
        if (timer) clearTimeout(timer)
        timer = setTimeout(() => {
            if (timer) {
                clearTimeout(timer)
                timer = null
            }
            if (!immediate) result = func.apply(self, params)
        }, wait)
        if (callNow) result = func.apply(self, params)
        return result
    }
}
