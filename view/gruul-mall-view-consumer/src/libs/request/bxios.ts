import type { Result } from '@/utils/types'
import type { IRequestConfig, PromiseHandlers, UnireqResponse } from '@/utils/request'

// tslint:disable-next-line: max-classes-per-file
class InterceptorManager<T> {
  private handlers: Array<PromiseHandlers<T>> = []

  use(fulfilled: PromiseHandlers<T>['fulfilled'], rejected: PromiseHandlers<T>['rejected']): number {
    this.handlers.push({
      fulfilled,
      rejected,
    })
    return this.handlers.length - 1
  }
  forEach(fn: (arg: PromiseHandlers<T>) => void) {
    this.handlers.forEach((h: PromiseHandlers<T>) => fn(h))
  }
}
class Bxios {
  public interceptors: {
    request: InterceptorManager<IRequestConfig>
    response: InterceptorManager<Result>
  }

  private defaults: IRequestConfig = {
    timeout: 5000,
    reqState: false,
    url: '',
    header: {},
    data: '',
    method: 'GET',
    dataType: 'json',
    baseUrl: '',
  }

  constructor(defaultConfig: Partial<IRequestConfig> = {}) {
    this.defaults = Object.assign({}, this.defaults, defaultConfig)
    this.interceptors = {
      request: new InterceptorManager<IRequestConfig>(),
      response: new InterceptorManager<Result>(),
    }
  }

  /**
   *
   * @param url 请求地址
   * @param data query
   * @param config 配置 config.cache 开启缓存 duration 缓存时间（ms） 默认 3000
   */
  get<T = any, D = any>(url: string, data?: D, config: Partial<IRequestConfig> = {}): Promise<Result<T>> {
    return this.request<T>(
      Object.assign({
        header: Object.assign(this.defaults.header, config),
        method: 'GET',
        data,
        url,
      }),
    )
  }

  post<T = any, D = any>(url: string, data?: D, config?: Partial<IRequestConfig>): Promise<Result<T>> {
    return this.request<T>(
      Object.assign({
        header: { ...this.defaults.header, ...config },
        method: 'POST',
        data,
        url,
      }),
    )
  }

  put<T = any, D = any>(url: string, data?: D, config?: Partial<IRequestConfig>): Promise<Result<T>> {
    return this.request<T>(
      Object.assign({
        header: Object.assign(this.defaults.header, config),
        method: 'PUT',
        data,
        url,
      }),
    )
  }

  delete<T = any, D = any>(url: string, data?: D, config?: Partial<IRequestConfig>, params?: any): Promise<Result<T>> {
    return this.request<T>(
      Object.assign({
        header: Object.assign(this.defaults.header, config),
        method: 'DELETE',
        data,
        url,
        params,
      }),
    )
  }

  private request<T, R = Result<T>>(config: IRequestConfig): Promise<R> {
    const chain: (
      | PromiseHandlers<IRequestConfig>['fulfilled']
      | PromiseHandlers<Result>['fulfilled']
      | typeof this.dispatchRequest<T>
      | undefined
    )[] = [this.dispatchRequest.bind(this)<T>, undefined]
    let promise = Promise.resolve<any>({ ...this.defaults, ...config })
    this.interceptors.request.forEach((interceptor: PromiseHandlers<IRequestConfig>) => {
      chain.unshift(interceptor.fulfilled.bind(this, config), interceptor.rejected)
    })

    this.interceptors.response.forEach((interceptor: PromiseHandlers<Result>) => {
      chain.push(interceptor.fulfilled, interceptor.rejected)
    })

    while (chain.length) {
      promise = promise.then<any, any>(chain.shift(), chain.shift())
    }
    return promise as Promise<R>
  }
  private requestTaskMap = new Map<string, any>()
  private createRequestKey(config: IRequestConfig): string {
    return `${config.method}~${config.url}`
  }
  private xhrAdapter<T>(config: IRequestConfig): Promise<Result<T>> {
    if (config.cancelLast) {
      this.requestTaskMap.get(this.createRequestKey(config))?.abort()
    }
    return new Promise((resolve, reject) => {
      const requestTaskTemp = uni.request(
        Object.assign(
          {},
          config,
          { url: `${this.defaults.baseUrl}${config.url}` },
          {
            success: (res: UnireqResponse<T>) => {
              if (res.statusCode === 200) {
                resolve({ data: res.data, config } as Result<T>)
              } else {
                reject(res.data.error)
              }
            },
            // eslint-disable-next-line no-undef
            fail: (err: UniApp.GeneralCallbackResult) => {
              reject(err)
            },
          },
        ),
      )
      if (config.cancelLast) {
        this.requestTaskMap.set(this.createRequestKey(config), requestTaskTemp)
      }
    })
  }

  private async dispatchRequest<T>(config: IRequestConfig): Promise<Result<T>> {
    const adapter = this.xhrAdapter<T>(config)
    try {
      return await adapter
    } catch (reason) {
      return Promise.reject(reason)
    }
  }
}

export default Bxios
