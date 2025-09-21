import Sortable from 'sortablejs'

type ParseDataType = (treeData: any[]) => any[]

const useTransformData = () => {
    const parseData: ParseDataType = (array: any[] = []) => {
        return [].concat(
            ...array.map((item: any) => {
                // console.log(item);
                if (item.children && item.children.length > 0) {
                    // 有children元素，且为数组形式，数组长度大于等于1(例，二级 2-1)
                    return [].concat(item, ...parseData(item.children))
                } else if (item.children) {
                    // 有children元素，且为对象形式(例，二级 3-1)
                    return [].concat(item, item.children)
                } else {
                    // 没有children元素(例，一级 4)
                    return item
                }
            }),
        )
    }
    const toTreeData = (parseDataList: any[], pidKey = 'parentId') => {
        let result: any[] = []
        if (!Array.isArray(parseDataList)) {
            return result
        }
        parseDataList.forEach((item) => {
            delete item.children
        })
        let map: any = {}
        parseDataList.forEach((item) => {
            map[item.id] = item
        })
        parseDataList.forEach((item) => {
            let parent = map[item[pidKey]]
            if (parent) {
                ;(parent.children || (parent.children = [])).push(item)
            } else {
                result.push(item)
            }
        })
        return result
    }
    const buildRelationData = (evt: Sortable.MoveEvent, parseData: any[] = []) => {
        const evtFrom = evt.from as HTMLTableElement
        let index = 0,
            draggedData: any,
            relatedData: any
        for (let item of evtFrom.rows as any) {
            if (item === evt.dragged) {
                draggedData = parseData[index]
            }
            if (item === evt.related) {
                relatedData = parseData[index]
            }
            index++
        }
        return {
            draggedData,
            relatedData,
        }
    }
    return {
        parseData,
        toTreeData,
        buildRelationData,
    }
}

export default useTransformData
