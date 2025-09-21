type RegionData = {
    label: string
    value: string
    children?: RegionData[]
}
/**
 * @description: 根据code返回地区名称
 * @param {string} arr code 数组
 * @returns {*}
 */
export function AddressFn(data: RegionData[], arr: string[]): string {
    const newArr: string[] = []
    let index = 0
    function code(data: RegionData[], str: string) {
        const res = data.find((item) => item.value === str)
        if (res) {
            index++
            newArr.push(res.label)
            if (res.children) {
                code(res.children, arr[index])
            }
        }
    }
    code(data, arr[0])
    return newArr.join('')
}
