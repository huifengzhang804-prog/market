import type { VNode, RendererNode, RendererElement } from 'vue'

type ColumnsType = Record<'label' | 'prop' | 'customStyle' | 'width', string>
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
    default: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
}
