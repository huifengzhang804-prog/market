export function isVideo(str: string) {
  let isPass = false
  if (!/.(gif|jpg|jpeg|png|gif|jpg|png|webp|avif|jfif)$/i.test(str)) {
    isPass = true
  }
  return isPass
}
/**
 * 预览图片
 * @param {number | string} index
 * @param {string} imageList
 */
export function imgPreview(current: number | string, imageList: string[]) {
  // 预览图片
  const imgData: string[] = []
  imageList.forEach((item) => {
    if (!isVideo(item)) {
      imgData.push(item)
    }
  })
  uni.previewImage({
    urls: imgData,
    current,
    loop: true,
  })
}
