import { RendererElement, RendererNode, VNode } from 'vue'
import { BreakPoint, Responsive } from './Grid/interface'

export type SearchType =
    | 'input'
    | 'input-number'
    | 'select'
    | 'select-v2'
    | 'tree-select'
    | 'cascader'
    | 'date-picker'
    | 'time-picker'
    | 'time-select'
    | 'switch'
    | 'slider'
    | 'copy'

export type SearchProps = {
    valueType?: SearchType // 当前项搜索框的类型
    label?: string // 当前项搜索框的 label
    labelWidth?: number
    // 当前项搜索框的 options
    options?:
        | {
              value?: any[]
          }
        | Obj[]
    prop: string // 搜索项参数，根据 element plus 官方文档来传递，该属性所有值会透传到组件
    span?: number // 搜索项所占用的列数，默认为 1 列
    offset?: number // 搜索字段左侧偏移列数
    defaultValue?: string | number | boolean | any[] | Ref<any> // 搜索项默认值
    fieldProps: Obj // 搜索项的属性，根据 element plus 官方文档来传递，该属性所有值会透传到组件
    fieldNames?: {
        label: string
        value: string
        children: string
    }
    // 自定义搜索项 返回h函数
    renderField?: (modelValue?: any, onChange?: any) => VNode<RendererNode, RendererElement, { [key: string]: any }>
} & Partial<Record<BreakPoint, Responsive>>
