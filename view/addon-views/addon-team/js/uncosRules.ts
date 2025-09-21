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

export const rules = [
  [
    /^c-w-(\d+)$/,
    ([, number]: number[]) => ({
      width: `${number}px`,
    }),
  ],
  [
    /^c-h-(\d+)$/,
    ([, number]: number[]) => ({
      height: `${number}px`,
    }),
  ],
  [
    /^c-lh-(\d+)$/,
    ([, number]: number[]) => ({
      'line-height': `${number}px`,
    }),
  ],
  [
    /^c-fs-(\d+)$/,
    ([, number]: number[]) => ({
      'font-size': `${number}px`,
    }),
  ],
  [
    /^c-mr-(\d+)$/,
    ([, number]: number[]) => ({
      'margin-right': `${number}px`,
    }),
  ],
  [
    /^c-ml-(\d+)$/,
    ([, number]: number[]) => ({
      'margin-left': `${number}px`,
    }),
  ],
  [
    /^c-mt-(\d+)$/,
    ([, number]: number[]) => ({
      'margin-top': `${number}px`,
    }),
  ],
  [
    /^c-mb-(\d+)$/,
    ([, number]: number[]) => ({
      'margin-bottom': `${number}px`,
    }),
  ],
  [
    /^c-pl-(\d+)$/,
    ([, number]: number[]) => ({
      'padding-left': `${number}px`,
    }),
  ],
  [
    /^c-pr-(\d+)$/,
    ([, number]: number[]) => ({
      'padding-right': `${number}px`,
    }),
  ],
  [
    /^c-pt-(\d+)$/,
    ([, number]: number[]) => ({
      'padding-top': `${number}px`,
    }),
  ],
  [
    /^c-pb-(\d+)$/,
    ([, number]: number[]) => ({
      'padding-bottom': `${number}px`,
    }),
  ],
  [
    /^c-ellipsis-(\d+)$/,
    ([, number]: number[]) => ({
      display: '-webkit-box',
      '-webkit-box-orient': 'vertical',
      '-webkit-line-clamp': `${number}`,
      overflow: 'hidden',
    }),
  ],
  [
    /^c-br-(\d+)$/,
    ([, number]: number[]) => ({
      'border-radius': `${number}px`,
    }),
  ],
  [
    /^c-top-(\d+)$/,
    ([, number]: number[]) => ({
      top: `${number}%`,
    }),
  ],
  [
    /^c-gap-(\d+)$/,
    ([, number]: number[]) => ({
      gap: `${number}px`,
    }),
  ],
  [
    /^c-c-(.+)$/,
    ([, string]: string[]) => ({
      color: `#${string}`,
    }),
  ],
  [
    /^c-bg-(.+)$/,
    ([, string]: string[]) => ({
      background: `#${string}`,
    }),
  ],
  [
    /^c-bc-(.+)$/,
    ([, string]: string[]) => ({
      'border-color': `#${string}`,
    }),
  ],
  [
    /^(?:overflow-x|of)-(.+)$/,
    ([, string]: string[]) => ({
      'overflow-x': `${string}`,
    }),
  ],
  [
    /^(?:overflow-y|of)-(.+)$/,
    ([, string]: string[]) => ({
      'overflow-y': `${string}`,
    }),
  ],
  [
    /^c-(min-|max-)?(width|height)-?(.+)$/,
    ([c, first, mid, last]: string[]) => {
      const returnObj = {} as any
      returnObj[`${first}${mid}`] = `${last}px`
      return returnObj
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
      'font-family': 'Helvetica Neue, Helvetica, Arial, Hiragino Sans GB, 微软雅黑, tahoma, simsun, 宋体',
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
      'box-shadow': '0px 0px 13px 4px rgba(0, 0, 0, 0.04)',
    },
  ],
  [
    'e-br',
    {
      'border-radius': `50%`,
    },
  ],
]
