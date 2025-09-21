import { cloneDeep } from 'lodash-es'

export interface TagsInterface {
    [key: string]: any
    id?: string
    tagName: string
    option?: boolean
}
export const compareTags = (shopTags: TagsInterface[] = [], newTags: TagsInterface[] = []) => {
    const delUserTagIdList: string[] = []
    const addTagList: TagsInterface[] = []
    const updateTagList: TagsInterface[] = []
    newTags.forEach((tag) => {
        if (!tag.id) {
            addTagList.push(tag)
        } else {
            updateTagList.push(tag)
        }
    })
    shopTags.forEach((tag) => {
        const findedIndex = newTags.findIndex((newTag) => tag.id === newTag.id)
        if (findedIndex === -1 && tag.id) {
            delUserTagIdList.push(tag.id)
        }
    })
    return {
        delUserTagIdList,
        addTagList,
        updateTagList,
    }
}

export const getInitialLabelsForMember = (currentMemberLabels: TagsInterface[], shopTags: TagsInterface[]) => {
    const cloneShopTags = cloneDeep(shopTags)
    cloneShopTags.forEach((item) => {
        const findedIndex = currentMemberLabels.findIndex((currentMemberLabel) => currentMemberLabel.tagId === item.id)
        item.option = findedIndex > -1
    })
    return cloneShopTags
}
