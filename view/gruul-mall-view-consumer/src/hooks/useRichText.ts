import linkNavTo from '@/libs/linkNavTo'
import type { LinkType, NavBarMenuType } from '@decoration/components/types'
import { defaultLinkMap, useSettingStore } from '@/store/modules/setting'
import { constNavBar } from '@/store/modules/setting/state'

// 定义接口类型
interface NodeAttrs {
  href: string
  ignore?: () => void
  [key: string]: any
}

interface RichTextNode {
  name: string
  attrs: NodeAttrs
}

export function useRichText() {
  function handleTextClick(event: { detail: { node: RichTextNode } }) {
    const node = event.detail.node
    switch (node.name) {
      case 'a':
        if (!node?.attrs?.href) return
        handleLinkClick(node.attrs)
        break
      default:
        break
    }
  }

  // 富文本a标签跳转
  function handleLinkClick(attrs: NodeAttrs) {
    try {
      attrs?.ignore?.()
      const res: LinkType | null = getUrlTypeInfo(attrs.href)
      if (!res) {
        return uni.setClipboardData({
          data: attrs.href,
          success: () => uni.showToast({ title: `处理失败，链接已复制`, icon: 'none' }),
        })
      }

      linkNavTo(res)
    } catch (error) {
      uni.showToast({ title: '链接处理失败', icon: 'none' })
    }
  }

  return { handleTextClick, handleLinkClick }
}

/**
 * 获取底部导航条的信息
 */
export function getNavBarInfo(): Record<string, LinkType> {
  const navBarMap: Record<string, LinkType> = {}
  const $settingStore = useSettingStore()
  const navBarList: NavBarMenuType[] = $settingStore?.gettterPlatformNavBar?.menuList || constNavBar.menuList

  navBarList.forEach((item: NavBarMenuType) => {
    if (defaultLinkMap[item.link.name]) {
      navBarMap[defaultLinkMap[item.link.name]] = item.link
    } else {
      navBarMap[item.link.url] = item.link
    }
  })
  return navBarMap
}

/**
 * 解析URL并返回链接类型信息
 * @param href 链接地址
 * @returns 链接类型信息|null
 */
export function getUrlTypeInfo(href: string): LinkType | null {
  const defaultNavBarMap: Record<string, LinkType> = getNavBarInfo()
  console.log(href, defaultNavBarMap)
  const navBarInfo = defaultNavBarMap[href]
  if (navBarInfo) return navBarInfo

  const defaultProps = { id: '', name: '', append: '', shopId: '' }
  const trimmedHref = href.trim()
  if (!trimmedHref) return null

  try {
    switch (true) {
      case /^(http|\/\/)/.test(trimmedHref):
        return { type: 6, url: trimmedHref, ...defaultProps }
      case trimmedHref.startsWith('/'):
        return { type: 999, url: trimmedHref, ...defaultProps }
      case trimmedHref.startsWith('wx'):
        return { type: 7, url: trimmedHref, ...defaultProps }
      default:
        return null
    }
  } catch (error) {
    console.error('URL parsing error:', error)
    return null
  }
}
