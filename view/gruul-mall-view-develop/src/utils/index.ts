/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-04-15 13:44:39
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-04-15 14:09:57
 */
export const debounce = function debounce(
  func: Function,
  wait: undefined | boolean | number,
  immediate:boolean
) {
  if (typeof func !== "function")
    throw new TypeError("func must be a function!");
  if (typeof wait === "undefined") {
    wait = 500;
    immediate = false;
  }
  if (typeof wait === "boolean") {
    immediate = wait;
    wait = 500;
  }
  if (typeof immediate === "undefined") {
    immediate = false;
  }
  if (typeof wait !== "number") throw new TypeError("wait must be a number!");
  if (typeof immediate !== "boolean")
    throw new TypeError("immediate must be a boolean!");
  let timer:NodeJS.Timeout| null = null,result
  return function proxy(...params) {
    let self = this,
      callNow = !timer && immediate;
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      clearTimeout(timer);
      timer = null;
      if (!immediate) result = func.apply(self, params);
    }, wait);
    if (callNow) result = func.apply(self, params);
    return result;
  };
};

export const throttle = function throttle(func:Function, wait:undefined | boolean | number) {
    if (typeof func !== "function") throw new TypeError('func must be a function!');
    wait = +wait;
    if (isNaN(wait)) wait = 300;
    let timer = null,
        previous = 0,
        result;
    return function proxy(...params) {
        let now = +new Date,
            remaining = wait - (now - previous),
            self = this;
        if (remaining <= 0) {
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            previous = now;
            result = func.apply(self, params);
            return result;
        }
        if (!timer) {
            timer = setTimeout(() => {
                clearTimeout(timer);
                timer = null;
                previous = +new Date;
                result = func.apply(self, params);
            }, remaining);
        }
        return result;
    };
};

