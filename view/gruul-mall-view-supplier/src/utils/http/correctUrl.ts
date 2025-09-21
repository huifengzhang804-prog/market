import { RequestOptions } from '#/types/axios'
type UrlProcessFn = (url: string, config: Partial<RequestOptions>) => string
class UrlManager {
    private returnUrl: string
    private config: Partial<RequestOptions>
    private process: UrlProcessFn[] = []
    constructor(processList: UrlProcessFn[]) {
        this.config = {}
        this.returnUrl = ''
        processList.forEach((item) => {
            this.register(item)
        })
    }
    register(fn: UrlProcessFn) {
        this.process.push(fn)
    }
    updateUrl(url: string, config: RequestOptions) {
        // if (!this.returnUrl) {
        this.returnUrl = url
        this.config = config
        // }
        this.process.forEach((item) => {
            this.returnUrl = item(this.returnUrl, this.config)
        })
    }
    getUrl(url: string, config: RequestOptions) {
        this.updateUrl(url, config)
        return this.returnUrl
    }
}
/**
 * @description: 服务切换url更改
 * @param {string} url
 * @param {RequestOptions} config
 */
function exchangeServer(url: string, config: Partial<RequestOptions>) {
    console.log(url)
    const singleUrlCorrectRegex = /\/?.*?\//
    return config.isSingleServer ? url.replace(singleUrlCorrectRegex, '/') : url
}
const urlManager = new UrlManager([exchangeServer])
export default urlManager
