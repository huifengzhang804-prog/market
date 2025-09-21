import type { Ref } from 'vue'
interface IntervalFnOptions {
    immediate?: boolean
    immediateCallback?: boolean
}
interface UseIntervalFn {
    pause: Fn
    resume: Fn
    clean: Fn
    isActive: Ref<boolean>
}
/**
 * 定时触发
 * @param cb 回调函数
 * @param interval 回调执行间隔
 * @param options 配置项
 */
function useSetInterval(cb: Fn, interval = 1000, options: IntervalFnOptions = {}): UseIntervalFn {
    const { immediate = true, immediateCallback = false } = options
    let timer: NodeJS.Timeout | null = null
    const isActive = ref(false)
    function clean() {
        if (timer) {
            clearInterval(timer)
            timer = null
        }
    }
    function pause() {
        isActive.value = false
        clean()
    }

    function resume() {
        if (interval <= 0) return
        isActive.value = true
        if (immediateCallback) cb()
        clean()
        timer = setInterval(cb, interval)
    }
    if (immediate) {
        resume()
    }
    return {
        isActive,
        pause,
        resume,
        clean,
    }
}

export default useSetInterval
