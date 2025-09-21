/**
 * 函数防抖 延迟执行
 * @param func
 * @param delay
 * @param isImmediately
 */
export  function  debounce(func:Function<void>, delay:number,isImmediately = false){
    let timer, flag = true;
    return (...args) => {
        // 需要立即执行的情况
        if (isImmediately){
            if (flag){
                func(...args);
                flag = false;
            }else {
                clearTimeout(timer);
                timer = setTimeout(() => {
                    func(...args);
                    flag = true
                }, delay)
            }
        }else {
            // 非立即执行的情况
            clearTimeout(timer);
            timer = setTimeout(() => {
                func(...args)
            }, delay)
        }
    }
}

/**
 * 函数节流  一定时间内只执行一次
 * @param func
 * @param delay
 * @param isImmediately
 */
export function throttle(func, delay, isImmediately = false){
    let flag = true;
    return (...args)=>{
        if(flag){
            flag = false
            isImmediately && func(...args)
            setTimeout(() => {
                !isImmediately && func(...args)
                flag = true
            },delay)
        }
    }
}