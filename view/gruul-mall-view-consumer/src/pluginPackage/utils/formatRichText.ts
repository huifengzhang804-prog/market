/**
 * 处理富文本里的图片宽度自适应
 * 1.去掉img标签里的style、width、height属性
 * 2.修改所有style里的width属性为max-width:100%
 * 3.img标签添加style属性：max-width:100%;height:auto
 * @param html
 * @return string
 */
function formatRichText(html: string) {
  // 去掉img标签里的style、width、height属性
  let newContent = html.replace(/<img[^>]*>/gi, (match, capture) => {
    match = match.replace(/style="[^"]+"/gi, '').replace(/style='[^']+'/gi, '')
    match = match.replace(/width="[^"]+"/gi, '').replace(/width='[^']+'/gi, '')
    match = match.replace(/height="[^"]+"/gi, '').replace(/height='[^']+'/gi, '')
    return match
  })
  // 修改所有style里的width属性为max-width:100%
  newContent = newContent.replace(/style="[^"]+"/gi, (match, capture) => {
    match = match.replace(/width:[^;]+;/gi, 'max-width:100%;').replace(/width:[^;]+;/gi, 'max-width:100%;')
    return match
  })
  // img标签添加style属性：max-width:100%;height:auto
  newContent = newContent.replace(/style=""/gi, '')
  newContent = newContent.replace(/\<img/gi, '<img style="max-width:100%;height:auto;display:block;margin:0px auto;"')
  newContent = newContent.replace(/\<li/gi, '<li style="list-style: none; padding: 0; margin: 0"')
  newContent = newContent.replace(/\<ul/gi, '<ul style="padding: 0; margin: 0"')
  // 兼容视频播放
  //     <video poster="" controls="true" width="auto" height="auto">
  //     <source
  //         src="https://whealthcn-1254881449.cos.ap-shanghai.myqcloud.com/mall/20240426/0dbf099eb9b44728910d498f66c1b135.mp4"
  //         type="video/mp4"
  //     />
  // </video>
  newContent = newContent.replace(/\><source/gi, '')
  newContent = newContent.replace(/\<\/video>/gi, '')
  return newContent
}

export default formatRichText
