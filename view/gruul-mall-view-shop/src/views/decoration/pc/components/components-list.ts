import { ComponentItem } from './packages/index/formModel'

export const basicComponentList: ComponentItem[] = [
    {
        icon: 'linear-new-other-cover-flow-banner',
        value: 'swiper',
        label: '轮播图',
        width: 950,
        show: true,
        loadingHeight: '582px',
    },
    {
        icon: 'shangpin3',
        value: 'goods',
        label: '商品组件',
        width: 950,
        show: true,
        space: 24,
        loadingHeight: '601px',
    },
    {
        icon: 'tuijian',
        value: 'recommend',
        label: '推荐商品',
        width: 950,
        show: true,
        space: 10,
        loadingHeight: '601px',
    },
]

/**
 * 默认底部不可编辑组件
 */
export const getDefaultFixedEndComponents = (): ComponentItem[] => [
    {
        formData: { img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2024/03/12/65eff2048531567dcea2fae1.jpeg' },
        icon: 'a-37-fuwu-xianxing',
        id: '1710222827274',
        label: '服务保障',
        loadingHeight: '84px',
        show: true,
        space: 5,
        value: 'guarantee',
        width: 480,
    },
    {
        icon: 'toolbar',
        id: '1710152754261',
        label: '底部信息',
        loadingHeight: '434px',
        value: 'footerInfo',
        show: true,
        width: 804,
        formData: {
            QRcode: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2024/03/12/65efef5582c4d47178297549.jpeg',
            logo: {
                img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2024/03/12/65efef1482c4d47178297548.png',
                logoInfo: [
                    { title: '宁波启山科技有限公司', link: '' },
                    { title: '18158554030', link: '' },
                    { title: '浙江省宁波市长兴路199号柯力物联网大厦', link: '' },
                ],
            },
            module: [
                {
                    moduleName: '关于我们',
                    moduleTitle: [
                        { title: '公司动态', link: '' },
                        { title: '增值电信业务经营许可', link: '' },
                    ],
                },
                {
                    moduleName: '产品',
                    moduleTitle: [
                        { title: '社区团购', link: '' },
                        { title: 'O2O外卖配送', link: '' },
                        { title: 'B2C单商户商城', link: '' },
                        { title: 'B2B2C多商户商城', link: '' },
                    ],
                },
            ],
            icon: 'toolbar',
            id: '1710152754261',
            label: '底部信息',
            loadingHeight: '434px',
            value: 'footerInfo',
            width: 804,
        },
    },
    {
        formData: [{ title: '宁波启山科技有限公司', link: '' }],
        icon: 'copyright',
        id: '1710152878157',
        label: '版权信息',
        loadingHeight: '432px',
        show: true,
        top: true,
        value: 'copyright',
        width: 804,
    },
]

/**
 * 默认顶部不可编辑组件
 */
export const getDefaultFixedStartComponents = (): ComponentItem[] => [
    {
        borderColor: '#666',
        formData: { message: '这里是欢迎语~', settledIn: true },
        icon: 'zhanghaodenglu',
        id: '1709953988727',
        label: '登录栏',
        loadingHeight: '80px',
        show: true,
        top: true,
        value: 'loginLine',
        width: 480,
    },
    {
        borderColor: '#fff',
        formData: {
            car: true,
            logo: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2024/03/12/65efef1482c4d47178297548.png',
            search: true,
        },
        icon: 'sousuo1',
        show: true,
        id: '1710152416242',
        label: '搜索栏',
        loadingHeight: '180px',
        value: 'search',
        width: 480,
    },
]

/**
 * 默认顶部可编辑不可拖动组件
 */
export const getDefaultStartComponents = (): ComponentItem[] => [
    {
        icon: 'linear-new-other-cover-flow-banner',
        value: 'shopSign',
        label: '店招',
        width: 950,
        show: true,
        loadingHeight: '324px',
        formData: {
            newTips: true,
            service: true,
            follow: true,
            img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2024/03/13/65f147d082c4d4717829754c.jpeg',
            type: '自定义链接',
            link: '',
        },
    },
    {
        borderColor: '#fff',
        formData: [{ text: '所有分类', type: 'system', link: '所有分类', id: 1710152404895 }],
        icon: 'daohangliebiao',
        id: '1710152892549',
        label: '导航栏',
        show: true,
        loadingHeight: '530px',
        top: true,
        value: 'navigation',
        width: 530,
    },
]
