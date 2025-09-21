// c-w-(number) 宽度大小
// c-h-(number) 高度大小
// c-fs-(number) 字体大小
// c-lh-(number) 行高大小
// c-ellipsis-(number) 保留行数其余省略
// c-mr-(number) 右外边距
// c-ml-(number) 左外边距
// c-mt-(number) 上外边距
// c-mb-(number) 下外边距
// c-pr-(number) 右内边距
// c-pl-(number) 左内边距
// c-pt-(number) 上内边距
// c-pb-(number) 下内边距
// c-br-(number) 圆角大小
// c-top-(number) top百分比
// c-gap-(number) 间距大小
// c-c-(string)  字体颜色
// c-bg-(string) 背景颜色
// c-bc-(string) 边框颜色

// e-bg-linear 常用红色背景渐变
// e-bg-linear-grey 常用灰色背景渐变
// e-ff 常用字体
// e-c-(0,3,6,9,f) 常用字体颜色
// e-tj 文字两端对齐
// e-bs 常用阴影
// e-br 圆角50%

// 新增规则说明
// b-(1,2,b,t,l,r) 边框样式
// hover-c-* 悬停颜色
// hover-b-* 悬停边框
// fcenter,ccenter,fdc 布局辅助
// mla,fl,ma 位置辅助
// fw-(700,900) 字重
// cursor-pointer,cp,cup 鼠标指针
// overflow-(hidden,auto) 溢出处理
// text-(left,center,right) 文本对齐
// spot 文本溢出省略
// choosed,disabled 状态样式

import type { Rule } from 'unocss'

