import { LinkSelectItem } from '@/components/link-select/linkSelectItem'
export interface CubeBoxFormData {
    borderWidth: number
    layoutWidth: number
    layoutHeight: number
    showMethod: number
    pageMargin: number
    width: number
    subEntry: IBanners[]
}
export interface IBanners {
    x: number
    y: number
    width: number
    height: number
    img: string
    link: LinkSelectItem
    linkName: string
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
