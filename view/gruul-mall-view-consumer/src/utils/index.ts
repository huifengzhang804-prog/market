/**
 * 监测元素是否进入可视区域
 * @param {function} fn
 * @return {IntersectionObserver} observer 实例对象
 */
export function initLazyIntersectionObserver(fn: (entry: IntersectionObserverEntry) => void): IntersectionObserver {
  const observer: IntersectionObserver = new IntersectionObserver((entrys) => entrys.forEach((entry) => fn(entry)), {
    rootMargin: '0px',
    threshold: 0,
  })
  return observer
}

/**
 * 自动裁剪图片
 * @return {string} url 拼接地址
 */
const standard = 468
export function cropImg(src = '', w: number, h: number) {
  const newSrc = Array.isArray(src) ? src[0] : src
  const imgSuffix = newSrc.split('.').pop() || ''
  // 包含域名 腾讯云：myqcloud.com，阿里云：alicdn.com，京东云：360buyimg.com
  const myqcloud = /myqcloud\.com/
  const alicdn = /alicdn\.com/
  const jdcdn = /360buyimg.com/
  const standardW = w <= standard ? standard : getCropPx(w)
  const standardH = h <= standard ? standard : getCropPx(h)
  let regPrefix = ''
  let suffix = null
  if (myqcloud.test(newSrc)) {
    suffix = `$&?imageMogr2/thumbnail/${standardW}x${standardH}/format/webp/gravity/center`
  } else if (alicdn.test(newSrc)) {
    // 获取alicdn的图片后缀名
    if (['jpg', 'jpeg', 'png', 'gif'].includes(imgSuffix)) {
      suffix = `.${imgSuffix}_${standardW}x${standardW}q75.jpg_.webp`
    } else if (imgSuffix === 'webp') {
      // 获取到的后缀已经是webp格式的说明已经处理过了，不需要进行二次处理了
      suffix = ''
    } else {
      suffix = `.jpg_${standardW}x${standardW}q75.jpg_.webp`
    }
  } else if (jdcdn.test(newSrc)) {
    suffix = `!cc_${standardW}x${standardW}.webp`
    regPrefix = `$&`
  }
  return suffix && !newSrc.includes(suffix) ? newSrc.replace(/\.(jpg|jpeg|png|gif)(?!\.)/g, regPrefix + suffix) : newSrc
}

/**
 * 根据设备转换 px
 * @return {*}
 */
export const getCropPx = (unit: number) => uni.upx2px(unit)
/**
 * 文本图片地址转换
 * @param {string} text 文本
 * @return {stirng} 转换后文本
 */
export function transformTextToImg(text: string) {
  let textNode = text
  const regImgUrl = /<img.*?src="(.*?)"/
  const regStyle = /width:\s*(.*?);.*?height:\s*(.*?);/
  const matchImgUrl = textNode.match(regImgUrl) as Array<string>
  const matchStyle = textNode.match(regStyle) as Array<string>
  if (matchStyle && matchImgUrl) {
    const standardWidth = +matchStyle[1].split('px')[0] * 2 // 逻辑像素x2 = 物理像素
    const standardHeight = +matchStyle[2].split('px')[0] * 2
    const newImgSrc = cropImg(matchImgUrl[1], standardWidth, standardHeight)
    textNode = text
      .replace(/src="(.*?)"/, `src="${newImgSrc}"`)
      .replace(/(background-image:\s*url\(["']?)(.*?)["']?\);?/g, `$1${newImgSrc});`)
      .replace(/width:\s*(.*?);/, `width: ${getCropPx(standardWidth)}px;`)
      .replace(/height:\s*(.*?);/, `height: ${getCropPx(standardHeight)}px;`)
  }
  return textNode
}

/**
 * 截取 unit 单位
 * @return {*}
 */
export const extractUnit = (unitText: string) => {
  const matchNum = unitText.match(/\d+/)
  if (matchNum && matchNum[0]) {
    return parseInt(matchNum[0], 10)
  }
}

/**
 * 获取 cdn 完整图片地址
 * @return {*}
 */
export const getCdnUrl = (url: string) => `${import.meta.env.VITE_CDN_URL}${url}`
