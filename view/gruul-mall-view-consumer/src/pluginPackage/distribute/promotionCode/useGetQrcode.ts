import { doGetQrcode } from '@/apis/good/index'

interface Options_ {
  path: string
  params?: any
}

/**
 * 分享商品详情地址
 */
export const SHARE_HERF = (code: string) => {
  const env = import.meta.env.VITE_BASE_URL
  return `${env.replace(/api/, 'h5')}#/pages/platform/Index?code=${code}`
}
// 获取二维码图片
export const useGetQrcode = () => {
  // 获取二维码
  const handleGetQrcode = async (fn: (qrcode: string) => void, callBackfn: () => void = () => {}, options_?: Options_) => {
    // @ts-ignore
    const baseUrl = import.meta.env.VITE_BASE_H5_VIEW_URL

    const pages = getCurrentPages()
    // #ifdef MP-WEIXIN
    // @ts-ignore
    const { route, options } = pages[pages.length - 1]
    // #endif
    // #ifndef MP-WEIXIN
    // @ts-ignore
    // eslint-disable-next-line no-redeclare
    const { route, options } = pages[pages.length - 1].$page
    // #endif
    const qrcodeData = {
      hash: true,
      baseUrl,
      path: options_?.path || route,
      params: options_?.params || options,
    }
    const { data, success } = await doGetQrcode(qrcodeData)
    if (!success) {
      return uni.showToast({
        icon: 'none',
        title: '获取二维码失败',
      })
    }
    // #ifdef MP-WEIXIN
    base64srcWx(data, (filePath) => {
      fn(filePath)
    })
    // #endif

    // #ifdef H5
    base64srcH5(data, (filePath) => {
      fn(filePath)
    })
    // #endif

    // #ifdef APP-PLUS
    base64ToURL(data, (filePath) => {
      fn(filePath)
    })
    // #endif

    callBackfn()
  }
  return {
    handleGetQrcode,
  }
}
// #ifdef MP-WEIXIN
function base64srcWx(base64data: string, cb: (filePath: string) => void) {
  const fsm = uni.getFileSystemManager()
  const FILE_BASE_NAME = 'tmp_base64src'
  const [, format, bodyData] = /data:image\/(\w+);base64,(.*)/.exec(base64data) || []
  if (!format) {
    return new Error('ERROR_BASE64SRC_PARSE')
  }
  // @ts-ignore
  // eslint-disable-next-line no-undef
  const filePath = `${wx.env.USER_DATA_PATH}/${FILE_BASE_NAME}.${format}`
  const buffer = uni.base64ToArrayBuffer(bodyData)
  return fsm.writeFile({
    filePath,
    data: buffer,
    encoding: 'binary',
    success() {
      cb(filePath)
    },
    fail() {
      return new Error('ERROR_BASE64SRC_WRITE')
    },
  })
}
// #endif

// #ifdef H5
function base64ImgtoFile(dataurl: any, filename = 'file') {
  //将base64格式分割：['data:image/png;base64','XXXX']
  const arr = dataurl.split(',')
  // .*？ 表示匹配任意字符到下一个符合条件的字符 刚好匹配到：
  // image/png
  const mime = arr[0].match(/:(.*?);/)[1] //image/png
  //[image,png] 获取图片类型后缀
  const suffix = mime.split('/')[1] //png
  const bstr = atob(arr[1]) //atob() 方法用于解码使用 base-64 编码的字符串
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  const file = new File([u8arr], `${filename}.${suffix}`, {
    type: mime,
  })
  console.log('file', file)
  return file
}
function base64srcH5(base64data: string, cb: (filePath: string) => void) {
  const blob = base64ImgtoFile(base64data)
  const blobUrl = URL.createObjectURL(blob)
  cb(blobUrl)
}
// #endif

// #ifdef APP-PLUS
function base64ToURL(base64String: string, cb: (filePath: string) => void) {
  let extName: any = base64String.match(/data\:\S+\/(\S+);/)
  if (extName) {
    extName = extName[1]
  } else {
    new Error('base64 error')
  }
  let fileName = Date.now() + '.' + extName
  // @ts-ignore

  let bitmap = new plus.nativeObj.Bitmap('bitmap' + Date.now())
  bitmap.loadBase64Data(
    base64String,
    function () {
      let filePath = '_doc/uniapp_temp/' + fileName
      bitmap.save(
        filePath,
        {},
        function () {
          bitmap.clear()
          cb(filePath)
        },
        function (error) {
          bitmap.clear()
        },
      )
    },
    function (error) {
      bitmap.clear()
    },
  )
  return
}
// #endif
