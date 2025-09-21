import { gsap } from 'gsap'

export const downloadAnimation = (e: MouseEvent) => {
    if (!e) {
        console.log('点击事件的参数没传')
        return
    }
    const targetDom = document.querySelector('#download_icon')
    // 下载动画
    // 在event对象中获取鼠标的坐标
    const { clientX, clientY } = e
    // 获取目标元素的位置
    const { x, y } = targetDom?.getBoundingClientRect() || {}
    // 计算鼠标和目标元素之间的距离
    const distanceX = clientX - (x || 0)
    const distanceY = clientY - (y || 0)
    // 在 { clientX, clientY } 处创建一个新的元素(动画结束后销毁该元素)
    const downloadIcon = document.createElement('span')
    downloadIcon.classList.add('iconfont')
    downloadIcon.classList.add('icon-xiazai')
    downloadIcon.style.position = 'fixed'
    downloadIcon.style.left = `${clientX}px`
    downloadIcon.style.top = `${clientY}px`
    downloadIcon.style.fontSize = '28px'
    downloadIcon.style.color = '#555CFD'
    downloadIcon.style.zIndex = '99999999'
    downloadIcon.style.fontWeight = 'bold'
    document.body.appendChild(downloadIcon)
    // 设置动画
    gsap.to(downloadIcon, {
        x: -distanceX,
        y: -distanceY,
        duration: 1,
        ease: 'power1.in',
        scale: 0.5,
        onComplete: () => {
            document.body.removeChild(downloadIcon)
            ElMessage.success('下载成功')
        },
    })
}
