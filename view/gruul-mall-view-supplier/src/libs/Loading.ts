import { ElLoading } from 'element-plus'
import { LoadingOptions } from 'element-plus/es/components/loading/src/types'
import { LoadingInstance } from 'element-plus/es/components/loading/src/loading'

const defaultOptions: LoadingOptions = {
    lock: true,
    text: '加载中...',
    background: 'rgba(252,252,252,0.7)',
    target: '#admin__main--mask',
    fullscreen: false,
}

class Loading {
    static state = 0
    static loadingInstance: LoadingInstance | null = null

    static open(): void {
        if (Loading.state++ === 0) {
            Loading.loadingInstance = ElLoading.service(defaultOptions)
        }
    }

    static close() {
        if (--Loading.state <= 0) {
            Loading.state = 0
            Loading.loadingInstance?.close()
            Loading.loadingInstance = null
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
