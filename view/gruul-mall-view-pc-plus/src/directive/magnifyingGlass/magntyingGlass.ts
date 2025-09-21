let magnifyingGlass = {
  beforeMount(el: HTMLDivElement) {
    const sImg: HTMLDivElement | null = el.querySelector('#magnifyingGlass-s-img')
    const mask: HTMLDivElement | null = el.querySelector('#magnifyingGlass-mask')
    const lImg: HTMLDivElement | null = el.querySelector('#magnifyingGlass-p-img')
    const picImg: HTMLImageElement | null = el.querySelector('#magnifyingGlass-pic-img')
    el.addEventListener(
      'mouseover',
      function mouseover_() {
        if (lImg && mask) {
          lImg.style.display = 'block'
          mask.style.display = 'block'
        }
      },

      false,
    )
    el.addEventListener('mousemove', function mousemove_(e: MouseEvent) {
      if (sImg && mask && lImg && picImg) {
        // 获取浏览器滚动覆盖的值
        let topCover = document.querySelector(`#app`)?.scrollTop || 0
        let x = e.pageX - el.offsetLeft - mask.offsetWidth / 2
        let y = e.pageY + topCover - el.offsetTop - mask.offsetHeight / 2
        if (x < 0) {
          x = 0
        }
        if (y < 0) {
          y = 0
        }
        if (x > lImg.offsetWidth - mask.offsetWidth) {
          x = lImg.offsetWidth - mask.offsetWidth
        }
        if (y > lImg.offsetHeight - mask.offsetHeight) {
          y = lImg.offsetHeight - mask.offsetHeight
        }
        // 小图移动
        mask.style.left = x + 'px'
        mask.style.top = y + 'px'
        // 大图移动
        picImg.style.left = -x * 2 + 'px'
        picImg.style.top = -y * 2 + 'px'
      }
    })
    el.addEventListener(
      'mouseleave',
      function mouseover_() {
        if (lImg && mask) {
          lImg.style.display = 'none'
          mask.style.display = 'none'
        }
      },
      false,
    )
  },
  // 安装绑定元素的父组件时...「等同于inserted」
  mounted() {},
  // 在包含组件的VNode更新之前...
  beforeUpdate() {},
  // 在包含组件的VNode及其子VNode更新后...「等同于componentUpdated」
  updated() {},
  // 在卸载绑定元素的父组件之前...
  beforeUnmount() {},
  // 指令与元素解除绑定且父组件已卸载时...「等同于unbind」
  unmounted() {},
}

export default magnifyingGlass
