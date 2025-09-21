export const useConvertString = (value: number) => {
  const k = 10000
  const sizes = ['', '万', '亿', '万亿']
  let i = 0
  if (value < k) {
    return value
  } else {
    i = Math.floor(Math.log(value) / Math.log(k))
    return `${(value / Math.pow(k, i)).toFixed(2)}${sizes[i]}+`
  }
}
