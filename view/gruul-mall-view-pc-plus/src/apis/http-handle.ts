import request from './request'
import { ElNotification } from 'element-plus'
import { RequestData, R, TOKEN_OVERDUE } from './http.type'
import { AxiosRequestConfig, AxiosResponse } from 'axios'
import { FullScreenLoadingHelper } from '../utils/Loading'

abstract class AbstractHttp {
  protected abstract doRequest<T>(url: string, config: AxiosRequestConfig, data?: any): Promise<AxiosResponse<R<any>>>

  /**
   * @param url 请求url
   * @param params url请求参数
   * @param data 请求体数据
   * @param showLoading  是否显示loading
   * @param headers 请求头
   */
  request<T>({ url, params = {}, data = {}, showLoading = true, headers = {} }: RequestData): Promise<R<T>> {
    FullScreenLoadingHelper.openLoading(showLoading)
    return new Promise<R<T>>((resolve) => {
      this.doRequest<T>(
        url,
        {
          params,
          headers,
        },
        data,
      )
        .then((res) => {
          FullScreenLoadingHelper.closeLoading(showLoading)
          resolve(res.data)
        })
        .catch((err) => {
          FullScreenLoadingHelper.closeLoading(showLoading)
          this.errHandler(err)
        })
        .finally(() => {
          FullScreenLoadingHelper.closeLoading(showLoading)
        })
    })
  }

  private static notify(msg: string): void {
    // @ts-ignore
    ElNotification.error({
      title: '错误',
      message: msg,
    })
  }

  //错误处理
  private errHandler(error: any): void {
    console.log('!!!!!!请求出错', error)
    if (TOKEN_OVERDUE.includes(error?.code)) return
    if (!error?.msg) {
      AbstractHttp.notify('响应错误')
      return
    }
    let promise = Promise.resolve().then(() => {
      AbstractHttp.notify(error.msg)
    })

    if (error.data) {
      ;(<Array<string>>error.data).forEach((msg) => {
        if (!msg) {
          return
        }
        promise = promise.then(() => {
          AbstractHttp.notify(msg)
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
