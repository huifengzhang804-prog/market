import { navBarDefaultData } from '@/components/link-select/linkSelectItem'
/**
 * 控件nav默认值
 * @param {number} codeStyle 选择样式,1普通样式 2中间大图样式
 * @param {} menuList 底部导航列表
 * @param {string} selectColor 字体选中颜色
 * @param {string} defaultColor 字体未选择颜色
 * @param {string} underfillColor 底部填充颜色
 * @param {string} text 导航名称
 * @param {string} iconType 图标类型 1-系统图标 2-自定义图标
 * @param {string} iconPath 未选中图标url
 * @param {string} selectedIconPath 选中图标url
 * @param {boolean} isHome  是否为首页
 * @param {number} sortIndex  下标位置
 */
const formData = {
    codeStyle: 1,
    selectColor: '#F64E3F',
    defaultColor: '#7A7E83',
    underfillColor: '#FFFFFF',
    menuList: [
        {
            text: '首页',
            iconType: 2,
            codeStyle: 1,
            iconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/9ad3a62298fd4c2aa75f93ac2b3a517a.png',
            selectedIconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/1e26aab823e84979a6994a0d34918526.png',
            isHome: true,
            id: '',
            sortIndex: 0,
            isAdd: true,
            link: getLink('首页'),
        },
        {
            text: '商超',
            iconType: 2,
            codeStyle: 1,
            iconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/f8d2575562af4c9490cb25b0101b680e.png',
            selectedIconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/7dae3bdf8ee24833b9600185fcec5fed.png',
            isHome: false,
            id: '',
            isAdd: true,
            sortIndex: 1,
            link: getLink('分类'),
        },
        // {
        //     text: '购物车',
        //     iconType: 2,
        //     iconPath: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220122/027920714b4341ffb72703cc04f7cfad.png',
        //     selectedIconPath: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220122/34c016b6cbea4f60b54c37ea0670a2a2.png',
        //     name: 'shopCar',
        //     isHome: false,
        //     type: 0,
        //     id: '',
        //     isAdd: true,
        //     sortIndex: 2,
        // },
        // {
        //     text: '我的',
        //     iconType: 2,
        //     iconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/4c5bcb9fb63f4e8e9e4cd50be247e00a.png',
        //     selectedIconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/13d654d202bb4fd69e2da28a6c20a90f.png',
        //     linkSelectItem: '',
        //     linkUrl: '/pages/index/index',
        //     linkName: '个人中心',
        //     name: '',
        //     isHome: false,
        //     type: 0,
        //     id: '',
        //     isAdd: true,
        //     sortIndex: 3,
        // },
    ],
}
export default formData
export const navBarItem = {
    text: '',
    iconType: 2,
    iconPath: '',
    selectedIconPath: '',
    isHome: false,
    id: '',
    isAdd: true,
    codeStyle: 1,
    link: {
        id: '',
        type: 0,
        name: '/basePackage/pages/merchant/Index',
        url: '',
        append: 1,
    },
}

function getLink(text: string) {
    return navBarDefaultData.filter((item) => {
        return item.name === text
    })[0]
}
