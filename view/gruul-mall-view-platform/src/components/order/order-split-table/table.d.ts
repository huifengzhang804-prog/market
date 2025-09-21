import type { VNode, RendererNode, RendererElement } from 'vue'

type ColumnsType = Record<'label' | 'prop' | 'customStyle' | 'width' | 'fixed' | 'align', string>
interface MTableSlotsType {
    header?: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
    noData?: () => VNode<RendererNode, RendererElement>[]
    custom?: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
    default: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
}
