import { VNode, RendererNode, RendererElement } from 'vue'

type ColumnsType = Record<'label' | 'prop' | 'customStyle' | 'width' | 'sort' | 'fixed', string>

interface MTableSlotsType {
    header?: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
    custom?: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
    noData?: () => VNode<RendererNode, RendererElement, {}>[]
    default: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
}
