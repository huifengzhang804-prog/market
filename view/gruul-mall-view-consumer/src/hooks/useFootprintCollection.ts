import type { FootprintItem, FootprintList } from '@/basePackage/pages/footprint/type'
/**
 * 足迹列表数据分月处理
 * @param {FootprintItem} params
 */
export function useFootprintCollection(params: FootprintItem[]) {
  const footprintCollection: FootprintList[] = []
  for (let i = 0; i < params.length; i++) {
    let isHere = footprintCollection.some((item) => item.date === params[i].date)
    if (!isHere) {
      footprintCollection.push({
        date: params[i].date,
        records: [Object.assign(params[i], { done: false })],
      })
    } else {
      for (let j = 0; j < footprintCollection.length; j++) {
        if (footprintCollection[j].date === params[i].date) {
          footprintCollection[j].records.push(Object.assign(params[i], { done: false }))
        }
      }
    }
  }
  return footprintCollection
}
