import { isObject } from './is'
export function deepMerge(src: any = {}, target: any = {}) {
    let key: string
    for (key in target) {
        src[key] = isObject(target[key]) ? deepMerge(src[key], target[key]) : (src[key] = target[key])
    }
    return src
}
