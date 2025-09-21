type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>
export interface ShopCategoryList extends ShopCategoryItem {
    disabled?: boolean
    children: ShopCategoryList[]
}
