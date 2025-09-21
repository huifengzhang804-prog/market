import { ElLoading } from 'element-plus'
import { ILoadingOptions, ILoadingInstance } from 'element-plus/es'

const defaultOptions: ILoadingOptions = {
  lock: true,
  text: '加载中...',
  background: 'rgba(252,252,252,0.7)',
}

let state = 0
let loading: ILoadingInstance

export class Loading {
  static open(options: ILoadingOptions = defaultOptions): void {
    if (loading) loading.close()
    nextTick(() => {
      if (state === 0) {
        loading = ElLoading.service(options)
      }
      state++
    })
  }

  static close() {
    if (state > 0) {
      state--
    }
    if (state === 0 && !!loading) {
      loading?.close()
    }
  }
}

export class FullScreenLoadingHelper {
  static openLoading(showLoading: boolean): void {
    if (showLoading) {
      Loading.open()
    }
  }

  static closeLoading(showLoading: boolean): void {
    if (showLoading) {
      Loading.close()
    }
  }
}