export const rules: Rule[] = [
  // 基础尺寸类
  [
    /^c-w-(\d+)$/,
    ([, number]) => ({
      width: `${number}px`,
    }),
  ],
  [
    /^c-h-(\d+)$/,
    ([, number]) => ({
      height: `${number}px`,
    }),
  ],
  [
    /^c-lh-(\d+)$/,
    ([, number]) => ({
      'line-height': `${number}px`,
    }),
  ],
  [
    /^c-fs-(\d+)$/,
    ([, number]) => ({
      'font-size': `${number}px`,
    }),
  ],

  // 边距类
  [
    /^c-mr-(\d+)$/,
    ([, number]) => ({
      'margin-right': `${number}px`,
    }),
  ],
  [
    /^c-ml-(\d+)$/,
    ([, number]) => ({
      'margin-left': `${number}px`,
    }),
  ],
  [
    /^c-mt-(\d+)$/,
    ([, number]) => ({
      'margin-top': `${number}px`,
    }),
  ],
  [
    /^c-mb-(\d+)$/,
    ([, number]) => ({
      'margin-bottom': `${number}px`,
    }),
  ],
  [
    /^c-pl-(\d+)$/,
    ([, number]) => ({
      'padding-left': `${number}px`,
    }),
  ],
  [
    /^c-pr-(\d+)$/,
    ([, number]) => ({
      'padding-right': `${number}px`,
    }),
  ],
  [
    /^c-pt-(\d+)$/,
    ([, number]) => ({
      'padding-top': `${number}px`,
    }),
  ],
  [
    /^c-pb-(\d+)$/,
    ([, number]) => ({
      'padding-bottom': `${number}px`,
    }),
  ],

  // 文本省略类
  [
    /^c-ellipsis-(\d+)$/,
    ([, number]) => ({
      display: '-webkit-box',
      '-webkit-box-orient': 'vertical',
      '-webkit-line-clamp': `${number}`,
      overflow: 'hidden',
    }),
  ],

  // 圆角类
  [
    /^c-br-(\d+)$/,
    ([, number]) => ({
      'border-radius': `${number}px`,
    }),
  ],

  // 定位类
  [
    /^c-top-(\d+)$/,
    ([, number]) => ({
      top: `${number}%`,
    }),
  ],

  // 间距类
  [
    /^c-gap-(\d+)$/,
    ([, number]) => ({
      gap: `${number}px`,
    }),
  ],

  // 颜色相关类
  [
    /^c-c-(.+)$/,
    ([, string]) => {
      // 安全处理颜色值
      const safeColor = string.replace(/[^\da-fA-F]/g, '')
      return {
        color: `#${safeColor}`,
      }
    },
  ],
  [
    /^c-bg-(.+)$/,
    ([, string]) => {
      // 安全处理颜色值
      const safeColor = string.replace(/[^\da-fA-F]/g, '')
      return {
        background: `#${safeColor}`,
      }
    },
  ],
  [
    /^c-bc-(.+)$/,
    ([, string]) => {
      // 只保留16进制颜色字符
      const safeColor = string.replace(/[^\da-fA-F]/g, '')
      return {
        'border-color': `#${safeColor}`,
      }
    },
  ],

  // 溢出处理类
  [
    /^(?:overflow-x|of)-(.+)$/,
    ([, string]) => ({
      'overflow-x': `${string}`,
    }),
  ],
  [
    /^(?:overflow-y|of)-(.+)$/,
    ([, string]) => ({
      'overflow-y': `${string}`,
    }),
  ],

  // 尺寸类扩展
  [
    /^c-(min-|max-)?(width|height)-?(.+)$/,
    ([, first, mid, last]) => {
      const returnObj = {} as any
      returnObj[`${first}${mid}`] = `${last}px`
      return returnObj
    },
  ],

  // 边框样式类
  [
    /^b-(1|2|b|t|l|r)$/,
    ([, type]) => {
      const styles = {
        '1': { 'border-style': 'solid', 'border-width': '1px' },
        '2': { 'border-style': 'solid', 'border-width': '2px' },
        b: { 'border-bottom-style': 'solid' },
        t: { 'border-top-style': 'solid' },
        l: { 'border-left-style': 'solid' },
        r: { 'border-right-style': 'solid' },
      }
      return styles[type as keyof typeof styles]
    },
  ],

  // 悬停样式类
  [
    /^hover-c-#(.+)$/,
    ([, color]) => ({
      '&:hover': {
        color: `#${color}`,
      },
    }),
  ],
  [
    /^hover-b-(.+)$/,
    ([, style]) => {
      if (style === 'b') {
        return {
          '&:hover': {
            'border-bottom-style': 'solid',
          },
        }
      }
      return {}
    },
  ],

  // Flex布局类
  [
    /^(flex-wrap|justify-center)$/,
    ([, type]) => {
      const styles = {
        'flex-wrap': { 'flex-wrap': 'wrap' },
        'justify-center': { 'justify-content': 'center' },
      }
      return styles[type as keyof typeof styles]
    },
  ],

  // 位置助手类
  [
    /^(mla|fl|ma)$/,
    ([, type]) => {
      const styles = {
        mla: { 'margin-left': 'auto' },
        fl: { float: 'left' },
        ma: { margin: 'auto' },
      }
      return styles[type as keyof typeof styles]
    },
  ],

  // 字重类
  [
    /^fw-(\d+)$/,
    ([, weight]) => ({
      'font-weight': weight,
    }),
  ],

  // 文本对齐类
  [
    /^text-(left|center|right)$/,
    ([, align]) => ({
      'text-align': align,
    }),
  ],

  // 鼠标样式类
  [
    /^(cursor-pointer|cp|cup)$/,
    () => ({
      cursor: 'pointer',
    }),
  ],

  [
    'disabled',
    {
      opacity: '0.5',
      cursor: 'not-allowed',
    },
  ],
  [
    'spot',
    {
      'white-space': 'nowrap',
      overflow: 'hidden',
      'text-overflow': 'ellipsis',
    },
  ],

  // 背景色预设
  [
    /^(!?)c-bg-([A-Fa-f0-9]+)$/,
    ([, important, color]) => {
      // 确保颜色值是有效的16进制
      const safeColor = color.replace(/[^\da-fA-F]/g, '')
      return {
        'background-color': `#${safeColor}${important ? ' !important' : ''}`,
      }
    },
  ],
  [
    'bg-white',
    {
      'background-color': '#ffffff',
    },
  ],

  // 常量
  [
    'e-bg-linear',
    {
      background: 'linear-gradient(270deg, #ff2e64 0%, #ff2828 100%)',
    },
  ],
  [
    'e-bg-linear-grey',
    {
      background: 'linear-gradient(180deg, #ffffff 45%, #eef0f6)',
    },
  ],
  [
    'e-c-f',
    {
      color: '#ffffff',
    },
  ],
  [
    'e-c-0',
    {
      color: '#000000',
    },
  ],
  [
    'e-c-3',
    {
      color: '#333333',
    },
  ],
  [
    'e-c-6',
    {
      color: '#666666',
    },
  ],
  [
    'e-c-9',
    {
      color: '#999999',
    },
  ],
  [
    'e-ff',
    {
      'font-family': 'PingFang HK, Helvetica Neue, Helvetica, Arial, Hiragino Sans GB, 微软雅黑, tahoma, simsun, 宋体',
    },
  ],
  [
    'e-tj',
    {
      'text-align-last': 'justify',
    },
  ],
  [
    'e-bs',
    {
      'box-shadow': '0px 0px 13px 4px rgba(0, 0, 0, 0.04)',
    },
  ],
  [
    'e-br',
    {
      'border-radius': '50%',
    },
  ],
]
