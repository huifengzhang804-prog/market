import type { NavBarType } from '@decoration/components/types'

interface TabbarStatus {
  [key: string]: boolean
}

interface StateType {
  currentSwiperId: string
  ChoosedNavName: string
  navBar: NavBarType | null
  shopId: Long
  tabbarStatus: TabbarStatus
  refreshState: number
  loading: boolean
}

export const constNavBar = {
  codeStyle: 1,
  defaultColor: '#7A7E83',
  selectColor: '#F64E3F',
  menuList: [
    {
      actIcon: 'https://oss-tencent.bgniao.cn/api/home_page1.png',
      codeStyle: 1,
      defIcon: 'https://oss-tencent.bgniao.cn/api/home_page.png',
      iconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/9ad3a62298fd4c2aa75f93ac2b3a517a.png',
      iconType: 1,
      id: '',
      isAdd: true,
      isHome: true,
      link: { id: '1', type: 0, name: '首页', url: '/basePackage/pages/merchant/Index', append: '0' },
      selectedIconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/1e26aab823e84979a6994a0d34918526.png',
      text: '首页',
    },
    {
      actIcon: 'https://oss-tencent.bgniao.cn/api/shopping_mall.png',
      codeStyle: 1,
      defIcon: 'https://oss-tencent.bgniao.cn/api/shopping_mall1.png',
      iconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/f8d2575562af4c9490cb25b0101b680e.png',
      iconType: 1,
      id: '',
      isAdd: true,
      isHome: false,
      link: { id: '2', type: 0, name: '分类', url: '/basePackage/pages/merchant/Index', append: '1' },
      selectedIconPath: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200327/7dae3bdf8ee24833b9600185fcec5fed.png',
      text: '商超',
    },
  ],
  underfillColor: '#7A7E83',
}

const defaultState: StateType = {
  currentSwiperId: '1',
  // 底部导航list
  navBar: null,
  ChoosedNavName: '首页',
  shopId: '0',
  tabbarStatus: {},
  refreshState: 0,
  loading: false,
}
export default defaultState
