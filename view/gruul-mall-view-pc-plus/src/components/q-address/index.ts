export function AddressFn(data: any[], arr: string[]): string {
  const newArr: any[] = []
  let index = 0
  function code(data: any[], str: string) {
    const res = data.find((item) => item.value === str)
    if (res) {
      index++
      newArr.push(res.label)
      if (res.children) {
        code(res.children, arr[index])
      }
    }
  }
  code(data, arr?.[0])
  return newArr.join(' ')
}
