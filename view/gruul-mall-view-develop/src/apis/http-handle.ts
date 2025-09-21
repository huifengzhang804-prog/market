/**
 * api请求处理句柄
 * @author 张治保
 */
import request from "./request";
import { ElNotification } from "element-plus";
import { RequestData, R } from "./http.type";
import { AxiosRequestConfig, AxiosResponse } from "axios";
import { FullScreenLoadingHelper } from "../utils/Loading";

abstract class AbstractHttp {
  protected abstract doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>>;

  /**
   * @param url 请求url
   * @param params url请求参数
   * @param data 请求体数据
   * @param showLoading  是否显示loading
   */
  request({
    url,
    params = {},
    data = {},
    showLoading = true,
  }: RequestData): Promise<R> {
    FullScreenLoadingHelper.openLoading(showLoading);
    return new Promise<R>((resolve) => {
      this.doRequest(
        url,
        {
          params,
        },
        data
      )
        .then((res) => {
          FullScreenLoadingHelper.closeLoading(showLoading);
          resolve(res.data);
        })
        .catch((err) => {
          FullScreenLoadingHelper.closeLoading(showLoading);
          this.errHandler(err);
        });
    });
  }

  private static notify(msg: string): void {
    // @ts-ignore
    ElNotification.error({
      title: "错误",
      message: msg,
    });
  }

  //错误处理
  private errHandler(error: any): void {
    console.log("!!!!!!请求出错", error);
    if (!error.msg) {
      AbstractHttp.notify("响应错误");
      return;
    }
    let promise = Promise.resolve().then(() => {
      AbstractHttp.notify(error.msg);
    });

    if (!!error.data) {
      (<Array<string>>error.data).forEach((msg) => {
        if (!msg) {
          return;
        }
        promise = promise.then(() => {
          AbstractHttp.notify(msg);
        });
      });
    }
  }
}

export class HttpGet extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.get<R>(url, config);
  }
}

export class HttpPost extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.post<R>(url, JSON.stringify(data), config);
  }
}

export class HttpPut extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.put<R>(url, JSON.stringify(data), config);
  }
}

export class HttpDelete extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.delete<R>(url, config);
  }
}

export class HttpPatch extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.patch<R>(url, JSON.stringify(data), config);
  }
}

export class HttpHead extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.head<R>(url, config);
  }
}

export class HttpOptions extends AbstractHttp {
  doRequest(
    url: string,
    config: AxiosRequestConfig,
    data?: any
  ): Promise<AxiosResponse<R>> {
    return request.options<R>(url, config);
  }
}
