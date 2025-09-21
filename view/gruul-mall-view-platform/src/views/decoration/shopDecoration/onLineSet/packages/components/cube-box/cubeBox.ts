import { LinkSelectItem } from '@/components/link-select/linkSelectItem'

/**
 * 魔方选中后的块类型
 */
export interface ShowCubeListWrap {
    top: string
    left: string
    width: string
    height: string
    paddingTop: string
    img: any
    borderWidth?: string
    text?: string
}

/**
 * 魔方正在选择时的类型
 */
export interface ShowCubeList {
    coordinates: string
    finish: boolean | number
    selected: boolean
    start: number
    style: { width: string; height: string }
}

export default {
    // 图片间隙
    borderWidth: 0,
    // 列数
    layoutWidth: 2,
    //行数
    layoutHeight: 1,
    //行数index
    showMethod: 0,
    //页面边距离
    pageMargin: 0,
    //展示个数
    width: 2,
    //图片数组
    subEntry: [
        {
            x: 0,
            y: 0,
            width: 1,
            height: 1,
            img: '',
            link: {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            },
            linkName: '',
        },
        {
            x: 1,
            y: 0,
            width: 1,
            height: 1,
            img: '',
            link: {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            },
            linkName: '',
        },
    ],
}
