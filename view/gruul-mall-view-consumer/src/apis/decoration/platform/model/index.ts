interface PageFormData {
  background: string
  borderColor: string
  borderRadius: number
  btnBorderRadius: number
  color: string
  height: number
  hotWord: string[]
  keyWord: string
  showStyle: string
}
interface PageInfoItem {
  formData: PageFormData
  icon: string
  id: string
  label: string
  value: string
}
export type PageInfo = PageInfoItem[]
