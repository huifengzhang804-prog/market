export default function throttle(func: FN, wait: number) {
    if (typeof func !== 'function') throw new TypeError('func must be a function!')
    wait = +wait
    if (isNaN(wait)) wait = 1000
    let timer: NodeJS.Timeout | null = null,
        previous = 0,
        result: void
    return function proxy(this: Fn, ...params: any[]) {
        let now = +new Date(),
            remaining = wait - (now - previous),
            self = this
        if (remaining <= 0) {
            if (timer) {
                clearTimeout(timer)
                timer = null
            }
            previous = now
            result = func.apply(self, params)
            return result
        }
        if (!timer) {
            timer = setTimeout(() => {
                if (timer) {
                    clearTimeout(timer)
                    timer = null
                }
                previous = +new Date()
                result = func.apply(self, params)
            }, remaining)
        }
        return result
    }
}
