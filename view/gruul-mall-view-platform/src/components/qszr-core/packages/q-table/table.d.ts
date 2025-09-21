import type { VNode, RendererNode, RendererElement } from 'vue'

type ColumnsType = Record<'label' | 'prop' | 'customStyle' | 'width' | 'sort' | 'fixed', string | boolean>
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
    noData?: (row: any) => VNode<
        RendererNode,
        RendererElement,
        {
            [key: string]: any
        }
    >[]
    default: (row: any) =>
        | VNode<
              RendererNode,
              RendererElement,
              {
                  [key: string]: any
              }
          >[]
        | undefined
}
