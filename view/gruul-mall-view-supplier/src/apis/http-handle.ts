import request from './request'
import { ElNotification } from 'element-plus'
import { RequestData, R } from './http.type'
import { CRUD_ERROR_CODE } from './http.type'
import { AxiosRequestConfig, AxiosResponse } from 'axios'
import { FullScreenLoadingHelper } from '@/libs/Loading'

abstract class AbstractHttp {
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
     * @param headers 请求头
     */
    request<T>({
        url,
        params = {},
        data = {},
        showLoading = false,
        timeout = Number(import.meta.env.VITE_REQUEST_TIME_OUT),
        headers = {},
        errorImmediately = true,
    }: RequestData): Promise<R<T>> {
        return new Promise<R<T>>((resolve, reject) => {
            FullScreenLoadingHelper.openLoading(showLoading)
            this.doRequest<T>(
                url,
                {
                    params,
                    headers,
                    timeout,
                },
                data,
            )
                .then((res) => {
                    resolve(res.data)
                })
                .catch((err) => {
                    this.errHandler(err, errorImmediately)
                    reject(err)
                })
                .finally(() => FullScreenLoadingHelper.closeLoading(showLoading))
        })
    }

    protected abstract doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<T>>>

    //错误处理
    private errHandler(error: any, errorImmediately: boolean): void {
        if (error instanceof Error) {
            // http状态不为200
            if (errorImmediately) {
                AbstractHttp.notify('服务器内部错误')
            }
            return
        }
        if (!error.msg && !CRUD_ERROR_CODE.includes(error.data.code)) {
            if (errorImmediately) {
                AbstractHttp.notify('响应错误')
            }
            return
        }
        let promise = Promise.resolve().then(() => {
            if (!CRUD_ERROR_CODE.includes(error.data.code)) {
                if (errorImmediately) {
                    AbstractHttp.notify(error.msg)
                }
            }
        })
        // eslint-disable-next-line no-extra-boolean-cast
        if (!!error.data) {
            ;(<Array<string>>error.data).forEach((msg) => {
                if (!msg) {
                    return
                }
                promise = promise.then(() => {
                    if (!CRUD_ERROR_CODE.includes(error.data.code)) {
                        if (errorImmediately) {
                            AbstractHttp.notify(msg)
                        }
                    }
                })
            })
        }
    }
}

export class HttpGet extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.get<R<any>>(url, config)
    }
}

export class HttpPost extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.post<R<any>>(url, JSON.stringify(data), config)
    }
}

export class HttpPut extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.put<R<any>>(url, JSON.stringify(data), config)
    }
}

export class HttpDelete extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.delete<R<any>>(url, { ...config, data: data })
    }
}

export class HttpPatch extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.patch<R<any>>(url, JSON.stringify(data), config)
    }
}

export class HttpHead extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.head<R<any>>(url, config)
    }
}

export class HttpOptions extends AbstractHttp {
    doRequest(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>> {
        return request.options<R<any>>(url, config)
    }
}
