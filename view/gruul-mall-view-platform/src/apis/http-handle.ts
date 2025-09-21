import request from './request'
import { ElNotification } from 'element-plus'
import { RequestData, R } from './http.type'
import { AxiosRequestConfig, AxiosResponse } from 'axios'
import { FullScreenLoadingHelper } from '@/libs/Loading'
import { TOKEN_OVERDUE } from './http.type'
import { debounce } from 'lodash-es'

abstract class AbstractHttp {
    private static debounceNotify = debounce(AbstractHttp.notify, 500)

    private static notify(msg: string): void {
        ElNotification.error({
            title: '错误',
            message: msg,
        })
    }

    /**
     * @param url 请求url
     * @param params url请求参数
     * @param data 请求体数据
     * @param showLoading  是否显示loading
     */
    request<T>({ url, params = {}, data = {}, showLoading = true, errorImmediately = true }: RequestData): Promise<R<T>> {
        FullScreenLoadingHelper.openLoading(showLoading)
        return new Promise<R<T>>((resolve, reject) => {
            this.doRequest<T>(
                url,
                {
                    data,
                    params,
                },
                data,
            )
                .then((res) => {
                    FullScreenLoadingHelper.closeLoading(showLoading)
                    if (res?.data) {
                        resolve(res.data)
                    } else {
                        reject(res)
                    }
                })
                .catch((err) => {
                    FullScreenLoadingHelper.closeLoading(showLoading)
                    this.errHandler(err, errorImmediately)
                    reject(err)
                })
        })
    }

    protected abstract doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>>

    //错误处理
    private errHandler(error: any, errorImmediately = true): void {
        if (TOKEN_OVERDUE.includes(error?.code)) return
        if (!error?.msg) {
            if (Object.getPrototypeOf(error).__CANCEL__) {
                return
            }
            if (errorImmediately) {
                AbstractHttp.debounceNotify('响应错误')
            }
            return
        }
        let promise = Promise.resolve().then(() => {
            if (errorImmediately) {
                AbstractHttp.debounceNotify(error?.msg)
            }
        })

        if (error.data) {
            ;(<Array<string>>error?.data).forEach((msg) => {
                if (!msg) {
                    return
                }
                promise = promise.then(() => {
                    if (errorImmediately) {
                        AbstractHttp.debounceNotify(msg)
                    }
                })
            })
        }
    }
}

export class HttpGet extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.get<R<T>>(url, config)
    }
}

export class HttpPost extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.post<R<T>>(url, data, config)
    }
}

export class HttpPut extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.put<R<T>>(url, data, config)
    }
}

export class HttpDelete extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.delete<R<T>>(url, { ...config, data: data })
    }
}

export class HttpPatch extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.patch<R<T>>(url, data, config)
    }
}

export class HttpHead extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.head<R<T>>(url, config)
    }
}

export class HttpOptions extends AbstractHttp {
    doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>> {
        return request.options<R<T>>(url, config)
    }
}
